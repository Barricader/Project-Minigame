package newserver;

import java.util.ArrayList;

import org.json.simple.JSONObject;

import gameobjects.NewPlayer;
import util.NewJSONObject;

@SuppressWarnings({ "static-access", "unchecked" })	// hide stupid warnings!!
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
	
	/**
	 * Adds a player to the server array of players, from a JSONObject. Checks
	 * to make sure that the player is valid (i.e. no duplicate name and we
	 * haven't reached the max player limit.)
	 * @param obj
	 */
	public void addPlayer(JSONObject obj) {
		NewPlayer p = NewPlayer.fromJSON(obj);
		NewJSONObject out = null;
		JSONObject error = new JSONObject();	// for any errors that might occur
		if (players.size() < MAX_PLAYERS) {
			if (!checkDuplicate(p)) {
				p.setID(players.size());
				p.style(PlayerStyles.getInstance().getStyle());

				players.add(p);
				echoAllPlayers();	// update to all players
			} else {
				out = new NewJSONObject(p.getID(), Keys.Commands.ERROR);	// duplicate error
				error.put(Keys.ERROR_TITLE, "Duplicate name!");
				error.put(Keys.ERROR_MSG, "Another player already exists with name: " 
						+ p.getName() + ". Try another name!");
				out.put(Keys.Commands.ERROR, error);
				server.echoAll(out);
			}
		} else {
			out = new NewJSONObject(p.getID(), Keys.Commands.ERROR);	// max player limit error
			error.put(Keys.ERROR_TITLE, "Player refused!");
			error.put(Keys.ERROR_MSG, "Max player limit reached!");
			out.put(Keys.Commands.ERROR, error);
			server.echoAll(out);
		}
	}
	
	/**
	 * Echoes all players that exist in the array, so that newly connected
	 * clients receive them.
	 */
	private void echoAllPlayers() {
		for (NewPlayer p : players) {
			NewJSONObject out = new NewJSONObject(p.getID(), Keys.Commands.ADD_PLAYER);
			out = new NewJSONObject(p.getID(), Keys.Commands.ADD_PLAYER);
			out.put(Keys.ID, p.getID());
			out.put(Keys.PLAYER, p.toJSONObject());	
			server.echoAll(out);
		}
	}
	
	/**
	 * Checks the specified player for duplicate names against the player
	 * array.
	 * @param p - Player name to check
	 * @return true if duplicate found, false otherwise
	 */
	private boolean checkDuplicate(NewPlayer p) {
		boolean duplicateName = false;
		for (int i = 0; i < players.size(); i++) {
			if (players.get(i).getName().equals(p.getName())) {
				duplicateName = true;
			}
		}
		System.out.println("duplicate? " + duplicateName);
		return duplicateName;
	}
	
	public ArrayList<NewPlayer> getPlayers() {
		return players;
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
		k.put("playerID", id);
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
