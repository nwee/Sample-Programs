/*********************************************
 *  Agent.java 
 *  Agent for Text-Based Adventure Game
 *  COMP3411 Artificial Intelligence
 *  UNSW Session 1, 2015
 *  
 *  Group 41
 *  Nelson Wee   z3352078
 *  Yu Sing Chan z3484460
 *  
 *  
 */

/*
 * Answer to the Question:
 * 
 * Our program is split into 6 main Java files consisting of Agent, Map, Cell, Player, BFS and AStar. Agent is 
 * our main file and contains the AI code in get_action() that fuels our agent. Additionally, all the data 
 * structures such as our map, our search functions and others all originate from the initialisation happening 
 * in main(). Map most importantly covers our implementation of the agent's map (represented as an 2D array), but 
 * also various other map functions such as manipulating which parts of the map we should update, to storing the 
 * locations of items we have seen but not necessarily obtained. The notion behind the agent having their own map 
 * is simple - we must keep track of what the agent has come across and note it down in the map. Since the player 
 * can start anywhere in the map, and the map can be at most 80 by 80 cells big, we decided to initialise a map 
 * that is approximately four times the size of an 80 by 80 map (precisely it will be 79+1+79 = 159 as there will 
 * be a single column and row of overlap, with 79 cells on either side). The reasoning behind this is that even 
 * if the player starts in the corner of the largest sized map, we will still be able to generate the map no 
 * matter which direction it grows. One of our design decisions in this process is that the agent will always 
 * face "north/up" at the start of execution, this ensures a correct map representation no matter which way the 
 * agent will face initially. Our map contains 159x159 cell objects which are defined in the Cell class. Each 
 * cell stores the location in the map (row, column), as well as the type of cell it is (land, water, item) and 
 * whether it has been explored or not. Cells also contain A* variables to assist the A* algorithm (more on that 
 * later). Lastly, our Player class stores the "inventory" of a player, the location inside the map, and also 
 * what direction they are facing.
 * 
 * For algorithms, we have employed two predominant search algorithms - (modified) BFS and A*. BFS' main purpose 
 * was to provide a path for the agent to explore the map. When the agent steps onto a new cell, we mark it as 
 * being explored and add it to 'visited list' in BFS to ensure we do not search the same cell twice. The 
 * modification that was made to BFS was that instead of adding newly discovered nodes to the end of the BFS 
 * 'queue', we would add them to the start of the queue. This provided us with the wonderful property of having 
 * the agent explore the nodes that are closer in locality, and hence reduces the downfall of the original BFS 
 * having to constantly jump back and forth between top and bottom parts of the map. BFS would stop only when we 
 * have both seen and have a valid path to the gold (in which we would return back to the start), or when there 
 * were no more nodes to search in that current instance. A* on the other hand, served to provide us the shortest 
 * and most viable path from point A to point B. Not only can it be used to find and translate a path from the 
 * player's current location to the gold, but also to find a path between two contiguous BFS path nodes i and j. 
 * The reason as to why we would use A* between two BFS path nodes is that the nodes may not always be located 
 * adjacent to each other. Following the exploration of our current map boundaries, our program's decision making 
 * mode is initiated. This is achieved logically by utilising what is known about the map from our exploration. 
 * For instance, whether or not the player possesses an axe would determine if Tree-cutting is a viable decision 
 * to open new paths in the map. The choice of which Tree (or wall) to remove, is determined by an 'exploredCost' 
 * method which evaluates possible candidates and returns the most promising Cell. A* is used to facilitate 
 * movement between the player and a location adjacent to the object of interest, for the player to interact with it.
 * 
 */

import java.util.*;
import java.lang.*;
import java.io.*;
import java.net.*;

public class Agent {
	static char lastMove = ' ';
	
	static Map m;
	static BFS bfs;
	static AStar astar;
	static boolean explore; 			/* Flag to determine if we are in exploration mode or not */
	
