// PuzzlePanel.java
package view;

import model.*;
import exceptions.*;
import javax.swing.*;
import java.awt.*;

public class PuzzlePanel extends JPanel {
    private MainFrame parent;
    private GameManager game;
    private Puzzle currentPuzzle;

    private JLabel puzzleTitleLabel;
    private JTextArea puzzleDescriptionArea;
    private JPanel inputPanel;
    private JButton submitButton;
    private JButton useItemButton;
    private JButton backButton;
    private JTextArea hintArea;

    public PuzzlePanel(MainFrame parent, GameManager game) {
        this.parent = parent;
        this.game = game;
        initializeGUI();
    }

    private void initializeGUI() {
        setLayout(new BorderLayout());
        setBackground(new Color(40, 40, 80));
    }

    public void setPuzzle(Puzzle puzzle) {
        this.currentPuzzle = puzzle;
        refreshPuzzleView();
    }

    private void refreshPuzzleView() {
        removeAll();

        if (currentPuzzle == null) {
            parent.showMessage("No puzzle selected!");
            parent.showGame();
            return;
        }

        // Title
        puzzleTitleLabel = new JLabel("SOLVE THE PUZZLE", JLabel.CENTER);
        puzzleTitleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        puzzleTitleLabel.setForeground(Color.WHITE);
        puzzleTitleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        // Description
        puzzleDescriptionArea = new JTextArea(currentPuzzle.getDescription());
        puzzleDescriptionArea.setEditable(false);
        puzzleDescriptionArea.setLineWrap(true);
        puzzleDescriptionArea.setWrapStyleWord(true);
        puzzleDescriptionArea.setBackground(new Color(60, 60, 110));
        puzzleDescriptionArea.setForeground(Color.WHITE);
        puzzleDescriptionArea.setFont(new Font("Arial", Font.PLAIN, 16));
        puzzleDescriptionArea.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Input Panel (varies by puzzle type)
        inputPanel = new JPanel();
        inputPanel.setBackground(new Color(40, 40, 80));
        createInputComponents();

        // Hint Area
        hintArea = new JTextArea(3, 40);
        hintArea.setEditable(false);
        hintArea.setLineWrap(true);
        hintArea.setWrapStyleWord(true);
        hintArea.setBackground(new Color(50, 50, 90));
        hintArea.setForeground(Color.YELLOW);
        hintArea.setFont(new Font("Arial", Font.ITALIC, 12));
        hintArea.setText("Hint: " + currentPuzzle.getHint());
        hintArea.setBorder(BorderFactory.createTitledBorder("Hint"));

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(new Color(50, 50, 100));

        useItemButton = new JButton("Use Item");
        useItemButton.setFont(new Font("Arial", Font.BOLD, 14));
        useItemButton.setBackground(new Color(80, 120, 80));
        useItemButton.setForeground(Color.WHITE);

        backButton = new JButton("Back to Room");
        backButton.setFont(new Font("Arial", Font.BOLD, 14));
        backButton.setBackground(new Color(80, 80, 140));
        backButton.setForeground(Color.WHITE);

        buttonPanel.add(useItemButton);
        buttonPanel.add(backButton);

        add(puzzleTitleLabel, BorderLayout.NORTH);
        add(new JScrollPane(puzzleDescriptionArea), BorderLayout.CENTER);
        add(inputPanel, BorderLayout.WEST);
        add(new JScrollPane(hintArea), BorderLayout.EAST);
        add(buttonPanel, BorderLayout.SOUTH);

        setupEventHandlers();
        revalidate();
        repaint();
    }

    private void createInputComponents() {
        inputPanel.removeAll();
        inputPanel.setLayout(new BorderLayout());

        if (currentPuzzle instanceof CodePuzzle) {
            createCodePuzzleInput();
        } else if (currentPuzzle instanceof RiddlePuzzle) {
            createRiddlePuzzleInput();
        } else if (currentPuzzle instanceof LockPuzzle) {
            createLockPuzzleInput();
        }
    }

