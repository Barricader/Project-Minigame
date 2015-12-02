package panels;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;

import org.json.simple.JSONObject;

import client.ClientApp;
import client.IOHandler;
import util.GameUtils;

/**
 * This class will display all the current player's scores.
 * @author David Kramer
 *
 */
public class LeaderBoardPanel extends JPanel {
	private ClientApp app;
	private Controller controller;
	private DefaultListModel<String> nameListModel;
	private DefaultListModel<Integer> scoreListModel;
	private JList<String> nameList;
	private JList<Integer> scoreList;
	// title labels
	private JLabel titleLabel;
	private JLabel nameLabel;
	private JLabel scoreLabel;
	
	public LeaderBoardPanel(ClientApp app) {
		this.app = app;
		controller = new Controller();
		init();
		setPreferredSize(new Dimension(100, 100));
	}
	
	protected void init() {
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
		add(titleLabel, c);
		
		// name label
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.WEST;
		c.gridx = 0;
		c.gridwidth = 0;
		c.weightx = 0.9;
		c.gridy = 1;
		add(nameLabel, c);
		
		// score label
		c.anchor = GridBagConstraints.EAST;
		c.gridx = 1;
		c.weightx = 0.1;
		add(scoreLabel, c);
		
		// name list
		c.anchor = GridBagConstraints.SOUTH;
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		c.gridwidth = 1;
		c.weightx = 0.8;
		c.gridy = 3;
		c.weighty = 1.0;
		add(nameList, c);
		
		// score list
		c.anchor = GridBagConstraints.SOUTHEAST;
		c.gridx = 1;
		c.weightx = 0.1;
		add(scoreList, c);
	}
	
	protected void createComponents() {
		Font f = new Font("Courier New", Font.BOLD, 14);
		titleLabel = new JLabel("Leaderboard");
		titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
		titleLabel.setFont(f);
		
		nameLabel = new JLabel("Name:");
		nameLabel.setFont(f);
		
		scoreLabel = new JLabel("Score:");
		scoreLabel.setFont(f);
		scoreLabel.setHorizontalAlignment(SwingConstants.LEFT);
		
		nameListModel = new DefaultListModel<>();
		nameList = new JList<>(nameListModel);
		nameList.setPreferredSize(new Dimension(100, 100));
		nameList.setBackground(GameUtils.colorFromHex("#999999"));
		
		scoreListModel = new DefaultListModel<>();
		scoreList = new JList<>(scoreListModel);
		scoreList.setPreferredSize(new Dimension(100, 100));
		scoreList.setBackground(GameUtils.colorFromHex("#999999"));
		
		// dummy players! TODO remove this crap later
		nameListModel.addElement("Dummy 1");
		scoreListModel.addElement(20);
		
		nameListModel.addElement("Dummy 2");
		scoreListModel.addElement(8);
		
		scoreList.repaint();
		nameList.repaint();
	}
	
	public class Controller extends IOHandler {

		@Override
		public void send(JSONObject out) {
		}

		@Override
		public void receive(JSONObject in) {

		}
		
	}
}
