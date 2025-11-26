package model;

import exceptions.InventoryFullException;
import exceptions.WrongItemException;

import java.util.ArrayList;
import java.util.List;

public class Player {
    private final String name;
    private int coins;
    private final List<GachaItem> inventory;
    private int totalPulls;
    private int puzzlesSolved;
    private static final int MAX_INVENTORY_SIZE = 20;
    private int totalCoinsEarned;
    private int totalCoinsSpent;
    private int roomsCompleted;

    public Player(String name){
        this.name = name;
        this.coins = 100;
        this.inventory = new ArrayList<>();
        this.totalPulls = 0;
        this.puzzlesSolved = 0;
        this.totalCoinsEarned = 0;
        this.totalCoinsSpent = 0;
        this.roomsCompleted = 0;
    }

    public void setPuzzlesSolved(int puzzlesSolved) {
        this.puzzlesSolved = puzzlesSolved;
    }

    public void setRoomsCompleted(int roomsCompleted) {
        this.roomsCompleted = roomsCompleted;
    }

    public void setTotalPulls(int totalPulls) {
        this.totalPulls = totalPulls;
    }

    public void setTotalCoinsEarned(int totalCoinsEarned) {
        this.totalCoinsEarned = totalCoinsEarned;
    }

    public void setTotalCoinsSpent(int totalCoinsSpent) {
        this.totalCoinsSpent = totalCoinsSpent;
    }

    public int getTotalCoinsEarned() { return totalCoinsEarned; }
    public int getTotalCoinsSpent() { return totalCoinsSpent; }
    public int getRoomsCompleted() { return roomsCompleted; }
    public int getMaxInventorySize() { return MAX_INVENTORY_SIZE; }
    public int getCurrentInventorySize() { return inventory.size(); }

    public void recordCoinsEarned(int amount) {
        this.totalCoinsEarned += amount;
    }

    public void recordCoinsSpent(int amount) {
        this.totalCoinsSpent += amount;
    }

    public void incrementRoomsCompleted() {
        this.roomsCompleted++;
    }

    public void incrementTotalPulls() {
        this.totalPulls++;
    }

    public void setCoins(int coins) {
        this.coins = coins;
    }

    public void earnCoins(int amount) {
        this.coins += amount;
        recordCoinsEarned(amount); // Track stats
    }

    public boolean spendCoins(int amount) {
        if (this.coins >= amount) {
            this.coins -= amount;
            recordCoinsSpent(amount); // Track stats
            return true;
        }
        return false;
    }

    public void addItem(GachaItem item) throws InventoryFullException {
        if (inventory.size() >= MAX_INVENTORY_SIZE) {
            throw new InventoryFullException(inventory.size(), MAX_INVENTORY_SIZE);
        }
        this.inventory.add(item);
    }

    public boolean useItem(GachaItem item, Puzzle puzzle) throws WrongItemException {
        if (!inventory.contains(item))
            throw new WrongItemException("Item '" + item.getName() + "' is not in your inventory!");

        boolean success = item.use(puzzle);

        if(!success) {
            throw new WrongItemException("The " + item.getName() + " cannot be used on this type of puzzle!");
        } else {
            puzzlesSolved++;
        }

        return true;
    }

    public List<GachaItem> getInventory() {
        return new ArrayList<>(inventory);
    }

    public int getCoinBalance() {return coins;}
    public int getTotalPulls() {return totalPulls;}
    public int getPuzzlesSolved() {return puzzlesSolved;}
    public String getName() {return name;}
}
