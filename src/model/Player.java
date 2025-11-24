package model;

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

    public boolean useItem(GachaItem item, Puzzle puzzle){
        return item.use(puzzle);
    }

    public List<GachaItem> getInventory() {
        return new ArrayList<>(inventory);
    }

    public int getCoinBalance() {return coins;}
    public int getTotalPulls() {return totalPulls;}
    public int getPuzzlesSolved() {return puzzlesSolved;}
    public String getName() {return name;}
}
