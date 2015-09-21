import java.util.ArrayList;
import java.util.PriorityQueue;
import java.text.DecimalFormat;

/**
 * @author Nelson Wee 
 * nwee991, z3352078
 * This is a AdjacencyGraph with ArrayLists 	
 */
public class AdjacencyGraph implements Graph {	
	
	private ArrayList<Node> nodes;
	private ArrayList<Edge> jobList;
	private Node zeroNode;
	private ArrayList<Edge> firstStatePath;
	
	public AdjacencyGraph(){
		nodes = new ArrayList<Node>();
		jobList = new ArrayList<Edge>();
		zeroNode = new Node(0,0);
		nodes.add(zeroNode); //adds zero Node to list
		firstStatePath = new ArrayList<Edge>();
	}
	
	public void addJob(Node from, Node to) {
		Edge line = new Edge(from, to, getCost(from,to));
		jobList.add(line);
	}
	
	public void addNode(Node n) {
		//Only add the node if its unique
		if (!checkNode(n)) {
			nodes.add(n);
		}
	}
	
	public void addEdges() {
		//Goes through list of nodes 
		for(int count = 0; count< nodes.size(); count++){
			Node temp = nodes.get(count);
			//connects all nodes to the node minus the same node
			for(int count2 = 0; count2< nodes.size(); count2++){
				if (count!=count2) {
					double cost = getCost(temp, nodes.get(count2));
					temp.addEdgePath(nodes.get(count2), cost);
				}
			}
		}
	}
	
	public boolean checkNode(Node n) {
		if(!nodes.isEmpty()) {
			for(Node temp: nodes) {
				if ((temp.getX() == n.getX()&&(temp.getY() == n.getY()))){
					return true;
				}
			}
		}
		return false;
	}
	
	public double getCost(Node from, Node to){
		double xDiff = from.getX()-to.getX();
		double yDiff = from.getY()-to.getY();
		return (double) Math.sqrt((xDiff*xDiff)+(yDiff*yDiff));
	}
	
	public boolean edgeInList(Edge e, ArrayList<Edge> j){
		for(Edge temp: j) {
			if (compareEdge(e, temp)){
				return true;
			}
		}
		return false;
	}
	
	public boolean compareEdge(Edge a, Edge b) {
		//Compares the edges if they are exactly alike
		if(a.getFrom().getX()== b.getFrom().getX() && a.getFrom().getY()== b.getFrom().getY() 
				&& a.getTo().getX() == b.getTo().getX() && a.getTo().getY() == b.getTo().getY()) {
			return true;
		}
		//Compares the edges if they are inverted
		else if(a.getFrom().getX()== b.getTo().getX() && a.getFrom().getY()== b.getTo().getY() 
				&& a.getTo().getX() == b.getFrom().getX() && a.getTo().getY() == b.getFrom().getY()){
			return true;
		}
		return false;
	}

	public double heuristicCost(SearchState s, Edge e) {
		if(!edgeInList(e,s.getJobList())){
			return e.getCost();
		}
		else{
			return 0;
		}
	}
	
	@SuppressWarnings("unchecked")
	public void aStarPathFind()throws Exception{
		PriorityQueue<SearchState> toVisit = new PriorityQueue<SearchState>();
		
		double totalJobCost = 0;		
		for(Edge e2: jobList){
			totalJobCost = totalJobCost + e2.getCost();
		}
		SearchState firstState = new SearchState(zeroNode,firstStatePath,totalJobCost);
		
		ArrayList<Edge> tempJobList = (ArrayList<Edge>) jobList.clone();
		ArrayList<Edge> newPathSoFar;
		ArrayList<Edge> currentJobList;
		ArrayList<Edge> finalPath;
		
		firstState.remJobList(tempJobList);
		toVisit.add(firstState);
		SearchState currentState = toVisit.poll();
		
		//Keep searching until all "jobs" have been done
		while(currentState.getJobList().size()!=0){
			boolean visited = false;
			for(Edge e1: currentState.getCurrentNode().getEdge()){	
				visited = false;
				for(Edge e2: currentState.getPathTravelled()){ 
					if(compareEdge(e1,e2)){
						visited = true;            
					}                		 
			    }
				//If the edge has not been visited, add new searchState in priority queue
				if (!visited) {
					newPathSoFar = (ArrayList<Edge>) currentState.getPathTravelled().clone();
					newPathSoFar.add(e1);
					SearchState newState = new SearchState(e1.getTo(), newPathSoFar, currentState.getAccumCost() + heuristicCost(currentState,e1));	
					currentJobList = (ArrayList<Edge>) currentState.getJobList().clone();
					newState.remJobList(currentJobList);	
					if(heuristicCost(currentState,e1) == 0) {
						for(int counter = 0; counter < newState.getJobList().size(); counter++){
							if(compareEdge(newState.getJobList().get(counter),e1)){
								newState.getJobList().remove(counter);
							}
						}
					}
					toVisit.add(newState); 
					//System.out.println(toVisit.size());
				}
			}
			currentState = toVisit.poll();
		}		
		// Print the path;
		finalPath = currentState.getPathTravelled();
		System.out.println(currentState.getPathTravelled().size()+1+" nodes expanded");
		DecimalFormat deciFormat = new DecimalFormat("#.00");
		System.out.println("cost = "+deciFormat.format(currentState.getAccumCost()));
		for(Edge e1: finalPath){
			boolean isJob = false;
			for(int index = 0; index < jobList.size() && !isJob; index++ ){
				if(compareEdge(e1,jobList.get(index))){
					isJob = true;
					System.out.println("Draw from "+e1.getFrom().getX()+" "+e1.getFrom().getY()+" to "+e1.getTo().getX()+" "+e1.getTo().getY());
				}
			}
			if(!isJob){
					System.out.println("Move from "+e1.getFrom().getX()+" "+e1.getFrom().getY()+" to "+e1.getTo().getX()+" "+e1.getTo().getY());
			}
		}
	}
	
	public void printNodeEdges(){
		Node z = zeroNode;
		System.out.println("For Node "+z.getX()+" "+z.getY());
			for(Edge e: z.getEdge() ){
				System.out.println("From: "+e.getFrom().getX()+" "+e.getFrom().getY()+" to "+e.getTo().getX()+" "+e.getTo().getY()+" Cost: "+e.getCost());
			}		
		for(Node n: nodes) {
			System.out.println("For Node "+n.getX()+" "+n.getY());
			for(Edge e: n.getEdge() ){
				System.out.println("From: "+e.getFrom().getX()+" "+e.getFrom().getY()+" to "+e.getTo().getX()+" "+e.getTo().getY()+" Cost: "+e.getCost());
			}		
		}
	}
}
