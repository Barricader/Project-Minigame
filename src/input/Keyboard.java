package input;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * Basic keyboard class that listens to and keeps track of keyboard input.
 * @author David Kramer
 * @author Joseph Jones
 */
public class Keyboard implements KeyListener {
	// Modifier key flags
	private boolean shiftFlag;
	private boolean altFlag;
	private boolean ctrlFlag;
	
	public boolean[] keys; // encompasses most used keys
	
	private int lastKey;	// key code of last key press
	public boolean spacePressed = false;
	
	public Keyboard() {
		shiftFlag = false;
		altFlag = false;
		ctrlFlag = false;
		lastKey = 0;

		keys = new boolean[120];
		//System.out.println("UUUUUUUUUUUUUUUH");
	}

	/**
	 * Updates last key and also any modifier key flags.
	 */
	public void keyPressed(KeyEvent e) {
		lastKey = e.getKeyCode();
		//System.out.println(e.getKeyCode());

		switch (e.getKeyCode()) {
		case KeyEvent.VK_ALT:
			altFlag = true;
			break;
		case KeyEvent.VK_SHIFT:
			shiftFlag = true;
			break;
		case KeyEvent.CTRL_DOWN_MASK:
			ctrlFlag = true;
		}
		keys[e.getKeyCode()] = true;
		
		if (!spacePressed && e.getKeyCode() == KeyEvent.VK_SPACE) {
			spacePressed = true;
		}
	}

	/**
	 * Clears out all keys, as none as currently pressed.
	 */
	public void keyReleased(KeyEvent e) {
		switch (e.getKeyCode()) {
		case KeyEvent.VK_ALT:
			altFlag = false;
			break;
		case KeyEvent.VK_SHIFT:
			shiftFlag = false;
			break;
		case KeyEvent.CTRL_DOWN_MASK:
			ctrlFlag = false;
		}
		
		if (spacePressed && e.getKeyCode() == KeyEvent.VK_SPACE) {
			spacePressed = false;
		}
		
		keys[e.getKeyCode()] = false;
	}
	
	// Not used
	public void keyTyped(KeyEvent e) {
	}
	
	// Accessor methods
	
	/**
	 * 
	 * @return Gives the array of keys
	 */
	public boolean[] getKeys() {
		return keys;
	}
	
	/**
	 * 
	 * @return KeyEvent key code of last key press
	 */
	public int getLastKey() {
		return lastKey;
	}
	
	/**
	 * 
	 * @return true if shift modifier key is pressed
	 */
	public boolean isShift() {
		return shiftFlag;
	}
	
	/**
	 * 
	 * @return true if alt modifier key is pressed
	 */
	public boolean isAlt() {
		return altFlag;
	}
	
	/**
	 * 
	 * @return true if ctrl key is pressed
	 */
	public boolean isCtrl() {
		return ctrlFlag;
	}
}
