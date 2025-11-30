package main;

import Panels.MainMenuPanel;
import Panels.GachaPanel;
import model.GameManager;
import javax.swing.*;
import java.awt.*;

public class MainApplication extends JFrame {
    // ... rest of your existing code remains the same
    private GameManager game;
    private CardLayout cardLayout;
    private JPanel mainPanel;

    // Panel references
    private MainMenuPanel mainMenuPanel;
    private GamePanel gamePanel;
    private PuzzlePanel puzzlePanel;
    private GachaPanel gachaPanel;
    private InventoryPanel inventoryPanel;

    public MainApplication() {
        game = GameManager.getInstance();
        initializeGUI();
        showMainMenu();
    }

    private void initializeGUI() {
        setTitle("Luck in the Dungeon");
        setSize(900, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // CardLayout for panel switching
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // Initialize all panels
        mainMenuPanel = new MainMenuPanel(this, game);
        gamePanel = new GamePanel(this, game);
        puzzlePanel = new PuzzlePanel(this, game);
        gachaPanel = new GachaPanel(this, game);
        inventoryPanel = new InventoryPanel(this, game);

        // Add panels to card layout
        mainPanel.add(mainMenuPanel, "MAIN_MENU");
        mainPanel.add(gamePanel, "GAME");
        mainPanel.add(puzzlePanel, "PUZZLE");
        mainPanel.add(gachaPanel, "GACHA");
        mainPanel.add(inventoryPanel, "INVENTORY");

        setContentPane(mainPanel);
    }

    // Navigation methods
    public void showMainMenu() {
        cardLayout.show(mainPanel, "MAIN_MENU");
        mainMenuPanel.refresh();
    }

    public void showGame() {
        cardLayout.show(mainPanel, "GAME");
        gamePanel.refresh();
    }

    public void showPuzzle() {
        cardLayout.show(mainPanel, "PUZZLE");
        puzzlePanel.refresh();
    }

    public void showGacha() {
        cardLayout.show(mainPanel, "GACHA");
        gachaPanel.refresh();
    }

    public void showInventory() {
        cardLayout.show(mainPanel, "INVENTORY");
        inventoryPanel.refresh();
    }

    public void showMessage(String message) {
        JOptionPane.showMessageDialog(this, message);
    }

    public boolean showConfirmDialog(String message) {
        int result = JOptionPane.showConfirmDialog(this, message, "Confirmation",
                JOptionPane.YES_NO_OPTION);
        return result == JOptionPane.YES_OPTION;
    }
}