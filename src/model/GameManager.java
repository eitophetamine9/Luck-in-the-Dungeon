package model;

import exceptions.RoomLockedException;
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

        // Finalized Rooms (can add more)
        rooms.add(new Room(1, "Ruined Time Lab",
                "The ruins of the original time lab. Broken machinery litters the room. You can still feel the residual temporal energy."));
        rooms.add(new Room(2, "Research Archives",
                "Shelves overflow with research notes. The scientist's mad scrawlings cover every surface. Blueprints for the original machine are hidden somewhere."));
        rooms.add(new Room(3, "Chronal Alchemy Lab",
                "Beakers bubble with strange energies. This is where the scientist created the exotic materials needed for time travel."));
        rooms.add(new Room(4, "Assembly Observatory",
                "A circular room with a central platform - the assembly point for the new time machine. Temporal energy crackles in the air."));

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

        // Puzzle 1: Time Sequence Pattern
        CodePuzzle timeSequence = new CodePuzzle(
                "The scientist left a time sequence: 12:00 → 12:15 → 12:30 → 12:45 → ?",
                25,
                1,
                "1:00",
                3
        );
        room1.addPuzzle(timeSequence);

        // Puzzle 2: Broken Machine Parts
        RiddlePuzzle machineParts = new RiddlePuzzle(
                "I have hands but cannot clap. I have a face but cannot see. I tell time but cannot speak. What am I?",
                30,
                1,
                "What has hands but can't clap, a face but can't see, tells time but can't speak?",
                "clock",
                "Think about time-telling devices"
        );

        // Gacha Items - Basic Time Machine Parts
        room1.addItemToGacha(new ToolItem("Temporal Crystal", "A glowing crystal that hums with energy", Rarity.COMMON, "time_component", 1));
        room1.addItemToGacha(new ToolItem("Broken Gears", "Ancient clockwork mechanisms", Rarity.COMMON, "time_component", 1));
        room1.addItemToGacha(new KeyItem("Lab Access Card", "Opens restricted areas", Rarity.RARE, "blue", false));
        room1.addItemToGacha(new ToolItem("Energy Core", "Powers time devices", Rarity.EPIC, "time_component", 1));
    }

    private void initializeRoom2() {
        Room room2 = rooms.get(1);

        // Puzzle 1: Decode Scientist's Notes
        CodePuzzle scientistNotes = new CodePuzzle(
                "Scientist's code: T = 20, I = 7, M = 15, E = 25. What is TIME?",
                35,
                2,
                "67",
                3
        );
        room2.addPuzzle(scientistNotes);

        // Puzzle 2: Timeline Reconstruction
        RiddlePuzzle timeline = new RiddlePuzzle(
                "Arrange these events: Machine Built → Reality Split → Scientist Rules → Machine Destroyed. What comes after Machine Destroyed?",
                40,
                2,
                "In the sequence: Machine Built, Reality Split, Scientist Rules, Machine Destroyed... what event should happen next?",
                "rebuild machine",
                "What are you trying to do right now?"
        );
        room2.addPuzzle(timeline);

        // Gacha Items - Research & Components
        room2.addItemToGacha(new ToolItem("Time Circuit Board", "Complex electronic pathways", Rarity.COMMON, "time_component", 1));
        room2.addItemToGacha(new ToolItem("Schematic Scrolls", "Partial time machine designs", Rarity.RARE, "blueprint", 2));
        room2.addItemToGacha(new ToolItem("Chronal Stabilizer", "Prevents time paradoxes", Rarity.RARE, "time_component", 1));
        room2.addItemToGacha(new KeyItem("Archive Key", "Opens secret research files", Rarity.EPIC, "silver", false));
    }

    private void initializeRoom3() {
        Room room3 = rooms.get(2);

        // Puzzle 1: Energy Formula
        CodePuzzle energyFormula = new CodePuzzle(
                "Mix elements: 2 parts Temporal Energy + 3 parts Quantum Flux = ? units Chronal Power",
                45,
                2,
                "5",
                3
        );
        room3.addPuzzle(energyFormula);

        // Puzzle 2: Material Properties
        RiddlePuzzle materials = new RiddlePuzzle(
                "I'm needed for time travel but I'm not energy. I can be solid but I'm not metal. I glow but I'm not light. What am I?",
                50,
                3,
                "What material is essential for time machines, glows, but isn't energy or metal?",
                "crystal",
                "Think about what might focus temporal energy"
        );
        room3.addPuzzle(materials);

        // Gacha Items - Advanced Components
        room3.addItemToGacha(new ToolItem("Quantum Flux Capacitor", "The heart of time travel", Rarity.RARE, "time_component", 1));
        room3.addItemToGacha(new ToolItem("Temporal Alloy", "Metal that exists across time", Rarity.RARE, "time_component", 1));
        room3.addItemToGacha(new ToolItem("Reality Anchor", "Keeps you grounded in your timeline", Rarity.EPIC, "time_component", 1));
        room3.addItemToGacha(new GachaItem("Distorted Watch", "Shows multiple times at once", Rarity.COMMON, ItemType.TOOL) {
            @Override
            public boolean use(Puzzle puzzle) {
                System.out.println("The watch spins wildly... but reveals nothing useful.");
                return false;
            }
        });
    }

    private void initializeRoom4() {
        Room room4 = rooms.get(3);

        // Puzzle 1: Final Activation Sequence
        CodePuzzle activation = new CodePuzzle(
                "Enter the activation code: The year the scientist traveled back to",
                60,
                3,
                "2024",
                3
        );
        room4.addPuzzle(activation);

        // Puzzle 2: Time Machine Assembly
        RiddlePuzzle assembly = new RiddlePuzzle(
                "To travel through time, you need: Energy to power it, a Crystal to focus it, Circuits to guide it, and ___ to survive it?",
                70,
                4,
                "What protects the traveler during time jumps?",
                "stabilizer",
                "Think about what keeps you safe in the time stream"
        );
        room4.addPuzzle(assembly);

        // Gacha Items - Final Assembly Parts
        room4.addItemToGacha(new ToolItem("Master Control Chip", "The final piece needed", Rarity.EPIC, "time_component", 1));
        room4.addItemToGacha(new KeyItem("Reality Key", "Opens portals between timelines", Rarity.EPIC, "gold", true));
        room4.addItemToGacha(new ToolItem("Temporal Navigator", "Guides through time streams", Rarity.RARE, "time_component", 1));

        // Junk items - failed experiments
        room4.addItemToGacha(new GachaItem("Melted Components", "The scientist's failed attempts", Rarity.COMMON, ItemType.TOOL) {
            @Override
            public boolean use(Puzzle puzzle) {
                System.out.println("These components are useless... but show how difficult time travel is.");
                return false;
            }
        });
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
        // Check if all rooms are complete AND player has key time machine parts
        for (Room room : rooms) {
            if (!room.isComplete()) {
                return false;
            }
        }

        // Check if player has minimum time machine parts
        int timeParts = currentPlayer.getTimeMachinePartsCollected();
        return timeParts >= 3; // Need at least 3 key components
    }

    // NEW: Jump to specific room
    public void moveToRoom(int roomIndex) throws RoomLockedException {
        if (roomIndex < 0 || roomIndex >= rooms.size()) {
            throw new IllegalArgumentException("Invalid room index: " + roomIndex);
        }

        Room targetRoom = rooms.get(roomIndex);
        if (targetRoom.isLocked()) {
            throw new RoomLockedException(roomIndex + 1, targetRoom.getName());
        }

        this.currentRoomIndex = roomIndex;
    }

    // NEW: Check if room is accessible
    public boolean isRoomAccessible(int roomIndex) {
        return roomIndex >= 0 && roomIndex < rooms.size() &&
                !rooms.get(roomIndex).isLocked();
    }

    // NEW: Get room status for map
    public String getRoomStatus(int roomIndex) {
        if (roomIndex >= rooms.size()) return "INVALID";
        if (rooms.get(roomIndex).isLocked()) return "LOCKED";
        if (roomIndex == currentRoomIndex) return "CURRENT";
        if (rooms.get(roomIndex).isComplete()) return "COMPLETED";
        return "UNLOCKED";  // unlocked but not completed
    }
}