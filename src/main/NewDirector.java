package main;
import java.util.ArrayList;

import states.BoardState;
import states.NewStartState;
import states.State;

// TODO: Make it output the player list sorting to test

/**
 * 
 * Main class that will keep track of the game
 * @author David Kramer
 * @author JoJones
 *
 */
public class NewDirector implements Runnable {
	public static final int MAX_PLAYERS = 4;	// max players allowed
	public static final int FPS = 60;
	
	// States of the board
	private ArrayList<Player> players;
	private BoardState boardState;
	private State miniGameState;
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
		this.boardState = new BoardState(this);
		this.turn = 1;
		this.m = m;
		
		this.players = new ArrayList<>();
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
	 * Stop the thread
	 */
	public synchronized void stop() {
		running = false;
		try {
			t.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	// SEE THE NEW RUN METHOD BELOW
	/*
	 * When I was running this application, I was checking its performance in task
	 * manager. It was using about 10% of CPU and peaked at about 20%. Seemed pretty
	 * high, considering the simplicity of this application.
	 */

//	/**
//	 * Runs the update method 60 times a second.
//	 * Runs the render method as much as possible.
//	 */
//	public void run() {
//		// For precision
//		long lastTime = System.nanoTime();
//		long timer = System.currentTimeMillis();
//		final double ns = 1000000000.0 / 60.0;
//		
//		// Init some stuff
//		double delta = 0;
//		int frames = 0;
//		
//		// Main loop
//		while (running) {
//			//System.out.println("MAIN LOOP"); // DEBUG
//			long now = System.nanoTime();
//			delta += (now - lastTime) / ns;
//			lastTime = now;
//			// Runs the update method every 60th of a second
//			while (delta >= 1) {
//				update();
//				delta--;
//			}
//			
//			render();
//			System.out.println("Frames: " + frames);
//			frames++;
//			
//			// TESTING STUFF | DELETE ME, (this if, timer var, and frames var) IF NOT IN USE
//			if (System.currentTimeMillis() - timer > 1000) {
//				timer += 1000;
//				m.setTitle("Project-Minigame | " + frames + " fps");
//				frames = 0;
//			}
//		}
//		
//		stop();
//	}
	
	// A new game loop implementation that is about 80% more efficient.
	/**
	 * Main loop of the application that updates and renders the current state.
	 */
	public void run() {
		long startTime;
		long elapsed;
		long wait;
		double fps;
		int targetTime = 1000 / FPS;
		
		while (running) {
			
			startTime = System.nanoTime();
			update();
			render();
			
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
			fps = 1000000000.0 / (System.nanoTime() - startTime);
			m.setTitle("Project-Minigame | " + (int)fps + " fps");
		}
		
		stop();
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
	
	// Mutator methods
	
	public void setPlayers(ArrayList<Player> players) {
		this.players = players;
	}
	
	public void setState(State state) {
		if (state instanceof BoardState) {
			
		}
		this.curState = state;
		m.updateView();	// we have to update view to main manually!
	}	
	
	public void setTurns(int turns) {
		maxTurns = turns;
	}
	
	// Accessor methods

	public ArrayList<Player> getPlayers() {
		return players;
	}
	
	public BoardState getBoardState() {
		return boardState;
	}
	
	public State getState() {
		return curState;
	}
	
	public Main getMain() {
		return m;
	}
	
	public int getTurn() {
		return turn;
	}
	
}