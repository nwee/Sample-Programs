import java.util.ArrayList;
import java.util.Comparator;
import java.util.PriorityQueue;

public class AStar {
	
	ArrayList<Cell> visited;			/* Stores nodes that we have visited */
	PriorityQueue<Cell> astar_frontier;	/* Queue of nodes that we have not yet visited but have discovered */
	ArrayList<Cell> neighbours;			/* List of neighbours for a particular cell */
	ArrayList<Cell> path;				/* A* path */
	
	/* Executes the A* algorithm */
	public ArrayList<Cell> run_astar(Cell start, Cell goal, Map m) {
		
		Cell curr;				/* Current Node from frontier */
 		int new_cost;
		
		astar_frontier = new PriorityQueue<>(10, comp);	/* Comparator shuffles lowest cost nodes to the front */
		astar_frontier.add(start);
		
		visited = new ArrayList<Cell>();
		visited.add(start);
		start.set_costSoFar(0);
		
		/* Iterate through all the nodes in the search space until there are none left */
		while (!astar_frontier.isEmpty()) {
			curr = astar_frontier.remove();
			
			/* Break if we reach the goal node */
			if (curr.equals(goal)) {
				break;
			}
			
			neighbours = m.get_neighbours(curr);
			for (Cell next : neighbours) {
				new_cost = curr.astar_costSoFar + 1; /* 1 is the cost to move from the current node to the next node */
				/* Check if we have found a shorter path */
				if ((next.astar_costSoFar == 0) || (new_cost < next.astar_costSoFar)) {
					next.set_costSoFar(new_cost);
					next.set_priority(new_cost + heuristic(goal, next));
					astar_frontier.add(next);
					visited.add(next);
					next.set_cameFrom(curr);
				}
				
			}
		}
		path = path_reconstruct(start, goal);
		astar_clean(visited);			/* Clean up assigned values */
		
		/* Can return null if path reconstruction not successful, i.e non-valid path */
		return path;	
	}
	
	/* Manhattan Distance */
	private int heuristic(Cell a, Cell b) {
		int absx = Math.abs(a.row - b.row);
		int absy = Math.abs(a.col - b.col);
		return (absx + absy); 
	}
	
	/* Resets values that were changed during A* so we can use them for the next A* */
	private void astar_clean(ArrayList<Cell> visited) {
		for(Cell c : visited) {
			c.astar_cameFrom = null;
			c.astar_costSoFar = 0;
			c.astar_priority = 0;
		}
	}
	
	/* Returns the A* path from a starting cell to the goal cell */
	public ArrayList<Cell> path_reconstruct(Cell start, Cell goal) {
		
		path = new ArrayList<Cell>();
		Cell curr = goal;
		
		while(!curr.equals(start)) {
			path.add(0, curr);	/* Add to the start of the list */
			if (curr.astar_cameFrom != null) {
				curr = curr.astar_cameFrom;	/* breaks here cause no came from */
			} else {
				return null;
			}
		}
		return path;
	}
	
    /* Comparator anonymous class implementation for comparing two nodes */
    public static Comparator<Cell> comp = new Comparator<Cell>(){

    	/* Negative means a comes before b. Positive means b comes before a */
		@Override
		public int compare(Cell a, Cell b) {
			return a.astar_priority - b.astar_priority;
		}
    };
	
    
}
