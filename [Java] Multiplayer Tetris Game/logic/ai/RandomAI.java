package logic.ai;

/* Author Jiazhou Liu z3351904  
 * This random AI will generate random moves for the ai
 */
import java.util.Random;

import logic.Board;


public class RandomAI {

	public static final int ROTATE = 0;
    public static final int LEFT = 1;
    public static final int RIGHT = 2;
    public static final int DROP = 3;
    public static final int DOWN = 4;
	private Board board;
	
	public RandomAI(Board board){
		
		this.board = board;
	}
	
	// random generater
	public int generateRandom(){
		int[] myIntArray = {ROTATE, DOWN, LEFT, RIGHT, DROP};
		int rnd = new Random().nextInt(myIntArray.length);
		return myIntArray[rnd];
	}
	
	// AI move randomly
	public void moveRandomly() {
			int AIkey = generateRandom();
			switch (AIkey) {
			case LEFT:
				board.shiftLeft();
				break;
			case RIGHT:
				board.shiftRight();
				break;
			case DOWN:
				board.shiftDownNewPiece();
				break;
			case ROTATE:
				board.rotateRight();
				break;
			case DROP:
				break;
			//default:
            //    throw new RuntimeException("Bad move");
			}
		
	}
		
}
