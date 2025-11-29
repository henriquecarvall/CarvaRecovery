package engine;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.*;

public class FileRecoveryEngine {
    private File device;
    private List<RecoveredFileInfo> recoveredFiles;
    private boolean isScanning;
    private RecoveryListener listener;

    // Assinaturas de arquivos (headers)
    private static final Map<String, byte[]> FILE_SIGNATURES = new HashMap<>();
    private static final Map<String, byte[]> FILE_FOOTERS = new HashMap<>();

    static {
        // Headers (inícios de arquivo)
        FILE_SIGNATURES.put("jpg", new byte[]{(byte)0xFF, (byte)0xD8, (byte)0xFF});
        FILE_SIGNATURES.put("png", new byte[]{ (byte)0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A});
        FILE_SIGNATURES.put("pdf", new byte[]{0x25, 0x50, 0x44, 0x46});
        FILE_SIGNATURES.put("zip", new byte[]{0x50, 0x4B, 0x03, 0x04});
        FILE_SIGNATURES.put("doc", new byte[]{(byte)0xD0, (byte)0xCF, 0x11, (byte)0xE0, (byte)0xA1, (byte)0xB1, 0x1A, (byte)0xE1});
        FILE_SIGNATURES.put("mp4", new byte[]{0x00, 0x00, 0x00, 0x18, 0x66, 0x74, 0x79, 0x70});

        // Footers (fins de arquivo)
        FILE_FOOTERS.put("jpg", new byte[]{(byte)0xFF, (byte)0xD9});
        FILE_FOOTERS.put("pdf", new byte[]{0x25, 0x25, 0x45, 0x4F, 0x46}); // %%EOF
    }

    public interface RecoveryListener {
        void onFileFound(RecoveredFileInfo fileInfo);
        void onProgressUpdate(int progress, String status);
        void onError(String errorMessage);
    }

    public static class RecoveredFileInfo {
        private String fileName;
        private String fileExtension;
        private long fileSize;
        private long startOffset;
        private long endOffset;
        private boolean recovered;

        public RecoveredFileInfo(String fileName, String fileExtension, long fileSize,
                                 long startOffset, long endOffset) {
            this.fileName = fileName;
            this.fileExtension = fileExtension;
            this.fileSize = fileSize;
            this.startOffset = startOffset;
            this.endOffset = endOffset;
            this.recovered = false;
        }

        // Getters
        public String getFileName() { return fileName; }
        public String getFileExtension() { return fileExtension; }
        public long getFileSize() { return fileSize; }
        public long getStartOffset() { return startOffset; }
        public long getEndOffset() { return endOffset; }
        public boolean isRecovered() { return recovered; }
        public void setRecovered(boolean recovered) { this.recovered = recovered; }
    }

    public FileRecoveryEngine(File device) {
        this.device = device;
        this.recoveredFiles = new ArrayList<>();
        this.isScanning = false;
    }

    public void setRecoveryListener(RecoveryListener listener) {
        this.listener = listener;
    }

    public void startDeepRecovery() {
        if (isScanning) {
            notifyError("Scan ja esta em andamento");
            return;
        }

        isScanning = true;
        recoveredFiles.clear();

        new Thread(() -> {
            try {
                performDeepRecovery();
            } catch (Exception e) {
                notifyError("Erro durante recuperacao: " + e.getMessage());
            } finally {
                isScanning = false;
            }
        }).start();
    }

    public void stopRecovery() {
        isScanning = false;
    }

    private void performDeepRecovery() throws IOException {
        notifyProgress(0, "Iniciando analise do dispositivo...");

        long deviceSize = getDeviceSize();
        if (deviceSize <= 0) {
            notifyError("Nao foi possivel determinar o tamanho do dispositivo");
            return;
        }

        notifyProgress(5, "Tamanho do dispositivo: " + formatSize(deviceSize));

        // Scanner por assinaturas
        scanForFileSignatures(deviceSize);

        notifyProgress(100, "Recuperacao concluida! " + recoveredFiles.size() + " arquivos encontrados.");
    }

    private void scanForFileSignatures(long deviceSize) {
        notifyProgress(10, "Procurando por assinaturas de arquivos...");

        try (RandomAccessFile raf = new RandomAccessFile(device, "r")) {
            byte[] buffer = new byte[8192]; // 8KB buffer
            long bytesRead = 0;
            int readBytes;

            while ((readBytes = raf.read(buffer)) != -1 && isScanning) {
                // Procura por assinaturas no buffer atual
                scanBufferForSignatures(buffer, readBytes, bytesRead);

                bytesRead += readBytes;
                int progress = (int) ((bytesRead * 85) / deviceSize) + 10;
                notifyProgress(progress, "Analisando setores... " + formatSize(bytesRead) + " de " + formatSize(deviceSize));
            }

        } catch (IOException e) {
            notifyError("Erro de leitura do dispositivo: " + e.getMessage());
        }
    }

    private void scanBufferForSignatures(byte[] buffer, int bufferLength, long globalOffset) {
        for (Map.Entry<String, byte[]> entry : FILE_SIGNATURES.entrySet()) {
            String fileType = entry.getKey();
            byte[] signature = entry.getValue();

            for (int i = 0; i <= bufferLength - signature.length; i++) {
                if (!isScanning) return;

                if (matchesSignature(buffer, i, signature)) {
                    // Assinatura encontrada! Vamos tentar recuperar o arquivo
                    RecoveredFileInfo fileInfo = attemptFileRecovery(fileType, globalOffset + i);
                    if (fileInfo != null) {
                        recoveredFiles.add(fileInfo);
                        notifyFileFound(fileInfo);
                    }
                }
            }
        }
    }

