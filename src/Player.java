import java.awt.Color;

public class Player 
{
	private String name = "";
	private Color col;
	private int score1 = 0;
	private int score2 = 0;
	public Player(String n,Color c){
		name = n;
		col = c;
	}
	
	public void addPoint1s(){
		score1++;
	}
	
	public void addScore2(){
		score2++;
	}
	
	
}
