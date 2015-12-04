package panels.minis;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;

import org.json.simple.JSONObject;

import client.ClientApp;
import client.IOHandler;
import panels.BaseMiniPanel;
import util.GameUtils;
import util.Keys;
import util.NewJSONObject;

public class KeyFinder extends BaseMiniPanel {
	public KeyFinder(ClientApp app) {
		super(app);
	}
	
	public void init() {
		
	}
	
	public void update() {
		System.out.println("I am part of the update method!");
	}
	
	public void playerPressed() {
		// Send JSON here
		// must put name key with player name
		if (isActive) {
			System.out.println("Player pressed!");
			clientPlayer = app.getBoardPanel().getClientPlayer();
			NewJSONObject k = new NewJSONObject(clientPlayer.getID(), Keys.Commands.MINI_STOPPED);
			k.put(Keys.NAME, clientPlayer.getName());
			controller.send(k);
			isActive = false;
		}
	}
	
	/**
	 * Draws tiles and players to the screen.
	 * @param g - Graphics context to draw to
	 */
	public void paintComponent(Graphics g) {
		final Graphics2D g2d = (Graphics2D)g.create();
		try {
			g2d.setColor(GameUtils.colorFromHex("#C0C0C0"));
			g2d.fillRect(0, 0, getWidth(), getHeight());
			g2d.setColor(Color.BLACK);
			g2d.setFont(new Font("Courier New", Font.BOLD, 50));
			g2d.drawString("<KeyFinder-Minigame>", getWidth() / 2 - 20, getHeight() / 2 - 20);
			g2d.setColor(Color.CYAN);
			drawPlayers(g2d);
		} finally {
			g2d.dispose();
		}
	}
	
	public class Controller extends IOHandler {
		private KeyFinder mp;
		
		public Controller(KeyFinder mp) {
			this.mp = mp;
		}

		public void send(JSONObject out) {
			app.getClient().getIOHandler().send(out);
		}

		public void receive(JSONObject in) {
			
		}
	}	
}
