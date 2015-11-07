package states;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.DefaultListSelectionModel;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import main.NewDirector;
import main.Player;
import screen.GameButton;
import screen.GameUtils;
import screen.PlayerListCellRenderer;

public class NewStartState extends State {
	private static final long serialVersionUID = 1L;
	
	private NameField nameField;
	private JTextField customTurnField;	// field for entering numeric value for turn count
	private GameButton addBtn;
	private GameButton removeBtn;
	private GameButton startBtn;
	private PlayerList<Player> playerList;
	private ButtonGroup buttonGroup;	// button group for radio buttons
	private JRadioButton turn10Btn;	// 10 turns
	private JRadioButton turn20Btn;	// 20 turns
	private JRadioButton customTurnBtn;	// for having a custom value
	private JPanel radioBtnPanel;	// panel that holds all radio buttons for turns
	private JLabel titleLabel;
	private JLabel gameLabel;
	private JLabel playersLabel;
	private JLabel remainingLabel;
	private int playersRemaining;
	
	public NewStartState(NewDirector director) {
		super(director);
		init();
	}
	
	/**
	 * Creates all components and then uses a GridBagLayout to lay them out
	 * on this panel.
	 */
	private void init() {
		// create all GUI components first
		playersRemaining = NewDirector.MAX_PLAYERS;
		nameField = new NameField(10);	// player name field
		playerList = new PlayerList<>();
		createButtons();
		createLabels();	
		
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();	// constraints for all components
		
		// players label
		c.anchor = GridBagConstraints.NORTHWEST;
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		c.gridy = 0;
		add(playersLabel, c);
		
		// playerlist
		c.anchor = GridBagConstraints.WEST;
		c.gridx = 0;
		c.ipadx = 50;
		c.gridy = 1;
		c.gridheight = 3;
		c.weighty = 10.0;
		add(playerList, c);
		
		// remove button
		c.anchor = GridBagConstraints.SOUTHWEST;
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		c.weightx = 0.0;
		c.gridy = 4;
		c.weighty = 0.0;
		add(removeBtn, c);
		
		// title label
		c.anchor = GridBagConstraints.NORTH;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 1;
		c.gridwidth = 2;
		c.weightx = 0.5;
		c.ipadx = 0;
		c.gridy = 0;
		c.gridheight = 1;
		add(titleLabel, c);
		
		// players remaining label
		c.anchor = GridBagConstraints.NORTHEAST;
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 6;
		c.weightx = 0.0;
		c.gridy = 0;
		c.weighty = 0.0;
		add(remainingLabel, c);
		
		// name text field
		c.insets = new Insets(20, 20, 20, 20);	// 20px padding all the way around
		c.gridx = 1;
		c.weightx = 0.0;
		c.gridwidth = 2;
		c.ipadx = 0;
		c.gridy = 1;
		c.ipady = 0;
		add(nameField, c);
		
		// add button
		c.anchor = GridBagConstraints.NORTHEAST;
		c.gridx = 3;
		c.gridwidth = 0;
		c.weightx = 0.0;
		c.ipadx = 20;
		c.gridy = 1;
		c.ipady = 30;
		add(addBtn, c);
		
		// game label
		c.anchor = GridBagConstraints.CENTER;
		c.gridx = 1;
		c.gridwidth = 2;
		c.ipadx = 0;
		c.ipady = 0;
		c.gridy = 3;
		add(gameLabel, c);
		
		// radio button panel
		c.anchor = GridBagConstraints.CENTER;
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 3;
		c.gridwidth = 0;
		c.ipadx = 0;
		c.gridy = 2;
		c.gridheight = 3;
		c.weighty = 0;
		c.ipady = 0;
		add(radioBtnPanel, c);
		
		// start button
		c.anchor = GridBagConstraints.SOUTH;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 1;
		c.gridwidth = 0;
		c.weightx = 1.0;
		c.ipadx = 0;
		c.gridy = 4;
		c.gridheight = 0;
		c.weighty = 1.0;
		c.ipady = 10;
		add(startBtn, c);
	}
	
