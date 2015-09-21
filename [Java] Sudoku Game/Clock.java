import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.border.Border;

public class Clock extends JLabel implements Runnable {
	
	public Clock(int time) {
		this.time = time;
		
		// set clock size.
		Dimension size = new Dimension(48, 30);
		setMinimumSize(size);
		setPreferredSize(size);
		setFont(new Font("Times New Roman", Font.BOLD, 18)); // set attributes of font.
		setForeground(new Color(239, 233, 85)); // set font color.
		
		// set border of the clock.
		Border line = BorderFactory.createMatteBorder(
				1, 1, 1, 1, new Color(79, 87, 239));
		Border newLine = BorderFactory.createMatteBorder(
				2, 2, 2, 2, new Color(255, 255, 255));
		Border compound = BorderFactory.createCompoundBorder(
                line, newLine);
		setBorder(compound);
		setText("00:00"); // initial state.
	}
	
	// return the spent time.
	public int getTime() {
		return time;
	}

	// get the distance between current time and the old time.
	// this serves for the score sub system.
	public int getTimeDistance() {
		int distance = time - oldTime;
		oldTime = time;
		return distance;
	}
	
	// reset the time.
	public void reset() {
		oldTime = 0;
		time = 0;
		setText("00:00");
	}
	
	// running the clock. Time variable increase 1 by each second.
	public void run() {
		// TODO Auto-generated method stub
		while (true) {
			String timeText = "";
			// for the condition that someone play more than 100 minutes.
			// the game GUI serves well for time less than 999 minutes each game.
			// if a user played more than 999 minutes, the time will be reset.
			if ((time / 60) > 999) {
				reset();
				JOptionPane.showMessageDialog(null, "Warning" + 
				           "\nYou have played too long.\n" + 
						   "Reset the time to make you feel better");
			} else if ((time / 60) > 99) {
				Dimension size = new Dimension(58, 30);
				setMinimumSize(size);
				setPreferredSize(size);
				timeText = String.format("%03d:%02d", time/60, (time % 60));
			} else {
				Dimension size = new Dimension(48, 30);
				setMinimumSize(size);
				setPreferredSize(size);
				timeText = String.format("%02d:%02d", time/60, (time % 60));
			}
			setText(timeText);
			time += 1;
			Thread t1 = new Thread();
			try {
				t1.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	// This method mainly paints the background of clock
	public void paintComponent(Graphics graph) {
		Graphics2D graph2D = (Graphics2D) graph;
		GradientPaint gp = new GradientPaint(50, 0, Color.red, 50, 30, Color.black);
		graph2D.setPaint(gp);
		graph.fillRect(0,0,getWidth(),getHeight());
		super.paintComponent(graph);
	}
	
	private int time; // clock time.
	private int oldTime; // the old clock time.
}