package newserver;

import java.util.ArrayList;
import java.util.Random;

import gameobjects.NewPlayer;
import gameobjects.Player;

public class ServerDirector {
	private static final int MAX_PLAYERS = 4;
	private Server server;
	private ArrayList<NewPlayer> players;
	
	public ServerDirector(Server server) {
		this.server = server;
		players = new ArrayList<>();
	}
	
	public void addPlayer(NewPlayer p) {
		if (players.size() < MAX_PLAYERS) {
			players.add(p);
			System.out.println("Player added to server director players!");
		}
	}
	
	public ArrayList<NewPlayer> getPlayers() {
		return players;
	}
	
	// TESTING STUFF OUT. REMOVE THIS LATER PROBABLY
	public void addRandomPlayer() {
		String testName = "test";
		int ID = players.size();	// get ID based off how many players are in array
		int colorNum = PlayerStyles.getInstance().getStyle();
		
		NewPlayer p = new NewPlayer(testName, ID);
		// send message to all clients about adding a player
		
		if (players.size() < MAX_PLAYERS) {
//			System.out.println("Style NUm: " + playerStyles.getStyle());
			addPlayer(p);
			server.echoAll("!addPlayer " + p.getName() + " " + p.getID() + " " + colorNum);	
		}
	}
}
