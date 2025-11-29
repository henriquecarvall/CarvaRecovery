package models;

import java.util.*;

public class RecoveryStats {
    private int totalFilesFound;
    private int totalFilesRecovered;
    private long totalBytesProcessed;
    private long totalBytesRecovered;
    private Map<String, Integer> filesByFormat;
    private Map<String, Long> bytesByFormat;
    private Date statsStartTime;
    private double averageRecoveryQuality;
    private int corruptedFiles;

    public RecoveryStats() {
        this.totalFilesFound = 0;
        this.totalFilesRecovered = 0;
        this.totalBytesProcessed = 0;
        this.totalBytesRecovered = 0;
        this.filesByFormat = new HashMap<>();
        this.bytesByFormat = new HashMap<>();
        this.statsStartTime = new Date();
        this.averageRecoveryQuality = 0.0;
        this.corruptedFiles = 0;
    }

    public void updateStats(RecoveredFile file) {
        totalFilesFound++;
        totalBytesProcessed += file.getFileSize();

        String format = file.getFileExtension().toLowerCase();
        filesByFormat.put(format, filesByFormat.getOrDefault(format, 0) + 1);
        bytesByFormat.put(format, bytesByFormat.getOrDefault(format, 0L) + file.getFileSize());

        if (file.isRecovered()) {
            totalFilesRecovered++;
            totalBytesRecovered += file.getFileSize();
        }

        if (file.isCorrupted()) {
            corruptedFiles++;
        }

        // Atualiza qualidade média
        if (file.getRecoveryQuality() > 0) {
            averageRecoveryQuality = ((averageRecoveryQuality * (totalFilesFound - 1)) + file.getRecoveryQuality()) / totalFilesFound;
        }
    }

    public void removeFileFromStats(RecoveredFile file) {
        totalFilesFound--;
        totalBytesProcessed -= file.getFileSize();

        String format = file.getFileExtension().toLowerCase();
        filesByFormat.put(format, filesByFormat.get(format) - 1);
        bytesByFormat.put(format, bytesByFormat.get(format) - file.getFileSize());

        if (file.isRecovered()) {
            totalFilesRecovered--;
            totalBytesRecovered -= file.getFileSize();
        }

        if (file.isCorrupted()) {
            corruptedFiles--;
        }
    }

    // Getters
    public int getTotalFilesFound() { return totalFilesFound; }
    public int getTotalFilesRecovered() { return totalFilesRecovered; }
    public long getTotalBytesProcessed() { return totalBytesProcessed; }
    public long getTotalBytesRecovered() { return totalBytesRecovered; }
    public Map<String, Integer> getFilesByFormat() { return Collections.unmodifiableMap(filesByFormat); }
    public Map<String, Long> getBytesByFormat() { return Collections.unmodifiableMap(bytesByFormat); }
    public Date getStatsStartTime() { return statsStartTime; }
    public double getAverageRecoveryQuality() { return averageRecoveryQuality; }
    public int getCorruptedFiles() { return corruptedFiles; }

    // Métodos calculados
    public double getRecoveryRate() {
        return totalFilesFound > 0 ? (totalFilesRecovered * 100.0) / totalFilesFound : 0.0;
    }

    public double getBytesRecoveryRate() {
        return totalBytesProcessed > 0 ? (totalBytesRecovered * 100.0) / totalBytesProcessed : 0.0;
    }

    public double getProgressPercentage() {
        // Simula progresso baseado em heurísticas
        if (totalFilesFound == 0) return 0.0;
        return Math.min(100.0, (totalFilesFound * 100.0) / Math.max(totalFilesFound, 100));
    }

    public String getTopFormat() {
        return filesByFormat.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("N/A");
    }

    public int getFilesByFormatCount(String format) {
        return filesByFormat.getOrDefault(format.toLowerCase(), 0);
    }

    public long getBytesByFormat(String format) {
        return bytesByFormat.getOrDefault(format.toLowerCase(), 0L);
    }

    public String getFormattedTotalBytesProcessed() {
        return formatBytes(totalBytesProcessed);
    }

    public String getFormattedTotalBytesRecovered() {
        return formatBytes(totalBytesRecovered);
    }

    public String getFormattedAverageQuality() {
        return String.format("%.1f%%", averageRecoveryQuality);
    }

    public long getScanDuration() {
        return new Date().getTime() - statsStartTime.getTime();
    }

    public String getFormattedScanDuration() {
        long duration = getScanDuration();
        long seconds = duration / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;

        if (hours > 0) {
            return String.format("%dh %dm %ds", hours, minutes % 60, seconds % 60);
        } else if (minutes > 0) {
            return String.format("%dm %ds", minutes, seconds % 60);
        } else {
            return String.format("%ds", seconds);
        }
    }

    public double getFilesPerSecond() {
        long duration = getScanDuration() / 1000;
        return duration > 0 ? totalFilesFound / (double) duration : 0.0;
    }

    public double getBytesPerSecond() {
        long duration = getScanDuration() / 1000;
        return duration > 0 ? totalBytesProcessed / (double) duration : 0.0;
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
                "Estatísticas da Recuperação:\n" +
                        "Arquivos Encontrados: %d\n" +
                        "Arquivos Recuperados: %d (%.1f%%)\n" +
                        "Bytes Processados: %s\n" +
                        "Bytes Recuperados: %s (%.1f%%)\n" +
                        "Qualidade Média: %s\n" +
                        "Duração: %s\n" +
                        "Arquivos/Segundo: %.2f",
                totalFilesFound, totalFilesRecovered, getRecoveryRate(),
                getFormattedTotalBytesProcessed(), getFormattedTotalBytesRecovered(), getBytesRecoveryRate(),
                getFormattedAverageQuality(), getFormattedScanDuration(), getFilesPerSecond()
        );
    }
}