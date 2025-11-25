package model;

public class KeyItem extends GachaItem {
    private String keyColor;
    private boolean isMasterKey;

    public KeyItem(String name, String description, Rarity rarity, String keyColor, boolean isMasterKey) {
        super(name, description, rarity, ItemType.KEY);
        this.keyColor = keyColor;
        this.isMasterKey = isMasterKey;
    }

    @Override
    public boolean use(Puzzle puzzle) {
        // PUZZLE WILL BE ADDED SOON
        if(puzzle instanceof LockPuzzle) {
            LockPuzzle lockPuzzle = (LockPuzzle) puzzle;

            if(this.keyColor.equals(lockPuzzle.getLockColor()) || this.isMasterKey)
                return lockPuzzle.attemptSolve(this);
        }
        return false;
    }

    public String getKeyColor() { return keyColor; }
    public boolean isMasterKey() { return isMasterKey; }
}
