package main;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;

/**
 * A player should be created from the main start state. A player has a random name,
 * color, and score. During game play, players can move from tile to tile in the board.
 * Depending on what spaces they land on, their scores can be affected by the action
 * of that particular tile. 
 * @author JoJones
 * @author David Kramer
 *
 */
public class Player extends Rectangle implements ActionListener, Comparable<Player> {
	/* Size of players should be uniform for all */
	public static final int WIDTH = 40;
	public static final int HEIGHT = 40;
	
	private String name = "";
	private Color color;
	private Point newLocation;	// the new location that the player will move to
	private Timer animationTimer;	// timer to control player movement animation
	private boolean isSelected;	// have we clicked on a player?
	private boolean isMoving;	// are we moving right now?
	private byte playerID = 0;
	private byte tileID = 0;
	private int score1 = 0;
	private int lastRoll;

	//private int score2 = 0;	// Not implemented currently
	
	/**
	 * @deprecated - We have sub-classed Rectangle
	 */
	private int xPos;
	
	/**
	 * @deprecated - We have sub-classed Rectangle
	 */
	private int yPos;
	
	/**
	 * Default constructor. Sets players position to (0,0) and default WIDTH and HEIGHT.
	 */
	public Player() {
		super(0, 0, WIDTH, HEIGHT);
	}
	
	/**
	 * Constructs a player with a name and a color
	 * @param name - name of Player
	 * @param color - color of player
	 */
	public Player(String name, Color color) {
		super(0, 0, WIDTH, HEIGHT);
		this.name = name;
		this.color = color;
		init();
	}
	
	/**
	 * Constructs a new player with name, color, and ID
	 * @param name - name of Player
	 * @param color - color of Player
	 * @param playerID - ID of Player
	 */
	public Player(String name, Color color, byte playerID) {
		super(0, 0, WIDTH, HEIGHT);
		this.name = name;
		this.color = color;
		this.playerID = playerID;
		init();
	}
	
	/**
	 * Draws the player to the screen with their current location and name.
	 * @param g Graphics context we will draw to
	 */
	public void draw(Graphics g) {
		g.setColor(color);
		
		if (isSelected) {
			g.setColor(color.brighter());
			g.drawRect(x, y, WIDTH, HEIGHT);
			g.fillOval(x, y, WIDTH, HEIGHT);
		} else {
			g.drawRect(x, y, WIDTH, HEIGHT);
			g.drawOval(x, y, WIDTH, HEIGHT);	
		}
		
		if (isMoving) {
			// draw crosshair
			g.drawLine(newLocation.x, newLocation.y, newLocation.x + 10, newLocation.y + 10);
			g.drawLine(newLocation.x + 10, newLocation.y, newLocation.x, newLocation.y + 10);
		}
		
		g.drawString(name, x, y + 50);	// draw name of player
	}
	
	/**
	 * When the player needs to move to a new location (i.e. after a dice roll).
	 * This method will run the animation to move the player to their new
	 * location in a smooth fashion. The location information should be specified
	 * by the tile that they will move to.
	 * @param newLocation - new destination location to move player to
	 */
	public void moveTo(Point newLocation) {
		if (!isMoving) {
			isMoving = true;
			this.newLocation = newLocation;
			animationTimer.start();
		} else {
			
			if (newLocation.x > x) {
				x++;
			} else if (newLocation.x != x) {	// only move x if not equal
				x--;
			}
			
			if (newLocation.y > y) {
				y++;
			} else if (newLocation.y != y) {	// only move y if not equal
				y--;
			}
			
			if (x == newLocation.x && y == newLocation.y) {	// reached destination!
				animationTimer.stop();
				isMoving = false;
				newLocation = null;	// clear out, we don't need anymore
			}
		}
	}
	
	/**
	 * Animation timer update method. Just calls moveTo() to continue
	 * the animation, if possible.
	 */
	public void actionPerformed(ActionEvent e) {
		moveTo(newLocation);
	}
	
	public int compareTo(Player comparePlayer) {
		int compareRoll = comparePlayer.getLastRoll();
		
		// ASC
		//return this.lastRoll - compareRoll;
		// DESC
		return compareRoll - lastRoll;
	}
	
	/**
	 * Finish initializing anything else that is needed.
	 */
	private void init() {
		this.lastRoll = 0;
		animationTimer = new Timer(10, this);	// player movement update timer
		isMoving = false;
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
	
	public void setID(byte ID) {
		this.playerID = ID;
	}
	
	public void setLastRoll(int lr) {
		this.lastRoll = lr;
	}
	
//	public void setGoes(int goes) {
//	this.goes = goes;
//	}
	
	/**
	 * @deprecated - We have sub-classed Rectangle
	 */
	public void setXPos(int x) {
		xPos = x;
	}
	
	/**
	 * @deprecated - We have sub-classed Rectangle
	 */
	public void setYPos(int y) {
		yPos = y;
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
	
	public boolean isSelected() {
		return isSelected;
	}
	
	public boolean isMoving() {
		return isMoving;
	}
	
	public byte getPlayerID() {
		return playerID;
	}
	
	public byte getTileID() {
		return tileID;
	}
	
	public int getLastRoll() {
		return lastRoll;
	}
	
	public int getScore1() {
		return score1;
	}
	
//	public int getGoes() {
//		return goes;
//	}
	
	/**
	 * @deprecated - We have sub-classed Rectangle
	 */
	public int getXPos() {
		return xPos;
	}
	
	/**
	 * @deprecated - We have sub-classed Rectangle
	 */	
	public int getYPos() {
		return yPos;
	}

}