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
	public static final String TITLE = "Project MiniGame by Jo, Jack, and David";
	public static final Dimension SIZE = new Dimension(1280, 720);
	
	private StartPanel startPanel;	// initial start panel

	public Main() {
		startPanel = new StartPanel();
		add(startPanel);
		
		setTitle(TITLE);
		setSize(SIZE);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setVisible(true);
	}
	
	public static void main(String[] args) {
		Main testScreen = new Main();
	}
}
