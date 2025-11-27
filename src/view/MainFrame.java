// MainFrame.java
package view;

import model.*;
import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    private GameManager game;
    private CardLayout cardLayout;
    private JPanel mainPanel;

    // Panels
    private MainMenuPanel mainMenuPanel;
    private GamePanel gamePanel;
    private InventoryPanel inventoryPanel;
    private GachaPanel gachaPanel;
    private PuzzlePanel puzzlePanel;

    public MainFrame(GameManager game) {
        this.game = game;
        initializeGUI();
    }

    private void initializeGUI() {
        setTitle("Luck in the Dungeon");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 700);
        setLocationRelativeTo(null);

        // CardLayout for panel switching
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // Initialize panels
        mainMenuPanel = new MainMenuPanel(this, game);
        gamePanel = new GamePanel(this, game);
        inventoryPanel = new InventoryPanel(this, game);
        gachaPanel = new GachaPanel(this, game);
        puzzlePanel = new PuzzlePanel(this, game);

        // Add panels to card layout
        mainPanel.add(mainMenuPanel, "MAIN_MENU");
        mainPanel.add(gamePanel, "GAME");
        mainPanel.add(inventoryPanel, "INVENTORY");
        mainPanel.add(gachaPanel, "GACHA");
        mainPanel.add(puzzlePanel, "PUZZLE");

        add(mainPanel);

        // Start with main menu
        showMainMenu();
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

    public void showInventory() {
        cardLayout.show(mainPanel, "INVENTORY");
        inventoryPanel.refresh();
    }

    public void showGacha() {
        cardLayout.show(mainPanel, "GACHA");
        gachaPanel.refresh();
    }

    public void showPuzzle(Puzzle puzzle) {
        puzzlePanel.setPuzzle(puzzle);
        cardLayout.show(mainPanel, "PUZZLE");
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