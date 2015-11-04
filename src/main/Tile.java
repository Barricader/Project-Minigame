package main;

import java.awt.Color;
import java.awt.Graphics;
import javax.swing.*;

public class Tile {
	public static final int ACTION_ADDSCORE1 = 0;
	public static final int ACTION_SUBTRACT = 1;
	public static final int ACTION_ADDSCORE2 = 2;
	
	private Color color;
	private int action;
	private int ID;
	private int xPos;
	private int yPos;
	private int width;
	private int height;
	//private final int ACTION_MINIGAME = 3;
	
	public Tile(Color color, int action, int ID, int xPos, int yPos, int width, int height) {
		this.color = color;
		this.action = action;
		this.ID = ID;
		this.xPos = xPos;
		this.yPos = yPos;
		this.width = width;
		this.height = height;
	}
	
	
	public void setTileID(int id) {
		ID = id;
	}
	
	public int getTileID() {
		return ID;
	}
	public void setAction(int act) {
		action = act;
	}
	
	public int getAction() {
		return action;
	}
	
	public int getXPos() {
		return xPos;
	}
	
	public int getYPos() {
		return yPos;
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
	
	public Color getColor() {
		return color;
	}
	
	public void action(Player p) {//The action a tile will compute if the player lands on it
		if(action == ACTION_ADDSCORE1){
			p.addScore1();
		}
		else if(action == ACTION_SUBTRACT) {
			p.minusScore1();
		}
		else if(action == ACTION_ADDSCORE2) {
			if(p.getScore1() >= 10){
				p.buyScore2();
			}
			
		}
	}
	
	public void draw(Graphics g) {
		g.setColor(Color.BLACK);
		g.drawRect(xPos, yPos, width, height);	// draw outline
		g.setColor(color);
		g.fillRect(xPos, yPos, width, height);
	}
	
}

