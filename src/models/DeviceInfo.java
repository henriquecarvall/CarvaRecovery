package models;

import java.io.File;

public class DeviceInfo {
    private File device;
    private String name;
    private String path;
    private long totalSpace;
    private long freeSpace;
    private long usedSpace;
    private String fileSystem;
    private boolean isRemovable;
    private boolean isReadable;
    private String healthStatus;
    private int badSectors;

    public DeviceInfo(File device) {
        this.device = device;
        this.path = device.getAbsolutePath();
        this.name = generateDeviceName(device);
        updateSpaceInfo();
    }

    private String generateDeviceName(File device) {
        String path = device.getAbsolutePath().toUpperCase();
        if (path.equals("C:") || path.startsWith("C:\\")) {
            return "HD Interno (Sistema C:)";
        } else if (path.startsWith("D:") || path.startsWith("E:") || path.startsWith("F:")) {
            return "HD Interno (" + path.charAt(0) + ":)";
        } else {
            return "Dispositivo Removível (" + path + ")";
        }
    }

    public void updateSpaceInfo() {
        this.totalSpace = device.getTotalSpace();
        this.freeSpace = device.getFreeSpace();
        this.usedSpace = totalSpace - freeSpace;
        this.isReadable = device.canRead();

        // Detecta se é removível (simplificado)
        this.isRemovable = !path.startsWith("C:") && !path.startsWith("D:");
    }

    // Getters
    public File getDevice() { return device; }
    public String getName() { return name; }
    public String getPath() { return path; }
    public long getTotalSpace() { return totalSpace; }
    public long getFreeSpace() { return freeSpace; }
    public long getUsedSpace() { return usedSpace; }
    public String getFileSystem() { return fileSystem; }
    public boolean isRemovable() { return isRemovable; }
    public boolean isReadable() { return isReadable; }
    public String getHealthStatus() { return healthStatus; }
    public int getBadSectors() { return badSectors; }

    // Setters
    public void setFileSystem(String fileSystem) { this.fileSystem = fileSystem; }
    public void setHealthStatus(String healthStatus) { this.healthStatus = healthStatus; }
    public void setBadSectors(int badSectors) { this.badSectors = badSectors; }

    // Métodos utilitários
    public double getUsedPercentage() {
        return totalSpace > 0 ? (usedSpace * 100.0) / totalSpace : 0;
    }

    public double getFreePercentage() {
        return totalSpace > 0 ? (freeSpace * 100.0) / totalSpace : 0;
    }

    public String getFormattedTotalSpace() {
        return formatBytes(totalSpace);
    }

    public String getFormattedUsedSpace() {
        return formatBytes(usedSpace);
    }

    public String getFormattedFreeSpace() {
        return formatBytes(freeSpace);
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
                "Device: %s\nPath: %s\nTotal: %s\nUsed: %s (%.1f%%)\nFree: %s (%.1f%%)\nReadable: %s\nRemovable: %s",
                name, path, getFormattedTotalSpace(), getFormattedUsedSpace(), getUsedPercentage(),
                getFormattedFreeSpace(), getFreePercentage(), isReadable, isRemovable
        );
    }
}