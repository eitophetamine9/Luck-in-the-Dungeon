package model;

public class RiddlePuzzle extends Puzzle{
    private String question;
    private String answer;
    private String hint;

    public RiddlePuzzle(String description, int coinReward, int difficultyLevel,
                        String riddleQuestion, String answer, String hint){
        super(description, coinReward, difficultyLevel);
        this.question = riddleQuestion;
        this.answer = answer;
        this.hint = hint; // Store the hint for tool usage
    }

    public String getHint() {
        return hint; // Add this getter method
    }

    @Override
    public boolean attemptSolve(GachaItem item){
        //RIDDLE PUZZLES DONT USE ITEMS - THEY NEED ANSWER INPUT
        if(item instanceof ToolItem) {
            ToolItem tool = (ToolItem) item;
            return tool.use(this); // ✅ Allow hintbook to work
        }
        return false;
    }

    public boolean checkAnswer(String playerAnswer){
        if(playerAnswer.equalsIgnoreCase(answer)) {
            markSolved();
            return true;
        }
        return false;
    }

    public String getQuestion(){return question;}
}
