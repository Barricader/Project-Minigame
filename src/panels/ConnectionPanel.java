package panels;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.net.Socket;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;

import client.Client;
import client.ClientApp;
import client.IOHandler;
import newserver.Server;
import util.GameUtils;

/**
 * Simple connection panel that allows the user to connect / disconnect and provides
 * a connection status label at the bottom part of this panel.
 * @author David Kramer
 *
 */
public class ConnectionPanel extends JPanel {
	private static final Dimension SIZE = new Dimension(200, 30);
	private ClientApp app;
	private Controller controller;
	
	private JButton connectBtn;
	private JButton testAddPlayerBtn;		// TODO REMOVE ME. This is a test!
	private JLabel statusLabel;
	
	public ConnectionPanel(ClientApp app) { 
		this.app = app;
		controller = new Controller();
		init();
		setPreferredSize(SIZE);
		setMinimumSize(SIZE);
	}
	
	private void init() {
		createComponents();
		
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(5, 5, 5, 5);	// margin
		
		// status label
		c.anchor = GridBagConstraints.NORTHWEST;
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		c.weightx = 1.0;
		c.gridy = 0;
		c.weighty = 1.0;
		add(statusLabel, c);
		
		// add player button
		c.anchor = GridBagConstraints.NORTH;
		c.gridx = 1;
		c.weightx = 0.0;
		c.gridy = 0;
		c.weighty = 1.0;
		add(testAddPlayerBtn, c);
		
		// connect button
		c.anchor = GridBagConstraints.NORTHEAST;
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 2;
		c.ipadx = 100;
		c.weightx = 0;
		c.gridy = 0;
		c.ipady = 10;
		add(connectBtn, c);
	}
	
	private void createComponents() {
		connectBtn = new JButton("Connect");
		connectBtn = controller.connect();
		
		// TEST -> Remove me later!
		testAddPlayerBtn = new JButton("Test: Add Player");
		testAddPlayerBtn = controller.addPlayer();
		
		statusLabel = new JLabel("Status: Not Connected!");
		statusLabel.setHorizontalAlignment(SwingConstants.LEFT);
		setBorder(new LineBorder(Color.LIGHT_GRAY));
		setLayout(new GridBagLayout());
	}
	
	public void showConnectionError() {
		JOptionPane.showMessageDialog(app, "Unable to connect... Server may not have been started!"
				, "Connection Error", JOptionPane.ERROR_MESSAGE);
	}
	
	
	// Accessor methods
	
	public JButton getConnectBtn() {
		return connectBtn;
	}
	
	public JLabel statusLabel() {
		return statusLabel;
	}
	
	public Controller getController() {
		return controller;
	}
	
	public class Controller extends IOHandler {
		public static final int STATUS_ERROR = -1;
		public static final int STATUS_DISCONNECTED = 0;
		public static final int STATUS_CONNECTED = 1;
		
		private IOHandler ioHandler;
		
		public Controller() {
		}
		
		/**
		 * Clears out any action listeners on a specified JButton.
		 * @param btn - JButton to clear any action listeners on.
		 */
		private void clearActions(JButton btn) {
			for (ActionListener action : btn.getActionListeners()) {
				btn.removeActionListener(action);
			}
		}	
		
		public void receive(String in) {
			if (in.equals("!connection -1")) {
				updateStatus(STATUS_ERROR);
			} else if (in.equals("!connection 0")) {
				updateStatus(STATUS_DISCONNECTED);
			} else if (in.equals("!connection 1")) {
				updateStatus(STATUS_CONNECTED);
			}
		}

		public void send(String out) {
			app.getClient().getIOHandler().send(out);
		}
		
		/**
		 * 
		 * @return connectBtn with connect action.
		 */
		public JButton connect() {
			clearActions(connectBtn);
			connectBtn.setText("Connect");
			connectBtn.addActionListener( e -> {
				try {
					if (app.getClient() == null) {
						app.setClient(new Client(app));
					}
					if (!app.getClient().isConnected()) {
						app.getClient().connect(new Socket(Server.HOST, Server.PORT));
						app.getClient().start();	
					}
				} catch (Exception e1) {
					e1.printStackTrace();
					showConnectionError();
					updateStatus(STATUS_ERROR);
				}
			});
			return connectBtn;
		}
		
		/**
		 * 
		 * @return connectBtn with disconnect action.
		 */
		public JButton disconnect() {
			clearActions(connectBtn);
			connectBtn.setText("Disconnect");
			connectBtn.addActionListener( e -> {
				try {
					Client c = app.getClient();
					c.terminate();
					app.resetClient();
					controller.updateStatus(STATUS_DISCONNECTED);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			});
			return connectBtn;
		}
		
		/**
		 * TODO - This is a test! Remove this later!
		 * @return testAddPlayerBtn with addPlayer functionality
		 */
		public JButton addPlayer() {
			clearActions(testAddPlayerBtn);
			testAddPlayerBtn.addActionListener( e-> {
				IOHandler test = app.getClient().getIOHandler();
				System.out.println("Should be sending addPlayer cmd: " + test);
				if (test != null) {
					System.out.println("Really should be sending!");
					send("!addPlayer");
				} else {
					System.out.println("Problem when sending !addPlayer cmd!");
				}
			});
			return testAddPlayerBtn;
		}
		
		public void updateStatus(int statusCode) {
			Color statusColor = null;
			String status = "Status: ";
			switch (statusCode) {
			case STATUS_ERROR:
				statusColor = Color.RED;
				status += "Error! Not connected.";
				connectBtn = connect();
				break;
			case STATUS_DISCONNECTED:
				statusColor = Color.GRAY;
				status += "Disconnected.";
				connectBtn = connect();
				break;
			case STATUS_CONNECTED:
				statusColor = GameUtils.colorFromHex("#16A611");
				status += "Connected!";
				connectBtn = disconnect();
				break;
			}
			
			statusLabel.setForeground(statusColor);
			statusLabel.setText(status);
			app.getChatPanel().getController().toggleUI(app.getClient().isConnected());
			repaint();
		}
		
		public IOHandler getIOHandler() {
			return ioHandler;
		}
	}
	
}
