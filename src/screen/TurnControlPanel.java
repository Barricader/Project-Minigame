package screen;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import util.GameUtils;

/**
 * This class provides controls for altering the amount of turns that the game 
 * will have. There are 3 possibilities: 10, 20 or a custom numeric value which
 * can be entered by the user.
 * @author David Kramer
 *
 */
public class TurnControlPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	private ButtonGroup buttonGroup;	// button group for radio buttons
	private JRadioButton turn10Btn;	// 10 turns
	private JRadioButton turn20Btn;	// 20 turns
	private JRadioButton customTurnBtn;	// for having a custom value
	private JTextField customTurnField;
	
	public TurnControlPanel() {
		init();
	}
	
	private void init() {
		buttonGroup = new ButtonGroup();
		turn10Btn = new JRadioButton("10");
		turn10Btn = (JRadioButton)GameUtils.customizeComp(turn10Btn, Color.BLACK, Color.CYAN, 40);
		turn10Btn.setSelected(true);	// enable 10 turns by default
		
		turn20Btn = new JRadioButton("20");
		turn20Btn = (JRadioButton)GameUtils.customizeComp(turn20Btn, Color.BLACK, Color.CYAN, 40);
		
		customTurnBtn = new JRadioButton("Custom");
		customTurnBtn = (JRadioButton)GameUtils.customizeComp(customTurnBtn, Color.BLACK, Color.CYAN, 14);
		customTurnBtn.addChangeListener(e -> {
			if (customTurnBtn.isSelected()) {	// enable or disable custom turn field
				customTurnField.setEnabled(true);
			} else {
				customTurnField.setEnabled(false);	
			}
		});
		
		buttonGroup.add(turn10Btn);
		buttonGroup.add(turn20Btn);
		buttonGroup.add(customTurnBtn);
		
		customTurnField = new JTextField(3);
		customTurnField = (JTextField)GameUtils.customizeComp(customTurnField, Color.BLACK, Color.CYAN, 20);
		customTurnField.setEnabled(false);	// disable unless radio button is checked!
		customTurnField.setBorder(new LineBorder(Color.CYAN));
		customTurnField.setMaximumSize(new Dimension(100, 20));
		customTurnField.setHorizontalAlignment(SwingConstants.CENTER);
		
		addComponents();
	}
	
	/**
	 * Adds all components to this panel, using a GridBagLayout.
	 */
	private void addComponents() {
		setBorder(new TitledBorder(new LineBorder(Color.CYAN), "Game Turns:", TitledBorder.CENTER,
					TitledBorder.BELOW_TOP,
					new Font("Courier New", Font.BOLD, 14), Color.CYAN));
		setBackground(Color.BLACK);
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		
		c.gridx = 0;
		c.gridy = 0;
		c.weighty = 1.0;
		add(turn10Btn, c);
		
		c.gridx = 0;
		c.gridy = 1;
		add(turn20Btn, c);
		
		c.gridx = 0;
		c.gridy = 2;
		add(customTurnBtn, c);
		
		c.gridx = 1;
		c.gridy = 2;
		c.weighty = 0.0;
		add(customTurnField, c);
		
		c.gridx = 0;
		c.gridy = 3;
		c.gridwidth = 2;
		add(Box.createVerticalStrut(20), c);	// bottom strut
	}
	
	// Accessor methods
	
	public ButtonGroup getButtonGroup() {
		return buttonGroup;
	}
	
	/**
	 * 
	 * @return turn count based on the radio button selected or text field entry.
	 */
	public int getTurnCount() {
		int turns = 0;
		
		if (buttonGroup.isSelected(turn10Btn.getModel())) {
			turns = 10;
		}
		else if (buttonGroup.isSelected(turn20Btn.getModel())) {
			turns = 20;
		}
		else if (buttonGroup.isSelected(customTurnBtn.getModel())) {
			turns = Integer.parseInt(customTurnField.getText());
		}
		
		return turns;
	}

}
