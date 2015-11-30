package panels;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.concurrent.ConcurrentHashMap;

import javax.swing.JPanel;
import javax.swing.Timer;

import org.json.simple.JSONObject;

import client.ClientApp;
import client.IOHandler;
import gameobjects.NewPlayer;
import input.Keyboard;
import newserver.Keys;
import util.GameUtils;
import util.NewJSONObject;

public class MiniPanel extends JPanel {
	private ClientApp app;
	private Controller controller;

	private ConcurrentHashMap<String, NewPlayer> players;	// thread safe!
	private NewPlayer clientPlayer;	// the player that belong to this client!
	private Timer t;
	private Keyboard key;
	
	public MiniPanel(ClientApp app) {
		this.app = app;
		init();
		players = new ConcurrentHashMap<>();
		controller = new Controller(this);
		
		key = new Keyboard(this);
		
		addKeyListener(key);
	}
	
	private void init() {
		
	}
	
	public void playerPressed() {
		// Send JSON here
		// must put name key with player name
		NewJSONObject k = new NewJSONObject(clientPlayer.getID(), Keys.Commands.MINI_STOPPED);
		k.put(Keys.NAME, clientPlayer.getName());
		controller.send(k);
	}
	
	/**
	 * Draws tiles and players to the screen.
	 * @param g - Graphics context to draw to
	 */
	public void paintComponent(Graphics g) {
		final Graphics2D g2d = (Graphics2D)g.create();
		try {
			g2d.setColor(GameUtils.colorFromHex("#C0C0C0"));
			g2d.fillRect(0, 0, getWidth(), getHeight());
			g2d.setColor(GameUtils.getRandomColor());
			g2d.fillOval(40, 40, 20, 60);
			g2d.setColor(Color.CYAN);
			drawPlayers(g2d);
		} finally {
			g2d.dispose();
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
	
	public Controller getController() {
		return controller;
	}
	
	public void setClientPlayer(NewPlayer player) {
		this.clientPlayer = player;
	}
	
	public ConcurrentHashMap<String, NewPlayer> getPlayers() {
		return players;
	}
	
	public NewPlayer getClientPlayer() {
		return clientPlayer;
	}
	
	public class Controller extends IOHandler {
		private MiniPanel mp;
		
		public Controller(MiniPanel mp) {
			this.mp = mp;
		}

		public void send(JSONObject out) {
			app.getClient().getIOHandler().send(out);
		}

		public void receive(JSONObject in) {
			
		}
	}	
}
