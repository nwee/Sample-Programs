// Created by JiaLong LIU, z3345987
//            Nelson Wee,  z3352078
//			  Molei Wang,  z3390139
// COMP2911, Project
// Date: 2 June 2013
// This class is for setting the limitation of
// input of Grid, which will only allow 1 character
// from '1' to '9'.

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

public class GridInputLimitation extends PlainDocument {

	private int limit; 
	public GridInputLimitation() {
		super();
	} 
	
	public void insertString(int offset, String string, AttributeSet attribute) 
			throws BadLocationException {   
		if (string == null){
		    return;
		}
		
		// only accept 1 character for each grid.
		if ((string.length() + getLength()) <= 1) {
		    char[] text = string.toCharArray();
		    int length = 0;
		    for (int i = 0; i < text.length; i++) {
		    	// only accept number from 1 to 9.
		    	if (text[i] >= '1' && text[i] <= '9') {
		    		text[length++] = text[i];
		    	}
		    }
			super.insertString(offset, new String(text, 0, length), attribute);
		}
	}
}