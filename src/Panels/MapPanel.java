package Panels;

import main.MainApplication;
import model.GameManager;
import exceptions.RoomLockedException;

import javax.swing.*;
import java.awt.*;

public class MapPanel extends JPanel {
    private JPanel mapPanel;
    private JButton room1Button;
    private JButton room2Button;
    private JButton room3Button;
    private JButton room4Button;
    private JButton backButton;
    private JLabel titleLabel;

    private MainApplication mainApp;
    private GameManager game;

    public MapPanel(MainApplication mainApp, GameManager game) {
        this.mainApp = mainApp;
        this.game = game;
        initializePanel();
        setupEventHandlers();
        refresh();
    }

    private void initializePanel() {
        setLayout(new BorderLayout());
        add(mapPanel, BorderLayout.CENTER);
    }

    private void setupEventHandlers() {
        room1Button.addActionListener(e -> navigateToRoom(0));
        room2Button.addActionListener(e -> navigateToRoom(1));
        room3Button.addActionListener(e -> navigateToRoom(2));
        room4Button.addActionListener(e -> navigateToRoom(3));
        backButton.addActionListener(e -> mainApp.showGame());
    }

    private void navigateToRoom(int roomIndex) {
        try {
            game.moveToRoom(roomIndex);
            mainApp.showMessage("Traveling to: " + game.getCurrentRoom().getName());
            mainApp.showGame();
        } catch (Exception e) {
            mainApp.showMessage("Cannot travel to that room: " + e.getMessage());
        }
    }

    public void refresh() {
        // Update room button states and appearance
        updateRoomButton(room1Button, 0);
        updateRoomButton(room2Button, 1);
        updateRoomButton(room3Button, 2);
        updateRoomButton(room4Button, 3);
    }

    private void updateRoomButton(JButton button, int roomIndex) {
        String status = game.getRoomStatus(roomIndex);
        String roomName = game.getRooms().get(roomIndex).getName();

        // Update button text with status
        button.setText("<html><center>ROOM " + (roomIndex + 1) + "<br>" +
                roomName + "<br>[" + status + "]</center></html>");

        // Update button appearance based on status
        switch (status) {
            case "CURRENT":
                button.setBackground(new Color(144, 238, 144)); // Light green
                button.setEnabled(true);
                break;
            case "COMPLETED":
                button.setBackground(new Color(173, 216, 230)); // Light blue
                button.setEnabled(true);
                break;
            case "UNLOCKED":
                button.setBackground(new Color(255, 255, 150)); // Light yellow
                button.setEnabled(true);
                break;
            case "LOCKED":
                button.setBackground(Color.LIGHT_GRAY);
                button.setEnabled(false);
                break;
        }

        // Set consistent button properties
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setPreferredSize(new Dimension(180, 70));
        button.setFocusPainted(false);
    }
}