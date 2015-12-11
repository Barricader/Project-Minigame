package util;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.Timer;

import gameobjects.NewPlayer;

/**
 * Collection of useful utilities that can be used throughout the game.
 * @author David Kramer
 *
 */
public class GameUtils {
	public static Random random = new Random();

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
	 * Returns a new color that is inverted from the color passed in.
	 * @param colorToInvert
	 * @return Inverted RGB Color Value
	 */
	public static Color getInvertedColor(Color colorToInvert) {
		int rgbColor = colorToInvert.getRGB();
		
		int red = ~rgbColor >> 16 & 0xff;
		int green = ~rgbColor >> 8 & 0xff;
		int blue = ~rgbColor & 0xff;
		
		return new Color(red, green, blue);
	}
	
	/**
	 * Creates a random RGB color
	 * @return RGB Color 
	 */
	public static Color getRandomColor() {		
		int red = random.nextInt(255);
		int green = random.nextInt(255);
		int blue = random.nextInt(255);
		
		return new Color(red, green, blue);
	}
	
	/**
	 * Creates a random RGB color that has a guarantee for displaying
	 * brighter.
	 * @return RGB Color
	 */
	public static Color getBrightColor() {
		int red = random.nextInt(130) + 125;
		int green = random.nextInt(130) + 125;
		int blue = random.nextInt(130) + 125;
		
		return new Color(red, green, blue);
	}
	
	public static Color getAlphaColor(Color c, int a) {
		int r = c.getRed();
		int g = c.getGreen();
		int b = c.getBlue();
		
		return new Color(r, g, b, a);
	}
	
	/**
	 * Customize labels to reduce some redundancy when applying styles. The font set is
	 * Courier New BOLD.
	 * @param label JLabel to customize
	 * @param bgColor background color
	 * @param fgColor foreground color
	 * @param fontSize size of font
	 * @return customized JLabel
	 */
	public static JLabel customizeLabel(JLabel label, Color bgColor, Color fgColor, int fontSize) {
		label.setFont(new Font("Courier New", Font.BOLD, fontSize));
		label.setBackground(bgColor);
		label.setForeground(fgColor);
		return label;
	}
	
	/**
	 * Customizes any component with specified colors and font size.
	 * @param c - Component to customize
	 * @param bgColor - background color
	 * @param fgColor - foreground color
	 * @param fontSize - size of font
	 * @return
	 */
	public static Component customizeComp(Component c, Color bgColor, Color fgColor, int fontSize) {
		c.setFont(new Font("Courier New", Font.BOLD, fontSize));
		c.setBackground(bgColor);
		c.setForeground(fgColor);
		return c;
	}
	
	/**
	 * Utility method that converts a map to an array.
	 * @param map - Map to convert
	 * @param c - The class type that the map values contain
	 * @return - An array, based off values stored in map
	 */
	@SuppressWarnings("unchecked")
	public static <K, V> V[] mapToArray(Map<K, V> map, Class<V> c) {
		V[] temp = (V[]) Array.newInstance(c, map.size());
		Iterator<V> iterator = map.values().iterator();
		int i = 0;
		while (iterator.hasNext()) {
			temp[i] = iterator.next();
			i++;
		}
		return temp;
	}
	
	/**
	 * Utility method to sort players by score in descending order
	 */
	public static void sortPlayersByScore(NewPlayer[] pArray) {
		Arrays.sort(pArray, new Comparator<NewPlayer>() {
			public int compare(NewPlayer p1, NewPlayer p2) {
				int score1 = p1.getScore();
				int score2 = p2.getScore();
				
				if (score1 == score2) {
					return 0;
				} else if (score1 < score2) {
					return 1;
				} else {
					return -1;
				}
			}
		});
	}
	
	/**
	 * Utility method to sort players by their name.
	 * @param pArray
	 */
	public static void sortPlayersByName(NewPlayer[] pArray) {
		Arrays.sort(pArray, new Comparator<NewPlayer>() {
			public int compare(NewPlayer p1, NewPlayer p2) {
				return p1.getName().compareTo(p2.getName());
			}
		});
	}
	
	/**
	 * Clears any actions on a JButton.
	 * @param btnToReset - The JButton to remove actions from.
	 */
	public static void clearActions(JButton btnToReset) {
		for (ActionListener a : btnToReset.getActionListeners()) {
			btnToReset.removeActionListener(a);
		}
	}
	
	/**
	 * Resets a timer by ensuring it's stopped and removes any actions.
	 * @param timer - Timer to reset
	 */
	public static void resetTimer(Timer timer) {
		if (timer != null) {
			timer.stop();
			for (ActionListener a : timer.getActionListeners()) {
				timer.removeActionListener(a);
			}
		}
	}
	
	/**
	 * Utility method that writes a string of text to a log file and
	 * provides a GUI save dialog.
	 * @param text - Textarea containing text to write.
	 */
	public static void writeLogFile(JTextArea textArea) {
		JFileChooser chooser = new JFileChooser();
		int choice = chooser.showSaveDialog(null);
		// they didn't cancel out dialog
		if (choice == JFileChooser.APPROVE_OPTION) {
			File f = chooser.getSelectedFile();
			String filePath = f.getAbsolutePath();
		
			// add.log extension if not exists!
			if (!filePath.endsWith(".log")) {
				f = new File(filePath + ".log");
			}
			
			BufferedWriter bw = null;
			
			try {
				bw = new BufferedWriter(new FileWriter(f));
				textArea.write(bw);
				//System.out.println("Log file written successfully!");
			} catch (IOException e) {
				//System.out.println("An error occurred when trying to write log file!");
				e.printStackTrace();
			} finally {
				try {
					bw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}	
		}
	}
	
	/**
	 * Utility method that clears out actions from a specified timer.
	 * @param t - Timer to clear
	 */
	public static void clearTimer(Timer t) {
		if (t != null) {
			t.stop();
			for (ActionListener a : t.getActionListeners()) {
				t.removeActionListener(a);
			}
		}
	}
	
	/**
	 * Plays a sound from the specified string file path.
	 * @param file - String to file
	 */
	public static void playSound(String file) {
		File f = new File(file);
		try {
			Clip clip = AudioSystem.getClip();
			clip.open(AudioSystem.getAudioInputStream(f));
			clip.start();
			clip.addLineListener(new LineListener() {
				public void update(LineEvent event) {
					if (event.getType() == LineEvent.Type.STOP) {	// close out when finished playing
						clip.close();
					}
				}
			});
		} catch (Exception e) {
			System.out.println("an error occurred when playing sound!");
		}	
	};
}