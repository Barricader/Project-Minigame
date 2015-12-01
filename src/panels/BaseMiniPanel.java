package panels;

import java.awt.Graphics;
import java.util.concurrent.ConcurrentHashMap;

import javax.swing.JPanel;

import org.json.simple.JSONObject;

import client.ClientApp;
import client.IOHandler;
import gameobjects.NewPlayer;

public abstract class BaseMiniPanel extends JPanel {
	private static final long serialVersionUID = -2710194893729492174L;
	private ClientApp app;
	private Controller controller;
	private boolean isActive;
	private ConcurrentHashMap<String, NewPlayer> players;
	private NewPlayer clientPlayer;
	//private Timer t;
	
	public BaseMiniPanel(ClientApp app) {
		this.app = app;
		init();
		players = new ConcurrentHashMap<>();
		controller = new Controller(this);
	}
	
	protected abstract void init();
	
	public abstract void update();
	
	/**
	 * Draws players.
	 * @param g - Graphics context to draw to
	 */
	protected void drawPlayers(Graphics g) {
		for (NewPlayer p : players.values()) {
			p.draw(g);
		}
	}
	
	public Controller getController() {
		return controller;
	}
	
	public void setActive(boolean b) {
		isActive = b;
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
	
	public boolean isActive() {
		return isActive;
	}
	
	protected class Controller extends IOHandler {
		private BaseMiniPanel bmp;
		
		public Controller(BaseMiniPanel bmp) {
			this.bmp = bmp;
		}

		public void send(JSONObject out) {
			app.getClient().getIOHandler().send(out);
		}

		public void receive(JSONObject in) {
			// check updates from server here
		}
	}	
}
