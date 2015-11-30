package panels;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.json.simple.JSONObject;

import client.ClientApp;
import client.IOHandler;
import gameobjects.NewPlayer;
import main.Dice;
import newserver.Keys;
import newserver.PlayerStyles;
import util.GameUtils;
import util.NewJSONObject;

public class DicePanel extends JPanel implements MouseListener {
	private static final Color BG_COLOR = GameUtils.colorFromHex("#D6D9DF");	// gray
	private Controller controller;
	private ClientApp app;
	private Dice dice;
	private JLabel statusLabel;	// indicates if it's this player's turn
	private boolean canRoll = false;
	
	public DicePanel(ClientApp app) {
		this.app = app;
		controller = new Controller();
		dice = new Dice(0, 0, this);
		dice.setEnabled(false);
		addMouseListener(this);
		statusLabel = new JLabel();
		statusLabel.setOpaque(true);
		statusLabel.setBorder(new EmptyBorder(10, 10, 10, 10)); // add some padding
		statusLabel.setBackground(Color.BLACK);
		statusLabel.setFont(new Font("Courier New", Font.BOLD, 20));
		add(statusLabel);
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
	
	/**
	 * Handles dice rolling action, if we're allowed to roll.
	 */
	public void mouseClicked(MouseEvent e) {
		if (canRoll) {
			if (dice.contains(e.getPoint())) {
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

	// unused
	public void mousePressed(MouseEvent e) {}
	public void mouseReleased(MouseEvent e) {}
	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}
	
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
				dice.setEnabled(true);
//				JOptionPane.showMessageDialog(app, "Your turn to roll!");
			} else {
				statusLabel.setText(p.getName() + "'s turn");
				statusLabel.setVisible(true);
				dice.setEnabled(false);
			}
			statusLabel.setForeground(PlayerStyles.getColor(p.getStyleID()));
			statusLabel.setBackground(Color.BLACK);
			statusLabel.setOpaque(true);
			repaint();
			
		}
		
	}
}
