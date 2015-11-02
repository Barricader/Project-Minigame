package screen;

import java.awt.Dimension;

import javax.swing.JFrame;

/**
 * Main window of the application that provides a container for the active view.
 * @author David Kramer
 *
 */
public class Screen extends JFrame {
	public static final String TITLE = "Project MiniGame by Jo, Jack, and David";
	public static final Dimension SIZE = new Dimension(1280, 720);
	
	private StartPanel startPanel;	// initial start panel

	public Screen() {
		startPanel = new StartPanel();
		add(startPanel);
		
		setTitle(TITLE);
		setSize(SIZE);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setVisible(true);
	}
	
	public static void main(String[] args) {
		Screen testScreen = new Screen();
	}
}
