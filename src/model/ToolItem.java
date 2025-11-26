package model;

import model.LockPuzzle; // Needed for instanceof check

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

        // All tools provide hints instead of auto-solving
        if(puzzle instanceof LockPuzzle && "lockpick".equals(toolType)){
            LockPuzzle lock = (LockPuzzle) puzzle;
            System.out.println("🔓 Lockpick reveals: This lock requires a " +
                    lock.getLockColor() + " key");
            wasUsed = true;

        } else if(puzzle instanceof CodePuzzle && "decoder".equals(toolType)){
            CodePuzzle codePuzzle = (CodePuzzle) puzzle;
            String solution = codePuzzle.getSolution();
            if(solution.length() >= 2) {
                String hint = solution.substring(0, 2) + "..." +
                        (solution.length() > 4 ? (solution.length()-2) + " more chars" : "");
                System.out.println("🔍 Decoder reveals: Code starts with: " + hint);
            }
            wasUsed = true;

        } else if (puzzle instanceof RiddlePuzzle && "hintbook".equals(toolType)) {
            RiddlePuzzle riddle = (RiddlePuzzle) puzzle;
            System.out.println("📖 Hintbook reveals: " + riddle.getHint());
            wasUsed = true;

        } else if (puzzle instanceof CodePuzzle && "alchemy".equals(toolType)) {
            CodePuzzle alchemyPuzzle = (CodePuzzle) puzzle;
            System.out.println("⚗️ Alchemy tool reveals: The symbols represent chemical elements");
            wasUsed = true;

        } else if (puzzle instanceof CodePuzzle && "astronomy".equals(toolType)) {
            CodePuzzle astronomyPuzzle = (CodePuzzle) puzzle;
            System.out.println("🔭 Astronomy tool reveals: These are zodiac constellation symbols");
            wasUsed = true;
        }

        if (!wasUsed) {
            System.out.println("This " + toolType + " cannot help with this type of puzzle.");
        } else {
            usesRemaining--; // Decrement uses when successfully used
        }

        return wasUsed;
    }

    public String getToolType(){return toolType;}
    public int getUsesRemaining(){return usesRemaining;}
}