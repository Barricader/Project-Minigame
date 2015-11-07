package main;

import java.awt.Dimension;

import javax.swing.JFrame;

import screen.StartPanel;

/**
 * Main window of the application that provides a container for the active view.
 * @author David Kramer
 *
 */
public class Main extends JFrame {
	private static final long serialVersionUID = 1L;
	private static Main instance = null;	//singleton reference
	private static final String TITLE = "Project MiniGame by Jo, Jack, and David";
	private static final Dimension SIZE = new Dimension(1280, 720);
	
	private StartPanel startPanel;	// initial start panel
	private NewDirector dir;

	public Main() {
		startPanel = new StartPanel(this);
		dir = new NewDirector(this);
		setContentPane(dir.getState());
		
		setTitle(TITLE);
		setSize(SIZE);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setVisible(true);
		instance = this;
	}
	
	public static Main getInstance() {
		if (instance == null) {
			instance = new Main();
		}
		return instance;
	}
	
	/**
	 * Updates the view when director changes to a different state.
	 */
	public void updateView() {
		getContentPane().removeAll();
		setContentPane(dir.getState());
		validate();
	}
	
	public NewDirector getDirector() {
		return dir;
	}
	
	@SuppressWarnings("unused")
	public static void main(String[] args) {
		Main testScreen = new Main();
	}
}
