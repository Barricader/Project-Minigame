package screen;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.border.EmptyBorder;

import client.ClientApp;
import gameobjects.NewPlayer;
import util.PlayerStyles;

/**
 * List cell renderer for the leaderboard player names.
 * @author David Kramer
 *
 */
public class LBPlayerCellRenderer extends JLabel implements ListCellRenderer<Object> {
	private static final long serialVersionUID = 1L;
	protected ClientApp app;
	
	protected LBPlayerCellRenderer() {}	// blank constructor
	
	public LBPlayerCellRenderer(ClientApp app) {
		this.app = app;
	}
	
	public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
			boolean cellHasFocus) {
		
		String pName = (String)value;
		NewPlayer p = app.getBoardPanel().getPlayers().get(pName);
		if (app.getBoardPanel().getClientPlayer().getName().equals(pName)) {
			setText("" + value + "*");
		} else {
			setText("" + value);
		}
		Color c = PlayerStyles.colors[p.getStyleID()];
		style(c, new Font("Courier New", Font.BOLD, 12));
		return this;
	}
	
	/**
	 * Styles current index list value item with specified Color and font.
	 * @param fg - Foreground color
	 * @param font - Font to apply
	 */
	protected void style(Color fg, Font font) {
		setBorder(new EmptyBorder(2, 2, 2, 2));
		setBackground(Color.BLACK);
		setForeground(fg);
		setFont(font);
	}
}
