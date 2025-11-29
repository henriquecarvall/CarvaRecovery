package models;

public class ScanConfig {
    public enum ScanDepth {
        QUICK,      // Scan rápido - apenas áreas conhecidas
        STANDARD,   // Scan padrão - áreas com maior probabilidade
        DEEP,       // Scan profundo - dispositivo inteiro
        CUSTOM      // Scan personalizado
    }

    public enum FileSystem {
        ALL,        // Todos os sistemas de arquivos
        NTFS,       // Windows NTFS
        FAT32,      // FAT32
        EXFAT,      // exFAT
        UNKNOWN     // Desconhecido/corrompido
    }

    private ScanDepth scanDepth;
    private FileSystem targetFileSystem;
    private long maxScanSize;
    private boolean recoverFragmentedFiles;
    private boolean ignoreSystemFiles;
    private boolean createPreviews;
    private boolean verifyChecksums;
    private int threadCount;
    private String outputDirectory;

    public ScanConfig() {
        // Valores padrão
        this.scanDepth = ScanDepth.STANDARD;
        this.targetFileSystem = FileSystem.ALL;
        this.maxScanSize = 2L * 1024 * 1024 * 1024; // 2GB
        this.recoverFragmentedFiles = true;
        this.ignoreSystemFiles = true;
        this.createPreviews = true;
        this.verifyChecksums = false;
        this.threadCount = Runtime.getRuntime().availableProcessors();
        this.outputDirectory = System.getProperty("user.home") + "/CarvaRecovery";
    }

    // Getters
    public ScanDepth getScanDepth() { return scanDepth; }
    public FileSystem getTargetFileSystem() { return targetFileSystem; }
    public long getMaxScanSize() { return maxScanSize; }
    public boolean shouldRecoverFragmentedFiles() { return recoverFragmentedFiles; }
    public boolean shouldIgnoreSystemFiles() { return ignoreSystemFiles; }
    public boolean shouldCreatePreviews() { return createPreviews; }
    public boolean shouldVerifyChecksums() { return verifyChecksums; }
    public int getThreadCount() { return threadCount; }
    public String getOutputDirectory() { return outputDirectory; }

    // Setters
    public void setScanDepth(ScanDepth scanDepth) { this.scanDepth = scanDepth; }
    public void setTargetFileSystem(FileSystem targetFileSystem) { this.targetFileSystem = targetFileSystem; }
    public void setMaxScanSize(long maxScanSize) { this.maxScanSize = maxScanSize; }
    public void setRecoverFragmentedFiles(boolean recoverFragmentedFiles) { this.recoverFragmentedFiles = recoverFragmentedFiles; }
    public void setIgnoreSystemFiles(boolean ignoreSystemFiles) { this.ignoreSystemFiles = ignoreSystemFiles; }
    public void setCreatePreviews(boolean createPreviews) { this.createPreviews = createPreviews; }
    public void setVerifyChecksums(boolean verifyChecksums) { this.verifyChecksums = verifyChecksums; }
    public void setThreadCount(int threadCount) { this.threadCount = threadCount; }
    public void setOutputDirectory(String outputDirectory) { this.outputDirectory = outputDirectory; }

    // Métodos utilitários
    public String getFormattedMaxScanSize() {
        return formatBytes(maxScanSize);
    }

    public boolean isQuickScan() {
        return scanDepth == ScanDepth.QUICK;
    }

    public boolean isDeepScan() {
        return scanDepth == ScanDepth.DEEP;
    }

    public boolean isCustomScan() {
        return scanDepth == ScanDepth.CUSTOM;
    }

    public String getScanDepthDescription() {
        switch (scanDepth) {
            case QUICK: return "Scan Rápido (áreas conhecidas)";
            case STANDARD: return "Scan Padrão (alta probabilidade)";
            case DEEP: return "Scan Profundo (dispositivo inteiro)";
            case CUSTOM: return "Scan Personalizado";
            default: return "Desconhecido";
        }
    }

    public String getFileSystemDescription() {
        switch (targetFileSystem) {
            case ALL: return "Todos os Sistemas";
            case NTFS: return "NTFS (Windows)";
            case FAT32: return "FAT32";
            case EXFAT: return "exFAT";
            case UNKNOWN: return "Desconhecido/Corrompido";
            default: return "Desconhecido";
        }
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
                "Configurações do Scan:\n" +
                        "Profundidade: %s\n" +
                        "Sistema de Arquivos: %s\n" +
                        "Tamanho Máximo: %s\n" +
                        "Arquivos Fragmentados: %s\n" +
                        "Ignorar Sistema: %s\n" +
                        "Previews: %s\n" +
                        "Verificar Checksums: %s\n" +
                        "Threads: %d\n" +
                        "Diretório de Saída: %s",
                getScanDepthDescription(), getFileSystemDescription(), getFormattedMaxScanSize(),
                recoverFragmentedFiles ? "Sim" : "Não", ignoreSystemFiles ? "Sim" : "Não",
                createPreviews ? "Sim" : "Não", verifyChecksums ? "Sim" : "Não",
                threadCount, outputDirectory
        );
    }
}