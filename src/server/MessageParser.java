package server;

import java.io.IOException;
import java.util.ArrayList;

/* Possible return codes:
 * 0 - All is fine
 * -1 - Incorrect command
 * -2 - Incorrect amount of parameters
 * -3 - Incorrect parameter
 */

/**
 * Checks messages sent to server and checks if they are commands or not.
 * If they are commands, it then executes some code.
 * @author JoJones
 *
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
	
	public int parse(String str, int ID) throws IOException, InterruptedException  {
		if (str.startsWith("!")) {
			String temp = str.replace(" ", "");
			String[] words = str.split(" ");
			command = words[0];
			
			for (String s : words) {
				parameters.add(s);
			}
			parameters.remove(0);		// Remove the command string
			
			if (command.equals("!quit")) {
				if (parameters.size() == 0) {
					if (server.existsID(ID)) {
						System.out.println("Parsed command: should be removing client!");
						server.remove(ID);
						reset();
						return 0;
					} else {
						reset();
						return -3;
					}
				} else {
					reset();
					return -2;
				}
			} else {
				reset();
				return -1;
			}
		} else {
			reset();
			return 0;
		}
		
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
					reset();
					return -3;
				}
			}
			else {
				reset();
				return -2;
			}
		}
		else {
			reset();
			return -1;
		}
		
		reset();
		return 0;
	}
	
	private void reset() {
		command = "";
		parameters.clear();
		paramCount = 0;
	}
}
