package client;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;

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
	public static final String HOST = "localhost";
	public static final int PORT = 64837;
	private ClientThread client;
	
	// GUI components
	private JScrollPane chatScrollPane;	// scroll pane for long chats
	private JTextArea chatArea;
	private JTextField msgField;
	private JButton sendBtn;
	private JButton collapseBtn; 	// TODO make the chat window able to collapse / expand
	private JButton connectBtn;		// connect or disconnect button
	
	private boolean isConnected;	// are we connected to the server?
	
	/**
	 * Constructs a new ClientPanel.
	 */
	public ClientPanel() {
		init();
		setMinimumSize(SIZE);
		setPreferredSize(SIZE);
	}
	
	/**
	 * Constructs a new ClientPanel with an active client thread.
	 * @param client - The client thread
	 */
	public ClientPanel(ClientThread client) {
		this.client = client;
		init();
		setMinimumSize(SIZE);
		setPreferredSize(SIZE);
	}
	
	/**
	 * Connects to the specified host name and port. This will also display a warning
	 * message if the connection to server was unsuccessful.
	 * @param name - Host name of server
	 * @param port - Port number of server
	 * @return - true if connection was successful, false otherwise.
	 */
	public boolean connect(String name, int port) {
		printMessage("Attempting to connect to: " + name + "...");
		try {
			Socket sock = new Socket(name, port);
			open(sock);
			printMessage("Connected to: " + sock);
			connectBtn = disconnectAction();	// change button status
			isConnected = true;
			return true;
		} catch (IOException e) {
//			JOptionPane.showMessageDialog(Main.getInstance(), "Unable to connect! Server may have"
//					+ " not been started...", "Connection Error", JOptionPane.ERROR_MESSAGE);
			printMessage("Connection attempt failed.");
		}
		return false;
	}

	/**
	 * Opens a new client thread based on the available socket.
	 * @param socket - Socket to connect the client thread to.
	 */
	public void open(Socket socket) {
		try {
			client = new ClientThread(this, socket);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Closes and terminates any client threads and its input/output data streams. The client
	 * thread is then set to null. The connect button to perform a connection action, if clicked
	 * again later.
	 */
	public void terminateClient() throws IOException, InterruptedException {
		isConnected = false;
		connectBtn = connectAction();
		// do everything to kill thread!
		client.close();
		client.interrupt();
		client.stopThread();
		client.join();
		client = null;
	}
	
	/**
	 * Disconnects from server and closes the client out.
	 */
	public void disconnect() {
		isConnected = false;
		try {
			terminateClient();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			// don't do anything. Thread should be stopped. No further action should be necessary
		}
	}
	
	/**
	 * Changes out the connectBtn with the connection action.
	 * @return - JButton with connection action listener
	 */
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
	
	/**
	 * Changes out the connectBtn with the disconnect action.
	 * @return - JButton with disconnect action listener
	 */
	public JButton disconnectAction() { 
		// clear out any previous actions
		for (ActionListener action : connectBtn.getActionListeners()) {
			connectBtn.removeActionListener(action);
		}
		connectBtn.setText("Disconnect");
		connectBtn.addActionListener(e -> {
			int val = JOptionPane.showConfirmDialog(Main.getInstance(), "Are you sure you want to disconnect? "
					, "Confirm Disconnect", JOptionPane.OK_CANCEL_OPTION);
			
			if (val == 0) {
				send("!quit " + client.getID());
				connectBtn = connectAction();
			}

		});
		return connectBtn;
	}

	/**
	 * Sends specified text to the client thread, to be send to the server, and then 
	 * clears out the text field in this chat panel.
	 * @param text
	 */
	public void send(String text) {
		try {
			client.send(text);
			msgField.setText("");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * This method is called from the client thread run method, and is called whenever
	 * data has been written to the input stream of this client. Unless the incoming 
	 * message is a special command, this will just print the text out to the text
	 * area. If the text is a command, it will then execute the proper command.
	 * @param msg
	 */
	public void handle(String msg) throws IOException, SocketException, InterruptedException {
		System.out.println("msg: " + msg);
		if (isConnected) {
			if (msg.startsWith("/ID/")) {
				System.out.println("SHould be assigning ID!");
				String ID = msg.split("/ID/|/e/")[1].trim();
				client.setID(Integer.parseInt(ID));
			} else if (msg.equals("!invalidName!")) {
				client.setHasValidName(false);
			} else if (msg.equals("bye") || msg.equals("!quit")) {
				printMessage(msg);
				terminateClient();
			} else {
				printMessage(msg);
			}	
		}
	}

	/**
	 * Sends a message to the client if we're connected and the text field is not empty.
	 */
	private void sendMessage() {
		String text = msgField.getText();
		if (!text.isEmpty() && isConnected) {
			send(text);
		}
	}

	/**
	 * Prints specified message to the chat area.
	 * @param msg
	 */
	private void printMessage(String msg) {
		String msgArea = chatArea.getText();
		msgArea += msg + "\n";
		chatArea.setText(msgArea);
		chatArea.setCaretPosition(chatArea.getDocument().getLength());	// move scroll bar down to bottom
	}
	
	/**
	 * Initializes all GUI components of this panel using a GridBagLayout.
	 */
	private void init() {
		createComponents();
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		
		// connect / disconnect btn
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
		add(chatScrollPane, c);
		
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
	
	/**
	 * Creates all GUI components and adds any necessary action listeners.
	 */
	private void createComponents() {
		
		// chat history area
		chatArea = new JTextArea();
		chatArea.setBorder(new LineBorder(Color.CYAN));
		chatArea.setBackground(Color.BLACK);
		chatArea.setForeground(Color.CYAN);
		chatArea.setLineWrap(true);
		chatArea.setWrapStyleWord(true);
		chatArea.setEditable(false);
		chatArea.setCaretPosition(chatArea.getDocument().getLength());	// scroll down
		
//		chat scroll pane
		chatScrollPane = new JScrollPane(chatArea);
		chatScrollPane.setBackground(Color.BLACK);
		chatScrollPane.setBorder(new LineBorder(Color.BLACK));
		chatScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		
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
			public void keyPressed(KeyEvent e) {}
			public void keyReleased(KeyEvent e) {}

		});
		
		collapseBtn = new JButton(">>");
		collapseBtn.setBackground(Color.CYAN);
		collapseBtn.setForeground(Color.BLACK);
		collapseBtn.setBorder(new LineBorder(Color.CYAN));
		collapseBtn.addActionListener( e -> {
			// TODO possibly add chat window collapse functionality?
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
	
	// Accessor methods
	
	public boolean isConnected() {
		return isConnected;
	}
	
	public ClientThread getClient() {
		return client;
	}
}
