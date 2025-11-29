package Panels;

import model.GameManager;
import javax.swing.*;
import java.awt.*;

public class MainMenuPanel extends JPanel {
    // Auto-bound from .form file
    private JPanel mainPanel;
    private JButton newGameButton;
    private JButton loadGameButton;
    private JButton exitButton;
    private JLabel titleLabel;

    private MainApplication mainApp;
    private GameManager game;
    private Image backgroundImage;

    public MainMenuPanel(MainApplication mainApp, GameManager game) {
        this.mainApp = mainApp;
        this.game = game;

        loadBackgroundImage();
        setupEventHandlers();
        refresh();
    }

    private void loadBackgroundImage() {
        try {
            backgroundImage = new ImageIcon("src/images/dungeon_bg.jpg").getImage();
        } catch (Exception e) {
            System.out.println("Background image not found - using color");
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        } else {
            // Fallback color
            g.setColor(new Color(30, 30, 60));
            g.fillRect(0, 0, getWidth(), getHeight());
        }
    }

    private void setupEventHandlers() {
        newGameButton.addActionListener(e -> handleNewGame());
        loadGameButton.addActionListener(e -> handleLoadGame());
        exitButton.addActionListener(e -> handleExit());
    }

    private void handleNewGame() {
        if (game.saveExists()) {
            boolean confirm = mainApp.showConfirmDialog(
                    "Starting a new game will overwrite your existing save. Continue?"
            );
            if (!confirm) return;
        }

        String playerName = showCharacterNameDialog();
        if (playerName != null && !playerName.trim().isEmpty()) {
            game.startNewGame(playerName);
            mainApp.showMessage("Welcome, " + playerName + "! Your adventure begins...");
            mainApp.showGame();
        }
    }

    private void handleLoadGame() {
        if (game.loadGame()) {
            mainApp.showMessage("Game loaded successfully! Welcome back, " +
                    game.getCurrentPlayer().getName() + "!");
            mainApp.showGame();
        } else {
            mainApp.showMessage("No save file found or load failed!");
        }
    }

    private void handleExit() {
        if (mainApp.showConfirmDialog("Are you sure you want to exit?")) {
            System.exit(0);
        }
    }

    private String showCharacterNameDialog() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));

        JLabel titleLabel = new JLabel("Create Your Character", JLabel.CENTER);
        titleLabel.setFont(new Font("Serif", Font.BOLD, 18));
        titleLabel.setForeground(Color.YELLOW);

        JLabel nameLabel = new JLabel("Enter Character Name:");
        nameLabel.setForeground(Color.WHITE);

        JTextField nameField = new JTextField(20);
        nameField.setBackground(new Color(60, 60, 110));
        nameField.setForeground(Color.WHITE);
        nameField.setCaretColor(Color.WHITE);

        panel.setBackground(new Color(30, 30, 60));
        panel.add(titleLabel, BorderLayout.NORTH);

        JPanel inputPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        inputPanel.setBackground(new Color(30, 30, 60));
        inputPanel.add(nameLabel);
        inputPanel.add(nameField);

        panel.add(inputPanel, BorderLayout.CENTER);

        int result = JOptionPane.showConfirmDialog(
                mainApp,
                panel,
                "Character Creation",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result == JOptionPane.OK_OPTION) {
            String name = nameField.getText().trim();
            if (name.length() > 20) {
                mainApp.showMessage("Name too long! Maximum 20 characters.");
                return showCharacterNameDialog();
            }
            return name.isEmpty() ? "Adventurer" : name;
        }
        return null;
    }

    public void refresh() {
        loadGameButton.setEnabled(game.saveExists());
    }
}