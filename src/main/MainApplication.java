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

    public AudioManager getAudioManager() {
        return audioManager;
    }

    // Navigation methods
    public void showMainMenu() {
        // Play main menu music
        audioManager.playMusic(AudioFiles.MAIN_MENU);

        cardLayout.show(mainPanel, "MAIN_MENU");
        mainMenuPanel.refresh();

        cardLayout.show(mainPanel, "MENU");
        if (mainMenuPanel != null) {
            mainMenuPanel.refresh();
        }
    }

    public void showGame() {
        // Determine which music to play based on current room
        int roomIndex = game.getCurrentRoomIndex();

        switch(roomIndex) {
            case 0: audioManager.playMusic(AudioFiles.ROOM_1); break;
            case 1: audioManager.playMusic(AudioFiles.ROOM_2); break;
            case 2: audioManager.playMusic(AudioFiles.ROOM_3); break;
            case 3: audioManager.playMusic(AudioFiles.ROOM_4); break;
            case 4: audioManager.playMusic(AudioFiles.ROOM_5); break;  // Room 5 - Temporal Nexus
            default: audioManager.playMusic(AudioFiles.ROOM_1);
        }

        cardLayout.show(mainPanel, "GAME");
        gamePanel.refresh();

        cardLayout.show(mainPanel, "GAME");
        if (gamePanel != null) {
            gamePanel.refresh();
        }
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
        // If it's an error message, play error sound
        if (message.contains("❌") || message.contains("error") ||
                message.contains("Error") || message.contains("failed")) {
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
        audioManager.playMusic(AudioFiles.ROOM_5); // Or play special victory music
    }

}