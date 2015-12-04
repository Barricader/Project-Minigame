package newserver;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import javax.swing.Timer;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import gameobjects.NewPlayer;
import util.GameUtils;
import util.Keys;
import util.MiniGames;
import util.NewJSONObject;
import util.PlayerStyles;

@SuppressWarnings({ "static-access", "unchecked" })	// hide stupid warnings!!
public class ServerDirector {
	public static final int BOARD = 0;
	public static final int MINIGAME = 1;
	
	private static final int MAX_PLAYERS = 4;
	private static final int WAIT_TIME = 3;	// TODO change back to 20 secs
	private int timeLeft = WAIT_TIME;	// time remaining
	private Server server;
	private ConcurrentHashMap<String, NewPlayer> players;	// thread safe!
	private ConcurrentHashMap<String, NewPlayer> rolledPlayers;	// players that have rolled;
	private NewPlayer activePlayer;		// we will probably need this. Haven't used it yet though.
	private String curMini = "null";
	private String[] nameMinis = MiniGames.names;
	private List<NewPlayer> leaderboard;
	
	private HashMap<Integer, NewPlayer> temp;
	
	private int lastMini = -1;
	private int stopped;
	private int over;
	private int turnCount;	// how many turns are we in?
	
	private Timer timer;	// timer for controlling events
	
	public ServerDirector(Server server) {
		this.server = server;
		players = new ConcurrentHashMap<>();
		rolledPlayers = new ConcurrentHashMap<>();
		temp = new HashMap<>();

		stopped = 0;
		turnCount = 0;
		leaderboard = Collections.synchronizedList(new ArrayList<NewPlayer>());
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
				players.put(p.getName(), p);
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
		checkCountdown();
	}
	
	/**
	 * Checks the countdown for starting the game.
	 */
	private void checkCountdown() {
		NewJSONObject obj = new NewJSONObject(-1, Keys.Commands.TIMER);
		JSONObject timerObj = new JSONObject();
		if (players.size() >= 2) {
			timeLeft = WAIT_TIME;
			GameUtils.resetTimer(timer);
			
			if (players.size() == 4) {
				timeLeft = 5;	// shorten time, we have reached player limit
			}
			timer = new Timer(1000, e -> {
				
				if (timeLeft == 0) {
					timer.stop();
					System.out.println("OK. CHANGE ALL CLIENTS STATE TO BOARD!!!!");
					changeClientState(BOARD);
				} else {
					timeLeft--;
					// create timer packet and send to all clients.
					timerObj.put(Keys.TIME, timeLeft);
					obj.put(Keys.Commands.TIMER, timerObj);
					server.echoAll(obj);
				}
		
			});
			timer.start();	
		} else {
			GameUtils.resetTimer(timer);
			timerObj.put(Keys.TIME, "reset");
			obj.put(Keys.Commands.TIMER, timerObj);
			server.echoAll(obj);
		}
	}
	
	/**
	 * Sends a command to client to change to a new state.
	 * @param state - Name of state to change to.
	 */
	private void changeClientState(int state) {
		NewJSONObject obj = new NewJSONObject(-1, Keys.Commands.STATE_UPDATE);
		obj.put(Keys.STATE, state);
//		JSONObject stateObj = new JSONObject();
//		stateObj.put(Keys.STATE, state);
		server.echoAll(obj);
		nextPlayer();	// assign next player
	}
	
	/**
	 * Removes a player, and resets the countdown timer.
	 * @param obj - JSONObject containing the player to remove.
	 */
	public void removePlayer(JSONObject obj) {
		System.out.println("should be removing player from server!");
		NewPlayer p = NewPlayer.fromJSON(obj);
		NewJSONObject out = null;
		
		if (players.containsKey(p.getName())) {
			players.remove(p.getName());
			// echo to other clients to remove player!
			out = new NewJSONObject(p.getID(), Keys.Commands.REM_PLAYER);
			out.put(Keys.PLAYER, p.toJSONObject());
			server.echoAll(out);
		}
		checkCountdown();
	}
	
	/**
	 * Updates a player from a JSONObject.
	 * @param obj - JSONObject containing player
	 */
	public void updatePlayer(JSONObject obj) {
		NewPlayer p = NewPlayer.fromJSON(obj);
		players.put(p.getName(), p);
		System.out.println("SERVER UPDATE PLAYER RECEIVED: " + p.toJSONObject().toJSONString());
		// echo back
		NewJSONObject update = new NewJSONObject(p.getID(), Keys.Commands.UPDATE);
		update.put(Keys.PLAYER, p.toJSONObject());
		server.echoAll(update);
	}
	
