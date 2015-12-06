package main;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.SwingUtilities;
import javax.swing.Timer;

import gameobjects.NewPlayer;
import panels.BoardPanel;
import util.Keys;
import util.NewJSONObject;

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
	
//	/**
//	 * Animates a player. This needs to be called after the player initMove() method
//	 * has been established.
//	 * @param board - Reference to board panel so we can repaint
//	 * @param player - The player to animate
//	 */
//	public void animatePlayer(BoardPanel board, NewPlayer player) {
//		if (!animateTimer.isRunning()) {	// shouldn't run multiple times!
//			animateTimer = new Timer(SPEED, e -> {
//				if (player.getNewLocation() != null) {
//					player.move();
//					board.repaint();	
//				} else {
//					animateTimer.stop();
//					// send command to server that client has finished animating player!
////					board.getController().update();
//					NewJSONObject obj = new NewJSONObject(player.getID(), Keys.Commands.STOPPED);
//					board.getController().send(obj);
//				}
//			});
//			animateTimer.start();	
//		}
//	}
	
	/* This method uses a thread, instead of a timer and makes movement slightly smoother */
	
	/**
	 * Animates a player. THis needs to be called after the player initMove() method
	 * has been invoked.
	 * @param board - Reference to board panel so we can repaint
	 * @param player - The player to animate
	 */
	public void animatePlayer(BoardPanel board, NewPlayer player) {
		ExecutorService executor = Executors.newFixedThreadPool(1);
		
		Runnable move = () -> {
			
			final int FPS = 60;
			long startTime;
			long elapsed;
			long wait;
			int targetTime = 1000 / FPS;
			
			while (player.getNewLocation() != null) {
				startTime = System.nanoTime();
				player.move();
				board.repaint();
				
				elapsed = System.nanoTime() - startTime;
				
				wait = targetTime - elapsed / 1000000;
				
				if (wait < 0) {
					wait = 5;
				}
				
				try {
					Thread.sleep(wait);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			// remove the active indicator after done moving and send update
			player.setActive(false);	
			NewJSONObject obj = new NewJSONObject(player.getID(), Keys.Commands.STOPPED);
			board.getController().send(obj);
		};
		executor.submit(move);	// submits movement task which runs until finished
		executor.shutdown();
	}
}
