package logic.timers;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;

import logic.Game;
import logic.Player;

public class PlayerTimer implements ActionListener {
	private final Timer timer;
	private final Player player;
	
	/**
	 * Constructs a new PlayerTimer object.
	 * @param player - The player that this timer belongs to.
	 */
	public PlayerTimer(Player player) {
		final int speed = 400;
		
		this.player = player;
		this.timer = new Timer(speed, this);
		this.timer.setInitialDelay(speed);
	}
	
	/**
	 * The timer starts ticking on calling this function. The constructor does
	 * not start the timer.
	 */
	public void start() {
		timer.start();
	}

	/**
	 * Stops the timer so the board's state should not update. Good for paused
	 * or game over states.
	 */
	public void stop() {
		timer.stop();
	}
	
	/**
	 * Stops and starts the timer used for random events such as a key press.
	 */
	public void restart() {
		timer.stop();
		timer.start();
	}
	
	/**
	 * Changes the speed of the game timer. Good for level mode where the speed
	 * of the falling tetromino must increase over time.
	 * @param milliseconds - The interval length of the timer.
	 */
	public void changeSpeed(int milliseconds) {
		timer.setDelay(milliseconds);
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		player.timerTick();
		if (player.game.state == Game.State.GAME_OVER) {
			this.stop();
			player.game.gameGUI.repaint();
			return;
		}
	}
}
