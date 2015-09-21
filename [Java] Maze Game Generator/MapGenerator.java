import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.Stack;

/**
* This class is to generate the maze
* 
* @author Nelson Wee, z3352078
* @author Renmark Marte, z3464929
* @author Sung Min Park, z3278712
* @author Luna Pradhananga, z3358423 
*
*/
public class MapGenerator {
	public MapGenerator(int row, int col) {
		MapGenerator.MAPSIZE = row+col+1;
		cell = new Cell[MAPSIZE][MAPSIZE];
		maze = new int[MAPSIZE][MAPSIZE];
		hint = new PathFinding();
		list = new Stack<Cell>();
		initialize(); 
		generateMap();
	}

	
	/**
	 * This method initializes cell and boundary
	 */
	private void initialize() {
		for (int i = 0; i < MAPSIZE; i++) {
			for (int j = 0; j < MAPSIZE; j++) {
				cell[i][j] = new Cell(i, j, false);
			}
		}
		
		for (int i = 0; i < MAPSIZE; i++) {
			maze[i][MAPSIZE - 1] = 1; 
			maze[i][0] = 1; 
			maze[MAPSIZE - 1][i] = 1;
			maze[0][i] = 1;
		}
	}

	
	/**
	 * Check neighbours if the coordinates satisfies the conditions, add into the stack
	 * @param randX x coordinate
	 * @param randY y coordinate
	 */
	private void addNeighbour(int randX, int randY) {
		if (randX + 1 != MAPSIZE) {
			list.add(cell[randX][randY + 1]);
		}
		if (randX - 1 != 0) {
			list.add(cell[randX][randY - 1]);
		}
		if (randY + 1 != MAPSIZE) {
			list.add(cell[randX + 1][randY]);
		}
		if (randY - 1 != 0) {
			list.add(cell[randX - 1][randY]);
		}
	}

	
	
	/**
	 * Generate maze
	 */
	public void generateMap() {
	
		int randX = (int) (Math.random() * MAPSIZE/2) * 2 + 1; 
		int randY = (int) (Math.random() * MAPSIZE/2) * 2 + 1; 
		
		try{								
			cell[randX][randY].setVisited(); 
			addNeighbour(randX, randY);
		}catch(Exception e){
			randX = (int) (Math.random() * MAPSIZE/2) * 2 + 1; 
			randY = (int) (Math.random() * MAPSIZE/2) * 2 + 1; 
			cell[randX][randY].setVisited(); 
			addNeighbour(randX, randY);
		}
		
		while (!list.isEmpty()) {
			Collections.shuffle(list.subList(0, list.size() - 1));
			Cell wall = list.pop();
			x = wall.getX();
			y = wall.getY();
			if (x % 2 == 1) { 
				try{
					if (y - 1 != 0 && cell[x][y - 1].getVisited() == false && y  != MAPSIZE - 1 ) {
						cell[x][y - 1].setVisited();
						cell[x][y].setVisited();
						if (x - 1 != 0) {
							list.add(cell[x - 1][y - 1]);
						}
						if (x + 1 != 0) {
							list.add(cell[x + 1][y - 1]);
						}
						
					} else if (y + 1 <= MAPSIZE - 1 && cell[x][y + 1].getVisited() == false) {
						cell[x][y + 1].setVisited();
	
						cell[x][y].setVisited();
	
						if (x - 1 != 0) {
							list.add(cell[x - 1][y + 1]);
						}
						if (x + 1 != 0) {
							list.add(cell[x + 1][y + 1]);
						}

					}
				}catch(Exception e){
					generateMap();
		        }	
					
			} else {
				try{
					if (x - 1 != 0 && cell[x - 1][y].getVisited() == false &&  x  != MAPSIZE - 1) {
						cell[x - 1][y].setVisited();
						cell[x][y].setVisited();
						if (y - 1 != 0) {
							list.add(cell[x - 1][y - 1]);
						}
						if (y + 1 != 0) {
							list.add(cell[x - 1][y + 1]);
						}
					} else if (x + 1 <= MAPSIZE - 1 && cell[x + 1][y].getVisited() == false) {
						cell[x + 1][y].setVisited();
						cell[x][y].setVisited();
						if (y - 1 != 0) {
							list.add(cell[x + 1][y - 1]);
						}
						if (y + 1 != 0) {
							list.add(cell[x + 1][y + 1]);
						}
					}
				}catch(Exception e){
					generateMap();
		        }
			}
		}

		setMap();
		setStart(randY);
		setEnd(randX);
		
		hint.path(maze, start, end, MAPSIZE/2,MAPSIZE/2);
		
		
		
	}

	/**
	 * Set the maze
	 */
	private void setMap() {
		for (int i = 0; i < MAPSIZE; i++) {
			for (int j = 0; j < MAPSIZE; j++) {
				if (cell[i][j].getVisited() == false) {
					if (i % 2 == 1) {
						if (j % 2 == 0) {
							maze[i][j] = 1;
						}
					} else {
						maze[i][j] = 1;
					}
				}
			}
		}
	}

	
	/**
	 * Set a random starting point
	 * @param randY
	 */
	private void setStart(int randY) {
		int randomNum = 1 + (int) (Math.random() * 9);
		if (randY % randomNum == 0) {
			maze[0][randY] = 0;
			start = new Cell(0, randY, false);
		} else {
			maze[randY][0] = 0;
			start = new Cell(randY, 0, false);
		}
	}

	/**
	 * Set a random ending point, if starting point and end point are too closed, 
	 * set a new end point recursively
	 * @param randX
	 */
	private void setEnd(int randX) {
		int randomNum = 1 + (int) (Math.random() * 9);
		if (randX % randomNum == 0) {
			maze[MAPSIZE - 1][randX] = 0;
			end = new Cell(MAPSIZE - 1, randX, false);
		} else {
			maze[randX][MAPSIZE - 1] = 0;
			end = new Cell(randX, MAPSIZE - 1, false);
		}

		if (check_distnace(start, end) < 25) {
			setEnd(randX);
		}

	}
	/**
	 * Checking distance between starting point and end point
	 * @param start
	 * @param end
	 * @return
	 */
	private double check_distnace(Cell start, Cell end) {
		return Math.sqrt((Math.pow((start.getX() - end.getX()), 2))
				+ Math.pow((start.getY() - end.getY()), 2));
	}

	static int MAPSIZE;
	static Cell[][] cell;
	public static int[][] maze;
	public static Cell start;
	public static Cell end;
	PathFinding hint;
	private int x = 0;
	private int y = 0;
	Stack<Cell> list;
}