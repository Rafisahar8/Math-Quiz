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
                selectedAnswer = selectedAnswer.replace(',', '.');
                answer = Double.parseDouble(selectedAnswer);
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
