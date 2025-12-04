package Panels;

import main.MainApplication;
import model.*;
import javax.swing.*;
import java.awt.*;
import java.util.List;

public class InventoryPanel extends JPanel {
    private JPanel inventoryPanel;
    private JTextArea itemDetails;
    private JButton slot00, slot01, slot02, slot03, slot04;
    private JButton slot10, slot11, slot12, slot13, slot14;
    private JButton slot20, slot21, slot22, slot23, slot24;
    private JButton slot30, slot31, slot32, slot33, slot34;
    private JButton useButton, discardButton, backButton;
    private JLabel titleLabel, capacityLabel;

    private MainApplication mainApp;
    private GameManager game;
    private GachaItem selectedItem;
    private JButton[] allSlots;

    public InventoryPanel(MainApplication mainApp, GameManager game) {
        this.mainApp = mainApp;
        this.game = game;

        setLayout(new BorderLayout());
        add(inventoryPanel, BorderLayout.CENTER);

        initializeComponents();
        setupEventHandlers();
        styleComponents();
        refresh();
    }

    private void initializeComponents() {
        setOpaque(false);
        if (inventoryPanel != null) {
            inventoryPanel.setOpaque(false);
        }

        // Initialize slot array
        allSlots = new JButton[]{
                slot00, slot01, slot02, slot03, slot04,
                slot10, slot11, slot12, slot13, slot14,
                slot20, slot21, slot22, slot23, slot24,
                slot30, slot31, slot32, slot33, slot34
        };

        // Configure text area
        if (itemDetails != null) {
            itemDetails.setEditable(false);
            itemDetails.setLineWrap(true);
            itemDetails.setWrapStyleWord(true);
            itemDetails.setFont(new Font("Monospaced", Font.PLAIN, 12));
            itemDetails.setBackground(new Color(40, 35, 25, 230));
            itemDetails.setForeground(new Color(255, 255, 200));
            itemDetails.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(180, 150, 100), 2),
                    BorderFactory.createEmptyBorder(10, 10, 10, 10)
            ));
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        // Draw leather-like background
        GradientPaint gradient = new GradientPaint(
                0, 0, new Color(50, 40, 30),
                0, getHeight(), new Color(70, 55, 40)
        );
        g2d.setPaint(gradient);
        g2d.fillRect(0, 0, getWidth(), getHeight());

        // Add subtle texture
        g2d.setColor(new Color(40, 30, 20, 30));
        for (int i = 0; i < getWidth(); i += 30) {
            for (int j = 0; j < getHeight(); j += 30) {
                g2d.fillOval(i, j, 20, 10);
            }
        }
    }

    private void styleComponents() {
        // Style title
        if (titleLabel != null) {
            titleLabel.setForeground(new Color(255, 215, 0));
            titleLabel.setFont(new Font("Monospaced", Font.BOLD, 28));
            titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
            titleLabel.setText("🎒 INVENTORY BAG 🎒");
        }

        // Style capacity label
        if (capacityLabel != null) {
            capacityLabel.setForeground(new Color(255, 255, 150));
            capacityLabel.setFont(new Font("Monospaced", Font.BOLD, 14));
            capacityLabel.setHorizontalAlignment(SwingConstants.CENTER);
        }

        // Style action buttons (SIMPLE - no custom painting)
        styleSimpleButton(useButton, "🛠️ USE");
        styleSimpleButton(discardButton, "🗑️ DISCARD");
        styleSimpleButton(backButton, "🔙 BACK");

        // Style slot buttons (SIMPLE - no custom painting)
        for (JButton slot : allSlots) {
            if (slot != null) styleSimpleSlot(slot);
        }
    }

    private void styleSimpleButton(JButton button, String text) {
        if (button == null) return;

        button.setText(text);
        button.setForeground(new Color(255, 215, 0));
        button.setFont(new Font("Monospaced", Font.BOLD, 14));
        button.setFocusPainted(false);

        // Stone-like appearance
        Color stoneColor = new Color(80, 70, 60, 220);
        Color stoneBorder = new Color(150, 140, 130);

        button.setBackground(stoneColor);
        button.setOpaque(true);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(stoneBorder, 2),
                BorderFactory.createEmptyBorder(8, 15, 8, 15)
        ));

        // Simple hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                if (button.isEnabled()) {
                    button.setBackground(new Color(120, 60, 40, 220));
                    button.setForeground(Color.YELLOW);
                    button.setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(Color.ORANGE, 2),
                            BorderFactory.createEmptyBorder(8, 15, 8, 15)
                    ));
                }
            }

            public void mouseExited(java.awt.event.MouseEvent e) {
                button.setBackground(stoneColor);
                button.setForeground(new Color(255, 215, 0));
                button.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(stoneBorder, 2),
                        BorderFactory.createEmptyBorder(8, 15, 8, 15)
                ));
            }
        });
    }

    private void styleSimpleSlot(JButton button) {
        if (button == null) return;

        button.setFont(new Font("Monospaced", Font.BOLD, 14));
        button.setFocusPainted(false);
        button.setOpaque(true);

        // Default empty slot style
        button.setBackground(new Color(60, 50, 40, 220));
        button.setForeground(new Color(200, 200, 180));
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(120, 110, 100), 2),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        button.setText("➕");

        // Simple hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                button.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(255, 200, 100), 2),
                        BorderFactory.createEmptyBorder(10, 10, 10, 10)
                ));
            }

            public void mouseExited(java.awt.event.MouseEvent e) {
                button.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(120, 110, 100), 2),
                        BorderFactory.createEmptyBorder(10, 10, 10, 10)
                ));
            }
        });
    }

    private void setupEventHandlers() {
        for (int i = 0; i < allSlots.length; i++) {
            final int index = i;
            if (allSlots[i] != null) {
                allSlots[i].addActionListener(e -> selectSlot(index));
            }
        }

        if (useButton != null) useButton.addActionListener(e -> useSelectedItem());
        if (discardButton != null) discardButton.addActionListener(e -> discardSelectedItem());
        if (backButton != null) backButton.addActionListener(e -> mainApp.showGame());
    }

    private void selectSlot(int slotIndex) {
        List<GachaItem> inventory = game.getCurrentPlayer().getInventory();

        // Reset all slot borders first
        for (JButton slot : allSlots) {
            if (slot != null) {
                slot.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(120, 110, 100), 2),
                        BorderFactory.createEmptyBorder(10, 10, 10, 10)
                ));
            }
        }

        if (slotIndex < inventory.size()) {
            selectedItem = inventory.get(slotIndex);
            updateItemDetails();

            // Highlight selected slot with gold border
            if (allSlots[slotIndex] != null) {
                allSlots[slotIndex].setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(255, 215, 0), 3),
                        BorderFactory.createEmptyBorder(10, 10, 10, 10)
                ));
            }
        } else {
            selectedItem = null;
            if (itemDetails != null) {
                itemDetails.setText("Empty pouch selected.\n\nClick on a filled pouch to view item details.");
            }
        }

        updateActionButtons();
    }

    private void updateItemDetails() {
        if (selectedItem == null || itemDetails == null) return;

        StringBuilder details = new StringBuilder();
        details.append("══════════════════════════════\n");
        details.append("        ITEM DETAILS          \n");
        details.append("══════════════════════════════\n\n");

        details.append("📛 ").append(selectedItem.getName()).append("\n");
        details.append("⭐ Rarity: ").append(selectedItem.getRarity()).append("\n");
        details.append("📖 ").append(selectedItem.getDescription()).append("\n\n");

        if (selectedItem instanceof KeyItem) {
            KeyItem key = (KeyItem) selectedItem;
            details.append("🔑 Key Color: ").append(key.getKeyColor()).append("\n");
            if (key.isMasterKey()) details.append("🌟 MASTER KEY\n");
        } else if (selectedItem instanceof ToolItem) {
            ToolItem tool = (ToolItem) selectedItem;
            details.append("🛠️ Type: ").append(tool.getToolType()).append("\n");
            details.append("📊 Uses: ").append(tool.getUsesRemaining()).append("\n");
        }

        itemDetails.setText(details.toString());
    }

    private void updateActionButtons() {
        boolean hasSelection = selectedItem != null;

        if (useButton != null) {
            useButton.setEnabled(hasSelection);
            useButton.setText(hasSelection ?
                    (selectedItem.getItemType() == ItemType.KEY ? "USE KEY 🔑" : "USE TOOL 🛠️") :
                    "🛠️ USE");
        }

        if (discardButton != null) {
            discardButton.setEnabled(hasSelection);
        }
    }

    private void useSelectedItem() {
        if (selectedItem == null) return;

        List<Puzzle> puzzles = game.getAvailablePuzzles();
        if (puzzles.isEmpty()) {
            mainApp.showMessage("⚠️ No puzzles available!\nGo to Puzzle panel first.");
            return;
        }

        String[] puzzleOptions = puzzles.stream()
                .map(p -> p.getDescription().length() > 40 ?
                        p.getDescription().substring(0, 37) + "..." : p.getDescription())
                .toArray(String[]::new);

        String choice = (String) JOptionPane.showInputDialog(
                mainApp,
                "Use " + selectedItem.getName() + " on:",
                "Use Item",
                JOptionPane.QUESTION_MESSAGE,
                null,
                puzzleOptions,
                puzzleOptions[0]
        );

        if (choice != null) {
            int index = List.of(puzzleOptions).indexOf(choice);
            Puzzle puzzle = puzzles.get(index);

            String result = game.getCurrentPlayer().useItemWithFeedback(selectedItem, puzzle);
            mainApp.showMessage(result);

            if (puzzle.isSolved()) {
                mainApp.showMessage("🎉 Solved!\n💰 +" + puzzle.getCoinReward() + " coins!");
            }

            refresh();
        }
    }

    private void discardSelectedItem() {
        if (selectedItem == null) return;

        int confirm = JOptionPane.showConfirmDialog(
                mainApp,
                "Discard " + selectedItem.getName() + "?",
                "Confirm",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            String result = game.getCurrentPlayer().safeRemoveItem(selectedItem);
            mainApp.showMessage(result);
            selectedItem = null;
            refresh();
        }
    }

    public void refresh() {
        List<GachaItem> inventory = game.getCurrentPlayer().getInventory();

        // Update slot buttons
        for (int i = 0; i < allSlots.length; i++) {
            if (allSlots[i] != null) {
                if (i < inventory.size()) {
                    GachaItem item = inventory.get(i);
                    String emoji = item.getItemType() == ItemType.KEY ? "🔑" : "🛠️";

                    // Shorten long item names
                    String displayName = item.getName();
                    if (displayName.length() > 8) {
                        displayName = displayName.substring(0, 7) + ".";
                    }

                    allSlots[i].setText("<html><center>" + emoji + "<br>" + displayName + "</center></html>");
                    allSlots[i].setBackground(getRarityColor(item.getRarity()));
                    allSlots[i].setForeground(Color.BLACK);
                    allSlots[i].setToolTipText(item.getName() + " (" + item.getRarity() + ")");
                } else {
                    allSlots[i].setText("➕");
                    allSlots[i].setBackground(new Color(60, 50, 40, 220));
                    allSlots[i].setForeground(new Color(200, 200, 180));
                    allSlots[i].setToolTipText("Empty slot");
                }
            }
        }

        // Update capacity label
        if (capacityLabel != null) {
            capacityLabel.setText(String.format("🎒 %d/20 Slots • %d Time Parts",
                    inventory.size(), game.getCurrentPlayer().getTimeMachinePartsCollected()));
        }

        // Update details if no selection
        if (selectedItem == null && itemDetails != null) {
            itemDetails.setText("══════════════════════════════\n" +
                    "      BAG CONTENTS            \n" +
                    "══════════════════════════════\n\n" +
                    "Items: " + inventory.size() + "/20\n" +
                    "Time Parts: " + game.getCurrentPlayer().getTimeMachinePartsCollected() + "/6\n\n" +
                    "Click any item to inspect.");
        }

        updateActionButtons();
        repaint();
    }

    private Color getRarityColor(Rarity rarity) {
        switch (rarity) {
            case COMMON: return new Color(220, 220, 220);
            case RARE: return new Color(150, 200, 255);
            case EPIC: return new Color(255, 215, 100);
            default: return Color.WHITE;
        }
    }
}