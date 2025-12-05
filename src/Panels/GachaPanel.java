package Panels;

import main.MainApplication;
import model.GameManager;
import model.GachaItem;
import exceptions.NotEnoughCoinsException;
import exceptions.InventoryFullException;

import javax.swing.*;
import java.awt.*;

public class GachaPanel extends JPanel {
    private JPanel gachaPanel;
    private JLabel titleLabel;
    private JTextArea resultArea;
    private JButton pullButton;
    private JButton backButton;
    private JLabel machineLabel;
    private JLabel costLabel;
    private JLabel coinsLabel;
    private JLabel pityLabel;

    private MainApplication mainApp;
    private GameManager game;

    public GachaPanel(MainApplication mainApp, GameManager game) {
        this.mainApp = mainApp;
        this.game = game;

        setLayout(new BorderLayout());
        add(gachaPanel, BorderLayout.CENTER);

        initializeComponents();
        setupEventHandlers();
        styleComponents();
        refresh();
    }

    private void initializeComponents() {
        // Make everything transparent
        setOpaque(false);
        if (gachaPanel != null) {
            gachaPanel.setOpaque(false);
        }

        // Make labels transparent
        makeLabelTransparent(titleLabel);
        makeLabelTransparent(machineLabel);
        makeLabelTransparent(costLabel);
        makeLabelTransparent(coinsLabel);
        makeLabelTransparent(pityLabel);

        // Configure result area WITH ALPHA IN CODE
        if (resultArea != null) {
            resultArea.setEditable(false);
            resultArea.setLineWrap(true);
            resultArea.setWrapStyleWord(true);
            resultArea.setFont(new Font("Monospaced", Font.PLAIN, 12));

            // Set colors with alpha IN CODE
            resultArea.setBackground(new Color(30, 20, 40, 220)); // Alpha here!
            resultArea.setForeground(Color.WHITE);
            resultArea.setCaretColor(Color.YELLOW);

            // Create border IN CODE
            resultArea.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(150, 100, 200), 2),
                    BorderFactory.createEmptyBorder(15, 15, 15, 15)
            ));

            // Make the text area itself non-opaque to show background
            resultArea.setOpaque(false);
        }
    }

    private void makeLabelTransparent(JLabel label) {
        if (label != null) {
            label.setOpaque(false);
            label.setBackground(new Color(0, 0, 0, 0)); // Fully transparent
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        // Draw background WITH ALPHA
        Color color1 = new Color(40, 20, 50, 255);     // Solid dark purple
        Color color2 = new Color(60, 30, 70, 255);     // Solid lighter purple

        GradientPaint gradient = new GradientPaint(
                0, 0, color1,
                0, getHeight(), color2
        );
        g2d.setPaint(gradient);
        g2d.fillRect(0, 0, getWidth(), getHeight());

        // Add magical particles WITH ALPHA
        g2d.setColor(new Color(255, 255, 100, 30)); // Alpha here!
        int particleCount = 30;
        for (int i = 0; i < particleCount; i++) {
            int x = (int)(Math.random() * getWidth());
            int y = (int)(Math.random() * getHeight());
            int size = (int)(Math.random() * 5) + 2;
            g2d.fillOval(x, y, size, size);
        }

        // Draw arcane circles WITH ALPHA
        g2d.setColor(new Color(100, 255, 255, 20)); // Alpha here!
        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;
        int circleSize = Math.min(getWidth(), getHeight()) - 100;
        g2d.setStroke(new BasicStroke(3));
        g2d.drawOval(centerX - circleSize/2, centerY - circleSize/2, circleSize, circleSize);
    }

    private void styleComponents() {
        // Style title
        if (titleLabel != null) {
            titleLabel.setForeground(new Color(255, 215, 0));
            titleLabel.setFont(new Font("Monospaced", Font.BOLD, 28));
            titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
            titleLabel.setText("🎰 GACHA MACHINE 🎰");
        }

        // Style info labels with colors
        styleLabel(machineLabel, "🎰 Machine: Time Capsule", new Color(180, 220, 255));
        styleLabel(costLabel, "💰 Cost: 20 coins", new Color(255, 255, 200));
        styleLabel(coinsLabel, "🪙 Coins: 100", new Color(255, 255, 150));
        styleLabel(pityLabel, "📊 Pity: 0/10", new Color(150, 255, 255));

        // Style buttons
        styleGachaButton(pullButton, "🎲 PULL GACHA", new Color(180, 60, 60));
        styleGachaButton(backButton, "🔙 BACK TO GAME", new Color(80, 70, 60));
    }

    private void styleLabel(JLabel label, String text, Color color) {
        if (label == null) return;

        label.setText(text);
        label.setForeground(color);
        label.setFont(new Font("Monospaced", Font.BOLD, 14));
        label.setHorizontalAlignment(SwingConstants.CENTER);
    }

    private void styleGachaButton(JButton button, String text, Color baseColor) {
        if (button == null) return;

        button.setText(text);
        button.setFont(new Font("Monospaced", Font.BOLD, Math.max(14, button.getFont().getSize())));
        button.setFocusPainted(false);

        // Create semi-transparent background WITH ALPHA
        Color buttonBg = new Color(baseColor.getRed(), baseColor.getGreen(), baseColor.getBlue(), 220);

        button.setBackground(buttonBg);
        button.setForeground(Color.WHITE);
        button.setOpaque(true);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(255, 255, 200), 2),
                BorderFactory.createEmptyBorder(10, 20, 10, 20)
        ));

        // Hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                if (button.isEnabled()) {
                    // Brighter version WITH ALPHA
                    Color hoverColor = new Color(
                            Math.min(255, baseColor.getRed() + 40),
                            Math.min(255, baseColor.getGreen() + 40),
                            Math.min(255, baseColor.getBlue() + 40),
                            240
                    );
                    button.setBackground(hoverColor);
                    button.setForeground(Color.YELLOW);
                    button.setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(Color.ORANGE, 3),
                            BorderFactory.createEmptyBorder(10, 20, 10, 20)
                    ));
                }
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                button.setBackground(buttonBg);
                button.setForeground(Color.WHITE);
                button.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(255, 255, 200), 2),
                        BorderFactory.createEmptyBorder(10, 20, 10, 20)
                ));
            }
        });
    }

    private void setupEventHandlers() {
        if (pullButton != null) {
            pullButton.addActionListener(e -> pullGacha());
        }
        if (backButton != null) {
            backButton.addActionListener(e -> mainApp.showGame());
        }
    }

    private void pullGacha() {
        try {
            if (game.getCurrentGachaMachine() == null) {
                mainApp.showMessage("❌ No gacha machine here!");
                return;
            }

            GachaItem pulledItem = game.getCurrentGachaMachine().pull(game.getCurrentPlayer());

            if (pulledItem != null) {
                try {
                    game.getCurrentPlayer().addItem(pulledItem);

                    String rarityDisplay = "";
                    String itemIcon = pulledItem.getItemType() == model.ItemType.KEY ? "🔑 " : "🛠️ ";

                    switch (pulledItem.getRarity()) {
                        case EPIC: rarityDisplay = "✨✨✨ EPIC ✨✨✨"; break;
                        case RARE: rarityDisplay = "🌟🌟🌟 RARE 🌟🌟🌟"; break;
                        default: rarityDisplay = "⭐ COMMON ⭐";
                    }

                    if (resultArea != null) {
                        resultArea.setText(rarityDisplay + "\n\n" +
                                itemIcon + "YOU GOT: " + pulledItem.getName() + "\n" +
                                "📝 " + pulledItem.getDescription() + "\n\n" +
                                "✅ Added to inventory!\n\n" +
                                "🎒 " + game.getCurrentPlayer().getInventoryStatus());
                        resultArea.setCaretPosition(0);
                    }

                    mainApp.showMessage("🎊 " + pulledItem.getRarity() + " ITEM!\n" + pulledItem.getName());

                } catch (InventoryFullException e) {
                    if (resultArea != null) {
                        resultArea.setText("❌ INVENTORY FULL!\n\n" +
                                "You pulled: " + pulledItem.getName() + "\n" +
                                "But inventory is full!\n\n" +
                                "Go to Inventory panel first.");
                    }
                    mainApp.showMessage("❌ Inventory full!");
                }
            }

            refresh();

        } catch (NotEnoughCoinsException e) {
            mainApp.showMessage("❌ Need " + e.getRequired() + " coins!\nYou have: " + e.getAvailable());
        } catch (Exception e) {
            mainApp.showMessage("❌ Error: " + e.getMessage());
        }
    }

    public void refresh() {
        if (game.getCurrentGachaMachine() != null) {
            if (machineLabel != null) {
                machineLabel.setText("🎰 " + game.getCurrentGachaMachine().getMachineName());
            }

            if (costLabel != null) {
                costLabel.setText("💰 Cost: " + game.getCurrentGachaMachine().getPullCost() + " coins");
            }

            int pullsSinceEpic = game.getCurrentGachaMachine().getPullsSinceLastEpic();
            if (pityLabel != null) {
                String pityText = "📊 Pity: " + pullsSinceEpic + "/10";
                if (pullsSinceEpic >= 9) {
                    pityText = "🔥 " + pityText + " - NEXT IS EPIC!";
                    pityLabel.setForeground(new Color(255, 100, 100));
                } else if (pullsSinceEpic >= 7) {
                    pityText = "⚡ " + pityText + " - EPIC SOON!";
                    pityLabel.setForeground(new Color(255, 200, 100));
                } else {
                    pityLabel.setForeground(new Color(150, 255, 255));
                }
                pityLabel.setText(pityText);
            }
        }

        int coins = game.getCurrentPlayer().getCoinBalance();
        if (coinsLabel != null) {
            coinsLabel.setText("🪙 Coins: " + coins);
            if (coins < 20) {
                coinsLabel.setForeground(new Color(255, 100, 100));
            } else if (coins < 50) {
                coinsLabel.setForeground(new Color(255, 200, 100));
            } else {
                coinsLabel.setForeground(new Color(255, 255, 150));
            }
        }

        if (pullButton != null) {
            boolean canPull = coins >= 20;
            pullButton.setEnabled(canPull);

            if (canPull) {
                pullButton.setText("🎲 PULL GACHA (20 coins)");
                pullButton.setBackground(new Color(180, 60, 60, 220));
            } else {
                pullButton.setText("❌ NEED 20 COINS");
                pullButton.setBackground(new Color(100, 100, 100, 180));
            }
        }

        if (resultArea != null && resultArea.getText().isEmpty()) {
            resultArea.setText("🎰 GACHA MACHINE 🎰\n\n" +
                    "• 20 coins per pull\n" +
                    "• Epic every 10 pulls!\n" +
                    "• Get time machine parts\n\n" +
                    "Click PULL to try! 🍀");
            resultArea.setCaretPosition(0);
        }

        repaint();
    }
}