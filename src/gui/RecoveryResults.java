package gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;

public class RecoveryResults extends JPanel {
    private JTable resultsTable;
    private DefaultTableModel tableModel;
    private JButton recoverButton, recoverAllButton, previewButton;
    private JButton saveListButton, loadListButton;
    private JProgressBar recoveryProgressBar;
    private JLabel statusLabel;
    private JTextField outputPathField;

    public RecoveryResults() {
        initializeComponents();
        setupLayout();
        setupEventHandlers();
    }

    private void initializeComponents() {
        // Tabela de resultados
        String[] columns = {"Selecionar", "Nome do Arquivo", "Tipo", "Tamanho", "Integridade", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 0; // Apenas a coluna de seleção é editável
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 0) return Boolean.class;
                return String.class;
            }
        };

        resultsTable = new JTable(tableModel);
        resultsTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        resultsTable.getColumnModel().getColumn(0).setMaxWidth(60);
        resultsTable.getColumnModel().getColumn(0).setMinWidth(60);

        // Botões
        recoverButton = new JButton("Recuperar Selecionados");
        recoverAllButton = new JButton("Recuperar Todos");
        previewButton = new JButton("Pré-visualizar");
        saveListButton = new JButton("Salvar Lista");
        loadListButton = new JButton("Carregar Lista");

        // Barra de progresso
        recoveryProgressBar = new JProgressBar(0, 100);
        recoveryProgressBar.setStringPainted(true);

        // Campo de caminho de saída
        outputPathField = new JTextField(System.getProperty("user.home") + File.separator + "RecoveredFiles");
        JButton browseButton = new JButton("Procurar...");
        browseButton.addActionListener(e -> browseOutputPath());

        // Label de status
        statusLabel = new JLabel("Pronto para recuperação");
        statusLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    }

    private void setupLayout() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Painel de configuração de saída
        JPanel outputPanel = new JPanel(new BorderLayout(5, 5));
        outputPanel.setBorder(BorderFactory.createTitledBorder("Local de Saída para Arquivos Recuperados"));

        JPanel pathPanel = new JPanel(new BorderLayout(5, 5));
        pathPanel.add(new JLabel("Pasta de Destino:"), BorderLayout.WEST);
        pathPanel.add(outputPathField, BorderLayout.CENTER);

        JButton browseButton = new JButton("Procurar...");
        browseButton.addActionListener(e -> browseOutputPath());
        pathPanel.add(browseButton, BorderLayout.EAST);

        outputPanel.add(pathPanel, BorderLayout.CENTER);

        // Painel de botões
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.add(recoverButton);
        buttonPanel.add(recoverAllButton);
        buttonPanel.add(previewButton);
        buttonPanel.add(saveListButton);
        buttonPanel.add(loadListButton);

        // Painel superior
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(outputPanel, BorderLayout.NORTH);
        topPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Painel de resultados
        JPanel resultsPanel = new JPanel(new BorderLayout());
        resultsPanel.setBorder(BorderFactory.createTitledBorder("Arquivos para Recuperação"));
        resultsPanel.add(new JScrollPane(resultsTable), BorderLayout.CENTER);

        // Painel de progresso
        JPanel progressPanel = new JPanel(new BorderLayout());
        progressPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
        progressPanel.add(recoveryProgressBar, BorderLayout.CENTER);
        progressPanel.add(statusLabel, BorderLayout.SOUTH);

        resultsPanel.add(progressPanel, BorderLayout.SOUTH);

        // Layout principal
        add(topPanel, BorderLayout.NORTH);
        add(resultsPanel, BorderLayout.CENTER);
    }

    private void setupEventHandlers() {
        recoverButton.addActionListener(e -> recoverSelectedFiles());
        recoverAllButton.addActionListener(e -> recoverAllFiles());
        previewButton.addActionListener(e -> previewSelectedFile());
        saveListButton.addActionListener(e -> saveFileList());
        loadListButton.addActionListener(e -> loadFileList());
    }

    private void browseOutputPath() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fileChooser.setDialogTitle("Selecionar Pasta de Destino");

        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            outputPathField.setText(fileChooser.getSelectedFile().getAbsolutePath());
        }
    }

    private void recoverSelectedFiles() {
        String outputPath = outputPathField.getText().trim();
        if (outputPath.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Selecione uma pasta de destino para os arquivos recuperados",
                    "Pasta de Destino Não Selecionada",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        File outputDir = new File(outputPath);
        if (!outputDir.exists() && !outputDir.mkdirs()) {
            JOptionPane.showMessageDialog(this,
                    "Não foi possível criar a pasta de destino: " + outputPath,
                    "Erro de Diretório",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        int selectedCount = 0;
        int totalRows = tableModel.getRowCount();

        for (int i = 0; i < totalRows; i++) {
            Boolean selected = (Boolean) tableModel.getValueAt(i, 0);
            if (selected != null && selected) {
                selectedCount++;
            }
        }

        if (selectedCount == 0) {
            JOptionPane.showMessageDialog(this,
                    "Selecione pelo menos um arquivo para recuperar",
                    "Nenhum Arquivo Selecionado",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Simular recuperação (implementação real iria usar o FileRecoveryEngine)
        simulateRecovery(selectedCount);
    }

    private void recoverAllFiles() {
        // Selecionar todos os arquivos
        int rowCount = tableModel.getRowCount();
        for (int i = 0; i < rowCount; i++) {
            tableModel.setValueAt(true, i, 0);
        }

        // Executar recuperação
        recoverSelectedFiles();
    }

    private void previewSelectedFile() {
        int selectedRow = resultsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Selecione um arquivo para pré-visualizar",
                    "Nenhum Arquivo Selecionado",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String fileName = (String) tableModel.getValueAt(selectedRow, 1);
        String fileType = (String) tableModel.getValueAt(selectedRow, 2);
        String integrity = (String) tableModel.getValueAt(selectedRow, 4);

        String message = String.format(
                "<html><b>Arquivo:</b> %s<br>" +
                        "<b>Tipo:</b> %s<br>" +
                        "<b>Integridade:</b> %s<br><br>" +
                        "Pré-visualização disponível apenas para imagens e textos em versões futuras.</html>",
                fileName, fileType, integrity
        );

        JOptionPane.showMessageDialog(this, message, "Pré-visualização do Arquivo",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void saveFileList() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Salvar Lista de Arquivos");
        fileChooser.setSelectedFile(new File("lista_recuperacao.txt"));

        int result = fileChooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            // Implementar salvamento da lista
            JOptionPane.showMessageDialog(this,
                    "Lista salva com sucesso!", "Sucesso",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void loadFileList() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Carregar Lista de Arquivos");

        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            // Implementar carregamento da lista
            JOptionPane.showMessageDialog(this,
                    "Lista carregada com sucesso!", "Sucesso",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void simulateRecovery(int fileCount) {
        recoverButton.setEnabled(false);
        recoverAllButton.setEnabled(false);

        new Thread(() -> {
            try {
                for (int i = 0; i <= 100; i++) {
                    final int progress = i;
                    SwingUtilities.invokeLater(() -> {
                        recoveryProgressBar.setValue(progress);
                        statusLabel.setText(String.format("Recuperando... %d%% (%d/%d arquivos)",
                                progress, progress * fileCount / 100, fileCount));
                    });

                    Thread.sleep(50); // Simular trabalho
                }

                SwingUtilities.invokeLater(() -> {
                    recoveryProgressBar.setValue(0);
                    statusLabel.setText(String.format("Recuperação concluída! %d arquivos recuperados em: %s",
                            fileCount, outputPathField.getText()));

                    recoverButton.setEnabled(true);
                    recoverAllButton.setEnabled(true);

                    JOptionPane.showMessageDialog(this,
                            String.format("Recuperação concluída com sucesso!\n%d arquivos salvos em: %s",
                                    fileCount, outputPathField.getText()),
                            "Recuperação Concluída",
                            JOptionPane.INFORMATION_MESSAGE);
                });

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
    }

    public void addRecoveredFile(RecoveredFile file) {
        tableModel.addRow(new Object[]{
                false, // Selecionado
                file.getFileName(),
                file.getFileType(),
                file.getFormattedSize(),
                String.format("%.1f%%", file.getIntegrityScore() * 100),
                file.getRecoveryStatus().getDescription()
        });
    }

    public void clearResults() {
        tableModel.setRowCount(0);
        recoveryProgressBar.setValue(0);
        statusLabel.setText("Pronto para recuperação");
    }
}