package gui;

import engine.FileRecoveryEngine;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.HashSet;
import java.util.Set;

public class FormatScanner extends JPanel {
    private FileRecoveryEngine recoveryEngine;
    private JCheckBox jpgCheckBox, pngCheckBox, pdfCheckBox, docCheckBox;
    private JCheckBox mp3CheckBox, mp4CheckBox, zipCheckBox, allFormatsCheckBox;
    private JButton scanButton, stopButton;
    private JProgressBar progressBar;
    private JTable resultsTable;
    private DefaultTableModel tableModel;
    private JLabel statusLabel;

    public FormatScanner() {
        recoveryEngine = new FileRecoveryEngine();
        initializeComponents();
        setupLayout();
        setupEventHandlers();
    }

    private void initializeComponents() {
        // Checkboxes de formatos
        jpgCheckBox = new JCheckBox("JPEG (.jpg, .jpeg)");
        pngCheckBox = new JCheckBox("PNG (.png)");
        pdfCheckBox = new JCheckBox("PDF (.pdf)");
        docCheckBox = new JCheckBox("Documentos (.doc, .docx)");
        mp3CheckBox = new JCheckBox("Áudio MP3 (.mp3)");
        mp4CheckBox = new JCheckBox("Vídeo MP4 (.mp4)");
        zipCheckBox = new JCheckBox("Arquivos ZIP (.zip)");
        allFormatsCheckBox = new JCheckBox("Todos os Formatos");

        // Botões
        scanButton = new JButton("Iniciar Varredura");
        stopButton = new JButton("Parar");
        stopButton.setEnabled(false);

        // Barra de progresso
        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);