    private void createCodePuzzleInput() {
        CodePuzzle codePuzzle = (CodePuzzle) currentPuzzle;

        JLabel inputLabel = new JLabel("Enter your answer:");
        inputLabel.setForeground(Color.WHITE);
        inputLabel.setFont(new Font("Arial", Font.BOLD, 14));

        JTextField answerField = new JTextField(20);
        answerField.setFont(new Font("Arial", Font.PLAIN, 14));

        JLabel attemptsLabel = new JLabel("Attempts: " + codePuzzle.getCurrentAttempts() +
                "/" + codePuzzle.getRemainingAttempts());
        attemptsLabel.setForeground(codePuzzle.hasAttemptsLeft() ? Color.GREEN : Color.RED);
        attemptsLabel.setFont(new Font("Arial", Font.BOLD, 12));

        submitButton = new JButton("Submit Answer");
        submitButton.setBackground(new Color(80, 120, 80));
        submitButton.setForeground(Color.WHITE);

        submitButton.addActionListener(e -> {
            String answer = answerField.getText().trim();
            if (answer.isEmpty()) {
                parent.showMessage("Please enter an answer!");
                return;
            }

            if (codePuzzle.validateCode(answer)) {
                // Puzzle solved!
                game.getCurrentPlayer().earnCoins(currentPuzzle.getCoinReward());
                parent.showMessage("Correct! You earned " + currentPuzzle.getCoinReward() + " coins!");
                parent.showGame();
            } else {
                attemptsLabel.setText("Attempts: " + codePuzzle.getCurrentAttempts() +
                        "/" + codePuzzle.getRemainingAttempts());
                attemptsLabel.setForeground(codePuzzle.hasAttemptsLeft() ? Color.YELLOW : Color.RED);

                if (!codePuzzle.hasAttemptsLeft()) {
                    submitButton.setEnabled(false);
                    parent.showMessage("No attempts left! This puzzle is now locked.");
                } else {
                    parent.showMessage("Incorrect! Try again. Attempts left: " +
                            codePuzzle.getRemainingAttempts());
                }
            }
        });

        JPanel inputComponents = new JPanel(new GridLayout(4, 1, 10, 10));
        inputComponents.setBackground(new Color(40, 40, 80));
        inputComponents.add(inputLabel);
        inputComponents.add(answerField);
        inputComponents.add(attemptsLabel);
        inputComponents.add(submitButton);

        inputPanel.add(inputComponents, BorderLayout.NORTH);
        inputPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
    }

    private void createRiddlePuzzleInput() {
        RiddlePuzzle riddlePuzzle = (RiddlePuzzle) currentPuzzle;

        // Update description to show the actual riddle question
        puzzleDescriptionArea.setText(riddlePuzzle.getQuestion());

        JLabel inputLabel = new JLabel("Your answer:");
        inputLabel.setForeground(Color.WHITE);
        inputLabel.setFont(new Font("Arial", Font.BOLD, 14));

        JTextField answerField = new JTextField(20);
        answerField.setFont(new Font("Arial", Font.PLAIN, 14));

        submitButton = new JButton("Submit Answer");
        submitButton.setBackground(new Color(80, 120, 80));
        submitButton.setForeground(Color.WHITE);

        submitButton.addActionListener(e -> {
            String answer = answerField.getText().trim();
            if (answer.isEmpty()) {
                parent.showMessage("Please enter an answer!");
                return;
            }

            if (riddlePuzzle.checkAnswer(answer)) {
                game.getCurrentPlayer().earnCoins(currentPuzzle.getCoinReward());
                parent.showMessage("Correct! You earned " + currentPuzzle.getCoinReward() + " coins!");
                parent.showGame();
            } else {
                parent.showMessage("Incorrect! Try again.");
            }
        });

        JPanel inputComponents = new JPanel(new GridLayout(3, 1, 10, 10));
        inputComponents.setBackground(new Color(40, 40, 80));
        inputComponents.add(inputLabel);
        inputComponents.add(answerField);
        inputComponents.add(submitButton);

        inputPanel.add(inputComponents, BorderLayout.NORTH);
        inputPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
    }

