package client;

import java.io.IOException;
import java.util.HashMap;

import org.json.simple.JSONObject;

import util.Keys;


/**
 * This class provides implementation for handling JSON objects both sending
 * and receiving from the connected Client. This class is responsible for 
 * sending and receiving JSON objects.
 * @author David Kramer
 *
 */
public class ClientIOHandler extends IOHandler {
	private ClientApp app;
	private HashMap<String, IOHandler> handlerMap;
	
	/**
	 * Constructs a new ClientIOHandler with a link to the main ClientApp.
	 * @param app - Target ClientApp
	 */
	public ClientIOHandler(ClientApp app) {
		super();
		this.app = app;
		initHandlerMap();
	}
	
	/**
	 * Maps keys to handlers, so that we can easily route incoming JSON
	 * objects to their appropriate IOHandler controllers. These keys should
	 * be the main "header" of a JSON object, where supplementary keys are
	 * nested inside and can be further processed by the specified controller.
	 */
	private void initHandlerMap() {
		handlerMap = new HashMap<>();
		handlerMap.put(Keys.Commands.CONNECT,app.getConnPanel().getController());
		handlerMap.put(Keys.Commands.MSG, app.getChatPanel().getController());
		handlerMap.put(Keys.Commands.STATE_UPDATE, app.getStatePanel().getController());
		handlerMap.put(Keys.Commands.UPDATE, app.getBoardPanel().getController());
		handlerMap.put(Keys.Commands.MOVE, app.getBoardPanel().getController());
		handlerMap.put(Keys.Commands.ADD_PLAYER, app.getLoginPanel().getController());
		handlerMap.put(Keys.Commands.REM_PLAYER, app.getLoginPanel().getController());
		handlerMap.put(Keys.Commands.TIMER, app.getLoginPanel().getController());
		handlerMap.put(Keys.Commands.ACTIVE, app.getBoardPanel().getController());
		handlerMap.put(Keys.Commands.ROLL, app.getDicePanel().getController());
		handlerMap.put(Keys.Commands.ERROR, app.getErrorHandler());
	}

	/**
	 * Sends a JSONObject using the clients ObjectOutputStream.
	 */
	public void send(JSONObject out) {
		try {
			app.getClient().getOutputStream().writeObject(out);
			app.getClient().getOutputStream().flush();
			app.getClient().getOutputStream().reset();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Receives an incoming JSON object and routes the execution to the
	 * handler map, depending on the key(s) received.
	 */
	public void receive(JSONObject in) {
		System.out.println("Client Handler received: " + in);
		String cmdKey = (String)in.get(Keys.CMD);
		
		// have to handle mini updates here, otherwise it points to wrong thing.
		if (cmdKey.equals(Keys.Commands.MINI_UPDATE)) {
			handlerMap.put(Keys.Commands.MINI_UPDATE, app.getMinis().get(app.getMini()).getController());
		}
		
		if (handlerMap.containsKey(cmdKey)) {
			handlerMap.get(cmdKey).receive(in);
		}
		
	}
}
