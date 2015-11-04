package screen;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.ArrayList;

import javax.swing.JPanel;

import main.Dice;
import main.Director;
import main.Player;
import main.Tile;

/**
 * Contains all of the tiles that will be present in the board. These tiles are
 * RED, GREEN, and BLUE. Tiles can have random events associated with them, as 
 * well as their respective mini game.
 * @author David Kramer
 *
 */
public class Board extends JPanel {
	public static final byte TILE_COUNT = 10;
	private static final long serialVersionUID = 1L;	
	private Director director;
	private Rectangle midRect;
	private Dice dice;
	private ArrayList<Tile> tiles;	// tiles of the board

	public Board(Director director) {
		this.director = director;
		tiles = new ArrayList<>();
		midRect = createMidRect();
		dice = new Dice(500, 500);
		createTiles();
	}
	
	private void createTiles() {
		
		// tile sizing
		int tileWidth = 1280 / TILE_COUNT;
		int tileHeight = 150;
		
		// horizontal tiles
		for (int x = 0; x < TILE_COUNT; x++) {
			Color color = GameUtils.getRandomColor();
			int ID = x;
			Tile t = new Tile(color, 0, ID, (x * tileWidth), 0, tileWidth, tileHeight);
			tiles.add(t);
		}
		
		// vertical tiles
		for (int y = 0; y < (TILE_COUNT / 2); y++) {
			Color color = GameUtils.getRandomColor();
			int ID = y;
			Tile t = new Tile(color, 0, ID, 0, (y * tileHeight), tileWidth, tileHeight);
			tiles.add(t);
		}
		repaint();
	}
	
	/**
	 * Creates a 500 x 300 rectangle in the center of the screen that will contain
	 * all the players, turn count indicator, and dice.
	 * @return
	 */
	private Rectangle createMidRect() {
		int width = 500;
		int height = 350;
		int x = (1280 - width) / 2;	// center (x,y) on screen
		int y = (720 - height) / 2;
		
		return new Rectangle(x, y, width, height);
	}
	
	private void drawPlayers(Graphics g) {
		int gap = 100;	// 80 px gap between each player
		ArrayList<Player> players = director.getPlayers();
		
		//TODO fix player offsets
		for (int i = 0; i < players.size(); i++) {
			Player p = players.get(i);
			p.setXPos(50 + midRect.x + gap * i);
			p.setYPos(50 + midRect.y + 200);
			p.draw(g);
		}
	}
	
	private void drawTurnIndicator(Graphics g) {
		
	}
	
	public void paintComponent(Graphics g) {
		g.clearRect(0, 0, getWidth(), getHeight());
		for (int i = 0; i < tiles.size(); i++) {	// draws all tiles
			Tile t = tiles.get(i);
			t.draw(g);
		}
		
		// draw middle box container for players, dice, and turn indicator
		g.setColor(Color.RED);
		g.drawRect(midRect.x, midRect.y, midRect.width, midRect.height);
		
		// draw players evenly spaced
		drawPlayers(g);
		
		// draw turn count from director
		drawTurnIndicator(g);
		
		//TODO draw dice stuff here
		dice.roll(4);
		dice.draw(g);
	}
}
