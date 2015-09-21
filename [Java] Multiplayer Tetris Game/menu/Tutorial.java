package menu;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

public class Tutorial {
	public static void about() {
		JOptionPane.showMessageDialog(null, 
				"COMP4920 Project: Tetris Fight 2013\n\n "
				+ "Authors: \n"
				+ "Jiazhou Liu (Joe)    z3351904\n"
				+ "Mathew Yeap          z3290568\n"
				+ "Natalia Vaudagnotto  z3377468\n"
				+ "Nelson Wee             z3352078\n"
				+ "Xuanru Sun (Sean)    z3398364\n", "About",JOptionPane.PLAIN_MESSAGE);
	}
	
	public static void controls() {
		ImageIcon icon = new ImageIcon(".//src//data//controls.jpg");
		JOptionPane.showOptionDialog(null,
		"",	"Controls",
		JOptionPane.PLAIN_MESSAGE, 0,
		icon, null, null);
	}
	
	public static void basic() {
		ImageIcon icon = new ImageIcon(".//src//data//basic.jpg");
				Object[] options = {"Modes","Fight Mode","Techniques","Controls", "Exit"};
				// Text, Title
				int n = JOptionPane.showOptionDialog(null,
				"In Tetris, the goal of the game is to\n"
				+ "match seven specific geometric shapes\n"
				+ "(I, J, L, O, S, T, Z) Tetriminos, into\n"
				+ "a row of ten blocks called lines. \n\n"
				+ "This is achieved by controlling \n"
				+ "individual falling blocks: \n "
				+ "- Moving them sideways\n" 
				+ "- Rotating the block clockwise by \n"
				+ "  90 degree movements. \n\n"
				+ "When a row is created, it is deleted\n"
				+ "and all blocks above it fall by the \n"
				+ "number of rows cleared.\n\n"
				+ "Points\n"
				+ "1 Line Cleared = 10 pts\n"
				+ "'Bomb' line Cleared = 15 pts\n"
				+ "Swapping Tetrominos = -5pts\n"
				+ "Perfect Clear = 100 pts (Clear field)\n",
				"Tutorial - Basics",
				JOptionPane.YES_NO_OPTION, 0,
				icon, options, options[3]);
				
				if (n==0) 
					modes();
				else if (n==1)
					fightMode();
				else if (n==2)
					techs();
				else if (n==3)
					controls();
				
	}
	public static void modes() { 
		ImageIcon icon = new ImageIcon(".//src//data//modesTut.png");
				Object[] options = {"Basics", "Fight Mode","Techniques", "Controls", "Exit"};
				int n = JOptionPane.showOptionDialog(null,
				"- Players progress through levels with the number of lines they clear,\n"
				+ "At the start of each level the natural fall speed increases\n "
				+ "- Their score is based on how many levels and lines the player\n"
				+ "can clear before losing\n\n"
				+ "- Players try and clear as many lines as they can within a timeframe. \n"
				+ "- Their score is based on the number of lines cleared.\n\n"
				+ "- Player tries to clear 40 lines as quickly as possible \n"
				+ "- Their score is based on the time it takes\n\n "
				+ "- Players can compete against other players or an AI\n"
				+ "- Players try to knock their opponent out by sending over 'bomb' lines\n"
				+ "- The winner is the one who knocks the other out ",
				"Tutorial - Modes ",
				JOptionPane.YES_NO_OPTION, 0,
				icon, options, options[1]);
				
				if(n == 0)
					basic();
				else if (n==1) 
					fightMode();
				else if (n==2)
					techs();
				else if (n==3)
					controls();
	}
	
	public static void fightMode() { 
		ImageIcon icon = new ImageIcon(".//src//data//fightTut.jpg");
				Object[] options = {"Basics", "Modes","Techniques", "Controls","Exit"};
				int n = JOptionPane.showOptionDialog(null,
				"-Players compete with each other in\n"
				+ " clearing as many lines as possible.\n"
					+ "-Victory is achieved when one player\n"
					+ " manages to “knock-out”, KO, the other\n"
					+ " player by transferring their cleared \n"
					+ "lines to their opponent’s game, in the\n"
					+ "form of 'bomb' lines\n\n"
					+ "This limits their playing space and \n"
					+ "forcing them closer to their upper limit.\n\n\n"
					+ "'Bombs' are lines that appear generated \n"
					+ "by their opponent.\n"
					+ "- Simply touching the bombs with their \n"
					+ "Tetromino blocks, will clear the line with \n"
					+ "and extend their combos for more points\n"
					+ "- 'Bomb' lines are sent when a player \n"
					+ "generates a minimum of 15 points, from\n"
					+ "cleared lines and combo points"
					+ "- 'Maps' is an initally generated field of\n"
					+ "blocks, starting the game with a twist.\n\n\n\n"
					+ " ",
				"Tutorial - Fight Mode ",
				JOptionPane.YES_NO_OPTION, 0,
				icon, options, options[1]);
				
				if(n == 0)
					basic();
				else if (n==1) 
					modes();
				else if (n==2)
					techs();		
				else if (n==2)
					controls();
	}
	
	public static void techs() { 
		ImageIcon icon = new ImageIcon(".//src//data//scores.jpg");
				Object[] options = {"Basics", "Modes","Fight Mode","Controls", "Exit"};
				int n = JOptionPane.showOptionDialog(null,
				"Storage\n"
				+ "- Tetriminos can be stored temporarily and\n"
				+ " be swapped with the current Tetrimino\n "
				+ "for later use\n"
				+ "- The storage is initially empty and the\n"
				+ " first time the player “swaps” their current\n"
				+ " block goes into the storage and it is replaced\n"
				+ " by the next block in the queue.\n"
				+ "- Every use of the storage function will deduct\n"
				+ " 5 pts from the user's total score\n\n"
				+ "Combos\n"
				+ "- Combo points can be earned by clearing\n"
				+ "lines in succession or by touching 'bombs' with\n"
				+ "their Tetromino\n\n"
				+ "- 'Maps' are an initally generated field of\n"
				+ "blocks, starting the game with a twist.\n", 
				"Tutorial - Techniques ",
				JOptionPane.YES_NO_OPTION, 0,
				icon, options, options[1]);
				
				if(n == 0)
					basic();
				else if (n==1) 
					modes();
				else if (n==2)
					fightMode();	
				else if (n==3)
					controls();
				
	}
}
