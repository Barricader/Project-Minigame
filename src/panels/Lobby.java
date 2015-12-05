package panels;

import java.awt.Color;
import java.awt.Dimension;
import java.util.concurrent.ConcurrentHashMap;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.border.LineBorder;

import client.ClientApp;
import gameobjects.NewPlayer;
import screen.PlayerListCellRenderer;
import util.DisabledItemSelectionModel;

/**
 * This class provides a list view of all connected players that
 * are waiting to play.
 * @author David Kramer
 *
 */
public class Lobby extends JList<NewPlayer> {
	private ClientApp app;
	private DefaultListModel<NewPlayer> listModel;
	private PlayerListCellRenderer listRenderer;
	private ConcurrentHashMap<String, NewPlayer> playerMap;
	
	/**
	 * Constructs a new lobby with a connection to main client app
	 * @param app - Target client app
	 */
	public Lobby(ClientApp app) {
		this.app = app;
		init();
	}
	
	/**
	 * Initializes all components for the lobby.
	 */
	private void init() {
		createComponents();
		setBackground(Color.BLACK);
		setPreferredSize(new Dimension(200, 55));
	}
	
	/**
	 * Create GUI components for lobby.
	 */
	private void createComponents() {
		listRenderer = new PlayerListCellRenderer();
		listModel = new DefaultListModel<>();
		setCellRenderer(listRenderer);
		setSelectionModel(new DisabledItemSelectionModel());
		setBorder(new LineBorder(Color.LIGHT_GRAY));
		setModel(listModel);
		repaint();
	}
	
	/**
	 * Updates the list with player names that are stored in the
	 * board panel.
	 */
	public void updateList() {
		playerMap = app.getBoardPanel().getPlayers();
		listModel.removeAllElements();
		for (NewPlayer p : playerMap.values()) {
			listModel.addElement(p);
			System.out.println("element: " + p + ", was added to list");
			repaint();
		}
		repaint();
		app.repaint();
	}
	
}
