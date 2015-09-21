package menu;

import gui.Sound;

public class GameOptions {
	private int numPlayers; // 1-2
	public Mode mode;
	private boolean soundOnOff; // 0-off 1-on
	public Sound sound;
	public int boardWidth;
	public int boardHeight;
	public AiDiff ai;
	public int mapIndex;
	public int timeLimit; // Time limit in seconds for timer mode.
	public int rushLimit; // Number of lines to clear in rush mode.

	public enum Mode {
		/*
		 * In level mode, blocks fall faster depending on the level. Level
		 * increments after clearing a set number of lines.
		 */
		LEVEL("Level"),
		/*
		 * In rush mode, the game ends when a set number of lines (default of
		 * 40) are cleared by the winning player.
		 */
		RUSH("Rush"),
		/*
		 * In fight mode, lines can be sent across to the other players board by
		 * clearing lines and performing combos. A combo is the number of
		 * consecutive lines cleared.
		 */
		FIGHT("Fight"),
		/*
		 * In timer mode, player(s) play for a set amount of time (default 1
		 * minute). The winning player clears the most lines.
		 */
		TIMED("Timed");

		Mode(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}

		private final String name;
	}

	public enum AiDiff {
		HUMAN("HUMAN"), EASY("Easy AI"), MEDIUM("Medium AI"), EXPERT("Expert AI");

		AiDiff() {
			this.name = "EasyAI";
		}

		AiDiff(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}

		private final String name;
	}

	public GameOptions() {
		this(1);
	}

	/**
	 * Constructor with default options besides the number of players.
	 * 
	 * @param numPlayers
	 *            The number of players.
	 */
	public GameOptions(int numPlayers) {
		this(numPlayers, 10, 22, Mode.LEVEL, AiDiff.HUMAN, 0, 0, 0);
	}

	/**
	 * Constructor for all game options.
	 * 
	 * @param nPlayers
	 *            The number of players.
	 * @param boardWidth
	 *            Board width.
	 * @param boardHeight
	 *            Board height.
	 * @param mode
	 *            TetrisFight game mode.
	 * @param AiDiff
	 *            AI difficulty.
	 * @param mapIndex
	 *            The map all players will start off with.
	 * @param timeLimit
	 *            The time limit for timed mode (0 for no time limit).
	 */
	public GameOptions(int nPlayers, int boardWidth, int boardHeight,
			Mode mode, AiDiff AiDiff, int mapIndex, int timeLimit, int rushLimit) {
		this.numPlayers = nPlayers;
		this.boardWidth = boardWidth;
		this.boardHeight = boardHeight;
		this.mode = mode;
		this.soundOnOff = true;
		this.ai = AiDiff;
		this.mapIndex = mapIndex;
		this.timeLimit = timeLimit;
		this.rushLimit = rushLimit;

	}

	public int getNumPlayers() {
		return numPlayers;
	}

	/**
	 * @param numPlayers
	 *            The number of players to set.
	 */
	public void setNumPlayers(int numPlayers) {
		this.numPlayers = numPlayers;
	}

	/**
	 * @return The mode
	 */
	public Mode getMode() {
		return mode;
	}

	/**
	 * @param mode
	 *            the mode to set
	 */
	public void setMode(Mode mode) {
		this.mode = mode;
		if (mode == Mode.TIMED)
			this.timeLimit = 120;
		else if (mode == Mode.RUSH)
			this.rushLimit = 40;
	}

	/**
	 * @return the sound
	 */
	public boolean getSoundOnOff() {
		return soundOnOff;
	}

	/**
	 * @param sound
	 *            the sound to set
	 */
	public void setSoundOnOff(boolean sound) {
		this.soundOnOff = sound;
	}

}
