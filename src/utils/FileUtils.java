package utils;

import java.io.*;
import java.nio.file.*;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.List;

public class FileUtils {

    // Assinaturas de arquivos (magic numbers)
    private static final byte[] JPEG_SIGNATURE = {(byte)0xFF, (byte)0xD8, (byte)0xFF};
    private static final byte[] PNG_SIGNATURE = {(byte)0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A};
    private static final byte[] PDF_SIGNATURE = {0x25, 0x50, 0x44, 0x46};
    private static final byte[] ZIP_SIGNATURE = {0x50, 0x4B, 0x03, 0x04};
    private static final byte[] MP4_SIGNATURE = {0x00, 0x00, 0x00, 0x18, 0x66, 0x74, 0x79, 0x70};
    private static final byte[] MP3_SIGNATURE = {(byte)0xFF, (byte)0xFB};

    public static boolean isFileSignature(byte[] data, byte[] signature) {
        if (data.length < signature.length) return false;

        for (int i = 0; i < signature.length; i++) {
            if (data[i] != signature[i]) {
                return false;
            }
        }
        return true;
    }

    public static String detectFileFormat(byte[] header) {
        if (isFileSignature(header, JPEG_SIGNATURE)) return "jpg";
        if (isFileSignature(header, PNG_SIGNATURE)) return "png";
        if (isFileSignature(header, PDF_SIGNATURE)) return "pdf";
        if (isFileSignature(header, ZIP_SIGNATURE)) return "zip";
        if (isFileSignature(header, MP4_SIGNATURE)) return "mp4";
        if (isFileSignature(header, MP3_SIGNATURE)) return "mp3";
        return "unknown";
    }

    public static byte[] readFileHeader(File file, int headerSize) throws IOException {
        try (RandomAccessFile raf = new RandomAccessFile(file, "r")) {
            byte[] header = new byte[headerSize];
            int bytesRead = raf.read(header);
            return bytesRead == headerSize ? header : Arrays.copyOf(header, bytesRead);
        }
    }

    public static byte[] extractFilePreview(File file, long offset, int previewSize) throws IOException {
        try (RandomAccessFile raf = new RandomAccessFile(file, "r")) {
            raf.seek(offset);
            byte[] preview = new byte[previewSize];
            int bytesRead = raf.read(preview);
            return bytesRead == previewSize ? preview : Arrays.copyOf(preview, bytesRead);
        }
    }

    public static boolean isValidFilePath(String path) {
        if (path == null || path.trim().isEmpty()) return false;

        try {
            Paths.get(path);
            return true;
        } catch (InvalidPathException e) {
            return false;
        }
    }

    public static boolean createDirectory(String path) {
        File dir = new File(path);
        if (!dir.exists()) {
            return dir.mkdirs();
        }
        return dir.isDirectory();
    }

    public static String generateUniqueFilename(String baseName, String extension, String directory) {
        File outputDir = new File(directory);
        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }

        String filename = baseName + "." + extension;
        File file = new File(outputDir, filename);

        if (!file.exists()) {
            return filename;
        }

        // Adiciona timestamp se o arquivo já existir
        String nameWithoutExt = baseName;
        String timestamp = String.valueOf(System.currentTimeMillis());
        return nameWithoutExt + "_" + timestamp + "." + extension;
    }

    public static long getFileSize(File file) {
        return file.exists() ? file.length() : 0;
    }

    public static String getFileExtension(String filename) {
        if (filename == null || filename.lastIndexOf(".") == -1) {
            return "";
        }
        return filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
    }

    public static String calculateChecksum(File file) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            try (FileInputStream fis = new FileInputStream(file)) {
                byte[] buffer = new byte[8192];
                int bytesRead;
                while ((bytesRead = fis.read(buffer)) != -1) {
                    md.update(buffer, 0, bytesRead);
                }
            }
            byte[] digest = md.digest();

            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            return "error";
        }
    }

    public static boolean isImageFile(String filename) {
        String ext = getFileExtension(filename);
        return List.of("jpg", "jpeg", "png", "gif", "bmp", "tiff").contains(ext);
    }

    public static boolean isVideoFile(String filename) {
        String ext = getFileExtension(filename);
        return List.of("mp4", "avi", "mkv", "mov", "wmv", "flv").contains(ext);
    }

    public static boolean isDocumentFile(String filename) {
        String ext = getFileExtension(filename);
        return List.of("pdf", "doc", "docx", "xls", "xlsx", "txt", "rtf").contains(ext);
    }

    public static boolean isArchiveFile(String filename) {
        String ext = getFileExtension(filename);
        return List.of("zip", "rar", "7z", "tar", "gz").contains(ext);
    }

    public static String formatFileSize(long bytes) {
        if (bytes < 1024) return bytes + " B";
        if (bytes < 1024 * 1024) return String.format("%.1f KB", bytes / 1024.0);
        if (bytes < 1024 * 1024 * 1024) return String.format("%.1f MB", bytes / (1024.0 * 1024));
        return String.format("%.1f GB", bytes / (1024.0 * 1024 * 1024));
    }

    public static double calculateRecoveryQuality(File original, File recovered) {
        if (!original.exists() || !recovered.exists()) return 0.0;

        try {
            long originalSize = original.length();
            long recoveredSize = recovered.length();

            if (originalSize == 0) return 0.0;

            // Verifica integridade básica
            double sizeRatio = (double) recoveredSize / originalSize;
            if (sizeRatio < 0.5 || sizeRatio > 2.0) return 10.0; // Tamanho muito diferente

            // Verifica checksum se possível
            if (recoveredSize < 10 * 1024 * 1024) { // Só para arquivos menores que 10MB
                String originalChecksum = calculateChecksum(original);
                String recoveredChecksum = calculateChecksum(recovered);

                if (originalChecksum.equals(recoveredChecksum)) {
                    return 100.0; // Arquivo perfeito
                }
            }

            // Qualidade baseada no ratio de tamanho (simplificado)
            return Math.min(100.0, sizeRatio * 80.0);

        } catch (Exception e) {
            return 50.0; // Qualidade média em caso de erro
        }
    }

    public static boolean safeDelete(File file) {
        if (file.exists()) {
            try {
                return file.delete();
            } catch (SecurityException e) {
                return false;
            }
        }
        return true;
    }

    public static void cleanTempFiles(String directory) {
        File tempDir = new File(directory);
        if (tempDir.exists() && tempDir.isDirectory()) {
            File[] tempFiles = tempDir.listFiles((dir, name) -> name.startsWith("temp_") || name.endsWith(".tmp"));
            if (tempFiles != null) {
                for (File tempFile : tempFiles) {
                    safeDelete(tempFile);
                }
            }
        }
    }
}