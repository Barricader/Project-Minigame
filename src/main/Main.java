package main;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JPanel;

import client.ClientPanel;
import input.Keyboard;

/**
 * Main window of the application that provides a container for the active view. This class
 * contains the director of the application, and all content rendered to the screen is 
 * provided by the director object. This class also contains the ClientPanel sidebar which 
 * allows for chat, and connection to the server, as a sidebar.
 * @author David Kramer
 * @author JoJones
 *
 */
public class Main extends JFrame {
	private static final long serialVersionUID = 1L;
	private static final String TITLE = "Project MiniGame by Jo, Jack, and David";
	private static final Dimension SIZE = new Dimension(1280, 720);
	
	private static Main instance = null;	//singleton reference

	private Director dir;					// main director to control states of application
	private JPanel panel;					// container to hold everything
	private ClientPanel clientPanel;		// chat / command sidebar for server connection
	private Keyboard key;

	/**
	 * Constructs window and sets up the viewable content of the game.
	 */
	public Main() {
		dir = new Director(this);
		key = new Keyboard();
		clientPanel = new ClientPanel();	// create here, so it's not overwritten when view is updated!
		init();
		setTitle(TITLE);
		setSize(SIZE);
		setMinimumSize(SIZE);  // no smaller than 1280 x 720
		setLocationRelativeTo(null);
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);	// handle disconnection first!
		setVisible(true);
		addKeyListener(key);
		
		// disconnect from server when client is closed!
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				if (clientPanel.isConnected()) {
					clientPanel.send("!quit " + clientPanel.getClient().getID());
				}
				setDefaultCloseOperation(EXIT_ON_CLOSE);
				dispose();
			}
		});
		
		instance = this;
	}
	
	private void init() {
		createComponents();
	}
	
	private void createComponents() {
		// panel that will hold director state and chat panel
		System.out.println("Creating components!");
		panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		
		GridBagConstraints c = new GridBagConstraints();
		
		// director content
		c.fill = GridBagConstraints.BOTH;
		c.anchor = GridBagConstraints.WEST;
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1.0;
		c.weighty = 1.0;
		panel.add(dir.getState(), c);
		
		System.out.println("Director state: " + dir.getState());
		
		// client sidebar
		c.anchor = GridBagConstraints.EAST;
		c.gridx = 1;
		c.gridy = 0;
		c.weightx = 0;
		panel.add(clientPanel, c);
		
		setContentPane(panel);
	}
	
	/**
	 * Updates the view when director changes to a different state.
	 */
	public void updateView() {
		getContentPane().removeAll();
		panel.removeAll();
		createComponents();
		panel.validate();
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
	
	public Director getDirector() {
		return dir;
	}
	
	public ClientPanel getClientPanel() {
		return clientPanel;
	}
	
	public Keyboard getKeyboard() {
		return key;
	}

}
