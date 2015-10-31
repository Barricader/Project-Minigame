import mini.Minigame;
import mini.Test;

/**
 * 
 * Main class that will keep track of the game
 * @author JoJones
 *
 */
public class Director {
	// States of the board
	// I made them bytes to optimize memory usage
	private final byte START = 0, BOARD = 1, MINIGAME = 2, END = 3;
	private final byte MAX_WEIGHT = 100;
	private final byte MAX_GAMES = 20;
	private byte curPlayer;
	private Player[] players;
	private byte state;
	private int turn;
	private int maxTurns;
	private Minigame[] minigames;
	private byte[] minigameWeight;
	private Board board;
	
	/**
	 * Create a Director object
	 * @param maxTurns - Amount of max turns in the current game
	 * @param players - Players to play in the game
	 */
	public Director (int maxTurns, byte players) {
		this.maxTurns = maxTurns;
		this.state = START;
		this.turn = 1;
		this.curPlayer = 1;
		
		board = new Board();
		
		// Init players here
		for (byte i = 0; i < players; i++) {
			this.players[i] = new Player();
		}
		
		// Init minigames here
		for (byte i = 0; i < MAX_GAMES; i++) {
			this.minigames[i]= new Test(i);
			this.minigameWeight[i] = 0;
		}
		
		loop();
	}
	
	/**
	 * Main game loop
	 */
	public void loop() {
		while (state != END) {
			// Main game loop here
			
			if (state == BOARD) {
				
			}
			else if (state == MINIGAME) {
				
			}
			
			if (curPlayer == players.length - 1) {
				curPlayer = 0;
			}
			curPlayer++;
			
			if (state == BOARD) {
				state = MINIGAME;
			}
			else {
				state = BOARD;
			}
		}
	}
}
