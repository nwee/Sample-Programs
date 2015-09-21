package gui;

import helper.MultiKeyPressListener;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.UIManager;

import logic.Game;
import logic.Player;
import logic.timers.GameTimer;
import menu.Tutorial;
import menu.GameOptions.AiDiff;

@SuppressWarnings("serial")
public class GameGUI extends JFrame {
	public JLabel statusBar;
	GameTimer gameTimer;
	private final Game game;
	private int numPlayers = 1;
	public boolean isPaused = false;

	public GameGUI(final Game game) {

		this.game = game;

		setTitle("Tetris Fight Game");
	    setBackground(Color.gray );  //maybe can add here picture
	    setDefaultCloseOperation(EXIT_ON_CLOSE);
	
	    BorderLayout layout = new BorderLayout();
	    this.setLayout(layout);
		this.setFocusable(true);
	    this.addKeyListener(new TAdapter());

	    addWindowListener(new WindowAdapter() {
		    public void windowClosing(WindowEvent e) {
		    	game.audio.stopMusic();
		    	try {
					Thread.sleep(100);
				} catch (InterruptedException e1) {
				}
		    	
		    	System.exit(0);
		    }
	    });
	}
	
	private JPanel createMenuBar() {
		JPanel menu = new JPanel();
		menu.setLayout(new BorderLayout());
		
		JMenuBar menuBar = new JMenuBar(); 
		JMenu file = new JMenu("File");
		JMenuItem mainMenu = new JMenuItem("Main Menu");
		JMenuItem pause = new JMenuItem("Pause");
		JMenuItem exit = new JMenuItem("Exit");

		file.add(mainMenu);
		file.add(pause);
		file.add(exit);	
		
		mainMenu.addActionListener(new ActionListener()	{
			public void actionPerformed(ActionEvent event) {
				game.audio.stopMusic();
				setVisible(false);
			    dispose();
				game.pause();
				Game.main(null);
			}
		});
		mainMenu.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N,ActionEvent.CTRL_MASK));
		
		pause.addActionListener(new ActionListener()	{
			public void actionPerformed(ActionEvent event) {
				game.pause();
			}
		});
		
		exit.addActionListener(new ActionListener()	{
			public void actionPerformed(ActionEvent event) {
		    	System.exit(0);
			}
		});
		exit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q,ActionEvent.CTRL_MASK));
		
		JMenu help = new JMenu("Help");
		JMenuItem about = new JMenuItem("About");
		JMenuItem howToPlay = new JMenuItem("How to Play");
		JMenuItem controls = new JMenuItem("Controls");
		help.add(about);
		about.addActionListener(new ActionListener()	{
			public void actionPerformed(ActionEvent event) {
				game.pause();
				Tutorial.about();
				game.togglePause();
		    	
			}
		});
		help.add(howToPlay);
		howToPlay.addActionListener(new ActionListener()	{
			public void actionPerformed(ActionEvent event) {
				game.pause();
				Tutorial.basic();
				game.togglePause();
		    	
			}
		});
		
		help.add(controls);
		controls.addActionListener(new ActionListener()	{
			public void actionPerformed(ActionEvent event) {
				game.pause();
				Tutorial.controls();
				game.togglePause();
		    	
			}
		});
		
		
		menuBar.add(file);
		menuBar.add(help);
		menu.add(menuBar);
		
		return menu;
	}
		
	public void init(List<Player> players) {
		try {
	        UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
	    } catch (Exception evt) {
	    	// Do nothing
	    }
		
		JPanel gamePanel = new JPanel();
		GridLayout gameLayout = new GridLayout(1, 4, 100, 0);
		gamePanel.setLayout(gameLayout);
		
		for (Player player : players)
			gamePanel.add(player.playerGUI);

		
		this.add("North",createMenuBar());
		this.add("Center",gamePanel);
	    this.pack();
	    centreWindow(this);
	    
	    numPlayers = players.size();
	    //1-Player or 2-Player
	    if (numPlayers == 1) 
	    	this.setSize(405, 600);
	    else 
	    	this.setSize(850, 600);
	    
	    this.setVisible(true);
	    //this.setResizable(false);
	}

	public static void centreWindow(Window frame) {
	    Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
	    int x = (int) ((dimension.getWidth() - frame.getWidth()) / 2);
	    int y = (int) ((dimension.getHeight() - frame.getHeight()) / 2);
	    frame.setLocation(x, y);
	}

	class TAdapter extends MultiKeyPressListener {
		
		 @Override
	    public synchronized void keyPressed(KeyEvent e) {
	        pressed.add(e.getKeyCode());
	        if (pressed.size() >= 1) {
	        	for (int keycode : pressed) {
	    	        if (game.state == Game.State.GAME_OVER)
	    				return;
	    			
	    			if (keycode == KeyEvent.VK_P) {
	    				game.togglePause();
	    				return;
	    			}
	    			
	    			// Don't allow players to move pieces if the game is paused.
	    			if (game.isPaused)
	    				return;
	    			
	    			if ((numPlayers == 1) || game.gameOptions.ai != AiDiff.HUMAN ) {
	    				
	    				switch (keycode) {
	    				
	    				/*
	    				 * Solo Mode:
	    				 * Arrows - Move, Rotate
	    				 * Shift - Swap
	    				 * Space - Hard Drop
	    				 */
	    				
	    				case KeyEvent.VK_LEFT:
	    					game.players.get(0).board.shiftLeft();
	    					break;
	    				case KeyEvent.VK_RIGHT:
	    					game.players.get(0).board.shiftRight();
	    					break;
	    				case KeyEvent.VK_DOWN:
	    					game.players.get(0).board.shiftDownNewPiece();
	    					break;
	    				case KeyEvent.VK_UP:
	    					game.players.get(0).board.rotateRight();
	    					break;
	    				case KeyEvent.VK_SHIFT:
	    					game.players.get(0).board.trySwap();
	    					break;
	    				case KeyEvent.VK_SPACE:
	    					game.players.get(0).board.hardDrop();
	    					break;
	    				}
	    			}
	    			else {	//2p			
	    				switch (keycode) {
	    								
	    				/* 
	    				 * Player 1: 
	    				 * WASD - Move, Rotate
	    				 * Shift - Swap
	    				 * C - Hard Drop 		
	    				 */
	    				case KeyEvent.VK_W:
	    					game.players.get(0).board.rotateLeft();
	    					break;
	    				case KeyEvent.VK_A:
	    					game.players.get(0).board.shiftLeft();
	    					break;
	    				case KeyEvent.VK_S:
	    					game.players.get(0).board.shiftDownNewPiece();
	    					break;
	    				case KeyEvent.VK_D:
	    					game.players.get(0).board.shiftRight();
	    					break;
	    				case KeyEvent.VK_SHIFT:
	    					game.players.get(0).board.trySwap();
	    					break;
	    				case KeyEvent.VK_C:
	    					game.players.get(0).board.hardDrop();
	    					break;
	    				
	    				/*
	    				 * Player 2: 
	    				 * IJKL - Move, Rotate
	    				 * N - Swap
	    				 * . - Hard Drop
	    				 */
	    				case KeyEvent.VK_I:
	    					game.players.get(1).board.rotateLeft();
	    					break;
	    				case KeyEvent.VK_J:
	    					game.players.get(1).board.shiftLeft();
	    					break;
	    				case KeyEvent.VK_K:
	    					game.players.get(1).board.shiftDownNewPiece();
	    					break;
	    				case KeyEvent.VK_L:
	    					game.players.get(1).board.shiftRight();
	    					break;
	    				case KeyEvent.VK_N:
	    					game.players.get(1).board.trySwap();
	    					break;
	    				case KeyEvent.VK_PERIOD:
	    					game.players.get(1).board.hardDrop();
	    					break;	
	    				}
	    				
	    			}
				}
			}
		}
	}
}