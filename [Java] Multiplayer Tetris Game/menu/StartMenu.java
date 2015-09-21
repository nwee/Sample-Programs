package menu;
import gui.Sound;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;

import javax.swing.*;

public class StartMenu {
	private GameOptions options;
	/**
	 * @return the options
	 */
	public GameOptions getOptions() {
		return options;
	}

	/**
	 * @param options the options to set
	 */
	public void setOptions(GameOptions options) {
		this.options = options;
	}

	private Sound sound;
	private JFrame MenuFrame;
	private String[] names;
	private int att[][];
	private ArrayList<JComponent> GUIComponent;
	
	
	public StartMenu(GameOptions ops) {
		options = new GameOptions();
		options = ops;
		
		
		// attribute for the fill Constraints (ENUM)
		int fill[] = {
				GridBagConstraints.BOTH,
				GridBagConstraints.VERTICAL,
				GridBagConstraints.HORIZONTAL,
				GridBagConstraints.NONE
		};
		
		// attribute for the anchor Constraints (ENUM)
		int anchor[] = {
				GridBagConstraints.CENTER,
				GridBagConstraints.EAST,
				GridBagConstraints.SOUTHEAST,
				GridBagConstraints.SOUTH,
				GridBagConstraints.SOUTHWEST,
				GridBagConstraints.WEST,
				GridBagConstraints.NORTHWEST,
				GridBagConstraints.NORTH,
				GridBagConstraints.NORTHEAST
		};
		
		// attribute for button names
		String n[] = {
				"",
				"",
				""
		};
		
		names = n;
		
		// setup attribute 2D array "att"
		int a[][] = {
				{1, 0, 2, 1, 0, 0, fill[1], anchor[0]},
				{1, 2, 2, 1, 0, 0, fill[0], anchor[0]},
				{1, 3, 1, 1, 0, 0, fill[0], anchor[0]},
				{2, 3, 1, 1, 0, 0, fill[0], anchor[0]}
		};
		att = a;
		
		MenuFrame = new JFrame();
		
		// ArrayList of JComponent for Widgets in frame 
		GUIComponent = new ArrayList<JComponent>(4);

		if (ops.getSoundOnOff() == true) {
			sound = new Sound();
		}
	}
	

	public void run(GameOptions ops) {
		this.options = ops;
		MenuFrame.setSize(200, 300);
		MenuFrame.setLayout(new GridBagLayout());
		MenuFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		centreWindow(MenuFrame);
		
		JLabel titleJL = new JLabel("");
		ImageIcon title = new ImageIcon(".//src//data//title.png");
		titleJL.setIcon(title);
		GUIComponent.add(titleJL);
		
		int i;
		// Add Button widgets to the arraylist
		for (i = 0; i < 3; i++) {
			JButton tButton = new JButton(names[i]);
			GUIComponent.add(tButton);
		}
		
		// Add widgets to the frame
		for (i = 0; i < GUIComponent.size(); i++) {
			addComponent(i);
		} 
		
		// Add Action Listener to the buttons
		JButton b1 = (JButton) GUIComponent.get(1);
		ImageIcon play = new ImageIcon(".//src//data//play.png");
			b1.setIcon(play);
		b1.addActionListener(new PlayButtonListener());
		
		JButton b2 = (JButton) GUIComponent.get(2);
		ImageIcon options = new ImageIcon(".//src//data//options.png");
			b2.setIcon(options);
		b2.addActionListener(new OptionListener());
		
		JButton b3 = (JButton) GUIComponent.get(3);
		ImageIcon help = new ImageIcon(".//src//data//help.png");
			b3.setIcon(help);
		b3.addActionListener(new HelpButtonListener());
		
		MenuFrame.setVisible(true);
		
		if (ops.getSoundOnOff() == true) {
			sound.playMenu();
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

	// Create a new play menu object with option
	class PlayButtonListener implements ActionListener {

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
	// Create a new play number menu object with option
//	class PLayerNumButtonListener implements ActionListener {
//
//		@Override
//		public void actionPerformed(ActionEvent e) {
//			MenuFrame.dispose();
//			PlayNumMenu pn = new PlayNumMenu();
//			pn.run(options);
//			if (options.getSoundOnOff() == true) {
//				sound.stopMusic();
//			}
//			
//		}
//	}
	// Create a new option menu object with option
	class OptionListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			MenuFrame.dispose();
			OptionMenu op = new OptionMenu();
			op.run(options);
			
			if (options.getSoundOnOff() == true) {
				sound.stopMusic();
			}
			
		}
	}
	
	class HelpButtonListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			MenuFrame.dispose();
			HelpMenu hm = new HelpMenu();
			hm.run(options);
			
			if (options.getSoundOnOff() == true) {
				sound.stopMusic();
			}
			
		}
	}
	

	public static void centreWindow(Window frame) {
		    Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
		    int x = (int) ((dimension.getWidth() - frame.getWidth()) / 2);
		    int y = (int) ((dimension.getHeight() - frame.getHeight()) / 2);
		    frame.setLocation(x, y);
	}
	
//	public static void main (String[] args) {
//		StartMenu m = new StartMenu(new GameOptions());
//		m.run(m.getOptions());
////		EncryptorGUI2 eg = new EncryptorGUI2();
////		eg.run();
//
//	}

}
