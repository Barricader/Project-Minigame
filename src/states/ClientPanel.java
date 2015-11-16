package states;

import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.IOException;
import java.net.Socket;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;

import client.ClientThread;
import main.Main;

/**
 * This is a GUI panel that will represent the chat window as well as any logged
 * events from the server. All events will appear in the chat window, to give status
 * to other players (such as amount rolls). This class will handle basic communication
 * between the client and server. 
 * @author David Kramer
 *
 */
public class ClientPanel extends JPanel {
	private static Dimension SIZE = new Dimension(250, 720);	// min size!
	// server info
	private static final String HOST = "localhost";
	private static final int PORT = 64837;
	
	private ClientThread client;
	
	private JTextArea chatArea;
	private JTextField msgField;
	private JButton sendBtn;
	private JButton collapseBtn;
	private JButton connectBtn;	// connect or disconnect button
	
	private boolean isConnected;
	
	public ClientPanel() {
		init();
		setMinimumSize(SIZE);
		setPreferredSize(SIZE);
	}
	
	public boolean connect(String name, int port) {
		printMessage("Attempting to connect to: " + name + "...");
		try {
			Socket sock = new Socket(name, port);
			open(sock);
			printMessage("Connected to: " + sock);
			return true;
		} catch (IOException e) {
			JOptionPane.showMessageDialog(Main.getInstance(), "Unable to connect! Server may have"
					+ " not been started...", "Connection Error", JOptionPane.ERROR_MESSAGE);
			printMessage("Connection attempt failed.");
		}
		return false;
	}

	public void open(Socket socket) {
		try {
			client = new ClientThread(this, socket);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void close() {
		try {
			client.close();
			client.join();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public void disconnect() {
		try {
			client.send("!disc " + client.getID());
			isConnected = false;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public JButton connectAction() {
		// clear out any previous actions
		for (ActionListener action : connectBtn.getActionListeners()) {
			connectBtn.removeActionListener(action);
		}
		connectBtn.setText("Connect To Server....");
		connectBtn.addActionListener(e -> {
			if (connect(HOST, PORT)) {
				isConnected = true;
				connectBtn = disconnectAction();
			}
		});
		return connectBtn;
	}
	
	public JButton disconnectAction() { 
		// clear out any previous actions
		for (ActionListener action : connectBtn.getActionListeners()) {
			connectBtn.removeActionListener(action);
		}
		connectBtn.setText("Disconnect");
		connectBtn.addActionListener(e -> {
			int val = JOptionPane.showConfirmDialog(Main.getInstance(), "Are you sure you want to disconnect? "
					, "Confirm Disconnect", JOptionPane.OK_CANCEL_OPTION);
			
			if (val == 0) {	// they hit OK button
				// TODO disconnect them from server
				connectBtn = connectAction();
			}

		});
		return connectBtn;
	}

	public void send(String text) {
		try {
			client.send(text);
			msgField.setText("");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void handle(String msg) {
		System.out.println("msg: " + msg);
		
		if (msg.startsWith("/ID/")) {
			System.out.println("SHould be assigning ID!");
			String ID = msg.split("/ID/|/e/")[1].trim();
			client.setID(Integer.parseInt(ID));
		} else if (msg.equals("bye")) {
			printMessage(msg);
			close();
		} else {
			printMessage(msg);
		}
	}

	private void sendMessage() {
		String text = msgField.getText();
		if (!text.isEmpty()) {
			send(text);
		}
	}

	private void printMessage(String msg) {
		String msgArea = chatArea.getText();
		msgArea += msg + "\n";
		chatArea.setText(msgArea);
	}
	
	private void init() {
		createComponents();
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		
		// connect / disconnect btn
		c.anchor = GridBagConstraints.NORTH;
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		c.gridwidth = 2;
		c.gridy = 0;
		c.ipady = 10;
		c.weightx = 1.0;
		c.weighty = 0.0;
		add(connectBtn, c);
		
		// chat area
		c.anchor = GridBagConstraints.NORTH;
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		c.gridwidth = 2;
		c.gridy = 1;
		c.ipady = 0;
		c.weightx = 1.0;
		c.weighty = 1.0;
		add(chatArea, c);
		
		// text field
		c.anchor = GridBagConstraints.SOUTH;
		c.gridx = 0;
		c.gridwidth = 1;
		c.gridy = 2;
		c.weightx = 0.5;
		c.weighty = 0.0;
		add(msgField, c);
		
		// send btn
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 1;
		c.gridy = 2;
		c.weightx = 0.0;
		c.weighty = 0.0;
		add(sendBtn, c);
	}
	
	private void createComponents() {
		// chat history area
		chatArea = new JTextArea();
		chatArea.setBorder(new LineBorder(Color.CYAN));
		chatArea.setBackground(Color.BLACK);
		chatArea.setForeground(Color.CYAN);
		chatArea.setLineWrap(true);
		chatArea.setWrapStyleWord(true);
		chatArea.setEditable(false);
		
		// text field
		msgField = new JTextField(10);
		msgField.setBackground(Color.BLACK);
		msgField.setForeground(Color.CYAN);
		msgField.setBorder(new LineBorder(Color.CYAN));
		msgField.setCaretColor(Color.CYAN);
		msgField.addKeyListener(new KeyListener() {
			public void keyTyped(KeyEvent e) {
				if (e.getKeyChar() == '\n') {
					sendMessage();
				}
			}

			// unused
			public void keyPressed(KeyEvent e) {
			}

			public void keyReleased(KeyEvent e) {
			}

		});
		
		collapseBtn = new JButton(">>");
		collapseBtn.setBackground(Color.CYAN);
		collapseBtn.setForeground(Color.BLACK);
		collapseBtn.setBorder(new LineBorder(Color.CYAN));
		collapseBtn.addActionListener( e -> {
			
		});
		
		connectBtn = new JButton("Connect To Server...");
		connectBtn.setBackground(Color.CYAN);
		connectBtn.setForeground(Color.BLACK);
		connectBtn.setBorder(new LineBorder(Color.CYAN));
		connectBtn = connectAction();
		
		// send btn
		sendBtn = new JButton("Send");
		sendBtn.setBackground(Color.BLACK);
		sendBtn.setForeground(Color.CYAN);
		sendBtn.addActionListener( e -> {
			sendMessage();
		});
	}	
	
	public boolean isConnected() {
		return isConnected;
	}
	
	public ClientThread getClient() {
		return client;
	}
}
