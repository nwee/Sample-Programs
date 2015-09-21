package logic;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import logic.timers.PlayerTimer;
import gui.BoardGUI;
import helper.Pos;
//import gui.Sound;

public class Board {
	public final Game game;
	
	final static Random RND = new Random();

	public final int width;
	public final int height;
	public Tetromino.Shape[][] tetrominos;
	public Tetromino currTetromino;
	public int currX;
	public int currY;
	public boolean lost;
	public List<Tetromino> tetrominoQ;
	public Tetromino storedTetromino;
	public int numLinesCleared;
	public int numCombos;
	public int totalLinesCleared;
	public int attacks;
	
	public Player player;
	public BoardGUI boardGUI;
	public PlayerTimer boardTimer;

	public Board(Game game, Player player) {
		this(game, player, 10, 22);
	}

	public Board(Game game, Player player, int width, int height) {
		this.game = game;
		this.player = player;
		this.width = width;
		this.height = height;
		this.lost = false;
		this.storedTetromino = null;
		this.numLinesCleared = 0;
		this.numCombos = 0;
		this.attacks = 0;
		
		// Delete this later.
		Map map = new Map(width, height);
		tetrominos = map.getMap(game.gameOptions.mapIndex);
		
		this.invalidateCurrPiece();
		this.totalLinesCleared = 0;

		boardGUI = new BoardGUI(this);

		// Set up the queue of of 5 random tetrominos.
		tetrominoQ = new LinkedList<Tetromino>();
		for (int i = 0; i < 5; ++i)
			tetrominoQ.add(Tetromino.makeRandomTetromino(tetrominoQ));
	}

	// Clone a new board will be same as the current board
	public Board clone() {
    	Board cloned = new Board(this.game, this.player, width, height);
    	for(int x = 0; x < width; x++){
            for(int y = 0; y < height; y++){
            	cloned.tetrominos[y][x] = tetrominos[y][x];
            }
    	}
    	return cloned;
    }

	/**
	 *  Gets the next tetromino and tries to place it on the board.
	 *  @return Returns true if successful, otherwise false to signify player has lost.
	 */
	boolean nextTetromino() {
		currTetromino = tetrominoQ.remove(0);
		tetrominoQ.add(Tetromino.makeRandomTetromino(tetrominoQ));

		player.playerGUI.nextPanel.repaint();

		int x = width / 2;
		int y = height - 1 - currTetromino.getYMax();
		return tryMove(x, y);
	}

	/**
	 *  Drops the current piece as far down as it can go.
	 */
	public void hardDrop() {
		if (isCurrPieceInvalid())
			return;
		
		int y = currY;
		while (tryMove(currX, --y))
			;
		
		tryMove(currX, y + 1);
		shiftDownNewPiece();
		player.playerTimer.restart();
	}
	
	/** Tries to swap the current piece with the one in storage.
	 * If not possible then nothing will happen.
	 * 
	 * @return true if successful, otherwise false.
	 */
	public boolean trySwap() {
		if (isCurrPieceInvalid())
			return false;
		
		Tetromino oldTetromino = currTetromino;
		currTetromino = storedTetromino == null ? tetrominoQ.get(0) : storedTetromino;
		
		if (!tryMove(currX, currY)) {
			// Can't swap because the new piece won't fit on the board.
			// Restore the old piece.
			currTetromino = oldTetromino;
			return false;
		}

		// Swap was successful. Place the swapped tetromino in storage.
		if (storedTetromino == null) {
			tetrominoQ.remove(0);
			tetrominoQ.add(Tetromino.makeRandomTetromino(tetrominoQ));
		}
		
		player.score.swap();
		storedTetromino = oldTetromino;
		player.playerGUI.storePanel.repaint();
		player.playerGUI.nextPanel.repaint();
		game.audio.playSwap();
		return true;
	}
	
