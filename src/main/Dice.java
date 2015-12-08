package main;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.Timer;

import panels.DicePanel;

/**
 * A dice object that renders the dice and randomly rolls when asked
 * @author David Kramer
 * @author JoJones
 *
 */
public class Dice extends Rectangle implements ActionListener {
	private static final long serialVersionUID = 1L;
	public final static int SIZE = 6;
	public static final int WIDTH = 64;
	public static final int HEIGHT = 64;
	private Image[] imgs;
	private int value;
	private boolean isRolling;	// are we rolling and animating the dice?
	private Timer animationTimer;	// timer to control dice animation
	private boolean isEnabled;	
	private Random r;
	private DicePanel dicePanel;
	
	public Dice(int x, int y, DicePanel dicePanel) {
		super(x, y, WIDTH, HEIGHT);	// build rectangle
		this.dicePanel = dicePanel;
		this.value = 1;
		this.imgs = new Image[] {null, null, null, null, null, null, null};
		r = new Random();
		
		try {
			load();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		isRolling = false;
		isEnabled = true;
		animationTimer = new Timer(20, this);
	}
	
	/**
	 * Draws dice to the screen.
	 * @param g Graphics to draw too.
	 */
	public void draw(Graphics g) {
		g.drawRect(x, y, width, height);
		
		if (isRolling) {
			g.drawImage(imgs[r.nextInt(Dice.SIZE) + 1], x, y, width, height, null);	
		} else {
			g.drawImage(imgs[value], x, y, width, height, null);	
		}	
		
		if (!isEnabled) {	// gray out
			g.setColor(new Color(0, 0, 0, 200));
			g.fillRect(x, y, width, height);
		}
	}
	
	/**
	 * Changes the cyan color from dice, to the specified color.
	 * @param newColor
	 */
	public void colorizeDice(Color newColor) {
		Color target = new Color(24, 24, 24);
		for (int i = 1; i <= 6; i++) {
			BufferedImage img = (BufferedImage) imgs[i];
			
			for (int x = 0; x < img.getWidth(); x++) {
				for (int y = 0; y < img.getHeight(); y++) {
					int pixel = img.getRGB(x, y);
					if (pixel != target.getRGB()) {
						img.setRGB(x, y, newColor.getRGB());	
					}
				}
			}
		}
	}
	
	/**
	 * Rolls the dice and generates a random value. Also starts the animation of
	 * the dice.
	 * @param size - Boundary of dice. Typically should be 6.
	 * @return numeric value of dice roll
	 */
	public int roll(int size) {
		isEnabled = false;
		if (!isRolling) {
			isRolling = true;
			animationTimer.start();
		}
		value = r.nextInt(size) + 1;
		return value;
	}
	
	/**
	 * Method for animation timer. Stops the dice rolling animation timer.
	 */
	public void actionPerformed(ActionEvent e) {
		dicePanel.repaint();
		if (animationTimer.isRunning()) {
			isRolling = false;
			animationTimer.stop();
			isEnabled = true;
		}
	}
	
	private void load() throws IOException {
		String[] paths = { "", "res/die1_dark.png", "res/die2_dark.png", "res/die3_dark.png",
						   "res/die4_dark.png", "res/die5_dark.png", "res/die6_dark.png" };
		for (int i = 1; i <= 6; i++) {
			BufferedImage img = ImageIO.read(new File(paths[i]));
			imgs[i] = img;
		}
	}
	
	// Mutator methods
	
	public void setX(int x) {
		this.x = x;
	}
	
	public void setY(int y) {
		this.y = y;
	}
	
	public void setEnabled(boolean b) {
		isEnabled = b;
	}

	// Accessor methods
	
	public int getValue() {
		return value;
	}
	
	public boolean isRolling() {
		return isRolling;
	}
	
	public boolean isEnabled() {
		return isEnabled;
	}

}

