package gameobjects;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;

import javax.imageio.ImageIO;

import util.Vector;

/**
 * A player should be created from the main start state. A player has a random name,
 * color, and score. During game play, players can move from tile to tile in the board.
 * Depending on what spaces they land on, their scores can be affected by the action
 * of that particular tile. 
 * @author JoJones
 * @author David Kramer
 *
 */
public class Player extends Rectangle implements Comparable<Player>, Serializable {
	private static final long serialVersionUID = 1L;
	/* Size of players should be uniform for all */
	public static final int WIDTH = 40;
	public static final int HEIGHT = 40; // DELETE taken?
	public static boolean[] taken = { false, false, false, false, false, false, false, false, false };
	private static Image[] imgs = { null, null, null, null, null, null, null, null, null };
	
	private String name = "";
	private Color color;
	private Point newLocation;	// the new location that the player will move to
	private Tile tile;	// what tile are we on
	private boolean isSelected;	// have we clicked on a player?
	private boolean isActive;	// is this player the active one?
	private boolean isMoving;	// are we moving right now?
	private byte playerID = 0;
	private int score1 = 0;
	private int firstRoll;	// first time rolling value
	private int lastRoll;
	private boolean hasFirstRolled = false;	// has player been rolled for the first time?
	private boolean hasRolled = false;
	private ArrayList<Tile> path;
	//private int score2 = 0;	// Not implemented currently
	private int colorNum;
	
	/**
	 * Default constructor. Sets players position to (0,0) and default WIDTH and HEIGHT.
	 * @deprecated
	 */
	public Player() {
		super(0, 0, WIDTH, HEIGHT);
	}
	
	/**
	 * Constructs a player with a name
	 * @param name - name of Player
	 */
	public Player(String name) {
		super(0, 0, WIDTH, HEIGHT);
		this.name = name;
		colorNum = -1;
		
		Random r = new Random();
		Color myColor = new Color(0);
		
		boolean chosen = false;
		int c = 0;
		
		// Get a random, not yet chosen, number
		while (!chosen) {
			// Change to 9 if you want teal
			c = r.nextInt(8);
			if (!taken[c]) {
				taken[c] = true;
				chosen = true;
				colorNum = c;
			}
		}
		
		// Choose color based on the random number
		switch (colorNum) {
		case 0:
			myColor = new Color(255, 0, 0);
			break;
		case 1:
			myColor = new Color(0, 255, 0);
			break;
		case 2:
			myColor = new Color(0, 0, 255);
			break;
		case 3:
			myColor = new Color(255, 255, 0);
			break;
		case 4:
			myColor = new Color(255, 0, 255);
			break;
		case 5:
			myColor = new Color(0, 255, 255);
			break;
		case 6:
			myColor = new Color(255, 127, 0);
			break;
		case 7:
			myColor = new Color(127, 0, 255);
			break;
		case 8:
			myColor = new Color(0, 255, 127);
			break;
		}
		
		color = myColor;
		init();
	}
	
	/**
	 * Constructs a player with a name and a color
	 * @param name - name of Player
	 * @param color - color of player
	 */
	public Player(String name, int color) {
		super(0, 0, WIDTH, HEIGHT);
		this.name = name;
		colorNum = color;
		
		Random r = new Random();
		Color myColor = new Color(0);
		
		// COMMENTED OUT FOR TESTING -> CREATING PLAYER FROM SERVER, WITH ALREADY SPECIFIED COLOR ID NUM.
//		if (!taken[colorNum]) {
//			boolean chosen = false;
//			int c = 0;
//			
//			// Get a random, not yet chosen, number
//			while (!chosen) {
//				// Change to 9 if you want teal
//				c = r.nextInt(8);
//				if (!taken[c]) {
//					taken[c] = true;
//					chosen = true;
//					colorNum = c;
//				}
//			}
//		}
		
		// Choose color based on the random number
		switch (colorNum) {
		case 0:
			myColor = new Color(255, 0, 0);
			break;
		case 1:
			myColor = new Color(0, 255, 0);
			break;
		case 2:
			myColor = new Color(0, 0, 255);
			break;
		case 3:
			myColor = new Color(255, 255, 0);
			break;
		case 4:
			myColor = new Color(255, 0, 255);
			break;
		case 5:
			myColor = new Color(0, 255, 255);
			break;
		case 6:
			myColor = new Color(255, 127, 0);
			break;
		case 7:
			myColor = new Color(127, 0, 255);
			break;
		case 8:
			myColor = new Color(0, 255, 127);
			break;
		}
		
		this.color = myColor;
		init();
	}
	
	/**
	 * Constructs a new player with name, color, and ID
	 * @param name - name of Player
	 * @param color - color of Player
	 * @param playerID - ID of Player
	 * @deprecated
	 */
	public Player(String name, Color color, byte playerID) {
		super(0, 0, WIDTH, HEIGHT);
		this.name = name;
		this.color = color;
		this.playerID = playerID;
		init();
	}
	
	public void initColor() {
		
	}
	
	public static void load() throws IOException {
		String[] pColors = { "res/pRed.png",    "res/pGreen.png",  "res/pBlue.png",
							 "res/pYellow.png", "res/pPink.png",   "res/pCyan.png",
							 "res/pOrange.png", "res/pPurple.png", "res/pTeal.png" };
		for (int i = 0; i < pColors.length; i++) {
			BufferedImage img = ImageIO.read(new File(pColors[i]));
			imgs[i] = img;
		}
	}
	
