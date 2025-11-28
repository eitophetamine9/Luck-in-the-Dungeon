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

    public void startNewGame(String playerName){
        currentPlayer = new Player(playerName);
        currentRoomIndex = 0;
        gameState = "PLAYING";

        rooms.clear();
        initializeGame();
    }
    public void startNewGame() {
        startNewGame("Adventurer");
    }
    private void initializeRoom1() {
        Room room1 = rooms.get(0);

        // Puzzle 1: The Missing Sequence (CodePuzzle) - Fixed for 67
        CodePuzzle sequencePuzzle = new CodePuzzle(
                "Find the pattern: 2 (+3)→5 (+6)→11 (+8)→19 (+10)→29 (+12)→41 (+?)→?",
                25,  // coin reward
                1,   // difficulty
                "67", // solution - NOW 67
                3     // max attempts
        );
        room1.addPuzzle(sequencePuzzle);

        // Add gacha items for Room 1 - Better decoder distribution
        room1.addItemToGacha(new KeyItem("Basic Key", "Unlocks Level 2", Rarity.COMMON, "brown", false));
        room1.addItemToGacha(new ToolItem("Number Decoder", "Automatically solves number puzzles", Rarity.COMMON, "decoder", 2));
        room1.addItemToGacha(new KeyItem("Silver Key", "Shiny advanced key", Rarity.RARE, "silver", false));
        room1.addItemToGacha(new ToolItem("Advanced Decoder", "Solves complex patterns", Rarity.RARE, "decoder", 3));
    }

    private void initializeRoom2() {
        Room room2 = rooms.get(1);

        // Puzzle 1: Symbol Cipher (CodePuzzle) - REMOVED ANSWER FROM DESCRIPTION
        CodePuzzle symbolCipher = new CodePuzzle(
                "Decode the message: H ▼ V Y ▲ S ▼ L V D T ▼ D ▼ Y?",
                35,  // coin reward
                2,   // difficulty
                "HAVE YOU SOLVED TODAY", // solution - kept internally
                3     // max attempts
        );
        room2.addPuzzle(symbolCipher);

        // Puzzle 2: Color Pattern Lock (CodePuzzle) - Also removed answer
        CodePuzzle colorPattern = new CodePuzzle(
                "Color sequence: RED, BLUE, GREEN, YELLOW, RED, BLUE, ?, ?", // No answer!
                30,  // coin reward
                2,   // difficulty
                "GREEN YELLOW", // solution
                3     // max attempts
        );
        room2.addPuzzle(colorPattern);

        // Add gacha items for Room 2 - More decoders
        room2.addItemToGacha(new KeyItem("Blue Key", "Unlocks Level 3", Rarity.COMMON, "blue", false));
        room2.addItemToGacha(new ToolItem("Cipher Solver", "Decodes symbol puzzles", Rarity.COMMON, "decoder", 2));
        room2.addItemToGacha(new KeyItem("Master Key", "Opens any lock", Rarity.EPIC, "master", true));
        room2.addItemToGacha(new ToolItem("Pattern Decoder", "Solves color patterns", Rarity.RARE, "decoder", 2));
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
                "Track each turn step by step: N→E→S→W→N→S" // improved hint
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

        // Add gacha items for Room 3 - Junk items and specialized guides
        room3.addItemToGacha(new ToolItem("Ancient Compass", "Guides through direction puzzles", Rarity.RARE, "hintbook", 2));
        room3.addItemToGacha(new ToolItem("Math Solver", "Guides through mathematical puzzles", Rarity.RARE, "decoder", 2));
        room3.addItemToGacha(new ToolItem("Balance Guide", "Explains weight balance puzzles", Rarity.COMMON, "decoder", 1));

        // Junk items - common but useless
        room3.addItemToGacha(new GachaItem("Empty Soda Can", "Just trash...", Rarity.COMMON, ItemType.TOOL) {
            @Override
            public boolean use(Puzzle puzzle) {
                System.out.println("You shake the empty can... nothing happens. It's just trash.");
                return false;
            }
        });
        room3.addItemToGacha(new GachaItem("Broken Tool", "Completely useless", Rarity.COMMON, ItemType.TOOL) {
            @Override
            public boolean use(Puzzle puzzle) {
                System.out.println("The broken tool falls apart in your hands. Worthless.");
                return false;
            }
        });
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
                "642", // answer
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

        // Add gacha items for Room 4 - Powerful guides and some junk
        room4.addItemToGacha(new ToolItem("Quantum Decoder", "Guides through complex patterns", Rarity.EPIC, "decoder", 3));
        room4.addItemToGacha(new ToolItem("Final Hint Book", "Provides detailed puzzle guidance", Rarity.EPIC, "hintbook", 2));
        room4.addItemToGacha(new ToolItem("Binary Guide", "Explains binary conversion", Rarity.RARE, "decoder", 2));

        // Junk items - less common in final room but still possible
        room4.addItemToGacha(new GachaItem("Dusty Relic", "Ancient but useless", Rarity.COMMON, ItemType.TOOL) {
            @Override
            public boolean use(Puzzle puzzle) {
                System.out.println("The relic crumbles to dust. It was worthless.");
                return false;
            }
        });
        room4.addItemToGacha(new GachaItem("Bent Coin", "Can't even spend this", Rarity.COMMON, ItemType.TOOL) {
            @Override
            public boolean use(Puzzle puzzle) {
                System.out.println("The coin is too bent to use. Completely worthless.");
                return false;
            }
        });

        // Only one actual useful key in the final room
        room4.addItemToGacha(new KeyItem("Master Key", "ESCAPE! Final key", Rarity.EPIC, "master", true));
    }

    public void saveGame() {
        try{
            fileManager.saveGame(this);
        } catch (SaveFileCorruptedException e){
            System.err.println("Save failed: " + e.getMessage());
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