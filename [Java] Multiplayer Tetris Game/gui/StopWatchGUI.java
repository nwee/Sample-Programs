package gui;

import helper.StopWatch;

import java.awt.FlowLayout;
import java.awt.Graphics;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

@SuppressWarnings("serial")
public class StopWatchGUI extends JPanel {
	public JLabel timerComponent;
	private final StopWatch stopWatch;
	
	public StopWatchGUI(StopWatch stopWatch) {
		this.stopWatch = stopWatch;
		setLayout(new FlowLayout(FlowLayout.LEFT));
		add(new JLabel("Time"));
		
		timerComponent = new JLabel("0:00", SwingConstants.CENTER);
		add(timerComponent);
	}
	
	public void paint(Graphics g) {
		super.paint(g);
		
		timerComponent.setText(stopWatch.toString());
	}
}
