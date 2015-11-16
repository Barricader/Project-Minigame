package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Server wrapper
 * @author JoJones
 */
public class Server implements Runnable {
	private static int PORT = 64837;
	private static final int MAX_CLIENTS = 4;
	private ServerThread clients[] = new ServerThread[MAX_CLIENTS];
	private ServerSocket serverSock = null;
	private Thread t = null;
	private int clientCount = 0;
	private MessageParser parser;
	
	public Server() throws IOException {
		serverSock = new ServerSocket(PORT);
		System.out.println("Server started: " + serverSock);
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
	 * Kills server
	 * @throws InterruptedException
	 * @throws IOException 
	 */
	public void stop() throws InterruptedException, IOException {
		if (t != null) {
			t.join();
			t = null;
			for (int i = 0; i < clients.length; i++) {
				clients[i].close();
			}
			serverSock.close();
		}
	}
	
	/**
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
	public synchronized void handle(int ID, String input) throws IOException, InterruptedException {
		// If a client types 'bye', they exit from the server
		System.out.println("Server handle");
		System.out.println("Input: " + input);
		if (input.equals("bye")) {
			clients[findClient(ID)].send("bye");
			remove(ID);
		}
		parser.parse(input);
		
		// send message to clients
		for (int i = 0; i < clientCount; i++) {
			clients[i].send(ID + ": " + input);
		}
	}
	
	/**
	 * Removes a client
	 * @param ID - Client ID to remove
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public synchronized void remove(int ID) throws IOException, InterruptedException {
		int pos = findClient(ID);
		if (pos >= 0) {
			ServerThread toTerminate = clients[pos];
			System.out.println("Removing client thread " + ID + " at " + pos);
			
			if (pos < clientCount - 1) {
				for (int i = pos + 1; i < clientCount; i++) {
					clients[i - 1] = clients[i];
				}
			}
			
			clientCount--;
			
			toTerminate.close();
			toTerminate.join();
		}
	}
	
	/**
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
	 * Add a thread for each client
	 * @param sock - Socket of incoming client
	 * @throws IOException
	 */
	public void addThread(Socket sock) throws IOException {
		if (clientCount < clients.length) {
			System.out.println("Client accepted: " + sock);
			clients[clientCount] = new ServerThread(this, sock);
			
			clients[clientCount].open();
			clients[clientCount].start();
			clientCount++;
		}
		else {
			System.out.println("Client refused: max clients reached...");
		}
	}
	
	public static void main(String[] args) throws IOException {
		Server s = new Server();
	}
}
