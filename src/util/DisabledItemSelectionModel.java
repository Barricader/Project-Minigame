package util;

import javax.swing.DefaultListSelectionModel;

/**
 * This basic class just makes it so that you can't click and highlight a 
 * value in a list.
 * @author David Kramer
 *
 */
public class DisabledItemSelectionModel extends DefaultListSelectionModel {
	
	/**
	 * Whatever interval selection was made, this won't allow for a highlight
	 * to be draw behind the element.
	 */
	public void setSelectionInterval(int index0, int index1) {
		super.setSelectionInterval(-1, -1);
	}
}