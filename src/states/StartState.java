package states;

import java.awt.Color;

import main.NewDirector;

public class StartState extends State {
	private static final long serialVersionUID = 1L;

	public StartState(NewDirector director) {
		super(director);
	}

	public void update() {
		System.out.println("State State Updating!");
	}

	public void render() {
		// REMOVE THESE COMMENTS AFTER g GETS FIXED
		//g.setColor(Color.BLUE);
		//g.fillRect(20, 20, 100, 100);
		
		// DRAW STUFF HERE!
		System.out.println("Start State Rendering");
		
		repaint();
		
	}

}
