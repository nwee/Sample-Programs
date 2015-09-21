package gui;

import java.awt.Button;
import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class Menu extends JPanel {
	public Menu() {//takes an int for number of players
		JPanel panel = new JPanel(new GridLayout(0,1));
		this.add(panel);
		panel.add(new JLabel("Menu/Options"));
		panel.add(new Button("Restart")); //add all the options here
	}

}
