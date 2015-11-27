package client;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import org.json.simple.JSONObject;

import newserver.Keys;
import newserver.Server;
import panels.BoardPanel;
import panels.ChatPanel;
import panels.ConnectionPanel;
import panels.ConnectionPanel.Controller;
import panels.LoginPanel;
import panels.StatePanel;

/**
 * This will be the new "MAIN" application that the client will run to use
 * to connect to the server and play the game. 
 * @author David Kramer
 *
 */
public class ClientApp extends JFrame {
	private static final String TITLE = "Project Mini-Game by Jo & Kramer";
	private static final Dimension SIZE = new Dimension(960, 800);	// min size
	private static ClientApp instance = null;
	private Client client;
	
	// GUI stuff
	private JPanel panel;	// main container panel for all other panels
	private StatePanel statePanel;	// render state view
	private ChatPanel chatPanel;
	private BoardPanel boardPanel;
	private ConnectionPanel connPanel;
	
	private ErrorHandler errorHandler;
	
	public ClientApp() {
		client = new Client(this);
		errorHandler = new ErrorHandler();
		init();
		createAndShowGUI();
		client.setIOHandler(new ClientIOHandler(this));
		instance = this;
	}
	
	/**
	 * Initializes this ClientApp and lays out GUI component using GridBagLayout.
	 */
	private void init() {
		createComponents();
		
		panel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(5, 5, 5, 5);	// 5 px margin all around
		
		// connection panel
		c.anchor = GridBagConstraints.NORTHWEST;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridwidth = 5;
		c.weightx = 1.0;
		c.gridy = 0;
		c.ipady = 10;
		c.weighty = 0.0;
		panel.add(connPanel, c);
		
		// draw panel
		c.anchor = GridBagConstraints.CENTER;
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		c.gridwidth = 10;
		c.weightx = 1.0;
		c.gridy = 1;
		c.gridheight = 5;
		c.weighty = 0.8;
//		boardPanel = new BoardPanel(this);	// here to workaround first size glitch.
		panel.add(statePanel, c);
		
		// chat panel
		c.anchor = GridBagConstraints.SOUTH;
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		c.weightx = 1.0;
		c.gridwidth = 8;
		c.gridy = 6;
		c.gridheight = 4;
		c.weighty = 0.4;
		panel.add(chatPanel, c);
		
		add(panel);
	}
	
	/**
	 * Creates GUI components for this ClientApp.
	 */
	private void createComponents() {
		panel = new JPanel();
		statePanel = new StatePanel(this);
		chatPanel = new ChatPanel(this);
		connPanel = new ConnectionPanel(this);
		boardPanel = new BoardPanel(this);
//		loginPanel = new LoginPanel(this);
	}
	
	/**
	 * Sizes out the application window and makes it visible to the screen.
	 */
	private void createAndShowGUI() {
		setSize(SIZE);
		setMinimumSize(SIZE);
		setTitle(TITLE);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setVisible(true);
	}
	
	/**
	 * 
	 * @return Singleton instance of ClientApp
	 */
	public static ClientApp getInstance() {
		if (instance == null) {
			instance = new ClientApp();
		}
		return instance;
	}
	
	/**
	 * Establishes a Client-Server connection if the client isn't connected.
	 * @throws ConnectException
	 * @throws IOException
	 */
	public void connectClient() throws ConnectException, IOException {
		if (client == null) {
			resetClient();
		}
		if (!client.isConnected()) {
			client.connect(new Socket(Server.HOST, Server.PORT));
			client.start();	
		}
	}
	
	/**
	 * Resets the client of this application, typically after a disconnection.
	 */
	public void resetClient() {
		client = null;
		client = new Client(this);
		client.setIOHandler(new ClientIOHandler(this));
	}
	
	/**
	 * Closes out the client if a time out has occurred.
	 */
	public void timeout() {
		try {
			client.close();
			client.terminate();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			connPanel.getController().updateStatus(Controller.STATUS_ERROR);
			showTimeOutError();
		}
	}
	
	/**
	 * Shows a time out error message to the screen.
	 */
	public void showTimeOutError() {
		JOptionPane.showMessageDialog(this, "Client has timed out. Please try reconnecting to server!", 
				"Timeout", JOptionPane.ERROR_MESSAGE);
	}
	
	public Client getClient() {
		return client;
	}
	
	public StatePanel getStatePanel() {
		return statePanel;
	}
	
	public ConnectionPanel getConnPanel() {
		return connPanel;
	}
	
	public ChatPanel getChatPanel() {
		return chatPanel;
	}
	
	public BoardPanel getBoardPanel() {
		return boardPanel;
	}
	
	public LoginPanel getLoginPanel() {
		return statePanel.getLoginPanel();
	}
	
	public void setClient(Client client) {
		this.client = client;
	}
	
	public ErrorHandler getErrorHandler() {
		return errorHandler;
	}
	
	public class ErrorHandler extends IOHandler {
		
		public void send(JSONObject out) {}

		public void receive(JSONObject in) {
			System.out.println("error handler received: " + in.toJSONString());
			JSONObject error = (JSONObject) in.get(Keys.Commands.ERROR);
			int id = (int)in.get(Keys.ID);
			
			if (id != getLoginPanel().getClientPlayer().getID()) {	// only show to this client!
				String errorMsg = (String) error.get(Keys.ERROR_MSG);
				String errorTitle = (String) error.get(Keys.ERROR_TITLE);
				showErrorDialog(errorMsg, errorTitle);	
			}
		}
		
		public void showErrorDialog(String msg, String title) {
			JOptionPane.showMessageDialog(ClientApp.this, msg, title, JOptionPane.ERROR_MESSAGE);
		}
			
	}
	
	/**
	 * Main method. Starts up client GUI app.
	 * @param args
	 */
	public static void main(String[] args) {
		// change look and feel to nimbus
		try {
		    for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
		        if ("Nimbus".equals(info.getName())) {
		            UIManager.setLookAndFeel(info.getClassName());
		            break;
		        }
		    }
		} catch (Exception e) {
		    e.printStackTrace();
		}
		ClientApp app = new ClientApp();
	}
}
