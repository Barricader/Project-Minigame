package panels.minis;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.concurrent.ConcurrentHashMap;

import javax.swing.Timer;

import org.json.simple.JSONObject;

import com.sun.glass.events.KeyEvent;

import client.ClientApp;
import gameobjects.NewPlayer;
import gameobjects.PongBall;
import panels.BaseMiniPanel;
import util.BaseController;
import util.GameUtils;
import util.Keys;
import util.NewJSONObject;
import util.PlayerStyles;

public class Pong extends BaseMiniPanel {
	// bound limits for pong game objects
	public static final int BOUND_WIDTH = 720;
	public static final int BOUND_HEIGHT = 350;
	private Rectangle boundRect = new Rectangle(BOUND_WIDTH, BOUND_HEIGHT);
	
	private static final long serialVersionUID = 318748987007949296L;
	public static final int ROUND_COUNT = 3;	// rounds before gameover
	private PongRect playerRect;	// the pong rectangle that belongs to the player
	private boolean xAxis;	// movement restricted to x-axis
	private boolean yAxis;	// movement restricted to y-axis
	private boolean didPressEnter;	// have we pressed enter already to exit?
	
	private ConcurrentHashMap<String, PongRect> playerRects;
	private NewPlayer[] pArray;
	private PongBall pongBall;
	private boolean sentBoundUpdate;	// determines if we have sent an update yet, regarding our bound limit
	private int roundsLeft;
	
	/**
	 * Constructs new Pong mini game with a link to the main ClientApp.
	 * @param app - Target client app.
	 */
	public Pong(ClientApp app) {
		super(app);
		controller = new Controller(app);
	}
	
	/**
	 * Initializes the player pong rect, based on their ID. Players
	 * can move either on the x-axis or y-axis.
	 */
	public void init() {
		playerRects = new ConcurrentHashMap<>();
		players = app.getBoardPanel().getPlayers();
		isActive = true;
		didPressEnter = false;
		roundsLeft = 1;
		clientPlayer = app.getBoardPanel().getClientPlayer();
		pArray = GameUtils.mapToArray(app.getBoardPanel().getPlayers(), NewPlayer.class);
		GameUtils.sortPlayersByName(pArray);
		
		// initial bound rect x,y placement
		boundRect.x = (app.getStatePanel().getWidth() - boundRect.width) / 2;
		boundRect.y = (app.getStatePanel().getHeight() - boundRect.height) / 2;
		int id = clientPlayer.getID();
		
		// initial ball placement setup		
		pongBall = new PongBall();
		resetBall(1);
		
		// paddle width and height
		switch (id) {
		case 0:
			yAxis = true;
			playerRect = new PongRect(boundRect.x + 5, 
					(boundRect.height - 10) / 2, 
					10, 50, PongRect.Y_AXIS);
			break;
		case 1: 
			yAxis = true;
			playerRect = new PongRect((boundRect.width + boundRect.x) - 15, 
					(boundRect.height - 10) / 2, 
					10, 50, PongRect.Y_AXIS);
			break;
		case 2:
			xAxis = true;
			playerRect = new PongRect(boundRect.width / 2,
					boundRect.y + 5, 50, 10, PongRect.X_AXIS);
			break;
		case 3:
			xAxis = true;
			playerRect = new PongRect(boundRect.width / 2, 
					boundRect.y + boundRect.height - 15, 
					50, 10, PongRect.X_AXIS);
		}
		
		// set color of rectangle and assign name of this client player
		Color c = PlayerStyles.colors[app.getBoardPanel().getClientPlayer().getStyleID()];
		playerRect.setColor(c);
		playerRect.setName(app.getBoardPanel().getClientPlayer().getName());
		
		sendUpdate();	// be sure to send first update, when playerRect is created
		
		// use the mouse wheel for movement for this player
		addMouseWheelListener(new MouseWheelListener() {
			public void mouseWheelMoved(MouseWheelEvent e) {
				if (xAxis) {
					playerRect.x += e.getWheelRotation() * 15;
				} else if (yAxis) {
					playerRect.y += e.getWheelRotation() * 15;
				}
				if (checkMovement()) {
					sendUpdate();
				}
			}
		});
		
		// mouse movement for this player
		addMouseMotionListener(new MouseMotionAdapter() {
			public void mouseMoved(MouseEvent e) {
				System.out.println("mouse moved!");
				if (xAxis) {
					playerRect.x = e.getPoint().x;
				} else if (yAxis) {
					playerRect.y = e.getPoint().y;
				}
				if (checkMovement()) {
					sendUpdate();
				}
			}
		});
		
		if (!t.isRunning()) {
			t.start();
		}
	}
	
