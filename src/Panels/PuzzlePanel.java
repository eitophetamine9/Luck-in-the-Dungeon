package Panels;

import main.MainApplication;
import model.*;
import javax.swing.*;
import java.awt.*;
import java.util.List;
import audio.AudioFiles;


public class PuzzlePanel extends JPanel {
    private JPanel puzzlePanel;
    private JScrollPane descScroll;
    private JComboBox<Puzzle> puzzleSelector;
    private JTextArea puzzleDescription;
    private JTextArea hintTextArea;
    private JTextField answerField;
    private JButton solveButton;
    private JComboBox<GachaItem> toolsComboBox;
    private JButton useItemButton;
    private JButton backButton;
    private JLabel titleLabel;
    private JLabel coinRewardLabel;
    private JLabel difficultyLabel;
    private JLabel toolsLabel;
    private JComboBox<String> itemSelector;
    private JLabel answerLabel;
    private JLabel selectPuzzleLabel;
    private JButton hintButton;

    private MainApplication mainApp;
    private GameManager game;
    private Puzzle currentPuzzle;

    public PuzzlePanel(MainApplication mainApp, GameManager game) {
        this.mainApp = mainApp;
        this.game = game;

        setLayout(new BorderLayout());
        add(puzzlePanel, BorderLayout.CENTER);

        if (descScroll != null && puzzleDescription == null) {
            puzzleDescription = (JTextArea) descScroll.getViewport().getView();
        }

        initializePanel();
        setupEventHandlers();
        stylePuzzleComponents();
        refresh();
    }

    private void initializePanel() {
        setOpaque(false);
        if (puzzlePanel != null) {
            puzzlePanel.setOpaque(false);
        }

        makeLabelTransparent(titleLabel);
        makeLabelTransparent(coinRewardLabel);
        makeLabelTransparent(difficultyLabel);
        makeLabelTransparent(toolsLabel);
        makeLabelTransparent(answerLabel);
        makeLabelTransparent(selectPuzzleLabel);

        if (puzzleDescription != null) {
            puzzleDescription.setEditable(false);
            puzzleDescription.setLineWrap(true);
            puzzleDescription.setWrapStyleWord(true);
            puzzleDescription.setFont(new Font("Monospaced", Font.BOLD, 20));
            puzzleDescription.setForeground(new Color(220, 220, 255));
            puzzleDescription.setCaretColor(Color.YELLOW);
            puzzleDescription.setOpaque(false);
        }

        if (hintTextArea != null) {
            hintTextArea.setEditable(false);
            hintTextArea.setLineWrap(true);
            hintTextArea.setWrapStyleWord(true);
            hintTextArea.setFont(new Font("Monospaced", Font.PLAIN, 16));
            hintTextArea.setForeground(new Color(255, 255, 180));
            hintTextArea.setCaretColor(Color.ORANGE);
            hintTextArea.setOpaque(false);
        }

        if (puzzleSelector != null) {
            puzzleSelector.setRenderer(new PuzzleListCellRenderer());
            puzzleSelector.setMaximumRowCount(5);
        }

        if (toolsComboBox != null) {
            toolsComboBox.setRenderer(new ToolsListCellRenderer());
            toolsComboBox.setMaximumRowCount(5);
        }

        if (itemSelector != null) {
            itemSelector.setVisible(false);
        }
    }

    private class PuzzleListCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                      boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

