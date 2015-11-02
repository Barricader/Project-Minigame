package main;

import javax.swing.*;
import java.awt.*;

public class Player 
{
	private String name = "";
	private Color color;
	private int score1 = 0;
	private int score2 = 0;
	private byte playerID = 0;
	private byte tileID = 0;
	private int xPos;
	private int yPos;
	private int width;
	private int height;
	
	public Player(){
		
	}
	
	public Player(String name, Color color) {
		this.name = name;
		this.color = color;
	}
	
	public Player(String name, Color color, byte playerID, byte tileID) {
		this.name = name;
		this.color = color;
		this.playerID = playerID;
		this.tileID = tileID;
	}
	
	public void addScore1() {
		score1++;
	}
	
	public void minusScore1() {
		score1--;
	}
	
	public int getScore1() {
		return score1;
	}
	
	public void buyScore2() {
		score1 = score1 - 10;
		score2++;
	}
	
	public void setXPos(int x) {
		xPos = x;
	}
	
	public int getXPos() {
		return xPos;
	}
	
	public void setYPos(int y) {
		yPos = y;
	}
	
	public int getYPos() {
		return yPos;
	}
	
	public void setHeight(int h) {
		height = h;
	}
	
	public int getHeight() {
		return height;
	}
	
	public void setWidth(int w) {
		width = w;
	}
	
	public int getWidth() {
		return width;
	}
	
	public byte getPlayerID() {
		return playerID;
	}
	
	public byte getTileID() {
		return tileID;
	}
	
	public String toString() {
		return name;
	}
	
	public void draw(Graphics g) {
		g.drawRect(xPos, yPos, width, height);	
	}
}