package states;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Rectangle;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

import main.Dice;
import main.Main;
import main.NewDirector;
import main.Player;
import main.Tile;
import screen.GameUtils;
import screen.StatusPanel;

/**
 * The main board state of the game. A board is a collection of tiles that the player
 * can land on to initiate random mini games. During game play, the active player
 * can roll the dice provided from the board, to determine their next space that
 * they will land on. Whenever this move is made, the event associated with the
 * tile will then have to be completed by one or more players.
 * @author David Kramer
 *
 */
public class BoardState extends State implements ComponentListener, MouseListener, MouseMotionListener {
	public static final byte HORIZONTAL_TILE_COUNT = 10;
	public static final byte VERTICAL_TILE_COUNT = HORIZONTAL_TILE_COUNT / 2;
	private static final long serialVersionUID = 1L;	
	
	private StatusPanel statusPanel;	// display active player and turns remaining
	private JPanel boardPanel;	// this will contain all the board tile drawing
	private Rectangle midRect;	// rect in the middle that contains game objects such as dice
	private PlayerManager playerMngr;
	private BufferedImage img;	// img we will draw stuff to
	private Player activePlayer;
	private Dice dice;			// TODO maybe split up some more stuff to make this class smaller and easier to read

	/**
	 * Tiles will now be mapped with their tile ID. This will make it more
	 * efficient when we move from tile to tile based on the roll. We can know
	 * exactly where to go, based on the tile ID. 
	 */
	private ArrayList<Tile> tiles;	// tiles of the board
	//private Map<Byte, Tile> tileMap;	// more efficient way to keep track of tiles on board, instead of tiles array
	private boolean havePlayersRolled = false;	// have players first rolled 
	/**
	 * Constructs a new board state.
	 * @param director - main director control of application
	 */
	public BoardState(NewDirector director) {
		super(director);
		img = new BufferedImage(1280, 720, BufferedImage.TYPE_INT_RGB);
		//init();
	}

	public void update() {
		//TODO update director after player has moved
		if (director.getMain().getKeyboard().spacePressed) {
			System.out.println("Space is pressed");
		}
		for (Player p : director.getPlayers()) {
			p.update();
		}
	}
	
	/**
	 * Calls each draw method for redrawing dice, players, and tiles to the
	 * screen.
	 * @param g - Graphics to draw to
	 */
	public void paintComponent(Graphics g) {
		final Graphics2D g2d = (Graphics2D)g.create();
		try {
//			g2d.setColor(GameUtils.colorFromHex("#2b2b2b"));
//			g2d.translate(0, StatusPanel.Y_OFFSET);
//			g2d.fillRect(0, 0, getWidth(), getHeight());
//			statusPanel.repaint();
//			boardPanel.repaint();
//			g2d.setColor(Color.CYAN);
//			g2d.drawRect(midRect.x, midRect.y, midRect.width, midRect.height);
//			drawTiles(g2d);
//			dice.draw(g2d);
//			drawPlayers(g2d);
			statusPanel.repaint();
			boardPanel.repaint();
		} finally {
			g2d.dispose();	
		}
	
	}

	/**
	 * Redraws everything to the screen, as defined in paintComponent().
	 */
	public void render() {
		repaint();
	}
	
	public void firstRollPlayers() {
		for (Player p : director.getPlayers()) {
			if (!p.hasFirstRolled()) {
				JOptionPane.showMessageDialog(null, "Player: " + p + " hasn't rolled yet!");
				p.setTile(tiles.get(0));
				p.setLastRoll(dice.roll(Dice.SIZE));
				p.setHasFirstRolled(true);
				if (director.getPlayers().indexOf(p) == director.getPlayers().size()-1) {
					for (Player p2 : director.getPlayers()) {
						p2.move(tiles.get(0));
					}
				}
				return;
			}
		}
		havePlayersRolled = true;
	}
	
