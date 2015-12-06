package panels;

import java.awt.Graphics;
import java.util.concurrent.ConcurrentHashMap;

import javax.swing.JPanel;
import javax.swing.Timer;

import org.json.simple.JSONObject;

import client.ClientApp;
import client.IOHandler;
import gameobjects.NewPlayer;
import input.Keyboard;
import util.BaseController;

public abstract class BaseMiniPanel extends JPanel {
	protected static final long serialVersionUID = -2710194893729492174L;
	protected ClientApp app;
	protected BaseController controller;
	protected boolean isActive;
	protected ConcurrentHashMap<String, NewPlayer> players;
	protected NewPlayer clientPlayer;
	protected Timer t;
	protected Keyboard key;
	
	public BaseMiniPanel(ClientApp app) {
		this.app = app;
//		init();
		players = new ConcurrentHashMap<>();
//		controller = new Controller(this);
		t = new Timer(16, e -> update());
		//t.start();
	}
	
	public abstract void init();
	
	public abstract void update();
	
	/**
	 * Abstract method for handling key events.
	 */
	public abstract void playerPressed();
	
	/**
	 * Draws players.
	 * @param g - Graphics context to draw to
	 */
	protected void drawPlayers(Graphics g) {
		for (NewPlayer p : players.values()) {
			p.draw(g);
		}
	}
	
	public BaseController getController() {
		return controller;
	}
	
	public void setActive(boolean b) {
		isActive = b;
		t.start();
	}
	
	public void exit() {
		t.stop();
	}
	
	public void setClientPlayer(NewPlayer player) {
		this.clientPlayer = player;
	}
	
	public void setKey(Keyboard key) {
		this.key = key;
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
	
}
