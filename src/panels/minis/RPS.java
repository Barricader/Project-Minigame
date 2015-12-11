package panels.minis;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.Random;

import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.border.LineBorder;

import org.json.simple.JSONObject;

import client.ClientApp;
import gameobjects.NewPlayer;
import panels.BaseMiniPanel;
import util.BaseController;
import util.DarkButton;
import util.GameUtils;
import util.Keys;
import util.NewJSONObject;
import util.PlayerStyles;

/**
 * Basic rock paper scissors game. Each player goes through 3 rounds of
 * rock paper scissors, and they have to try and beat the computer, which
 * is at random chance. There is a countdown timer, and if they don't choose
 * within the allotted time, their choice is also randomized. 
 * @author David Kramer
 *
 */
public class RPS extends BaseMiniPanel {
	private static final long serialVersionUID = 1552274782392228091L;
	private static final int MAX_TURNS = 3;	// should only go 3 times
	private static final int WAIT_TIME = 10;
	private int timeLeft = WAIT_TIME;
	
	private NewPlayer player;
	private String[] choices = {"Rock", "Paper", "Scissor"};
	private String playerChoice;
	private Random rng = new Random();
	
	private DarkButton rockBtn;
	private DarkButton paperBtn;
	private DarkButton scissorBtn;
	private JLabel timerLabel;	// displays countdown
	private JLabel playerChoiceLabel;
	private JLabel compChoiceLabel;	// choice that the computer chose
	private JLabel turnLabel;	// displays what turn we're on
	private JLabel statusLabel;	// displays if we're done, but waiting
	private Timer countdownTimer;
	private Timer compTimer;	// timer to delay comp choice
	
	private int turnCount;	// what turn are we currently on?
	private int wins;
	
	public RPS(ClientApp app) {
		super(app);
	}
	
	/**
	 * Initializes components and lays them out using GridBagLayout.
	 */
	public void init() {
		wins = 0;
		turnCount = 0;
		controller = new Controller(app);
		countdownTimer = new Timer(1000, null);
		compTimer = new Timer(1000, null);
		createComponents();
		updateView();
		countdownTimer.start();
	}
	
	/**
	 * Updates the view of this RPS game.
	 */
	private void updateView() {
		removeAll();
		// player choice
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		
		// player choice
		c.anchor = GridBagConstraints.NORTH;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.weightx = 1.0;
		c.gridwidth = 2;
		c.gridy = 0;
		c.ipady = 20;
		add(playerChoiceLabel, c);
		
		// timer label
		c.gridx = 2;
		c.gridwidth = 4;
		c.ipadx = 0;
		c.gridy = 0;
		c.ipady = 0;
		add(timerLabel, c);
		
		// comp choice
		c.gridx = 6;
		c.gridwidth = 2;
		c.gridy = 0;
		c.ipady = 20;
		c.weighty = 1.0;
		add(compChoiceLabel, c);
		
		// rock btn
		c.anchor = GridBagConstraints.SOUTH;
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		c.gridwidth = 2;
		c.gridy = 5;
		c.weighty = 1.0;
		add(rockBtn, c);
		
		c.gridx = 2;
		c.gridwidth = 1;
		add(Box.createHorizontalStrut(20), c);
		
		// paper btn
		c.gridx = 3;
		c.gridwidth = 2;
		c.gridy = 5;
		add(paperBtn, c);
		
		c.gridx = 5;
		c.gridwidth = 1;
		add(Box.createHorizontalStrut(20), c);
		
		// scissors
		c.gridx = 6;
		c.gridwidth = 2;
		c.gridy = 5;
		add(scissorBtn, c);
		
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridwidth = 3;
		c.gridy = 6;
		add(turnLabel, c);
		
		c.gridx = 3;
		c.gridwidth = 6;
		add(statusLabel, c);
		
		revalidate();
		repaint();
	}
	
	/**
	 * Creates GUI components and timer.
	 */
	private void createComponents() {
		rockBtn = new DarkButton("Rock");
		rockBtn.setForeground(Color.RED);
		rockBtn.setBorder(new LineBorder(Color.RED));
		rockBtn.setFont(new Font("Courier New", Font.BOLD, 40));
		rockBtn.addActionListener(e -> {
			playerChoice = "Rock";
			playerChoiceLabel.setText(playerChoice);
			compChoose();
		});
		
		paperBtn = new DarkButton("Paper");
		paperBtn.setForeground(Color.GREEN);
		paperBtn.setBorder(new LineBorder(Color.GREEN));
		paperBtn.setFont(new Font("Courier New", Font.BOLD, 40));
		paperBtn.addActionListener(e -> {
			playerChoice = "Paper";
			playerChoiceLabel.setText(playerChoice);
			compChoose();
		});
		
		scissorBtn = new DarkButton("Scissor");
		scissorBtn.setForeground(Color.BLUE);
		scissorBtn.setBorder(new LineBorder(Color.BLUE));
		scissorBtn.setFont(new Font("Courier New", Font.BOLD, 40));
		scissorBtn.addActionListener(e -> {
			playerChoice = "Scissor";
			playerChoiceLabel.setText(playerChoice);
			compChoose();
		});
		
		reset();
		countdownTimer.start();
	}
	
	/**
	 * Disables buttons, after a user has made their choice. This toggles
	 * the comp timer and causes a slight delay, before the results are 
	 * determined.
	 */
	private void compChoose() {
		toggleButtons(false);
		resetCompTimer();
		compTimer.start();
	}
	