	static ArrayList<Character> moves;	/* Movelist queue for the agent */
	static boolean itemMove = false;
	
	
	public char get_action( char view[][] ) {

		int ch = 0;
		Cell curr, next;
		
		/* Exploration Mode */
		if (explore == true) {
	        if (moves.isEmpty()) {
	        	
	        	/* Check if we have the gold, if so then stop exploring and return to start */
	        	if (m.p.gotGold == true) {
	        		explore = false;
	        		lastMove = ' ';
	        		ch = lastMove;
	        		return ((char)ch);
	        	}

	        	curr = m.map[m.p.row][m.p.col];	/* Initial cell is the player's current position */
	        	
	        	/* Check if we need to re-run BFS for the new parts of the map */
	            if (bfs.path.isEmpty()) {
	            	bfs.path = bfs.run_bfs(m);
	            }
	            
	            /* Check if BFS has not returned a valid path (still possible) */
	        	if(bfs.path.isEmpty()) {
	        		
	        		/* Check previous path to see if they have newly discovered neighbours that we haven't visited */
	        		Cell bfs_transition = m.unexplored_neighbours(bfs.prevpath);
	        		
	        		if (bfs_transition != null) {
	        			/* Need to run A* as contiguous BFS path nodes may not always be adjacent cells */
	        			astar.path = astar.run_astar(curr, bfs_transition, m); 
	                	
	                	/* Add all moves for the A* path between a BFS path node i and j to the move queue */
			         	translate_astar();
			         	
	                    /* Clean temp path */
	                    bfs.prevpath.clear();
	                    
	        		} else {
	        			/* Scan around to player to find unexplored cells */
	        			astar.path = scan_unexplored();
	        			
	        			if (astar.path != null) {
	        				/* Add all moves for the A* path between a BFS path node i and j to the move queue */
	    		         	translate_astar();
	        			} else {
	        				/* Nothing needs to be explored */
		        			explore = false;
	        			}
	        		}
	        	} else {
		            curr = m.map[m.p.row][m.p.col];	/* Initial cell is the player's current position */
		         	next = bfs.path.remove(0);
		         	bfs.prevpath.add(next);
		         	astar.path = astar.run_astar(curr, next, m); /* May not always be adjacent cells */
		         	
		         	/* Add all moves for the A* path between a BFS path node i and j to the move queue */
		         	translate_astar();
	        	}
	        }
	        
	        /* Prevents removing moves from an empty movelist */
	        if (!moves.isEmpty()) {
	        	lastMove = moves.remove(0);
	        } else {
	        	lastMove = ' ';
	        }
	        ch = lastMove;
		} 
		/* Exited exploration mode, entering decision making mode */
		else {
			ArrayList<Cell> pathToGold;
			Cell playerPos = m.map[m.p.row][m.p.col];	/* Player's current position */
			
			/* Clears old moves */
			moves.clear(); 
			
			/* 
			 * ITEM/Transport based decisions 
			 * Decides next move based on what is known about the map.
			 * Calls the method m.returnLead(char type) to return a suitable lead 
			 * (i.e. tree to cut down, wall to blow up) to open new paths.
			 * - The global variable m.lead is the adjacent, explored cell to the goal 
			 * - If returnLead returns a cell with row 999, it did not find a suitable one of that type. 
			 * - The boolean itemMove restricts the access to these decisions if previous moves are 
			 * in progress  
			 */
			
			/* GOLD - Check if we have the gold in possession */
			if(m.p.gotGold == true) {
				/* Return to start */
				ArrayList<Cell> pathHome = astar.run_astar(playerPos, m.map[79][79], m);
				
				/* If the gold is found on an island, return to the ship */
				if (m.exploredTheSea() && !m.p.onBoat && pathHome == null) {
					/* Calls the returnLead method to update the best choice (lead)*/
					m.returnLead("boat"); 
					/* Searches for the ship */
					if (m.p.col != m.lead.col || m.p.row != m.lead.row) { 
						astar.path = astar.run_astar(playerPos, m.lead, m);
						translate_astar();
					}
					/* Returns to the launching point  */
					else if (m.p.col == m.lead.col && m.p.row == m.lead.row){
						/* Enables path searching in the water */
						if (m.seaSearch) m.seaSearch = false;
						
						m.onABoat(); 
						itemMove = true;
						astar.path = astar.run_astar(playerPos, m.homePort, m);
					}
				}
				else astar.path = astar.run_astar(playerPos, m.map[79][79], m);
				translate_astar();
			} 
			
			/* Check if there is a valid path to the gold */
			else if (m.foundGold() && (pathToGold = astar.run_astar(playerPos, m.gold_loc, m)) != null) {
				/* Iterate through the A* */			
				astar.path = pathToGold;
				translate_astar();
			}
			
			/* AXE - Decide to use Axe to find new paths by cutting trees down. */
			else if (m.p.hasAxe && m.returnLead("axe").row!=999 && !itemMove) {			
				/* Facilitates movement to the best tree */ 
				if (m.p.col != m.lead.col || m.p.row != m.lead.row) { 
					astar.path = astar.run_astar(playerPos, m.lead, m);
					translate_astar();
				}
				/* If player is in front of the Tree, turn to face it and cut it down*/
				else if (m.p.col == m.lead.col && m.p.row == m.lead.row){
					itemMove = true;
					translate(0,m.p.facing,'a',m.returnLead("axe"));
				}	
			}
			
			/* DYNAMITE - Decide to use dynamite to find new paths by blowing walls up. */
			else if (m.p.dyn > 0 && m.returnLead("dynamite").row!=999 && !itemMove) {
				/* Facilitates movement to the best wall or tree (with no axe)*/ 
				if (m.p.col != m.lead.col || m.p.row != m.lead.row) { 
					astar.path = astar.run_astar(playerPos, m.lead, m);
					translate_astar();
				}
				/* if player is in front of the wall then turn to face, and blow it up*/
				else if (m.p.col == m.lead.col && m.p.row == m.lead.row){
					itemMove = true;
					--m.p.dyn;
					translate(0,m.p.facing,'d',m.returnLead("dynamite"));
				}	
			}
			
			/* BOAT - Search for and use boat. */
			else if (m.returnLead("boat").row!=999 && !itemMove) { 
				/* Stores the initial port location for the return journey*/
				m.homePort = m.returnLead("boat");
				/* Facilitates movement to the boat */
				if (m.p.col != m.lead.col || m.p.row != m.lead.row) { 
					astar.path = astar.run_astar(playerPos, m.lead, m);
					translate_astar();
				}
				else if (m.p.col == m.lead.col && m.p.row == m.lead.row){
					m.onABoat();
					/* Enables the exploration of the water */
					explore = true;
				}
			}
			/* BOAT - searches for unexplored land to dock boat*/ 
			else if (m.p.onBoat &&!itemMove) { 
				m.returnLead("dock");
				/* Facilitates movement to an unexplored land*/
				if (m.p.col != m.lead.col || m.p.row != m.lead.row) { 
					astar.path = astar.run_astar(playerPos, m.lead, m);
					/* Enables path searching in the water */
					if (m.seaSearch) m.seaSearch = false; 
					translate_astar();
				}
				else if (m.p.col == m.lead.col && m.p.row == m.lead.row){
					/* Enables the exploration of the land */
					explore = true;
				}
			}
						
			/* Execute queued item moves, then attempt to explore new paths */
			else if (itemMove) { 
				if (moves.isEmpty()) {
					itemMove = false;
					explore = true;
				}
			}
			
			/* Executes moves available */ 
	        if (!moves.isEmpty()) {
	        	lastMove = moves.remove(0);
	        } else {
	        	lastMove = ' ';
	        }
	        ch = lastMove;
		}
        
        return((char) ch);
   }