	/**
	 * Creates radio button controls and text field for controlling the 
	 * amount of turns the game will have.
	 */
	private void createTurnControls() {
		buttonGroup = new ButtonGroup();
		turn10Btn = new JRadioButton("10");
		turn10Btn = (JRadioButton)GameUtils.customizeComp(turn10Btn, Color.BLACK, Color.CYAN, 40);
		turn10Btn.setSelected(true);	// enable 10 turns by default
		turn10Btn.addActionListener(e -> {
			// TODO add updating
		});
		
		turn20Btn = new JRadioButton("20");
		turn20Btn = (JRadioButton)GameUtils.customizeComp(turn20Btn, Color.BLACK, Color.CYAN, 40);
		turn20Btn.addActionListener(e -> {
			//TODO add updating
		});
		
		customTurnBtn = new JRadioButton("Custom");
		customTurnBtn = (JRadioButton)GameUtils.customizeComp(customTurnBtn, Color.BLACK, Color.CYAN, 14);
		customTurnBtn.addChangeListener(e -> {
			if (customTurnBtn.isSelected()) {	// enable or disable custom turn field
				customTurnField.setEnabled(true);
			} else {
				customTurnField.setEnabled(false);	
			}
		});
		
		customTurnField = new JTextField(3);
		customTurnField = (JTextField)GameUtils.customizeComp(customTurnField, Color.BLACK, Color.CYAN, 20);
		customTurnField.setEnabled(false);	// disable unless radio button is checked!
		customTurnField.setBorder(new LineBorder(Color.CYAN));
		customTurnField.setMaximumSize(new Dimension(100, 20));
		customTurnField.setHorizontalAlignment(SwingConstants.CENTER);
		
		buttonGroup.add(turn10Btn);
		buttonGroup.add(turn20Btn);
		buttonGroup.add(customTurnBtn);
		
		// panel to hold all radio buttons
		radioBtnPanel = new JPanel();
		radioBtnPanel.setBorder(new TitledBorder(new LineBorder(Color.CYAN), "Game Turns:",
				TitledBorder.CENTER, TitledBorder.BELOW_TOP,
				new Font("Courier New", Font.BOLD, 14), Color.CYAN));
		radioBtnPanel.setBackground(Color.BLACK);
		radioBtnPanel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		
		c.gridx = 0;
		c.gridy = 0;
		c.weighty = 1.0;
		radioBtnPanel.add(turn10Btn, c);
		
		c.gridx = 0;
		c.gridy = 1;
		radioBtnPanel.add(turn20Btn, c);
		
		c.gridx = 0;
		c.gridy = 2;
		radioBtnPanel.add(customTurnBtn, c);
		
		c.gridx = 1;
		c.gridy = 2;
		c.weighty = 0.0;
		radioBtnPanel.add(customTurnField, c);
		
		c.gridx = 0;
		c.gridy = 3;
		c.gridwidth = 2;
		radioBtnPanel.add(Box.createVerticalStrut(20), c);	// bottom strut
	}
	
	/**
	 * Creates all GUI control buttons in this start state.
	 */
	private void createButtons() {
		addBtn = new GameButton("+ Add Player", GameUtils.colorFromHex("#00BB00"));
		addBtn.setFont(new Font("Courier New", Font.BOLD, 20));
		addBtn.setEnabled(false);	// enable by default
		addBtn.setBorder(new LineBorder(GameUtils.colorFromHex("#00AA00")));
		addBtn.addActionListener(e -> {
			playerList.addPlayer();
		});
		
		removeBtn = new GameButton("Remove", GameUtils.colorFromHex("#EB6238"));
		removeBtn.setFont(new Font("Courier New", Font.BOLD, 25));
		removeBtn.setBorder(new LineBorder(GameUtils.colorFromHex("#AD2900")));
		removeBtn.setIcon(new ImageIcon("res/trashIcon.png"));
		removeBtn.setEnabled(false); // disable initially
		removeBtn.addActionListener(e -> {
			playerList.removePlayer();
		});
		
		startBtn = new GameButton("Start!", Color.BLACK);
		startBtn.setFont(new Font("Courier New", Font.BOLD, 50));
		startBtn.setEnabled(false);
		startBtn.addActionListener(e -> {
			initGame();
		});
		
		createTurnControls();	// now make radio button turn controls
	}
	
