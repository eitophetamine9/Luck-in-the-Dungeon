package test;

import exceptions.InventoryFullException;
import model.*;
import exceptions.NotEnoughCoinsException;
import exceptions.WrongItemException;
import java.util.List;
import java.util.Scanner;

public class BackendSystemTest {
    private static GameManager game;
    private static Scanner scanner;

    public static void main(String[] args) {
        scanner = new Scanner(System.in);
        game = GameManager.getInstance();

        System.out.println("🎮 === LUCK IN THE DUNGEON - CONSOLE TEST === 🎮\n");
        System.out.println("Welcome, " + game.getCurrentPlayer().getName() + "!");
        System.out.println("You start with " + game.getCurrentPlayer().getCoinBalance() + " coins.");
        System.out.println("Goal: Solve all puzzles in all 4 rooms to escape!\n");

        // Initial instructions
        System.out.println("💡 HOW TO PLAY:");
        System.out.println("- Solve puzzles to earn coins");
        System.out.println("- Use coins to pull from gacha machines");
        System.out.println("- Some puzzles REQUIRE specific gacha items");
        System.out.println("- Manage your inventory (max 20 items)");
        System.out.println("- Complete each room to progress\n");

        mainMenu();

        scanner.close();
    }

    private static void mainMenu() {
        while (true) {
            System.out.println("\n" + "═".repeat(50));
            System.out.println("=== MAIN MENU ===");
            System.out.println("📍 Current Room: " + game.getCurrentRoom().getName());
            System.out.println("💰 Coins: " + game.getCurrentPlayer().getCoinBalance());
            System.out.println("🎒 Inventory: " + game.getCurrentPlayer().getInventory().size() + "/20 items");
            System.out.println("📊 Puzzles Solved: " + game.getCurrentPlayer().getPuzzlesSolved());
            System.out.println("🏠 Rooms Completed: " + game.getCurrentPlayer().getRoomsCompleted());
            System.out.println("✅ Room Complete: " + game.isCurrentRoomComplete());

            if (game.checkWinCondition()) {
                System.out.println("\n🎉🎉🎉 CONGRATULATIONS! YOU ESCAPED THE DUNGEON! 🎉🎉🎉");
                System.out.println("Final Stats:");
                System.out.println("- Total Puzzles Solved: " + game.getCurrentPlayer().getPuzzlesSolved());
                System.out.println("- Total Rooms Completed: " + game.getCurrentPlayer().getRoomsCompleted());
                System.out.println("- Total Coins Earned: " + game.getCurrentPlayer().getTotalCoinsEarned());
                System.out.println("- Total Gacha Pulls: " + game.getCurrentPlayer().getTotalPulls());
                return;
            }

            System.out.println("\nWhat would you like to do?");
            System.out.println("1. 🔍 View Current Room & Puzzles");
            System.out.println("2. 🧩 Solve Puzzles");
            System.out.println("3. 🎰 Use Gacha Machine");
            System.out.println("4. 🎒 View Inventory");
            System.out.println("5. 🛠️ Use Item on Puzzle");
            System.out.println("6. 💾 Save Game");
            System.out.println("7. 📂 Load Game");
            System.out.println("8. 🚪 Move to Next Room");
            System.out.println("9. 📊 Check Progress");
            System.out.println("0. ❌ Exit");

            System.out.print("\nEnter your choice (0-9): ");
            String choice = scanner.nextLine();

            switch (choice) {
                case "1": viewCurrentRoom(); break;
                case "2": solvePuzzles(); break;
                case "3": useGachaMachine(); break;
                case "4": viewInventory(); break;
                case "5": useItemOnPuzzle(); break;
                case "6": saveGame(); break;
                case "7": loadGame(); break;
                case "8": moveToNextRoom(); break;
                case "9": checkProgress(); break;
                case "0":
                    System.out.print("Are you sure you want to exit? (y/n): ");
                    String confirm = scanner.nextLine().toLowerCase();
                    if (confirm.equals("y") || confirm.equals("yes")) {
                        System.out.println("Thanks for playing! Goodbye! 👋");
                        return;
                    }
                    break;
                default:
                    System.out.println("❌ Invalid choice. Please enter a number between 0-9.");
            }
        }
    }

