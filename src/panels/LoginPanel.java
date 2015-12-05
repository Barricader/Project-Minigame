package panels;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.net.ConnectException;
import java.util.concurrent.ConcurrentHashMap;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;

import org.json.simple.JSONObject;

import client.ClientApp;
import client.IOHandler;
import gameobjects.NewPlayer;
import util.ErrorUtils;
import util.GameUtils;
import util.Keys;
import util.NewJSONObject;

/**
 * The Login panel class allows the user to enter their name and connect to the server.
 * This class also contains the lobby, which shows players that are already connected 
 * to the server. Once 2+ players are connected, the server echoes a countdown timer,
 * which will is also displayed here, before the transition is made to the board.
 * @author David Kramer
 *
 */
public class LoginPanel extends JPanel {
	private ClientApp app;
	private Controller controller;
	private NewPlayer clientPlayer;	// player created by this client
	private JLabel titleLabel;
	private JLabel timerLabel;
	private JLabel nameLabel;
	private JTextField nameField;
	private JButton joinBtn;
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
		setBorder(new LineBorder(Color.LIGHT_GRAY));
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
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		c.weightx = 1.0;
		c.gridwidth = 8;
		c.gridy = 0;
		c.gridheight = 1;
		add(titleLabel, c);
		
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
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.ipadx = 0;
		c.weightx = 1.0;
		c.gridwidth = 1;
		c.gridy = 1;
		c.gridheight = 1;
		add(nameLabel, c);
		
		// name field
		c.gridx = 1;
		c.gridy = 1;
		add(nameField, c);
		
		// join btn
		c.gridx = 2;
		c.gridy = 1;
		add(joinBtn, c);
		
		// timer label
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		c.gridwidth = 8;
		c.gridy = 3;
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
		titleLabel.setFont(new Font("Courier New", Font.BOLD, 18));
		titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
		
		nameLabel = new JLabel("Name:");
		nameLabel.setFont(new Font("Courier New", Font.BOLD, 20));
		nameLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		
		timerLabel = new JLabel(" ");
		timerLabel.setFont(new Font("Courier New", Font.BOLD, 30));
		timerLabel.setForeground(GameUtils.colorFromHex("#E82539"));
		timerLabel.setHorizontalAlignment(SwingConstants.CENTER);
		
		nameField = new JTextField(10);
		nameField.setFont(new Font("Courier New", Font.BOLD, 20));
		nameField.addKeyListener(new KeyListener() {
			
			public void keyReleased(KeyEvent e) {
				if (!nameField.getText().isEmpty()) {
					joinBtn.setEnabled(true);
					if (e.getKeyChar() == '\n') {
						controller.joinPlayer();
					}
				} else {
					joinBtn.setEnabled(false);
				}
			}
			// unused
			public void keyTyped(KeyEvent e) {}
			public void keyPressed(KeyEvent e) {}
		});
		
		joinBtn = new JButton("Join Game");
		joinBtn.setFont(new Font("Courier New", Font.BOLD, 20));
		joinBtn.setEnabled(false);
		joinBtn.addActionListener( e -> {
			controller.joinPlayer();
		});
		
		lobby = new Lobby(app);
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
			System.out.println("login panel received: " + in.toJSONString());
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
				}
				app.getBoardPanel().addPlayer(newPlayer);
				lobby.updateList();
			}
		}
		
		/**
		 * Removes player from board and player list.
		 * @param player - Player to remove
		 */
		public void removePlayer(NewPlayer player) {
			if (player.getName().equals(app.getBoardPanel().getClientPlayer().getName())) {
				app.reset();
			}
			app.getBoardPanel().getPlayers().remove(player.getName());
			lobby.updateList();
		}
		
		/**
		 * Joins the player to the server with the specified name.
		 */
		public void joinPlayer() {
			if (!app.getClient().isConnected()) {
				try {
					app.connectClient();
				} catch (ConnectException e) {
					ErrorUtils.showConnectionError(app);
				} catch (IOException e) {
					ErrorUtils.showConnectionError(app);
				}
			}
			
			NewJSONObject obj = new NewJSONObject(app.getClient().getID(), Keys.Commands.ADD_PLAYER);
			String name = nameField.getText();
			clientPlayer = new NewPlayer(name, app.getClient().getID());
			// setup location for board
			obj.put(Keys.PLAYER, clientPlayer.toJSONObject());
			send(obj);
			nameField.setText(""); 	// clear out
		}
		
		/**
		 * Disconnects a player and clears them out from server.
		 */
		public void disconnectPlayer() {
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
			}
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
