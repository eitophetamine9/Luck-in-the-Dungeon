package Panels;

import main.MainApplication;
import model.GameManager;
import model.Room;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;
import audio.AudioFiles;

public class GamePanel extends JPanel {
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
    private JButton saveButton;
    private JLabel coinsLabel;
    private JLabel puzzleAvailableLabel;
    private JLabel roomProgressLabel;
    private JButton quickSaveButton;
    private JButton quickLoadButton;
    private JButton saveInfoButton;

    private MainApplication mainApp;
    private GameManager game;
    private Image backgroundImage;
    private String[] roomBackgrounds = {
            "room1_lab.png",
            "room2_archives.png",
            "room3_alchemy.png",
            "room4_observatory.png",
            "room5_nexus.png"
    };

    // 🆕 Add: Store current music
    private String currentRoomMusic = "";

    // 🆕 Add: Room change tracker
    private int lastRoomIndex = -1;

    public GamePanel(MainApplication mainApp, GameManager game) {
        this.mainApp = mainApp;
        this.game = game;
        this.lastRoomIndex = -1;

        setLayout(new BorderLayout());
        add(gamePanel, BorderLayout.CENTER);

        testImageLoading();
        updateBackground();

        setOpaque(false);
        if (gamePanel != null) {
            gamePanel.setOpaque(false);
        }

        if (descScroll != null && roomDescriptionTextArea == null) {
            roomDescriptionTextArea = (JTextArea) descScroll.getViewport().getView();
        }

        if (descScroll != null) {
            descScroll.setOpaque(false);
            descScroll.getViewport().setOpaque(false);
            descScroll.setBackground(new Color(20, 20, 30, 180));
            descScroll.getViewport().setBackground(new Color(30, 30, 60, 200));
            descScroll.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        }

        if (roomDescriptionTextArea != null) {
            roomDescriptionTextArea.setEditable(false);
            roomDescriptionTextArea.setLineWrap(true);
            roomDescriptionTextArea.setWrapStyleWord(true);
            roomDescriptionTextArea.setFont(new Font("Monospaced", Font.BOLD, 14));
            roomDescriptionTextArea.setForeground(Color.cyan);
            roomDescriptionTextArea.setBackground(new Color(30, 30, 60, 180));
            roomDescriptionTextArea.setCaretColor(Color.cyan);
            roomDescriptionTextArea.setText("");
            roomDescriptionTextArea.setDisabledTextColor(new Color(255, 255, 200));
        }

        if (quickSaveButton != null) {
            styleDungeonButton(quickSaveButton, "💾 QUICK SAVE");
        }
        if (quickLoadButton != null) {
            styleDungeonButton(quickLoadButton, "📂 QUICK LOAD");
        }
        if (saveInfoButton != null) {
            styleDungeonButton(saveInfoButton, "📊 SAVE INFO");
        }

        setupEventHandlers();
        setupDungeonButtons();
        refresh();
    }

    // 🆕 Add: Update room music
    private void updateRoomMusic() {
        int roomIndex = game.getCurrentRoomIndex();
        String newMusic = AudioFiles.getRoomMusic(roomIndex);

        // Only change music if room has changed or music is different
        if (roomIndex != lastRoomIndex || !newMusic.equals(currentRoomMusic)) {
            System.out.println("🎵 Changing music from " + currentRoomMusic + " to " + newMusic);
            mainApp.getAudioManager().playMusic(newMusic);
            currentRoomMusic = newMusic;
            lastRoomIndex = roomIndex;
        }
    }

    private void testImageLoading() {
        System.out.println("\n🔍 TESTING IMAGE LOADING...");
        for (int i = 0; i < roomBackgrounds.length; i++) {
            String imagePath = "images/rooms/" + roomBackgrounds[i];
            java.net.URL url = getClass().getClassLoader().getResource(imagePath);
            System.out.println((i+1) + ". " + imagePath + " → " +
                    (url != null ? "✅ FOUND" : "❌ NOT FOUND"));
        }
    }

