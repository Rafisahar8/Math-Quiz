package model;

public class Player {
    private String name;
    private int score;
    private int level;
    private int lives;
    private int questionsAnswered;
    private int totalQuestionsPerLevel;
    
    public class LevelProgress {
        private int questionsInCurrentLevel;
        
        public LevelProgress() {
            this.questionsInCurrentLevel = 0;
        }
        
        public void increment() {
            questionsInCurrentLevel++;
        }
        
        public void reset() {
            questionsInCurrentLevel = 0;
        }
        
        public int getQuestionsInCurrentLevel() {
            return questionsInCurrentLevel;
        }
        
        public boolean isLevelComplete() {
            return questionsInCurrentLevel >= 10;
        }
        
        public double getProgressPercentage() {
            return (double) questionsInCurrentLevel / 10 * 100;
        }
    }
    
    private LevelProgress levelProgress;
    
    public Player(String name) {
        this.name = name;
        this.score = 0;
        this.level = 1;
        this.lives = 3;
        this.questionsAnswered = 0;
        this.totalQuestionsPerLevel = 10;
        this.levelProgress = new LevelProgress();
    }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }
    
    public int getLevel() { return level; }
    public void setLevel(int level) { 
        this.level = level;
        this.levelProgress.reset();
    }
    
    public int getLives() { return lives; }
    public void setLives(int lives) { this.lives = lives; }
    
    public int getQuestionsAnswered() { return questionsAnswered; }
    public int getTotalQuestionsPerLevel() { return totalQuestionsPerLevel; }
    
    public LevelProgress getLevelProgress() { return levelProgress; }
    
    public void addScore(int points) {
        this.score += points;
    }
    
    public void loseLife() {
        if (this.lives > 0) {
            this.lives--;
        }
    }
    
    public boolean isAlive() {
        return lives > 0;
    }
    
    public void incrementQuestionsAnswered() {
        this.questionsAnswered++;
        this.levelProgress.increment();
    }
    
    public boolean canLevelUp() {
        return levelProgress.isLevelComplete() && level < 8;
    }
    
    public boolean isQuizComplete() {
        return level >= 8 && levelProgress.isLevelComplete();
    }
    
    public double getProgressPercentage() {
        return levelProgress.getProgressPercentage();
    }
}