package gui;

import helper.GUI;
import helper.Pos;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import logic.Board;
import logic.Game;
import logic.Tetromino;

@SuppressWarnings("serial")
public class BoardGUI extends JPanel {
	private final int boardW = 140;
	private final int boardH = 484;
	private final Board board;
	
	public BoardGUI(Board board) {
		this.board = board;
		setBackground(Color.white);
		setMinimumSize(new Dimension(boardW, boardH));
		setMaximumSize(new Dimension(boardW, boardH));
		setPreferredSize(new Dimension(boardW, boardH));
		setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
	}
	
	int blockWidth() { return  (int) (getSize().getWidth() / board.width); }
    int blockHeight() { return (int) (getSize().getHeight() / board.height); }
    
	public void paint(Graphics g) {
		super.paint(g);
		
		if (board.game.state == Game.State.GAME_OVER) {
			int yAccum = getHeight() / 2 - 20;
			String str = "GAME OVER.";
			GUI.printSimpleString(g, str, getWidth(), 0, yAccum, new Font(Font.SANS_SERIF, Font.PLAIN, 18));
			
			yAccum += 20;
			
			str = "";
			if (board.game.gameOptions.getNumPlayers() == 1) {
				str += "YOUR SCORE: " + board.player.score.value;
			} else {
				str += " YOU";
				str += board.lost ? " LOSE" : " WIN";
				str += "!";
			}
			
			GUI.printSimpleString(g, str, getWidth(), 0, yAccum, new Font(Font.SANS_SERIF, Font.PLAIN, 18));
			return;
		}

		final int xOffset = (this.getWidth() - board.width * blockWidth()) / 2;
		final int yOffset = this.getHeight() - board.height * blockHeight();
        for (int y = 0; y < board.height; ++y) {
            for (int x = 0; x < board.width; ++x) {
                Tetromino.Shape shape = board.tetrominos[y][x];
                if (shape != Tetromino.Shape.EMPTY) {
                	int xPos = x * blockWidth();
                	int yPos = (board.height - 1 - y) * blockHeight();
                	drawBlock(g, xOffset + xPos, yOffset + yPos, shape);
                }
            }
        }

        if (board.currTetromino != null && board.currTetromino.shape != Tetromino.Shape.EMPTY) {
        	Pos[] positions = board.currTetromino.getCoords();
        	
            for (int i = 0; i < positions.length; ++i) {
            	int xPos = (board.currX + positions[i].x) * blockWidth();
            	int yPos = (board.height - 1 - (board.currY + positions[i].y)) * blockHeight();
            	drawBlock(g, xOffset + xPos, yOffset + yPos, board.currTetromino.shape);
            }
        }
    }
	
	private void drawBlock(Graphics g, int x, int y, Tetromino.Shape shape) {
		GUI.drawBlock(g, shape, x, y, blockWidth(), blockHeight());
    }
}
