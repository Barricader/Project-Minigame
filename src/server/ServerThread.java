package server;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.Random;

/**
 * Server thread base class
 * @author JoJones
 */
public class ServerThread extends Thread {
	public static boolean[] taken = { false, false, false, false, false, false, false, false, false };
	private Server server = null;
	private Socket sock = null;
	private int ID = -1;
	private String name = null;
	private int color = 0;
	private boolean running = false;
	private int timeOutTimer = 0;
	
	private Thread test = null;
	
	private DataInputStream streamIn = null;
	private DataOutputStream streamOut = null;
	
	public ServerThread(Server server, Socket sock) {
		super();
		running = true;
		this.server = server;
		this.sock = sock;
		ID = this.sock.getPort();
		setName("SERVER THREAD: " + ID);
		
		boolean chosen = false;
		Random r = new Random();
		int c = 0;
		
		// Get a random, not yet chosen, number
		while (!chosen) {
			// Change to 9 if you want teal
			c = r.nextInt(8);
			if (!taken[c]) {
				taken[c] = true;
				chosen = true;
				color = c;
			}
		}
		
		try {
			open();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		test = new Thread(new Runnable() {
			public void run() {
				boolean te = true;	
				long startTime;
				long elapsed;
				long wait;
				int targetTime = 1000 / 60;
				
				while (te) {
					startTime = System.nanoTime();
					elapsed = System.nanoTime() - startTime;
					wait = targetTime - elapsed / 1000000;
					if (wait < 0) {
						wait = 5;
					}
					
					try {
						Thread.sleep(wait);
					} catch (Exception e) {
						e.printStackTrace();
					}
					
					timeOutTimer++;
					System.out.println(timeOutTimer);
					
					// TODO: change me to a better value
					if (timeOutTimer > 750) {
						te = false;
						// Timeout occurred, do something here
					}
				}
			}
		});
		test.start();
		
		// Send message to create player on client
		try {
			send("!hookPlayer " + ID + " " + name + " " + color);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Send input to server
	 * @param msg - String to input
	 * @throws IOException
	 */
	public void send(String msg) throws IOException {
		streamOut.writeUTF(msg);
		streamOut.flush();
		timeOutTimer = 0;
	}
	
	/**
	 * Loop of each thread. Listeners to input stream and calls server handle method
	 * for processing of input.
	 */
	public void run() {
		System.out.println("Server Thread: " + ID + " is running...");
		assignIDToClient(ID);
		while (running) {
			try {
				System.out.println("Server Thread:  checking input");
				server.handle(ID, streamIn.readUTF());
				timeOutTimer = 0;
			} catch (SocketException e) {
				running = false;	// socket streams are closed. stop running thread!
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				running = false;	// stop thread execution
			}
		}
	}
	
	/**
	 * Create streams for the socket/thread
	 * @throws IOException
	 */
	public void open() throws IOException {	
		streamIn = new DataInputStream(new BufferedInputStream(sock.getInputStream()));
		streamOut = new DataOutputStream(new BufferedOutputStream(sock.getOutputStream()));
	}
	
	/**
	 * Close the thread and streams
	 * @throws IOException
	 */
	public void close() throws SocketException, IOException {
		if (sock != null) {
			sock.shutdownInput();
			sock.shutdownOutput();
		}
		if (streamIn != null) {
			streamIn.close();
		}
		if (streamOut != null) {
			streamOut.close();
		}
	}
	
	/**
	 * Sets running flag to false, to hopefully eventually terminate the 
	 * active thread.
	 */
	public void stopThread() {
		running = false;
	}
	
	/**
	 * Sends command to assign ID to a newly added client.
	 * @param ID - ID to assign to client
	 */
	private void assignIDToClient(int ID) {
		try {
			send("/ID/" + ID + "/e/");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void setClientName(String name) {
		this.name = name;
	}
	
	public int getID() {
		return ID;
	}
	
	public String getClientName() {
		return name;
	}
}