	/**
	 * Computer chooses, and then it checks to see who won.
	 * @param choosePlayer - flag to set if we should also choose player
	 */
	private void compChoose(boolean choosePlayer) {
		countdownTimer.stop();
		compTimer.stop();
		
		// choose for player
		if (choosePlayer) {
			playerChoice = choices[rng.nextInt(choices.length)];
			playerChoiceLabel.setText(playerChoice);
		}
		
		String compChoice = choices[rng.nextInt(choices.length)];
		compChoiceLabel.setText(compChoice);
		boolean tied = false;
		boolean compWon = false;
		boolean playerWon = false;
		
		// determine who won
		if (compChoice.equals(playerChoice)) {
			tied = true;
			JOptionPane.showMessageDialog(app, "Tied!");
		}
		
		if (!tied) {
			if (compChoice.equals("Rock")) {
				if (playerChoice.equals("Paper")) {
					playerWon = true;
				} else if (playerChoice.equals("Scissor")) {
					compWon = true;
				}
			} else if (compChoice.equals("Paper")) {
				if (playerChoice.equals("Rock")) {
					compWon = true;
				} else if (playerChoice.equals("Scissor")) {
					playerWon = true;
				}
			} else if (compChoice.equals("Scissor")) {
				if (playerChoice.equals("Rock")) {
					playerWon = true;
				} else if (playerChoice.equals("Paper")) {
					compWon = true;
				}
			}	
			
			if (playerWon) {
				JOptionPane.showMessageDialog(app, "Player won!");
				wins++;
			} else if (compWon) {
				JOptionPane.showMessageDialog(app, "Comp won!");
			}	
		}
		
		turnCount++;
		
		// we're done with the game, have to wait for others to finish!
		if (turnCount == MAX_TURNS) {
			countdownTimer.stop();
			compTimer.stop();
			toggleButtons(false);	// disable buttons
			sendUpdate();
			statusLabel.setVisible(true);
			return;
		}
		reset();	
	}
	
	/**
	 * Enables or disables buttons depending on boolean value.
	 * @param b - Boolean value to assign to enabled state on buttons
	 */
	private void toggleButtons(boolean b) {
		rockBtn.setEnabled(b);
		paperBtn.setEnabled(b);
		scissorBtn.setEnabled(b);
	}
	
	/**
	 * Resets the view / labels, and the timer.
	 */
	private void reset() {
		resetCountTimer();
		resetCompTimer();
		toggleButtons(true);
		
		playerChoice = "";
		timerLabel = new JLabel("" + timeLeft);
		timerLabel.setHorizontalAlignment(SwingConstants.CENTER);
		app.colorize(timerLabel, null, 40);
		
		compChoiceLabel = new JLabel("Computer Choice");
		compChoiceLabel.setFont(new Font("Courier New", Font.BOLD, 18));
		compChoiceLabel.setOpaque(true);
		compChoiceLabel.setBackground(Color.BLACK);
		compChoiceLabel.setForeground(Color.CYAN);
		compChoiceLabel.setBorder(new LineBorder(Color.CYAN, 1));
		compChoiceLabel.setHorizontalAlignment(SwingConstants.CENTER);
		
		player = app.getBoardPanel().getClientPlayer();
		playerChoiceLabel = new JLabel(player.getName() + " Choice");
		playerChoiceLabel.setFont(new Font("Courier New", Font.BOLD, 18));
		playerChoiceLabel.setOpaque(true);
		playerChoiceLabel.setBackground(Color.BLACK);
		playerChoiceLabel.setForeground(PlayerStyles.getColor(player.getStyleID()));
		playerChoiceLabel.setBorder(new LineBorder(playerChoiceLabel.getForeground(), 1));
		playerChoiceLabel.setHorizontalAlignment(SwingConstants.CENTER);
		
		turnLabel = new JLabel("Turn: " + (turnCount + 1) + " of " + MAX_TURNS);
		app.colorize(turnLabel, null, 20);
		
		statusLabel = new JLabel("Finished. Waiting for other clients!");
		app.colorize(statusLabel, null, 20);
		statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
		statusLabel.setVisible(false);
		
		updateView();
		
		if (turnCount < MAX_TURNS) {
			countdownTimer.start();	
		}
	}
	
	/**
	 * Resets the timer and time left!
	 */
	private void resetCountTimer() {
		timeLeft = WAIT_TIME;
		GameUtils.resetTimer(countdownTimer);
		countdownTimer = new Timer(1000, e -> {
			timeLeft--;
			if (timeLeft == 0) {
				compChoose(true);
			} else {
				timerLabel.setText("" + timeLeft);
			}
			repaint();
		});
	}
	
	private void resetCompTimer() {
		GameUtils.resetTimer(compTimer);
		compTimer = new Timer(1200, e -> {
			compChoose(false);
		});
		compTimer.setRepeats(false);	// only run once!
	}
	
	public void update() {}	// active update not used this game!
	
	/**
	 * Sends a JSON packet with the win count for this player.
	 */
	@SuppressWarnings("unchecked")
	public void sendUpdate() {
		NewJSONObject obj = new NewJSONObject(player.getID(), Keys.Commands.MINI_UPDATE);
		obj.put(Keys.PLAYER_NAME, player.getName());
		obj.put(Keys.NAME, "rps");
		obj.put(Keys.WINS, wins);
		controller.send(obj);
		//System.out.println("should be sending update: " + obj.toJSONString());
	
		NewJSONObject k = new NewJSONObject(player.getID(), Keys.Commands.MINI_STOPPED);
		k.put(Keys.PLAYER_NAME, player.getName());
		k.put(Keys.NAME, "rps");
		isActive = false;
		controller.send(k);
		//System.out.println("sent RPS update!");
	}
	
	public void playerPressed() {}	// currently unused
	
	public class Controller extends BaseController {

		public Controller(ClientApp app) {
			super(app);
		}

		public void send(JSONObject out) {
			app.getClient().getIOHandler().send(out);
		}

		public void receive(JSONObject in) {
			
		}
	}	
}
