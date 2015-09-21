/**
* This class is a cell that used in MapGenerator
* 
* @author Nelson Wee, z3352078
* @author Renmark Marte, z3464929
* @author Sung Min Park, z3278712
* @author Luna Pradhananga, z3358423 
*
*/


public class Cell {

	public Cell(int x, int y, boolean visited) {
		this.x = x;
		this.y = y;
		this.visited = visited;
	}
	
	/**
	 * 
	 * @return x
	 */
	public int getX(){
		return x;
	}
	
	/**
	 * 
	 * @return y
	 */
	public int getY(){
		return y;
	}
	
	/**
	 * 
	 * @return visited
	 */
	public boolean getVisited(){
		return visited;
	}
	
	
	/**
	 * set visited
	 */
	public void setVisited() {
		visited = true;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
	
		Cell other = (Cell) obj;
		if (x != other.x)
			return false;
		if (y != other.y)
			return false;
		return true;
	}
	
	private int x;
	private int y;
	private boolean visited;
}