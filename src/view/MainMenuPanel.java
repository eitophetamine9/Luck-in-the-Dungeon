// MainMenuPanel.java
package view;

import model.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainMenuPanel extends JPanel {
    private MainFrame parent;
    private GameManager game;

    private JLabel titleLabel;
    private JButton newGameButton;
    private JButton loadGameButton;
    private JButton exitButton;

    public MainMenuPanel(MainFrame parent, GameManager game) {
        this.parent = parent;
        this.game = game;
        initializeGUI();
    }

    private void initializeGUI() {
        setLayout(new BorderLayout());
        setBackground(new Color(30, 30, 60));

        // Title
        titleLabel = new JLabel("LUCK IN THE DUNGEON", JLabel.CENTER);
        titleLabel.setFont(new Font("Serif", Font.BOLD, 48));
        titleLabel.setForeground(Color.YELLOW);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(50, 0, 50, 0));

        // Button Panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(3, 1, 10, 20));
        buttonPanel.setBackground(new Color(30, 30, 60));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 200, 100, 200));

        newGameButton = createMenuButton("New Game");
        loadGameButton = createMenuButton("Load Game");
        exitButton = createMenuButton("Exit");

        buttonPanel.add(newGameButton);
        buttonPanel.add(loadGameButton);
        buttonPanel.add(exitButton);

        add(titleLabel, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.CENTER);

        setupEventHandlers();
    }

    private JButton createMenuButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 24));
        button.setBackground(new Color(70, 70, 120));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createRaisedBevelBorder());
        return button;
    }

    private void setupEventHandlers() {
        newGameButton.addActionListener(e -> {
            if (!game.saveExists() || parent.showConfirmDialog("Start new game? Current progress will be lost.")) {
                String playerName = showCharacterNameDialog();
                if(playerName != null && !playerName.trim().isEmpty()){
                    game.startNewGame(playerName.trim());
                    parent.showGame();
                    parent.showMessage("Welcome, " + playerName + "! Your adventure begins...");
                } else {
                    parent.showMessage("Game start cancelled. Please enter a character name.");
                }
            }
        });

        loadGameButton.addActionListener(e -> {
            if (game.loadGame()) {
                parent.showGame();
                parent.showMessage("Game loaded successfully! Welcome back, " + game.getCurrentPlayer().getName() + "!");
            } else {
                parent.showMessage("No save file found or load failed!");
            }
        });

        exitButton.addActionListener(e -> {
            if (parent.showConfirmDialog("Are you sure you want to exit?")) {
                System.exit(0);
            }
        });
    }

    private String showCharacterNameDialog() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(new Color(30, 30, 60));

        // Title
        JLabel titleLabel = new JLabel("Create Your Character", JLabel.CENTER);
        titleLabel.setFont(new Font("Serif", Font.BOLD, 18));
        titleLabel.setForeground(Color.YELLOW);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        // Input field with styling
        JLabel nameLabel = new JLabel("Enter Character Name:");
        nameLabel.setForeground(Color.WHITE);
        nameLabel.setFont(new Font("Arial", Font.BOLD, 14));

        JTextField nameField = new JTextField(20);
        nameField.setFont(new Font("Arial", Font.PLAIN, 14));
        nameField.setBackground(new Color(60, 60, 110));
        nameField.setForeground(Color.WHITE);
        nameField.setCaretColor(Color.WHITE);
        nameField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));

        // Suggestions label
        JLabel suggestionLabel = new JLabel("Examples: Adventurer, Explorer, Hero, Mystic (max 20 chars)");
        suggestionLabel.setForeground(Color.LIGHT_GRAY);
        suggestionLabel.setFont(new Font("Arial", Font.ITALIC, 12));

        panel.add(titleLabel, BorderLayout.NORTH);

        JPanel inputPanel = new JPanel(new GridLayout(3, 1, 5, 5));
        inputPanel.setBackground(new Color(30, 30, 60));
        inputPanel.add(nameLabel);
        inputPanel.add(nameField);
        inputPanel.add(suggestionLabel);

        panel.add(inputPanel, BorderLayout.CENTER);

        int result = JOptionPane.showConfirmDialog(
                parent,
                panel,
                "Character Creation",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result == JOptionPane.OK_OPTION) {
            String name = nameField.getText().trim();
            if (name.length() > 20) {
                parent.showMessage("Name too long! Maximum 20 characters.");
                return showCharacterNameDialog(); // Recursive call to try again
            }
            return name;
        }
        return null;
    }


    public void refresh() {
        loadGameButton.setEnabled(game.saveExists());
    }
}