package gui;

import helper.GUI;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JPanel;

import logic.Tetromino;
import menu.GameOptions.Mode;

@SuppressWarnings("serial")
public class InfoGUI extends JPanel {
	private final PlayerGUI playerGUI;
	
	public InfoGUI(PlayerGUI playerGUI) {
		this.playerGUI = playerGUI;

		setMinimumSize(new Dimension(70, 484));
		setMaximumSize(new Dimension(70, 484));
		setPreferredSize(new Dimension(70, 484));
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setBorder(BorderFactory.createLineBorder(Color.RED));
	}
	
	public void paint(Graphics g) {
		super.paint(g);
		final Tetromino storedTetromino = playerGUI.board.storedTetromino;
		
		int yAccum = GUI.verticalGap;
		
		GUI.printSimpleString(g, "STORAGE", getWidth(), 0, yAccum, GUI.boldFont);
		yAccum += GUI.verticalGap;
		
		final int storageWidth = 4 * GUI.blockWidth;
		final int storageHeight = 4 * GUI.blockHeight;
		final int storageXOffset = (getWidth() - storageWidth) / 2;
		g.drawRect(storageXOffset, yAccum, storageWidth, storageHeight);
		
		if (storedTetromino == null) {
		} else {
			final int xPos = getWidth() / 2 - storedTetromino.getWidth() * GUI.blockWidth / 2; 
			final int yOffset = (4 * GUI.blockHeight - storedTetromino.getHeight() * GUI.blockHeight) / 2;
			GUI.drawTetromino(g, storedTetromino, xPos, yOffset + yAccum, GUI.blockWidth, GUI.blockHeight);
		}
		yAccum += 4 * GUI.blockHeight;
		yAccum += 2 * GUI.verticalGap;
		
		String infoString = new String();
		switch (playerGUI.player.game.gameOptions.mode) {
		case RUSH:
			final int rushLimit = playerGUI.player.game.gameOptions.rushLimit;
			infoString += rushLimit + " line";
			if (rushLimit != 1)
				infoString += "s";
			
			break;
		case TIMED:
			final int minutes = (playerGUI.player.game.gameOptions.timeLimit + 30) / 60;
			infoString += String.valueOf(minutes) + " min";
			if (minutes != 1)
				infoString += "s";
			
			break;
		default:
			break;
		
		}
		GUI.printSimpleString(g, "MODE", getWidth(), 0, yAccum, GUI.boldFont);
		yAccum += GUI.verticalGap;
		GUI.printSimpleString(g, playerGUI.player.game.gameOptions.mode.getName(), getWidth(), 0, yAccum, GUI.normalFont);
		yAccum += GUI.verticalGap;
		if (!infoString.isEmpty()) {
			GUI.printSimpleString(g, infoString, getWidth(), 0, yAccum, GUI.normalFont);
			yAccum += GUI.verticalGap;
		}
		yAccum += GUI.verticalGap;
		
		GUI.printSimpleString(g, "LINES", getWidth(), 0, yAccum, GUI.boldFont);
		yAccum += GUI.verticalGap;
		GUI.printSimpleString(g, String.valueOf(playerGUI.board.numLinesCleared), getWidth(), 0, yAccum, GUI.numberFont);
		yAccum += 2 * GUI.verticalGap;
		
		GUI.printSimpleString(g, "COMBOS", getWidth(), 0, yAccum, GUI.boldFont);
		yAccum += GUI.verticalGap;
		if (playerGUI.board.numCombos == 0) {
			GUI.printSimpleString(g, "0", getWidth(), 0, yAccum, GUI.numberFont);
			yAccum += GUI.verticalGap;
		} else {
			int xAccum = 5;
			for (int i = 0; i < playerGUI.board.numCombos; ++i) {
				if (i % 3 == 0) {
					xAccum = 5;
					yAccum += GUI.blockHeight;
				}
				g.drawImage(GUI.getStarImage(), xAccum, yAccum - GUI.blockHeight + 2, GUI.blockWidth, GUI.blockHeight, null);
				xAccum += GUI.blockWidth + 5;
			}
			yAccum += GUI.verticalGap;
		}
		yAccum += GUI.verticalGap;
		
		if (playerGUI.player.game.gameOptions.mode == Mode.FIGHT) {
			GUI.printSimpleString(g, "ATTACKS", getWidth(), 0, yAccum, GUI.boldFont);
			yAccum += GUI.verticalGap;

			if (playerGUI.board.attacks == 0) {
				GUI.printSimpleString(g, "0", getWidth(), 0, yAccum, GUI.numberFont);
				yAccum += GUI.verticalGap;
			} else {
				int xAccum = 5;
				for (int i = 0; i < playerGUI.board.attacks; ++i) {
					if (i % 3 == 0) {
						xAccum = 5;
						yAccum += GUI.blockHeight;
					}
					g.drawImage(GUI.getBombImage(), xAccum, yAccum - GUI.blockHeight + 2, GUI.blockWidth, GUI.blockHeight, null);
					xAccum += GUI.blockWidth + 5;
				}
				yAccum += GUI.verticalGap;
			}
			yAccum += GUI.verticalGap;
		}
		
		GUI.printSimpleString(g, "TOTAL", getWidth(), 0, yAccum, GUI.boldFont);
		yAccum += 10;
		GUI.printSimpleString(g, "LINES", getWidth(), 0, yAccum, GUI.boldFont);
		yAccum += GUI.verticalGap;
		GUI.printSimpleString(g, String.valueOf(playerGUI.board.totalLinesCleared), getWidth(), 0, yAccum, GUI.numberFont);
		yAccum += 2 * GUI.verticalGap;
		
		if (playerGUI.player.game.gameOptions.mode == Mode.LEVEL) {
			GUI.printSimpleString(g, "LEVEL", getWidth(), 0, yAccum, GUI.boldFont);
			yAccum += GUI.verticalGap;
			GUI.printSimpleString(g, String.valueOf(playerGUI.player.level), getWidth(), 0, yAccum, GUI.numberFont);
			yAccum += 2 * GUI.verticalGap;
		}
		
		GUI.printSimpleString(g, "SCORE", getWidth(), 0, yAccum, GUI.boldFont);
		yAccum += GUI.verticalGap;
		GUI.printSimpleString(g, String.valueOf(playerGUI.player.score.value), getWidth(), 0, yAccum, GUI.numberFont);
		yAccum += 2 * GUI.verticalGap;
	}
}
