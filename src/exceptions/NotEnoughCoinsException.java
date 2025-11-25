package exceptions;

public class NotEnoughCoinsException extends GameException {
  private int required;
  private int available;

  public NotEnoughCoinsException(int required, int available) {
        super("Not enough coins! Required: " + required + ", Available: " + available);
        this.required = required;
        this.available = available;
  }

  public NotEnoughCoinsException(String message){
    super(message);
  }

  public int getRequired() {
    return required;
  }

  public int getAvailable() {
    return available;
  }
}
