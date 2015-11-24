package gameobjects;

import java.awt.Rectangle;

import org.json.simple.JSONObject;

/**
 * Base class for all GameObjects.
 * @author David Kramer
 *
 */
public abstract class GameObject extends Rectangle {
	protected JSONObject jsonObj;
	
	public GameObject() {
		
	}
	
	/**
	 * Method that must be implemented to write properties of a GameObject
	 * to a JSON object so that it can be sent between client and server.
	 * @return
	 */
	public abstract JSONObject toJSONObject();
}
