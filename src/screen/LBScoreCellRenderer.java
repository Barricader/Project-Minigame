package screen;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.JList;
import client.ClientApp;
import gameobjects.NewPlayer;
import util.PlayerStyles;

/**
 * List cell renderer for leaderboard player scores.
 * @author David Kramer
 *
 */
public class LBScoreCellRenderer extends LBPlayerCellRenderer {
	private static final long serialVersionUID = 1L;
	private JList<String> pList;
	
	public LBScoreCellRenderer(ClientApp app, JList<String> pList) {
		super(app);
		this.pList = pList;
	}
	
	public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
			boolean cellHasFocus) {
		
		String pName = pList.getModel().getElementAt(index);
		NewPlayer p = app.getBoardPanel().getPlayers().get(pName);
		Color c = PlayerStyles.colors[p.getStyleID()];
		setText("" + value);
		style(c, new Font("Courier New", Font.BOLD, 12));
		return this;
	}
}
