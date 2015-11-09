package main;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;

import screen.GameUtils;

/**
 * A tile is a rectangular shape that is contained within the grid of the board. A tile
 * has an (x,y) coordinate, a random color, and a random action or mini-game event. A tile
 * also has an ID, which will be used to keep track of moving players, when the dice is
 * rolled in the game. Based on the players' current position, whatever amount they roll,
 * the tile that the player will land on will be their current tile + dice roll amount.
 * The implementation for player movement will be handled in the player class, but that
 * is the importance of ID's.
 * @author JoJones
 * @author David Kramer
 */
public class Tile extends Rectangle {
	public static final int ACTION_ADDSCORE1 = 0;
	public static final int ACTION_SUBTRACT = 1;
	public static final int ACTION_ADDSCORE2 = 2;
	public static byte TILE_ID;
	//public static final int ACTION_MINIGAME = 3;
	
	private Color color;
	private int action;
	private byte ID;
	
	/**
	 * @deprecated	- We have sub-classed Rectangle
	 */
	private int xPos;
	/**
	 * @deprecated - We have sub-classed Rectangle
	 */
	private int yPos;
	
	/**
	 * Constructs a new tile
	 * @param color - Color of tile
	 * @param action - Action of tile
	 * @param ID - Tile ID for location
	 * @param x - x coordinate
	 * @param y - y coordinate
	 * @param width - width of tile
	 * @param height - height of tile
	 */
	public Tile(Color color, int action, byte ID, int x, int y, int width, int height) {
		super(x, y, width, height);
		this.color = color;
		this.action = action;
		this.ID = ID;
		TILE_ID++;
	}
	
	/**
	 * Performs an action on Player p based on the tiles action
	 * @param p - a Player
	 */
	public void action(Player p) {
		if(action == ACTION_ADDSCORE1){
			p.addScore1();
		}
		else if(action == ACTION_SUBTRACT) {
			p.minusScore1();
		}
		else if(action == ACTION_ADDSCORE2) {
			if(p.getScore1() >= 10){
//				p.buyScore2();
			}
			
		}
	}
	
	/**
	 * @return the location info of this tile so that the player will know
	 * where to move to.
	 */
	public Point getLocation() {
		Point p = new Point(x * width + (width / 2), y * height + (height / 2));
		return p;
	}
	
	public boolean contains(Point p) {
		Rectangle r = new Rectangle(x * width, y * height, width, height);
		return r.contains(p);
	}
	
	/**
	 * Draws a tile to the board.
	 * @param g - graphics context to draw to
	 */
	public void draw(Graphics g) {
		final Graphics2D g2d = (Graphics2D)g.create();
		
		try {
			final int Y_OFFSET = 20;
			g2d.setColor(Color.BLACK);
			g2d.setStroke(new BasicStroke(2.0f));
			g2d.drawRect(x * width, y * height+Y_OFFSET, width, height);
			g2d.setColor(color);
			g2d.fillRect(x * width, y * height+Y_OFFSET, width, height);
		} finally {
			g2d.dispose();
		}
	}
	
	// Mutator methods
	
	public void setAction(int act) {
		action = act;
	}
	
	public void setTileID(byte id) {
		ID = id;
	}
	
	// Accessor methods
	
	public Color getColor() {
		return color;
	}
	
	public byte getTileID() {
		return ID;
	}
	
	public int getAction() {
		return action;
	}
	
	public double getWidth() {
		return width;
	}
	
	public double getHeight() {
		return height;
	}
	
	/**
	 * 
	 * @deprecated - We have sub-classed Rectangle
	 * @return
	 */
	public int getXPos() {
		return xPos;
	}
	
	/**
	 * @deprecated - We have sub-classed Rectangle
	 * @return
	 */
	public int getYPos() {
		return yPos;
	}
	
}

