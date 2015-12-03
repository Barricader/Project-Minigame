package panels;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.net.ConnectException;
import java.util.concurrent.ConcurrentHashMap;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;

import org.json.simple.JSONObject;

import client.ClientApp;
import client.IOHandler;
import gameobjects.NewPlayer;
import newserver.Server;
import util.ErrorUtils;
import util.GameUtils;
import util.Keys;
import util.NewJSONObject;

/**
 * Login panel that allows the user to enter their name and connect to the server.
 * This class will also contain the lobby, which shows players that are already
 * connected to the server.
 * @author David Kramer
 *
 */
public class LoginPanel extends JPanel {
	private static final Dimension SIZE = new Dimension(400, 200);
	private ClientApp app;
	private Controller controller;
	private NewPlayer clientPlayer;	// the player that is created by the client
	private LobbyPanel lobbyPanel;	// show waiting players when we login
	private JLabel titleLabel;
	private JLabel nameLabel;
	private JLabel timerLabel;
	private JButton joinBtn;
	// server config setting stuff
	private JPanel settingsPanel;
	private JLabel settingsLabel;
	private JLabel addressLabel;
	private JLabel portLabel;
	private JTextField addressField;
	private JTextField portField;
	private JTextField nameField;
	private JButton applyBtn;
	
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
		setBorder(new LineBorder(Color.RED));
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(5, 10, 5, 10);
		
		// title
		c.anchor = GridBagConstraints.NORTH;
		c.gridx = 0;
		c.gridwidth = 10;
		c.gridy = 0;
		add(titleLabel, c);
		
		// timer label
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.CENTER;
		c.gridx = 0;
		c.gridy = 1;
//		c.weighty = 1.0;
		add(timerLabel, c);
		
		// name label
		c.gridx = 0;
		c.gridwidth = 2;
		c.gridy = 2;
//		c.weighty = 1.0;
		c.ipady = 10;
		add(nameLabel, c);
	
		// name field
		c.gridx = 2;
		c.gridwidth = 6;
		c.gridy = 2;
		add(nameField, c);
		
		// join btn
		c.gridx = 8;
		c.gridwidth = 2;
		c.gridy = 2;
		add(joinBtn, c);
		
		// lobby panel
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.SOUTHWEST;
		c.gridx = 0;
		c.gridwidth = 8;
		c.weightx = 0.8;
		c.gridy = 3;
		c.ipady = 85;
		add(lobbyPanel, c);
		
		// settings panel
		c.anchor = GridBagConstraints.SOUTHEAST;
		c.gridx = 8;
		c.gridwidth = 2;
		c.weightx = 0.2;
		c.gridy = 3;
		c.ipady = 40;
		add(settingsPanel, c);
		
		c.anchor = GridBagConstraints.SOUTH;
		c.gridy = 4;
//		c.weighty = 1.0;
		
