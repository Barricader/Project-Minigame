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
		
	}

}
