package model;

import exceptions.InventoryFullException;
import exceptions.WrongItemException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private int timeMachinePartsCollected;

    public Player(String name){
        this.name = sanitizePlayerName(name);
        this.coins = 100;
        this.inventory = new ArrayList<>();
        this.totalPulls = 0;
        this.puzzlesSolved = 0;
        this.totalCoinsEarned = 0;
        this.totalCoinsSpent = 0;
        this.roomsCompleted = 0;
        this.timeMachinePartsCollected = 0;
    }

    private String sanitizePlayerName(String name){
        if(name == null || name.trim().isEmpty()) return "Time Traveler";

        String sanitized = name.trim().replaceAll("\\s+", " ");
        if(sanitized.length() > 20) sanitized = sanitized.substring(0, 20);

        return sanitized;
    }

    // === ENHANCED ITEM USAGE (PRIMARY METHOD) ===
    /**
     * Enhanced item usage system - returns user-friendly messages instead of throwing exceptions
     * This is the main method to use in the frontend
     */
    public String useItemWithFeedback(GachaItem item, Puzzle puzzle) {
        // Check if item is in inventory
        if (!inventory.contains(item)) {
            return "❌ You don't have '" + item.getName() + "' in your inventory!";
        }

        // Check if puzzle is already solved
        if (puzzle.isSolved()) {
            return "ℹ️ This puzzle has already been solved!";
        }

        // Try to use the item on the puzzle
        boolean success = item.use(puzzle);

        if (success) {
            puzzlesSolved++;

            // Special handling for different item types
            if (item instanceof ToolItem) {
                ToolItem tool = (ToolItem) item;
                if (tool.getUsesRemaining() <= 0) {
                    // Remove used-up tools from inventory
                    inventory.remove(item);
                    return String.format("✅ Used %s successfully! The item broke and was removed from inventory.",
                            item.getName());
                }
            }

            return String.format("✅ Used %s successfully! The puzzle gives you a new perspective.",
                    item.getName());
        } else {
            // Provide specific feedback based on item type
            if (item instanceof KeyItem) {
                return String.format("🔑 The %s doesn't fit this puzzle. Maybe it's for a different lock?",
                        item.getName());
            } else if (item instanceof ToolItem) {
                ToolItem tool = (ToolItem) item;
                return String.format("🛠️ The %s can't help with this type of puzzle. Try a different approach.",
                        tool.getName());
            } else {
                return String.format("❓ The %s doesn't seem to affect this puzzle.",
                        item.getName());
            }
        }
    }

    /**
     * Quick-use method for trying items on current puzzles with coin rewards
     */
    public String tryItemOnPuzzle(GachaItem item, Puzzle puzzle) {
        String result = useItemWithFeedback(item, puzzle);

        // If puzzle was solved, award coins
        if (puzzle.isSolved()) {
            int reward = puzzle.getCoinReward();
            earnCoins(reward);
            result += String.format("\n💰 Solved! You earned %d coins!", reward);
        }

        return result;
    }

    // === ORIGINAL ITEM USAGE (for compatibility) ===
    /**
     * Original item usage method that throws exceptions
     * Keep for compatibility with existing code that expects boolean + exceptions
     */
    public boolean useItem(GachaItem item, Puzzle puzzle) throws WrongItemException {
        if (!inventory.contains(item)) {
            throw new WrongItemException("Item '" + item.getName() + "' is not in your inventory!");
        }

        boolean success = item.use(puzzle);

        if(!success) {
            throw new WrongItemException("The " + item.getName() + " cannot be used on this type of puzzle!");
        } else {
            puzzlesSolved++;
        }

        return true;
    }

    // === VALIDATION & SAFETY ===
    public boolean validatePlayerState() {
        try {
            if (name == null || name.trim().isEmpty()) {
                System.err.println("Player state invalid: No name");
                return false;
            }

            if (coins < 0) {
                System.err.println("Player state invalid: Negative coins");
                return false;
            }

            if (inventory == null) {
                System.err.println("Player state invalid: No inventory");
                return false;
            }

            if (inventory.size() > MAX_INVENTORY_SIZE) {
                System.err.println("Player state invalid: Inventory overflow");
                return false;
            }

            return true;
        } catch (Exception e) {
            System.err.println("Player state validation failed: " + e.getMessage());
            return false;
        }
    }

    public String safeRemoveItem(GachaItem item) {
        if (!inventory.contains(item)) {
            return "❌ Item not found in inventory.";
        }

        try {
            inventory.remove(item);

            // Update time machine parts count if it was a component
            if (item instanceof ToolItem) {
                ToolItem tool = (ToolItem) item;
                if ("time_component".equals(tool.getToolType())) {
                    // Recalculate actual count
                    timeMachinePartsCollected = countTimeMachineParts();
                }
            }

            return "✅ Item removed from inventory.";
        } catch (Exception e) {
            return "❌ Error removing item: " + e.getMessage();
        }
    }

    // === INVENTORY MANAGEMENT ===
    public void addItem(GachaItem item) throws InventoryFullException {
        if (inventory.size() >= MAX_INVENTORY_SIZE) {
            throw new InventoryFullException(inventory.size(), MAX_INVENTORY_SIZE);
        }
        this.inventory.add(item);

        // Track if it's a time machine component
        if (item instanceof ToolItem) {
            ToolItem tool = (ToolItem) item;
            if ("time_component".equals(tool.getToolType())) {
                collectTimeMachinePart();
            }
        }
    }

    public boolean hasRequiredItem(String requiredToolType) {
        if (requiredToolType == null) return true;

        for (GachaItem item : inventory) {
            if (item instanceof ToolItem) {
                ToolItem tool = (ToolItem) item;
                if (tool.getToolType().equals(requiredToolType) && tool.getUsesRemaining() > 0) {
                    return true;
                }
            }
            if (item instanceof KeyItem) {
                KeyItem key = (KeyItem) item;
                if (key.getKeyColor().equals(requiredToolType) || key.isMasterKey()) {
                    return true;
                }
            }
        }
        return false;
    }

    // === INVENTORY UTILITIES ===
    public String getInventorySummary() {
        if (inventory.isEmpty()) {
            return "🎒 Your inventory is empty.";
        }

        StringBuilder summary = new StringBuilder();
        summary.append("🎒 INVENTORY (").append(inventory.size())
                .append("/").append(MAX_INVENTORY_SIZE).append(")\n");

        // Count items by type
        Map<String, Integer> typeCounts = new HashMap<>();
        Map<Rarity, Integer> rarityCounts = new HashMap<>();

        for (GachaItem item : inventory) {
            // By item type
            String itemType = item.getItemType().toString();
            typeCounts.put(itemType, typeCounts.getOrDefault(itemType, 0) + 1);

            // By rarity
            Rarity rarity = item.getRarity();
            rarityCounts.put(rarity, rarityCounts.getOrDefault(rarity, 0) + 1);
        }

        // Add type summary
        summary.append("Types: ");
        typeCounts.forEach((type, count) -> {
            summary.append(count).append(" ").append(type).append(" ");
        });

        summary.append("\nRarity: ");
        rarityCounts.forEach((rarity, count) -> {
            summary.append(count).append(" ").append(rarity).append(" ");
        });

        return summary.toString();
    }

    public List<GachaItem> findHelpfulItems(Puzzle puzzle) {
        List<GachaItem> helpful = new ArrayList<>();

        for (GachaItem item : inventory) {
            if (item instanceof ToolItem) {
                ToolItem tool = (ToolItem) item;

                // Check if tool has uses remaining
                if (tool.getUsesRemaining() <= 0) continue;

                // Determine if tool might help based on puzzle type
                if (puzzle instanceof CodePuzzle && "decoder".equals(tool.getToolType())) {
                    helpful.add(item);
                } else if (puzzle instanceof RiddlePuzzle && "hintbook".equals(tool.getToolType())) {
                    helpful.add(item);
                } else if (puzzle instanceof LockPuzzle && "lockpick".equals(tool.getToolType())) {
                    helpful.add(item);
                } else if ("time_component".equals(tool.getToolType())) {
                    // Time components might provide general hints
                    helpful.add(item);
                }
            } else if (item instanceof KeyItem) {
                // Key items might unlock certain puzzles
                helpful.add(item);
            }
        }

        return helpful;
    }

    public Map<String, Object> getLoadoutSummary() {
        Map<String, Object> loadout = new HashMap<>();

        // Item counts by type
        Map<String, Integer> typeCounts = new HashMap<>();
        Map<Rarity, Integer> rarityCounts = new HashMap<>();
        Map<String, Integer> toolTypeCounts = new HashMap<>();

        for (GachaItem item : inventory) {
            // By item type
            String itemType = item.getItemType().toString();
            typeCounts.put(itemType, typeCounts.getOrDefault(itemType, 0) + 1);

            // By rarity
            rarityCounts.put(item.getRarity(), rarityCounts.getOrDefault(item.getRarity(), 0) + 1);

            // By tool type (if applicable)
            if (item instanceof ToolItem) {
                ToolItem tool = (ToolItem) item;
                toolTypeCounts.put(tool.getToolType(),
                        toolTypeCounts.getOrDefault(tool.getToolType(), 0) + 1);
            }
        }

        loadout.put("totalItems", inventory.size());
        loadout.put("maxCapacity", MAX_INVENTORY_SIZE);
        loadout.put("itemTypes", typeCounts);
        loadout.put("rarities", rarityCounts);
        loadout.put("toolTypes", toolTypeCounts);
        loadout.put("timeMachineParts", timeMachinePartsCollected);

        return loadout;
    }

    public String getInventoryStatus() {
        int current = inventory.size();
        int max = MAX_INVENTORY_SIZE;
        double percentage = (double) current / max * 100;

        if (percentage >= 90) {
            return "🔴 Inventory almost full! (" + current + "/" + max + ")";
        } else if (percentage >= 75) {
            return "🟡 Inventory getting full (" + current + "/" + max + ")";
        } else {
            return "🟢 Inventory space available (" + current + "/" + max + ")";
        }
    }

    // === UTILITY METHODS ===
    public boolean hasItemType(String toolType) {
        for (GachaItem item : inventory) {
            if (item instanceof ToolItem) {
                ToolItem tool = (ToolItem) item;
                if (tool.getToolType().equals(toolType) && tool.getUsesRemaining() > 0) {
                    return true;
                }
            }
        }
        return false;
    }

    public List<GachaItem> getItemsByType(String toolType) {
        List<GachaItem> result = new ArrayList<>();
        for (GachaItem item : inventory) {
            if (item instanceof ToolItem) {
                ToolItem tool = (ToolItem) item;
                if (tool.getToolType().equals(toolType)) {
                    result.add(item);
                }
            }
        }
        return result;
    }

    public String getProgressSummary() {
        return String.format(
                "Player: %s | Coins: %d | Puzzles Solved: %d | Time Parts: %d/6",
                name, coins, puzzlesSolved, timeMachinePartsCollected
        );
    }

    public int countTimeMachineParts() {
        int count = 0;
        for (GachaItem item : inventory) {
            if (item instanceof ToolItem) {
                ToolItem tool = (ToolItem) item;
                if ("time_component".equals(tool.getToolType())) {
                    count++;
                }
            }
        }
        return count;
    }

    // === COIN MANAGEMENT ===
    public void setCoins(int coins) {
        this.coins = coins;
    }

    public void earnCoins(int amount) {
        this.coins += amount;
        recordCoinsEarned(amount);
    }

    public boolean spendCoins(int amount) {
        if (this.coins >= amount) {
            this.coins -= amount;
            recordCoinsSpent(amount);
            return true;
        }
        return false;
    }

    // === PROGRESS TRACKING ===
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

    public void collectTimeMachinePart() {
        this.timeMachinePartsCollected++;
    }

    // === SETTER METHODS (for game state loading) ===
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

    public void setTimeMachinePartsCollected(int count) {
        this.timeMachinePartsCollected = count;
    }

    // === GETTER METHODS ===
    public String getName() { return name; }
    public int getCoinBalance() { return coins; }
    public int getTotalPulls() { return totalPulls; }
    public int getPuzzlesSolved() { return puzzlesSolved; }
    public int getTotalCoinsEarned() { return totalCoinsEarned; }
    public int getTotalCoinsSpent() { return totalCoinsSpent; }
    public int getRoomsCompleted() { return roomsCompleted; }
    public int getMaxInventorySize() { return MAX_INVENTORY_SIZE; }
    public int getCurrentInventorySize() { return inventory.size(); }
    public int getTimeMachinePartsCollected() { return timeMachinePartsCollected; }
    public List<GachaItem> getInventory() {
        return new ArrayList<>(inventory);
    }
}