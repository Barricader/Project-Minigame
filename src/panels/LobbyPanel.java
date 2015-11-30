package panels;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;

import org.json.simple.JSONObject;

import client.ClientApp;
import client.IOHandler;
import gameobjects.NewPlayer;
import screen.PlayerListCellRenderer;

public class LobbyPanel extends JPanel {
	private ClientApp app;
	private Controller controller;
	private DefaultListModel<NewPlayer> listModel;
	private PlayerListCellRenderer listRenderer;
	private ConcurrentHashMap<String, NewPlayer> playerMap;
	private JList<NewPlayer> playerList;
	
	public LobbyPanel(ClientApp app) {
		this.app = app;
		controller = new Controller();
		init();
	}
	
	private void init() {
		createComponents();
		setBackground(Color.BLACK);
		playerList.setBackground(Color.BLACK);
		
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.anchor = GridBagConstraints.NORTH;
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		c.weightx = 1.0;
		c.gridy = 0;
		c.weighty = 1.0;
		add(playerList, c);
	}
	
	/**
	 * Create gui components for lobby.
	 */
	private void createComponents() {
		listRenderer = new PlayerListCellRenderer();
		listModel = new DefaultListModel<>();
		playerList = new JList<>(listModel);
		playerList.setCellRenderer(listRenderer);
		setBorder(new LineBorder(Color.LIGHT_GRAY));
	}
	
	/**
	 * Adds player to list.
	 * @param p - Player to add to list
	 * @deprecated 
	 * Use updateList(), to retrieve values from board player map.
	 */
	public void addPlayerToList(NewPlayer p) {
		listModel.addElement(p);
		repaint();
	}
	
	public void updateList() {
		playerMap = app.getBoardPanel().getPlayers();
		listModel.removeAllElements();
		for (NewPlayer p : playerMap.values()) {
			listModel.addElement(p);
		}
		repaint();
	}
	
	public Controller getController() {
		return controller;
	}
	
	public JList<NewPlayer> getPlayerList() {
		return playerList;
	}
	
	public class Controller extends IOHandler {

		public void send(JSONObject out) {
			//TODO implement using JSON
		}

		@Override
		public void receive(JSONObject in) {
			//TODO implement using JSON
		}
		
	}
}
