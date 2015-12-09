package util;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;

public class DarkButton extends JButton {
	private static final long serialVersionUID = 3153239231686497294L;
	public static final Color BG_COLOR = Color.BLACK;
	public static final Color FG_COLOR = Color.CYAN;
	private boolean isHovered;
	private boolean isPressed;
	public DarkButton(String text) {
		super(text);
		setOpaque(true);
		setBackground(BG_COLOR);
//		setForeground(FG_COLOR);
		setFocusable(false);
		setFocusPainted(false);
		
		addMouseMotionListener(new MouseAdapter() {
			public void mouseMoved(MouseEvent e) {
				if (contains(e.getPoint())) {
					isHovered = true;
					Cursor cursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
					setCursor(cursor);
				} else {
					isHovered = false;
					Cursor cursor = Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR);
					setCursor(cursor);
				}
			}
		});
		
		addMouseListener(new MouseAdapter() {
			public void mouseExited(MouseEvent e) {
				isHovered = false;
				Cursor cursor = Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR);
				setCursor(cursor);
			}
			
			public void mousePressed(MouseEvent e) {
				isPressed = true;
			}
			
			public void mouseReleased(MouseEvent e) {
				isPressed = false;
			}
		});
		
		repaint();
	}
	
	public int getFontHeight() {
		return getFont().getSize() / 2;
	}
	
	public int getTextWidth() {
		return getFontMetrics(getFont()).stringWidth(getText());
	}
	
	public int getTextX() {
		return (getWidth() - getTextWidth()) / 2;
	}
	
	public int getTextY() {
		return (getHeight() + getFontHeight()) / 2;
	}
	
	public void paintComponent(Graphics g) {
		final Graphics2D g2d = (Graphics2D)g.create();
		try {
			g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
			
			Color textColor = null;
			Color borderColor = null;
			Color bgColor = null;
			
			g.setColor(getBackground());
			g.fillRect(0, 0, getWidth(), getHeight());	// clear out first
			
			// determine how to colorize button
			if (!isEnabled()) {
				isHovered = false;	// don't draw highlight!
				borderColor = (new Color(0, 255, 255, 20));
				bgColor = getBackground().darker().darker();
				textColor = getForeground().darker().darker();
			} else {
				textColor = getForeground();
				bgColor = getBackground();
				borderColor = getForeground();	
				textColor = getForeground();
			} 
			
			if (isHovered) {
				bgColor = GameUtils.getAlphaColor(getForeground(), 85);
			}
			
			if (isPressed) {
				bgColor = getBackground().darker();
				textColor = getForeground().darker();
			}
			
			
			// draw background
			g2d.setColor(bgColor);
			g2d.fillRect(0, 0, getWidth(), getHeight());
			// draw border
			g2d.setColor(borderColor);
			g2d.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
			// draw text
			g2d.setColor(textColor);
			g2d.setFont(getFont());
			g2d.drawString(getText(), getTextX(), getTextY());
	
			
		} finally {
			g2d.dispose();
		}
		
	}
}
