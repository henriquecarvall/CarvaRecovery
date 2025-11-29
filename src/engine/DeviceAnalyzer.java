package engine;

import java.io.File;
import java.nio.file.FileStore;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class DeviceAnalyzer {
    private List<StorageDevice> devices;

    public DeviceAnalyzer() {
        this.devices = new ArrayList<>();
    }

    public List<StorageDevice> analyzeSystemDevices() {
        devices.clear();

        try {
            Iterable<Path> roots = FileSystems.getDefault().getRootDirectories();

            for (Path root : roots) {
                try {
                    FileStore store = Files.getFileStore(root);
                    StorageDevice device = new StorageDevice(
                            root.toString(),
                            store.name(),
                            store.getTotalSpace(),
                            store.getUsableSpace(),
                            store.getTotalSpace() - store.getUsableSpace(),
                            getFileSystemType(store.type())
                    );
                    devices.add(device);
                } catch (Exception e) {
                    System.err.println("Erro ao analisar dispositivo: " + root + " - " + e.getMessage());
                }
            }

            // Adicionar dispositivos físicos no Windows
            if (System.getProperty("os.name").toLowerCase().contains("win")) {
                analyzePhysicalDrives();
            }

        } catch (Exception e) {
            System.err.println("Erro na análise de dispositivos: " + e.getMessage());
        }

        return devices;
    }

    private void analyzePhysicalDrives() {
        File[] roots = File.listRoots();
        for (File root : roots) {
            try {
                StorageDevice device = new StorageDevice(
                        root.getAbsolutePath(),
                        "Disco Local",
                        root.getTotalSpace(),
                        root.getFreeSpace(),
                        root.getTotalSpace() - root.getFreeSpace(),
                        "NTFS/FAT32"
                );
                devices.add(device);
            } catch (Exception e) {
                System.err.println("Erro ao analisar drive físico: " + root + " - " + e.getMessage());
            }
        }
    }

    private String getFileSystemType(String type) {
        if (type == null || type.isEmpty()) return "Desconhecido";
        return type.toUpperCase();
    }

    public StorageDevice getDeviceByPath(String path) {
        return devices.stream()
                .filter(device -> device.getMountPoint().equals(path))
                .findFirst()
                .orElse(null);
    }

    public static class StorageDevice {
        private String mountPoint;
        private String name;
        private long totalSpace;
        private long freeSpace;
        private long usedSpace;
        private String fileSystem;

        public StorageDevice(String mountPoint, String name, long totalSpace,
                             long freeSpace, long usedSpace, String fileSystem) {
            this.mountPoint = mountPoint;
            this.name = name;
            this.totalSpace = totalSpace;
            this.freeSpace = freeSpace;
            this.usedSpace = usedSpace;
            this.fileSystem = fileSystem;
        }

        // Getters
        public String getMountPoint() { return mountPoint; }
        public String getName() { return name; }
        public long getTotalSpace() { return totalSpace; }
        public long getFreeSpace() { return freeSpace; }
        public long getUsedSpace() { return usedSpace; }
        public String getFileSystem() { return fileSystem; }

        public double getUsagePercentage() {
            return totalSpace > 0 ? (double) usedSpace / totalSpace * 100 : 0;
        }

        public String getFormattedSize() {
            DecimalFormat df = new DecimalFormat("#.##");
            if (totalSpace >= 1_000_000_000_000L) {
                return df.format(totalSpace / 1_000_000_000_000.0) + " TB";
            } else if (totalSpace >= 1_000_000_000L) {
                return df.format(totalSpace / 1_000_000_000.0) + " GB";
            } else if (totalSpace >= 1_000_000L) {
                return df.format(totalSpace / 1_000_000.0) + " MB";
            } else {
                return df.format(totalSpace / 1_000.0) + " KB";
            }
        }
    }
}