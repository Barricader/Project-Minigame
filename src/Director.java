/**
 * 
 * Main class that will keep track of the game
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
	
	public Director (int maxTurns) {
		this.maxTurns = maxTurns;
		state = START;
		turn = 1;
		curPlayer = 1;
		
		for (byte i = 0; i < MAX_GAMES; i++) {
			minigames[i]= new Minigame(i);
			minigameWeight[i] = 0;
		}
	}
}
