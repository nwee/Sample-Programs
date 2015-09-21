import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Scanner;

/**
 * This is the main class, it reads input  in this format "Line between 4 1 and 4 4" and performs an A* search   
 * @author Nelson Wee 
 * nwee991, z3352078	
 */
public class PenPlotter {
	public static void main(String[] args) throws FileNotFoundException {
		try {
			Scanner s = new Scanner(new FileReader(args[0]));
			//Scanner s = new Scanner(System.in);
			AdjacencyGraph g = new AdjacencyGraph();
			while(s.hasNext()) {
				if (s.hasNext("Line")) {
					s.next();	//Line
					s.next();	//between
					int x1 = s.nextInt();
					int y1 = s.nextInt();
					
					s.next();	//and
					int x2 = s.nextInt();
					int y2 = s.nextInt();
					
					Node from = new Node(x1,y1);
					Node to = new Node(x2,y2);
					g.addNode(from);
					g.addNode(to);
					
					g.addJob(from, to);	
				}
				else break;
			}
			g.addEdges();
			//g.printNodeEdges();
			
			g.aStarPathFind();
			s.close();
		} catch (Exception e) {
			e.printStackTrace();
		}	
	}
	

}
