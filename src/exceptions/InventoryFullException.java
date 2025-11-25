package exceptions;

import model.Inventory;

public class InventoryFullException extends GameException {
    private int currentSize;
    private int maxCapacity;

    public InventoryFullException(int currentSize, int maxCapacity){
      super("Inventory is full! Current: " + currentSize + " /" + maxCapacity);
      this.currentSize = currentSize;
      this.maxCapacity = maxCapacity;
    }

    public InventoryFullException(String message) {
        super(message);
    }

  public int getCurrentSize() {
    return currentSize;
  }

  public int getMaxCapacity(){
      return maxCapacity;
  }
}
