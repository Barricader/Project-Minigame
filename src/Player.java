
import javax.swing.*;
import java.awt.*;

public class Player 
{
	private String name = "";
	private Color col;
	private int score1 = 0;
	private int score2 = 0;
	private int ID = 0;
	private int xPos;
	private int yPos;
	private int width;
	private int height;
	public Player(){
		
	}
	public Player(String n, Color c, int x, int y, int w, int h){//Instantiates a player (creates a player for a user to control)
		name = n;
		col = c;
		xPos = x;
		yPos = y;
		width = w;
		height = h;
	}
	
	public void addScore1(){//adds a point to the score1 variable
		score1++;
	}
	public void minusScore1(){//subtracts a point to the score1 variable
		score1--;
	}
	public int getScore1(){//returns the value of score1
		return score1;
	}
	public void buyScore2(){//This method will be used if the player wants to increase his/her 2nd score can only be used if the player has 10 or more points in score1
		score1 = score1 - 10;
		score2++;
	}
	
	public void setXPos(int x){//set the x position of the player
		xPos = x;
	}
	
	public int getXPos(){//returns the x position of the player
		return xPos;
	}
	
	public void setYPos(int y){//sets the y position of the player
		yPos = y;
	}
	
	public int getYPos(){//returns the y position of the player
		return yPos;
	}
	
	public void setHeight(int h){//sets the height of the player
		height = h;
	}
	
	public int getHieght(){//returns the height of the player
		return height;
	}
	
	public void setWidth(int w){//sets the width of the player
		width = w;
	}
	
	public int getWidth(){//returns the width of the player
		return width;
	}
	
	
	public void setID(int data){//sets the ID (used when to find out the order player gets there turn
		ID = data;
	}
	
	public int getID(){//returns the ID variable(can be used to check the order or let the player get his/her turn
		return ID;
	}
	public String toString(){
		return name;
	}
	
	public void draw(Graphics g){// draws the player object
	g.drawRect(xPos, yPos, width, height);	
	}
}