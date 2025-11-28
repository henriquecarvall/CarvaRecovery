import gui.MainFrame;
import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        // Tenta usar o visual Nimbus (moderno) ou usa o padrão
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            // Se não conseguir, usa o visual padrão do sistema
            try {
                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            } catch (Exception ex) {
                // Ignora qualquer erro e usa o visual padrão
            }
        }

        // Cria e mostra a janela principal
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new MainFrame().setVisible(true);
            }
        });
    }
}