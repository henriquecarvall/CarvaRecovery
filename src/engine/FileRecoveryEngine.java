package engine;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class FileRecoveryEngine {
    private static final Set<String> COMMON_SIGNATURES = Set.of(
            "FFD8FF", // JPEG
            "89504E47", // PNG
            "47494638", // GIF
            "424D", // BMP
            "25504446", // PDF
            "504B0304", // ZIP
            "52617221", // RAR
            "377ABCAF271C", // 7Z
            "494433", // MP3
            "000001BA", // MPEG
            "000001B3", // MPEG
            "66747970", // MP4
            "1A45DFA3", // MKV
            "D0CF11E0" // DOC, XLS, PPT
    );

    private AtomicInteger filesRecovered;
    private boolean isScanning;
    private RecoveryProgressListener progressListener;

    public FileRecoveryEngine() {
        this.filesRecovered = new AtomicInteger(0);
        this.isScanning = false;
    }

    public void setProgressListener(RecoveryProgressListener listener) {
        this.progressListener = listener;
    }

    public List<RecoveredFile> scanForDeletedFiles(String devicePath, Set<String> fileTypes) {
        List<RecoveredFile> recoveredFiles = new ArrayList<>();
        isScanning = true;
        filesRecovered.set(0);

        try {
            Path device = Paths.get(devicePath);
            if (!Files.exists(device)) {
                throw new IOException("Dispositivo não encontrado: " + devicePath);
            }

            long totalSpace = Files.getFileStore(device).getTotalSpace();
            long scannedBytes = 0;

            // Simulação de varredura do dispositivo
            try (RandomAccessFile raf = new RandomAccessFile(devicePath, "r")) {
                byte[] buffer = new byte[8192];
                long filePointer = 0;

                while (isScanning && filePointer < totalSpace) {
                    raf.seek(filePointer);
                    int bytesRead = raf.read(buffer);
                    if (bytesRead == -1) break;

                    // Verificar assinaturas de arquivo
                    for (String signature : COMMON_SIGNATURES) {
                        List<Long> positions = findSignaturePositions(buffer, signature, filePointer);
                        for (Long position : positions) {
                            RecoveredFile file = attemptFileRecovery(raf, position, signature, fileTypes);
                            if (file != null) {
                                recoveredFiles.add(file);
                                filesRecovered.incrementAndGet();

                                if (progressListener != null) {
                                    progressListener.onFileFound(file);
                                }
                            }
                        }
                    }

                    scannedBytes += bytesRead;
                    filePointer += bytesRead;

                    if (progressListener != null) {
                        int progress = (int) ((scannedBytes * 100) / totalSpace);
                        progressListener.onProgressUpdate(progress, scannedBytes, totalSpace);
                    }

                    // Pequena pausa para não sobrecarregar o sistema
                    Thread.sleep(1);
                }
            }

        } catch (Exception e) {
            System.err.println("Erro durante a recuperação: " + e.getMessage());
        } finally {
            isScanning = false;
        }

        return recoveredFiles;
    }

    private List<Long> findSignaturePositions(byte[] buffer, String signature, long baseOffset) {
        List<Long> positions = new ArrayList<>();
        byte[] sigBytes = hexStringToByteArray(signature);

        for (int i = 0; i <= buffer.length - sigBytes.length; i++) {
            boolean match = true;
            for (int j = 0; j < sigBytes.length; j++) {
                if (buffer[i + j] != sigBytes[j]) {
                    match = false;
                    break;
                }
            }
            if (match) {
                positions.add(baseOffset + i);
            }
        }

        return positions;
    }

    private RecoveredFile attemptFileRecovery(RandomAccessFile raf, long startPosition,
                                              String signature, Set<String> fileTypes) {
        try {
            String fileExtension = getFileExtension(signature);

            if (!fileTypes.isEmpty() && !fileTypes.contains(fileExtension.toLowerCase())) {
                return null;
            }

            // Estimar tamanho do arquivo (simulação)
            long estimatedSize = estimateFileSize(raf, startPosition, signature);

            RecoveredFile file = new RecoveredFile(
                    "recovered_" + System.currentTimeMillis() + "." + fileExtension,
                    startPosition,
                    estimatedSize,
                    fileExtension.toUpperCase(),
                    new Date(),
                    RecoveredFile.RecoveryStatus.RECOVERABLE
            );

            return file;

        } catch (Exception e) {
            return null;
        }
    }

    private long estimateFileSize(RandomAccessFile raf, long startPosition, String signature) {
        // Simulação - na implementação real isso seria mais complexo
        switch (signature) {
            case "FFD8FF": return 1024 * 1024; // ~1MB para JPEG
            case "89504E47": return 512 * 1024; // ~512KB para PNG
            case "504B0304": return 2 * 1024 * 1024; // ~2MB para ZIP
            default: return 1024 * 1024; // 1MB padrão
        }
    }

    private String getFileExtension(String signature) {
        switch (signature) {
            case "FFD8FF": return "jpg";
            case "89504E47": return "png";
            case "47494638": return "gif";
            case "424D": return "bmp";
            case "25504446": return "pdf";
            case "504B0304": return "zip";
            case "52617221": return "rar";
            case "377ABCAF271C": return "7z";
            case "494433": return "mp3";
            case "000001BA": case "000001B3": return "mpg";
            case "66747970": return "mp4";
            case "1A45DFA3": return "mkv";
            case "D0CF11E0": return "doc";
            default: return "bin";
        }
    }

    private byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }

    public boolean recoverFile(RecoveredFile file, String outputPath, String devicePath) {
        try (RandomAccessFile raf = new RandomAccessFile(devicePath, "r");
             FileOutputStream fos = new FileOutputStream(outputPath + File.separator + file.getFileName())) {

            raf.seek(file.getStartPosition());
            byte[] buffer = new byte[8192];
            long bytesRemaining = file.getFileSize();

            while (bytesRemaining > 0 && isScanning) {
                int bytesToRead = (int) Math.min(buffer.length, bytesRemaining);
                int bytesRead = raf.read(buffer, 0, bytesToRead);
                if (bytesRead == -1) break;

                fos.write(buffer, 0, bytesRead);
                bytesRemaining -= bytesRead;

                if (progressListener != null) {
                    progressListener.onRecoveryProgress(file,
                            (int) ((file.getFileSize() - bytesRemaining) * 100 / file.getFileSize()));
                }
            }

            file.setRecoveryStatus(RecoveredFile.RecoveryStatus.RECOVERED);
            return true;

        } catch (Exception e) {
            file.setRecoveryStatus(RecoveredFile.RecoveryStatus.FAILED);
            return false;
        }
    }

    public void stopScanning() {
        isScanning = false;
    }

    public int getFilesRecoveredCount() {
        return filesRecovered.get();
    }

    public interface RecoveryProgressListener {
        void onProgressUpdate(int progress, long bytesScanned, long totalBytes);
        void onFileFound(RecoveredFile file);
        void onRecoveryProgress(RecoveredFile file, int progress);
    }
}