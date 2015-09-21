// Created by JiaLong LIU, z3345987
//            Nelson Wee,  z3352078
//			  Molei Wang,  z3390139
// COMP2911, Project
// Date: 2 June 2013
// This class is for the hint of the system.
// The hint shows the possible numbers for 
// the specific grid.

import java.awt.Color;
import java.awt.Font;
import javax.swing.JTextField;

public class Hint extends JTextField {
	public Hint() {
		super();
		setOpaque(true);
		setEditable(false);
		setHorizontalAlignment(JTextField.CENTER);
		this.setBackground(new Color(226, 236, 239)); // set background color.
		setFont(new Font("Times New Roman", Font.BOLD, 15)); // set attributes of font.
		setForeground(new Color(109, 153, 35)); // set color of font.
		openStatus = false;
	}
	
	// return the status of whether the hint is open or not.
	public boolean getStatus() {
		return openStatus;
	}
	
	// set that status of the hint.
	public void setStatus(boolean status) {
		openStatus = status;
	}
	
	private boolean openStatus; // the open status of the hint.
}