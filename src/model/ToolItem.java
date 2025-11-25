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
        if(usesRemaining <= 0) return false;

        boolean wasUsed = false;
        //Different tools work on different puzzles
        if(puzzle instanceof LockPuzzle && "lockpick".equals(toolType)){
            //Lockpick has a chance to solve any lock
            if(Math.random() > 0.5) {
                ((LockPuzzle)puzzle).markSolved();
                wasUsed = true;
            }
        } else if(puzzle instanceof CodePuzzle && "decoder".equals(toolType)){
            //Decoder tool - mark as used but doesn't auto solve
            wasUsed = true;
        } else if (puzzle instanceof RiddlePuzzle && "hintbook".equals(toolType)) {
            //Hint book tol - mark as used but doesn't auto solve
            wasUsed = true;
        }

        if(wasUsed) usesRemaining--;

        return wasUsed;
    }

    public String getToolType(){return toolType;}
    public int getUsesRemaining(){return usesRemaining;}
}
