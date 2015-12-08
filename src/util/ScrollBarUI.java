package util;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.SwingConstants;
import javax.swing.plaf.basic.BasicScrollBarUI;

import client.ClientApp;

public class ScrollBarUI extends BasicScrollBarUI {
	private ClientApp app;	// for colorizing
	
	private DarkButton decreaseButton;
	private DarkButton increaseButton;
	
	public ScrollBarUI(int orientation) {
		
		switch (orientation) {
		case SwingConstants.HORIZONTAL:
			decreaseButton = new DarkButton("◄");
			increaseButton = new DarkButton("►");
			break;
			
		case SwingConstants.VERTICAL:
			decreaseButton = new DarkButton("▲");
			increaseButton = new DarkButton("▼");
			break;
			default:
				throw new IllegalArgumentException("Orientation must be a "
						+ "SwingConstants field of horizontal or vertical!");
		}
		decreaseButton.setBorderPainted(false);
		increaseButton.setBorderPainted(false);
	}
	
	public void init() {
		
	}

	protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
		decreaseButton.setForeground(c.getForeground());
		increaseButton.setForeground(c.getForeground());
		g.setColor(Color.BLACK);
		g.fillRect(trackBounds.x, trackBounds.y, trackBounds.width, trackBounds.height);
		g.setColor(c.getForeground());
		g.drawRect(trackBounds.x, trackBounds.y, trackBounds.width, trackBounds.height);
	}
	
	protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
		g.setColor(c.getForeground());
		g.fillRect(thumbBounds.x, thumbBounds.y, thumbBounds.width, thumbBounds.height);
	}
	
	protected JButton createDecreaseButton(int orientation) {
		return decreaseButton;
	}
	
	protected JButton createIncreaseButton(int orientation) {
		return increaseButton;
	}
}
