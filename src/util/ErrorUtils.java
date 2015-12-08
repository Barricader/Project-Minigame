package util;

import java.awt.Component;

import javax.swing.JOptionPane;

import org.json.simple.JSONObject;

import client.ClientApp;

/**
 * This class provides useful utilities for dealing with displaying dialog messages.
 * Server error objects can be processed in this class, and provides the easy
 * ability to show error messages to the user. There are many predefined error
 * messages that can easily be called.
 * @author David Kramer
 *
 */
public class ErrorUtils {
	
	@SuppressWarnings("unchecked")
	public static NewJSONObject createJSONError(int id, String title, String msg) {
		NewJSONObject obj = new NewJSONObject(id, Keys.Commands.ERROR);
		JSONObject error = new JSONObject();
		error.put(Keys.ERROR_TITLE, title);
		error.put(Keys.ERROR_MSG, msg);
		obj.put(Keys.Commands.ERROR, error);
		return obj;
	}
	
	/**
	 * Displays custom errror dialog with specified message.
	 * @param c - component to plce error dialog relative to
	 * @param msg - error message details
	 */
	public static void showCustomError(Component c, String msg) {
		JOptionPane.showMessageDialog(c, msg, "Error", JOptionPane.ERROR_MESSAGE);
	}
	
	/**
	 * Displays custom error dialog with specified message and title
	 * @param c - component to place error dialog relative to 
	 * @param msg - error message details
	 * @param title - error message title
	 */
	public static void showCustomError(Component c, String msg, String title) {
		JOptionPane.showMessageDialog(c, msg, title, JOptionPane.ERROR_MESSAGE);
	}
	
	/**
	 * Displays custom warning dialog with specified message and title
	 * @param c - component to place warning dialog relative to 
	 * @param msg - warning message details
	 * @param title - warning message title
	 */
	public static void showCustomWarning(Component c, String msg, String title) {
		JOptionPane.showMessageDialog(c, msg, title, JOptionPane.WARNING_MESSAGE);
	}
	
	/**
	 * Utility method for processing error objects received from the server.
	 * @param in - JSONObject containing an error
	 */
	public static void processServerError(ClientApp app, JSONObject in) {
		JSONObject error = (JSONObject) in.get(Keys.Commands.ERROR);
		int id = (int) in.get(Keys.ID);
		
		if (id != app.getLoginPanel().getClientPlayer().getID()) {
			String errorMsg = (String) error.get(Keys.ERROR_MSG);
			String errorTitle = (String) error.get(Keys.ERROR_TITLE);
			showCustomError(app, errorMsg, errorTitle);
		}
	}
	
	//********************************************************
	//* 			PRE-DEFINED ERROR MESSAGES				 *
	//********************************************************
	
	/**
	 * Shows a predefined time-out error.
	 * @param c - component to place error dialog relative to
	 */
	public static void showTimeOutError(Component c) {
		JOptionPane.showMessageDialog(c, "Client has timed out. Please try reconnecting to server!",
				"Timeout", JOptionPane.ERROR_MESSAGE);
	}
	
	/**
	 * Shows a predefined invalid port error.
	 * @param c - component to place error dialog relative to
	 */
	public static void showInvalidPortError(Component c) {
		JOptionPane.showMessageDialog(c, "Port must be a number between 1024 - 65536!", 
				"Invalid Port", JOptionPane.ERROR_MESSAGE);
	}
	
	/**
	 * Displays an error message that we were unable to connect to server.
	 * @param c - component to place warning dialog relative to 
	 */
	public static void showConnectionError(Component c) {
		JOptionPane.showMessageDialog(c, "Unable to connect... Server may not have been started!"
				, "Connection Error", JOptionPane.ERROR_MESSAGE);
	}
	
	//********************************************************
	//* 			PRE-DEFINED WARNING MESSAGES			 *
	//********************************************************
	
	/**
	 * Shows a predefined disconnection warning message.
	 * @param c - component to to place warning dialog relative to.
	 * @return true if ok btn was pressed, false otherwise
	 */
	public static boolean showDisconnectWarning(Component c) {
		int choice = JOptionPane.showConfirmDialog(c, "Are you sure you want to leave?",
				"Confirm", JOptionPane.OK_CANCEL_OPTION);
		
		return choice == 0;	// they hit ok
	}
	
	/**
	 * Shows a predefined message letting the user know they have been
	 * disconnected from the server.
	 * @param c - component to to place warning dialog relative to.
	 */
	public static void showDisconnectMessage(Component c) {
		JOptionPane.showMessageDialog(c, "You have been disconnected from the server!");
	}
	
}
