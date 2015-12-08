package util;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

// TODO: maybe just remove this and just import simple JSON in
// server thread and client to handle it there

/**
 * Class that parses JSONObjects
 * 
 * There is more to the JSONObject stuff that I haven't looked into yet
 * so there is still some things to add
 * @author Jo Jones
 * @deprecated
 */
public class JSONUtility {
	// For decoding
	private JSONParser parser = new JSONParser();
	private Object obj = new Object();
	private JSONArray array = new JSONArray();
	private boolean hasDecoded = false;
	
	// For encoding
	private JSONObject objectToSend = new JSONObject();
	
	public JSONUtility() {
		// test string:
		//String s = "[0,{\"1\":{\"2\":{\"3\":{\"4\":[5,{\"6\":7}]}}}}]";
	}
	
	/**
	 * Decodes a JSON message into a map
	 * @param msg - JSON message to convert
	 */
	public void decode(String msg) {
		try {
			obj = parser.parse(msg);
			array = (JSONArray) obj;
			hasDecoded = true;
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
	
	public Object getValue(String key) {
		return getValue(0, key);
	}
	
	/**
	 * Get value of a key in a JSONObject
	 * @param arrayIndex - Array index of JSON Object
	 * @param key - Key to get value from
	 * @return Returns value of key
	 */
	public Object getValue(int arrayIndex, String key) {
		if (!hasDecoded) {
			return "";
		}
		
		JSONObject temp = (JSONObject) array.get(arrayIndex); 
		return temp.get(key);
	}
	
	/**
	 * Encodes and adds a key-value pair into a JSONObject
	 * @param key - Key to add
	 * @param value - Value of key
	 */
	@SuppressWarnings("unchecked")
	public void encode(String key, Object value) {
		objectToSend.put(key, value);
	}
	
	/**
	 * Gets JSON string of the object and clears it
	 * @return - Returns the JSON
	 */
	public String getObject() {
		String s = objectToSend.toString();
		resetObject();
		return s;
	}
	
	/**
	 * Resets the JSONObject
	 */
	private void resetObject() {
		objectToSend.clear();
	}
	
	/**
	 * Reset decoding stuff
	 */
	public void resetDecode() {
		obj = new Object();
		array.clear();
		hasDecoded = false;
	}
}