	/**
	 * Adds a bomb line to the bottom of this board. 
	 */
	void addBombLine() {
		if (!isLineEmpty(height - 1)) {
			game.state = Game.State.GAME_OVER;
			this.lost = true;
		}
		
		for (int y = height - 1; y > 0; --y) {
			for (int x = 0; x < width; ++x)
				tetrominos[y][x] = tetrominos[y - 1][x];
		}
		
		for (int x = 0; x < width; ++x)
			tetrominos[0][x] = Tetromino.Shape.SOLID;
		
		// Pick a random column to place the bomb.
		tetrominos[0][RND.nextInt(width)] = Tetromino.Shape.BOMB;
		
		boardGUI.repaint();
	}
	
	/** Try to move the current tetromino. 
	 * @param x - The new x coordinate.
	 * @param y - The new y coordinate.
	 * 
	 * @return Returns true if successful, else false.
	 */
	public boolean tryMove(int newX, int newY) {
		if (isCurrPieceInvalid())
			return false;
		
		if (game.state == Game.State.GAME_OVER) {
			return false;
		}

		int oldX = currX;
		int oldY = currY;

		currX = newX;
		currY = newY;

		if (!currPieceValidPosition()) {
			currX = oldX;
			currY = oldY;
			return false;
		}

		boardGUI.repaint();

		return true;
	}

	/**
	 * Clears all lines above or equal to the current piece that are full.
	 * @return The number of lines cleared.
	 */
	public int clearLines() {
		return clearLines(currY + currTetromino.getYMin());
	}
	
	/**
	 * Clears all lines above or equal to y that are full.
	 * @param yMin The lower bound.
	 * @return The number of lines cleared.
	 */
	public int clearLines(int yMin) {
		if (!validPosition(0, yMin))
			return 0;
		
		int nLinesCleared = 0;
		for (int y = height - 1; y >= yMin; --y) {
			if (isLineFull(y)) {
				collapseLine(y);
				++nLinesCleared;
			}
		}

		return nLinesCleared;
	}

	/**
	 * Checks if the specified line is full.
	 * @param y The line.
	 * @return True if line is full, otherwise false.
	 */
	private boolean isLineFull(int y) {
		for (int x = 0; x < width; ++x) {
			if (tetrominos[y][x].empty())
				return false;
		}

		return true;
	}

	/**
	 * 
	 * @param y The line.
	 * @return True if line is full, otherwise false.
	 */
	private boolean isLineEmpty(int y) {
		for (int x = 0; x < width; ++x) {
			if (!tetrominos[y][x].empty())
				return false;
		}

		return true;
	}

	/**
	 * 
	 * @param y - All lines above y will fall down by 1 and y will be replaced.
	 */
	private void collapseLine(int y) {
		while (y < height - 1) {
			replaceLine(y);
			if (isLineEmpty(y))
				break;

			++y;
		}
	}

	/**
	 * 
	 * @param y - Line y will be replaced by the one above it. If y is the top line, then just empty the line.
	 */
	private void replaceLine(int y) {
		if (y == height - 1) {
			for (int x = 0; x < width; ++x)
				tetrominos[y][x] = Tetromino.Shape.EMPTY;

			return;
		}

		for (int x = 0; x < width; ++x)
			tetrominos[y][x] = tetrominos[y + 1][x];
	}

	/** Try to move the current tetromino down by one.
	 * It will also get the next piece if the current piece can't be shifted down.
	 * If the next piece can't be placed on the board, the "lost" flag is set to true.
	 * This function will also advance the player's level if necessary.
	 * 
	 * @return The number of lines cleared.
	 */
	public int shiftDownNewPiece() {
		if (game.state == Game.State.GAME_OVER)
			return 0;

		if (isCurrPieceInvalid())
			return 0;

		if (!tryMove(currX, currY - 1)) {
			int numLinesJustCleared = solidifyAndClearLines();
			if (game.state == Game.State.GAME_OVER)
				return numLinesJustCleared;
			
			if (nextTetromino() == false) {
				game.state = Game.State.GAME_OVER;
				this.lost = true;
			}
			
			return numLinesJustCleared;
		}

		return 0;
	}

