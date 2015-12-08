package panels;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JPanel;
import javax.swing.border.LineBorder;

import org.json.simple.JSONObject;

import client.ClientApp;
import client.IOHandler;
import newserver.ServerDirector;
import util.GameUtils;
import util.Keys;

/**
 * This panel will be a container for any state that changes more frequently. This
 * will mainly be used to change from the login state, to board state, and any mini
 * game state.
 * @author David Kramer
 *
 */
public class StatePanel extends JPanel {
	private ClientApp app;
	
	// initial login components
	private Controller controller;
	private LoginPanel loginPanel;
	
	private boolean fixedBoardGlitch = false;	// yes, this is crude, it works!
	
	/**
	 * Constructs a new StatePanel with the default view set to login.
	 * @param app
	 */
	public StatePanel(ClientApp app) {
		this.app = app;
		controller = new Controller();
		loginPanel = new LoginPanel(app);
		updateView(loginPanel);
	}
	
	/**
	 * Updates the active state view (either between board or mini-game).
	 * @param newView - View to change.
	 */
	public void updateView(JPanel newView) {
		removeAll();
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.anchor = GridBagConstraints.NORTH;
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		c.weightx = 1.0;
		c.gridy = 0;
		c.weighty = 0.1;
		add(newView, c);
		app.revalidate();
		newView.setBackground(Color.BLACK);
	}
	
	/**
	 * Resets state panel back to a fresh login view.
	 */
	public void reset() {
		loginPanel = new LoginPanel(app);
		updateView(loginPanel);
	}
	
	// accessor methods
	
	public LoginPanel getLoginPanel() {
		return loginPanel;
	}
	
	public Controller getController() {
		return controller;
	}
	
	/**
	 * Controller for handling changing between various states of the game.
	 * @author David Kramer
	 *
	 */
	public class Controller extends IOHandler {

		public void send(JSONObject out) {	
			// currently unused
		}
		
		/**
		 * Handles receiving state change requests.
		 */
		public void receive(JSONObject in) {
			int stateType = (int) in.get(Keys.STATE);
			switch (stateType) {
			case ServerDirector.BOARD:
				if (in.containsKey("leaderboard")) {
					app.getLeaderPanel().updateList(in);
				}
				updateBoard();
				break;
			case ServerDirector.MINIGAME:
				String miniState = (String)in.get("mini");
				updateMiniState(miniState);
				break;
			}
		}
		
		/**
		 * Updates to board view.
		 */
		public void updateBoard() {
			System.out.println("view should be at board panel");
			if (!app.getMini().equals("null")) {
				app.getMinis().get(app.getMini()).exit();
			}
			app.getConnPanel().setVisible(true);
			app.getDicePanel().setEnabled(true);
			app.getDicePanel().setVisible(true);
			app.getLeaderPanel().setVisible(true);
			app.getLeaderPanel().updateList();
			updateView(app.getBoardPanel());
			
			// board rendering glitch fix for when players are cut off!
			if (!fixedBoardGlitch) {
				Dimension size = app.getSize();
				size.width += 2;
				size.height += 2;
				app.setSize(size);
				app.repaint();	
				fixedBoardGlitch = true;
			}
		}
		
		/**
		 * Updates to mini game view.
		 */
		public void updateMiniState(String curMini) {
			//app.getMiniPanel().setActive(true);
			app.getDicePanel().setEnabled(false);
			app.getDicePanel().setVisible(false);
			app.setMini(curMini);
			app.getMinis().get(curMini).setActive(true);
			app.updateKey(curMini);
			app.getMinis().get(curMini).init();
			updateView(app.getMinis().get(curMini));
			app.repaint();
		}
		
	}
}