	/**
	 * Draws the player to the screen with their current location and name.
	 * @param g Graphics context we will draw to
	 */
	public void draw(Graphics g) {
		final Graphics2D g2d = (Graphics2D)g.create();
		
		try {			
			// Maybe have a highlight when selected?
			// TODO: this won't draw red for some dumb reason, fix me
			if (isActive) {	// draw a translucent oval around active player
				g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); // smooth on
				g2d.setColor(Color.BLACK);
				g2d.setStroke(new BasicStroke(4.0f));
				g2d.drawOval(x - 5, y - 5, 50, 50);
				g2d.setColor(new Color(255, 255, 255, 180));
				g2d.fillOval(x - 5, y - 5, 50, 50);
			}
			g2d.setStroke(new BasicStroke());	// default
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,  RenderingHints.VALUE_ANTIALIAS_OFF); // smooth off
			g2d.drawImage(imgs[colorNum], x, y, null);
			
			if (isMoving) {
				// draw crosshair
				g2d.drawLine(newLocation.x, newLocation.y, newLocation.x + 10, newLocation.y + 10);
				g2d.drawLine(newLocation.x + 10, newLocation.y, newLocation.x, newLocation.y + 10);
			}
			
			g2d.setColor(color);
			
			if (getTile() != null && getTile().y == 4) {
				g2d.drawString(name, x, y - 20);
			} else {
				g2d.drawString(name, x, y + 50);	// draw name of player	
			}
				
		} finally {
			g2d.dispose();
		}
	}
	
	/**
	 * When the player needs to move to a new location (i.e. after a dice roll).
	 * This method will run the animation to move the player to their new
	 * location in a smooth fashion. The location information should be specified
	 * by the array of tiles that they will move through plus the destination tile.
	 */
	public void move() {
		if (!isMoving && path.size() > 0) {
			isMoving = true;
			newLocation = path.get(0).getLocation(playerID);
		}
		else {
			if (path.size() != 0) {
				double finalX = this.newLocation.x - (double)x;
				double finalY = this.newLocation.y - (double)y;
				Vector vec = new Vector(finalX, finalY);
				vec = vec.normalize();
				
				x += vec.getdX() * 3;
				y += vec.getdY() * 3;
				
				if ((x < this.newLocation.x + 2 && x > this.newLocation.x - 2) &&
					(y < this.newLocation.y + 2 && y > this.newLocation.y - 2)) {

					path.remove(0);
					if (path.size() != 0) {
						newLocation = path.get(0).getLocation(playerID);
					}
				}
			}
			else {
				isMoving = false;
				newLocation = null;
			}
		}
	}
	
	/**
	 * Allows you to specify a point to move to
	 * @param p - Point to travel to
	 */
	public void move(Point p) {
		// Maybe change this to x/64 and y/64
		path.add(new Tile(p.x, p.y, 64, 64));
		move();
	}     
	
	/**
	 * Specifies a tile to move to, only use this for testing and for the start, (and teleporting)?
	 * @param t - Tile to travel to
	 */
	public void move(Tile t) {
		path.add(t);
		move();
	}
	
	/**
	 * Updates the player every 1/60 of a second
	 */
	public void update() {
		if (newLocation != null) {
			move();
		}
	}
	
	public int compareTo(Player comparePlayer) {
		// changed to first roll compare
		int compareRoll = comparePlayer.getFirstRoll();
		
		// ASC
		//return this.lastRoll - compareRoll;
		// DESC - largest roll to shortest roll
		return compareRoll - firstRoll;
	}
	
	/**
	 * Finish initializing anything else that is needed.
	 */
	private void init() {
		this.lastRoll = 0;
		isMoving = false;
		path = new ArrayList<Tile>();
	}
	
	// mutator methods
	
	public void setNewLocation(Point newLocation) {
		this.newLocation = newLocation;
	}
	
	public void addScore1() {
		score1++;
	}
	
	public void minusScore1() {
		score1--;
	}
	
	public void setSelected(boolean b) {
		isSelected = b;
	}
	
	public void setActive(boolean b) {
		isActive = b;
	}
	
	public void setID(byte ID) {
		this.playerID = ID;
	}
	
	public void setTile(Tile t) {
		this.tile = t;
	}
	
	public void setFirstRoll(int roll) {
		firstRoll = roll;
	}
	
	public void setLastRoll(int lr) {
		this.lastRoll = lr;
	}
	
	public void setHasFirstRolled(boolean b) {
		hasFirstRolled = b;
	}
	
	public void setHasRolled(boolean b) {
		hasRolled = b;
	}
	
	// accessor methods
	
	public String getName() {
		return name;
	}
	
	public String toString() {
		return name;
	}
	
	public Color getColor() {
		return color;
	}
	
	public Tile getTile() {
		return tile;
	}
	
	public boolean isSelected() {
		return isSelected;
	}
	
	public boolean isMoving() {
		return isMoving;
	}
	
	public boolean isActive() {
		return isActive;
	}
	
	public boolean hasFirstRolled() {
		return hasFirstRolled;
	}
	
	public boolean hasRolled() {
		return hasRolled;
	}
	
	public int getFirstRoll() {
		return firstRoll;
	}
	
	public byte getPlayerID() {
		return playerID;
	}
	
	public byte getTileID() {
		byte t = (byte)(tile.getTileID() + 1);
		if (t > 26) {
			return 0;
		}
		else {
			return t;
		}
	}
	
	public int getLastRoll() {
		return lastRoll;
	}
	
	public int getScore1() {
		return score1;
	}
	
	public void setPath(ArrayList<Tile> p) {
		path = p;
	}
	
	public ArrayList<Tile> getPath() {
		return path;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public int getColorNum() {
		return colorNum;
	}
}