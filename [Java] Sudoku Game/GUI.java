// Created by JiaLong LIU, z3345987
//            Nelson Wee,  z3352078
//			  Molei Wang,  z3390139
// COMP2911, Project
// Date: 2 June 2013
// This class is the main Frame for our GUI.
// The Frame layout is BorderLayout, which
// northPanel in the "NORTH" contains menuBar
// and titlePanel while midPanel in the "CENTER"
// contains the panel for all grids.

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import javax.swing.*;

public class GUI extends JFrame {
		
	JLabel gameTitle = new JLabel("Sudoku"); // the title of game.
	JLabel timerTitle = new JLabel("Time"); // the title of clock.
	JLabel scoreTitle = new JLabel("Score"); // the title of score.
	Clock clock = new Clock(0); // Initialize clock to 00:00.
	Score score = new Score(); // Score object for showing current score that a user has.
	
	public GUI() throws IOException {
		//super("Puzzle Of Sudoku");
		setTitle("PuzzleofSudoku");
		setSize(500, 600);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false); // disable resize which makes our GUI works its best for users.
		
		JLabel contentPane = new JLabel();
		contentPane.setIcon(new ImageIcon(".//src//123.jpg"));
		contentPane.setLayout( new BorderLayout() );
		setContentPane(contentPane);
		
		// set the default display location of the frame to the center of the screen.
		Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
		int x = dimension.width/2-getSize().width/2;
		int y = dimension.height/2-getSize().height/2;
		setLocation(x, y);

		//
		// Constructors for the various layouts of the puzzle
	    //
		// midPanel: setting of the grid of the game.
		final Thread clockThread = new Thread(clock);
		final MidPanel midPanel = new MidPanel(clock, clockThread, score);

		// northPanel: Menubar and titlePanel
		// **********************************************************************
		JPanel northPanel = new JPanel();
		northPanel.setLayout(new GridLayout(2, 1)); // Layout for northPanel
		
		// titlePanel: clock, timerTitle, score and scoreTitle
		JPanel titlePanel = new JPanel();
		titlePanel.setLayout(new BorderLayout());
		JPanel westTitlePanel = new JPanel();
		//westTitlePanel.setLayout(new FlowLayout(FlowLayout.LEFT)); // clock on the left side.
		westTitlePanel.setLayout(new BorderLayout()); // clock on the left side.
		westTitlePanel.add(clock);
		westTitlePanel.add(timerTitle);
		JPanel eastTitlePanel = new JPanel();
		eastTitlePanel.setLayout(new FlowLayout(FlowLayout.RIGHT)); // score on the right side.
		eastTitlePanel.add(scoreTitle);
		eastTitlePanel.add(score);
		
		// setting of gameTitle and menu bar.
		gameTitle.setFont(new Font("Times New Roman", Font.BOLD, 20));
		gameTitle.setForeground(new Color(30, 100, 175));
		gameTitle.setHorizontalAlignment(SwingConstants.CENTER);
		titlePanel.add("West", westTitlePanel); // clock on the left side.
		titlePanel.add("Center", gameTitle); // gameTitle in the middle.
		titlePanel.add("East", eastTitlePanel);	// score on the right side.
		
		// MenuBar setting.
		JMenuBar menuBar = new JMenuBar();
		JMenu file = new JMenu("File");
		JMenu help = new JMenu("Help");
		menuBar.add(file);
		menuBar.add(help);
		northPanel.add(menuBar);
		northPanel.add(titlePanel);
		// **********************************************************************				
		
		//
		// Listeners and function calls for the menu and buttons
		//
		
		// Menu Items in the file menu.
		// Starts a new Game, CTRL+N
		final JMenuItem newGame = new JMenuItem("New");
		file.add(newGame);
		JMenuItem pauseGame = new JMenuItem("Pause");
		file.add(pauseGame);
		JMenuItem resetGame = new JMenuItem("Reset");
		file.add(resetGame);
		JMenuItem solveGame = new JMenuItem("Solve");
		file.add(solveGame);
		JMenuItem exit = new JMenuItem("Exit");
		file.add(exit);
		
