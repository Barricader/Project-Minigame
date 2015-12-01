package main;
import java.util.ArrayList;

import states.BoardState;
import states.StartState;
import states.State;

/**
 * 
 * Main class that will keep track of the game
 * @author David Kramer
 * @author JoJones
 *
 */
public class Director implements Runnable {
	public static final int MAX_PLAYERS = 4;	// max players allowed
	public static final int FPS = 60;
	
	// States of the board
	private ArrayList<Player> players;
	private BoardState boardState;
	private State mgState;
	private State curState;
	private int turn;
	private int maxTurns;
	private int turnCount;
	private int turnsLeft;
	private Main m;
	private Thread t;
	private boolean running;
	
	/**
	 * Create a NewDirector object that will hand stats for the whole game
	 * @param m - An instance of Main to use in NewDirector
	 */
	public Director (Main m) {
		this.m = m;
		this.curState = new StartState(this);
		this.boardState = new BoardState(this);
		//this.mgState = new MinigameState(this);
		this.turn = 1;
		//this.m = m;
		
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
	
	public void minusTurn() {
		turnsLeft--;
		if (turnsLeft == 0) {
			System.out.println("NO MORE TURNS. GAME SHOULD GO TO ENDSTATE!");
		}
	}
	
	public void setTurnCount(int turnCount) {
		this.turnCount = turnCount;
		this.turnsLeft = turnCount;	// initial turns left
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
	
	public int maxTurns() {
		return maxTurns;
	}
	
	public int getTurn() {
		return turn;
	}
	
	public int getTurnCount() {
		return turnCount;
	}
	
	public int getTurnsLeft() {
		return turnsLeft;
	}
	
}