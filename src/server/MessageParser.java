package server;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Checks messages sent to server and checks if they are commands or not.
 * If they are commands, it then executes some code.
 * @author JoJones
 * @author David Kramer
 */
public class MessageParser {
	/*
	 * Possible return codes
	 */
	public static final int VALID_CMD = 0;
	public static final int INVALID_CMD = -1;
	public static final int INVALID_PARAM_COUNT = -2;
	public static final int INVALID_PARAM = -3;
	
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
	
	public int parse(String str, int ID) throws IOException, InterruptedException {
		if (str.startsWith("!")) {	// potential command by using delim
			reset();	// clear out before we process
			String temp = str.replace(" ", "");	// remove space
			String[] params = str.split(" ");
			command = params[0];
			
			for (String p : params) {
				parameters.add(p);
			}
			
			// check commands
			if (server.clientExistsByID(ID)) {
				if (command.equals("!quit")) {
					server.remove(ID);
					return VALID_CMD;
				} else if (command.equals("!name")) {
					String name = null;
					if (parameters.size() >= 2) {
						name = parameters.get(1);	
					} else {
						return INVALID_CMD;
					}
					ServerThread client = server.getClientMap().get(ID);
					client.setClientName(name);
					client.send("You will now be seen as: " + name);
					return VALID_CMD;
				}
			}
		}
		return INVALID_CMD;
	}
	
//	public int parse(String str, int ID) throws IOException, InterruptedException  {
//		if (str.startsWith("!")) {
//			String temp = str.replace(" ", "");
//			String[] words = str.split(" ");
//			command = words[0];
//			
//			for (String s : words) {
//				parameters.add(s);
//			}
//			parameters.remove(0);		// Remove the command string
//			
//			reset();	// reset before we process
//			
//			if (command.startsWith("!quit")) {
//				if (parameters.size() == 0) {
//					if (server.existsByID(ID)) {
//						System.out.println("Parsed command: should be removing client!");
//						server.remove(ID);
//						return VALID_CMD;
//					} else {
//						return INVALID_PARAM;
//					}
//				} else {
//					return INVALID_PARAM_COUNT;
//				}
//			} else {
//				return INVALID_CMD;
//			}
//		} else {
//			return VALID_CMD;
//		}
//		
//	}
	
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
