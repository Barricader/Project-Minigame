package newserver;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONObject;

import client.IOHandler;
import util.Keys;

/**
 * This class provides implementation for handling JSON objects both sending
 * and receiving from the connected ServerClient. This class is responsible for 
 * sending and receiving JSON objects.
 * @author David Kramer
 *
 */
public class ServerIOHandler extends IOHandler {
	private ServerClient serverClient;
	private Map<String, Action> actionMap;	// routes commands to method calls
	private JSONObject input;	// last input received
	
	/**
	 * Constructs a new ServerIOHandler with a connection to a ServerClient.
	 * @param serverClient - ServerClient to connect with
	 */
	public ServerIOHandler(ServerClient serverClient) {
		super();
		this.serverClient = serverClient;
		initActionMap();
	}
	
	/**
	 * Initializes the action map, which routes command keys to their
	 * associated event methods, that are contained within this ServerClient's
	 * ServerDirector.
	 */
	private void initActionMap() {
		actionMap = new HashMap<>();
		ServerDirector dir = serverClient.getServer().getServerDirector();
		actionMap.put(Keys.Commands.ADD_PLAYER, 	() -> dir.addPlayer(input));
		actionMap.put(Keys.Commands.REM_PLAYER, 	() -> dir.removePlayer(input));
		actionMap.put(Keys.Commands.UPDATE, 		() -> dir.updatePlayer(input));
		actionMap.put(Keys.Commands.ROLLED, 		() -> dir.movePlayer(input));
		actionMap.put(Keys.Commands.STOPPED, 		() -> dir.isStopped());
		actionMap.put(Keys.Commands.MINI_STOPPED, 	() -> dir.getMiniMngr().isMinigameOver());
		actionMap.put(Keys.Commands.MINI_UPDATE, 	() -> dir.getMiniMngr().handle(input));
		actionMap.put(Keys.Commands.MSG, 			() -> serverClient.getServer().echoAll(input));
	}

	/**
	 * Writes a JSONObject through this ServerClient's output stream.
	 * @param out JSONObject to write out
	 */
	public void send(JSONObject out) {
		try {
			serverClient.getOutputStream().writeObject(out);
			serverClient.getOutputStream().flush();
			serverClient.getOutputStream().reset();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Receives incoming JSONObjects and checks to see if the actionMap 
	 * contains the command key, which it should, unless an undefined
	 * command has been passed. The actionMap then calls the appropriate
	 * method to carry out the execution of the command.
	 * @param in - JSONObject received 
	 */
	public void receive(JSONObject in) {
		String cmdKey = (String) in.get(Keys.CMD);
		input = in;
		System.out.println("ServerIOReceived: " + in.toJSONString());
		if (actionMap.containsKey(cmdKey)) {
			actionMap.get(cmdKey).execute();
		}
	}
}
