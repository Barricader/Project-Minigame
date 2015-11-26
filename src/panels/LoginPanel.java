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
import java.net.Socket;
import java.net.UnknownHostException;

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
import newserver.Server;

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
		
			public void keyTyped(KeyEvent e) {
				if (!nameField.getText().isEmpty()) {
					joinBtn.setEnabled(true);
				} else {
					joinBtn.setEnabled(false);
				}
			}
			// unused
			public void keyReleased(KeyEvent e) {}
			public void keyPressed(KeyEvent e) {}
		});
		
		joinBtn = new JButton("Join Game");
		joinBtn.setFont(new Font("Courier New", Font.BOLD, 20));
		joinBtn.setEnabled(false);
		joinBtn.addActionListener( e -> {
			controller.joinAction();
		});
		
		lobbyPanel = new LobbyPanel();
	}
	
	public Controller getController() {
		return controller;
	}
	
	public LobbyPanel getLobbyPanel() {
		return lobbyPanel;
	}
	
	public class Controller extends IOHandler {
		
		public Controller() {
			
		}

		public void send(JSONObject out) {
			try {
				app.getClient().getOutputStream().writeObject(out);
			} catch (IOException e) {
				System.out.println("failed to send. Client may not be connected!");
			}
		}

		public void receive(JSONObject in) {
			if (in.containsKey("addPlayer")) {
				if (in.get("name").equals(clientPlayer.getName())) {	// player belongs to this client
					//NewPlayer.updateFromJSON(clientPlayer, in);
					// TODO: Add player here
					System.out.println("Client player was updated!");
				}
			}
		}
		
		public void joinAction() {
			String name = nameField.getText();
			if (!app.getClient().isConnected()) {
				try {
					app.getClient().connect(new Socket(Server.HOST, Server.PORT));
				} catch (UnknownHostException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				app.getClient().start();	
			}
			if (!name.isEmpty()) {
				clientPlayer = new NewPlayer(name, -1);
				send(clientPlayer.toJSONObject());
				System.out.println("Should be sending >: " + clientPlayer.toJSONObject().toJSONString());
			}
		}
		
		public void nameFieldAction() {
			
		}
		
		
	}
}
