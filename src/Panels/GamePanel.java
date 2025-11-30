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

    private MainApplication mainApp;
    private GameManager game;

    public GamePanel(MainApplication mainApp, GameManager game) {
        this.mainApp = mainApp;
        this.game = game;

        setLayout(new BorderLayout());
        add(gamePanel, BorderLayout.CENTER);

        if (descScroll != null && roomDescriptionTextArea == null) {
            roomDescriptionTextArea = (JTextArea) descScroll.getViewport().getView();
        }

        if (roomDescriptionTextArea != null) {
            roomDescriptionTextArea.setEditable(false);
            roomDescriptionTextArea.setLineWrap(true);
            roomDescriptionTextArea.setWrapStyleWord(true);
        }

        setupEventHandlers();
        refresh();
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

    // ✅ ADD SAVE GAME METHOD
    private void handleSaveGame() {
        try {
            String result = game.saveGameWithBackup();
            mainApp.showMessage(result);
        } catch (Exception e) {
            mainApp.showMessage("❌ Save failed: " + e.getMessage());
        }
    }

    // ... rest of your existing methods
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

    public void refresh() {
        Room currentRoom = game.getCurrentRoom();
        if (currentRoom != null && roomDescriptionTextArea != null) {
            double completion = game.getCurrentRoomCompletion();
            roomHeaderLabel.setText("Room " + currentRoom.getRoomNumber() + ": " +
                    currentRoom.getName() +
                    String.format(" (%.0f%% Complete)", completion));

            roomDescriptionTextArea.setText(currentRoom.getRoomDescription() +
                    "\n\n📋 CURRENT MISSION:\n" +
                    game.getCurrentObjective());

            nextRoomBtn.setEnabled(game.isCurrentRoomComplete() &&
                    game.getCurrentRoomIndex() < game.getRooms().size() - 1);
            puzzleBtn.setEnabled(!game.getAvailablePuzzles().isEmpty());
            gachaBtn.setEnabled(game.canAffordGachaPull());

            nextRoomBtn.setText(game.isCurrentRoomComplete() ?
                    "Next Room →" : "Complete Puzzles First");
            puzzleBtn.setText("Solve Puzzles (" + game.getAvailablePuzzles().size() + " available)");
            gachaBtn.setText("Gacha Machine (" + game.getCurrentPlayer().getCoinBalance() + " coins)");

            // ✅ Update save button text
            if (saveButton != null) {
                saveButton.setText("💾 Save Game");
            }
        }
    }
}