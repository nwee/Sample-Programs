// Created by JiaLong LIU, z3345987
//            Nelson Wee,  z3352078
//			  Molei Wang,  z3390139
// COMP2911, Project
// Date: 2 June 2013
// This class is the container which contains all grids in the system.
// It mainly manipulate date for all grids.

import java.awt.*;
import java.util.*;

public class GridContainer {
	
	// constructor for initialization.
	public GridContainer() {
		rowList = new ArrayList<LinkedList<Grid>>();
		colList = new ArrayList<LinkedList<Grid>>();
		nightList = new ArrayList<LinkedList<Grid>>();
		// initialize rowList, colList and nightList with 9 lists inside each of them.
		for (int i = 0; i < 9; i++) {
			LinkedList<Grid> row = new LinkedList<Grid>();
			LinkedList<Grid> col = new LinkedList<Grid>();
			LinkedList<Grid> nightGrid = new LinkedList<Grid>();
			rowList.add(row);
			colList.add(col);
			nightList.add(nightGrid);
		}
	}
	
	// This method is for adding grid into each rowList, colList and nightList.
	public void add(Grid grid) {
		int row = grid.getYValue();
		int col = grid.getXValue();
		rowList.get(row).add(grid);
		colList.get(col).add(grid);
		int locationOfNightGrid = ((row / 3) * 3) + (col / 3); // get the index of the list in night ArrayList.
		nightList.get(locationOfNightGrid).add(grid);
	}
	
	public Grid getGrid(int col, int row) {
		return colList.get(col).get(row);
	}
	
	public boolean checkGrid(Grid grid) {
		int row = grid.getYValue();
		int col = grid.getXValue();
		int locationOfNightGrid = ((row / 3) * 3) + (col / 3); // get the index of the list in night ArrayList.
		return duplicateNumber(rowList.get(row))
				&& duplicateNumber(colList.get(col))
				&& duplicateNumber(nightList.get(locationOfNightGrid));
	}
	
	// This method is for checking whether there are duplicateNumber in the column, row and 3*3 grids.
	public boolean duplicateNumber(LinkedList<Grid> list) {
		boolean returnValue = true;
		LinkedList<Integer> checkList = new LinkedList<Integer>();
		for (Grid grid : list) {
			int value = grid.getValue();
			if (!checkList.contains(value)) {
				checkList.add(value);
			
			// -1 is the initial value of a grid.
			} else if (value != -1){
				returnValue = false; // returnValue = false means there's 
									 // at least one duplicate number in the list.
				break;
			}
		}
		return returnValue;
	}
	
	// This method is changing value for a grid specify by col and row.
	public void changeValue(int col, int row, int value) {
		Grid grid = rowList.get(row).get(col);
		int oldValue = grid.getValue();
		grid.setText(Integer.toString(value));
		grid.setEditable(false);
		
		// check whether the number is valid.
		// If not, reset it back to the oldValue.
		if (!checkGrid(grid)) {
			grid.setText(Integer.toString(oldValue));
			grid.setEditable(true);
		} else {
			grid.unEditablePaint();
		}
	}
	
	// This method is to clean all setting for all grids.
	public void clean() {
		for (int i = 0; i < 9; i++) {
			LinkedList<Grid> row = rowList.get(i);
			LinkedList<Grid> col = colList.get(i);
			LinkedList<Grid> night = nightList.get(i);
			for (Grid grid : row) {
				grid.setText("");
				grid.resetPaint();
				grid.resetOldValue();
				grid.setEditable(true);
			}
			for (Grid grid : col) {
				grid.setText("");
			}
			for (Grid grid : night) {
				grid.setText("");
			}
		}
	}
	
	
	// This method is for the solve method in MidPanel class.
	public void resetVal() {
		for (int i = 0; i < 9; i++) {
			LinkedList<Grid> row = rowList.get(i);
			LinkedList<Grid> col = colList.get(i);
			LinkedList<Grid> night = nightList.get(i);
			
			// reset all grid with input number to blank.
			for (Grid grid : row) {
				if (grid.isEditable()) 
					grid.setText("");
			}
		}
	}
	
	// This method is for the condition that a player won the game.
	// It will set all grids to uneditable.
	public void win() {
		for (int row = 0; row < 9; row++) {
			for (int col = 0; col < 9; col++) {
				rowList.get(row).get(col).setEditable(false);
			}
		}
	}
	
	private ArrayList<LinkedList<Grid>> rowList; // this List is for storing grids by row.
	private ArrayList<LinkedList<Grid>> colList; // this List is for storing grids by column.
	private ArrayList<LinkedList<Grid>> nightList; // this List is for storing 3*3 girds.
}