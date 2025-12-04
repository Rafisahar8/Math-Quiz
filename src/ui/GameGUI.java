package ui;

import service.GameService;
import model.MathQuestion;
import model.Player;

import javax.swing.*;
import javax.sound.sampled.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

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
    private JLabel questionCounterLabel;
    
    private final Color PRIMARY_COLOR = new Color(74, 107, 255);    
    private final Color SECONDARY_COLOR = new Color(255, 94, 124);  
    private final Color ACCENT_COLOR = new Color(46, 196, 182);     
    private final Color BACKGROUND_COLOR = new Color(18, 18, 32);   
    private final Color CARD_COLOR = new Color(30, 30, 50);         
    private final Color TEXT_COLOR = new Color(255, 255, 255);      
    
    private Set<String> usedQuestions = new HashSet<>();
    private String lastQuestion = "";
    
    public GameGUI() {
        initializeGame();
        setupUI();
        setupKeyBindings();
        startGame();
    }
    
    private void initializeGame() {
        String playerName = "User";  
        
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
        nameField.setText(" ");  
        
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
            if (playerName.isEmpty()) {
                playerName = "User";
            }
        } else if (result == JOptionPane.CANCEL_OPTION) {
            int confirm = JOptionPane.showConfirmDialog(null, 
                "Yakin mau keluar dari game?", "Konfirmasi Keluar",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (confirm == JOptionPane.YES_OPTION) {
                System.exit(0);
            } else {
                playerName = "User";
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
    
    private void setupKeyBindings() {
        InputMap inputMap = getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = getRootPane().getActionMap();
        
        inputMap.put(KeyStroke.getKeyStroke("F5"), "refresh");
        actionMap.put("refresh", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                refreshQuestion();
            }
        });
        
        inputMap.put(KeyStroke.getKeyStroke("ESCAPE"), "exit");
        actionMap.put("exit", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int confirm = JOptionPane.showConfirmDialog(GameGUI.this,
                    "Yakin mau keluar dari game?", "Konfirmasi Keluar",
                    JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                if (confirm == JOptionPane.YES_OPTION) {
                    System.exit(0);
                }
            }
        });
    }
    
    private void refreshQuestion() {
        if (lastQuestion != null && !lastQuestion.isEmpty()) {
            usedQuestions.remove(lastQuestion);
        }
        
        gameService.startNewQuestion();
        preventDuplicateQuestion();
        updateUI();
        
        JOptionPane.showMessageDialog(this, 
            "Pertanyaan telah direfresh!", 
            "Refresh", 
            JOptionPane.INFORMATION_MESSAGE);
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
        JPanel statsPanel = new JPanel();
        statsPanel.setLayout(new BoxLayout(statsPanel, BoxLayout.X_AXIS));
        statsPanel.setBackground(BACKGROUND_COLOR);
        statsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        playerNameLabel = createCompactStatCard("Player", gameService.getPlayer().getName(), "👤");
        scoreLabel = createCompactStatCard("Score", "0", "⭐");
        levelLabel = createCompactStatCard("Level", "1/8", "🚀");
      
        String initialLives = "❤❤❤"; 
        livesLabel = createCompactStatCard("Nyawa", initialLives, "💖");
        
        JPanel progressPanel = createProgressPanel();
        
        statsPanel.add(Box.createHorizontalGlue());
        statsPanel.add(playerNameLabel);
        statsPanel.add(Box.createRigidArea(new Dimension(15, 0)));
        statsPanel.add(scoreLabel);
        statsPanel.add(Box.createRigidArea(new Dimension(15, 0)));
        statsPanel.add(levelLabel);
        statsPanel.add(Box.createRigidArea(new Dimension(15, 0)));
        statsPanel.add(livesLabel);
        statsPanel.add(Box.createRigidArea(new Dimension(15, 0)));
        statsPanel.add(progressPanel);
        statsPanel.add(Box.createHorizontalGlue());
        
        return statsPanel;
    }
    
    private JPanel createCompactStatCard(String title, String value, String icon) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(CARD_COLOR);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(PRIMARY_COLOR, 1),
            BorderFactory.createEmptyBorder(12, 15, 12, 15)
        ));
        card.setPreferredSize(new Dimension(180, 85));
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(CARD_COLOR);

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setBackground(CARD_COLOR);
        topPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel iconTitlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        iconTitlePanel.setBackground(CARD_COLOR);

        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
        iconLabel.setForeground(ACCENT_COLOR);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        titleLabel.setForeground(new Color(200, 200, 200));

        iconTitlePanel.add(iconLabel);
        iconTitlePanel.add(titleLabel);

        topPanel.add(iconTitlePanel);

        JLabel valueLabel = new JLabel("<html><div style='text-align: center;'>" + value + "</div></html>");
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        valueLabel.setForeground(TEXT_COLOR);
        valueLabel.setHorizontalAlignment(SwingConstants.CENTER);

        contentPanel.add(topPanel, BorderLayout.NORTH);
        contentPanel.add(valueLabel, BorderLayout.CENTER);

        card.add(contentPanel, BorderLayout.CENTER);

        return card;
    }

    private String formatLives(int lives) {
        StringBuilder hearts = new StringBuilder();
        for (int i = 0; i < Math.min(lives, 5); i++) {
            hearts.append("❤️"); 
        }
        
        if (lives > 5) {
            return hearts.toString() + " +" + (lives - 5);
        }
        
        if (lives <= 0) {
            return "💀";
        }
        
        return hearts.toString();
    }
    
    private JPanel createProgressPanel() {
        JPanel progressPanel = new JPanel(new BorderLayout());
        progressPanel.setBackground(CARD_COLOR);
        progressPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(PRIMARY_COLOR, 1),
            BorderFactory.createEmptyBorder(12, 15, 12, 15)
        ));
        progressPanel.setPreferredSize(new Dimension(180, 75));
        
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(CARD_COLOR);
        contentPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
    
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        titlePanel.setBackground(CARD_COLOR);
        titlePanel.setMaximumSize(new Dimension(150, 25));
        
        JLabel progressIcon = new JLabel("📊");
        progressIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
        progressIcon.setForeground(ACCENT_COLOR);
        
        JLabel progressTitle = new JLabel("Progress Level");
        progressTitle.setFont(new Font("Segoe UI", Font.BOLD, 12));
        progressTitle.setForeground(new Color(200, 200, 200));
        
        titlePanel.add(progressIcon);
        titlePanel.add(progressTitle);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        levelProgress = new JProgressBar(0, 100);
        levelProgress.setValue(0);
        levelProgress.setStringPainted(true);
        levelProgress.setFont(new Font("Segoe UI", Font.BOLD, 12));
        levelProgress.setForeground(ACCENT_COLOR);
        levelProgress.setBackground(new Color(50, 50, 70));
        levelProgress.setPreferredSize(new Dimension(140, 25));
        levelProgress.setMinimumSize(new Dimension(140, 25));
        levelProgress.setMaximumSize(new Dimension(140, 25));
        levelProgress.setAlignmentX(Component.CENTER_ALIGNMENT);
        levelProgress.setString("0%");
        levelProgress.setToolTipText("Soal: 0/10");
        
        contentPanel.add(titlePanel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        contentPanel.add(levelProgress);
        
        progressPanel.add(contentPanel, BorderLayout.CENTER);
        
        return progressPanel;
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
        JPanel optionsPanel = new JPanel(new GridLayout(2, 2, 20, 20)); 
        optionsPanel.setBackground(BACKGROUND_COLOR);
        optionsPanel.setBorder(new EmptyBorder(0, 40, 40, 40)); 
        
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
            optionButtons[i].setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); 
            optionButtons[i].setHorizontalAlignment(SwingConstants.CENTER);
            optionButtons[i].setVerticalAlignment(SwingConstants.CENTER);
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
        private int cornerRadius = 25;
        
        public RoundedButton(String text) {
            super(text);
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
            setHorizontalTextPosition(SwingConstants.CENTER);
            setVerticalTextPosition(SwingConstants.CENTER);
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
        
        @Override
        public void setText(String text) {
            if (!text.startsWith("<html>")) {
                text = "<html><div style='text-align: center;'>" + text + "</div></html>";
            }
            super.setText(text);
        }
    }
    
    private void startGame() {
        gameService.startNewQuestion();
        preventDuplicateQuestion();
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
                usedQuestions.clear();
                lastQuestion = "";
            }
            
            gameService.startNewQuestion();
            preventDuplicateQuestion();
            updateUI();
        } else {
            int remaining = gameService.getPlayer().getLives();
            if (remaining <= 1) {
                playGameOverScream();
            } else {
                playWrongTone();
            }
            showJumpscare(remaining);
            
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

    private void preventDuplicateQuestion() {
        MathQuestion currentQuestion = gameService.getCurrentQuestion();
        if (currentQuestion != null) {
            String questionText = currentQuestion.getQuestion();
            
            if (usedQuestions.contains(questionText) || questionText.equals(lastQuestion)) {
                int maxAttempts = 5;
                int attempts = 0;
                
                while ((usedQuestions.contains(questionText) || questionText.equals(lastQuestion)) && attempts < maxAttempts) {
                    gameService.startNewQuestion();
                    currentQuestion = gameService.getCurrentQuestion();
                    if (currentQuestion == null) break;
                    questionText = currentQuestion.getQuestion();
                    attempts++;
                }
            }
            
            if (currentQuestion != null) {
                lastQuestion = questionText;
                usedQuestions.add(questionText);
                
                if (usedQuestions.size() > 50) {
                    usedQuestions.clear();
                }
            }
        }
    }
    
    private void showCorrectFeedback() {
        playGoodJobTone();

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
            "<span style='color: white; font-size: 14px;'>+" + 
            (gameService.getCurrentQuestion() != null ? gameService.getCurrentQuestion().getPoints() : 0) + 
            " poin!</span>" +
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
    
    private void playSound(byte[] audioData) {
        try {
            AudioFormat format = new AudioFormat(44100, 8, 1, true, false);
            InputStream byteArrayInputStream = new ByteArrayInputStream(audioData);
            AudioInputStream audioInputStream = new AudioInputStream(byteArrayInputStream, format, 
                                                                     audioData.length / format.getFrameSize());
            
            Clip clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            clip.start();
            
        } catch (Exception e) {
            System.err.println("Error playing sound: " + e.getMessage());
            Toolkit.getDefaultToolkit().beep();
        }
    }
    
    private byte[] generateSuccessSound() {
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
        return buf;
    }
    
    private byte[] generateLevelUpSound() {
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
        return buf;
    }
    
    private byte[] generateGoodJobSound() {
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
        return buf;
    }
    
    private byte[] generateWrongSound() {
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
        return buf;
    }
    
    private byte[] generateGameOverScreamSound() {
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
        return buf;
    }
    
    private byte[] generateGameOverSound() {
        final float SAMPLE_RATE = 44100f;
        final int ms = 700;
        byte[] buf = new byte[(int) (SAMPLE_RATE * ms / 1000)];
        double freq = 110;
        for (int i = 0; i < buf.length; i++) {
            double t = (double) i / SAMPLE_RATE;
            double angle = 2.0 * Math.PI * i * freq / SAMPLE_RATE;
            double env = 1.0 - (double) i / buf.length;
            buf[i] = (byte) (Math.sin(angle) * 127.0 * 0.35 * env);
        }
        return buf;
    }
    
    private void playSuccessTone() {
        playSound(generateSuccessSound());
    }
    
    private void playLevelUpTone() {
        playSound(generateLevelUpSound());
    }
    
    private void playGameOverTone() {
        playSound(generateGameOverSound());
    }
    
    private void playGoodJobTone() {
        playSound(generateGoodJobSound());
    }
    
    private void playWrongTone() {
        playSound(generateWrongSound());
    }
    
    private void playGameOverScream() {
        playSound(generateGameOverScreamSound());
    }
    
    private void showJumpscare(int remainingLives) {
        jumpscarePanel.triggerJumpscare(remainingLives);
        cardLayout.show(mainPanel, "jumpscare");
    }
    
    private void showLevelUpMessage() {
        playLevelUpTone();

        JPanel levelUpPanel = new JPanel(new BorderLayout());
        levelUpPanel.setBackground(BACKGROUND_COLOR);
        levelUpPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel titleLabel = new JLabel("LEVEL UP!", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(new Color(255, 215, 0)); 
        
        JLabel messageLabel = new JLabel("<html><div style='text-align: center;'>" +
            "<span style='color: white; font-size: 16px;'>Sekarang kamu di <b>Level " + 
            gameService.getPlayer().getLevel() + "/8</b></span>" +
            "</div></html>", JLabel.CENTER);
        
        levelUpPanel.add(titleLabel, BorderLayout.NORTH);
        levelUpPanel.add(messageLabel, BorderLayout.CENTER);
        
        JOptionPane.showMessageDialog(this, levelUpPanel, "Selamat!", 
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void gameOver() {
        playGameOverScream();

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
            "<b>Level Tertinggi:</b> %d/8<br>" +
            "<b>Score Akhir:</b> <span style='color: #FFD700;'>%d</span><br><br>" +
            "<span style='color: #4ECDC4;'>Coba lagi untuk mencapai score yang lebih tinggi!</span>" +
            "</div></html>",
            player.getName(), player.getLevel(), player.getScore()
        );
        
        JLabel messageLabel = new JLabel(message, JLabel.CENTER);
        messageLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        
        gameOverPanel.add(emojiLabel, BorderLayout.NORTH);
        gameOverPanel.add(messageLabel, BorderLayout.CENTER);
        
        Object[] options = {"Coba Lagi", "Keluar"};
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
        usedQuestions.clear();
        lastQuestion = "";
        gameService.startNewQuestion();
        preventDuplicateQuestion();
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

        updateCompactStatCard(playerNameLabel, player.getName());
        updateCompactStatCard(scoreLabel, String.valueOf(player.getScore()));
        updateCompactStatCard(levelLabel, player.getLevel() + "/8");
        
        int lives = player.getLives();
        StringBuilder hearts = new StringBuilder();
        for (int i = 0; i < lives; i++) {
            hearts.append("❤");
        }

        if (lives <= 0) {
            hearts.append("💀");
        }
        updateCompactStatCard(livesLabel, hearts.toString());

        double progress = player.getProgressPercentage();
        levelProgress.setValue((int) progress);
        levelProgress.setString(String.format("%.0f%%", progress));
        
        levelProgress.setToolTipText(String.format("Soal: %d/10", 
            player.getLevelProgress().getQuestionsInCurrentLevel()));

        if (question != null) {
            String questionText = "<html><div style='text-align: center; padding: 10px;'>" +
                "<span style='color: #4ECDC4; font-size: 30px; font-weight: bold; line-height: 1.5;'>" + 
                question.getQuestion() + 
                "</span></div></html>";
            questionLabel.setText(questionText);

            String[] options = question.getOptions();
            for (int i = 0; i < 4; i++) {
                if (i < options.length && options[i] != null) {
                    String buttonText = "<html><div style='text-align: center; padding: 15px;'>" + 
                                    "<span style='font-size: 20px; font-weight: bold;'>" + 
                                    options[i] + 
                                    "</span></div></html>";
                    optionButtons[i].setText(buttonText);
                    optionButtons[i].setEnabled(true);
                    optionButtons[i].setVisible(true);
                } else {
                    optionButtons[i].setText("<html><div style='text-align: center; padding: 15px;'>?</div></html>");
                    optionButtons[i].setEnabled(false);
                }
            }
            
        } else if (gameService.isQuizComplete()) {
            showQuizCompletionDialog();
        } else {
            questionLabel.setText("<html><div style='text-align: center; color: red; padding: 20px;'>" +
                                "ERROR: Tidak ada pertanyaan.<br>Tekan F5 untuk refresh.</div></html>");
            
            for (JButton btn : optionButtons) {
                btn.setText("<html><div style='text-align: center; padding: 15px;'>?</div></html>");
                btn.setEnabled(false);
            }
        }
        
        mainPanel.revalidate();
        mainPanel.repaint();
    }
    
    private void showQuizCompletionDialog() {
        Player player = gameService.getPlayer();

        JOptionPane.showMessageDialog(this,
            "SELAMAT " + player.getName().toUpperCase() + "!\n\n" +
            "TELAH MENAMATKAN QUIZ INI\n\n" +
            "Level Tertinggi: 8/8\n" +
            "Score Akhir: " + player.getScore(),
            "Quiz Selesai!",
            JOptionPane.INFORMATION_MESSAGE);

        int choice = JOptionPane.showConfirmDialog(this,
            "Apakah Anda ingin bermain lagi?",
            "Main Lagi?",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);

        if (choice == JOptionPane.YES_OPTION) {
            restartGame();
        } else {
            System.exit(0);
        }
    }

    private void updateCompactStatCard(JPanel statCard, String newValue) {
        if (statCard.getComponentCount() > 0) {
            Component comp = statCard.getComponent(0);
            if (comp instanceof JPanel) {
                JPanel contentPanel = (JPanel) comp;
                for (int i = contentPanel.getComponentCount() - 1; i >= 0; i--) {
                    Component c = contentPanel.getComponent(i);
                    if (c instanceof JLabel) {
                        JLabel label = (JLabel) c;
                        if (label.getFont().getSize() == 18) {
                            label.setText("<html><div style='text-align: center;'>" + newValue + "</div></html>");
                            break;
                        }
                    }
                }
            }
        }
    }
}
