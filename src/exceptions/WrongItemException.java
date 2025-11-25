package exceptions;

import model.GachaItem;
import model.Puzzle;

public class WrongItemException extends GameException {
    private GachaItem item;
    private Puzzle puzzle;

    public WrongItemException(GachaItem item, Puzzle puzzle){
        super("The item '" + item.getName() + "' cannot be used on this puzzle: " +
                puzzle.getDescription());
        this.item = item;
        this.puzzle = puzzle;
    }

    public WrongItemException(String message) {
        super(message);
    }

    public GachaItem getItem() { return item; }

    public Puzzle getPuzzle() { return puzzle; }
}
