package gameobjects;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import org.json.simple.JSONObject;

import newserver.Keys;
import newserver.PlayerStyles;
import util.Vector;

public class NewPlayer extends GameObject {
	private static final long serialVersionUID = 1L;
	// players should be uniform size
	public static final int WIDTH = 40;
	public static final int HEIGHT = 40;
	
	private String name;
	private BufferedImage img;	// img representation of player
	private Color color;	// color representation of player
	private NewTile tile;	// what tile is player on?
	private ArrayList<NewTile> movePath;	// path of tiles player will move on
	private Point newLocation;	// new location that player will move to
	
	// state flags
	private boolean isActive = false;	// is this player active?
	private boolean isMoving = false;	// is player currently moving?	
	private boolean hasRolled = false;	// has player already rolled the current round?
	
	private int ID = -1;
	private int styleID = 0;	// style color num from PlayerStyles
	private int tileID = 0;
	private int score = 0;
	private int lastRoll = 0;
	
	public NewPlayer(String name, int ID) {
		this.name = name;
		this.ID = ID;
		init();
	}
	
	/**
	 * Utility method that's used to update a player from a JSON object. Checks to make sure
	 * that the JSON object contains the key, so that we don't overwrite a value.
	 * @param player - Player to update
	 * @param obj - JSONObject that contains values for player
	 */
	public static void updateFromJSON(NewPlayer player, JSONObject obj) {
		System.out.println("Update from json: " + obj);
		if (obj.containsKey(Keys.PLAYER)) {	// ensure we extract the player object from cmd
			obj = (JSONObject) obj.get(Keys.PLAYER);
		}
		
		player.name 	= obj.containsKey(Keys.NAME) 		? (String) obj.get(Keys.NAME) 	: player.name;
		player.ID 		= obj.containsKey(Keys.ID) 			? (int) obj.get(Keys.ID) 		: player.ID;
		player.score	= obj.containsKey(Keys.SCORE) 		? (int) obj.get(Keys.SCORE) 	: player.score;
		player.tileID	= obj.containsKey(Keys.TILE_ID) 	? (int) obj.get(Keys.TILE_ID) 	: player.tileID;
		player.styleID	= obj.containsKey(Keys.STYLE_ID) 	? (int) obj.get(Keys.STYLE_ID) 	: player.styleID;
		player.lastRoll = obj.containsKey(Keys.LAST_ROLL) 	? (int) obj.get(Keys.LAST_ROLL) : player.lastRoll;
	}
	
	public static NewPlayer fromJSON(JSONObject obj) {
		NewPlayer p = new NewPlayer("", 0);	// empty player. Use update from JSON to get values from keys!
		updateFromJSON(p, obj);
		return p;
	}
	
	/**
	 * Initializes everything else that is needed.
	 */
	private void init() {
		x = 0;
		y = 0;
		width = WIDTH;
		height = HEIGHT;
		movePath = new ArrayList<>();
	}
	
	/**
	 * Gets style from static PlayerStyles class based on specified colorNum
	 * @param colorNum - Array index to get styles from in PlayerStyles.
	 */
	@SuppressWarnings("static-access")	// it lies!
	public void style(int colorNum) {
		img = PlayerStyles.getInstance().imgs[colorNum];
		System.out.println("pImg: [" + colorNum + "] " + img);
		color = PlayerStyles.getInstance().colors[colorNum];
		styleID = colorNum;
	}
	
