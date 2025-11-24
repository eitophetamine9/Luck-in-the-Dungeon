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
        // Simple implementation - just return true for now
        // We'll add proper puzzle solving logic later
        System.out.println("Using " + name + " on puzzle...");
        return true;
    }

    public String getKeyColor() { return keyColor; }
    public boolean isMasterKey() { return isMasterKey; }
}
