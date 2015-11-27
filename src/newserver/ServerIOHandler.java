package newserver;

import java.io.IOException;
import java.util.Set;

import org.json.simple.JSONObject;

import client.IOHandler;
import newserver.Keys.Commands;
import util.NewJSONObject;

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
		for (Object o : in.keySet()) {
			System.out.println("Key: " + o);
		}
		
		System.out.println("CMD STRING: " + in.get(Keys.CMD));
		
		if (in.get(Keys.CMD).equals(Keys.Commands.ADD_PLAYER)) {
			System.out.println("should be adding player on server!");
			serverClient.getServer().getServerDirector().addPlayer(in);
		}
		if (in.get(Keys.CMD).equals(Keys.Commands.ROLLED)) {
			int id = (int) in.get(Keys.ID);
			int roll = (int) in.get(Keys.ROLL);
			serverClient.getServer().getServerDirector().movePlayer(id, roll);
		}
		else if (in.get(Keys.CMD).equals(Keys.Commands.STOPPED)) {
			serverClient.getServer().getServerDirector().isStopped();
		}
		else if (in.get(Keys.CMD).equals(Keys.Commands.MSG)) {
			int id = (int) in.get(Keys.ID);
			String text = (String) in.get(Keys.TEXT);
			
			NewJSONObject k = new NewJSONObject(-1, Keys.Commands.MSG);
			k.put(Keys.PLAYER_ID, id);
			k.put(Keys.TEXT, text);
			System.out.println("echoing: " + k.toJSONString());
			serverClient.getServer().echoAll(k);
		}
	}

}
