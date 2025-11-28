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
    private JButton buyHintButton;

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

        buyHintButton = new JButton("Buy Better Hint (10 coins)");
        buyHintButton.setFont(new Font("Arial", Font.BOLD, 14));
        buyHintButton.setBackground(new Color(120, 80, 80));
        buyHintButton.setForeground(Color.WHITE);


        backButton = new JButton("Back to Room");
        backButton.setFont(new Font("Arial", Font.BOLD, 14));
        backButton.setBackground(new Color(80, 80, 140));
        backButton.setForeground(Color.WHITE);

        buttonPanel.add(useItemButton);
        buttonPanel.add(buyHintButton);
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

        if(buyHintButton != null){
            buyHintButton.addActionListener(e -> purchaseHint());
        }
    }

    private void purchaseHint(){
        Player player = game.getCurrentPlayer();
        if(player.spendCoins(10)){
            String enhancedHint = getEnhancedHint(currentPuzzle);
            hintArea.setText("💰 Premium Hint (Cost: 10 coins):\n" + enhancedHint);
            parent.showMessage("Better hint purchased! Check the hint area");
        } else {
            parent.showMessage("Not enough coins for hint! You need 10 coins");
        }
    }

    private String getEnhancedHint(Puzzle puzzle){
        if(puzzle instanceof CodePuzzle){
            CodePuzzle codePuzzle = (CodePuzzle) puzzle;
            String description = codePuzzle.getDescription();

            if (description.contains("▼") && description.contains("▲")) {
                return "Symbol Cipher Key:\n" +
                        "▼ = space\n" +
                        "▲ = O\n" +
                        "V = A\n" +
                        "Y = U\n" +
                        "Decoded message starts with: H▼VY▲ = H A V E  O\n" +
                        "Full solution: HAVE YOU SOLVED TODAY";
            }
            else if (description.contains("Color sequence")) {
                return "Color Pattern Analysis:\n" +
                        "The sequence repeats every 4 colors: RED, BLUE, GREEN, YELLOW\n" +
                        "Position 7 = GREEN, Position 8 = YELLOW";
            }
            else if (description.contains("binary")) {
                return "Binary Conversion:\n" +
                        "01001000 = H (72)\n" +
                        "01100101 = e (101)\n" +
                        "01101100 = l (108)\n" +
                        "01101100 = l (108)\n" +
                        "01101111 = o (111)\n" +
                        "Solution: HELLO";
            }
            else if (description.contains("2 (+3)â†’5 (+6)â†’11")) {
                return "Number Sequence Pattern:\n" +
                        "Differences: +3, +6, +8, +10, +12, +14...\n" +
                        "41 + 14 = 55\n" +
                        "Solution: 55";
            }
        }
        else if (puzzle instanceof RiddlePuzzle) {
            RiddlePuzzle riddle = (RiddlePuzzle) puzzle;
            return "Enhanced Riddle Guidance:\n" + riddle.getHint() +
                    "\n\nThink carefully about each part of the riddle.";
        }

        return "Detailed analysis: " + puzzle.getHint();
    }

    private void handleToolAutoSolve() {
        if (currentPuzzle.isSolved()) {
            // Puzzle was solved by the tool
            game.getCurrentPlayer().earnCoins(currentPuzzle.getCoinReward());

            // Show success message
            JOptionPane.showMessageDialog(this,
                    "Tool automatically solved the puzzle!\n" +
                            "You earned " + currentPuzzle.getCoinReward() + " coins!",
                    "Puzzle Solved!",
                    JOptionPane.INFORMATION_MESSAGE);

            // Return to game screen
            parent.showGame();
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
                .map(item -> {
                    ToolItem tool = (ToolItem) item;
                    return item.getName() + " (" + tool.getToolType() + ") - " +
                            tool.getUsesRemaining() + " uses left";
                })
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
                boolean wasUsed = player.useItem(selectedTool, currentPuzzle);
                if (wasUsed) {
                    // Check if the puzzle was solved by the tool
                    if (currentPuzzle.isSolved()) {
                        handleToolAutoSolve();
                    } else {
                        // Show the translation/decoding result in the hint area
                        String currentHint = hintArea.getText();
                        String toolResult = getToolResultMessage(selectedTool, currentPuzzle);
                        hintArea.setText(currentHint + "\n\n" + toolResult);

                        parent.showMessage("Tool used successfully! Check the hint area for the translation.");
                    }
                }
            } catch (WrongItemException ex) {
                parent.showMessage("This tool cannot be used on this puzzle type!");
            }
        }
    }

    private String getToolResultMessage(ToolItem tool, Puzzle puzzle) {
        String description = puzzle.getDescription();

        if (description.contains("▼") && description.contains("▲") && "decoder".equals(tool.getToolType())) {
            return "Cipher Translation Guide:\n" +
                    "▼ = space\n" +
                    "▲ = O\n" +
                    "V = A\n" +
                    "Y = U\n" +
                    "L = S\n" +
                    "D = O\n" +
                    "T = D\n\n" +
                    "Apply these substitutions to decode the message!";
        }
        else if (description.contains("Color sequence") && "decoder".equals(tool.getToolType())) {
            return "Pattern Analysis:\n" +
                    "The sequence repeats every 4 colors:\n" +
                    "Position 1: RED, Position 2: BLUE, Position 3: GREEN, Position 4: YELLOW\n" +
                    "Then repeats: Position 5: RED, Position 6: BLUE, Position 7: ?, Position 8: ?";
        }
        else if (description.contains("binary") && "decoder".equals(tool.getToolType())) {
            return "Binary Conversion Guide:\n" +
                    "Each 8-bit group = one character\n" +
                    "Convert binary to decimal, then to ASCII character\n" +
                    "01001000 = 72 = 'H'\n" +
                    "01100101 = 101 = 'e'\n" +
                    "01101100 = 108 = 'l'\n" +
                    "01101100 = 108 = 'l'\n" +
                    "01101111 = 111 = 'o'";
        }
        else if ((description.contains("Egyptian math") || description.contains("τ")) && "decoder".equals(tool.getToolType())) {
            return "Hieroglyphic Math Guide:\n" +
                    "Symbol values: %=5, i=10, c=50, τ=100, τ̄=1000, ε=1\n" +
                    "Solve step by step: (τ-τ̄) + (ε-1) × τ̄";
        }
        else if ((description.contains("Scale balance") || description.contains("kg")) && "decoder".equals(tool.getToolType())) {
            return "Weight Balance Guide:\n" +
                    "Left side total = Right side total\n" +
                    "Left: 5 + 8 + X = 13 + X\n" +
                    "Right: 6 + 10 + 7 = 23\n" +
                    "Equation: 13 + X = 23";
        }
        else if (description.contains("1, 1, 2, 3, 5, 8") && "decoder".equals(tool.getToolType())) {
            return "Sequence Pattern Guide:\n" +
                    "Fibonacci sequence rule:\n" +
                    "Each number = sum of previous two numbers\n" +
                    "1 + 1 = 2, 1 + 2 = 3, 2 + 3 = 5, 3 + 5 = 8, 5 + 8 = 13, 8 + 13 = 21, 13 + 21 = ?";
        }

        return "Tool provided guidance. Check the console for detailed explanation.";
    }
}