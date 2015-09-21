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

import menu.GameOptions.AiDiff;

public class AiMenu {
	private GameOptions options;
	private JFrame MenuFrame;
	private String[] names;
	private int att[][];
	private ArrayList<JComponent> GUIComponent;
	private Sound sound = new Sound();
	
	public AiMenu () {
		options = new GameOptions();
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
				{1, 2, 1, 1, 0, 0, fill[0], anchor[0]},
				{1, 3, 1, 1, 0, 0, fill[0], anchor[0]},
				{1, 4, 1, 1, 0, 0, fill[0], anchor[0]}
		};
		
		att = a;
		
		MenuFrame = new JFrame();
		// ArrayList of JComponent for Widgets in frame 
		GUIComponent = new ArrayList<JComponent>(3);

	}

	public void run(GameOptions op) {
		this.options = op;
		
		MenuFrame.setSize(200, 300);
		MenuFrame.setLayout(new GridBagLayout());
		MenuFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		centreWindow(MenuFrame);
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
		JButton b1 = (JButton) GUIComponent.get(0);
		ImageIcon easy = new ImageIcon(".//src//data//easy.png");
			b1.setIcon(easy);
		b1.addActionListener(new zeroListener());
		
		JButton b2 = (JButton) GUIComponent.get(1);
		ImageIcon normal = new ImageIcon(".//src//data//normal.png");
			b2.setIcon(normal);
		b2.addActionListener(new oneListener());
		
		JButton b3 = (JButton) GUIComponent.get(2);
		ImageIcon hard = new ImageIcon(".//src//data//hard.png");
			b3.setIcon(hard);
		b3.addActionListener(new twoListener());
		
		MenuFrame.setVisible(true);
		
		if (op.getSoundOnOff() == true) {
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
	
	
	// EASY
	class zeroListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			options.ai = AiDiff.EASY;
			MenuFrame.dispose();
			ModeMenu m = new ModeMenu();
			m.run(options);
			if (options.getSoundOnOff() == true) {
				sound.stopMusic();
			}
		}
	}
	// MED
	class oneListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			options.ai = AiDiff.MEDIUM;
			MenuFrame.dispose();
			ModeMenu m = new ModeMenu();
			m.run(options);
			if (options.getSoundOnOff() == true) {
				sound.stopMusic();
			}
		}
	}
	// HARD
	class twoListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			options.ai = AiDiff.EXPERT;
			MenuFrame.dispose();
			ModeMenu m = new ModeMenu();
			m.run(options);
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
}
