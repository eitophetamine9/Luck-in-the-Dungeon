package model;

import exceptions.SaveFileCorruptedException;
import util.FileManager;

import java.util.ArrayList;
import java.util.List;

public class GameManager {
    private static GameManager instance;
    private Player currentPlayer;
    private List<Room> rooms;
    private int currentRoomIndex;
    private String gameState;
    private FileManager fileManager;

    private GameManager(){
        this.rooms = new ArrayList<>();
        this.currentRoomIndex = 0;
        this.gameState = "MENU";
        this.fileManager = new FileManager();
        initializeGame();
    }

    public static GameManager getInstance(){
        if(instance == null) instance = new GameManager();

        return instance;
    }

    private void initializeGame(){
        currentPlayer = new Player("Adventurer");

        // Create basic rooms, content will be later
        rooms.add(new Room(1, "Starting Chamber", "A simple room to learn the basics"));
        rooms.add(new Room(2, "Ancient Library", "A room filled with ancient books"));
        rooms.add(new Room(3, "Alchemy Lab", "A mysterious laboratory"));
        rooms.add(new Room(4, "Final Observatory", "The final room"));

        initializeGameContent();
        rooms.get(0).unlock();
    }

    // ADD THIS METHOD: Initialize all puzzle content
    private void initializeGameContent() {
        initializeRoom1();
        initializeRoom2();
        initializeRoom3();
        initializeRoom4();
    }

    private void initializeRoom1() {
        Room room1 = rooms.get(0);

        // Puzzle 1: The Missing Sequence (CodePuzzle)
        CodePuzzle sequencePuzzle = new CodePuzzle(
                "Find the pattern and solve for the missing number: 2, 5, 11, 19, 29, 41, ?",
                25,  // coin reward
                1,   // difficulty
                "67", // solution
                3     // max attempts
        );
        room1.addPuzzle(sequencePuzzle);

        // Add gacha items for Room 1
        room1.addItemToGacha(new KeyItem("Basic Key", "Unlocks Level 2", Rarity.COMMON, "brown", false));
        room1.addItemToGacha(new ToolItem("Number Decoder", "Helps with numerical puzzles", Rarity.COMMON, "decoder", 3));
        room1.addItemToGacha(new KeyItem("Silver Key", "Shiny advanced key", Rarity.RARE, "silver", false));
    }

    private void initializeRoom2() {
        Room room2 = rooms.get(1);

        // Puzzle 1: Symbol Cipher (CodePuzzle)
        CodePuzzle symbolCipher = new CodePuzzle(
                "Decode the message: H ▼ V Y ▲ S ▼ L V D T ▼ D ▼ Y? (Answer: HAVE YOU SOLVED TODAY?)",
                35,  // coin reward
                2,   // difficulty
                "HAVE YOU SOLVED TODAY", // solution
                3     // max attempts
        );
        room2.addPuzzle(symbolCipher);

        // Puzzle 2: Color Pattern Lock (CodePuzzle)
        CodePuzzle colorPattern = new CodePuzzle(
                "Color sequence: RED, BLUE, GREEN, YELLOW, RED, BLUE, ?, ? (Next two colors?)",
                30,  // coin reward
                2,   // difficulty
                "GREEN YELLOW", // solution
                3     // max attempts
        );
        room2.addPuzzle(colorPattern);

        // Add gacha items for Room 2
        room2.addItemToGacha(new KeyItem("Blue Key", "Unlocks Level 3", Rarity.COMMON, "blue", false));
        room2.addItemToGacha(new ToolItem("Cipher Solver", "Helps decode messages", Rarity.COMMON, "decoder", 2));
        room2.addItemToGacha(new KeyItem("Master Key", "Opens any lock", Rarity.EPIC, "master", true));
    }

    private void initializeRoom3() {
        Room room3 = rooms.get(2);

        // Puzzle 1: Hieroglyphic Math (CodePuzzle)
        CodePuzzle hieroglyphicMath = new CodePuzzle(
                "Egyptian math: %=5, i=10, c=50, c=100. Solve: (τ-τ̄) + (ε-1) × τ̄",
                40,  // coin reward
                3,   // difficulty
                "460", // solution
                3     // max attempts
        );
        room3.addPuzzle(hieroglyphicMath);

        // Puzzle 2: Directional Lock (RiddlePuzzle)
        RiddlePuzzle directionalLock = new RiddlePuzzle(
                "Direction puzzle: Start NORTH, RIGHT×3, LEFT×2, RIGHT×1. Where are you facing?",
                35,  // coin reward
                3,   // difficulty
                "Start facing NORTH. Turn RIGHT 90° × 3, LEFT 90° × 2, RIGHT 90° × 1. What direction?",
                "SOUTH", // answer
                "Track each turn step by step" // hint
        );
        room3.addPuzzle(directionalLock);

        // Puzzle 3: Weight Balance (CodePuzzle)
        CodePuzzle weightBalance = new CodePuzzle(
                "Scale balance: Left=5kg+8kg+Xkg, Right=6kg+10kg+7kg. Find X if balanced.",
                45,  // coin reward
                3,   // difficulty
                "10", // solution
                3     // max attempts
        );
        room3.addPuzzle(weightBalance);

        // Add gacha items for Room 3
        room3.addItemToGacha(new KeyItem("Silver Key", "Unlocks Level 4", Rarity.RARE, "silver", false));
        room3.addItemToGacha(new ToolItem("Math Solver", "Helps with calculations", Rarity.RARE, "decoder", 2));
        room3.addItemToGacha(new ToolItem("Ancient Compass", "Helps with direction puzzles", Rarity.RARE, "hintbook", 2));
    }