		add(Box.createVerticalStrut(10), c);
	
	}
	
	/**
	 * Creates GUI components.
	 */
	private void createComponents() {
		titleLabel = new JLabel("<Project Mini-Game>");
		titleLabel.setFont(new Font("Courier New", Font.BOLD, 20));
		titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
		
		nameLabel = new JLabel("Enter Player Name: ");
		nameLabel.setFont(new Font("Courier New", Font.BOLD, 20));
		nameLabel.setHorizontalAlignment(SwingConstants.LEFT);
		
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
		
		lobbyPanel = new LobbyPanel(app);
		
		createSettingsPanel();
	}
	
	private void createSettingsPanel() {
		settingsLabel = new JLabel("Connection Settings: ");
		settingsLabel.setHorizontalAlignment(SwingConstants.CENTER);
		
		addressLabel = new JLabel("IP Address: ");
		addressLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		addressField = new JTextField();
		addressField.setText(Server.HOST);
		addressField.addKeyListener(handleKey());
		
		portLabel = new JLabel("Port No: ");
		portLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		portField = new JTextField("" + Server.PORT);
		portField.addKeyListener(handleKey());
		
		applyBtn = new JButton("Apply");
		applyBtn.setEnabled(false);	// disable initially, until we change values
		applyBtn.addActionListener( e -> {
			applySettings();
		});
		
		settingsPanel = new JPanel();
		settingsPanel.setPreferredSize(new Dimension(100, 100));
		settingsPanel.setMaximumSize(new Dimension(100, 100));
		settingsPanel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(0, 5, 0, 5);
		
		// settings label
		c.anchor = GridBagConstraints.NORTH;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridwidth = 2;
		c.gridy = 0;
		c.weighty = 1.0;
		settingsPanel.add(settingsLabel, c);
		
		// address label
		c.anchor = GridBagConstraints.CENTER;
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		c.weightx = 0.3;
		c.gridwidth = 1;
		c.gridy = 1;
		c.weighty = 1.0;
		settingsPanel.add(addressLabel, c);
		
		// address field
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 1;
		c.weightx = 0.5;
		c.gridy = 1;
		settingsPanel.add(addressField, c);
		
		// port label
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		c.weightx = 0.2;
		c.gridy = 2;
		settingsPanel.add(portLabel, c);
		
		// port field
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 1;
		c.weightx = 0.9;
		c.gridy = 2;
		settingsPanel.add(portField, c);
		
		// apply btn
		c.anchor = GridBagConstraints.SOUTH;
		c.gridx = 0;
		c.weightx = 1.0;
		c.gridwidth = 2;
		c.gridy = 3;
		c.weighty = 1.0;
		settingsPanel.add(applyBtn, c);
		
		settingsPanel.setBorder(new LineBorder(Color.LIGHT_GRAY));
		
	}
	
	private KeyListener handleKey() {
		KeyListener key = new KeyListener() {
			
			public void keyTyped(KeyEvent e) {
				applyBtn.setEnabled(true);
			}
			
			public void keyReleased(KeyEvent e) {}
		
			public void keyPressed(KeyEvent e) {}
		};
		return key;
	}
	
	private void applySettings() {
		// make sure fields aren't empty
		String address = addressField.getText();
		String port = portField.getText();
		int portNo = 0;
		
		if (address.isEmpty() || port.isEmpty()) {
			ErrorUtils.showCustomError(app, "Connection settings fields cannot be blank!");
			return;
		} else {
			try {
				portNo = Integer.parseInt(port);
			} catch (NumberFormatException e) {
				ErrorUtils.showInvalidPortError(app);
				return;
			}
			
			if (portNo < 1024 || portNo > 65536) {
				ErrorUtils.showInvalidPortError(app);
				return;
			}
		}
		// we're good. change up values!
		app.setHost(address);
		app.setPort(portNo);
		applyBtn.setEnabled(false);	// disable, until value changes again in the future!
	
	}
	
	public Controller getController() {
		return controller;
	}
	
	public LobbyPanel getLobbyPanel() {
		return lobbyPanel;
	}
	
	public NewPlayer getClientPlayer() {
		return clientPlayer;
	}
	
	/**
	 * IOHandler for handling player add/remove as well as updating the countdown
	 * timer.
	 * @author David Kramer
	 *
	 */
	public class Controller extends IOHandler {
		
		public Controller() {}

		public void send(JSONObject out) {
			app.getClient().getIOHandler().send(out);
		}

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
				lobbyPanel.updateList();
			}
		}
		
		/**
		 * Removes player from board and player list.
		 * @param player - Player to remove
		 */
		public void removePlayer(NewPlayer player) {
			app.getBoardPanel().getPlayers().remove(player.getName());
			lobbyPanel.updateList();
		}
		
		/**
		 * Joins the player to the server with the specified name.
		 */
		public void joinPlayer() {
			if (!app.getClient().isConnected()) {
				try {
					app.connectClient();
				} catch (ConnectException e) {
					app.getConnPanel().showConnectionError();
				} catch (IOException e) {
					app.getConnPanel().showConnectionError();
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
				
				// clear out old players
				clientPlayer = null;
				app.resetClient();
				app.getStatePanel().getLoginPanel().getLobbyPanel().removeAll();
				app.getBoardPanel().getPlayers().clear();
				app.repaint();
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
				timerLabel.setVisible(false);
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
