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
        if(usesRemaining > 0){
            boolean wasUsed = puzzle.attemptSolve(this);
            if(wasUsed) usesRemaining--;

            return wasUsed;
        }
        return false;
    }

    public String getToolType(){return toolType;}
    public int getUsesRemaining(){return usesRemaining;}
}
