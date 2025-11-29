package gui;

import engine.DeviceAnalyzer;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class DeviceSelector extends JPanel {
    private JComboBox<String> deviceComboBox;
    private DeviceAnalyzer deviceAnalyzer;
    private JLabel infoLabel;

    public DeviceSelector() {
        deviceAnalyzer = new DeviceAnalyzer();
        initializeComponents();
        setupLayout();
        loadDevices();
    }

    private void initializeComponents() {
        deviceComboBox = new JComboBox<>();
        deviceComboBox.addActionListener(e -> updateDeviceInfo());

        infoLabel = new JLabel("Selecione um dispositivo");
        infoLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    }

    private void setupLayout() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createTitledBorder("Seleção de Dispositivo"));

        JPanel comboBoxPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        comboBoxPanel.add(new JLabel("Dispositivo:"));
        comboBoxPanel.add(deviceComboBox);

        add(comboBoxPanel, BorderLayout.NORTH);
        add(infoLabel, BorderLayout.CENTER);

        JButton refreshButton = new JButton("Atualizar");
        refreshButton.addActionListener(e -> loadDevices());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(refreshButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void loadDevices() {
        deviceComboBox.removeAllItems();
        var devices = deviceAnalyzer.analyzeSystemDevices();

        for (var device : devices) {
            deviceComboBox.addItem(device.getMountPoint() + " - " + device.getName() +
                    " (" + device.getFormattedSize() + ")");
        }

        if (!devices.isEmpty()) {
            deviceComboBox.setSelectedIndex(0);
        }
    }

    private void updateDeviceInfo() {
        int selectedIndex = deviceComboBox.getSelectedIndex();
        if (selectedIndex == -1) return;

        var devices = deviceAnalyzer.analyzeSystemDevices();
        if (selectedIndex < devices.size()) {
            var device = devices.get(selectedIndex);

            String info = String.format(
                    "<html><b>%s</b><br>" +
                            "Sistema de Arquivos: %s<br>" +
                            "Tamanho Total: %s<br>" +
                            "Espaço Livre: %s (%.1f%%)<br>" +
                            "Espaço Usado: %s (%.1f%%)</html>",
                    device.getName(),
                    device.getFileSystem(),
                    formatSize(device.getTotalSpace()),
                    formatSize(device.getFreeSpace()),
                    (device.getTotalSpace() > 0 ? (double) device.getFreeSpace() / device.getTotalSpace() * 100 : 0),
                    formatSize(device.getUsedSpace()),
                    device.getUsagePercentage()
            );

            infoLabel.setText(info);
        }
    }

    public String getSelectedDevicePath() {
        int selectedIndex = deviceComboBox.getSelectedIndex();
        if (selectedIndex == -1) return null;

        var devices = deviceAnalyzer.analyzeSystemDevices();
        if (selectedIndex < devices.size()) {
            return devices.get(selectedIndex).getMountPoint();
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
}