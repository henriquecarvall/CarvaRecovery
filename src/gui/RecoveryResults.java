package gui;

import engine.FileRecoveryEngine;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.List;

public class RecoveryResults extends JFrame {
    private File selectedDevice;
    private JProgressBar progressBar;
    private JTextArea logArea;
    private JList<String> filesList;
    private DefaultListModel<String> listModel;
    private JLabel statusLabel;
    private JLabel filesFoundLabel;
    private FileRecoveryEngine recoveryEngine;
    private int filesFound = 0;

    public RecoveryResults(File device) {
        this.selectedDevice = device;
        this.recoveryEngine = new FileRecoveryEngine(device);
        setupWindow();
        createUI();
        setupRecoveryEngine();
        startRecoveryProcess();
    }

    private void setupWindow() {
        setTitle("CarvaRecovery - Recuperando Arquivos");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 750);
        setLocationRelativeTo(null);
    }

    private void createUI() {
        // Painel principal
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(new Color(45, 45, 48));

        // Cabeçalho
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(45, 45, 48));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        JLabel titleLabel = new JLabel("Recuperacao em Andamento", JLabel.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(new Color(0, 122, 204));

        statusLabel = new JLabel("Preparando scan...", JLabel.CENTER);
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        statusLabel.setForeground(Color.LIGHT_GRAY);

        headerPanel.add(titleLabel, BorderLayout.NORTH);
        headerPanel.add(statusLabel, BorderLayout.SOUTH);

        // Painel de informações
        JPanel infoPanel = new JPanel(new GridLayout(1, 2, 15, 0));
        infoPanel.setBackground(new Color(45, 45, 48));
        infoPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));

        JLabel deviceLabel = new JLabel("Dispositivo: " + selectedDevice.getAbsolutePath());
        deviceLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        deviceLabel.setForeground(Color.WHITE);

        filesFoundLabel = new JLabel("Arquivos encontrados: 0");
        filesFoundLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        filesFoundLabel.setForeground(Color.WHITE);

        infoPanel.add(deviceLabel);
        infoPanel.add(filesFoundLabel);

        // Barra de progresso
        progressBar = new JProgressBar(0, 100);
        progressBar.setPreferredSize(new Dimension(0, 30));
        progressBar.setBackground(new Color(60, 60, 60));
        progressBar.setForeground(new Color(0, 122, 204));
        progressBar.setStringPainted(true);
        progressBar.setFont(new Font("Segoe UI", Font.BOLD, 12));

        // Área de log
        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setBackground(Color.BLACK);
        logArea.setForeground(Color.GREEN);
        logArea.setFont(new Font("Consolas", Font.PLAIN, 12));

        JScrollPane logScroll = new JScrollPane(logArea);
        logScroll.setPreferredSize(new Dimension(900, 200));
        logScroll.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(100, 100, 100)),
                "Log do Sistema"
        ));

        // Lista de arquivos recuperados
        listModel = new DefaultListModel<>();
        filesList = new JList<>(listModel);
        filesList.setBackground(new Color(60, 60, 60));
        filesList.setForeground(Color.WHITE);
        filesList.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        JScrollPane filesScroll = new JScrollPane(filesList);
        filesScroll.setPreferredSize(new Dimension(900, 250));
        filesScroll.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(100, 100, 100)),
                "Arquivos Recuperados"
        ));

        // Botões
        JPanel buttonsPanel = new JPanel(new FlowLayout());
        buttonsPanel.setBackground(new Color(45, 45, 48));

        JButton stopButton = createStyledButton("PARAR SCAN", new Color(200, 80, 80));
        JButton saveButton = createStyledButton("SALVAR ARQUIVOS", new Color(80, 180, 80));
        JButton backButton = createStyledButton("VOLTAR", new Color(80, 80, 80));

        stopButton.addActionListener(e -> stopRecovery());
        saveButton.addActionListener(e -> saveRecoveredFiles());
        backButton.addActionListener(e -> goBack());

        buttonsPanel.add(stopButton);
        buttonsPanel.add(saveButton);
        buttonsPanel.add(backButton);

        // Layout principal
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(infoPanel, BorderLayout.CENTER);
        mainPanel.add(progressBar, BorderLayout.SOUTH);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(new Color(45, 45, 48));
        centerPanel.add(logScroll, BorderLayout.NORTH);
        centerPanel.add(filesScroll, BorderLayout.CENTER);

        mainPanel.add(centerPanel, BorderLayout.CENTER);
        mainPanel.add(buttonsPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private void setupRecoveryEngine() {
        recoveryEngine.setRecoveryListener(new FileRecoveryEngine.RecoveryListener() {
            @Override
            public void onFileFound(FileRecoveryEngine.RecoveredFileInfo fileInfo) {
                SwingUtilities.invokeLater(() -> {
                    filesFound++;
                    listModel.addElement(fileInfo.getFileName() + " (" + formatSize(fileInfo.getFileSize()) + ")");
                    filesFoundLabel.setText("Arquivos encontrados: " + filesFound);
                    log("ENCONTRADO: " + fileInfo.getFileName() + " - " + formatSize(fileInfo.getFileSize()));
                });
            }

            @Override
            public void onProgressUpdate(int progress, String status) {
                SwingUtilities.invokeLater(() -> {
                    progressBar.setValue(progress);
                    statusLabel.setText(status);
                    if (progress % 10 == 0) { // Log a cada 10% para não poluir
                        log("PROGRESSO: " + progress + "% - " + status);
                    }
                });
            }

            @Override
            public void onError(String errorMessage) {
                SwingUtilities.invokeLater(() -> {
                    log("ERRO: " + errorMessage);
                    statusLabel.setText("Erro durante recuperacao");
                });
            }
        });
    }

    private void startRecoveryProcess() {
        log("INICIANDO MOTOR DE RECUPERACAO REAL...");
        log("Dispositivo: " + selectedDevice.getAbsolutePath());
        log("Procurando por assinaturas de arquivos...");

        recoveryEngine.startDeepRecovery();
    }

    private void stopRecovery() {
        recoveryEngine.stopRecovery();
        log("SCAN INTERROMPIDO PELO USUARIO");
        statusLabel.setText("Scan interrompido");
    }

    private void saveRecoveredFiles() {
        if (filesFound == 0) {
            JOptionPane.showMessageDialog(this,
                    "Nenhum arquivo para salvar. Execute o scan primeiro.",
                    "Nenhum Arquivo",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setDialogTitle("Selecione onde salvar os arquivos recuperados");

        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File outputDir = chooser.getSelectedFile();

            new Thread(() -> {
                recoveryEngine.saveAllRecoveredFiles(outputDir);
            }).start();
        }
    }

    private void goBack() {
        new FormatScanner(selectedDevice).setVisible(true);
        this.dispose();
    }

    private JButton createStyledButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
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

    private void log(String message) {
        SwingUtilities.invokeLater(() -> {
            logArea.append("[" + java.time.LocalTime.now().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss")) + "] " + message + "\n");
            logArea.setCaretPosition(logArea.getDocument().getLength());
        });
    }

    private String formatSize(long bytes) {
        if (bytes < 1024) return bytes + " B";
        if (bytes < 1024 * 1024) return (bytes / 1024) + " KB";
        if (bytes < 1024 * 1024 * 1024) return (bytes / (1024 * 1024)) + " MB";
        return (bytes / (1024 * 1024 * 1024)) + " GB";
    }
}