	/**
	 * Looks at the current piece and checks if it is touching a bomb
	 * underneath it. If there is then the bomb line underneath will be cleared.
	 * 
	 * @return Number of lines cleared (max of 1).
	 */
	private int checkAndClearBombLine() {
		if (isCurrPieceInvalid())
			return 0;
		
		int tetrominoYMin = currTetromino.getYMin();
		
		Pos[] coords = currTetromino.getCoords();
		for (Pos pos : coords) {
			if (pos.y == tetrominoYMin) {
				int checkBombX = currX + pos.x;
				int checkBombY = currY + pos.y - 1;
				if (validPosition(checkBombX, checkBombY) && tetrominos[checkBombY][checkBombX] == Tetromino.Shape.BOMB) {
					collapseLine(checkBombY);
					--currY;
					game.audio.playBomb();
					return 1;
				}
			}
		}
		
		return 0;
	}
	
	/**
	 * Solidifies the current piece in stone onto the board (like a gargoyle),
	 * so it can't be moved around anymore.
	 *
	 * @return The number of lines cleared.
	 */
	private int solidifyAndClearLines() {
		if (isCurrPieceInvalid())
			return 0;
		
		Pos[] coords = currTetromino.getCoords();
		for (int i = 0; i < coords.length; ++i)
			tetrominos[currY + coords[i].y][currX + coords[i].x] = currTetromino.shape;

		int numLinesJustCleared = checkAndClearBombLine(); 
		numLinesJustCleared += clearLines(currY + currTetromino.getYMin());
		invalidateCurrPiece();

		if (numLinesJustCleared == 0) {
			game.audio.playSolidify();
			
			// If no line was cleared, cancel the combo.
			this.numCombos = 0;
		} else if (this.numLinesCleared > 0) {
			// Here we are continuing a combo.
			this.numCombos += numLinesJustCleared;
		} else {
			// Here we are starting a new combo. I.e. this.numLinesCleared == 0.
			this.numCombos += numLinesJustCleared - 1;
		}

		this.totalLinesCleared += numLinesJustCleared;
		this.numLinesCleared = numLinesJustCleared;

		switch (game.gameOptions.mode) {
			case LEVEL:
				// Update player's level.	
				player.numLinesClearedThisLevel += numLinesJustCleared;
				if (player.numLinesClearedThisLevel >= player.level)
					player.advanceLevel();
				
				break;
			case FIGHT:
				// Add bomb lines to other players.
				this.attacks = (numLinesJustCleared + this.numCombos) / 3;
				for (int i = 0; i < attacks; ++i)
					game.attackOtherPlayers(player);
				
				break;
			case RUSH:
				// Check if rush limit has been reached.
				if (this.totalLinesCleared >= game.gameOptions.rushLimit) {
					game.state = Game.State.GAME_OVER;
					game.setWinningPlayer(this.player);
				}
				
				break;
			case TIMED:
				break;
			default:
				break;
				
		}
		
		// Update player score based on lines and combos here.
		this.player.score.lines(numLinesJustCleared);
		this.player.score.combos(this.numCombos);
		if (checkPerfectClear())
			this.player.score.perfectClear();

		if (numLinesJustCleared > 0)
			game.audio.playClear(numLinesJustCleared);

		return numLinesJustCleared;
	}

	/** Try to move the current tetromino right by one.
	 * 
	 * @return False if can't, otherwise true.
	 */
	public boolean shiftRight() {
		if (isCurrPieceInvalid())
			return false;
		
		return tryMove(currX + 1, currY);
	}

	/** Try to move the current tetromino left by one.
	 * 
	 * @return False if can't, otherwise true.
	 */
	public boolean shiftLeft() {
		if (isCurrPieceInvalid())
			return false;
		
		return tryMove(currX - 1, currY);
	}

