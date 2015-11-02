package main;

import java.awt.Color;
import java.awt.Graphics;
import javax.swing.*;

public class Tile {
	private Color tColor;
	private int action;
	private int TID;
	private final int ACTION_ADDSCORE1 = 0;
	private final int ACTION_SUBTRACT = 1;
	private final int ACTION_ADDSCORE2 = 2;
	private int xPos;
	private int yPos;
	private int width;
	private int height;
	//private final int ACTION_MINIGAME = 3;
	
	public Tile(Color tCol, int act, int tid, int x, int y, int w, int h){//creates tile on the screen on the game board
		tColor = tCol;
		action = act;
		TID = tid;
		xPos = x;
		yPos = y;
		width = w;
		height = h;
	}
	
	public void setTileID(int tid){//Sets the tile ID(specifies what tile it is on the screen
		TID = tid;
	}
	
	public int getTileID(){//Returns the tile ID 
		return TID;
	}
	public void setAction(int act){//Sets the action that the tile will run if the player lands on it
		action = act;
	}
	
	public int getAction(){//returns the action of the tile
		return action;
	}
	
	public void Action(Player p){//The action a tile will compute if the player lands on it
		if(action == ACTION_ADDSCORE1){
			p.addScore1();
		}
		else if(action == ACTION_SUBTRACT){
			p.minusScore1();
		}
		else if(action == ACTION_ADDSCORE2){
			if(p.getScore1() >= 10){
				p.buyScore2();
			}
			
		}
	}
	
	public void draw(Graphics g){//draws the tile
		g.drawRect(xPos, yPos, width, height);
	}
	
	
	
}

