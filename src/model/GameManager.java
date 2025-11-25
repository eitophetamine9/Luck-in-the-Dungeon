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
        rooms.add(new Room(1, "Starting Chamber", "A simple room to learn the basice"));
        rooms.add(new Room(2, "Ancient Library", "A room filled with ancient books"));
        rooms.add(new Room(3, "Alchemy Lab", "A nysterious laboratory"));
        rooms.add(new Room(4, "Final Observatory", "The final room"));

        rooms.get(0).unlock();
    }

    public void startNewGame() {
        currentPlayer = new Player("Adventurer");
        currentRoomIndex = 0;
        gameState = "PLAYING";
        initializeGame();
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
}