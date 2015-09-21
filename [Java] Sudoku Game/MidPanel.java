// Created by JiaLong LIU, z3345987
//            Nelson Wee,  z3352078
//			  Molei Wang,  z3390139
// COMP2911, Project
// Date: 2 June 2013
// This class is for MidPanel in the GUI interface.
// It interacts with GUI and GridPanel class.
// It will perform the initialization of grid, reset,
// solve, pause and resume in the system.

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class MidPanel extends JPanel {
	
	// constructor for initialization.
	public MidPanel(Clock clock, Thread clockThread, Score score) {
		super();
		
		finish = false; // set the game become to not finish.
		isStart = false; // set the game become to not start.
		container = new GridContainer(); // create a new container to contain all grids information.
		hint = new Hint(); // create a hint object.
		this.clock = clock; // the clock is from GUI.
		this.clockThread = clockThread; // the clockThead is from GUI
		this.score = score; // the score is from GUI
		panel = new GridPanel(container, hint, clock, clockThread, score); // GridPanel
		setLayout(new BorderLayout());
		// If someone open the hint and close it later, it will blink in the bottom of the GUI.
		add("South", hint);
		add(panel);
	}
	
	// start new game.
	public void startSetting(int isEasy) {
		finish = false; // set the game become to not finish.
		numOfGridLeft = 81; // set numOfGridLeft to 81.
		isStart = true; // set the game  become to start.
		container.clean(); // clean all old setting before starting a new Game.
		clock.reset(); // reset the clock.
		score.reset(); // reset the score.
		
		// reset the resetSudokuArray.
		for (int row = 0; row < 9; row++) {
			for (int col = 0; col < 9; col++) {
				resetSudokuArray[row][col] = 0;
			}
		}
		
		generateNewPuzzle(); // generate a new puzzle.
		int num = 68;	//Expert
		if (isEasy == 0) num = 40;	//Easy
		else if(isEasy == 1) num = 58;	//Normal
		
		// This loop ensure the game will reach to the condition 
		// set by the specify numOfGridLeft.
		while (numOfGridLeft > num) {
			for (int row = 0; row < 9; row++) {
				for (int col = 0; col < 9; col++) {
					Random rand = new Random();
					int randNum = rand.nextInt(9);
					if (((col == randNum) || (row == randNum)) && (numOfGridLeft > num)) {
						if (container.getGrid(col, row).isEditable()) {
							container.changeValue(col, row, internalSudokuArray[row][col]);
							
							// resetSudokuArray stores the initial state of the game for later reset.
							resetSudokuArray[row][col] = internalSudokuArray[row][col];
							numOfGridLeft--; // each setting of grid will reduce numOfGridLeft by 1.
						}
					}
				}
			}
		}
		
		panel.setFinish(finish); // set not finish to the GridPanel.(panel is the object of GridPanel)
		panel.setNumOfGridLeft(numOfGridLeft); // set numOfGridLeft to the GridPanel.
	}
	
	// reset the game.
	public void reset() {
		finish = false; // set the game become to not finish.
		panel.setFinish(finish); // set not finish to the GridPanel.(panel is the object of GridPanel)
		numOfGridLeft = 81; // reset numOfGridLeft back to 81.
		isStart = true; // set the game  become to start.
		container.clean(); // clean all old setting of container.
		clock.reset(); // reset the clock.
		score.reset(); // reset the score.
		
		// reset the game back to the initial state.
		// the initial state of the game is stored in the resetSudokuArray.
		for (int row = 0; row < 9; row++) {
			for (int col = 0; col < 9; col++) {
				if (resetSudokuArray[row][col] != 0) {
					container.changeValue(col, row, resetSudokuArray[row][col]);
					numOfGridLeft--;
				}
			}
		}
		panel.setNumOfGridLeft(numOfGridLeft); // set numOfGridLeft to the GridPanel.
	}
	
	// solve the game.
	public void solve() {
		// check whether the game has started or not.
		// if the game is not started yet, it can't solve.
		if (isStart) {
			container.resetVal();
			for (int row = 0; row < 9; row++) {
				for (int col = 0; col < 9; col++) {
					if (container.getGrid(col, row).isEditable()) {
						container.changeValue(col, row, internalSudokuArray[row][col]);
					}
				}
			}
			finish = true; // set the game become to finish.
			panel.setFinish(finish); // set finish to the GridPanel.(panel is the object of GridPanel)
			isStart = false; // set the game become to not start.
		} else {
			JOptionPane.showMessageDialog(null, "A new game has not started yet.\n" + 
										"Please select a new game [CTRL+N] to restart");
		}
	}
	
	// pause the game.
	public void startPause() {
		for (int row = 0; row < 9; row++) {
			for (int col = 0; col < 9; col++) {
				container.getGrid(col, row).startPause(); // make numbers of all grids disappear.
			}
		}
	}
	
	// resume the game.
	public void resume() {
		for (int row = 0; row < 9; row++) {
			for (int col = 0; col < 9; col++) {
				container.getGrid(col, row).resume(); // make all disappear numbers appear again.
			}
		}
	}
	
	// This method is for generating a list containing random number.
	private ArrayList<Integer> generateRandomList() {
		ArrayList<Integer> randomList = new ArrayList<Integer>();
		Random rand = new Random(); // generate a random number
		for (int i = 0; i < 9; i++) {
			int randNum = rand.nextInt(9) + 1; // a random number from 1 to 9
			
			// loop until found a number which is not in the list.
			while (randomList.contains(randNum)) {
				randNum = rand.nextInt(9) + 1;
			}
			randomList.add(randNum);
		}
		
		return randomList;
	}
	
	// This method is for generating a new puzzle.
	private void generateNewPuzzle() {
		ArrayList<Integer> randomList = generateRandomList();
		
		for (int row = 0; row < 9; row++) {
			for (int column = 0; column < 9; column++) {
				for (int i = 0; i < 9; i++) {
					if(internalSudokuArray[row][column] == randomList.get(i)) {
						// each number of internalSudokuArray is replaced by 
						// the number in the next index of the same number 
						// in the randomList.
						internalSudokuArray[row][column] = randomList.get((i + 1) % 9); // (i + 1) % 9 is for the condition that i == 8,
																						// to avoid index exception
						break;
					}
				}
			}
		}
	}
	
	// preset Sudoku matrix.
	private int internalSudokuArray[][]={
			{8,1,5,6,4,2,7,9,3},
			{9,6,3,1,8,7,5,4,2},
			{2,7,4,9,3,5,8,1,6},
			{3,8,9,5,1,6,2,7,4},
			{5,2,6,4,7,9,3,8,1},
			{1,4,7,3,2,8,9,6,5},
			{4,3,8,7,5,1,6,2,9},
			{6,5,2,8,9,4,1,3,7},
			{7,9,1,2,6,3,4,5,8}
	};
	
	// this Array is for reseting the game.
	private int resetSudokuArray[][]={
			{0,0,0,0,0,0,0,0,0},
			{0,0,0,0,0,0,0,0,0},
			{0,0,0,0,0,0,0,0,0},
			{0,0,0,0,0,0,0,0,0},
			{0,0,0,0,0,0,0,0,0},
			{0,0,0,0,0,0,0,0,0},
			{0,0,0,0,0,0,0,0,0},
			{0,0,0,0,0,0,0,0,0},
			{0,0,0,0,0,0,0,0,0}
	};
	
	public void paintComponent(Graphics comp) {
		super.paintComponent(comp);
	}

	GridPanel panel;
	private GridContainer container; // container of Grids
	private int numOfGridLeft; // the number of grid left in the game.
	private boolean isStart; // determine whether the game has started or not
	private Hint hint; // the hint of the game.
	private boolean finish; // determine whether the game has finished or not
	Clock clock; // clock from GUI
	Thread clockThread; // clockThread from GUI
	Score score; // score from GUI
}