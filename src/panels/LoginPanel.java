package panels;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.net.ConnectException;
import java.util.concurrent.ConcurrentHashMap;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;

import org.json.simple.JSONObject;

import client.ClientApp;
import client.IOHandler;
import gameobjects.NewPlayer;
import newserver.ServerClient;
import util.DarkButton;
import util.ErrorUtils;
import util.GameUtils;
import util.Keys;
import util.NewJSONObject;
import util.PlayerStyles;

/**
 * The Login panel class allows the user to enter their name and connect to the server.
 * This class also contains the lobby, which shows players that are already connected 
 * to the server. Once 2+ players are connected, the server echoes a countdown timer,
 * which will is also displayed here, before the transition is made to the board.
 * @author David Kramer
 *
 */
public class LoginPanel extends JPanel {
	private static final long serialVersionUID = -459019336212020379L;
	private ClientApp app;
	private Controller controller;
	private NewPlayer clientPlayer;	// player created by this client
	private JLabel titleLabel;
	private JLabel timerLabel;
	private JLabel nameLabel;
	private JTextField nameField;
	private DarkButton joinBtn;
	private DarkButton startBtn;	// bypass countdown
	private Lobby lobby;
	private SettingsPanel settingsPanel;
	
	/**
	 * Constructs a new LoginPanel with a link to the ClientApp.
	 * @param app - Target client app.
	 */
	public LoginPanel(ClientApp app) {
		this.app = app;
		controller = new Controller();
		init();
	}
	
	/**
	 * Lays out GUI components using GridBagLayout.
	 */
	private void init() {
		createComponents();
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(10, 10, 10, 10);	// 10 px margin
		
		// title label
		c.anchor = GridBagConstraints.NORTH;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridwidth = 2;
		c.weightx = 1.0;
		c.gridy = 0;
		c.gridheight = 1;
		add(titleLabel, c);
		
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 2;
		c.gridy = 0;
		add(startBtn, c);
		
		// settings panel
		c.anchor = GridBagConstraints.NORTHEAST;
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 8;
		c.weightx = 0.4;
		c.ipadx = 40;
		c.gridwidth = 2;
		c.gridy = 0;
		c.gridheight = 4;
		add(settingsPanel, c);
		
		// name label
		c.anchor = GridBagConstraints.CENTER;
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		c.ipadx = 0;
		c.weightx = 1.0;
		c.gridwidth = 1;
		c.gridy = 1;
		c.gridheight = 1;
		add(nameLabel, c);
		
		// name field
		c.fill = GridBagConstraints.BOTH;
		c.ipadx = 10;
		c.gridx = 1;
		c.gridy = 1;
		add(nameField, c);
		
		// join btn
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 2;
		c.gridy = 1;
		c.ipady = 5;
		add(joinBtn, c);
		
		// timer label
		c.gridx = 0;
		c.gridwidth = 8;
		c.gridy = 3;
		c.ipady = 0;
		add(timerLabel, c);
		
		// lobby
		c.anchor = GridBagConstraints.SOUTH;
		c.gridx = 0;
		c.gridwidth = 10;
		c.ipadx = 0;
		c.gridy = 4;
		c.weighty = 1.0;
		add(lobby, c);
	}
	
