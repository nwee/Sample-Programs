package logic;

import java.util.List;
import java.util.Vector;

import logic.timers.GameTimer;
import menu.*;
import gui.GameGUI;
import gui.Sound;
import helper.StopWatch;


public class Game {
	public final GameOptions gameOptions;
	public final GameTimer gameTimer;
	public final List<Player> players;
	public final GameGUI gameGUI;
	public State state;
	public Sound audio;
	public StopWatch stopWatch;
	public boolean isPaused;

	public enum State {
		IDLE, PLAY, PAUSED, GAME_OVER
	}
	
	public Game(GameOptions gameOptions) {
		this.gameOptions = gameOptions;
		this.gameGUI = new GameGUI(this);
		this.audio = new Sound();
		this.stopWatch = new StopWatch(gameOptions.timeLimit);
		this.gameTimer = new GameTimer(this);
		this.isPaused = false;

		players = new Vector<Player>();
		for (int i = 0; i < gameOptions.getNumPlayers(); ++i) {
			Player player;
			if (i == 1)
				player = new Player(this, "Player " + (i + 1), gameOptions.ai);
			else
				player = new Player(this, "Player " + (i + 1), GameOptions.AiDiff.HUMAN);
				
			players.add(player);
		}
		
		gameGUI.init(players);
	}
	
	/**
	 * Starts running a game.
	 */
	public void start() {
		for (Player player : players)
			player.board.nextTetromino();

		gameTimer.start();
		audio.playBattle();
	}
	
	/**
	 * Pauses the whole game.
	 */
	public void pause() {
		if (state == State.GAME_OVER)
			return;
		
		gameTimer.stop();
		this.isPaused = true;
	}
	
	/**
	 * Toggles the pause state of the game.
	 */
	public void togglePause() {
		if (state == State.GAME_OVER)
			return;
		
		if (!this.isPaused)
			gameTimer.stop();
		else
			gameTimer.start();

		this.isPaused = !this.isPaused;
	}
	
	/**
	 * Adds bomb lines to the bottom of the bottom of each player's board
	 * except for the specified player who initiated the attack.
	 * 
	 * @param player The player who initiated the attack. This player's board
	 * does not get a bomb line.
	 */
	public void attackOtherPlayers(Player player) {
		List<Player> playersToAttack = new Vector<Player>(players);
		playersToAttack.remove(player);
		
		for (Player playerToAttack : playersToAttack)
			playerToAttack.board.addBombLine();
	}
	
	/**
	 * Marks this player as the winning player and all other players
	 * as losers so that the GUI can display this info.
	 * @param player The winning player.
	 */
	public void setWinningPlayer(Player winner) {
		for (Player player : players) {
			if (player == winner)
				player.board.lost = false;
			else
				player.board.lost = true;
		}
	}
	
	public static void main(String args[]) {
		// initialize a GameOptions instance and pass to the menu and run to setup settings
		GameOptions gameOptions = new GameOptions();
		StartMenu m = new StartMenu(gameOptions);
		m.run(m.getOptions());		
	}
}
