package client;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import main.Main;
import states.StartState;

/**
 * This is the login screen that will allow a client to connect to the game 
 * server with a specified name.
 * @author David Kramer
 *
 */
public class ClientLogin extends JFrame {
	private static final long serialVersionUID = -7840356911382391248L;
	private static final Dimension SIZE = new Dimension(300, 500);

	private JPanel panel;
	private JLabel titleLabel;
	private JTextField nameField;
	private JButton connectBtn;
	
	public ClientLogin() {
		init();
		setSize(SIZE);
		setTitle("Project-MiniGame Client Login");
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setVisible(true);
	}
	
	/**
	 * Layouts out all GUI components using GridBagLayout.
	 */
	private void init() {
		createComponents();
		
		panel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		
		// title label
		c.anchor = GridBagConstraints.NORTH;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 0;
		c.ipady = 20;
		c.weighty = 0.1;
		c.weightx = 1.0;
		panel.add(titleLabel, c);
		
		// text field
		c.anchor = GridBagConstraints.CENTER;
		c.gridx = 0;
		c.gridy = 1;
		c.ipady = 20;
		panel.add(nameField, c);
		
		// connect btn
		c.anchor = GridBagConstraints.SOUTH;
		c.gridx = 0;
		c.gridy = 2;
		c.weighty = 1.0;
		c.ipady = 100;
		panel.add(connectBtn, c);
		
		add(panel);
		
	}
	
	/**
	 * Creates all GUI components for this ClientLogin panel
	 */
	private void createComponents() {
		panel = new JPanel();
		panel.setBackground(Color.BLACK);
		
		// title label
		titleLabel = new JLabel("Connect to Project Mini-Game");
		titleLabel.setBackground(Color.BLACK);
		titleLabel.setForeground(Color.CYAN);
		titleLabel.setFont(new Font("Courier New", Font.BOLD, 15));
		titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
		
		// name text field
		nameField = new JTextField(10);
		nameField.setBackground(Color.BLACK);
		nameField.setForeground(Color.CYAN);
		nameField.setBorder(new TitledBorder(new LineBorder(Color.CYAN), "Name: ", TitledBorder.CENTER,
							TitledBorder.CENTER, new Font("Courier New", Font.BOLD, 12), Color.CYAN));
		nameField.setCaretColor(Color.CYAN);
		nameField.setFocusable(true);
		nameField.requestFocus();
		nameField.addKeyListener(new KeyListener() {

			public void keyTyped(KeyEvent e) {
				if (validateField()) {
					if (e.getKeyChar() == '\n') {	// if enter hit and field is valid, try to connect
						connect();
					}
				}
			}

			public void keyPressed(KeyEvent e) {}

			public void keyReleased(KeyEvent e) {
				if (validateField()) {
					if (e.getKeyCode() == KeyEvent.VK_ENTER) {	// if enter and field is valid, try to connect
						connect();
					}
				}
			}
			
		});
		
		// connect button
		connectBtn = new JButton("Connect To Server");
		connectBtn.setEnabled(false);
		connectBtn.setBackground(Color.CYAN);
		connectBtn.setForeground(Color.BLACK);
		connectBtn.addActionListener(e -> {
			connect();
		});
	}
	
	private void connect() {
		ClientPanel c = new ClientPanel();
		if (c.connect(ClientPanel.HOST, ClientPanel.PORT)) {
			c.send("!name " + nameField.getText());
			if (c.getClient().hasValidName()) {
				Main main = new Main(c);	// create main client window
				createServerPlayer(main);
			} else {	// name is invalid
				JOptionPane.showMessageDialog(this, "Invalid client name (duplicate). "
						+ "Please try a different name!", "Invalid Name", JOptionPane.ERROR_MESSAGE);
			}
		} else {
			JOptionPane.showMessageDialog(this, "Unable to connect! Server may have"
					+ " not been started...", "Connection Error", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	private void createServerPlayer(Main main) {
		StartState s = (StartState) main.getDirector().getState();
		s.getPlayerList().addPlayer(nameField.getText());
		
	}
	
	private boolean validateField() {
		if (nameField.getText().isEmpty()) {
			connectBtn.setEnabled(false);
			return false;
		} else {
			connectBtn.setEnabled(true);
			return true;
		}
	}
	
	@SuppressWarnings("unused")
	public static void main(String[] args) {
		ClientLogin login = new ClientLogin();
	}
}
