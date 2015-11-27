package panels;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JPanel;
import javax.swing.border.LineBorder;

import org.json.simple.JSONObject;

import client.ClientApp;
import client.IOHandler;

/**
 * This panel will be a container for any state that changes more frequently. This
 * will mainly be used to change from the login state, to board state, and any mini
 * game state.
 * @author David Kramer
 *
 */
public class StatePanel extends JPanel {
	private ClientApp app;
	
//	private JPanel view;	// content we want to display
	private Controller controller;
	private LoginPanel loginPanel;
	
	public StatePanel(ClientApp app) {
		this.app = app;
		controller = new Controller();
		loginPanel = new LoginPanel(app);
		updateView(loginPanel);
		setBorder(new LineBorder(Color.RED));
	}
	
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
			// TODO change state stuff!
		}
		
	}
}
