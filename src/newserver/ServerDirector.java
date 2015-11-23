package newserver;

import java.util.ArrayList;
import java.util.Random;

import gameobjects.Player;

public class ServerDirector {
	private static final int MAX_PLAYERS = 4;
	private Server server;
	private ArrayList<Player> players;
	
	public ServerDirector(Server server) {
		this.server = server;
		players = new ArrayList<>();
	}
	
	public void addPlayer(Player p) {
		if (players.size() < MAX_PLAYERS) {
			players.add(p);
			System.out.println("Player added to server director players!");
		}
	}
	
	public ArrayList<Player> getPlayers() {
		return players;
	}
	
	// TESTING STUFF OUT. REMOVE THIS LATER PROBABLY
	public void addRandomPlayer() {
		Random rng = new Random();
		String testName = "test";
		int randColor = rng.nextInt(8) + 1;
		
		Player p = new Player(testName, randColor);
		// send message to all clients about adding a player
		
		if (players.size() < MAX_PLAYERS) {
			addPlayer(p);
			server.echoAll("!addPlayer " + p.getName() + " " + p.getColorNum());	
		}
	}
}
