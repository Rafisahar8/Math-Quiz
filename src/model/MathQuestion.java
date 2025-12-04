package model;

import java.text.DecimalFormat;

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
        if (Double.isInfinite(correctAnswer)) {
            return Double.isInfinite(answer);
        }
        if (Double.isNaN(correctAnswer)) {
            return Double.isNaN(answer);
        }

        double tolerance;
        switch (difficulty) {
            case EASY:
                tolerance = 0.1;
                break;
            case MEDIUM:
                tolerance = 0.01;
                break;
            case HARD:
                tolerance = 0.001;
                break;
            case EXPERT:
                tolerance = 0.0001;
                break;
            default:
                tolerance = 0.01;
                break;
        }

        if (correctAnswer != Math.floor(correctAnswer)) {
            tolerance = Math.max(tolerance, 0.001);
        }

        return Math.abs(answer - correctAnswer) < tolerance;
    }
    
    public String[] getOptions() { return options; }
}