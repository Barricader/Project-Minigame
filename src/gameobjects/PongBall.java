package gameobjects;

import java.awt.Color;
import java.awt.Graphics2D;

import org.json.simple.JSONObject;

public class PongBall extends GameObject {
	public static final int WIDTH = 10;
	public static final int HEIGHT = 10;
	public static final Color COLOR = Color.WHITE;
	
	private int xVel;
	private int yVel;
	
	public PongBall() {
		xVel = 5;
		yVel = 5;
		width = WIDTH;
		height = HEIGHT;
	}
	
	public void draw(Graphics2D g2d) {
		g2d.setColor(COLOR);
		g2d.fillOval(x, y, WIDTH, HEIGHT);
	}
	
	public void setXVel(int xVel) {
		this.xVel = xVel;
	}
	
	public void setYVel(int yVel) {
		this.yVel = yVel;
	}
	
	public int getXVel() {
		return xVel;
	}
	
	public int getYVel() {
		return yVel;
	}
	
	public JSONObject toJSONObject() {
		JSONObject pongBall = new JSONObject();
		pongBall.put("x", x);
		pongBall.put("y", y);
		pongBall.put("xVel", xVel);
		pongBall.put("yVel", yVel);
		return pongBall;
	}

}
