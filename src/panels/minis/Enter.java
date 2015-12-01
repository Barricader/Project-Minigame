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

public class Enter extends BaseMiniPanel {
	public Enter(ClientApp app) {
		super(app);
	}
	
	protected void init() {
		
	}
	
	public void update() {
		
	}
	
	public void playerPressed() {
		// Send JSON here
		// must put name key with player name
		if (isActive) {
			System.out.println("Player pressed!");
			clientPlayer = app.getBoardPanel().getClientPlayer();
			
			NewJSONObject k1 = new NewJSONObject(clientPlayer.getID(), Keys.Commands.MINI_UPDATE);
			k1.put(Keys.NAME, "enter");
			k1.put(Keys.PLAYER_NAME, clientPlayer.getName());
			controller.send(k1);
			
			NewJSONObject k2 = new NewJSONObject(clientPlayer.getID(), Keys.Commands.MINI_STOPPED);
			k2.put(Keys.NAME, clientPlayer.getName());
			controller.send(k2);
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
			g2d.drawString("<Enter-Minigame>", getWidth() / 2 - 20, getHeight() / 2 - 20);
			g2d.setColor(Color.CYAN);
			drawPlayers(g2d);
		} finally {
			g2d.dispose();
		}
	}
	
	public class Controller extends IOHandler {
		private Enter mp;
		
		public Controller(Enter mp) {
			this.mp = mp;
		}

		public void send(JSONObject out) {
			app.getClient().getIOHandler().send(out);
		}

		public void receive(JSONObject in) {
			
		}
	}	
}
