package newserver;

import java.io.IOException;
import java.net.Socket;

import javax.swing.Timer;

import client.Client;

public class ServerClient extends Client {
	public static final int MAX_NAME_LENGTH = 10;	// max allowed name characters
	private Server server;
	
	/**
	 * Default constructor.
	 */
	public ServerClient() {
		init();
	}
	
	/**
	 * Constructs a client with specified ID and socket.
	 * @param ID
	 * @param socket
	 */
	public ServerClient(int ID, Socket socket, Server server) {
		this.ID = ID;
		this.socket = socket;
		this.server = server;
		init();
	}
	
	private void init() {
		ioHandler = new ServerIOHandler(this);
	}
	
	/**
	 * Echo's ID to the client connected through the ServerClient.
	 * @throws IOException
	 */
	public void echoID() throws IOException {
		//TODO implement using JSON
	}
	
	public Server getServer() {
		return server;
	}
}
