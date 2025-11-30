package model;

import exceptions.SaveFileCorruptedException;
import util.FileManager;
import java.io.Serializable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class GameManager implements Serializable {
    private static final long serialVersionUID = 1L;
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
        currentPlayer = new Player("Time Traveler");

        rooms.add(new Room(1, "Ruined Time Lab", "The ruins of the original time lab. Broken machinery litters the room. You can still feel the residual temporal energy."));
        rooms.add(new Room(2, "Research Archives", "Shelves overflow with research notes. The scientist's mad scrawlings cover every surface. Blueprints for the original machine are hidden somewhere."));
        rooms.add(new Room(3, "Chronal Alchemy Lab", "Beakers bubble with strange energies. This is where the scientist created the exotic materials needed for time travel."));
        rooms.add(new Room(4, "Assembly Observatory", "A circular room with a central platform - the assembly point for the new time machine. Temporal energy crackles in the air."));

        initializeGameContent();
        rooms.get(0).unlock();
    }

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
        startNewGame("Time Traveler");
    }

    private void initializeRoom1() {
        Room room1 = rooms.get(0);

        CodePuzzle timeSequence = new CodePuzzle(
                "The scientist left a time sequence: 12:00 → 12:15 → 12:30 → 12:45 → ?",
                25,
                1,
                "1:00",
                3
        );
        room1.addPuzzle(timeSequence);

        RiddlePuzzle machineParts = new RiddlePuzzle(
                "I have hands but cannot clap. I have a face but cannot see. I tell time but cannot speak. What am I?",
                30,
                1,
                "What has hands but can't clap, a face but can't see, tells time but can't speak?",
                "clock",
                "Think about time-telling devices"
        );
        room1.addPuzzle(machineParts);

        room1.addItemToGacha(new ToolItem("Temporal Crystal", "A glowing crystal that hums with energy", Rarity.COMMON, "time_component", 1));
        room1.addItemToGacha(new ToolItem("Broken Gears", "Ancient clockwork mechanisms", Rarity.COMMON, "time_component", 1));
        room1.addItemToGacha(new KeyItem("Lab Access Card", "Opens restricted areas", Rarity.RARE, "blue", false));
        room1.addItemToGacha(new ToolItem("Energy Core", "Powers time devices", Rarity.EPIC, "time_component", 1));
    }

    private void initializeRoom2() {
        Room room2 = rooms.get(1);

        CodePuzzle scientistNotes = new CodePuzzle(
                "Scientist's code: T = 20, I = 7, M = 15, E = 25. What is TIME?",
                35,
                2,
                "67",
                3
        );
        room2.addPuzzle(scientistNotes);

        RiddlePuzzle timeline = new RiddlePuzzle(
                "Arrange these events: Machine Built → Reality Split → Scientist Rules → Machine Destroyed. What comes after Machine Destroyed?",
                40,
                2,
                "In the sequence: Machine Built, Reality Split, Scientist Rules, Machine Destroyed... what event should happen next?",
                "rebuild machine",
                "What are you trying to do right now?"
        );
        room2.addPuzzle(timeline);

        room2.addItemToGacha(new ToolItem("Time Circuit Board", "Complex electronic pathways", Rarity.COMMON, "time_component", 1));
        room2.addItemToGacha(new ToolItem("Schematic Scrolls", "Partial time machine designs", Rarity.RARE, "blueprint", 2));
        room2.addItemToGacha(new ToolItem("Chronal Stabilizer", "Prevents time paradoxes", Rarity.RARE, "time_component", 1));
        room2.addItemToGacha(new KeyItem("Archive Key", "Opens secret research files", Rarity.EPIC, "silver", false));
    }

    private void initializeRoom3() {
        Room room3 = rooms.get(2);

        CodePuzzle energyFormula = new CodePuzzle(
                "Mix elements: 2 parts Temporal Energy + 3 parts Quantum Flux = ? units Chronal Power",
                45,
                2,
                "5",
                3
        );
        room3.addPuzzle(energyFormula);

        RiddlePuzzle materials = new RiddlePuzzle(
                "I'm needed for time travel but I'm not energy. I can be solid but I'm not metal. I glow but I'm not light. What am I?",
                50,
                3,
                "What material is essential for time machines, glows, but isn't energy or metal?",
                "crystal",
                "Think about what might focus temporal energy"
        );
        room3.addPuzzle(materials);

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

        CodePuzzle activation = new CodePuzzle(
                "Enter the activation code: The year the scientist traveled back to",
                60,
                3,
                "2024",
                3
        );
        room4.addPuzzle(activation);

        RiddlePuzzle assembly = new RiddlePuzzle(
                "To travel through time, you need: Energy to power it, a Crystal to focus it, Circuits to guide it, and ___ to survive it?",
                70,
                4,
                "What protects the traveler during time jumps?",
                "stabilizer",
                "Think about what keeps you safe in the time stream"
        );
        room4.addPuzzle(assembly);

        room4.addItemToGacha(new ToolItem("Master Control Chip", "The final piece needed", Rarity.EPIC, "time_component", 1));
        room4.addItemToGacha(new KeyItem("Reality Key", "Opens portals between timelines", Rarity.EPIC, "gold", true));
        room4.addItemToGacha(new ToolItem("Temporal Navigator", "Guides through time streams", Rarity.RARE, "time_component", 1));

        room4.addItemToGacha(new GachaItem("Melted Components", "The scientist's failed attempts", Rarity.COMMON, ItemType.TOOL) {
            @Override
            public boolean use(Puzzle puzzle) {
                System.out.println("These components are useless... but show how difficult time travel is.");
                return false;
            }
        });
    }

    // === ENHANCED SAVE SYSTEM ===
    public String saveGameWithBackup() {
        try {
            if (!validateGameState()) {
                return "⚠️ Save skipped: Game state invalid";
            }

            fileManager.saveGame(this);
            return "✅ Game saved successfully! Backup created.";
        } catch (SaveFileCorruptedException e) {
            return "❌ Save failed: " + e.getMessage();
        }
    }

    public String autoSave() {
        if (!validateGameState()) {
            return "⚠️ Auto-save skipped: Game state invalid";
        }

        try {
            fileManager.saveGame(this);
            return "🔄 Game auto-saved.";
        } catch (SaveFileCorruptedException e) {
            return "❌ Auto-save failed: " + e.getMessage();
        }
    }

    public boolean validateGameState() {
        try {
            if (currentPlayer == null) {
                System.err.println("Game state invalid: No current player");
                return false;
            }
            if (rooms == null || rooms.isEmpty()) {
                System.err.println("Game state invalid: No rooms available");
                return false;
            }
            if (currentRoomIndex < 0 || currentRoomIndex >= rooms.size()) {
                System.err.println("Game state invalid: Invalid current room index: " + currentRoomIndex);
                return false;
            }
            if (!currentPlayer.validatePlayerState()) {
                System.err.println("Game state invalid: Player state validation failed");
                return false;
            }
            return true;
        } catch (Exception e) {
            System.err.println("Game state validation failed: " + e.getMessage());
            return false;
        }
    }

    // === ENHANCED NAVIGATION ===
    public String moveToRoom(int roomIndex) {
        System.out.println("🚀 Attempting to move to room " + roomIndex);

        if (roomIndex < 0 || roomIndex >= rooms.size()) {
            return "❌ Invalid room number!";
        }

        Room targetRoom = rooms.get(roomIndex);
        System.out.println("🎯 Target room: " + targetRoom.getName() + " (Locked: " + targetRoom.isLocked() + ")");

        if (targetRoom.isLocked()) {
            return String.format("🔒 Room %d: %s is locked! Complete previous rooms first.",
                    roomIndex + 1, targetRoom.getName());
        }

        int previousRoom = currentRoomIndex;
        currentRoomIndex = roomIndex;

        System.out.println("✅ Successfully moved from room " + previousRoom + " to room " + currentRoomIndex);

        // ✅ DEBUG: Show puzzles in the new room
        Room newRoom = getCurrentRoom();
        System.out.println("🎲 Puzzles in new room: " + newRoom.getPuzzles().size());
        for (Puzzle p : newRoom.getPuzzles()) {
            System.out.println("   - " + p.getDescription().substring(0, Math.min(30, p.getDescription().length())));
        }

        if (previousRoom != roomIndex) {
            String transitionMessage = getRoomTransitionMessage(previousRoom, roomIndex);
            System.out.println(transitionMessage);
            return transitionMessage;
        }

        return String.format("🔄 You're already in %s", targetRoom.getName());
    }

    private String getRoomTransitionMessage(int fromRoom, int toRoom) {
        String[] transitions = {
                "🚀 You step through the temporal gateway...",
                "⏰ The air shimmers as you move between rooms...",
                "🔮 Reality bends around you as you travel...",
                "⚡ Chronal energy crackles during your transition...",
                "🌌 You feel the fabric of time shift around you..."
        };

        Random random = new Random();
        String transition = transitions[random.nextInt(transitions.length)];

        return String.format("%s\n🏰 You arrive at: %s",
                transition, getCurrentRoom().getName());
    }

    // === STORY PROGRESSION ===
    public String getStoryContext() {
        int partsCollected = currentPlayer.getTimeMachinePartsCollected();
        int totalRooms = rooms.size();
        int completedRooms = (int) rooms.stream().filter(Room::isComplete).count();

        StringBuilder story = new StringBuilder();
        story.append("🕰️ TIME MACHINE RECONSTRUCTION\n");
        story.append("================================\n");

        story.append(String.format("Progress: %d/%d rooms completed\n", completedRooms, totalRooms));
        story.append(String.format("Components: %d/6 time machine parts collected\n\n", partsCollected));

        story.append("CURRENT OBJECTIVE:\n");
        if (completedRooms < totalRooms) {
            Room current = getCurrentRoom();
            story.append("• ").append(getCurrentObjective()).append("\n");
            story.append("• Location: ").append(current.getName()).append("\n");
        } else {
            story.append("• Assemble the time machine with all collected parts\n");
            story.append("• Restore the original timeline\n");
        }

        return story.toString();
    }

    public String getCurrentObjective() {
        switch (currentRoomIndex) {
            case 0: return "Search the ruined lab for basic time machine components and clues about the original design.";
            case 1: return "Decode the scientist's research notes and find schematics in the archives.";
            case 2: return "Create exotic materials in the alchemy lab needed for time travel.";
            case 3:
                int parts = currentPlayer.getTimeMachinePartsCollected();
                if (parts >= 4) {
                    return "Assemble the time machine using all collected components.";
                } else {
                    return String.format("Collect more time machine components (%d/4 needed).", parts);
                }
            default: return "Explore and solve puzzles to progress.";
        }
    }

    public String getNextStepHint() {
        Room current = getCurrentRoom();

        if (!current.isComplete()) {
            List<Puzzle> available = current.getAvailablePuzzles();
            if (!available.isEmpty()) {
                Puzzle nextPuzzle = available.get(0);
                return String.format("💡 Try solving: %s", nextPuzzle.getDescription());
            }
        }

        if (currentRoomIndex < rooms.size() - 1 && current.isComplete()) {
            return "💡 This room is complete! Move to the next room to continue.";
        }

        if (currentPlayer.getTimeMachinePartsCollected() < 4 && currentRoomIndex == 3) {
            return "💡 You need more time machine components. Try using the gacha machine or explore previous rooms.";
        }

        return "💡 Explore the room and interact with objects to find puzzles.";
    }

    // === ACHIEVEMENTS & PROGRESS ===
    public Map<String, Object> getAchievements() {
        Map<String, Object> achievements = new HashMap<>();

        int totalPuzzles = rooms.stream().mapToInt(r -> r.getPuzzles().size()).sum();
        int solvedPuzzles = rooms.stream()
                .mapToInt(r -> (int) r.getPuzzles().stream().filter(Puzzle::isSolved).count())
                .sum();

        achievements.put("puzzleMaster", solvedPuzzles >= totalPuzzles);
        achievements.put("puzzlesSolved", solvedPuzzles);
        achievements.put("totalPuzzles", totalPuzzles);

        achievements.put("timeTraveler", currentPlayer.getTimeMachinePartsCollected() >= 6);
        achievements.put("gachaEnthusiast", currentPlayer.getTotalPulls() >= 20);

        long completedRooms = rooms.stream().filter(Room::isComplete).count();
        achievements.put("roomExplorer", completedRooms >= rooms.size());
        achievements.put("coinCollector", currentPlayer.getTotalCoinsEarned() >= 500);

        return achievements;
    }

    // === EXPANSION HOOKS ===
    public boolean canExpandStory() {
        boolean allComplete = rooms.stream().allMatch(Room::isComplete);
        boolean hasAllParts = currentPlayer.getTimeMachinePartsCollected() >= 6;
        return allComplete && hasAllParts && "COMPLETED".equals(gameState);
    }

    public String getExpansionTeaser() {
        if (!canExpandStory()) {
            return "Complete the current story to unlock future adventures!";
        }

        return """
               🌌 BEYOND THE TIME MACHINE...
               
               The time machine hums to life, its dials spinning wildly. 
               You've restored the original timeline, but new temporal anomalies 
               are appearing across history...
               
               Future expansions may include:
               • Ancient Egypt temporal rift
               • Renaissance art forgery mystery  
               • Future dystopia intervention
               • Parallel reality exploration
               
               Your journey through time is just beginning!
               """;
    }

    // === UTILITY METHODS ===
    public Map<String, Object> getGameStats() {
        Map<String, Object> stats = new HashMap<>();

        int totalRooms = rooms.size();
        int completedRooms = 0;
        int totalPuzzles = 0;
        int solvedPuzzles = 0;

        for (Room room : rooms) {
            if (room.isComplete()) completedRooms++;
            totalPuzzles += room.getPuzzles().size();
            solvedPuzzles += (int) room.getPuzzles().stream()
                    .filter(Puzzle::isSolved)
                    .count();
        }

        stats.put("totalRooms", totalRooms);
        stats.put("completedRooms", completedRooms);
        stats.put("totalPuzzles", totalPuzzles);
        stats.put("solvedPuzzles", solvedPuzzles);
        stats.put("currentRoom", currentRoomIndex + 1);
        stats.put("playerCoins", currentPlayer.getCoinBalance());
        stats.put("timeMachineParts", currentPlayer.getTimeMachinePartsCollected());
        stats.put("inventorySize", currentPlayer.getCurrentInventorySize());
        stats.put("totalPulls", currentPlayer.getTotalPulls());

        return stats;
    }

    public boolean canAffordGachaPull() {
        GachaMachine currentGacha = getCurrentGachaMachine();
        return currentGacha != null && currentPlayer.getCoinBalance() >= currentGacha.getPullCost();
    }

    public double getRoomCompletion(int roomIndex) {
        if (roomIndex < 0 || roomIndex >= rooms.size()) return 0.0;

        Room room = rooms.get(roomIndex);
        if (room.getPuzzles().isEmpty()) return 0.0;

        long solved = room.getPuzzles().stream().filter(Puzzle::isSolved).count();
        return (double) solved / room.getPuzzles().size() * 100;
    }

    public double getCurrentRoomCompletion() {
        return getRoomCompletion(currentRoomIndex);
    }

    public boolean isRoomAccessible(int roomIndex) {
        return roomIndex >= 0 && roomIndex < rooms.size() && !rooms.get(roomIndex).isLocked();
    }

    public String getRoomStatus(int roomIndex) {
        if (roomIndex < 0 || roomIndex >= rooms.size()) return "INVALID";

        Room room = rooms.get(roomIndex);
        if (room.isLocked()) return "🔒 LOCKED";
        if (roomIndex == currentRoomIndex) return "📍 CURRENT";
        if (room.isComplete()) return "✅ COMPLETED";
        return "⚪ UNLOCKED";
    }

    public List<Room> getAccessibleRooms() {
        List<Room> accessible = new ArrayList<>();
        for (int i = 0; i < rooms.size(); i++) {
            if (isRoomAccessible(i)) {
                accessible.add(rooms.get(i));
            }
        }
        return accessible;
    }

    // === WIN CONDITIONS ===
    public Map<String, Object> checkWinConditionDetailed() {
        Map<String, Object> result = new HashMap<>();

        boolean allRoomsComplete = true;
        for (Room room : rooms) {
            if (!room.isComplete()) {
                allRoomsComplete = false;
                break;
            }
        }

        int requiredParts = 4;
        int collectedParts = currentPlayer.getTimeMachinePartsCollected();
        boolean hasEnoughParts = collectedParts >= requiredParts;

        if (allRoomsComplete && hasEnoughParts) {
            result.put("gameState", "COMPLETED");
            result.put("message", "🎉 CONGRATULATIONS! You've rebuilt the time machine and can now restore the timeline!");
            result.put("canProceed", true);
        } else if (allRoomsComplete && !hasEnoughParts) {
            result.put("gameState", "BLOCKED");
            result.put("message", String.format(
                    "🔧 All rooms complete, but you need %d time machine components (have %d). Keep exploring!",
                    requiredParts, collectedParts));
            result.put("canProceed", false);
        } else {
            result.put("gameState", "IN_PROGRESS");
            result.put("message", "Keep exploring and solving puzzles!");
            result.put("canProceed", false);
        }

        result.put("roomsComplete", allRoomsComplete);
        result.put("hasRequiredParts", hasEnoughParts);
        result.put("collectedParts", collectedParts);
        result.put("requiredParts", requiredParts);

        return result;
    }

    public boolean checkWinCondition() {
        Map<String, Object> detailed = checkWinConditionDetailed();
        return "COMPLETED".equals(detailed.get("gameState"));
    }

    // === EXISTING METHODS ===
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

    public String moveToNextRoom() {
        if (currentRoomIndex >= rooms.size() - 1) {
            return "🎉 You've completed all rooms!";
        }

        if (!isCurrentRoomComplete()) {
            return "❌ Complete all puzzles in the current room first!";
        }

        // Move to next room and unlock it
        currentRoomIndex++;
        rooms.get(currentRoomIndex).unlock();

        // ✅ IMPORTANT: Reset puzzle attempts and state for the new room
        Room newRoom = getCurrentRoom();
        for (Puzzle puzzle : newRoom.getPuzzles()) {
            // Reset puzzle state for the new room (but keep them unsolved)
            // This ensures puzzles are fresh when entering a new room
            puzzle.recordAttempt(); // This just increments attempts counter
        }

        return "🚀 Advanced to: " + getCurrentRoom().getName();
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

    public boolean isCurrentRoomComplete() {
        Room currentRoom = getCurrentRoom();
        return currentRoom != null && currentRoom.isComplete();
    }

    public List<Puzzle> getAvailablePuzzles() {
        Room currentRoom = getCurrentRoom();
        return currentRoom != null ? currentRoom.getAvailablePuzzles() : new ArrayList<>();
    }

    public GachaMachine getCurrentGachaMachine() {
        Room currentRoom = getCurrentRoom();
        return currentRoom != null ? currentRoom.getGachaMachine() : null;
    }


}