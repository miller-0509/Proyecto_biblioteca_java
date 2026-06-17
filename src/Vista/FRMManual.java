package Vista;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

public class FRMManual extends JInternalFrame {

    private static final Color SENA_BLUE = new Color(54, 119, 229);
    private static final Color TEXT_DARK = new Color(28, 34, 45);
    private static final Color TEXT_SOFT = new Color(96, 105, 121);

    public FRMManual() {
        super("Manual de Usuario", true, true, true, true);
        initComponents();
    }

    private void initComponents() {
        setSize(900, 600);
        setMinimumSize(new Dimension(700, 450));

        JPanel contentPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setPaint(new GradientPaint(0, 0, new Color(245, 248, 250), 0, getHeight(), new Color(235, 240, 245)));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        contentPanel.setOpaque(false);

        JPanel centerPanel = new JPanel();
        centerPanel.setOpaque(false);
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBorder(new EmptyBorder(60, 40, 60, 40));

        JLabel iconLabel = new JLabel("PDF", SwingConstants.CENTER);
        iconLabel.setFont(new Font("SansSerif", Font.BOLD, 48));
        iconLabel.setForeground(SENA_BLUE);
        iconLabel.setAlignmentX(0.5f);

        JLabel titleLabel = new JLabel("Manual de Usuario", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 26));
        titleLabel.setForeground(TEXT_DARK);
        titleLabel.setAlignmentX(0.5f);

        JLabel descLabel = new JLabel("<html><div style='text-align:center;width:400px;'>Guía detallada con los procesos, permisos y límites correspondientes a tu perfil.</div></html>", SwingConstants.CENTER);
        descLabel.setFont(new Font("SansSerif", Font.PLAIN, 15));
        descLabel.setForeground(TEXT_SOFT);
        descLabel.setAlignmentX(0.5f);

        JLabel stateLabel = new JLabel("En desarrollo", SwingConstants.CENTER);
        stateLabel.setOpaque(true);
        stateLabel.setBackground(new Color(54, 119, 229));
        stateLabel.setForeground(Color.WHITE);
        stateLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
        stateLabel.setBorder(new EmptyBorder(10, 20, 10, 20));
        stateLabel.setAlignmentX(0.5f);

        centerPanel.add(Box.createVerticalGlue());
        centerPanel.add(iconLabel);
        centerPanel.add(Box.createVerticalStrut(16));
        centerPanel.add(titleLabel);
        centerPanel.add(Box.createVerticalStrut(12));
        centerPanel.add(descLabel);
        centerPanel.add(Box.createVerticalStrut(24));
        centerPanel.add(stateLabel);
        centerPanel.add(Box.createVerticalGlue());

        contentPanel.add(centerPanel, BorderLayout.CENTER);
        setContentPane(contentPanel);
    }
}
