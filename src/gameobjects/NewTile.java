package gameobjects;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;

import org.json.simple.JSONObject;

public class NewTile extends GameObject {
	// Actions
	public static final int ACTION_ADDSCORE1 = 0;
	public static final int ACTION_SUBTRACT = 1;
	public static final int ACTION_ADDSCORE2 = 2;
	public static int TILE_COUNT;	// counts up every time tile is created (assigned to tile as this.ID)
	
	private Color color;
	private int action;
	private int ID;
	private Rectangle[] playerCells;	// quadrant rectangles for players
	
	public NewTile(Color color, int action, int ID, int x, int y, int width, int height) {
		this.color = color;
		this.action = action;
		this.ID = ID;
		// rect setup
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		TILE_COUNT++;
		createPlayerCells();
	}
	
	/**
	 * Creates the player cells in each quadrant of a tile, which is the
	 * location that players will move to, based on their ID, when they land on
	 * a tile.
	 */
	public void createPlayerCells() {
		playerCells = new Rectangle[4];
		
		int w = 40;	// width of player rect
		int h = 40;	// height of player rect
		
		// upper left
		Rectangle r0 = new Rectangle(x * width, y * height, w, h);
		// upper right
		Rectangle r1 = new Rectangle(x * width + (width - w), y * height, w, h);
		// lower left
		Rectangle r2 = new Rectangle(x * width, y * height + (height - h), w, h);
		// lower right
		Rectangle r3 = new Rectangle(x * width + (width - w), y * height + (height - h), w, h);
		
		playerCells[0] = r0;
		playerCells[1] = r1;
		playerCells[2] = r2;
		playerCells[3] = r3;
	}
	
	/**
	 * Draws tile to the screen.
	 * @param g - Graphics context to draw to
	 */
	public void draw(Graphics g) {
		final Graphics2D g2d = (Graphics2D)g.create();
		
		try {
			g2d.setColor(Color.BLACK);
			g2d.setStroke(new BasicStroke(2.0f));
			g2d.drawRect(x * width, y * height, width, height);
			g2d.setColor(color);
			g2d.fillRect(x * width, y * height, width, height);
			drawPlayerCells(g2d);
		} finally {
			g2d.dispose();
		}
	}
	
	/**
	 * Draws player quadrant cells.
	 * @param g2d - Graphics context to draw to
	 */
	private void drawPlayerCells(Graphics2D g2d) {	
		createPlayerCells();
//		char[] letters = {'a', 'b', 'c', 'd'};	// cell letters, (DEBUG STUFF)

		for (int i = 0; i < playerCells.length; i++) {
			Rectangle r = playerCells[i];
			g2d.setColor(new Color(0, 0, 0, 50));
			g2d.fillRect(r.x, r.y, r.width, r.height);
			
			// DEBUG STUFF - Probably don't need, but keep for now.
			// draw location ID
//			String locID = ID + "" + letters[i];
//			int w = g2d.getFontMetrics().stringWidth(locID);
//			Point midPt = new Point(r.x + ((r.width + w) / 2) - w, r.y + (r.height / 2));	// pt to draw quad cell letter
//			g2d.setColor(Color.WHITE);
//			g2d.drawString(locID , midPt.x, midPt.y);
		}
	}
	
	// accessor methods
	
	public Color getColor() {
		return color;
	}

	public int getAction() {
		return action;
	}

	public int getID() {
		return ID;
	}

	public Point getCellLocation(int ID) {
		Rectangle r = playerCells[ID];
		return new Point(r.x, r.y);
	}

	public JSONObject toJSONObject() {
		return null;
	}

}