	/**
	 * Creates GUI components for this LoginPanel.
	 */
	private void createComponents() {
		titleLabel = new JLabel("<Project Mini-Game>");
		titleLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		app.colorize(titleLabel, null, 18);
		
		nameLabel = new JLabel("Name:");
		nameLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		app.colorize(nameLabel, null, 20);
		
		timerLabel = new JLabel(" ");
		timerLabel.setHorizontalAlignment(SwingConstants.CENTER);
		app.colorize(timerLabel, null, 30);
		
		nameField = new JTextField(ServerClient.MAX_NAME_LENGTH);
		nameField.setCaretColor(Color.WHITE);
		nameField.setTransferHandler(null);	// disable copy paste
		
		app.colorize(nameField, new LineBorder(Color.CYAN), 20);
		nameField.addKeyListener(new KeyAdapter() {
			
			public void keyReleased(KeyEvent e) {
				String text = nameField.getText();
				text = text.trim();	// don't allow whitespace for name!
				if (!text.isEmpty()) {
					joinBtn.setEnabled(true);
				} else {
					joinBtn.setEnabled(false);
				}
			}
			
			public void keyPressed(KeyEvent e) {
				// if they hit back space, we can allow the msg field to be editable.
				String text = nameField.getText();
				text = text.trim();	// make sure whitespace is removed!
				if (text.length() >= ServerClient.MAX_NAME_LENGTH 
						&& e.getKeyCode() != KeyEvent.VK_BACK_SPACE) {
					nameField.setEditable(false);
				} else {
					nameField.setEditable(true);
				}
				nameField.setText(text);
				
				if (!text.isEmpty()) {
					joinBtn.setEnabled(true);
					if (e.getKeyChar() == '\n') {
						controller.joinPlayer();
					}
				} else {
					joinBtn.setEnabled(false);
				}
			}
		});
		
		joinBtn = new DarkButton("Join Game");
		joinBtn.setEnabled(false);
		joinBtn.setBorder(new LineBorder(app.getGlobalColor().getColor()));
		app.colorize(joinBtn, (LineBorder)joinBtn.getBorder(), 20);
		
		joinBtn.addActionListener( e -> {
			controller.joinPlayer();
		});
		
		startBtn = new DarkButton("Force Start");
		startBtn.setVisible(false);
		startBtn.addActionListener( e -> {
			controller.forceStart();
		});
		app.colorize(startBtn, new LineBorder(app.getGlobalColor().getColor()), 20);
		
		lobby = new Lobby(app);
		app.colorize(lobby, (LineBorder)lobby.getBorder());
		settingsPanel = new SettingsPanel(app);
	}
	
	
	// accessor methods
	
	public Lobby getLobby() {
		return lobby;
	}
	
	public Controller getController() {
		return controller;
	}
	
	public NewPlayer getClientPlayer() {
		return clientPlayer;
	}
	
	/**
	 * Controller for handling connection events such as adding / removing players,
	 * as well as the initial countdown to the start of the game.
	 * @author David Kramer
	 *
	 */
	public class Controller extends IOHandler {
		
		public Controller() {}

		public void send(JSONObject out) {
			app.getClient().getIOHandler().send(out);
		}

		/**
		 * Processes incoming JSONObjects that deal with players or game
		 * transitions.
		 */
		public void receive(JSONObject in) {
			String cmd = (String) in.get(Keys.CMD);	
			NewPlayer player = NewPlayer.fromJSON(in);
			
			switch (cmd) {
			case Keys.Commands.ADD_PLAYER:
				addPlayer(player);
				break;
			case Keys.Commands.REM_PLAYER:
				removePlayer(player);
				break;
			case Keys.Commands.TIMER:
				JSONObject timer = (JSONObject) in.get(cmd);
				boolean reset = false;
				int timeLeft = 0;
				
				// did we reset?
				if (timer.get(Keys.TIME).equals("reset")) {
					reset = true;
				} else {
					timeLeft = Integer.parseInt(timer.get(Keys.TIME).toString());	
				}
				updateTimer(reset, timeLeft);
			}
			
		}
		
