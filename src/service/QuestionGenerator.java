package service;

import model.MathQuestion;
import model.Question;

public class QuestionGenerator {
    
    public static MathQuestion generateQuestion(int level) {
        Question.QuestionDifficulty difficulty = getDifficultyByLevel(level);
        
        switch (difficulty) {
            case EASY:
                return generateEasyQuestion();
            case MEDIUM:
                return generateMediumQuestion();
            case HARD:
                return generateHardQuestion();
            case EXPERT:
                return generateExpertQuestion();
            default:
                return generateEasyQuestion();
        }
    }
    
    private static Question.QuestionDifficulty getDifficultyByLevel(int level) {
        if (level <= 2) return Question.QuestionDifficulty.EASY;
        else if (level <= 4) return Question.QuestionDifficulty.MEDIUM;
        else if (level <= 6) return Question.QuestionDifficulty.HARD;
        else return Question.QuestionDifficulty.EXPERT;
    }
    
    private static MathQuestion generateEasyQuestion() {
        int a = (int) (Math.random() * 10) + 1;
        int b = (int) (Math.random() * 10) + 1;
        int operation = (int) (Math.random() * 2);

        String question;
        double answer;
        java.util.Set<String> optionSet = new java.util.LinkedHashSet<>();

        if (operation == 0) {
            question = a + " + " + b + " = ?";
            answer = a + b;
        } else {
            question = a + " - " + b + " = ?";
            answer = a - b;
        }

        optionSet.add(String.valueOf((int) answer));
        while (optionSet.size() < 4) {
            int wrong = (int) (answer + (Math.random() * 10) - 5);
            String wrongStr = String.valueOf(wrong);
            if (!optionSet.contains(wrongStr)) {
                optionSet.add(wrongStr);
            }
        }
        String[] options = optionSet.toArray(new String[4]);
        shuffleArray(options);
        return new MathQuestion(question, answer, 10, Question.QuestionDifficulty.EASY, options);
    }
    
    private static MathQuestion generateMediumQuestion() {
        int a = (int) (Math.random() * 20) + 1;
        int b = (int) (Math.random() * 10) + 1;
        int operation = (int) (Math.random() * 3);

        String question;
        double answer;
        java.util.Set<String> optionSet = new java.util.LinkedHashSet<>();

        switch (operation) {
            case 0:
                question = a + " × " + b + " = ?";
                answer = a * b;
                break;
            case 1:
                question = (a * b) + " ÷ " + a + " = ?";
                answer = b;
                break;
            default:
                question = "(" + a + " + " + b + ") × 2 = ?";
                answer = (a + b) * 2;
                break;
        }

        optionSet.add(String.valueOf((int) answer));
        while (optionSet.size() < 4) {
            int wrong = (int) (answer + (Math.random() * 20) - 10);
            String wrongStr = String.valueOf(wrong);
            if (!optionSet.contains(wrongStr)) {
                optionSet.add(wrongStr);
            }
        }
        String[] options = optionSet.toArray(new String[4]);
        shuffleArray(options);
        return new MathQuestion(question, answer, 20, Question.QuestionDifficulty.MEDIUM, options);
    }
    
    private static MathQuestion generateHardQuestion() {
        int a = (int) (Math.random() * 50) + 1;
        int b = (int) (Math.random() * 20) + 1;
        int c = (int) (Math.random() * 10) + 1;

        String question;
        double answer;
        int pattern = (int) (Math.random() * 3);
        switch (pattern) {
            case 0:
                question = "(" + a + " × " + b + ") ÷ " + c + " = ?";
                answer = Math.round((a * b) / (double) c * 10.0) / 10.0;
                break;
            case 1:
                question = a + " adalah berapa persen dari " + (a * 2) + "?";
                answer = 50.0;
                break;
            default:
                question = "Jika x = " + a + ", maka 2x + " + b + " = ?";
                answer = (double) (2 * a + b);
                break;
        }
        // Always ensure correct answer is present and matches calculation
        String correctStr = String.format("%.0f", answer);
        java.util.List<String> optionsList = new java.util.ArrayList<>();
        optionsList.add(correctStr);
        // Generate wrong answers with larger gaps to avoid confusion
        while (optionsList.size() < 4) {
            int offset = ((int) answer) + ((int) (Math.random() * 60) - 30);
            if (Math.abs(offset - answer) > 5) { // ensure gap > 5
                String wrongStr = String.format("%.0f", (double) offset);
                if (!optionsList.contains(wrongStr)) {
                    optionsList.add(wrongStr);
                }
            }
        }
        java.util.Collections.shuffle(optionsList);
        String[] options = optionsList.toArray(new String[4]);
        // DEBUG: Print correct answer and options for verification
        System.out.println("[DEBUG HARD] Q: " + question + " | Correct: " + correctStr + " | Options: " + java.util.Arrays.toString(options));
        return new MathQuestion(question, answer, 30, Question.QuestionDifficulty.HARD, options);
    }
    
    private static MathQuestion generateExpertQuestion() {
        double a = Math.random() * 100 + 1;
        double b = Math.random() * 50 + 1;

        String question;
        double answer;
        java.util.Set<String> optionSet = new java.util.LinkedHashSet<>();

        int pattern = (int) (Math.random() * 3);
        switch (pattern) {
            case 0:
                question = "x² + " + (int)a + "x + " + (int)b + " = 0, diskriminannya?";
                answer = a * a - 4 * b;
                break;
            case 1:
                question = "Luas lingkaran dengan jari-jari " + (int)a + "? (π=3.14)";
                answer = 3.14 * a * a;
                break;
            default:
                question = "√(" + (int)(a*a) + ") + ∛(" + (int)(b*b*b) + ") = ?";
                answer = a + b;
                break;
        }

        optionSet.add(String.format("%.2f", answer));
        while (optionSet.size() < 4) {
            double wrongAnswer = answer + (Math.random() * 100) - 50;
            String wrongStr = String.format("%.2f", wrongAnswer);
            if (!optionSet.contains(wrongStr)) {
                optionSet.add(wrongStr);
            }
        }
        String[] options = optionSet.toArray(new String[4]);
        shuffleArray(options);
        return new MathQuestion(question, answer, 50, Question.QuestionDifficulty.EXPERT, options);
    }
    
    private static void shuffleArray(String[] array) {
        for (int i = array.length - 1; i > 0; i--) {
            int index = (int) (Math.random() * (i + 1));
            String temp = array[index];
            array[index] = array[i];
            array[i] = temp;
        }
    }
}