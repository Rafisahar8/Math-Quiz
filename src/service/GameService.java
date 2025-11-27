package service;

import model.Player;
import model.MathQuestion;

public class GameService {
    private Player player;
    private MathQuestion currentQuestion;
    
    public GameService(String playerName) {
        this.player = new Player(playerName);
    }
    
    public void startNewQuestion() {
        // Set max level to 6
        if (player.getLevel() > 6) {
            currentQuestion = null;
            return;
        }
        currentQuestion = QuestionGenerator.generateQuestion(player.getLevel());
    }
    
    public boolean checkAnswer(int selectedOption) {
        if (currentQuestion == null) return false;
        
        String[] options = currentQuestion.getOptions();
        
        // DEBUG: Print informasi untuk troubleshooting
        System.out.println("=== DEBUG CHECKANSWER ===");
        System.out.println("Level: " + player.getLevel());
        System.out.println("Selected option: " + selectedOption);
        System.out.println("Options: " + java.util.Arrays.toString(options));
        
        try {
            String selectedAnswer = options[selectedOption];
            System.out.println("Selected answer string: '" + selectedAnswer + "'");
            
            double answer = Double.parseDouble(selectedAnswer);
            System.out.println("Parsed answer: " + answer);
            System.out.println("Correct answer: " + currentQuestion.getCorrectAnswer());
            
            boolean isCorrect = currentQuestion.checkAnswer(answer);
            System.out.println("Is correct: " + isCorrect);
            
            if (isCorrect) {
                player.addScore(currentQuestion.getPoints());
                return true;
            } else {
                player.loseLife();
                return false;
            }
        } catch (NumberFormatException e) {
            System.out.println("ERROR: Cannot parse '" + options[selectedOption] + "' to double");
            player.loseLife();
            return false;
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("ERROR: Invalid option index: " + selectedOption);
            player.loseLife();
            return false;
        }
    }      
      
    public boolean canLevelUp() {
        return player.canLevelUp();
    }
    
    public void levelUp() {
        if (canLevelUp() && player.getLevel() < 6) {
            player.setLevel(player.getLevel() + 1);
        }
    }
    
    public Player getPlayer() { return player; }
    public MathQuestion getCurrentQuestion() { return currentQuestion; }
    public boolean isGameOver() { return !player.isAlive(); }
}