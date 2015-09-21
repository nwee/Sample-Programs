package logic.timers;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;

import logic.Game;
import logic.Player;
import logic.Game.State;
import menu.GameOptions.Mode;

public class GameTimer implements ActionListener {
	private final Timer timer;
	private final Game game;
	public final StopWatchGUITimer stopWatchGUITimer;

	private class StopWatchGUITimer implements ActionListener {
		public Timer timer;

		public StopWatchGUITimer(int updateInterval) {
			this.timer = new Timer(updateInterval, this);
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			for (Player player : game.players)
				player.playerGUI.stopWatchPanel.repaint();
		}

	}

	/**
	 * Constructs a new GameTimer object.
	 * 
	 * @param game
	 *            The Game that this timer belongs to.
	 */
	public GameTimer(Game game) {
		final int speed = 400;

		this.game = game;
		this.timer = new Timer(speed, this);
		this.timer.setInitialDelay(speed);
		this.stopWatchGUITimer = new StopWatchGUITimer(100);
	}

	/**
	 * The timer starts ticking on calling this function. The constructor does
	 * not start the timer.
	 */
	public void start() {
		timer.start();
		game.stopWatch.start();
		game.audio.playBattle();
		stopWatchGUITimer.timer.start();

		for (Player player : game.players)
			player.playerTimer.start();
	}

	/**
	 * Stops the timer so the game state should not update. Good for paused or
	 * game over states. Also stops the music.
	 */
	public void stop() {
		stopWatchGUITimer.timer.stop();
		game.audio.stopMusic();
		game.stopWatch.stop();
		timer.stop();

		for (Player player : game.players)
			player.playerTimer.stop();
	}

	/**
	 * Changes the speed of the game timer. Good for level mode where the speed
	 * of the falling tetromino must increase over time.
	 * 
	 * @param milliseconds
	 *            The interval length of the timer.
	 */
	public void changeSpeed(int milliseconds) {
		timer.setDelay(milliseconds);
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		game.gameGUI.repaint();

		if (game.gameOptions.mode == Mode.TIMED
				&& game.stopWatch.getRemainingMillis() == 0) {
			game.state = State.GAME_OVER;
			
			int maxScore = game.players.get(0).score.value;
			for (Player player : game.players)
				maxScore = Math.max(maxScore, player.score.value);
			
			for (Player player : game.players) {
				if (player.score.value != maxScore)
					player.board.lost = true;
			}
				
		}

		if (game.state == Game.State.GAME_OVER) {
			this.stop();
		}
	}
}
