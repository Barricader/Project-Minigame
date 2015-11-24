package main;

import javax.swing.Timer;

import gameobjects.NewPlayer;
import panels.BoardPanel;

/**
 * Utility class for animating player movement. Use this class to handle
 * moving players, with animation. 
 * @author David Kramer
 *
 */
public class Animator {
	public static final int SPEED = 10;	// timer update speed (affects how fast player movement is rendered)
	private Timer animateTimer;	// timer to control animation
	
	public Animator() {
		animateTimer = new Timer(0, null);	// default initialization
	}
	
	/**
	 * Animates a player. This needs to be called after the player initMove() method
	 * has been established.
	 * @param board - Reference to board panel so we can repaint
	 * @param player - The player to animate
	 */
	public void animatePlayer(BoardPanel board, NewPlayer player) {
		if (!animateTimer.isRunning()) {	// shouldn't run multiple times!
			animateTimer = new Timer(SPEED, e -> {
				if (player.getNewLocation() != null) {
					player.move();
					board.repaint();	
				} else {
					animateTimer.stop();
				}
			});
			animateTimer.start();	
		}
	}
}
