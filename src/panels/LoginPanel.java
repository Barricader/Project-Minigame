package panels;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.net.ConnectException;
import java.util.HashMap;

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
import newserver.Keys;
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
	private JButton joinBtn;
	private JTextField nameField;
	private NewPlayer clientPlayer;	// the player that is created by the client
	
	public LoginPanel(ClientApp app) {
		this.app = app;
		controller = new Controller();
		init();
	}
	
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
		
		// name label
		c.insets = new Insets(20, 20, 20, 20);	// margin
		c.anchor = GridBagConstraints.CENTER;
		c.gridx = 0;
		c.gridwidth = 1;
		c.weightx = 0.0;
		c.gridy = 1;
		c.weighty = 0.0;
		add(nameLabel, c);

		// name field
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 1;
		c.weightx = 1.0;
		c.gridy = 1;
		c.ipady = 20;
		add(nameField, c);
		
		// join btn
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 3;
		c.weightx = 0.1;
		c.gridy = 1;
		add(joinBtn, c);
		
		// player lobby
		c.anchor = GridBagConstraints.SOUTH;
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		c.weightx = 1.0;
		c.gridwidth = 4;
		c.gridy = 2;
		c.weighty = 10.0;
		add(lobbyPanel, c);
		
	}
	
	private void createComponents() {
		titleLabel = new JLabel("<Project Mini-Game>");
		titleLabel.setFont(new Font("Courier New", Font.BOLD, 20));
		titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
		
		nameLabel = new JLabel("Enter Player Name: ");
		nameLabel.setFont(new Font("Courier New", Font.BOLD, 20));
		nameLabel.setHorizontalAlignment(SwingConstants.LEFT);
		
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
		
		lobbyPanel = new LobbyPanel();
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
	
	public class Controller extends IOHandler {
		
		public Controller() {}

		public void send(JSONObject out) {
			app.getClient().getIOHandler().send(out);
		}

		public void receive(JSONObject in) {
			NewPlayer newPlayer = NewPlayer.fromJSON(in);
			addPlayer(newPlayer);
		}
		
		/**
		 * Adds a new player to the player list / board, if possible.
		 * @param newPlayer
		 */
		public void addPlayer(NewPlayer newPlayer) {
			if (newPlayer.getID() == -1) {	// shouldn't add yet!
				return;
			}
			
			// new player belongs to this client!
			if (newPlayer.getName().equals(clientPlayer.getName())) {
				clientPlayer = newPlayer;
				newPlayer = clientPlayer;
				nameField.setText("");
				nameField.setEnabled(false);
				removeActionListeners(joinBtn);
				joinBtn.setText("Disconnect");
				joinBtn.addActionListener(e -> {
					disconnectPlayer();
				});
				
			}
		
			HashMap<String, NewPlayer> players = app.getBoardPanel().getPlayers();
			boolean canAdd = true;
			for (String name : players.keySet()) {
				NewPlayer p = players.get(name);
				if (p.getName().equals(newPlayer.getName())) {	// don't add duplicates!
					canAdd = false;
				}
			}
			if (canAdd) {
				players.put(newPlayer.getName(), newPlayer);
				lobbyPanel.addPlayerToList(newPlayer);
			}
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
			obj.put(Keys.PLAYER, clientPlayer.toJSONObject());
			send(obj);
		}
		
		/**
		 * Disconnects the player.
		 */
		public void disconnectPlayer() {
			app.getConnPanel().getController().reset();
			lobbyPanel.getPlayerList().removeAll();
			removeActionListeners(joinBtn);
			joinBtn.setText("Join Game");
			joinBtn.addActionListener(e -> {
				joinPlayer();
			});
		}
		
		/**
		 * Remove action listeners from specified button
		 * @param btn - Button to remove action listeners from.
		 */
		private void removeActionListeners(JButton btn) {
			for (ActionListener a : btn.getActionListeners()) {
				btn.removeActionListener(a);
			}
		}
	}
}
