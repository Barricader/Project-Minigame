package panels;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;

import org.json.simple.JSONObject;

import client.IOHandler;
import gameobjects.NewPlayer;
import screen.PlayerListCellRenderer;

public class LobbyPanel extends JPanel {
	private Controller controller;
	private DefaultListModel<NewPlayer> listModel;
	private PlayerListCellRenderer listRenderer;
	private JList<NewPlayer> playerList;
	
	public LobbyPanel() {
		controller = new Controller();
		init();
	}
	
	private void init() {
		createComponents();
		
//		TEST -> REMOVE ME LATER AND GET FROM SERVER
		NewPlayer test1 = new NewPlayer("TEST", 1);
		test1.style(4);
		addPlayerToList(test1);
		addPlayerToList(new NewPlayer("Player3", 3));
		
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
	
	private void createComponents() {
		listRenderer = new PlayerListCellRenderer();
		listModel = new DefaultListModel<>();
		playerList = new JList<>(listModel);
		playerList.setCellRenderer(listRenderer);
		setBorder(new LineBorder(Color.LIGHT_GRAY));
	}
	
	public void addPlayerToList(NewPlayer p) {
		listModel.addElement(p);
		playerList.repaint();
	}
	
	public Controller getController() {
		return controller;
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
