package newserver;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import gameobjects.NewPlayer;
import util.GameUtils;
import util.Keys;
import util.MiniGames;

public class MiniGameManager {
	private NewServerDirector serverDir;
	private Map<String, Action> actionMap;
	private int over;	// count of clients finished with active mini game state
	private JSONObject miniObj;	// input received for mini game
	
	private int lastMini = -1;
	private List<NewPlayer> leaderboard;
	private Map<Integer, NewPlayer> temp;	// holds win values from mini-games
	
	public MiniGameManager(NewServerDirector serverDir) {
		this.serverDir = serverDir;
		initActionMap();
		leaderboard = Collections.synchronizedList(new ArrayList<NewPlayer>());
		temp = new HashMap<>();
	}
	
	private void initActionMap() {
		actionMap = new HashMap<>();
		actionMap.put(MiniGames.names[0], () -> handleEnter(miniObj));
		actionMap.put(MiniGames.names[1], () -> handleKeyFinder(miniObj));
		actionMap.put(MiniGames.names[2], () -> handlePaint(miniObj));
		actionMap.put(MiniGames.names[3], () -> handlePong(miniObj));
		actionMap.put(MiniGames.names[4], () -> handleRPS(miniObj));
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
			
			// update leaderboard
			String pName = (String) in.get(Keys.PLAYER_NAME);
			leaderboard.add(serverDir.getPlayers().get(pName));
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
			serverDir.changeState(NewServerDirector.BOARD);
		}
	}
	
	public String randMini() {
		int ranNum = lastMini;
		while (ranNum == lastMini) {
			ranNum = GameUtils.random.nextInt(MiniGames.names.length);
		}
		lastMini = ranNum;
		return MiniGames.names[lastMini];
	}
	
	public JSONArray getJSONLeaderboard() {
		if (!temp.isEmpty()) {
			List<Integer> sortedKeys = new ArrayList<>(temp.keySet());
			Collections.sort(sortedKeys);
			leaderboard.addAll(temp.values());
		}
		
		JSONArray players = new JSONArray();
		for (int i = 0; i < leaderboard.size(); i++) {
			JSONObject k = new JSONObject();
			String name = leaderboard.get(i).getName();
			System.out.println("leader json array: " + name + ", wins-> " + temp.get(name));
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
		
	}
	
	private void handleKeyFinder(JSONObject obj) {

	}

	private void handlePaint(JSONObject obj) {

	}

	private void handlePong(JSONObject obj) {

	}
	
	private void handleRPS(JSONObject obj) {
		String pName = (String) obj.get(Keys.PLAYER_NAME);
		int wins = (int) obj.get(Keys.WINS);
		System.out.println("wins for : " + pName + ", is " + wins);
		temp.put(wins, serverDir.getPlayers().get(pName));
	}
}
