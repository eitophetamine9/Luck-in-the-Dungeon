package model;

import exceptions.WrongItemException;

import java.util.ArrayList;
import java.util.List;

public class Player {
    private String name;
    private int coins;
    private List<GachaItem> inventory;
    private int totalPulls;
    private int puzzlesSolved;

    public Player(String name){
        this.name = name;
        this.coins = 100;
        this.inventory = new ArrayList<>();
        this.totalPulls = 0;
        this.puzzlesSolved = 0;
    }

    public void incrementTotalPulls() {
        this.totalPulls++;
    }

    public void setCoins(int coins) {
        this.coins = coins;
    }

    public void earnCoins(int amount){
        this.coins += amount;
    }

    public boolean spendCoins(int amount){
        if(this.coins >= amount){
            this.coins -= amount;
            return true;
        }
        return false;
    }

    public void addItem(GachaItem item){
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
