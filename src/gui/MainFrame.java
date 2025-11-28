package gui;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    public MainFrame() {
        setupWindow();
        createUI();
    }

    private void setupWindow() {
        setTitle("CarvaRecovery - Recuperação de Arquivos Avançada");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);

        applyDarkTheme();
    }

    private void applyDarkTheme() {
        Color darkBg = new Color(45, 45, 48);
        Color darkerBg = new Color(30, 30, 30);
        Color accent = new Color(0, 122, 204);
        Color textColor = new Color(240, 240, 240);

        UIManager.put("Panel.background", darkBg);
        UIManager.put("Frame.background", darkBg);
        UIManager.put("Label.foreground", textColor);
        UIManager.put("Button.background", accent);
        UIManager.put("Button.foreground", Color.WHITE);
        UIManager.put("ProgressBar.background", darkerBg);
        UIManager.put("ProgressBar.foreground", accent);
    }

    private void createUI() {
        // Painel principal com gradiente escuro
        JPanel mainPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                Color color1 = new Color(35, 35, 38);
                Color color2 = new Color(50, 50, 55);
                g2d.setPaint(new GradientPaint(0, 0, color1, getWidth(), getHeight(), color2));
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainPanel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        // Título com logo personalizado
        JLabel titleLabel = new JLabel("CarvaRecovery", JLabel.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
        titleLabel.setForeground(new Color(0, 122, 204));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        // Subtítulo
        JLabel subtitleLabel = new JLabel("Recuperação Avançada para HDs Corrompidos", JLabel.CENTER);
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        subtitleLabel.setForeground(new Color(180, 180, 180));
        subtitleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 60, 0));

        // Botão para iniciar
        JButton startButton = createModernButton("INICIAR RECUPERAÇÃO");
        startButton.addActionListener(e -> openDeviceSelector());

        // Layout
        mainPanel.add(titleLabel, BorderLayout.NORTH);
        mainPanel.add(subtitleLabel, BorderLayout.CENTER);
        mainPanel.add(startButton, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private JButton createModernButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 16));
        button.setBackground(new Color(0, 122, 204));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(15, 30, 15, 30));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Efeito hover
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(0, 142, 224));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(0, 122, 204));
            }
        });

        return button;
    }

    private void openDeviceSelector() {
        // AGORA CHAMA A TELA REAL DE SELEÇÃO DE DISPOSITIVOS
        new DeviceSelector().setVisible(true);
        this.dispose(); // Fecha esta janela
    }
}