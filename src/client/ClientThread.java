package client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;

import javax.swing.JOptionPane;

/**
 * Establishes a connection to the main server socket. A client thread is an
 * independent process that actively runs to read in input that has been
 * received from its DataInputStream. In this thread, all of the processing
 * of incoming input, is handled by the clientPanel handle method.
 * @author David Kramer
 * @author JoJones
 *
 */
public class ClientThread extends Thread {
	private Socket sock = null;
	private String name = null;
	private int ID;
	private boolean running = false;
	private ClientPanel clientPanel = null;

	private DataInputStream streamIn = null;
	private DataOutputStream streamOut = null;

	/**
	 * Constructor for connecting via chat panel.
	 * 
	 * @param chatPanel - Chat Panel that will render all text between clients
	 * @param sock - Active socket
	 * @throws IOException
	 */
	public ClientThread(ClientPanel chatPanel, Socket sock) throws IOException {
		super();
		running = true;
		this.clientPanel = chatPanel;
		this.sock = sock;
		setName("CLIENT THREAD");
		open();
		start();
	}
	
	/**
	 * Constructor for connecting via chat panel with specified name.
	 * 
	 * @param chatPanel - Chat Panel that will render all text between clients
	 * @param sock - Active socket
	 * @throws IOException
	 */
	public ClientThread(ClientPanel chatPanel, Socket sock, String name) throws IOException {
		super();
		running = true;
		this.clientPanel = chatPanel;
		this.sock = sock;
		this.name = name;
		setName("CLIENT THREAD -> " + name.toUpperCase());
		open();
		start();
		send("!name " + name);	// send name to server!
	}

	/**
	 * Opens input and output data streams.
	 * @throws IOException
	 */
	public void open() throws IOException {
		streamOut = new DataOutputStream(sock.getOutputStream());
		streamIn = new DataInputStream(sock.getInputStream());
	}

	/**
	 * Closes all input and output data streams as well as the active socket.
	 * @throws IOException
	 */
	public void close() throws SocketException, IOException {
		if (sock != null) {
			sock.shutdownInput();
			sock.shutdownOutput();
		}
		if (streamOut != null) {
			streamOut.close();
		}
		if (streamIn != null) {
			streamIn.close();
		}
	}

	/**
	 * Writes a string message to the output stream of this thread.
	 * @param msg - The message to send.
	 * @throws IOException
	 */
	public void send(String msg) throws IOException {
		System.out.println("Client send: " + msg);
		try {
			streamOut.writeUTF(msg);
			streamOut.flush();
		} catch (SocketException e) {	// server disconnected!
			JOptionPane.showMessageDialog(clientPanel, "It appears that the client isn't connected to server anymore!");
			clientPanel.disconnect();
		}
	}

	/**
	 * Main client thread loop. Actively listens to and waits for input, and then calls
	 * handle method on client panel, to properly execute any input it has received.
	 */
	public void run() {
		while (running) {
			try {
				clientPanel.handle(streamIn.readUTF());
			} catch (SocketException e) {
				if (sock.isClosed()) {
					running = false;	
				}
			} catch (EOFException e) {			// end of stream signal and end thread execution
				running = false;
			} catch (IOException e) {			// some other error occurred
				e.printStackTrace();
			} catch (InterruptedException e) {	// interrupt stream thread-block and end thread execution
				running = false;		
			}
		}
	}
	
	public void stopThread() {
		running = false;
	}
	
	// Mutator methods
	
	public void setID(int ID) {
		this.ID = ID;
	}
	
	// Accessor methods
	
	public int getID() {
		return ID;
	}

}
