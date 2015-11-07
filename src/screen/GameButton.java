package screen;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JButton;

import main.Main;

/**
 * Custom button appearance that changes from the default look and feel
 * of the Swing JButton. Game buttons have a specified background color.
 * @author David Kramer
 *
 */
public class GameButton extends JButton implements MouseListener {	
	private Color bgColor;	// bg color of button
	private boolean isHovered;

	public GameButton(String text, Color bgColor) {
		super(text);
		setContentAreaFilled(false);
		setFocusPainted(false);
		setFont(new Font("Courier New", Font.BOLD, 12));
		setOpaque(false);
		setForeground(Color.WHITE);
		addMouseListener(this);
		this.bgColor = bgColor;
	}
	
	public void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D)g;
        
        if (!isEnabled()) {
        	g2d.setColor(bgColor.darker());
        } else if (isHovered) {
        	g2d.setColor(bgColor.brighter());
        } else {
        	g2d.setColor(bgColor);	
        }
        
        g2d.fillRect(0, 0, getWidth(), getHeight());
        
        super.paintComponent(g);
        g2d.dispose();
	}
	
	public void mouseEntered(MouseEvent e) {
		isHovered = true;
	}

	public void mouseExited(MouseEvent e) {
		isHovered = false;
	}

	// unused
	public void mouseClicked(MouseEvent e) {}
	public void mousePressed(MouseEvent e) {}
	public void mouseReleased(MouseEvent e) {}


}
