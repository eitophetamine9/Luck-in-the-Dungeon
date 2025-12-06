package Panels;

import main.MainApplication;
import model.GameManager;

import javax.swing.*;
import javax.swing.border.Border; // 🆕 IMPORT ADDED
import java.awt.*;
import audio.AudioFiles;

public class MapPanel extends JPanel {
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

    // 🆕 FIX: Store button dimensions
    private static final Dimension ROOM_BUTTON_SIZE = new Dimension(250, 60);
    private static final Dimension BACK_BUTTON_SIZE = new Dimension(200, 50);

    public MapPanel(MainApplication mainApp, GameManager game) {
        this.mainApp = mainApp;
        this.game = game;

        // Keep your original layout
        setLayout(new BorderLayout());
        add(mapPanel, BorderLayout.CENTER);

        loadMapImage();
        setOpaque(false);
        if (mapPanel != null) {
            mapPanel.setOpaque(false);
        }

        setupEventHandlers();
        setupDungeonButtons();
        refresh();
    }

    private void setupEventHandlers() {
        room1Button.addActionListener(e -> navigateToRoom(0));
        room2Button.addActionListener(e -> navigateToRoom(1));
        room3Button.addActionListener(e -> navigateToRoom(2));
        room4Button.addActionListener(e -> navigateToRoom(3));
        backButton.addActionListener(e -> {
            mainApp.getAudioManager().playSound(AudioFiles.CLICK);
            mainApp.showGame();
        });
    }

    private void setupDungeonButtons() {
        // Set fixed sizes for all buttons
        room1Button.setPreferredSize(ROOM_BUTTON_SIZE);
        room1Button.setMaximumSize(ROOM_BUTTON_SIZE);
        room1Button.setMinimumSize(ROOM_BUTTON_SIZE);

        room2Button.setPreferredSize(ROOM_BUTTON_SIZE);
        room2Button.setMaximumSize(ROOM_BUTTON_SIZE);
        room2Button.setMinimumSize(ROOM_BUTTON_SIZE);

        room3Button.setPreferredSize(ROOM_BUTTON_SIZE);
        room3Button.setMaximumSize(ROOM_BUTTON_SIZE);
        room3Button.setMinimumSize(ROOM_BUTTON_SIZE);

        room4Button.setPreferredSize(ROOM_BUTTON_SIZE);
        room4Button.setMaximumSize(ROOM_BUTTON_SIZE);
        room4Button.setMinimumSize(ROOM_BUTTON_SIZE);

        backButton.setPreferredSize(BACK_BUTTON_SIZE);
        backButton.setMaximumSize(BACK_BUTTON_SIZE);
        backButton.setMinimumSize(BACK_BUTTON_SIZE);

        // Style all buttons with FIRE effects (FIXED VERSION)
        styleButtonWithFire(room1Button, "🏰 ROOM 1");
        styleButtonWithFire(room2Button, "🏰 ROOM 2");
        styleButtonWithFire(room3Button, "🏰 ROOM 3");
        styleButtonWithFire(room4Button, "🏰 ROOM 4");
        styleButtonWithFire(backButton, "🔙 BACK TO GAME");

        // Style title
        if (titleLabel != null) {
            titleLabel.setForeground(new Color(255, 215, 0)); // Gold
            titleLabel.setFont(new Font("Monospaced", Font.BOLD, 28));
            titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
            titleLabel.setText("🗺️ DUNGEON MAP 🗺️");
        }
    }

    // 🆕 COMPLETELY FIXED VERSION: No border changes during hover
    private void styleButtonWithFire(JButton button, String text) {
        // Set text and font
        button.setText(text);
        button.setForeground(new Color(255, 215, 0)); // Gold
        button.setFont(new Font("Monospaced", Font.BOLD, 14));

        // Create stone-like look
        Color stoneColor = new Color(70, 60, 50, 200); // Dark stone with transparency
        Color stoneBorderColor = new Color(150, 140, 130); // Light stone border

        button.setBackground(stoneColor);
        button.setOpaque(true);
        button.setBorderPainted(true);
        button.setFocusPainted(false);
        button.setContentAreaFilled(true);

        // 🆕 FIX: Create ONE permanent border that won't change
        Border permanentBorder = BorderFactory.createCompoundBorder(
                BorderFactory.createRaisedBevelBorder(),
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(stoneBorderColor, 2),
                        BorderFactory.createEmptyBorder(10, 15, 10, 15)
                )
        );

        button.setBorder(permanentBorder);

        // 🆕 FIXED: Fire hover effect - only changes colors, NOT border
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                if (button.isEnabled()) {
                    button.setBackground(new Color(120, 50, 30, 220)); // Fire-like red/orange
                    button.setForeground(Color.YELLOW);

                    // 🆕 NO BORDER CHANGES - only color animation
                    Timer fireTimer = new Timer(200, null);
                    final boolean[] isBright = {false};

                    fireTimer.addActionListener(evt -> {
                        if (button.isEnabled() && isBright[0]) {
                            button.setForeground(new Color(255, 255, 150)); // Bright yellow
                        } else if (button.isEnabled()) {
                            button.setForeground(Color.ORANGE);
                        }
                        isBright[0] = !isBright[0];
                        button.repaint();
                    });

                    button.putClientProperty("fireTimer", fireTimer);
                    fireTimer.start();
                }
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                // Stop fire animation timer
                Object timer = button.getClientProperty("fireTimer");
                if (timer instanceof Timer) {
                    ((Timer) timer).stop();
                }

