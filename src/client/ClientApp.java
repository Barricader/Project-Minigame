package client;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.KeyboardFocusManager;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;

import org.json.simple.JSONObject;

import input.Keyboard;
import newserver.Server;
import panels.BaseMiniPanel;
import panels.BoardPanel;
import panels.ChatPanel;
import panels.ConnectionPanel;
import panels.ConnectionPanel.Controller;
import panels.DicePanel;
import panels.LeaderBoardPanel;
import panels.LoginPanel;
import panels.StatePanel;
import panels.minis.Enter;
import panels.minis.Pong;
import panels.minis.RPS;
import util.ErrorUtils;
import util.GameUtils;
import util.GlobalColor;
import util.MiniGames;

/**
 * This will be the new "MAIN" application that the client will run to use
 * to connect to the server and play the game. 
 * @author David Kramer
 * @author JoJones
 *
 */
public class ClientApp extends JFrame {
	private static final long serialVersionUID = 1417615047380477284L;
	// connection settings
	private String host = Server.HOST;
	private int port = Server.PORT;
	
	private GlobalColor globalColor;
	
	private static final String TITLE = "Project Mini-Game by Jo & Kramer";
	private static final Dimension SIZE = new Dimension(960, 800);	// min size
	private static ClientApp instance = null;
	private Client client;
	
	private Keyboard key;	// keyboard to give mini games access to, when we switch to them.
	
	// GUI stuff
	private JPanel panel;	// main container panel for all other panels
	private StatePanel statePanel;	// render state view
	private ChatPanel chatPanel;
	private BoardPanel boardPanel;
	private DicePanel dicePanel;
	private ConnectionPanel connPanel;
	private LeaderBoardPanel leaderPanel;
	private ConcurrentHashMap<String, BaseMiniPanel> minis;
	
	/* default start value, so that ClientIOHandler map doesn't throw a 
	 * NullPointException when accessing a non-existent minigame 
	 */
	private String curMini = MiniGames.names[0];
	
	private ErrorHandler errorHandler;
	
	/**
	 * Constructs a new ClientApp that has a client ready to be connected
	 * to the server.
	 */
	public ClientApp() {
		client = new Client(this);
		globalColor = new GlobalColor(Color.LIGHT_GRAY);	// default start color
		errorHandler = new ErrorHandler();
		init();
		initMinis();
		createAndShowGUI();
		client.setIOHandler(new ClientIOHandler(this));
		instance = this;
		setFocusable(true);
		requestFocus();
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
		c.anchor = GridBagConstraints.NORTH;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridwidth = 10;
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
		c.weighty = 0.7;
		c.ipady = 0;
		panel.add(statePanel, c);
		
		// leaderboard panel
		c.anchor = GridBagConstraints.SOUTHWEST;
		c.gridx = 0;
		c.weightx = 0.1;
		c.gridwidth = 2;
		c.gridy = 6;
		c.gridheight = 4;
		c.weighty = 0.4;
		panel.add(leaderPanel, c);
		
		// chat panel
		c.anchor = GridBagConstraints.SOUTH;
		c.gridx = 2;
		c.weightx = 0.8;
		c.gridwidth = 6;
		panel.add(chatPanel, c);
		
		// dice panel
		c.anchor = GridBagConstraints.SOUTHEAST;
		c.gridx = 8;
		c.gridwidth = 2;
		c.weightx = 0.1;
		panel.add(dicePanel, c);
		
		add(panel);
	}
	
	/**
	 * Creates GUI components for this ClientApp.
	 */
	private void createComponents() {
		panel = new JPanel();
		panel.setBackground(Color.BLACK);
		statePanel = new StatePanel(this);
		chatPanel = new ChatPanel(this);
		connPanel = new ConnectionPanel(this);
		connPanel.setVisible(false);
		boardPanel = new BoardPanel(this);
		dicePanel = new DicePanel(this);
		dicePanel.setVisible(false);
		leaderPanel = new LeaderBoardPanel(this);
		leaderPanel.setVisible(false);
	}
	
