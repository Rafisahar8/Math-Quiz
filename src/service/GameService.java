package service;

import model.Player;
import model.MathQuestion;
import javax.swing.JOptionPane;

public class GameService {
    private Player player;
    private MathQuestion currentQuestion;
    
    public GameService(String playerName) {
        this.player = new Player(playerName);
        QuestionGenerator.resetPatterns();
    }
    
    public void startNewQuestion() {
        if (player.isQuizComplete()) {
            currentQuestion = null;
            return;
        }
        
        if (player.canLevelUp() && player.getLevel() < 8) {
            player.setLevel(player.getLevel() + 1);
            QuestionGenerator.resetPatterns();
        }
        
        int currentLevel = Math.min(player.getLevel(), 8);
        int attempts = 0;
        
        while (attempts < 3) {
            currentQuestion = QuestionGenerator.generateQuestion(currentLevel);
            
            if (currentQuestion != null && 
                currentQuestion.getOptions() != null && 
                currentQuestion.getOptions().length >= 4) {
                break;
            }
            
            attempts++;
            System.err.println("[WARN] Attempt " + attempts + ": Generated invalid question, retrying...");
        }
        
        if (currentQuestion == null) {
            System.err.println("[ERROR] Failed to generate question, creating fallback...");
            currentQuestion = createFallbackQuestion(currentLevel);
        }
        
        System.out.println("[GameService] Level " + currentLevel + 
                          " - Question generated: " + (currentQuestion != null));
    }
    
    private MathQuestion createFallbackQuestion(int level) {
        String question = "2 + 2 = ?";
        double answer = 4;
        int points = 10;
        String[] options = {"3", "4", "5", "6"};
        
        model.Question.QuestionDifficulty difficulty;
        if (level <= 2) difficulty = model.Question.QuestionDifficulty.EASY;
        else if (level <= 4) difficulty = model.Question.QuestionDifficulty.MEDIUM;
        else if (level <= 6) difficulty = model.Question.QuestionDifficulty.HARD;
        else difficulty = model.Question.QuestionDifficulty.EXPERT;
        
        return new MathQuestion(question, answer, points, difficulty, options);
    }
    
    public boolean checkAnswer(int selectedOption) {
        if (currentQuestion == null) {
            System.err.println("[ERROR] No current question!");
            showErrorMessage("Tidak ada pertanyaan saat ini!");
            return false;
        }
        
        String[] options = currentQuestion.getOptions();
        
        if (selectedOption < 0 || selectedOption >= options.length) {
            System.err.println("[ERROR] Invalid option index: " + selectedOption);
            showErrorMessage("Pilihan tidak valid!");
            player.loseLife();
            return false;
        }
        
        try {
            String selectedAnswer = options[selectedOption];
            
            double answer;
            if (selectedAnswer.equals("∞") || selectedAnswer.equalsIgnoreCase("infinity")) {
                answer = Double.POSITIVE_INFINITY;
            } else if (selectedAnswer.equalsIgnoreCase("Error") ||
                      selectedAnswer.equalsIgnoreCase("Tidak terdefinisi")) {
                answer = Double.NaN;
            } else {
                selectedAnswer = selectedAnswer.replace(',', '.').replace(" ", "");
                try {
                    answer = parseSymbolicAnswer(selectedAnswer);
                } catch (NumberFormatException ex) {
                    answer = Double.parseDouble(selectedAnswer);
                }
            }
            
            boolean isCorrect = currentQuestion.checkAnswer(answer);
            
            if (isCorrect) {
                player.addScore(currentQuestion.getPoints());
                player.incrementQuestionsAnswered();
                return true;
            } else {
                player.loseLife();
                return false;
            }
        } catch (NumberFormatException e) {
            System.err.println("[ERROR] Invalid number format in answer: " + e.getMessage());
            showErrorMessage("Format jawaban tidak valid!");
            player.loseLife();
            return false;
        } catch (Exception e) {
            System.err.println("[ERROR] Unexpected error in checkAnswer: " + e.getMessage());
            showErrorMessage("Terjadi kesalahan sistem!");
            player.loseLife();
            return false;
        }
    }
    
    private void showErrorMessage(String message) {
        System.err.println("[UI Error] " + message);
    }

    private double parseSymbolicAnswer(String s) throws NumberFormatException {
        if (s == null || s.isEmpty()) throw new NumberFormatException("Empty answer");

        if (s.startsWith("akar") && !s.contains("/")) {
            return Math.sqrt(Double.parseDouble(s.substring(4)));
        }

        if (s.contains("/")) {
            String[] parts = s.split("/");
            if (parts.length == 2) {
                double numerator;
                String numPart = parts[0];
                if (numPart.startsWith("akar")) {
                    numerator = Math.sqrt(Double.parseDouble(numPart.substring(4)));
                } else if (numPart.startsWith("\u221A")) {
                    numerator = Math.sqrt(Double.parseDouble(numPart.substring(1)));
                } else {
                    numerator = Double.parseDouble(numPart);
                }
                double denominator = Double.parseDouble(parts[1]);
                return numerator / denominator;
            }
        }

        if (s.startsWith("\u221A")) {
            return Math.sqrt(Double.parseDouble(s.substring(1)));
        }

        return Double.parseDouble(s);
    }
    
    public boolean canLevelUp() {
        return player.canLevelUp();
    }
    
    public void levelUp() {
        if (canLevelUp() && player.getLevel() < 8) {
            player.setLevel(player.getLevel() + 1);
        }
    }
    
    public Player getPlayer() { 
        return player; 
    }
    
    public MathQuestion getCurrentQuestion() { 
        return currentQuestion; 
    }
    
    public boolean isGameOver() { 
        return !player.isAlive(); 
    }
    
    public boolean isQuizComplete() {
        return player.isQuizComplete();
    }
}
