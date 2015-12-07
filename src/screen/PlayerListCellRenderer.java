package screen;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.border.EmptyBorder;

import gameobjects.NewPlayer;
import util.GameUtils;
import util.PlayerStyles;

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
		
		NewPlayer p = (NewPlayer)value;
		setText(p.getName());
		
		setBorder(new EmptyBorder(10, 10, 10, 10));	// add some padding
		
		if (p.getName().length() > 15) {
			setFont(new Font("Courier New", Font.BOLD, 24));
		} else {
			setFont(new Font("Courier New", Font.BOLD, 30));
		}
		Color playerColor = PlayerStyles.colors[p.getStyleID()];	// based on assign StyleID
		
		/* isSelected no longer applies, as we're using the DisabledItemSelection
		 * Model, which negates any selection. But if we weren't this, would would
		 * swap out fg and bg colors on an element selection.
		 */
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