	public void resetBall(int direction) {
		pongBall.x = (boundRect.width + PongBall.WIDTH) / 2;
		pongBall.y = (boundRect.height + PongBall.HEIGHT) / 2;
		pongBall.setXVel(5 * direction);
		pongBall.setYVel(3 * direction);
		pongBall.setLastHitPName(null);
	}
	
	public void changeScore() {
		if (pongBall.getLastHitPName() != null) {
			NewPlayer hitPlayer = players.get(pongBall.getLastHitPName());
			System.out.println("hit player:" + hitPlayer);
			if (hitPlayer != null) {
				hitPlayer.setScore(hitPlayer.getScore() + 1);
				app.getLeaderPanel().updateList();
			}
		}
//		GameUtils.resetTimer(t);
//		System.out.println("CHANGE SCORE RESET!");
//		t.stop();
//		t.setInitialDelay(1200);
//		t = new Timer(16, e -> {
//			update();
//		});
//		t.start();
	}
	
	/**
	 * Checks to make sure that pongRect stay within bounds. If the 
	 * pongRect is at an extreme boundary, (i.e. edge of screen), this 
	 * will return false. This is useful when sending updates, as it will
	 * limit redundant packet updates, since nothing will have changed and 
	 * reduces the load on the server.
	 * @return true if within bounds, false otherwise.
	 * 
	 */
	private boolean checkMovement() {
		boolean isAtBounds = false;
		if (xAxis) {
			if (playerRect.x <= boundRect.x) {
				playerRect.x = boundRect.x;
				isAtBounds = true;
			} else if (playerRect.x >= (boundRect.x + boundRect.width) - playerRect.width) {
				playerRect.x = boundRect.width - playerRect.width;
				isAtBounds = true;
			}
			
		} else if (yAxis) {
			if (playerRect.y <= boundRect.y) {
				playerRect.y = boundRect.y;	
				isAtBounds = true;
			} else if (playerRect.y >= (boundRect.y + boundRect.height) - playerRect.height) {
				playerRect.y = boundRect.y + boundRect.height - (playerRect.height);
				isAtBounds = true;
			}	
		}
		if (!isAtBounds) {
			// send location since we're within bounds
			sentBoundUpdate = false;
			return true;
		} else if (isAtBounds && !sentBoundUpdate) {
			// we're at bounds, but haven't sent last update
			sentBoundUpdate = true;
			return true;
		} else {
			// we've sent bound updates and now we shouldn't send anymore!
			// while we're at the bounds, to reduce redundant packets.
			sentBoundUpdate = true;
			return false;
		}
	}
	
	/**
	 * Main timer loop that actively updates the ball and checks
	 * for collision with the world and pong rects.
	 */
	public void update() {
		pongBall.x += pongBall.getXVel();
		pongBall.y += pongBall.getYVel();
		
		boolean paddleCollide = false;
		
		for (PongRect pr : playerRects.values()) {
			if (pr.intersects(pongBall)) {
				pongBall.x = pongBall.lastX;
				
				pongBall.reflectX();
				pongBall.setLastHitPName(pr.getName());
				GameUtils.playSound("res/doot.wav");
				paddleCollide = true;
				break;
			}
		}
		
		if (!paddleCollide) {
			if (pongBall.x <= (boundRect.x + pongBall.width)) {
				pongBall.reflectX();
				checkShouldScore(PongRect.X_AXIS, PongRect.LEFT_Y_AXIS);
			} else if (pongBall.x >= (boundRect.x + boundRect.width) - pongBall.width) {
				pongBall.reflectX();
				checkShouldScore(PongRect.X_AXIS, PongRect.RIGHT_Y_AXIS);
			} else if (pongBall.y <= (boundRect.y + pongBall.width)) {
				pongBall.reflectY();
				if (pArray.length > 2) {
					checkShouldScore(PongRect.Y_AXIS, PongRect.TOP_X_AXIS);
				}
				
			} else if (pongBall.y >= (boundRect.y + boundRect.height) - pongBall.height) {
				pongBall.reflectY();
				if (pArray.length > 2) {
					checkShouldScore(PongRect.Y_AXIS, PongRect.BTM_X_AXIS);
				}
			}
//			if (pongBall.x <= (boundRect.x + pongBall.width) 
//					|| pongBall.x >= (boundRect.x + boundRect.width) - pongBall.width) {
//				pongBall.x = pongBall.lastX;
//				pongBall.reflectX();
//				checkShouldScore(PongRect.X_AXIS);
//			} else if (pongBall.y <= (boundRect.y + pongBall.width)
//					|| pongBall.y >= (boundRect.y + boundRect.height) - pongBall.height) {
//				pongBall.y = pongBall.lastY;
//				pongBall.reflectY();
//				if (pArray.length > 2) {
//					checkShouldScore(PongRect.Y_AXIS);	
//				}
//			}
		}
		pongBall.lastX = pongBall.x - pongBall.getXVel();
		pongBall.lastY = pongBall.y - pongBall.getYVel();
		repaint();
	}
	
