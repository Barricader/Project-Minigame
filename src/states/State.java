package states;

import javax.swing.JPanel;

import main.NewDirector;

/**
 * Base class for all states. A state is represents how things should be updated
 * and rendered to the screen. A state will define its own behavior for updating
 * and drawing to the screen, and will be linked to the director.
 * @author David Kramer
 *
 */
public abstract class State {
	protected NewDirector director;	// director we will communicate too
	protected JPanel drawPanel;	// drawing object we will draw too
	protected State currentState;	// what state is currently active?
	
	/**
	 * Method that must be implemented to allow a state to update itself.
	 */
	public abstract void update();
	
	/**
	 * Method that must be implemented to allow a state to draw itself to
	 * the screen using an active drawing object (i.e. JPanel).
	 */
	public abstract void render();
	
}
