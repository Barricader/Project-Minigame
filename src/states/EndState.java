package states;

import main.NewDirector;

public class EndState extends State {
	private static final long serialVersionUID = 1L;

	public EndState(NewDirector director) {
		super(director);
	}

	public void update() {
		System.out.println("End State Updating!");
	}

	@Override
	public void render() {
		// TODO Auto-generated method stub
		
	}
}
