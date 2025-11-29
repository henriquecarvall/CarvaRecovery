package engine;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.FileStore;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class DeviceAnalyzer {
    private File device;
    private DeviceAnalysisResult analysisResult;

    public DeviceAnalyzer(File device) {
        this.device = device;
        this.analysisResult = new DeviceAnalysisResult();
    }

    public static class DeviceAnalysisResult {
        private long totalSpace;
        private long freeSpace;
        private long usedSpace;
        private long sectorSize;
        private long totalSectors;
        private long badSectors;
        private List<Long> badSectorList;
        private double healthPercentage;
        private String fileSystem;
        private boolean isReadable;
        private String analysisStatus;

        public DeviceAnalysisResult() {
            this.badSectorList = new ArrayList<>();
        }

        // Getters
        public long getTotalSpace() { return totalSpace; }
        public long getFreeSpace() { return freeSpace; }
        public long getUsedSpace() { return usedSpace; }
        public long getSectorSize() { return sectorSize; }
        public long getTotalSectors() { return totalSectors; }
        public long getBadSectors() { return badSectors; }
        public List<Long> getBadSectorList() { return badSectorList; }
        public double getHealthPercentage() { return healthPercentage; }
        public String getFileSystem() { return fileSystem; }
        public boolean isReadable() { return isReadable; }
        public String getAnalysisStatus() { return analysisStatus; }

        // Setters
        public void setTotalSpace(long totalSpace) { this.totalSpace = totalSpace; }
        public void setFreeSpace(long freeSpace) { this.freeSpace = freeSpace; }
        public void setUsedSpace(long usedSpace) { this.usedSpace = usedSpace; }
        public void setSectorSize(long sectorSize) { this.sectorSize = sectorSize; }
        public void setTotalSectors(long totalSectors) { this.totalSectors = totalSectors; }
        public void setBadSectors(long badSectors) { this.badSectors = badSectors; }
        public void setHealthPercentage(double healthPercentage) { this.healthPercentage = healthPercentage; }
        public void setFileSystem(String fileSystem) { this.fileSystem = fileSystem; }
        public void setReadable(boolean readable) { isReadable = readable; }
        public void setAnalysisStatus(String analysisStatus) { this.analysisStatus = analysisStatus; }

        public void addBadSector(long sector) {
            this.badSectorList.add(sector);
        }

        @Override
        public String toString() {
            return String.format(
                    "Dispositivo: %s\n" +
                            "Espaço Total: %s\n" +
                            "Espaço Usado: %s\n" +
                            "Espaço Livre: %s\n" +
                            "Setores: %d (%s cada)\n" +
                            "Setores Defeituosos: %d\n" +
                            "Saúde: %.1f%%\n" +
                            "Sistema de Arquivos: %s\n" +
                            "Legível: %s\n" +
                            "Status: %s",
                    "Analisado",
                    formatSize(totalSpace),
                    formatSize(usedSpace),
                    formatSize(freeSpace),
                    totalSectors, formatSize(sectorSize),
                    badSectors,
                    healthPercentage,
                    fileSystem != null ? fileSystem : "Desconhecido",
                    isReadable ? "Sim" : "Não",
                    analysisStatus
            );
        }
    }

    public interface AnalysisListener {
        void onProgressUpdate(int progress, String status);
        void onAnalysisComplete(DeviceAnalysisResult result);
        void onError(String errorMessage);
    }

    public void performAnalysis(AnalysisListener listener) {
        new Thread(() -> {
            try {
                performCompleteAnalysis(listener);
            } catch (Exception e) {
                if (listener != null) {
                    listener.onError("Erro na análise: " + e.getMessage());
                }
            }
        }).start();
    }

    private void performCompleteAnalysis(AnalysisListener listener) {
        if (listener != null) {
            listener.onProgressUpdate(0, "Iniciando análise do dispositivo...");
        }

        // Informações básicas do dispositivo
        analyzeBasicInfo(listener);

        if (listener != null) {
            listener.onProgressUpdate(30, "Verificando setores defeituosos...");
        }

        // Verificação de setores defeituosos
        checkForBadSectors(listener);

        if (listener != null) {
            listener.onProgressUpdate(80, "Calculando saúde do dispositivo...");
        }

        // Cálculo da saúde do dispositivo
        calculateDeviceHealth();

        analysisResult.setAnalysisStatus("Análise concluída com sucesso");

        if (listener != null) {
            listener.onProgressUpdate(100, "Análise concluída!");
            listener.onAnalysisComplete(analysisResult);
        }
    }

    private void analyzeBasicInfo(AnalysisListener listener) {
        try {
            // Espaço total, livre e usado
            long totalSpace = device.getTotalSpace();
            long freeSpace = device.getFreeSpace();
            long usedSpace = totalSpace - freeSpace;

            analysisResult.setTotalSpace(totalSpace);
            analysisResult.setFreeSpace(freeSpace);
            analysisResult.setUsedSpace(usedSpace);

            // Tamanho do setor (assume 512 bytes como padrão)
            analysisResult.setSectorSize(512);
            analysisResult.setTotalSectors(totalSpace / 512);

            // Sistema de arquivos
            try {
                Path devicePath = device.toPath();
                FileStore store = Files.getFileStore(devicePath);
                analysisResult.setFileSystem(store.type());
            } catch (Exception e) {
                analysisResult.setFileSystem("Desconhecido");
            }

            // Verifica se é legível
            analysisResult.setReadable(device.canRead());

            if (listener != null) {
                listener.onProgressUpdate(20, "Informações básicas coletadas");
            }

        } catch (Exception e) {
            throw new RuntimeException("Erro ao analisar informações básicas: " + e.getMessage());
        }
    }

    private void checkForBadSectors(AnalysisListener listener) {
        long totalSectors = analysisResult.getTotalSectors();
        long sectorsToCheck = Math.min(totalSectors, 10000); // Limita a 10000 setores para performance

        int badSectorsFound = 0;

        try (RandomAccessFile raf = new RandomAccessFile(device, "r")) {
            byte[] buffer = new byte[512]; // Tamanho do setor

            for (long sector = 0; sector < sectorsToCheck; sector++) {
                if (sector % 1000 == 0 && listener != null) {
                    int progress = 30 + (int)((sector * 50) / sectorsToCheck);
                    listener.onProgressUpdate(progress,
                            String.format("Verificando setores... %d/%d", sector, sectorsToCheck));
                }

                try {
                    long position = sector * 512;
                    if (position < device.length()) {
                        raf.seek(position);
                        int bytesRead = raf.read(buffer);

                        if (bytesRead == -1) {
                            // Erro de leitura - setor defeituoso
                            analysisResult.addBadSector(sector);
                            badSectorsFound++;
                        }
                    }
                } catch (IOException e) {
                    // Setor defeituoso
                    analysisResult.addBadSector(sector);
                    badSectorsFound++;
                }
            }

            analysisResult.setBadSectors(badSectorsFound);

        } catch (IOException e) {
            throw new RuntimeException("Erro ao verificar setores: " + e.getMessage());
        }
    }

    private void calculateDeviceHealth() {
        long totalSectors = analysisResult.getTotalSectors();
        long badSectors = analysisResult.getBadSectors();

        if (totalSectors > 0) {
            double health = 100.0 - ((badSectors * 100.0) / totalSectors);
            analysisResult.setHealthPercentage(Math.max(0, health));
        } else {
            analysisResult.setHealthPercentage(100.0);
        }
    }

    public DeviceAnalysisResult getQuickAnalysis() {
        try {
            analyzeBasicInfo(null);
            calculateDeviceHealth();
            analysisResult.setAnalysisStatus("Análise rápida concluída");
            return analysisResult;
        } catch (Exception e) {
            analysisResult.setAnalysisStatus("Erro na análise rápida: " + e.getMessage());
            return analysisResult;
        }
    }

    public DeviceAnalysisResult getAnalysisResult() {
        return analysisResult;
    }

    private static String formatSize(long bytes) {
        if (bytes < 1024) return bytes + " B";
        if (bytes < 1024 * 1024) return (bytes / 1024) + " KB";
        if (bytes < 1024 * 1024 * 1024) return (bytes / (1024 * 1024)) + " MB";
        return (bytes / (1024 * 1024 * 1024)) + " GB";
    }

    // Método utilitário para verificar se um dispositivo é analisável
    public static boolean isDeviceAnalyzable(File device) {
        return device.exists() && device.canRead() && device.getTotalSpace() > 0;
    }
}