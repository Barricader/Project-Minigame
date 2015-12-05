package newserver;

import java.util.HashMap;
import java.util.Map;

import util.Keys;

public class ServerActions {
	private Map<String, Action> actionMap;
	
	public ServerActions() {
		initMap();
		actionMap.get(Keys.Commands.ADD_PLAYER).execute();
	}
	
	private void initMap(){
		actionMap = new HashMap<>();
		actionMap.put(Keys.Commands.ADD_PLAYER, () -> addPlayer());
		actionMap.put(Keys.Commands.REM_PLAYER, () -> removePlayer());
		actionMap.put(Keys.Commands.UPDATE, () -> movePlayer());
	}
	
	public void addPlayer() {
		
	}
	
	public void removePlayer() {
		
	}
	
	public void movePlayer() {
		
	}
}
