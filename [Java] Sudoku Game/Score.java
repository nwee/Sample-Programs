// Created by JiaLong LIU, z3345987
//            Nelson Wee,  z3352078
//			  Molei Wang,  z3390139
// COMP2911, Project
// Date: 2 June 2013
// This class is for the score of the system.
// The score is based on the time that a user
// spent on solving each grid and also how much
// time a user spent to solve the game.
// Initial score is 1000. The score will never
// lower than 0.

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.border.Border;

public class Score extends JLabel {
	public Score() {
		super("1000");
		score = 1000; // initial score.
		
		// set score size.
		Dimension size = new Dimension(52, 30);
		setMinimumSize(size);
		setPreferredSize(size);
		
		setFont(new Font("Times New Roman", Font.BOLD, 18)); // set attributes of font.
		setForeground(new Color(239, 233, 85)); // set font color.
		
		// set border of the clock.
		Border line = BorderFactory.createMatteBorder(
				1, 1, 1, 1, new Color(79, 87, 239));
		Border newLine = BorderFactory.createMatteBorder(
				2, 2, 2, 2, new Color(255, 255, 255));
		Border compound = BorderFactory.createCompoundBorder(
                line, newLine);
		setBorder(compound);
	}
	
	// add number to current score.
	public void addScore(int number) {
		score += number;
		setText(Integer.toString(score));
	}
	
	// subtract number from current score.
	public void loseScore(int number) {
		score -= number;
		if (score > 0) {
			setText(Integer.toString(score));
		} else {
			score = 0;
			setText("0");
		}
	}
	
	// return the current score.
	public int getScore() {
		return score;
	}
	
	// reset score to initial state, which is 1000.
	public void reset() {
		setText("1000");
		score = 1000;
	}
	
	// This method mainly paints the background of score
	public void paintComponent(Graphics graph) {
		Graphics2D graph2D = (Graphics2D) graph;
		GradientPaint gp = new GradientPaint(50, 0, Color.red, 50, 30, Color.black);
		graph2D.setPaint(gp);
		graph.fillRect(0,0,getWidth(),getHeight());
		super.paintComponent(graph);
	}
	
	private int score; // the current score that a user have.
}