/**COMP2911 Project: Maze Runner 2014
 * The player class maintains various player details, including their coordinates and highscore values
 * 
 * @author Nelson Wee, z3352078
 * @author Renmark Marte, z3464929
 * @author Sung Min Park, z3278712
 * @author Luna Pradhananga, z3358423 
 *
 */
public class Player implements Comparable<Player>{
	public int x;
	public int y;
	private int scoreBonus;
	private int score;
	private String name;
	
	public Player(int origX, int origY){
		x = origX;
		y = origY;
		scoreBonus = 0;
		score = 0;
		name = "";
	}
	/**
	 * This method returns the X coordinate
	 * @return x
	 */
	public int getX(){
		return x;
	}
	
	/**
	 * This method returns the X coordinate
	 * @return y
	 */
	public int getY(){
		return y;
	}
	
	/**
	 * This method modifies and updates the X coordinate
	 * @param change the amount to be changed
	 */
	public void changeX(int change){
		x += change;
	}
	
	/**
	 * This method modifies and updates the Y coordinate
	 * @param change the amount to be changed
	 */
	public void changeY(int change){
		y += change;
	}
	
	/**
	 * This method returns the current bonus modifier
	 * @return scoreBonus
	 */
	public int getScoreBonus(){
		return scoreBonus;
	}
	
	/**
	 * This method adds to the scoreBonus modifier in instances of 100
	 * @param bonus amount to be changed
	 */
	public void setScoreBonus(int bonus) {
		scoreBonus += bonus;
	}
	
	/**
	 * This method returns the score of the player
	 * @return score
	 */
	public int getScore(){
		return score;
	}
	
	/**
	 * This method calculates the score based on time
	 * @param time
	 * 
	 */
	public void calcScore(int time) {
		if (time*100 < 10000) {
			score = 10000 - time*100 + scoreBonus;
		}
		else {
			score += scoreBonus;
		}
	}
	
	//Highscore Methods
	/**
	 * This method sets the high score, used when reading from file
	 * @param score
	 */
	public void setHighScore(int score) {
		this.score = score;
	}
	
	/**
	 * This method sets the high score name, used when reading from file
	 * @param name
	 */
	public void setHighScoreName(String name) {
		this.name = name; 
	}
	
	/**
	 * This method returns the name of the player
	 * @return name
	 */
	public String getHighScoreName() {
		return name; 
	}
	
	/**
	 * This class allows for the players to be compared and sorted based on score. 
	 */
	@Override
	public int compareTo(Player o) {
		if(this.getScore() < o.getScore()) { 
			return 1; 
		}
		if (this.getScore() > o.getScore()) { 
			return -1; 
		}
		//if (this.getScore() == o.getScore()) {
		return 0;
		//}
	}
	
}
