package newserver;

import java.io.IOException;

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
	
	public ServerIOHandler(ServerClient serverClient) {
		super();
		this.serverClient = serverClient;
	}

	public void send(JSONObject out) {
//		System.out.println("ServerClient should be sending: " + out);
		// send JSON object through objectOutputStream
		try {
			serverClient.getOutputStream().writeObject(out);
			serverClient.getOutputStream().flush();
			serverClient.getOutputStream().reset();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void receive(JSONObject in) {
		String cmd = (String) in.get(Keys.CMD);
		
		ServerDirector dir = serverClient.getServer().getServerDirector();
		switch (cmd) {
		case Keys.Commands.ADD_PLAYER:
			dir.addPlayer(in);
			break;
		case Keys.Commands.REM_PLAYER:
			dir.removePlayer(in);
			break;
		case Keys.Commands.UPDATE:
			dir.updatePlayer(in);
			break;
		case Keys.Commands.ROLLED:
			dir.movePlayer(in);
			break;
		case Keys.Commands.STOPPED:
			dir.isStopped();
			break;
		case Keys.Commands.MINI_STOPPED:
			dir.isMinigameOver();
			break;
		case Keys.Commands.MINI_UPDATE:
			dir.updateMinigame(in);
			break;
		case Keys.Commands.MSG:
			serverClient.getServer().echoAll(in);	// echo to all other clients
			break;
		}
	}
}
