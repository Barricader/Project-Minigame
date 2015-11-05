package main;
import java.util.ArrayList;
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
public class NewDirector implements Runnable {
	// States of the board
	public static final byte START = 0, BOARD = 1, MINIGAME = 2, END = 3, INIT = 4;
	private ArrayList<Player> players;
	private byte state;
	private int turn;
	private int maxTurns;
	private Main m;
	
	/**
	 * Create a Director object
	 * @param maxTurns - Amount of max turns in the current game
	 * @param players - Players to play in the game
	 */
	public NewDirector (Main m) {
		this.state = START;
		this.turn = 1;
		this.m = m;
		
		this.players = new ArrayList<Player>();
	}
	
	public void update() {
		
	}
	
	public void render() {
		
	}
	
	public void setState(byte state) {
		this.state = state;
	}
	
	public void setPlayers(ArrayList<Player> players) {
		this.players = players;
	}
	
	public ArrayList<Player> getPlayers() {
		return players;
	}
	
	public void setTurns(int turns) {
		maxTurns = turns;
	}

	public void run() {
		
	}
}