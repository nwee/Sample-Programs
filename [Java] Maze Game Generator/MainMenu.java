import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Scanner;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

/**
* This class contains the MAIN function, and creates the mainMenu which acquires settings chosen by users before generating the game. 
* This class also reads from hs.txt to populate a list of highscores
* 
* @author Nelson Wee, z3352078
* @author Renmark Marte, z3464929
* @author Sung Min Park, z3278712
* @author Luna Pradhananga, z3358423 
*
*/
@SuppressWarnings("serial")
public class MainMenu extends JFrame {
	private int numPlayers;
	private int Row;
	private int Col;
	private Sound music;
	public static ArrayList<Player> highScore;
	
	public MainMenu() {
  		numPlayers = 1;
  		Row = 20;
  		Col = 20;
  		music = new Sound();
  		music.playMenu();
		setTitle("Maze Runner");
	    setDefaultCloseOperation(EXIT_ON_CLOSE);
	    this.setLayout(new BorderLayout());
	    this.setSize(1250, 800);
		this.setResizable(false);
		this.getContentPane().setBackground(Color.decode("#19306b"));
		JLabel bg = new JLabel(new ImageIcon("./files/Main_menu.png"));
		this.add(bg);
		bg.setLayout(new FlowLayout());
		
	    JPanel buttonPanel = new JPanel();
	    buttonPanel.setLayout(new GridLayout(4,1));
	    JButton play = new JButton("Play");
	    JButton hScore = new JButton("High Score");
	    JButton about = new JButton("About");
	    JButton quit = new JButton("Quit");
	    //if you wanna change the button size you may have to change the layout from Grid
	    buttonPanel.add(play);
	    buttonPanel.add(hScore);
	    buttonPanel.add(about);
	    buttonPanel.add(quit);
	    buttonPanel.setBorder(new EmptyBorder(0, 400, 30, 400));
		buttonPanel.setBackground(Color.decode("#19306b"));
	    this.add("South",buttonPanel);
	    
	    this.setFocusable(true);
	    this.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);//.EXIT_ON_CLOSE);
	    this.setVisible(true);
	    GameGUI.centreWindow(this);
	    
	    //PLAY
	    play.addActionListener(new ActionListener()	{
			public void actionPerformed(ActionEvent event) {
				//Popup menu for selecting players
				Object[] playerOptions = {"One Player", "Two Players"};
				int n = JOptionPane.showOptionDialog(null,"Select Number of Players!",
					"Main Menu", JOptionPane.PLAIN_MESSAGE, 1, null, playerOptions, playerOptions[1]);
				//Default Players is 1
				
				if (n == 1) {
					numPlayers = 2;
				}
				
				music.stopMusic();
				setVisible(false); //hides this menu
				new MapGenerator(Row, Col);
		  		new GameGUI(numPlayers);
			}
		});
	    
	    hScore.addActionListener(new ActionListener()	{
			public void actionPerformed(ActionEvent event) {
				GameGUI.viewHighScores();
			}
		}); 
	    
	    // QUIT
	 		quit.addActionListener(new ActionListener() {
	 			public void actionPerformed(ActionEvent event) {
	 				System.exit(0);
	 			}
	 		});
	    
	    //ABOUT
	    about.addActionListener(new ActionListener()	{
			public void actionPerformed(ActionEvent event) {
				GameGUI.viewAbout();
			}
		});  
	    
	    // Stops any form of music playing when the window is closed
	    addWindowListener(new WindowAdapter() {
	        public void windowClosing(WindowEvent e) {
	            music.stopMusic();
	        }
	    });
	}
		
	public static void main(String[] args) throws IOException {
		//Reads the txtfile and stores past scores in an ArrayList
		Scanner s = new Scanner(new FileReader(args[0]));
		highScore = new ArrayList<Player>();
		while(s.hasNext()) {
			String name = s.next();
			int score = s.nextInt();
			Player temp = new Player(0,0);
			temp.setHighScoreName(name);
			temp.setHighScore(score);
			highScore.add(temp);
		}
		new MainMenu();  
	}
}
