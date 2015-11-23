package client;

import java.io.IOException;

import gameobjects.Player;

/**
 * This class provides implementation for handling input/output on the ClientSide.
 * THIS IS AN EXAMPLE IMPLEMENTATION. CHANGE THE IMPLEMENTATION DETAILS TO HANDLE
 * SENDING AND RECEIVING STRINGS VIA A JSON STRING OBJECT!!
 * @author David Kramer
 *
 */
public class ClientIOHandler extends IOHandler {
	private ClientApp app;
	
	public ClientIOHandler(ClientApp app) {
		super();
		this.app = app;
	}

	public void send(String out) {
		if (app.getClient().getOutputStream() != null) {
			try {
				app.getClient().getOutputStream().writeUTF(out);
				app.getClient().getOutputStream().flush();
				app.getClient().getOutputStream().reset();
			} catch (IOException e) {
				e.printStackTrace();
			}	
		} else {
			System.out.println("output stream is null for some reason...");
		}
	}

	public void receive(String in) {
		// test
		System.out.println("Client received: " + in);
		if (in.startsWith("!addPlayer")) {
			addPlayer(in);
		} else if (in.startsWith("!connection")) {
			app.getConnPanel().getController().receive(in);
		}
		else {
			app.getChatPanel().printMessage(in);
		}
	}
	
	private void addPlayer(String in) {
		System.out.println("Player received!");
		String[] params = in.split(" ");
		String name = params[1];
		int colorID = Integer.parseInt(params[2]);
		Player p = new Player(name, colorID);
		app.getBoardPanel().addPlayer(p);
	}

}
