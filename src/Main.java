import gui.MainFrame;
import config.SettingsManager;
import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        // Carrega configurações
        SettingsManager.loadSettings();

        // Configura o look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeel());
        } catch (Exception e) {
            try {
                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeel());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        // Cria e exibe a janela principal
        SwingUtilities.invokeLater(() -> {
            new MainFrame().setVisible(true);
        });
    }
}