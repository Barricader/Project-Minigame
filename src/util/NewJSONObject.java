package util;

import org.json.simple.JSONObject;

public class NewJSONObject extends JSONObject {
	private static final long serialVersionUID = -3684644029817668627L;

	public NewJSONObject(int id, String cmd) {
		put("id", id);
		put("cmd", cmd);
		put("log", false);
		
		checkDefaults();
	}

	public NewJSONObject(int id, String cmd, boolean log) {
		put("id", id);
		put("cmd", cmd);
		put("log", log);
		
		checkDefaults();
	}
	
	public NewJSONObject(int id, String cmd, String msg) {
		put("id", id);
		put("cmd", cmd);
		put("log", false);
		put("text", msg);
		
		checkDefaults();
	}
	
	// NOT USED FOR NOW
	private void checkDefaults() {
	}
}
