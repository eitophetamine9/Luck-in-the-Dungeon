package Panels;

import main.MainApplication;
import model.GameManager;
import model.Room;

import javax.swing.*;
import java.awt.*;

public class GamePanel extends JPanel {
    private JPanel mainPanel;
    private JButton puzzleBtn;
    private JButton mainMenuBtn;
    private JButton gachaBtn;
    private JButton inventoryBtn;
    private JButton nextRoomBtn;
    private JButton mapBtn;
    private JLabel roomHeaderLabel;
    private JTextArea roomDescriptionTextArea;

    private MainApplication mainApp;
    private GameManager game;

    public GamePanel(MainApplication mainApp, GameManager game) {
        this.mainApp = mainApp;
        this.game = game;
        initializePanel();
        setupEventHandlers();
        refresh();
    }

    private void initializePanel() {
        setLayout(new BorderLayout());
        add(mainPanel, BorderLayout.CENTER);

        // Configure text area for better readability
        roomDescriptionTextArea.setEditable(false);
        roomDescriptionTextArea.setLineWrap(true);
        roomDescriptionTextArea.setWrapStyleWord(true);
    }

    private void setupEventHandlers() {
        puzzleBtn.addActionListener(e -> mainApp.showPuzzle());
        gachaBtn.addActionListener(e -> mainApp.showGacha());
        inventoryBtn.addActionListener(e -> mainApp.showInventory());
        mapBtn.addActionListener(e -> handleMapNavigation());
        nextRoomBtn.addActionListener(e -> handleNextRoom());
        mainMenuBtn.addActionListener(e -> handleMainMenu());
    }

    private void handleMapNavigation() {
        mainApp.showMessage("Map feature - navigate between unlocked rooms");
    }

    private void handleNextRoom() {
        if (!game.isCurrentRoomComplete()) {
            mainApp.showMessage("Complete all puzzles to proceed to the next room.");
            return;
        }

        game.moveToNextRoom();
        refresh();
        mainApp.showMessage("Moving to next room: " + game.getCurrentRoom().getName());
    }

    private void handleMainMenu() {
        if (mainApp.showConfirmDialog("Return to main menu? Progress may be lost.")) {
            mainApp.showMainMenu();
        }
    }

    public void refresh() {
        Room currentRoom = game.getCurrentRoom();
        if (currentRoom != null) {
            roomHeaderLabel.setText("Room " + currentRoom.getRoomNumber() + ": " + currentRoom.getName());
            roomDescriptionTextArea.setText(currentRoom.getRoomDescription());

            // Update button states based on game conditions
            nextRoomBtn.setEnabled(game.isCurrentRoomComplete());
            puzzleBtn.setEnabled(!game.getAvailablePuzzles().isEmpty());
            gachaBtn.setEnabled(game.getCurrentPlayer().getCoinBalance() >= 20);
        }
    }
}