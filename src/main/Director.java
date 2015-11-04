package main;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Random;

import mini.Minigame;
import mini.Test;
import screen.Board;

// TODO: Make it output the player list sorting to test

/**
 * 
 * Main class that will keep track of the game
 * @author JoJones
 *
 */
public class Director {
	// States of the board
	// I made them bytes to optimize memory usage
	public static final byte START = 0, BOARD = 1, MINIGAME = 2, END = 3;
	private final byte MAX_WEIGHT = 100;
	private final byte MAX_GAMES = 20;
	private byte curPlayer;
	private ArrayList<Player> players;
	private ArrayList<Player> rank;
	private byte state;
	private int turn;
	private int maxTurns;
	private Minigame[] minigames;
	private byte[] minigameWeight;
	private Board board;
	private Random r;
	private Main m;
	private Dice die;
	
	/**
	 * Create a Director object
	 * @param maxTurns - Amount of max turns in the current game
	 * @param players - Players to play in the game
	 */
	public Director (Main m) {
		this.state = START;
		this.turn = 1;
		this.curPlayer = 0;
		this.m = m;
		
		die = new Dice(m.getWidth() / 2 + 32, m.getHeight() / 2 + 32);
		board = new Board();
		r = new Random();
		
		this.players = new ArrayList<Player>();
		
		// Init minigames here
		this.minigames = new Minigame[MAX_GAMES];
		this.minigameWeight = new byte[MAX_WEIGHT];
		for (byte i = 0; i < MAX_GAMES; i++) {
			this.minigames[i]= new Test(i);
			this.minigameWeight[i] = 0;
		}
		
		//loop();
	}
	
	// TODO: Split up the states into separate functions for more clarity
	/**
	 * Main game loop
	 */
	public void loop() {
		if (state == START) {
			// Start stuff
		}
		else {
			System.out.println("State: " + (state==1?"BOARD":state==2?"MINIGAME":"END"));	// TESTING STATEMENT
			if (state != END) {		// If the game is not in the over state AKA ingame
				// Check what state we are in
				if (state == BOARD) {
					for (int i = 0; i < players.size(); i++) {
						// do player stuff here
					}
		
					// Other stuff
					System.out.println("Turn " + turn);	
				}
				else if (state == MINIGAME) {
					// Get a random minigame
					int minigameNum = getRandomMinigame();
					
					System.out.println("Playing minigame: " + minigameNum);
					
					// Play minigame here
					minigames[minigameNum].setRunning(true);
					while (minigames[minigameNum].getRunning()) {
						// minigame loop
						// FIx this, this might break the loop maybe dunno
						minigames[minigameNum].setRunning(false);
					}
					
					// Update weights after minigame has been played
					for (int i = 0; i < MAX_GAMES; i++) {
						minigameWeight[i] -= 10;
					}
					minigameWeight[minigameNum] = MAX_WEIGHT;
				}
				
				// Update game
//				if (curPlayer == players.size() - 1) {
//					curPlayer = 0;
//				}
//				curPlayer++;
				
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
			else {		// If the game is in the over state

			}
		}
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
	
	private void sortPlayerOrder() {
		// Sort  the player array based on turn order
	}
	
	/**
	 * Sorts the rank array by comparing player scores
	 */
	private void sortRank() {
		ArrayList<Integer> t = new ArrayList<Integer>();
		for (int i = 0; i < players.size(); i++) {
			t.add(players.get(i).getScore1());
		}

		// get sorting to work
		players.sort((Comparator<? super Player>) t);
			
		for (int i = 0; i < players.size(); i++) {
			System.out.println(players.get(i).getScore1());
		}
	}
	
	public void setState(byte state) {
		this.state = state;
	}
	
	public void setPlayers(ArrayList<Player> players) {
		this.players = players;
		this.rank = players;
	}
	
	public void setTurns(int turns) {
		maxTurns = turns;
	}
}