package client;

import java.io.IOException;
import java.util.HashMap;

import org.json.simple.JSONObject;

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
		handlerMap = new HashMap<String, IOHandler>();
		System.out.println("TEST" + app.getConnPanel().getController());
		handlerMap.put("connection", app.getConnPanel().getController());
		handlerMap.put("msg", app.getChatPanel().getController());
		handlerMap.put("update", app.getBoardPanel().getController());
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
		
		for (Object key : in.keySet()) {
			if (handlerMap.containsKey(key)) {
				System.out.println("Routing action to : " + key);
				handlerMap.get(key).receive(in);
			}
		}
		
		
	}
}
