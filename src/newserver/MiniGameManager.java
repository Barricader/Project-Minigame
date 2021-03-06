package newserver;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import util.GameUtils;
import util.Keys;
import util.MiniGames;

/**
 * This class is responsible for handling the various mini games, when the
 * ServerDirector changes states from Board to Mini. This will keep track of
 * player's progress and win counts for various mini games and has the ability
 * to send out JSON leaderboard packets, to update clients of their score.
 * @author David Kramer
 *
 */
public class MiniGameManager {
	private ServerDirector serverDir;
	private Map<String, Action> actionMap;
	private int over;	// count of clients finished with active mini game state
	private JSONObject miniObj;	// input received for mini game
	
	private int lastMini = -1;
	private List<String> leaderboard;
	private Map<String, Integer> temp;	// holds win values from mini-games
	private int index = -1;
	
	public MiniGameManager(ServerDirector serverDir) {
		this.serverDir = serverDir;
		initActionMap();
		leaderboard = Collections.synchronizedList(new ArrayList<String>());
		temp = new HashMap<>();
	}
	
	/**
	 * Initializes the action map which routes the various mini game action updates
	 * to their respective method. Depending on the type of mini-game update we
	 * receive based on the name, the correct handle method will be called.
	 */
	private void initActionMap() {
		actionMap = new HashMap<>();
		actionMap.put(MiniGames.names[0], () -> handleEnter(miniObj));
		actionMap.put(MiniGames.names[1], () -> handlePong(miniObj));
		actionMap.put(MiniGames.names[2], () -> handleRPS(miniObj));
	}
	
	/**
	 * Handles receiving a minigame JSONObject and executes the appropriate
	 * minigame handle method if the key is valid.
	 * @param in - JSONObject containing minigame stuff
	 */
	public void handle(JSONObject in) {
		String name = (String) in.get(Keys.NAME);
		
		if (actionMap.containsKey(name)) {
			miniObj = in;
			actionMap.get(name).execute();
		}
	}
	
	/**
	 * Called when a client is finished with a minigame. If all players have finished
	 * the minigame, we need to move onto the board state
	 */
	public void isMinigameOver() {
		over++;
		if (over == serverDir.getPlayers().size()) {
			over = 0;
			serverDir.changeState(ServerDirector.BOARD);
		}
	}
	
	/**
	 * Generates a random mini game, that is not equal to the last mini game that
	 * we have just played.
	 * @return - A string containing the name of one of the available mini games.
	 */
	public String randMini() {
		int ranNum = lastMini;
		while (ranNum == lastMini) {
			ranNum = GameUtils.random.nextInt(MiniGames.names.length);
		}
		lastMini = ranNum;
		
		index++;
		return MiniGames.names[index % 3];
	}
	
	/**
	 * Creates an array of all players and their win count for the leaderboard
	 * packet update.
	 * @return - JSONArray containing leaderboard wins by player name.
	 */
	@SuppressWarnings("unchecked")
	public JSONArray getJSONLeaderboard() {
		if (!temp.isEmpty()) {
			//System.out.println("\n\n"+temp);
			TempComparator tc = new TempComparator(temp);
			TreeMap<String, Integer> sorted = new TreeMap<String, Integer>(tc);
			sorted.putAll(temp);
			//System.out.println(sorted);
			leaderboard.addAll(sorted.keySet());
		}
		
		JSONArray players = new JSONArray();
		for (int i = 0; i < leaderboard.size(); i++) {
			JSONObject k = new JSONObject();
			String name = leaderboard.get(i);
			k.put("name", name);
			players.add(k);
		}
		return players;
	}
	
	/**
	 * Clears out the leaderboard stuff.
	 */
	public void clearLeaderboard() {
		leaderboard.clear();
		temp.clear();
	}
	
	private void handleEnter(JSONObject obj) {
		// update leaderboard
		String pName = (String) obj.get(Keys.PLAYER_NAME);
		leaderboard.add(pName);
	}

	private void handlePong(JSONObject obj) {
		serverDir.getServer().echoAll(obj);
	}
	
	private void handleRPS(JSONObject obj) {
		String pName = (String) obj.get(Keys.PLAYER_NAME);
		int wins = (int) obj.get(Keys.WINS);
		System.out.println("wins for : " + pName + ", is " + wins);
		temp.put(pName, wins);
	}
	
	public int getLastMini() {
		return lastMini;
	}
	
	public ServerDirector getServerDir() {
		return serverDir;
	}
	
	class TempComparator implements Comparator<String> {
		Map<String, Integer> base;
		
		public TempComparator(Map<String, Integer> base) {
			this.base = base;
		}

		public int compare(String o1, String o2) {
			if ((Integer)base.get(o1) <= (Integer)base.get(o2)) {
				return 1;
			}
			else {
				return -1;
			}
		}
		
	}
}
