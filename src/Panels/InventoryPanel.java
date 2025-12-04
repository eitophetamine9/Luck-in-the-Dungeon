package Panels;

import main.MainApplication;
import model.GameManager;
import model.GachaItem;
import model.Puzzle;
import model.Rarity;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class InventoryPanel extends JPanel {
    // Form components
    private JPanel inventoryPanel;
    private JTextArea itemDetails;

    // Inventory Slots
    private JButton slot00, slot01, slot02, slot03, slot04;
    private JButton slot10, slot11, slot12, slot13, slot14;
    private JButton slot20, slot21, slot22, slot23, slot24;
    private JButton slot30, slot31, slot32, slot33, slot34;

    // Action Buttons
    private JButton useButton;
    private JButton discardButton;
    private JButton backButton;
    private JLabel titleLabel;
    private JLabel capacityLabel; // This was null in your form

    private MainApplication mainApp;
    private GameManager game;
    private GachaItem selectedItem;
    private JButton[] allSlots;
    private Image invImage;

    public InventoryPanel(MainApplication mainApp, GameManager game) {
        this.mainApp = mainApp;
        this.game = game;

        // Initialize form components
        setLayout(new BorderLayout());

        // ✅ SAFE: Check if inventoryPanel exists before adding
        if (inventoryPanel != null) {
            add(inventoryPanel, BorderLayout.CENTER);
        } else {
            // Fallback: create basic panel
            System.err.println("❌ inventoryPanel is null - creating fallback UI");
            createFallbackUI();
        }

        initializePanel();
        setupEventHandlers();
        loadinvImage();
        setOpaque(false);
        if (inventoryPanel != null){
            inventoryPanel.setOpaque(false);
        }

        // ✅ SAFE: Only refresh if components are initialized
        if (componentsInitialized()) {
            refresh();
        } else {
            System.err.println("❌ Components not initialized - skipping refresh");
        }
    }

    private void createFallbackUI() {
        JPanel fallbackPanel = new JPanel(new BorderLayout());
        JLabel fallbackLabel = new JLabel("Inventory Panel - Form not loaded properly", JLabel.CENTER);
        fallbackLabel.setFont(new Font("Arial", Font.BOLD, 16));
        fallbackPanel.add(fallbackLabel, BorderLayout.CENTER);

        JButton backBtn = new JButton("Back to Game");
        backBtn.addActionListener(e -> mainApp.showGame());
        fallbackPanel.add(backBtn, BorderLayout.SOUTH);

        add(fallbackPanel, BorderLayout.CENTER);
    }

    private boolean componentsInitialized() {
        // Check if critical components are initialized
        boolean slotsInitialized = allSlots != null && allSlots.length > 0;
        boolean buttonsInitialized = useButton != null && discardButton != null && backButton != null;

        return slotsInitialized && buttonsInitialized;
    }

    private void initializePanel() {
        // ✅ SAFE: Check before configuring
        if (itemDetails != null) {
            itemDetails.setEditable(false);
            itemDetails.setLineWrap(true);
            itemDetails.setWrapStyleWord(true);
            itemDetails.setFont(new Font("Monospaced", Font.PLAIN, 12));
        } else {
            System.err.println("❌ itemDetails is null");
        }

        // ✅ SAFE: Initialize slot array
        if (slot00 != null && slot01 != null && slot02 != null && slot03 != null && slot04 != null &&
                slot10 != null && slot11 != null && slot12 != null && slot13 != null && slot14 != null &&
                slot20 != null && slot21 != null && slot22 != null && slot23 != null && slot24 != null &&
                slot30 != null && slot31 != null && slot32 != null && slot33 != null && slot34 != null) {

            allSlots = new JButton[]{
                    slot00, slot01, slot02, slot03, slot04,
                    slot10, slot11, slot12, slot13, slot14,
                    slot20, slot21, slot22, slot23, slot24,
                    slot30, slot31, slot32, slot33, slot34
            };
        } else {
            System.err.println("❌ Some slot buttons are null");
            allSlots = new JButton[0]; // Empty array to prevent NPE
        }
    }

    private void setupEventHandlers() {
        // ✅ SAFE: Setup slot buttons
        for (int i = 0; i < allSlots.length; i++) {
            if (allSlots[i] != null) {
                final int index = i;
                allSlots[i].addActionListener(e -> selectSlot(index));
            }
        }

        // ✅ SAFE: Setup action buttons
        if (useButton != null) {
            useButton.addActionListener(e -> useSelectedItem());
        } else {
            System.err.println("❌ useButton is null");
        }

        if (discardButton != null) {
            discardButton.addActionListener(e -> discardSelectedItem());
        } else {
            System.err.println("❌ discardButton is null");
        }

        if (backButton != null) {
            backButton.addActionListener(e -> mainApp.showGame());
        } else {
            System.err.println("❌ backButton is null");
        }
    }

    private void selectSlot(int slotIndex) {
        if (slotIndex < 0 || slotIndex >= allSlots.length || allSlots[slotIndex] == null) {
            return;
        }

        List<GachaItem> inventory = game.getCurrentPlayer().getInventory();

        if (slotIndex < inventory.size()) {
            selectedItem = inventory.get(slotIndex);
            updateItemDetails();

            // Visual feedback for selected slot
            for (int i = 0; i < allSlots.length; i++) {
                if (allSlots[i] != null) {
                    allSlots[i].setBorder(i == slotIndex ?
                            BorderFactory.createLineBorder(Color.RED, 3) :
                            BorderFactory.createLineBorder(Color.GRAY, 1));
                }
            }
        } else {
            selectedItem = null;
            if (itemDetails != null) {
                itemDetails.setText("SELECTED: None\n\nSelect an item to view details and actions");
            }
        }

        updateActionButtons();
    }

    private void updateItemDetails() {
        if (selectedItem != null && itemDetails != null) {
            String details = "🎒 SELECTED ITEM:\n" +
                    "📛 Name: " + selectedItem.getName() + "\n" +
                    "⭐ Rarity: " + selectedItem.getRarity() + "\n" +
                    "🔧 Type: " + selectedItem.getItemType() + "\n" +
                    "📖 " + selectedItem.getDescription() + "\n\n";

            if (selectedItem instanceof model.KeyItem) {
                model.KeyItem key = (model.KeyItem) selectedItem;
                details += "🔑 Key Color: " + key.getKeyColor() + "\n";
                details += key.isMasterKey() ? "🌟 MASTER KEY (opens any lock)" : "Standard key";
            } else if (selectedItem instanceof model.ToolItem) {
                model.ToolItem tool = (model.ToolItem) selectedItem;
                details += "🛠️ Tool Type: " + tool.getToolType() + "\n";
                details += "📊 Uses Remaining: " + tool.getUsesRemaining();
            }

            itemDetails.setText(details);
        }
    }

    private void updateActionButtons() {
        boolean hasSelection = selectedItem != null;

        if (useButton != null) {
            useButton.setEnabled(hasSelection);
        }

        if (discardButton != null) {
            discardButton.setEnabled(hasSelection);
        }

        if (hasSelection && useButton != null) {
            useButton.setText(selectedItem.getItemType() == model.ItemType.KEY ?
                    "Use Key 🔑" : "Use Tool 🛠️");
        }
    }

    private void useSelectedItem() {
        if (selectedItem == null) return;

        List<Puzzle> availablePuzzles = game.getAvailablePuzzles();
        if (availablePuzzles.isEmpty()) {
            mainApp.showMessage("❌ No puzzles available to use this item on!\n" +
                    "Go to the Puzzle panel to find puzzles.");
            return;
        }

        Puzzle[] puzzleArray = availablePuzzles.toArray(new Puzzle[0]);
        String[] puzzleNames = new String[puzzleArray.length];
        for (int i = 0; i < puzzleArray.length; i++) {
            puzzleNames[i] = (i+1) + ". " + puzzleArray[i].getDescription().substring(0,
                    Math.min(50, puzzleArray[i].getDescription().length())) +
                    (puzzleArray[i].getDescription().length() > 50 ? "..." : "");
        }

        String selectedPuzzleName = (String) JOptionPane.showInputDialog(
                mainApp,
                "Select a puzzle to use " + selectedItem.getName() + " on:",
                "Use Item on Puzzle",
                JOptionPane.QUESTION_MESSAGE,
                null,
                puzzleNames,
                puzzleNames[0]
        );

        if (selectedPuzzleName != null) {
            int puzzleIndex = Integer.parseInt(selectedPuzzleName.split("\\.")[0]) - 1;
            Puzzle selectedPuzzle = puzzleArray[puzzleIndex];

            String result = game.getCurrentPlayer().useItemWithFeedback(selectedItem, selectedPuzzle);
            mainApp.showMessage(result);

            if (selectedPuzzle.isSolved()) {
                mainApp.showMessage("🎉 Puzzle Solved!\n" +
                        "💰 Earned: " + selectedPuzzle.getCoinReward() + " coins!");
            }

            refresh();
        }
    }

    private void discardSelectedItem() {
        if (selectedItem == null) return;

        String result = game.getCurrentPlayer().safeRemoveItem(selectedItem);

        if (result.contains("✅")) {
            mainApp.showMessage("🗑️ Discarded: " + selectedItem.getName());
            selectedItem = null;
            refresh();
        } else {
            mainApp.showMessage("❌ Failed to discard: " + result);
        }
    }

    public void refresh() {
        // ✅ SAFE: Check if components are initialized before refreshing
        if (!componentsInitialized()) {
            System.err.println("❌ Cannot refresh - components not initialized");
            return;
        }

        List<GachaItem> inventory = game.getCurrentPlayer().getInventory();

        // ✅ SAFE: Update slot buttons with null checks
        for (int i = 0; i < allSlots.length; i++) {
            if (allSlots[i] != null) {
                if (i < inventory.size()) {
                    GachaItem item = inventory.get(i);
                    allSlots[i].setText("<html><center>" + getItemIcon(item) + "<br>" +
                            item.getName().substring(0, Math.min(8, item.getName().length())) +
                            "</center></html>");
                    allSlots[i].setBackground(getRarityColor(item.getRarity()));
                    allSlots[i].setToolTipText(item.getName() + " (" + item.getRarity() + ")");
                } else {
                    allSlots[i].setText("➕");
                    allSlots[i].setBackground(Color.LIGHT_GRAY);
                    allSlots[i].setToolTipText("Empty Slot");
                }

                allSlots[i].setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
            }
        }

        // ✅ FIXED: Safe capacityLabel usage
        if (capacityLabel != null) {
            capacityLabel.setText("🎒 " + game.getCurrentPlayer().getInventoryStatus() +
                    " (" + inventory.size() + "/20)");
        } else {
            System.err.println("❌ capacityLabel is null - cannot update inventory status");
            // Fallback: show status in itemDetails if available
            if (itemDetails != null) {
                itemDetails.setText("🎒 " + game.getCurrentPlayer().getInventoryStatus() +
                        " (" + inventory.size() + "/20)\n\n" +
                        "SELECTED: None\n\n" +
                        "Click on an item to view details and use/discard options");
            }
        }

        // Reset selection and details
        selectedItem = null;
        if (itemDetails != null) {
            itemDetails.setText("SELECTED: None\n\n" +
                    "Click on an item to view details and use/discard options\n\n" +
                    "🎒 " + game.getCurrentPlayer().getInventorySummary());
        }
        updateActionButtons();
    }

    private String getItemIcon(GachaItem item) {
        switch (item.getItemType()) {
            case KEY: return "🔑";
            case TOOL: return "🛠️";
            default: return "❓";
        }
    }

    private Color getRarityColor(Rarity rarity) {
        switch (rarity) {
            case COMMON: return Color.WHITE;
            case RARE: return new Color(173, 216, 230);
            case EPIC: return new Color(255, 215, 0);
            default: return Color.WHITE;
        }
    }


    private void loadinvImage() {
        try {
            System.out.println("🔍 Loading background image for Inventory Panel...");

            // Try to load the same background as MainMenuPanel
            java.net.URL imageURL = getClass().getClassLoader().getResource("images/leather-texture-background.jpg");

            if (imageURL != null) {
                System.out.println("✅ Found image at URL: " + imageURL);
                invImage = new ImageIcon(imageURL).getImage();
                System.out.println("✅ Map background loaded!");
                return;
            } else {
                System.out.println("❌ Image not found, using gradient fallback");
                invImage = null;
            }

        } catch (Exception e) {
            System.out.println("❌ Error loading background: " + e.getMessage());
            invImage = null;
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (invImage != null) {
            // Draw image scaled to panel size
            g.drawImage(invImage, 0, 0, getWidth(), getHeight(), this);
        } else {
            // Fallback gradient
            Graphics2D g2d = (Graphics2D) g;
            Color color1 = new Color(30, 30, 60);
            Color color2 = new Color(50, 50, 100);
            g2d.setPaint(new GradientPaint(0, 0, color1, 0, getHeight(), color2));
            g2d.fillRect(0, 0, getWidth(), getHeight());
        }
    }
}