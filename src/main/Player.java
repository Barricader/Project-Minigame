package main;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

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
public class Player extends Rectangle implements Comparable<Player> {
	private static final long serialVersionUID = 1L;
	/* Size of players should be uniform for all */
	public static final int WIDTH = 40;
	public static final int HEIGHT = 40;
	
	private String name = "";
	private Color color;
	private Point newLocation;	// the new location that the player will move to
	private Tile tile;	// what tile are we on
	private boolean isSelected;	// have we clicked on a player?
	private boolean isMoving;	// are we moving right now?
	private byte playerID = 0;
	private int score1 = 0;
	private int lastRoll;
	private boolean hasFirstRolled = false;	// has player been rolled for the first time?
	private ArrayList<Tile> path;
	//private int score2 = 0;	// Not implemented currently
	
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
		final Graphics2D g2d = (Graphics2D)g.create();
		
		try {
			g2d.setStroke(new BasicStroke(1.0f));
			g2d.setColor(color);
			
			if (isSelected) {
				g2d.setColor(color.brighter());
				g2d.drawRect(x, y, WIDTH, HEIGHT);
				g2d.fillOval(x, y, WIDTH, HEIGHT);
			} else {
				g2d.drawRect(x, y, WIDTH, HEIGHT);
				g2d.drawOval(x, y, WIDTH, HEIGHT);	
			}
			
			if (isMoving) {
				// draw crosshair
				g2d.drawLine(newLocation.x, newLocation.y, newLocation.x + 10, newLocation.y + 10);
				g2d.drawLine(newLocation.x + 10, newLocation.y, newLocation.x, newLocation.y + 10);
			}
			
			g2d.drawString(name, x, y + 50);	// draw name of player	
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
			newLocation = path.get(0).getLocation();
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
						newLocation = path.get(0).getLocation();
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
		int compareRoll = comparePlayer.getLastRoll();
		
		// ASC
		//return this.lastRoll - compareRoll;
		// DESC - largest roll to shortest roll
		return compareRoll - lastRoll;
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
	
	public void setID(byte ID) {
		this.playerID = ID;
	}
	
	public void setTile(Tile t) {
		this.tile = t;
	}
	
	public void setLastRoll(int lr) {
		this.lastRoll = lr;
	}
	
	public void setHasFirstRolled(boolean b) {
		hasFirstRolled = b;
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
	
	public boolean hasFirstRolled() {
		return hasFirstRolled;
	}
	
	public byte getPlayerID() {
		return playerID;
	}
	
	public byte getTileID() {
		return tile.getTileID();
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
}