	/** Try to rotate the current tetromino left.
	 * 
	 * @return False if can't, otherwise true.
	 */
	public boolean rotateLeft() {
		if (isCurrPieceInvalid())
			return false;
		
		currTetromino.rotateLeft();
		// Note: this doesn't actually try to move the piece. It just checks if the rotation is valid.
		if (!tryMove(currX, currY)) {
			currTetromino.rotateRight();
			return false;
		}

		return true;	
	}

	/** Try to rotate the current tetromino right.
	 * 
	 * @return False if can't, otherwise true.
	 */
	public boolean rotateRight() {
		if (isCurrPieceInvalid())
			return false;
		
		currTetromino.rotateRight();
		// Note: this doesn't actually try to move the piece. It just checks if the rotation is valid.
		// Then why? Code re-use.
		if (!tryMove(currX, currY)) {
			currTetromino.rotateLeft();
			return false;
		}

		return true;	
	}
	
	public boolean tryRotate(int rotationIndex) {
		if (isCurrPieceInvalid())
			return false;
		
		int oldRotationIndex = currTetromino.rotationIndex;
		currTetromino.rotationIndex = rotationIndex;
		if (tryMove(currX, currY))
			return true;
		
		currTetromino.rotationIndex = oldRotationIndex;
		return false;
		
	}

	/**
	 * Checks if the (x, y) coord is a valid position on the board.
	 * 
	 * @param x The x coordinate to check.
	 * @param y The y coordinate to check.
	 * 
	 * @return true if valid, else false.
	 */
	private boolean validPosition(int x, int y) {
		return x >= 0 && y >= 0 && x < width && y < height;
	}

	/**
	 * Checks if the current tetromino is in a valid position on the board.
	 * Useful for checking if a move/rotation was successful or not.
	 * 
	 * @return true if the tetromino position is valid, otherwise false.
	 */
	public boolean currPieceValidPosition() {
		if (isCurrPieceInvalid())
			return false;

		Pos[] positions = currTetromino.getCoords();
		if (positions == null)
			return false;

		for (int i = 0; i < positions.length; ++i) {
			int x = currX + positions[i].x;
			int y = currY + positions[i].y;
			if (!validPosition(x, y) || tetrominos[y][x] != Tetromino.Shape.EMPTY)
				return false;
		}

		return true;
	}
	
	/**
	 * Checks if the board is perfectly cleared.
	 * @return true if the whole board is empty, else false.
	 */
	private boolean checkPerfectClear() {
		for (int y = height - 1; y >= 0; --y) {
			for (int x = 0; x < width; ++x) {
				if (tetrominos[y][x] != Tetromino.Shape.EMPTY)
					return false;
			}
		}
		
		return true;
	}

	public void print() {
		System.out.print(" ");
		for (int x = 0; x < width; ++x)
			System.out.print("_");

		System.out.println();

		int index = 0;
		Pos[] positions = currTetromino == null ? null : currTetromino.getCoords();

		for (int y = height - 1; y >= 0; --y) {
			System.out.print("|");
			for (int x = 0; x < width; ++x) {
				String str;
				if (!lost && positions != null &&
						index < positions.length &&
						positions[index].equals(x - currX, y - currY)) {
					str = "X";
					++index;
				} else {
					str = tetrominos[y][x].empty() ? "." : "x";
				}

				System.out.print(str);
			}

			System.out.println("|");
		}

		System.out.print(" ");
		for (int x = 0; x < width; ++x)
			System.out.print("^");

		System.out.println();
	}

	private void invalidateCurrPiece() {
		currTetromino = null;
		currX = -1;
		currY = -1;
	}
	
	private boolean isCurrPieceInvalid() {
		return currTetromino == null;
	}
	
	// try move but don't actually move it.
	public boolean canMoveTo(int newX) {
        boolean possible = true;
        int oldX = currX;
        
        while (possible && currX != newX) {
        	if (currX < newX)
        		++currX;
        	else
        		--currX;
        
        	if (!tryMove(currX, currY))
        		possible = false;
        }
        
        currX = oldX;
        
        return possible;
}