    private static void viewCurrentRoom() {
        Room room = game.getCurrentRoom();
        System.out.println("\n" + "═".repeat(50));
        System.out.println("=== " + room.getName().toUpperCase() + " ===");
        System.out.println("📝 Description: " + room.getRoomDescription());
        System.out.println("🔒 Locked: " + (room.isLocked() ? "Yes ❌" : "No ✅"));
        System.out.println("✅ Complete: " + (room.isComplete() ? "Yes 🎉" : "No ❌"));
        System.out.println("🎰 Gacha Machine: " + room.getGachaMachine().getMachineName());
        System.out.println("💵 Pull Cost: " + room.getGachaMachine().getPullCost() + " coins");

        List<Puzzle> puzzles = room.getPuzzles();
        if (!puzzles.isEmpty()) {
            System.out.println("\n📜 PUZZLES IN THIS ROOM:");
            for (int i = 0; i < puzzles.size(); i++) {
                Puzzle puzzle = puzzles.get(i);
                String status = puzzle.isSolved() ? "✅ SOLVED" : "❌ UNSOLVED";
                System.out.println("\n" + (i+1) + ". " + puzzle.getDescription());
                System.out.println("   Status: " + status);
                System.out.println("   Reward: " + puzzle.getCoinReward() + " coins");
                System.out.println("   Difficulty: " + "⭐".repeat(puzzle.getDifficultyLevel()));

                if (!puzzle.isSolved()) {
                    // Show gacha requirement
                    if (puzzle.requiresGachaItem()) {
                        String requirementMessage = game.getPuzzleRequirementMessage(puzzle);
                        if (requirementMessage != null) {
                            System.out.println("   " + requirementMessage);
                        } else {
                            System.out.println("   ✅ You have the required item!");
                        }
                    }

                    System.out.println("   💡 Hint: " + puzzle.getHint());

                    // Show attempts for CodePuzzles
                    if (puzzle instanceof CodePuzzle) {
                        CodePuzzle codePuzzle = (CodePuzzle) puzzle;
                        System.out.println("   🔄 Attempts: " + codePuzzle.getCurrentAttempts() + "/" +
                                (codePuzzle.getCurrentAttempts() + codePuzzle.getRemainingAttempts()));
                    }
                }
            }
        }

        System.out.print("\nPress Enter to continue...");
        scanner.nextLine();
    }

    private static void solvePuzzles() {
        List<Puzzle> availablePuzzles = game.getAvailablePuzzles();

        if (availablePuzzles.isEmpty()) {
            System.out.println("🎉 All puzzles in this room are already solved!");
            System.out.print("Press Enter to continue...");
            scanner.nextLine();
            return;
        }

        System.out.println("\n" + "═".repeat(50));
        System.out.println("=== SOLVE PUZZLES ===");
        for (int i = 0; i < availablePuzzles.size(); i++) {
            Puzzle puzzle = availablePuzzles.get(i);
            String type = "";
            if (puzzle instanceof CodePuzzle) type = "🔐 ";
            else if (puzzle instanceof RiddlePuzzle) type = "🤔 ";
            else if (puzzle instanceof LockPuzzle) type = "🔒 ";

            System.out.println((i+1) + ". " + type + puzzle.getDescription());
            System.out.println("   Reward: " + puzzle.getCoinReward() + " coins | Difficulty: " + "⭐".repeat(puzzle.getDifficultyLevel()));
        }

        System.out.print("\nSelect a puzzle to solve (1-" + availablePuzzles.size() + ") or 0 to go back: ");
        try {
            int choice = Integer.parseInt(scanner.nextLine());
            if (choice == 0) return;
            if (choice < 1 || choice > availablePuzzles.size()) {
                System.out.println("❌ Invalid puzzle selection.");
                System.out.print("Press Enter to continue...");
                scanner.nextLine();
                return;
            }

            Puzzle selectedPuzzle = availablePuzzles.get(choice - 1);

            // Handle different puzzle types
            if (selectedPuzzle instanceof CodePuzzle) {
                solveCodePuzzle((CodePuzzle) selectedPuzzle);
            } else if (selectedPuzzle instanceof RiddlePuzzle) {
                solveRiddlePuzzle((RiddlePuzzle) selectedPuzzle);
            } else if (selectedPuzzle instanceof LockPuzzle) {
                System.out.println("🔒 Lock puzzles require keys! Use 'Use Item on Puzzle' from the main menu.");
            }

        } catch (NumberFormatException e) {
            System.out.println("❌ Please enter a valid number.");
        }
        System.out.print("Press Enter to continue...");
        scanner.nextLine();
    }