	private void checkShouldScore(int axis, int side) {
		if (pongBall.getLastHitPName() != null) {
			PongRect pr = playerRects.get(pongBall.getLastHitPName());
			System.out.println("last hit player: " + pongBall.getLastHitPName());
			if (pr.getAxis() != axis) {
				System.out.println(pongBall.getLastHitPName() + ", should score!");
				int pScore = players.get(pongBall.getLastHitPName()).getScore();
				players.get(pongBall.getLastHitPName()).setScore(++pScore);
			}
		}
		updateRound();
		if (side == PongRect.LEFT_Y_AXIS) {
			resetBall(-1);
		} else if (side == PongRect.RIGHT_Y_AXIS) {
			resetBall(1);
		} else {
			resetBall(1);
		}
	}
	
	private void updateRound() {
		roundsLeft++;
		app.getLeaderPanel().updateList();
		GameUtils.resetTimer(t);
		t.setInitialDelay(1000);
		if (roundsLeft > ROUND_COUNT) {
			sendExitRequest();
		} else {
			t.addActionListener(e -> {
				update();
			});
			t.start();
		}
	}
	
	/**
	 * Sends a JSONObject update with the coordinates and size of this
	 * player's pongRect as well as the style id, so that it can be
	 * colored easily.
	 */
	@SuppressWarnings("unchecked")
	public void sendUpdate() {
		NewJSONObject obj = new NewJSONObject(clientPlayer.getID(), Keys.Commands.MINI_UPDATE);
		obj.put(Keys.NAME, "pong");
		obj.put(Keys.PLAYER_NAME, clientPlayer.getName());
		obj.put(Keys.STYLE_ID, clientPlayer.getStyleID());
		obj.put("x", playerRect.x);
		obj.put("y", playerRect.y);
		obj.put("axis", playerRect.getAxis());
		playerRects.put(clientPlayer.getName(), playerRect);
		controller.send(obj);
	}
	
	/**
	 * Called whenever a key is pressed on the keyboard.
	 */
	public void playerPressed() {
		if (isActive) {
			if (xAxis) {
				moveX();
			} else if (yAxis) {
				moveY();
			}
		}
		
		// send request to leave pong
		if (key.keys[KeyEvent.VK_ENTER] && !didPressEnter) {
			sendExitRequest();
		}
	}
	
	/**
	 * Sends a request to the server, letting it know that this client
	 * is ready to leave.
	 */
	@SuppressWarnings("unchecked")
	public void sendExitRequest() {
		NewJSONObject k = new NewJSONObject(clientPlayer.getID(), Keys.Commands.MINI_STOPPED);
		k.put(Keys.NAME, clientPlayer.getName());
		controller.send(k);
		isActive = false;	
		didPressEnter = true;
		GameUtils.resetTimer(t);
		t = new Timer(16, e -> {
			update();
		});
	}
	
	/**
	 * Moves the pongRect on the x-axis via the keyboard left/right arrow
	 * keys.
	 */
	private void moveX() {
		if (key.keys[KeyEvent.VK_LEFT]) {
			playerRect.x-= 10;
		} else if (key.keys[KeyEvent.VK_RIGHT]) {
			playerRect.x+= 10;
		}
		if (checkMovement()) {
			sendUpdate();
		}
	}
	
	/**
	 * Moves the pongRect on the y-axis via the keyboard up/down arrow
	 * keys.
	 */
	private void moveY() {
		if (key.keys[KeyEvent.VK_UP]) {
			playerRect.y-= 10;
		} else if (key.keys[KeyEvent.VK_DOWN]) {
			playerRect.y+= 10;
		}
		if (checkMovement()) {
			sendUpdate();
		}
	}
	
