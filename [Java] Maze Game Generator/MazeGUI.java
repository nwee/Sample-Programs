import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

/**COMP2911 Project: Maze Runner 2014
 * This class that manages and draws the map and players 
 * 
 * @author Nelson Wee, z3352078
 * @author Renmark Marte, z3464929
 * @author Sung Min Park, z3278712
 * @author Luna Pradhananga, z3358423 
 *
 */
@SuppressWarnings("serial")
public class MazeGUI extends JPanel {
	private int numPlayers;
	private HashMap<Integer, Player> players;
	
	private final int boardW = 600;
	private final int boardH = 600;
	// maze dimension
	private final int ROW = MapGenerator.MAPSIZE;
	private final int COL = MapGenerator.MAPSIZE;

	// 500 / ROW
	private int BLOCK_SIZE = 13;
	int[][] mazeArray;
	private static Image pathImg;
	private static Image characterOneImg;
	private static Image characterTwoImg;
	private static Image charactersTogetherImg;
	public static boolean hint = false;
	public PathFinding p; 
	
	public MazeGUI(int numPlayers) {
		setMinimumSize(new Dimension(boardW, boardH));
		setMaximumSize(new Dimension(boardW, boardH));
		setPreferredSize(new Dimension(boardW, boardH));
		this.setBackground(Color.decode("#19316b"));
		
		//Map Generation
		mazeArray = new int[ROW][COL];
		mazeArray = MapGenerator.maze;	
		//Player Setting Generation
		this.numPlayers = numPlayers;
		p = new PathFinding();
		
		players = new HashMap<Integer, Player>();
				
		int startX = MapGenerator.start.getX()*BLOCK_SIZE;
		int startY = MapGenerator.start.getY()*BLOCK_SIZE;
		players.put(1, new Player(startX, startY));
		if (numPlayers == 2) {
			players.put(2,new Player(startX, startY));
		}
		this.pathImg = Toolkit.getDefaultToolkit().createImage("./files/Path.jpg");
		this.characterOneImg = Toolkit.getDefaultToolkit().createImage("./files/CharacterOne.jpg");
		this.characterTwoImg = Toolkit.getDefaultToolkit().createImage("./files/CharacterTwo.jpg");
		this.charactersTogetherImg = Toolkit.getDefaultToolkit().createImage("./files/CharacterTogether.jpg");
	}
	
	/**
	 * This method paints the maze
	 */
	public void paint(Graphics g) {
		super.paint(g);
		
		if (GameGUI.isPaused) { //paint the pauseblock
			Graphics2D g4 = (Graphics2D) g;
			g4.setColor(Color.decode("#19316b"));
			Rectangle2D pauseRec = new Rectangle2D.Double(0, 0, 600, 600);
			g4.fill(pauseRec);
		}
			
		else { //paint the maze
			if (GameGUI.isHint) {
				p.path(mazeArray, MapGenerator.start, MapGenerator.end, ROW, COL);
				for (int i = 0; i < p.getPath().size(); i++) {
					//System.out.println("Path X "+MainMenu.p.getPath().get(i).getX()+" Path Y "+ MainMenu.p.getPath().get(i).getY());
					drawBlock(g, p.getPath().get(i).getX()*BLOCK_SIZE, p.getPath().get(i).getY()*BLOCK_SIZE, BLOCK_SIZE, BLOCK_SIZE);						
				}
			}
			int coordX = 0;
			int coordY = 0;
			for (int i = 0; i < ROW; i++) {
				for (int i2 = 0; i2 < COL; i2++) {
					if (mazeArray[i][i2] == 1) {
						coordX = i * BLOCK_SIZE;
						coordY = i2 * BLOCK_SIZE;
						g.drawImage(pathImg, coordX, coordY, BLOCK_SIZE, BLOCK_SIZE, null);
					}
				}
			}
		}	
	}

