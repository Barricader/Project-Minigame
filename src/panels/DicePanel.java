package panels;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;

import org.json.simple.JSONObject;

import client.ClientApp;
import client.IOHandler;
import gameobjects.NewPlayer;
import main.Dice;
import util.GameUtils;
import util.Keys;
import util.NewJSONObject;
import util.PlayerStyles;

/**
 * Dice panel contains the dice, needed for player movement. The player
 * can click on the dice if it is their turn, and then whatever roll amt
 * they get, will be sent to the server to update other players about
 * this player's roll.
 * @author David Kramer
 *
 */
public class DicePanel extends JPanel implements MouseListener, MouseMotionListener {
	private static final Color BG_COLOR = Color.BLACK;
	private Controller controller;
	private ClientApp app;
	private Dice dice;
	private JLabel statusLabel;	// indicates if it's this player's turn
	private boolean canRoll = false;
	
	public DicePanel(ClientApp app) {
		this.app = app;
		controller = new Controller();
		init();
		addMouseListener(this);
		addMouseMotionListener(this);
		setPreferredSize(new Dimension(100, 100));
		app.colorize(this, new LineBorder(null));
	}
	
	/**
	 * Initializes and lays out components using GridBagLayout.
	 */
	private void init() {
		createComponents();
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.anchor = GridBagConstraints.NORTH;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.weightx = 1.0;
		c.gridy = 0;
		c.ipady = 20;
		c.weighty = 1.0;
		add(statusLabel, c);
	}
	
	/**
	 * Creates GUI components.
	 */
	private void createComponents() {
		dice = new Dice(0, 0, this);
		dice.setEnabled(false);
		
		statusLabel = new JLabel();
		statusLabel.setOpaque(true);
		statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
		statusLabel.setBackground(Color.BLACK);
		statusLabel.setFont(new Font("Courier New", Font.BOLD, 16));
		statusLabel.setMaximumSize(new Dimension(100, 50));
		statusLabel.setText(" ");
	}
	
	/**
	 * Draws dice on the panel.
	 */
	public void paintComponent(Graphics g) {
		g.setColor(BG_COLOR);
		g.fillRect(0, 0, getWidth(), getHeight());
		dice.x = (getWidth() - dice.width) / 2;
		dice.y = (getHeight() - dice.height) / 2;
		dice.draw(g);
		System.out.println("dice enabled ? " + dice.isEnabled());
	}
	
	public void colorizeDice(Color color) {
		dice.colorizeDice(color);	
	}
	
	/**
	 * Handles dice rolling action, if we're allowed to roll.
	 */
	public void mouseClicked(MouseEvent e) {
		if (canRoll) {
			if (dice.contains(e.getPoint())) {
				Cursor cursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
				setCursor(cursor);
				int rollAmt = dice.roll(Dice.SIZE);
				System.out.println("Rolled: " + rollAmt);
				NewPlayer activePlayer = app.getBoardPanel().getActivePlayer();
				
				// disable dice, since we've rolled already!
				dice.setEnabled(false);
				canRoll = false;
				statusLabel.setText("You Rolled: " + rollAmt);
				repaint();
				app.repaint();
				
				// send out update
				NewJSONObject obj = new NewJSONObject(activePlayer.getID(), Keys.Commands.ROLLED);
				obj.put(Keys.PLAYER, activePlayer.toJSONObject());
				obj.put(Keys.ROLL_AMT, rollAmt);
				controller.send(obj);

			}
		}
	}
	
	/**
	 * Updates mouse cursor to pointing hand, if user hovers over mouse
	 * and they can roll.
	 */
	public void mouseMoved(MouseEvent e) {
		Cursor cursor = Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR);
		if (canRoll) {
			if (dice.contains(e.getPoint())) {
				cursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
			}
		}
		setCursor(cursor);
	}
	
	// unused
	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}
	public void mouseDragged(MouseEvent e) {}
	public void mousePressed(MouseEvent e) {}
	public void mouseReleased(MouseEvent e) {}
	
	public Controller getController() {
		return controller;
	}
	
	/**
	 * IO Handler for dice rolling.
	 * @author David Kramer
	 *
	 */
	public class Controller extends IOHandler {

		public void send(JSONObject out) {
			app.getClient().getIOHandler().send(out);
		}

		public void receive(JSONObject in) {
			System.out.println("DICE PANEL RECEIVED: " + in);
			NewPlayer p = NewPlayer.fromJSON(in);
			p = app.getBoardPanel().getPlayers().get(p.getName());
			
			app.getBoardPanel().setActive(p.getName());
			
			if (p.getName().equals(app.getLoginPanel().getClientPlayer().getName())) {
				canRoll = true;
				statusLabel.setVisible(true);
				statusLabel.setText("Your turn!");
				statusLabel.setForeground(Color.BLACK);
				statusLabel.setBackground(PlayerStyles.getColor(p.getStyleID()));
				dice.setEnabled(true);
			} else {
				statusLabel.setText(p.getName() + "'s turn");
				statusLabel.setVisible(true);
				statusLabel.setOpaque(true);
				statusLabel.setForeground(PlayerStyles.getColor(p.getStyleID()));
				statusLabel.setBackground(Color.BLACK);
				dice.setEnabled(false);
			}
			Color c = PlayerStyles.getColor(p.getStyleID());
			
			if (c.getRGB() == Color.YELLOW.getRGB()) {	// make yellow easier to see!
				setForeground(Color.BLACK);
			} else {
				setForeground(Color.WHITE);
			}
			repaint();
		}
		
	}
}
