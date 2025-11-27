// InventoryPanel.java
package view;

import model.*;
import exceptions.*;
import javax.swing.*;
import java.awt.*;
import java.util.List;

public class InventoryPanel extends JPanel {
    private MainFrame parent;
    private GameManager game;

    private JPanel itemsPanel;
    private JButton backButton;
    private JLabel inventoryStatsLabel;

    public InventoryPanel(MainFrame parent, GameManager game) {
        this.parent = parent;
        this.game = game;
        initializeGUI();
    }

    private void initializeGUI() {
        setLayout(new BorderLayout());
        setBackground(new Color(40, 40, 80));

        // Title
        JLabel titleLabel = new JLabel("INVENTORY", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 32));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        // Items Panel
        itemsPanel = new JPanel();
        itemsPanel.setLayout(new BoxLayout(itemsPanel, BoxLayout.Y_AXIS));
        itemsPanel.setBackground(new Color(40, 40, 80));

        JScrollPane scrollPane = new JScrollPane(itemsPanel);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Items"));

        // Stats
        inventoryStatsLabel = new JLabel("", JLabel.CENTER);
        inventoryStatsLabel.setForeground(Color.YELLOW);
        inventoryStatsLabel.setFont(new Font("Arial", Font.BOLD, 14));

        // Back button
        backButton = new JButton("Back to Game");
        backButton.setFont(new Font("Arial", Font.BOLD, 16));
        backButton.setBackground(new Color(80, 80, 140));
        backButton.setForeground(Color.WHITE);
        backButton.addActionListener(e -> parent.showGame());

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(new Color(50, 50, 100));
        bottomPanel.add(inventoryStatsLabel, BorderLayout.CENTER);
        bottomPanel.add(backButton, BorderLayout.EAST);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(titleLabel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    public void refresh() {
        Player player = game.getCurrentPlayer();
        List<GachaItem> inventory = player.getInventory();

        // Update stats
        inventoryStatsLabel.setText(String.format(
                "Items: %d/%d | Keys: %d | Tools: %d",
                player.getCurrentInventorySize(), player.getMaxInventorySize(),
                countKeys(inventory), countTools(inventory)
        ));

        // Update items display
        itemsPanel.removeAll();

        if (inventory.isEmpty()) {
            JLabel emptyLabel = new JLabel("Inventory is empty. Pull from gacha machines to get items!");
            emptyLabel.setForeground(Color.WHITE);
            emptyLabel.setFont(new Font("Arial", Font.ITALIC, 16));
            emptyLabel.setAlignmentX(CENTER_ALIGNMENT);
            itemsPanel.add(emptyLabel);
        } else {
            for (GachaItem item : inventory) {
                JPanel itemCard = createItemCard(item);
                itemsPanel.add(itemCard);
                itemsPanel.add(Box.createRigidArea(new Dimension(0, 10)));
            }
        }

        itemsPanel.revalidate();
        itemsPanel.repaint();
    }

    private int countKeys(List<GachaItem> inventory) {
        return (int) inventory.stream()
                .filter(item -> item instanceof KeyItem)
                .count();
    }

    private int countTools(List<GachaItem> inventory) {
        return (int) inventory.stream()
                .filter(item -> item instanceof ToolItem)
                .count();
    }

    private JPanel createItemCard(GachaItem item) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(getRarityColor(item.getRarity()));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.WHITE),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        // Item info
        JLabel nameLabel = new JLabel(item.getName());
        nameLabel.setFont(new Font("Arial", Font.BOLD, 16));
        nameLabel.setForeground(Color.WHITE);

        JLabel descLabel = new JLabel(item.getDescription());
        descLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        descLabel.setForeground(Color.WHITE);

        JLabel rarityLabel = new JLabel("Rarity: " + item.getRarity());
        rarityLabel.setFont(new Font("Arial", Font.BOLD, 12));
        rarityLabel.setForeground(getRarityTextColor(item.getRarity()));

        JPanel infoPanel = new JPanel(new GridLayout(3, 1));
        infoPanel.setBackground(getRarityColor(item.getRarity()));
        infoPanel.add(nameLabel);
        infoPanel.add(descLabel);
        infoPanel.add(rarityLabel);

        // Specific item details
        JLabel detailsLabel = new JLabel();
        detailsLabel.setForeground(Color.WHITE);
        detailsLabel.setFont(new Font("Arial", Font.PLAIN, 12));

        if (item instanceof KeyItem) {
            KeyItem key = (KeyItem) item;
            detailsLabel.setText("Key Color: " + key.getKeyColor() +
                    (key.isMasterKey() ? " | MASTER KEY" : ""));
        } else if (item instanceof ToolItem) {
            ToolItem tool = (ToolItem) item;
            detailsLabel.setText("Type: " + tool.getToolType() +
                    " | Uses: " + tool.getUsesRemaining());
        }

        JPanel detailsPanel = new JPanel(new BorderLayout());
        detailsPanel.setBackground(getRarityColor(item.getRarity()));
        detailsPanel.add(infoPanel, BorderLayout.CENTER);
        detailsPanel.add(detailsLabel, BorderLayout.SOUTH);

        card.add(detailsPanel, BorderLayout.CENTER);

        return card;
    }

    private Color getRarityColor(Rarity rarity) {
        switch (rarity) {
            case COMMON: return new Color(100, 100, 100);    // Gray
            case RARE: return new Color(0, 100, 200);        // Blue
            case EPIC: return new Color(180, 0, 180);        // Purple
            default: return new Color(60, 60, 110);
        }
    }

    private Color getRarityTextColor(Rarity rarity) {
        switch (rarity) {
            case COMMON: return Color.LIGHT_GRAY;
            case RARE: return Color.CYAN;
            case EPIC: return Color.MAGENTA;
            default: return Color.WHITE;
        }
    }
}