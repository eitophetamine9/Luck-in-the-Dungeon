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
                game.startNewGame();
                parent.showGame();
            }
        });

        loadGameButton.addActionListener(e -> {
            if (game.loadGame()) {
                parent.showGame();
                parent.showMessage("Game loaded successfully!");
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

    public void refresh() {
        loadGameButton.setEnabled(game.saveExists());
    }
}