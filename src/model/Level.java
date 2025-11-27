package model;

public class Level {
    private int levelNumber;
    private int requiredScore;
    private Question.QuestionDifficulty difficulty;
    
    public Level(int levelNumber, int requiredScore, Question.QuestionDifficulty difficulty) {
        this.levelNumber = levelNumber;
        this.requiredScore = requiredScore;
        this.difficulty = difficulty;
    }
    
    public int getLevelNumber() { return levelNumber; }
    public int getRequiredScore() { return requiredScore; }
    public Question.QuestionDifficulty getDifficulty() { return difficulty; }
}