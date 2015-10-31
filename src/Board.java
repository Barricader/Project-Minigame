/**
 * Holds the board stuff
 * @author JoJones
 *
 */
public class Board {
	private final byte MAX_TILES = 80;
	private Tile[] tiles;
	/**
	 * Create a board
	 */
	public Board() {
		// Init tiles
		for (int i = 0; i < MAX_TILES; i++) {
			tiles[i] = new Tile();
		}
	}
}
