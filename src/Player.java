import java.awt.Color;

public class Player 
{
	private String name = "";
	private Color col;
	private int score1 = 0;
	private int score2 = 0;
	private int ID = 0;
	public Player(String n,Color c){
		name = n;
		col = c;
		
	}
	
	public void addScore1(){//adds a point to the score1 variable
		score1++;
	}
	public void minusScore1(){//subtracts a point to the score1 variable
		score1--;
	}
	
	public void addScore2(){//adds a point to score2 variable
		score2++;
	}
	
	public void setID(int data){//sets the ID (used when to find out the order player gets there turn
		ID = data;
	}
	
	public int getID(){//returns the ID variable(can be used to check the order or let the player get his/her turn
		return ID;
	}
 
	public String toString() {
		return name;
	}
	
}
