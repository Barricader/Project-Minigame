package panels.minis;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.concurrent.ConcurrentHashMap;

import javax.swing.Timer;

import org.json.simple.JSONObject;

import com.sun.glass.events.KeyEvent;

import client.ClientApp;
import gameobjects.NewPlayer;
import panels.BaseMiniPanel;
import util.BaseController;
import util.GameUtils;
import util.Keys;
import util.NewJSONObject;
import util.PlayerStyles;

public class Pong extends BaseMiniPanel {
	
	private PongRect playerRect;	// the pong rectangle that belongs to the player
	private boolean xAxis;	// movement restricted to x-axis
	private boolean yAxis;	// movement restricted to y-axis
	private NewPlayer player;	// this player
	
	private ConcurrentHashMap<String, PongRect> playerRects;
	
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
		player = app.getBoardPanel().getClientPlayer();
		int id = player.getID();
		
		// size info from current state panel
		int h = app.getStatePanel().getHeight();
		int w = app.getStatePanel().getWidth();
		
		switch (id) {
		case 0:
			System.out.println("player rect should be on left");
			yAxis = true;
			playerRect = new PongRect(5, h / 100, 30, 100);
			break;
		case 1: 
			yAxis = true;
			playerRect = new PongRect(w - 35, h / 100, 30, 100);
			System.out.println("player rect should be on right!");
			break;
		case 2:
			xAxis = true;
			playerRect = new PongRect(w / 2, 0, 100, 30);
			System.out.println("player rect should be on top!");
			break;
		case 3:
			xAxis = true;
			playerRect = new PongRect(w / 2, h - 30, 100, 30);
			System.out.println("player rect should be on bottom!");
		}
		
		// set color of rectangle
		Color c = PlayerStyles.colors[app.getBoardPanel().getClientPlayer().getStyleID()];
		playerRect.setColor(c);
		
		sendUpdate();	// be sure to send first update, when playerRect is created
		
		/* Unused currently. Not sure how ball movement will be handled yet.*/
//		t = new Timer(16, e -> {
//			update();
//		});
		
		// use the mouse wheel for movement
		addMouseWheelListener(new MouseWheelListener() {
			public void mouseWheelMoved(MouseWheelEvent e) {
				if (xAxis) {
					playerRect.x += e.getWheelRotation() * 15;
				} else if (yAxis) {
					playerRect.y += e.getWheelRotation() * 15;
				}
				checkMovement();
				sendUpdate();
			}
		});
	
	}
	
	/**
	 * Checks to make sure that paddles stay within bounds.
	 */
	private void checkMovement() {
		if (xAxis) {
			if (playerRect.x <= 5) {
				playerRect.x = 5;
			} else if (playerRect.x >= app.getStatePanel().getWidth() - playerRect.width) {
				playerRect.x = app.getStatePanel().getWidth() - playerRect.width;
			}
			
		} else if (yAxis) {
			if (playerRect.y <= 0) {
				playerRect.y = 0;
			} else if (playerRect.y >= app.getStatePanel().getHeight() - playerRect.height) {
				playerRect.y = app.getStatePanel().getHeight() - playerRect.height;
			}	
		}
	}
	
	public void update() {}	// this method unused. we use sendUpdate instead
	
	/**
	 * Sends a JSONObject update with the coordinates and size of this
	 * player's pongRect as well as the style id, so that it can be
	 * colored easily.
	 */
	public void sendUpdate() {
		NewJSONObject obj = new NewJSONObject(player.getID(), Keys.Commands.MINI_UPDATE);
		obj.put(Keys.NAME, "pong");
		obj.put(Keys.PLAYER_NAME, player.getName());
		obj.put(Keys.STYLE_ID, player.getStyleID());
		obj.put("x", playerRect.x);
		obj.put("y", playerRect.y);
		obj.put("width", playerRect.width);
		obj.put("height", playerRect.height);
		playerRects.put(player.getName(), playerRect);
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
		sendUpdate();
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
		checkMovement();
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
		checkMovement();
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
			g2d.drawString("Pong!", getWidth() / 2, getHeight() / 2);
			for (String name : playerRects.keySet()) {
				PongRect r = playerRects.get(name);
				g2d.setColor(r.getColor());
				g2d.fillRect(r.x, r.y, r.width, r.height);
				if (name.equals(player.getName())) {
					g2d.setColor(Color.BLACK);
					g2d.setStroke(new BasicStroke(2.0f));
					g2d.drawRect(r.x, r.y, r.width, r.height);
				}
			}
			g2d.setColor(Color.CYAN);
			drawPlayers(g2d);
		} finally {
			g2d.dispose();
		}
	}
	
	/**
	 * Simple helper class that just stores a color value for
	 * a rectangle, so that it can easily be set and drawn.
	 * @author David Kramer
	 *
	 */
	class PongRect extends Rectangle {
		private Color color;
		
		public PongRect() {}
		
		public PongRect(int x, int y, int w, int h) {
			super(x, y, w, h);
		}

		public void setColor(Color color) {
			this.color = color;
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
			System.out.println("Pong client received: " + in.toJSONString());
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
			r.setColor(c);
			playerRects.put(pName, r);
			repaint();
		}
	}	
}
