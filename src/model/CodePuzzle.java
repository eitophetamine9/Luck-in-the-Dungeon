package model;

public class CodePuzzle extends Puzzle{
    private String solution;
    private String playerInput;
    private int maxAttempts;

    public CodePuzzle(String description, int coinReward, int difficultyLevel,
                      String solution, int maxAttempts){
        super(description, coinReward, difficultyLevel);
        this.solution = solution;
        this.maxAttempts = maxAttempts;
        this.playerInput = "";
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
