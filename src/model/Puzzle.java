package model;

public abstract class Puzzle {
    protected String description;
    protected boolean isSolved;
    protected int coinReward;
    protected int difficultyLevel;

    public Puzzle(String description, int coinReward, int difficultyLevel){
        this.description = description;
        this.isSolved = false;
        this.coinReward = coinReward;
        this.difficultyLevel = difficultyLevel;
    }

    public abstract boolean attemptSolve(GachaItem item);

    public String getHint() {
        return "Think carefully about you need to solve this";
    }

    public int getCoinReward(){return  coinReward;}
    public boolean isSolved(){return isSolved;}
    public String getDescription(){return description;}
    public int getDifficultyLevel(){return difficultyLevel;}

    public void markSolved(){this.isSolved = true;}
}
