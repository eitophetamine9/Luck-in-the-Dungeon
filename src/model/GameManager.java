package model;

import exceptions.SaveFileCorruptedException;
import util.FileManager;

import java.io.IOException;
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
    private transient FileManager fileManager;

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

        // ✅ ADD: 5th Room (Temporal Nexus)
        rooms.add(new Room(5, "Temporal Nexus", "A shimmering portal of pure temporal energy. This gateway leads to untold adventures beyond the original timeline..."));

        initializeGameContent();
        rooms.get(0).unlock();
    }

    private void initializeGameContent() {
        initializeRoom1();
        initializeRoom2();
        initializeRoom3();
        initializeRoom4();
        initializeRoom5(); // ✅ ADD: Initialize room 5
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
                "Scientist's code: T = 20, I = 7, M = 15, E = 25. T-I-M-E file, a mathematical pattern perhaps? (This might be helpful later on)",
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
                "90 failed tests and 2 more untested. The atomic number of Uranium..? (This might be useful later on)",
                45,
                2,
                "92",
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
                "Enter the activation code: (T-I-M-E file and Uranium... could be helpful)",
                60,
                3,
                "6792",
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

    // ✅ ADD: Initialize Room 5 (Temporal Nexus)
    private void initializeRoom5() {
        Room room5 = rooms.get(4);

        // Add special "coming soon" puzzle
        CodePuzzle nexusPuzzle = new CodePuzzle(
                "The Temporal Gateway",
                100,
                5,
                "TEMPORAL_NEXUS",
                1,
                false
        );
        nexusPuzzle.markSolved(); // Auto-solve it since it's just a placeholder
        room5.addPuzzle(nexusPuzzle);

        // Add special gacha items for the expansion
        room5.addItemToGacha(new ToolItem("Chrono-Key", "A key that can unlock any temporal gateway",
                Rarity.EPIC, "nexus_key", 1));
        room5.addItemToGacha(new ToolItem("Reality Shard", "A fragment of alternate reality",
                Rarity.RARE, "nexus_component", 1));
        room5.addItemToGacha(new KeyItem("Nexus Pass", "Access all temporal dimensions",
                Rarity.EPIC, "nexus", true));
        room5.addItemToGacha(new ToolItem("Time Paradox Crystal", "Glows with impossible energy",
                Rarity.EPIC, "nexus_component", 1));

        // Lock the room initially - it unlocks after completing room 4
        room5.lock();
    }

    public static void replaceInstance(GameManager newInstance) {
        if (newInstance != null) {
            instance = newInstance;
            // Reinitialize fileManager in the new instance
            instance.fileManager = new FileManager();
        }
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

        // ✅ ADD: Special check for Room 5 (Temporal Nexus)
        if (roomIndex == 4) { // Room 5
            // Check if Room 4 is complete AND player has enough time machine parts
            Room room4 = rooms.get(3);
            int requiredParts = 4;
            int collectedParts = currentPlayer.getTimeMachinePartsCollected();

            if (!room4.isComplete()) {
                return "❌ Complete Room 4 first to unlock the Temporal Nexus!";
            }

            if (collectedParts < requiredParts) {
                return String.format(
                        "🔧 You need %d time machine components to access the Temporal Nexus! (Currently have: %d)\n" +
                                "Complete puzzles and use gacha to collect more parts.",
                        requiredParts, collectedParts
                );
            }

            // If we passed all checks, ensure Room 5 is unlocked
            targetRoom.unlock();
        }

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
            case 4: // ✅ ADD: 5th room objective
                return "This temporal gateway leads to future expansions! The adventure continues...";
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
        return "🌌 BEYOND THE TIME MACHINE...\n\n" +
                "The time machine hums to life, its dials spinning wildly. \n" +
                "You've restored the original timeline, but new temporal anomalies \n" +
                "are appearing across history...\n\n" +
                "Future expansions may include:\n" +
                "• Ancient Egypt temporal rift\n" +
                "• Renaissance art forgery mystery\n" +
                "• Future dystopia intervention\n" +
                "• Parallel reality exploration\n\n" +
                "Your journey through time is just beginning!";
    }

    public String checkSpecialAchievements() {
        if (currentRoomIndex >= 4) {
            return "🏆 ACHIEVEMENT UNLOCKED: Nexus Explorer\n" +
                    "✨ You've reached the Temporal Nexus!\n" +
                    "🌌 You're ready for future adventures!";
        }
        return "";
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

        // ✅ ADD: Special logic for Room 5
        if (roomIndex == 4) {
            Room room4 = rooms.get(3);
            int requiredParts = 4;
            int collectedParts = currentPlayer.getTimeMachinePartsCollected();

            // Check if prerequisites are met
            if (!room4.isComplete()) {
                return "🔒 REQUIREMENTS: Complete Room 4";
            }

            if (collectedParts < requiredParts) {
                return String.format("🔧 NEED %d PARTS: %d/4 collected", requiredParts, collectedParts);
            }

            if (room.isLocked()) {
                return "⚡ READY: Complete Room 4 to unlock";
            }
        }

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

        boolean timeMachineBuilt = isTimeMachineBuilt();

        if (timeMachineBuilt) {
            // Unlock room 5 if it exists
            if (rooms.size() > 4) {
                rooms.get(4).unlock();
            }

            result.put("gameState", "COMPLETED");
            result.put("message", "🎉 CONGRATULATIONS! You've rebuilt the time machine!\n\n" +
                    "⚡ The Temporal Nexus has been unlocked!\n" +
                    "🌌 Continue your adventure in future expansions!");
            result.put("canProceed", true);
        } else {
            Room room4 = rooms.get(3);
            int collectedParts = currentPlayer.getTimeMachinePartsCollected();

            if (room4.isComplete()) {
                result.put("gameState", "BLOCKED");
                result.put("message", String.format(
                        "🔧 Room complete, but you need %d time machine components (have %d).\n" +
                                "Use gacha machines to collect more parts!",
                        4, collectedParts));
            } else {
                result.put("gameState", "IN_PROGRESS");
                result.put("message", "Keep exploring and solving puzzles!");
            }
            result.put("canProceed", false);
        }

        result.put("hasRequiredParts", timeMachineBuilt);
        result.put("collectedParts", currentPlayer.getTimeMachinePartsCollected());
        result.put("requiredParts", 4);

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
            return "🎉 You've reached the Temporal Nexus!\n🌌 The gateway to future adventures awaits...";
        }

        if (!isCurrentRoomComplete()) {
            return "❌ Complete all puzzles in the current room first!";
        }

        // ✅ ADD: Special check for moving to Room 5
        if (currentRoomIndex == 3) { // Moving from Room 4 to Room 5
            Room room4 = rooms.get(3);
            int requiredParts = 4;
            int collectedParts = currentPlayer.getTimeMachinePartsCollected();

            if (!room4.isComplete()) {
                return "❌ Complete all puzzles in Room 4 first!";
            }

            if (collectedParts < requiredParts) {
                return String.format(
                        "🔧 You need %d time machine components to build the time machine!\n" +
                                "Currently have: %d/4\n" +
                                "Use gacha machines to collect more parts.",
                        requiredParts, collectedParts
                );
            }

            // Show special achievement message
            System.out.println("🏆 TIME MACHINE BUILT! Temporal Nexus unlocked!");
        }

        // Move to next room and unlock it
        currentRoomIndex++;
        rooms.get(currentRoomIndex).unlock();

        // Special message for 5th room
        if (currentRoomIndex == 4) { // Room 5 (index 4)
            return "⚡ TEMPORAL NEXUS UNLOCKED!\n\n" +
                    "Congratulations! You've successfully rebuilt the time machine!\n" +
                    "The Temporal Nexus now awaits your exploration...\n\n" +
                    "🚀 More rooms, puzzles, and time travel adventures await!";
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

    // enhanced save with human readable summary
    public String saveGameWithSummary() {
        try {
            if (!validateGameState()) {
                return "⚠️ Save skipped: Game state invalid";
            }

            fileManager.saveGameWithSummary(this);
            return "✅ Game saved successfully!\n📝 Save summary created.";
        } catch (SaveFileCorruptedException e) {
            return "❌ Save failed: " + e.getMessage();
        }
    }

    // enhanced load with better feedback
    public String loadGameWithFeedback() {
        try {
            if (fileManager == null) {
                fileManager = new FileManager();
            }

            if (fileManager.loadGame(this)) {
                // Important: Now refresh the singleton reference
                GameManager newInstance = fileManager.getLoadedInstance();
                if (newInstance != null) {
                    replaceInstance(newInstance);
                }

                // Validate the loaded game
                if (!validateGameState()) {
                    return "⚠️ Loaded game state is invalid!";
                }

                return String.format("✅ Game loaded successfully!\n" +
                                "Welcome back, %s!\n" +
                                "📍 Current room: %s\n" +
                                "🪙 Coins: %d | 🧩 Puzzles: %d",
                        getCurrentPlayer().getName(),
                        getCurrentRoom() != null ? getCurrentRoom().getName() : "Unknown",
                        getCurrentPlayer().getCoinBalance(),
                        getCurrentPlayer().getPuzzlesSolved());
            } else {
                return "❌ No save file found!";
            }
        } catch (Exception e) {
            return "❌ Load failed: " + e.getMessage();
        }
    }

    // gets save file info
    public String getSaveInfo() {
        if (fileManager == null) {
            fileManager = new FileManager();
        }
        return fileManager.getSaveInfo();
    }

    // gets save summary
    public String getSaveSummary() {
        if (fileManager == null) {
            fileManager = new FileManager();
        }
        return fileManager.createSaveSummary(this);
    }

    // deletes save with confirmation
    public String deleteSaveWithConfirmation() {
        if (!saveExists()) {
            return "ℹ️ No save file to delete.";
        }

        try {
            // Get backup info before deletion
            String backupInfo = fileManager.getBackupInfo();
            fileManager.deleteSave();

            return String.format("🗑️ Save deleted successfully!\n\n" +
                            "Previous backups available:\n%s",
                    backupInfo);
        } catch (SaveFileCorruptedException e) {
            return "❌ Delete failed: " + e.getMessage();
        }
    }

    public void restoreGameState(GameManager loadedGame) {
        try {
            if (loadedGame == null) return;

            // Copy player state
            Player loadedPlayer = loadedGame.getCurrentPlayer();
            if (loadedPlayer != null && currentPlayer != null) {
                restorePlayerState(loadedPlayer);
            }

            // Copy room states
            List<Room> loadedRooms = loadedGame.getRooms();
            if (loadedRooms != null && rooms != null) {
                restoreRoomStates(loadedRooms, loadedGame.getCurrentRoomIndex());
            }

            // Copy other fields
            currentRoomIndex = loadedGame.getCurrentRoomIndex();
            gameState = loadedGame.getGameState();

            System.out.println("✅ Game state restored successfully");

        } catch (Exception e) {
            System.err.println("❌ Error restoring game state: " + e.getMessage());
        }
    }

    private void restorePlayerState(Player loadedPlayer) {
        try {
            // Use reflection to copy all non-static fields
            java.lang.reflect.Field[] fields = Player.class.getDeclaredFields();

            for (java.lang.reflect.Field field : fields) {
                // Skip static fields
                if (java.lang.reflect.Modifier.isStatic(field.getModifiers())) {
                    continue;
                }

                field.setAccessible(true);
                Object value = field.get(loadedPlayer);
                field.set(currentPlayer, value);
            }

            System.out.println("✅ Player state restored: " + currentPlayer.getName());

        } catch (Exception e) {
            System.err.println("❌ Error restoring player state: " + e.getMessage());
        }
    }

    private void restoreRoomStates(List<Room> loadedRooms, int loadedRoomIndex) {
        try {
            // Clear current rooms
            rooms.clear();

            // Copy each room
            for (Room loadedRoom : loadedRooms) {
                // Create new room with same properties
                Room newRoom = new Room(
                        loadedRoom.getRoomNumber(),
                        loadedRoom.getName(),
                        loadedRoom.getRoomDescription()
                );

                // Copy lock state
                if (!loadedRoom.isLocked()) {
                    newRoom.unlock();
                }

                // Copy puzzles
                for (Puzzle loadedPuzzle : loadedRoom.getPuzzles()) {
                    // Create a copy of the puzzle with the same solved state
                    Puzzle copiedPuzzle = copyPuzzleWithState(loadedPuzzle);
                    newRoom.addPuzzle(copiedPuzzle);
                }

                rooms.add(newRoom);
            }

            // Set current room index
            currentRoomIndex = loadedRoomIndex;

            System.out.println("✅ Room states restored. Current room: " + currentRoomIndex);

        } catch (Exception e) {
            System.err.println("❌ Error restoring room states: " + e.getMessage());
        }
    }

    private Puzzle copyPuzzleWithState(Puzzle original) throws Exception {
        // Serialize and deserialize to create a deep copy
        java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
        java.io.ObjectOutputStream oos = new java.io.ObjectOutputStream(baos);
        oos.writeObject(original);
        oos.close();

        java.io.ByteArrayInputStream bais = new java.io.ByteArrayInputStream(baos.toByteArray());
        java.io.ObjectInputStream ois = new java.io.ObjectInputStream(bais);
        return (Puzzle) ois.readObject();
    }

    public void debugCurrentState() {
        System.out.println("\n🔍 CURRENT GAME STATE DEBUG:");
        System.out.println("Player: " + currentPlayer.getName());
        System.out.println("Coins: " + currentPlayer.getCoinBalance());
        System.out.println("Current Room Index: " + currentRoomIndex);
        System.out.println("Total Rooms: " + rooms.size());

        for (int i = 0; i < rooms.size(); i++) {
            Room room = rooms.get(i);
            System.out.println("\nRoom " + (i + 1) + ": " + room.getName());
            System.out.println("  Locked: " + room.isLocked());
            System.out.println("  Complete: " + room.isComplete());
            System.out.println("  Puzzles: " + room.getPuzzles().size());

            for (Puzzle puzzle : room.getPuzzles()) {
                System.out.println("    - " + puzzle.getDescription().substring(0, Math.min(30, puzzle.getDescription().length())) +
                        " (Solved: " + puzzle.isSolved() + ")");
            }
        }
        System.out.println("\n" + "=".repeat(50));
    }

    public void restoreGachaState(GameManager loadedGame) {
        try {
            // Restore pity counters for each room's gacha machine
            for (int i = 0; i < rooms.size(); i++) {
                if (i < loadedGame.getRooms().size()) {
                    Room loadedRoom = loadedGame.getRooms().get(i);
                    Room currentRoom = rooms.get(i);

                    // Copy pity counter
                    currentRoom.getGachaMachine().setTotalPullsWithoutEpic(
                            loadedRoom.getGachaMachine().getTotalPullsWithoutEpic()
                    );
                    System.out.println("✅ Restored pity for " + currentRoom.getName() +
                            ": " + loadedRoom.getGachaMachine().getTotalPullsWithoutEpic() + "/10");
                }
            }
        } catch (Exception e) {
            System.err.println("⚠️ Could not restore gacha state: " + e.getMessage());
        }
    }

    public boolean isTimeMachineBuilt() {
        Room room4 = rooms.get(3);
        int requiredParts = 4;
        int collectedParts = currentPlayer.getTimeMachinePartsCollected();

        return room4.isComplete() && collectedParts >= requiredParts;
    }







}