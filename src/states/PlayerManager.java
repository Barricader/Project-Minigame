package states;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.Timer;

import main.Dice;
import main.NewDirector;
import main.Player;
import main.Tile;

public class PlayerManager implements ActionListener {
	private NewBoardState boardState;
	private ArrayList<Player> players;
	private Player activePlayer;
	private Player nextPlayer;
	private boolean firstRollDone = false;	// have all players first rolled initially?
	private boolean playerOrderSorted = false;	// have players been ranked in their sort
	private JLabel statusLabel;
	private Timer timer;	// timer for controlling various events
	
	public PlayerManager(NewDirector director, NewBoardState boardState) {
		this.boardState = boardState;
		this.players = director.getPlayers();
		
		// update status
		statusLabel = boardState.getStatusPanel().getCurPlayerLabel();
		statusLabel.setText("All players need to roll!");
		timer = new Timer(2000, this);
	}
	
	public void updateStatusOnLaunch() {
		timer.start();
	}
	
	public void actionPerformed(ActionEvent e) {
		if (!firstRollDone) {
			if (activePlayer == null) {
				activePlayer = players.get(0);
			}
			statusLabel.setForeground(activePlayer.getColor());
			statusLabel.setText(activePlayer + ", needs to roll");
			timer.stop();
		}
	}
	
	public void firstRoll(Dice dice) {
		for (int i = 0; i < players.size(); i++) {
			Player p = players.get(i);
			
			if (!p.hasFirstRolled()) {
				activePlayer = p;
				statusLabel.setForeground(activePlayer.getColor());
				statusLabel.setText(activePlayer + ", needs to roll");
				p.setFirstRoll(dice.roll(Dice.SIZE));
				p.setHasFirstRolled(true);	
				
				
				// update label for next player's roll
				Player nextPlayer = null;
				if (i + 1 < players.size() - 1) {
					nextPlayer = players.get(i + 1);
					statusLabel.setForeground(nextPlayer.getColor());
					statusLabel.setText(nextPlayer + ", needs to roll!");
				}
				
				if (i == players.size() - 1) {
					break;	// break out and then validate the rolls
				} else {
					return;	// there are more players, that need to be first rolled
				}
			}
		}
	
		if (validateRoll()) {
			firstRollDone = true;
			statusLabel.setForeground(Color.CYAN);
			statusLabel.setText("All players have rolled!");
			System.out.println("All players have rolled! Now checking for highest score");
			
			for (int i = 0; i < players.size(); i++) {
				Player p = players.get(i);
				Tile t = boardState.getBoard().getTiles().get(0);
				p.setTile(t);
				p.move(t);
			}
		}
	}
	
	public void rollNextPlayer(Dice dice) {
		boolean playerAlreadyMoving = false;
		
		// check for movement in players already
		for (int i = 0; i < players.size(); i++) {
			Player p = players.get(i);
			if (p.isMoving()) {
				playerAlreadyMoving = true;
			}
		}
		
		if (!playerAlreadyMoving) {
			movePlayer(dice);
		}
		
	}
	
	public void movePlayer(Dice dice) {
		int roll = dice.roll(Dice.SIZE);
		ArrayList<Tile> tiles = boardState.getBoard().getTiles();
		
		if (activePlayer != null) {
			boardState.getStatusPanel().updateCurPlayerLabel(activePlayer);
			int ID = activePlayer.getPlayerID();
			
			// switch out player
			if (!(++ID >= players.size())) {
				activePlayer = players.get(ID);
			} else {
				activePlayer = players.get(0);
			}
			
			int curTileID = activePlayer.getTileID();
			int newTileID = curTileID + roll;
			
			ArrayList<Tile> temp = new ArrayList<>();
			for (int i = curTileID; i < newTileID; i++) {
				if (i > tiles.size()-1) {
					temp.add(tiles.get(i - tiles.size()));
				}
				else {
					temp.add(tiles.get(i));
				}
			}
			
			if (newTileID >= tiles.size()) {
				newTileID -= tiles.size();
			}
			
			Tile newTile = tiles.get(newTileID - 1);
			System.out.println("new tile: " + newTile);
			
			activePlayer.setPath(temp);
			activePlayer.move();
			activePlayer.setTile(newTile);
		}
	}
	
	private boolean validateRoll() {
		ArrayList<Player> tiedPlayers = new ArrayList<>();
		boolean tiedRoll = false;
		int highRoll = 0;
		
		for (int i = 0; i < players.size(); i++) {
			Player p = players.get(i);
			
			if (p.getFirstRoll() > highRoll) {
				highRoll = p.getFirstRoll();
				tiedRoll = false;
				tiedPlayers.clear();
				continue;
			}
			
			if (p.getFirstRoll() == highRoll) {
				tiedRoll = true;
				tiedPlayers.add(p);
			}
		}
		
		if (tiedRoll) {
			String ties = "";
			
			for (Player p : tiedPlayers) {
				ties += p.getName() + "\t";
			}
			
			JOptionPane.showMessageDialog(null, "Tied Players: " + ties);
			return false;
		}
		return true;
	}
	
	private void updateTurns() {
		
	}
	
	public ArrayList<Player> getPlayers() {
		return players;
	}
	
	
	public Player getActivePlayer() {
		return activePlayer;
	}
	
	public boolean firstRollDone() {
		return firstRollDone;
	}
	
}
