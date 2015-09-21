import java.util.ArrayList;

/**
 * @author Nelson Wee 
 * nwee991, z3352078	
 */
public interface Graph {
	/**
	 * Adds Edges for lines (jobs) into a list, used for confirming end state of the A* search
	 * @param from
	 * @param to
	 */
	public void addJob(Node from, Node to) ;
	
	/**
	 * Adds node to a list, if its unique
	 * @param n
	 */
	public void addNode(Node n);
	
	/**
	 * After input, this method links all the nodes with each other, connecting the graph
	 */
	public void addEdges();
	
	/**
	 * Searches the nodes list to check if the requested node exists already
	 * @param n
	 * @return boolean, if the node is in the list or not
	 */
	public boolean checkNode(Node n);
	
	/**
	 * Calculates the direct line cost between the two nodes 
	 * @param from Node
	 * @param to Node
	 * @return a double value of the line distance between the nodes
	 */
	public double getCost(Node from, Node to);
	
	/** 
	 * @param e Edge
	 * @param j ArrayList of Jobs
	 * @return Return true if Edge e is inside the ArrayList j
	 */
	public boolean edgeInList(Edge e, ArrayList<Edge> j);

	
	/** 
	 * @param a
	 * @param b
	 * @return boolean, if Edges a and b have the same associated nodes in either direction
	 */
	public boolean compareEdge(Edge a, Edge b);
	
	/**
	 * This heuristic takes into account the "moving" edges cost to the nearest "job" edge and 
	 * utilizes the remaining uncompleted totalJobCost as the TrueCost.
	 * If the edge is a "moving" edge it returns the cost of the edge. 
	 * If the edge is a "Job" edge then it returns 0.
	 * TotalCost = TotalCost + hCost and TrueCost <= TotalCost 
	 * Hence, its estimate of the TrueCost is less than the TotalCost making the heuristic admissible. 
	 * @param s SearchState
	 * @param e Edge
	 * @return "Moving" edge: edgeCost "Job" edge: 0   
	 */
	public double heuristicCost(SearchState s, Edge e);
		
	/**
	 * The A* to find the optimal path;
	 */
	public void aStarPathFind()throws Exception;	
		
	/**
	 * Helper method to print the edges connected to each node
	 */
	public void printNodeEdges();
}