	/**
	 * Updates the director with newly create players and sets the state to board.
	 * We are now ready to begin playing the game!
	 */
	private void initGame() {
		// assign ID's to each player finally
		
		for (int i = 0; i < playerList.players.size(); i++) {
			Player p = playerList.players.get(i);
			p.setID((byte)i);
		}
		
		director.setPlayers(playerList.players);
		director.setState(new BoardState(director));
		
		// figure out turn count
		int turns = 0;
		
		if (buttonGroup.isSelected(turn10Btn.getModel())) {
			turns = 10;
		}
		else if (buttonGroup.isSelected(turn20Btn.getModel())) {
			turns = 20;
		}
		else if (buttonGroup.isSelected(customTurnBtn.getModel())) {
			turns = Integer.parseInt(customTurnField.getText());
		}
		
		director.setTurns(turns);
	}
	
	/**
	 * Creates all labels in this start state.
	 */
	private void createLabels() {
		// game label
		gameLabel = new JLabel("<Project Mini Game>");
		gameLabel.setOpaque(false);
		gameLabel = GameUtils.customizeLabel(gameLabel, null, new Color(0, 255, 255, 50), 50);
		gameLabel.setHorizontalAlignment(SwingConstants.CENTER);
		
		// title label
		titleLabel = new JLabel("Add Players");
		titleLabel.setOpaque(true);
		titleLabel = GameUtils.customizeLabel(titleLabel, Color.BLACK, Color.CYAN, 35);
		titleLabel.setBorder(new LineBorder(Color.BLACK));
		titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
		
		// players label
		playersLabel = new JLabel("Players:");
		playersLabel.setOpaque(true);
		playersLabel = GameUtils.customizeLabel(playersLabel, Color.BLACK, Color.CYAN, 35);
		playersLabel.setBorder(new EmptyBorder(0, 20, 0, 0));
		
		// players remaining label
		remainingLabel = new JLabel("Remaining: " + playersRemaining);
		remainingLabel.setOpaque(true);
		remainingLabel = GameUtils.customizeLabel(remainingLabel, Color.BLACK, Color.CYAN, 18);
		remainingLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		remainingLabel.setBorder(new EmptyBorder(0, 0, 0, 20));	// right side padding
		
	}

	public void update() {
	}
	
	public void paintComponent(Graphics g) {
		System.out.println("Repaintining!");
		g.fillRect(0, 0, getWidth(), getHeight());
	}

	public void render() {
		//TODO we might not need to do anything for start state?
	}
	
	/**
	 * Inner class for Player list, which is the list of players that the user can 
	 * add to / delete, as necessary.
	 * @author David Kramer
	 *
	 */
	private class PlayerList<E> extends JList {
		private DefaultListModel<Player> listModel;
		private ArrayList<Player> players;
		private PlayerListCellRenderer renderer;
		
		public PlayerList() {
			init();
		}
		