	/**
	 * Draws the player to the screen at their current location.
	 * @param g - Graphics context to draw to.
	 */
	public void draw(Graphics g) {
		final Graphics2D g2d = (Graphics2D)g.create();
		
		try {
			// draw oval highlight if player is active
			if (isActive) {
				g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); // smooth on
				g2d.setColor(Color.BLACK);
				g2d.setStroke(new BasicStroke(4.0f));
				g2d.drawOval(x - 5, y - 5, 50, 50);
				g2d.setColor(new Color(255, 255, 255, 180));	// trans white
				g2d.fillOval(x - 5, y - 5, 50, 50);
			}
			// draw player alias
			g2d.setStroke(new BasicStroke());	// default
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,  RenderingHints.VALUE_ANTIALIAS_OFF); // smooth off
			g2d.drawImage(img, x, y, null);
			g2d.setColor(Color.WHITE);
			g2d.setFont(new Font("Tahoma", Font.BOLD, 10));
			int w = g2d.getFontMetrics().stringWidth(name);	// width of name string
			// draw player name
			if (tile != null && tile.y == 4) {	
				g2d.drawString(name, x + (w / 2), y - 20);	// on top tile
			} else {
				g2d.drawString(name, x + (w / 2), y + 50);	// on bottom tile	
			}
			
		} finally {
			g2d.dispose();
		}
	}
	
	/**
	 * Adds a single tile to the movement path and calls move().
	 * @param t - Tile to move to.
	 */
	public void move(NewTile t) {
		movePath.add(t);
		move();
	}
	
	/**
	 * Sets up the player to move to a new tile location, based on the
	 * specified move path
	 * @param movePath - The movement tile path the player will traverse
	 */
	public void initMove(ArrayList<NewTile> movePath) {
		NewTile endTile = movePath.get(movePath.size() - 1);	// last tile is end tile
		this.movePath = movePath;
		this.tile = endTile;
		move(endTile);
	}
	
	/**
	 * Moves the player, if tiles still exist on the movement path. *NOTE animation isn't
	 * handled here, but is called by animatePlayer() in the Animator class.
	 */
	public void move() {
		if (!isMoving && movePath.size() > 0) {
			isMoving = true;
			newLocation = movePath.get(0).getCellLocation(ID);
		} else {
			if (movePath.size() != 0) {
				double finalX = newLocation.x - (double)x;
				double finalY = newLocation.y - (double)y;
				Vector vec = new Vector(finalX, finalY);
				vec = vec.normalize();
				
				x += vec.getdX() * 3;
				y += vec.getdY() * 3;
				
				if ((x < newLocation.x + 2 && x > newLocation.x - 2) &&
					(y < newLocation.y + 2 && y > newLocation.y - 2)) {

					movePath.remove(0);
					if (movePath.size() != 0) {
						newLocation = movePath.get(0).getCellLocation(ID);
					}
				}
			} else {
				isMoving = false;
				// clear out
				newLocation = null;
				movePath = null;
			}
		}
	}
	
	/**
	 * Sets new location for this player.
	 * @param p
	 */
	public void setNewLocation(Point p) {
		newLocation = p;
	}
	
	/**
	 * Sets the movement path for the player.
	 * @param tilePath
	 */
	public void setMovePath(ArrayList<NewTile> tilePath) {
		this.movePath = tilePath;
	}
	
	public void setTile(NewTile tile) {
		this.tile = tile;
	}
	
	public void setID(int ID) {
		this.ID = ID;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void setActive(boolean b) {
		isActive = b;
	}
	
	public void setLastRoll(int lr) {
		lastRoll = lr;
	}
	
	public void setHasRolled(boolean b) {
		hasRolled = b;
	}
	
	public int getID() {
		return ID;
	}
	
	public int getTileID() {
		return tile.getID();
	}
	
	public boolean hasRolled() {
		return hasRolled;
	}
	
	public boolean isMoving() {
		return isMoving;
	}
	
	public NewTile getTile() {
		return tile;
	}
	
	public Point getNewLocation() {
		return newLocation;
	}
	
	public int getStyleID() {
		return styleID;
	}
	
	public String getName() {
		return name;
	}
	
	public int getLastRoll() {
		return lastRoll;
	}
	
	public String toString() {
		return ID + " " + name;
	}
	
	public JSONObject toJSONObject() {
		JSONObject player = new JSONObject();
		player.put(Keys.ID, ID);
		player.put(Keys.NAME, name);
		player.put(Keys.SCORE, score);
		player.put(Keys.TILE_ID, tileID);
		player.put(Keys.STYLE_ID, styleID);
		player.put(Keys.LAST_ROLL, lastRoll);
		return player;
	}

}
