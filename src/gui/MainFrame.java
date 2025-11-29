package gui;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    private DeviceAnalysis deviceAnalysis;
    private FormatScanner formatScanner;
    private RecoveryResults recoveryResults;
    private JTabbedPane tabbedPane;

    public MainFrame() {
        initializeUI();
        setupComponents();
        setupLayout();
    }

    private void initializeUI() {
        setTitle("Sistema de Recuperação de Dados - DADASD");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(1200, 800));
        setMinimumSize(new Dimension(1000, 700));

        // Usar look and feel do sistema - FORMA CORRETA
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            // Se Nimbus não estiver disponível, usar o padrão
            try {
                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            } catch (Exception ex) {
                System.err.println("Erro ao definir look and feel: " + ex.getMessage());
            }
        }
    }

    private void setupComponents() {
        deviceAnalysis = new DeviceAnalysis();
        formatScanner = new FormatScanner();
        recoveryResults = new RecoveryResults();

        tabbedPane = new JTabbedPane();
    }

    private void setupLayout() {
        // Configurar abas
        tabbedPane.addTab("📊 Análise do Dispositivo", deviceAnalysis);
        tabbedPane.addTab("🔍 Varredura de Formatos", formatScanner);
        tabbedPane.addTab("💾 Resultados e Recuperação", recoveryResults);

        // Adicionar painel de abas ao frame
        setLayout(new BorderLayout());
        add(tabbedPane, BorderLayout.CENTER);

        // Adicionar barra de status
        add(createStatusBar(), BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(null); // Centralizar na tela
    }

    private JPanel createStatusBar() {
        JPanel statusBar = new JPanel(new BorderLayout());
        statusBar.setBorder(BorderFactory.createEtchedBorder());

        JLabel statusLabel = new JLabel(" Pronto");
        statusLabel.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));

        JLabel versionLabel = new JLabel("DADASD v1.0 ");
        versionLabel.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));

        statusBar.add(statusLabel, BorderLayout.WEST);
        statusBar.add(versionLabel, BorderLayout.EAST);

        return statusBar;
    }

    public String getSelectedDevice() {
        return deviceAnalysis.getSelectedDevice();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                new MainFrame().setVisible(true);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null,
                        "Erro ao iniciar a aplicação: " + e.getMessage(),
                        "Erro de Inicialização",
                        JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        });
    }
}