    private static void solveCodePuzzle(CodePuzzle puzzle) {
        System.out.println("\n" + "═".repeat(50));
        System.out.println("🔐 CODE PUZZLE");
        System.out.println("📝 " + puzzle.getDescription());
        System.out.println("💡 " + puzzle.getHint());
        System.out.println("🔄 Attempts: " + puzzle.getCurrentAttempts() + " used, " + puzzle.getRemainingAttempts() + " remaining");
        System.out.println("💰 Reward: " + puzzle.getCoinReward() + " coins");

        if (!puzzle.hasAttemptsLeft()) {
            System.out.println("💀 No attempts left! Use a decoder tool or try another puzzle.");
            return;
        }

        System.out.print("Enter your solution: ");
        String answer = scanner.nextLine().trim();

        // FIXED: Actually call validateCode to check the answer and increment attempts
        boolean isCorrect = puzzle.validateCode(answer);

        if (isCorrect) {
            System.out.println("🎉 CORRECT! You solved the puzzle!");
            System.out.println("💰 +" + puzzle.getCoinReward() + " coins earned!");
            game.getCurrentPlayer().earnCoins(puzzle.getCoinReward());
        } else {
            System.out.println("❌ Incorrect. Attempts remaining: " + puzzle.getRemainingAttempts());
            if (!puzzle.hasAttemptsLeft()) {
                System.out.println("💀 No attempts remaining! This puzzle is now locked.");
                System.out.println("💡 Use a decoder tool from your inventory to get hints.");
            }
        }
    }

    private static void solveRiddlePuzzle(RiddlePuzzle puzzle) {
        System.out.println("\n" + "═".repeat(50));
        System.out.println("🤔 RIDDLE PUZZLE");
        System.out.println("❓ " + puzzle.getQuestion());
        System.out.println("💡 " + puzzle.getHint());
        System.out.println("💰 Reward: " + puzzle.getCoinReward() + " coins");

        System.out.print("Enter your answer: ");
        String answer = scanner.nextLine().trim();

        // FIXED: Actually call checkAnswer to verify the solution
        boolean isCorrect = puzzle.checkAnswer(answer);

        if (isCorrect) {
            System.out.println("🎉 CORRECT! You solved the riddle!");
            System.out.println("💰 +" + puzzle.getCoinReward() + " coins earned!");
            game.getCurrentPlayer().earnCoins(puzzle.getCoinReward());
        } else {
            System.out.println("❌ Incorrect answer. Try again or use a hint book!");
        }
    }