	/**
	 * Mouse click event. Checks to see if mouse click pt location is contained
	 * inside of dice and players. If it is, their state is then updated to 
	 * reflect this event.
	 */
	public void mouseClicked(MouseEvent e) {
		System.out.println("Mouse clicked! @ " + e.getPoint());
		System.out.println("Dice is at : " + dice);
		if (dice.contains(e.getPoint())) {
			System.out.println("Dice clicked!");
			if (!playerMngr.havePlayersRolled()) {
//				firstRollPlayers();
				playerMngr.firstRoll();
			} else {
				boolean t = false;
				for (int i = 0; i < director.getPlayers().size(); i++) {
					if (director.getPlayers().get(i).isMoving()) {
						t = true;
					}
				}
				if (!t) {
//					movePlayer();
					playerMngr.movePlayer();
				}
			}
		}
			
		for (Player p : director.getPlayers()) {
			if (p.contains(e.getPoint())) {
				if (p.isSelected()) {	//toggle between isSelected
					p.setSelected(false);
				} else {
					p.setSelected(true);
					activePlayer = p;
				}
			}
		}
	}
	
	// unused mouse listener methods
	public void mousePressed(MouseEvent e) {}
	public void mouseReleased(MouseEvent e) {}
	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}
	
	/**
	 * Updates the appearance of all objects in the game such as their positioning and
	 * widths, if the application has been resized. By designating this into its own
	 * component listener, we can be more efficient when updating, rather than constantly
	 * checking in the main update method.
	 */
	public void componentResized(ComponentEvent e) {
		System.out.println("Board resized! Updating stuff!");
		midRect.x = (getWidth() - midRect.width) / 2;
		midRect.y = (getHeight() - midRect.height) / 2;
//		img = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
		resizeTiles();	// update tiles with new size info
	}

	// unused component listener methods
	public void componentMoved(ComponentEvent e) {}
	public void componentShown(ComponentEvent e) {}
	public void componentHidden(ComponentEvent e) {}

	/**
	 * Mouse drag event. Checks to see if mouse drag pt location intersects within the
	 * midrect bounds. If true, the dice can be moved around, so long as the location
	 * doesn't completely exit the midrect bounds.
	 */
	public void mouseDragged(MouseEvent e) {
		// Make the collision box smaller so the dice doesn't show out of bounds
		Rectangle newMid = (Rectangle) midRect.clone();
		newMid.x += dice.width/2;
		newMid.y += dice.height/2;
		newMid.width -= dice.width;
		newMid.height -= dice.height;
		if (newMid.contains(e.getPoint())) {
			if (midRect.intersects(dice)) {
				dice.x = e.getPoint().x - dice.width/2;
				dice.y = e.getPoint().y - dice.height/2;
			}
		}
	}

	// unused mouse motion listener methods
	public void mouseMoved(MouseEvent e) {}
	
	/**
	 * Initializes everything and sets up the layout of the board. 
	 */
	public void init() {
		statusPanel = new StatusPanel(director);
		boardPanel = new BoardPanel();
		img = new BufferedImage(1280, 720, BufferedImage.TYPE_INT_RGB);
		boardPanel.setBackground(Color.ORANGE);
		playerMngr = new PlayerManager();
		midRect = createMidRect();
		dice = new Dice(600, 400);
		//key = new Keyboard();
		
		// setup layout
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.NORTH;
		c.gridx = 0;
		c.gridy = 0;
		c.ipady = 10;
		add(statusPanel, c);
		
		c.gridx = 0;
		c.gridy = 1;
		c.fill = GridBagConstraints.BOTH;
		c.anchor = GridBagConstraints.SOUTH;
		c.weightx = 1.0;
		c.weighty = 1.0;
		add(boardPanel, c);
		
		loadPlayers();	// setup players for first
		createTiles();
		
		// add event listeners
		//addKeyListener(key);
		addComponentListener(this);
		addMouseListener(this);
		addMouseMotionListener(this);
		Main.getInstance().setSize(new Dimension(1281, 722));	//TODO this is a workaround to loading status panel!
	}
	
	/**
	 * Load all new players into the board state from the director.
	 */
	private void loadPlayers() {
		createMidRect();
		ArrayList<Player> players = director.getPlayers();
		
		for (int i = 0; i < players.size(); i++) {
			Player p = players.get(i);
			
			p.x = midRect.x + (p.width * i) + 50;
			p.y = midRect.y;
		}
	}
	
	private void movePlayer() {
		byte roll = (byte)dice.roll(Dice.SIZE);
		System.out.println("Rolled: " + roll);
		
		if (activePlayer != null) {
			byte curTileID = activePlayer.getTileID();
			byte newTileID = (byte)(curTileID + roll);
			
			ArrayList<Tile> temp = new ArrayList<Tile>();
			for (int i = curTileID; i < newTileID; i++) {
				if (i > tiles.size()-1) {
					temp.add(tiles.get(i - tiles.size()));
				}
				else {
					temp.add(tiles.get(i));
				}
			}
			
			if (newTileID >= tiles.size()) {
				newTileID -= tiles.size();
			}
			
			Tile newTile = tiles.get(newTileID-1);
			
			
			activePlayer.setPath(temp);
			activePlayer.move();
			activePlayer.setTile(newTile);
		}
	}
	
	/**
	 * Creates all the game tiles of the game. Currently it is 10 x 5 x 10 x 5.
	 */
	private void createTiles() {
		// Get the values of the file "tiles.map". They are our x's and y's for our tiles
		ArrayList<String> coords = new ArrayList<String>();
		tiles = new ArrayList<Tile>();
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
		
		// default size 1280 x 720 for initial tile sizing
		int tileWidth = boardPanel.getWidth() / HORIZONTAL_TILE_COUNT;
		int tileHeight = boardPanel.getHeight() / VERTICAL_TILE_COUNT;
		
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
				color = Color.BLUE;
			}
			else if (c == 1) {
				color = Color.RED;
			}
			else {
				color = Color.GREEN;
			}
				
			Tile t = new Tile(color, 0, Tile.TILE_ID, x, y, tileWidth, tileHeight);
			tiles.add(t.getTileID(), t);
		}
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
	
	/**
	 * Redraws each player to the screen.
	 * @param g - Graphics to draw to
	 */
	private void drawPlayers(Graphics g) {
		for (int i = 0; i < director.getPlayers().size(); i++) {
			Player p = director.getPlayers().get(i);
			p.draw(g);
		}
	}
	
	/**
	 * Redraws each tile to the screen.
	 * @param g - Graphics to draw to
	 */
	private void drawTiles(Graphics g) {		
		for (Tile t : tiles) {
			t.draw(g);
		}
	}
	
	/**
	 * Updates the sizing of tiles. This should only be called from componentResized().
	 * This just ensures that all the tiles will fill the board, depending on the 
	 * current size of the window.
	 */
	private void resizeTiles() {
		int tileWidth = (boardPanel.getWidth() / HORIZONTAL_TILE_COUNT);
		int tileHeight = (boardPanel.getHeight() / VERTICAL_TILE_COUNT);
		
		for (Tile t : tiles) {
			t.width = tileWidth;
			t.height = tileHeight;
			t.createPlayerRects();
		}
		
		// update player within tile bounds
		if (activePlayer != null) {
			activePlayer.setLocation(activePlayer.getTile().getLocation());	
		}
	}
	
	private class BoardPanel extends JPanel {
		
		public void paintComponent(Graphics g) {
			final Graphics2D g2d = (Graphics2D)g.create();
			try {
				g2d.setColor(GameUtils.colorFromHex("#2b2b2b"));
				g2d.fillRect(0, 0, getWidth(), getHeight());
				g2d.setColor(Color.CYAN);
				g2d.drawRect(midRect.x, midRect.y, midRect.width, midRect.height);
				drawTiles(g2d);
				dice.draw(g2d);
				drawPlayers(g2d);	
			} finally {
				g2d.dispose();
			}
		}
	}
	
	/**
	 * This inner class will be responsible for managing players and determining which 
	 * player can move. This class will also keep track of all turns that have occurred.
	 * For instance, if player "foo" rolled and move. Foo can't roll next round, until
	 * all other players have rolled.
	 * @author David Kramer
	 *
	 */
	private class PlayerManager {
		private ArrayList<Player> players;
		private Player activePlayer;	// the current player
		private Player nextPlayer;	// the next player that will go
		private boolean haveAllPlayersRolled = false;	// have all players first rolled initially?
		
		public PlayerManager() {
			players = director.getPlayers();
		}
		
//		/**
//		 * Determines who will be the first to play the game, by whoever rolls
//		 * the highest.
//		 */
//		public void firstRoll() {
//			for (Player p : players) {
//				if (!p.hasFirstRolled()) {
//					activePlayer = p;
//					activePlayer.setLastRoll(dice.roll(Dice.SIZE));
//					JOptionPane.showMessageDialog(null, "Player: " + activePlayer + " hasn't rolled yet!");
//					activePlayer.moveTo(tiles.get(0).getLocation(activePlayer.getPlayerID()));
//					activePlayer.setTile(tiles.get(0));
//					activePlayer.setHasFirstRolled(true);
//				}
//			}
//			// TODO check for duplicate rolls
//			havePlayersRolled = true;
//			System.out.println("have players rolled? " + havePlayersRolled);
//		}
		
		public void firstRoll() {
			for (Player p : director.getPlayers()) {
				if (!p.hasFirstRolled()) {
					JOptionPane.showMessageDialog(null, "Player: " + p + " hasn't rolled yet!");
					p.setTile(tiles.get(0));
					p.setLastRoll(dice.roll(Dice.SIZE));
					p.setHasFirstRolled(true);
					activePlayer = p;
					if (director.getPlayers().indexOf(p) == director.getPlayers().size()-1) {
						for (Player p2 : director.getPlayers()) {
							p2.move(tiles.get(0));
						}
					}
					return;
				}
			}
			havePlayersRolled = true;
		}
		
		public void movePlayer() {
			byte roll = (byte)dice.roll(Dice.SIZE);
			System.out.println("Rolled: " + roll);
			
			int ID = activePlayer.getPlayerID();
			if (!(++ID >= players.size())) {	// alternate player
				activePlayer = players.get(ID);
			} else {
				activePlayer = players.get(0);
			}
			
			statusPanel.updateCurPlayerLabel(activePlayer);
//			activePlayer.setLastRoll(dice.roll(Dice.SIZE));
			
			byte curTileID = activePlayer.getTileID();
			byte newTileID = (byte)(curTileID + roll);
			
			ArrayList<Tile> temp = new ArrayList<Tile>();
			for (int i = curTileID; i < newTileID; i++) {
				if (i > tiles.size()-1) {
					temp.add(tiles.get(i - tiles.size()));
				}
				else {
					temp.add(tiles.get(i));
				}
			}
			
			if (newTileID >= tiles.size()) {
				newTileID -= tiles.size();
			}
			
			Tile newTile = tiles.get(newTileID);
			
			//activePlayer.moveTo(newTile.getLocation());
			activePlayer.setPath(temp);
			activePlayer.move();
			activePlayer.setTile(newTile);
		}
		
//		/**
//		 * Moves the active player, based on the dice roll. Once a player has 
//		 * arrived onto a tile, that event will fire, and a random mini game
//		 * state will be created thereafter.
//		 */
//		public void movePlayer() {
//			updateTurns();
//			if (activePlayer != null) {
//				System.out.println("Active PLayer was: " + activePlayer);
//				int ID = activePlayer.getPlayerID();
//				if (!(++ID >= players.size())) {
//					activePlayer = players.get(ID);
//				} else {
//					activePlayer = players.get(0);
//				}
//				statusPanel.updateCurPlayerLabel(activePlayer);
//				activePlayer.setLastRoll(dice.roll(Dice.SIZE));
//				System.out.println("Active PLayer is now: " + activePlayer);
//				byte roll = (byte)dice.roll(Dice.SIZE);
//				System.out.println("Rolled: " + roll);
//		
//				byte curTileID = activePlayer.getTileID();
//				byte newTileID = (byte)(curTileID + roll);
//				
//				if (newTileID > tiles.size()) {
//					newTileID = 1;
//				}
//				
//				Tile newTile = tiles.get(newTileID);
//				
//				activePlayer.setTile(newTile);
//				activePlayer.moveTo(newTile.getLocation(activePlayer.getPlayerID()));
//			}
//		}
		
		private boolean updateTurns() {
			if (director.getTurnsLeft() > 0) {
				director.minusTurn();
//				statusPanel.updateTurnsRemainingLabel();
				return true;
			} else {
				JOptionPane.showMessageDialog(null, "Game over. Thanks for playing!");
				System.out.println("We should be calculating final player scores!");
				return false;
			}
		}
		
		public boolean havePlayersRolled() {
			return havePlayersRolled;
		}
	}
	
}
