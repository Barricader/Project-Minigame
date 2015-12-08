package newserver;

import java.util.concurrent.ConcurrentHashMap;

import javax.swing.Timer;

import org.json.simple.JSONObject;

import gameobjects.NewPlayer;
import util.GameUtils;
import util.Keys;
import util.NewJSONObject;
import util.PlayerStyles;

/**
 * The ServerDirector class is responsible for managing players and alternating
 * between 2 states: Board and MiniGame. This class keeps track of players who
 * have rolled for the current round. This class also handles adding / removing
 * players of the game.
 * @author David Kramer
 *
 */
@SuppressWarnings({ "static-access", "unchecked" })	// hide stupid warnings!!
public class ServerDirector {
	/* Game States */
	public static final int BOARD = 0;
	public static final int MINIGAME = 1;
	
	private static final int MAX_PLAYERS = 4;
	private static final int WAIT_TIME = 3;	// countdown time, until we start game!
	private Server server;
	private ConcurrentHashMap<String, NewPlayer> players;	// thread safe!
	private ConcurrentHashMap<String, NewPlayer> rolledPlayers;
	private NewPlayer activePlayer;
	private Timer timer;	// timer for controlling events
	
	private MiniGameManager miniMngr;	// manager to deal with mini-game stuff
	
	private boolean hasStarted = false;	// have we started playing game yet?
	private int timeLeft = WAIT_TIME;
	private int stopCount;
	private int turnCount;
	private int over;	// count of clients finished with active mini game state
	
	/**
	 * Constructs a new ServerDirector with a connection to the main server.
	 * @param server - Main server that we're connected to
	 */
	public ServerDirector(Server server) {
		this.server = server;
		players = new ConcurrentHashMap<>();
		rolledPlayers = new ConcurrentHashMap<>();
		miniMngr = new MiniGameManager(this);
	}
	
	/**
	 * Adds a player to the player map, from a JSONObject.
	 * @param obj - JSONObject containing player to add
	 */
	public void addPlayer(JSONObject obj) {
		NewPlayer p = NewPlayer.fromJSON(obj);
		
		if (players.size() < MAX_PLAYERS) {
			if (!checkDuplicate(p)) {
				p.setID(players.size());
				p.style(PlayerStyles.getInstance().getStyle());
				players.put(p.getName(), p);
				echoAllPlayers();	// update to all players
				server.getServerApp().getListPanel().getListModel().addElement(p.getName());
			} else {
				// TODO duplicate error!
			}
		} else {
			// TODO max player error!
		}
		checkCountdown();
	}
	
	/**
	 * Removes a player from the player map, from a JSONObject.
	 * @param obj - JSONObject containing player to remove
	 */
	public void removePlayer(JSONObject obj) {
		NewPlayer p = NewPlayer.fromJSON(obj);
		removePlayer(p);
		PlayerStyles.taken[p.getStyleID()] = false;		// style is available again
	}
	
	/**
	 * Removes a player from the map, from a Player object itself.
	 * @param p - Player to remove (if it exists in map by the name)
	 */
	public void removePlayer(NewPlayer p) {
		NewJSONObject out = null;
		
		if (players.containsKey(p.getName())) {
			players.remove(p.getName());
			// echo to other clients to remove player!
			out = new NewJSONObject(p.getID(), Keys.Commands.REM_PLAYER);
			out.put(Keys.PLAYER, p.toJSONObject());
			server.echoAll(out);
			
			// if the active player was what we just removed, go to the next player!
			if (activePlayer != null) {
				if (activePlayer.getName().equals(p.getName())) {
					nextPlayer();
				}
			}
			server.getServerApp().getListPanel().updateList();	// update list with players still here!
		}
		checkCountdown();
	}
	
	/**
	 * Sends a move command to all clients to request that they animate
	 * a player.
	 * @param obj - JSONObject containing a player
	 */
	public void movePlayer(JSONObject obj) {
		NewPlayer p = NewPlayer.fromJSON(obj);
		p.setLastRoll((int)obj.get(Keys.ROLL_AMT));
		p.setHasRolled(true);
		NewJSONObject out = new NewJSONObject(p.getID(), Keys.Commands.MOVE);
		out.put(Keys.PLAYER, p.toJSONObject());
		out.put(Keys.ROLL_AMT, p.getLastRoll());
		players.put(p.getName(), p);	// update player map!
		//System.out.println("should be echoing to move players: " + out.toJSONString());
		server.echoAll(out);
	}
	