    private static void useGachaMachine() {
        GachaMachine gacha = game.getCurrentGachaMachine();
        Player player = game.getCurrentPlayer();

        System.out.println("\n" + "═".repeat(50));
        System.out.println("=== 🎰 " + gacha.getMachineName() + " ===");
        System.out.println("💵 Pull Cost: " + gacha.getPullCost() + " coins");
        System.out.println("💰 Your Coins: " + player.getCoinBalance());
        System.out.println("🎒 Inventory Space: " + player.getCurrentInventorySize() + "/20");
        System.out.println("📊 Total Pulls: " + player.getTotalPulls());
        System.out.println("🎯 Pity System: " + gacha.getPullsSinceLastEpic() + "/10 pulls since last EPIC");

        if (!gacha.canPull(player)) {
            System.out.println("❌ Not enough coins! Solve puzzles to earn more coins.");
            System.out.print("Press Enter to continue...");
            scanner.nextLine();
            return;
        }

        if (player.getCurrentInventorySize() >= player.getMaxInventorySize()) {
            System.out.println("❌ Inventory full! You cannot carry more items.");
            System.out.println("💡 Use or discard some items from your inventory.");
            System.out.print("Press Enter to continue...");
            scanner.nextLine();
            return;
        }

        System.out.print("Pull from gacha? (y/n): ");
        String choice = scanner.nextLine().toLowerCase();

        if (choice.equals("y") || choice.equals("yes")) {
            try {
                GachaItem pulledItem = gacha.pull(player);
                if (pulledItem != null) {
                    System.out.println("\n🎉 CONGRATULATIONS! You pulled:");
                    System.out.println("✨ " + pulledItem.getName() + " (" + pulledItem.getRarity() + ") ✨");
                    System.out.println("📝 " + pulledItem.getDescription());

                    if (pulledItem instanceof KeyItem) {
                        KeyItem key = (KeyItem) pulledItem;
                        System.out.println("🔑 Key Color: " + key.getKeyColor() + (key.isMasterKey() ? " (MASTER KEY 🔓)" : ""));
                    } else if (pulledItem instanceof ToolItem) {
                        ToolItem tool = (ToolItem) pulledItem;
                        System.out.println("🛠️ Tool Type: " + tool.getToolType());
                        System.out.println("🔢 Uses Remaining: " + tool.getUsesRemaining());
                    }

                    player.addItem(pulledItem);
                    System.out.println("💰 Coins remaining: " + player.getCoinBalance());
                    System.out.println("🎒 Inventory: " + player.getCurrentInventorySize() + "/20 items");
                } else {
                    System.out.println("❌ No items available in the gacha machine!");
                }
            } catch (NotEnoughCoinsException e) {
                System.out.println("❌ " + e.getMessage());
            } catch (InventoryFullException e) {
                System.out.println("❌ " + e.getMessage());
            }
        } else {
            System.out.println("Gacha pull cancelled.");
        }
        System.out.print("Press Enter to continue...");
        scanner.nextLine();
    }

    private static void viewInventory() {
        List<GachaItem> inventory = game.getCurrentPlayer().getInventory();

        System.out.println("\n" + "═".repeat(50));
        System.out.println("=== 🎒 INVENTORY ===");
        System.out.println("Items: " + inventory.size() + "/20");

        if (inventory.isEmpty()) {
            System.out.println("Your inventory is empty. Use the gacha machine to get items!");
            System.out.print("Press Enter to continue...");
            scanner.nextLine();
            return;
        }

        for (int i = 0; i < inventory.size(); i++) {
            GachaItem item = inventory.get(i);
            String type = "🔑";
            if (item instanceof ToolItem) {
                type = "🛠️";
            }
            System.out.println("\n" + (i+1) + ". " + type + " " + item.getName() + " (" + item.getRarity() + ")");
            System.out.println("   📝 " + item.getDescription());

            if (item instanceof KeyItem) {
                KeyItem key = (KeyItem) item;
                System.out.println("   🎨 Key Color: " + key.getKeyColor() + (key.isMasterKey() ? " (MASTER KEY 🔓)" : ""));
            } else if (item instanceof ToolItem) {
                ToolItem tool = (ToolItem) item;
                System.out.println("   🔧 Tool Type: " + tool.getToolType());
                System.out.println("   🔢 Uses Remaining: " + tool.getUsesRemaining());
            }
        }

        System.out.print("\nPress Enter to continue...");
        scanner.nextLine();
    }

