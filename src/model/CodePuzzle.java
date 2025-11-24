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

    @Override
    public boolean attemptSolve(GachaItem item){
        //CODE PUZZLES THAT DON'T USE ITEMS - THEY NEED DIRECT INPUT
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
}
