package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class JumpscarePanel extends JPanel {
    private Timer timer;
    private boolean showScare;
    private String scareMessage;
    private Color primaryColor = Color.RED;
    private Color accent = Color.YELLOW;

    public JumpscarePanel() {
        setPreferredSize(new Dimension(800, 600));
        setBackground(Color.BLACK);
        showScare = false;

        Toolkit.getDefaultToolkit().beep();
    }

    public void triggerJumpscare(int remainingLives) {
        showScare = true;
        if (remainingLives <= 0) {
            scareMessage = "🔥 GAME OVER! 🔥";
            primaryColor = Color.BLACK;
            accent = Color.RED.darker();
        } else if (remainingLives == 1) {
            scareMessage = "💀 HATI-HATI! 💀";
            primaryColor = new Color(60, 0, 0);
            accent = Color.ORANGE;
        } else {
            scareMessage = generateScareMessage();
            primaryColor = Color.RED;
            accent = Color.YELLOW;
        }

        repaint();

        if (timer != null && timer.isRunning()) timer.stop();
        timer = new Timer(2000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showScare = false;
                timer.stop();
                repaint();
            }
        });
        timer.setRepeats(false);
        timer.start();
    }

    private String generateScareMessage() {
        String[] messages = {
            "❌ SALAH! ❌",
            "💀 HAMPIR MATI! 💀",
            "👻 AWAS! 👻",
            "⚡ KESALAHAN FATAL! ⚡"
        };
        return messages[(int) (Math.random() * messages.length)];
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g.create();
        GradientPaint gradient = new GradientPaint(0, 0, primaryColor, getWidth(), getHeight(), Color.DARK_GRAY);
        g2d.setPaint(gradient);
        g2d.fillRect(0, 0, getWidth(), getHeight());

        if (showScare) {
            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            Font mainFont = new Font("Segoe UI Emoji", Font.BOLD, 56);
            g2d.setFont(mainFont);
            FontMetrics fmMain = g2d.getFontMetrics(mainFont);
            int mainWidth = fmMain.stringWidth(scareMessage);
            int mainX = (getWidth() - mainWidth) / 2;
            int mainY = (getHeight() - fmMain.getHeight()) / 2 + fmMain.getAscent();

            g2d.setColor(Color.BLACK);
            g2d.drawString(scareMessage, mainX + 4, mainY + 4);

            g2d.setColor(accent);
            g2d.drawString(scareMessage, mainX, mainY);

            Font subFont = new Font("Segoe UI", Font.BOLD, 22);
            g2d.setFont(subFont);
            FontMetrics fmSub = g2d.getFontMetrics(subFont);
            String sub = "Kamu kehilangan 1 nyawa!";
            int subWidth = fmSub.stringWidth(sub);
            int subX = (getWidth() - subWidth) / 2;
            int subY = mainY + fmMain.getDescent() + fmSub.getAscent() + 12;
            g2d.setColor(accent);
            g2d.drawString(sub, subX, subY);
        }

        g2d.dispose();
    }
}