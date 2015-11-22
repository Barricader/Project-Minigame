package util;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Class that parses JSONObjects
 * 
 * There is more to the JSONObject stuff that I haven't looked into yet
 * so there is still some things to add
 * @author Jo Jones
 */
public class JSONParser {
	private ArrayList<JSONObject> objects;
	
	public JSONParser() {
		objects = new ArrayList<JSONObject>();
	}
	
	/**
	 * Creates a JSON object based on a source
	 * @param source - file or stream to create a JSON object from
	 * @return Returns index where the object is stored to use for later
	 */
	public int addObject(String source) {
		// Maybe change this to a file stream ouput to read from server
		objects.add(new JSONObject(source));
		return objects.size() - 1;
	}
	
	/**
	 * Gets a string from a key in a JSONObject
	 * @param index - JSONObject in the objects array
	 * @param key - Key to get the string from
	 * @return Returns a string from a key
	 */
	public String getString(int index, String key) {
		return objects.get(index).getString(key);
	}
	
	/**
	 * Gets an array of strings from a key in a JSONArray
	 * in a JSONObject
	 * @param index - JSONObject in the objects array
	 * @param arrayKey - Array to get the strings from
	 * @param key - Key to get the string from
	 * @return
	 */
	public String[] getArrayString(int index, String arrayKey, String key) {
		JSONArray arr = objects.get(index).getJSONArray(arrayKey);
		String[] strings = new String[arr.length()];
		for (int i = 0; i < arr.length(); i++) {
			strings[i] = arr.getJSONObject(i).getString("key");
		}
		return strings;
	}
}
