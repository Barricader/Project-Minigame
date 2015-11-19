package screen;

import java.awt.Color;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;

import main.Director;
import main.Player;
import util.GameUtils;

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
	private JLabel turnsLeftLabel;	// displays turns that are left
	private Director dir;
	
	public StatusPanel(Director dir) {
		this.dir = dir;
		init();
	}
	
	public void update() {
		// TODO update labels
	}
	
	private void init() {
		curPlayerLabel = new JLabel("Current Player Turn:");
		curPlayerLabel = GameUtils.customizeLabel(curPlayerLabel, Color.BLACK, Color.CYAN, 20);

		turnsLeftLabel = new JLabel("Turns Left: " + dir.getTurnsLeft());
		turnsLeftLabel = GameUtils.customizeLabel(turnsLeftLabel, Color.BLACK, Color.CYAN, 20);
		
		// decorate this panel
		setBackground(GameUtils.colorFromHex("3a3a3a"));	// dark grey
		setBorder(new LineBorder(Color.BLACK, 1));
		
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		add(Box.createHorizontalStrut(20));
		add(curPlayerLabel);
		add(Box.createHorizontalGlue());
		add(turnsLeftLabel);
		add(Box.createHorizontalStrut(20));
	}
	
	public void updateCurPlayerLabel(Player p) {
		curPlayerLabel.setForeground(p.getColor());
		curPlayerLabel.setText("Current Player Turn: " + p.getName());
	}
	
	public void updateTurnsLeftLabel() {
		turnsLeftLabel.setText("Turns Left: " + dir.getTurnsLeft());
	}
	
	public JLabel getCurPlayerLabel() {
		return curPlayerLabel;
	}

}
