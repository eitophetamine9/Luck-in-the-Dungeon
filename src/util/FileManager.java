package util;

import model.*;
import exceptions.SaveFileCorruptedException;
import java.io.*;
import java.util.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileManager {
    private static final String SAVE_DIRECTORY = "saves/";
    private static final String SAVE_FILE = "game_save.json";

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

            //saving basic game data
            writer.println("PLAYER_NAME: " + player.getName());
            writer.println("COINS:" + player.getCoinBalance());
            writer.println("CURRENT_ROOM:" + gameManager.getCurrentRoomIndex());
            writer.println("GAME_STATE:" + gameManager.getGameState());

            // save inventory
            writer.println("INVENTORY_START");
            List<GachaItem> inventory = player.getInventory();
            for(GachaItem item : inventory) {
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
            System.out.println("Game saved successfully to: " + filePath);
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

            //clear game state
            gameManager.startNewGame();

            while((line = br.readLine()) != null){
                if(line.equals("INVENTORY_START")) {
                    readingInventory = true;
                    continue;
                } else if(line.equals("INVETORY_END")){
                    readingInventory = false;
                    continue;
                }

                if(readingInventory){
                    // Processing inventory items
                    if(line.startsWith("KEY_ITEM:")) {
                        String[] parts = line.substring("KEY_ITEMS:".length()).split(":");
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
                            String tooltType = parts[2];
                            int uses = Integer.parseInt(parts[3]);

                            ToolItem tool = new ToolItem(name, "Restored tool", rarity, tooltType, uses);
                            player.addItem(tool);
                        }
                    }
                } else {
                    // process game state
                    if(line.startsWith("PLAYER_NAME:")) {
                        // player name loaded during startNewGame()
                    } else if (line.startsWith("COINS:")) {
                        int coins = Integer.parseInt(line.substring("COINS:".length()));
                        // add coins to player (starting coins + saved coins)
                        player.earnCoins(coins);
                    } else if (line.startsWith("CURRENT_ROOM:")){
                        int roomIndex = Integer.parseInt(line.substring("CURRENT_ROOM".length()));
                        // move to the saved room
                        for (int i = 0; i < roomIndex; i++){
                            gameManager.moveToNextRoom();
                        }
                    }
                }
            }

            System.out.println("Game loaded successfully from: " + filePath);
            return true;
        } catch (IOException e) {
            throw new SaveFileCorruptedException("Failed to load game from: " + filePath, e);
        } catch (NumberFormatException e){
            throw new SaveFileCorruptedException("Save file contains invalid data", e);
        } catch (IllegalArgumentException e){
            throw new SaveFileCorruptedException("Save file contains invalid enum values", e);
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
