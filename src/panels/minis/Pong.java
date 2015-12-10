package panels.minis;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.concurrent.ConcurrentHashMap;

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
	private Rectangle boundRect = new Rectangle(720, 350);	// bound limits for pong game objects
	
	private static final long serialVersionUID = 318748987007949296L;
	public static final int ROUND_COUNT = 5;	// rounds before gameover
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
		isActive = true;
		didPressEnter = false;
		roundsLeft = ROUND_COUNT;
		clientPlayer = app.getBoardPanel().getClientPlayer();
		pArray = GameUtils.mapToArray(app.getBoardPanel().getPlayers(), NewPlayer.class);
		
		// initial bound rect x,y placement
		boundRect.x = (app.getStatePanel().getWidth() - boundRect.width) / 2;
		boundRect.y = (app.getStatePanel().getHeight() - boundRect.height) / 2;
		int id = clientPlayer.getID();
		
		// initial ball placement setup		
		pongBall = new PongBall();
		pongBall.x = (boundRect.width + PongBall.WIDTH) / 2;
		pongBall.y = (boundRect.height + PongBall.HEIGHT) / 2;
		
		// paddle width and height
		switch (id) {
		case 0:
			yAxis = true;
			playerRect = new PongRect(boundRect.x + 5, (boundRect.height - 10) / 2, 10, 50);
			break;
		case 1: 
			yAxis = true;
			playerRect = new PongRect((boundRect.width + boundRect.x) - 15, (boundRect.height - 10) / 2, 10, 50);
			break;
		case 2:
			xAxis = true;
			playerRect = new PongRect(boundRect.width / 2, boundRect.y + 5, 50, 10);
			break;
		case 3:
			xAxis = true;
			playerRect = new PongRect(boundRect.width / 2, boundRect.y + boundRect.height - 15, 50, 10);
		}
		
		// set color of rectangle and assign name of this client player
		Color c = PlayerStyles.colors[app.getBoardPanel().getClientPlayer().getStyleID()];
		playerRect.setColor(c);
		playerRect.setName(app.getBoardPanel().getClientPlayer().getName());
		
		sendUpdate();	// be sure to send first update, when playerRect is created
		
		// use the mouse wheel for movement
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
		System.out.println("is at bounds? " + isAtBounds);
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
	
	public void update() {
		pongBall.x += pongBall.getXVel();
		pongBall.y += pongBall.getYVel();
		
		// check collision
		
		for (PongRect p : playerRects.values()) {
			if (p.intersects(pongBall)) {
				NewPlayer player = app.getBoardPanel().getPlayers().get(p.getName());
				pongBall.setXVel(pongBall.getXVel() * -1);
				pongBall.setYVel(pongBall.getYVel() * -1);
				player.setScore(player.getScore() + 1);
				app.getLeaderPanel().updateList();
			}
		}
		
		NewPlayer p = null;	// player whose score we will affect, if ball hits their wall
		
		if (pongBall.x <= boundRect.x + PongBall.WIDTH) {	// left y-axis wall collision
			p = pArray[0];
			pongBall.setXVel(pongBall.getXVel() * -1);
			pongBall.x += pongBall.getXVel();
		} else if (pongBall.x >= (boundRect.x + boundRect.width) - PongBall.WIDTH) {	// right y-axis wall collision
			p = pArray[1];
			pongBall.setXVel(pongBall.getXVel() * -1);
			pongBall.x += pongBall.getXVel();
		} else if (pongBall.y <= boundRect.y + PongBall.HEIGHT) {	// top x-axis wall collision
			if (pArray.length > 2) {
				p = pArray[2];	
			}
			pongBall.setYVel(pongBall.getYVel() * -1);
			pongBall.y += pongBall.getYVel();
		} else if (pongBall.y >= (boundRect.y + boundRect.height) - PongBall.HEIGHT) {	// btm x-axis wall collision
			if (pArray.length > 2) {
				p = pArray[3];	
			}
			pongBall.setYVel(pongBall.getYVel() * -1);
			pongBall.y += pongBall.getYVel();
		}
		
		// a ball has hit their wall, reduce their score
		if (p != null) {
			p.setScore(p.getScore() - 1);
			app.getLeaderPanel().updateList();
		}
		repaint();
		
//		if (roundsLeft == 0) {
//			sendExitRequest();
//		}
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
		obj.put("width", playerRect.width);
		obj.put("height", playerRect.height);
		playerRects.put(clientPlayer.getName(), playerRect);
		controller.send(obj);
	}
	
	/**
	 * Called whenever a key is pressed on the keyboard.
	 */
	@SuppressWarnings("unchecked")
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
	public void sendExitRequest() {
		NewJSONObject k = new NewJSONObject(clientPlayer.getID(), Keys.Commands.MINI_STOPPED);
		k.put(Keys.NAME, clientPlayer.getName());
		controller.send(k);
		isActive = false;	
		didPressEnter = true;
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
			drawPlayers(g2d);
			drawScore(g2d);
		} finally {
			g2d.dispose();
		}
	}
	
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
				x = 50;
				y = boundRect.y + boundRect.height - 30;
			} else if (i == 3) {
				x = boundRect.width + boundRect.x - scoreWidth - pad;
				y = boundRect.y + boundRect.height - 30;
			}
			
			g2d.drawString(score, x, y);
		}
	}
	
	/**
	 * Simple helper class that just stores a color value for
	 * a rectangle, so that it can easily be set and drawn.
	 * @author David Kramer
	 *
	 */
	class PongRect extends Rectangle {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1659495291668720826L;
		private Color color;
		private String pName;	// name of player this rect belongs to
		
		public PongRect() {}
		
		public PongRect(int x, int y, int w, int h) {
			super(x, y, w, h);
		}

		public void setColor(Color color) {
			this.color = color;
		}
		
		public void setName(String pName) {
			this.pName = pName;
		}
		
		public String getName() {
			return pName;
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
			if (in.containsKey("objectOnlyUpdate")) {
				updateBall(in);
				return;
			}
			
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
			r.width = (int) in.get("width");
			r.height = (int) in.get("height");
			r.setName(pName);
			r.setColor(c);
			playerRects.put(pName, r);
			repaint();
		}
		
		private void updateBall(JSONObject in) {
			if (pongBall == null) {
				pongBall = new PongBall();
			}
			
			JSONObject ball = (JSONObject)in.get("ball");
			pongBall.x = (int) ball.get("x");
			pongBall.y = (int) ball.get("y");
		}
	}	
}
