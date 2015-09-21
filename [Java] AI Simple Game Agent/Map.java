import java.util.*;

/**
 *  Stores the agent's representation of the map
 *  Contain's all the information that is known to the agent at any particular point in time
 */
public class Map {

	private int x;
	private int y;
	public Cell[][] map;
	Player p;
	
	/* Store location of items */
	ArrayList<Cell> dyna_loc;
	Cell gold_loc;
	Cell axe_loc;
	
	Cell homePort; 				/* Stores the initial port, for return trips to starting point */
	Cell lead;     				/* Stores a cell adjacent to a cell of interest as the goal for the A* */
	boolean seaSearch = false; 	/* Assists in sea exploration */
	
	/* Constructor for the Map class */
	public Map(int x, int y) {
		this.x = x;
		this.y = y;
		this.map = new Cell[x][y];
		dyna_loc = new ArrayList<Cell>();
	}
	
	/* Initialises the basic map*/
	void initMap (char view[][]) {
		/* Fills the map with empty cells */
		for(int i=0; i < 159; i++ ) {
			for(int j=0; j < 159; j++ ) {
				map[i][j] = new Cell(i, j);
			}
		}
		
		/* Stores all given cell data */
		for(int row=0; row < 5; row++ ) {
			for(int col=0; col < 5; col++ ) {
				/* Sets up the player */
				if(( row == 2 )&&( col == 2 )) {
					p = new Player(79,79);
					p.turn('u');
					map[79][79].changeType(' ');
					map[79][79].explored = true;
	            }
	            else {
	            	/* Offsets the value from 80,80 origin */
	            	Cell temp = map[77+row][77+col]; 
	            	temp.changeType(view[row][col]);
	            	update_item_loc(temp);
	            }
	         }
	      }	
	}
	
	/* This method handles the updating of the map and player based on the given command */
	void updateMap(char movement, char view[][], char prev_view[][]) {
		/* Updates player position forwards */
		if (movement == 'f') { 
			Cell next = map[p.getNext().row][p.getNext().col];
			/* If the player is moving from water to land, leave boat behind */
			if (map[p.row][p.col].type == '~' && p.onBoat && next.type == ' ') {
				map[p.row][p.col].type = 'B';
				p.onBoat = false;
				seaSearch = false;
				p.moveForwards();
			}
			else p.moveForwards();
					
			map[p.row][p.col].explored = true;
			/* Adds new frontiers to the internal map based on where the player is facing */
			switch (p.facing) { 
				case 'u': 
					for(int i = 0; i<5; i++) {
						Cell temp = map[p.row-2][p.col-2+i]; 
						temp.changeType(view[0][i]);
						update_item_loc(temp);
					}
					break;
				case 'd': 
					for(int i = 0; i<5; i++) {
						Cell temp = map[p.row+2][p.col+2-i]; 
						temp.changeType(view[0][i]);
						update_item_loc(temp);
					}
					break;
				case 'l': 
					for(int i = 0; i<5; i++) {
						Cell temp = map[p.row+2-i][p.col-2]; 
						temp.changeType(view[0][i]);
						update_item_loc(temp);
					}
					break;
				case 'r': 
					for(int i = 0; i<5; i++) {
						Cell temp = map[p.row-2+i][p.col+2]; 
						temp.changeType(view[0][i]);
						update_item_loc(temp);
					}
					break;
			}
		}
		/* Updates where the player is facing for a left turn */
		else if (movement == 'l') { 
			if (p.facing == 'u') 		p.turn('l');
			else if (p.facing == 'd') 	p.turn('r');
			else if (p.facing == 'l') 	p.turn('d');
			else if (p.facing == 'r') 	p.turn('u');
		}
		/* Updates where the player is facing for a right turn */
		else if (movement == 'r') {
			if (p.facing == 'u') 		p.turn('r');
			else if (p.facing == 'd') 	p.turn('l');
			else if (p.facing == 'l') 	p.turn('u');
			else if (p.facing == 'r') 	p.turn('d');
		}
		/* Updates the map when the player chops down a tree  */
		else if (movement == 'c') {
			if (p.facing == 'u' && map[p.row-1][p.col].type == 'T')			map[p.row-1][p.col].type = ' ';  
			else if (p.facing == 'd' && map[p.row+1][p.col].type == 'T')	map[p.row+1][p.col].type = ' '; 
			else if (p.facing == 'l' && map[p.row][p.col-1].type == 'T')	map[p.row][p.col-1].type = ' ';
			else if (p.facing == 'r' && map[p.row][p.col+1].type == 'T')	map[p.row][p.col+1].type = ' ';
		}
		/* Updates the map when the player blows up a wall */
		else if (movement == 'b') {
			if (p.facing == 'u' && map[p.row-1][p.col].type == '*')			map[p.row-1][p.col].type = ' ';  
			else if (p.facing == 'd' && map[p.row+1][p.col].type == '*')	map[p.row+1][p.col].type = ' '; 
			else if (p.facing == 'l' && map[p.row][p.col-1].type == '*')	map[p.row][p.col-1].type = ' ';
			else if (p.facing == 'r' && map[p.row][p.col+1].type == '*')	map[p.row][p.col+1].type = ' ';
		}
	}
	
