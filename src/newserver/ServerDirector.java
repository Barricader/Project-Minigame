package newserver;

import java.util.ArrayList;
import java.util.Random;

import gameobjects.NewPlayer;
import gameobjects.Player;
import util.NewJSONObject;

public class ServerDirector {
	private static final int MAX_PLAYERS = 4;
	private Server server;
	private ArrayList<NewPlayer> players;
	
	private int activeIndex;
	private int stopped;
	
	public ServerDirector(Server server) {
		this.server = server;
		players = new ArrayList<>();
		
		activeIndex = 0;
		stopped = 0;
		
		setActive();
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
	
//	public void setActive(int id) {
//		NewJSONObject k = new NewJSONObject(-1, "active");
//		k.put("playerID", id);
//		server.echoAll(k);
//	}
	
	public void setActive() {
		NewJSONObject k = new NewJSONObject(-1, "active");
		k.put("playerID", activeIndex);
		server.echoAll(k);
	}
	
	public void movePlayer(int id, int roll) {
		NewJSONObject k = new NewJSONObject(-1, "update");
		k.put("roll", roll);
		server.echoAll(k);
	}
	
	public void isStopped() {
		stopped++;
		if (stopped == players.size()) {
			// DO STUFF
			if (activeIndex == players.size()) {
				activeIndex = 0;
				setActive();
			}
			else {
				activeIndex++;
				setActive();
			}
			stopped = 0;
		}
	}
}