    private boolean matchesSignature(byte[] buffer, int offset, byte[] signature) {
        for (int i = 0; i < signature.length; i++) {
            if (buffer[offset + i] != signature[i]) {
                return false;
            }
        }
        return true;
    }

    private RecoveredFileInfo attemptFileRecovery(String fileType, long startOffset) {
        try {
            RandomAccessFile raf = new RandomAccessFile(device, "r");
            raf.seek(startOffset);

            // Tenta determinar o tamanho do arquivo baseado no tipo
            long fileSize = estimateFileSize(fileType, raf, startOffset);

            if (fileSize > 0) {
                String fileName = generateFileName(fileType, startOffset);
                RecoveredFileInfo fileInfo = new RecoveredFileInfo(
                        fileName, fileType, fileSize, startOffset, startOffset + fileSize
                );
                return fileInfo;
            }

            raf.close();
        } catch (IOException e) {
            // Ignora erros de leitura em setores específicos
        }

        return null;
    }

    private long estimateFileSize(String fileType, RandomAccessFile raf, long startOffset) throws IOException {
        // Estrategias diferentes para cada tipo de arquivo
        switch (fileType) {
            case "jpg":
                return findJPEGSize(raf, startOffset);
            case "pdf":
                return findPDFSize(raf, startOffset);
            case "zip":
                return findZIPSize(raf, startOffset);
            default:
                // Tamanho padrao para tipos desconhecidos
                return 1024 * 1024; // 1MB
        }
    }

    private long findJPEGSize(RandomAccessFile raf, long startOffset) throws IOException {
        // Procura pelo footer do JPEG (FF D9)
        byte[] buffer = new byte[4096];
        long currentPos = startOffset;
        raf.seek(currentPos);

        while (raf.read(buffer) != -1) {
            for (int i = 0; i < buffer.length - 1; i++) {
                if ((buffer[i] & 0xFF) == 0xFF && (buffer[i + 1] & 0xFF) == 0xD9) {
                    return (currentPos + i + 2) - startOffset;
                }
            }
            currentPos += buffer.length;
        }

        return -1; // Nao encontrou o footer
    }

    private long findPDFSize(RandomAccessFile raf, long startOffset) throws IOException {
        // Procura por %%EOF
        byte[] eofSignature = FILE_FOOTERS.get("pdf");
        byte[] buffer = new byte[8192];
        long currentPos = startOffset;
        raf.seek(currentPos);

        while (raf.read(buffer) != -1) {
            for (int i = 0; i <= buffer.length - eofSignature.length; i++) {
                if (matchesSignature(buffer, i, eofSignature)) {
                    return (currentPos + i + eofSignature.length) - startOffset;
                }
            }
            currentPos += buffer.length;
        }

        return -1;
    }

    private long findZIPSize(RandomAccessFile raf, long startOffset) throws IOException {
        // Para ZIP, podemos tentar ler a estrutura central directory
        // Por simplicidade, vamos usar um tamanho estimado
        return 10 * 1024 * 1024; // 10MB estimado
    }

    private String generateFileName(String fileType, long offset) {
        return "recuperado_" + offset + "." + fileType;
    }

    public boolean saveRecoveredFile(RecoveredFileInfo fileInfo, File outputDir) {
        try {
            if (!outputDir.exists()) {
                outputDir.mkdirs();
            }

            File outputFile = new File(outputDir, fileInfo.getFileName());

            try (RandomAccessFile source = new RandomAccessFile(device, "r");
                 FileOutputStream dest = new FileOutputStream(outputFile)) {

                source.seek(fileInfo.getStartOffset());
                byte[] buffer = new byte[8192];
                long bytesToRead = fileInfo.getFileSize();
                long totalRead = 0;

                while (totalRead < bytesToRead && isScanning) {
                    int bytesRead = source.read(buffer, 0,
                            (int) Math.min(buffer.length, bytesToRead - totalRead));

                    if (bytesRead == -1) break;

                    dest.write(buffer, 0, bytesRead);
                    totalRead += bytesRead;
                }

                if (totalRead == fileInfo.getFileSize()) {
                    fileInfo.setRecovered(true);
                    return true;
                }
            }

        } catch (IOException e) {
            notifyError("Erro ao salvar arquivo " + fileInfo.getFileName() + ": " + e.getMessage());
        }

        return false;
    }

    public void saveAllRecoveredFiles(File outputDir) {
        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }

        int successCount = 0;
        for (RecoveredFileInfo fileInfo : recoveredFiles) {
            if (!isScanning) break;

            if (saveRecoveredFile(fileInfo, outputDir)) {
                successCount++;
            }
        }

        notifyProgress(100, "Salvamento concluido: " + successCount + "/" + recoveredFiles.size() + " arquivos");
    }

    public List<RecoveredFileInfo> getRecoveredFiles() {
        return new ArrayList<>(recoveredFiles);
    }

    public int getRecoveredFilesCount() {
        return recoveredFiles.size();
    }

    public boolean isScanning() {
        return isScanning;
    }

    private long getDeviceSize() {
        return device.getTotalSpace();
    }

    private String formatSize(long bytes) {
        if (bytes < 1024) return bytes + " B";
        if (bytes < 1024 * 1024) return (bytes / 1024) + " KB";
        if (bytes < 1024 * 1024 * 1024) return (bytes / (1024 * 1024)) + " MB";
        return (bytes / (1024 * 1024 * 1024)) + " GB";
    }

    private void notifyProgress(int progress, String status) {
        if (listener != null) {
            listener.onProgressUpdate(progress, status);
        }
    }

    private void notifyFileFound(RecoveredFileInfo fileInfo) {
        if (listener != null) {
            listener.onFileFound(fileInfo);
        }
    }

    private void notifyError(String errorMessage) {
        if (listener != null) {
            listener.onError(errorMessage);
        }
    }
}