	/* Helper method to print out internal map, and player inventory, testing purposes only */
	void printMap() {
	      System.out.println("\n+----------------------------------------------------------------------------------------------------------------------------------------------------------+");
	      for(int row=0; row < x; row++ ) {	      
	         System.out.print("|");
	         for(int col=0; col < y; col++ ) {
	            if(( row == p.row )&&( col == p.col )) {
	            	char dir = 'v'; //prints the facing of the player
	            	if (p.facing == 'u') dir = '^';		 
	           		else if (p.facing == 'd') dir = 'v';	
	           		else if (p.facing == 'l') dir = '<';	
	           		else if (p.facing == 'r') dir = '>';
	           		System.out.print(dir);
	            }
	            else {
	            	if(map[row][col].explored == true && map[row][col].type != 'B' && map[row][col].type != '~') {
	            		/* Symbolizes land that we have explored */
	            		System.out.print('-');
	            	} 
	            	else if(map[row][col].explored == true && map[row][col].type == '~') {
	            		/* Symbolizes water that we have explored */
	            		System.out.print('&');  
	            	}
	            	else {
	            		System.out.print(map[row][col].type);
	            	}
	               
	            }
	         }
	         System.out.println("|");
	      }
	      System.out.println("+----------------------------------------------------------------------------------------------------------------------------------------------------------+");
	      System.out.println("Player AXE: "+p.hasAxe+" DYNA: "+p.dyn+" BOAT: "+p.onBoat+"\n");
	}
		
	/*
	 *  Return the neighbours of a particular cell.
	 *  Neighbours are cells adjacent to the current cell.
	 */
	ArrayList<Cell> get_neighbours(Cell c) {
		
		ArrayList<Cell> neighbours = new ArrayList<Cell>();
		Cell temp;
		
		temp = map[c.row-1][c.col];			/* Up */
		if(check_moveable_loc(temp)) {		/* Check that the agent can actually move there */
			neighbours.add(temp);
		}		
		temp = map[c.row][c.col+1];			/* Right */
		if(check_moveable_loc(temp)) {
			neighbours.add(temp);
		}
		temp = map[c.row+1][c.col];			/* Down */
		if(check_moveable_loc(temp)) {
			neighbours.add(temp);
		}
		temp = map[c.row][c.col-1];			/* Left */
		if(check_moveable_loc(temp)) {
			neighbours.add(temp);
		}
		return neighbours;
	}
	
	/* Checks whether the location is a cell that the agent can move to */
	boolean check_moveable_loc(Cell c) {
		if((c.type == '*') || (c.type == 'T') || (c.type == '.')) {
			return false;
		}
		if((c.type == '~') && (p.onBoat == false)) {
			return false;
		}
		if (seaSearch) if(c.type ==' ' && p.onBoat)return false;
		
		return true;
	}
	
