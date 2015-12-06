package util;

import org.json.simple.JSONObject;

import client.ClientApp;
import client.IOHandler;

public abstract class BaseController extends IOHandler {
	protected ClientApp app;
	
	public BaseController(ClientApp app) {
		this.app = app;
	}

	public void send(JSONObject out) {
		app.getClient().getIOHandler().send(out);
	}
	
	public void receive(JSONObject in) {
	
	}
}
