package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.HashMap;

/**
 * Server wrapper
 * @author JoJones
 */
public class Server implements Runnable {
	// Ports that are open: 7742 - 7745
	private static int PORT = 7742;
	private static final int MAX_CLIENTS = 4;
	@Deprecated 
	/*
	 * We are using clientMap now, to store and look up clients by ID
	 */
	private ServerThread clients[] = new ServerThread[MAX_CLIENTS];
	private HashMap<Integer, ServerThread> clientMap; // testing hash map instead of array
	private ServerSocket serverSock = null;
	private Thread t = null;
	private int clientCount = 0;
	private MessageParser parser;
	
	/**
	 * Starts the main server that all clients and their associated server threads
	 * will connect to. Also creates the message parser, that will be useful for
	 * performing actions based on data input.
	 * @throws IOException
	 */
	public Server() throws IOException {
		serverSock = new ServerSocket(PORT);
		clientMap = new HashMap<>();
		System.out.println("Server started: " + serverSock);
		System.out.println("IP: " + serverSock.getLocalSocketAddress());
		t = new Thread(this);
		t.start();
		parser = new MessageParser(this);
	}
	
	/**
	 * Server loop, listens for clients and accepts them automatically
	 */
	public void run() {
		while (t != null) {
			try {
				System.out.println("Waiting for client...");
				addThread(serverSock.accept());
			} catch (IOException e) {
				e.printStackTrace();
				break;
			}
		}
	}

	/**
	 * Kills all ServerThread clients.
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public void stop() throws IOException, InterruptedException {
		for (ServerThread t : clientMap.values()) {
			terminateClient(t);
		}
		serverSock.close();
	}
	
	/**
	 * @deprecated - We can now lookup client using ID key in client map!
	 * 
	 * Finds client by an ID
	 * @param ID - Uses this ID to search for a client
	 * @return
	 */
	private int findClient(int ID) {
		for (int i = 0; i < clientCount; i++) {
			if (clients[i].getID() == ID) {
				return i;
			}
		}

		return -1;
	}
	
	/**
	 * Handles input from client and sends it to other clients
	 * @param ID - ID of client
	 * @param input - String of text that they want to input
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public synchronized void handle(int ID, String input) throws IOException, SocketException, InterruptedException {
		System.out.println("Server handle");
		System.out.println("Input: " + input);
	
		if (parser.parse(input, ID) == MessageParser.VALID_CMD) { // if command is valid, don't echo to clients
			return;
		} else {
			for (ServerThread t : clientMap.values()) {			
				t.send(getClientName(ID) + ": " + input);
			}	
		}
		
	}
	
	/**
	 * Sends specified message to all clients.
	 * @param msg - Message to send
	 * @throws IOException
	 */
	public void sendToAll(String msg) throws IOException {
		for (ServerThread t : clientMap.values()) {
			t.send(msg);
		}
	}
	
	/**
	 * Removes a specified client
	 * @param ID - ID of client to lookup in client map
	 * @throws IOException
	 * @throws SocketException
	 * @throws InterruptedException
	 */
	public synchronized void remove(int ID) throws IOException, SocketException, InterruptedException {
		ServerThread clientToRemove = clientMap.get(ID);
		clientToRemove.send("!quit");
		sendToAll("Client: " + getClientName(ID) + ", has disconnected.");
		clientMap.remove(ID);
		clientCount--;
		terminateClient(clientToRemove);
		System.out.println("Removed client : " + ID);
	}
	
	/**
	 * Closes all streams on client and kills the thread process.
	 * @param client - ServerThread client to terminate
	 * @throws IOException
	 * @throws InterruptedException
	 */
	private void terminateClient(ServerThread client) throws IOException, InterruptedException {
		client.close();
		client.stopThread();
		client.interrupt();
	}
	
	/**
	 * @deprecated - Use clientExistsByID instead to lookup key in client map and see
	 * if it exists.
	 * 
	 * Checks if the client exists via ID
	 * @param id - ID to check for
	 * @return Whether or not the client exists
	 */
	public boolean existsID(int id) {
		int code = findClient(id);
		if (code != -1) {
			return true;
		}
		
		return false;
	}
	
	/**
	 * Add a thread for each client, and stores it in the client map.
	 * @param sock - Socket of incoming client
	 * @throws IOException
	 */
	public void addThread(Socket sock) throws IOException {
		if (clientCount < clients.length) {
			System.out.println("Client accepted: " + sock);
			// create server client and add to client map
			ServerThread serverClient = new ServerThread(this, sock);
			int serverClientID = serverClient.getID();
			serverClient.open();
			serverClient.start();
			clientMap.put(serverClientID, serverClient);
			sendToAll("Client: " + getClientName(serverClientID) + ", has connected!");	
			clientCount++;
		}
		else {
			System.out.println("Client refused: max clients reached...");
		}
	}
	
	public void assignName(int clientID, String name) {
		ServerThread client = clientMap.get(clientID);	// client we're targeting
		
		// check for duplicate names
		boolean duplicateFound = false;
		for (ServerThread c : clientMap.values()) {
			if (c.getClientName() != null) {
				if (c.getClientName().equalsIgnoreCase(name)) {
					duplicateFound = true;
					break;
				}
			}
		}
		
		try {
			if (duplicateFound) {
				client.send("!invalidName!");
				client.send("<Duplicate Name - No Assignment Made!>");
			} else {
				client.setClientName(name);
				client.send("You will now be seen as: " + name);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	@SuppressWarnings("unused")
	public static void main(String[] args) throws IOException {
		Server s = new Server();
	}
	
	// accessor methods
	
	public HashMap<Integer, ServerThread> getClientMap() {
		return clientMap;
	}
	
	/**
	 * Returns the name of the client, if it isn't null, else it will return a string
	 * of the client ID. Useful, for echoing stuff to other clients to determine which
	 * client is the sender.
	 * @param ID - ID of client to lookup
	 * @return - Client name or client ID as a string
	 */
	public String getClientName(int ID) {
		String name = clientMap.get(ID).getClientName() != null ? clientMap.get(ID).getClientName() : "" + ID;
		return name;
	}
	
	public boolean clientExistsByID(int ID) {
		return clientMap.containsKey(ID);
	}
}
