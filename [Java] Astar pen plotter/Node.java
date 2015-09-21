import java.util.ArrayList;

/**
 * This is the Node class
 * @author Nelson Wee 
 * nwee991, z3352078	
 */
public class Node {
	private int x;
	private int y;
	private ArrayList<Edge> edges;
	
	public Node(int x, int y){
		this.x = x;
		this.y = y;
		edges = new ArrayList<Edge>();
	}
	
	/**
	 * This method returns the x value of the node
	 * @return int x
	 */
	public int getX(){
		return x;
	}
	
	/**
	 * This method returns the y value of the node
	 * @return int y
	 */
	public int getY(){
		return y;
	}
	
	/**
	 * This method adds an edge to this Node
	 * @param to
	 * @param zeroCost
	 */
	public void addEdgePath(Node to, double zeroCost) {
		edges.add(new Edge(this,to,zeroCost));
	}
	
	/**
	 * This method returns an ArrayList of connected edges to this node
	 * @return edges
	 */
	public ArrayList<Edge> getEdge(){
		return edges;
	}
}
