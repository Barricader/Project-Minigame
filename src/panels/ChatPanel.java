package panels;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;

import org.json.simple.JSONObject;

import client.ClientApp;
import client.IOHandler;
import util.NewJSONObject;

public class ChatPanel extends JPanel {
	public ClientApp app;
	
	private JScrollPane scrollPane;
	private JTextArea chatArea;
	private JTextField msgField;
	private JButton sendBtn;
	private Controller controller;
	
	public ChatPanel(ClientApp app) {
		this.app = app;
		controller = new Controller(this);
		init();
	}
	
	private void init() {
		createComponents();
		
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(5, 5, 5, 5);	// margin
		
		// chat area
		c.anchor = GridBagConstraints.NORTH;
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		c.gridwidth = 2;
		c.weightx = 1.0;
		c.gridy = 0;
		c.weighty = 1.0;
		add(scrollPane, c);
		
		// msg field
		c.anchor = GridBagConstraints.SOUTHWEST;
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		c.weightx = 1.0;
		c.gridwidth = 1;
		c.gridy = 1;
		c.weighty = 0.0;
		add(msgField, c);
		
		// send button
		c.anchor = GridBagConstraints.SOUTHEAST;
		c.fill = GridBagConstraints.VERTICAL;
		c.gridx = 1;
		c.weightx = 0.0;
		c.gridy = 1;
		c.weighty = 0.0;
		add(sendBtn, c);
	}
	
	private void createComponents() {
		chatArea = new JTextArea();
		chatArea.setEditable(false);
		chatArea.setWrapStyleWord(true);
		chatArea.setLineWrap(true);
		chatArea.setCaretPosition(chatArea.getDocument().getLength());	// scroll down
		chatArea.setBorder(new LineBorder(Color.LIGHT_GRAY));
		
		msgField = new JTextField();
		msgField.addKeyListener(new KeyListener() {
			public void keyTyped(KeyEvent e) {
				if (e.getKeyChar() == '\n') {
					controller.sendMessage();
				}
			}

			// unused
			public void keyPressed(KeyEvent e) {}
			public void keyReleased(KeyEvent e) {}
		});
		msgField.setFocusable(true);
		msgField.requestFocus();
		
		sendBtn = new JButton("Send");
		sendBtn.addActionListener( e -> {
			controller.sendMessage();
		});
		
		scrollPane = new JScrollPane(chatArea);
		scrollPane.setPreferredSize(new Dimension(1, 2));	// workaround for "jumping" glitch when resizing window
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
	
		controller.toggleUI(app.getClient().isConnected());
	}
	
	public Controller getController() {
		return controller;
	}
	
	/**
	 * Prints specified message to the chat area.
	 * @param msg
	 */
	public void printMessage(String msg) {
		String msgArea = chatArea.getText();
		msgArea += msg + "\n";
		chatArea.setText(msgArea);
		chatArea.setCaretPosition(chatArea.getDocument().getLength());	// move scroll bar down to bottom
	}
	
	public class Controller extends IOHandler {
		ChatPanel cp;
		
		public Controller(ChatPanel cp) {
			this.cp = cp;
		}
		
		public void sendMessage() {
			String text = msgField.getText();
			if (!text.isEmpty() && app.getClient().isConnected()) {
				if (text.equals("cls")) {	// clear chat area
					chatArea.setText("");
					msgField.setText("");
					return;
				}
				else {
					NewJSONObject k = new NewJSONObject(app.getClient().getID(), "msg", text);
					send(k);
				}
			}
		}
		
		public void toggleUI(boolean enabled) {
			chatArea.setEnabled(enabled);
			msgField.setEnabled(enabled);
			sendBtn.setEnabled(enabled);
		}

		public void send(JSONObject out) {
			cp.app.getClient().getIOHandler().send(out);
		}

		public void receive(JSONObject in) {
			if (in.get("cmd") == "msg") {
				String text = (String) in.get("text");
				int id = (int) in.get("playerID");
				cp.printMessage(app.getBoardPanel().getPlayers().get(id).getName() + "> " + text);
			}
		}
	}
	
}
