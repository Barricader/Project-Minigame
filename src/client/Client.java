package client;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;

import org.json.simple.JSONObject;

/**
 * A client that connects to the server and runs on its own thread. A client contains
 * Object Input/Output streams for passing data back and forth between the server
 * it is connected to.
 * @author David Kramer
 *
 */
public class Client extends Thread {
	private ClientApp app;
	protected boolean running = false;
	protected boolean connected = false;
	protected Socket socket;
	
	protected ObjectOutputStream streamOut;
	protected ObjectInputStream streamIn;
	
	protected IOHandler ioHandler;	// handles input/output data
	protected int ID;
	
	/**
	 * Default constructor.
	 */
	public Client() {}
	
	/**
	 * Constructs new client with link to main app.
	 * @param app - ClientApp
	 */
	public Client(ClientApp app) {
		this.app = app;
	}
	
	/**
	 * Constructs a client with link to main app, and specified ID and socket.
	 * @param app - ClientApp
	 * @param ID - ID to assign
	 * @param socket - Socket to connect to
	 */
	public Client(ClientApp app, int ID, Socket socket) {
		this.app = app;
		this.ID = ID;
		this.socket = socket;
	}
	
	/**
	 * Starts this thread.
	 */
	public void start() {
		running = true;
		super.start();
	}
	
	/**
	 * Main thread loop. Listens to input received on InputStream.
	 */
	public void run() {
		while (running) {
			try {
				ioHandler.receive((JSONObject)streamIn.readObject());
			} catch (EOFException | SocketException e) {
				running = false;
				try {
					terminate();
				} catch (IOException e1) {
					e1.printStackTrace();
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Attempts to connect to the specified socket.
	 * @param socket - Socket to connect to.
	 * @throws IOException
	 */
	public void connect(Socket socket) throws IOException {
		this.socket = socket;
		open();
		connected = true;
	}
	
	/**
	 * Opens I/O streams.
	 * @throws IOException
	 */
	public void open() throws IOException {
		streamOut = new ObjectOutputStream(new BufferedOutputStream(socket.getOutputStream()));
		streamOut.flush();
		streamIn = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));
	}
	
	/**
	 * Closes any open I/O streams.
	 * @throws IOException
	 */
	public void close() throws IOException {
		if (socket != null) {
			socket.shutdownInput();
			socket.shutdownOutput();
		}
		if (streamIn != null) {
			streamIn.close();
		}
		if (streamOut != null) {
			streamOut.close();
		}
	}
	
	/**
	 * Terminates this thread process.
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public void terminate() throws IOException, InterruptedException {
		running = false;
		connected = false;
		interrupt();
		close();
		join();
	}
	
	// Mutator methods
	
	public void setIOHandler(IOHandler handler) {
		this.ioHandler = handler;
	}
	
	public void setID(int ID) {
		this.ID = ID;
	}
	
	// Accessor methods
	
	public boolean isRunning() {
		return running;
	}
	
	public boolean isConnected() {
		return connected;
	}
	
	public ObjectOutputStream getOutputStream() {
		return streamOut;
	}
	
	public ObjectInputStream getInputStream() {
		return streamIn;
	}
	
	public IOHandler getIOHandler() {
		return ioHandler;
	}
	
	public int getID() {
		return ID;
	}
	
}
