package mini;

/**
 * A default minigame
 * @author JoJones
 *
 */
public abstract class Minigame {
	private final byte ID;
	private String name;
	private String inst;
	private int[] rankValues;
	private boolean running;
	
	/**
	 * Constructs the minigame
	 * @param id - Sets identifier for a minigame
	 */
	public Minigame(byte id) {
		this.ID = id;
		running = false;
	}
	
	public abstract void loop();
	
	
	public void setRunning(boolean r) {
		running = r;
	}
	
	public boolean getRunning() {
		return running;
	}
	
	/**
	 * @return Name of minigame
	 */
	public byte getID() {
		return ID;
	}
	
	/**
	 * @return Name of minigame
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * @return Instructions of minigame
	 */
	public String getInst() {
		return inst;
	}
	
	/**
	 * @return Rank values of minigame
	 */
	public int[] getRankValues() {
		return rankValues;
	}
}
