package states;
import java.awt.Color;
import java.awt.Rectangle;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.Timer;

import main.Dice;
import main.NewDirector;
import main.Player;
import main.Tile;

public class PlayerManager {
	private NewBoardState boardState;
	private NewDirector director;
	private ArrayList<Player> players;
	private ArrayList<Player> tiedPlayers;	// keeping track of tied players
	private Player activePlayer;
	private boolean firstRollDone = false;	// have all players first rolled initially?
	private boolean playerOrderSorted = false;	// have players been ranked in their sort
	private JLabel statusLabel;
	private Timer timer;	// timer for controlling delay for various events
	
	public PlayerManager(NewDirector director, NewBoardState boardState) {
		this.director = director;
		this.boardState = boardState;
		this.players = director.getPlayers();
		tiedPlayers = new ArrayList<>();
		
		// update status
		statusLabel = boardState.getStatusPanel().getCurPlayerLabel();
		statusLabel.setText("All players need to roll!");
		timer = new Timer(2000, null);
	}
	
	public void updateStatusOnLaunch() {
		resetTimer(2000, false);
		timer.start();
		timer.addActionListener(e -> {
			if (!firstRollDone) {
				if (activePlayer == null) {
					activePlayer = players.get(0);
					toggleActiveIndicator(activePlayer);
				}
				updateStatus(activePlayer);
				timer.stop();
			} else {
				updateActivePlayer();
				timer.stop();
			}
		});
	}
	
	/**
	 * First roll of the game. All players roll to determine the playing
	 * order. 
	 * @param dice - Dice to roll
	 */
	public void firstRoll(Dice dice) {
		for (int i = 0; i < players.size(); i++) {
			Player p = players.get(i);
			if (!p.hasFirstRolled()) {
				activePlayer = p;
				toggleActiveIndicator(activePlayer);
				delayFirstRollUpdate(activePlayer);
				p.setFirstRoll(dice.roll(Dice.SIZE));
				p.setHasFirstRolled(true);	
				
				
				// update label for next player's roll
				Player nextPlayer = null;
				if (i + 1 < players.size()) {
					nextPlayer = players.get(i + 1);
					delayFirstRollUpdate(nextPlayer);
				}
				
				if (i == players.size() - 1) {
					updateStatus("All Players Rolled", Color.GREEN);
					updateStatusOnLaunch();
					break;	// break out and then validate the rolls
				} else {
					return;	// there are more players, that need to be first rolled
				}
			}
		}
	
		if (validateRoll(dice)) {
			firstRollDone = true;
			Tile startTile = boardState.getBoard().getTiles().get(0);
			
			// move all players to tile 0
			for (int i = 0; i < players.size(); i++) {
				Player p = players.get(i);
				p.setActive(false);	// hide indicator when players are moving to tile 0
				p.setTile(startTile);
				p.move(startTile);
			}
		} else {
			rollTies(dice);
		}
	}
	
	/**
	 * Updates all players positioning in response to a window resize event.
	 */
	public void updatePlayersFromResize() {
		for (int i = 0; i < players.size(); i++) {
			Player p = players.get(i);
			
			if (p.getTile() != null) {	// we can reassign location based on current tile
				p.setLocation(p.getTile().getLocation(p.getPlayerID()));
			} else { // still in middle of screen, update relative to mid rect
				Rectangle midRect = boardState.getBoard().getMidRect();
				p.x = midRect.x + (p.width * i) + 100 + (i * 50);
				p.y = midRect.y + 50;
			}
		}
	}
	
	/**
	 * Toggles the active indicator on a player, and ensures that no other players
	 * will have it displayed.
	 * @param activePlayer The player to target the indicator on
	 */
	public void toggleActiveIndicator(Player activePlayer) {
		System.out.println("Toggle active indicator");
		activePlayer.setActive(true);
		
		for (Player p : players) {
			if (p != activePlayer) {
				p.setActive(false);
			}
		}
	}
	
	/**
	 * Rolls the next player, if a player isn't already moving.
	 * @param dice
	 */
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
	
	/**
	 * Moves the player according to the dice roll.
	 * @param dice
	 */
	public void movePlayer(Dice dice) {
		updateActivePlayer();
		ArrayList<Tile> tiles = boardState.getBoard().getTiles();
		int roll = dice.roll(Dice.SIZE);
		int curTileID = activePlayer.getTileID();
		int newTileID = curTileID + roll;
		
		ArrayList<Tile> temp = new ArrayList<>();
		for (int i = curTileID; i < newTileID; i++) {
			if (i > tiles.size()-1) {
				temp.add(tiles.get(i - tiles.size()));
			} else {
				temp.add(tiles.get(i));
			}
		}
		
		if (newTileID >= tiles.size()) {
			newTileID -= tiles.size();
		}
		
		Tile newTile = tiles.get(newTileID - 1);
		System.out.println("new tile: " + newTile);
		
		activePlayer.setPath(temp);
		delayMove(newTile);	// don't move player immediately after rolling!
	}
	
	/**
	 * Checks to see if the active player is currently moving, before updating the status
	 * panel.
	 */
	private void delayStatusUpdate() {
		resetTimer(80, true);
		timer.addActionListener(e -> {
			if (!activePlayer.isMoving()) {
				timer.stop();
				updateActivePlayer();
				updateTurns();	
			}
		});
	}
	
