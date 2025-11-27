package model;

public class MathQuestion extends Question {
    private String[] options;
    
    public MathQuestion(String question, double correctAnswer, int points, 
                       QuestionDifficulty difficulty, String[] options) {
        super(question, correctAnswer, points, difficulty);
        this.options = options;
    }
    
    @Override
    public String getQuestionText() {
        StringBuilder sb = new StringBuilder();
        sb.append(question).append("\n");
        for (int i = 0; i < options.length; i++) {
            sb.append((i + 1)).append(". ").append(options[i]).append("\n");
        }
        return sb.toString();
    }
    
    @Override
    public boolean checkAnswer(double answer) {
        return Math.abs(answer - correctAnswer) < 0.5; 
    }
    
    public String[] getOptions() { return options; }
}