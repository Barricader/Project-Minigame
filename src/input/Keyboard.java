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
	
	private boolean[] keys = new boolean[120]; // encompasses most used keys
	
	private int lastKey;	// key code of last key press
	
	public Keyboard() {}

	/**
	 * Updates last key and also any modifier key flags.
	 */
	public void keyTyped(KeyEvent e) {
		lastKey = e.getKeyCode();
		
		switch (e.getModifiers()) {
		case KeyEvent.VK_ALT:
			altFlag = true;
			break;
		case KeyEvent.VK_SHIFT:
			shiftFlag = true;
			break;
		case KeyEvent.CTRL_DOWN_MASK:
			ctrlFlag = true;
		}
		
	}

	/**
	 * Updates last key and also any modifier key flags.
	 */
	public void keyPressed(KeyEvent e) {
		lastKey = e.getKeyCode();

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
		
		keys[e.getKeyCode()] = false;
	}
	
	// Accessor methods
	
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
