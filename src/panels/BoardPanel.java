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
import java.util.concurrent.ConcurrentHashMap;

import javax.swing.JPanel;

import org.json.simple.JSONObject;

import client.ClientApp;
import client.IOHandler;
import gameobjects.NewPlayer;
import gameobjects.NewTile;
import main.Animator;
import newserver.Keys;
import util.GameUtils;
import util.NewJSONObject;

public class BoardPanel extends JPanel implements ComponentListener {
	public static final int HORIZONTAL_TILE_COUNT = 10;
	public static final byte VERTICAL_TILE_COUNT = HORIZONTAL_TILE_COUNT / 2;
	
	private ClientApp app;
	private Controller controller;
	
	private ArrayList<NewTile> tiles;
	private ConcurrentHashMap<String, NewPlayer> players;	// thread safe!
	private NewPlayer activePlayer;
	
	public BoardPanel(ClientApp app) {
		this.app = app;
		init();
		players = new ConcurrentHashMap<>();
		controller = new Controller();
		
		controller.setBP(this);
		addComponentListener(this);
	}
	
	private void init() {
		createTiles();
	}
	
	/**
	 * Adds a player to the board at the default location (0,0).1
	 * @param p - Player to add
	 */
	public void addPlayer(NewPlayer p) {
		System.out.println("board addplayer!");
		p.style(p.getStyleID());	// make sure player is styled!
		p.setTile(tiles.get(0));
		p.setLocation(tiles.get(0).getCellLocation(p.getID()));
		players.put(p.getName(), p);
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
	 * Generates a movement path for the specified player, based on the dice roll.
	 * @param player - Player to create path for
	 * @param roll - Tiles to add from active player current position
	 * @return - Array of tiles for movement
	 */
	public ArrayList<NewTile> createPathFromRoll(NewPlayer player, int roll) {
		int curTileID = player.getTileID();
		int newTileID = curTileID + roll + 1;	// add 1 to fix glitch!
		
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
		for (NewPlayer p : players.values()) {
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
		
		// even though these are zero, tiles will be properly sized when component resizes!
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
		for (NewPlayer p : players.values()) {
			if (p.getTile() != null && !p.isMoving()) {	// we can reassign location based on current tile
				p.setLocation(p.getTile().getCellLocation(p.getID()));
			}
		}
	}

	/**
	 * Resize any GUI elements.
	 */
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
	
	public void setActive(String name) {
		activePlayer = players.get(name);
		activePlayer.setActive(true);
		repaint();
	}
	
	public ConcurrentHashMap<String, NewPlayer> getPlayers() {
		return players;
	}
	
	public ArrayList<NewTile> getTiles() {
		return tiles;
	}
	
	public NewPlayer getActivePlayer() {
		return activePlayer;
	}
	
	public class Controller extends IOHandler {
		private BoardPanel bp;
		
		public void setBP(BoardPanel bp) {
			this.bp = bp;
		}

		public void send(JSONObject out) {
			app.getClient().getIOHandler().send(out);
		}

		public void receive(JSONObject in) {
			String cmdKey = (String)in.get("cmd");
			NewPlayer p = NewPlayer.fromJSON(in);
			p = players.get(p.getName());	// reference this player from board
			
			switch (cmdKey) {
			case Keys.Commands.MOVE:
				System.out.println("SHOULD BE MOVING PLAYER: " + p);
				Animator test = new Animator();
				setActive(p.getName());
				activePlayer.initMove(createPathFromRoll(p, (int)in.get(Keys.ROLL_AMT)));
				test.animatePlayer(BoardPanel.this, activePlayer);
				players.put(activePlayer.getName(), activePlayer);
				repaint();
				break;
			case Keys.Commands.UPDATE:
				// update stuff
				break;
			case Keys.Commands.ACTIVE:
				// active stuff
				break;
			}
		}
		
	}
}
