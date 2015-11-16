package server;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Server thread base class
 * @author JoJones
 */
public class ServerThread extends Thread {
	private Server server = null;
	private Socket sock = null;
	private int ID = -1;

	private DataInputStream streamIn = null;
	private DataOutputStream streamOut = null;
	
	public ServerThread(Server server, Socket sock) {
		super();
		this.server = server;
		this.sock = sock;
		ID = this.sock.getPort();
	}
	
	/**
	 * Send input to server
	 * @param msg - String to input
	 * @throws IOException
	 */
	public void send(String msg) throws IOException {
		streamOut.writeUTF(msg);
		streamOut.flush();
	}
	
	/**
	 * Loop of each thread
	 */
	public void run() {
		System.out.println("Server thread " + ID + " running...");
		assignIDToClient(ID);	// establish client ID
		while (true) {
			try {
				// Checks for input
				System.out.println("Checking input");
				server.handle(ID, streamIn.readUTF());
			} catch (IOException e) {
				try {
					// If something bad happens with input, remove client
					server.remove(ID);
				} catch (IOException | InterruptedException e2) {
					e2.printStackTrace();
				}
				try {
					// Also, delete this thread
					join();
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void assignIDToClient(int ID) {
		try {
			send("/ID/" + ID + "/e/");
		} catch (IOException e) {
			e.printStackTrace();
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
	public void close() throws IOException {
		if (sock != null) {
			sock.close();
		}
		if (streamIn != null) {
			streamIn.close();
		}
		if (streamOut != null) {
			streamOut.close();
		}
	}
	
	public int getID() {
		return ID;
	}
}
