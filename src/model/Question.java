package model;

public abstract class Question {
    protected String question;
    protected double correctAnswer;
    protected int points;
    protected QuestionDifficulty difficulty;
    
    public enum QuestionDifficulty {
        EASY, MEDIUM, HARD, EXPERT
    }
    
    public Question(String question, double correctAnswer, int points, QuestionDifficulty difficulty) {
        this.question = question;
        this.correctAnswer = correctAnswer;
        this.points = points;
        this.difficulty = difficulty;
    }
    
    public abstract String getQuestionText();
    public abstract boolean checkAnswer(double answer);
    
    public String getQuestion() { return question; }
    public double getCorrectAnswer() { return correctAnswer; }
    public int getPoints() { return points; }
    public QuestionDifficulty getDifficulty() { return difficulty; }
}