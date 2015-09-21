import java.util.*;

/**
 * Stores the value of the each cell 
 */
public class Cell {
	
	int row;
	int col;
	char type;
	boolean explored;		/* Flag telling us whether we have stepped onto this cell or not */
	
	/* A* Variables */
	Cell astar_cameFrom;	/* Cell that we came from in A* search */
	int astar_costSoFar;	/* Current cost to this cell in A* search */
	int astar_priority;
	
	/* Constructor for the Cell class */
	public Cell(int row, int col) {
		this.row = row;
		this.col = col;
		type = '.'; 		/* . signifies the cell has not been discovered */
		explored = false;	/* Determines whether the cell has explored/visited */
		
		astar_cameFrom = null;
		astar_costSoFar = 0;
		astar_priority = 0;
	}
	
	/* Change the type of cell */ 
	void changeType(char c) {
		this.type = c;
	}
	
	/* Override the equals method so we can compare cells properly */
	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
        	return false;
        }
		/* If the object is compared with itself then return true */
        if (obj == this) {
            return true;
        }
        
        /* Check if obj is an instance of Cell or not */
        if (!(obj instanceof Cell)) {
            return false;
        }
         
        /* Typecast obj to Cell so that we can compare data members */
        Cell c = (Cell) obj;
         
        /* Compare the data members and return accordingly */
        if( (row == c.row) && (col == c.col) && (type == c.type) ) {
        	return true;
        }
        return false;
	}
	
	/*
	 * Setter Methods
	 */
	public void set_cameFrom(Cell c) {
		astar_cameFrom = c;
	}
	
	public void set_costSoFar(int i) {
		astar_costSoFar = i;
	}
	
	public void set_priority(int i) {
		astar_priority = i;
	}
}