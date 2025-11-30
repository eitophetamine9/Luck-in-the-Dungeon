package Panels;

import main.MainApplication;
import model.GameManager;
import model.Puzzle;
import model.GachaItem;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class PuzzlePanel extends JPanel {
    private JPanel puzzlePanel;
    private JComboBox<String> puzzleSelector;
    private JLabel titleLabel;
    private JTextArea puzzleDesc;
    private JScrollPane descScroll;
    private JButton solveButton;
    private JTextField answerField;
    private JLabel answerLabel;
    private JTextArea hintTextArea;
    private JButton useItemButton;
    private JComboBox<String> itemSelector;
    private JLabel toolsLabel;
    private JButton backButton;

    private MainApplication mainApp;
    private GameManager game;
    private Puzzle currentPuzzle;

    public PuzzlePanel(MainApplication mainApp, GameManager game) {
        this.mainApp = mainApp;
        this.game = game;
        initializePanel();
        setupEventHandlers();
    }

    private void initializePanel() {
        setLayout(new BorderLayout());
        add(puzzlePanel, BorderLayout.CENTER);

        // Configure text areas
        puzzleDesc.setEditable(false);
        puzzleDesc.setLineWrap(true);
        puzzleDesc.setWrapStyleWord(true);

        hintTextArea.setEditable(false);
        hintTextArea.setLineWrap(true);
        hintTextArea.setWrapStyleWord(true);
    }

    private void setupEventHandlers() {
        // When puzzle selection changes
        puzzleSelector.addActionListener(e -> selectPuzzle());

        // Solve button
        solveButton.addActionListener(e -> solvePuzzle());

        // Use item button
        useItemButton.addActionListener(e -> useItem());

        // Back to game
        backButton.addActionListener(e -> mainApp.showGame());
    }

    private void selectPuzzle() {
        int index = puzzleSelector.getSelectedIndex();
        List<Puzzle> puzzles = game.getAvailablePuzzles();

        if (index >= 0 && index < puzzles.size()) {
            currentPuzzle = puzzles.get(index);
            updatePuzzleDisplay();
        }
    }

    private void updatePuzzleDisplay() {
        if (currentPuzzle != null) {
            puzzleDesc.setText(currentPuzzle.getDescription());
            hintTextArea.setText(currentPuzzle.getHint());
        }
    }

    private void solvePuzzle() {
        if (currentPuzzle == null) {
            mainApp.showMessage("Please select a puzzle first.");
            return;
        }

        String answer = answerField.getText().trim();
        if (answer.isEmpty()) {
            mainApp.showMessage("Please enter an answer.");
            return;
        }

        // For now - just show message
        mainApp.showMessage("Solving puzzle: " + answer);
        answerField.setText("");
    }

    private void useItem() {
        if (currentPuzzle == null) {
            mainApp.showMessage("Please select a puzzle first.");
            return;
        }

        int index = itemSelector.getSelectedIndex();
        if (index < 0) {
            mainApp.showMessage("Please select an item to use.");
            return;
        }

        mainApp.showMessage("Using item on puzzle...");
    }

    public void refresh() {
        // Update puzzle list
        puzzleSelector.removeAllItems();
        for (Puzzle puzzle : game.getAvailablePuzzles()) {
            puzzleSelector.addItem(puzzle.getDescription());
        }

        // Update item list
        itemSelector.removeAllItems();
        for (GachaItem item : game.getCurrentPlayer().getInventory()) {
            itemSelector.addItem(item.getName());
        }

        // Reset current puzzle
        if (!game.getAvailablePuzzles().isEmpty()) {
            puzzleSelector.setSelectedIndex(0);
            selectPuzzle();
        } else {
            currentPuzzle = null;
            puzzleDesc.setText("No puzzles available in this room.");
            hintTextArea.setText("");
        }
    }
}