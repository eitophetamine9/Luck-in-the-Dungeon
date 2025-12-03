package Panels;

import main.MainApplication;
import model.GameManager;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class MainMenuPanel extends JPanel {
    // ✅ MUST MATCH FORM COMPONENT NAMES
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

        // ✅ Initialize form components (KEEP YOUR EXISTING LAYOUT)
        setLayout(new BorderLayout());
        add(mainPanel, BorderLayout.CENTER);

        // ✅ Load background image
        loadBackgroundImage();

        // ✅ Make main panel transparent to show background
        setOpaque(false);

        // ✅ Make the form's panel transparent
        if (mainPanel != null) {
            mainPanel.setOpaque(false);
        }

        setupEventHandlers();

        // ✅ Apply dungeon-themed button styling
        setupSimpleDungeonButtons();

        refresh();
    }

    private void loadBackgroundImage() {
        try {
            System.out.println("🔍 Loading background image...");

            // NOTE: Changed extension to .jpeg!
            java.net.URL imageURL = getClass().getClassLoader().getResource("images/dungeon_bg.jpeg");

            if (imageURL != null) {
                System.out.println("✅ Found image at URL: " + imageURL);
                backgroundImage = new ImageIcon(imageURL).getImage();
                System.out.println("✅ Background loaded from resources!");
                return;
            } else {
                System.out.println("❌ Image not found via getResource()");

                // Debug: List all resources
                System.out.println("🔍 Listing available resources:");
                try {
                    java.net.URL resourceURL = getClass().getClassLoader().getResource("images/");
                    if (resourceURL != null) {
                        java.io.File resourceDir = new java.io.File(resourceURL.toURI());
                        String[] files = resourceDir.list();
                        if (files != null) {
                            for (String file : files) {
                                System.out.println("   - " + file);
                            }
                        }
                    }
                } catch (Exception e) {
                    System.out.println("❌ Could not list resources: " + e.getMessage());
                }
            }

            // Fallback to gradient
            System.out.println("🎨 Using gradient fallback");
            backgroundImage = null;

        } catch (Exception e) {
            System.out.println("❌ Error loading background: " + e.getMessage());
            backgroundImage = null;
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // ✅ Draw background image or gradient
        if (backgroundImage != null) {
            // Draw background image
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        } else {
            // Fallback gradient background
            Graphics2D g2d = (Graphics2D) g;
            Color color1 = new Color(30, 30, 60);     // Dark blue
            Color color2 = new Color(10, 10, 30);     // Darker blue

            GradientPaint gradient = new GradientPaint(
                    0, 0, color1,
                    getWidth(), getHeight(), color2
            );

            g2d.setPaint(gradient);
            g2d.fillRect(0, 0, getWidth(), getHeight());
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
        try {
            if (game.loadGame()) {
                mainApp.showMessage("Game loaded successfully! Welcome back, " +
                        game.getCurrentPlayer().getName() + "!");
                mainApp.showGame();
            } else {
                mainApp.showMessage("No save file found or load failed!");
            }
        } catch (Exception e) {
            mainApp.showMessage("Error loading game: " + e.getMessage());
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

    private void setupSimpleDungeonButtons() {
        // Add fire icons to existing buttons
        if (newGameButton != null) {
            styleButtonWithFire(newGameButton, "🔥 NEW GAME 🔥");
        }
        if (loadGameButton != null) {
            // ⚠️ TEMPORARY FIX: Always show "LOAD GAME" until save system works
            styleButtonWithFire(loadGameButton, "📜 LOAD GAME 📜");
        }
        if (exitButton != null) {
            styleButtonWithFire(exitButton, "🚪 EXIT 🚪");
        }
    }

    private void styleButtonWithFire(JButton button, String text) {
        button.setText(text);
        button.setForeground(new Color(255, 215, 0)); // Gold

        // Make sure font is preserved (your Goudy Stout)
        Font originalFont = button.getFont();
        button.setFont(originalFont.deriveFont(Font.BOLD, Math.max(16f, originalFont.getSize())));

        // Create rock-like look
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
                        BorderFactory.createEmptyBorder(12, 25, 12, 25)
                )
        ));

        // Fire hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                button.setBackground(new Color(120, 50, 30)); // Fire-like red/orange
                button.setForeground(Color.YELLOW);

                // Create fiery border
                button.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(Color.ORANGE, 3),
                        BorderFactory.createCompoundBorder(
                                BorderFactory.createLineBorder(new Color(255, 200, 0), 1),
                                BorderFactory.createEmptyBorder(12, 25, 12, 25)
                        )
                ));

                // Add subtle fire animation with timer
                Timer fireTimer = new Timer(200, null);
                final boolean[] isBright = {false};

                fireTimer.addActionListener(evt -> {
                    if (isBright[0]) {
                        button.setForeground(new Color(255, 255, 150)); // Bright yellow
                        button.setBorder(BorderFactory.createCompoundBorder(
                                BorderFactory.createLineBorder(new Color(255, 150, 0), 3),
                                BorderFactory.createCompoundBorder(
                                        BorderFactory.createLineBorder(new Color(255, 220, 0), 1),
                                        BorderFactory.createEmptyBorder(12, 25, 12, 25)
                                )
                        ));
                    } else {
                        button.setForeground(Color.ORANGE);
                        button.setBorder(BorderFactory.createCompoundBorder(
                                BorderFactory.createLineBorder(Color.ORANGE, 3),
                                BorderFactory.createCompoundBorder(
                                        BorderFactory.createLineBorder(new Color(255, 200, 0), 1),
                                        BorderFactory.createEmptyBorder(12, 25, 12, 25)
                                )
                        ));
                    }
                    isBright[0] = !isBright[0];
                    button.repaint();
                });

                button.putClientProperty("fireTimer", fireTimer);
                fireTimer.start();
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                // Stop fire animation timer
                Object timer = button.getClientProperty("fireTimer");
                if (timer instanceof Timer) {
                    ((Timer) timer).stop();
                }

                // Back to stone appearance
                button.setBackground(stoneColor);
                button.setForeground(new Color(255, 215, 0)); // Gold

                // Restore stone border
                button.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createRaisedBevelBorder(),
                        BorderFactory.createCompoundBorder(
                                BorderFactory.createLineBorder(stoneBorder, 2),
                                BorderFactory.createEmptyBorder(12, 25, 12, 25)
                        )
                ));
            }
        });
    }

    public void refresh() {
        // ⚠️ TEMPORARY: Disable load button until save system works
        // loadGameButton.setEnabled(game.saveExists());
        loadGameButton.setEnabled(false); // Disable for now

        // Update tooltip to explain why it's disabled
        loadGameButton.setToolTipText("Save system under development - Coming soon!");

        // Optional: Gray out the button to show it's disabled
        if (!loadGameButton.isEnabled()) {
            loadGameButton.setForeground(Color.GRAY);
            loadGameButton.setBackground(new Color(90, 80, 70)); // Darker stone
        }
    }
}