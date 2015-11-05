package states;

import java.awt.Color;

import javax.swing.JPanel;

import main.NewDirector;

public class EndState extends State {
	
	public EndState(NewDirector director) {
		super(director);
	}

	public void update() {
		System.out.println("End State Updating!");
	}

	public void render() {
		//g = getGraphics();
		g.setColor(Color.RED);
		g.fillRect(20, 20, 100, 100);
		
		// DRAW STUFF HERE!
		System.out.println("End State Rendering");
		
		JPanel test = new JPanel();
		
		repaint();
		
	}

}
