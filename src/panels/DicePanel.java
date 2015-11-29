package panels;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.json.simple.JSONObject;

import client.ClientApp;
import client.IOHandler;
import gameobjects.NewPlayer;
import main.Dice;
import newserver.Keys;
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
		statusLabel = new JLabel("Your turn!");
		statusLabel.setVisible(false);
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
	}

	/**
	 * Handles dice rolling action, if we are allowed to roll.
	 */
	public void mouseClicked(MouseEvent e) {
		if (canRoll) {
			int rollAmt = dice.roll(Dice.SIZE);
			System.out.println("rolled: " + rollAmt);
			NewPlayer p = app.getBoardPanel().getActivePlayer();
			p.setLastRoll(rollAmt);
			p.setHasRolled(true);

			NewJSONObject obj = new NewJSONObject(p.getID(), Keys.Commands.ROLLED);
			obj.put(Keys.ROLL_AMT, rollAmt);
			obj.put(Keys.PLAYER, p.toJSONObject());
			controller.send(obj);

			dice.setEnabled(false);
			canRoll = false;
			statusLabel.setVisible(false);
			app.repaint();
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
			
			if (p.getName().equals(app.getLoginPanel().getClientPlayer().getName())) {
				canRoll = true;
				statusLabel.setVisible(true);
				dice.setEnabled(true);
				repaint();
				JOptionPane.showMessageDialog(app, "Your turn to roll!");
			}
			
			app.getBoardPanel().setActive(p.getName());
		}
		
	}
}
