package models;

import java.io.File;
import java.util.Date;

public class RecoveredFile {
    private String fileName;
    private String fileExtension;
    private String fileType;
    private long fileSize;
    private long startOffset;
    private long endOffset;
    private String filePath;
    private Date recoveryDate;
    private boolean isRecovered;
    private boolean isCorrupted;
    private double recoveryQuality;
    private byte[] filePreview;
    private String checksum;

    public RecoveredFile(String fileName, String fileExtension, long fileSize,
                         long startOffset, long endOffset) {
        this.fileName = fileName;
        this.fileExtension = fileExtension;
        this.fileType = determineFileType(fileExtension);
        this.fileSize = fileSize;
        this.startOffset = startOffset;
        this.endOffset = endOffset;
        this.recoveryDate = new Date();
        this.isRecovered = false;
        this.isCorrupted = false;
        this.recoveryQuality = 0.0;
        this.filePreview = new byte[0];
    }

    private String determineFileType(String extension) {
        switch (extension.toLowerCase()) {
            case "jpg": case "jpeg": return "JPEG Image";
            case "png": return "PNG Image";
            case "pdf": return "PDF Document";
            case "zip": case "rar": return "Archive";
            case "mp4": case "avi": return "Video";
            case "mp3": case "wav": return "Audio";
            case "doc": case "docx": return "Word Document";
            case "xlsx": case "xls": return "Excel Spreadsheet";
            default: return "Unknown File";
        }
    }

    // Getters
    public String getFileName() { return fileName; }
    public String getFileExtension() { return fileExtension; }
    public String getFileType() { return fileType; }
    public long getFileSize() { return fileSize; }
    public long getStartOffset() { return startOffset; }
    public long getEndOffset() { return endOffset; }
    public String getFilePath() { return filePath; }
    public Date getRecoveryDate() { return recoveryDate; }
    public boolean isRecovered() { return isRecovered; }
    public boolean isCorrupted() { return isCorrupted; }
    public double getRecoveryQuality() { return recoveryQuality; }
    public byte[] getFilePreview() { return filePreview; }
    public String getChecksum() { return checksum; }

    // Setters
    public void setFilePath(String filePath) { this.filePath = filePath; }
    public void setRecovered(boolean recovered) { this.isRecovered = recovered; }
    public void setCorrupted(boolean corrupted) { this.isCorrupted = corrupted; }
    public void setRecoveryQuality(double quality) { this.recoveryQuality = quality; }
    public void setFilePreview(byte[] preview) { this.filePreview = preview; }
    public void setChecksum(String checksum) { this.checksum = checksum; }

    // Métodos utilitários
    public String getFormattedSize() {
        return formatBytes(fileSize);
    }

    public String getFormattedRecoveryDate() {
        return recoveryDate.toString();
    }

    public String getQualityStatus() {
        if (recoveryQuality >= 90) return "Excelente";
        if (recoveryQuality >= 70) return "Boa";
        if (recoveryQuality >= 50) return "Regular";
        return "Ruim";
    }

    public boolean isPreviewAvailable() {
        return filePreview != null && filePreview.length > 0;
    }

    public boolean isImageFile() {
        String ext = fileExtension.toLowerCase();
        return ext.equals("jpg") || ext.equals("jpeg") || ext.equals("png") ||
                ext.equals("gif") || ext.equals("bmp");
    }

    public boolean isVideoFile() {
        String ext = fileExtension.toLowerCase();
        return ext.equals("mp4") || ext.equals("avi") || ext.equals("mkv") || ext.equals("mov");
    }

    public boolean isDocumentFile() {
        String ext = fileExtension.toLowerCase();
        return ext.equals("pdf") || ext.equals("doc") || ext.equals("docx") ||
                ext.equals("xlsx") || ext.equals("txt");
    }

    private String formatBytes(long bytes) {
        if (bytes < 1024) return bytes + " B";
        if (bytes < 1024 * 1024) return String.format("%.1f KB", bytes / 1024.0);
        if (bytes < 1024 * 1024 * 1024) return String.format("%.1f MB", bytes / (1024.0 * 1024));
        return String.format("%.1f GB", bytes / (1024.0 * 1024 * 1024));
    }

    @Override
    public String toString() {
        return String.format(
                "File: %s\nType: %s\nSize: %s\nOffset: %d - %d\nRecovered: %s\nQuality: %.1f%% (%s)",
                fileName, fileType, getFormattedSize(), startOffset, endOffset,
                isRecovered ? "Yes" : "No", recoveryQuality, getQualityStatus()
        );
    }
}