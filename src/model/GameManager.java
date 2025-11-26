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

        // Puzzle 1: The Missing Sequence (CodePuzzle) - NO GACHA REQUIREMENT
        CodePuzzle sequencePuzzle = new CodePuzzle(
                "Find the pattern and solve for the missing number: 2, 5, 11, 19, 29, 41, ?",
                25,
                1,
                "55", // Note: Changed from "67" to "55" (pattern: +3, +6, +8, +10, +12, +14)
                3,
                false  // No gacha requirement for tutorial
        );
        room1.addPuzzle(sequencePuzzle);

        // Keep existing gacha items for Room 1 (no changes needed)
        room1.addItemToGacha(new KeyItem("Basic Key", "Unlocks Level 2", Rarity.COMMON, "brown", false));
        room1.addItemToGacha(new ToolItem("Number Decoder", "Helps with numerical puzzles", Rarity.COMMON, "decoder", 3));
        room1.addItemToGacha(new KeyItem("Silver Key", "Shiny advanced key", Rarity.RARE, "silver", false));
    }

    private void initializeRoom2() {
        Room room2 = rooms.get(1);

        // PUZZLE 1: Symbol Cipher (REQUIRES DECODER TOOL)
        CodePuzzle symbolCipher = new CodePuzzle(
                "Decode the message: H ▼ V Y ▲ S ▼ L V D T ▼ D ▼ Y? " +
                        "(The symbols are encrypted - you need a decoder tool to read them)",
                45,  // Increased coin reward
                2,
                "HAVE YOU SOLVED TODAY",
                3,
                true  // REQUIRES GACHA ITEM
        );
        symbolCipher.setRequiredToolType("decoder"); // Must have decoder tool
        room2.addPuzzle(symbolCipher);

        // PUZZLE 2: Ancient Riddle Lock (REQUIRES SPECIFIC KEY)
        LockPuzzle ancientLock = new LockPuzzle(
                "An ancient lock with hieroglyphic symbols. It requires a special key from this library's gacha machine.",
                50, // Increased reward
                2,
                "hieroglyphic",
                "golden"
        );
        room2.addPuzzle(ancientLock);

        // PUZZLE 3: Color Pattern Lock (existing but enhanced)
        CodePuzzle colorPattern = new CodePuzzle(
                "Color sequence: RED, BLUE, GREEN, YELLOW, RED, BLUE, ?, ? (Next two colors?)",
                35,
                2,
                "GREEN YELLOW",
                3,
                false  // This one doesn't require gacha item
        );
        room2.addPuzzle(colorPattern);

        // UPDATE GACHA ITEMS - ADD REQUIRED ITEMS
        room2.addItemToGacha(new KeyItem("Golden Hieroglyphic Key", "Unlocks ancient lock", Rarity.RARE, "golden", false));
        room2.addItemToGacha(new ToolItem("Ancient Decoder", "Deciphers symbol puzzles", Rarity.COMMON, "decoder", 3));
        room2.addItemToGacha(new ToolItem("Cipher Solver", "Helps decode messages", Rarity.COMMON, "decoder", 2));
        room2.addItemToGacha(new KeyItem("Library Master Key", "Opens any library lock", Rarity.EPIC, "master", true));
    }

    private void initializeRoom3() {
        Room room3 = rooms.get(2);

        // PUZZLE 1: Elemental Combination Lock (REQUIRES ELEMENTAL KEY)
        LockPuzzle elementalLock = new LockPuzzle(
                "A complex alchemical lock with four elemental slots. Requires an elemental key to proceed.",
                60,  // Increased reward
                3,
                "elemental",
                "crystal"
        );
        room3.addPuzzle(elementalLock);

        // PUZZLE 2: Potion Formula Puzzle (REQUIRES ALCHEMY TOOL)
        CodePuzzle potionPuzzle = new CodePuzzle(
                "Balance the alchemical equation: ☿ + ♁ + ♀ = ? " +
                        "(You need an alchemy tool to measure the elements)",
                55,  // Increased reward
                3,
                "42",
                3,
                true  // REQUIRES GACHA ITEM
        );
        potionPuzzle.setRequiredToolType("alchemy");
        room3.addPuzzle(potionPuzzle);

        // PUZZLE 3: Directional Lock (existing)
        RiddlePuzzle directionalLock = new RiddlePuzzle(
                "Direction puzzle: Start NORTH, RIGHT×3, LEFT×2, RIGHT×1. Where are you facing?",
                35,
                3,
                "Start facing NORTH. Turn RIGHT 90° × 3, LEFT 90° × 2, RIGHT 90° × 1. What direction?",
                "SOUTH",
                "Track each turn step by step",
                false  // Doesn't require gacha
        );
        room3.addPuzzle(directionalLock);

        // UPDATE GACHA ITEMS - ADD ELEMENTAL ITEMS
        room3.addItemToGacha(new KeyItem("Crystal Elemental Key", "Unlocks elemental mechanisms", Rarity.RARE, "crystal", false));
        room3.addItemToGacha(new ToolItem("Alchemy Measurer", "Measures potion ingredients", Rarity.COMMON, "alchemy", 2));
        room3.addItemToGacha(new ToolItem("Elemental Compass", "Navigates alchemical puzzles", Rarity.RARE, "alchemy", 3));
        room3.addItemToGacha(new KeyItem("Philosopher's Key", "Ultimate alchemical key", Rarity.EPIC, "philosopher", true));
    }

    private void initializeRoom4() {
        Room room4 = rooms.get(3);

        // PUZZLE 1: Star Map Alignment (REQUIRES ASTRONOMICAL TOOL)
        CodePuzzle starMap = new CodePuzzle(
                "Align the constellations: ♈ ♉ ♊ ♋ ♌ ♍ ♎ ♏ ♐ ♑ ♒ ♓ " +
                        "(Requires astronomical tool to read star patterns)",
                75,  // Increased reward
                4,
                "ARIES-TAURUS-GEMINI",
                3,
                true  // REQUIRES GACHA ITEM
        );
        starMap.setRequiredToolType("astronomy");
        room4.addPuzzle(starMap);

        // PUZZLE 2: Cosmic Final Lock (REQUIRES ULTIMATE KEY)
        LockPuzzle cosmicLock = new LockPuzzle(
                "The final barrier - a lock powered by starlight. Only a cosmic key can open the way to freedom.",
                85,  // Increased reward
                4,
                "cosmic",
                "starlight"
        );
        room4.addPuzzle(cosmicLock);

        // PUZZLE 3: Mathematical Riddle (existing)
        RiddlePuzzle mathRiddle = new RiddlePuzzle(
                "Three-digit number puzzle: digits sum to 12, first=3×third, second=first-2",
                55,
                4,
                "I am a three-digit number. My digits add up to 12. The first digit is three times the third digit. The second digit is two less than the first digit. What number am I?",
                "642",
                "Let digits be A, B, C. A + B + C = 12, A = 3C, B = A - 2",
                false  // Doesn't require gacha
        );
        room4.addPuzzle(mathRiddle);

        // UPDATE GACHA ITEMS - ADD COSMIC ITEMS
        room4.addItemToGacha(new KeyItem("Starlight Cosmic Key", "Final key to escape", Rarity.EPIC, "starlight", false));
        room4.addItemToGacha(new ToolItem("Astronomical Sextant", "Measures celestial patterns", Rarity.RARE, "astronomy", 2));
        room4.addItemToGacha(new ToolItem("Star Chart Decoder", "Deciphers cosmic codes", Rarity.EPIC, "astronomy", 1));
        room4.addItemToGacha(new KeyItem("Universal Master Key", "Opens all cosmic locks", Rarity.EPIC, "universal", true));
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

    public boolean canSolvePuzzle(Puzzle puzzle) {
        if (puzzle.requiresGachaItem()) {
            String requiredTool = puzzle.getRequiredToolType();
            return currentPlayer.hasRequiredItem(requiredTool);
        }
        return true;
    }

    public String getPuzzleRequirementMessage(Puzzle puzzle) {
        if (puzzle.requiresGachaItem()) {
            String requiredTool = puzzle.getRequiredToolType();
            if (!currentPlayer.hasRequiredItem(requiredTool)) {
                return "🔒 This puzzle requires a " + requiredTool + " from the gacha machine!";
            }
        }
        return null;
    }
}