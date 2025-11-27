// GachaPanel.java
package view;

import model.*;
import exceptions.*;
import javax.swing.*;
import java.awt.*;

public class GachaPanel extends JPanel {
    private MainFrame parent;
    private GameManager game;

    private JLabel machineInfoLabel;
    private JLabel ratesLabel;
    private JLabel pityLabel;
    private JButton pullButton;
    private JButton backButton;
    private JTextArea resultArea;

    public GachaPanel(MainFrame parent, GameManager game) {
        this.parent = parent;
        this.game = game;
        initializeGUI();
    }

    private void initializeGUI() {
        setLayout(new BorderLayout());
        setBackground(new Color(40, 40, 80));

        // Title
        JLabel titleLabel = new JLabel("GACHA MACHINE", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 32));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        // Info Panel
        JPanel infoPanel = createInfoPanel();

        // Result Area
        resultArea = new JTextArea(5, 30);
        resultArea.setEditable(false);
        resultArea.setBackground(new Color(30, 30, 60));
        resultArea.setForeground(Color.WHITE);
        resultArea.setFont(new Font("Arial", Font.PLAIN, 14));
        resultArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(new Color(50, 50, 100));

        pullButton = new JButton("PULL! (Cost: ?)");
        pullButton.setFont(new Font("Arial", Font.BOLD, 20));
        pullButton.setBackground(new Color(200, 50, 50));
        pullButton.setForeground(Color.WHITE);
        pullButton.setFocusPainted(false);

        backButton = new JButton("Back to Game");
        backButton.setFont(new Font("Arial", Font.BOLD, 14));
        backButton.setBackground(new Color(80, 80, 140));
        backButton.setForeground(Color.WHITE);

        buttonPanel.add(pullButton);
        buttonPanel.add(backButton);

        add(titleLabel, BorderLayout.NORTH);
        add(infoPanel, BorderLayout.CENTER);
        add(new JScrollPane(resultArea), BorderLayout.SOUTH);
        add(buttonPanel, BorderLayout.EAST);

        setupEventHandlers();
    }

    private JPanel createInfoPanel() {
        JPanel infoPanel = new JPanel(new GridLayout(3, 1, 10, 10));
        infoPanel.setBackground(new Color(50, 50, 100));
        infoPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        machineInfoLabel = new JLabel("", JLabel.CENTER);
        machineInfoLabel.setForeground(Color.WHITE);
        machineInfoLabel.setFont(new Font("Arial", Font.BOLD, 16));

        ratesLabel = new JLabel("", JLabel.CENTER);
        ratesLabel.setForeground(Color.YELLOW);
        ratesLabel.setFont(new Font("Arial", Font.PLAIN, 14));

        pityLabel = new JLabel("", JLabel.CENTER);
        pityLabel.setForeground(Color.CYAN);
        pityLabel.setFont(new Font("Arial", Font.PLAIN, 14));

        infoPanel.add(machineInfoLabel);
        infoPanel.add(ratesLabel);
        infoPanel.add(pityLabel);

        return infoPanel;
    }

    private void setupEventHandlers() {
        pullButton.addActionListener(e -> performPull());
        backButton.addActionListener(e -> parent.showGame());
    }

    private void performPull() {
        GachaMachine gacha = game.getCurrentGachaMachine();
        Player player = game.getCurrentPlayer();

        if (gacha == null) {
            parent.showMessage("No gacha machine available in this room!");
            return;
        }

        try {
            GachaItem result = gacha.pull(player);

            if (result != null) {
                // Add to inventory
                player.addItem(result);

                // Display result
                String rarityColor = getRarityColorCode(result.getRarity());
                resultArea.setText(String.format(
                        "🎊 PULL RESULT 🎊\n" +
                                "You got: %s%s%s!\n" +
                                "Type: %s\n" +
                                "Description: %s\n\n" +
                                "Added to your inventory!",
                        rarityColor, result.getName(), Color.WHITE,
                        result.getItemType(),
                        result.getDescription()
                ));

                // Update display
                refresh();
                parent.showMessage("Pull successful! Got: " + result.getName());
            } else {
                resultArea.setText("❌ Pull failed! No item received.");
            }
        } catch (NotEnoughCoinsException ex) {
            parent.showMessage("Not enough coins! Need " + ex.getRequired() + " but only have " + ex.getAvailable());
        } catch (InventoryFullException ex) {
            parent.showMessage("Inventory is full! Cannot add new items.");
        } catch (Exception ex) {
            parent.showMessage("Pull failed: " + ex.getMessage());
        }
    }

    private String getRarityColorCode(Rarity rarity) {
        switch (rarity) {
            case COMMON: return "[GRAY]";
            case RARE: return "[BLUE]";
            case EPIC: return "[PURPLE]";
            default: return "[WHITE]";
        }
    }

    public void refresh() {
        GachaMachine gacha = game.getCurrentGachaMachine();
        Player player = game.getCurrentPlayer();

        if (gacha != null) {
            machineInfoLabel.setText(
                    gacha.getMachineName() + " | Cost: " + gacha.getPullCost() + " coins"
            );

            pullButton.setText("PULL! (" + gacha.getPullCost() + " coins)");
            pullButton.setEnabled(gacha.canPull(player));

            // Display rates
            var rates = gacha.getRateTable();
            ratesLabel.setText(String.format(
                    "Rates: Common %.0f%% | Rare %.0f%% | Epic %.0f%%",
                    rates.get(Rarity.COMMON) * 100,
                    rates.get(Rarity.RARE) * 100,
                    rates.get(Rarity.EPIC) * 100
            ));

            // Pity counter
            pityLabel.setText("Pity Counter: " + gacha.getPullsSinceLastEpic() + "/10 until guaranteed EPIC");
        }

        // Update player coin display
        if (pullButton != null) {
            pullButton.setToolTipText("Your coins: " + player.getCoinBalance());
        }
    }
}