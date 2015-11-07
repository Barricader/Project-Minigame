package dialogs;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JTextField;

import screen.GameUtils;
import states.StartState;

/**
 * Dialog that is created to add a new player. This allows the user to enter the name
 * and create a new player.
 * @author David Kramer
 *
 */
public class AddPlayerDialog extends JDialog {
	private static final long serialVersionUID = 1L;
	private JTextField nameField;
	private JLabel colorLabel;
	private JLabel nameLabel;
	private JButton okBtn;
	private JButton cancelBtn;
	private StartState state;
	
	public AddPlayerDialog(StartState state) {
		this.state = state;
		init();
	}
	
	/**
	 * Initializes all GUI components that are related to this dialog and displays it 
	 * to the screen.
	 */
	private void init() {
		createLabels();
		createTextFields();
		createButtons();
		createAndShowDialog();
	}

	
	private void createLabels() {
		nameLabel = new JLabel("Name: ");
		colorLabel = new JLabel("Color: " );
	}
	
	/**
	 * Creates all buttons and their action handlers that are a part of this dialog.
	 */
	private void createButtons() {
		okBtn = new JButton("Ok");
		okBtn.setEnabled(false);	// disable by default until name has been entered
		okBtn.addActionListener(e -> {
			if (!nameField.getText().isEmpty()) {
				String name = nameField.getText();
				// TODO player color is random right now, add functionality to choose custom color later.
				if (state.addPlayer(name, GameUtils.getRandomColor())) {
					dispose();	
				}
			}
		});
		
		cancelBtn = new JButton("Cancel");
		cancelBtn.addActionListener(e -> {	// just close dialog
			dispose();
		});
	}
	
	/**
	 * Creates all text fields that are part of this dialog.
	 */
	private void createTextFields() {
		nameField = new JTextField(10);
		nameField.addKeyListener(new KeyListener() {
			public void keyReleased(KeyEvent e) {
				if (!nameField.getText().isEmpty()) {	// only enable ok button if there is text
					okBtn.setEnabled(true);
				} else {
					okBtn.setEnabled(false);
				}
			}
			
			// unused
			public void keyPressed(KeyEvent e) {}
			public void keyTyped(KeyEvent e) {}
		
		});
	}
	
	/**
	 * Lays out and adds all GUI components to this dialog.
	 */
	private void addComponents() {
		setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
		add(nameLabel);
		add(nameField);
		add(okBtn);
		add(cancelBtn);
	}
	
	/**
	 * Sets up the dialog window and renders it to the screen.
	 */
	private void createAndShowDialog() {
		addComponents();
		setTitle("Add New Player");
		setSize(new Dimension(350, 85));
		setLocationRelativeTo(state);
		setVisible(true);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
	}
	
}