package screen;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;

import main.NewDirector;
import main.Player;

/**
 * This panel will be displayed at the top of the screen in the board state.
 * This will be used to show the turns remaining, the active player, and 
 * any other useful information about the game.
 * @author David Kramer
 *
 */
public class StatusPanel extends JPanel {
	public static final int Y_OFFSET = 20;
	private static final long serialVersionUID = 1L;
	private JLabel curPlayerLabel;	// displays current players' turn
	private JLabel turnsRemainingLabel;	// displays turns that are left
	private NewDirector dir;
	
	public StatusPanel(NewDirector dir) {
		this.dir = dir;
		init();
	}
	
	public void update() {
		// TODO update labels
	}
	
	private void init() {
		curPlayerLabel = new JLabel("Current Player Turn:");
		curPlayerLabel = GameUtils.customizeLabel(curPlayerLabel, Color.BLACK, Color.CYAN, 20);

		turnsRemainingLabel = new JLabel("Turns Remaining: " + dir.getTurnsLeft());
		turnsRemainingLabel = GameUtils.customizeLabel(turnsRemainingLabel, Color.BLACK, Color.CYAN, 20);
		
		// decorate this panel
		setBackground(GameUtils.colorFromHex("3a3a3a"));	// dark grey
		setBorder(new LineBorder(Color.BLACK, 1));
		
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		add(Box.createHorizontalStrut(20));
		add(curPlayerLabel);
		add(Box.createHorizontalGlue());
		add(turnsRemainingLabel);
		add(Box.createHorizontalStrut(20));
	}
	
	public void updateCurPlayerLabel(Player p) {
		curPlayerLabel.setForeground(p.getColor());
		curPlayerLabel.setText("Current Player Turn: " + p.getName());
	}
	
	public void updateTurnsRemainingLabel() {
		System.out.println("UPDATE TURNS REMAINING");
	}
	
	public JLabel getCurPlayerLabel() {
		return curPlayerLabel;
	}

}
