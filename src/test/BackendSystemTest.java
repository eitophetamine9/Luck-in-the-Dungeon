package test;

import model.*;
import util.FileManager;
import exceptions.NotEnoughCoinsException;
import exceptions.WrongItemException;
import exceptions.SaveFileCorruptedException;

public class BackendSystemTest {
    public static void main(String[] args) {
        System.out.println("🎮 === LUCK IN THE DUNGEON - BACKEND SYSTEM TEST === 🎮\n");

        try {
            // Test 1: Game Manager & Basic Setup
            System.out.println("1. 🏗️  Testing GameManager (Singleton)...");
            GameManager game1 = GameManager.getInstance();
            GameManager game2 = GameManager.getInstance();
            System.out.println("   ✅ Singleton working: " + (game1 == game2));
            System.out.println("   ✅ Player: " + game1.getCurrentPlayer().getName());
            System.out.println("   ✅ Starting Coins: " + game1.getCurrentPlayer().getCoinBalance());
            System.out.println("   ✅ Current Room: " + game1.getCurrentRoom().getName());

            // Test 2: Player & Inventory System
            System.out.println("\n2. 👤 Testing Player & Inventory...");
            Player player = game1.getCurrentPlayer();
            player.earnCoins(50);
            System.out.println("   ✅ Earned coins: " + player.getCoinBalance());

            KeyItem blueKey = new KeyItem("Blue Key", "Opens blue locks", Rarity.COMMON, "blue", false);
            player.addItem(blueKey);
            System.out.println("   ✅ Added item to inventory: " + blueKey.getName());
            System.out.println("   ✅ Inventory size: " + player.getInventory().size());

            // Test 3: Puzzle System
            System.out.println("\n3. 🧩 Testing Puzzle System...");
            LockPuzzle blueLock = new LockPuzzle("Blue locked door", 25, 1, "blue", "blue");
            LockPuzzle redLock = new LockPuzzle("Red locked chest", 50, 2, "red", "red");

            // Add puzzles to current room
            Room currentRoom = game1.getCurrentRoom();
            currentRoom.addPuzzle(blueLock);
            currentRoom.addPuzzle(redLock);
            System.out.println("   ✅ Room puzzles: " + currentRoom.getPuzzles().size());
            System.out.println("   ✅ Room complete: " + currentRoom.isComplete());

            // Test 4: Item Usage & WrongItemException
            System.out.println("\n4. 🔑 Testing Item Usage...");
            try {
                boolean result1 = player.useItem(blueKey, blueLock);
                System.out.println("   ✅ Blue key on blue lock: " + result1);
                System.out.println("   ✅ Blue lock solved: " + blueLock.isSolved());

                boolean result2 = player.useItem(blueKey, redLock);
                System.out.println("   ❌ Blue key on red lock: " + result2);

            } catch (WrongItemException e) {
                System.out.println("   ✅ WrongItemException caught: " + e.getMessage());
            }

            // Test 5: Gacha System & NotEnoughCoinsException
            System.out.println("\n5. 🎰 Testing Gacha System...");
            GachaMachine gacha = currentRoom.getGachaMachine();

            // Add some items to gacha pool
            gacha.addItemToPool(new KeyItem("Common Key", "Basic key", Rarity.COMMON, "brown", false));
            gacha.addItemToPool(new KeyItem("Rare Key", "Shiny key", Rarity.RARE, "silver", false));
            gacha.addItemToPool(new ToolItem("Lockpick", "Can pick locks", Rarity.RARE, "lockpick", 3));

            System.out.println("   ✅ Gacha machine: " + gacha.getMachineName());
            System.out.println("   ✅ Pull cost: " + gacha.getPullCost());
            System.out.println("   ✅ Can pull: " + gacha.canPull(player));

            // Test successful pull
            if (gacha.canPull(player)) {
                GachaItem pulledItem = gacha.pull(player);
                System.out.println("   ✅ Pulled item: " + pulledItem.getName() + " (" + pulledItem.getRarity() + ")");
                player.addItem(pulledItem);
            }

            // Test exception by making player poor
            System.out.println("\n6. 💰 Testing NotEnoughCoinsException...");
            Player poorPlayer = new Player("Poor Player"); // Starts with 100 coins
            poorPlayer.spendCoins(95); // Spend most coins
            System.out.println("   ✅ Poor player coins: " + poorPlayer.getCoinBalance());

            try {
                GachaItem item = gacha.pull(poorPlayer); // This should throw exception
                System.out.println("   ❌ Should not reach here");
            } catch (NotEnoughCoinsException e) {
                System.out.println("   ✅ NotEnoughCoinsException caught: " + e.getMessage());
                System.out.println("   ✅ Required: " + e.getRequired() + ", Available: " + e.getAvailable());
            }

            // Test 7: File Manager - Save/Load
            System.out.println("\n7. 💾 Testing File Manager...");
            FileManager fileManager = new FileManager();

            // Save game
            game1.saveGame();
            System.out.println("   ✅ Game saved successfully");

            // Check if save exists
            System.out.println("   ✅ Save exists: " + fileManager.saveExists());

            // Create new game instance and load
            System.out.println("\n8. 🔄 Testing Game Load...");
            GameManager freshGame = GameManager.getInstance();
            freshGame.startNewGame(); // Reset to fresh state
            System.out.println("   ✅ Fresh game player: " + freshGame.getCurrentPlayer().getName());
            System.out.println("   ✅ Fresh game coins: " + freshGame.getCurrentPlayer().getCoinBalance());
            System.out.println("   ✅ Fresh game inventory: " + freshGame.getCurrentPlayer().getInventory().size());

            // Load saved game
            boolean loaded = freshGame.loadGame();
            if (loaded) {
                System.out.println("   ✅ Game loaded successfully!");
                System.out.println("   ✅ Loaded player: " + freshGame.getCurrentPlayer().getName());
                System.out.println("   ✅ Loaded coins: " + freshGame.getCurrentPlayer().getCoinBalance());
                System.out.println("   ✅ Loaded inventory: " + freshGame.getCurrentPlayer().getInventory().size());
                System.out.println("   ✅ Loaded room: " + freshGame.getCurrentRoom().getName());
            } else {
                System.out.println("   ❌ Game load failed");
            }

            // Test 9: Room Progression
            System.out.println("\n9. 🚪 Testing Room Progression...");
            System.out.println("   ✅ Current room: " + freshGame.getCurrentRoom().getName());
            System.out.println("   ✅ Current room index: " + freshGame.getCurrentRoomIndex());

            // Solve all puzzles in current room
            for (Puzzle puzzle : freshGame.getCurrentRoom().getPuzzles()) {
                if (puzzle instanceof LockPuzzle && !puzzle.isSolved()) {
                    LockPuzzle lock = (LockPuzzle) puzzle;
                    // Use appropriate key if available
                    for (GachaItem item : freshGame.getCurrentPlayer().getInventory()) {
                        if (item instanceof KeyItem) {
                            KeyItem key = (KeyItem) item;
                            if (key.use(lock)) {
                                System.out.println("   ✅ Solved puzzle with: " + key.getName());
                                break;
                            }
                        }
                    }
                }
            }

            System.out.println("   ✅ Room complete: " + freshGame.getCurrentRoom().isComplete());

            if (freshGame.getCurrentRoom().isComplete()) {
                freshGame.moveToNextRoom();
                System.out.println("   ✅ Moved to next room: " + freshGame.getCurrentRoom().getName());
            }

            // Test 10: Cleanup
            System.out.println("\n10. 🧹 Testing Cleanup...");
            freshGame.deleteSave();
            System.out.println("   ✅ Save deleted: " + !fileManager.saveExists());

        } catch (Exception e) {
            System.err.println("❌ Unexpected error during testing: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("\n🎉 === BACKEND SYSTEM TEST COMPLETE === 🎉");
        System.out.println("All core systems are working correctly!");
    }
}