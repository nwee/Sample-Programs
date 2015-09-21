/**
 * This is the Edge class, it stores both the "from" and "to" Nodes along with the cost between them 
 * @author Nelson Wee 
 * nwee991, z3352078	
 */
public class Edge {
	private Node to;
	private Node from;
	private double cost;
	
	public Edge(Node from, Node to, double zeroCost){
		this.from = from;
		this.to = to;
		this.cost = zeroCost;
	}
	
	/**
	 * This method returns the "from" node 
	 * @return from
	 */
	public Node getFrom(){
		return from;
	}
	
	/**
	 * This method returns the "to" node 
	 * @return to
	 */
	public Node getTo(){
		return to;
	}
	
	/**
	 * This method returns the cost of the edge 
	 * @return cost
	 */
	public double getCost(){
		return cost;
	}
}
