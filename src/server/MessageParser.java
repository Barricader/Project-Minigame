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
	private Server server;
	
	public MessageParser(Server server) {
		command = "";
		parameters = new ArrayList<String>();
		this.server = server;
	}
	
	public int parse(String str, int ID) throws IOException, InterruptedException {
		if (str.startsWith("!")) {	// potential command by using delim
			reset();	// clear out before we process
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
						return INVALID_PARAM_COUNT;
					}
					server.assignName(ID, name);
					return VALID_CMD;
				}
			}
		}
		return INVALID_CMD;
	}
	
	private void reset() {
		command = "";
		parameters.clear();
	}
}
