package main;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;

import javax.imageio.ImageIO;

public class Dice {
	private final static int SIZE = 6;
	private Image[] imgs;
	private int x, y;
	private int value;
	private Random r;
	
	public Dice(int x, int y) {
		this.x = x;
		this.y = y;
		this.value = 1;
		this.imgs = new Image[] {null, null, null, null, null, null};
		try {
			load();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void load() throws IOException {
		String[] paths = { "", "res/die1.png", "res/die2.png", "res/die3.png",
						   "res/die4.png", "res/die5.png", "res/die6.png" };
		for (int i = 1; i <= 6; i++) {
			BufferedImage img = ImageIO.read(new File(paths[i]));
			imgs[i] = img;
		}
	}
	
	public void setX(int x) {
		this.x = x;
	}
	
	public void setY(int y) {
		this.y = y;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public int getValue() {
		return value;
	}
	
	public int roll(int size) {
		value = r.nextInt(size) + 1;
		return value;
	}
	
	public void draw(Graphics g) {
		g.drawImage(imgs[value], x, y, 64, 64, null);
	}
}

