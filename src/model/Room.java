package model;

import java.util.ArrayList;
import java.util.List;
import java.io.Serializable;

public class Room implements Serializable{
    private static final long serialVersionUID = 1L;
    private int roomNumber;
    private String name;
    private String description;
    private boolean isLocked;
    private List<Puzzle> puzzles;
    private GachaMachine gachaMachine;

    public Room(int roomNumber, String name, String description){
        this.roomNumber = roomNumber;
        this.name = name;
        this.description = description;
        this.isLocked = true;
        this.puzzles = new ArrayList<>();
        this.gachaMachine = new GachaMachine(name + " Gacha", 20);
    }

    public void unlock(){
        this.isLocked = false;
    }

    public void lock() {
        this.isLocked = true;
    }

    public boolean isComplete() {
        for(Puzzle puzzle : puzzles){
            if(!puzzle.isSolved()) return false;
        }
        return true;
    }

    public List<Puzzle> getAvailablePuzzles() {
        List<Puzzle> available = new ArrayList<>();
        for(Puzzle puzzle : puzzles){
            if(!puzzle.isSolved()) available.add(puzzle);
        }
        return available;
    }

    public void addItemToGacha(GachaItem item){
        this.gachaMachine.addItemToPool(item);
    }

    // Optional: Add method to set room description
    public void setDescription(String description) {
        this.description = description;
    }

    // Optional: Add method to set room name
    public void setName(String name) {
        this.name = name;
    }

    public GachaMachine getGachaMachine(){return gachaMachine;}

    public String getRoomDescription(){return description;}

    public void addPuzzle(Puzzle puzzle){this.puzzles.add(puzzle);}

    public int getRoomNumber(){return roomNumber;}

    public String getName(){return name;}

    public boolean isLocked(){return isLocked;}

    public List<Puzzle> getPuzzles(){
        return new ArrayList<>(puzzles);
    }
}
