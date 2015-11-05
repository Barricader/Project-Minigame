package states;

import java.awt.Color;

import main.NewDirector;

public class EndState extends State {
	private static final long serialVersionUID = 1L;

	public EndState(NewDirector director) {
		super(director);
	}

	public void update() {
		System.out.println("End State Updating!");
	}

	public void render() {
		//g = getGraphics();
		g.setColor(Color.GREEN);
		g.fillRect(20, 20, 100, 100);
		
		// DRAW STUFF HERE!
		System.out.println("End State Rendering");
		
		repaint();
		
	}

}
