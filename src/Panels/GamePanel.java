package Panels;

import main.MainApplication;
import model.GameManager;
import model.Room;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class GamePanel extends JPanel {
    // ✅ ADD saveButton to your component list
    private JPanel gamePanel;
    private JScrollPane descScroll;
    private JTextArea roomDescriptionTextArea;
    private JLabel roomHeaderLabel;
    private JButton puzzleBtn;
    private JButton mainMenuBtn;
    private JButton gachaBtn;
    private JButton inventoryBtn;
    private JButton nextRoomBtn;
    private JButton mapBtn;
    private JButton saveButton; // ✅ ADD THIS
    private JLabel coinsLabel;
    private JLabel puzzleAvailableLabel;
    private JLabel roomProgressLabel;

    private MainApplication mainApp;
    private GameManager game;
    private Image backgroundImage;
    private String[] roomBackgrounds = { // 🆕 ADDED: Room-specific background names
            "room1_lab.png",
            "room2_archives.png",
            "room3_alchemy.png",
            "room4_observatory.png"
    };

    // 🆕 ADD: Room change tracker
    private int lastRoomIndex = -1;

    public GamePanel(MainApplication mainApp, GameManager game) {
        this.mainApp = mainApp;
        this.game = game;
        this.lastRoomIndex = -1; // Initialize tracker

        // ✅ Initialize form components
        setLayout(new BorderLayout());
        add(gamePanel, BorderLayout.CENTER);

        // 🆕 Test image loading first
        testImageLoading();

        // ✅ Load room-specific background image
        updateBackground();

        // ✅ Make main panel transparent to show background
        setOpaque(false);

        // ✅ Make the form's panel transparent
        if (gamePanel != null) {
            gamePanel.setOpaque(false);
        }

        // ✅ Get text area from scroll pane
        if (descScroll != null && roomDescriptionTextArea == null) {
            roomDescriptionTextArea = (JTextArea) descScroll.getViewport().getView();
        }

        // ✅ FIX: Make JScrollPane and viewport transparent
        if (descScroll != null) {
            descScroll.setOpaque(false);
            descScroll.getViewport().setOpaque(false);
            descScroll.setBackground(new Color(20, 20, 30, 180));
            descScroll.getViewport().setBackground(new Color(30, 30, 60, 200));
            descScroll.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        }

        // ✅ Configure text area with monospaced font
        if (roomDescriptionTextArea != null) {
            roomDescriptionTextArea.setEditable(false);
            roomDescriptionTextArea.setLineWrap(true);
            roomDescriptionTextArea.setWrapStyleWord(true);
            roomDescriptionTextArea.setFont(new Font("Monospaced", Font.BOLD, 14));
            roomDescriptionTextArea.setForeground(Color.cyan);
            roomDescriptionTextArea.setBackground(new Color(30, 30, 60, 180)); // Semi-transparent
            roomDescriptionTextArea.setCaretColor(Color.cyan);
            roomDescriptionTextArea.setText(""); // Clear any initial text
            roomDescriptionTextArea.setDisabledTextColor(new Color(255, 255, 200));
        }

        setupEventHandlers();

        // ✅ Apply dungeon-themed button styling
        setupDungeonButtons();

        refresh();
    }

    // 🆕 DEBUG: Test image loading
    private void testImageLoading() {
        System.out.println("\n🔍 TESTING IMAGE LOADING...");

        // Test all room images
        for (int i = 0; i < roomBackgrounds.length; i++) {
            String imagePath = "images/rooms/" + roomBackgrounds[i];
            java.net.URL url = getClass().getClassLoader().getResource(imagePath);
            System.out.println((i+1) + ". " + imagePath + " → " +
                    (url != null ? "✅ FOUND" : "❌ NOT FOUND"));
        }

        // Test default
        java.net.URL defaultURL = getClass().getClassLoader().getResource("images/dungeon_bg.jpeg");
        System.out.println("Default: images/dungeon_bg.jpeg → " +
                (defaultURL != null ? "✅ FOUND" : "❌ NOT FOUND"));
    }

    // 🆕 UPDATED: Now loads room-specific backgrounds with room change detection
    private void updateBackground() {
        System.out.println("\n=== UPDATE BACKGROUND ===");
        System.out.println("Current room index: " + game.getCurrentRoomIndex());

        try {
            int roomIndex = game.getCurrentRoomIndex();
            System.out.println("Room index: " + roomIndex);

            // 🆕 Check if room actually changed
            if (roomIndex == lastRoomIndex && backgroundImage != null) {
                System.out.println("📌 Same room, keeping current background");
                return;
            }

            lastRoomIndex = roomIndex; // Update tracker

            // Try room-specific image first
            if (roomIndex >= 0 && roomIndex < roomBackgrounds.length) {
                String imageName = roomBackgrounds[roomIndex];
                System.out.println("🔍 Looking for: images/rooms/" + imageName);

                java.net.URL imageURL = getClass().getClassLoader().getResource("images/rooms/" + imageName);

                if (imageURL != null) {
                    System.out.println("✅ Found room image!");
                    backgroundImage = new ImageIcon(imageURL).getImage();
                    System.out.println("🎨 Loaded Room " + (roomIndex + 1) + " background");

                    // Force immediate repaint
                    SwingUtilities.invokeLater(() -> repaint());
                    return;
                }
            }

            // Fallback: Default dungeon background
            System.out.println("🔍 Trying default: images/dungeon_bg.jpeg");
            java.net.URL defaultURL = getClass().getClassLoader().getResource("images/dungeon_bg.jpeg");

            if (defaultURL != null) {
                System.out.println("✅ Found default image");
                backgroundImage = new ImageIcon(defaultURL).getImage();
                System.out.println("🏰 Using default background");
                return;
            }

            // Final fallback: Gradient
            backgroundImage = null;
            System.out.println("🎨 Using gradient background");

        } catch (Exception e) {
            System.out.println("💥 ERROR loading background: " + e.getMessage());
            backgroundImage = null;
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (backgroundImage != null) {
            // Draw background image
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        } else {
            // Fallback gradient background
            Graphics2D g2d = (Graphics2D) g;
            Color color1 = new Color(30, 30, 60);
            Color color2 = new Color(10, 10, 30);

            GradientPaint gradient = new GradientPaint(
                    0, 0, color1,
                    getWidth(), getHeight(), color2
            );

            g2d.setPaint(gradient);
            g2d.fillRect(0, 0, getWidth(), getHeight());
        }
    }

    @Override
    public void doLayout() {
        super.doLayout();

        // Prevent components from getting too small
        if (descScroll != null) {
            // Ensure scroll pane has minimum size
            if (descScroll.getWidth() < 300 || descScroll.getHeight() < 150) {
                descScroll.setSize(
                        Math.max(300, descScroll.getWidth()),
                        Math.max(150, descScroll.getHeight())
                );
            }
        }
    }

    private void setupEventHandlers() {
        puzzleBtn.addActionListener(e -> mainApp.showPuzzle());
        gachaBtn.addActionListener(e -> mainApp.showGacha());
        inventoryBtn.addActionListener(e -> mainApp.showInventory());
        mapBtn.addActionListener(e -> mainApp.showMap());
        nextRoomBtn.addActionListener(e -> handleNextRoom());
        mainMenuBtn.addActionListener(e -> handleMainMenu());

        // ✅ ADD SAVE BUTTON HANDLER
        if (saveButton != null) {
            saveButton.addActionListener(e -> handleSaveGame());
        }
    }

    private void setupDungeonButtons() {
        // ✅ Style all buttons with dungeon theme
        if (puzzleBtn != null) {
            styleDungeonButton(puzzleBtn, "🧩 SOLVE PUZZLES 🧩");
        }
        if (gachaBtn != null) {
            styleDungeonButton(gachaBtn, "🎰 GACHA MACHINE 🎰");
        }
        if (inventoryBtn != null) {
            styleDungeonButton(inventoryBtn, "🎒 INVENTORY 🎒");
        }
        if (mapBtn != null) {
            styleDungeonButton(mapBtn, "🗺️ MAP 🗺️");
        }
        if (nextRoomBtn != null) {
            styleDungeonButton(nextRoomBtn, "🚪 NEXT ROOM 🚪");
        }
        if (mainMenuBtn != null) {
            styleDungeonButton(mainMenuBtn, "🏠 MAIN MENU 🏠");
        }
        if (saveButton != null) {
            styleDungeonButton(saveButton, "💾 SAVE GAME 💾");
        }

        // ✅ Style labels for better visibility
        if (roomHeaderLabel != null) {
            styleDungeonLabel(roomHeaderLabel);
        }
        if (coinsLabel != null) {
            styleInfoLabel(coinsLabel);
        }
        if (puzzleAvailableLabel != null) {
            styleInfoLabel(puzzleAvailableLabel);
        }
        if (roomProgressLabel != null) {
            styleInfoLabel(roomProgressLabel);
        }
    }

    private void styleDungeonButton(JButton button, String text) {
        button.setText(text);
        button.setForeground(new Color(255, 215, 0)); // Gold

        // Use monospaced font
        button.setFont(new Font("Monospaced", Font.BOLD, Math.max(14, button.getFont().getSize())));

        // Create stone-like look
        Color stoneColor = new Color(70, 60, 50); // Dark stone
        Color stoneBorder = new Color(150, 140, 130); // Light stone border

        button.setBackground(stoneColor);
        button.setOpaque(true);
        button.setBorderPainted(true);
        button.setFocusPainted(false);
        button.setContentAreaFilled(true);

        // Create 3D stone border effect
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createRaisedBevelBorder(),
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(stoneBorder, 2),
                        BorderFactory.createEmptyBorder(8, 15, 8, 15)
                )
        ));

        // Fire hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                if (button.isEnabled()) {
                    button.setBackground(new Color(120, 50, 30)); // Fire-like red/orange
                    button.setForeground(Color.YELLOW);

                    // Create fiery border
                    button.setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(Color.ORANGE, 3),
                            BorderFactory.createCompoundBorder(
                                    BorderFactory.createLineBorder(new Color(255, 200, 0), 1),
                                    BorderFactory.createEmptyBorder(8, 15, 8, 15)
                            )
                    ));

                    // Add subtle fire animation with timer
                    Timer fireTimer = new Timer(200, null);
                    final boolean[] isBright = {false};

                    fireTimer.addActionListener(evt -> {
                        if (button.isEnabled() && isBright[0]) {
                            button.setForeground(new Color(255, 255, 150)); // Bright yellow
                            button.setBorder(BorderFactory.createCompoundBorder(
                                    BorderFactory.createLineBorder(new Color(255, 150, 0), 3),
                                    BorderFactory.createCompoundBorder(
                                            BorderFactory.createLineBorder(new Color(255, 220, 0), 1),
                                            BorderFactory.createEmptyBorder(8, 15, 8, 15)
                                    )
                            ));
                        } else if (button.isEnabled()) {
                            button.setForeground(Color.ORANGE);
                            button.setBorder(BorderFactory.createCompoundBorder(
                                    BorderFactory.createLineBorder(Color.ORANGE, 3),
                                    BorderFactory.createCompoundBorder(
                                            BorderFactory.createLineBorder(new Color(255, 200, 0), 1),
                                            BorderFactory.createEmptyBorder(8, 15, 8, 15)
                                    )
                            ));
                        }
                        isBright[0] = !isBright[0];
                        button.repaint();
                    });

                    button.putClientProperty("fireTimer", fireTimer);
                    fireTimer.start();
                }
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                // Stop fire animation timer
                Object timer = button.getClientProperty("fireTimer");
                if (timer instanceof Timer) {
                    ((Timer) timer).stop();
                }

                // Back to stone appearance
                Color stoneColor = new Color(70, 60, 50);
                Color stoneBorder = new Color(150, 140, 130);

                if (button.isEnabled()) {
                    button.setBackground(stoneColor);
                    button.setForeground(new Color(255, 215, 0)); // Gold
                } else {
                    button.setBackground(new Color(100, 90, 80)); // Darker stone for disabled
                    button.setForeground(Color.GRAY);
                }

                // Restore stone border
                button.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createRaisedBevelBorder(),
                        BorderFactory.createCompoundBorder(
                                BorderFactory.createLineBorder(stoneBorder, 2),
                                BorderFactory.createEmptyBorder(8, 15, 8, 15)
                        )
                ));
            }
        });
    }

    private void styleDungeonLabel(JLabel label) {
        label.setForeground(new Color(255, 215, 0)); // Gold
        label.setFont(new Font("Monospaced", Font.BOLD, Math.max(16, label.getFont().getSize())));

        // Add subtle text shadow for better readability
        label.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
    }

    private void styleInfoLabel(JLabel label) {
        label.setForeground(Color.WHITE);
        label.setFont(new Font("Monospaced", Font.PLAIN, Math.max(12, label.getFont().getSize())));
    }

    // ✅ ADD SAVE GAME METHOD
    private void handleSaveGame() {
        try {
            String result = game.saveGameWithBackup();
            mainApp.showMessage(result);
        } catch (Exception e) {
            mainApp.showMessage("❌ Save failed: " + e.getMessage());
        }
    }

    private void handleNextRoom() {
        if (!game.isCurrentRoomComplete()) {
            mainApp.showMessage("Complete all puzzles in this room to proceed!");
            return;
        }

        int currentIndex = game.getCurrentRoomIndex();
        if (currentIndex < game.getRooms().size() - 1) {
            game.moveToNextRoom();

            if (game.checkWinCondition()) {
                Map<String, Object> winDetails = game.checkWinConditionDetailed();
                mainApp.showMessage(winDetails.get("message").toString());
            }

            refresh();
            mainApp.showMessage("🏰 Moving to: " + game.getCurrentRoom().getName() +
                    "\n" + game.getCurrentObjective());
        } else {
            mainApp.showMessage("🎉 You've reached the final room! Complete your mission!");
        }
    }

    private void handleMainMenu() {
        if (mainApp.showConfirmDialog("Return to main menu? Unsaved progress will be lost.")) {
            mainApp.showMainMenu();
        }
    }

    // 🆕 COMPLETELY UPDATED refresh() method with room change detection
    public void refresh() {
        System.out.println("\n🔄 === GAMEPANEL REFRESH ===");

        // 🆕 UPDATE BACKGROUND (checks if room changed internally)
        updateBackground();

        Room currentRoom = game.getCurrentRoom();
        if (currentRoom != null && roomDescriptionTextArea != null) {
            double completion = game.getCurrentRoomCompletion();

            System.out.println("📊 Current room: " + currentRoom.getName());
            System.out.println("📈 Room completion: " + completion + "%");

            // 🆕 METHOD 3: DIRECT STRING ASSIGNMENT (NO DUPLICATION)
            String roomText =
                    "📍 " + currentRoom.getName().toUpperCase() + " 📍\n" +
                            "════════════════════════════════════════\n\n" +
                            currentRoom.getRoomDescription() + "\n\n" +
                            "════════════════════════════════════════\n" +
                            "📋 CURRENT MISSION:\n" +
                            game.getCurrentObjective() + "\n" +
                            "════════════════════════════════════════\n\n" +
                            "🎮 Available Actions:";

            // 🆕 SET TEXT ONCE - REPLACES EVERYTHING
            roomDescriptionTextArea.setText(roomText);
            roomDescriptionTextArea.setCaretPosition(0);

            // Update room header
            if (roomHeaderLabel != null) {
                roomHeaderLabel.setText("Room " + currentRoom.getRoomNumber() + ": " +
                        currentRoom.getName() +
                        String.format(" (%.0f%% Complete)", completion));
            }

            // Update button states
            boolean canGoNext = game.isCurrentRoomComplete() &&
                    game.getCurrentRoomIndex() < game.getRooms().size() - 1;

            nextRoomBtn.setEnabled(canGoNext);
            puzzleBtn.setEnabled(!game.getAvailablePuzzles().isEmpty());
            gachaBtn.setEnabled(game.canAffordGachaPull());

            nextRoomBtn.setText(canGoNext ?
                    "🚪 NEXT ROOM →" : "Complete Puzzles First");
            puzzleBtn.setText("🧩 Solve Puzzles (" + game.getAvailablePuzzles().size() + " available)");
            gachaBtn.setText("🎰 Gacha Machine (" + game.getCurrentPlayer().getCoinBalance() + " coins)");

            // Update info labels
            if (coinsLabel != null) {
                coinsLabel.setText("🪙 Coins: " + game.getCurrentPlayer().getCoinBalance());
            }
            if (puzzleAvailableLabel != null) {
                puzzleAvailableLabel.setText("🧩 Puzzles: " + game.getAvailablePuzzles().size() + " available");
            }
            if (roomProgressLabel != null) {
                roomProgressLabel.setText("📊 Progress: " + String.format("%.0f%%", completion));
            }

            // Update save button
            if (saveButton != null) {
                saveButton.setText("💾 Save Game");
            }

            System.out.println("✅ UI updated successfully");
        } else {
            System.out.println("⚠️ Could not update UI - null components");
        }

        // Force repaint
        repaint();
        System.out.println("✅ REFRESH COMPLETE\n");
    }
}