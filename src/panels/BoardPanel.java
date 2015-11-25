package panels;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.JPanel;

import org.json.simple.JSONObject;

import client.ClientApp;
import client.IOHandler;
import gameobjects.NewPlayer;
import gameobjects.NewTile;
import main.Animator;
import util.GameUtils;

public class BoardPanel extends JPanel implements ComponentListener {
	public static final int HORIZONTAL_TILE_COUNT = 10;
	public static final byte VERTICAL_TILE_COUNT = HORIZONTAL_TILE_COUNT / 2;
	
	private ClientApp app;
	private Controller controller;
	
	private ArrayList<NewTile> tiles;
	private ArrayList<NewPlayer> players;
	private NewPlayer activePlayer;
	
	public BoardPanel(ClientApp app) {
		this.app = app;
		init();
		players = new ArrayList<>();
		controller = new Controller();
		
		// test players
		addComponentListener(this);
	}
	
	private void init() {
		createTiles();
	}
	
	public void addPlayer(NewPlayer p) {
		System.out.println("New Player " + p + ", added to board!");
		players.add(p);
		activePlayer = p;
		
		// test
		activePlayer.setTile(tiles.get(0));	// start at 0
		ArrayList<NewTile> movePath = createPathFromRoll(20);	// static roll
		activePlayer.initMove(movePath);
		Animator test = new Animator();
		test.animatePlayer(this, activePlayer);
		repaint();
	}
	
	/**
	 * Generates a movement path for the active player, based on the dice roll.
	 * @param roll - Tiles to add from active player current position
	 * @return - Array of tiles for movement
	 */
	public ArrayList<NewTile> createPathFromRoll(int roll) {
		int curTileID = activePlayer.getTile().getID();
		int newTileID = curTileID + roll;
		
		ArrayList<NewTile> movePath = new ArrayList<>();
		for (int i = curTileID; i < newTileID; i++) {
			if (i > tiles.size() - 1) {
				movePath.add(tiles.get(i - tiles.size()));
			} else {
				movePath.add(tiles.get(i));
			}
		}
		
		if (newTileID >= tiles.size()) {
			newTileID -= tiles.size();
		}
		return movePath;
	}
	
	/**
	 * Draws tiles and players to the screen.
	 * @param g - Graphics context to draw to
	 */
	public void paintComponent(Graphics g) {
		final Graphics2D g2d = (Graphics2D)g.create();
		try {
			g2d.setColor(GameUtils.colorFromHex("#2b2b2b"));
			g2d.fillRect(0, 0, getWidth(), getHeight());
			g2d.setColor(Color.CYAN);
			drawTiles(g2d);
			drawPlayers(g2d);
		} finally {
			g2d.dispose();
		}
	}
	
	/**
	 * Draws tiles.
	 * @param g - Graphics context to draw to
	 */
	private void drawTiles(Graphics g) {
		for (NewTile t : tiles) {
			t.draw(g);
		}
	}
	
	/**
	 * Draws players.
	 * @param g - Graphics context to draw to
	 */
	private void drawPlayers(Graphics g) {
		for (NewPlayer p : players) {
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
		
//		// size of this component is currently unknown, so we have to get content size from ClientApp!
//		int width = ClientApp.getInstance().getStatePanel().getSize().width;
//		int height = ClientApp.getInstance().getStatePanel().getSize().height;
		
		// despite being zero, the component is resized which fixes size issue.
		int width = 0;
		int height = 0;
		
		// use Math.ceil() to eliminate the small 1-2px gap that would occur around edges
		int tileWidth = (int)Math.ceil((float)width / HORIZONTAL_TILE_COUNT);
		int tileHeight = (int)Math.ceil((float)height / VERTICAL_TILE_COUNT);
		
		// Set the tiles with our x's and y's from our file
		for (int i = 0; i < coords.size(); i++) {
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
			NewTile t = new NewTile(color, 0, NewTile.TILE_COUNT, x, y, tileWidth, tileHeight);
			tiles.add(t.getID(), t);
		}
	}
	
	/**
	 * Resizes the tiles in response to the window being resized.
	 */
	private void resizeTiles() {
		int tileWidth = (int)Math.ceil((float)getWidth() / HORIZONTAL_TILE_COUNT);
		int tileHeight = (int)Math.ceil((float)getHeight() / VERTICAL_TILE_COUNT);
		
		for (NewTile t : tiles) {
			t.width = tileWidth;
			t.height = tileHeight;
		}
	}
	
	/**
	 * Updates all players positioning in response to a window resize event.
	 */
	private void resizePlayers() {
		for (int i = 0; i < players.size(); i++) {
			NewPlayer p = players.get(i);
			System.out.println("should be resizing players!");
			if (p.getTile() != null) {	// we can reassign location based on current tile
				p.setLocation(p.getTile().getCellLocation(p.getID()));
				System.out.println("Is player tile null? Shouldn't be if you're reading this!");
			} 
		}
	}

	public void componentResized(ComponentEvent e) {
		resizeTiles();
		resizePlayers();
		System.out.println(ClientApp.getInstance().getSize());
		repaint();
	}

	// unused component listener methods
	public void componentMoved(ComponentEvent e) {}
	public void componentShown(ComponentEvent e) {}
	public void componentHidden(ComponentEvent e) {}
	
	public Controller getController() {
		return controller;
	}

	
	public class Controller extends IOHandler {

		public void send(JSONObject out) {
		}

		@Override
		public void receive(JSONObject in) {
		}
		
	}
}
