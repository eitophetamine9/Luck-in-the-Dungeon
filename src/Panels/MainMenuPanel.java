package Panels;

import main.MainApplication;
import model.GameManager;

import javax.swing.*;
import javax.swing.plaf.basic.BasicButtonUI;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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

        // ✅ Initialize form components
        setLayout(new BorderLayout());
        add(mainPanel, BorderLayout.CENTER);

        // ✅ Load background image
        loadBackgroundImage();

        // ✅ Make panels transparent to show background
        setOpaque(false);
        if (mainPanel != null) {
            mainPanel.setOpaque(false);
        }

        setupEventHandlers();

        // ✅ Apply the ADVANCED fire styling
        setupDungeonButtons();

        refresh();
    }

    private void loadBackgroundImage() {
        try {
            java.net.URL imageURL = getClass().getClassLoader().getResource("images/dungeon_bg.jpeg");
            if (imageURL != null) {
                backgroundImage = new ImageIcon(imageURL).getImage();
            } else {
                backgroundImage = null; // Will fallback to gradient in paintComponent
            }
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
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        } else {
            // Fallback gradient background
            Graphics2D g2d = (Graphics2D) g;
            GradientPaint gradient = new GradientPaint(
                    0, 0, new Color(30, 30, 60),
                    getWidth(), getHeight(), new Color(10, 10, 30)
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

    private void setupDungeonButtons() {
        // Apply the advanced UI to all buttons
        applyFireUI(newGameButton, "🔥 NEW GAME 🔥");
        applyFireUI(loadGameButton, "📜 LOAD GAME 📜");
        applyFireUI(exitButton, "🚪 EXIT 🚪");
    }

    private void applyFireUI(JButton button, String text) {
        button.setText(text);

        // Preserve original font but make it bold/larger if needed
        Font original = button.getFont();
        button.setFont(original.deriveFont(Font.BOLD, 16f));

        // Set the custom UI renderer
        button.setUI(new FireButtonUI());
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Add padding for the bevel effect
        button.setBorder(BorderFactory.createEmptyBorder(12, 25, 12, 25));
    }

    public void refresh() {
        // Check save status
        boolean hasSave = game.saveExists();

        // 1. UPDATE NEW GAME BUTTON
        newGameButton.setBackground(new Color(70, 60, 50)); // Normal Stone
        newGameButton.setForeground(new Color(255, 215, 0)); // Gold Text

        // 2. UPDATE LOAD GAME BUTTON (Handle Disabled State)
        // ⚠️ logic currently disabled as requested, but set up for future use
        loadGameButton.setEnabled(hasSave);

        if (loadGameButton.isEnabled()) {
            loadGameButton.setBackground(new Color(70, 60, 50)); // Normal Stone
            loadGameButton.setForeground(new Color(255, 215, 0)); // Gold
            loadGameButton.setToolTipText("Load your previous adventure");
        } else {
            // DISABLED LOOK (Gray Stone)
            loadGameButton.setBackground(new Color(60, 55, 55)); // Dark Grey Stone
            loadGameButton.setForeground(Color.GRAY); // Gray Text
            loadGameButton.setToolTipText("No save file found");
        }

        // 3. UPDATE EXIT BUTTON
        exitButton.setBackground(new Color(70, 60, 50));
        exitButton.setForeground(new Color(255, 215, 0));
    }

    private void handleNewGame() {
        if (game.saveExists()) {
            boolean confirm = mainApp.showConfirmDialog("Starting a new game will overwrite your existing save. Continue?");
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
        if (!game.saveExists()) {
            mainApp.showMessage("❌ No save file found!\nStart a new game to begin your adventure.");
            return;
        }

        int choice = JOptionPane.showConfirmDialog(
                mainApp,
                "Load saved game?\n\nCurrent progress will be lost.",
                "Load Game",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (choice == JOptionPane.YES_OPTION) {
            loadGameButton.setText("⏳ Loading...");
            loadGameButton.setEnabled(false);

            Timer loadTimer = new Timer(300, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        String result = game.loadGameWithFeedback();

                        if (result.startsWith("✅")) {
                            // DEBUG: Show what was loaded
                            game.debugCurrentState();

                            // Refresh the main menu
                            refresh();

                            // Show success message
                            JOptionPane.showMessageDialog(
                                    mainApp,
                                    result + "\n\nClick 'Continue' to play.",
                                    "Game Loaded",
                                    JOptionPane.INFORMATION_MESSAGE
                            );

                            // Switch to game screen
                            mainApp.showGame();
                        } else {
                            mainApp.showMessage(result);
                        }
                    } catch (Exception ex) {
                        mainApp.showMessage("❌ Load error: " + ex.getMessage());
                    } finally {
                        loadGameButton.setText("📜 LOAD GAME 📜");
                        loadGameButton.setEnabled(true);
                        ((Timer) e.getSource()).stop();
                    }
                }
            });
            loadTimer.setRepeats(false);
            loadTimer.start();
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
        titleLabel.setForeground(Color.BLACK); // Changed to black for better visibility in dialogs

        JLabel nameLabel = new JLabel("Enter Character Name:");
        JTextField nameField = new JTextField(20);

        panel.add(titleLabel, BorderLayout.NORTH);
        JPanel inputPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        inputPanel.add(nameLabel);
        inputPanel.add(nameField);
        panel.add(inputPanel, BorderLayout.CENTER);

        int result = JOptionPane.showConfirmDialog(mainApp, panel, "Character Creation", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String name = nameField.getText().trim();
            if (name.length() > 20) return showCharacterNameDialog(); // Recurse if too long
            return name.isEmpty() ? "Adventurer" : name;
        }
        return null;
    }

    // ==========================================
    //  ADVANCED FIRE BUTTON UI CLASS (Shared)
    // ==========================================
    private static class FireButtonUI extends BasicButtonUI {
        private Timer animationTimer;
        private float phase = 0;
        private final List<Ember> embers = new ArrayList<>();

        private static class Ember {
            double x, y, speedY, size;
            float alpha;
            Ember(int w, int h) { reset(w, h, true); }
            void reset(int w, int h, boolean startRandomY) {
                x = Math.random() * w;
                y = startRandomY ? Math.random() * h : h;
                speedY = 1.0 + Math.random() * 2.0;
                size = 2.0 + Math.random() * 3.0;
                alpha = 1.0f;
            }
        }

        @Override
        public void installUI(JComponent c) {
            super.installUI(c);
            AbstractButton button = (AbstractButton) c;
            button.setOpaque(false);
            button.setContentAreaFilled(false);
            button.setFocusPainted(false);
            button.setBorderPainted(false);

            for(int i = 0; i < 20; i++) embers.add(new Ember(100, 30));

            animationTimer = new Timer(16, e -> {
                if (button.getModel().isRollover() && button.isEnabled()) {
                    phase += 0.1f;
                    for (Ember ember : embers) {
                        ember.y -= ember.speedY;
                        ember.alpha -= 0.03f;
                        ember.x += Math.sin(phase + ember.y * 0.1) * 0.5;
                        if (ember.y < 0 || ember.alpha <= 0) ember.reset(button.getWidth(), button.getHeight(), false);
                    }
                    button.repaint();
                }
            });
        }

        @Override
        public void uninstallUI(JComponent c) {
            if (animationTimer != null) animationTimer.stop();
            super.uninstallUI(c);
        }

        @Override
        public void paint(Graphics g, JComponent c) {
            AbstractButton b = (AbstractButton) c;
            int w = b.getWidth();
            int h = b.getHeight();
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            if (b.getModel().isRollover() && b.isEnabled()) {
                if (!animationTimer.isRunning()) animationTimer.start();
                paintFireEffect(g2d, w, h);
            } else {
                if (animationTimer.isRunning()) animationTimer.stop();
                paintStoneEffect(g2d, b, w, h);
            }

            super.paint(g2d, c);
            g2d.dispose();
        }

        private void paintFireEffect(Graphics2D g2d, int w, int h) {
            // Background Fire Gradient
            GradientPaint fireBase = new GradientPaint(0, h, new Color(140, 20, 10), 0, 0, new Color(255, 100, 0));
            g2d.setPaint(fireBase);
            g2d.fillRoundRect(0, 0, w, h, 10, 10);

            // Core Heat
            RadialGradientPaint heat = new RadialGradientPaint(
                    new java.awt.geom.Point2D.Float(w/2.0f, h), w/1.5f,
                    new float[]{0.0f, 1.0f},
                    new Color[]{new Color(255, 220, 100, 150), new Color(255, 100, 0, 0)}
            );
            g2d.setPaint(heat);
            g2d.fillRoundRect(0, 0, w, h, 10, 10);

            // Embers
            g2d.setColor(new Color(255, 255, 200));
            for (Ember e : embers) {
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, Math.max(0, e.alpha)));
                g2d.fill(new Ellipse2D.Double(e.x, e.y, e.size, e.size));
            }
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));

            // Fire Border
            g2d.setColor(new Color(255, 180, 50));
            g2d.setStroke(new BasicStroke(2));
            g2d.drawRoundRect(1, 1, w-2, h-2, 10, 10);
        }

        private void paintStoneEffect(Graphics2D g2d, AbstractButton b, int w, int h) {
            g2d.setColor(b.getBackground());
            g2d.fillRoundRect(2, 2, w-4, h-4, 8, 8);

            // Texture
            g2d.setColor(new Color(0,0,0,30));
            for(int i=0; i<w; i+=4) if(i%3==0) g2d.fillRect(i, (i*7)%h, 2, 2);

            // Bevels
            g2d.setStroke(new BasicStroke(2));
            g2d.setColor(new Color(255, 255, 255, 50)); // Highlight
            g2d.drawRoundRect(2, 2, w-5, h-5, 8, 8);
            g2d.setColor(new Color(0, 0, 0, 100)); // Shadow
            g2d.drawRoundRect(1, 1, w-2, h-2, 8, 8);
            g2d.setColor(new Color(150, 140, 130)); // Border
            g2d.drawRoundRect(0, 0, w-1, h-1, 8, 8);
        }
    }
}