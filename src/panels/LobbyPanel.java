package panels;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.util.concurrent.ConcurrentHashMap;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import org.json.simple.JSONObject;

import client.ClientApp;
import client.IOHandler;
import gameobjects.NewPlayer;
import screen.PlayerListCellRenderer;
import util.GameUtils;

public class LobbyPanel extends JList<NewPlayer> {
	private ClientApp app;
	private Controller controller;
	private DefaultListModel<NewPlayer> listModel;
	private PlayerListCellRenderer listRenderer;
	private ConcurrentHashMap<String, NewPlayer> playerMap;
//	private JList<NewPlayer> playerList;
	
	public LobbyPanel(ClientApp app) {
		this.app = app;
		controller = new Controller();
		init();
	}
	
	private void init() {
		createComponents();
		setBackground(Color.BLACK);
//		setBorder(new TitledBorder(new LineBorder(Color.CYAN), " Waiting Players: ", TitledBorder.LEFT, 
//				TitledBorder.CENTER, new Font("Courier New", Font.BOLD, 16), Color.CYAN));
		setPreferredSize(new Dimension(200, 55));
	}
	
	/**
	 * Create gui components for lobby.
	 */
	private void createComponents() {
		listRenderer = new PlayerListCellRenderer();
		listModel = new DefaultListModel<>();
		setCellRenderer(listRenderer);
		setBorder(new LineBorder(Color.LIGHT_GRAY));
		setModel(listModel);
		repaint();
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
	
//	public JList<NewPlayer> getPlayerList() {
//		return playerList;
//	}
	
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