                // Back to appropriate appearance
                updateButtonAppearance(button);
            }
        });
    }

    private void navigateToRoom(int roomIndex) {
        mainApp.getAudioManager().playSound(AudioFiles.CLICK);

        String result = game.moveToRoom(roomIndex);

        // Play unlock sound if successful
        if (result.contains("✅") || !result.contains("🔒")) {
            mainApp.getAudioManager().playSound(AudioFiles.UNLOCK);
        }

        if (result.contains("🔒")) {
            mainApp.showMessage(result);
        } else {
            mainApp.showMessage("✅ " + result);
            mainApp.showGame();
        }
    }

    public void refresh() {
        // Update room buttons with status
        updateRoomButtonDisplay(room1Button, 0);
        updateRoomButtonDisplay(room2Button, 1);
        updateRoomButtonDisplay(room3Button, 2);
        updateRoomButtonDisplay(room4Button, 3);

        // Update back button
        updateButtonAppearance(backButton);
    }

    private void updateRoomButtonDisplay(JButton button, int roomIndex) {
        String status = game.getRoomStatus(roomIndex);
        String roomName = game.getRooms().get(roomIndex).getName();

        // Status emoji
        String statusEmoji = "";
        switch (status) {
            case "📍 CURRENT": statusEmoji = "📍 "; break;
            case "✅ COMPLETED": statusEmoji = "✅ "; break;
            case "⚪ UNLOCKED": statusEmoji = "⚪ "; break;
            case "🔒 LOCKED": statusEmoji = "🔒 "; break;
        }

        // Update button text with status
        button.setText(statusEmoji + roomName);
        button.setToolTipText("Room " + (roomIndex + 1) + " - " + status);

        button.setEnabled(!status.equals("🔒 LOCKED"));
        updateButtonAppearance(button);
    }

    // 🆕 FIXED: Update appearance without changing border size
    private void updateButtonAppearance(JButton button) {
        if (button == null) return;

        String text = button.getText();
        Color stoneColor = new Color(70, 60, 50, 200);
        Color stoneBorderColor = new Color(150, 140, 130);

        if (text.contains("🔒")) {
            // Locked room - gray stone
            stoneColor = new Color(100, 90, 80, 180);
            stoneBorderColor = new Color(120, 110, 100);
            button.setForeground(Color.GRAY);
        }
        else if (text.contains("📍")) {
            // Current room - green stone
            stoneColor = new Color(80, 120, 80, 220);
            stoneBorderColor = new Color(120, 180, 120);
            button.setForeground(Color.WHITE);
        }
        else if (text.contains("✅")) {
            // Completed room - blue stone
            stoneColor = new Color(80, 100, 140, 220);
            stoneBorderColor = new Color(120, 150, 200);
            button.setForeground(Color.WHITE);
        }
        else if (text.contains("⚪")) {
            // Unlocked room - yellow stone
            stoneColor = new Color(140, 130, 80, 220);
            stoneBorderColor = new Color(200, 190, 120);
            button.setForeground(Color.BLACK);
        }
        else {
            // Back button - regular stone
            button.setForeground(new Color(255, 215, 0));
        }

        // Update button colors
        if (button.isEnabled()) {
            button.setBackground(stoneColor);
        } else {
            button.setBackground(new Color(100, 90, 80, 180));
            button.setForeground(Color.GRAY);
        }

        // 🆕 FIXED: Update border COLOR only, not size
        Border newBorder = BorderFactory.createCompoundBorder(
                BorderFactory.createRaisedBevelBorder(),
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(stoneBorderColor, 2),
                        BorderFactory.createEmptyBorder(10, 15, 10, 15)
                )
        );

        button.setBorder(newBorder);
    }

    private void loadMapImage() {
        try {
            // Try different image names
            String[] possibleImages = {
                    "images/top_downview.jpeg",
                    "images/top_downview.jpg",
                    "images/top_downview.png",
                    "images/map_background.jpeg",
                    "images/dungeon_map.jpeg"
            };

            for (String imagePath : possibleImages) {
                java.net.URL imageURL = getClass().getClassLoader().getResource(imagePath);
                if (imageURL != null) {
                    System.out.println("✅ Found map image: " + imagePath);
                    mapImage = new ImageIcon(imageURL).getImage();
                    return;
                }
            }

            System.out.println("❌ No map image found, using gradient");
            mapImage = null;

        } catch (Exception e) {
            System.out.println("❌ Error loading map image: " + e.getMessage());
            mapImage = null;
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (mapImage != null) {
            // Draw image covering entire panel
            g.drawImage(mapImage, 0, 0, getWidth(), getHeight(), this);
        } else {
            // Fallback: Fire-themed gradient background
            Graphics2D g2d = (Graphics2D) g;

            // Dark red/orange gradient like fire embers
            Color color1 = new Color(40, 20, 10);     // Dark brown/red
            Color color2 = new Color(80, 40, 20);     // Reddish brown

            GradientPaint gradient = new GradientPaint(
                    0, 0, color1,
                    getWidth(), getHeight(), color2
            );
            g2d.setPaint(gradient);
            g2d.fillRect(0, 0, getWidth(), getHeight());

            // Add subtle fire/ember effect
            g2d.setColor(new Color(255, 100, 0, 30));
            int emberSize = 100;
            for (int i = 0; i < 5; i++) {
                int x = (int)(Math.random() * getWidth());
                int y = (int)(Math.random() * getHeight());
                g2d.fillOval(x, y, emberSize, emberSize);
            }
        }
    }
}