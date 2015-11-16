package server;

import java.util.ArrayList;

/* Possible return codes:
 * 0 - All is fine
 * -1 - incorrect command
 * -2 - incorrect amount of parameters
 */

public class MessageParser {
	private String command;
	private ArrayList<String> parameters;
	private int paramCount;
	
	public MessageParser() {
		command = "";
		parameters = new ArrayList<String>();
		paramCount = 0;
	}
	
	public int parse(String str) {
		String temp = str.replace(" ", "");
		paramCount = str.length() - temp.length();
		String[] words = str.split(" ");
		command = words[0];
		
		for (String s : words) {
			parameters.add(s);
		}
		parameters.remove(0);		// Remove the command string
		
		if (command.equals("!disc")) {
			if (parameters.size() == 1) {
				int clientID = Integer.parseInt(parameters.get(0));
				// do remove stuff here
			}
			else {
				return -2;
			}
		}
		else {
			return -1;
		}
		
		return 0;
	}
	
	private void reset() {
		command = "";
		parameters.clear();
		paramCount = 0;
	}
}
