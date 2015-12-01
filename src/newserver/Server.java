package newserver;

import java.io.EOFException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.security.SecureRandom;
import java.util.HashMap;

import org.json.simple.JSONObject;

import util.Keys;
import util.NewJSONObject;

public class Server extends Thread {
	private static SecureRandom rng = new SecureRandom();	// ID generator
	public static final String HOST = "localhost";
	public static final int PORT = 7742;
	private static final int MAX_CLIENTS = 4;
	
	private boolean running = false;
	private ServerSocket serverSocket = null;
	private HashMap<Integer, ServerClient> clients;
	
	private ServerDirector serverDir;
	
	public Server() {
		clients = new HashMap<>();
		serverDir = new ServerDirector(this);
	}
	
	/**
	 * Starts this thread.
	 */
	public void start() {
		running = true;
		super.start();
		System.out.println("Server started....");
	}
	
	/**
	 * Main server loop. Waits and listens for new clients.
	 */
	public void run() {
		while (running) {
			try {
				// wait and accept new clients
				System.out.println("Waiting for client...");
				addClient(serverSocket.accept());
			} catch (SocketException e) {	// terminate if socket is closed!
				running = false;
			} catch (EOFException e) {
				running = false;
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Opens server socket on defined PORT.
	 * @throws IOException
	 */
	public void open() throws IOException {
		serverSocket = new ServerSocket(PORT);
	}
	
	/**
	 * Closes server socket.
	 * @throws IOException
	 */
	public void close() throws IOException {
		serverSocket.close();
	}
	
	/**
	 * Adds a client to the clients map.
	 * @param socket - Socket to allow server client to connect
	 * @throws IOException
	 */
	public void addClient(Socket socket) throws IOException {
		if (clients.size() < MAX_CLIENTS) {
			int ID = serverDir.getPlayers().size();
			ServerClient sc = new ServerClient(ID, socket, this);
			sc.open();
			sc.start();
			sc.echoID(); // echo ID to main client thread
			NewJSONObject k = new NewJSONObject(ID, Keys.Commands.CONNECT);	// send connection status!
			k.put(Keys.CONNECT_STATUS, 1);	// connection is good!
			sc.getIOHandler().send(k);
			clients.put(ID, sc);
			System.out.println("client: " + ID + ", added!");
		} else {
			System.out.println("Client limit reached. Client connection refused!");
		}
	}
	
	/**
	 * Removes client from clients map with specified ID.
	 * @param clientID - ID of client to remove.
	 */
	public void removeClient(int clientID) {
		ServerClient sc = clients.get(clientID);
		if (sc != null) {
			clients.remove(sc.getID());
			try {
				sc.terminate();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Echoes string to all clients.
	 * @param out - String to send to all clients.
	 * @Decrepated
	 */
	public void echoAll(String out) {
		for (ServerClient sc : clients.values()) {
			//sc.getIOHandler().send(out);
		}
	}
	
	public void echoAll(JSONObject out) {
		for (ServerClient sc : clients.values()) {
			sc.getIOHandler().send(out);
		}
	}
	
	/**
	 * Terminates this thread process.
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public void terminate() throws IOException, InterruptedException {
		running = false;
		interrupt();
		close();
		join();
	}
	
	public ServerDirector getServerDirector() {
		return serverDir;
	}
	
	public static void main(String[] args) {
		Server server = new Server();
		try {
			server.open();
		} catch (IOException e) {
			e.printStackTrace();
		}
		server.start();
	}
}
