package Panels;

import main.MainApplication;
import model.GameManager;
import model.GachaItem;
import exceptions.NotEnoughCoinsException;
import exceptions.InventoryFullException;

import javax.swing.*;
import java.awt.*;

public class GachaPanel extends JPanel {
    // ✅ MUST MATCH FORM COMPONENT NAMES
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

        // ✅ Initialize form components
        setLayout(new BorderLayout());
        add(gachaPanel, BorderLayout.CENTER);

        initializePanel();
        setupEventHandlers();
        refresh();
    }

    private void initializePanel() {
        resultArea.setEditable(false);
        resultArea.setLineWrap(true);
        resultArea.setWrapStyleWord(true);
        resultArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
    }

    private void setupEventHandlers() {
        pullButton.addActionListener(e -> pullGacha());
        backButton.addActionListener(e -> mainApp.showGame());
    }

    private void pullGacha() {
        try {
            GachaItem pulledItem = game.getCurrentGachaMachine().pull(game.getCurrentPlayer());

            if (pulledItem != null) {
                try {
                    game.getCurrentPlayer().addItem(pulledItem);

                    String rarityDisplay = "";
                    String itemIcon = pulledItem.getItemType() == model.ItemType.KEY ? "🔑 " : "🛠️ ";

                    switch (pulledItem.getRarity()) {
                        case EPIC: rarityDisplay = "🟡 LEGENDARY EPIC 🟡"; break;
                        case RARE: rarityDisplay = "🔵 RARE ITEM 🔵"; break;
                        default: rarityDisplay = "⚪ COMMON ITEM ⚪";
                    }

                    resultArea.setText(rarityDisplay + "\n\n" +
                            itemIcon + " YOU PULLED: " + pulledItem.getName() + "\n" +
                            "📝 " + pulledItem.getDescription() + "\n\n" +
                            "✅ Successfully added to inventory!\n\n" +
                            "🎒 " + game.getCurrentPlayer().getInventoryStatus());

                    mainApp.showMessage("🎊 " + pulledItem.getRarity() + " ITEM! \n" +
                            pulledItem.getName() + "\n" +
                            "Added to your inventory!");

                } catch (InventoryFullException e) {
                    resultArea.setText("❌ INVENTORY FULL!\n\n" +
                            "You pulled: " + pulledItem.getName() + "\n" +
                            "But your inventory is full!\n\n" +
                            "Please discard some items to make space.");
                    mainApp.showMessage("❌ Inventory full! Cannot add " + pulledItem.getName());
                }
            }

            refresh();

        } catch (NotEnoughCoinsException e) {
            mainApp.showMessage("❌ Not enough coins! You need " + e.getRequired() +
                    " coins but only have " + e.getAvailable());
        } catch (Exception e) {
            mainApp.showMessage("❌ Error during pull: " + e.getMessage());
        }
    }

    public void refresh() {
        if (game.getCurrentGachaMachine() != null) {
            machineLabel.setText("🎰 Machine: " + game.getCurrentGachaMachine().getMachineName());
            costLabel.setText("💰 Cost: " + game.getCurrentGachaMachine().getPullCost() + " coins per pull");

            int pullsSinceEpic = game.getCurrentGachaMachine().getPullsSinceLastEpic();
            pityLabel.setText("📊 Pity Counter: " + pullsSinceEpic + "/10" +
                    (pullsSinceEpic >= 8 ? " (Epic Soon!)" : ""));
        }

        int coins = game.getCurrentPlayer().getCoinBalance();
        coinsLabel.setText("🪙 Your Coins: " + coins);

        pullButton.setEnabled(coins >= 20);
        pullButton.setText(pullButton.isEnabled() ? "🎲 PULL GACHA (20 coins)" : "❌ Need 20 coins");

        if (resultArea.getText().isEmpty()) {
            resultArea.setText("🎰 WELCOME TO THE GACHA MACHINE!\n\n" +
                    "• Each pull costs 20 coins\n" +
                    "• Every 10th pull guarantees an EPIC item!\n" +
                    "• Items go directly to your inventory\n\n" +
                    "Click PULL to try your luck! 🍀");
        }
    }
}