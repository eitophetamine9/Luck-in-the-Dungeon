package Panels;

import main.MainApplication;
import model.GameManager;
import model.GachaItem;
import exceptions.NotEnoughCoinsException;

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
        initializePanel();
        setupEventHandlers();
        refresh();
    }

    private void initializePanel() {
        setLayout(new BorderLayout());
        add(gachaPanel, BorderLayout.CENTER);

        // Configure text area
        resultArea.setEditable(false);
        resultArea.setLineWrap(true);
        resultArea.setWrapStyleWord(true);
    }

    private void setupEventHandlers() {
        pullButton.addActionListener(e -> pullGacha());
        backButton.addActionListener(e -> mainApp.showGame());
    }

    private void pullGacha() {
        try {
            GachaItem pulledItem = game.getCurrentGachaMachine().pull(game.getCurrentPlayer());

            if (pulledItem != null) {
                // Display the pulled item
                resultArea.setText("🎉 You pulled: " + pulledItem.getName() +
                        "\nRarity: " + pulledItem.getRarity() +
                        "\n" + pulledItem.getDescription());

                mainApp.showMessage("Congratulations! You got: " + pulledItem.getName());
            }

            refresh(); // Update coins and pity counter

        } catch (NotEnoughCoinsException e) {
            mainApp.showMessage("Not enough coins! You need 20 coins for a pull.");
        } catch (Exception e) {
            mainApp.showMessage("Error during pull: " + e.getMessage());
        }
    }

    public void refresh() {
        // Update machine info
        if (game.getCurrentGachaMachine() != null) {
            machineLabel.setText("Machine: " + game.getCurrentGachaMachine().getMachineName());
            pityLabel.setText("Pity: " + game.getCurrentGachaMachine().getPullsSinceLastEpic() + "/10");
        }

        // Update coin balance
        coinsLabel.setText("Coins: " + game.getCurrentPlayer().getCoinBalance());

        // Enable/disable pull button based on coins
        pullButton.setEnabled(game.getCurrentPlayer().getCoinBalance() >= 20);

        // Clear results if no pull has been made
        if (resultArea.getText().isEmpty()) {
            resultArea.setText("Pull the gacha to see your results here!");
        }
    }
}