    private void initializeRoom4() {
        Room room4 = rooms.get(3);

        // Puzzle 1: Binary Sequence (CodePuzzle)
        CodePuzzle binarySequence = new CodePuzzle(
                "Convert binary to text: 01001000 01100101 01101100 01101100 01101111",
                50,  // coin reward
                4,   // difficulty
                "HELLO", // solution
                3     // max attempts
        );
        room4.addPuzzle(binarySequence);

        // Puzzle 2: Mathematical Riddle (RiddlePuzzle)
        RiddlePuzzle mathRiddle = new RiddlePuzzle(
                "Three-digit number puzzle: digits sum to 12, first=3×third, second=first-2",
                55,  // coin reward
                4,   // difficulty
                "I am a three-digit number. My digits add up to 12. The first digit is three times the third digit. The second digit is two less than the first digit. What number am I?",
                "642", // answer - CORRECTED from 842 to 642
                "Let digits be A, B, C. A + B + C = 12, A = 3C, B = A - 2" // hint
        );
        room4.addPuzzle(mathRiddle);

        // Puzzle 3: Final Sequence Challenge (CodePuzzle)
        CodePuzzle finalSequence = new CodePuzzle(
                "Complete the pattern: 1, 1, 2, 3, 5, 8, 13, 21, ?",
                60,  // coin reward
                4,   // difficulty
                "34", // solution
                3     // max attempts
        );
        room4.addPuzzle(finalSequence);

        // Add gacha items for Room 4
        room4.addItemToGacha(new KeyItem("Master Key", "ESCAPE! Final key", Rarity.EPIC, "master", true));
        room4.addItemToGacha(new ToolItem("Quantum Decoder", "Solves complex patterns", Rarity.EPIC, "decoder", 3));
        room4.addItemToGacha(new ToolItem("Final Hint Book", "Reveals ultimate solutions", Rarity.EPIC, "hintbook", 1));
    }

    public void startNewGame() {
        currentPlayer = new Player("Adventurer");
        currentRoomIndex = 0;
        gameState = "PLAYING";

        // Reset all rooms - lock all first, then unlock only first room
        for (Room room : rooms) {
            room.lock(); // Lock all rooms first
        }
        rooms.get(0).unlock(); // Then unlock only first room
    }

    public void saveGame() {
        try{
            fileManager.saveGame(this);
        } catch (SaveFileCorruptedException e){
            System.err.println("Save failed: " + e.getMessage());
            // UI LATER
        }

    }

    public boolean loadGame() {
        try {
            return fileManager.loadGame(this);
        } catch (SaveFileCorruptedException e){
            System.err.println("Load failed: " + e.getMessage());
            return false;
        }
    }

    public boolean saveExists() {return fileManager.saveExists();}

    public void deleteSave(){
        try{
            fileManager.deleteSave();
        } catch (SaveFileCorruptedException e) {
            System.err.println("Delete failed: " + e.getMessage());
        }
    }

    public void moveToNextRoom(){
        if(currentRoomIndex < rooms.size() - 1) {
            currentRoomIndex++;
            rooms.get(currentRoomIndex).unlock();
        } else {
            gameState = "COMPLETED";
        }
    }

    public Room getCurrentRoom(){
        if(currentRoomIndex < rooms.size()) return rooms.get(currentRoomIndex);

        return null;
    }

    public Player getCurrentPlayer(){return currentPlayer;}
    public String getGameState(){return gameState;}
    public int getCurrentRoomIndex(){return currentRoomIndex;}
    public List<Room> getRooms() {
        return new ArrayList<>(rooms);
    }

    // ADD THIS METHOD: Check if current room is complete
    public boolean isCurrentRoomComplete() {
        Room currentRoom = getCurrentRoom();
        return currentRoom != null && currentRoom.isComplete();
    }

    // ADD THIS METHOD: Get available puzzles in current room
    public List<Puzzle> getAvailablePuzzles() {
        Room currentRoom = getCurrentRoom();
        return currentRoom != null ? currentRoom.getAvailablePuzzles() : new ArrayList<>();
    }

    // ADD THIS METHOD: Get current room's gacha machine
    public GachaMachine getCurrentGachaMachine() {
        Room currentRoom = getCurrentRoom();
        return currentRoom != null ? currentRoom.getGachaMachine() : null;
    }
    public boolean checkWinCondition() {
        // Check if all rooms are complete
        for (Room room : rooms) {
            if (!room.isComplete()) {
                return false;
            }
        }
        return true;
        // OR if you want win when final room is complete:
        // return gameState.equals("COMPLETED");
    }
}