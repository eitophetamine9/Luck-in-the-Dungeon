package main;

import Panels.*;
import model.GameManager;
import javax.swing.*;
import java.awt.*;
import audio.AudioManager;
import audio.AudioFiles;

public class MainApplication extends JFrame {
    private GameManager game;
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private AudioManager audioManager;

    // Panel references
    private MainMenuPanel mainMenuPanel;
    private GamePanel gamePanel;
    private PuzzlePanel puzzlePanel;
    private GachaPanel gachaPanel;
    private InventoryPanel inventoryPanel;
    private MapPanel mapPanel;

    public MainApplication() {
        audioManager = AudioManager.getInstance();
        audioManager.setVolume(0.6f);

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

        mainMenuPanel = new MainMenuPanel(this, game);
        gamePanel = new GamePanel(this, game);
        puzzlePanel = new PuzzlePanel(this, game);
        gachaPanel = new GachaPanel(this, game);
        inventoryPanel = new InventoryPanel(this, game);
        mapPanel = new MapPanel(this, game);

        mainPanel.add(mainMenuPanel, "MENU");
        mainPanel.add(gamePanel, "GAME");
        mainPanel.add(puzzlePanel, "PUZZLE");
        mainPanel.add(gachaPanel, "GACHA");
        mainPanel.add(inventoryPanel, "INVENTORY");
        mainPanel.add(mapPanel, "MAP");

        setContentPane(mainPanel);
    }

    public AudioManager getAudioManager() {
        return audioManager;
    }

    // Navigation methods
    public void showMainMenu() {
        cardLayout.show(mainPanel, "MENU");
        mainMenuPanel.refresh();
        audioManager.playMusic(AudioFiles.MAIN_MENU);
    }

    public void showGame() {
        cardLayout.show(mainPanel, "GAME");
        gamePanel.refresh();

        // Let GamePanel handle room-specific music
        // Music will be updated when GamePanel.refresh() is called
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

    public void showMap() {
        cardLayout.show(mainPanel, "MAP");
        mapPanel.refresh();
        audioManager.playSound(AudioFiles.MAP);
    }

    public void showMessage(String message) {
        if (message.contains("❌") || message.toLowerCase().contains("error") ||
                message.toLowerCase().contains("failed")) {
            audioManager.playSound(AudioFiles.ERROR);
        }

        JOptionPane.showMessageDialog(this, message);
    }

    public boolean showConfirmDialog(String message) {
        int result = JOptionPane.showConfirmDialog(this, message, "Confirmation",
                JOptionPane.YES_NO_OPTION);
        return result == JOptionPane.YES_OPTION;
    }

    public void playVictoryMusic() {
        audioManager.playMusic(AudioFiles.ROOM_5);
    }
}