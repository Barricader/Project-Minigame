package screen;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JButton;

/**
 * Custom button appearance that changes from the default look and feel
 * of the Swing JButton.
 * @author David Kramer
 *
 */
public class GameButton extends JButton {	
	private GradientPaint gradient;	// gradient fill style of the button

	public GameButton(String text) {
		super(text);
		setFocusPainted(false);
		setOpaque(false);
	}
	
	public void setGradient(GradientPaint gradient) {
		this.gradient = gradient;
	}
	
	
	public void paintComponent(Graphics g) {
		Graphics2D g2d = (Graphics2D)g;
		g2d.setPaint(Styles.HORIZONTAL_BLUE);
		g2d.fillRect(0, 0, getWidth(), getHeight());
		g2d.setColor(Color.WHITE);
	}
}
