package screen;

import java.awt.Color;
import java.util.Random;

/**
 * Collection of useful utilities that can be used throughout the game.
 * @author David Kramer
 *
 */
public class GameUtils {

	/**
	 * Returns an RGB color based on a 6-character hex value string. The # at the 
	 * beginning doesn't matter.
	 * @param hexStr - 6 digit hex color value
	 * @return RGB Color
	 */
	public static Color colorFromHex(String hexStr) {
		hexStr = hexStr.trim();

		if (hexStr.charAt(0) == '#') {
			hexStr = hexStr.substring(1, hexStr.length());	// remove # from start of string
		}
		
		int rgbColor = Integer.decode("0x" + hexStr);	// decode as hex
	
		int red = rgbColor >> 16 & 0xff;
		int green = rgbColor >> 8 & 0xff;
		int blue = rgbColor & 0xff;
		
		return new Color(red, green, blue);

	}
	
	/**
	 * Creates a random RGB color
	 * @return RGB Color 
	 */
	public static Color getRandomColor() {
		Random rng = new Random();
		
		int red = rng.nextInt(255);
		int green = rng.nextInt(255);
		int blue = rng.nextInt(255);
		
		return new Color(red, green, blue);
	}
}
