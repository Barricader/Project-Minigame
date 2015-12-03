package panels.minis;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.Timer;

import org.json.simple.JSONObject;

import client.ClientApp;
import client.IOHandler;
import gameobjects.NewPlayer;
import panels.BaseMiniPanel;
import util.Keys;
import util.NewJSONObject;
import util.PlayerStyles;

public class RPS extends BaseMiniPanel {
	private static final int MAX_TURNS = 3;	// should only go 3 times
	private static final int WAIT_TIME = 10;
	private int timeLeft = WAIT_TIME;
	
	private NewPlayer player;
	private String[] choices = {"Rock", "Paper", "Scissor"};
	private String playerChoice;
	private Random rng = new Random();
	
	private JButton rockBtn;
	private JButton paperBtn;
	private JButton scissorBtn;
	private JLabel timerLabel;
	private JLabel playerChoiceLabel;
	private JLabel compChoiceLabel;	// choice that the computer chose
	private Timer countdownTimer;	
	
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
		resetTimer();
		
		createComponents();
		
		// player choice
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.anchor = GridBagConstraints.NORTHWEST;
		c.gridx = 0;
		c.gridy = 0;
		add(playerChoiceLabel, c);
		
		// timer label
		c.anchor = GridBagConstraints.NORTH;
		c.gridx = 1;
		c.gridy = 0;
		add(timerLabel, c);
		
		// comp choice
		c.anchor = GridBagConstraints.NORTHEAST;
		c.gridx = 2;
		add(compChoiceLabel, c);
		
		// rock
		c.anchor = GridBagConstraints.CENTER;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.weightx = 1.0;
		c.gridy = 1;
		c.ipady = 50;
		c.weighty = 1.0;
		add(rockBtn, c);
		
		// paper
		c.gridx = 1;
		c.gridy = 1;
		c.weighty = 1.0;
		add(paperBtn, c);
		
		// scissor
		c.gridx = 2;
		c.gridy = 1;
		c.weighty = 1.0;
		add(scissorBtn, c);
		
		countdownTimer.start();
	}
	
	/**
	 * Resets the timer and time left!
	 * @return - this countdown timer.
	 */
	private void resetTimer() {
		timeLeft = WAIT_TIME;
		
		countdownTimer = new Timer(1000, e -> {
			timeLeft--;
			
			if (timeLeft == 0) {
				compChoose(true);	// choose for player!
				countdownTimer.stop();
			} else {
				timerLabel.setText("" + timeLeft);
			}
		});
	}
	
	/**
	 * Creates GUI components.
	 */
	private void createComponents() {
		rockBtn = new JButton("Rock");
		rockBtn.addActionListener(e -> {
			playerChoice = "Rock";
			playerChoiceLabel.setText(playerChoiceLabel.getText() + playerChoice);
			compChoose(false);
		});
		
		paperBtn = new JButton("Paper");
		paperBtn.addActionListener(e -> {
			playerChoice = "Paper";
			playerChoiceLabel.setText(playerChoiceLabel.getText() + playerChoice);
			compChoose(false);
		});
		
		scissorBtn = new JButton("Scissor");
		scissorBtn.addActionListener(e -> {
			playerChoice = "Scissor";
			playerChoiceLabel.setText(playerChoiceLabel.getText() + playerChoice);
			compChoose(false);
		});
		
		reset();
	}
	
	/**
	 * Computer chooses, and then it checks to see who won.
	 * @param choosePlayer - flag to set if we should also choose player
	 */
	private void compChoose(boolean choosePlayer) {
		countdownTimer.stop();
		turnCount++;
		
		// choose for player
		if (choosePlayer) {
			playerChoice = choices[rng.nextInt(choices.length)];
			playerChoiceLabel.setText(playerChoiceLabel.getText() + playerChoice);
		}
		
		String compChoice = choices[rng.nextInt(choices.length)];
		compChoiceLabel.setText(compChoiceLabel.getText() + compChoice);
		boolean compWon = false;
		boolean playerWon = false;
		
		// determine who won
		if (compChoice.equals(playerChoice)) {
			JOptionPane.showMessageDialog(app, "Tied!");
			reset();
			return;
		}
		
		if (compChoice.equals("Rock")) {
			if (playerChoice.equals("Paper")) {
				playerWon = true;
			} else if (playerChoice.equals("Scissor")) {
				compWon = true;
			}
		} else if (compChoice.equals("paper")) {
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
		
		if (turnCount == MAX_TURNS) {
			sendUpdate();
			return;
		}
		reset();	
	}
	
	private void reset() {
		resetTimer();
		playerChoice = "";
		timerLabel = new JLabel("" + timeLeft);
		timerLabel.setFont(new Font("Courier New", Font.BOLD, 50));
		
		compChoiceLabel = new JLabel("Computer Choice: ");
		compChoiceLabel.setFont(new Font("Courier New", Font.BOLD, 30));
		
		player = app.getBoardPanel().getClientPlayer();
		playerChoiceLabel = new JLabel();
		playerChoiceLabel.setText(player.getName() + " Choice: ");
		playerChoiceLabel.setForeground(PlayerStyles.getColor(player.getStyleID()));
		playerChoiceLabel.setFont(new Font("Courier New", Font.BOLD, 30));
		
		repaint();
		System.out.println("reset!");
		countdownTimer.start();
	}
	
	public void update() {
	}
	
	/**
	 * Sends a JSON packet with the win count for this player.
	 */
	public void sendUpdate() {
		NewJSONObject obj = new NewJSONObject(player.getID(), Keys.Commands.MINI_UPDATE);
		obj.put(Keys.PLAYER_NAME, player.getName());
		obj.put(Keys.NAME, "rps");
		obj.put(Keys.WINS, wins);
		controller.send(obj);
		System.out.println("should be sending update: " + obj.toJSONString());
	
		NewJSONObject k = new NewJSONObject(player.getID(), Keys.Commands.MINI_STOPPED);
		k.put(Keys.PLAYER_NAME, player.getName());
		k.put(Keys.NAME, "rps");
		isActive = false;
		controller.send(k);
	}
	
	public void playerPressed() {
		// Send JSON here
		// must put name key with player name
//		if (isActive) {
//			System.out.println("Player pressed!");
//			clientPlayer = app.getBoardPanel().getClientPlayer();
//			NewJSONObject k = new NewJSONObject(clientPlayer.getID(), Keys.Commands.MINI_STOPPED);
//			k.put(Keys.NAME, clientPlayer.getName());
//			controller.send(k);
//			isActive = false;
//		}
	}
	
//	/**
//	 * Draws tiles and players to the screen.
//	 * @param g - Graphics context to draw to
//	 */
//	public void paintComponent(Graphics g) {
//		final Graphics2D g2d = (Graphics2D)g.create();
//		try {
//			g2d.setColor(GameUtils.colorFromHex("#C0C0C0"));
//			g2d.fillRect(0, 0, getWidth(), getHeight());
//			g2d.setColor(GameUtils.getRandomColor());
//			g2d.fillOval(40, 40, 20, 60);
//			g2d.setFont(new Font("Courier New", Font.BOLD, 50));
//			g2d.drawString("Rock Paper Scissors", app.getStatePanel().getWidth() / 3, app.getStatePanel().getHeight() / 3);
//			g2d.setColor(Color.CYAN);
//			drawPlayers(g2d);
//		} finally {
//			g2d.dispose();
//		}
//	}
	
	public class Controller extends IOHandler {

		public void send(JSONObject out) {
			app.getClient().getIOHandler().send(out);
		}

		public void receive(JSONObject in) {
			
		}
	}	
}