	/**
	 * Initializes the mini games for this app.
	 */
	private void initMinis() {
		minis = new ConcurrentHashMap<>(MiniGames.names.length);
		minis.put(MiniGames.names[0], new Enter(this));
		minis.put(MiniGames.names[1], new Pong(this));
		minis.put(MiniGames.names[2], new RPS(this));
	}
	
	/**
	 * Sizes out the application window and makes it visible to the screen.
	 */
	private void createAndShowGUI() {
		setSize(SIZE);
		setMinimumSize(SIZE);
		setTitle(TITLE);
		setLocationRelativeTo(null);
//		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		setVisible(true);
		
		// window close - make sure to disconnect nicely!
		addWindowListener(new WindowAdapter() {
		    public void windowClosing(WindowEvent e) {
		    	if (client.isConnected()) {
		    		if(statePanel.getLoginPanel().getController().disconnectPlayer()) {
		    			dispose();
		    			System.exit(0);
		    		} else {
		    			// they canceled their decision to disconnect. don't close window!
		    			return;
		    		}
		    	} else {
		    		// not connected, just close out window!
			    	dispose();
			    	System.exit(0);	
		    	}
		    }
		});
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
			client.connect(new Socket(host, port));
			client.start();	
		}
	}
	
	/**
	 * Resets the client connection. This should be used when we disconnect from
	 * the server.
	 */
	public void resetClient() {
		client = null;
		client = new Client(this);
		client.setIOHandler(new ClientIOHandler(this));
		chatPanel.getController().toggleUI(client.isConnected());
	}
	
	/**
	 * Resets everything in the client app, typically when the client disconnects
	 * from the server. All players are cleared out, and the active client connection
	 * is terminated.
	 */
	public void reset() {
		try {
			client.terminate();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		finally {
			statePanel.reset();
			chatPanel.getController().toggleUI(false);
			dicePanel.setVisible(false);
			leaderPanel.setVisible(false);
			boardPanel.getPlayers().clear();
			globalColor.setColor(Color.GRAY);	// back to default
			resetClient();
			repaint();	
		}
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
			ErrorUtils.showTimeOutError(this);
		}
	}
	
	/**
	 * Updates keyboard to the specified state.
	 * @param state
	 */
	public void updateKey(String state) {
		key = new Keyboard(minis.get(state));
		key.setKFM(KeyboardFocusManager.getCurrentKeyboardFocusManager());
		minis.get(state).setKey(key);
	}
	
	public void colorize(JComponent c) {
		globalColor.add(c);
	}
	
	public void colorize(JComponent c, LineBorder border) {
		globalColor.add(c, border);
	}
	
	public void colorize(JComponent c, LineBorder border, int fontSize) {
		GameUtils.customizeComp(c, null, globalColor.getColor(), fontSize);
		globalColor.add(c, border);
	}
	
	// mutator methods
	
	public void setHost(String host) {
		this.host = host;
	}
	
	public void setPort(int port) {
		this.port = port;
	}
	
	public void setClient(Client client) {
		this.client = client;
	}
	
	public void setMini(String curMini) {
		this.curMini = curMini;
	}

	
	// accessor methods
	
	public Client getClient() {
		return client;
	}
	
	public String getHost() {
		return host;
	}
	
	public GlobalColor getGlobalColor() {
		return globalColor;
	}
	
	public int getPort() {
		return port;
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
	
	public LeaderBoardPanel getLeaderPanel() {
		return leaderPanel;
	}
	
	public DicePanel getDicePanel() {
		return dicePanel;
	}
	
	public ErrorHandler getErrorHandler() {
		return errorHandler;
	}
	
	public String getMini() {
		return curMini;
	}
	
	public ConcurrentHashMap<String, BaseMiniPanel> getMinis() {
		return minis;
	}
	
	/**
	 * This class is responsible for handling errors when received from
	 * the server.
	 * @author David Kramer
	 *
	 */
	public class ErrorHandler extends IOHandler {
		
		public void send(JSONObject out) {}

		public void receive(JSONObject in) {
			ErrorUtils.processServerError(ClientApp.this, in);
		}
	}
	
	/**
	 * Main method. Starts up client GUI app.
	 * @param args
	 */
	@SuppressWarnings("unused")
	public static void main(String[] args) {
		ClientApp app = new ClientApp();
	}
}
