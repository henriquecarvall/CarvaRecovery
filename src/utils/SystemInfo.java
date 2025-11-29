package utils;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.text.DecimalFormat;

public class SystemInfo {

    private static final DecimalFormat DF = new DecimalFormat("#.##");

    public static String getOSName() {
        return System.getProperty("os.name") + " " + System.getProperty("os.version");
    }

    public static String getJavaVersion() {
        return System.getProperty("java.version");
    }

    public static String getArchitecture() {
        return System.getProperty("os.arch");
    }

    public static long getTotalMemory() {
        return Runtime.getRuntime().totalMemory();
    }

    public static long getFreeMemory() {
        return Runtime.getRuntime().freeMemory();
    }

    public static long getUsedMemory() {
        return getTotalMemory() - getFreeMemory();
    }

    public static long getMaxMemory() {
        return Runtime.getRuntime().maxMemory();
    }

    public static String getFormattedMemoryUsage() {
        long used = getUsedMemory();
        long total = getTotalMemory();
        long max = getMaxMemory();

        return String.format("Usada: %s / %s (Máx: %s)",
                formatBytes(used), formatBytes(total), formatBytes(max));
    }

    public static double getMemoryUsagePercentage() {
        return (double) getUsedMemory() / getTotalMemory() * 100;
    }

    public static int getAvailableProcessors() {
        return Runtime.getRuntime().availableProcessors();
    }

    public static String getCPUUsage() {
        try {
            OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();
            if (osBean instanceof com.sun.management.OperatingSystemMXBean) {
                com.sun.management.OperatingSystemMXBean sunOsBean =
                        (com.sun.management.OperatingSystemMXBean) osBean;
                double load = sunOsBean.getSystemCpuLoad() * 100;
                return DF.format(load) + "%";
            }
        } catch (Exception e) {
            // Fallback para sistemas não compatíveis
        }
        return "N/A";
    }

    public static String getDiskSpace(File path) {
        if (path.exists()) {
            long total = path.getTotalSpace();
            long free = path.getFreeSpace();
            long used = total - free;

            return String.format("Usado: %s / %s (Livre: %s)",
                    formatBytes(used), formatBytes(total), formatBytes(free));
        }
        return "N/A";
    }

    public static double getDiskUsagePercentage(File path) {
        if (path.exists()) {
            long total = path.getTotalSpace();
            long free = path.getFreeSpace();
            long used = total - free;

            return total > 0 ? (double) used / total * 100 : 0;
        }
        return 0;
    }

    public static String getSystemSummary() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== Informações do Sistema ===\n");
        sb.append("Sistema Operacional: ").append(getOSName()).append("\n");
        sb.append("Arquitetura: ").append(getArchitecture()).append("\n");
        sb.append("Java Version: ").append(getJavaVersion()).append("\n");
        sb.append("Processadores: ").append(getAvailableProcessors()).append("\n");
        sb.append("Uso de CPU: ").append(getCPUUsage()).append("\n");
        sb.append("Memória: ").append(getFormattedMemoryUsage()).append("\n");

        // Informações do disco do sistema
        File systemDrive = new File("C:"); // Windows
        if (!systemDrive.exists()) {
            systemDrive = new File("/"); // Linux/Mac
        }
        sb.append("Disco Sistema: ").append(getDiskSpace(systemDrive)).append("\n");

        return sb.toString();
    }

    public static boolean isWindows() {
        return System.getProperty("os.name").toLowerCase().contains("windows");
    }

    public static boolean isLinux() {
        return System.getProperty("os.name").toLowerCase().contains("linux");
    }

    public static boolean isMac() {
        return System.getProperty("os.name").toLowerCase().contains("mac");
    }

    public static boolean is64Bit() {
        return System.getProperty("os.arch").contains("64");
    }

    public static String getTempDirectory() {
        return System.getProperty("java.io.tmpdir");
    }

    public static String getUserHome() {
        return System.getProperty("user.home");
    }

    public static String getWorkingDirectory() {
        return System.getProperty("user.dir");
    }

    public static void printSystemInfo() {
        System.out.println(getSystemSummary());
    }

    public static boolean hasSufficientMemory(long requiredBytes) {
        long availableMemory = getFreeMemory() + (getMaxMemory() - getTotalMemory());
        return availableMemory >= requiredBytes;
    }

    public static boolean hasSufficientDiskSpace(File path, long requiredBytes) {
        return path.getFreeSpace() >= requiredBytes;
    }

    private static String formatBytes(long bytes) {
        if (bytes < 1024) return bytes + " B";
        if (bytes < 1024 * 1024) return DF.format(bytes / 1024.0) + " KB";
        if (bytes < 1024 * 1024 * 1024) return DF.format(bytes / (1024.0 * 1024)) + " MB";
        return DF.format(bytes / (1024.0 * 1024 * 1024)) + " GB";
    }
}