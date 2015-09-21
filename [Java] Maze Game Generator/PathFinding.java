import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Stack;

/**
* This class is to find the shortest path
* 
* @author Nelson Wee, z3352078
* @author Renmark Marte, z3464929
* @author Sung Min Park, z3278712
* @author Luna Pradhananga, z3358423 
*
*/
public class PathFinding {

	public PathFinding() {
		queue = new ArrayDeque<Cell>();
		visited = new ArrayDeque<Cell>();
		path = new Stack<Cell>();
		parent = new HashMap<Cell, Cell>();
		neighbours = new ArrayList<Cell>();
		four_directions = new ArrayList<Cell>();
		possible_path = new ArrayDeque<Cell>();
		final_path = new Stack<Cell>();
	}

	
	
	/**
	 * Finding the shortest path from starting point to ending point
	 * @param maze maze in binary format
	 * @param start starting coordination
	 * @param end ending coordination
	 */
	public void path(int[][] maze, Cell start, Cell end, int Row, int Col) {
		
		for (int i = 0; i < maze.length; i++) {
			for (int j = 0; j < maze.length; j++) {
				if (maze[i][j] == 0) {
					possible_path.add(new Cell(i, j, false));
				}
			}
		}

		Cell current = start;
		queue.addLast(current);
		visited.add(current);
		possible_path.remove(start);
		while (!queue.isEmpty()) {
			current = queue.pop();
			neighbours = getNeighbours(current);
			for (Cell ngs : neighbours) {
				if (!hasVisited(ngs.getX(), ngs.getY())) {

					queue.addLast(ngs);
					visited.add(ngs);
					possible_path.remove(current);
					parent.put(ngs, current);
				}
			}

			if (current.getX() == end.getX() && current.getY() == end.getY()) {
				System.out.println("target");
				break;
			}
		}
		
		current = end;
		final_path.add(current);

		while(current!=start){
			sleep++;
			current = findValues(current);
			System.out.println(current);
			final_path.add(current);
			if(sleep > 150){
				new MapGenerator(Row, Col);
			}
		}
		Collections.reverse(final_path);
	}

	
	/**
	 * 
	 * @return final_path for painting
	 */
	public Stack<Cell> getPath(){
		return final_path;
	}
	
	
	/**
	 * Grab values corresponding to the current position from back-tracking
	 * @param current current position
	 * @return
	 */
	private Cell findValues(Cell current){
		Cell temp = new Cell(0,0,false);
		Cell found = new Cell(0,0,false);
		for(Cell ngs : parent.keySet()){
			if(ngs.getX() == current.getX() && ngs.getY() == current.getY()){
				temp = parent.get(ngs);
				found = ngs;
			}
		}
		parent.remove(found);
		return temp;
	}
	
	
	/**
	 * Check four different directions of the current position from possible_path, if exists
	 * add into neighbours
	 * @param current current position
	 * @return  neighbours of the current position
	 */
	public ArrayList<Cell> getNeighbours(Cell current) {
		
		if (contains(current.getX(), current.getY()-1)) {
			Cell n = new Cell(current.getX(), current.getY()-1, false);
			neighbours.add(n);
			possible_path.remove(n);
		}
		
		

		if (contains(current.getX(), current.getY()+1)) {
			Cell n = new Cell(current.getX(), current.getY()+1, false);
			neighbours.add(n);
			possible_path.remove(n);
		}
		

		if (contains(current.getX()-1, current.getY())) {
			Cell n = new Cell(current.getX()-1, current.getY(), false);
			neighbours.add(n);
			possible_path.remove(n);
		}
		

		if (contains(current.getX()+1, current.getY())) {
			Cell n = new Cell(current.getX()+1, current.getY(), false);
			neighbours.add(n);
			possible_path.remove(n);
		}

		return neighbours;

	}
	
	
	/**
	 * Check if the coordinates are existing in the possible_path
	 * @param x x coordinate
	 * @param y y coordinate
	 * @return true if exists whereas false
	 */
	private boolean contains(int x, int y) {
		for (Cell c: possible_path) {
			if ((c.getX() == x) && (c.getY()==y))
			return true;
		}
			return false;
		
	}

	
	/**
	 * Check if the coordinates are existing in the visited
	 * @param x x x coordinate
	 * @param y y y coordinate
	 * @return true if exists whereas false
	 */
	private boolean hasVisited(int x, int y) {
		for (Cell c: visited) {
			if ((c.getX() == x) && (c.getY()==y))
			return true;
		}
			return false;
		
	}
	
	
	/**
	 * Check details of the start and end point
	 * @param start position
	 * @param end position
	 * @return ture if its equal whereas false
	 */
	private boolean equals(Cell start, Cell end) {
		if (start.getY() == end.getX() && start.getX() == start.getY())
			return true;
		else
			return false;
	}

	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
		
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((parent == null) ? 0 : parent.hashCode());
		result = prime * result + ((path == null) ? 0 : path.hashCode());
		return result;
	}

	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PathFinding other = (PathFinding) obj;
		if (parent == null) {
			if (other.parent != null)
				return false;
		} else if (!parent.equals(other.parent))
			return false;
		if (path == null) {
			if (other.path != null)
				return false;
		} else if (!path.equals(other.path))
			return false;
		return true;
	}



	private int sleep = 0;
	ArrayDeque<Cell> possible_path;
	Stack<Cell> path;
	Stack<Cell> final_path;
	ArrayDeque<Cell> visited;
	ArrayList<Cell> neighbours;
	ArrayList<Cell> four_directions;
	HashMap<Cell, Cell> parent;
	ArrayDeque<Cell> queue;
}