	/* Updates the locations of items in the respective data structures */
	private void update_item_loc(Cell c) {
		if (c.type == 'd') {
			dyna_loc.add(c);
		} else if (c.type == 'g') {
			gold_loc = c;
		} else if (c.type == 'a') {
			axe_loc = c;
		}
	}
	
	/* Check if the player is on an item, if so then add it to the player's inventory */
	public void add_item() {
		Cell playerPos = map[p.row][p.col];
		
		/* Check what kind of cell the player is on */
		if (playerPos.type == 'g') {
			p.gotGold = true;
		} else if (playerPos.type == 'a') {
			p.hasAxe = true;
		} else if (playerPos.type == 'd') {
			p.dyn++;
			/* Remove dynamite from the known dynamite locations */
			for(int i = 0; i < dyna_loc.size(); i++) {
				if((dyna_loc.get(i).row == playerPos.row) && (dyna_loc.get(i).col == playerPos.col)) {
					dyna_loc.remove(i);
				}
			}
		} else if (playerPos.type == 'b') {
			p.onBoat = true;
		}
	}
	
	/* Returns a cell that has a unexplored neighbouring cell */
	public Cell unexplored_neighbours(ArrayList<Cell> path) {
		for (Cell c : path) {
			ArrayList<Cell> neighbours = get_neighbours(c);
			for (Cell n : neighbours) {
				if(n.explored == false) {
					return n;
				}
			}
		}
		return null;
	}
	
	
	/* Checks whether we have gone past the boundary of our map or into unexplored regions*/
	public boolean boundary_check(Cell c) {
		if ((c.row < 0) || (c.row > 158)) {
			return false;
		}
		if ((c.col < 0) || (c.col > 158)) {
			return false;
		}
		return true;
	}
	
	/**
	 * Returns the most viable cell of the given lead type
	 * i.e. type = "axe" would search for the best tree to return 
	 * @param type
	 * @return
	 */
	public Cell returnLead(String type){
		Cell adjCell = new Cell(0,0);
		/* If unchanged, represents no leads*/
		Cell leadCell = new Cell(999,999);
		/* Initial exploration cost */
		int Ecost = 50;
		
		/* Searches for an ideal tree and returns the adjacent location */ 
		if (type.equals("axe")) {
			for(int row=0; row < x; row++ ) {
		         for(int col=0; col < y; col++ ) {
		            if(map[row][col].type == 'T') {
		            	int cost = exploredCost(map[row][col]);
		            	/*Finds the cell with the lowest cost*/
			            if (cost < Ecost && cost > 0) {
			            	Ecost = cost;
			            	/* returns the position of the tree to cut */
			            	leadCell = map[row][col]; 
			            	adjCell = getAdjCell(map[row][col]);
			            }
		            } 
		         }			
			}
		}
		/* Searches for an ideal wall and returns the adjacent location */
		else if (type.equals("dynamite")) { 
			for(int row=0; row < x; row++ ) {
		         for(int col=0; col < y; col++ ) {
		            if(map[row][col].type == '*' || (map[row][col].type == 'T' && !p.hasAxe)) {
		            	int cost = exploredCost(map[row][col]);
		            	/* Finds the cell with the lowest cost */
			            if (cost < Ecost && cost > 0) {
			            	Ecost = cost;
			            	/* Returns the position of the wall to blow up */
			            	leadCell = map[row][col]; 
			            	adjCell = getAdjCell(map[row][col]);
			            }
		            } 
		         }			
			}
		}
		/* Searches for boat to board */
		else if (type.equals("boat")) { 
			for(int row=0; row < x; row++ ) {
		         for(int col=0; col < y; col++ ) {
		            if(map[row][col].type == 'B') {
		            	int cost = exploredCost(map[row][col]);
			            if (cost < Ecost && cost > 0) {
			            	Ecost = cost;
			            	leadCell = map[row][col]; 
			            	adjCell = leadCell;
			            	seaSearch = true;
			            }
		            } 
		         }			
			}
		}
		/* Searches for land to dock */
		else if (type.equals("dock")) {
			for(int row=0; row < x; row++ ) {
		         for(int col=0; col < y; col++ ) {
		        	/* Land in unexplored territory */
		            if(map[row][col].type == ' ' && !map[row][col].explored) { 
		            	int cost = exploredCost(map[row][col]);
		            	if (cost < Ecost && cost > 0) {
		            		Ecost = cost;
				          	leadCell = map[row][col]; 
				            adjCell = getAdjCell(map[row][col]);
		            	}
		            }
		         }
			}			
		}

		/* Updates the lead to whatever cell it might have */
		lead = adjCell; 
				
		return leadCell;
	}
	
