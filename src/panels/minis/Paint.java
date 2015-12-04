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

public class Paint extends BaseMiniPanel {
	
	public Paint(ClientApp app) {
		super(app);
	}
	
	public void init() {
		
	}
	
	public void update() {
		System.out.println("I has paint minigame");
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
			g2d.setColor(GameUtils.getRandomColor());
			g2d.fillOval(40, 40, 20, 60);
			g2d.setFont(new Font("Courier New", Font.BOLD, 50));
			g2d.drawString("PAINT, YAY!!!!", getWidth() / 3, getHeight() / 3);
			g2d.setColor(Color.CYAN);
			
			// draw circles
			for (int i = 0; i < 20; i++) {
				g2d.drawOval(i * 20, i * 20, 20 + i, 20 + (i * 2));
			}
			drawPlayers(g2d);
		} finally {
			g2d.dispose();
		}
	}
	
	public class Controller extends IOHandler {

		public void send(JSONObject out) {
			app.getClient().getIOHandler().send(out);
		}

		public void receive(JSONObject in) {
			
		}
	}	
}
