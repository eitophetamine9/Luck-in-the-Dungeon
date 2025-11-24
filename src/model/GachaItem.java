package model;

public abstract class GachaItem {
    protected String name;
    protected String description;
    protected Rarity rarity;
    protected ItemType itemType;

    public GachaItem(String name, String description, Rarity rarity, ItemType itemType){
        this.name = name;
        this.description = description;
        this.rarity = rarity;
        this.itemType = itemType;
    }

    public abstract boolean use(Puzzle puzzle);

    public String getName() {return name;}

    public ItemType getItemType() {return itemType;}

    public Rarity getRarity() {return rarity;}

    public String getDescription() {return description;}
}