	/**
	 * Draws tiles and players to the screen.
	 * @param g - Graphics context to draw to
	 */
	public void paintComponent(Graphics g) {
		final Graphics2D g2d = (Graphics2D)g.create();
		try {
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2d.setColor(GameUtils.colorFromHex("#2b2b2b"));
			g2d.fillRect(0, 0, getWidth(), getHeight());
			g2d.setColor(Color.WHITE);
			g2d.setFont(new Font("Courier New", Font.BOLD, 40));
			g2d.drawString("Pong!", (boundRect.x + boundRect.width) / 2, (boundRect.y + boundRect.height) / 2);
			g2d.drawRect(boundRect.x, boundRect.y, boundRect.width, boundRect.height);
			for (String name : playerRects.keySet()) {
				PongRect r = playerRects.get(name);
				g2d.setColor(r.getColor());
				g2d.fillRect(r.x, r.y, r.width, r.height);
				if (name.equals(clientPlayer.getName())) {
					g2d.setColor(Color.BLACK);
					g2d.setStroke(new BasicStroke(2.0f));
					g2d.drawRect(r.x, r.y, r.width, r.height);
				}
			}
			pongBall.draw(g2d);
			g2d.setColor(Color.CYAN);
			drawScore(g2d);
			drawRoundCount(g2d);
		} finally {
			g2d.dispose();
		}
	}
	
	/**
	 * Draws the player scores in the corners of the screen
	 * with their specified color.
	 * @param g2d
	 */
	private void drawScore(Graphics2D g2d) {
		final int pad = 20;
		
		for (int i = 0; i < pArray.length; i++) {
			g2d.setColor(PlayerStyles.colors[pArray[i].getStyleID()]);
			String score = "" + pArray[i].getScore();
			int scoreWidth = g2d.getFontMetrics().stringWidth(score);
			int x = boundRect.x + (scoreWidth / 2) + pad;
			int y = 50;

			if (i == 1) {
				x = boundRect.width + boundRect.x - scoreWidth - pad;
			} else if (i == 2) {
				x = boundRect.x + (scoreWidth / 2) + pad;
				y = boundRect.y + boundRect.height - 30;
			} else if (i == 3) {
				x = boundRect.width + boundRect.x - scoreWidth - pad;
				y = boundRect.y + boundRect.height - 30;
			}
			
			g2d.drawString(score, x, y);
		}
	}
	
	private void drawRoundCount(Graphics2D g2d) {
		g2d.setFont(new Font("Courier New", Font.BOLD, 20));
		g2d.setColor(Color.WHITE);
		String rounds = "Round: " + roundsLeft + " of " + ROUND_COUNT;
		int x = 375;
		g2d.drawString(rounds, x, 50);
	}
	
	/**
	 * Simple helper class that just stores a color value and name for
	 * a rectangle, so that it can easily be set and drawn.
	 * @author David Kramer
	 *
	 */
	class PongRect extends Rectangle {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1659495291668720826L;
		public static final int X_AXIS = 0;
		public static final int Y_AXIS = 1;
		public static final int LEFT_Y_AXIS = 0;
		public static final int RIGHT_Y_AXIS = 1;
		public static final int TOP_X_AXIS = 2;
		public static final int BTM_X_AXIS = 3;
		private Color color;
		private String pName;	// name of player this rect belongs to
		private int axis;
		private int dirAxis;	// left, right, btm, top?
		
		public PongRect() {}
		
		public PongRect(int x, int y, int w, int h, int axis) {
			super(x, y, w, h);
			this.axis = axis;
		}

		public void setColor(Color color) {
			this.color = color;
		}
		
		public void setName(String pName) {
			this.pName = pName;
		}
		
		public void setAxis(int axis) {
			this.axis = axis;
			// size up the rectangle depending on axis
			if (axis == X_AXIS) {
				width = 50;
				height = 10;
			} else if (axis == Y_AXIS) {
				height = 50;
				width = 10;
			}
		}
		
		public String getName() {
			return pName;
		}
		
		public int getAxis() {
			return axis;
		}
		
		public Color getColor() {
			return color;
		}
	}
	
	/**
	 * Controller for handling updates with pong stuff.
	 * @author David Kramer
	 *
	 */
	public class Controller extends BaseController {

		public Controller(ClientApp app) {
			super(app);
		}

		public void send(JSONObject out) {
			app.getClient().getIOHandler().send(out);
		}

		public void receive(JSONObject in) {			
			String pName = (String) in.get(Keys.PLAYER_NAME);
			Color c = PlayerStyles.colors[(int) in.get(Keys.STYLE_ID)];	// color the rectangle
			
			// update rectangle from received player
			PongRect r = null;
			if (playerRects.containsKey(pName)) {
				r = playerRects.get(pName);
			} else {
				r = new PongRect();	// haven't received yet.
			}
			r.x = (int) in.get("x");
			r.y = (int) in.get("y");
//			r.width = (int) in.get("width");
//			r.height = (int) in.get("height");
			r.setName(pName);
			r.setColor(c);
			r.setAxis((int) in.get("axis"));
			playerRects.put(pName, r);
			repaint();
		}
	}	
}
