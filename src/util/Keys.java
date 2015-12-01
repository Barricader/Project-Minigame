package util;

/**
 * Utility class that contains all available keys. Where keys are needed to write
 * JSONObject, call the field constants from this class to ensure that keys are
 * accurate and consistent, to prevent any potential errors. 
 * @author David Kramer
 *
 */
public final class Keys {
	
	private Keys() {}	// this class shouldn't be instantiated!
	
	/**
	 * Key indicating the ID of the sender / receiver.
	 */
	public static final String ID = "id";
	
	/**
	 * Key indicating that the value follows will be one of the available
	 * CommandKeys. 
	 */
	public static final String CMD = "cmd";

	/**
	 * Key indicating the string value that coincides with CommandKey.MSG.
	 */
	public static final String TEXT = "text";
	
	/**
	 * Key indicating the value of time, typically from a timer countdown.
	 */
	public static final String TIME = "time";
	
	/**
	 * Key indicating the connection status that coincides with CommandKey.CONNECT.
	 */
	public static final String CONNECT_STATUS = "connectStatus";
	
	/**
	 * Key indicating the state a client should be at.
	 */
	public static final String STATE = "state";
	
	/**
	 * Key indicating the name of the state.
	 * ** NOTE -> USE PLAYER_NAME for player
	 */
	public static final String NAME = "name";
	
	/**
	 * Key indicating the string value that coincides with CommandKey.ERROR.
	 */
	public static final String ERROR_MSG = "errorMsg";
	
	/**
	 * Key indicating the title of the error that coincides with CommandKey.ERROR.
	 */
	public static final String ERROR_TITLE = "errorTitle";
	
	/**
	 * Key indicating whether or not the JSONObject should be logged.
	 */
	public static final String LOG = "log";
	
	
	//********************************************************
	//* 			    PLAYER SPECIFIC KEYS				 *
	//********************************************************
	
	/**
	 * Key indicating the JSONObject contains a player.
	 */
	public static final String PLAYER = "player";
	
	/**
	 * Key indicating the player name.
	 */
	public static final String PLAYER_NAME = "playerName";
	
	/**
	 * Key indicating the player score.
	 */
	public static final String SCORE = "score";
	
	/**
	 * Key indicating the tileID that the player is on.
	 */
	public static final String TILE_ID = "tileID";
	
	/**
	 * Key indicating the styleID that the player has been assigned from
	 * the PlayerStyles class.
	 */
	public static final String STYLE_ID = "styleID";
	
	/**
	 * Key indicating if player is active or not.
	 */
	public static final String ACTIVE = "active";
	
	/**
	 * Key indicating the PlayerID that the player has been assigned.
	 */
	public static final String PLAYER_ID = "playerID";
	
	/**
	 * Key indicating the player roll amount.
	 */
	public static final String ROLL_AMT = "rollAmt";
	
	/**
	 * Key indicating the last roll that the player has completed.
	 */
	public static final String LAST_ROLL = "lastRoll";
	
	
	/**
	 * Utility class that contains all available command keys. Command keys contain
	 * the general "header" of processing information for I/O Handlers that read in
	 * JSONObjects and look for the value in "cmd". Access this class by calling
	 * Keys.Commands.<KEY_VALUE>
	 * @author David Kramer
	 *
	 */
	public final class Commands {
		
		private Commands() {}	// this class shouldn't be instantiated!
		
		/**
		 * Key indicating a connection request.
		 */
		public static final String CONNECT = "connect";
		
		/**
		 * Key indicating that a player should be added and send to all clients.
		 */
		public static final String ADD_PLAYER = "addPlayer";
		
		/**
		 * Key indicating that a player should be removed and updated on all clients.
		 */
		public static final String REM_PLAYER = "remPlayer";
		
		/**
		 * Key indicating the command for a timer countdown.
		 */
		public static final String TIMER = "timer";
		
		/**
		 * Key indicating an update of some sort.
		 */
		public static final String UPDATE = "update";
		
		/**
		 * Key indicating a state update, such as changing between board and
		 * mini-game.
		 */
		public static final String STATE_UPDATE = "stateUpdate";
		
		public static final String MINI_UPDATE = "miniUpdate";
		
		public static final String MINI_STOPPED = "miniStopped";
		
		/**
		 * Key indicating an active player.
		 */
		public static final String ACTIVE = "active";
		
		/**
		 * Key indicating an error.
		 */
		public static final String ERROR = "error";
		
		/**
		 * Key indicating that a message should be echoed to all clients.
		 */
		public static final String MSG = "msg";
		
		/**
		 * Key indicating that a player should move.
		 */
		public static final String MOVE = "move";
		
		/**
		 * Key indicating a player should roll.
		 */
		public static final String ROLL = "roll";
		
		/**
		 * Key indicating that a player has rolled.
		 */
		public static final String ROLLED = "rolled";
		
		/**
		 * Key indicating the movement status of a player.
		 */
		public static final String STOPPED = "stopped";
	}

}