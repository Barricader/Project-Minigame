package util;

import org.json.simple.JSONObject;

import newserver.Keys;

public class NewJSONObject extends JSONObject {
	private static final long serialVersionUID = -3684644029817668627L;

	public NewJSONObject(int id, String cmd) {
		put(Keys.ID, id);
		put(Keys.CMD, cmd);
		put(Keys.LOG, false);
		
		checkDefaults();
	}

	public NewJSONObject(int id, String cmd, boolean log) {
		put(Keys.ID, id);
		put(Keys.CMD, cmd);
		put(Keys.LOG, log);
		
		checkDefaults();
	}
	
	public NewJSONObject(int id, String cmd, String msg) {
		put(Keys.ID, id);
		put(Keys.CMD, cmd);
		put(Keys.LOG, false);
		put(Keys.TEXT, msg);
		
		checkDefaults();
	}
	
	// NOT USED FOR NOW
	private void checkDefaults() {
	}
}
