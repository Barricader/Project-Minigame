package main;
import java.util.ArrayList;

import states.StartState;
import states.State;

// TODO: Make it output the player list sorting to test

/**
 * 
 * Main class that will keep track of the game
 * @author JoJones
 *
 */
public class NewDirector implements Runnable {
	// States of the board
	private ArrayList<Player> players;
	private State curState;
	private int turn;
	private int maxTurns;
	private Main m;
	private Thread t;
	private boolean running;
	
	/**
	 * Create a Director object
	 * @param maxTurns - Amount of max turns in the current game
	 * @param players - Players to play in the game
	 */
	public NewDirector (Main m) {
		this.curState = new StartState(this);
		this.turn = 1;
		this.m = m;
		
		this.players = new ArrayList<Player>();
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
	
	private void update() {
		// Do state update here
		curState.update();
		System.out.println("updating");
	}
	
	private void render() {
		// Do state render here
		curState.render();
		System.out.println("rendering");
	}

	/**
	 * Runs the update method 60 times a second.
	 * Runs the render method as much as possible.
	 */
	public void run() {
		long lastTime = System.nanoTime();
		long timer = System.currentTimeMillis();
		final double ns = 1000000000.0 / 60.0;
		double delta = 0;
		int frames = 0;
		while (running) {
			long now = System.nanoTime();
			delta += (now - lastTime) / ns;
			lastTime = now;
			while (delta >= 1) {
				update();
				delta--;
			}
			render();
			frames++;
			
			if (System.currentTimeMillis() - timer > 1000) {
				timer += 1000;
				m.setTitle("Thing | " + frames + " fps");
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
	}
	
	public void setPlayers(ArrayList<Player> players) {
		this.players = players;
	}
	
	public ArrayList<Player> getPlayers() {
		return players;
	}
	
	public void setTurns(int turns) {
		maxTurns = turns;
	}
}