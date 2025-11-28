package gui;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class FormatScanner extends JFrame {
    private File selectedDevice;

    public FormatScanner(File device) {
        this.selectedDevice = device;
        setupWindow();
        createUI();
    }

    private void setupWindow() {
        setTitle("CarvaRecovery - Selecione os Formatos");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 700);
        setLocationRelativeTo(null);
    }

    private void createUI() {
        // Painel principal
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
        mainPanel.setBackground(new Color(45, 45, 48));

        // Título
        JLabel titleLabel = new JLabel("Selecione os Formatos para Recuperar", JLabel.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(new Color(0, 122, 204));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        // Informações do dispositivo
        JLabel deviceLabel = new JLabel("Dispositivo: " + selectedDevice.getAbsolutePath(), JLabel.CENTER);
        deviceLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        deviceLabel.setForeground(Color.LIGHT_GRAY);
        deviceLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));

        // Painel de formatos
        JPanel formatsPanel = createFormatsPanel();

        // Painel de visualização de blocos
        JPanel blocksPanel = createBlocksPanel();

        // Botões
        JPanel buttonsPanel = new JPanel(new FlowLayout());
        buttonsPanel.setBackground(new Color(45, 45, 48));

        JButton scanButton = createStyledButton("INICIAR SCAN PROFUNDO", new Color(0, 122, 204));
        JButton backButton = createStyledButton("VOLTAR", new Color(80, 80, 80));

        scanButton.addActionListener(e -> startRecovery());
        backButton.addActionListener(e -> goBack());

        buttonsPanel.add(backButton);
        buttonsPanel.add(scanButton);

        // Layout com abas
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setBackground(new Color(45, 45, 48));
        tabbedPane.setForeground(Color.WHITE);

        tabbedPane.addTab("Formatos de Arquivo", formatsPanel);
        tabbedPane.addTab("Visualizacao de Blocos", blocksPanel);

        mainPanel.add(titleLabel, BorderLayout.NORTH);
        mainPanel.add(deviceLabel, BorderLayout.CENTER);
        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        mainPanel.add(buttonsPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private JPanel createFormatsPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 3, 15, 15));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(new Color(60, 60, 60));

        // Formatos disponíveis
        String[] formats = {
                "JPEG/ JPG", "PNG", "PDF",
                "ZIP/ RAR", "TXT/ DOC", "MP4/ AVI"
        };

        for (int i = 0; i < formats.length; i++) {
            JButton formatButton = createFormatButton(formats[i]);
            panel.add(formatButton);
        }

        return panel;
    }

    private JButton createFormatButton(String format) {
        JButton button = new JButton("<html><center>" + format + "</center></html>");
        button.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        button.setBackground(new Color(80, 80, 80));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Efeito hover
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(0, 122, 204));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(80, 80, 80));
            }
        });

        return button;
    }

    private JPanel createBlocksPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(new Color(60, 60, 60));

        // Título da visualização
        JLabel titleLabel = new JLabel("Mapa de Blocos do Dispositivo", JLabel.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        // Painel de blocos (simulação)
        JPanel blocksVisualization = new JPanel(new GridLayout(10, 15, 2, 2));
        blocksVisualization.setBackground(new Color(45, 45, 48));
        blocksVisualization.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Cria blocos coloridos (simulação)
        for (int i = 0; i < 150; i++) {
            JLabel block = new JLabel();
            block.setOpaque(true);
            block.setBorder(BorderFactory.createLineBorder(new Color(30, 30, 30)));

            // Simula blocos com dados (verde) e vazios (cinza)
            if (Math.random() > 0.3) {
                block.setBackground(new Color(40, 180, 40)); // Verde - com dados
            } else {
                block.setBackground(new Color(100, 100, 100)); // Cinza - vazio
            }

            blocksVisualization.add(block);
        }

        // Legenda
        JPanel legendPanel = new JPanel(new FlowLayout());
        legendPanel.setBackground(new Color(60, 60, 60));

        JLabel greenLegend = createLegendLabel(new Color(40, 180, 40), "Blocos com dados recuperaveis");
        JLabel grayLegend = createLegendLabel(new Color(100, 100, 100), "Blocos vazios/sobrescritos");

        legendPanel.add(greenLegend);
        legendPanel.add(Box.createRigidArea(new Dimension(20, 0)));
        legendPanel.add(grayLegend);

        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(blocksVisualization, BorderLayout.CENTER);
        panel.add(legendPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JLabel createLegendLabel(Color color, String text) {
        JPanel legendItem = new JPanel(new FlowLayout(FlowLayout.LEFT));
        legendItem.setBackground(new Color(60, 60, 60));

        JLabel colorBox = new JLabel();
        colorBox.setOpaque(true);
        colorBox.setBackground(color);
        colorBox.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
        colorBox.setPreferredSize(new Dimension(20, 20));

        JLabel textLabel = new JLabel(text);
        textLabel.setForeground(Color.LIGHT_GRAY);
        textLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        legendItem.add(colorBox);
        legendItem.add(textLabel);

        JLabel container = new JLabel();
        container.setLayout(new BorderLayout());
        container.add(legendItem, BorderLayout.WEST);
        return container;
    }

    private JButton createStyledButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(12, 25, 12, 25));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(color.brighter());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(color);
            }
        });

        return button;
    }

    private void startRecovery() {
        // AGORA CHAMA A TELA REAL DE RECUPERAÇÃO
        new RecoveryResults(selectedDevice).setVisible(true);
        this.dispose();
    }

    private void goBack() {
        new DeviceSelector().setVisible(true);
        this.dispose();
    }
}