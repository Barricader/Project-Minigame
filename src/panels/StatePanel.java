package panels;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JPanel;

import org.json.simple.JSONObject;

import client.ClientApp;
import client.IOHandler;
import newserver.Keys;
import newserver.ServerDirector;

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
			JSONObject state = (JSONObject) in.get(Keys.CMD);
			int stateType = (int) state.get(Keys.STATE);
			
			switch (stateType) {
			case (ServerDirector.BOARD):	// board state
				updateBoard();
				break;
			case (ServerDirector.MINI):		// mini state
				updateMiniState();
				break;
				
			}
		}
		
		/**
		 * Updates to board view.
		 */
		public void updateBoard() {
			System.out.println("view should be at board panel");
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
		public void updateMiniState() {
			updateView(app.getMiniPanel());
			app.repaint();
		}
		
	}
}