		// Menu Items in the help menu.
		final JMenuItem instructions = new JMenuItem("Instructions");
		help.add(instructions);
		final JMenuItem howToPlay = new JMenuItem("How To Play");
		help.add(howToPlay);
		JMenuItem about = new JMenuItem("About");
		help.add(about);
		
		
		// NEW GAME
		newGame.addActionListener(new ActionListener() {
			@SuppressWarnings("deprecation")
			public void actionPerformed(ActionEvent event) {
				Object[] options = {"Easy", "Normal", "Expert"};
				// Text, Title
				int respon = JOptionPane.showOptionDialog(null,"Please Select Difficulty:\n",
						"Choose your Difficulty!",
						JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
				if (!(respon >= 0 && respon <= 2)) respon = 0; // for the condition that a user close the dialog window.
				
				//if the clockThread has started but suspended, 
				// then reset it to 0 and resume it for the new Game.
				if (clockThread.isAlive()) {
					clock.reset();
					clockThread.resume();
				} else {
					clockThread.start(); // if the clockThread has not started then start it.
				}
				midPanel.startSetting(respon);
            }
		});
		
		// Hotkey CTRL+N to start New Game.
		newGame.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N,ActionEvent.CTRL_MASK));

		// PAUSES the game, CTRL+P
		pauseGame.addActionListener(new ActionListener() {
			@SuppressWarnings("deprecation")
			public void actionPerformed(ActionEvent event) {
				clockThread.suspend(); // suspend the clockThread to stop running the clock.
				midPanel.startPause(); // make number of all grids in the midPanel disappear 
									   // to avoid a user pauses the game and try to think of solution.
				Object[] options = {"Resume"};
				// Text, Title
				JOptionPane.showOptionDialog(null,"\"Time you enjoy wasting is not wasted time.\"",
					"Paused",
					JOptionPane.PLAIN_MESSAGE, 1, null, options, options[0]);		
				// resume the game.
				midPanel.resume();
				clockThread.resume();       
			}
		});	
		// Hotkey CTRL+P to pause the Game.
		pauseGame.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P,ActionEvent.CTRL_MASK));
		
		// RESET the user input, CTRL+R
		resetGame.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				midPanel.reset(); // reset the Game to the initial state.
			}
		});
		// Hotkey CTRL+R to reset the Game.
		resetGame.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R,ActionEvent.CTRL_MASK));
				
		// SOLVE the puzzle, CRTL+S	
		solveGame.addActionListener(new ActionListener() {
			@SuppressWarnings("deprecation")
			public void actionPerformed(ActionEvent event) {
			    midPanel.solve(); // give out solutions.
			    if (clockThread.isAlive()) {
			    	clockThread.suspend(); // stop running the clock.
			    }
            }
		});
		// Hotkey CTRL+S to solve the Game.
		solveGame.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,ActionEvent.CTRL_MASK));
		
		// EXIT the program, CTRL+Q
		exit.addActionListener(new ActionListener()	{
			public void actionPerformed(ActionEvent event) {
		    	System.exit(0);
			}
		});
		// Hotkey CTRL+Q to exit the program.
		exit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q,ActionEvent.CTRL_MASK));
		
		// Opens INSTRUCTIONS
		instructions.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				//ImageIcon icon = new ImageIcon(".//instructions.jpg");
				ImageIcon icon = new ImageIcon(".//src//instructions.jpg");
				Object[] options = {"How To Play","Exit"};
				int n = JOptionPane.showOptionDialog(null, 
					"Instructions\n" +
					" \n" +
					"Hint Function:\n" +
					"Select an empty block and pressing the 'h' key\n" +
					"will open the 'hint' box, displaying the possible\n" +
					"numbers that can be inserted. Pressing the any key\n" +
					"will close the 'hint' box and will still flash the\n" +
					"hint below the grid\n\n" +
					"Shortcuts:\n" +
					"CTRL+N : New Game\n" +
					"CTRL+P : Pauses the Game\n" +
					"CTRL+R : Resets Player Input\n" +
					"CTRL+S : Solves the puzzle\n" +
					"CTRL+Q : Exits the Application\n\n" +
					"Scoring System:\n" +
					"- Players start with 1000 points\n" +
					"Earning Points:\n" +
					"- SPEED POINTS: Filling in a number within 5 seconds\n" +
					"earns you +100! But For every second beyond 5, the \n" +
					"points earnt will be 10 points less from 100 for a,\n" +
					"for a minimum of 10 points\n" +
					"- COMPETION POINTS: Completing will earn +1000\n" +
					"- TIME POINTS: Are earned by finishing:\n" +
					"      >Within 10 Mins = +1000 Points\n" +
					"      >Within 20 Mins = +800 Points\n" +
					"      >Within 30 Mins = +500 Points\n" +
					"      >After 30 Mins = +0 Points\n" +
					"\nLosing Points: \n" +
					"- Points can be lost by CHANGING a previously\n" +
					"inputted number, however SPEED POINTS can still\n" +
					"be earned from the new input\n" , "Instructions",JOptionPane.PLAIN_MESSAGE,0,icon,options, options[0]);
				
				if (n==0) howToPlay.doClick();
			}
		});
		
		// Opens HOW TO PLAY
		howToPlay.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				//ImageIcon icon = new ImageIcon(".//HowTo1.jpg");
				//ImageIcon icon2 = new ImageIcon(".//HowTo2.jpg");
				ImageIcon icon = new ImageIcon(".//src//HowTo1.jpg");
				ImageIcon icon2 = new ImageIcon(".//src//HowTo2.jpg");
				Object[] options = {"Rules","Instructions", "Exit"};
				// Text, Title
				int n = JOptionPane.showOptionDialog(null,
				"Sudoku is a 9x9 puzzle grid made up\n" +
				" of 3x3 regions. Each region, row and\n" +
				"column contains 9 cells each. The \n" +
				"numbers shown are the 'givens' and \n" +
				"cannot be changed.\n\n" +
				"To solve the puzzle, players need\n" +
				"to fill in the empty cells with a\n" +
				"single number that doesn't violate\n" +
				"the Sudoku rules. This can be done\n" +
				"with various Sudoku solving techs.\n",
				"How To Play ",
				JOptionPane.YES_NO_OPTION, 0,
				icon, options, options[0]);
				if (n == 0) {
					JOptionPane.showMessageDialog(null, 
							"In the example the Blue numbers are\n" +
							"'givens'and cannot be changed. The \n" +
							"black ones are the ones that can be\n" +
							"filled in.\n\n" +
							"Rows\n" +
							"Every row must contain the numbers \n" +
							"1 to 9. There may not be any duplicates\n" +
							"in the row\n\n" +
							"Columns\n" +
							"Similar to the rows, it must contain\n" +
							"numbers from 1 to 9 with no duplicates\n\n" +
							"Regions\n" +
							"A region is a 3x3 box which also has\n" +
							"numbers from 1 to 9 with no duplicates",
							"Rules of Sudoku",JOptionPane.PLAIN_MESSAGE, icon2);				
				}
				else if (n==1) {
					instructions.doClick();
				}
			}
		});
		
		// ABOUT the program
		about.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				JOptionPane.showMessageDialog(null, 
					"COMP2911 Project: Sudoku Solver 2013\n" +
					"GUI ver 5.0\n" +
					"Back-End ver 4.0\n" +
					"AI ver 6.0\n\n" +
					"Authors: \n" +
					"JiaLong Liu, z3345987\n" +
					"Nelson Wee, z3352078\n" +
					"Molei Wang, z3390139", "About",JOptionPane.PLAIN_MESSAGE);
			}
		});
		

		// setting of the frame.
		BorderLayout border = new BorderLayout();
		setLayout(border);
		add(northPanel, border.NORTH);
		add(midPanel, border.CENTER);
		setVisible(true);	
		newGame.doClick(); //Immediately loads game
	}
	
	public static void main(String[] arguments) throws IOException {
		new GUI();
	}
	
}
