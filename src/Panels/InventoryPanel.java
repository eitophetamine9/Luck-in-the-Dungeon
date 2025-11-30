package Panels;

import main.MainApplication;
import model.GameManager;
import model.GachaItem;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class InventoryPanel extends JPanel {
    private JPanel inventoryPanel;
    private JTextArea itemDetails;

    // Inventory Slots - Row 1
    private JButton slot00, slot01, slot02, slot03, slot04;
    // Inventory Slots - Row 2
    private JButton slot10, slot11, slot12, slot13, slot14;
    // Inventory Slots - Row 3
    private JButton slot20, slot21, slot22, slot23, slot24;
    // Inventory Slots - Row 4
    private JButton slot30, slot31, slot32, slot33, slot34;

    // Action Buttons
    private JButton useButton;
    private JButton discardButton;
    private JButton backButton;
    private JLabel titleLabel;
    private JLabel capacityLabel;

    private MainApplication mainApp;
    private GameManager game;
    private GachaItem selectedItem;
    private JButton[] allSlots;

    public InventoryPanel(MainApplication mainApp, GameManager game) {
        this.mainApp = mainApp;
        this.game = game;
        initializePanel();
        setupEventHandlers();
        refresh();
    }

    private void initializePanel() {
        setLayout(new BorderLayout());
        add(inventoryPanel, BorderLayout.CENTER);

        // Configure text area
        itemDetails.setEditable(false);
        itemDetails.setLineWrap(true);
        itemDetails.setWrapStyleWord(true);

        // Initialize slot array for easy access
        allSlots = new JButton[]{
                slot00, slot01, slot02, slot03, slot04,
                slot10, slot11, slot12, slot13, slot14,
                slot20, slot21, slot22, slot23, slot24,
                slot30, slot31, slot32, slot33, slot34
        };
    }

    private void setupEventHandlers() {
        // Setup all slot buttons
        for (int i = 0; i < allSlots.length; i++) {
            final int index = i;
            allSlots[i].addActionListener(e -> selectSlot(index));
        }

        useButton.addActionListener(e -> useSelectedItem());
        discardButton.addActionListener(e -> discardSelectedItem());
        backButton.addActionListener(e -> mainApp.showGame());
    }

    private void selectSlot(int slotIndex) {
        List<GachaItem> inventory = game.getCurrentPlayer().getInventory();

        if (slotIndex < inventory.size()) {
            selectedItem = inventory.get(slotIndex);
            updateItemDetails();
        } else {
            selectedItem = null;
            itemDetails.setText("SELECTED: None\nSelect an item to view details");
        }

        updateActionButtons();
    }

    private void updateItemDetails() {
        if (selectedItem != null) {
            itemDetails.setText(
                    "SELECTED: " + selectedItem.getName() + "\n" +
                            "Rarity: " + selectedItem.getRarity() + "\n" +
                            selectedItem.getDescription()
            );
        }
    }

    private void updateActionButtons() {
        boolean hasSelection = selectedItem != null;
        useButton.setEnabled(hasSelection);
        discardButton.setEnabled(hasSelection);
    }

    private void useSelectedItem() {
        if (selectedItem == null) return;

        mainApp.showMessage("Using " + selectedItem.getName() + " on puzzle...");
        // Puzzle usage logic would go here
        refresh();
    }

    private void discardSelectedItem() {
        if (selectedItem == null) return;

        if (mainApp.showConfirmDialog("Discard " + selectedItem.getName() + "?")) {
            game.getCurrentPlayer().getInventory().remove(selectedItem);
            selectedItem = null;
            mainApp.showMessage("Item discarded!");
            refresh();
        }
    }

    public void refresh() {
        List<GachaItem> inventory = game.getCurrentPlayer().getInventory();

        // Update all slot buttons
        for (int i = 0; i < allSlots.length; i++) {
            if (i < inventory.size()) {
                GachaItem item = inventory.get(i);
                allSlots[i].setText(getItemIcon(item));
                allSlots[i].setBackground(getRarityColor(item.getRarity()));
            } else {
                allSlots[i].setText("➕");
                allSlots[i].setBackground(Color.LIGHT_GRAY);
            }
        }

        // Update capacity label
        capacityLabel.setText("(" + inventory.size() + "/20 Slots Used)");

        // Reset selection
        selectedItem = null;
        itemDetails.setText("SELECTED: None\nSelect an item to view details");
        updateActionButtons();
    }

    private String getItemIcon(GachaItem item) {
        switch (item.getItemType()) {
            case KEY: return "🔑";
            case TOOL: return "🛠️";
            default: return "❓";
        }
    }

    private Color getRarityColor(model.Rarity rarity) {
        switch (rarity) {
            case COMMON: return Color.WHITE;
            case RARE: return new Color(173, 216, 230); // Light blue
            case EPIC: return new Color(255, 215, 0);   // Gold
            default: return Color.WHITE;
        }
    }
}