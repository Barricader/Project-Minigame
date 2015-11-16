package client;

import java.applet.Applet;
import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Event;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Panel;
import java.awt.TextArea;
import java.awt.TextField;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Client extends Applet {
	private static final long serialVersionUID = 1L;
	private Socket sock = null;
	private DataOutputStream streamOut = null;
	private ClientThread client = null;
	private TextArea display = new TextArea();
	private TextField input = new TextField();
	private Button send = new Button("Send"), connect = new Button("Connect"), quit = new Button("Bye");
	private String serverName = "localhost";
	private int serverPort = 64837;
	
	public void init() {
		Panel keys = new Panel();
		keys.setLayout(new GridLayout(1, 2));
		keys.add(quit);
		keys.add(connect);
		Panel south = new Panel();
		south.setLayout(new BorderLayout());
		south.add("West", keys);
		south.add("Center", input);
		south.add("East", send);
		Label title = new Label("Simple Chat Client Applet", Label.CENTER);
		title.setFont(new Font("Helvetica", Font.BOLD, 14));
		setLayout(new BorderLayout());
		add("North", title);
		add("Center", display);
		add("South", south);
		quit.setEnabled(false);
		send.setEnabled(false);
	}
	
	public boolean action(Event e, Object o) {
		if (e.target == quit) {
			input.setText("bye");
			send();
			quit.setEnabled(false);
			send.setEnabled(false);
			connect.setEnabled(true);
		}
		else if (e.target == connect) {
			connect(serverName, serverPort);
		}
		else if (e.target == send) {
			send();
			input.requestFocus();
		}
		
		return true;
	}
	
	public void connect(String name, int port) {
		println("Establishing connection to " + name + "...");
		try {
			sock = new Socket(name, port);
			open();
			println("Connected to " + sock);
			send.setEnabled(true);
			connect.setEnabled(false);
			quit.setEnabled(true);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void send() {
		try {
			streamOut.writeUTF(input.getText());
			streamOut.flush();
			input.setText("");
			println(input.getText());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void handle (String  msg) throws IOException, InterruptedException {
		if (msg.equals("bye")) {
			println("Good bye, Press ENTER to exit...");
			close();
		}
		else {
			println(msg);
		}
	}
	
	public void open() throws IOException {
		streamOut = new DataOutputStream(sock.getOutputStream());
		client = new ClientThread(this, sock);
	}
	
	public void close() {
		try {
			if (streamOut != null) {
				streamOut.close();
			}
			if (sock != null) {
				sock.close();
			}
			
			client.close();
			client.join();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	private void println(String msg) {
		display.append(msg + "\n");
	}
}
