package main;

import Panels.*;
import model.GameManager;
import javax.swing.*;
import java.awt.*;

public class MainApplication extends JFrame {
    private GameManager game;
    private CardLayout cardLayout;
    private JPanel mainPanel;

    // Panel references
    private MainMenuPanel mainMenuPanel;
    private GamePanel gamePanel;
    private PuzzlePanel puzzlePanel;
    private GachaPanel gachaPanel;
    private InventoryPanel inventoryPanel;
    private MapPanel mapPanel;

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

        // ✅ FIXED: Remove Kotlin-style named parameters
        mainMenuPanel = new MainMenuPanel(this, game);
        gamePanel = new GamePanel(this, game);
        puzzlePanel = new PuzzlePanel(this, game);
        gachaPanel = new GachaPanel(this, game);
        inventoryPanel = new InventoryPanel(this, game);
        mapPanel = new MapPanel(this, game);

        // ✅ FIXED: Correct syntax for mainPanel.add() calls
        mainPanel.add(mainMenuPanel, "MAIN_MENU");
        mainPanel.add(gamePanel, "GAME");
        mainPanel.add(puzzlePanel, "PUZZLE");  // Fixed typo: was "PUZZL1"
        mainPanel.add(gachaPanel, "GACHA");
        mainPanel.add(inventoryPanel, "INVENTORY");  // Fixed: was "IN"
        mainPanel.add(mapPanel, "MAP");

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
        System.out.println("🧩 Switching to Puzzle Panel - Room: " + game.getCurrentRoomIndex());
        cardLayout.show(mainPanel, "PUZZLE");
        if (puzzlePanel != null) {
            puzzlePanel.refresh();
        } else {
            System.err.println("❌ puzzlePanel is null!");
        }
    }

    public void showGacha() {
        cardLayout.show(mainPanel, "GACHA");
        gachaPanel.refresh();
    }

    public void showInventory() {
        cardLayout.show(mainPanel, "INVENTORY");
        inventoryPanel.refresh();
    }

    public void showMap() {
        cardLayout.show(mainPanel, "MAP");
        mapPanel.refresh();
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