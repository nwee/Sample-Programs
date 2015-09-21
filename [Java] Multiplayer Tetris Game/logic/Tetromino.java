package logic;

import helper.Pos;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class Tetromino {
	final static Random RND = new Random();
	final static private int NUM_ROTATIONS = 4;

	public int rotationIndex = 0; // 0 = default shape. 1 = rotated clockwise 90 deg. 2 = rotated 180 deg.
	public Shape shape = Shape.EMPTY;
	Pos boardPos;

	public enum Shape {
		EMPTY ('.'),
		
		// PICKABLE SHAPES
		I ('I'),
		J ('J'),
		L ('L'),
		O ('O'),
		S ('S'),
		T ('T'),
		Z ('Z'),
		// END PICKABLE SHAPES
		
		SOLID ('@'),
		BOMB ('B')
		
		;
		
		private final char letter;
		
		Shape(char letter) {
			this.letter = letter;
		};
		
		public char getLetter() {
			return letter;
		}
		
		public boolean empty() {
			return this.ordinal() == 0;
		}
	}
	
	// Coords must be ordered by decreasing y then increasing x.
	private final static Pos[][][] ALL_COORDS = {
		{
			// Empty.
			{ new Pos(0, 0), new Pos(0, 0), new Pos(0, 0), new Pos(0, 0) },
			{ new Pos(0, 0), new Pos(0, 0), new Pos(0, 0), new Pos(0, 0) },
			{ new Pos(0, 0), new Pos(0, 0), new Pos(0, 0), new Pos(0, 0) },
			{ new Pos(0, 0), new Pos(0, 0), new Pos(0, 0), new Pos(0, 0) },
		},
		{
			// I shape.
			{ new Pos(+0, +2), new Pos(+0, +1), new Pos(+0, +0), new Pos(+0, -1) },
			{ new Pos(-1, +0), new Pos(+0, +0), new Pos(+1, +0), new Pos(+2, +0) },
			{ new Pos(+0, +1), new Pos(+0, +0), new Pos(+0, -1), new Pos(+0, -2) },
			{ new Pos(-2, +0), new Pos(-1, +0), new Pos(+0, +0), new Pos(+1, +0) },
		},
		{
			// J shape.
			{ new Pos(+0, +1), new Pos(+0, +0), new Pos(-1, -1), new Pos(+0, -1) },
			{ new Pos(-1, +1), new Pos(-1, +0), new Pos(+0, +0), new Pos(+1, +0) },
			{ new Pos(+0, +1), new Pos(+1, +1), new Pos(+0, +0), new Pos(+0, -1) },
			{ new Pos(-1, +0), new Pos(+0, +0), new Pos(+1, +0), new Pos(+1, -1) },
		},
		{
			// L shape.
			{ new Pos(+0, +1), new Pos(+0, +0), new Pos(+0, -1), new Pos(+1, -1) },
			{ new Pos(-1, +0), new Pos(+0, +0), new Pos(+1, +0), new Pos(-1, -1) },
			{ new Pos(-1, +1), new Pos(+0, +1), new Pos(+0, +0), new Pos(+0, -1) },
			{ new Pos(+1, +1), new Pos(-1, +0), new Pos(+0, +0), new Pos(+1, +0) },
		},
		{
			// O shape.
			{ new Pos(+0, +1), new Pos(+1, +1), new Pos(+0, +0), new Pos(+1, +0) },
			{ new Pos(+0, +1), new Pos(+1, +1), new Pos(+0, +0), new Pos(+1, +0) },
			{ new Pos(+0, +1), new Pos(+1, +1), new Pos(+0, +0), new Pos(+1, +0) },
			{ new Pos(+0, +1), new Pos(+1, +1), new Pos(+0, +0), new Pos(+1, +0) },
		},
		{
			// S shape.
			{ new Pos(-1, +1), new Pos(-1, +0), new Pos(+0, +0), new Pos(+0, -1) },
			{ new Pos(+0, +1), new Pos(+1, +1), new Pos(-1, +0), new Pos(+0, +0) },
			{ new Pos(+0, +1), new Pos(+0, +0), new Pos(+1, +0), new Pos(+1, -1) },
			{ new Pos(+0, +0), new Pos(+1, +0), new Pos(-1, -1), new Pos(+0, -1) },
		},
		{
			// T shape.
			{ new Pos(+0, +1), new Pos(-1, +0), new Pos(+0, +0), new Pos(+1, +0) },
			{ new Pos(+0, +1), new Pos(+0, +0), new Pos(+1, +0), new Pos(+0, -1) },
			{ new Pos(-1, +0), new Pos(+0, +0), new Pos(+1, +0), new Pos(+0, -1) },
			{ new Pos(+0, +1), new Pos(-1, +0), new Pos(+0, +0), new Pos(+0, -1) },
		},
		{
			// Z shape.
			{ new Pos(+1, +1), new Pos(+1, +0), new Pos(+0, +0), new Pos(+0, -1) },
			{ new Pos(-1, +0), new Pos(+0, +0), new Pos(+0, -1), new Pos(+1, -1) },
			{ new Pos(+0, +1), new Pos(-1, +0), new Pos(+0, +0), new Pos(-1, -1) },
			{ new Pos(-1, +1), new Pos(+0, +1), new Pos(+0, +0), new Pos(+1, +0) },
		},
	};

	public Tetromino() {
		this(Shape.EMPTY);
	}
	
	public Tetromino(Shape shape) {
		this.shape = shape;
	}
	
	/**
	 * Returns a Tetromino object that has a random shape and rotation.
	 * 
	 */
	public static Tetromino makeRandomTetromino() {
		// Choose a random tetromino shape.
		Shape[] shapes = Arrays.copyOfRange(Shape.values(), 1, 7);
		Shape shape = shapes[RND.nextInt(shapes.length)];
		Tetromino tetromino = new Tetromino(shape);
		
		// Randomly rotate the tetromino to spice things up.
		tetromino.rotationIndex = RND.nextInt(NUM_ROTATIONS);
		
		return tetromino;
	}

	/**
	 * Returns a Tetromino object that has a random shape not currently in the provided list of tetrominos.
	 * @param tetrominoQ - The list of tetrominos currently in the queue. 
	 */
	public static Tetromino makeRandomTetromino(List<Tetromino> tetrominoQ) {
		List<Shape> shapeQ = new LinkedList<Shape>();
		for (Tetromino t : tetrominoQ)
			shapeQ.add(t.shape);
		
		// Choose a random tetromino shape not already in the queue.
		Shape[] pickableShapeArray = Arrays.copyOfRange(Shape.values(), 1, 7);
		List<Shape> pickableShapes = new LinkedList<Shape>(Arrays.asList(pickableShapeArray));
		pickableShapes.removeAll(shapeQ);
		
		// Pick a random shape index between 1 and 7.
		Shape shape = pickableShapes.get(RND.nextInt(pickableShapes.size()));
		Tetromino tetromino = new Tetromino(shape);
		
		// Randomly rotate the tetromino to spice things up.
		tetromino.rotationIndex = RND.nextInt(NUM_ROTATIONS);
		
		return tetromino;
	}
	
	/**
	 * @return The relative coordinates of this tetromino.
	 */
	public Pos[] getCoords() {
		return ALL_COORDS[shape.ordinal()][rotationIndex];
	}
	
	/**
	 * Returns the relative coordainates of this tetromino shifted so that
	 * no values are negative. Useful for drawing a tetromino at a particular point
	 * starting at the top-left.
	 */
	public Pos[] getCoordsPositive() {
		// Do not edit the actual coordinates. Instead make a copy.
		Pos[] coords = getCoords().clone();
		final int xMin = getXMin();
		final int yMin = getYMin();
		
		if (xMin < 0) {
			final int xMinAbs = Math.abs(xMin);
			for (Pos pos : coords)
				pos.x += xMinAbs;
		}
		if (yMin < 0) {
			final int yMinAbs = Math.abs(yMin);
			for (Pos pos : coords)
				pos.y += yMinAbs;
		}
		
		return coords;
	}
	
	/**
	 * @return The minimum distance from the top of the board that this tetromino can be placed.
	 */
	public int getYMax() {
		// Be clever and use the fact that the array is sorted by descending y.
		return getCoords()[0].y;
	}
	
	/**
	 * @return The lowest point of this piece.
	 */
	public int getYMin() {
		// Be clever and use the fact that the array is sorted by descending y.
		Pos[] coords = getCoords();
		return coords[coords.length - 1].y;
	}
	
	/**
	 * Gets the left most relative x co-ordinate of this piece.
	 */
	public int getXMin() {
		Pos[] coords = getCoords();
		int xMin = coords[0].x;
		for (Pos pos : coords)
			xMin = Math.min(xMin, pos.x);
		
		return xMin;
	}

	/**
	 * Gets the right most relative x co-ordinate of this piece.
	 */
	public int getXMax() {
		Pos[] coords = getCoords();
		int xMax = coords[0].x;
		for (Pos pos : coords)
			xMax = Math.max(xMax, pos.x);
		
		return xMax;
	}
	
	/**
	 * Calculates the height of this rotated tetromino.
	 */
	public int getHeight() {
		return getYMax() - getYMin() + 1;
	}
	
	/**
	 * Calculates the width of this rotated tetromino.
	 */
	public int getWidth() {
		return getXMax() - getXMin() + 1;
	}
	
	// This is for debugging only.
	public void print() {
		Pos[] coords = getCoords();
		
		int index = 0;
		for (int y = 2; y >= -2; --y) {
			for (int x = -2; x <= 2; ++x) {
				if (x == 0 && y == 0) {
					System.out.print("X");
					++index;
				} else if (index < coords.length && coords[index].equals(x, y)) {
					System.out.print("x");
					++index;
				} else {
					System.out.print(".");
				}
			}
			System.out.println();
		}
	}
	
	public void rotateLeft() {
		rotationIndex = (NUM_ROTATIONS + rotationIndex - 1) % NUM_ROTATIONS;
	}
	
	public void rotateRight() {
		rotationIndex = (NUM_ROTATIONS + rotationIndex + 1) % NUM_ROTATIONS;
	}
	public int[] findMaxValue(Pos[] pos){
    	int maxX = -4;
    	int maxY = -4;
    	for(int n = 0; n < 4; n++){
    		if(pos[n].x > maxX)	maxX = pos[n].x;
    		if(pos[n].y > maxY)	maxY = pos[n].y;
    	}
    	int[] max = {maxX, maxY};
    	return max;
    }
    
    public int[] findMinValue(Pos[] pos){
    	int minX = 4;
    	int minY = 4;
    	for(int n = 0; n < 4; n++){
    		if(pos[n].x < minX)	minX = pos[n].x;
    		if(pos[n].y < minY)	minY = pos[n].y;
    	}
    	int[] min = {minX, minY};
    	return min;
    }
}