    private static void useItemOnPuzzle() {
        List<GachaItem> inventory = game.getCurrentPlayer().getInventory();
        List<Puzzle> availablePuzzles = game.getAvailablePuzzles();

        if (inventory.isEmpty()) {
            System.out.println("❌ Your inventory is empty! Use the gacha machine to get items first.");
            System.out.print("Press Enter to continue...");
            scanner.nextLine();
            return;
        }

        if (availablePuzzles.isEmpty()) {
            System.out.println("🎉 No puzzles left to solve in this room!");
            System.out.print("Press Enter to continue...");
            scanner.nextLine();
            return;
        }

        System.out.println("\n" + "═".repeat(50));
        System.out.println("=== USE ITEM ON PUZZLE ===");

        // Show inventory
        System.out.println("🎒 YOUR ITEMS:");
        for (int i = 0; i < inventory.size(); i++) {
            GachaItem item = inventory.get(i);
            String type = item instanceof KeyItem ? "🔑" : "🛠️";
            System.out.println((i+1) + ". " + type + " " + item.getName() + " (" + item.getRarity() + ")");
        }

        // Show puzzles
        System.out.println("\n📜 AVAILABLE PUZZLES:");
        for (int i = 0; i < availablePuzzles.size(); i++) {
            Puzzle puzzle = availablePuzzles.get(i);
            String requirement = puzzle.requiresGachaItem() ? " [Requires: " + puzzle.getRequiredToolType() + "]" : "";
            System.out.println((i+1) + ". " + puzzle.getDescription() + requirement);
        }

        try {
            System.out.print("\nSelect item (1-" + inventory.size() + "): ");
            int itemChoice = Integer.parseInt(scanner.nextLine());

            System.out.print("Select puzzle (1-" + availablePuzzles.size() + "): ");
            int puzzleChoice = Integer.parseInt(scanner.nextLine());

            if (itemChoice < 1 || itemChoice > inventory.size() ||
                    puzzleChoice < 1 || puzzleChoice > availablePuzzles.size()) {
                System.out.println("❌ Invalid selection.");
                System.out.print("Press Enter to continue...");
                scanner.nextLine();
                return;
            }

            GachaItem item = inventory.get(itemChoice - 1);
            Puzzle puzzle = availablePuzzles.get(puzzleChoice - 1);

            try {
                boolean success = game.getCurrentPlayer().useItem(item, puzzle);
                if (success) {
                    System.out.println("✅ Successfully used " + item.getName() + " on the puzzle!");
                    if (puzzle.isSolved()) {
                        System.out.println("🎉 Puzzle solved! You earned " + puzzle.getCoinReward() + " coins!");
                        game.getCurrentPlayer().earnCoins(puzzle.getCoinReward());
                    }
                } else {
                    System.out.println("❌ The item didn't work on this puzzle.");
                    System.out.println("💡 Try using a different item or check the puzzle requirements.");
                }
            } catch (WrongItemException e) {
                System.out.println("❌ " + e.getMessage());
            }

        } catch (NumberFormatException e) {
            System.out.println("❌ Please enter valid numbers.");
        }
        System.out.print("Press Enter to continue...");
        scanner.nextLine();
    }

    private static void saveGame() {
        game.saveGame();
        System.out.println("💾 Game saved successfully!");
        System.out.print("Press Enter to continue...");
        scanner.nextLine();
    }

    private static void loadGame() {
        if (game.loadGame()) {
            System.out.println("📂 Game loaded successfully!");
            System.out.println("Welcome back, " + game.getCurrentPlayer().getName() + "!");
            System.out.println("You have " + game.getCurrentPlayer().getCoinBalance() + " coins.");
            System.out.println("Current room: " + game.getCurrentRoom().getName());
        } else {
            System.out.println("❌ No save file found or load failed.");
        }
        System.out.print("Press Enter to continue...");
        scanner.nextLine();
    }