        // Tabela de resultados
        String[] columns = {"Nome do Arquivo", "Tipo", "Tamanho", "Integridade", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return String.class;
            }
        };

        resultsTable = new JTable(tableModel);
        resultsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Label de status
        statusLabel = new JLabel("Pronto para escanear");
        statusLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    }

    private void setupLayout() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Painel de formatos
        JPanel formatPanel = new JPanel(new GridLayout(0, 2, 5, 5));
        formatPanel.setBorder(BorderFactory.createTitledBorder("Formatos para Recuperar"));

        formatPanel.add(jpgCheckBox);
        formatPanel.add(pngCheckBox);
        formatPanel.add(pdfCheckBox);
        formatPanel.add(docCheckBox);
        formatPanel.add(mp3CheckBox);
        formatPanel.add(mp4CheckBox);
        formatPanel.add(zipCheckBox);
        formatPanel.add(allFormatsCheckBox);

        // Painel de controles
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        controlPanel.add(scanButton);
        controlPanel.add(stopButton);
        controlPanel.add(progressBar);

        // Painel principal norte
        JPanel northPanel = new JPanel(new BorderLayout());
        northPanel.add(formatPanel, BorderLayout.CENTER);
        northPanel.add(controlPanel, BorderLayout.SOUTH);

        // Painel de resultados
        JPanel resultsPanel = new JPanel(new BorderLayout());
        resultsPanel.setBorder(BorderFactory.createTitledBorder("Arquivos Encontrados"));
        resultsPanel.add(new JScrollPane(resultsTable), BorderLayout.CENTER);
        resultsPanel.add(statusLabel, BorderLayout.SOUTH);

        // Layout principal
        add(northPanel, BorderLayout.NORTH);
        add(resultsPanel, BorderLayout.CENTER);
    }

    private void setupEventHandlers() {
        allFormatsCheckBox.addActionListener(e -> {
            boolean selected = allFormatsCheckBox.isSelected();
            jpgCheckBox.setSelected(selected);
            pngCheckBox.setSelected(selected);
            pdfCheckBox.setSelected(selected);
            docCheckBox.setSelected(selected);
            mp3CheckBox.setSelected(selected);
            mp4CheckBox.setSelected(selected);
            zipCheckBox.setSelected(selected);
        });

        scanButton.addActionListener(e -> startScanning());
        stopButton.addActionListener(e -> stopScanning());

        // Listener para atualização de progresso
        recoveryEngine.setProgressListener(new FileRecoveryEngine.RecoveryProgressListener() {
            @Override
            public void onProgressUpdate(int progress, long bytesScanned, long totalBytes) {
                SwingUtilities.invokeLater(() -> {
                    progressBar.setValue(progress);
                    statusLabel.setText(String.format("Escaneando... %d%% (%s de %s)",
                            progress, formatSize(bytesScanned), formatSize(totalBytes)));
                });
            }

            @Override
            public void onFileFound(RecoveredFile file) {
                SwingUtilities.invokeLater(() -> addFileToTable(file));
            }

            @Override
            public void onRecoveryProgress(RecoveredFile file, int progress) {
                // Não usado na varredura
            }
        });
    }

    private void startScanning() {
        if (getSelectedDevice() == null) {
            JOptionPane.showMessageDialog(this,
                    "Selecione um dispositivo primeiro na aba 'Análise do Dispositivo'",
                    "Dispositivo Não Selecionado",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        Set<String> selectedFormats = getSelectedFormats();
        if (selectedFormats.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Selecione pelo menos um formato de arquivo para recuperar",
                    "Nenhum Formato Selecionado",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Limpar resultados anteriores
        tableModel.setRowCount(0);

        // Configurar interface para escaneamento
        scanButton.setEnabled(false);
        stopButton.setEnabled(true);
        progressBar.setValue(0);

        // Executar escaneamento em thread separada
        new Thread(() -> {
            var recoveredFiles = recoveryEngine.scanForDeletedFiles(getSelectedDevice(), selectedFormats);

            SwingUtilities.invokeLater(() -> {
                scanButton.setEnabled(true);
                stopButton.setEnabled(false);
                statusLabel.setText(String.format("Varredura concluída! %d arquivos encontrados.", recoveredFiles.size()));
            });
        }).start();
    }

    private void stopScanning() {
        recoveryEngine.stopScanning();
        scanButton.setEnabled(true);
        stopButton.setEnabled(false);
        statusLabel.setText("Varredura interrompida pelo usuário");
    }

    private Set<String> getSelectedFormats() {
        Set<String> formats = new HashSet<>();

        if (allFormatsCheckBox.isSelected() || jpgCheckBox.isSelected()) {
            formats.add("jpg");
            formats.add("jpeg");
        }
        if (allFormatsCheckBox.isSelected() || pngCheckBox.isSelected()) {
            formats.add("png");
        }
        if (allFormatsCheckBox.isSelected() || pdfCheckBox.isSelected()) {
            formats.add("pdf");
        }
        if (allFormatsCheckBox.isSelected() || docCheckBox.isSelected()) {
            formats.add("doc");
            formats.add("docx");
        }
        if (allFormatsCheckBox.isSelected() || mp3CheckBox.isSelected()) {
            formats.add("mp3");
        }
        if (allFormatsCheckBox.isSelected() || mp4CheckBox.isSelected()) {
            formats.add("mp4");
        }
        if (allFormatsCheckBox.isSelected() || zipCheckBox.isSelected()) {
            formats.add("zip");
            formats.add("rar");
            formats.add("7z");
        }

        return formats;
    }

    private void addFileToTable(RecoveredFile file) {
        tableModel.addRow(new Object[]{
                file.getFileName(),
                file.getFileType(),
                file.getFormattedSize(),
                String.format("%.1f%%", file.getIntegrityScore() * 100),
                file.getRecoveryStatus().getDescription()
        });
    }

    private String getSelectedDevice() {
        // Obter o dispositivo selecionado da aba de análise
        MainFrame mainFrame = (MainFrame) SwingUtilities.getWindowAncestor(this);
        if (mainFrame != null) {
            return mainFrame.getSelectedDevice();
        }
        return null;
    }

    private String formatSize(long bytes) {
        if (bytes >= 1_000_000_000_000L) {
            return String.format("%.2f TB", bytes / 1_000_000_000_000.0);
        } else if (bytes >= 1_000_000_000L) {
            return String.format("%.2f GB", bytes / 1_000_000_000.0);
        } else if (bytes >= 1_000_000L) {
            return String.format("%.2f MB", bytes / 1_000_000.0);
        } else if (bytes >= 1_000L) {
            return String.format("%.2f KB", bytes / 1_000.0);
        } else {
            return bytes + " B";
        }
    }

    public java.util.List<RecoveredFile> getSelectedFiles() {
        // Retornar arquivos selecionados para recuperação
        // Implementação simplificada
        return java.util.Collections.emptyList();
    }
}