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
	private LobbyPanel lobbyPanel;	// show waiting players when we login
	private JLabel titleLabel;
	private JLabel nameLabel;
	private JLabel timerLabel;
	private JButton joinBtn;
	private JTextField nameField;
	private NewPlayer clientPlayer;	// the player that is created by the client
	
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
		setBorder(new LineBorder(Color.LIGHT_GRAY));
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		
		// title label
		c.anchor = GridBagConstraints.NORTH;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 1.0;
		c.gridx = 0;
		c.gridwidth = 4;
		c.gridy = 0;
		c.ipady = 20;
		c.weighty = 0.4;
		add(titleLabel, c);
		
		// countdown timer
		c.gridx = 0;
		c.gridy = 1;
		c.ipady = 0;
		add(timerLabel, c);
		
		// name label
		c.insets = new Insets(20, 20, 20, 20);	// margin
		c.anchor = GridBagConstraints.CENTER;
		c.gridx = 0;
		c.gridwidth = 1;
		c.weightx = 0.0;
		c.gridy = 2;
		c.weighty = 0.0;
		add(nameLabel, c);

		// name field
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 1;
		c.weightx = 1.0;
		c.gridy = 2;
		c.ipady = 20;
		add(nameField, c);
		
		// join btn
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 3;
		c.weightx = 0.1;
		c.gridy = 2;
		add(joinBtn, c);
		
		// player lobby
		c.anchor = GridBagConstraints.SOUTH;
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		c.weightx = 1.0;
		c.gridwidth = 4;
		c.gridy = 3;
		c.weighty = 10.0;
		add(lobbyPanel, c);
		
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
		
		timerLabel = new JLabel("Start Timer: XX");
		timerLabel.setVisible(false);
		timerLabel.setFont(new Font("Courier New", Font.BOLD, 20));
		timerLabel.setForeground(Color.RED);
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
			if (showWarningDisconnect()) {
				NewJSONObject obj = new NewJSONObject(app.getClient().getID(), Keys.Commands.REM_PLAYER);
				obj.put(Keys.PLAYER, clientPlayer.toJSONObject());
				send(obj);
				
				// clear out old players
				clientPlayer = null;
				app.resetClient();
				app.getStatePanel().getLoginPanel().getLobbyPanel().getPlayerList().removeAll();
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
		
		/**
		 * Shows a confirm dialog to ensure the player wants to disconnect
		 * @return true if they hit ok, false if they hit cancel.
		 */
		private boolean showWarningDisconnect() {
			int choice = JOptionPane.showConfirmDialog(app, "Are you sure you want to leave?",
					"Confirm", JOptionPane.OK_CANCEL_OPTION);
			
			return choice == 0;	// they hit ok
		}
	}
}
