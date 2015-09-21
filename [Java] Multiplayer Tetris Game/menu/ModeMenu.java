/**
 * 
 */
package menu;

import gui.Sound;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import logic.Game;
import menu.GameOptions.Mode;

public class ModeMenu {
	private GameOptions options;
	private JFrame MenuFrame;
	private String[] names;
	private int att[][];
	private ArrayList<JComponent> GUIComponent;
	private Sound sound = new Sound();
	private int ButtonCount;

	public ModeMenu() {
		options = new GameOptions();
		// attribute for the fill Constraints (ENUM)
		int fill[] = { GridBagConstraints.BOTH, GridBagConstraints.VERTICAL,
				GridBagConstraints.HORIZONTAL, GridBagConstraints.NONE };

		// attribute for the anchor Constraints (ENUM)
		int anchor[] = { GridBagConstraints.CENTER, GridBagConstraints.EAST,
				GridBagConstraints.SOUTHEAST, GridBagConstraints.SOUTH,
				GridBagConstraints.SOUTHWEST, GridBagConstraints.WEST,
				GridBagConstraints.NORTHWEST, GridBagConstraints.NORTH,
				GridBagConstraints.NORTHEAST };

		// attribute for button names
		String n[] = { "", "", "", "", "", "" };

		names = n;

		// setup attribute 2D array "att"
		int a[][] = {
				{ 1, 1, 1, 1, 0, 0, fill[0], anchor[0] },
				{ 1, 2, 1, 1, 0, 0, fill[0], anchor[0] },
				{ 1, 3, 1, 1, 0, 0, fill[0], anchor[0] },
				{ 1, 4, 1, 1, 0, 0, fill[0], anchor[0] },
				{ 1, 5, 1, 1, 0, 0, fill[0], anchor[0] },
				{ 1, 6, 1, 1, 0, 0, fill[0], anchor[0] },
		};

		att = a;

		MenuFrame = new JFrame();
		// ArrayList of JComponent for Widgets in frame
		GUIComponent = new ArrayList<JComponent>(4);
	}

	public void run(GameOptions op) {
		this.options = op;

		MenuFrame.setSize(200, 320);
		MenuFrame.setLayout(new GridBagLayout());
		MenuFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		centreWindow(MenuFrame);
		
		JLabel titleJL = new JLabel("");
		ImageIcon title = new ImageIcon(".//src//data//modeTitle.png");
		titleJL.setIcon(title);
		GUIComponent.add(titleJL);
		
		if (options.getNumPlayers() == 1) {
			ButtonCount = 5;
		} else if (options.getNumPlayers() == 2) {
			ButtonCount = 5;
		}
		
		int i;
		// Add Button widgets to the arraylist
		for (i = 0; i < ButtonCount; i++) {
			JButton tButton = new JButton(names[i]);
			GUIComponent.add(tButton);
		}

		// Add widgets to the frame
		for (i = 0; i < ButtonCount; i++) {
			addComponent(i);
		}

		// Add Action Listener to the buttons
		JButton b1 = (JButton) GUIComponent.get(1);
		ImageIcon timed = new ImageIcon(".//src//data//timed.png");
			b1.setIcon(timed);
		b1.addActionListener(new TimedModeListener());

		JButton b2 = (JButton) GUIComponent.get(2);
		ImageIcon rush = new ImageIcon(".//src//data//rush.png");
			b2.setIcon(rush);
		b2.addActionListener(new RushModeListener());

		if (options.getNumPlayers() == 1) {
			JButton b3 = (JButton) GUIComponent.get(3);
			ImageIcon level = new ImageIcon(".//src//data//level.png");
				b3.setIcon(level);
			b3.addActionListener(new LevelModeListener());
			
			JButton b5 = (JButton) GUIComponent.get(4);
			ImageIcon back = new ImageIcon(".//src//data//back.png");
				b5.setIcon(back);
			b5.addActionListener(new backListener());
			
		}
		else if (options.getNumPlayers() ==2) {
				JButton b4 = (JButton) GUIComponent.get(3);
				ImageIcon fight = new ImageIcon(".//src//data//fight.png");
					b4.setIcon(fight);
				b4.addActionListener(new FightModeListener());
				
				JButton b5 = (JButton) GUIComponent.get(4);
				ImageIcon back = new ImageIcon(".//src//data//back.png");
					b5.setIcon(back);
				b5.addActionListener(new backListener());
		}
		
		
		
		MenuFrame.setVisible(true);
		if (op.getSoundOnOff() == true) {
			sound.playOptions();
		}
	}

	// add number i widget to the frame according attributes
	private void addComponent(int i) {
		GridBagConstraints c = new GridBagConstraints();
		int a[] = att[i];

		c.gridx = a[0];
		c.gridy = a[1];
		c.gridwidth = a[2];
		c.gridheight = a[3];
		c.weightx = a[4];
		c.weighty = a[5];
		c.fill = a[6];
		c.anchor = a[7];
		MenuFrame.add(GUIComponent.get(i), c);
	}

	class TimedModeListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			options.setMode(Mode.TIMED);
			MenuFrame.dispose();
			
			setMap();
			
			if (options.getSoundOnOff() == true) {
				sound.stopMusic();
			}
			// start the game in mode TIMED

			Game game = new Game(options);
			game.start();

		}
	}

	class RushModeListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			options.setMode(Mode.RUSH);
			MenuFrame.dispose();
			
			setMap();
			
			if (options.getSoundOnOff() == true) {
				sound.stopMusic();
			}
			// start the game in mode RUSH

			Game game = new Game(options);
			game.start();
		}
	}

	class FightModeListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			options.setMode(Mode.FIGHT);
			MenuFrame.dispose();
			//set maps
			
			setMap();
			
			if (options.getSoundOnOff() == true) {
				sound.stopMusic();
			}
			// start the game in mode FIGHT

			Game game = new Game(options);
			game.start();
		}
	}
	
	class LevelModeListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			options.setMode(Mode.LEVEL);
			MenuFrame.dispose();

			setMap();
			
			if (options.getSoundOnOff() == true) {
				sound.stopMusic();
			}
			// start the game in mode LEVEL

			Game game = new Game(options);
			game.start();
		}
	}
	
	class backListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			MenuFrame.dispose();
			PlayNumMenu pn = new PlayNumMenu();
			pn.run(options);
			if (options.getSoundOnOff() == true) {
				sound.stopMusic();
			}
		}
	}
	
	public void setMap() {
		ImageIcon icon = new ImageIcon(".//src//data//maps.jpg");
		Object[] choose = {"None", "Smiley","Corridor", "Buildings", "Tablecloth"};
		int n = JOptionPane.showOptionDialog(null,
		"",
		"Please Choose Map",
		JOptionPane.YES_NO_OPTION, 0,
		icon, choose, choose[1]);
		if (n == JOptionPane.CLOSED_OPTION)
			n=0;
		options.mapIndex = n;
	}
	
	public static void centreWindow(Window frame) {
	    Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
	    int x = (int) ((dimension.getWidth() - frame.getWidth()) / 2);
	    int y = (int) ((dimension.getHeight() - frame.getHeight()) / 2);
	    frame.setLocation(x, y);
	}
}