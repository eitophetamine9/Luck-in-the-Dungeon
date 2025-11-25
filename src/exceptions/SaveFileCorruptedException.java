package exceptions;

public class SaveFileCorruptedException extends GameException {
    public SaveFileCorruptedException(String message) {
      super(message);
    }

    public SaveFileCorruptedException(String message, Throwable cause) {
      super(message, cause);
    }
    // Custom constructors for different scenarios
    public SaveFileCorruptedException(String fileName, String operation) {
      super("Failed to " + operation + " file: " + fileName + ". File may be corrupted.");
    }
}
