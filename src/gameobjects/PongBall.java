package gameobjects;

import java.awt.Color;
import java.awt.Graphics2D;

import org.json.simple.JSONObject;

public class PongBall extends GameObject {
	private static final long serialVersionUID = 7246226056305104239L;
	public static final int WIDTH = 12;
	public static final int HEIGHT = 12;
	public static final Color COLOR = Color.WHITE;
	
	private int xVel;
	private int yVel;
	public int lastX;	// previous x-pos
	public int lastY;	// previous y-pos
	private String lastHitPName;
	
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
	
	public void setLastHitPName(String pName) {
		this.lastHitPName = pName;
	}
	
	public void reflectX() {
		this.xVel *= -1;
	}
	
	public void reflectY() {
		this.yVel *= -1;
	}
	
	public void setXVel(int xVel) {
		this.xVel = xVel;
	}
	
	public void setYVel(int yVel) {
		this.yVel = yVel;
	}
	
	public String getLastHitPName() {
		return lastHitPName;
	}
	
	public int getXVel() {
		return xVel;
	}
	
	public int getYVel() {
		return yVel;
	}
	
	@SuppressWarnings("unchecked")
	public JSONObject toJSONObject() {
		JSONObject pongBall = new JSONObject();
		pongBall.put("x", x);
		pongBall.put("y", y);
		pongBall.put("xVel", xVel);
		pongBall.put("yVel", yVel);
		return pongBall;
	}

}
