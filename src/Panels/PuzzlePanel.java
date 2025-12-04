package Panels;

import main.MainApplication;
import model.*;
import javax.swing.*;
import java.awt.*;
import java.util.List;

public class PuzzlePanel extends JPanel {
    // ✅ UPDATED: Added all missing components from your form
    private JPanel puzzlePanel;
    private JScrollPane descScroll;
    private JComboBox<Puzzle> puzzleSelector;
    private JTextArea puzzleDescription;
    private JTextArea hintTextArea;   // ✅ CHANGED: hintArea → hintTextArea
    private JTextField answerField;
    private JButton solveButton;
    private JComboBox<GachaItem> toolsComboBox;
    private JButton useItemButton;
    private JButton backButton;
    private JLabel titleLabel;
    private JLabel coinRewardLabel;
    private JLabel difficultyLabel;

    // ✅ ADDED: Missing components from your form
    private JLabel toolsLabel;
    private JComboBox<String> itemSelector;
    private JLabel answerLabel;
    private JButton hintButton;

    private MainApplication mainApp;
    private GameManager game;
    private Puzzle currentPuzzle;

    public PuzzlePanel(MainApplication mainApp, GameManager game) {
        this.mainApp = mainApp;
        this.game = game;

        // ✅ Initialize form components
        setLayout(new BorderLayout());
        add(puzzlePanel, BorderLayout.CENTER);

        // ✅ Get text area from scroll pane if not directly bound
        if (descScroll != null && puzzleDescription == null) {
            puzzleDescription = (JTextArea) descScroll.getViewport().getView();
        }

        initializePanel();
        setupEventHandlers();
        refresh();
    }

    private void initializePanel() {
        // Configure text areas
        if (puzzleDescription != null) {
            puzzleDescription.setEditable(false);
            puzzleDescription.setLineWrap(true);
            puzzleDescription.setWrapStyleWord(true);
            puzzleDescription.setFont(new Font("Monospaced", Font.PLAIN, 12));
        }

        // ✅ CHANGED: hintArea → hintTextArea
        if (hintTextArea != null) {
            hintTextArea.setEditable(false);
            hintTextArea.setLineWrap(true);
            hintTextArea.setWrapStyleWord(true);
            hintTextArea.setBackground(new Color(160, 86, 45));
            hintTextArea.setFont(new Font("Monospaced", Font.ITALIC, 12));
        }

        // ✅ Configure toolsLabel if it exists
        if (toolsLabel != null) {
            toolsLabel.setText("Available Tools:");
            toolsLabel.setFont(new Font("Arial", Font.BOLD, 14));
        }

        // ✅ Configure answerLabel if it exists
        if (answerLabel != null) {
            answerLabel.setText("Your Answer:");
            answerLabel.setFont(new Font("Arial", Font.BOLD, 14));
        }

        // Configure combo box renderers
        if (puzzleSelector != null) {
            puzzleSelector.setRenderer(new DefaultListCellRenderer() {
                @Override
                public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                              boolean isSelected, boolean cellHasFocus) {
                    super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                    if (value instanceof Puzzle) {
                        Puzzle puzzle = (Puzzle) value;
                        String status = puzzle.isSolved() ? "✅ " : "❓ ";
                        setText(status + puzzle.getDescription().substring(0,
                                Math.min(40, puzzle.getDescription().length())) +
                                (puzzle.getDescription().length() > 40 ? "..." : ""));
                    }
                    return this;
                }
            });
        }

