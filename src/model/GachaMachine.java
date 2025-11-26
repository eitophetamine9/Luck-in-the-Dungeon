package model;

import exceptions.NotEnoughCoinsException;
import java.util.*;

public class GachaMachine {
    private String machineName;
    private int pullCost;
    private List<GachaItem> itemPool;
    private Map<Rarity, Double> rateTable;
    private static final int PITY_THRESHOLD = 10;
    private int pullsSinceLastEpic;

    public GachaMachine(String machineName, int pullCost){
        this.machineName = machineName;
        this.pullCost = pullCost;
        this.itemPool = new ArrayList<>();
        this.rateTable = new HashMap<>();
        this.pullsSinceLastEpic = 0;
        initializeRates();
    }

    private void initializeRates(){
        rateTable.put(Rarity.COMMON, 0.70);
        rateTable.put(Rarity.RARE, 0.25);
        rateTable.put(Rarity.EPIC, 0.05);
    }

    public GachaItem pull(Player player) throws NotEnoughCoinsException {
        if(!player.spendCoins(pullCost))
            throw new NotEnoughCoinsException(pullCost, player.getCoinBalance());

        pullsSinceLastEpic++;
        Rarity rolledRarity = Rarity.COMMON; // ✅ SAFE DEFAULT

        // Pity system logic
        if (pullsSinceLastEpic >= PITY_THRESHOLD) {
            rolledRarity = Rarity.EPIC;
            pullsSinceLastEpic = 0;
            System.out.println("🎉 Pity system activated! Guaranteed EPIC!");
        } else {
            // Normal probability calculation
            Random random = new Random();
            double roll = random.nextDouble();
            double cumulative = 0.0;

            for(Map.Entry<Rarity, Double> entry : rateTable.entrySet()){
                cumulative += entry.getValue();
                if(roll <= cumulative) {
                    rolledRarity = entry.getKey();
                    break;
                }
            }

            // Reset if epic pulled naturally
            if (rolledRarity == Rarity.EPIC) {
                pullsSinceLastEpic = 0;
            }
        }

        // Filter items by rolled rarity
        List<GachaItem> eligibleItems = new ArrayList<>();
        for(GachaItem item : itemPool){
            if(item.getRarity() == rolledRarity) eligibleItems.add(item);
        }

        // Fallback to common if no items
        if(eligibleItems.isEmpty()) {
            for(GachaItem item : itemPool){
                if(item.getRarity() == Rarity.COMMON) eligibleItems.add(item);
            }
        }

        if(!eligibleItems.isEmpty()){
            GachaItem result = eligibleItems.get(new Random().nextInt(eligibleItems.size()));
            player.incrementTotalPulls();
            return result;
        }

        return null;
    }

    public int getPullsSinceLastEpic() {
        return pullsSinceLastEpic;
    }

    public boolean canPull(Player player) {return player.getCoinBalance() >= pullCost;}
    public int getPullCost() {return pullCost;}
    public Map<Rarity, Double> getRateTable(){return new HashMap<>(rateTable);}
    public void addItemToPool(GachaItem item){itemPool.add(item);}
    public String getMachineName(){return machineName;}
}