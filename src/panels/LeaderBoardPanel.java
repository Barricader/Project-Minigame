package panels;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.Iterator;
import java.util.Map;

import javax.swing.DefaultListModel;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import client.ClientApp;
import client.IOHandler;
import gameobjects.NewPlayer;
import util.GameUtils;
import util.Keys;

/**
 * This class will display all the current player's scores.
 * @author David Kramer
 *
 */
public class LeaderBoardPanel extends JPanel {
	private ClientApp app;
	private NewPlayer[] players;	// holds players, in sorted order by their score
	private DefaultListModel<String> nameListModel;
	private DefaultListModel<Integer> scoreListModel;
	private JList<String> nameList;
	private JList<Integer> scoreList;
	// header labels
	private JLabel titleLabel;
	private JLabel nameLabel;
	private JLabel scoreLabel;
	
	/**
	 * Constructs new LeaderBoard panel.
	 * @param app - Root client app
	 */
	public LeaderBoardPanel(ClientApp app) {
		this.app = app;
		init();
		setPreferredSize(new Dimension(100, 100));
	}
	
	/**
	 * Initializes and lays out components using GridBagLayout.
	 */
	private void init() {
		createComponents();
		
		setBorder(new LineBorder(Color.LIGHT_GRAY));	
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		
		// title label
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridwidth = 2;
		c.weightx = 1.0;
		c.gridy = 0;
		c.ipady = 20;
		add(titleLabel, c);
		
		// name label
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.WEST;
		c.gridx = 0;
		c.gridwidth = 0;
		c.weightx = 0.9;
		c.gridy = 1;
		c.ipady = 5;
		add(nameLabel, c);
		
		// score label
		c.anchor = GridBagConstraints.EAST;
		c.gridx = 1;
		c.weightx = 0.1;
		c.gridy = 1;
		add(scoreLabel, c);
		
		// name list
		c.anchor = GridBagConstraints.SOUTH;
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		c.gridwidth = 1;
		c.weightx = 0.8;
		c.gridy = 3;
		c.ipady = 0;
		c.weighty = 1.0;
		add(nameList, c);
		
		// score list
		c.anchor = GridBagConstraints.SOUTHEAST;
		c.gridx = 1;
		c.weightx = 0.1;
		add(scoreList, c);
	}
	
	/**
	 * Creates GUI components for this leaderboard.
	 */
	private void createComponents() {
		// leaderboard title label
		titleLabel = new JLabel("Leaderboard");
		titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
		titleLabel.setFont(new Font("Courier New", Font.BOLD, 16));
		titleLabel.setOpaque(true);
		titleLabel.setBackground(Color.BLACK);
		titleLabel.setForeground(Color.CYAN);
		titleLabel.setBorder(new LineBorder(Color.CYAN, 2));
		
		// name label
		nameLabel = new JLabel(" Name");
		nameLabel.setFont(new Font("Courier New", Font.BOLD, 14));
		nameLabel.setBorder(new MatteBorder(1, 0, 0, 0, Color.LIGHT_GRAY));
		
		// score label
		scoreLabel = new JLabel("Score ");
		scoreLabel.setFont(new Font("Courier New", Font.BOLD, 14));
		scoreLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		scoreLabel.setBorder(new MatteBorder(1, 1, 0, 0, Color.LIGHT_GRAY));
		
		// name list
		nameListModel = new DefaultListModel<>();
		nameList = new JList<>(nameListModel);
		nameList.setSelectionModel(new DisabledItemSelectionModel());
		nameList.setPreferredSize(new Dimension(100, 100));
		nameList.setBackground(GameUtils.colorFromHex("#D6D9DF"));
		nameList.setFont(new Font("Courier New", Font.BOLD, 11));
		nameList.setForeground(Color.DARK_GRAY);
		nameList.setBorder(new MatteBorder(1, 0, 0, 0, Color.LIGHT_GRAY));
		
		// score list
		scoreListModel = new DefaultListModel<>();
		scoreList = new JList<>(scoreListModel);
		scoreList.setSelectionModel(new DisabledItemSelectionModel());
		scoreList.setPreferredSize(new Dimension(100, 100));
		scoreList.setBackground(GameUtils.colorFromHex("#D6D9DF"));
		scoreList.setFont(new Font("Courier New", Font.BOLD, 11));
		scoreList.setForeground(Color.DARK_GRAY);
		scoreList.setBorder(new MatteBorder(1, 1, 0, 0, Color.LIGHT_GRAY));
	}
	
	/**
	 * This method updates the leader board list with new values, whenever
	 * a leader board object is received from the server.
	 */
	public void updateList() {
		// make sure old values are cleared out!
		players = new NewPlayer[app.getBoardPanel().getPlayers().size()];
		nameListModel.clear();
		scoreListModel.clear();
		// get all player values and add to this player array
		Iterator<NewPlayer> iterator = app.getBoardPanel().getPlayers().values().iterator();
		int i = 0;
		while (iterator.hasNext()) {
			players[i] = iterator.next();
			i++;
		}
		GameUtils.sortPlayersByScore(players);
		
		for (NewPlayer p : players) {
			nameListModel.addElement(p.getName());
			scoreListModel.addElement(p.getScore());
			repaint();
		}
		app.repaint();
	}
	
	/**
	 * Updates the list using a JSONObject that contains leaderboard scores.
	 * @param in - JSONObject player win scores
	 */
	public void updateList(JSONObject in) {
		JSONArray leaderboard = (JSONArray) in.get("leaderboard");
		Map<String, NewPlayer> players = app.getBoardPanel().getPlayers();
		
		for (int i = 0; i < leaderboard.size(); i++) {
			JSONObject obj = (JSONObject) leaderboard.get(i);
			String name = (String) obj.get(Keys.NAME);
			players.get(name).setScore(players.get(name).getScore() + players.size() - i);
		}
		updateList();
	}
	
	/**
	 * This class just makes it so that you can't click and highlight a 
	 * value in the leader board list.
	 * @author David Kramer
	 *
	 */
	class DisabledItemSelectionModel extends DefaultListSelectionModel {
		public void setSelectionInterval(int index0, int index1) {
			super.setSelectionInterval(-1, -1);
		}
	}
}
