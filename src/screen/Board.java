package screen;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.JPanel;

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
	private ArrayList<Tile> tiles;	// tiles of the board

	public Board() {
		tiles = new ArrayList<>();
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
	
	public void paintComponent(Graphics g) {
		g.clearRect(0, 0, getWidth(), getHeight());
		for (int i = 0; i < tiles.size(); i++) {
			Tile t = tiles.get(i);
			t.draw(g);
		}
	}
}
