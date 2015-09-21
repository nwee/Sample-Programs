package gui;

import helper.GUI;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import logic.Tetromino;

@SuppressWarnings("serial")
public class NextGUI extends JPanel {
	private final PlayerGUI playerGUI;
	
	public NextGUI(PlayerGUI playerGUI) {
		this.playerGUI = playerGUI;
		
		setMinimumSize(new Dimension(4 * GUI.blockWidth + GUI.horizontalSpacing, 484));
		setPreferredSize(new Dimension(4 * GUI.blockWidth + GUI.horizontalSpacing, 484));
		setBorder(BorderFactory.createLineBorder(Color.RED));
	}
	
	public void paint(Graphics g) {
		super.paint(g);
		
		int yAccum = GUI.blockHeight;
		
		GUI.printSimpleString(g, "QUEUE", getWidth(), 0, yAccum, GUI.boldFont);
		
		yAccum += GUI.verticalGap;
		
		for (Tetromino tetromino : playerGUI.board.tetrominoQ) {
			final int xPos = getWidth() / 2 - tetromino.getWidth() * GUI.blockWidth / 2; 
			
			GUI.drawTetromino(g, tetromino, xPos, yAccum, GUI.blockWidth, GUI.blockHeight);
			yAccum += tetromino.getHeight() * GUI.blockHeight + GUI.verticalGap;
		}
	}
}