   void print_view( char view[][] )
   {
      int i,j;

      System.out.println("\n+-----+");
      for( i=0; i < 5; i++ ) {
         System.out.print("|");
         for( j=0; j < 5; j++ ) {
            if(( i == 2 )&&( j == 2 )) {
               System.out.print('^');
            }
            else {
               System.out.print( view[i][j] );
            }
         }
         System.out.println("|");
      }
      System.out.println("+-----+");
   }
   
  	/**
  	 * Translation method that takes in the cell of the next step and 
  	 * saves the moves into a an ArrayList. Needs to be called for each step of the path 
  	 * i.e. facing up need to move down = [L, L, F] to turn the player
  	 * @param i
  	 * @return
  	 */
	public static char translate(int i, char p_dir, char itemMove, Cell next) {
		Cell current, nextCell;
		/* Initialise with dummy values */
		current = m.map[m.p.row][m.p.col];
		nextCell = new Cell(0,0);
		
		/* Determine which 2 cells to evaluate for direction alignment 
		 * If next move is not an item move then continue with the astar_translate()
		 */
		if (itemMove ==' ') {
			if (i == 0) {
				current = m.map[m.p.row][m.p.col];
				nextCell = astar.path.get(i);
			} else {
				current = astar.path.get(i-1);
				nextCell = astar.path.get(i);
			}
		}
		
		/* If the next move is a item move then handle the moves */
		if (itemMove == 'a' || itemMove == 'd' || itemMove == 'b') {
			current = m.map[m.p.row][m.p.col];
			nextCell = next;
		}
		
		/* cell_direction is the direction of the adjacent 'nextCell' compared to the current cell */
		char cell_direction = difference(current, nextCell);
		
		/* If facing the correct direction just go forward */
		if (p_dir != cell_direction) {
			if (cell_direction == 'u') {
				if (p_dir == 'l') {
					moves.add('R');
				}
				else if (p_dir == 'r') { 
					moves.add('L');
				}
				else if (p_dir == 'd') { 
					moves.add('L');
					moves.add('L');
				}
				/* Allows us to simulate the agent walking through the path without actually moving */
				p_dir = 'u';	
			}
			else if (cell_direction == 'd') {
				if (p_dir == 'l') {
					moves.add('L');
				}
				else if (p_dir == 'r') { 
					moves.add('R');
				}
				else if (p_dir == 'u') { 
					moves.add('L');
					moves.add('L');
				}			
				p_dir = 'd';
			}
			else if (cell_direction == 'l') {
				if (p_dir == 'u') {
					moves.add('L');
				}
				else if (p_dir == 'd') { 
					moves.add('R');
				}
				else if (p_dir == 'r') { 
					moves.add('L');
					moves.add('L');
				}				
				p_dir = 'l';
			}
			else if (cell_direction == 'r') {
				if (p_dir == 'u') {
					moves.add('R');
				}
				else if (p_dir == 'd') { 
					moves.add('L');
				}
				else if (p_dir == 'l') { 
					moves.add('L');
					moves.add('L');
				}				
				p_dir = 'r';
			}		
		}
		if (itemMove == 'a') moves.add('C');
		else if (itemMove == 'd') moves.add('B');
		moves.add('F');
		
		return p_dir;
	}
	
