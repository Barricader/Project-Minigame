package main;

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
	private Random r;
	
	public Dice(int x, int y) {
		super(x, y, WIDTH, HEIGHT);	// build rectangle
		this.value = 1;
		this.imgs = new Image[] {null, null, null, null, null, null, null};
		r = new Random();
		
		try {
			load();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		isRolling = false;
		animationTimer = new Timer(1000, this);
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
	}
	
	/**
	 * Rolls the dice and generates a random value. Also starts the animation of
	 * the dice.
	 * @param size - Boundary of dice. Typically should be 6.
	 * @return numeric value of dice roll
	 */
	public int roll(int size) {		
		if (!isRolling) {
			isRolling = true;
			animationTimer.start();
		}
		value = r.nextInt(size) + 1;
		//System.out.println("DICE ROLLED: " + value);
		return value;
	}
	
	/**
	 * Method for animation timer. Stops the dice rolling animation timer.
	 */
	public void actionPerformed(ActionEvent e) {
		if (animationTimer.isRunning()) {
			isRolling = false;
			animationTimer.stop();
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
	
	// Accessor methods
	
	public int getValue() {
		return value;
	}
	
	public boolean isRolling() {
		return isRolling;
	}

}

