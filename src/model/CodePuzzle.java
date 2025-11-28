package model;

public class CodePuzzle extends Puzzle {
    private String solution;
    private String playerInput;
    //private int maxAttempts;
    //private int currentAttempts;

    public CodePuzzle(String description, int coinReward, int difficultyLevel,
                      String solution, int maxAttempts, boolean requiresGachaItem){
        super(description, coinReward, difficultyLevel, requiresGachaItem);
        this.solution = solution;
        this.playerInput = "";
    }

    public CodePuzzle(String description, int coinReward, int difficultyLevel,
                      String solution, int maxAttempts){
        this(description, coinReward, difficultyLevel, solution, maxAttempts, false); // Default to no gacha requirement
    }

    // ✅ FIX: Allow tools to work on these puzzles
    @Override
    public boolean attemptSolve(GachaItem item){
        if(item instanceof ToolItem) {
            ToolItem tool = (ToolItem) item;
            return tool.use(this); // Let the tool decide if it can help
        }
        return false;
    }
    /*
    public int getRemainingAttempts() {
        return maxAttempts - currentAttempts;
    }

    public boolean hasAttemptsLeft() {
        return currentAttempts < maxAttempts;
    }

    public int getCurrentAttempts() {
        return currentAttempts;
    }
    */

    @Override
    public String getHint() {
        return "Analyze the pattern carefully. Look for the mathematical sequences or logical progressions";
    }

    public void setPlayerInput(String input){
        this.playerInput = input;
    }

    public boolean validateCode(String code){
        if(code.equals(solution)){
            markSolved();
            return true;
        }
        return false;
    }

    public String getSolution() {
        return solution;
    }
}
