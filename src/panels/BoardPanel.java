package panels;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.JPanel;

import client.ClientApp;
import gameobjects.Player;
import gameobjects.Tile;
import util.GameUtils;

public class BoardPanel extends JPanel implements ComponentListener {
	public static final int HORIZONTAL_TILE_COUNT = 10;
	public static final byte VERTICAL_TILE_COUNT = HORIZONTAL_TILE_COUNT / 2;
	
	private ArrayList<Tile> tiles;
	private ArrayList<Player> players;
	private Rectangle midRect;
	
	public BoardPanel() {
		init();
		try {
			Player.load();
		} catch (IOException e) {
			e.printStackTrace();
		}
		players = new ArrayList<>();
		addComponentListener(this);
	}
	
	private void init() {
		createTiles();
		createMidRect();
	}
	
	public void addPlayer(Player p) {
		players.add(p);
		repaint();
	}
	
	public void paintComponent(Graphics g) {
		final Graphics2D g2d = (Graphics2D)g.create();
		try {
			g2d.setColor(GameUtils.colorFromHex("#2b2b2b"));
			g2d.fillRect(0, 0, getWidth(), getHeight());
			g2d.setColor(Color.CYAN);
			g2d.drawRect(midRect.x, midRect.y, midRect.width, midRect.height);
			drawTiles(g2d);
			drawPlayers(g2d);
		} finally {
			g2d.dispose();
		}
	}
	
	private void drawTiles(Graphics g) {
		for (Tile t : tiles) {
			t.draw(g);
		}
	}
	
	private void drawPlayers(Graphics g) {
		for (Player p : players) {
			p.draw(g);
		}
	}
	
	/**
	 * Creates all the board tiles
	 */
	private void createTiles() {
		tiles = new ArrayList<>();
		
		ArrayList<String> coords = new ArrayList<>();
		File map = new File("res/tiles.map");
		try {
			Scanner sc = new Scanner(map);
			while(sc.hasNextLine()) {
				String line = sc.nextLine();
				coords.add(line);
				//System.out.println(line);
			}
			sc.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		// size of this component is currently unknown, so we have to get content size from ClientApp!
//		int width = ClientApp.getInstance().getStatePanel().getSize().width;
//		int height = ClientApp.getInstance().getStatePanel().getSize().height;
		
		int width = 800;
		int height = 400;
		
		// use Math.ceil() to eliminate the small 1-2px gap that would occur around edges
		int tileWidth = (int)Math.ceil((float)width / HORIZONTAL_TILE_COUNT);
		int tileHeight = (int)Math.ceil((float)height / VERTICAL_TILE_COUNT);
		
		// Set the tiles with our x's and y's from our file
		for (int i = 0; i < coords.size(); i++) {
			// tile ID's are being setup through a static variable in the tile class.
			// whenever a new tile is created, the overall Tile ID is incremented.
			String[] s = coords.get(i).split(",");
			int x = Integer.parseInt(s[0]);
			int y = Integer.parseInt(s[1]);
			int c = Integer.parseInt(s[2]);
			
			Color color = Color.PINK;
			if (c == 0) {
				color = GameUtils.colorFromHex("#3772BF"); // blue
			}
			else if (c == 1) {
				color = GameUtils.colorFromHex("#D91E2B");	// red
			}
			else {
				color = GameUtils.colorFromHex("#1DD147");	// green
			}
				
			Tile t = new Tile(color, 0, Tile.TILE_ID, x, y, tileWidth, tileHeight);
			tiles.add(t.getTileID(), t);
		}
	}
	
	private void createMidRect() {
		int width = ClientApp.getInstance().getStatePanel().getSize().width;
		int height = ClientApp.getInstance().getStatePanel().getSize().height;
		
		System.out.println("WIDTH: " + width + ", height: " + height);
		
		int rectWidth = 300;
		int rectHeight = 150;
		int x = (width - rectWidth) / 2;
		int y = (height - rectHeight) / 2;
		
		midRect = new Rectangle(x, y, rectWidth, rectHeight);
	}
	
	/**
	 * Resizes the tiles in response to the window being resized.
	 */
	private void resizeTiles() {
		int tileWidth = (int)Math.ceil((float)getWidth() / HORIZONTAL_TILE_COUNT);
		int tileHeight = (int)Math.ceil((float)getHeight() / VERTICAL_TILE_COUNT);
		
		for (Tile t : tiles) {
			t.width = tileWidth;
			t.height = tileHeight;
		}
	}
	
	/**
	 * Resizes the midRect in response to the window being resized.
	 */
	private void resizeMidRect() {
		midRect.x = (getWidth() - midRect.width) / 2;
		midRect.y = (getHeight() - midRect.height) / 2;
	}

	public void componentResized(ComponentEvent e) {
		resizeTiles();
		resizeMidRect();
	}

	// unused component listener methods
	public void componentMoved(ComponentEvent e) {}
	public void componentShown(ComponentEvent e) {}
	public void componentHidden(ComponentEvent e) {}

}
