package helper;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import javax.imageio.ImageIO;

import logic.Tetromino;

public class GUI {
	public final static int blockWidth = 15;
	public final static int blockHeight = 15;
	public final static int horizontalSpacing = blockWidth;
	public final static int verticalGap = blockHeight;
	
	public static Font boldFont = new Font(Font.MONOSPACED, Font.BOLD, 14);
	public static Font normalFont = new Font(Font.MONOSPACED, Font.PLAIN, 13);
	public static Font numberFont = new Font(Font.DIALOG, Font.PLAIN, 15);
	
	private static Image starImage = null;
	private static Image bombImage = null;
	
	private static final Color colors[] = {
		new Color(0, 0, 0),
		new Color(255, 86, 86),
		new Color(86, 255, 86),
		new Color(86, 86, 255),
		new Color(255, 255, 86),
		new Color(255, 86, 255),
		new Color(86, 223, 255),
		new Color(255, 150, 0),
		new Color(255, 255, 255)
	};

	/**
	 * Draws the rotated tetromino at the designated coordinates on the graphics
	 * object.
	 * 
	 * @param g
	 *            Java awt graphics objects to draw on.
	 * @param tetromino
	 *            The tetromino to draw.
	 * @param xPos
	 *            The x coordinate to draw the top left corner of the tetromino
	 *            at.
	 * @param yPos
	 *            The y coordinate to draw the top left corner of the tetromino
	 *            at.
	 * @param blockWidth
	 *            The width of each block in the tetromino.
	 * @param blockHeight
	 *            The height of each block in the tetromino.
	 */
	public static void drawTetromino(Graphics g, Tetromino tetromino, int xPos,
			int yPos, int blockWidth, int blockHeight) {
		
		Pos[] coords = tetromino.getCoordsPositive();
		for (Pos pos : coords) {
			int x = xPos + pos.x * blockWidth;
			int y = yPos + pos.y * blockHeight;
			drawBlock(g, tetromino.shape, x, y, blockWidth, blockHeight);
		}
	}

	public static void drawBlock(Graphics g, Tetromino.Shape shape, int xPos,
			int yPos, int blockWidth, int blockHeight) {

		if (shape == Tetromino.Shape.BOMB) {
			g.drawImage(getBombImage(), xPos + 1, yPos + 1, blockWidth - 2, blockHeight - 2, null);
			return;
		} else if (shape == Tetromino.Shape.SOLID) {
			g.setColor(Color.BLACK);
			g.fillRect(xPos, yPos, blockWidth, blockHeight);
			return;
		}

		Color color = colors[shape.ordinal()];
		g.setColor(color);
		g.fillRect(xPos + 1, yPos + 1, blockWidth - 2, blockHeight - 2);

		// shadow effect
		// g.setColor(color.brighter());
		g.setColor(colors[0]);
		g.drawLine(xPos, yPos + blockHeight - 1, xPos, yPos); // parallel lines
		g.drawLine(xPos, yPos, xPos + blockWidth - 1, yPos); // horizontal lines

		// g.setColor(color.darker());
		g.drawLine(xPos + 1, yPos + blockHeight - 1, xPos + blockWidth - 1,
				yPos + blockHeight - 1);
		g.drawLine(xPos + blockWidth - 1, yPos + blockHeight - 1, xPos
				+ blockWidth - 1, yPos + 1);

	}
	
	public static void printSimpleString(Graphics g, String s, int width, int XPos, int YPos, Font font) {
		g.setFont(font);
        int stringLen = (int) g.getFontMetrics().getStringBounds(s, g).getWidth();  
        int start = width/2 - stringLen/2;  
        g.drawString(s, start + XPos, YPos);
	}
	
	public static Image getStarImage() {
		if (starImage == null) {
			try {
				starImage = ImageIO.read(GUI.class.getResource("/data/star.png"));
			} catch (Exception e) {
			}
		}
		
		return starImage;
	}
	
	public static Image getBombImage() {
		if (bombImage  == null) {
			try {
				bombImage = ImageIO.read(GUI.class.getResource("/data/bomb.png"));
			} catch (Exception e) {
			}
		}
		
		return bombImage;
	}
}
