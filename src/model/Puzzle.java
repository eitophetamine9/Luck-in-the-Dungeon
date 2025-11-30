package model;

import java.util.HashMap;
import java.util.Map;
import java.io.Serializable;

public abstract class Puzzle implements Serializable {
    private static final long serialVersionUID = 1L;
    protected String description;
    protected boolean isSolved;
    protected int coinReward;
    protected int difficultyLevel;
    protected boolean requiresGachaItem;
    protected String requiredToolType;
    protected int attempts;
    protected long startTime;
    protected long solveTime;

    public Puzzle(String description, int coinReward, int difficultyLevel, boolean requiresGachaItem){
        this.description = description;
        this.isSolved = false;
        this.coinReward = coinReward;
        this.difficultyLevel = difficultyLevel;
        this.requiresGachaItem = requiresGachaItem;
        this.requiredToolType = null;
        this.attempts = 0;
        this.startTime = System.currentTimeMillis();
        this.solveTime = 0;
    }

    public boolean requiresGachaItem() {
        return requiresGachaItem;
    }

    public String getRequiredToolType() {
        return requiredToolType;
    }

    public void setRequiredToolType(String toolType) {
        this.requiredToolType = toolType;
    }

    public abstract boolean attemptSolve(GachaItem item);

    public String getHint() {
        return "Think carefully about what you need to solve this";
    }

    // === ENHANCED PROGRESS TRACKING ===
    public void recordAttempt() {
        this.attempts++;
    }

    public void markSolved() {
        this.isSolved = true;
        this.solveTime = System.currentTimeMillis() - startTime;
    }

    public Map<String, Object> getPuzzleStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("attempts", attempts);
        stats.put("solved", isSolved);
        stats.put("difficulty", difficultyLevel);
        stats.put("reward", coinReward);

        if (isSolved) {
            stats.put("solveTimeMs", solveTime);
            stats.put("solveTimeSeconds", solveTime / 1000);
            stats.put("efficiency", attempts == 0 ? 0 : (double) solveTime / attempts);
        }

        return stats;
    }

    public String getDifficultyStars() {
        return "⭐".repeat(difficultyLevel) + "☆".repeat(5 - difficultyLevel);
    }

    public String getProgressiveHint() {
        if (attempts == 0) {
            return "Try examining the puzzle carefully. Look for patterns or sequences.";
        } else if (attempts == 1) {
            return getHint();
        } else if (attempts >= 2) {
            return getDetailedHint();
        }
        return getHint();
    }

    public String getDetailedHint() {
        return "Think about the story context. This puzzle relates to time travel concepts.";
    }

    // === EXISTING METHODS ===
    public int getCoinReward(){return  coinReward;}
    public boolean isSolved(){return isSolved;}
    public String getDescription(){return description;}
    public int getDifficultyLevel(){return difficultyLevel;}
}