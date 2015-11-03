package main;

import java.awt.Dimension;

import javax.swing.JFrame;

import screen.StartPanel;

/**
 * Main window of the application that provides a container for the active view.
 * @author David Kramer
 *
 */
public class Main extends JFrame implements Runnable {
	private static final long serialVersionUID = 1L;
	private static final String TITLE = "Project MiniGame by Jo, Jack, and David";
	private static final Dimension SIZE = new Dimension(1280, 720);
	
	private StartPanel startPanel;	// initial start panel
	private Director dir;
	
	// thread stuff
	private Thread thread;
	private boolean running = false;

	public Main() {
		startPanel = new StartPanel(this);
		dir = new Director(this);
		thread = new Thread(this);
		add(startPanel);
		
		setTitle(TITLE);
		setSize(SIZE);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setVisible(true);
		thread.start();
	}
	
	public void run() {
		// Creating a limit on updates to 60 times a second
		running = true;
		long lastTime = System.nanoTime();
		final double ns = 1000000000.0 / 60.0;
		double delta = 0;
		requestFocus();
		while (running) {
			long now = System.nanoTime();
			delta += (now - lastTime) / ns;
			lastTime = now;
			while (delta >= 1) {
				dir.loop();
				delta--;
			}
		}
	}
	
	public Director getDirector() {
		return dir;
	}
	
	public static void main(String[] args) {
		Main testScreen = new Main();
	}
}
