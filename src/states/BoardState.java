package states;

import java.awt.Color;
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
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import main.Dice;
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
	private Rectangle midRect;	// rect in the middle that contains game objects such as dice
	private Player activePlayer;
	private Dice dice;
	
	/**
	 * Tiles will now be mapped with their tile ID. This will make it more
	 * efficient when we move from tile to tile based on the roll. We can know
	 * exactly where to go, based on the tile ID. 
	 * @deprecated
	 */
	private ArrayList<Tile> tiles;	// tiles of the board
	private Map<Byte, Tile> tileMap;	// more efficient way to keep track of tiles on board, instead of tiles array
	/**
	 * Constructs a new board state.
	 * @param director - main director control of application
	 */
	public BoardState(NewDirector director) {
		super(director);
		//init();
	}

	public void update() {
		//TODO update director after player has moved
	}
	
	/**
	 * Calls each draw method for redrawing dice, players, and tiles to the
	 * screen.
	 * @param g - Graphics to draw to
	 */
	public void paintComponent(Graphics g) {
		final Graphics2D g2d = (Graphics2D)g.create();
		try {
			g2d.setColor(GameUtils.colorFromHex("#2b2b2b"));
			g2d.fillRect(0, 0, getWidth(), getHeight());
			statusPanel.repaint();
			g2d.setColor(Color.CYAN);
			g2d.drawRect(midRect.x, midRect.y, midRect.width, midRect.height);
			drawTiles(g2d);
			dice.draw(g2d);
			drawPlayers(g2d);	
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
	
	/**
	 * Mouse click event. Checks to see if mouse click pt location is contained
	 * inside of dice and players. If it is, their state is then updated to 
	 * reflect this event.
	 */
	public void mouseClicked(MouseEvent e) {
		if (dice.contains(e.getPoint())) {
			movePlayer();
		}
		
		for (Player p : director.getPlayers()) {
			if (p.contains(e.getPoint())) {
				if (p.isSelected()) {	//toggle between isSelected
					p.setSelected(false);
				} else {
					p.setSelected(true);
				}
			} else if (p.isSelected()) {
				for (Tile t : tileMap.values()) {
					if (t.contains(e.getPoint())) {
						p.moveTo(t.getLocation());
						activePlayer = p;
						p.setTile(t);
					}
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
		if (midRect.contains(e.getPoint())) {
			if (midRect.intersects(dice)) {
				dice.x = e.getPoint().x;
				dice.y = e.getPoint().y;
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
		midRect = createMidRect();
		dice = new Dice(600, 400);
		
		// setup layout
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.NORTH;
		c.gridx = 0;
		c.gridy = 0;
		c.ipady = 10;
		c.weightx = 1.0;
		c.weighty = 1.0;
		
		add(statusPanel, c);
		loadPlayers();	// setup players for first
		createTiles();
		
		// add event listeners
		addComponentListener(this);
		addMouseListener(this);
		addMouseMotionListener(this);
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
			
			if (newTileID > tileMap.size()) {
				newTileID = 1;
			}
			
			Tile newTile = tileMap.get(newTileID);
			
			activePlayer.moveTo(newTile.getLocation());
			activePlayer.setTile(newTile);
		}
	}
	
	/**
	 * Creates all the game tiles of the game. Currently it is 10 x 5 x 10 x 5.
	 */
	private void createTiles() {
		// Get the values of the file "tiles.map". They are our x's and y's for our tiles
		ArrayList<String> coords = new ArrayList<String>();
		File map = new File("res/tiles.map");
		try {
			Scanner sc = new Scanner(map);
			while(sc.hasNextLine()) {
				String line = sc.nextLine();
				coords.add(line);
				//System.out.println(line);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		tileMap = new HashMap<Byte, Tile>();
		System.out.println(tileMap);
		// default size 1280 x 720 for initial tile sizing
		int tileWidth = 1280 / HORIZONTAL_TILE_COUNT;
		int tileHeight = 720 / VERTICAL_TILE_COUNT;
		
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
			tileMap.put(t.getTileID(), t);
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
//		for (int i = 0; i < tiles.size(); i++) {	//TODO remove this
//			Tile t = tiles.get(i);
//			t.draw(g);
//		}
		
		for (Tile t : tileMap.values()) {
			t.draw(g);
		}
	}
	
	/**
	 * Updates the sizing of tiles. This should only be called from componentResized().
	 * This just ensures that all the tiles will fill the board, depending on the 
	 * current size of the window.
	 */
	private void resizeTiles() {
		int tileWidth = (getWidth() / HORIZONTAL_TILE_COUNT);
		int tileHeight = (getHeight() / VERTICAL_TILE_COUNT);
		
		for (Tile t : tileMap.values()) {
			t.width = tileWidth;
			t.height = tileHeight;
		}
		
		// update player within tile bounds
		if (activePlayer != null) {
			activePlayer.setLocation(activePlayer.getTile().getLocation());	
		}
	}
	
}