	/**
	 * This method paints the players
	 */
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (!GameGUI.isPaused) {
			Graphics2D g2 = (Graphics2D) g;
			Player one = players.get(1);
			g2.drawImage(characterOneImg, one.getX(), one.getY(), BLOCK_SIZE, BLOCK_SIZE, null);
			
			if (numPlayers == 2) {
				Graphics2D g3 = (Graphics2D) g;
				Player two = players.get(2);
				g2.drawImage(characterTwoImg, two.getX(), two.getY(), BLOCK_SIZE, BLOCK_SIZE, null);
				
				//The case where both Players are on the same tile
				if ((one.getX() == two.getX()) && (one.getY() == two.getY())){
					g2.drawImage(charactersTogetherImg, two.getX(), two.getY(), BLOCK_SIZE, BLOCK_SIZE, null);
				}
			}
		}		
	}
	
	public static void drawBlock(Graphics g, int xPos, int yPos,
			int blockWidth, int blockHeight) {
		g.setColor(Color.yellow);
		g.fillRect(xPos, yPos, blockWidth, blockHeight);
		//g.setColor(Color.BLACK);
		g.drawRect(xPos, yPos, blockWidth, blockHeight);
		/*
		// shadow effect
		g.drawLine(xPos + 1, yPos + blockHeight - 1, xPos + blockWidth - 1,
				yPos + blockHeight - 1);
		g.drawLine(xPos + blockWidth - 1, yPos + blockHeight - 1, xPos
				+ blockWidth - 1, yPos + 1);
				*/
	}
	
	/**
	 * This method modifies the coordinates to reflect the move
	 * @param playerNum
	 */
	public void moveUp(int playerNum) {
		int playerX = players.get(playerNum).getX();
		int playerY = players.get(playerNum).getY();
		if (isValidMove(0, -1,playerX,playerY)) {
			players.get(playerNum).changeY(-BLOCK_SIZE);
		}
	}

	/**
	 * This method modifies the coordinates to reflect the move
	 * @param playerNum
	 */
	public void moveDown(int playerNum) {
		int playerX = players.get(playerNum).getX();
		int playerY = players.get(playerNum).getY();
		if (isValidMove(0, 1,playerX,playerY)) {
			players.get(playerNum).changeY(BLOCK_SIZE);
		}
	}

	/**
	 * This method modifies the coordinates to reflect the move
	 * @param playerNum
	 */
	public void moveLeft(int playerNum) {
		int playerX = players.get(playerNum).getX();
		int playerY = players.get(playerNum).getY();
		if (isValidMove(-1, 0,playerX,playerY)) {
			players.get(playerNum).changeX(-BLOCK_SIZE);
		}
	}

	/**
	 * This method modifies the coordinates to reflect the move
	 * @param playerNum
	 */
	public void moveRight(int playerNum) {
		int playerX = players.get(playerNum).getX();
		int playerY = players.get(playerNum).getY();
		if (isValidMove(1, 0,playerX,playerY)) {
			players.get(playerNum).changeX(BLOCK_SIZE);
		}
	}

	/**
	 * This method checks if the move attempted is a valid move
	 * @param xChange
	 * @param yChange
	 * @param currX
	 * @param currY
	 * @return
	 */
	private boolean isValidMove(int xChange, int yChange, int currX, int currY) {
		int mazeX = currX / BLOCK_SIZE + xChange;
		int mazeY = currY / BLOCK_SIZE + yChange;
		if (mazeX >= 0 && mazeX < ROW && mazeY >= 0 && mazeY < ROW) { // not past map bounds
			if (mazeArray[mazeX][mazeY] == 0)
				return true;
		}
		return false;
	}
	
	/**
	 * This method checks if the point is an end point and returns boolean
	 * @param playerNum
	 * @return
	 */
	public boolean isEndPoint(int playerNum) {
		int playerX = players.get(playerNum).getX();
		int playerY = players.get(playerNum).getY();
		int endY = MapGenerator.end.getY()*BLOCK_SIZE;
		int endX = MapGenerator.end.getX()*BLOCK_SIZE;
		
		if (playerX == endX && playerY==endY ) {
			if (players.get(playerNum).getScore() == 0) {
				players.get(playerNum).calcScore(GameGUI.t.getTime());
			}
			return true;
		}
		return false;
	}
	
	/**
	 * This method returns a player with the associated hash 
	 * @param playerNum
	 * @return
	 */
	public Player getPlayer(int playerNum) {
		return players.get(playerNum);
	}
}