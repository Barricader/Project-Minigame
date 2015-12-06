package newserver;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.BindException;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.border.EtchedBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import gameobjects.NewPlayer;
import util.ErrorUtils;
import util.GameUtils;

/**
 * ServerApp is a GUI manager for controlling the server. Events
 * that happen on the server, are logged to the console window. You can
 * visually see a list of connected clients, and can optionally disconnect
 * them if needed. This class contains multiple nested inner classes which are 
 * all of the various panels in this GUI app.
 * @author David Kramer
 *
 */
public class ServerApp extends JFrame {
	private static final Dimension SIZE = new Dimension(500, 300);
	private Server server;
	
	private JPanel panel;	// panel that will hold everything else
	private ConsolePanel consolePanel;
	private ListPanel listPanel;
	private ControlPanel ctrlPanel;
	private InfoPanel infoPanel;
	
	/**
	 * Creates a new ServerApp with a connection to the server. NOTE, the server
	 * is not yet started. It is up to the user to click the start btn, to actually
	 * open the server socket and begin accepting connections.
	 */
	public ServerApp() {
		server = new Server(this);
		init();
		createAndShowGUI();
	}
	
	/**
	 * Initializes and lays out GUI components using GridBagLayout.
	 */
	private void init() {
		createComponents();
		panel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(5, 5, 5, 5);
		
		c.anchor = GridBagConstraints.NORTHWEST;
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		c.gridwidth = 8;
		c.weightx = 1.0;
		c.gridy = 0;
		c.weighty = 1.0;
		panel.add(consolePanel, c);
		
		// player list
		c.anchor = GridBagConstraints.NORTHEAST;
		c.gridx = 8;
		c.ipadx = 50;
		c.gridwidth = 2;
		c.weightx = 0.0;
		panel.add(listPanel, c);
		
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		c.weightx = 1.0;
		c.gridwidth = 4;
		c.gridy = 1;
		c.weighty = 0.0;
		panel.add(ctrlPanel, c);
		
		c.gridx = 8;
		c.weightx = 0;
		c.gridwidth = 2;
		c.gridy = 1;
		panel.add(infoPanel, c);
		
		add(panel);
	}
	
	/**
	 * Creates all of the various GUI panels.
	 */
	private void createComponents() {
		panel = new JPanel();
		listPanel = new ListPanel();
		ctrlPanel = new ControlPanel();
		infoPanel = new InfoPanel();
		consolePanel = new ConsolePanel();
	}
	