    private static void moveToNextRoom() {
        if (!game.isCurrentRoomComplete()) {
            System.out.println("❌ You must solve all puzzles in this room first!");
            System.out.println("💡 Check which puzzles are still unsolved in the 'View Current Room' menu.");
            System.out.print("Press Enter to continue...");
            scanner.nextLine();
            return;
        }

        if (game.getCurrentRoomIndex() >= game.getRooms().size() - 1) {
            System.out.println("🎉 You're in the final room! Solve it to win the game!");
            System.out.print("Press Enter to continue...");
            scanner.nextLine();
            return;
        }

        System.out.println("🚪 Ready to move to the next room?");
        System.out.println("Current progress: Room " + (game.getCurrentRoomIndex() + 1) + " of " + game.getRooms().size());
        System.out.print("Move to next room? (y/n): ");
        String choice = scanner.nextLine().toLowerCase();

        if (choice.equals("y") || choice.equals("yes")) {
            game.moveToNextRoom();
            System.out.println("🚪 You moved to: " + game.getCurrentRoom().getName());
            System.out.println("📝 " + game.getCurrentRoom().getRoomDescription());
            System.out.println("💡 Explore the new room and check what items you'll need from the gacha machine!");
        } else {
            System.out.println("Staying in current room.");
        }
        System.out.print("Press Enter to continue...");
        scanner.nextLine();
    }

    private static void checkProgress() {
        System.out.println("\n" + "═".repeat(50));
        System.out.println("=== 📊 GAME PROGRESS ===");

        boolean hasWon = game.checkWinCondition();
        int totalRooms = game.getRooms().size();
        int solvedRooms = 0;
        int totalPuzzles = 0;
        int solvedPuzzles = 0;

        for (Room room : game.getRooms()) {
            if (room.isComplete()) solvedRooms++;
            List<Puzzle> roomPuzzles = room.getPuzzles();
            totalPuzzles += roomPuzzles.size();
            for (Puzzle puzzle : roomPuzzles) {
                if (puzzle.isSolved()) solvedPuzzles++;
            }
        }

        System.out.println("🏠 Rooms: " + solvedRooms + "/" + totalRooms + " completed");
        System.out.println("🧩 Puzzles: " + solvedPuzzles + "/" + totalPuzzles + " solved");
        System.out.println("💰 Coins: " + game.getCurrentPlayer().getCoinBalance());
        System.out.println("🎒 Inventory: " + game.getCurrentPlayer().getInventory().size() + "/20 items");
        System.out.println("🎰 Total Gacha Pulls: " + game.getCurrentPlayer().getTotalPulls());
        System.out.println("💸 Total Coins Spent: " + game.getCurrentPlayer().getTotalCoinsSpent());
        System.out.println("💎 Total Coins Earned: " + game.getCurrentPlayer().getTotalCoinsEarned());

        if (hasWon) {
            System.out.println("\n🎉🎉🎉 CONGRATULATIONS! YOU ESCAPED THE DUNGEON! 🎉🎉🎉");
        } else {
            System.out.println("\n📈 Progress to next room:");
            if (game.isCurrentRoomComplete()) {
                System.out.println("✅ Current room complete! You can move to the next room.");
            } else {
                int remainingInRoom = game.getAvailablePuzzles().size();
                System.out.println("❌ Current room not complete. " + remainingInRoom + " puzzle(s) remaining.");

                // Show gacha requirements for current room
                List<Puzzle> availablePuzzles = game.getAvailablePuzzles();
                boolean hasGachaRequirements = false;
                for (Puzzle puzzle : availablePuzzles) {
                    if (puzzle.requiresGachaItem() && !game.canSolvePuzzle(puzzle)) {
                        if (!hasGachaRequirements) {
                            System.out.println("\n💡 Gacha items needed for current room:");
                            hasGachaRequirements = true;
                        }
                        System.out.println("   - " + puzzle.getRequiredToolType() + " for: " + puzzle.getDescription());
                    }
                }
            }
        }

        System.out.print("\nPress Enter to continue...");
        scanner.nextLine();
    }
}