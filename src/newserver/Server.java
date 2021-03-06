package newserver;

import java.io.EOFException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.ConcurrentHashMap;

import org.json.simple.JSONObject;

import util.Keys;
import util.NewJSONObject;

/**
 * The Server class is responsible for accepting new client connections through
 * this server socket. If the client count is less than the max allowed clients,
 * new connections will be accepted, otherwise they are refused. Each connection
 * spawns a ServerClient which is an independent thread with a link to this 
 * server that relays information between the client and this server.
 * @author David Kramer
 * @author JoJones
 *
 */
public class Server extends Thread {
	public static final String HOST = "localhost";
	public static final int PORT = 7742;
	private static final int MAX_CLIENTS = 4;
	private ServerApp app;
	
	private boolean running = false;
	private ServerSocket serverSocket = null;
	private ConcurrentHashMap<Integer, ServerClient> clients;
	private ServerDirector serverDir;
	private int portNo = PORT;
	
	/**
	 * Constructs a new Server.
	 */
	public Server() {
		clients = new ConcurrentHashMap<>();
		serverDir = new ServerDirector(this);
	}
	
	/**
	 * Constructs a new Server with a link to the ServerApp.
	 * @param app - Target ServerApp
	 */
	public Server(ServerApp app) {
		this.app = app;
		clients = new ConcurrentHashMap<>();
		serverDir = new ServerDirector(this);
	}
	
	/**
	 * Starts this thread.
	 */
	public void start() {
		running = true;
		super.start();
		app.log("Server started....");
	}
	
	/**
	 * Main server loop. Waits and listens for new clients.
	 */
	public void run() {
		while (running) {
			try {
				app.log("Waiting for client...");
				addClient(serverSocket.accept());
			} catch (SocketException e) {
				running = false;
			} catch (EOFException e) {
				running = false;
				app.log(e.getMessage());
				e.printStackTrace();
			} catch (IOException e) {
				app.log(e.getMessage());
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Opens server socket on defined PORT.
	 * @throws IOException
	 */
	public void open() throws IOException {
		serverSocket = new ServerSocket(portNo);
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
	@SuppressWarnings("unchecked")
	public void addClient(Socket socket) throws IOException {
		if (clients.size() < MAX_CLIENTS) {
			int ID = serverDir.getPlayers().size();
			System.out.println("Socket: " + socket);
			ServerClient sc = new ServerClient(ID, socket, this);
			sc.open();
			sc.start();
			sc.echoID();
			NewJSONObject k = new NewJSONObject(ID, Keys.Commands.CONNECT);
			k.put(Keys.CONNECT_STATUS, 1);
			sc.getIOHandler().send(k);
			clients.put(ID, sc);
			app.log("client: " + ID + ", added!");
		} else {
			app.log("Client limit reached. Client connection refused!");
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
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Echoes JSONObject to all clients.
	 * @param out - JSONObject to send to all clients.
	 */
	public void echoAll(JSONObject out) {
		for (ServerClient sc : clients.values()) {
			sc.getIOHandler().send(out);
		}
		if (out.containsKey(Keys.LOG)) {
			if ((boolean)out.get(Keys.LOG)) {
				app.log(out.toJSONString());
			}
		}
	}
	
	/**
	 * Terminates this server thread process.
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public void terminate() throws IOException, InterruptedException {
		clients.clear();
		running = false;
		close();
		interrupt();
		join();
	}
	
	public ServerApp getServerApp() {
		return app;
	}
	
	public ServerDirector getServerDirector() {
		return serverDir;
	}
	
	public boolean isRunning() {
		return running;
	}
	
	public void setPort(int portNo) {
		this.portNo = portNo;
	}
}
