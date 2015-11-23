package newserver;

import java.io.IOException;

import client.IOHandler;

/**
 * This class provides implementation for handling input/output on ServerSide.
 * @author David Kramer
 *
 */
public class ServerIOHandler extends IOHandler {
	private ServerClient serverClient;
	
	public ServerIOHandler(ServerClient serverClient) {
		super();
		this.serverClient = serverClient;
	}

	public void send(String out) {
		System.out.println("Should be sending: " + out);
		try {
			serverClient.getOutputStream().writeUTF(out);
			serverClient.getOutputStream().flush();
			serverClient.getOutputStream().reset();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void receive(String in) {
		System.out.println("ServerIO Handler received: " + in);
		// test
		if (in.startsWith("!quit")) {
			serverClient.getServer().removeClient(serverClient.getID());
		}
		if (in.equals("!addPlayer")) {
			serverClient.getServer().getServerDirector().addRandomPlayer();	// TEST
		}
	}

}
