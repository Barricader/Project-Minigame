package panels;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.ArrayList;

import javax.swing.JPanel;
import javax.swing.border.LineBorder;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import client.ClientApp;
import client.IOHandler;
import gameobjects.NewPlayer;
import newserver.ServerDirector;
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
	
	private ArrayList<NewPlayer> temp;
	
	// initial login components
	private Controller controller;
	private LoginPanel loginPanel;
	
	/**
	 * Constructs a new StatePanel with the default view set to login.
	 * @param app
	 */
	public StatePanel(ClientApp app) {
		this.app = app;
		controller = new Controller();
		loginPanel = new LoginPanel(app);
		updateView(loginPanel);
		temp = new ArrayList<NewPlayer>();
		setBorder(new LineBorder(Color.GREEN));
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
	}
	
	public LoginPanel getLoginPanel() {
		return loginPanel;
	}
	
	public Controller getController() {
		return controller;
	}
	
	public class Controller extends IOHandler {

		@Override
		public void send(JSONObject out) {
			// TODO Auto-generated method stub
			
		}

		public void receive(JSONObject in) {
			int stateType = (int) in.get(Keys.STATE);
			switch (stateType) {
			case (ServerDirector.BOARD):	// board state
				if (in.containsKey("leaderboard")) {
					JSONArray test = (JSONArray) in.get("leaderboard");
					if (test.size() != 0) {
						for (int i = 0; i < app.getBoardPanel().getPlayers().size(); i++) {
							String name = (String) ((JSONObject) test.get(i)).get("name");
							System.out.println("Name " + i + ": " + name);
							System.out.println("Score " + i + ": " + app.getBoardPanel().getPlayers().get(name).getScore());
							app.getBoardPanel().getPlayers().get(name).setScore(app.getBoardPanel().getPlayers().get(name).getScore() + app.getBoardPanel().getPlayers().size() - i);
							app.getLeaderPanel().updateList();
						}
					}
				}
				updateBoard();
				break;
			case (ServerDirector.MINIGAME):		// mini state
				updateMiniState((String)in.get("mini"));
				app.setMini((String)in.get("mini"));
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
			
			// board rendering glitch fix
			Dimension size = app.getSize();
			app.setSize(size);
			app.repaint();
		}
		
		/**
		 * Updates to mini game view.
		 */
		public void updateMiniState(String curMini) {
			//app.getMiniPanel().setActive(true);
			app.getDicePanel().setEnabled(false);
			app.getMinis().get(curMini).setActive(true);
			app.setMini(curMini);
			app.updateKey(curMini);
			updateView(app.getMinis().get(curMini));
			app.repaint();
		}
		
	}
}
