package utils;

import config.AppConfig;
import javax.swing.*;
import java.awt.*;

public class ThemeManager {

    public static void applyDarkTheme() {
        try {
            // Cores básicas
            UIManager.put("Panel.background", AppConfig.Colors.DARK_BG);
            UIManager.put("Frame.background", AppConfig.Colors.DARK_BG);
            UIManager.put("Dialog.background", AppConfig.Colors.DARK_BG);
            UIManager.put("Label.foreground", AppConfig.Colors.TEXT);
            UIManager.put("Button.foreground", Color.WHITE);
            UIManager.put("TextField.foreground", AppConfig.Colors.TEXT);
            UIManager.put("TextArea.foreground", AppConfig.Colors.TEXT);
            UIManager.put("List.foreground", AppConfig.Colors.TEXT);
            UIManager.put("ComboBox.foreground", AppConfig.Colors.TEXT);
            UIManager.put("CheckBox.foreground", AppConfig.Colors.TEXT);
            UIManager.put("RadioButton.foreground", AppConfig.Colors.TEXT);

            // Backgrounds
            UIManager.put("TextField.background", AppConfig.Colors.DARKER_BG);
            UIManager.put("TextArea.background", AppConfig.Colors.DARKER_BG);
            UIManager.put("List.background", AppConfig.Colors.DARKER_BG);
            UIManager.put("ComboBox.background", AppConfig.Colors.DARKER_BG);
            UIManager.put("ScrollPane.background", AppConfig.Colors.DARK_BG);
            UIManager.put("Viewport.background", AppConfig.Colors.DARK_BG);

            // Bordas
            UIManager.put("TextField.border", BorderFactory.createLineBorder(new Color(100, 100, 100)));
            UIManager.put("TextArea.border", BorderFactory.createLineBorder(new Color(100, 100, 100)));

            // ProgressBar
            UIManager.put("ProgressBar.background", AppConfig.Colors.DARKER_BG);
            UIManager.put("ProgressBar.foreground", AppConfig.Colors.ACCENT);
            UIManager.put("ProgressBar.border", BorderFactory.createLineBorder(new Color(80, 80, 80)));

            // Tabela
            UIManager.put("Table.background", AppConfig.Colors.DARKER_BG);
            UIManager.put("Table.foreground", AppConfig.Colors.TEXT);
            UIManager.put("Table.gridColor", new Color(80, 80, 80));
            UIManager.put("Table.selectionBackground", AppConfig.Colors.ACCENT);
            UIManager.put("Table.selectionForeground", Color.WHITE);

            // Separador
            UIManager.put("Separator.background", new Color(100, 100, 100));
            UIManager.put("Separator.foreground", new Color(100, 100, 100));

            // ToolTip
            UIManager.put("ToolTip.background", AppConfig.Colors.DARKER_BG);
            UIManager.put("ToolTip.foreground", AppConfig.Colors.TEXT);
            UIManager.put("ToolTip.border", BorderFactory.createLineBorder(new Color(100, 100, 100)));

        } catch (Exception e) {
            System.err.println("Erro ao aplicar tema escuro: " + e.getMessage());
        }
    }

    public static void applyAccentTheme(Color accentColor) {
        try {
            UIManager.put("Button.background", accentColor);
            UIManager.put("ProgressBar.foreground", accentColor);
            UIManager.put("Table.selectionBackground", accentColor);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Color getSuccessColor() {
        return AppConfig.Colors.SUCCESS;
    }

    public static Color getWarningColor() {
        return AppConfig.Colors.WARNING;
    }

    public static Color getErrorColor() {
        return AppConfig.Colors.ERROR;
    }

    public static Color getAccentColor() {
        return AppConfig.Colors.ACCENT;
    }

    public static Font getTitleFont() {
        return new Font("Segoe UI", Font.BOLD, 24);
    }

    public static Font getSubtitleFont() {
        return new Font("Segoe UI", Font.BOLD, 18);
    }

    public static Font getNormalFont() {
        return new Font("Segoe UI", Font.PLAIN, 14);
    }

    public static Font getSmallFont() {
        return new Font("Segoe UI", Font.PLAIN, 12);
    }

    public static Font getMonospaceFont() {
        return new Font("Consolas", Font.PLAIN, 12);
    }

    public static void styleButton(JButton button, Color backgroundColor) {
        button.setBackground(backgroundColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Efeito hover
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(backgroundColor.brighter());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(backgroundColor);
            }
        });
    }

    public static void styleProgressBar(JProgressBar progressBar) {
        progressBar.setBackground(AppConfig.Colors.DARKER_BG);
        progressBar.setForeground(AppConfig.Colors.ACCENT);
        progressBar.setBorder(BorderFactory.createLineBorder(new Color(80, 80, 80)));
        progressBar.setStringPainted(true);
    }

    public static void styleTextArea(JTextArea textArea) {
        textArea.setBackground(AppConfig.Colors.DARKER_BG);
        textArea.setForeground(AppConfig.Colors.TEXT);
        textArea.setCaretColor(AppConfig.Colors.TEXT);
        textArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        textArea.setFont(getMonospaceFont());
    }
}