		private void init() {
			players = new ArrayList<>();
			setBackground(Color.BLACK);
			listModel = new DefaultListModel<>();
			setModel(listModel);
			renderer = new PlayerListCellRenderer();	// custom list element rendering
			renderer.setBorder(new EmptyBorder(5, 20, 5, 10));	// add some padding
			setCellRenderer(renderer);
			
			// Allow user to select and deselect users to edit or remove them
			setSelectionModel(new DefaultListSelectionModel() {
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
			addListSelectionListener(e -> {
				if (getSelectedValuesList().size() > 0) { // selected players
					removeBtn.setEnabled(true);
				} else {
					removeBtn.setEnabled(false);
				}
				
				if (listModel.size() > 0) {
					startBtn.setEnabled(true);
				} else {
					startBtn.setEnabled(false);
				}
			});
		}
		
		/**
		 * Checks to see if this list contains specified text.
		 * @param text Text to check
		 * @return true if text matches a list model element
		 */
		public boolean checkNameRepeats(String text) {
			for (int i = 0; i < listModel.size(); i++) {
				Player p = listModel.getElementAt(i);
				if (text.equals(p.getName())) {
					return true;
				}
			}
			return false;
		}
		
		/**
		 * Adds a player with a random color to the list and player array.
		 */
		public void addPlayer() {
			Player p = new Player(nameField.getText(), GameUtils.getRandomColor());
			listModel.addElement(p);
			players.add(p);
			nameField.setText(""); // clear out
			updateControls();
			addBtn.setEnabled(false);
			playerList.repaint();
		}
		
		/**
		 * Removes a player from this list, based on the selection.
		 */
		public void removePlayer() {
			List<Player> selectedPlayers = getSelectedValuesList();
			
			for (Player p : selectedPlayers) {
				listModel.removeElement(p);
				players.remove(p);
			}
			
			updateControls();
		}
		
		/**
		 * Updates controls and ensures controls are either enabled or disabled,
		 * depending on how many players have been created.
		 */
		private void updateControls() {
			playersRemaining = NewDirector.MAX_PLAYERS - players.size();
			
			if (playersRemaining == 0) {
				addBtn.setEnabled(false);
				nameField.setEnabled(false);
			} else {
				nameField.setEnabled(true);
			}
			remainingLabel.setText("Remaining: " + playersRemaining);
			
			if (players.size() == 0) {	// disable start btn if we have no players
				startBtn.setEnabled(false);
			} else {
				startBtn.setEnabled(true);
			}
		}
		
	}
	
	/**
	 * Class for the player name field, that checks to make sure names entered are
	 * valid (not duplicates).
	 * @author David Kramer
	 *
	 */
	private class NameField extends JTextField implements KeyListener {
		// Default border appearance
		private final Border DEFAULT_BORDER = new TitledBorder(new LineBorder(Color.CYAN), "Enter a player name:",
													TitledBorder.LEFT, TitledBorder.BELOW_TOP,
													new Font("Courier New", Font.BOLD, 14), Color.CYAN);
		
		// Border for invalid input (duplicate name)
		private final Border ERROR_BORDER = new TitledBorder(new LineBorder(Color.BLACK), "Duplicate Name!",
													TitledBorder.LEFT, TitledBorder.BELOW_TOP,
													new Font("Courier New", Font.BOLD, 14), Color.BLACK);
		
		public NameField(int col) {
			super(col);
			
			setBackground(Color.BLACK);
			setForeground(Color.CYAN);
			setBorder(DEFAULT_BORDER);
			setFont(new Font("Courier New", Font.BOLD, 25));
			addKeyListener(this);
		}
		
		/**
		 * Checks to see if a player can be added to the player list. If the textfield
		 * is empty or contains a duplicate player name, it can't be added.
		 * @return True if field is valid
		 */
		public boolean validateField() {
			if (getText().isEmpty()) {
				addBtn.setEnabled(false);
				return false;
			} else if (playerList.checkNameRepeats(getText().trim())) {
				addBtn.setEnabled(false);
				setBorder(ERROR_BORDER);
				setForeground(Color.BLACK);
				setBackground(Color.RED);
				return false;
			} else {
				addBtn.setEnabled(true);
				setBorder(DEFAULT_BORDER);
				nameField.setForeground(Color.CYAN);
				nameField.setBackground(Color.BLACK);
				return true;
			}
		}

		/**
		 * Checks and validates the textfield as the user presses keys. If validation passes
		 * and they press enter, the name is added to the player list.
		 */
		public void keyPressed(KeyEvent e) {
			if (validateField()) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					playerList.addPlayer();
				}
			}
			
		}

		/**
		 * Checks and validates text field as keys are released.
		 */
		public void keyReleased(KeyEvent e) {
			validateField();
		}
		
		// unused
		public void keyTyped(KeyEvent e) {}
		
	}

}
