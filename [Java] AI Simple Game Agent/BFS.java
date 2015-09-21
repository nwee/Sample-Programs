
import java.util.*;
import java.io.*;
import java.net.*;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.Queue;

public class BFS {
	
	ArrayList<Cell> visited;		/* Stores nodes that we have visited */
	LinkedList<Cell> bfs_frontier;	/* Queue of nodes that we have not yet visited but have discovered */
	ArrayList<Cell> path;			/* Path run by BFS over its duration */
	ArrayList<Cell> prevpath;		/* Stores the previous BFS path */
	
	/* Constructor for the BFS class */
	public BFS() {
		path = new ArrayList<Cell>();
		visited = new ArrayList<Cell>();
		prevpath = new ArrayList<Cell>();
	}
	
	/*
	 * Modified BFS algorithm that adds the most recently discovered nodes to the front
	 */
	public ArrayList<Cell> run_bfs(Map map) {
		
		Cell curr;
		Cell playerPos;
		ArrayList<Cell> neighbours;		/* List of neighbours of the current cell */
		
		playerPos = new Cell(map.p.row,map.p.col);
		bfs_frontier = new LinkedList<Cell>();
		bfs_frontier.add(playerPos);	/* Add player's current location */
		
		if(!visited.contains(playerPos)) {
			visited.add(playerPos);
		}
		
		/* Continue until we have searched all the cells */
		while (!bfs_frontier.isEmpty()) {
			curr = bfs_frontier.remove();
			
			/* We do not want to add the current position to the path */
			if(!curr.equals(playerPos)) {	
				path.add(curr);
			}
			
			neighbours = map.get_neighbours(curr);
			for (Cell next : neighbours) {
				if (!visited.contains(next)) {	/* With overrided equals */
					bfs_frontier.addFirst(next);
					visited.add(next);
				}
			}
		}
		return path;
		
	}
	
	/* Helper method to print the BFS path */
	void printPath() {
		System.out.print("BFS path: ");
		for (Cell next : path) {
			System.out.print("[R"+next.row+",C"+next.col+"]");
		}
		System.out.print("\n");
	}
	
}
