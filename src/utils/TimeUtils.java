package utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class TimeUtils {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm:ss");
    private static final SimpleDateFormat FILE_DATE_FORMAT = new SimpleDateFormat("yyyyMMdd_HHmmss");

    public static String formatDate(Date date) {
        return date != null ? DATE_FORMAT.format(date) : "N/A";
    }

    public static String formatTime(Date date) {
        return date != null ? TIME_FORMAT.format(date) : "N/A";
    }

    public static String formatFileDate(Date date) {
        return FILE_DATE_FORMAT.format(date);
    }

    public static String formatDuration(long milliseconds) {
        if (milliseconds < 1000) {
            return milliseconds + "ms";
        }

        long seconds = milliseconds / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;

        if (days > 0) {
            return String.format("%dd %dh %dm %ds", days, hours % 24, minutes % 60, seconds % 60);
        } else if (hours > 0) {
            return String.format("%dh %dm %ds", hours, minutes % 60, seconds % 60);
        } else if (minutes > 0) {
            return String.format("%dm %ds", minutes, seconds % 60);
        } else {
            return String.format("%ds", seconds);
        }
    }

    public static String formatDurationShort(long milliseconds) {
        long seconds = milliseconds / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;

        if (hours > 0) {
            return String.format("%02d:%02d:%02d", hours, minutes % 60, seconds % 60);
        } else {
            return String.format("%02d:%02d", minutes, seconds % 60);
        }
    }

    public static String getCurrentTimestamp() {
        return formatDate(new Date());
    }

    public static String getCurrentTime() {
        return formatTime(new Date());
    }

    public static String getTimestampForFilename() {
        return FILE_DATE_FORMAT.format(new Date());
    }

    public static long calculateRemainingTime(long startTime, int progress, int total) {
        if (progress <= 0 || total <= 0) return 0;

        long elapsed = System.currentTimeMillis() - startTime;
        double itemsPerMs = (double) progress / elapsed;

        if (itemsPerMs <= 0) return 0;

        long remainingItems = total - progress;
        return (long) (remainingItems / itemsPerMs);
    }

    public static String formatRemainingTime(long remainingMs) {
        if (remainingMs <= 0) return "Calculando...";

        long seconds = TimeUnit.MILLISECONDS.toSeconds(remainingMs) % 60;
        long minutes = TimeUnit.MILLISECONDS.toMinutes(remainingMs) % 60;
        long hours = TimeUnit.MILLISECONDS.toHours(remainingMs);

        if (hours > 0) {
            return String.format("%dh %dm %ds", hours, minutes, seconds);
        } else if (minutes > 0) {
            return String.format("%dm %ds", minutes, seconds);
        } else {
            return String.format("%ds", seconds);
        }
    }

    public static boolean isOlderThan(Date date, long minutes) {
        if (date == null) return false;

        long diff = System.currentTimeMillis() - date.getTime();
        return diff > (minutes * 60 * 1000);
    }

    public static Date addMinutes(Date date, int minutes) {
        return new Date(date.getTime() + (minutes * 60 * 1000L));
    }

    public static Date subtractMinutes(Date date, int minutes) {
        return new Date(date.getTime() - (minutes * 60 * 1000L));
    }

    public static long getDaysBetween(Date start, Date end) {
        long diff = end.getTime() - start.getTime();
        return TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
    }

    public static String getRelativeTime(Date date) {
        if (date == null) return "Nunca";

        long diff = System.currentTimeMillis() - date.getTime();
        long seconds = diff / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;

        if (days > 0) {
            return days + " dia" + (days > 1 ? "s" : "") + " atrás";
        } else if (hours > 0) {
            return hours + " hora" + (hours > 1 ? "s" : "") + " atrás";
        } else if (minutes > 0) {
            return minutes + " minuto" + (minutes > 1 ? "s" : "") + " atrás";
        } else {
            return "Agora mesmo";
        }
    }
}