	/* Adds all moves for the A* path between a BFS path node i and j to the move queue */
	public void translate_astar() {
     	char temp_dir = m.p.facing;
     	for (int y = 0; y < astar.path.size(); y++) {
        	temp_dir = translate(y, temp_dir,' ',null);
        }
	}
	
	/* Returns which direction the adjacent square is relative to player */
	static char difference(Cell current, Cell next) {
		if (next.col - current.col > 0) 
			return 'r';
		else if (next.col - current.col < 0) 
			return 'l';
		else if (next.row - current.row > 0) 
			return 'd';
		else if (next.row - current.row < 0) 
			return 'u';
		
		return 'u';
	}
	
	/*
	 *  Scans in a 'ring' around the player for a square that has not been explored.
	 *  The distance of the 'ring' (and consequently the size) is determined by the border.
	 *  The 'ring' will grow bigger over-time, only stopping if we have found an unexplored square or violated rules.
	 *  Returns an A* path to the unexplored cell.
	 */
	public ArrayList<Cell> scan_unexplored() {
		int border = 1;	/* Determines how far out we are scanning from the player */
		ArrayList<Cell> path;
		Cell playerPos = m.map[m.p.row][m.p.col];
		int bordertlr, bordertlc;
		/* Above 60 might break it */
		while(border < 50) {
			bordertlr = m.p.row - border;		/* Row number of top left corner of the border */
			bordertlc = m.p.col - border;		/* Col number of top left corner of the border */
			
			for (int i = 1; i <= (2*border+1); i++) {
				for (int j = 1; j <= (2*border+1); j++) {
					if ( (i == 1) || (i == (2*border-1)) ||		/* Top and bottom row */
							(j == 1) || (j == (2*border-1)) ) {	/* Left and right-most column */
						if (m.boundary_check(m.map[bordertlr+i-1][bordertlc+j-1]) == false) continue;
						
						if ((m.check_moveable_loc(m.map[bordertlr+i-1][bordertlc+j-1]) == true) 
								&& (m.map[bordertlr+i-1][bordertlc+j-1].explored == false)) {
							
							/* Check valid path to cell */
							if(!((path = astar.run_astar(playerPos, m.map[bordertlr+i-1][bordertlc+j-1], m)) == null) ) {
								return path;
							}
						}
					}
				}
			}
			border++;
		}
		return null;
	}

