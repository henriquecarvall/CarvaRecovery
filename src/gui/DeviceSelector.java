package gui;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DeviceSelector extends JFrame {
    private JPanel devicesPanel;
    private List<File> availableDevices;

    public DeviceSelector() {
        setupWindow();
        createUI();
        detectDevices();
    }

    private void setupWindow() {
        setTitle("CarvaRecovery - Selecione o Dispositivo");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 650);
        setLocationRelativeTo(null);
        setBackground(new Color(45, 45, 48));
    }

    private void createUI() {
        // Painel principal com fundo escuro
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
        mainPanel.setBackground(new Color(45, 45, 48));

        // Título
        JLabel titleLabel = new JLabel("Selecione o Dispositivo para Recuperacao", JLabel.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(new Color(0, 122, 204));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));

        // Painel para os dispositivos
        devicesPanel = new JPanel();
        devicesPanel.setLayout(new BoxLayout(devicesPanel, BoxLayout.Y_AXIS));
        devicesPanel.setBackground(new Color(45, 45, 48));

        JScrollPane scrollPane = new JScrollPane(devicesPanel);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(60, 60, 60)));
        scrollPane.getViewport().setBackground(new Color(45, 45, 48));
        scrollPane.setPreferredSize(new Dimension(800, 450));

        // Botão voltar
        JButton backButton = createStyledButton("Voltar para Tela Inicial");
        backButton.setBackground(new Color(80, 80, 80));
        backButton.addActionListener(e -> goBackToMain());

        mainPanel.add(titleLabel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(backButton, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private void detectDevices() {
        availableDevices = new ArrayList<>();

        // Detecta todas as unidades de disco disponiveis
        File[] roots = File.listRoots();
        for (File root : roots) {
            if (isValidDevice(root)) {
                availableDevices.add(root);
                addDeviceButton(root);
            }
        }

        // Se nao encontrou dispositivos
        if (availableDevices.isEmpty()) {
            JLabel noDevicesLabel = new JLabel("Nenhum dispositivo encontrado. Conecte um HD, Pen Drive ou HD Externo.");
            noDevicesLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            noDevicesLabel.setForeground(Color.LIGHT_GRAY);
            noDevicesLabel.setHorizontalAlignment(JLabel.CENTER);
            noDevicesLabel.setBorder(BorderFactory.createEmptyBorder(50, 0, 0, 0));
            devicesPanel.add(noDevicesLabel);
        }
    }

    private boolean isValidDevice(File root) {
        try {
            // Ignora disquetes (A: e B:)
            String path = root.getAbsolutePath().toUpperCase();
            if (path.startsWith("A:") || path.startsWith("B:")) {
                return false;
            }

            // Verifica se tem espaço total (dispositivo valido)
            long totalSpace = root.getTotalSpace();
            return totalSpace > 0;

        } catch (Exception e) {
            return false;
        }
    }

    private void addDeviceButton(File device) {
        JButton deviceButton = new JButton();
        deviceButton.setLayout(new BorderLayout());
        deviceButton.setMaximumSize(new Dimension(750, 80));
        deviceButton.setBackground(new Color(60, 60, 60));
        deviceButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(100, 100, 100), 2),
                BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));
        deviceButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Informacoes do dispositivo
        String deviceName = getDeviceName(device);
        String deviceInfo = getDeviceInfo(device);

        JLabel nameLabel = new JLabel(deviceName);
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        nameLabel.setForeground(Color.WHITE);

        JLabel infoLabel = new JLabel(deviceInfo);
        infoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        infoLabel.setForeground(new Color(180, 180, 180));

        JPanel textPanel = new JPanel(new BorderLayout());
        textPanel.setBackground(new Color(60, 60, 60));
        textPanel.add(nameLabel, BorderLayout.NORTH);
        textPanel.add(infoLabel, BorderLayout.SOUTH);

        // Icone do dispositivo
        JLabel iconLabel = new JLabel("💾");
        iconLabel.setFont(new Font("Segoe UI", Font.PLAIN, 24));
        iconLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 20));

        // Botão de análise
        JButton analyzeButton = new JButton("🔍 Analisar");
        analyzeButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        analyzeButton.setBackground(new Color(0, 122, 204));
        analyzeButton.setForeground(Color.WHITE);
        analyzeButton.setFocusPainted(false);
        analyzeButton.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        analyzeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        analyzeButton.addActionListener(e -> {
            new DeviceAnalysis(device).setVisible(true);
            ((Window) SwingUtilities.getRoot(analyzeButton)).dispose();
        });

        deviceButton.add(iconLabel, BorderLayout.WEST);
        deviceButton.add(textPanel, BorderLayout.CENTER);
        deviceButton.add(analyzeButton, BorderLayout.EAST);

        // Efeito hover
        deviceButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                deviceButton.setBackground(new Color(80, 80, 80));
                deviceButton.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(0, 122, 204), 2),
                        BorderFactory.createEmptyBorder(15, 20, 15, 20)
                ));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                deviceButton.setBackground(new Color(60, 60, 60));
                deviceButton.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(100, 100, 100), 2),
                        BorderFactory.createEmptyBorder(15, 20, 15, 20)
                ));
            }
        });

        // Acao do botao
        deviceButton.addActionListener(e -> onDeviceSelected(device));

        devicesPanel.add(deviceButton);
        devicesPanel.add(Box.createRigidArea(new Dimension(0, 15)));
    }

    private String getDeviceName(File device) {
        String path = device.getAbsolutePath();
        if (path.equals("C:") || path.startsWith("C:\\")) {
            return "HD Interno (Sistema C:)";
        } else {
            return "Dispositivo (" + path + ")";
        }
    }

    private String getDeviceInfo(File device) {
        try {
            long totalGB = device.getTotalSpace() / (1024 * 1024 * 1024);
            long freeGB = device.getFreeSpace() / (1024 * 1024 * 1024);
            long usedGB = totalGB - freeGB;

            return String.format("Capacidade: %d GB | Usado: %d GB | Livre: %d GB",
                    totalGB, usedGB, freeGB);
        } catch (Exception e) {
            return "Informacoes nao disponiveis";
        }
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(12, 25, 12, 25));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private void onDeviceSelected(File selectedDevice) {
        JOptionPane.showMessageDialog(this,
                "Dispositivo selecionado: " + selectedDevice.getAbsolutePath() +
                        "\n\nAgora vamos para a tela de formatos!",
                "Dispositivo Selecionado",
                JOptionPane.INFORMATION_MESSAGE);

        // Aqui vamos abrir a proxima tela (Formatos + Blocos)
        openFormatScanner(selectedDevice);
    }

    private void openFormatScanner(File device) {
        // Esta sera a proxima tela - vamos criar depois
        new FormatScanner(device).setVisible(true);
        this.dispose();
    }

    private void goBackToMain() {
        new MainFrame().setVisible(true);
        this.dispose();
    }
}