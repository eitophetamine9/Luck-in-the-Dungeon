package util;

import exceptions.InventoryFullException;
import model.*;
import exceptions.SaveFileCorruptedException;
import java.io.*;

public class FileManager {
    private static final String SAVE_DIRECTORY = "saves/";
    private static final String SAVE_FILE = "game_save.dat";

    public FileManager() {
        createSaveDirectory();
    }

    private void createSaveDirectory() {
        File directory = new File(SAVE_DIRECTORY);
        if(!directory.exists()) {
            boolean created = directory.mkdirs();
            if(!created)
                System.err.println("Warning: Could not create save directory");
        }
    }

    public void saveGame(GameManager gameManager) throws SaveFileCorruptedException {
        String filePath = SAVE_DIRECTORY + SAVE_FILE;
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            Player player = gameManager.getCurrentPlayer();

            // ✅ COMPLETE: Save all game state
            writer.println("PLAYER_NAME:" + player.getName());
            writer.println("COINS:" + player.getCoinBalance());
            writer.println("CURRENT_ROOM:" + gameManager.getCurrentRoomIndex());
            writer.println("GAME_STATE:" + gameManager.getGameState());

            // ✅ COMPLETE: Save ALL player statistics
            writer.println("STATS_PUZZLES_SOLVED:" + player.getPuzzlesSolved());
            writer.println("STATS_ROOMS_COMPLETED:" + player.getRoomsCompleted());
            writer.println("STATS_TOTAL_PULLS:" + player.getTotalPulls());
            writer.println("STATS_COINS_EARNED:" + player.getTotalCoinsEarned());
            writer.println("STATS_COINS_SPENT:" + player.getTotalCoinsSpent());

            // ✅ COMPLETE: Save room and puzzle states
            writer.println("ROOM_STATES_START");
            for (int i = 0; i < gameManager.getRooms().size(); i++) {
                Room room = gameManager.getRooms().get(i);
                writer.println("ROOM:" + i + ":LOCKED:" + room.isLocked());

                for (int j = 0; j < room.getPuzzles().size(); j++) {
                    Puzzle puzzle = room.getPuzzles().get(j);
                    writer.println("PUZZLE:" + i + ":" + j + ":SOLVED:" + puzzle.isSolved());
                }
            }
            writer.println("ROOM_STATES_END");

            // Save inventory
            writer.println("INVENTORY_START");
            for(GachaItem item : player.getInventory()) {
                if(item instanceof KeyItem) {
                    KeyItem key = (KeyItem) item;
                    writer.println("KEY_ITEM:" + key.getName() + ":" + key.getRarity() +
                            ":" + key.getKeyColor() + ":" + key.isMasterKey());
                } else if (item instanceof ToolItem) {
                    ToolItem tool = (ToolItem) item;
                    writer.println("TOOL_ITEM:" + tool.getName() + ":" + tool.getRarity() +
                            ":" + tool.getToolType() + ":" + tool.getUsesRemaining());
                }
            }
            writer.println("INVENTORY_END");

            System.out.println("✅ Game saved successfully to: " + filePath);
        } catch (IOException e){
            throw new SaveFileCorruptedException("Failed to save game to: " + filePath, e);
        }
    }

    public boolean loadGame(GameManager gameManager) throws SaveFileCorruptedException {
        String filePath = SAVE_DIRECTORY + SAVE_FILE;
        File saveFile = new File(filePath);

        if(!saveFile.exists()) return false;

        try(BufferedReader br = new BufferedReader(new FileReader(saveFile))){
            String line;
            Player player = gameManager.getCurrentPlayer();
            boolean readingInventory = false;
            boolean readingRoomStates = false;

            // Clear and reset game state
            gameManager.startNewGame();
            player = gameManager.getCurrentPlayer(); // Get fresh player instance

            while((line = br.readLine()) != null){
                if(line.equals("INVENTORY_START")) {
                    readingInventory = true;
                    continue;
                } else if(line.equals("INVENTORY_END")){
                    readingInventory = false;
                    continue;
                } else if(line.equals("ROOM_STATES_START")) {
                    readingRoomStates = true;
                    continue;
                } else if(line.equals("ROOM_STATES_END")) {
                    readingRoomStates = false;
                    continue;
                }

                if(readingInventory){
                    // Process inventory items
                    if(line.startsWith("KEY_ITEM:")) {
                        String[] parts = line.substring("KEY_ITEM:".length()).split(":");
                        if(parts.length >= 4){
                            String name = parts[0];
                            Rarity rarity = Rarity.valueOf(parts[1]);
                            String keyColor = parts[2];
                            boolean isMasterKey = Boolean.parseBoolean(parts[3]);

                            KeyItem key = new KeyItem(name, "Restored key", rarity, keyColor, isMasterKey);
                            player.addItem(key);
                        }
                    } else if(line.startsWith("TOOL_ITEM:")){
                        String[] parts = line.substring("TOOL_ITEM:".length()).split(":");
                        if(parts.length >= 4){
                            String name = parts[0];
                            Rarity rarity = Rarity.valueOf(parts[1]);
                            String toolType = parts[2];
                            int uses = Integer.parseInt(parts[3]);

                            ToolItem tool = new ToolItem(name, "Restored tool", rarity, toolType, uses);
                            player.addItem(tool);
                        }
                    }
                } else if (readingRoomStates) {
                    // ✅ COMPLETE: Process room and puzzle states
                    if (line.startsWith("ROOM:")) {
                        String[] parts = line.substring("ROOM:".length()).split(":");
                        int roomIndex = Integer.parseInt(parts[0]);
                        boolean locked = Boolean.parseBoolean(parts[2]);
                        Room room = gameManager.getRooms().get(roomIndex);
                        if (locked) room.lock();
                        else room.unlock();
                    } else if (line.startsWith("PUZZLE:")) {
                        String[] parts = line.substring("PUZZLE:".length()).split(":");
                        int roomIndex = Integer.parseInt(parts[0]);
                        int puzzleIndex = Integer.parseInt(parts[1]);
                        boolean solved = Boolean.parseBoolean(parts[3]);
                        Room room = gameManager.getRooms().get(roomIndex);
                        Puzzle puzzle = room.getPuzzles().get(puzzleIndex);
                        if (solved) puzzle.markSolved();
                    }
                } else {
                    // ✅ COMPLETE: Process ALL statistics with the new setters
                    if (line.startsWith("COINS:")) {
                        int coins = Integer.parseInt(line.substring("COINS:".length()));
                        player.setCoins(coins);
                    } else if (line.startsWith("CURRENT_ROOM:")){
                        int roomIndex = Integer.parseInt(line.substring("CURRENT_ROOM:".length()));
                        // Move to saved room
                        for (int i = 0; i < roomIndex; i++){
                            gameManager.moveToNextRoom();
                        }
                    } else if (line.startsWith("STATS_PUZZLES_SOLVED:")) {
                        int puzzlesSolved = Integer.parseInt(line.substring("STATS_PUZZLES_SOLVED:".length()));
                        player.setPuzzlesSolved(puzzlesSolved); // ✅ NOW WORKS - setter exists
                    } else if (line.startsWith("STATS_ROOMS_COMPLETED:")) {
                        int roomsCompleted = Integer.parseInt(line.substring("STATS_ROOMS_COMPLETED:".length()));
                        player.setRoomsCompleted(roomsCompleted); // ✅ NOW WORKS - setter exists
                    } else if (line.startsWith("STATS_TOTAL_PULLS:")) {
                        int totalPulls = Integer.parseInt(line.substring("STATS_TOTAL_PULLS:".length()));
                        player.setTotalPulls(totalPulls); // ✅ NOW WORKS - setter exists
                    } else if (line.startsWith("STATS_COINS_EARNED:")) {
                        int coinsEarned = Integer.parseInt(line.substring("STATS_COINS_EARNED:".length()));
                        player.setTotalCoinsEarned(coinsEarned); // ✅ NOW WORKS - setter exists
                    } else if (line.startsWith("STATS_COINS_SPENT:")) {
                        int coinsSpent = Integer.parseInt(line.substring("STATS_COINS_SPENT:".length()));
                        player.setTotalCoinsSpent(coinsSpent); // ✅ NOW WORKS - setter exists
                    }
                    // ✅ ALL STATISTICS NOW HANDLED - no more "Add other statistics here" comment needed
                }
            }

            System.out.println("✅ Game loaded successfully from: " + filePath);
            return true;
        } catch (IOException e) {
            throw new SaveFileCorruptedException("Failed to load game from: " + filePath, e);
        } catch (NumberFormatException e){
            throw new SaveFileCorruptedException("Save file contains invalid data", e);
        } catch (IllegalArgumentException e){
            throw new SaveFileCorruptedException("Save file contains invalid enum values", e);
        } catch (InventoryFullException e) {
            throw new SaveFileCorruptedException("Inventory full while loading saved items", e);
        }
    }

    public boolean saveExists(){
        File saveFile = new File(SAVE_DIRECTORY + SAVE_FILE);
        return saveFile.exists() && saveFile.length() > 0;
    }

    public void deleteSave() throws SaveFileCorruptedException {
        String filePath = SAVE_DIRECTORY + SAVE_FILE;
        File saveFile = new File(filePath);
        if(saveFile.exists()) {
            boolean deleted = saveFile.delete();
            if(!deleted) throw new SaveFileCorruptedException("Could not delete save file: " + filePath);
            System.out.println("Save file deleted: " + filePath);
        }
    }
}