package screen;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.ButtonModel;
import javax.swing.DefaultListModel;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import main.Director;
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
	private JList playerList;	// list of players displayed
	private ButtonGroup buttonGroup;	// button group for radio buttons
	private JRadioButton turn10Btn;	// 10 turns
	private JRadioButton turn20Btn;	// 20 turns
	private JRadioButton customTurnBtn;	// for having a custom value
	private JTextField customTurnField;	// field for entering numeric value for turn count
	private JButton addPlayerBtn;
	private JButton removePlayerBtn;
	private JButton doneBtn;	
	private JLabel turnLabel;
	private JLabel titleLabel;	// title label at top of screen	

	public StartPanel(Main main) {
		this.main = main;
		init();
	}
	
	/**
	 * Initialize everything.
	 */
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
		
		// Allow user to select and deselect users to edit or remove them
		playerList.setSelectionModel(new DefaultListSelectionModel() {
			
			public void setSelectionInterval(int index0, int index1) {
				if (index0 == index1) {
					if (playerList.isSelectedIndex(index0)) {
						removeSelectionInterval(index0, index0);
						return;
					}
				}
				super.setSelectionInterval(index0, index1);
			}
			
			public void addSelectionInterval(int index0, int index1) {
				if (index0 == index1) {
					if (isSelectedIndex(index0)) {
						removeSelectionInterval(index0, index0);
						return;
					}
				}
				super.addSelectionInterval(index0, index1);
			}
		});
		
		// enables or disables buttons based on players present in list
		playerList.addListSelectionListener(e -> {
			if (playerList.getSelectedValuesList().size() > 0) { // selected players
				removePlayerBtn.setEnabled(true);
			} else {
				removePlayerBtn.setEnabled(false);
			}
			
			if (players.size() > 0) {
				doneBtn.setEnabled(true);
			} else {
				doneBtn.setEnabled(false);
			}
		});
		
		// decorate
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
		
		removePlayerBtn = new JButton("× Remove Player");
		removePlayerBtn.setEnabled(false);
		removePlayerBtn.setPreferredSize(new Dimension(150, 50));
		removePlayerBtn.setFocusPainted(false);
		removePlayerBtn.addActionListener(e -> {
			removePlayer();
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
	
	/**
	 * Creates radio buttons and text field for controlling the amount of turns
	 * that the game will have.
	 */
	private void createRadioButtons() {
		buttonGroup = new ButtonGroup();
		turn10Btn = new JRadioButton("10");
		turn10Btn.setSelected(true);	// enable 10 turns by default
		
		turn20Btn = new JRadioButton("20");
		
		customTurnBtn = new JRadioButton("Custom: ");
		customTurnBtn.addChangeListener(e -> {
			if (customTurnBtn.isSelected()) {	// enable or disable custom turn field
				customTurnField.setEnabled(true);
			} else {
				customTurnField.setEnabled(false);	
			}
		});
		
		customTurnField = new JTextField(5);
		customTurnField.setEnabled(false);	// disable unless radio button is checked!
		
		buttonGroup.add(turn10Btn);
		buttonGroup.add(turn20Btn);
		buttonGroup.add(customTurnBtn);
	}
	
	/**
	 * Creates the instruction labels for this panel.
	 */
	private void createLabels() {
		titleLabel = new JLabel("Add Players");
		titleLabel.setFont(new Font("Courier New", Font.BOLD, 60));
		turnLabel = new JLabel("Turn Count: ");
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
		JPanel btmPanel = new JPanel();	// holding panel for button controls
		btmPanel.add(addPlayerBtn);
		btmPanel.add(Box.createHorizontalStrut(20));
		btmPanel.add(removePlayerBtn);
		btmPanel.add(Box.createHorizontalStrut(20));
		
		// turn controls
		btmPanel.add(turnLabel);
		btmPanel.add(turn10Btn);
		btmPanel.add(turn20Btn);
		btmPanel.add(customTurnBtn);
		btmPanel.add(customTurnField);
		btmPanel.add(Box.createHorizontalStrut(20));
		btmPanel.add(doneBtn);
		add(btmPanel, BorderLayout.SOUTH);
		
	}
	
	/**
	 * Updates the director with player and turn information and sets the state of
	 * the game to board.
	 */
	private void loadBoard() {
		if (players.size() >= 1 && players.size() <= MAX_PLAYERS) {
			int turns = 0;
			
			// Jo: Moved this stuff around, am testing stuff
//			if (buttonGroup.getSelection() == turn10Btn) {
//				turns = 10;
//			} else if (buttonGroup.getSelection() == turn20Btn) {
//				turns = 20;
//			}
//			else if (buttonGroup.getSelection() == customTurnBtn) {
//				// TODO right now, text field is NOT filtered to only allow for numeric values!
//				turns = Integer.parseInt(customTurnField.getText());
//			}
			
			if (buttonGroup.isSelected(turn10Btn.getModel())) {
				turns = 20;
			}
			else if (buttonGroup.isSelected(turn20Btn.getModel())) {
				turns = 20;
			}
			else if (buttonGroup.isSelected(customTurnBtn.getModel())) {
				turns = Integer.parseInt(customTurnField.getText());
			}
			
			
			System.out.println(turns);
			// load board
			Director director = main.getDirector();
			director.setPlayers(players);
			director.setTurns(turns);
			director.setState(Director.BOARD);
			setVisible(false);	// Setting the intro screen to invis
			// Maybe make another panel visible??????
		}
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
	 * Adds a player to the players array with a name and a color. If a player already exists
	 * with the same name, an error message dialog will show up.
	 * @param name Name of the player
	 * @param color Color of the player
	 * @return true - if player was added successfully, false if duplicate name found
	 */
	public boolean addPlayer(String name, Color color) {
		Player player = new Player(name, color);
		
		for (int i = 0; i < players.size(); i++) {
			if (players.get(i).getName().equals(name)) {	// show warning about duplicate player names
				JOptionPane.showMessageDialog(null, "Duplicate names are not allowed!",
							"Notice", JOptionPane.INFORMATION_MESSAGE);
				return false;
			}
		}
		
		players.add(player);
		playerListModel.addElement(player);	// update list of players
		
		if (playerListModel.getSize() > 0) {
			doneBtn.setEnabled(true);
		}
		
		if (players.size() >= MAX_PLAYERS) {	// disable add player button if we reach max limit
			addPlayerBtn.setEnabled(false);
		}
		
		playerList.repaint();
		return true;
	}
	
	/**
	 * Removes player(s), based on selection in the player list.
	 */
	public void removePlayer() {
		List<Player> selectedPlayers = playerList.getSelectedValuesList();
		
		for (Player p : selectedPlayers) {
			playerListModel.removeElement(p);
			players.remove(p);
		}
		
		if (players.size() <= 0) {	// disable done btn if we have no players
			doneBtn.setEnabled(false);
		} else if (players.size() <= MAX_PLAYERS) { // only enable if within limits
			addPlayerBtn.setEnabled(true);
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
			okBtn.setEnabled(false);	// disable by default until name has been entered
			okBtn.addActionListener(e -> {
				if (!nameField.getText().isEmpty()) {
					String name = nameField.getText();
					// TODO player color is random right now, add functionality to choose custom color later.
					if (startPanel.addPlayer(name, GameUtils.getRandomColor())) {
						dispose();	
					}
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