	/**
	 * Updates status label with the specified player
	 * @param p Player to display
	 */
	public void updateStatus(Player p) {
		statusLabel.setForeground(p.getColor());
		statusLabel.setText("Player: " + p + "'s turn!");
	}
	
	/**
	 * Update status label with specified text and color
	 * @param text Text to display
	 * @param c color to display
	 */
	public void updateStatus(String text, Color c) {
		statusLabel.setForeground(c);
		statusLabel.setText(text);
	}
	
	/**
	 * Validates the first roll of the players, and checks for any duplicates.
	 * @return 
	 */
	private boolean validateRoll(Dice dice) {
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
		
		// handle tied rolls if necessary
		if (tiedRoll) {
			String ties = "Ties: "; 
		
			for (Player p : tiedPlayers) {
				ties = ties + " " + p.getName() + "\t";
			}
			
			JOptionPane.showMessageDialog(null, "Tied Players: " + ties);
			
//			rollTies(tiedPlayers, dice);
			return false;
		}
		
		Collections.sort(players);

		activePlayer = players.get(0);
		printRolls();
		return true;
	}
	
	/**
	 * Rolls players if they tied. If they tie again, a random number is picked.
	 * @param dice
	 */
	private void rollTies(Dice dice) {
		boolean tiedRoll = false;
		
		for (int i = 0; i < tiedPlayers.size(); i++) {
			Player p = players.get(i);
			toggleActiveIndicator(p);
			delayFirstRollUpdate(p);
			p.setFirstRoll(dice.roll(Dice.SIZE));
			
			Player nextPlayer = null;
			if (i + 1 < players.size()) {
				nextPlayer = players.get(i + 1);
				delayFirstRollUpdate(nextPlayer);
			}
			
			if (i == players.size() - 1) {
				break;
			}
		}
		
		int highRoll = players.get(players.size() - 1).getFirstRoll();
		
		for (int i = 0; i < tiedPlayers.size(); i++) {
			Player p  = players.get(i);
			if (p.getFirstRoll() > highRoll) {
				highRoll = p.getFirstRoll();
				tiedRoll = false;
			}
			
			if (p.getFirstRoll() == highRoll) {
				tiedRoll = true;
			}
		}
		
		// we have tied again! Just pick a random player
		if (tiedRoll) {
			Random rng = new Random();
			
			Player firstPlayer = tiedPlayers.get(rng.nextInt(tiedPlayers.size() + 1));
			
			int index = players.indexOf(firstPlayer);
			players.remove(index);
			players.set(0, firstPlayer);
			toggleActiveIndicator(firstPlayer);
		}
	}
	
	/**
	 * Updates the active player, based on if they have recently rolled. If all players
	 * have rolled, they are reset, and the first order player will become the active
	 * player.
	 */
	private void updateActivePlayer() {
		if (activePlayer.hasRolled()) {
			activePlayer.setActive(true);
			int index = players.indexOf(activePlayer);
			
			if (!(index + 1 >= players.size())) {
				Player nextPlayer = players.get(index + 1);
				if (!nextPlayer.hasRolled()) {
					activePlayer = nextPlayer;
				} else {
					clearRolls();
				}
			} else {
				clearRolls();
			}
		}
		toggleActiveIndicator(activePlayer);
		updateStatus(activePlayer);
	}
	
	/**
	 * Clears out roll status for all players in array.
	 */
	private void clearRolls() {
		for (Player p : players) {
			p.setHasRolled(false);
		}
		activePlayer = players.get(0);
		activePlayer.setActive(true);
		System.out.println("Players reset: active player is now: " + activePlayer);
	}
	
	/**
	 * For debugging. TODO remove me later!
	 */
	private void printRolls() {
		for (int i = 0; i < players.size(); i++) {
			Player p = players.get(i);
			System.out.println("Player: " + p + ", rolled: " + p.getFirstRoll());
		}
	}
	
	/**
	 * Takes 1 away from the turns that are left, and updates the status panel
	 * accordingly.
	 */
	private void updateTurns() {
		director.minusTurn();
		boardState.getStatusPanel().updateTurnsLeftLabel();
	}
	
	/**
	 * Removes any pre-existing action listeners from the main timer, so
	 * that we don't have any overwriting actions when using the timer.
	 * The new delay and repeats can then be set. The timer is then started.
	 */
	private void resetTimer(int newDelay, boolean repeats) {
		for (ActionListener action : timer.getActionListeners()) {
			timer.removeActionListener(action);
		}
		timer.setInitialDelay(newDelay);
		timer.setRepeats(repeats);
		timer.start();
	}
	
	/**
	 * Delays the movement of the player, until the dice has finished rolling.
	 * After the delay has passed, the player will move to the specified new tile.
	 * @param newTile New tile to move player to
	 */
	private void delayMove(Tile newTile) {		
		resetTimer(1200, false);
		timer.addActionListener(e -> {
			timer.stop();
			activePlayer.move();
			activePlayer.setTile(newTile);
			activePlayer.setHasRolled(true);
			delayStatusUpdate();			
		});
	}
	
	/**
	 * Sets a small delay before updating the status to reflect the next players move,
	 * as well as changing out the active indicator.
	 * @param player
	 */
	private void delayFirstRollUpdate(Player player) {
		resetTimer(1800, false);
		timer.addActionListener(e -> {
			updateStatus(player);
			toggleActiveIndicator(player);
		});
	}
	
	
	// Accessor methods
	
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