    private void updateBackground() {
        System.out.println("\n=== UPDATE BACKGROUND ===");
        System.out.println("Current room index: " + game.getCurrentRoomIndex());

        try {
            int roomIndex = game.getCurrentRoomIndex();
            System.out.println("Room index: " + roomIndex);

            if (roomIndex == lastRoomIndex && backgroundImage != null) {
                System.out.println("📌 Same room, keeping current background");
                return;
            }

            lastRoomIndex = roomIndex;

            if (roomIndex >= 0 && roomIndex < roomBackgrounds.length) {
                String imageName = roomBackgrounds[roomIndex];
                System.out.println("🔍 Looking for: images/rooms/" + imageName);

                java.net.URL imageURL = getClass().getClassLoader().getResource("images/rooms/" + imageName);

                if (imageURL != null) {
                    System.out.println("✅ Found room image!");
                    backgroundImage = new ImageIcon(imageURL).getImage();
                    System.out.println("🎨 Loaded Room " + (roomIndex + 1) + " background");
                    SwingUtilities.invokeLater(() -> repaint());
                    return;
                }
            }

            System.out.println("🔍 Trying default: images/dungeon_bg.jpeg");
            java.net.URL defaultURL = getClass().getClassLoader().getResource("images/dungeon_bg.jpeg");

            if (defaultURL != null) {
                System.out.println("✅ Found default image");
                backgroundImage = new ImageIcon(defaultURL).getImage();
                System.out.println("🏰 Using default background");
                return;
            }

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
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        } else {
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

        if (descScroll != null) {
            if (descScroll.getWidth() < 300 || descScroll.getHeight() < 150) {
                descScroll.setSize(
                        Math.max(300, descScroll.getWidth()),
                        Math.max(150, descScroll.getHeight())
                );
            }
        }
    }

    private void setupEventHandlers() {
        puzzleBtn.addActionListener(e -> {
            mainApp.getAudioManager().playSound(AudioFiles.CLICK);
            mainApp.showPuzzle();
        });

        gachaBtn.addActionListener(e -> {
            mainApp.getAudioManager().playSound(AudioFiles.CLICK);
            mainApp.showGacha();
        });

        inventoryBtn.addActionListener(e -> {
            mainApp.getAudioManager().playSound(AudioFiles.CLICK);
            mainApp.showInventory();
        });

        mapBtn.addActionListener(e -> {
            mainApp.getAudioManager().playSound(AudioFiles.CLICK);
            mainApp.showMap();
        });

        nextRoomBtn.addActionListener(e -> {
            mainApp.getAudioManager().playSound(AudioFiles.CLICK);
            handleNextRoom();
        });

        mainMenuBtn.addActionListener(e -> {
            mainApp.getAudioManager().playSound(AudioFiles.CLICK);
            handleMainMenu();
        });

        if (saveButton != null) {
            saveButton.addActionListener(e -> {
                mainApp.getAudioManager().playSound(AudioFiles.CLICK);
                handleSaveGame();
            });
        }

        if (quickSaveButton != null) {
            quickSaveButton.addActionListener(e -> {
                mainApp.getAudioManager().playSound(AudioFiles.CLICK);
                handleQuickSave();
            });
        }

        if (quickLoadButton != null) {
            quickLoadButton.addActionListener(e -> {
                mainApp.getAudioManager().playSound(AudioFiles.CLICK);
                handleQuickLoad();
            });
        }

        if (saveInfoButton != null) {
            saveInfoButton.addActionListener(e -> {
                mainApp.getAudioManager().playSound(AudioFiles.CLICK);
                handleSaveInfo();
            });
        }
    }

    private void handleQuickSave() {
        if (quickSaveButton != null) {
            quickSaveButton.setText("⏳ SAVING...");
            quickSaveButton.setEnabled(false);
        }

        Timer saveTimer = new Timer(100, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String result = game.saveGameWithSummary();

                JTextArea resultArea = new JTextArea(20, 50);
                resultArea.setEditable(false);
                resultArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
                resultArea.setText(result + "\n\n" + game.getSaveSummary());

                JScrollPane scrollPane = new JScrollPane(resultArea);
                scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

                JOptionPane.showMessageDialog(
                        mainApp,
                        scrollPane,
                        "Save Result",
                        JOptionPane.INFORMATION_MESSAGE
                );

                if (quickSaveButton != null) {
                    quickSaveButton.setText("💾 QUICK SAVE");
                    quickSaveButton.setEnabled(true);
                }

                ((Timer) e.getSource()).stop();
            }
        });
        saveTimer.setRepeats(false);
        saveTimer.start();
    }

    private void handleQuickLoad() {
        if (!game.saveExists()) {
            mainApp.showMessage("❌ No save file found!");
            return;
        }

        String saveInfo = game.getSaveInfo();
        int choice = JOptionPane.showConfirmDialog(
                mainApp,
                "Load saved game?\n\n" + saveInfo + "\nCurrent progress will be lost.",
                "Load Game",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (choice == JOptionPane.YES_OPTION) {
            String result = game.loadGameWithFeedback();

            if (result.startsWith("✅")) {
                refresh();

                JTextArea messageArea = new JTextArea(10, 40);
                messageArea.setEditable(false);
                messageArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
                messageArea.setText(result + "\n\n" + game.getSaveSummary());

                JScrollPane scrollPane = new JScrollPane(messageArea);
                JOptionPane.showMessageDialog(
                        mainApp,
                        scrollPane,
                        "Game Loaded",
                        JOptionPane.INFORMATION_MESSAGE
                );
            } else {
                mainApp.showMessage(result);
            }
        }
    }

    private void handleSaveInfo() {
        String saveInfo = game.getSaveInfo();
        String saveSummary = game.getSaveSummary();

        JTabbedPane tabbedPane = new JTabbedPane();

        JTextArea fileInfoArea = new JTextArea(15, 50);
        fileInfoArea.setEditable(false);
        fileInfoArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        fileInfoArea.setText(saveInfo);
        tabbedPane.addTab("📁 File Info", new JScrollPane(fileInfoArea));

        JTextArea summaryArea = new JTextArea(20, 50);
        summaryArea.setEditable(false);
        summaryArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        summaryArea.setText(saveSummary);
        tabbedPane.addTab("📊 Save Summary", new JScrollPane(summaryArea));

        Map<String, Object> stats = game.getGameStats();
        StringBuilder statsText = new StringBuilder();
        stats.forEach((key, value) -> {
            String displayKey = key.replaceAll("([A-Z])", " $1").toLowerCase();
            displayKey = displayKey.substring(0, 1).toUpperCase() + displayKey.substring(1);
            statsText.append(String.format("%-25s: %s\n", displayKey, value));
        });

        JTextArea statsArea = new JTextArea(15, 50);
        statsArea.setEditable(false);
        statsArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        statsArea.setText(statsText.toString());
        tabbedPane.addTab("📈 Game Stats", new JScrollPane(statsArea));

        JOptionPane.showMessageDialog(
                mainApp,
                tabbedPane,
                "Save Information",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    private void setupDungeonButtons() {
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
        button.setForeground(new Color(255, 215, 0));
        button.setFont(new Font("Monospaced", Font.BOLD, Math.max(14, button.getFont().getSize())));

        Color stoneColor = new Color(70, 60, 50);
        Color stoneBorder = new Color(150, 140, 130);

        button.setBackground(stoneColor);
        button.setOpaque(true);
        button.setBorderPainted(true);
        button.setFocusPainted(false);
        button.setContentAreaFilled(true);

        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createRaisedBevelBorder(),
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(stoneBorder, 2),
                        BorderFactory.createEmptyBorder(8, 15, 8, 15)
                )
        ));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                if (button.isEnabled()) {
                    button.setBackground(new Color(120, 50, 30));
                    button.setForeground(Color.YELLOW);

                    button.setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(Color.ORANGE, 3),
                            BorderFactory.createCompoundBorder(
                                    BorderFactory.createLineBorder(new Color(255, 200, 0), 1),
                                    BorderFactory.createEmptyBorder(8, 15, 8, 15)
                            )
                    ));

                    Timer fireTimer = new Timer(200, null);
                    final boolean[] isBright = {false};

                    fireTimer.addActionListener(evt -> {
                        if (button.isEnabled() && isBright[0]) {
                            button.setForeground(new Color(255, 255, 150));
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
                Object timer = button.getClientProperty("fireTimer");
                if (timer instanceof Timer) {
                    ((Timer) timer).stop();
                }

                Color stoneColor = new Color(70, 60, 50);
                Color stoneBorder = new Color(150, 140, 130);

                if (button.isEnabled()) {
                    button.setBackground(stoneColor);
                    button.setForeground(new Color(255, 215, 0));
                } else {
                    button.setBackground(new Color(100, 90, 80));
                    button.setForeground(Color.GRAY);
                }

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
        label.setForeground(new Color(255, 215, 0));
        label.setFont(new Font("Monospaced", Font.BOLD, Math.max(16, label.getFont().getSize())));
        label.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
    }

    private void styleInfoLabel(JLabel label) {
        label.setForeground(Color.WHITE);
        label.setFont(new Font("Monospaced", Font.PLAIN, Math.max(12, label.getFont().getSize())));
    }

    private void handleSaveGame() {
        if (saveButton != null) {
            saveButton.setText("⏳ Saving...");
            saveButton.setEnabled(false);
        }

        Timer saveTimer = new Timer(300, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    if (!game.validateGameState()) {
                        JOptionPane.showMessageDialog(
                                mainApp,
                                "⚠️ Cannot save: Game state is invalid.",
                                "Save Error",
                                JOptionPane.WARNING_MESSAGE
                        );
                        return;
                    }

                    String result = game.saveGameWithBackup();

                    JTextArea messageArea = new JTextArea(result);
                    messageArea.setEditable(false);
                    messageArea.setFont(new Font("Monospaced", Font.PLAIN, 12));

                    JOptionPane.showMessageDialog(
                            mainApp,
                            new JScrollPane(messageArea),
                            "Save Game",
                            JOptionPane.INFORMATION_MESSAGE
                    );

                } catch (Exception ex) {
                    String errorMsg = "❌ Save failed:\n" + ex.getMessage() +
                            "\n\nMake sure all model classes implement Serializable.";
                    JOptionPane.showMessageDialog(
                            mainApp,
                            errorMsg,
                            "Save Error",
                            JOptionPane.ERROR_MESSAGE
                    );
                    ex.printStackTrace();
                } finally {
                    if (saveButton != null) {
                        saveButton.setText("💾 Save Game");
                        saveButton.setEnabled(true);
                    }
                    ((Timer) e.getSource()).stop();
                }
            }
        });
        saveTimer.setRepeats(false);
        saveTimer.start();
    }

    // 🆕 FIXED: Next room with music update
    private void handleNextRoom() {
        if (!game.isCurrentRoomComplete()) {
            mainApp.showMessage("Complete all puzzles in this room to proceed!");
            return;
        }

        int currentIndex = game.getCurrentRoomIndex();
        if (currentIndex < game.getRooms().size() - 1) {
            String result = game.moveToNextRoom();

            // 🎵 CRITICAL: Update music after room change
            updateRoomMusic();

            if (game.checkWinCondition()) {
                Map<String, Object> winDetails = game.checkWinConditionDetailed();
                mainApp.showMessage(winDetails.get("message").toString());
            } else {
                mainApp.showMessage("🏰 Moving to: " + game.getCurrentRoom().getName() +
                        "\n" + game.getCurrentObjective());
            }

            refresh();
        } else {
            mainApp.showMessage("🎉 You've reached the final room! Complete your mission!");
        }
    }

    private void handleMainMenu() {
        if (mainApp.showConfirmDialog("Return to main menu? Unsaved progress will be lost.")) {
            mainApp.showMainMenu();
        }
    }

    public void refresh() {
        System.out.println("\n🔄 === GAMEPANEL REFRESH ===");
        System.out.println("Current room index: " + game.getCurrentRoomIndex());

        // 🎵 Update music when panel refreshes
        updateRoomMusic();
        updateBackground();

        Room currentRoom = game.getCurrentRoom();
        if (currentRoom != null && roomDescriptionTextArea != null) {
            double completion = game.getCurrentRoomCompletion();

            System.out.println("📊 Current room: " + currentRoom.getName());
            System.out.println("📈 Room completion: " + completion + "%");

            if (game.getCurrentRoomIndex() == 4) {
                handleRoom5Display(currentRoom);
            } else {
                String roomText =
                        "📍 " + currentRoom.getName().toUpperCase() + " 📍\n" +
                                "════════════════════════════════════════\n\n" +
                                currentRoom.getRoomDescription() + "\n\n" +
                                "════════════════════════════════════════\n" +
                                "📋 CURRENT MISSION:\n" +
                                game.getCurrentObjective() + "\n" +
                                "════════════════════════════════════════\n\n" +
                                "🎮 Available Actions:";

                roomDescriptionTextArea.setText(roomText);
                roomDescriptionTextArea.setCaretPosition(0);

                puzzleBtn.setEnabled(!game.getAvailablePuzzles().isEmpty());
                gachaBtn.setEnabled(game.canAffordGachaPull());

                boolean canGoNext = game.isCurrentRoomComplete() &&
                        game.getCurrentRoomIndex() < game.getRooms().size() - 1;
                nextRoomBtn.setEnabled(canGoNext);
                nextRoomBtn.setText(canGoNext ? "🚪 NEXT ROOM →" : "Complete Puzzles First");

                puzzleBtn.setText("🧩 Solve Puzzles (" + game.getAvailablePuzzles().size() + " available)");

                if (puzzleAvailableLabel != null) {
                    puzzleAvailableLabel.setText("🧩 Puzzles: " + game.getAvailablePuzzles().size() + " available");
                }
            }

            if (roomHeaderLabel != null) {
                roomHeaderLabel.setText("Room " + currentRoom.getRoomNumber() + ": " +
                        currentRoom.getName() +
                        String.format(" (%.0f%% Complete)", completion));
            }

            if (coinsLabel != null) {
                coinsLabel.setText("🪙 Coins: " + game.getCurrentPlayer().getCoinBalance());
            }

            if (roomProgressLabel != null) {
                roomProgressLabel.setText("📊 Progress: " + String.format("%.0f%%", completion));
            }

            if (saveButton != null) {
                saveButton.setText("💾 Save Game");
            }

            System.out.println("✅ UI updated successfully");
        } else {
            System.out.println("⚠️ Could not update UI - null components");
        }

        repaint();
        System.out.println("✅ REFRESH COMPLETE\n");
    }

    private void handleRoom5Display(Room currentRoom) {
        if (roomDescriptionTextArea == null) return;

        String roomText =
                "⚡ TEMPORAL NEXUS ⚡\n" +
                        "════════════════════════════════════════\n\n" +
                        "You stand before a shimmering portal of pure temporal energy.\n" +
                        "The air crackles with possibilities of untold adventures.\n\n" +
                        "✨ This gateway leads to:\n" +
                        "   • Ancient temporal civilizations\n" +
                        "   • Parallel reality exploration\n" +
                        "   • Future dystopia intervention\n" +
                        "   • Time paradox resolution\n\n" +
                        "════════════════════════════════════════\n" +
                        "📋 EXPANSION PREVIEW:\n" +
                        "The adventure continues in future updates!\n\n" +
                        "🔧 Development Status: COMING SOON\n" +
                        "📅 Estimated: Next Semester\n\n" +
                        "🎮 Available Actions:";

        roomDescriptionTextArea.setText(roomText);
        roomDescriptionTextArea.setCaretPosition(0);

        puzzleBtn.setEnabled(false);
        puzzleBtn.setText("🧩 Expansion Content (Coming Soon)");

        gachaBtn.setEnabled(game.canAffordGachaPull());
        gachaBtn.setText("🎰 Special Nexus Gacha");

        nextRoomBtn.setEnabled(false);
        nextRoomBtn.setText("🌌 Gateway Active");

        if (puzzleAvailableLabel != null) {
            puzzleAvailableLabel.setText("🧩 Puzzles: Expansion Content");
        }
    }
}