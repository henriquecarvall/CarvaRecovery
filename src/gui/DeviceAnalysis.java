package gui;

import engine.DeviceAnalyzer;
import javax.swing.*;
import java.awt.*;
import java.io.File;

public class DeviceAnalysis extends JFrame {
    private File selectedDevice;
    private JProgressBar progressBar;
    private JTextArea analysisArea;
    private JLabel statusLabel;
    private DeviceAnalyzer deviceAnalyzer;

    public DeviceAnalysis(File device) {
        this.selectedDevice = device;
        this.deviceAnalyzer = new DeviceAnalyzer(device);
        setupWindow();
        createUI();
        startAnalysis();
    }

    private void setupWindow() {
        setTitle("CarvaRecovery - Análise do Dispositivo");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
    }

    private void createUI() {
        // Painel principal
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(new Color(45, 45, 48));

        // Cabeçalho
        JLabel titleLabel = new JLabel("Análise do Dispositivo", JLabel.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(new Color(0, 122, 204));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        // Informações do dispositivo
        JLabel deviceLabel = new JLabel("Dispositivo: " + selectedDevice.getAbsolutePath(), JLabel.CENTER);
        deviceLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        deviceLabel.setForeground(Color.LIGHT_GRAY);
        deviceLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        // Barra de progresso
        progressBar = new JProgressBar(0, 100);
        progressBar.setPreferredSize(new Dimension(0, 25));
        progressBar.setBackground(new Color(60, 60, 60));
        progressBar.setForeground(new Color(0, 122, 204));
        progressBar.setStringPainted(true);
        progressBar.setFont(new Font("Segoe UI", Font.BOLD, 12));

        // Status
        statusLabel = new JLabel("Iniciando análise...", JLabel.CENTER);
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        statusLabel.setForeground(Color.WHITE);
        statusLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        // Área de análise
        analysisArea = new JTextArea();
        analysisArea.setEditable(false);
        analysisArea.setBackground(new Color(30, 30, 30));
        analysisArea.setForeground(Color.GREEN);
        analysisArea.setFont(new Font("Consolas", Font.PLAIN, 12));
        analysisArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JScrollPane analysisScroll = new JScrollPane(analysisArea);
        analysisScroll.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(100, 100, 100)),
                "Resultado da Análise"
        ));

        // Botões
        JPanel buttonsPanel = new JPanel(new FlowLayout());
        buttonsPanel.setBackground(new Color(45, 45, 48));

        JButton backButton = createStyledButton("VOLTAR", new Color(80, 80, 80));
        JButton detailedAnalysisButton = createStyledButton("ANÁLISE DETALHADA", new Color(0, 122, 204));
        JButton quickAnalysisButton = createStyledButton("ANÁLISE RÁPIDA", new Color(80, 180, 80));

        backButton.addActionListener(e -> goBack());
        detailedAnalysisButton.addActionListener(e -> startDetailedAnalysis());
        quickAnalysisButton.addActionListener(e -> showQuickAnalysis());

        buttonsPanel.add(backButton);
        buttonsPanel.add(quickAnalysisButton);
        buttonsPanel.add(detailedAnalysisButton);

        // Layout
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(45, 45, 48));
        headerPanel.add(titleLabel, BorderLayout.NORTH);
        headerPanel.add(deviceLabel, BorderLayout.CENTER);

        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(progressBar, BorderLayout.CENTER);
        mainPanel.add(statusLabel, BorderLayout.SOUTH);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(new Color(45, 45, 48));
        centerPanel.add(analysisScroll, BorderLayout.CENTER);

        mainPanel.add(centerPanel, BorderLayout.CENTER);
        mainPanel.add(buttonsPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private void startAnalysis() {
        // Análise rápida inicial
        showQuickAnalysis();
    }

    private void showQuickAnalysis() {
        analysisArea.setText("Executando análise rápida...\n\n");

        new Thread(() -> {
            try {
                DeviceAnalyzer.DeviceAnalysisResult result = deviceAnalyzer.getQuickAnalysis();

                SwingUtilities.invokeLater(() -> {
                    analysisArea.setText(result.toString());
                    statusLabel.setText("Análise rápida concluída");
                    progressBar.setValue(100);
                });

            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> {
                    analysisArea.setText("Erro na análise rápida: " + e.getMessage());
                    statusLabel.setText("Erro na análise");
                });
            }
        }).start();
    }

    private void startDetailedAnalysis() {
        analysisArea.setText("Iniciando análise detalhada...\n\n");
        progressBar.setValue(0);

        deviceAnalyzer.performAnalysis(new DeviceAnalyzer.AnalysisListener() {
            @Override
            public void onProgressUpdate(int progress, String status) {
                SwingUtilities.invokeLater(() -> {
                    progressBar.setValue(progress);
                    statusLabel.setText(status);
                    analysisArea.append("[" + progress + "%] " + status + "\n");
                });
            }

            @Override
            public void onAnalysisComplete(DeviceAnalyzer.DeviceAnalysisResult result) {
                SwingUtilities.invokeLater(() -> {
                    analysisArea.append("\n=== ANÁLISE CONCLUÍDA ===\n\n");
                    analysisArea.append(result.toString());
                    statusLabel.setText("Análise detalhada concluída");
                });
            }

            @Override
            public void onError(String errorMessage) {
                SwingUtilities.invokeLater(() -> {
                    analysisArea.append("ERRO: " + errorMessage + "\n");
                    statusLabel.setText("Erro na análise");
                });
            }
        });
    }

    private void goBack() {
        new DeviceSelector().setVisible(true);
        this.dispose();
    }

    private JButton createStyledButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
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
}