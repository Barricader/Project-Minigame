/**
 * 
 * @author JoJones
 * Main class that will keep track of the game
 *
 */
public class Director {
	private final byte START = 0, BOARD = 1, MINIGAME = 2, END = 3;
	private int state = 0;
	private int turn = 0;
	
	public Director (int x) {
		state = START;
		turn = 0;
	}
}
