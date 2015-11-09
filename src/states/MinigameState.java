package states;

import java.util.Random;

import main.NewDirector;
import mini.Minigame;

public class MinigameState extends State {
	private static final long serialVersionUID = 1L;
	private final byte MAX_WEIGHT = 100;
	private final byte MAX_GAMES = 20;
	private Minigame[] minigames;
	private byte[] minigameWeight;
	private Random r;
	private int curMinigame;
	private boolean chosen;
	
	public MinigameState(NewDirector director) {
		super(director);
		r = new Random();
		curMinigame = 0;
		chosen = false;
	}

	public void update() {
		if (!chosen) {
			// Get a random minigame
			curMinigame = getRandomMinigame();
			
			System.out.println("Playing minigame: " + curMinigame);
			chosen = true;
		}
		
		// Play minigame here
		minigames[curMinigame].setRunning(true);
		while (minigames[curMinigame].getRunning()) {
			// minigame loop
			
			minigames[curMinigame].update();
			
			// DELETE THIS
			minigames[curMinigame].setRunning(false);
		}
		
		// Update weights after minigame has been played
		for (int i = 0; i < MAX_GAMES; i++) {
			minigameWeight[i] -= 10;
		}
		minigameWeight[curMinigame] = MAX_WEIGHT;
		chosen = false;
		//director.setState(director.);
		System.out.println("Minigame State Updating!");
	}

	public void render() {
		if (curMinigame != -1) {
			minigames[curMinigame].render();
		}
		
		// DRAW STUFF HERE!
		System.out.println("Minigame State Rendering");
		
		repaint();
		
	}
	
	/**
	 * Gets a random minigame. The randomness is based on a weight
	 * depending on when the minigame was last played.
	 */
	private int getRandomMinigame() {
		boolean picked = false;
		byte curMinigame = -1;
		while (!picked) {
			curMinigame = (byte)r.nextInt(MAX_GAMES);
			// Weight check
			if (minigameWeight[curMinigame] <= 50) {
				double defaultChance = 100 / MAX_GAMES;
				// Change this is max games changes
				// REDO this so that everything has an equal chance disregarding weight
				defaultChance -= 0.1 * minigameWeight[curMinigame];
				if ((double)r.nextInt(100) < defaultChance) {
					picked = true;
				}
			}
		}
		
		return curMinigame;
	}

}
