package model;

public class LockPuzzle extends Puzzle {
    private String requiredKeyType;
    private String lockColor;

    public LockPuzzle(String description, int coinReward, int difficultyLevel,
                      String requiredKeyType, String lockColor) {
        super(description, coinReward, difficultyLevel);
        this.requiredKeyType = requiredKeyType;
        this.lockColor = lockColor;
    }

    @Override
    public boolean attemptSolve(GachaItem item) {
        if (isSolved) {
            return false;
        }

        if (item instanceof KeyItem) {
            KeyItem key = (KeyItem) item;
            if (key.getKeyColor().equals(lockColor) || key.isMasterKey()) {
                markSolved();
                return true;
            }
        }
        return false;
    }

    public String getRequiredKeyType() {
        return requiredKeyType;
    }

    public String getLockColor() {
        return lockColor;
    }
}