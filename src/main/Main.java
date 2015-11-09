package main;

import java.awt.Dimension;
import javax.swing.JFrame;

/**
 * Main window of the application that provides a container for the active view. This class
 * contains the director of the application, and all content rendered to the screen is 
 * provided by the director object.
 * @author David Kramer
 * @author JoJones
 *
 */
public class Main extends JFrame {
	private static final long serialVersionUID = 1L;
	private static final String TITLE = "Project MiniGame by Jo, Jack, and David";
	private static final Dimension SIZE = new Dimension(1280, 720);
	
	private static Main instance = null;	//singleton reference
	
//	private StartPanel startPanel;	// initial start panel	//TODO remove this when we know everything else works!
	private NewDirector dir;	// main director to control states of application

	/**
	 * Constructs window and sets up the viewable content of the game.
	 */
	public Main() {
//		startPanel = new StartPanel(this);
		dir = new NewDirector(this);
		setContentPane(dir.getState());
		setTitle(TITLE);
		setSize(SIZE);
		setMinimumSize(new Dimension(800, 600));  // no smaller than 800 x 600!
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setVisible(true);
		instance = this;
	}
	
	/**
	 * 
	 * @return Singleton instance of this application.
	 */
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
	
	@SuppressWarnings("unused")
	/**
	 * Main method that starts the application.
	 * @param args
	 */
	public static void main(String[] args) {
		Main testScreen = new Main();
	}
	
	// accessor methods
	public NewDirector getDirector() {
		return dir;
	}

}
