package newserver;

import java.io.IOException;

import org.json.simple.JSONObject;

import client.IOHandler;

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
		System.out.println("ServerClient should be sending: " + out);
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
		case Keys.Commands.MSG:
			serverClient.getServer().echoAll(in);	// echo to all other clients
			break;
		}
		
		
//		if (in.get(Keys.CMD).equals(Keys.Commands.ADD_PLAYER)) {
//			System.out.println("should be adding player on server!");
//			serverClient.getServer().getServerDirector().addPlayer(in);
//		}
//		
//		if (in.get(Keys.CMD).equals(Keys.Commands.ROLLED)) {
//			int id = (int) in.get(Keys.ID);
//			int roll = (int) in.get(Keys.ROLL);
//			serverClient.getServer().getServerDirector().movePlayer(id, roll);
//		}
//		else if (in.get(Keys.CMD).equals(Keys.Commands.STOPPED)) {
//			serverClient.getServer().getServerDirector().isStopped();
//		}
//		else if (in.get(Keys.CMD).equals(Keys.Commands.MSG)) {
//			int id = (int) in.get(Keys.ID);
//			String text = (String) in.get(Keys.TEXT);
//			
//			NewJSONObject k = new NewJSONObject(-1, Keys.Commands.MSG);
//			k.put(Keys.PLAYER_ID, id);
//			k.put(Keys.TEXT, text);
//			System.out.println("echoing: " + k.toJSONString());
//			serverClient.getServer().echoAll(k);
//		}
	}

}
