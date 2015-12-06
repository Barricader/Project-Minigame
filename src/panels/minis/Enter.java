package panels.minis;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JLabel;

import org.json.simple.JSONObject;

import client.ClientApp;
import panels.BaseMiniPanel;
import util.BaseController;
import util.GameUtils;
import util.Keys;
import util.NewJSONObject;

public class Enter extends BaseMiniPanel {
	private int pressed = 0;
	private int rX = 0, rY = 0;
	private Random r = new Random();
	private JButton theButton;
	private JLabel theLabel, theTime;
	private int diff = 80;
	private int counter = 0;
	private boolean hasWon = false;
	
	public Enter(ClientApp app) {
		super(app);
		theButton = new JButton("PRESS ME");
		theButton.addActionListener(e -> {
			pressed++;
			diff -= 15;
			if (pressed == 3) {
				end();
			}
			else {
				moveButton();
			}
		});
		controller = new Controller(app);
		theLabel = new JLabel("Waiting for other players...");
		theTime = new JLabel("Time left: 20");
		add(theButton);
		add(theLabel);
		theLabel.setVisible(false);
		theTime.setVisible(true);
		moveButton();
		theTime.setLocation(80, 80);
		theTime.setForeground(Color.BLACK);
		theLabel.setLocation(getWidth() / 2, 80);
		theLabel.setForeground(Color.BLACK);
	}
	
	public void init() {
		moveButton();
		theLabel.setVisible(false);
		theLabel.setLocation(getWidth() / 2, 80);
		theButton.setEnabled(true);
		hasWon = false;
		counter = 0;
	}
	
	public void update() {
		theTime.setLocation(new Point(80, 80));
		theLabel.setLocation(new Point(getWidth() / 2, 80));
		System.out.println(theTime.getLocation());
		System.out.println(theLabel.getLocation());
		if (!hasWon) {
			if (counter % diff == 0) {
				moveButton();
			}
			if (counter % 60 == 0) {
				theTime.setText("Time Left: " + (1200/60 - counter/60));
			}
			counter++;
		}
		if (counter % 1201 == 1200) {
			end();
		}
	}
	
	public void moveButton() {
		rX = r.nextInt(app.getStatePanel().getSize().width - theButton.getSize().width + 1);
		rY = r.nextInt(app.getStatePanel().getSize().height - theButton.getSize().height + 1);
		theButton.setLocation(new Point(rX, rY));
	}
	
	public void reset() {
		pressed = 0;
		counter = 0;
		diff = 80;
	}
	
	public void playerPressed() {
		// Send JSON here
		// must put name key with player name
		if (isActive) {
//			System.out.println("Player pressed!");
//			clientPlayer = app.getBoardPanel().getClientPlayer();
//			
//			NewJSONObject k1 = new NewJSONObject(clientPlayer.getID(), Keys.Commands.MINI_UPDATE);
//			k1.put(Keys.NAME, "enter");
//			k1.put(Keys.PLAYER_NAME, clientPlayer.getName());
//			controller.send(k1);
//			
//			NewJSONObject k2 = new NewJSONObject(clientPlayer.getID(), Keys.Commands.MINI_STOPPED);
//			k2.put(Keys.NAME, clientPlayer.getName());
//			controller.send(k2);
//			isActive = false;
//			reset();
		}
	}
	
	private void end() {
		if (isActive) {
			//theLabel.setLocation(getWidth() / 2, 80);
			hasWon = true;
			theButton.setEnabled(false);
			theLabel.setEnabled(true);
			clientPlayer = app.getBoardPanel().getClientPlayer();
			
			NewJSONObject k1 = new NewJSONObject(clientPlayer.getID(), Keys.Commands.MINI_UPDATE);
			k1.put(Keys.NAME, "enter");
			k1.put(Keys.PLAYER_NAME, clientPlayer.getName());
			controller.send(k1);
			
			NewJSONObject k2 = new NewJSONObject(clientPlayer.getID(), Keys.Commands.MINI_STOPPED);
			k2.put(Keys.NAME, clientPlayer.getName());
			controller.send(k2);
			isActive = false;
			reset();
		}
	}
	
	/**
	 * Draws tiles and players to the screen.
	 * @param g - Graphics context to draw to
	 */
	public void paintComponent(Graphics g) {
		final Graphics2D g2d = (Graphics2D)g.create();
		try {
			g2d.setColor(GameUtils.colorFromHex("#C0C0C0"));
			g2d.fillRect(0, 0, getWidth(), getHeight());
			g2d.setColor(Color.BLACK);
			g2d.setFont(new Font("Courier New", Font.BOLD, 50));
			//g2d.drawString("<Enter-Minigame>", getWidth() / 2 - 20, getHeight() / 2 - 20);
			g2d.setColor(Color.CYAN);
			drawPlayers(g2d);
		} finally {
			g2d.dispose();
		}
	}
	
	class Controller extends BaseController {

		public Controller(ClientApp app) {
			super(app);
		}

		public void receive(JSONObject in) {
			
		}
		
	}
}