            if (value instanceof Puzzle) {
                Puzzle puzzle = (Puzzle) value;

                String emoji = puzzle.isSolved() ? "✅ " : "🧩 ";
                String difficultyStars = puzzle.getDifficultyStars();

                String desc = puzzle.getDescription();
                if (desc.length() > 50) {
                    desc = desc.substring(0, 47) + "...";
                }

                String displayText = String.format(
                        "<html><div style='padding: 8px; font-size: 14px;'>" +
                                "<b>%s%s</b><br>" +
                                "<font size='-1' color='#AAAAAA'>%s</font><br>" +
                                "<font size='-2'>Difficulty: %s | Reward: %d coins</font>" +
                                "</div></html>",
                        emoji, desc,
                        puzzle.isSolved() ? "✓ SOLVED" : "✗ UNSOLVED",
                        difficultyStars, puzzle.getCoinReward()
                );

                setText(displayText);

                if (isSelected) {
                    setBackground(new Color(100, 80, 120, 240));
                    setForeground(Color.WHITE);
                    setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(new Color(255, 215, 0), 2),
                            BorderFactory.createEmptyBorder(5, 8, 5, 8)
                    ));
                } else {
                    if (puzzle.isSolved()) {
                        setBackground(new Color(60, 100, 60, 180));
                        setForeground(new Color(200, 255, 200));
                    } else {
                        setBackground(index % 2 == 0 ?
                                new Color(50, 40, 70, 180) :
                                new Color(60, 50, 80, 180));
                        setForeground(new Color(240, 240, 255));
                    }
                    setBorder(BorderFactory.createEmptyBorder(5, 8, 5, 8));
                }

                setFont(new Font("Segoe UI", Font.BOLD, 16));
            }

            return this;
        }
    }

    private class ToolsListCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                      boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

            if (value instanceof GachaItem) {
                GachaItem item = (GachaItem) value;

                if (item.getName().equals("No helpful items")) {
                    setText("<html><div style='padding: 5px; color: #888888; font-style: italic;'>" +
                            "No helpful items in inventory</div></html>");
                    setBackground(isSelected ? new Color(80, 80, 100, 200) : new Color(60, 60, 80, 180));
                    setForeground(new Color(180, 180, 180));
                    setBorder(BorderFactory.createEmptyBorder(5, 8, 5, 8));
                    setFont(new Font("Segoe UI", Font.ITALIC, 12));
                    return this;
                }

                Color rarityColor;
                String rarityText;
                switch (item.getRarity()) {
                    case EPIC:
                        rarityColor = new Color(255, 215, 0);
                        rarityText = "✨ EPIC";
                        break;
                    case RARE:
                        rarityColor = new Color(100, 200, 255);
                        rarityText = "⭐ RARE";
                        break;
                    default:
                        rarityColor = new Color(200, 200, 200);
                        rarityText = "○ COMMON";
                }

                String itemIcon = item.getItemType() == ItemType.KEY ? "🔑 " : "🛠️ ";
                String itemType = item.getItemType() == ItemType.KEY ? "Key" : "Tool";

                String displayText = String.format(
                        "<html><div style='padding: 5px;'>" +
                                "<b>%s%s</b><br>" +
                                "<font color='#%02X%02X%02X'>%s</font> | <font size='-1'>%s</font><br>" +
                                "<font size='-2' color='#AAAAAA'>%s</font>" +
                                "</div></html>",
                        itemIcon, item.getName(),
                        rarityColor.getRed(), rarityColor.getGreen(), rarityColor.getBlue(), rarityText,
                        itemType,
                        item.getDescription().length() > 60 ?
                                item.getDescription().substring(0, 57) + "..." : item.getDescription()
                );

                setText(displayText);

                if (isSelected) {
                    setBackground(new Color(120, 80, 40, 240));
                    setForeground(Color.WHITE);
                    setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(rarityColor, 2),
                            BorderFactory.createEmptyBorder(5, 8, 5, 8)
                    ));
                } else {
                    setBackground(index % 2 == 0 ?
                            new Color(70, 60, 50, 180) :
                            new Color(80, 70, 60, 180));
                    setForeground(new Color(240, 240, 255));
                    setBorder(BorderFactory.createEmptyBorder(5, 8, 5, 8));
                }

                setFont(new Font("Segoe UI", Font.PLAIN, 12));
            }

            return this;
        }
    }

    private void makeLabelTransparent(JLabel label) {
        if (label != null) {
            label.setOpaque(false);
            label.setBackground(new Color(0, 0, 0, 0));
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        // CHANGE: Make these deep blues almost black
        Color color1 = new Color(15, 12, 20, 255); // Was (30, 25, 40)
        Color color2 = new Color(25, 20, 35, 255); // Was (50, 40, 65)

        GradientPaint gradient = new GradientPaint(
                0, 0, color1,
                getWidth(), getHeight(), color2
        );
        g2d.setPaint(gradient);
        g2d.fillRect(0, 0, getWidth(), getHeight());

        g2d.setColor(new Color(255, 255, 255, 10));
        for (int i = 0; i < getWidth(); i += 30) {
            g2d.drawLine(i, 0, i, getHeight());
        }
        for (int i = 0; i < getHeight(); i += 30) {
            g2d.drawLine(0, i, getWidth(), i);
        }

        g2d.setColor(new Color(100, 150, 255, 15));
        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;
        int glowSize = Math.min(getWidth(), getHeight()) / 2;
        g2d.fillOval(centerX - glowSize/2, centerY - glowSize/2, glowSize, glowSize);

        g2d.setColor(new Color(255, 255, 200, 40));
        int particleCount = 20;
        for (int i = 0; i < particleCount; i++) {
            int x = (int)(Math.random() * getWidth());
            int y = (int)(Math.random() * getHeight());
            int size = (int)(Math.random() * 4) + 1;
            g2d.fillOval(x, y, size, size);
        }

        g2d.setColor(new Color(150, 100, 255, 25));
        g2d.setStroke(new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        for (int i = 0; i < 3; i++) {
            int circleSize = 100 + i * 80;
            g2d.drawOval(centerX - circleSize/2, centerY - circleSize/2, circleSize, circleSize);
        }
    }

    private void stylePuzzleComponents() {
        if (titleLabel != null) {
            titleLabel.setForeground(new Color(255, 215, 0));
            titleLabel.setFont(new Font("Monospaced", Font.BOLD, 28));
            titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
            titleLabel.setText("🧠 ANCIENT PUZZLES 🧠");
        }

        stylePuzzleLabel(coinRewardLabel, "💰 Reward:", new Color(255, 255, 150));
        stylePuzzleLabel(difficultyLabel, "⭐ Difficulty:", new Color(150, 255, 255));
        stylePuzzleLabel(toolsLabel, "🛠️ Available Tools:", new Color(180, 220, 255));
        stylePuzzleLabel(answerLabel, "📝 Your Answer:", new Color(255, 200, 150));
        stylePuzzleLabel(selectPuzzleLabel, "🔍 Select Puzzle:", new Color(200, 255, 200));

        if (puzzleDescription != null) {
            puzzleDescription.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(100, 80, 120, 200), 2),
                    BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(new Color(150, 130, 180, 100), 1),
                            BorderFactory.createEmptyBorder(15, 15, 15, 15)
                    )
            ));
        }

        if (hintTextArea != null) {
            hintTextArea.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(80, 120, 100, 200), 2),
                    BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(new Color(130, 180, 150, 100), 1),
                            BorderFactory.createEmptyBorder(12, 12, 12, 12)
                    )
            ));
        }

        if (puzzleSelector != null) {
            puzzleSelector.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(150, 130, 180, 200), 2),
                    BorderFactory.createEmptyBorder(8, 12, 8, 12)
            ));
            puzzleSelector.setBackground(new Color(60, 50, 80, 220));
            puzzleSelector.setForeground(Color.WHITE);
            puzzleSelector.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        }

        if (toolsComboBox != null) {
            toolsComboBox.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(130, 150, 180, 200), 2),
                    BorderFactory.createEmptyBorder(8, 12, 8, 12)
            ));
            toolsComboBox.setBackground(new Color(60, 50, 80, 220));
            toolsComboBox.setForeground(Color.WHITE);
            toolsComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        }

        stylePuzzleButton(solveButton, "🎯 SOLVE PUZZLE", new Color(80, 160, 80));
        stylePuzzleButton(useItemButton, "🔧 USE ITEM", new Color(160, 120, 80));
        stylePuzzleButton(backButton, "🔙 BACK TO GAME", new Color(80, 80, 120));
        stylePuzzleButton(hintButton, "💡 GET HINT", new Color(160, 80, 160));

        if (answerField != null) {
            answerField.setFont(new Font("Segoe UI", Font.BOLD, 14));
            answerField.setForeground(new Color(255, 255, 200));
            answerField.setCaretColor(Color.YELLOW);
            answerField.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(180, 150, 100, 200), 2),
                    BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(new Color(220, 200, 150, 100), 1),
                            BorderFactory.createEmptyBorder(10, 15, 10, 15)
                    )
            ));
            answerField.setOpaque(false);
        }

        if (descScroll != null) {
            descScroll.setOpaque(false);
            descScroll.getViewport().setOpaque(false);
            descScroll.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
            descScroll.getViewport().setBorder(null);
        }
    }

    private void stylePuzzleLabel(JLabel label, String defaultText, Color color) {
        if (label == null) return;

        if (!label.getText().isEmpty()) {
            label.setText("✨ " + label.getText());
        } else {
            label.setText(defaultText);
        }
        label.setForeground(color);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        label.setHorizontalAlignment(SwingConstants.LEFT);
    }

    private void stylePuzzleButton(JButton button, String defaultText, Color baseColor) {
        if (button == null) return;

        if (button.getText().isEmpty()) {
            button.setText(defaultText);
        }

        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setFocusPainted(false);

        Color buttonBg = new Color(baseColor.getRed(), baseColor.getGreen(), baseColor.getBlue(), 200);
        Color hoverColor = new Color(
                Math.min(255, baseColor.getRed() + 40),
                Math.min(255, baseColor.getGreen() + 40),
                Math.min(255, baseColor.getBlue() + 40),
                230
        );

        button.setBackground(buttonBg);
        button.setForeground(Color.WHITE);
        button.setOpaque(true);

        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(255, 255, 220, 150), 2),
                BorderFactory.createEmptyBorder(12, 25, 12, 25)
        ));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                if (button.isEnabled()) {
                    button.setBackground(hoverColor);
                    button.setForeground(Color.YELLOW);
                    button.setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(Color.ORANGE, 3),
                            BorderFactory.createEmptyBorder(12, 25, 12, 25)
                    ));

                    Timer glowTimer = new Timer(150, null);
                    final boolean[] isBright = {false};
                    glowTimer.addActionListener(evt -> {
                        if (button.isEnabled()) {
                            if (isBright[0]) {
                                button.setForeground(new Color(255, 255, 180));
                                button.setBorder(BorderFactory.createCompoundBorder(
                                        BorderFactory.createLineBorder(new Color(255, 200, 100), 3),
                                        BorderFactory.createEmptyBorder(12, 25, 12, 25)
                                ));
                            } else {
                                button.setForeground(Color.YELLOW);
                                button.setBorder(BorderFactory.createCompoundBorder(
                                        BorderFactory.createLineBorder(Color.ORANGE, 3),
                                        BorderFactory.createEmptyBorder(12, 25, 12, 25)
                                ));
                            }
                            isBright[0] = !isBright[0];
                            button.repaint();
                        } else {
                            glowTimer.stop();
                        }
                    });
                    button.putClientProperty("glowTimer", glowTimer);
                    glowTimer.start();
                }
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                Object timer = button.getClientProperty("glowTimer");
                if (timer instanceof Timer) {
                    ((Timer) timer).stop();
                }

                button.setBackground(buttonBg);
                button.setForeground(Color.WHITE);
                button.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(255, 255, 220, 150), 2),
                        BorderFactory.createEmptyBorder(12, 25, 12, 25)
                ));
            }
        });
    }

    private void setupEventHandlers() {
        if (puzzleSelector != null) {
            puzzleSelector.addActionListener(e -> {
                mainApp.getAudioManager().playSound(AudioFiles.CLICK);
                updatePuzzleDisplay();
            });
        }
        if (solveButton != null) {
            solveButton.addActionListener(e -> {
                mainApp.getAudioManager().playSound(AudioFiles.CLICK);
                attemptSolve();
            });
        }
        if (useItemButton != null) {
            useItemButton.addActionListener(e -> {
                mainApp.getAudioManager().playSound(AudioFiles.CLICK);
                useSelectedItem();
            });
        }
        if (backButton != null) {
            backButton.addActionListener(e -> {
                mainApp.getAudioManager().playSound(AudioFiles.CLICK);
                mainApp.showGame();
            });
        }
        if (hintButton != null) {
            hintButton.addActionListener(e -> {
                mainApp.getAudioManager().playSound(AudioFiles.CLICK);
                showAdditionalHint();
            });
        }
    }

    private void showAdditionalHint() {
        if (currentPuzzle == null) {
            mainApp.showMessage("Please select a puzzle first!");
            return;
        }

        if (currentPuzzle.isSolved()) {
            mainApp.showMessage("This puzzle is already solved!");
            return;
        }

        String detailedHint = currentPuzzle.getDetailedHint();
        if (hintTextArea != null) {
            hintTextArea.setText("💡 ADVANCED HINT:\n" + detailedHint);
        }
        mainApp.showMessage("💡 Advanced hint revealed!");
    }

    private void updatePuzzleDisplay() {
        if (puzzleSelector != null) {
            currentPuzzle = (Puzzle) puzzleSelector.getSelectedItem();
        }

        if (currentPuzzle != null) {
            if (puzzleDescription != null) {
                puzzleDescription.setText("📋 " + currentPuzzle.getDescription() +
                        "\n\n" + currentPuzzle.getDifficultyStars() + " Difficulty");
            }

            if (hintTextArea != null) {
                hintTextArea.setText("💡 HINT:\n" + currentPuzzle.getProgressiveHint());
            }

            if (coinRewardLabel != null) {
                coinRewardLabel.setText("💰 Reward: " + currentPuzzle.getCoinReward() + " coins");
            }

            if (difficultyLabel != null) {
                difficultyLabel.setText("⭐ Difficulty: " + currentPuzzle.getDifficultyStars());
            }

            if (currentPuzzle.requiresGachaItem() && hintTextArea != null) {
                hintTextArea.append("\n\n🔒 SPECIAL REQUIREMENT: This puzzle needs a specific item!");
                if (currentPuzzle.getRequiredToolType() != null) {
                    hintTextArea.append("\nRequired: " + currentPuzzle.getRequiredToolType());
                }
            }

            updateToolsComboBox();

            // 🔥 CRITICAL FIX: Update button states based on current puzzle
            updateButtonStates();
        }
    }

    private void updateButtonStates() {
        if (currentPuzzle != null) {
            boolean isSolved = currentPuzzle.isSolved();

            // Solve button should be enabled for UNSOLVED puzzles
            if (solveButton != null) {
                solveButton.setEnabled(!isSolved);
                if (!isSolved) {
                    solveButton.setText("🎯 SOLVE PUZZLE");
                    solveButton.setBackground(new Color(80, 160, 80, 200));
                } else {
                    solveButton.setText("✅ ALREADY SOLVED");
                    solveButton.setBackground(new Color(100, 100, 100, 180));
                }
            }

            // Hint button should be enabled for UNSOLVED puzzles
            if (hintButton != null) {
                hintButton.setEnabled(!isSolved);
                if (!isSolved) {
                    hintButton.setText("💡 GET HINT");
                    hintButton.setBackground(new Color(160, 80, 160, 200));
                } else {
                    hintButton.setText("✅ NO HINT NEEDED");
                    hintButton.setBackground(new Color(100, 100, 100, 180));
                }
            }

            // Use item button should be enabled for UNSOLVED puzzles with helpful items
            if (useItemButton != null) {
                boolean hasHelpfulItems = toolsComboBox != null &&
                        toolsComboBox.getItemCount() > 0 &&
                        !currentPuzzle.isSolved() &&
                        !toolsComboBox.getItemAt(0).getName().equals("No helpful items");
                useItemButton.setEnabled(hasHelpfulItems && !isSolved);
                if (hasHelpfulItems && !isSolved) {
                    useItemButton.setText("🔧 USE ITEM");
                    useItemButton.setBackground(new Color(160, 120, 80, 200));
                } else if (isSolved) {
                    useItemButton.setText("✅ NO ITEM NEEDED");
                    useItemButton.setBackground(new Color(100, 100, 100, 180));
                } else {
                    useItemButton.setText("🔧 NO HELPFUL ITEMS");
                    useItemButton.setBackground(new Color(100, 100, 100, 180));
                }
            }
        }
    }

    private void updateToolsComboBox() {
        if (toolsComboBox == null || currentPuzzle == null) return;

        toolsComboBox.removeAllItems();
        List<GachaItem> helpfulItems = game.getCurrentPlayer().findHelpfulItems(currentPuzzle);

        for (GachaItem item : helpfulItems) {
            toolsComboBox.addItem(item);
        }

        if (toolsComboBox.getItemCount() == 0 && !currentPuzzle.isSolved()) {
            toolsComboBox.addItem(new GachaItem("No helpful items", "Check your inventory",
                    Rarity.COMMON, ItemType.TOOL) {
                @Override
                public boolean use(Puzzle puzzle) { return false; }
            });
        }

        // 🔥 IMPORTANT: Update button states after changing tools combo box
        updateButtonStates();
    }

    private void updateItemSelector() {
        if (itemSelector != null || currentPuzzle == null) return;
    }

    private void attemptSolve() {
        if (currentPuzzle == null) {
            mainApp.showMessage("Please select a puzzle first!");
            return;
        }

        if (currentPuzzle.isSolved()) {
            mainApp.showMessage("This puzzle is already solved!");
            return;
        }

        if (answerField == null) {
            mainApp.showMessage("Answer field not available!");
            return;
        }

        String answer = answerField.getText().trim();
        if (answer.isEmpty()) {
            mainApp.showMessage("Please enter an answer!");
            return;
        }

        boolean solved = false;
        String puzzleType = "";

        if (currentPuzzle instanceof RiddlePuzzle) {
            RiddlePuzzle riddle = (RiddlePuzzle) currentPuzzle;
            solved = riddle.checkAnswer(answer);
            puzzleType = "riddle";
        } else if (currentPuzzle instanceof CodePuzzle) {
            CodePuzzle codePuzzle = (CodePuzzle) currentPuzzle;
            solved = codePuzzle.validateCode(answer);
            puzzleType = "code";
        } else if (currentPuzzle instanceof LockPuzzle) {
            mainApp.showMessage("🔒 This is a lock puzzle! You need to use a key item from your inventory.");
            return;
        }

        if (solved) {
            handlePuzzleSolved();
        } else {
            currentPuzzle.recordAttempt();
            mainApp.showMessage("Incorrect " + puzzleType + "! Try again. Attempts: " +
                    currentPuzzle.getPuzzleStats().get("attempts"));
            if (hintTextArea != null) {
                hintTextArea.setText("💡 HINT:\n" + currentPuzzle.getProgressiveHint());
            }
        }
    }

    private void useSelectedItem() {
        if (currentPuzzle == null) {
            mainApp.showMessage("Please select a puzzle first!");
            return;
        }

        if (currentPuzzle.isSolved()) {
            mainApp.showMessage("This puzzle is already solved!");
            return;
        }

        if (toolsComboBox == null) {
            mainApp.showMessage("Tools combo box not available!");
            return;
        }

        GachaItem selectedItem = (GachaItem) toolsComboBox.getSelectedItem();
        if (selectedItem == null || selectedItem.getName().equals("No helpful items")) {
            mainApp.showMessage("Please select a valid item to use!");
            return;
        }

        String result = game.getCurrentPlayer().useItemWithFeedback(selectedItem, currentPuzzle);
        if (hintTextArea != null) {
            hintTextArea.setText("🛠️ ITEM USAGE RESULT:\n" + result);
        }

        if (currentPuzzle.isSolved()) {
            handlePuzzleSolved();
        } else {
            // 🔥 Update button states even if puzzle wasn't solved (item might be used up)
            updateButtonStates();
            refresh();
        }
    }

    private void handlePuzzleSolved() {
        mainApp.getAudioManager().playSound(AudioFiles.SUCCESS);
        mainApp.getAudioManager().playSound(AudioFiles.COIN);

        int reward = currentPuzzle.getCoinReward();
        game.getCurrentPlayer().earnCoins(reward);


        java.util.Map<String, Object> stats = currentPuzzle.getPuzzleStats();
        String message = "🎉 PUZZLE SOLVED!\n" +
                "💰 Earned: " + reward + " coins\n" +
                "⚡ Attempts: " + stats.get("attempts") + "\n" +
                "⭐ Difficulty: " + currentPuzzle.getDifficultyStars();

        mainApp.showMessage(message);

        if (game.getCurrentRoom().isComplete()) {
            mainApp.showMessage("🏆 ROOM COMPLETE!\n" +
                    "You've solved all puzzles in " + game.getCurrentRoom().getName() +
                    "!\nYou can now proceed to the next room.");
        }

        // 🔥 CRITICAL: Refresh the display AND update button states
        refresh();
        updateButtonStates();
    }

    public void refresh() {
        System.out.println("🔄 PuzzlePanel refresh called");

        if (puzzleSelector != null) {
            puzzleSelector.removeAllItems();

            Room currentRoom = game.getCurrentRoom();
            if (currentRoom != null) {
                System.out.println("📍 Current Room: " + currentRoom.getName() +
                        " (Room " + currentRoom.getRoomNumber() + ")");

                List<Puzzle> roomPuzzles = currentRoom.getPuzzles();
                System.out.println("🎯 Found " + roomPuzzles.size() + " puzzles in this room");

                int availablePuzzles = 0;
                for (Puzzle puzzle : roomPuzzles) {
                    puzzleSelector.addItem(puzzle);
                    availablePuzzles++;

                    System.out.println("   - " + puzzle.getDescription().substring(0, Math.min(40, puzzle.getDescription().length())) +
                            " [Solved: " + puzzle.isSolved() + "]");
                }

                if (availablePuzzles > 0) {
                    puzzleSelector.setSelectedIndex(0);
                    updatePuzzleDisplay();
                    System.out.println("✅ Loaded " + availablePuzzles + " puzzles into selector");
                } else {
                    System.out.println("❌ No puzzles found in current room!");
                    showNoPuzzlesMessage();
                }
            } else {
                System.out.println("❌ Current room is null!");
                showNoPuzzlesMessage();
            }
        } else {
            System.out.println("❌ puzzleSelector is null!");
        }

        if (answerField != null) {
            answerField.setText("");
            answerField.requestFocus();
        }

        // 🔥 CRITICAL: Always update button states after refresh
        updateButtonStates();

        repaint();
    }

    private void showNoPuzzlesMessage() {
        if (puzzleDescription != null) {
            puzzleDescription.setText("❌ No puzzles available in this room.\n\nThis might be a bug - check console for details.");
        }
        if (hintTextArea != null) {
            hintTextArea.setText("If you just moved to a new room, try:\n1. Going back to Game panel\n2. Returning to Puzzle panel\n3. If still broken, check console logs");
        }
        if (solveButton != null) {
            solveButton.setEnabled(false);
            solveButton.setText("❌ NO PUZZLES");
            solveButton.setBackground(new Color(100, 100, 100, 180));
        }
        if (useItemButton != null) {
            useItemButton.setEnabled(false);
            useItemButton.setText("❌ NO PUZZLES");
            useItemButton.setBackground(new Color(100, 100, 100, 180));
        }
        if (hintButton != null) {
            hintButton.setEnabled(false);
            hintButton.setText("❌ NO PUZZLES");
            hintButton.setBackground(new Color(100, 100, 100, 180));
        }
    }
}