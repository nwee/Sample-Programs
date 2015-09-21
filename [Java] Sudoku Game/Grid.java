// Created by JiaLong LIU, z3345987
//            Nelson Wee,  z3352078
//			  Molei Wang,  z3390139
// COMP2911, Project
// Date: 2 June 2013
// This class is for the grid of the system.
// It stores all attributes of grid.

import java.awt.*;
import javax.swing.*;
import javax.swing.border.Border;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.Timer;

class Grid extends JTextField {
	GradientPaint gp = new GradientPaint(50F, 10F, new Color(216, 236, 239),
			50F, 50F, new Color(200, 218, 250), true);
	
	// constructor
	public Grid(int x, int y) {
		super();
		this.x = x;
		this.y = y;
		value = -1;
		oldGP = gp;
		oldValue = getText();
		setOpaque(false);
		setHorizontalAlignment(JTextField.CENTER); // make the Text appear in the center of the grid.
		setDocument(new GridInputLimitation()); // only allow 
	}

	// get the x value of the grid, which is column in the GridPanel.
	public int getXValue() {
		return x;
	}
	
	// get the y value of the grid, which is row in the GridPanel.
	public int getYValue() {
		return y;
	}
	
	// return the number value of the grid.
	public int getValue() {
		if (!getText().equals("")) {
			value = Integer.parseInt(getText());
		} else {
			value = -1;
		}
		return value;
	}
	
	// paint the border and background for those unEditable grid.
	public void unEditablePaint() {
		Border newLine = BorderFactory.createMatteBorder(
				1, 1, 1, 1, new Color(100, 100, 100));
		Border compound = BorderFactory.createCompoundBorder(
				this.getBorder(), newLine);
		setBorder(compound);
		gp = new GradientPaint(50F, 10F, new Color(180, 206, 189),
				50F, 50F, new Color(220, 228, 250), true);
	}
	
	// record the old border.
	// It is for the case that when a user has already opened a game
	// and want to start the new game, which makes the system need to
	// clean reset border.
	public void storeBorder() {
		oldBorder = getBorder();
	}

	// reset the Paint for starting a new game.
	public void resetPaint() {
		setBorder(oldBorder);
		gp = oldGP;
	}
	
	
	// store the old value. This serves for GridPanel to make
	// decision for adding score.
	public void storeOldValue() {
		oldValue = getText();
	}
	
	// return the old value. This serves for GridPanel to make
	// decision for adding score.
	public String getOldValue() {
		return oldValue;
	}
	
	// reset the old value. This is for starting a new Game.
	public void resetOldValue() {
		oldValue = "";
	}
	
	// record the current value of the grid and make the number
	// of grid disappear when it is pausing.
	public void startPause() {
		pauseValue = getText();
		setText("");
	}
	
	// resume the value of the grid after paused.
	public void resume() {
		setText(pauseValue);
	}
	
	// this method mainly paint the background of grid.
	public void paintComponent(Graphics graph) {
		Graphics2D graph2D = (Graphics2D) graph;
		graph2D.setPaint(gp);
		graph.fillRect(0,0,getWidth(),getHeight());
		super.paintComponent(graph);
	}
	
	private int x; // column in the GridPanel.
	private int y; // row in the GridPanel.
	private int value; // the number of grid.
	private String oldValue; // the old number before change to new number in the grid .
	private String pauseValue; // the number before pausing in the grid.
	private Border oldBorder; // the old border of grid.
	private GradientPaint oldGP; // the old background color of grid.
}