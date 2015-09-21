package logic;

public class Score {
	final Player player;
	public int value;
	
	public Score(Player player) {
		this.player = player;
	}
	
	/**
	 * Updates the player's score based on the amount of lines just cleared.
	 * 
	 * @param numLinesCleared The number of lines cleared (used to update the player's score).
	 */
	public void lines(int numLinesCleared) {
		this.value += 10 * numLinesCleared;
	}
	
	/**
	 * Updates the player's score based on the amount of combos in the current streak.
	 * 
	 * @param numCombosPerformed The number of combos in the current streak.
	 */
	public void combos(int numCombosInStreak) {
		this.value += 15 * numCombosInStreak;
	}
	
	/**
	 * Updates the player's score when they swap their piece.
	 */
	public void swap() {
		this.value -= 10;
	}
	
	/**
	 * Updates the player's score when they do a perfect clear (the board is totally empty).
	 * 
	 */
	public void perfectClear() {
		this.value += 100;
	}
}
