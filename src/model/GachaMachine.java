package model;

import exceptions.NotEnoughCoinsException;

import java.util.*;

public class GachaMachine {
    private String machineName;
    private int pullCost;
    private List<GachaItem> itemPool;
    private Map<Rarity, Double> rateTable;
    private static final int PITY_THRESHOLD = 10; // Epic every 10 pulls
    private int pullsSinceLastEpic; // Track pulls since last epic

    public GachaMachine(String machineName, int pullCost){
        this.machineName = machineName;
        this.pullCost = pullCost;
        this.itemPool = new ArrayList<>();
        this.rateTable = new HashMap<>();
        this.pullsSinceLastEpic = 0; // PROPERLY INITIALIZED
        initializeRates();
    }

    private void initializeRates(){
        rateTable.put(Rarity.COMMON, 0.60); // Reduced from 70%
        rateTable.put(Rarity.RARE, 0.30);   // Increased from 25%
        rateTable.put(Rarity.EPIC, 0.10);   // Increased from 5%
    }

    public GachaItem pull(Player player) throws NotEnoughCoinsException {
        if(!player.spendCoins(pullCost)) {
            throw new NotEnoughCoinsException(pullCost, player.getCoinBalance());
        }

        pullsSinceLastEpic++; // Track every pull

        // Pity system: Guaranteed epic every 10 pulls
        if (pullsSinceLastEpic >= PITY_THRESHOLD) {
            pullsSinceLastEpic = 0; // Reset counter
            System.out.println("🎉 Pity system activated! Guaranteed EPIC!");
            return getGuaranteedEpic();
        }

        Random random = new Random();
        double roll = random.nextDouble();

        // Determine rarity based on rates
        Rarity rolledRarity = Rarity.COMMON;
        double cumulative = 0.0;

        for(Map.Entry<Rarity, Double> entry : rateTable.entrySet()){
            cumulative += entry.getValue();
            if(roll <= cumulative) {
                rolledRarity = entry.getKey();
                break;
            }
        }

        // Filter items by rolled rarity
        List<GachaItem> eligibleItems = new ArrayList<>();
        for(GachaItem item : itemPool){
            if(item.getRarity() == rolledRarity) eligibleItems.add(item);
        }

        // If no items of that rarity, fallback to common
        if(eligibleItems.isEmpty()) {
            for(GachaItem item : itemPool){
                if(item.getRarity() == Rarity.COMMON) eligibleItems.add(item);
            }
        }

        // Return random item from eligible ones
        if(!eligibleItems.isEmpty()){
            GachaItem result = eligibleItems.get(random.nextInt(eligibleItems.size()));
            player.incrementTotalPulls();
            return result;
        }

        return null; // will never happen if itemPool is properly initialized
    }

    private GachaItem getGuaranteedEpic() {
        List<GachaItem> epicItems = new ArrayList<>();
        for(GachaItem item : itemPool){
            if(item.getRarity() == Rarity.EPIC) epicItems.add(item);
        }

        if (!epicItems.isEmpty()) {
            Random random = new Random();
            return epicItems.get(random.nextInt(epicItems.size()));
        }

        // Fallback to any item if no epics in pool
        if (!itemPool.isEmpty()) {
            Random random = new Random();
            return itemPool.get(random.nextInt(itemPool.size()));
        }

        return null;
    }

    public boolean canPull(Player player) {
        return player.getCoinBalance() >= pullCost;
    }

    public int getPullCost() {
        return pullCost;
    }

    public Map<Rarity, Double> getRateTable(){
        return new HashMap<>(rateTable);
    }

    // Methods to add items to the pool
    public void addItemToPool(GachaItem item){
        itemPool.add(item);
    }

    public int getPullsSinceLastEpic() {
        return pullsSinceLastEpic;
    }

    public String getMachineName(){
        return machineName;
    }
}