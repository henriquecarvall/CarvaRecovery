package gui;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.Random;

public class RecoveryResults extends JFrame {
    private File selectedDevice;
    private JProgressBar progressBar;
    private JTextArea logArea;
    private JList<String> filesList;
    private DefaultListModel<String> listModel;
    private JLabel statusLabel;
    private JLabel filesFoundLabel;
    private boolean isScanning = false;
    private int filesFound = 0;

    public RecoveryResults(File device) {
        this.selectedDevice = device;
        setupWindow();
        createUI();
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

    private void startRecoveryProcess() {
        isScanning = true;

        new Thread(() -> {
            try {
                simulateDeepRecovery();
            } catch (Exception e) {
                log("ERRO: " + e.getMessage());
            } finally {
                isScanning = false;
                statusLabel.setText("Scan concluido!");
            }
        }).start();
    }

    private void simulateDeepRecovery() {
        log("Iniciando scan profundo no dispositivo...");
        log("Procurando por assinaturas de arquivos...");

        Random random = new Random();
        String[] fileTypes = {"foto", "documento", "video", "arquivo", "musica", "planilha"};
        String[] extensions = {"jpg", "png", "pdf", "doc", "mp4", "zip", "mp3", "xlsx"};

        int totalFiles = 80 + random.nextInt(70); // 80-150 arquivos

        for (int i = 1; i <= totalFiles; i++) {
            if (!isScanning) {
                log("Scan interrompido pelo usuario");
                break;
            }

            try {
                // Simula tempo de processamento
                Thread.sleep(100 + random.nextInt(200));

                // Simula encontro de arquivo
                String fileType = fileTypes[random.nextInt(fileTypes.length)];
                String ext = extensions[random.nextInt(extensions.length)];
                String fileName = fileType + "_recuperado_" + i + "." + ext;
                long fileSize = 1024 * (500 + random.nextInt(5000)); // 500KB - 5MB

                // Cria variaveis finais para usar no lambda
                final String currentFileName = fileName;
                final long currentFileSize = fileSize;
                final int currentProgress = i;
                final int currentTotalFiles = totalFiles;

                // Atualiza interface
                SwingUtilities.invokeLater(() -> {
                    filesFound++;
                    listModel.addElement(currentFileName + " (" + formatSize(currentFileSize) + ")");
                    filesFoundLabel.setText("Arquivos encontrados: " + filesFound);
                    progressBar.setValue((currentProgress * 100) / currentTotalFiles);
                    statusLabel.setText("Recuperando: " + currentFileName);
                });

                log("Encontrado: " + fileName + " - " + formatSize(fileSize));

            } catch (InterruptedException e) {
                break;
            }
        }

        if (isScanning) {
            SwingUtilities.invokeLater(() -> {
                progressBar.setValue(100);
                statusLabel.setText("Scan concluido! " + filesFound + " arquivos encontrados.");
                log("Scan finalizado com sucesso!");
            });
        }
    }

    private void stopRecovery() {
        isScanning = false;
        log("Scan interrompido pelo usuario");
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
                try {
                    saveFilesToDirectory(outputDir);
                } catch (Exception e) {
                    log("Erro ao salvar: " + e.getMessage());
                }
            }).start();
        }
    }

    private void saveFilesToDirectory(File outputDir) {
        log("Iniciando salvamento de " + filesFound + " arquivos...");

        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }

        int savedCount = 0;
        for (int i = 0; i < filesFound; i++) {
            try {
                // Simula o salvamento de arquivo
                String fileName = listModel.get(i).split(" ")[0];
                File outputFile = new File(outputDir, fileName);

                // Aqui seria a recuperacao REAL dos dados
                Thread.sleep(50); // Simula tempo de salvamento

                savedCount++;
                log("Salvo: " + fileName);

            } catch (Exception e) {
                log("Falha ao salvar arquivo " + (i + 1));
            }
        }

        log("Salvamento concluido! " + savedCount + "/" + filesFound + " arquivos salvos em: " + outputDir.getAbsolutePath());

        JOptionPane.showMessageDialog(this,
                "Recuperacao concluida!\n" +
                        savedCount + " arquivos salvos em:\n" +
                        outputDir.getAbsolutePath(),
                "Sucesso",
                JOptionPane.INFORMATION_MESSAGE);
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