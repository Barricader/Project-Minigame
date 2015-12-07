package util;

import java.awt.Color;
import java.util.ArrayList;

import javax.swing.JComponent;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;

public class GlobalColor {
	private Color color;
	
	private ArrayList<ColorComp> comps;
	
	public GlobalColor(Color color) {
		this.color = color;
		comps = new ArrayList<>();
	}
	
	public void add(JComponent c) {
		add(c, null);
	}
	
	public void add(JComponent c, LineBorder border) {
		c.setForeground(color);
		ColorComp comp = new ColorComp(c, border);
		if (border != null) {
			c.setBorder(new LineBorder(color));	
			c.repaint();
		}
		comps.add(comp);
	}
	
	public Color set(ColorComp c) {
		c.getComp().setForeground(color);
		comps.add(c);
		return color;
	}
	
	public void setColor(Color color) {
		this.color = color;
		refresh();
	}
	
	public void refresh() {
		for (ColorComp c : comps) {
			c.getComp().setForeground(color);
			c.getComp().revalidate();
			if (c.hasBorder()) {
				JComponent comp = c.getComp();
				comp.setBorder(new LineBorder(color));
				if (comp instanceof JTextField) {
					comp = (JTextField)comp;
					((JTextField) comp).setCaretColor(color);
				}
			}
		}
	}
	
	public Color getColor() {
		return color;
	}
	
}
