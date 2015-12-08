package panels;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.json.simple.JSONObject;

import client.ClientApp;
import client.IOHandler;
import gameobjects.NewPlayer;
import gameobjects.NewTile;
import main.Animator;
import util.GameUtils;
import util.Keys;
import util.NewJSONObject;

public class BoardPanel extends JPanel {
	public static final int HORIZONTAL_TILE_COUNT = 10;
	public static final byte VERTICAL_TILE_COUNT = HORIZONTAL_TILE_COUNT / 2;
	
	private ClientApp app;
	private Controller controller;
	
	private ArrayList<NewTile> tiles;
	private ConcurrentHashMap<String, NewPlayer> players;	// thread safe!
	private NewPlayer clientPlayer;	// the player that belongs to this client!
	private NewPlayer activePlayer;	// the player that is allowed to move / isMoving
	private int maxTurns;
	private int curTurn;
	
	/**
	 * Constructs a new BoardPanel with a connection to the main client app
	 * @param app - Target client app
	 */
	public BoardPanel(ClientApp app) {
		this.app = app;
		init();
		players = new ConcurrentHashMap<>();
		controller = new Controller();
		setBackground(Color.BLACK);
	}
	
	/**
	 * Initializes all necessary items for this boardpanel.
	 */
	private void init() {
		createTiles();
		maxTurns = 10;
		curTurn = 1;
		
		// handles resizing window event
		addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent e) {
				resizeTiles();
				resizePlayers();
				repaint();
			}
		});
	}
	
	/**
	 * Adds a player to the board at the default location (0,0).1
	 * @param p - Player to add
	 */
	public void addPlayer(NewPlayer p) {
		p.style(p.getStyleID());	// make sure player is styled!
		p.setTile(tiles.get(tiles.size()-1));	// default starting location!
		p.setLocation(tiles.get(tiles.size()-1).getCellLocation(p.getID()));
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
		int newTileID = curTileID + roll;	// adding 1 fixes glitch, and is intentional!
		
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
		
		if (newTileID != 0) {
			switch (tiles.get(newTileID-1).getType()) {
				case 0:
					players.get(player.getName()).setScore(players.get(player.getName()).getScore() + 1);
					break;
				case 1:
					players.get(player.getName()).setScore(players.get(player.getName()).getScore() - 1);
					break;
				case 2:
					players.get(player.getName()).setScore(players.get(player.getName()).getScore() + 5);
					break;
			}
		}
		else {
			players.get(player.getName()).setScore(players.get(player.getName()).getScore() + 1);
		}
		
		// If passing start, give plus 3 score
		if (movePath.contains(tiles.get(0)) && movePath.indexOf(tiles.get(0)) != 0) {
			players.get(player.getName()).setScore(players.get(player.getName()).getScore() + 3);
		}
		
		app.getLeaderPanel().updateList();
		
		player.setTileID(newTileID);
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
			if (!p.getName().equals(activePlayer.getName())) {
				p.draw(g);
			}
		}
		activePlayer.draw(g);	// draw active player last, so that it's on top!
	}
	
	/**
	 * Creates all the board tiles as specified in the tiles.map file.
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
			NewTile t = new NewTile(color, 0, NewTile.TILE_COUNT, x, y, c, tileWidth, tileHeight);
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
			if (!p.isMoving()) {
				if (p.getTileID() > 0) {
					p.setLocation(tiles.get(p.getTileID() - 1).getCellLocation(p.getID()));	
				} else {
					p.setLocation(tiles.get(p.getTileID()).getCellLocation(p.getID()));
				}
			}
		}
	}
	
	// mutator methods
	
	public void setActive(String name) {
		activePlayer = players.get(name);
		activePlayer.setActive(true);	// draws the active indicator!
		repaint();
	}
	
	public void setClientPlayer(NewPlayer player) {
		this.clientPlayer = player;
	}
	
	// accessor methods
	
	public ConcurrentHashMap<String, NewPlayer> getPlayers() {
		return players;
	}
	
	public ArrayList<NewTile> getTiles() {
		return tiles;
	}
	
	public NewPlayer getActivePlayer() {
		return activePlayer;
	}
	
	public NewPlayer getClientPlayer() {
		return clientPlayer;
	}
	
	public Controller getController() {
		return controller;
	}
	
	/**
	 * Controller for handling player movements and updates on this board panel.
	 * @author David Kramer
	 *
	 */
	public class Controller extends IOHandler {

		public void send(JSONObject out) {
			app.getClient().getIOHandler().send(out);
		}

		public void receive(JSONObject in) {
			String cmdKey = (String)in.get(Keys.CMD);
			NewPlayer p = NewPlayer.fromJSON(in);
			p.style(p.getStyleID());	// make sure player is always styled!
			//System.out.println("board panel received: " + p.toJSONObject().toJSONString());
			
			switch (cmdKey) {
			case Keys.Commands.MOVE:
				//System.out.println("Should be moving player!");
				movePlayer(in, p);
				break;
			case Keys.Commands.UPDATE:
				updatePlayer(p);
				break;
			case Keys.Commands.ACTIVE:
				// active stuff
				break;
			}
		}
		
		/**
		 * Moves the player, using an Animator object, based on a specified 
		 * roll amount for specified player
		 * @param in - JSONObject containing roll amt
		 * @param p - Target player we need to animate
		 */
		public void movePlayer(JSONObject in, NewPlayer p) {
			Animator animator = new Animator();
			players.put(p.getName(), p);
			setActive(p.getName());
			//System.out.println("ACTIVE PLAYER TILE NUM: " + p.getTileID());
			int temp = (int) in.get(Keys.ROLL_AMT);
			if (p.getTileID() > 0) {
				p.setLocation(tiles.get(p.getTileID() - 1).getCellLocation(p.getID()));	
			} else {
				p.setLocation(tiles.get(p.getTileID()).getCellLocation(p.getID()));
				temp++;
			}
			activePlayer.initMove(createPathFromRoll(p, temp));
			animator.animatePlayer(BoardPanel.this, p);
		}
		
		/**
		 * Updates specified player by setting their tile location
		 * on their tile ID.
		 * @param p - Player to update
		 */
		public void updatePlayer(NewPlayer p) {
			//System.out.println("updating player: " + p.toJSONObject().toJSONString() + ", on board panel!");
			if (p.getTileID() > 0) {
				p.setLocation(tiles.get(p.getTileID() - 1).getCellLocation(p.getID()));
			}
			else {
				p.setLocation(tiles.get(p.getTileID()).getCellLocation(p.getID()));
			}
			players.put(p.getName(), p);
			repaint();
		}
	}
}