	/**
	 * Returns a "cost" of suitable cells to find the most ideal cell, lower cost is better
	 * i.e if there is a path/gold in sight near the suitable cell, returns better cost    
	 * @param goal
	 * @return
	 */
	public int exploredCost(Cell goal){
		int cost = 0;
		int row = goal.row;
		int col = goal.col;
		
		/* Cost is lower if: An unexplored space, or items/gold can be seen nearby */  		
		/* Check up+down */
		if (row > 0) { 
			if (map[row+1][col].explored) cost +=10;
			else if(map[row+1][col].type == ' ' && !map[row+1][col].explored) cost -= 1;
				
			if (map[row-1][col].explored) cost +=10;
			else if(map[row-1][col].type == ' ' && !map[row-1][col].explored) cost -= 1;
		}   
		/* Check left+right */
		if (col > 0) { 
			if (map[row][col+1].explored) cost +=10;
			else if(map[row][col+1].type == ' ' && !map[row][col+1].explored) cost -= 1;
			
			if (map[row][col-1].explored) cost +=10;
			else if(map[row][col-1].type == ' ' && !map[row][col-1].explored) cost -= 1;
		}
		/* Checks if an item/gold can be spotted from that cell */
		if (col > 2 && row > 2) {
			for (int r = row - 2; r <= row + 2; r++) {
				for(int c = col - 2; c<= col + 2;c++) {
					if(map[r][c].type == 'g') {
						cost -= 2;
					}
					else if(map[r][c].type == 'a') {
						cost -= 1;
					}
					else if(map[r][c].type == 'd') {
						cost -= 1;
					}
				}
			}
		}	
		if (cost <= 0) return 0;
		
		return cost;
	}
	/**
	 * Returns an adjacent cell to the cell of interest (T,*,B) to allow for player actions
	 * such as cutting or blowing up
	 * @param goal
	 * @return
	 */
	public Cell getAdjCell(Cell goal){
		int row = goal.row;
		int col = goal.col;
		if (row > 0) { 
			if (map[row+1][col].explored) return map[row+1][col];
			if (map[row-1][col].explored) return map[row-1][col];
		}   
		if (col > 0) { 
			if (map[row][col+1].explored) return map[row][col+1];
			if (map[row][col-1].explored) return map[row][col-1];
		}
		return null;
	}
	
	/* Returns location of gold */
	public boolean foundGold() {
		for(int i=0; i < 159; i++ ) {
			for(int j=0; j < 159; j++ ) {
				if (map[i][j].type == 'g') {
					gold_loc = map[i][j];
					return true;
				}
			}
		}
		return false;
	}
	
	/* Changes cell type from water to boat */
	public void onABoat() {
		for(int i=0; i < 159; i++ ) {
			for(int j=0; j < 159; j++ ) {
				if (map[i][j].type == 'B' && p.row==i && p.col == j) {
					p.onBoat = true;
					map[i][j].type = '~';
				}
			}
		}
	}
	
	/* Checks if the player has arrived on an island by boat */
	public boolean exploredTheSea(){
		if (p.col > 0 && p.row > 0) {
			for(int i=0; i < 159; i++ ) {
				for(int j=0; j < 159; j++ ) {
					if (map[i][j].type == '~' && map[i][j].explored) {
						return true;
					}
				}
			}
		}
		return false;
	}
}