package server;

import java.io.IOException;
import java.util.ArrayList;

/* Possible return codes:
 * 0 - All is fine
 * -1 - Incorrect command
 * -2 - Incorrect amount of parameters
 * -3 - Incorrect parameter
 */

public class MessageParser {
	private String command;
	private ArrayList<String> parameters;
	private int paramCount;
	private Server server;
	
	public MessageParser(Server server) {
		command = "";
		parameters = new ArrayList<String>();
		paramCount = 0;
		this.server = server;
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
				if (server.existsID(clientID)) {
					try {
						server.remove(clientID);
					} catch (IOException e) {
						e.printStackTrace();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				else {
					return -3;
				}
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
