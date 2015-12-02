package panels;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JPanel;

import org.json.simple.JSONObject;

import client.ClientApp;
import client.IOHandler;
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
	}
	
	/**
	 * Updates the active state view (either between board or mini-game).
	 * @param newView - View to change.
	 */
	public void updateView(JPanel newView) {
		removeAll();
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.anchor = GridBagConstraints.CENTER;
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		c.weightx = 1.0;
		c.gridy = 0;
		c.weighty = 1.0;
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
			app.getDicePanel().setVisible(true);
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
			app.getMinis().get(curMini).setActive(true);
			app.setMini(curMini);
			app.updateKey(curMini);
			updateView(app.getMinis().get(curMini));
			app.repaint();
		}
		
	}
}
