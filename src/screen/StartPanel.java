package screen;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import main.Main;
import main.Player;

/**
 * The starting panel of the game. This provides controls for allowing the user to
 * add / remove players from the game. 
 * @author David Kramer
 *
 */
public class StartPanel extends JPanel {
	public static final int MAX_PLAYERS = 4;
	
	private Main main;
	private ArrayList<Player> players;	// players we create
	private DefaultListModel<Player> playerListModel; // list model for players
	private ButtonGroup buttonGroup;	// button group for radio buttons
	private JLabel turnLabel;
	private JRadioButton turnCount10Btn;	// 10 turns
	private JRadioButton turnCount20Btn;	// 20 turns
	private JRadioButton turnCountCustomBtn;	// for having a custom value
	private JTextField customTurnField;	// field for entering numeric value for turn count
	private JList playerList;	// list of players displayed
	private JLabel titleLabel;	// title label at top of screen
	private JButton addPlayerBtn;
	private JButton doneBtn;	

	public StartPanel(Main main) {
		this.main = main;
		init();
	}
	
	private void init() {
		createList();
		createButtons();
		createLabels();
		addComponents();	// add everything to the screen
	}
	
	/**
	 * Creates player lists and player list models.
	 */
	private void createList() {
		players = new ArrayList<>();
		playerListModel = new DefaultListModel<>();
		playerList = new JList<>(playerListModel);
		playerList.setFont(new Font("Courier New", Font.BOLD, 50));
		playerList.setForeground(Color.CYAN);
		playerList.setBackground(Color.BLACK);
		playerList.setSize(new Dimension(400, 400));
	}
	
	/**
	 * Creates the button controls for the start panel to allow the user to
	 * create players. Also allows the user to move along to the board when
	 * completed.
	 */
	private void createButtons() {
		createRadioButtons();
		addPlayerBtn = new JButton("+ Add Player");
		addPlayerBtn.setPreferredSize(new Dimension(150, 50));
		addPlayerBtn.setFocusPainted(false);
		addPlayerBtn.addActionListener(e -> {
			addPlayer();
		});		
		
		doneBtn = new JButton("Done!");
		doneBtn.setEnabled(false);	// disabled by default until we add players
		doneBtn.setPreferredSize(new Dimension(150, 50));
		addPlayerBtn.setPreferredSize(new Dimension(150, 50));
		addPlayerBtn.setFocusPainted(false);
		doneBtn.addActionListener(e -> {
			loadBoard();
		});
		
	}
	
	private void createRadioButtons() {
		buttonGroup = new ButtonGroup();
		turnCount10Btn = new JRadioButton("10");
		turnCount20Btn = new JRadioButton("20");
		turnCountCustomBtn = new JRadioButton("Custom: ");
		customTurnField = new JTextField(5);
		
		buttonGroup.add(turnCount10Btn);
		buttonGroup.add(turnCount20Btn);
		buttonGroup.add(turnCountCustomBtn);
	}
	
	/**
	 * Creates the instruction labels for this panel.
	 */
	private void createLabels() {
		titleLabel = new JLabel("Add Players");
		titleLabel.setFont(new Font("Courier New", Font.BOLD, 60));
		turnLabel = new JLabel("Turn Count: ");
	}
	
	private void loadBoard() {
		if (players.size() >= 1 && players.size() <= MAX_PLAYERS) {
			int turns = 0;
		
			if (buttonGroup.getSelection() == turnCountCustomBtn) {	// TODO try to fix this so it gets value of button itself
				turns = Integer.parseInt(customTurnField.getText());
			} else if (buttonGroup.getSelection() == turnCount10Btn) {
				turns = 10;
			} else if (buttonGroup.getSelection() == turnCount20Btn) {
				turns = 20;
			}
			// load board
			main.getDirector().setPlayers(players);
			main.getDirector().setTurns(turns);
			main.getDirector().setState(Director.BOARD);
		}
	}
	
	
	/**
	 * Adds GUI components to this start panel.
	 */
	private void addComponents() {
		setLayout(new BorderLayout());
		
		titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
		titleLabel.setHorizontalTextPosition(SwingConstants.CENTER);
		add(titleLabel, BorderLayout.NORTH);
		add(playerList, BorderLayout.CENTER);
		
		// bottom buttons
		JPanel btmPanel = new JPanel();
		btmPanel.add(addPlayerBtn);
		btmPanel.add(Box.createHorizontalStrut(20));
		btmPanel.add(doneBtn);
		btmPanel.add(Box.createHorizontalStrut(20));
		
		// turn controls
		btmPanel.add(turnLabel);
		btmPanel.add(turnCount10Btn);
		btmPanel.add(turnCount20Btn);
		btmPanel.add(turnCountCustomBtn);
		btmPanel.add(customTurnField);
		add(btmPanel, BorderLayout.SOUTH);
		
	}
	
