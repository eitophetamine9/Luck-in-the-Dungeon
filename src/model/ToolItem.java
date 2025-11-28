package model;

public class ToolItem extends GachaItem {
    private String toolType;
    private int usesRemaining;

    public ToolItem(String name, String description, Rarity rarity,
                    String toolType, int usesRemaining){
        super(name,description,rarity, ItemType.TOOL);
        this.toolType = toolType;
        this.usesRemaining = usesRemaining;
    }

    @Override
    public boolean use(Puzzle puzzle){
        if(usesRemaining <= 0) {
            System.out.println("This tool is broken and cannot be used!");
            return false;
        }

        boolean wasUsed = false;

        if(puzzle instanceof LockPuzzle && "lockpick".equals(toolType)){
            LockPuzzle lock = (LockPuzzle) puzzle;
            System.out.println("🔓 Lockpick reveals: This lock requires a " +
                    lock.getLockColor() + " key");
            wasUsed = true;

        } else if(puzzle instanceof CodePuzzle && "decoder".equals(toolType)){
            CodePuzzle codePuzzle = (CodePuzzle) puzzle;
            String description = codePuzzle.getDescription();

            // FIXED: Correct cipher translations
            if (description.contains("▼") && description.contains("▲")) {
                // Symbol cipher puzzle - CORRECTED MAPPING
                String decodedMessage = decodeSymbolCipher(description);
                System.out.println("🔍 Cipher Solver reveals the translation: " + decodedMessage);
            }
            else if (description.contains("Color sequence")) {
                // Color pattern puzzle
                String patternExplanation = decodeColorPattern(description);
                System.out.println("🎨 Pattern Decoder reveals: " + patternExplanation);
            }
            else if (description.contains("binary")) {
                // Binary puzzle
                String binaryGuide = guideBinaryConversion(description);
                System.out.println("💻 Binary Guide explains: " + binaryGuide);
            }
            else if (description.contains("Egyptian math") || description.contains("τ") || description.contains("ε")) {
                // Hieroglyphic math
                String mathGuide = guideHieroglyphicMath(description);
                System.out.println("📐 Math Solver explains: " + mathGuide);
            }
            else if (description.contains("Scale balance") || description.contains("kg")) {
                // Weight balance
                String balanceGuide = guideWeightBalance(description);
                System.out.println("⚖️ Balance Guide explains: " + balanceGuide);
            }
            else if (description.contains("1, 1, 2, 3, 5, 8")) {
                // Fibonacci sequence
                String sequenceGuide = guideFibonacci(description);
                System.out.println("🔢 Pattern Guide explains: " + sequenceGuide);
            }
            else {
                // Generic number/sequence puzzle
                String sequenceHint = guideNumberSequence(description);
                System.out.println("🔍 Decoder provides hint: " + sequenceHint);
            }
            wasUsed = true;

        } else if (puzzle instanceof RiddlePuzzle && "hintbook".equals(toolType)) {
            RiddlePuzzle riddle = (RiddlePuzzle) puzzle;
            System.out.println("📖 Hintbook provides detailed guidance: " + getRiddleGuidance(riddle));
            wasUsed = true;
        }

        if (wasUsed) {
            usesRemaining--;
            if (usesRemaining <= 0) {
                System.out.println("This " + toolType + " has broken and can no longer be used!");
            }
        } else {
            System.out.println("This " + toolType + " cannot help with this type of puzzle.");
        }

        return wasUsed;
    }

    // FIXED: Correct cipher mapping
    private String decodeSymbolCipher(String description) {
        return "Cipher Translation Key:\n" +
                "▼ = space\n" +
                "▲ = O\n" +
                "V = A\n" +
                "Y = U\n" +
                "S = S\n" +
                "L = L\n" +
                "D = D\n" +
                "T = T\n\n" +
                "Apply to decode: H▼VY▲S▼LVDT▼D▼Y becomes: H A V E  O U  S L O V E D  T O D A Y";
    }

    private String decodeColorPattern(String description) {
        return "The pattern repeats every 4 colors: RED, BLUE, GREEN, YELLOW. After RED, BLUE comes GREEN, YELLOW.";
    }

    private String guideBinaryConversion(String description) {
        return "Each group of 8 bits represents one character. Convert binary to decimal, then use ASCII table:\n" +
                "01001000 = 72 = H, 01100101 = 101 = e, 01101100 = 108 = l, 01101100 = 108 = l, 01101111 = 111 = o";
    }

    private String guideHieroglyphicMath(String description) {
        return "Symbol values: %=5, i=10, c=50, τ=100, τ̄=1000, ε=1\n" +
                "Equation: (τ-τ̄) + (ε-1) × τ̄ = (100-1000) + (1-1) × 1000 = (-900) + (0) × 1000 = -900";
    }

    private String guideWeightBalance(String description) {
        return "For the scale to balance, left side total = right side total:\n" +
                "Left: 5 + 8 + X = 13 + X\n" +
                "Right: 6 + 10 + 7 = 23\n" +
                "Equation: 13 + X = 23";
    }

    private String guideFibonacci(String description) {
        return "This is the Fibonacci sequence! Each number is the sum of the two previous ones:\n" +
                "1 + 1 = 2, 1 + 2 = 3, 2 + 3 = 5, 3 + 5 = 8, 5 + 8 = 13, 8 + 13 = 21, 13 + 21 = 34";
    }

    private String guideNumberSequence(String description) {
        if (description.contains("2, 5, 11, 19, 29, 41")) {
            return "Look at the differences between numbers: 5-2=3, 11-5=6, 19-11=8, 29-19=10, 41-29=12\n" +
                    "The differences are increasing by 2 each time: 3,6,8,10,12,14...\n" +
                    "Next number: 41 + 14 = 55";
        }
        return "Look for patterns in the differences between numbers or multiplication factors.";
    }

    private String getRiddleGuidance(RiddlePuzzle riddle) {
        String question = riddle.getQuestion();

        if (question.contains("three-digit number")) {
            return "Let the digits be A, B, C where:\n" +
                    "A + B + C = 12\n" +
                    "A = 3 × C\n" +
                    "B = A - 2\n" +
                    "Substitute and solve the equations!";
        } else if (question.contains("facing")) {
            return "Track directions step by step:\n" +
                    "Start: NORTH\n" +
                    "RIGHT×3: N→E→S→W\n" +
                    "LEFT×2: W→S→E\n" +
                    "RIGHT×1: E→S\n" +
                    "Final direction: SOUTH";
        }

        return riddle.getHint() + "\n\nThink about the key elements mentioned in the riddle.";
    }

    public String getToolType(){return toolType;}
    public int getUsesRemaining(){return usesRemaining;}
}