        if (toolsComboBox != null) {
            toolsComboBox.setRenderer(new DefaultListCellRenderer() {
                @Override
                public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                              boolean isSelected, boolean cellHasFocus) {
                    super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                    if (value instanceof GachaItem) {
                        GachaItem item = (GachaItem) value;
                        String icon = item.getItemType() == ItemType.KEY ? "🔑 " : "🛠️ ";
                        setText(icon + item.getName() + " (" + item.getRarity() + ")");

                        switch (item.getRarity()) {
                            case EPIC: setForeground(new Color(255, 215, 0)); break;
                            case RARE: setForeground(new Color(65, 105, 225)); break;
                            default: setForeground(Color.BLACK);
                        }
                    }
                    return this;
                }
            });
        }

        // ✅ Configure itemSelector if it exists
        if (itemSelector != null) {
            itemSelector.setVisible(false);
        }
    }

    private void setupEventHandlers() {
        if (puzzleSelector != null) {
            puzzleSelector.addActionListener(e -> updatePuzzleDisplay());
        }
        if (solveButton != null) {
            solveButton.addActionListener(e -> attemptSolve());
        }
        if (useItemButton != null) {
            useItemButton.addActionListener(e -> useSelectedItem());
        }
        if (backButton != null) {
            backButton.addActionListener(e -> mainApp.showGame());
        }

        if (itemSelector != null) {
            itemSelector.addActionListener(e -> {
                // Optional: Add functionality if itemSelector is used
            });
        }
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

            // ✅ CHANGED: hintArea → hintTextArea
            if (hintTextArea != null) {
                hintTextArea.setText("💡 HINT:\n" + currentPuzzle.getProgressiveHint());
            }

            if (coinRewardLabel != null) {
                coinRewardLabel.setText("💰 Reward: " + currentPuzzle.getCoinReward() + " coins");
            }

            if (difficultyLabel != null) {
                difficultyLabel.setText("⭐ Difficulty: " + currentPuzzle.getDifficultyStars());
            }

            // ✅ CHANGED: hintArea → hintTextArea
            if (currentPuzzle.requiresGachaItem() && hintTextArea != null) {
                hintTextArea.append("\n\n🔒 SPECIAL REQUIREMENT: This puzzle needs a specific item!");
                if (currentPuzzle.getRequiredToolType() != null) {
                    hintTextArea.append("\nRequired: " + currentPuzzle.getRequiredToolType());
                }
            }

            updateToolsComboBox();
        }
    }

    private void updateToolsComboBox() {
        if (toolsComboBox == null || currentPuzzle == null) return;

        toolsComboBox.removeAllItems();
        List<GachaItem> helpfulItems = game.getCurrentPlayer().findHelpfulItems(currentPuzzle);

        for (GachaItem item : helpfulItems) {
            toolsComboBox.addItem(item);
        }

        if (useItemButton != null) {
            useItemButton.setEnabled(toolsComboBox.getItemCount() > 0 && !currentPuzzle.isSolved());
        }

        if (toolsComboBox.getItemCount() == 0 && !currentPuzzle.isSolved()) {
            toolsComboBox.addItem(new GachaItem("No helpful items", "Check your inventory",
                    Rarity.COMMON, ItemType.TOOL) {
                @Override
                public boolean use(Puzzle puzzle) { return false; }
            });
        }

        if (itemSelector != null && itemSelector.isVisible()) {
            updateItemSelector();
        }
    }

    private void updateItemSelector() {
        if (itemSelector == null || currentPuzzle == null) return;

        itemSelector.removeAllItems();
        List<GachaItem> inventory = game.getCurrentPlayer().getInventory();

        for (GachaItem item : inventory) {
            itemSelector.addItem(item.getName() + " (" + item.getRarity() + ")");
        }
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
            // ✅ CHANGED: hintArea → hintTextArea
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
        // ✅ CHANGED: hintArea → hintTextArea
        if (hintTextArea != null) {
            hintTextArea.setText("🛠️ ITEM USAGE RESULT:\n" + result);
        }

        if (currentPuzzle.isSolved()) {
            handlePuzzleSolved();
        }

        refresh();
    }

    private void handlePuzzleSolved() {
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

        refresh();
    }

    public void refresh() {
        System.out.println("🔄 PuzzlePanel refresh called");

        if (puzzleSelector != null) {
            puzzleSelector.removeAllItems();

            // ✅ FIX: Get the CURRENT room properly
            Room currentRoom = game.getCurrentRoom();
            if (currentRoom != null) {
                System.out.println("📍 Current Room: " + currentRoom.getName() +
                        " (Room " + currentRoom.getRoomNumber() + ")");

                List<Puzzle> roomPuzzles = currentRoom.getPuzzles();
                System.out.println("🎯 Found " + roomPuzzles.size() + " puzzles in this room");

                int availablePuzzles = 0;
                for (Puzzle puzzle : roomPuzzles) {
                    // Show ALL puzzles from current room, but mark solved ones differently
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
        }
        if (useItemButton != null) {
            useItemButton.setEnabled(false);
        }
    }
}