    private void createLockPuzzleInput() {
        LockPuzzle lockPuzzle = (LockPuzzle) currentPuzzle;

        JLabel infoLabel = new JLabel("<html>This is a lock puzzle.<br>You need a matching key item to solve it.</html>");
        infoLabel.setForeground(Color.WHITE);
        infoLabel.setFont(new Font("Arial", Font.PLAIN, 14));

        JLabel lockInfoLabel = new JLabel("Lock Color: " + lockPuzzle.getLockColor());
        lockInfoLabel.setForeground(Color.YELLOW);
        lockInfoLabel.setFont(new Font("Arial", Font.BOLD, 14));

        submitButton = new JButton("Use Key from Inventory");
        submitButton.setBackground(new Color(80, 120, 80));
        submitButton.setForeground(Color.WHITE);

        submitButton.addActionListener(e -> {
            // Show inventory selection for keys only
            showKeySelectionDialog(lockPuzzle);
        });

        JPanel inputComponents = new JPanel(new GridLayout(3, 1, 10, 10));
        inputComponents.setBackground(new Color(40, 40, 80));
        inputComponents.add(infoLabel);
        inputComponents.add(lockInfoLabel);
        inputComponents.add(submitButton);

        inputPanel.add(inputComponents, BorderLayout.NORTH);
        inputPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
    }

    private void showKeySelectionDialog(LockPuzzle lockPuzzle) {
        Player player = game.getCurrentPlayer();
        java.util.List<GachaItem> keys = player.getInventory().stream()
                .filter(item -> item instanceof KeyItem)
                .toList();

        if (keys.isEmpty()) {
            parent.showMessage("You don't have any keys in your inventory!");
            return;
        }

        String[] keyNames = keys.stream()
                .map(item -> item.getName() + " (" + ((KeyItem) item).getKeyColor() + ")")
                .toArray(String[]::new);

        String selected = (String) JOptionPane.showInputDialog(
                this,
                "Select a key to use:",
                "Use Key",
                JOptionPane.QUESTION_MESSAGE,
                null,
                keyNames,
                keyNames[0]
        );

        if (selected != null) {
            int selectedIndex = java.util.Arrays.asList(keyNames).indexOf(selected);
            KeyItem selectedKey = (KeyItem) keys.get(selectedIndex);

            try {
                if (player.useItem(selectedKey, lockPuzzle)) {
                    player.earnCoins(currentPuzzle.getCoinReward());
                    parent.showMessage("Puzzle solved! You earned " + currentPuzzle.getCoinReward() + " coins!");
                    parent.showGame();
                }
            } catch (WrongItemException ex) {
                parent.showMessage("This key doesn't work on this lock!");
            }
        }
    }

    private void setupEventHandlers() {
        if (backButton != null) {
            backButton.addActionListener(e -> parent.showGame());
        }

        if (useItemButton != null) {
            useItemButton.addActionListener(e -> showItemSelectionDialog());
        }
    }

    private void showItemSelectionDialog() {
        Player player = game.getCurrentPlayer();
        java.util.List<GachaItem> tools = player.getInventory().stream()
                .filter(item -> item instanceof ToolItem)
                .toList();

        if (tools.isEmpty()) {
            parent.showMessage("You don't have any tools in your inventory!");
            return;
        }

        String[] toolNames = tools.stream()
                .map(item -> item.getName() + " (" + ((ToolItem) item).getToolType() + ")")
                .toArray(String[]::new);

        String selected = (String) JOptionPane.showInputDialog(
                this,
                "Select a tool to use:",
                "Use Tool",
                JOptionPane.QUESTION_MESSAGE,
                null,
                toolNames,
                toolNames[0]
        );

        if (selected != null) {
            int selectedIndex = java.util.Arrays.asList(toolNames).indexOf(selected);
            ToolItem selectedTool = (ToolItem) tools.get(selectedIndex);

            try {
                if (player.useItem(selectedTool, currentPuzzle)) {
                    parent.showMessage("Tool used successfully! Check the hint area.");
                    // Refresh hint area with new information
                    hintArea.setText("Hint: " + currentPuzzle.getHint() +
                            "\n\nTool used: " + selectedTool.getName());
                }
            } catch (WrongItemException ex) {
                parent.showMessage("This tool cannot be used on this puzzle type!");
            }
        }
    }
}