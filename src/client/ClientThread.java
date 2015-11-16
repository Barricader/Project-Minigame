package client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import states.ClientPanel;

public class ClientThread extends Thread {
	private Socket sock = null;
	private Client client = null;
	private ClientPanel clientPanel = null;

	private DataInputStream streamIn = null;
	private DataOutputStream streamOut = null;
	
	private int ID;

	public ClientThread(Client client, Socket sock) throws IOException {
		this.client = client;
		this.sock = sock;
		open();
		start();
	}

	/**
	 * Constructor for connecting to a client window.
	 * 
	 * @param clientWindow
	 * @param sock
	 * @throws IOException
	 */
	public ClientThread(ClientPanel chatPanel, Socket sock) throws IOException {
		this.clientPanel = chatPanel;
		this.sock = sock;
		open();
		start();
	}

	public void open() throws IOException {
		streamOut = new DataOutputStream(sock.getOutputStream());
		streamIn = new DataInputStream(sock.getInputStream());
	}

	public void close() throws IOException {
		if (streamOut != null) {
			streamOut.close();
		}
		if (sock != null) {
			sock.close();
		}
	}

	public void send(String msg) throws IOException {
		System.out.println("Client send: " + msg);
		streamOut.writeUTF(msg);
		streamOut.flush();
	}

	public void run() {
		while (true) {
			try {
				clientPanel.handle(streamIn.readUTF());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void setID(int ID) {
		this.ID = ID;
	}

}