		/**
		 * Adds a new player to the player list / board, if possible.
		 * @param newPlayer
		 */
		public void addPlayer(NewPlayer newPlayer) {
			if (newPlayer.getID() == -1) {	// shouldn't add yet!
				return;
			}
			
			nameField.setText("");
			nameField.setEnabled(false);
			GameUtils.clearActions(joinBtn);
			joinBtn.setText("Disconnect");
			joinBtn.addActionListener(e -> {
				disconnectPlayer();
			});
		
			ConcurrentHashMap<String, NewPlayer> players = app.getBoardPanel().getPlayers();
			boolean canAdd = true;
			for (String name : players.keySet()) {
				NewPlayer p = players.get(name);
				if (p.getName().equals(newPlayer.getName())) {	// don't add duplicates!
					canAdd = false;
				}
			}
			if (canAdd) {
				if (newPlayer.getName().equals(clientPlayer.getName())) {	// player belongs to this client!
					clientPlayer = newPlayer;
					app.getBoardPanel().setClientPlayer(clientPlayer);
					app.getGlobalColor().setColor(PlayerStyles.getColor(clientPlayer.getStyleID()));
					app.getDicePanel().colorizeDice(PlayerStyles.getColor(clientPlayer.getStyleID()));
				}
				app.getBoardPanel().addPlayer(newPlayer);
				lobby.updateList();
			}
			startBtn.setVisible(players.size() >= 2);
		}
		
		/**
		 * Removes player from board and player list.
		 * @param player - Player to remove
		 */
		public void removePlayer(NewPlayer player) {
			NewPlayer p = app.getBoardPanel().getClientPlayer();
			if (p != null) {
				if (player.getName().equals(p.getName())) {
					Runnable r = () -> { app.reset(); };
					new Thread(r).start();	// disconnect everything in background
					ErrorUtils.showDisconnectMessage(app);
					app.reset();
				}	
			}
			ConcurrentHashMap<String, NewPlayer> players = app.getBoardPanel().getPlayers();
			players.remove(player.getName());
			lobby.updateList();
			startBtn.setVisible(players.size() >= 2);
		}
		
		/**
		 * Joins the player to the server with the specified name.
		 */
		@SuppressWarnings("unchecked")
		public void joinPlayer() {
			if (!app.getClient().isConnected() && !nameField.getText().isEmpty()) {
				try {
					app.connectClient();
				} catch (ConnectException e) {
					ErrorUtils.showConnectionError(app);
					joinBtn.setEnabled(false);
				} catch (IOException e) {
					ErrorUtils.showConnectionError(app);
					joinBtn.setEnabled(false);
				}
			}
			
			NewJSONObject obj = new NewJSONObject(app.getClient().getID(), Keys.Commands.ADD_PLAYER);
			String name = nameField.getText();
			clientPlayer = new NewPlayer(name, app.getClient().getID());
			// setup location for board
			obj.put(Keys.PLAYER, clientPlayer.toJSONObject());
			send(obj);
			nameField.setText(""); 	// clear out
//			joinBtn.setEnabled(false);
		}
		
		/**
		 * Disconnects a player and clears them out from server.
		 */
		@SuppressWarnings("unchecked")
		public boolean disconnectPlayer() {
			if (ErrorUtils.showDisconnectWarning(app)) {
				NewJSONObject obj = new NewJSONObject(app.getClient().getID(), Keys.Commands.REM_PLAYER);
				obj.put(Keys.PLAYER, clientPlayer.toJSONObject());
				send(obj);
				// setup to join again
				GameUtils.clearActions(joinBtn);
				nameField.setEnabled(true);
				joinBtn.setText("Join Game");
				joinBtn.setEnabled(false);
				joinBtn.addActionListener(e -> {
					joinPlayer();
				});
				return true;
			}
			return false;
		}
		
		/**
		 * Sends message to the server to skip the countdown timer
		 * and immediately enter the game.
		 */
		public void forceStart() {
			NewJSONObject obj = new NewJSONObject(app.getClient().getID(), Keys.Commands.FORCE_START);
			send(obj);
		}
		
		/**
		 * Updates the time on the countdown timer
		 * @param reset - flag to show / hide the timer
		 * @param timeLeft - Time remaining
		 */
		private void updateTimer(boolean reset, int timeLeft) {
			if (reset) {
				timerLabel.setText(" ");	// don't setVisible(false), but blank text, otherwise it can mess w/ layout
			} else {
				timerLabel.setVisible(true);
				String endStr = " seconds";
				
				if (timeLeft == 1) {	// sec instead of secs
					endStr = " second";
				}
				
				timerLabel.setText("Starting in: " + timeLeft + endStr);	
			}
		}
	}
}
