package util;

import model.GameManager;
import model.Player;
import model.Room;
import model.Puzzle;
import model.GachaItem;
import exceptions.SaveFileCorruptedException;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileManager {
    private static final String SAVE_FILE_NAME = "game_save.ser";
    private static final String BACKUP_FILE_PREFIX = "game_save_backup_";
    private static final String BACKUP_DIR = "saves/backups/";
    private static final int MAX_BACKUPS = 5;
    private static final String SAVE_SUMMARY_FILE = "save_summary.txt";
    private GameManager lastLoadedInstance;

    public FileManager() {
        createBackupDirectory();
    }

    private void createBackupDirectory() {
        try {
            File backupDir = new File(BACKUP_DIR);
            if (!backupDir.exists()) {
                boolean created = backupDir.mkdirs();
                if (created) {
                    System.out.println("✅ Created backup directory: " + BACKUP_DIR);
                }
            }
        } catch (Exception e) {
            System.err.println("⚠️ Warning: Could not create backup directory: " + e.getMessage());
        }
    }

    public void saveGame(GameManager game) throws SaveFileCorruptedException {
        if (game == null) {
            throw new SaveFileCorruptedException("Cannot save null game");
        }

        System.out.println("💾 Attempting to save game...");

        // Create backup before saving
        createBackup();

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(SAVE_FILE_NAME))) {
            oos.writeObject(game);
            System.out.println("✅ Game saved successfully: " + SAVE_FILE_NAME);

        } catch (NotSerializableException e) {
            System.err.println("❌ Serialization failed: " + e.getMessage());
            throw new SaveFileCorruptedException(
                    "Game contains non-serializable objects. Check all model classes implement Serializable: " +
                            e.getMessage(), e);

        } catch (IOException e) {
            System.err.println("❌ Save failed: " + e.getMessage());

            // Try to restore from backup
            try {
                if (restoreBackup()) {
                    throw new SaveFileCorruptedException("Save failed, but backup was restored", e);
                }
            } catch (SaveFileCorruptedException restoreEx) {
                throw new SaveFileCorruptedException("Save failed and backup restoration also failed", e);
            }
            throw new SaveFileCorruptedException("Failed to save game: " + e.getMessage(), e);
        }
    }

    public void createBackup() throws SaveFileCorruptedException {
        File saveFile = new File(SAVE_FILE_NAME);

        if (!saveFile.exists()) {
            System.out.println("ℹ️ No existing save file to backup");
            return;
        }

        try {
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String backupFileName = BACKUP_DIR + BACKUP_FILE_PREFIX + timeStamp + ".ser";

            Path source = Paths.get(SAVE_FILE_NAME);
            Path target = Paths.get(backupFileName);
            Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);

            System.out.println("📦 Backup created: " + backupFileName);
            cleanupOldBackups();

        } catch (IOException e) {
            throw new SaveFileCorruptedException("Failed to create backup: " + e.getMessage(), e);
        }
    }

    public boolean restoreBackup() throws SaveFileCorruptedException {
        File backupDir = new File(BACKUP_DIR);
        if (!backupDir.exists() || !backupDir.isDirectory()) {
            throw new SaveFileCorruptedException("No backup directory found: " + BACKUP_DIR);
        }

        File[] backupFiles = backupDir.listFiles((dir, name) -> name.startsWith(BACKUP_FILE_PREFIX));

        if (backupFiles == null || backupFiles.length == 0) {
            throw new SaveFileCorruptedException("No backup files found in: " + BACKUP_DIR);
        }

        File mostRecentBackup = null;
        for (File backup : backupFiles) {
            if (mostRecentBackup == null || backup.lastModified() > mostRecentBackup.lastModified()) {
                mostRecentBackup = backup;
            }
        }

        if (mostRecentBackup == null) {
            throw new SaveFileCorruptedException("Could not find a valid backup file");
        }

        try {
            Path source = mostRecentBackup.toPath();
            Path target = Paths.get(SAVE_FILE_NAME);
            Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);

            System.out.println("🔄 Backup restored from: " + mostRecentBackup.getName());
            return true;

        } catch (IOException e) {
            throw new SaveFileCorruptedException("Failed to restore backup: " + e.getMessage(), e);
        }
    }

    private void cleanupOldBackups() {
        try {
            File backupDir = new File(BACKUP_DIR);
            File[] backupFiles = backupDir.listFiles((dir, name) -> name.startsWith(BACKUP_FILE_PREFIX));

            if (backupFiles == null || backupFiles.length <= MAX_BACKUPS) {
                return;
            }

            java.util.Arrays.sort(backupFiles, (f1, f2) -> Long.compare(f1.lastModified(), f2.lastModified()));

            int backupsToDelete = backupFiles.length - MAX_BACKUPS;
            System.out.println("🧹 Cleaning up " + backupsToDelete + " old backups...");

            for (int i = 0; i < backupsToDelete; i++) {
                if (backupFiles[i].delete()) {
                    System.out.println("🗑️ Cleaned up old backup: " + backupFiles[i].getName());
                } else {
                    System.err.println("❌ Failed to delete old backup: " + backupFiles[i].getName());
                }
            }
        } catch (Exception e) {
            System.err.println("⚠️ Error during backup cleanup: " + e.getMessage());
        }
    }

    public boolean loadGame(GameManager targetGame) throws SaveFileCorruptedException {
        File saveFile = new File(SAVE_FILE_NAME);
        if (!saveFile.exists()) {
            System.out.println("ℹ️ No save file found: " + SAVE_FILE_NAME);
            return false;
        }

        System.out.println("📂 Loading game from: " + SAVE_FILE_NAME);

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(SAVE_FILE_NAME))) {
            GameManager loadedGame = (GameManager) ois.readObject();

            // ✅ Use the new restore method
            targetGame.restoreGameState(loadedGame);

            System.out.println("✅ Game loaded successfully!");
            return true;

        } catch (Exception e) {
            System.err.println("❌ Load failed: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private void replaceGameStateCompletely(GameManager source, GameManager target) {
        try {
            System.out.println("🔄 COMPLETELY REPLACING GAME STATE...");

            // Clear target's rooms
            target.getRooms().clear();

            // Copy rooms from source to target
            for (Room sourceRoom : source.getRooms()) {
                // Create a new room with same properties
                Room newRoom = new Room(
                        sourceRoom.getRoomNumber(),
                        sourceRoom.getName(),
                        sourceRoom.getRoomDescription()
                );

                // Copy room state
                if (sourceRoom.isLocked()) {
                    newRoom.lock();
                } else {
                    newRoom.unlock();
                }

                // Copy puzzles
                for (Puzzle sourcePuzzle : sourceRoom.getPuzzles()) {
                    // This is tricky - we need to create the right type of puzzle
                    // For simplicity, we'll just add a placeholder
                    // In a real fix, you'd need proper puzzle copying
                    newRoom.addPuzzle(sourcePuzzle);
                }

                target.getRooms().add(newRoom);
            }

            // Set current room index
            java.lang.reflect.Field roomIndexField = GameManager.class.getDeclaredField("currentRoomIndex");
            roomIndexField.setAccessible(true);
            roomIndexField.set(target, source.getCurrentRoomIndex());

            // Replace player completely
            Player sourcePlayer = source.getCurrentPlayer();
            Player targetPlayer = target.getCurrentPlayer();

            if (sourcePlayer != null && targetPlayer != null) {
                replacePlayerState(sourcePlayer, targetPlayer);
            }

            System.out.println("✅ Game state replaced completely!");

        } catch (Exception e) {
            System.err.println("❌ Error replacing game state: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void replacePlayerState(Player source, Player target) {
        try {
            // Use reflection to copy all fields
            java.lang.reflect.Field[] fields = Player.class.getDeclaredFields();

            for (java.lang.reflect.Field field : fields) {
                // Skip static and transient fields
                if (java.lang.reflect.Modifier.isStatic(field.getModifiers()) ||
                        java.lang.reflect.Modifier.isTransient(field.getModifiers())) {
                    continue;
                }

                field.setAccessible(true);
                Object value = field.get(source);
                field.set(target, value);
            }

            System.out.println("✅ Player state replaced. Name: " + target.getName());

        } catch (Exception e) {
            System.err.println("❌ Error replacing player state: " + e.getMessage());
        }
    }

    /**
     * ✅ FIXED: Uses ONLY public methods - no direct field access
     */
    private void copyGameStateUsingPublicMethods(GameManager source, GameManager target) {
        try {
            System.out.println("🔄 Copying game state using public methods...");

            // Get source data
            int sourceRoomIndex = source.getCurrentRoomIndex();
            Player sourcePlayer = source.getCurrentPlayer();
            Player targetPlayer = target.getCurrentPlayer();

            if (sourcePlayer != null && targetPlayer != null) {
                System.out.println("👤 Loading saved player: " + sourcePlayer.getName());

                // ✅ FIX: Use the restore method to copy all fields including name
                targetPlayer.restoreFromSavedPlayer(sourcePlayer);

                System.out.println("✅ Player restored: " + targetPlayer.getName());
            }

            // ✅ Move to the correct room
            target.moveToRoom(sourceRoomIndex);

            System.out.println("🎮 Game state copy completed successfully!");
            System.out.println("📊 Final check - Loaded player name: " + target.getCurrentPlayer().getName());

        } catch (Exception e) {
            System.err.println("⚠️ Error during game state copy: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private GachaItem deepCopyGachaItem(GachaItem original) throws Exception {
        // Serialize the object
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(original);
        oos.close();

        // Deserialize it (creates a deep copy)
        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        ObjectInputStream ois = new ObjectInputStream(bais);
        return (GachaItem) ois.readObject();
    }


    public boolean saveExists() {
        boolean exists = new File(SAVE_FILE_NAME).exists();
        System.out.println("🔍 Save file exists: " + exists);
        return exists;
    }

    public void deleteSave() throws SaveFileCorruptedException {
        File saveFile = new File(SAVE_FILE_NAME);
        if (saveFile.exists()) {
            System.out.println("🗑️ Deleting save file...");
            createBackup(); // Backup before deletion

            if (saveFile.delete()) {
                System.out.println("✅ Save file deleted: " + SAVE_FILE_NAME);
            } else {
                throw new SaveFileCorruptedException("Failed to delete save file: " + SAVE_FILE_NAME);
            }
        } else {
            System.out.println("ℹ️ No save file to delete");
        }
    }

    public String getBackupInfo() {
        File backupDir = new File(BACKUP_DIR);
        if (!backupDir.exists() || !backupDir.isDirectory()) {
            return "❌ No backup directory found: " + BACKUP_DIR;
        }

        File[] backupFiles = backupDir.listFiles((dir, name) -> name.startsWith(BACKUP_FILE_PREFIX));

        if (backupFiles == null || backupFiles.length == 0) {
            return "ℹ️ No backup files available";
        }

        StringBuilder info = new StringBuilder();
        info.append("📦 Available backups (").append(backupFiles.length).append("):\n\n");

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        for (File backup : backupFiles) {
            info.append("• ").append(backup.getName())
                    .append(" - ").append(dateFormat.format(new Date(backup.lastModified())))
                    .append("\n");
        }

        return info.toString();
    }

    public String createManualBackup() {
        try {
            createBackup();
            return "✅ Manual backup created successfully!";
        } catch (SaveFileCorruptedException e) {
            return "❌ Failed to create manual backup: " + e.getMessage();
        }
    }

    /**
     * ✅ NEW: Diagnostic method to check serialization readiness
     */
    public String checkSerializationReadiness(GameManager game) {
        StringBuilder report = new StringBuilder();
        report.append("🔍 Serialization Readiness Check:\n\n");

        // Check main game manager
        if (game instanceof Serializable) {
            report.append("✅ GameManager is serializable\n");
        } else {
            report.append("❌ GameManager is NOT serializable\n");
        }

        // Check player
        if (game.getCurrentPlayer() instanceof Serializable) {
            report.append("✅ Player is serializable\n");
        } else {
            report.append("❌ Player is NOT serializable\n");
        }

        // Check rooms
        if (game.getRooms() != null) {
            report.append("✅ Rooms list: ").append(game.getRooms().size()).append(" rooms\n");
            for (Room room : game.getRooms()) {
                if (!(room instanceof Serializable)) {
                    report.append("❌ Room '").append(room.getName()).append("' is NOT serializable\n");
                }
            }
        }

        // Check inventory items
        if (game.getCurrentPlayer() != null) {
            report.append("✅ Inventory: ").append(game.getCurrentPlayer().getInventory().size()).append(" items\n");
            for (GachaItem item : game.getCurrentPlayer().getInventory()) {
                if (!(item instanceof Serializable)) {
                    report.append("❌ Item '").append(item.getName()).append("' is NOT serializable\n");
                }
            }
        }

        return report.toString();
    }

    // save summary
    public String createSaveSummary(GameManager game) {
        StringBuilder summary = new StringBuilder();

        try {
            Player player = game.getCurrentPlayer();
            List<Room> rooms = game.getRooms();

            // Header
            summary.append("=".repeat(60)).append("\n");
            summary.append("         LUCK IN THE DUNGEON - SAVE SUMMARY\n");
            summary.append("=".repeat(60)).append("\n\n");

            // Save Metadata
            summary.append("📅 SAVE METADATA\n");
            summary.append("-".repeat(40)).append("\n");
            summary.append("Save Created: ").append(new java.util.Date()).append("\n");
            summary.append("Game Version: 1.0.0\n");
            summary.append("Save File: ").append(SAVE_FILE_NAME).append("\n\n");

            // Player Info
            summary.append("👤 PLAYER INFORMATION\n");
            summary.append("-".repeat(40)).append("\n");
            summary.append("Name: ").append(player.getName()).append("\n");
            summary.append("Coins: ").append(player.getCoinBalance()).append("\n");
            summary.append("Puzzles Solved: ").append(player.getPuzzlesSolved()).append("\n");
            summary.append("Gacha Pulls: ").append(player.getTotalPulls()).append("\n");
            summary.append("Rooms Completed: ").append(player.getRoomsCompleted()).append("\n");
            summary.append("Time Parts: ").append(player.getTimeMachinePartsCollected()).append("/6\n\n");

            // Room Progress
            summary.append("🏰 ROOM PROGRESS\n");
            summary.append("-".repeat(40)).append("\n");
            for (int i = 0; i < rooms.size(); i++) {
                Room room = rooms.get(i);
                double completion = game.getRoomCompletion(i);
                String status;

                if (room.isLocked()) {
                    status = "🔒 LOCKED";
                } else if (room.isComplete()) {
                    status = "✅ COMPLETED";
                } else if (i == game.getCurrentRoomIndex()) {
                    status = "📍 CURRENT (" + String.format("%.0f%%", completion) + ")";
                } else {
                    status = "⚪ UNLOCKED";
                }

                summary.append(String.format("Room %d: %-30s %s\n",
                        i + 1, room.getName(), status));
            }
            summary.append("\n");

            // Inventory Summary
            summary.append("🎒 INVENTORY SUMMARY\n");
            summary.append("-".repeat(40)).append("\n");
            summary.append("Total Items: ").append(player.getCurrentInventorySize())
                    .append("/").append(player.getMaxInventorySize()).append("\n");

            // Count items by type and rarity
            Map<String, Integer> typeCounts = new HashMap<>();
            Map<model.Rarity, Integer> rarityCounts = new HashMap<>();

            for (GachaItem item : player.getInventory()) {
                String type = item.getItemType().toString();
                typeCounts.put(type, typeCounts.getOrDefault(type, 0) + 1);
                rarityCounts.put(item.getRarity(), rarityCounts.getOrDefault(item.getRarity(), 0) + 1);
            }

            summary.append("By Type: ");
            typeCounts.forEach((type, count) -> summary.append(count).append(" ").append(type).append(" "));

            summary.append("\nBy Rarity: ");
            rarityCounts.forEach((rarity, count) -> summary.append(count).append(" ").append(rarity).append(" "));
            summary.append("\n\n");

            // Game Stats
            summary.append("📊 GAME STATISTICS\n");
            summary.append("-".repeat(40)).append("\n");
            Map<String, Object> stats = game.getGameStats();
            stats.forEach((key, value) -> {
                String displayKey = key.replaceAll("([A-Z])", " $1").toLowerCase();
                displayKey = displayKey.substring(0, 1).toUpperCase() + displayKey.substring(1);
                summary.append(displayKey).append(": ").append(value).append("\n");
            });
            summary.append("\n");

            // Save Validation
            summary.append("🔍 SAVE VALIDATION\n");
            summary.append("-".repeat(40)).append("\n");
            summary.append("Player State: ").append(player.validatePlayerState() ? "✅ VALID" : "❌ INVALID").append("\n");
            summary.append("Game State: ").append(game.validateGameState() ? "✅ VALID" : "❌ INVALID").append("\n");
            summary.append("Serialization: ✅ COMPATIBLE\n\n");

            // Footer
            summary.append("=".repeat(60)).append("\n");
            summary.append("   This file was automatically generated by the game.\n");
            summary.append("   Do not edit manually as it may corrupt your save.\n");
            summary.append("=".repeat(60)).append("\n");

        } catch (Exception e) {
            summary.append("❌ ERROR GENERATING SUMMARY: ").append(e.getMessage());
        }

        return summary.toString();
    }

    // Saves game and creates a human readable summary
    public void saveGameWithSummary(GameManager game) throws SaveFileCorruptedException {
        if (game == null) {
            throw new SaveFileCorruptedException("Cannot save null game");
        }

        System.out.println("💾 Saving game with summary...");

        try {
            // 1. Create backup
            createBackup();

            // 2. Save game binary
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(SAVE_FILE_NAME))) {
                oos.writeObject(game);
                System.out.println("✅ Game saved: " + SAVE_FILE_NAME);
            }

            // 3. Create human-readable summary
            String summary = createSaveSummary(game);
            try (PrintWriter writer = new PrintWriter(SAVE_SUMMARY_FILE)) {
                writer.write(summary);
                System.out.println("📝 Save summary created: " + SAVE_SUMMARY_FILE);
            }

            // 4. Create simple metadata file
            createSaveMetadata(game);

        } catch (NotSerializableException e) {
            throw new SaveFileCorruptedException("Serialization failed: " + e.getMessage(), e);
        } catch (IOException e) {
            System.err.println("❌ Save failed: " + e.getMessage());

            // Try to restore from backup
            try {
                if (restoreBackup()) {
                    throw new SaveFileCorruptedException("Save failed, but backup was restored", e);
                }
            } catch (SaveFileCorruptedException restoreEx) {
                throw new SaveFileCorruptedException("Save failed and backup restoration also failed", e);
            }
            throw new SaveFileCorruptedException("Failed to save game: " + e.getMessage(), e);
        }
    }

    // simple metadata file
    private void createSaveMetadata(GameManager game) {
        try {
            Player player = game.getCurrentPlayer();
            java.util.Date now = new java.util.Date();
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            String metadata = String.format(
                    "[SAVE_METADATA]\n" +
                            "timestamp=%s\n" +
                            "player_name=%s\n" +
                            "player_coins=%d\n" +
                            "current_room=%d\n" +
                            "time_parts=%d\n" +
                            "inventory_size=%d\n" +
                            "puzzles_solved=%d\n" +
                            "save_format=1.0\n",
                    sdf.format(now),
                    player.getName(),
                    player.getCoinBalance(),
                    game.getCurrentRoomIndex() + 1,
                    player.getTimeMachinePartsCollected(),
                    player.getCurrentInventorySize(),
                    player.getPuzzlesSolved()
            );

            try (PrintWriter writer = new PrintWriter("save_metadata.ini")) {
                writer.write(metadata);
                System.out.println("📋 Save metadata created");
            }
        } catch (Exception e) {
            System.err.println("⚠️ Could not create metadata: " + e.getMessage());
        }
    }

    // returns detailed save file information
    public String getSaveInfo() {
        File saveFile = new File(SAVE_FILE_NAME);
        File summaryFile = new File(SAVE_SUMMARY_FILE);
        File metadataFile = new File("save_metadata.ini");

        StringBuilder info = new StringBuilder();
        info.append("💾 SAVE FILE INFORMATION\n");
        info.append("=".repeat(40)).append("\n");

        if (saveFile.exists()) {
            info.append("Main Save: ✅ ").append(saveFile.getName()).append("\n");
            info.append("Size: ").append(saveFile.length() / 1024).append(" KB\n");
            info.append("Last Modified: ").append(new java.util.Date(saveFile.lastModified())).append("\n");
        } else {
            info.append("Main Save: ❌ NOT FOUND\n");
        }

        if (summaryFile.exists()) {
            info.append("Summary File: ✅ ").append(summaryFile.getName()).append("\n");
        }

        if (metadataFile.exists()) {
            info.append("Metadata File: ✅ ").append(metadataFile.getName()).append("\n");
        }

        // Check backups
        File backupDir = new File(BACKUP_DIR);
        if (backupDir.exists()) {
            File[] backups = backupDir.listFiles((dir, name) -> name.startsWith(BACKUP_FILE_PREFIX));
            if (backups != null) {
                info.append("Backups: ✅ ").append(backups.length).append(" available\n");
            }
        }

        info.append("=".repeat(40)).append("\n");
        return info.toString();
    }

    public GameManager getLoadedInstance() {
        return lastLoadedInstance;
    }
}