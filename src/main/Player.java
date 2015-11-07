package main;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

public class Player extends Rectangle implements Comparable<Player> {
	public static final int WIDTH = 40;
	public static final int HEIGHT = 40;
	
	private String name = "";
	private Color color;
	private int score1 = 0;
	//private int score2 = 0;	// Not implemented currently
	private byte playerID = 0;
	private byte tileID = 0;
	private int xPos;
	private int yPos;
	private int lastRoll;
	
	public Player(){
		
	}
	
	public Player(String name, Color color) {
		this.name = name;
		this.color = color;
		this.lastRoll = 0;
	}
	
	public Player(String name, Color color, byte playerID, byte tileID) {
		this.name = name;
		this.color = color;
		this.playerID = playerID;
		this.tileID = tileID;
		this.lastRoll = 0;
	}
	
	public void setLastRoll(int lr) {
		this.lastRoll = lr;
	}
	
	public int getLastRoll() {
		return lastRoll;
	}
	
//	public void setGoes(int goes) {
//		this.goes = goes;
//	}
//	
//	public int getGoes() {
//		return goes;
//	}
	
	public void addScore1() {
		score1++;
	}
	
	public void minusScore1() {
		score1--;
	}
	
	public int getScore1() {
		return score1;
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
	
	public byte getPlayerID() {
		return playerID;
	}
	
	public byte getTileID() {
		return tileID;
	}
	
	public String getName() {
		return name;
	}
	
	public String toString() {
		return name;
	}
	
	public Color getColor() {
		return color;
	}
	
	public void setID(byte ID) {
		this.playerID = ID;
	}
	
	public void draw(Graphics g) {
		g.setColor(color);
		g.fillOval(xPos, yPos, WIDTH, HEIGHT);	
		g.setColor(Color.BLACK);
		g.drawString(name, xPos, yPos + 50);	// draw name of player
	}

	public int compareTo(Player comparePlayer) {
		int compareRoll = comparePlayer.getLastRoll();
		
		// ASC
		//return this.lastRoll - compareRoll;
		// DESC
		return compareRoll - lastRoll;
	}
}