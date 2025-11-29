package util;

import model.GameManager;
import exceptions.SaveFileCorruptedException;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.text.SimpleDateFormat;

public class FileManager {
    private static final String SAVE_FILE_NAME = "game_save.ser";
    private static final String BACKUP_FILE_PREFIX = "game_save_backup_";
    private static final String BACKUP_DIR = "saves/backups/";
    private static final int MAX_BACKUPS = 5;

    public FileManager() {
        createBackupDirectory();
    }

    private void createBackupDirectory() {
        try {
            File backupDir = new File(BACKUP_DIR);
            if (!backupDir.exists()) {
                backupDir.mkdirs();
            }
        } catch (Exception e) {
            System.err.println("Warning: Could not create backup directory: " + e.getMessage());
        }
    }

    public void saveGame(GameManager game) throws SaveFileCorruptedException {
        if (game == null) {
            throw new SaveFileCorruptedException("Cannot save null game");
        }

        createBackup();

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(SAVE_FILE_NAME))) {
            oos.writeObject(game);
            System.out.println("Game saved successfully: " + SAVE_FILE_NAME);
        } catch (IOException e) {
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
            return;
        }

        try {
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String backupFileName = BACKUP_DIR + BACKUP_FILE_PREFIX + timeStamp + ".ser";

            Path source = Paths.get(SAVE_FILE_NAME);
            Path target = Paths.get(backupFileName);
            Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);

            System.out.println("Backup created: " + backupFileName);
            cleanupOldBackups();

        } catch (IOException e) {
            throw new SaveFileCorruptedException("Failed to create backup: " + e.getMessage(), e);
        }
    }

    public boolean restoreBackup() throws SaveFileCorruptedException {
        File backupDir = new File(BACKUP_DIR);
        if (!backupDir.exists() || !backupDir.isDirectory()) {
            throw new SaveFileCorruptedException("No backup directory found");
        }

        File[] backupFiles = backupDir.listFiles((dir, name) -> name.startsWith(BACKUP_FILE_PREFIX));

        if (backupFiles == null || backupFiles.length == 0) {
            throw new SaveFileCorruptedException("No backup files found");
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

            System.out.println("Backup restored from: " + mostRecentBackup.getName());
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
            for (int i = 0; i < backupsToDelete; i++) {
                if (backupFiles[i].delete()) {
                    System.out.println("Cleaned up old backup: " + backupFiles[i].getName());
                } else {
                    System.err.println("Failed to delete old backup: " + backupFiles[i].getName());
                }
            }
        } catch (Exception e) {
            System.err.println("Error during backup cleanup: " + e.getMessage());
        }
    }

    public boolean loadGame(GameManager game) throws SaveFileCorruptedException {
        File saveFile = new File(SAVE_FILE_NAME);
        if (!saveFile.exists()) {
            return false;
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(SAVE_FILE_NAME))) {
            GameManager loadedGame = (GameManager) ois.readObject();
            copyGameState(loadedGame, game);

            System.out.println("Game loaded successfully: " + SAVE_FILE_NAME);
            return true;

        } catch (IOException | ClassNotFoundException e) {
            try {
                if (restoreBackup()) {
                    return loadGame(game);
                }
            } catch (SaveFileCorruptedException restoreEx) {
                throw new SaveFileCorruptedException("Load failed and backup restoration also failed", e);
            }
            throw new SaveFileCorruptedException("Failed to load game: " + e.getMessage(), e);
        }
    }

    private void copyGameState(GameManager source, GameManager target) {
        try {
            target.currentRoomIndex = source.currentRoomIndex;
            target.gameState = source.gameState;

            if (source.currentPlayer != null && target.currentPlayer != null) {
                target.currentPlayer.setCoins(source.currentPlayer.getCoinBalance());
                target.currentPlayer.setPuzzlesSolved(source.currentPlayer.getPuzzlesSolved());
                target.currentPlayer.setTotalPulls(source.currentPlayer.getTotalPulls());
                target.currentPlayer.setTotalCoinsEarned(source.currentPlayer.getTotalCoinsEarned());
                target.currentPlayer.setTotalCoinsSpent(source.currentPlayer.getTotalCoinsSpent());
                target.currentPlayer.setTimeMachinePartsCollected(source.currentPlayer.getTimeMachinePartsCollected());
            }

        } catch (Exception e) {
            System.err.println("Warning: Partial game state copy: " + e.getMessage());
        }
    }

    public boolean saveExists() {
        return new File(SAVE_FILE_NAME).exists();
    }

    public void deleteSave() throws SaveFileCorruptedException {
        File saveFile = new File(SAVE_FILE_NAME);
        if (saveFile.exists()) {
            createBackup();

            if (saveFile.delete()) {
                System.out.println("Save file deleted: " + SAVE_FILE_NAME);
            } else {
                throw new SaveFileCorruptedException("Failed to delete save file");
            }
        }
    }

    public String getBackupInfo() {
        File backupDir = new File(BACKUP_DIR);
        if (!backupDir.exists() || !backupDir.isDirectory()) {
            return "No backup directory found";
        }

        File[] backupFiles = backupDir.listFiles((dir, name) -> name.startsWith(BACKUP_FILE_PREFIX));

        if (backupFiles == null || backupFiles.length == 0) {
            return "No backup files available";
        }

        StringBuilder info = new StringBuilder();
        info.append("Available backups (").append(backupFiles.length).append("):\n");

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
            return "Manual backup created successfully!";
        } catch (SaveFileCorruptedException e) {
            return "Failed to create manual backup: " + e.getMessage();
        }
    }
}