package states;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;

import javax.swing.JPanel;

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
	private Dice dice;
	private ArrayList<Tile> tiles;	// tiles of the board

	/**
	 * Constructs a new board state.
	 * @param director - main director control of application
	 */
	public BoardState(NewDirector director) {
		super(director);
		init();
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
		g2d = (Graphics2D)g;
		g2d.clearRect(0, 0, getWidth(), getHeight());
		g2d.setColor(GameUtils.colorFromHex("#2b2b2b"));
		g2d.fillRect(0, 0, getWidth(), getHeight());
		g2d.setColor(Color.CYAN);
		g2d.drawRect(midRect.x, midRect.y, midRect.width, midRect.height);
		drawTiles(g2d);
		dice.draw(g2d);
		drawPlayers(g2d);
		g2d.dispose();
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
			dice.roll(Dice.SIZE);
		}
		
		for (Player p : director.getPlayers()) {
			if (p.contains(e.getPoint())) {
				if (p.isSelected()) {	//toggle between isSelected
					p.setSelected(false);
				} else {
					p.setSelected(true);
				}
			} else if (p.isSelected()) {
				if (p.isMoving()) {
					p.setNewLocation(e.getPoint());	// change to new destination
				} else {
					p.moveTo(e.getPoint());	
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
	private void init() {
		statusPanel = new StatusPanel(director);
		tiles = new ArrayList<>();
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
		
		for (int i = 0; i < director.getPlayers().size(); i++) {
			Player p = director.getPlayers().get(i);
			
			p.x = midRect.x * i;
			p.y = 500;
		}
	}
	
	/**
	 * Creates all the game tiles of the game. Currently it is 10 x 5 x 10 x 5.
	 */
	private void createTiles() {
		tiles = new ArrayList<>();
		// default size 1280 x 720 for initial tile sizing
		int tileWidth = 1280 / HORIZONTAL_TILE_COUNT;
		int tileHeight = 720 / VERTICAL_TILE_COUNT;
		
		for (int y = 0; y < 5; y++) {
			for (int x = 0; x < 10; x++) {
				Color color = GameUtils.getRandomColor();
				
				if (y == 0 || y == 4) {	// top and bottom tiles
					Tile t = new Tile(color, 0, x + y, x, y, tileWidth, tileHeight);
					tiles.add(t);
				} else
				if (x == 0 || x == 9) { // side tiles
					Tile t = new Tile(color, 0, x + y, x, y, tileWidth, tileHeight);
					tiles.add(t);
				}
			}
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
		for (int i = 0; i < tiles.size(); i++) {
			Tile t = tiles.get(i);
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
		
		for (int i = 0; i < tiles.size(); i++) {
			Tile t = tiles.get(i);
			t.width = tileWidth;
			t.height = tileHeight;
		}
	}
	
}
