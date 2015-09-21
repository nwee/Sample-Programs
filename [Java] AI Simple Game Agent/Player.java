import java.util.*;

/**
 * Stores and maintains the Player position and items.
 */
public class Player {
	int dyn;
	boolean hasAxe, onBoat, gotGold;
	int row,col;
	char facing; 	/* Possible values are 'u', 'l', 'd', 'r' representing up, left, down and right respectively */
	
	/* Constructor for the Player class */
	public Player(int x, int y) {
		this.row = y;
		this.col = x;
		facing = 'u';
		
		/* Items */
		dyn = 0;
		hasAxe = false;
		onBoat = false;
		gotGold = false;
	}
	
	/* Moves the player forwards and updates the position based on where they are facing */
	void moveForwards() {
		if (facing == 'u')		row -= 1; /* Moves up */
		else if (facing == 'd')	row += 1; /* Moves down */
		else if (facing == 'l')	col -= 1; /* Moves left */
		else if (facing == 'r')	col += 1; /* Moves right */
	}
	
	/* Returns the cell in front of the player */
	public Cell getNext() { 
		int r = row;
		int c = col;
		
		if (facing == 'u')		r -= 1; /* Moves up */
		else if (facing == 'd')	r += 1; /* Moves down */
		else if (facing == 'l')	c -= 1; /* Moves left */
		else if (facing == 'r')	c += 1; /* Moves right */
		return new Cell(r,c);
	}
	
	/* Simple method to change where the character is facing */
	void turn(char facing) {
		this.facing = facing;
	}
}
