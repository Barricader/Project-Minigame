package screen;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import gameobjects.NewPlayer;
import newserver.PlayerStyles;

/**
 * This class is the cell renderer for the Player list object. This renders each cell 
 * with a custom style, that is based on the player object's color. 
 * @author David Kramer
 *
 */
public class PlayerListCellRenderer extends JLabel implements ListCellRenderer<Object> {
	private static final long serialVersionUID = 1L;

	public PlayerListCellRenderer() {
		setOpaque(true);
	}

	public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
			boolean cellHasFocus) {
		
		setText(value.toString());
		NewPlayer p = (NewPlayer)value;
		
		if (p.getName().length() > 10) {
			setFont(new Font("Courier New", Font.BOLD, 12));
		} else {
			setFont(new Font("Courier New", Font.BOLD, 24));
		}
		Color playerColor = PlayerStyles.colors[p.getStyleID()];	// based on assign StyleID
		
		if (isSelected) {
			setBackground(playerColor);
			setForeground(Color.WHITE);

		} else {
			setBackground(Color.BLACK);
			setForeground(playerColor);
		}
		
		return this;
	}
}
