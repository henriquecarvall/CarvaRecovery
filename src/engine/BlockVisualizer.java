package engine;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class BlockVisualizer extends JPanel {
    private Map<Long, BlockType> blockMap;
    private long totalBlocks;
    private long blockSize;

    public enum BlockType {
        FREE(Color.WHITE, "Livre"),
        ALLOCATED(Color.BLUE, "Alocado"),
        SYSTEM(Color.RED, "Sistema"),
        DELETED(Color.ORANGE, "Excluído"),
        RECOVERABLE(Color.GREEN, "Recuperável"),
        BAD_SECTOR(Color.BLACK, "Setor Danificado");

        private final Color color;
        private final String description;

        BlockType(Color color, String description) {
            this.color = color;
            this.description = description;
        }

        public Color getColor() { return color; }
        public String getDescription() { return description; }
    }

    public BlockVisualizer() {
        this.blockMap = new HashMap<>();
        setPreferredSize(new Dimension(800, 400));
        setBackground(Color.LIGHT_GRAY);
    }

    public void setDiskInfo(long totalBlocks, long blockSize) {
        this.totalBlocks = totalBlocks;
        this.blockSize = blockSize;
        repaint();
    }

    public void setBlockStatus(long blockNumber, BlockType type) {
        blockMap.put(blockNumber, type);
    }

    public void clearBlocks() {
        blockMap.clear();
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (totalBlocks == 0) return;

        int width = getWidth();
        int height = getHeight();

        int blocksPerRow = 100;
        int blockSizePx = Math.min(width / blocksPerRow, 20);
        int rows = (int) Math.ceil((double) totalBlocks / blocksPerRow);

        int startY = (height - rows * blockSizePx) / 2;

        for (long i = 0; i < totalBlocks; i++) {
            int row = (int) (i / blocksPerRow);
            int col = (int) (i % blocksPerRow);

            int x = col * blockSizePx;
            int y = startY + row * blockSizePx;

            BlockType type = blockMap.getOrDefault(i, BlockType.FREE);
            g.setColor(type.getColor());
            g.fillRect(x, y, blockSizePx - 1, blockSizePx - 1);
            g.setColor(Color.GRAY);
            g.drawRect(x, y, blockSizePx - 1, blockSizePx - 1);
        }

        // Legenda
        drawLegend(g, 10, 10);
    }

    private void drawLegend(Graphics g, int x, int y) {
        g.setColor(Color.BLACK);
        g.drawString("Legenda do Mapa de Blocos:", x, y);

        int legendY = y + 20;
        for (BlockType type : BlockType.values()) {
            g.setColor(type.getColor());
            g.fillRect(x, legendY, 15, 15);
            g.setColor(Color.BLACK);
            g.drawRect(x, legendY, 15, 15);
            g.drawString(type.getDescription(), x + 20, legendY + 12);
            legendY += 20;
        }
    }
}