	/**
	 * Advances to the next player which hasn't rolled recently.
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
	 * Changes between specified state which is either BOARD or MINIGAME. A
	 * mini game state can be anyone of the available defined names as
	 * specified in the MiniGames.names array.
	 * @param state - State type to change to.
	 */
	public void changeState(int state) {
		NewJSONObject k = new NewJSONObject(-1, Keys.Commands.STATE_UPDATE);
		k.put(Keys.STATE, state);
		
		if (state == BOARD) {
			k.put("leaderboard", miniMngr.getJSONLeaderboard());
		} else if (state == MINIGAME) {
			k.put("mini", miniMngr.randMini());
		}
		server.echoAll(k);
		miniMngr.clearLeaderboard();
	}
	
	/**
	 * Called when a client sends a stopped command. If all players have finished
	 * animating the current player, we need to move onto the next player and/or
	 * change to a mini-game state.
	 */
	public void isStopped() {
		stopCount++;

		if (stopCount == players.size()) {
			stopCount = 0;
			activePlayer.setHasRolled(true);		
			
			if (rolledPlayers.size() == players.size()) {
				reset();
				// slight delay when transitioning
				GameUtils.resetTimer(timer);
				timer = new Timer(1500, e -> {
					changeState(MINIGAME);
					nextPlayer();
				});
				timer.setRepeats(false);
				timer.start();
				return;
			}
			nextPlayer();
		}
	}
	
	/**
	 * Clears out all players, and resets hasStarted flag to false.
	 * This should be called if we need to restart the server game.
	 */
	public void clearAll() {
		players.clear();
		rolledPlayers.clear();
		activePlayer = null;
		hasStarted = false;
	}
	
	/**
	 * Echoes all players that are stored on this server to all clients.
	 */
	private void echoAllPlayers() {
		for (NewPlayer p : players.values()) {
			NewJSONObject out = new NewJSONObject(p.getID(), Keys.Commands.ADD_PLAYER);
			out = new NewJSONObject(p.getID(), Keys.Commands.ADD_PLAYER);
			out.put(Keys.ID, p.getID());
			out.put(Keys.PLAYER, p.toJSONObject());	
			//System.out.println("echoing all players: " + p.toJSONObject().toJSONString());
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
	
	/**
	 * Resets players back to not having rolled, typically after a new round.
	 */
	private void reset() {
		activePlayer.setHasRolled(false);
		rolledPlayers.clear();
	}
	
	/**
	 * Checks the countdown for starting the game. If 2+ players are connected,
	 * the timer sends JSON packets to clients, counting down the time before
	 * the game launches into the BoardState. If players join during countdown,
	 * timer resets, unless there are 4 players, and time is reduced to 5 secs.
	 */
	private void checkCountdown() {
		NewJSONObject obj = new NewJSONObject(-1, Keys.Commands.TIMER);
		JSONObject timerObj = new JSONObject();
		
		if (players.size() < 2) {
			hasStarted = false;		// we need to reset!
		}
		
		if (!hasStarted) {
			if (players.size() >= 2) {
				timeLeft = WAIT_TIME;
				GameUtils.resetTimer(timer);
				
				if (players.size() == 4 && timeLeft > 5) {
					timeLeft = 5;	// shorten time, we have reached player limit
				}
				timer = new Timer(1000, e -> {
					
					if (timeLeft == 0) {
						timer.stop();
						hasStarted = true;
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
				hasStarted = false;
				obj.put(Keys.Commands.TIMER, timerObj);
				server.echoAll(obj);
			}	
		}
	}
	
	/**
	 * Sends a state update command to all clients to change to either board
	 * or mini-game state.
	 * @param state - State type to change to.
	 */
	private void changeClientState(int state) {
		NewJSONObject k = new NewJSONObject(-1, Keys.Commands.STATE_UPDATE);
		k.put(Keys.STATE, state);
		server.echoAll(k);
		nextPlayer();	// assign next player!
	}
	
	// accessor methods
	
	public Server getServer() {
		return server;
	}
	
	public ConcurrentHashMap<String, NewPlayer> getPlayers() {
		return players;
	}
	
	public MiniGameManager getMiniMngr() {
		return miniMngr;
	}
	
}
