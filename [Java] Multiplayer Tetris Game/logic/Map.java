package logic;

public class Map {

	public final Tetromino.Shape[][][] maps;
	public final int numMaps = 6;
	
	public Map(int width, int height) {
		maps = new Tetromino.Shape[numMaps][height][width];
		
		for (int mapIndex = 0; mapIndex < numMaps; ++mapIndex) {
			for (int y = 0; y < height; ++y) {
				for (int x = 0; x < width; ++x) {
					maps[mapIndex][y][x] = Tetromino.Shape.EMPTY;
				}
			}
		}
		// Map 0 is always empty.
		
		// Set up the first map. -- smiley face
		maps[1][0][4] = Tetromino.Shape.O;
		maps[1][0][5] = Tetromino.Shape.O;
		maps[1][9][4] = Tetromino.Shape.O;
		maps[1][9][5] = Tetromino.Shape.O;
		for (int y = 1; y <= 8; y = y+7) {
			for (int x = 3; x <= 6; x++) {
			   maps[1][y][x] = Tetromino.Shape.O;
			}
		}	
		for (int y = 2; y <= 7; y = y+5) {
			for (int x = 2; x <= 7; x++) {
			   maps[1][y][x] = Tetromino.Shape.O;
			}
		}		
		for (int y = 3; y <= 6; y++) {
			for (int x = 1; x <= 8; x++) {
			   maps[1][y][x] = Tetromino.Shape.O;
			}
		}		
		maps[1][3][3] = Tetromino.Shape.I;
		maps[1][3][6] = Tetromino.Shape.I;
		maps[1][2][4] = Tetromino.Shape.I;
		maps[1][2][5] = Tetromino.Shape.I;
		maps[1][6][3] = Tetromino.Shape.I;
		maps[1][6][6] = Tetromino.Shape.I;		
		
		// Set up the second map. -- 2 blocks empty in the middle
		for (int y = 0; y < 3*height/4; ++y){
			for (int x = 0; x < width; ++x) {
				if (x != 4 && x != 5) {
					maps[2][y][x] = Tetromino.makeRandomTetromino().shape;
					maps[2][y][x] = Tetromino.makeRandomTetromino().shape;
				}
			}
		}
		
		// Set up the third map. -- tall buildings
		for (int y = 0; y < height/2; ++y){
			for (int x = 0; x < width; ++x) {
				if (x%2 == 0) {
					maps[3][y][x] = Tetromino.makeRandomTetromino().shape;
					maps[3][y][x] = Tetromino.makeRandomTetromino().shape;
				}
			}
		}
		
		// Set up the fourth map. -- table cloth
		for (int y = 0; y < height/2; ++y){
			for (int x = 0; x < width; ++x) {
				if ((x+y)%2 == 0) {
					maps[4][y][x] = Tetromino.Shape.I;
				}
			}
		}
	}
	
	public Tetromino.Shape[][] getMap(int mapIndex) {
		return maps[mapIndex];
	}
}
