package panels;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;

import client.ClientApp;
import newserver.Server;
import util.ErrorUtils;

/**
 * This class contains the controls necessary to change the client--server 
 * connection settings, such as the IP Address and port no. This class
 * provides error checking to ensure that the inputs are valid.
 * @author David Kramer
 *
 */
public class SettingsPanel extends JPanel {
	private static final Dimension SIZE = new Dimension(200, 200);
	private static final Font font = new Font("Courier New", Font.PLAIN, 12);
	private ClientApp app;
	private JLabel settingsLabel;
	private JLabel addressLabel;
	private JLabel portLabel;
	private JTextField addressField;
	private JTextField portField;
	private JTextField nameField;
	private JButton applyBtn;
	
	/**
	 * Constructs a new SettingsPanel with a link to the main client.
	 * @param app - Target client app
	 */
	public SettingsPanel(ClientApp app) {
		this.app = app;
		init();
		setBorder(new LineBorder(Color.LIGHT_GRAY));
	}
	
	/**
	 * Initializes and lays out components using GridBagLayout.
	 */
	private void init() {
		createComponents();
		setPreferredSize(SIZE);
		setMaximumSize(SIZE);
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(0, 5, 0, 5);
		
		c.gridx = 0;
		c.gridwidth = 6;
		c.gridy = 0;
		c.weighty = 0.1;
		add(settingsLabel, c);
		
		c.fill = GridBagConstraints.NONE;
		c.gridx = 0;
		c.weightx = 0.0;
		c.gridwidth = 1;
		c.gridy = 1;
		add(addressLabel, c);
		
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 1;
		c.weightx = 1.0;
		c.gridwidth = 4;
		c.gridy = 1;
		add(addressField, c);
		
		c.fill = GridBagConstraints.NONE;
		c.gridx = 0;
		c.weightx = 0.0;
		c.gridwidth = 1;
		c.gridy = 2;
		add(portLabel, c);
		
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 1;
		c.weightx = 1.0;
		c.gridwidth = 4;
		c.gridy = 2;
		add(portField, c);
		
		c.gridx = 0;
		c.gridwidth = 6;
		c.gridy = 3;
		add(applyBtn, c);
	}
	
	/**
	 * Creates GUI components.
	 */
	private void createComponents() {
		settingsLabel = new JLabel("Connection Settings: ");
		settingsLabel.setFont(new Font("Courier New", Font.BOLD, 14));
		settingsLabel.setHorizontalAlignment(SwingConstants.CENTER);
		
		addressLabel = new JLabel("IP Address: ");
		addressLabel.setFont(font);
		addressLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		addressField = new JTextField();
		addressField.setFont(font);
		addressField.setText(Server.HOST);
		addressField.addKeyListener(handleKey());
		
		portLabel = new JLabel("Port No: ");
		portLabel.setFont(font);
		portLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		portField = new JTextField("" + Server.PORT);
		portField.setFont(font);
		portField.addKeyListener(handleKey());
		
		applyBtn = new JButton("Apply");
		applyBtn.setFont(font);
		applyBtn.setEnabled(false);	// disable initially, until we change values
		applyBtn.addActionListener( e -> {
			apply();
		});
	}
	
	/**
	 * Checks textfields on key events.
	 * @return - A KeyAdapter that listens to key typed, and key
	 * released events.
	 */
	private KeyAdapter handleKey() {
		KeyAdapter key = new KeyAdapter() {
			
			public void keyTyped(KeyEvent e) {
				checkFields();
			}
			
			public void keyReleased(KeyEvent e) {
				checkFields();
			}
		};
		return key;
	}
	
	/**
	 * Checks to see if the values in the text fields are equal to this client's
	 * IP Host or port number. This just provides better feedback, since we shouldn't
	 * have to apply settings that are already existing.
	 */
	private void checkFields() {
		if (!addressField.getText().equals(app.getHost()) || !portField.getText().equals("" + app.getPort())) {
			applyBtn.setEnabled(true);	
		} else {
			applyBtn.setEnabled(false);
		}
	}
	
	/**
	 * Action method for the apply btn. Checks to make sure that input 
	 * fields aren't blank, and that the port number is valid. If all
	 * is good, the client connection settings are then updated.
	 */
	private void apply() {
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
}