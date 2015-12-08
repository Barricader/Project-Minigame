package util;

import javax.swing.JComponent;
import javax.swing.border.LineBorder;

public class ColorComp {
	private JComponent comp;
	private LineBorder border;
	private boolean hasBorder;
	
	public ColorComp(JComponent comp) {
		this.comp = comp;
	}
	
	public ColorComp(JComponent comp, LineBorder border) {
		this.comp = comp;
		this.border = border;
		
		if (border != null) {
			hasBorder = true;	
		}
	}
	
	public JComponent getComp() {
		return comp;
	}
	
	public LineBorder getBorder() {
		return border;
	}
	
	public boolean hasBorder() {
		return hasBorder;
	}
}
