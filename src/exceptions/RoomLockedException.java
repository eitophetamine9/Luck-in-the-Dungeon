package exceptions;

public class RoomLockedException extends GameException {
    private int roomNumber;
    private String roomName;

    public RoomLockedException(int roomNumber, String roomName) {
        super("Room " + roomNumber + ": " + roomName + " is locked! Complete previous rooms first.");
        this.roomNumber = roomNumber;
        this.roomName = roomName;
    }

    public int getRoomNumber() { return roomNumber; }
    public String getRoomName() { return roomName; }
}