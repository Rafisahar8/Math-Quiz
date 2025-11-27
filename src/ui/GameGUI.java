package ui;

import service.GameService;
import model.MathQuestion;
import model.Player;

import javax.swing.*;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.DataLine;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GameGUI extends JFrame {
    private GameService gameService;
    private JumpscarePanel jumpscarePanel;
    private CardLayout cardLayout;
    private JPanel mainPanel;
    
    private JLabel questionLabel;
    private JButton[] optionButtons;
    private JPanel scoreLabel;
    private JPanel levelLabel;
    private JPanel livesLabel;
    private JProgressBar levelProgress;
    private JPanel playerNameLabel;
    
    private final Color PRIMARY_COLOR = new Color(74, 107, 255);    
    private final Color SECONDARY_COLOR = new Color(255, 94, 124);  
    private final Color ACCENT_COLOR = new Color(46, 196, 182);     
    private final Color BACKGROUND_COLOR = new Color(18, 18, 32);   
    private final Color CARD_COLOR = new Color(30, 30, 50);         
    private final Color TEXT_COLOR = new Color(255, 255, 255);      
    
    public GameGUI() {
        initializeGame();
        setupUI();
        startGame();
    }
    
    private void initializeGame() {
        String playerName = "Player";
        
        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.setBackground(BACKGROUND_COLOR);
        inputPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel inputLabel = new JLabel("MASUKKAN NAMA KAMU:");
        inputLabel.setFont(new Font("Arial", Font.BOLD, 16));
        inputLabel.setForeground(TEXT_COLOR);
        inputLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        JTextField nameField = new JTextField();
        nameField.setFont(new Font("Segoe UI", Font.BOLD, 14));
        nameField.setBackground(CARD_COLOR);
        nameField.setForeground(TEXT_COLOR);
        nameField.setCaretColor(ACCENT_COLOR);
        nameField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(PRIMARY_COLOR, 2),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        inputPanel.add(inputLabel, BorderLayout.NORTH);
        inputPanel.add(nameField, BorderLayout.CENTER);
        
        int result = JOptionPane.showConfirmDialog(
            null, inputPanel, "🎮 MATH QUIZ GAME 🎮", 
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE
        );
        
        if (result == JOptionPane.OK_OPTION) {
            if (nameField.getText() != null && !nameField.getText().trim().isEmpty()) {
                playerName = nameField.getText().trim();
            }
        } else if (result == JOptionPane.CANCEL_OPTION) {
            int confirm = JOptionPane.showConfirmDialog(null, 
                "Yakin mau keluar dari game?", "Konfirmasi Keluar",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (confirm == JOptionPane.YES_OPTION) {
                System.exit(0);
            }
        }
        
        gameService = new GameService(playerName);
    }
    
    private void setupUI() {
        setTitle("🎯 Math Quiz Game - Ultimate Challenge");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(1400, 900));
        setResizable(true);
        
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        mainPanel.setBackground(BACKGROUND_COLOR);
        
        setupJumpscarePanel();
        
        mainPanel.add(createGamePanel(), "game");
        mainPanel.add(jumpscarePanel, "jumpscare");
        
        add(mainPanel);
        pack();
        setLocationRelativeTo(null);
    }
    
    private void setupJumpscarePanel() {
        jumpscarePanel = new JumpscarePanel();
    }
    
    private JPanel createGamePanel() {
        JPanel gamePanel = new JPanel(new BorderLayout());
        gamePanel.setBackground(BACKGROUND_COLOR);
        gamePanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        JPanel headerPanel = createHeaderPanel();
        gamePanel.add(headerPanel, BorderLayout.NORTH);
        
        JPanel contentPanel = createContentPanel();
        gamePanel.add(contentPanel, BorderLayout.CENTER);
        
        return gamePanel;
    }
    
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(BACKGROUND_COLOR);
        headerPanel.setBorder(new EmptyBorder(0, 0, 15, 0));
        
        JLabel titleLabel = new JLabel("🧠 MATH QUIZ CHALLENGE 🧠");
        titleLabel.setFont(new Font("Segoe UI Emoji", Font.BOLD, 28));
        titleLabel.setForeground(ACCENT_COLOR);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        
        JPanel statsPanel = createStatsPanel();
        
        headerPanel.add(titleLabel, BorderLayout.NORTH);
        headerPanel.add(statsPanel, BorderLayout.CENTER);
        
        return headerPanel;
    }
    
    private JPanel createStatsPanel() {
        JPanel statsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        statsPanel.setBackground(BACKGROUND_COLOR);
        
        playerNameLabel = createCompactStatCard("👤 Player", gameService.getPlayer().getName());
        scoreLabel = createCompactStatCard("⭐ Score", "0");
        levelLabel = createCompactStatCard("🚀 Level", "1");
        livesLabel = createCompactStatCard("💖 Lives", "❤❤❤");
        
        JPanel progressPanel = new JPanel();
        progressPanel.setLayout(new BoxLayout(progressPanel, BoxLayout.Y_AXIS));
        progressPanel.setBackground(CARD_COLOR);
        progressPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(PRIMARY_COLOR, 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        
        JLabel progressTitle = new JLabel("Next Level");
        progressTitle.setFont(new Font("Arial", Font.BOLD, 11));
        progressTitle.setForeground(new Color(200, 200, 200));
        progressTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        levelProgress = new JProgressBar(0, 100);
        levelProgress.setValue(0);
        levelProgress.setStringPainted(true);
        levelProgress.setFont(new Font("Arial", Font.BOLD, 10));
        levelProgress.setForeground(ACCENT_COLOR);
        levelProgress.setBackground(new Color(50, 50, 70));
        levelProgress.setPreferredSize(new Dimension(80, 20));
        levelProgress.setMaximumSize(new Dimension(80, 20));
        levelProgress.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        progressPanel.add(progressTitle);
        progressPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        progressPanel.add(levelProgress);
        
        statsPanel.add(playerNameLabel);
        statsPanel.add(scoreLabel);
        statsPanel.add(levelLabel);
        statsPanel.add(livesLabel);
        statsPanel.add(progressPanel);
        
        return statsPanel;
    }
    
    private JPanel createCompactStatCard(String title, String value) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(CARD_COLOR);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(PRIMARY_COLOR, 1),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        card.setPreferredSize(new Dimension(100, 60));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI Emoji", Font.BOLD, 11));
        titleLabel.setForeground(new Color(200, 200, 200));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI Emoji", Font.BOLD, 14));
        valueLabel.setForeground(TEXT_COLOR);
        valueLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        card.add(titleLabel);
        card.add(Box.createRigidArea(new Dimension(0, 3)));
        card.add(valueLabel);
        
        return card;
    }
    
    private JPanel createContentPanel() {
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(BACKGROUND_COLOR);
        contentPanel.setBorder(new EmptyBorder(10, 0, 0, 0));
        
        JPanel questionPanel = createQuestionPanel();
        contentPanel.add(questionPanel, BorderLayout.CENTER);
        
        JPanel optionsPanel = createOptionsPanel();
        contentPanel.add(optionsPanel, BorderLayout.SOUTH);
        
        return contentPanel;
    }
    
    private JPanel createQuestionPanel() {
        JPanel questionPanel = new JPanel(new BorderLayout());
        questionPanel.setBackground(BACKGROUND_COLOR);
        questionPanel.setBorder(new EmptyBorder(0, 30, 20, 30));
        
        JPanel questionCard = new JPanel(new BorderLayout());
        questionCard.setBackground(CARD_COLOR);
        questionCard.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ACCENT_COLOR, 2),
            BorderFactory.createEmptyBorder(30, 30, 30, 30)
        ));
        
        questionLabel = new JLabel("", JLabel.CENTER);
        questionLabel.setFont(new Font("Arial", Font.BOLD, 24));
        questionLabel.setForeground(TEXT_COLOR);
        
        questionCard.add(questionLabel, BorderLayout.CENTER);
        questionPanel.add(questionCard, BorderLayout.CENTER);
        
        return questionPanel;
    }
    
    private JPanel createOptionsPanel() {
        JPanel optionsPanel = new JPanel(new GridLayout(2, 2, 15, 15));
        optionsPanel.setBackground(BACKGROUND_COLOR);
        optionsPanel.setBorder(new EmptyBorder(0, 30, 30, 30));
        
        optionButtons = new JButton[4];
        Color[] buttonColors = {
            new Color(74, 107, 255),   
            new Color(255, 94, 124),  
            new Color(46, 196, 182),  
            new Color(255, 177, 66)    
        };
        
        for (int i = 0; i < 4; i++) {
            final int index = i;
            final Color baseColor = buttonColors[i];
            
            optionButtons[i] = new RoundedButton("");
            optionButtons[i].setFont(new Font("Arial", Font.BOLD, 20));
            optionButtons[i].setForeground(TEXT_COLOR);
            optionButtons[i].setBackground(baseColor);
            optionButtons[i].setFocusPainted(false);
            optionButtons[i].setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
            
            optionButtons[i].addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    optionButtons[index].setBackground(baseColor.brighter());
                    optionButtons[index].setCursor(new Cursor(Cursor.HAND_CURSOR));
                }
                
                public void mouseExited(java.awt.event.MouseEvent evt) {
                    optionButtons[index].setBackground(baseColor);
                }
            });
            
            optionButtons[i].addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    handleAnswer(index);
                }
            });
            
            optionsPanel.add(optionButtons[i]);
        }
        
        return optionsPanel;
    }
    
    class RoundedButton extends JButton {
        private int cornerRadius = 20;
        
        public RoundedButton(String text) {
            super(text);
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            if (getModel().isPressed()) {
                g2.setColor(getBackground().darker());
            } else if (getModel().isRollover()) {
                g2.setColor(getBackground().brighter());
            } else {
                g2.setColor(getBackground());
            }
            
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius);
            super.paintComponent(g);
            g2.dispose();
        }
    }
    
    private void startGame() {
        gameService.startNewQuestion();
        updateUI();
        setVisible(true);
    }
    
    private void handleAnswer(int selectedOption) {
        boolean isCorrect = gameService.checkAnswer(selectedOption);

        if (isCorrect) {
            showCorrectFeedback();
            if (gameService.canLevelUp()) {
                gameService.levelUp();
                showLevelUpMessage();
            }
            gameService.startNewQuestion();
            updateUI();
        } else {
            int remaining = gameService.getPlayer().getLives();
            if (remaining <= 1) {
                new Thread(() -> playGameOverScream()).start();
            } else {
                new Thread(() -> playWrongTone()).start();
            }
            showJumpscare(remaining);
            // After jumpscare, return to same question (do not start new question)
            Timer nextQuestionTimer = new Timer(2000, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (gameService.isGameOver()) {
                        gameOver();
                    } else {
                        updateUI();
                        cardLayout.show(mainPanel, "game");
                    }
                }
            });
            nextQuestionTimer.setRepeats(false);
            nextQuestionTimer.start();
        }
    }
    
    private void showCorrectFeedback() {
        new Thread(() -> playGoodJobTone()).start();

        final JDialog dialog = new JDialog(this);
        dialog.setUndecorated(true);
        dialog.setModal(false);

        JPanel successPanel = new JPanel(new BorderLayout());
        successPanel.setBackground(BACKGROUND_COLOR);
        successPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(76, 175, 80), 3),
            BorderFactory.createEmptyBorder(18, 30, 18, 30)
        ));

        JLabel emojiLabel = new JLabel("🎉", JLabel.CENTER);
        emojiLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 56));

        JLabel messageLabel = new JLabel("<html><div style='text-align: center;'>" +
            "<b style='color: #4CAF50; font-size: 22px;'>BENAR! 🎯</b><br>" +
            "<span style='color: white; font-size: 14px;'>+" + gameService.getCurrentQuestion().getPoints() + " poin!</span>" +
            "</div></html>", JLabel.CENTER);
        messageLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));

        successPanel.add(emojiLabel, BorderLayout.NORTH);
        successPanel.add(messageLabel, BorderLayout.CENTER);

        dialog.add(successPanel);
        dialog.pack();
        dialog.setLocationRelativeTo(this);

        dialog.setVisible(true);
        Timer t = new Timer(1200, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.setVisible(false);
                dialog.dispose();
            }
        });
        t.setRepeats(false);
        t.start();
    }

    private void playSuccessTone() {
        final float SAMPLE_RATE = 44100f;
        final int noteMs = 160; 
        final double vol = 0.25; 
        final int freq1 = 880;  
        final int freq2 = 1320; 

        int samplesPerNote = (int) (SAMPLE_RATE * noteMs / 1000);
        byte[] buf = new byte[samplesPerNote * 2];

        for (int n = 0; n < 2; n++) {
            int freq = (n == 0) ? freq1 : freq2;
            int base = n * samplesPerNote;
            for (int i = 0; i < samplesPerNote; i++) {
                double t = (double) i / SAMPLE_RATE;
                double angle = 2.0 * Math.PI * freq * t;
                double env = 1.0;
                int attackSamples = (int) (SAMPLE_RATE * 0.01);
                int decaySamples = attackSamples;
                if (i < attackSamples) env = (double) i / attackSamples;
                else if (i > samplesPerNote - decaySamples) env = (double) (samplesPerNote - i) / decaySamples;
                double sample = Math.sin(angle) * vol * env;
                buf[base + i] = (byte) (sample * 127.0);
            }
        }

        AudioFormat af = new AudioFormat(SAMPLE_RATE, 8, 1, true, false);
        try {
            DataLine.Info info = new DataLine.Info(SourceDataLine.class, af);
            SourceDataLine sdl = (SourceDataLine) javax.sound.sampled.AudioSystem.getLine(info);
            sdl.open(af);
            sdl.start();
            sdl.write(buf, 0, buf.length);
            sdl.drain();
            sdl.stop();
            sdl.close();
        } catch (LineUnavailableException ex) {
            Toolkit.getDefaultToolkit().beep();
        }
    }

    private void playLevelUpTone() {
        final float SAMPLE_RATE = 44100f;
        final int noteMs = 120;
        final double vol = 0.25;
        final int f1 = 660; 
        final int f2 = 880; 

        int samplesPerNote = (int) (SAMPLE_RATE * noteMs / 1000);
        byte[] buf = new byte[samplesPerNote * 2];

        for (int n = 0; n < 2; n++) {
            int freq = (n == 0) ? f1 : f2;
            int base = n * samplesPerNote;
            for (int i = 0; i < samplesPerNote; i++) {
                double t = (double) i / SAMPLE_RATE;
                double angle = 2.0 * Math.PI * freq * t;
                double env = 1.0;
                int attack = (int) (SAMPLE_RATE * 0.01);
                if (i < attack) env = (double) i / attack;
                buf[base + i] = (byte) (Math.sin(angle) * 127.0 * vol * env);
            }
        }

        AudioFormat af = new AudioFormat(SAMPLE_RATE, 8, 1, true, false);
        try {
            DataLine.Info info = new DataLine.Info(SourceDataLine.class, af);
            SourceDataLine sdl = (SourceDataLine) javax.sound.sampled.AudioSystem.getLine(info);
            sdl.open(af);
            sdl.start();
            sdl.write(buf, 0, buf.length);
            sdl.drain();
            sdl.stop();
            sdl.close();
        } catch (LineUnavailableException ex) {
            Toolkit.getDefaultToolkit().beep();
        }
    }

    private void playGameOverTone() {
        final float SAMPLE_RATE = 44100f;
        final int ms = 700;
        byte[] buf = new byte[(int) (SAMPLE_RATE * ms / 1000)];
        double freq = 110; 
        for (int i = 0; i < buf.length; i++) {
            double angle = 2.0 * Math.PI * i * freq / SAMPLE_RATE;
            double env = 1.0 - (double) i / buf.length;
            buf[i] = (byte) (Math.sin(angle) * 127.0 * 0.35 * env);
        }

        AudioFormat af = new AudioFormat(SAMPLE_RATE, 8, 1, true, false);
        try {
            DataLine.Info info = new DataLine.Info(SourceDataLine.class, af);
            SourceDataLine sdl = (SourceDataLine) javax.sound.sampled.AudioSystem.getLine(info);
            sdl.open(af);
            sdl.start();
            sdl.write(buf, 0, buf.length);
            sdl.drain();
            sdl.stop();
            sdl.close();
        } catch (LineUnavailableException ex) {
            Toolkit.getDefaultToolkit().beep();
        }
    }
    
    private void playGoodJobTone() {
        final float SAMPLE_RATE = 44100f;
        final int noteMs = 160;
        final double vol = 0.45; 
        final int[] freqs = {880, 1108}; 

        int samplesPerNote = (int) (SAMPLE_RATE * noteMs / 1000);
        byte[] buf = new byte[samplesPerNote * freqs.length + 4000]; 

        for (int n = 0; n < freqs.length; n++) {
            int freq = freqs[n];
            int base = n * samplesPerNote;
            for (int i = 0; i < samplesPerNote; i++) {
                double t = (double) i / SAMPLE_RATE;
                double angle = 2.0 * Math.PI * freq * t;
                double sample = Math.sin(angle) + 0.4 * Math.sin(2 * angle);
                int attack = (int) (SAMPLE_RATE * 0.01);
                int decay = (int) (SAMPLE_RATE * 0.06);
                double env = 1.0;
                if (i < attack) env = (double) i / attack;
                else if (i > samplesPerNote - decay) env = (double) (samplesPerNote - i) / decay;
                double s = sample * vol * env;
                s = Math.max(-1.0, Math.min(1.0, s));
                buf[base + i] = (byte) (s * 127.0);
            }
        }

        int echoDelay = 1200; 
        double echoAtt = 0.45;
        for (int i = 0; i + echoDelay < buf.length; i++) {
            int src = i;
            int dst = i + echoDelay;
            if (dst < buf.length) {
                int mixed = buf[dst] + (int) (buf[src] * echoAtt);
                mixed = Math.max(-127, Math.min(127, mixed));
                buf[dst] = (byte) mixed;
            }
        }

        AudioFormat af = new AudioFormat(SAMPLE_RATE, 8, 1, true, false);
        try {
            DataLine.Info info = new DataLine.Info(SourceDataLine.class, af);
            SourceDataLine sdl = (SourceDataLine) javax.sound.sampled.AudioSystem.getLine(info);
            sdl.open(af);
            sdl.start();
            sdl.write(buf, 0, buf.length);
            sdl.drain();
            sdl.stop();
            sdl.close();
        } catch (LineUnavailableException ex) {
            Toolkit.getDefaultToolkit().beep();
        }
    }

    private void playWrongTone() {
        final float SAMPLE_RATE = 44100f;
        final int ms = 260;
        int len = (int) (SAMPLE_RATE * ms / 1000);
        byte[] buf = new byte[len];
        double baseFreq = 160; 

        for (int i = 0; i < len; i++) {
            double t = (double) i / SAMPLE_RATE;
            double sq = Math.signum(Math.sin(2.0 * Math.PI * baseFreq * t));
            double bite = 0.4 * Math.sin(2.0 * Math.PI * baseFreq * 5.3 * t);
            double env = Math.exp(-6.0 * t);
            double noise = (Math.random() - 0.5) * 0.12; 
            double s = (sq * 0.9 + bite) * env + noise;
            s = Math.max(-1.0, Math.min(1.0, s));
            buf[i] = (byte) (s * 127.0);
        }

        AudioFormat af = new AudioFormat(SAMPLE_RATE, 8, 1, true, false);
        try {
            DataLine.Info info = new DataLine.Info(SourceDataLine.class, af);
            SourceDataLine sdl = (SourceDataLine) javax.sound.sampled.AudioSystem.getLine(info);
            sdl.open(af);
            sdl.start();
            sdl.write(buf, 0, buf.length);
            sdl.drain();
            sdl.stop();
            sdl.close();
        } catch (LineUnavailableException ex) {
            Toolkit.getDefaultToolkit().beep();
        }
    }

    private void playGameOverScream() {
        final float SAMPLE_RATE = 44100f;
        final int ms = 1100;
        int len = (int) (SAMPLE_RATE * ms / 1000);
        byte[] buf = new byte[len + 5000];

        for (int i = 0; i < len; i++) {
            double t = (double) i / SAMPLE_RATE;
            double frac = (double) i / len;
            double freq = 220 + 3000 * Math.pow(frac, 1.2);
            double angle = 2.0 * Math.PI * freq * t;
            double s = 0.7 * Math.sin(angle) + 0.3 * Math.sin(2 * angle) + 0.15 * Math.sin(3 * angle);
            double noise = (Math.random() - 0.5) * (0.18 * Math.sin(Math.PI * frac));
            double env;
            if (frac < 0.15) env = frac / 0.15; 
            else env = 1.0 - Math.pow(frac, 2); 
            double sample = (s + noise) * env * 0.65;
            sample = Math.max(-1.0, Math.min(1.0, sample));
            buf[i] = (byte) (sample * 127.0);
        }

        int[] delays = {4000, 8000};
        double[] atts = {0.45, 0.22};
        for (int d = 0; d < delays.length; d++) {
            int delay = delays[d];
            double att = atts[d];
            for (int i = 0; i + delay < buf.length; i++) {
                int mixed = buf[i + delay] + (int) (buf[i] * att);
                mixed = Math.max(-127, Math.min(127, mixed));
                buf[i + delay] = (byte) mixed;
            }
        }

        AudioFormat af = new AudioFormat(SAMPLE_RATE, 8, 1, true, false);
        try {
            DataLine.Info info = new DataLine.Info(SourceDataLine.class, af);
            SourceDataLine sdl = (SourceDataLine) javax.sound.sampled.AudioSystem.getLine(info);
            sdl.open(af);
            sdl.start();
            sdl.write(buf, 0, buf.length);
            sdl.drain();
            sdl.stop();
            sdl.close();
        } catch (LineUnavailableException ex) {
            Toolkit.getDefaultToolkit().beep();
        }
    }
    
    private void showJumpscare(int remainingLives) {
        jumpscarePanel.triggerJumpscare(remainingLives);
        cardLayout.show(mainPanel, "jumpscare");
    }
    
    private void showLevelUpMessage() {
        new Thread(() -> playLevelUpTone()).start();

        JPanel levelUpPanel = new JPanel(new BorderLayout());
        levelUpPanel.setBackground(BACKGROUND_COLOR);
        levelUpPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel emojiLabel = new JLabel("🚀", JLabel.CENTER);
        emojiLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 48));
        
        JLabel messageLabel = new JLabel("<html><div style='text-align: center;'>" +
            "<b style='color: #FFD700; font-size: 24px;'>LEVEL UP! ⭐</b><br>" +
            "<span style='color: white; font-size: 16px;'>Sekarang kamu di Level " + 
            gameService.getPlayer().getLevel() + "</span>" +
            "</div></html>", JLabel.CENTER);
        
        levelUpPanel.add(emojiLabel, BorderLayout.NORTH);
        levelUpPanel.add(messageLabel, BorderLayout.CENTER);
        
        JOptionPane.showMessageDialog(this, levelUpPanel, "Selamat!", 
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void gameOver() {
        new Thread(() -> playGameOverScream()).start();

        Player player = gameService.getPlayer();
        
        JPanel gameOverPanel = new JPanel(new BorderLayout());
        gameOverPanel.setBackground(BACKGROUND_COLOR);
        gameOverPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        
        JLabel emojiLabel = new JLabel("💀", JLabel.CENTER);
        emojiLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 64));
        
        String message = String.format(
            "<html><div style='text-align: center; color: white;'>" +
            "<b style='color: #FF6B6B; font-size: 24px;'>GAME OVER!</b><br><br>" +
            "<b>Nama:</b> %s<br>" +
            "<b>Level Tertinggi:</b> %d<br>" +
            "<b>Score Akhir:</b> <span style='color: #FFD700;'>%d</span><br><br>" +
            "<span style='color: #4ECDC4;'>Coba lagi untuk mencapai score yang lebih tinggi!</span>" +
            "</div></html>",
            player.getName(), player.getLevel(), player.getScore()
        );
        
        JLabel messageLabel = new JLabel(message, JLabel.CENTER);
        messageLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        
        gameOverPanel.add(emojiLabel, BorderLayout.NORTH);
        gameOverPanel.add(messageLabel, BorderLayout.CENTER);
        
        Object[] options = {"Main Lagi", "Keluar"};
        int choice = JOptionPane.showOptionDialog(this, gameOverPanel, "Game Over",
            JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE,
            null, options, options[0]);
        
        if (choice == JOptionPane.YES_OPTION) {
            restartGame();
        } else {
            System.exit(0);
        }
    }
    
    private void restartGame() {
        String playerName = gameService.getPlayer().getName();
        gameService = new GameService(playerName);
        gameService.startNewQuestion();
        updateUI();
        if (cardLayout != null && mainPanel != null) {
            cardLayout.show(mainPanel, "game");
            mainPanel.revalidate();
            mainPanel.repaint();
        }
    }
    
    private void updateUI() {
        Player player = gameService.getPlayer();
        MathQuestion question = gameService.getCurrentQuestion();

        // Defensive: always set all buttons enabled/disabled and text
        String[] options = (question != null) ? question.getOptions() : null;
        if (options == null || options.length < 4) {
            System.out.println("ERROR: Options is null or insufficient for level " + (player != null ? player.getLevel() : "?") );
            for (int i = 0; i < 4; i++) {
                optionButtons[i].setText("?");
                optionButtons[i].setEnabled(false);
            }
            return;
        }

        // Update stats
        updateCompactStatCard(playerNameLabel, player.getName());
        updateCompactStatCard(scoreLabel, String.valueOf(player.getScore()));
        updateCompactStatCard(levelLabel, String.valueOf(player.getLevel()));

        // Update lives
        String livesText = "";
        for (int i = 0; i < player.getLives(); i++) {
            livesText += "❤";
        }
        updateCompactStatCard(livesLabel, livesText);

        // Update progress
        int progress = (player.getScore() % 100);
        levelProgress.setValue(progress);
        levelProgress.setString(progress + "%");

        // Update question and options
        if (question != null) {
            String questionText = "<html><div style='text-align: center; line-height: 1.8;'>" +
                "<span style='font-size: 28px; color: #4ECDC4;'>" + question.getQuestion() + "</span>" +
                "</div></html>";
            questionLabel.setText(questionText);

            for (int i = 0; i < 4; i++) {
                optionButtons[i].setText("<html><div style='text-align: center; font-size: 18px;'>" + options[i] + "</div></html>");
                optionButtons[i].setEnabled(true);
            }
        } else if (gameService.getPlayer().getLevel() > 6) {
            // Quiz completed: show congratulation and exit
            JOptionPane.showMessageDialog(this,
                "Selamat telah menamatkan quiz!",
                "Quiz Selesai",
                JOptionPane.INFORMATION_MESSAGE);
            System.exit(0);
        }
    }
    
    private void updateCompactStatCard(JPanel statCard, String newValue) {
        JLabel valueLabel = (JLabel) statCard.getComponent(2); 
        valueLabel.setText(newValue);
    }
}