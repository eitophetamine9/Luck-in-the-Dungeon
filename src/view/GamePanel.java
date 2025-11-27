// GamePanel.java
package view;

import model.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class GamePanel extends JPanel {
    private MainFrame parent;
    private GameManager game;

    private JLabel roomTitleLabel;
    private JTextArea roomDescriptionArea;
    private JPanel puzzlesPanel;
    private JButton inventoryButton;
    private JButton gachaButton;
    private JButton saveButton;
    private JButton menuButton;
    private JLabel playerStatsLabel;

    public GamePanel(MainFrame parent, GameManager game) {
        this.parent = parent;
        this.game = game;
        initializeGUI();
    }

    private void initializeGUI() {
        setLayout(new BorderLayout());
        setBackground(new Color(40, 40, 80));

        // Top Panel - Room Info
        JPanel topPanel = createTopPanel();

        // Center Panel - Puzzles
        JPanel centerPanel = createCenterPanel();

        // Bottom Panel - Actions
        JPanel bottomPanel = createBottomPanel();

        add(topPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private JPanel createTopPanel() {
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(50, 50, 100));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        roomTitleLabel = new JLabel("", JLabel.CENTER);
        roomTitleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        roomTitleLabel.setForeground(Color.WHITE);

        roomDescriptionArea = new JTextArea();
        roomDescriptionArea.setEditable(false);
        roomDescriptionArea.setLineWrap(true);
        roomDescriptionArea.setWrapStyleWord(true);
        roomDescriptionArea.setBackground(new Color(60, 60, 110));
        roomDescriptionArea.setForeground(Color.WHITE);
        roomDescriptionArea.setFont(new Font("Arial", Font.PLAIN, 14));
        roomDescriptionArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        playerStatsLabel = new JLabel();
        playerStatsLabel.setForeground(Color.YELLOW);
        playerStatsLabel.setFont(new Font("Arial", Font.BOLD, 14));

        topPanel.add(roomTitleLabel, BorderLayout.NORTH);
        topPanel.add(new JScrollPane(roomDescriptionArea), BorderLayout.CENTER);
        topPanel.add(playerStatsLabel, BorderLayout.SOUTH);

        return topPanel;
    }

    private JPanel createCenterPanel() {
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(new Color(40, 40, 80));
        centerPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.GRAY), "Available Puzzles"));

        puzzlesPanel = new JPanel();
        puzzlesPanel.setLayout(new BoxLayout(puzzlesPanel, BoxLayout.Y_AXIS));
        puzzlesPanel.setBackground(new Color(40, 40, 80));

        JScrollPane scrollPane = new JScrollPane(puzzlesPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        centerPanel.add(scrollPane, BorderLayout.CENTER);
        return centerPanel;
    }

    private JPanel createBottomPanel() {
        JPanel bottomPanel = new JPanel(new FlowLayout());
        bottomPanel.setBackground(new Color(50, 50, 100));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        inventoryButton = createActionButton("Inventory");
        gachaButton = createActionButton("Gacha Machine");
        saveButton = createActionButton("Save Game");
        menuButton = createActionButton("Main Menu");

        bottomPanel.add(inventoryButton);
        bottomPanel.add(gachaButton);
        bottomPanel.add(saveButton);
        bottomPanel.add(menuButton);

        setupEventHandlers();

        return bottomPanel;
    }

    private JButton createActionButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(new Color(80, 80, 140));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        return button;
    }

    private void setupEventHandlers() {
        inventoryButton.addActionListener(e -> parent.showInventory());
        gachaButton.addActionListener(e -> parent.showGacha());

        saveButton.addActionListener(e -> {
            game.saveGame();
            parent.showMessage("Game saved successfully!");
        });

        menuButton.addActionListener(e -> {
            if (parent.showConfirmDialog("Return to main menu?")) {
                parent.showMainMenu();
            }
        });
    }

    public void refresh() {
        Room currentRoom = game.getCurrentRoom();
        Player player = game.getCurrentPlayer();

        // Update room info
        roomTitleLabel.setText(currentRoom.getName() + " - Room " + (game.getCurrentRoomIndex() + 1));
        roomDescriptionArea.setText(currentRoom.getRoomDescription());

        // Update player stats
        playerStatsLabel.setText(String.format(
                "Player: %s | Coins: %d | Puzzles Solved: %d | Rooms Completed: %d",
                player.getName(), player.getCoinBalance(),
                player.getPuzzlesSolved(), player.getRoomsCompleted()
        ));

        // Update puzzles panel
        puzzlesPanel.removeAll();
        List<Puzzle> availablePuzzles = game.getAvailablePuzzles();

        if (availablePuzzles.isEmpty()) {
            JLabel noPuzzlesLabel = new JLabel("All puzzles completed! Room cleared!");
            noPuzzlesLabel.setForeground(Color.GREEN);
            noPuzzlesLabel.setFont(new Font("Arial", Font.BOLD, 16));
            noPuzzlesLabel.setAlignmentX(CENTER_ALIGNMENT);
            puzzlesPanel.add(noPuzzlesLabel);

            // Check if we can move to next room
            if (game.isCurrentRoomComplete() && game.getCurrentRoomIndex() < game.getRooms().size() - 1) {
                JButton nextRoomButton = new JButton("Move to Next Room");
                nextRoomButton.setAlignmentX(CENTER_ALIGNMENT);
                nextRoomButton.addActionListener(e -> {
                    game.moveToNextRoom();
                    refresh();
                    parent.showMessage("Welcome to the next room!");
                });
                puzzlesPanel.add(Box.createRigidArea(new Dimension(0, 10)));
                puzzlesPanel.add(nextRoomButton);
            } else if (game.checkWinCondition()) {
                JLabel winLabel = new JLabel("🎉 CONGRATULATIONS! YOU ESCAPED THE DUNGEON! 🎉");
                winLabel.setForeground(Color.YELLOW);
                winLabel.setFont(new Font("Arial", Font.BOLD, 18));
                winLabel.setAlignmentX(CENTER_ALIGNMENT);
                puzzlesPanel.add(winLabel);
            }
        } else {
            for (Puzzle puzzle : availablePuzzles) {
                JPanel puzzleCard = createPuzzleCard(puzzle);
                puzzlesPanel.add(puzzleCard);
                puzzlesPanel.add(Box.createRigidArea(new Dimension(0, 10)));
            }
        }

        puzzlesPanel.revalidate();
        puzzlesPanel.repaint();

        // Check for room completion to advance
        if (game.isCurrentRoomComplete() && game.getCurrentRoomIndex() < game.getRooms().size() - 1) {
            parent.showMessage("Room completed! You can now move to the next room.");
        }
    }

    private JPanel createPuzzleCard(Puzzle puzzle) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(new Color(60, 60, 110));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        // Puzzle info
        JLabel descriptionLabel = new JLabel("<html>" + puzzle.getDescription() + "</html>");
        descriptionLabel.setForeground(Color.WHITE);
        descriptionLabel.setFont(new Font("Arial", Font.PLAIN, 14));

        JLabel rewardLabel = new JLabel("Reward: " + puzzle.getCoinReward() + " coins | Difficulty: " + puzzle.getDifficultyLevel());
        rewardLabel.setForeground(Color.YELLOW);
        rewardLabel.setFont(new Font("Arial", Font.BOLD, 12));

        JPanel infoPanel = new JPanel(new BorderLayout());
        infoPanel.setBackground(new Color(60, 60, 110));
        infoPanel.add(descriptionLabel, BorderLayout.CENTER);
        infoPanel.add(rewardLabel, BorderLayout.SOUTH);

        // Attempt button
        JButton attemptButton = new JButton("Attempt Puzzle");
        attemptButton.setBackground(new Color(80, 120, 80));
        attemptButton.setForeground(Color.WHITE);
        attemptButton.addActionListener(e -> parent.showPuzzle(puzzle));

        card.add(infoPanel, BorderLayout.CENTER);
        card.add(attemptButton, BorderLayout.EAST);

        return card;
    }
}