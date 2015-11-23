package client;

import java.util.ArrayList;

import org.json.simple.JSONObject;

/**
 * Abstract class that enforces basic send/receive handling using JSON. Users
 * of this class will need to define their own behavior when it comes to
 * sending and receiving JSON objects.
 * @author David Kramer
 *
 */
public abstract class IOHandler {
	protected ArrayList<JSONObject> objects;
	
	public IOHandler() {
		objects = new ArrayList<>();
	}
	
	public abstract void send(String out);

	public abstract void receive(String in);
}