	/**
	 * Echoes all players that exist in the array, so that newly connected
	 * clients receive them.
	 */
	private void echoAllPlayers() {
		for (NewPlayer p : players.values()) {
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
		return players.containsKey(p.getName());
	}
	
	public ConcurrentHashMap<String, NewPlayer> getPlayers() {
		return players;
	}
	
	/**
	 * Selects the next active player and sends a request for the player 
	 * to roll the dice.
	 */
	public void nextPlayer() {
		for (NewPlayer p : players.values()) {
			if (!rolledPlayers.containsKey(p.getName())) {
				rolledPlayers.put(p.getName(), p);
				activePlayer = p;
				activePlayer.setActive(true);
				NewJSONObject obj = new NewJSONObject(activePlayer.getID(), Keys.Commands.ROLL);
				obj.put(Keys.PLAYER, activePlayer.toJSONObject());
				server.echoAll(obj);
				break;
			}
		}
	}
	
	/**
	 * Resets players back to not having rolled, typically after a new round.
	 */
	private void reset() {
		activePlayer.setHasRolled(false);
		rolledPlayers.clear();
	}
	
	/**
	 * Sends a request to all clients to animate and move the player.
	 * @param in - Player JSONObject that is the active player to move
	 */
	public void movePlayer(JSONObject in) {
		NewPlayer p = NewPlayer.fromJSON(in);
		p.setLastRoll((int)in.get(Keys.ROLL_AMT));
		p.setHasRolled(true);
		NewJSONObject obj = new NewJSONObject(p.getID(), Keys.Commands.MOVE);
		obj.put(Keys.PLAYER, p.toJSONObject());
		obj.put(Keys.ROLL_AMT, p.getLastRoll());
		players.put(p.getName(), p);	// update player map
		server.echoAll(obj);
	}
	
//	/**
//	 * Changes the state and lets the clients know
//	 * @param state - State to change to
//	 */
//	public void changeState(int state) {
//		NewJSONObject k = new NewJSONObject(-1, Keys.Commands.STATE_UPDATE);
//		k.put("state", state);
//		if (state == BOARD) {
//			// Sort the HashMap by values and sort the players into the leaderboard
//			if (!temp.isEmpty()) {
//				System.out.println("temp shouldn't be empty!");
//				List<Integer> sortedKeys = new ArrayList<Integer>(temp.keySet());
//				Collections.sort(sortedKeys);
//				leaderboard.addAll(temp.values());
//			}
//			
//			// Create a JSON array in the JSONObject that we want to send
//			JSONArray players = new JSONArray();
//			for (int i = 0; i < leaderboard.size(); i++) {
//				JSONObject temp = new JSONObject();
//				String name = leaderboard.get(i).getName();
//				temp.put("name", name);
//				temp.put("place", i);		// Place is not necessary, just for testing
//				players.add(temp);
//			}
//			
//			k.put("leaderboard", players);
//		}
//		else if (state == MINIGAME) {
//			// Choose a random minigame that wasn't the last one played
//			int ranNum = lastMini;
//			while (ranNum == lastMini) {
//				ranNum = new Random().nextInt(nameMinis.length);
//				curMini = nameMinis[ranNum];
//			}
////			k.put("mini", nameMinis[ranNum]);
//			k.put("mini", "rps");
//			lastMini = ranNum;
//		}
//		server.echoAll(k);
//		leaderboard.clear();
//		temp.clear();
//	}
	
	public void changeState(int state) {
		NewJSONObject k = new NewJSONObject(-1, Keys.Commands.STATE_UPDATE);
		k.put(Keys.STATE, state);
		
		if (state == BOARD) {
			JSONArray pArray = createLeaderBoard();
			k.put("leaderboard", pArray);
		} else if (state == MINIGAME) {
			int ranNum = lastMini;
			while (ranNum == lastMini) {
				ranNum = new Random().nextInt(nameMinis.length);
				curMini = nameMinis[ranNum];
			}
//			k.put("mini", nameMinis[ranNum]);
			k.put("mini", "rps");	//TODO change back to above. This locks on RPS game!
			lastMini = ranNum;
		}
		server.echoAll(k);
	}
	
	private JSONArray createLeaderBoard() {
		JSONArray pArray = new JSONArray();
		NewPlayer[] temp = GameUtils.mapToArray(players, NewPlayer.class);
		GameUtils.sortPlayersByName(temp);
		for (NewPlayer p : temp) {
			pArray.add(p);
		}
		return pArray;
	}
	
	public void updateMinigame(JSONObject in) {
		String name = (String) in.get("name");
		if (name.equals(MiniGames.names[0])) {
			handleEnter(in);
		}
		else if (name.equals(MiniGames.names[1])) {
			handleKeyFinder(in);
		}
		else if (name.equals(MiniGames.names[2])) {
			handlePaint(in);
		}
		else if (name.equals(MiniGames.names[3])) {
			handlePong(in);
		}
		else if (name.equals(MiniGames.names[4])) {
			handleRPS(in);
		}
		else {
			// Not a known minigame, send error
			handleError();
		}
	}
	
	private void handleEnter(JSONObject in) {
		leaderboard.add(players.get((String) in.get(Keys.PLAYER_NAME)));
	}
	
	private void handleKeyFinder(JSONObject in) {
		leaderboard.add(players.get((String) in.get(Keys.PLAYER_NAME)));	// CHANGEME
	}
	
	private void handlePaint(JSONObject in) {
		leaderboard.add(players.get((String) in.get(Keys.PLAYER_NAME)));	// CHANGEME
	}
	
	private void handlePong(JSONObject in) {
		leaderboard.add(players.get((String) in.get(Keys.PLAYER_NAME)));	// CHANGEME
	}
	
	private void handleRPS(JSONObject in) {
		temp.put((Integer) in.get(Keys.WINS), players.get((String) in.get(Keys.PLAYER_NAME)));
	}
	
	private void handleError() {
		System.out.println("ERROR: Not a valid minigame");
	}
	
	/**
	 * Called when a client sends a stopped command. If all players have finished
	 * animating the current player, we need to move onto the next player and/or
	 * change to a mini-game state.
	 */
	public void isStopped() {
		stopped++;
		if (stopped == players.size()) {
			stopped = 0;
			activePlayer.setHasRolled(true);			
			if (rolledPlayers.size() == players.size()) {
				reset();
				changeState(MINIGAME);
			}
			nextPlayer();
		}
	}
	
	/**
	 * Called when a client is finished with a minigame. If all players have finished
	 * the minigame, we need to move onto the board state
	 */
	public void isMinigameOver() {
		over++;
		if (over == players.size()) {
			over = 0;
			changeState(BOARD);
		}
	}
}
