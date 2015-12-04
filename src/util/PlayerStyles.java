package util;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;

import javax.imageio.ImageIO;

/**
 * Player styles class that contains arrays for all player images / colors. Player objects
 * can access these images, based on their assignment from ServerDirector. This way, we
 * we can isolate all the image / style data, but allow players to call this class and get
 * a reference, based on the colorNum that they are assigned.
 * @author David Kramer
 *
 */
public class PlayerStyles {
	private static Random rng = new Random();	// for assigning random style
	public static PlayerStyles instance = null;	// singleton instance
	public static boolean[] taken = { false, false, false, false, false, false, false, false, false };
	public static BufferedImage[] imgs = { null, null, null, null, null, null, null, null, null };
	
	public PlayerStyles() {
		load();
		instance = this;
	}
	
	/**
	 * There should only ever be one instance of this class at a time, to ensure
	 * that all data is in sync, when accessing from other classes.
	 * @return - Singleton instance of this class
	 */
	public static PlayerStyles getInstance() {
		if (instance == null) {
			instance = new PlayerStyles();
		}
		return instance;
	}
	
	// player img resource strings
	public static String[] pImgs = 
		{ 	"res/pRed.png", 
			"res/pGreen.png",
			"res/pBlue.png",
			"res/pYellow.png",
			"res/pCyan.png",
			"res/pOrange.png",
			"res/pPurple.png",
			"res/pPink.png",
		};
	
	// player colors
	public static Color[] colors = 
		{ 	new Color(255, 0, 0),
		 	new Color(0, 255, 0),
		 	new Color(0, 0, 255),
		 	new Color(255, 255, 0),
		 	new Color(0, 255, 255),
		 	new Color(255, 127, 0),
		 	new Color(127, 0, 255),	
		 	new Color(255, 0, 255),
		};
	
	/**
	 * Loads in image resource files.
	 */
	public static void load() {
		for (int i = 0; i < pImgs.length; i++) {
			BufferedImage img;
			try {
				img = ImageIO.read(new File(pImgs[i]));
				imgs[i] = img;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 
	 * @return - colornum of style for color / player image array index.
	 */
	public static int getStyle() {
		int colorNum = -1;
		boolean chosen = false;
		int c = 0;
		
		// check to see if any more styles are available, so we don't get caught in infinite loop
		// when trying to create a color num
//		boolean styleAvailable = false;
//		for (int i = 0; i < taken.length; i++) {
//			if (taken[i] = false) {
//				styleAvailable = true;	// we know that there are still some available!
//				break;
//			}
//		}

		while (!chosen) {
			c = rng.nextInt(8);
			if (!taken[c]) {
				taken[c] = true;
				chosen = true;
				colorNum = c;
			}
		}
		return colorNum;	
	}
	
	/**
	 * 
	 * @param colorNum - Index to lookup in colorArray
	 * @return - A color from color array
	 */
	public static Color getColor(int colorNum) {
		return colors[colorNum];
	}
}