	/**
	 * Sizes out the application window and makes it visible to the screen
	 * and makes sure that we close out the application nicely.
	 */
	private void createAndShowGUI() {
		setSize(SIZE);
		setMinimumSize(SIZE);
		setLocationRelativeTo(null);
		setTitle("Project MiniGame Server");
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		setVisible(true);
		
		// window close and ensures server is terminated nicely.
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				try {
					if (server.isRunning()) {
						terminateServer();
					}
				} catch (IOException | InterruptedException ex) {
					ex.printStackTrace();
				} finally {
					dispose();
					System.exit(0);
				}
			}
		});
	}
	
	/**
	 * Records a string to the console, such as when a client connects.
	 * Also prints everything out to the text-console as well.
	 * @param msg - String to log.
	 */
	public void log(String msg) {
		String consoleText = consolePanel.getConsole().getText();
		consoleText += "\n " + msg;
		consolePanel.getConsole().setText(consoleText);
		System.out.println(msg);
	}
	
	/**
	 * Terminates the server by disconnecting all connected players
	 * and then ends the process.
	 * @throws IOException
	 * @throws InterruptedException
	 */
	private void terminateServer() throws IOException, InterruptedException {
		for (NewPlayer p : server.getServerDirector().getPlayers().values()) {
			server.getServerDirector().removePlayer(p);
		}
		server.terminate();
	}
	
	public ListPanel getListPanel() {
		return listPanel;
	}
	
	
	//********************************************************
	//* 				    CONSOLE PANEL					 *
	//********************************************************
	
	class ConsolePanel extends JPanel {
		private JScrollPane scrollPane;
		private JTextArea console;
		
		/**
		 * Creates a console log window which records any events that 
		 * occur on the server, where the log flag has been set to
		 * true.
		 */
		public ConsolePanel() {
			init();
		}
		
		/**
		 * Initializes and lays out GUI components using GridBagLayout.
		 */
		private void init() {
			createComponents();
			setLayout(new GridBagLayout());
			GridBagConstraints c = new GridBagConstraints();
			c.fill = GridBagConstraints.BOTH;
			c.gridx = 0;
			c.weightx = 1.0;
			c.gridy = 0;
			c.weighty = 1.0;
			add(scrollPane, c);
		}
		
		/**
		 * Creates all of the various GUI components.
		 */
		private void createComponents() {
			setBorder(new TitledBorder(new EtchedBorder(), "Console Output",
					TitledBorder.LEFT, TitledBorder.CENTER));
			console = new JTextArea();
			console.setEditable(false);
			console.setFont(new Font("Courier New", Font.PLAIN, 12));
			console.setWrapStyleWord(true);
			console.setLineWrap(true);
			console.setComponentPopupMenu(new SaveContextMenu());
			scrollPane = new JScrollPane(console);
			scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		}
		
		// simple popup menu for saving to a log file
		private class SaveContextMenu extends JPopupMenu {
			private JMenuItem menuItem;
			
			public SaveContextMenu() {
				menuItem = new JMenuItem("Save to log file");
				add(menuItem);
				menuItem.addActionListener( e -> {
					GameUtils.writeLogFile(console);
				});
			}
		}
		
		public JTextArea getConsole() {
			return console;
		}
	}
	
	
	//********************************************************
	//* 				    LIST PANEL						 *
	//********************************************************
	
	class ListPanel extends JPanel {
		private DefaultListModel<String> listModel;
		private JList<String> pList;
		
		/**
		 * Creates new list panel which stores a list of all the players
		 * that are currently on the server.
		 */
		public ListPanel() {
			init();
		}
		
		/**
		 * Initializes and lays out GUI components using GridBagLayout.
		 */
		private void init() {
			createComponents();
			setLayout(new GridBagLayout());
			GridBagConstraints c = new GridBagConstraints();
			c.fill = GridBagConstraints.BOTH;
			c.gridx = 0;
			c.weightx = 1.0;
			c.gridy = 0;
			c.weighty = 1.0;
			add(pList, c);
		}
		
		/**
		 * Creates all of the various GUI components.
		 */
		private void createComponents() {
			setBorder(new TitledBorder(new EtchedBorder(), "Client List",
					TitledBorder.LEFT, TitledBorder.CENTER));
			listModel = new DefaultListModel<>();
			pList = new JList<>(listModel);
			pList.setBorder(new LineBorder(Color.GRAY));
			
			pList.addMouseListener(new MouseAdapter() {
				public void mousePressed(MouseEvent e) {
					if (SwingUtilities.isRightMouseButton(e)) {
						JList list = (JList) e.getSource();
						int row = list.locationToIndex(e.getPoint());
						list.setSelectedIndex(row);
						list.setComponentPopupMenu(new ContextMenu(row));
					}
				}
			});
		}
		
		public DefaultListModel<String> getListModel() {
			return listModel;
		}
		
		/**
		 * Updates the list with all players that are on the server.
		 */
		public void updateList() {
			listModel.removeAllElements();
			
			for (NewPlayer p : server.getServerDirector().getPlayers().values()) {
				listModel.addElement(p.getName());
			}
			pList.repaint();
		}
		
		
		//********************************************************
		//* 				    CONTEXT MENU					 *
		//********************************************************
		
		private class ContextMenu extends JPopupMenu {
			private JMenuItem menuItem;
			
			/**
			 * Constructs a new context menu option to remove a user, with the
			 * specified name, determined by the row index of the list.
			 * @param row
			 */
			public ContextMenu(int row) {
				String name = listModel.getElementAt(row);
				menuItem = new JMenuItem("Disconnect: \"" + name + "\" from server");
				menuItem.setForeground(Color.RED);
				menuItem.setIcon(UIManager.getIcon("OptionPane.warningIcon"));
				add(menuItem);
				menuItem.addActionListener( e -> {
					server.getServerDirector().removePlayer(new NewPlayer(name, -1));
				});
			}
		}
	}
	
	
	//********************************************************
	//* 				    CONTROL PANEL					 *
	//********************************************************
	
	class ControlPanel extends JPanel {
		private JButton startBtn;
		private JButton stopBtn;
		private JLabel statusLabel;
		
		/**
		 * Creates new control panel that provides controls for starting
		 * and stopping the server.
		 */
		public ControlPanel() {
			init();
		}
		
		/**
		 * Initializes and lays out GUI components using GridBagLayout.
		 */
		private void init() {
			createComponents();
			setLayout(new GridBagLayout());
			GridBagConstraints c = new GridBagConstraints();
			
			c.fill = GridBagConstraints.HORIZONTAL;
			c.gridx = 0;
			c.weightx = 1.0;
			c.gridy = 0;
			c.ipady = 10;
			c.weighty = 1.0;
			add(startBtn, c);
			
			c.gridx = 1;
			c.gridy = 0;
			c.weighty = 1.0;
			add(stopBtn, c);
			
			c.gridx = 0;
			c.gridwidth = 2;
			c.gridy = 1;
			add(statusLabel, c);
		}
		
		/**
		 * Creates all of the various GUI components.
		 */
		private void createComponents() {
			setBorder(new TitledBorder(new EtchedBorder(), "Server Control",
					TitledBorder.LEFT, TitledBorder.CENTER));
			// starts server
			startBtn = new JButton("Start");
			startBtn.addActionListener(e -> {
				if (!server.isRunning()) {
					try {
						server = new Server(ServerApp.this);
						server.open();
					} catch (BindException ex) {
						statusLabel.setText("Status: failed to start!");
					} catch (IOException ex) {
						statusLabel.setText("Status: failed to start!");
					}
					server.start();
					statusLabel.setText("Status: Running!");	
				} else {
					ErrorUtils.showCustomWarning(this, "Server instance already running!", "Already Running!");
				}
			});
			
			// stops the server
			stopBtn = new JButton("Stop");
			stopBtn.addActionListener(e -> {
				try {	
					if (server.isRunning()) {
						terminateServer();
						server.getServerDirector().clearAll();
					}
				} catch (InterruptedException ex) {
					statusLabel.setText("Status: Server Error.");
				} catch (IOException ex) {
					statusLabel.setText("Status: Server Error.");
				} finally {
					statusLabel.setText("Status: Not running");
				}
			});
			
			statusLabel = new JLabel("Status: Not running");
			statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
		}
	}
	
	
	//********************************************************
	//* 				    INFO PANEL						 *
	//********************************************************
	
	class InfoPanel extends JPanel {
		private JLabel hostLabel;
		private JLabel portLabel;
		
		/**
		 * Creates new InfoPanel that contains info about server
		 * connection.
		 */
		public InfoPanel() {
			init();
		}
		
		/**
		 * Initializes and lays out GUI components using GridBagLayout.
		 */
		private void init() {
			createComponents();
			setLayout(new GridBagLayout());
			GridBagConstraints c = new GridBagConstraints();
			c.insets = new Insets(10, 10, 10, 10);
			c.anchor = GridBagConstraints.WEST;
			c.gridx = 0;
			c.weightx = 1.0;
			c.gridy = 0;
			c.weighty = 1.0;
			add(hostLabel, c);
			
			c.gridx = 0;
			c.gridwidth = 2;
			c.gridy = 1;
			add(portLabel, c);
			
		}
		
		/**
		 * Creates all of the various GUI components.
		 */
		private void createComponents() {
			setBorder(new TitledBorder(new EtchedBorder(), "Connection Info",
					TitledBorder.LEFT, TitledBorder.CENTER));
			hostLabel = new JLabel("Host: " + Server.HOST);
			portLabel = new JLabel("Port: " + Server.PORT);
		}
	}
	
	
	/**
	 * Main method of the application. Creates an instance of a ServerApplication
	 * to provide GUI controls for server operations.
	 * @param args
	 */
	public static void main(String[] args) {
		// change look and feel to nimbus
		try {
		    for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
		        if ("Nimbus".equals(info.getName())) {
		            UIManager.setLookAndFeel(info.getClassName());
		            break;
		        }
		    }
		} catch (Exception e) {
		    e.printStackTrace();
		}
		ServerApp app = new ServerApp();
	}
}