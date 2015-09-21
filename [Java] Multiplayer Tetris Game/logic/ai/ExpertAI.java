package logic.ai;

/* Author Jiazhou Liu z3351904  
 * This expert AI will calculate the best move for the current tetromino to 
 * achieve a higher score
 */

import logic.Board;
import logic.Game;
import logic.Tetromino;

public class ExpertAI {

        private Board board;
        private long LineWeightP; 
        private long HeightWeightP;
        private long HoleWeightP;
        private long BlockageWeightP;
        private long BumpinessWeightP;
        private long WellWeightP;
        private int bestX;
        private int bestRotationIndex;
        //private char level;
        
        public ExpertAI(Board board, long LineWeight, long HeightWeight, long HoleWeight, 
                        long BlockageWeight, long BumpinessWeight, long WellWeight){    
                this.board = board;
                LineWeightP = LineWeight;
                HeightWeightP = HeightWeight;
                HoleWeightP = HoleWeight;
                BlockageWeightP = BlockageWeight;
                BumpinessWeightP = BumpinessWeight;
                WellWeightP = WellWeight;
                //this.level = level;
        }
        
        // the rater system based on 6 parameters
        public long evaluate(Board testBoard){
        		if(!testBoard.currPieceValidPosition())	return Long.MAX_VALUE;
        		else return LineWeightP*testBoard.clearLines() + 
                                HeightWeightP*testBoard.getMaxHeight()+
                                HoleWeightP*testBoard.getMaxHoles()+
                                BlockageWeightP*testBoard.getMaxBloackages()+
                                BumpinessWeightP*testBoard.getBumpiness()+
                                WellWeightP*testBoard.getWells();
        }
        
        // main algorithm for AI to try all the possible moves and calculate the best one
        public void think(){
        		if( board.getCHeight(board.currX) >= (board.height - board.currTetromino.getYMax()))	board.game.state = Game.State.GAME_OVER;
                bestX = board.currX;
                Tetromino piece = board.currTetromino;
                bestRotationIndex = piece.rotationIndex;
                Tetromino current = piece;
                long bestScore = Long.MAX_VALUE;
                for(int i = 0; i < 4; i++){

            		while(!board.tryRotate(i) && board.game.state != Game.State.GAME_OVER){
            			board.shiftDownNewPiece();
            		}
                	final int xBound = board.width - current.getWidth() + 1;
            
                	int leftWidth = Math.abs(current.findMinValue(current.getCoords())[0]);
                	// For current rotation, try all the possible columns
            		for (int x = leftWidth; x < xBound + leftWidth; x++) {
            			if (!board.canMoveTo(x))
                        continue;
                        
            			Board testBoard = board.clone(); // clone the same board for handling movements
            			testBoard.currTetromino = board.currTetromino;
            			 int m = testBoard.width / 2;
               			 int n = testBoard.height - 1 - testBoard.currTetromino.getYMax();
               			 testBoard.tryMove(m, n);
               			 testBoard.tryMove(x, testBoard.currY);
                
               			 int y = testBoard.currY;
               			 while (testBoard.tryMove(x, --y))
               				 ;

               			 long score = evaluate(testBoard);
               			 if (score < bestScore) {
               				 bestScore = score;
               				 bestX = x;
               				 bestRotationIndex = current.rotationIndex;
               			 }
            		}
           	 }
        }
        
        // after the best movement is calculated, move to the best place
        public void MoveToBest() {
                while (board.game.state != Game.State.GAME_OVER && !board.tryRotate(bestRotationIndex))
                        board.shiftDownNewPiece();
                
                while (board.game.state != Game.State.GAME_OVER && board.currX != bestX) {
                	board.tryMove(bestX, board.currY);
                    }
                
                board.hardDrop();
        }
}