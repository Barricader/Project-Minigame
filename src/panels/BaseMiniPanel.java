package panels;

import java.awt.Graphics;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.JPanel;
import javax.swing.Timer;

import client.ClientApp;
import gameobjects.NewPlayer;
import input.Keyboard;
import util.BaseController;

public abstract class BaseMiniPanel extends JPanel {
	protected static final long serialVersionUID = -2710194893729492174L;
	protected static int FPS = 60;	// default 60 -> sub classes can change.
	protected ClientApp app;
	protected BaseController controller;
	protected boolean isActive;
	protected ConcurrentHashMap<String, NewPlayer> players;
	protected NewPlayer clientPlayer;
	protected Timer t;
	protected Keyboard key;
	protected Runnable r;
	protected ExecutorService ex;
	
	public BaseMiniPanel(ClientApp app) {
		this.app = app;
//		init();
		players = new ConcurrentHashMap<>();
//		controller = new Controller(this);
//		t = new Timer(16, e -> update());
		//t.start();
		
		// thread stuff
		ex = Executors.newCachedThreadPool();
		r = () -> {
			
			long startTime;
			long elapsed;
			long wait;
			int targetTime = 1000 / FPS;
			
			while (isActive) {
				startTime = System.nanoTime();
				update();	// call sub class update
				
				elapsed = System.nanoTime() - startTime;
				
				wait = targetTime - elapsed / 1000000;
				
				if (wait < 0) {
					wait = 5;
				}
				
				try {
					Thread.sleep(wait);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
	}
	
	public Runnable getRunnable() {
		return r;
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
		ex = Executors.newCachedThreadPool();
		ex.submit(r);
//		t.start();
	}
	
	public void exit() {
//		t.stop();
		ex.shutdown();
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
