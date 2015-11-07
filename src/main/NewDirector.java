package main;
import java.util.ArrayList;

import states.NewStartState;
import states.State;

// TODO: Make it output the player list sorting to test

/**
 * 
 * Main class that will keep track of the game
 * @author JoJones
 *
 */
public class NewDirector implements Runnable {
	public static final int MAX_PLAYERS = 4;	// max players allowed
	
	// States of the board
	private ArrayList<Player> players;
	private State curState;
	private int turn;
	private int maxTurns;
	private Main m;
	private Thread t;
	private boolean running;
	
	/**
	 * Create a NewDirector object that will hand stats for the whole game
	 * @param m - An instance of Main to use in NewDirector
	 */
	public NewDirector (Main m) {
		this.curState = new NewStartState(this);
		this.turn = 1;
		this.m = m;
		
		this.players = new ArrayList<Player>();
		start();
	}
	
	/**
	 ** Start the thread
	 **/
	public synchronized void start() {
		running = true;
		t = new Thread(this, "Display");
		t.start();
	}
	
	/**
	 ** Stop the thread
	 **/
	public synchronized void stop() {
		running = false;
		try {
			t.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Updates the current state
	 */
	private void update() {
		curState.update();
	}
	
	/**
	 * Renders the current state
	 */
	private void render() {
		curState.render();
	}

	/**
	 * Runs the update method 60 times a second.
	 * Runs the render method as much as possible.
	 */
	public void run() {
		// For precision
		long lastTime = System.nanoTime();
		long timer = System.currentTimeMillis();
		final double ns = 1000000000.0 / 60.0;
		
		// Init some stuff
		double delta = 0;
		int frames = 0;
		
		// Main loop
		while (running) {
			//System.out.println("MAIN LOOP"); // DEBUG
			long now = System.nanoTime();
			delta += (now - lastTime) / ns;
			lastTime = now;
			// Runs the update method every 60th of a second
			while (delta >= 1) {
				update();
				delta--;
			}
			
			render();
			frames++;
			
			// TESTING STUFF | DELETE ME, (this if, timer var, and frames var) IF NOT IN USE
			if (System.currentTimeMillis() - timer > 1000) {
				timer += 1000;
				m.setTitle("Project-Minigame | " + frames + " fps");
				frames = 0;
			}
		}
		
		stop();
	}

	public State getState() {
		return curState;
	}
	
	public void setState(State state) {
		this.curState = state;
		m.updateView();	// we have to update view to main manually!
	}
	
	public void setPlayers(ArrayList<Player> players) {
		this.players = players;
	}
	
	public ArrayList<Player> getPlayers() {
		return players;
	}
	
	public Main getMain() {
		return m;
	}
	
	public void setTurns(int turns) {
		maxTurns = turns;
	}
	
	public int getTurn() {
		return turn;
	}
}