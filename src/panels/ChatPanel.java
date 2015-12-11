package panels;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import org.json.simple.JSONObject;

import client.ClientApp;
import client.IOHandler;
import gameobjects.NewPlayer;
import util.DarkButton;
import util.GameUtils;
import util.Keys;
import util.NewJSONObject;
import util.PlayerStyles;
import util.ScrollBarUI;

/**
 * Chat panel contains a text area where all messages that have been sent
 * will be kept track of. This allows for communication between other 
 * clients through instant messaging.
 * @author David Kramer
 *
 */
public class ChatPanel extends JPanel {
	private static final long serialVersionUID = -917086496887711313L;

	public ClientApp app;
	
	private JScrollPane scrollPane;
	private JTextPane chatArea;
	private JTextField msgField;
	private DarkButton sendBtn;
	private Controller controller;	
	
	/**
	 * Constructs a new ClientApp with a link to the main ClientApp.
	 * @param app - Target ClientApp.
	 */
	public ChatPanel(ClientApp app) {
		this.app = app;
		controller = new Controller(this);
		init();
		setBackground(Color.BLACK);
		app.colorize(this, new LineBorder(null));
	}
	
	/**
	 * Initializes all GUI components and lays them out using GridBagLayout.
	 */
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
		c.ipadx = 20;
		c.weightx = 0.0;
		c.gridy = 1;
		c.ipady = 10;
		c.weighty = 0.0;
		add(sendBtn, c);
	}
	
	/**
	 * Creates GUI components.
	 */
	private void createComponents() {
		chatArea = new JTextPane();
		chatArea.setEditable(false);
		chatArea.setCaretPosition(chatArea.getDocument().getLength());	// scroll down
		chatArea.setBorder(new EmptyBorder(5, 5, 5, 5));
		chatArea.setBackground(Color.BLACK);
		chatArea.setFont(new Font("Courier New", Font.BOLD, 12));
		app.colorize(chatArea);
		
		msgField = new JTextField();
		app.colorize(msgField, new LineBorder(null), 12);
		msgField.setCaretColor(app.getGlobalColor().getColor());
		msgField.putClientProperty("caretWidth", 2);
		msgField.addKeyListener(new KeyAdapter() {
			public void keyTyped(KeyEvent e) {
				if (e.getKeyChar() == '\n') {
					controller.sendMessage();
				}
			}
		});
		msgField.setFocusable(true);
		msgField.requestFocus();
		
		sendBtn = new DarkButton("Send");
		app.colorize(sendBtn, new LineBorder(null), 16);
		sendBtn.addActionListener( e -> {
			controller.sendMessage();
		});
		
		scrollPane = new JScrollPane(chatArea);
		scrollPane.setPreferredSize(new Dimension(1, 2));	// workaround for "jumping" glitch when resizing window
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.getHorizontalScrollBar().setUI(new ScrollBarUI(SwingConstants.HORIZONTAL));
		scrollPane.getVerticalScrollBar().setUI(new ScrollBarUI(SwingConstants.VERTICAL));
		
		app.colorize(scrollPane, new LineBorder(null), 12);
		app.colorize(scrollPane.getVerticalScrollBar());
		app.colorize(scrollPane.getHorizontalScrollBar());
	
		controller.toggleUI(app.getClient().isConnected());
	}
	
	public Controller getController() {
		return controller;
	}
	
	/**
	 * Prints specified message to the chat area.
	 * @param msg
	 */
	public void printMessage(String name, String text, Color c) {
		MutableAttributeSet attrs = chatArea.getInputAttributes();
		StyleConstants.setForeground(attrs, c);
		StyledDocument doc = chatArea.getStyledDocument();
		String msg = name + " >: " + text;
		
		int offset = doc.getLength();
		int length = msg.length();
		try {
			doc.insertString(offset, msg + "\n", attrs);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
		if (name.equals(app.getBoardPanel().getClientPlayer().getName())) {
			GameUtils.playSound("res/ding.wav");	// play sound, if it's not this client player!	
		}
		chatArea.setCaretPosition(chatArea.getDocument().getLength());	// make sure we're always at btm
		doc.setParagraphAttributes(offset, length, attrs, false);	// apply color style
	}
	
	/**
	 * IOHandler for handling chat messages.
	 * @author David Kramer
	 *
	 */
	public class Controller extends IOHandler {
		ChatPanel cp;
		
		public Controller(ChatPanel cp) {
			this.cp = cp;
		}
		
		/**
		 * Sends a message and echoes it to all other currently connected clients.
		 */
		@SuppressWarnings("unchecked")
		public void sendMessage() {
			String text = msgField.getText();
			if (!text.isEmpty() && app.getClient().isConnected()) {
				msgField.setText("");
				if (text.equals("cls")) {	// clear chat area
					chatArea.setText("");
					return;
				}
				else {
					NewJSONObject k = new NewJSONObject(app.getClient().getID(), Keys.Commands.MSG, text);
					NewPlayer player = app.getLoginPanel().getClientPlayer();
					k.put(Keys.NAME, player.getName());
					System.out.println("sending: " + k);
					send(k);
				}
			}
		}
		
		/**
		 * Toggles the chat UI, based on boolean value.
		 * @param enabled
		 */
		public void toggleUI(boolean enabled) {
			chatArea.setEnabled(enabled);
			msgField.setEnabled(enabled);
			sendBtn.setEnabled(enabled);
		}
		
		/**
		 * Sends JSONObject to clients.
		 */
		public void send(JSONObject out) {
			cp.app.getClient().getIOHandler().send(out);
		}

		/**
		 * Processes message from incoming JSONObject.
		 */
		public void receive(JSONObject in) {
			String text = (String)in.get(Keys.TEXT);
			String name = (String)in.get(Keys.NAME);
			int styleID = app.getBoardPanel().getPlayers().get(name).getStyleID();
			Color c = PlayerStyles.colors[styleID];
			printMessage(name, text, c);
		}
	}
	
}
