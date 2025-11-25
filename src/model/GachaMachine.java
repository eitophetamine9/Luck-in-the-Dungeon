package model;

import exceptions.NotEnoughCoinsException;

import java.util.*;

public class GachaMachine {
    private String machineName;
    private int pullCost;
    private List<GachaItem> itemPool;
    private int pityCounter;
    private Map<Rarity, Double> rateTable;
    private static final int PITY_THRESHOLD = 10; // Epic every 10 pulls
    private int pullCounter; // Track pulls since last epic

    public GachaMachine(String machineName, int pullCost){
        this.machineName = machineName;
        this.pullCost = pullCost;
        this.itemPool = new ArrayList<>();
        this.pityCounter = 0;
        this.rateTable = new HashMap<>();
        initilizeRates();
    }

    private void initilizeRates(){
        rateTable.put(Rarity.COMMON, 0.70); // Common 70% roll
        rateTable.put(Rarity.RARE, 0.25); // Rare 25% roll
        rateTable.put(Rarity.EPIC, 0.05); // Epic 5% roll
    }

    public GachaItem pull(Player player) throws NotEnoughCoinsException {

        if(!player.spendCoins(pullCost)) throw new NotEnoughCoinsException(pullCost, player.getCoinBalance());


        Random random = new Random();
        double roll = random.nextDouble();

        //Determine rarity based on rates
        Rarity rolledRarity = Rarity.COMMON;
        double cumulative = 0.0;

        for(Map.Entry<Rarity, Double> entry : rateTable.entrySet()){
            cumulative += entry.getValue();
            if(roll <= cumulative) {
                rolledRarity = entry.getKey();
                break;
            }
        }

        pullCounter++;
        if (pullCounter >= PITY_THRESHOLD) {
            rolledRarity = Rarity.EPIC; // Force epic
            pullCounter = 0; // Reset counter
            System.out.println("🎉 Pity system activated! Guaranteed EPIC!");
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
            GachaItem res = eligibleItems.get(random.nextInt(eligibleItems.size()));
            pityCounter++;
            player.incrementTotalPulls(); // ✅ Fixed - calls the new method
            return res;
        }

        return null; // will never happen if itemPool is properly initialized
    }

    public boolean canPull(Player player) {return player.getCoinBalance() >= pullCost;}

    public int getPullCost() {return pullCost;}

    public Map<Rarity, Double> getRateTable(){return new HashMap<>(rateTable);}

    // Methods to add items to the pool
    public void addItemToPool(GachaItem item){
        itemPool.add(item);
    }

    public String getMachineName(){return machineName;}
}
