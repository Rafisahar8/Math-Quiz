package model;

public class Player {
    private String name;
    private int score;
    private int level;
    private int lives;
    
    public Player(String name) {
        this.name = name;
        this.score = 0;
        this.level = 1;
        this.lives = 3;
    }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }
    
    public int getLevel() { return level; }
    public void setLevel(int level) { this.level = level; }
    
    public int getLives() { return lives; }
    public void setLives(int lives) { this.lives = lives; }
    
    public void addScore(int points) {
        this.score += points;
    }
    
    public void loseLife() {
        this.lives--;
    }
    
    public boolean isAlive() {
        return lives > 0;
    }
    
    public boolean canLevelUp() {
        return score >= level * 100; 
    }
}