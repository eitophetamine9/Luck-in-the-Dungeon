package main;

import model.*;

public class Main {
    public static void main(String[] args) {
        //TEST
        LockPuzzle blueLock = new LockPuzzle("Blue locked door", 50, 1, "blue", "blue");
        KeyItem blueKey = new KeyItem("Blue Key", "Opens blue locks", Rarity.COMMON, "blue", false);

        System.out.println("Testing UML structure:");
        System.out.println("Key: " + blueKey.getName());
        System.out.println("Puzzle: " + blueLock.getDescription());

        boolean result = blueKey.use(blueLock);
        System.out.println("Use result: " + result);
        System.out.println("Puzzle solved: " + blueLock.isSolved());

        // Test tool item
        ToolItem lockpick = new ToolItem("Lockpick", "Picks locks", Rarity.RARE, "lockpick", 3);
        boolean toolResult = lockpick.use(blueLock);
        System.out.println("Tool use result: " + toolResult);
        System.out.println("Tool uses remaining: " + lockpick.getUsesRemaining());
    }
}
