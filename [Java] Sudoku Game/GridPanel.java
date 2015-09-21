// Created by JiaLong LIU, z3345987
//            Nelson Wee,  z3352078
//			  Molei Wang,  z3390139
// COMP2911, Project
// Date: 2 June 2013
// This class is for GridPanel in the MidPanel.
// It interacts with MidPanel class.
// It mainly manipulate of users' input, score sub system and
// determine whether a user win or not.

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.*;
import javax.swing.border.Border;

public class GridPanel extends JPanel implements KeyListener {
	
	// constructor for initialization.
	public GridPanel(GridContainer container, Hint hint, Clock clock, 
			Thread clockThread, Score score) {
		super();
		numOfGridLeft = 81;
		width = 0;
		height = 0;
		hintX = 0;
		hintY = 0;
		finish = true;
		this.container = container;
		this.hint = hint;
		this.clock = clock;
		this.clockThread = clockThread;
		this.score = score;
		setLayout(new GridLayout(9, 9, 1, 1));
		
		// Setting attributes, color, listener, border for grids.
		// It sets up the entire grid panel in the GUI.
		for (int row = 0; row < 9; row++) {
			for (int col = 0; col < 9; col++) {
			    Grid grid = new Grid(col, row);
				grid.setFont(new Font("Times New Roman", Font.BOLD, 30));
				grid.setForeground(new Color(179, 103, 5));
				grid.addKeyListener(this);
				container.add(grid);
				Border line = BorderFactory.createMatteBorder(
						1, 1, 1, 1, new Color(79, 87, 239));
				Border newLine = BorderFactory.createMatteBorder(
						0, 0, 0, 0, new Color(196, 32, 32));
				if (row % 3 == 2) {
				    newLine = BorderFactory.createMatteBorder(
							0, 0, 2, 0, new Color(196, 32, 32));
				} else if (row == 0) {
					newLine = BorderFactory.createMatteBorder(
							2, 0, 0, 0, new Color(196, 32, 32));
				}
				if (col % 3 == 0) {
					if (row % 3 == 2) {
						newLine = BorderFactory.createMatteBorder(
								0, 2, 2, 0, new Color(196, 32, 32));
					} else if (row == 0) {
						newLine = BorderFactory.createMatteBorder(
								2, 2, 0, 0, new Color(196, 32, 32));
					} else {
						newLine = BorderFactory.createMatteBorder(
								0, 2, 0, 0, new Color(196, 32, 32));
					}
				} else if (col == 8) {
					if (row % 3 == 2) {
						newLine = BorderFactory.createMatteBorder(
								0, 0, 2, 2, new Color(196, 32, 32));
					} else if (row == 0) {
						newLine = BorderFactory.createMatteBorder(
								2, 0, 0, 2, new Color(196, 32, 32));
					} else {
						newLine = BorderFactory.createMatteBorder(
								0, 0, 0, 2, new Color(196, 32, 32));
					}
				}
				Border compound = BorderFactory.createCompoundBorder(
	                          line, newLine);
				grid.setBorder(compound); 
				grid.storeBorder();
				add(grid);
			}
		}
		tempGrid = container.getGrid(0, 0); // This variable is used to store the specific grid of keyPress event
	}
	
	// return an ArrayList contain all possible number from 1 to 9 for a grid.
	public ArrayList<Integer> checkPossibility(Grid grid) {
		ArrayList<Integer> possibility = new ArrayList<Integer>();
		String oldValue = grid.getText();
		for (int i = 1; i < 10; i++) {
			grid.setText(Integer.toString(i));
			if (container.checkGrid(grid)) {
				possibility.add(i);
			}
		}
		grid.setText(oldValue);
		
		return possibility;
	}
	
	// set the numOfGridLeft variable.
	public void setNumOfGridLeft(int num) {
		numOfGridLeft = num;
	}
	
	// set the finish variable.
	public void setFinish(boolean finish) {
		this.finish = finish;
	}
	
	// Close the hint.
	// The hint will blink in the bottom of GUI.
	private void closeHint() {
		width = 0;
		height = 0;
		repaint();
	}
	
	// add score for a user.
	private void addScore() {
		int timeDistance = clock.getTimeDistance();
		// If a user input a valid number in 5 second.
		// then he will get 100 points.
		if (timeDistance <= 5) {
			score.addScore(100);
		} else {
			// after 5 second, each second a user will get 
			// 10 less points from entering the valid input number.
			// 10 points is the minimum point that a user can get.
			int scoreToAdd = 100 - (timeDistance - 5) * 10;
			if (scoreToAdd > 10) {
				score.addScore(scoreToAdd);
			} else {
				score.addScore(10);
			}
		}
	}
	
	// lose 100 points by remove an input number from grid.
	// the lowest point for a user is 0.
	// so the score won't lower than 0.
	private void loseScore() {
		score.loseScore(100);
	}

	// TimerTask is for hint
	private TimerTask newTime() {
		return  new TimerTask() {
			public void run() {
				width += 3;
				height += 2;
				repaint();
				
				if (width > 110) {
					cancel();
				}
			}
		};
	}
	
	// This TimerTask is for the condition that a user open 
	// a hint of grid in the right most column.
	private TimerTask newTimeRightmost() {
		return  new TimerTask() {
			public void run() {
				width += 3;
				height += 2;
				hintX -= 3;
				repaint();
				
				if (width > 110) {
					cancel();
				}
			}
		};
	}
	
