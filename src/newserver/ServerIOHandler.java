package newserver;

import java.io.IOException;

import org.json.simple.JSONObject;

import client.IOHandler;
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
		System.out.println("Should be sending: " + out);
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
		System.out.println("ServerIO Handler received: " + in);
		if (in.get("cmd") == "rolled") {
			int id = (int) in.get("id");
			int roll = (int) in.get("roll");
			serverClient.getServer().getServerDirector().movePlayer(id, roll);
		}
		else if (in.get("cmd") == "stopped") {
			serverClient.getServer().getServerDirector().isStopped();
		}
		else if (in.get("cmd") == "msg") {
			int id = (int) in.get("id");
			String text = (String) in.get("text");
			
			NewJSONObject k = new NewJSONObject(-1, "msg");
			k.put("playerID", id);
			k.put("text", text);
			
			serverClient.getServer().echoAll(k);
		}
	}

}