	/**
	 * Adds a player if there are less than the maximum, by providing the user with a
	 * dialog interface. If we reach the max player limit, the add player button
	 * will be disabled to prevent further adding more players. 
	 */
	public void addPlayer() {
		if (players.size() < MAX_PLAYERS) {
			AddPlayerDialog addPlayerDialog = new AddPlayerDialog(this);
			if (players.size() >= MAX_PLAYERS) { // disable button if we reached limit
				addPlayerBtn.setEnabled(false);	
			}
		}
	}
	
	/**
	 * Adds a player to the players array with a name and a color
	 * @param name Name of the player
	 * @param color Color of the player
	 */
	public void addPlayer(String name, Color color) {
		Player player = new Player(name, color);
		players.add(player);
		playerListModel.addElement(player);	// update list of players
		if (playerListModel.getSize() > 0) {
			doneBtn.setEnabled(true);
		}
		playerList.repaint();
	}
	
	// Accessor methods
	
	public ArrayList<Player> getPlayers() {
		return players;
	}
	
	/**
	 * Dialog that is created to add a new player. This allows the user to enter the name
	 * and create a new player.
	 * @author David Kramer
	 *
	 */
	class AddPlayerDialog extends JDialog {
		private JTextField nameField;
		private JLabel colorLabel;
		private JLabel nameLabel;
		private JButton okBtn;
		private JButton cancelBtn;
		private StartPanel startPanel;
		
		public AddPlayerDialog(StartPanel startPanel) {
			this.startPanel = startPanel;
			init();
		}
		
		/**
		 * Initializes all GUI components that are related to this dialog and displays it 
		 * to the screen.
		 */
		private void init() {
			createLabels();
			createTextFields();
			createButtons();
			createAndShowDialog();
		}
	
		
		private void createLabels() {
			nameLabel = new JLabel("Name: ");
			colorLabel = new JLabel("Color: " );
		}
		
		/**
		 * Creates all buttons and their action handlers that are a part of this dialog.
		 */
		private void createButtons() {
			okBtn = new JButton("Ok");
			okBtn.setEnabled(false);	// enable by default until name has been entered
			okBtn.addActionListener(e -> {
				if (!nameField.getText().isEmpty()) {
					String name = nameField.getText();
					startPanel.addPlayer(name, GameUtils.getRandomColor());	// Random color for now.
					dispose();
				} else {
					
				}
			});
			
			cancelBtn = new JButton("Cancel");
			cancelBtn.addActionListener(e -> {	// just close dialog
				dispose();
			});
		}
		
		/**
		 * Creates all text fields that are part of this dialog.
		 */
		private void createTextFields() {
			nameField = new JTextField(10);
			nameField.addKeyListener(new KeyListener() {
				public void keyReleased(KeyEvent e) {
					if (!nameField.getText().isEmpty()) {	// only enable ok button if there is text
						okBtn.setEnabled(true);
					} else {
						okBtn.setEnabled(false);
					}
				}
				
				// unused
				public void keyPressed(KeyEvent e) {}
				public void keyTyped(KeyEvent e) {}
			
			});
		}
		
		/**
		 * Lays out and adds all GUI components to this dialog.
		 */
		private void addComponents() {
			setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
			add(nameLabel);
			add(nameField);
			add(okBtn);
			add(cancelBtn);
		}
		
		/**
		 * Sets up the dialog window and renders it to the screen.
		 */
		private void createAndShowDialog() {
			addComponents();
			setTitle("Add New Player");
			setSize(new Dimension(400, 300));
			setLocationRelativeTo(startPanel);
			setVisible(true);
			setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		}
		
	}
}
