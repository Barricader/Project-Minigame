package states;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.JPanel;

import main.Dice;
import main.Main;
import main.Director;
import main.Player;
import main.PlayerManager;
import main.Tile;
import screen.StatusPanel;
import util.GameUtils;

public class BoardState extends State {
	public static final byte HORIZONTAL_TILE_COUNT = 10;
	public static final byte VERTICAL_TILE_COUNT = HORIZONTAL_TILE_COUNT / 2;
	private static final long serialVersionUID = 1L;	
	
	private StatusPanel statusPanel;
	private BoardPanel boardPanel;
	private PlayerManager playerMngr;
	
	public BoardState(Director director) {
		super(director);
	}
	
	public void init() {
		setLayout(new GridBagLayout());
		statusPanel = new StatusPanel(director);
		playerMngr = new PlayerManager(director, this);
		boardPanel = new BoardPanel();
		
		GridBagConstraints c = new GridBagConstraints();
		
		// status panel
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.NORTH;
		c.gridx = 0;
		c.gridy = 0;
		c.ipady = 10;
		add(statusPanel, c);
	
		// board panel
		c.fill = GridBagConstraints.BOTH;
		c.anchor = GridBagConstraints.SOUTH;
		c.gridx = 0;
		c.gridy = 1;
		c.ipady = 0;
		c.weightx = 1.0;
		c.weighty = 1.0;
		add(boardPanel, c);

		playerMngr.updateStatusOnLaunch();
	}

	public void update() {
		for (Player p : playerMngr.getPlayers()) {
			p.update();
		}
	}

	public void render() {
		boardPanel.repaint();
	}
	
	public BoardPanel getBoard() {
		return boardPanel;
	}
	
	public StatusPanel getStatusPanel() {
		return statusPanel;
	}
	
	
	/**
	 * Inner class for the actual board drawing.
	 * @author David Kramer
	 *
	 */
	public class BoardPanel extends JPanel implements ComponentListener, MouseListener, MouseMotionListener {
		private static final long serialVersionUID = -7658637279679249153L;
		private ArrayList<Tile> tiles;
		private Dice dice;
		private Rectangle midRect;
		
		public BoardPanel() {
			init();
			addComponentListener(this);
			addMouseListener(this);
			addMouseMotionListener(this);
		}
		
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
		
		private void init() {
			createTiles();
			loadPlayers();
			createDice();
		}
		
		private void drawTiles(Graphics g) {
			for (Tile t : tiles) {
				t.draw(g);
			}
		}
		
		private void drawPlayers(Graphics g) {
			for (Player p : playerMngr.getPlayers()) {
				p.draw(g);
			}
		}
		
		private void loadPlayers() {
			createMidRect();
			
			for (int i = 0; i < playerMngr.getPlayers().size(); i++) {
				Player p = playerMngr.getPlayers().get(i);
				p.x = midRect.x + (p.width * i) + 100 + (i * 50);
				p.y = midRect.y + 50;
			}
		}
		
		private void createDice() {
			dice = new Dice(midRect.x + (midRect.width / 2 - Dice.WIDTH), midRect.y + (midRect.height / 2 - Dice.HEIGHT));
		}
		
		private void createMidRect() {
			int width = Main.getInstance().getContentPane().getSize().width;
			int height = Main.getInstance().getContentPane().getSize().height - 35;
			
			
			int rectWidth = 500;
			int rectHeight = 350;
			int x = (width - rectWidth) / 2;
			int y = (height - rectHeight) / 2;
			
			midRect = new Rectangle(x, y, rectWidth, rectHeight);
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
			
			// size of this component is currently unknown, so we have to get content size from main!
			int width = Main.getInstance().getContentPane().getSize().width;
			int height = Main.getInstance().getContentPane().getSize().height - statusPanel.getHeight();
			
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
		
		/**
		 * Updates the positioning of the dice, relative to the midrect, in response
		 * to the window being resized.
		 */
		private void updateDiceFromResize() {
			dice.x = midRect.x + (midRect.width / 2 - Dice.WIDTH);
			dice.y = midRect.y + (midRect.height / 2 - Dice.HEIGHT);
		}
		
		/**
		 * Updates all players positioning in response to a window resize event.
		 */
		private void updatePlayersFromResize() {
			for (int i = 0; i < playerMngr.getPlayers().size(); i++) {
				Player p = playerMngr.getPlayers().get(i);
				
				if (p.getTile() != null) {	// we can reassign location based on current tile
					p.setLocation(p.getTile().getLocation(p.getPlayerID()));
				} else { // still in middle of screen, update relative to mid rect
					Rectangle midRect = getMidRect();
					p.x = midRect.x + (p.width * i) + 100 + (i * 50);
					p.y = midRect.y + 50;
				}
			}
		}
		
		private void dragDice(Point p) {
			Rectangle newMid = (Rectangle) midRect.clone();
			newMid.x += dice.width/2;
			newMid.y += dice.height/2;
			newMid.width -= dice.width;
			newMid.height -= dice.height;
			if (newMid.contains(p)) {
				if (midRect.intersects(dice)) {
					dice.x = p.x - dice.width/2;
					dice.y = p.y - dice.height/2;
				}
			}
		}

		public void mouseClicked(MouseEvent e) {
			if (dice.contains(e.getPoint())) {	// clicked on dice, try to roll a player
				if (playerMngr.firstRollDone()) {
					playerMngr.rollNextPlayer(dice);
				} else {
					playerMngr.firstRoll(dice);
				}
			}
		}
		
		public void mouseDragged(MouseEvent e) {
			if (dice.contains(e.getPoint())) {
				dragDice(e.getPoint());
			}
		}

		// unused mouse listener methods
		public void mouseMoved(MouseEvent e) {}
		public void mousePressed(MouseEvent e) {}
		public void mouseReleased(MouseEvent e) {}
		public void mouseEntered(MouseEvent e) {}
		public void mouseExited(MouseEvent e) {}

		public void componentResized(ComponentEvent e) {
			resizeTiles();
			resizeMidRect();
			updateDiceFromResize();
			updatePlayersFromResize();
		}

		// unused component listener methods
		public void componentMoved(ComponentEvent e) {}
		public void componentShown(ComponentEvent e) {}
		public void componentHidden(ComponentEvent e) {}
		
		
		public ArrayList<Tile> getTiles() {
			return tiles;
		}
		
		public Rectangle getMidRect() {
			return midRect;
		}

	}

}
