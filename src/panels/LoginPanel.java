package panels;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import client.ClientApp;
import gameobjects.NewPlayer;

public class LoginPanel extends JPanel {
	private static final Dimension SIZE = new Dimension(400, 200);
	private ClientApp app;
	private JLabel titleLabel;
	private JLabel nameLabel;
	private JButton joinBtn;
	private JTextField nameField;
	private DefaultListModel<NewPlayer> playerListModel;
	private JList<NewPlayer> playerList;
	
	public LoginPanel(ClientApp app) {
		this.app = app;
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
		c.weighty = 10;
		add(nameField, c);
		
		// join btn
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 3;
		c.weightx = 0.1;
		c.gridy = 1;
		add(joinBtn, c);
		
		c.anchor = GridBagConstraints.CENTER;
		c.gridx = 0;
		c.gridwidth = 4;
		c.gridy = 2;
		add(playerList, c);
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
		
		joinBtn = new JButton("Join Game");
		joinBtn.setFont(new Font("Courier New", Font.BOLD, 20));
		joinBtn.setEnabled(false);
		
		playerListModel = new DefaultListModel<>();
		playerList = new JList<>(playerListModel);
		playerList.setBorder(new TitledBorder(new LineBorder(Color.GRAY), " Waiting Players: ", 
				TitledBorder.LEFT, TitledBorder.CENTER, null));
		playerListModel.addElement(new NewPlayer("test", 2));
		playerListModel.addElement(new NewPlayer("test2", 3));
	}
}
