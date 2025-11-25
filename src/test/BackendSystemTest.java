package test;

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

        mainMenu();

        scanner.close();
    }

    private static void mainMenu() {
        while (true) {
            System.out.println("\n=== MAIN MENU ===");
            System.out.println("Current Room: " + game.getCurrentRoom().getName());
            System.out.println("Coins: " + game.getCurrentPlayer().getCoinBalance());
            System.out.println("Inventory: " + game.getCurrentPlayer().getInventory().size() + " items");
            System.out.println("Room Complete: " + game.isCurrentRoomComplete());
            System.out.println("Game Won: " + game.checkWinCondition());

            System.out.println("\nWhat would you like to do?");
            System.out.println("1. View Current Room");
            System.out.println("2. Solve Puzzles");
            System.out.println("3. Use Gacha Machine");
            System.out.println("4. View Inventory");
            System.out.println("5. Use Item on Puzzle");
            System.out.println("6. Save Game");
            System.out.println("7. Load Game");
            System.out.println("8. Move to Next Room");
            System.out.println("9. Check Win Condition");
            System.out.println("0. Exit");

            System.out.print("\nEnter your choice: ");
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
                case "9": checkWinCondition(); break;
                case "0":
                    System.out.println("Thanks for playing! Goodbye! 👋");
                    return;
                default:
                    System.out.println("❌ Invalid choice. Please try again.");
            }

            // Check if game is won after each action
            if (game.checkWinCondition()) {
                System.out.println("\n🎉🎉🎉 CONGRATULATIONS! YOU ESCAPED THE DUNGEON! 🎉🎉🎉");
                System.out.println("You solved all puzzles across all rooms!");
                return;
            }
        }
    }

    private static void viewCurrentRoom() {
        Room room = game.getCurrentRoom();
        System.out.println("\n=== " + room.getName().toUpperCase() + " ===");
        System.out.println("Description: " + room.getRoomDescription());
        System.out.println("Locked: " + room.isLocked());
        System.out.println("Complete: " + room.isComplete());
        System.out.println("Available Puzzles: " + game.getAvailablePuzzles().size());
        System.out.println("Gacha Machine: " + room.getGachaMachine().getMachineName());

        List<Puzzle> puzzles = room.getPuzzles();
        if (!puzzles.isEmpty()) {
            System.out.println("\n📜 PUZZLES IN THIS ROOM:");
            for (int i = 0; i < puzzles.size(); i++) {
                Puzzle puzzle = puzzles.get(i);
                String status = puzzle.isSolved() ? "✅ SOLVED" : "❌ UNSOLVED";
                System.out.println((i+1) + ". " + puzzle.getDescription() + " [" + status + "]");
                System.out.println("   Reward: " + puzzle.getCoinReward() + " coins");
                if (!puzzle.isSolved()) {
                    System.out.println("   Hint: " + puzzle.getHint());

                    // Show attempts for CodePuzzles
                    if (puzzle instanceof CodePuzzle) {
                        CodePuzzle codePuzzle = (CodePuzzle) puzzle;
                        System.out.println("   Attempts: " + codePuzzle.getCurrentAttempts() + "/" +
                                (codePuzzle.getCurrentAttempts() + codePuzzle.getRemainingAttempts()));
                    }
                }
            }
        }
    }

    private static void solvePuzzles() {
        List<Puzzle> availablePuzzles = game.getAvailablePuzzles();

        if (availablePuzzles.isEmpty()) {
            System.out.println("🎉 All puzzles in this room are already solved!");
            return;
        }

        System.out.println("\n=== SOLVE PUZZLES ===");
        for (int i = 0; i < availablePuzzles.size(); i++) {
            Puzzle puzzle = availablePuzzles.get(i);
            String attemptsInfo = "";

            if (puzzle instanceof CodePuzzle) {
                CodePuzzle codePuzzle = (CodePuzzle) puzzle;
                attemptsInfo = " [Attempts: " + codePuzzle.getRemainingAttempts() + " left]";
            }

            System.out.println((i+1) + ". " + puzzle.getDescription() + attemptsInfo);
        }

        System.out.print("\nSelect a puzzle to solve (1-" + availablePuzzles.size() + ") or 0 to go back: ");
        try {
            int choice = Integer.parseInt(scanner.nextLine());
            if (choice == 0) return;
            if (choice < 1 || choice > availablePuzzles.size()) {
                System.out.println("❌ Invalid puzzle selection.");
                return;
            }

            Puzzle selectedPuzzle = availablePuzzles.get(choice - 1);

            if (selectedPuzzle instanceof CodePuzzle) {
                solveCodePuzzle((CodePuzzle) selectedPuzzle);
            } else if (selectedPuzzle instanceof RiddlePuzzle) {
                solveRiddlePuzzle((RiddlePuzzle) selectedPuzzle);
            } else {
                System.out.println("❌ This puzzle type cannot be solved directly. Try using an item.");
            }

        } catch (NumberFormatException e) {
            System.out.println("❌ Please enter a valid number.");
        }
    }

    private static void solveCodePuzzle(CodePuzzle puzzle) {
        if (!puzzle.hasAttemptsLeft()) {
            System.out.println("💀 No attempts left for this puzzle! Try using a tool item.");
            return;
        }

        System.out.println("\n🔐 CODE PUZZLE:");
        System.out.println(puzzle.getDescription());
        System.out.println("Hint: " + puzzle.getHint());
        System.out.println("Attempts remaining: " + puzzle.getRemainingAttempts());

        System.out.print("Enter your answer: ");
        String answer = scanner.nextLine();

        boolean solved = puzzle.validateCode(answer);
        if (solved) {
            System.out.println("✅ CORRECT! You earned " + puzzle.getCoinReward() + " coins!");
            game.getCurrentPlayer().earnCoins(puzzle.getCoinReward());
        } else {
            System.out.println("❌ Incorrect answer. Attempts remaining: " + puzzle.getRemainingAttempts());
            if (!puzzle.hasAttemptsLeft()) {
                System.out.println("💀 No attempts left for this puzzle!");
            }
        }
    }

    private static void solveRiddlePuzzle(RiddlePuzzle puzzle) {
        System.out.println("\n🤔 RIDDLE PUZZLE:");
        System.out.println("Question: " + puzzle.getQuestion());
        System.out.println("Hint: " + puzzle.getHint());

        System.out.print("Enter your answer: ");
        String answer = scanner.nextLine();

        boolean solved = puzzle.checkAnswer(answer);
        if (solved) {
            System.out.println("✅ CORRECT! You earned " + puzzle.getCoinReward() + " coins!");
            game.getCurrentPlayer().earnCoins(puzzle.getCoinReward());
        } else {
            System.out.println("❌ Incorrect answer. Try again!");
        }
    }

    private static void useGachaMachine() {
        GachaMachine gacha = game.getCurrentGachaMachine();
        Player player = game.getCurrentPlayer();

        System.out.println("\n=== 🎰 " + gacha.getMachineName() + " ===");
        System.out.println("Pull Cost: " + gacha.getPullCost() + " coins");
        System.out.println("Your Coins: " + player.getCoinBalance());

        if (!gacha.canPull(player)) {
            System.out.println("❌ Not enough coins! Solve puzzles to earn more.");
            return;
        }

        System.out.print("Pull from gacha? (y/n): ");
        String choice = scanner.nextLine().toLowerCase();

        if (choice.equals("y") || choice.equals("yes")) {
            try {
                GachaItem pulledItem = gacha.pull(player);
                if (pulledItem != null) {
                    System.out.println("🎉 You pulled: " + pulledItem.getName() + " (" + pulledItem.getRarity() + ")");
                    System.out.println("Description: " + pulledItem.getDescription());
                    player.addItem(pulledItem);
                    System.out.println("💰 Coins remaining: " + player.getCoinBalance());
                } else {
                    System.out.println("❌ No items available in the gacha machine!");
                }
            } catch (NotEnoughCoinsException e) {
                System.out.println("❌ " + e.getMessage());
            }
        }
    }

    private static void viewInventory() {
        List<GachaItem> inventory = game.getCurrentPlayer().getInventory();

        System.out.println("\n=== 🎒 INVENTORY ===");
        if (inventory.isEmpty()) {
            System.out.println("Your inventory is empty. Use the gacha machine to get items!");
            return;
        }

        for (int i = 0; i < inventory.size(); i++) {
            GachaItem item = inventory.get(i);
            String type = "🔑";
            if (item instanceof ToolItem) {
                type = "🛠️";
            }
            System.out.println((i+1) + ". " + type + " " + item.getName() + " (" + item.getRarity() + ")");
            System.out.println("   " + item.getDescription());

            if (item instanceof KeyItem) {
                KeyItem key = (KeyItem) item;
                System.out.println("   Key Color: " + key.getKeyColor() + (key.isMasterKey() ? " (MASTER KEY)" : ""));
            } else if (item instanceof ToolItem) {
                ToolItem tool = (ToolItem) item;
                System.out.println("   Tool Type: " + tool.getToolType());
                System.out.println("   Uses Remaining: " + tool.getUsesRemaining());
            }
        }
    }

    private static void useItemOnPuzzle() {
        List<GachaItem> inventory = game.getCurrentPlayer().getInventory();
        List<Puzzle> availablePuzzles = game.getAvailablePuzzles();

        if (inventory.isEmpty()) {
            System.out.println("❌ Your inventory is empty!");
            return;
        }

        if (availablePuzzles.isEmpty()) {
            System.out.println("❌ No puzzles available to solve!");
            return;
        }

        System.out.println("\n=== USE ITEM ON PUZZLE ===");

        // Show inventory
        System.out.println("🎒 YOUR ITEMS:");
        for (int i = 0; i < inventory.size(); i++) {
            System.out.println((i+1) + ". " + inventory.get(i).getName());
        }

        // Show puzzles
        System.out.println("\n📜 AVAILABLE PUZZLES:");
        for (int i = 0; i < availablePuzzles.size(); i++) {
            System.out.println((i+1) + ". " + availablePuzzles.get(i).getDescription());
        }

        try {
            System.out.print("\nSelect item (1-" + inventory.size() + "): ");
            int itemChoice = Integer.parseInt(scanner.nextLine());

            System.out.print("Select puzzle (1-" + availablePuzzles.size() + "): ");
            int puzzleChoice = Integer.parseInt(scanner.nextLine());

            if (itemChoice < 1 || itemChoice > inventory.size() ||
                    puzzleChoice < 1 || puzzleChoice > availablePuzzles.size()) {
                System.out.println("❌ Invalid selection.");
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
                }
            } catch (WrongItemException e) {
                System.out.println("❌ " + e.getMessage());
            }

        } catch (NumberFormatException e) {
            System.out.println("❌ Please enter valid numbers.");
        }
    }

    private static void saveGame() {
        game.saveGame();
        System.out.println("💾 Game saved successfully!");
    }

    private static void loadGame() {
        if (game.loadGame()) {
            System.out.println("📂 Game loaded successfully!");
            System.out.println("Welcome back, " + game.getCurrentPlayer().getName() + "!");
        } else {
            System.out.println("❌ No save file found or load failed.");
        }
    }

    private static void moveToNextRoom() {
        if (!game.isCurrentRoomComplete()) {
            System.out.println("❌ You must solve all puzzles in this room first!");
            return;
        }

        if (game.getCurrentRoomIndex() >= game.getRooms().size() - 1) {
            System.out.println("🎉 You're in the final room! Solve it to win!");
            return;
        }

        System.out.print("Move to next room? (y/n): ");
        String choice = scanner.nextLine().toLowerCase();

        if (choice.equals("y") || choice.equals("yes")) {
            game.moveToNextRoom();
            System.out.println("🚪 You moved to: " + game.getCurrentRoom().getName());
            System.out.println("📝 " + game.getCurrentRoom().getRoomDescription());
        }
    }

    private static void checkWinCondition() {
        boolean hasWon = game.checkWinCondition();

        if (hasWon) {
            System.out.println("🎉🎉🎉 YOU WIN! You've escaped the dungeon! 🎉🎉🎉");
            System.out.println("You solved all " + game.getRooms().size() + " rooms!");
        } else {
            int solvedRooms = 0;
            for (Room room : game.getRooms()) {
                if (room.isComplete()) solvedRooms++;
            }

            System.out.println("Keep going! You've solved " + solvedRooms +
                    " out of " + game.getRooms().size() + " rooms");

            if (game.isCurrentRoomComplete()) {
                System.out.println("✅ Current room complete! You can move to the next room.");
            } else {
                System.out.println("❌ Current room not complete. Solve all puzzles to proceed.");
            }
        }
    }
}