	public int getCHeight(int x){
        	int CHeight = 0;
            for(int n = height - 1; n >= 0; --n){
                    if(!tetrominos[n][x].empty() && CHeight < n){
                            CHeight = n;
                    }
            }
            return CHeight;
        }

        //calculate the column height
        public int getColumnHeight(int x){
        	Pos[] coords = currTetromino.getCoords();
            for (int i = 0; i < coords.length; ++i)
                    tetrominos[currY + coords[i].y][currX + coords[i].x] = currTetromino.shape;
        	int CHeight = 0;
        	for(int n = height - 1; n >= 0; --n){
        		if(!tetrominos[n][x].empty() && CHeight < n){
        			CHeight = n;
        		}
        	}
        	return CHeight + 1;
        }
        
		// calculate the max height
        public int getMaxHeight(){
        	int max = 0;
        	for(int x = 0; x < width; ++x){
        		if(getColumnHeight(x) > max){
        			max = getColumnHeight(x);
        		}
        	}
        	return max;
        }
		
		
        //calculate the column hole numbers
        public int getColumnHoles(int x){
        	Pos[] coords = currTetromino.getCoords();
            for (int i = 0; i < coords.length; ++i)
                    tetrominos[currY + coords[i].y][currX + coords[i].x] = currTetromino.shape;
        	int hole = 0;
        	if(getColumnHeight(x) == 0)	return 0;
        	else{
        		for(int m = getColumnHeight(x) - 1; m >= 0; --m){
        	
        			if(tetrominos[m][x].empty()){
        				hole++;
        			}
        		}
        	}
        	return hole;
        }
        
		//calculate the max hole numbers
        public int getMaxHoles(){
        	int holes = 0;
        	for(int x = 0; x < width; ++x){
        		holes = holes + getColumnHoles(x);
        	}
        	return holes;
        }
        
		// calculate each column blockages
        public int getColumnBlockages(int x){
        	Pos[] coords = currTetromino.getCoords();
            for (int i = 0; i < coords.length; ++i)
                    tetrominos[currY + coords[i].y][currX + coords[i].x] = currTetromino.shape;
        	int blockage = 0;
        	if(getColumnHoles(x) > 0){
        		int lowestHoleY = height - 1;
        		for(int n = height - 1; n >= 0; --n){
        			if(tetrominos[n][x].empty()){
        				lowestHoleY = n;
        			}
        		}
        		for(int m = height - 1; m > lowestHoleY; --m){
        			if(!tetrominos[m][x].empty()){
        				blockage++;
        			}
        		}
        	}
        	return blockage;
        }
        
		//calculate the current blockages
        public int getMaxBloackages(){
        	int blockages = 0;
        	for(int x = 0; x < width; ++x){
        		blockages = blockages + getColumnBlockages(x);
        	}
        	return blockages;
        }
        
		//calculate the current bumpiness
        public int getBumpiness(){
        	int bumpiness = 0;
        	for(int x = 0; x < width - 1; ++x){
        		bumpiness = bumpiness + Math.abs(getColumnHeight(x) - getColumnHeight(x+1));
        	}
        	return bumpiness;
        }
        
		//calculate the current well numbers
        public int getWells(){
        	int wells = 0;
        	for(int x = 0; x < width - 1; ++x){
        		if(x == 0){
        			if(getColumnHeight(1) - getColumnHeight(0) > 2)	wells++;
        		}
        		else if(x == width - 1){
        			if(getColumnHeight(width - 2) - getColumnHeight(x) > 2)	wells++;
        		}
        		else{
        			if((getColumnHeight(x+1) - getColumnHeight(x) > 2) && (getColumnHeight(x-1) - getColumnHeight(x) > 2))	wells++;
        		}
        	}
        	return wells;
        }
}
