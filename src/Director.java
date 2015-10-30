import mini.*;

/**
 * 
 * Main class that will keep track of the game
 * You should create this after the player registration is done
 * @author JoJones
 *
 */
public class Director {
	// States of the board
	private final byte START = 0, BOARD = 1, MINIGAME = 2, END = 3;
	private final byte MAX_WEIGHT = 100;
	private final byte MAX_GAMES = 20;
	private byte curPlayer;
	private byte state;
	private int turn;
	private int maxTurns;
	private Minigame[] minigames;
	private byte[] minigameWeight;
	
	/**
	 * 
	 * @param maxTurns - Set the max turns for the game before game over
	 */
	public Director (int maxTurns) {
		this.maxTurns = maxTurns;
		state = START;
		turn = 1;
		curPlayer = 1;
		
		for (byte i = 0; i < MAX_GAMES; i++) {
			minigames[i]= new Test(i);
			minigameWeight[i] = 0;
		}
	}
	
	public void loop() {
		
	}
}
