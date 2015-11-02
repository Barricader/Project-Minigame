package main;
import java.util.ArrayList;
import java.util.Random;

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
	private ArrayList<Player> players;
	private byte state;
	private int turn;
	private int maxTurns;
	private Minigame[] minigames;
	private byte[] minigameWeight;
	private Board board;
	private Random r;
	
	/**
	 * Create a Director object
	 * @param maxTurns - Amount of max turns in the current game
	 * @param players - Players to play in the game
	 */
	public Director (Main m) {
		this.state = START;
		this.turn = 1;
		this.curPlayer = 0;
		
		board = new Board();
		r = new Random();
		
		this.players = new ArrayList<Player>();
		
		// Init players here
//		this.players = new Player[players];
//		for (byte i = 0; i < players; i++) {
//			this.players[i] = new Player();
//		}
		
		// Init minigames here
		this.minigames = new Minigame[MAX_GAMES];
		this.minigameWeight = new byte[MAX_WEIGHT];
		for (byte i = 0; i < MAX_GAMES; i++) {
			this.minigames[i]= new Test(i);
			this.minigameWeight[i] = 0;
		}
		
		//loop();
	}
	
	/**
	 * Main game loop
	 */
	public void loop() {
		if (state == START) {
			// Start stuff
		}
		while (state != END) {
			// Main game loop here
			
			// Check what state we are in
			if (state == BOARD) {
				boolean isTurn = true;
				while (isTurn) {
					// Player does stuff on board like move
					isTurn = false;
				}
				
				// Other stuff
			}
			else if (state == MINIGAME) {
				// Check if minigame has been played recently
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
				
				System.out.println(curMinigame);
				
				// Play minigame here
				
				// Update weights after minigame has been played
				for (int i = 0; i < MAX_GAMES; i++) {
					minigameWeight[i] -= 10;
				}
				minigameWeight[curMinigame] = MAX_WEIGHT;
			}
			
			// Update game
			if (curPlayer == players.size() - 1) {
				curPlayer = 0;
			}
			curPlayer++;
			
			if (state == BOARD) {
				state = MINIGAME;
			}
			else {
				state = BOARD;
				turn++;
			}
			
			// Check turn, if it is over max turns, the game is over
			if (turn > maxTurns) {
				state = END;
			}
		}
	}
	
	public void setState(byte state) {
		this.state = state;
	}
	
	public void setPlayers(ArrayList<Player> players) {
		this.players = players;
		for (byte i = 0; i < players.size(); i++) {		
			this.players.add(new Player());
			this.players.set(i, players.get(i));
		}
	}
	
	public void setTurns(int turns) {
		maxTurns = turns;
	}
}