package screen;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JPanel;

/**
 * Holds the board stuff
 * @author JoJones
 *
 */
public class Board extends JPanel {
	private static final long serialVersionUID = 1L;

	public Board() {
		
	}
	
	public void paintComponent(Graphics g) {
		g.clearRect(0, 0, getWidth(), getHeight());
		for (int i = 0; i < 20; i++) {
			for (int j = 0; j < 20; j++) {
				Color c = GameUtils.getRandomColor();
				g.setColor(c);
				g.fillRect(i*100, j * 100, 100, 400);
			}
		}
	}
}
