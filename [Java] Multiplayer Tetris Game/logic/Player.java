package logic;

import logic.ai.ExpertAI;
import logic.ai.RandomAI;
import logic.timers.PlayerTimer;
import menu.GameOptions;
import menu.GameOptions.AiDiff;
import gui.PlayerGUI;

public class Player {
	public final String nick;
	public final Game game;
	public final Board board;
	public final PlayerGUI playerGUI;
	public final GameOptions.AiDiff aiLevel;
	
	public final RandomAI ai1;
	public final ExpertAI ai2;
	public final ExpertAI ai3;
	public int level;
	public int numLinesClearedThisLevel;
	public int totalLinesCleared;
	public Score score;
	
	public final PlayerTimer playerTimer;
	
	public Player(Game game, String nick) {
		this(game, nick, AiDiff.HUMAN);
	}
	
	public Player(Game game, String nick, GameOptions.AiDiff aiLevel) {
		this.game = game;
		this.nick = nick;
		this.level = 1;
		this.numLinesClearedThisLevel = 0;
		this.aiLevel = aiLevel;
		
		board = new Board(game, this);
		playerGUI = new PlayerGUI(this, nick);
		playerTimer = new PlayerTimer(this);
		score = new Score(this);
		ai1 = new RandomAI(board);
		ai2 = new ExpertAI(board, 1, 1, 1, 1, 1, 1);
		ai3 = new ExpertAI(board, -100, 1, 10, 10, 1, 1);
	}
	
	/**
	 * Increment the player's level by 1. Also changes the speed of the falling pieces.
	 */
	void advanceLevel() {
		this.numLinesClearedThisLevel -= this.level;
		++this.level;
		playerTimer.changeSpeed(Math.max(10, 400 - this.level * 50));
	}
	
	/**
	 * Force the player to move. If he/she can't move, then game over.
	 */
	public void timerTick() {
        switch (aiLevel) {
			case HUMAN:
		        board.shiftDownNewPiece();
				break;
			case EASY:
				ai1.moveRandomly();
				break;
			case MEDIUM:
				ai2.think();  		// medium ai
				ai2.MoveToBest(); 	// medium ai
				break;
			case EXPERT:
		        ai3.think();		// expert ai
		        ai3.MoveToBest();	// expert ai
				break;
			default:
				break;
        }
        
        playerGUI.repaint();
	}
}
