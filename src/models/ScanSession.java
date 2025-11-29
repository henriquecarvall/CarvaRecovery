package models;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class ScanSession {
    private String sessionId;
    private Date startTime;
    private Date endTime;
    private String devicePath;
    private String scanType;
    private List<String> targetFormats;
    private List<RecoveredFile> recoveredFiles;
    private ScanConfig config;
    private RecoveryStats stats;
    private String status;
    private boolean isCompleted;

    public ScanSession(String devicePath, String scanType, List<String> targetFormats, ScanConfig config) {
        this.sessionId = generateSessionId();
        this.startTime = new Date();
        this.devicePath = devicePath;
        this.scanType = scanType;
        this.targetFormats = new ArrayList<>(targetFormats);
        this.recoveredFiles = new CopyOnWriteArrayList<>();
        this.config = config;
        this.stats = new RecoveryStats();
        this.status = "Iniciando";
        this.isCompleted = false;
    }

    private String generateSessionId() {
        return "SESS_" + System.currentTimeMillis() + "_" + new Random().nextInt(1000);
    }

    // Getters
    public String getSessionId() { return sessionId; }
    public Date getStartTime() { return startTime; }
    public Date getEndTime() { return endTime; }
    public String getDevicePath() { return devicePath; }
    public String getScanType() { return scanType; }
    public List<String> getTargetFormats() { return Collections.unmodifiableList(targetFormats); }
    public List<RecoveredFile> getRecoveredFiles() { return Collections.unmodifiableList(recoveredFiles); }
    public ScanConfig getConfig() { return config; }
    public RecoveryStats getStats() { return stats; }
    public String getStatus() { return status; }
    public boolean isCompleted() { return isCompleted; }

    // Setters
    public void setEndTime(Date endTime) { this.endTime = endTime; }
    public void setStatus(String status) { this.status = status; }
    public void setCompleted(boolean completed) {
        this.isCompleted = completed;
        if (completed && endTime == null) {
            this.endTime = new Date();
        }
    }

    // Métodos de gerenciamento de arquivos
    public void addRecoveredFile(RecoveredFile file) {
        recoveredFiles.add(file);
        stats.updateStats(file);
    }

    public void removeRecoveredFile(RecoveredFile file) {
        recoveredFiles.remove(file);
        stats.removeFileFromStats(file);
    }

    public List<RecoveredFile> getFilesByFormat(String format) {
        List<RecoveredFile> result = new ArrayList<>();
        for (RecoveredFile file : recoveredFiles) {
            if (file.getFileExtension().equalsIgnoreCase(format)) {
                result.add(file);
            }
        }
        return result;
    }

    public List<RecoveredFile> getRecoveredFiles(boolean onlyRecovered) {
        if (!onlyRecovered) {
            return new ArrayList<>(recoveredFiles);
        }

        List<RecoveredFile> result = new ArrayList<>();
        for (RecoveredFile file : recoveredFiles) {
            if (file.isRecovered()) {
                result.add(file);
            }
        }
        return result;
    }

    // Métodos de tempo
    public long getDuration() {
        Date end = endTime != null ? endTime : new Date();
        return end.getTime() - startTime.getTime();
    }

    public String getFormattedDuration() {
        long duration = getDuration();
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

    // Métodos de progresso
    public double getProgressPercentage() {
        if (isCompleted) return 100.0;
        return stats.getProgressPercentage();
    }

    public int getFilesFoundCount() {
        return recoveredFiles.size();
    }

    public int getFilesRecoveredCount() {
        return (int) recoveredFiles.stream().filter(RecoveredFile::isRecovered).count();
    }

    @Override
    public String toString() {
        return String.format(
                "Session: %s\nDevice: %s\nType: %s\nStarted: %s\nDuration: %s\nFiles: %d found, %d recovered\nStatus: %s",
                sessionId, devicePath, scanType, startTime, getFormattedDuration(),
                getFilesFoundCount(), getFilesRecoveredCount(), status
        );
    }
}