import java.util.ArrayList;

/**
 * This is the SearchState class, with every new node the search moves to, a new SearchState is created. 
 * The searchState is then put into a priorityQueue which polls the searchState with the lowest cost
 * @author Nelson Wee 
 * nwee991, z3352078	
 */
public class SearchState implements Comparable<SearchState> {
	private double finalCost = 0;
	private ArrayList<Edge> pathTravelled;
	private Node currentNode;
	private ArrayList<Edge> jobList;	
	
	/** 
	 * @param node - The current node in the searchState 
	 * @param newPathSoFar - The path traveled
	 * @param total - the total heuristic cost, g + h
	 */
	public SearchState(Node node, ArrayList<Edge> newPathSoFar, double total){
		jobList = new ArrayList<Edge>();
		
		currentNode = node;
		pathTravelled = newPathSoFar;
		finalCost = total;
	}
	
	/**
	 * The initial list of jobs that need to be completed
	 * @param jobList 
	 */
	public void remJobList(ArrayList<Edge> jobList){
		this.jobList = jobList;
	}
	
	/** This method returns the unfinished jobList 
	 * @return 
	 */
	public ArrayList<Edge> getJobList(){
		return jobList;
	}
	
	/** This method returns the current accumulated cost of the searchState
	 * @return The total cost of the searchState;
	 */
	public double getAccumCost() {
		return finalCost;
	}

	/**
	 * Sets how the priority queue is ordered
	 */
	public int compareTo(SearchState otherState){
		if((this.getAccumCost() - otherState.getAccumCost() > 0)){
			return 1;
		}
		else{
			return -1;
		}
	}
		
	/**This method returns the current Node in the state
	 * @return the node of this state;
	 */
	public Node getCurrentNode() {
		return currentNode;
	}
	
	/**This method returns a the path traveled
	 * @return 
	 */
	public ArrayList<Edge> getPathTravelled() {
		return pathTravelled;
	}
}