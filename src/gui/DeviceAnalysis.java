package gui;

import engine.BlockVisualizer;
import engine.DeviceAnalyzer;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.DecimalFormat;

public class DeviceAnalysis extends JPanel {
    private DeviceAnalyzer deviceAnalyzer;
    private BlockVisualizer blockVisualizer;
    private JTable deviceTable;
    private DefaultTableModel tableModel;
    private JProgressBar usageBar;
    private JLabel infoLabel;

    public DeviceAnalysis() {
        deviceAnalyzer = new DeviceAnalyzer();
        initializeComponents();
        setupLayout();
        loadDevices();
    }

    private void initializeComponents() {
        // Tabela de dispositivos
        String[] columns = {"Ponto de Montagem", "Nome", "Sistema de Arquivos", "Tamanho Total", "Livre", "Usado", "Uso"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        deviceTable = new JTable(tableModel);
        deviceTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        deviceTable.getSelectionModel().addListSelectionListener(e -> updateDeviceDetails());

        // Visualizador de blocos
        blockVisualizer = new BlockVisualizer();

        // Componentes de detalhes
        usageBar = new JProgressBar(0, 100);
        usageBar.setStringPainted(true);
        infoLabel = new JLabel("Selecione um dispositivo para ver detalhes");
        infoLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    }

    private void setupLayout() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Painel superior com tabela
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createTitledBorder("Dispositivos de Armazenamento"));
        tablePanel.add(new JScrollPane(deviceTable), BorderLayout.CENTER);

        // Painel de detalhes
        JPanel detailsPanel = new JPanel(new BorderLayout());
        detailsPanel.setBorder(BorderFactory.createTitledBorder("Detalhes do Dispositivo"));

        JPanel infoPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        infoPanel.add(infoLabel);
        infoPanel.add(usageBar);
        detailsPanel.add(infoPanel, BorderLayout.NORTH);
        detailsPanel.add(blockVisualizer, BorderLayout.CENTER);

        // Layout principal
        add(tablePanel, BorderLayout.NORTH);
        add(detailsPanel, BorderLayout.CENTER);

        // Botão de atualizar
        JButton refreshButton = new JButton("Atualizar Dispositivos");
        refreshButton.addActionListener(e -> loadDevices());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(refreshButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void loadDevices() {
        tableModel.setRowCount(0);
        var devices = deviceAnalyzer.analyzeSystemDevices();

        DecimalFormat df = new DecimalFormat("#.##");

        for (var device : devices) {
            tableModel.addRow(new Object[]{
                    device.getMountPoint(),
                    device.getName(),
                    device.getFileSystem(),
                    formatSize(device.getTotalSpace()),
                    formatSize(device.getFreeSpace()),
                    formatSize(device.getUsedSpace()),
                    String.format("%.1f%%", device.getUsagePercentage())
            });
        }

        if (!devices.isEmpty()) {
            deviceTable.setRowSelectionInterval(0, 0);
        }
    }

    private void updateDeviceDetails() {
        int selectedRow = deviceTable.getSelectedRow();
        if (selectedRow == -1) return;

        String mountPoint = (String) tableModel.getValueAt(selectedRow, 0);
        var device = deviceAnalyzer.getDeviceByPath(mountPoint);

        if (device != null) {
            // Atualizar barra de progresso
            int usagePercent = (int) device.getUsagePercentage();
            usageBar.setValue(usagePercent);
            usageBar.setForeground(getUsageColor(usagePercent));

            // Atualizar informações
            String info = String.format(
                    "<html><b>%s</b><br>Sistema de Arquivos: %s<br>Tamanho Total: %s<br>Espaço Livre: %s<br>Espaço Usado: %s</html>",
                    device.getName(),
                    device.getFileSystem(),
                    formatSize(device.getTotalSpace()),
                    formatSize(device.getFreeSpace()),
                    formatSize(device.getUsedSpace())
            );
            infoLabel.setText(info);

            // Atualizar visualizador de blocos (simulação)
            long totalBlocks = device.getTotalSpace() / 4096; // Blocos de 4KB
            blockVisualizer.setDiskInfo(totalBlocks, 4096);

            // Simular alguns blocos para demonstração
            simulateBlockData(totalBlocks);
        }
    }

    private void simulateBlockData(long totalBlocks) {
        blockVisualizer.clearBlocks();

        // Simular alguns tipos de blocos para demonstração
        for (long i = 0; i < Math.min(totalBlocks, 10000); i++) {
            if (i % 10 == 0) {
                blockVisualizer.setBlockStatus(i, BlockVisualizer.BlockType.SYSTEM);
            } else if (i % 7 == 0) {
                blockVisualizer.setBlockStatus(i, BlockVisualizer.BlockType.ALLOCATED);
            } else if (i % 15 == 0) {
                blockVisualizer.setBlockStatus(i, BlockVisualizer.BlockType.DELETED);
            } else if (i % 20 == 0) {
                blockVisualizer.setBlockStatus(i, BlockVisualizer.BlockType.RECOVERABLE);
            }
        }
    }

    private Color getUsageColor(int usagePercent) {
        if (usagePercent < 70) return Color.GREEN;
        if (usagePercent < 90) return Color.ORANGE;
        return Color.RED;
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

    public String getSelectedDevice() {
        int selectedRow = deviceTable.getSelectedRow();
        if (selectedRow == -1) return null;
        return (String) tableModel.getValueAt(selectedRow, 0);
    }
}