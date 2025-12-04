package Panels;

import main.MainApplication;
import model.GameManager;

import javax.swing.*;
import java.awt.*;

public class MapPanel extends JPanel {
    // ✅ MUST MATCH FORM COMPONENT NAMES
    private JPanel mapPanel;
    private JButton room1Button;
    private JButton room2Button;
    private JButton room3Button;
    private JButton room4Button;
    private JButton backButton;
    private JLabel titleLabel;

    private MainApplication mainApp;
    private GameManager game;
    private Image mapImage;

    public MapPanel(MainApplication mainApp, GameManager game) {
        this.mainApp = mainApp;
        this.game = game;

        // ✅ Initialize form components
        setLayout(new BorderLayout());
        add(mapPanel, BorderLayout.CENTER);

        setupEventHandlers();
        loadMapImage();
        setOpaque(false);
        if (mapPanel != null){
            mapPanel.setOpaque(false);
        }
        refresh();
    }

    private void setupEventHandlers() {
        room1Button.addActionListener(e -> navigateToRoom(0));
        room2Button.addActionListener(e -> navigateToRoom(1));
        room3Button.addActionListener(e -> navigateToRoom(2));
        room4Button.addActionListener(e -> navigateToRoom(3));
        backButton.addActionListener(e -> mainApp.showGame());
    }

    private void navigateToRoom(int roomIndex) {
        String result = game.moveToRoom(roomIndex);

        if (result.contains("🔒")) {
            mainApp.showMessage(result);
        } else {
            mainApp.showMessage(result);
            mainApp.showGame();
        }
    }

    public void refresh() {
        updateRoomButton(room1Button, 0);
        updateRoomButton(room2Button, 1);
        updateRoomButton(room3Button, 2);
        updateRoomButton(room4Button, 3);
    }

    private void updateRoomButton(JButton button, int roomIndex) {
        String status = game.getRoomStatus(roomIndex);
        String roomName = game.getRooms().get(roomIndex).getName();

        button.setText("<html><center>ROOM " + (roomIndex + 1) + "<br>" +
                roomName + "<br>[" + status + "]</center></html>");

        switch (status) {
            case "📍 CURRENT":
                button.setBackground(new Color(144, 238, 144));
                button.setEnabled(true);
                break;
            case "✅ COMPLETED":
                button.setBackground(new Color(173, 216, 230));
                button.setEnabled(true);
                break;
            case "⚪ UNLOCKED":
                button.setBackground(new Color(255, 255, 150));
                button.setEnabled(true);
                break;
            case "🔒 LOCKED":
                button.setBackground(Color.LIGHT_GRAY);
                button.setEnabled(false);
                break;
        }

        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setPreferredSize(new Dimension(180, 70));
        button.setFocusPainted(false);
    }

    private void loadMapImage() {
        try {
            System.out.println("🔍 Loading background image for Map Panel...");

            // Try to load the same background as MainMenuPanel
            java.net.URL imageURL = getClass().getClassLoader().getResource("images/top_downview.jpeg");

            if (imageURL != null) {
                System.out.println("✅ Found image at URL: " + imageURL);
                mapImage = new ImageIcon(imageURL).getImage();
                System.out.println("✅ Map background loaded!");
                return;
            } else {
                System.out.println("❌ Image not found, using gradient fallback");
                mapImage = null;
            }

        } catch (Exception e) {
            System.out.println("❌ Error loading background: " + e.getMessage());
            mapImage = null;
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (mapImage != null) {
            // Draw image scaled to panel size
            g.drawImage(mapImage, 0, 0, getWidth(), getHeight(), this);
        } else {
            // Fallback gradient
            Graphics2D g2d = (Graphics2D) g;
            Color color1 = new Color(30, 30, 60);
            Color color2 = new Color(50, 50, 100);
            g2d.setPaint(new GradientPaint(0, 0, color1, 0, getHeight(), color2));
            g2d.fillRect(0, 0, getWidth(), getHeight());
        }
    }
}