	// This TimerTask is for the condition that a user open 
	// a hint of grid in the bottom row.
	private TimerTask newTimeBottom() {
		return  new TimerTask() {
			public void run() {
				width += 3;
				height += 2;
				hintY -= 3;
				repaint();
				
				if (width > 110) {
					cancel();
				}
			}
		};
	}
	
	// This TimerTask is for the condition that a user open 
	// a hint of grid in the right most of the bottom row.
	private TimerTask newTimeRightMostBottom() {
		return  new TimerTask() {
			public void run() {
				width += 3;
				height += 2;
				hintY -= 3;
				hintX -= 3;
				repaint();
				
				if (width > 110) {
					cancel();
				}
			}
		};
	}	
	
	public void keyPressed(KeyEvent event) {
		// TODO Auto-generated method stub
		tempGrid = (Grid) event.getSource(); // store the key press grid to avoid a user quickly 
											 // click other grid using mouse before releasing 
											 // keyBoard click, which might cause a bug occur.
	}

	// For each key released event.
	public void keyReleased(KeyEvent event) {
		// TODO Auto-generated method stub
		// If the game is not finish, then keyReleased events will be manipulated.
		if (!finish) {
			// If the input number is invalid, it will be reset.
			if (!container.checkGrid(tempGrid)) {
				tempGrid.setText("");
			
			// If a user type a valid number in a blank grid.
			} else if (tempGrid.getOldValue().equals("") && !tempGrid.getText().equals("")) {
				numOfGridLeft--;
				tempGrid.storeOldValue();
				addScore();
				closeHint();
				hint.setStatus(false);
				
			// If a user remove a number in a grid.
			} else if (!tempGrid.getOldValue().equals("") && tempGrid.getText().equals("")){
				tempGrid.storeOldValue();
				numOfGridLeft++;
				loseScore();
				closeHint();
				hint.setStatus(false);
			
			// If a user change a number from a grid.
			} else if (!tempGrid.getOldValue().equals(tempGrid.getText())) {
				tempGrid.storeOldValue();
				addScore();
				loseScore();
				closeHint();
				hint.setStatus(false);
			}
			
			// If numOfGRidLeft == 0, then the user win the game.
			if (numOfGridLeft == 0) {
				score.addScore(1000);
				clockThread.suspend();
				int timeSpent = clock.getTime();
				// If a user finished the game within 10 minutes, he will get 1000 bonus points.
				if (timeSpent <= 600) {
					score.addScore(1000);
				// If a user finished the game within 20 minutes, he will get 800 bonus points.
				} else if (timeSpent <= 1200) {
					score.addScore(800);
				// If a user finished the game within 30 minutes, he will get 500 bonus points.
				} else if (timeSpent <= 1800) {
					score.addScore(500);
				}
				// Pop up the win windows.
				JOptionPane.showMessageDialog(null, "Congratulation!\n" + 
						"You won the game! You are Awesome!\n" + "You got " + score.getScore() + " score!");
				finish = true; // set the game become to finish.
				hint.setText(""); // close hint.
				container.win(); // set the game become win.
			}
		}

	}

	// Ignore this method.
	public void keyTyped(KeyEvent event) {
		// TODO Auto-generated method stub
		// If the game is not finish, then keyTyped events will be manipulated.
		if (!finish) {
			Grid grid = (Grid) event.getSource();
			// hotkey 'h' is for opening the hint or closing the hint.
			if (event.getKeyChar() == 'h' && grid.isEditable()) {
				// if the hint is not open.
				if (!hint.getStatus()) {
					hint.setStatus(true); // set it become to open.
					
					// Codes before is to make the hint open smooth and 
					// also consider the location for the hint to open.
					width = 10;
					height = 10;
					hintX = 38 + grid.getXValue() * 53;
					hintY = 33 + grid.getYValue() * 50;
					ArrayList<Integer> possibility = checkPossibility(grid);
					String hintText = new String();
					for (Integer item : possibility) {
						hintText += Integer.toString(item) + " ";
					}
					hint.setText(hintText);
					if (grid.getXValue() >= 7 && (grid.getYValue() < 7)) {
						hintX -= 25;
						timer.scheduleAtFixedRate(newTimeRightmost(), 5, 7);
					} else if (grid.getYValue() >= 7 && grid.getXValue() < 7){
						timer.scheduleAtFixedRate(newTimeBottom(), 5, 7);	
					} else if (grid.getXValue() >= 7 && grid.getYValue() >=7) {
						hintX -= 23;
						timer.scheduleAtFixedRate(newTimeRightMostBottom(), 5, 7);
					} else {
						timer.scheduleAtFixedRate(newTime(), 5, 7);
					}
				} else {
					closeHint(); // close the hint.
					hint.setStatus(false); // set the hint status become close.
				}
			}
		}
	}
	
	public void paintComponent(Graphics comp) {
		super.paintComponent(comp);
		hint.setBounds(hintX, hintY, width, height);
	}
	
	private GridContainer container; // container of Grids
	private int numOfGridLeft; // number of grid left.
	private Hint hint;
	private int hintX; // x-coordinate of hint.
	private int hintY; // y-coordinate of hint.
	private int width; // width of hint.
	private int height; // height of hint
	private boolean finish;
	Clock clock;
	Thread clockThread;
	Score score;
	Timer timer = new Timer();
	Grid tempGrid;
}