package model;

import exceptions.NotEnoughCoinsException;
import java.io.Serializable;

import java.util.*;

public class GachaMachine implements Serializable {
    private static final long serialVersionUID = 1L;
    private String machineName;
    private int pullCost;
    private List<GachaItem> itemPool;
    private Map<Rarity, Double> rateTable;
    private static final int PITY_THRESHOLD = 10; // Epic every 10 pulls
    private int pullsSinceLastEpic; // Track pulls since last epic

    // ✅ ADD: Persistent pity counter that survives across game sessions
    private int totalPullsWithoutEpic;

    public GachaMachine(String machineName, int pullCost){
        this.machineName = machineName;
        this.pullCost = pullCost;
        this.itemPool = new ArrayList<>();
        this.rateTable = new HashMap<>();
        this.pullsSinceLastEpic = 0;
        this.totalPullsWithoutEpic = 0; // ✅ INITIALIZE persistent counter
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
        totalPullsWithoutEpic++; // ✅ ADD: Track persistent pity counter

        // Pity system: Guaranteed epic every 10 pulls
        if (totalPullsWithoutEpic >= PITY_THRESHOLD) {
            totalPullsWithoutEpic = 0; // ✅ Reset persistent counter
            pullsSinceLastEpic = 0;    // Reset session counter
            System.out.println("🎉 Pity system activated! Guaranteed EPIC!");
            GachaItem pityItem = getGuaranteedEpic();
            if (pityItem != null) {
                player.incrementTotalPulls();
                return pityItem;
            }
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

            // ✅ ADD: Reset pity counters if we get an epic naturally
            if (result.getRarity() == Rarity.EPIC) {
                totalPullsWithoutEpic = 0;
                pullsSinceLastEpic = 0;
                System.out.println("✨ Natural EPIC! Resetting pity counter.");
            }

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

    // ✅ ADD: Get total pity counter
    public int getTotalPullsWithoutEpic() {
        return totalPullsWithoutEpic;
    }

    // ✅ ADD: Get pity progress string
    public String getPityProgress() {
        int pullsLeft = PITY_THRESHOLD - totalPullsWithoutEpic;

        // Create visual progress bar
        StringBuilder progressBar = new StringBuilder();
        int filled = (int) ((double) totalPullsWithoutEpic / PITY_THRESHOLD * 10);

        for (int i = 0; i < 10; i++) {
            if (i < filled) {
                progressBar.append("█");
            } else {
                progressBar.append("░");
            }
        }

        return String.format("Pity: %s (%d/10) | Next epic in: %d pulls",
                progressBar.toString(), totalPullsWithoutEpic, pullsLeft);
    }

    // ✅ ADD: Get simple pity string for display
    public String getSimplePityString() {
        int pullsLeft = PITY_THRESHOLD - totalPullsWithoutEpic;
        return String.format("📊 Pity: %d/10", totalPullsWithoutEpic);
    }

    // ✅ ADD: Setter for loading saved games
    public void setTotalPullsWithoutEpic(int pulls) {
        this.totalPullsWithoutEpic = pulls;
        // Also update session counter to match for consistency
        this.pullsSinceLastEpic = Math.min(pulls, PITY_THRESHOLD - 1);
    }

    public String getMachineName(){
        return machineName;
    }
}