   public static void main( String[] args )
   {
      InputStream in  = null;
      OutputStream out= null;
      Socket socket   = null;
      Agent  agent    = new Agent();
      char   view[][] = new char[5][5];
      char   action   = 'F';
      int port;
      int ch;
      int i,j;

      if( args.length < 2 ) {
         System.out.println("Usage: java Agent -p <port>\n");
         System.exit(-1);
      }

      port = Integer.parseInt( args[1] );
      
      try { // open socket to Game Engine
         socket = new Socket( "localhost", port );
         in  = socket.getInputStream();
         out = socket.getOutputStream();
      }
      catch( IOException e ) {
         System.out.println("Could not bind to port: "+port);
         System.exit(-1);
      }

      try { /* Scan 5-by-5 window around current location */
    	 
    	 /* Initialise Map, BFS and A* */
    	 m = new Map(159,159);
    	 bfs = new BFS();
    	 astar = new AStar();
    	 moves = new ArrayList<Character>();
    	 boolean init = false; 					/* Have not initialised map yet */
    	 
    	 
         while( true ) {      		
        	/* Store the previous view of the agent for valid move checking */
        	char[][] prev_view = new char[5][5];
        	 
            for( i=0; i < 5; i++ ) {
               for( j=0; j < 5; j++ ) {
                  if( !(( i == 2 )&&( j == 2 ))) {
                     ch = in.read();
                     if( ch == -1 ) {
                        System.exit(-1);
                     }
                     prev_view[i][j] = view[i][j];
                     view[i][j] = (char) ch;
                  }
               }
            }
            
            /* Check if we have initialised the board or not */
            if (!init) { 
            	m.initMap(view);
            	init = true;
            	explore = true;	 /* Set explore to true here, as we want the agent to explore */
            }
            else { 
            	/* Take whatever the last command was and update the board */
            	if(lastMove != ' ') { 
            		m.updateMap(Character.toLowerCase(lastMove), view, prev_view);
            	}
            	/* Check whether we found an item last move */
            	m.add_item();
            }
	                   
           	action = agent.get_action( view );
           	out.write( action );
         }
      }
      catch( IOException e ) {
         System.out.println("Lost connection to port: "+ port );
         System.exit(-1);
      }
      finally {
         try {
            socket.close();
         }
         catch( IOException e ) {}
      }
   }
   
	
}
