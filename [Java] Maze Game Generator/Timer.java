import java.awt.Dimension;
import java.awt.Font;
import javax.swing.JLabel;

/**
* This class runs a thread that serves as a timer system
* 
* @author Nelson Wee, z3352078
* @author Renmark Marte, z3464929
* @author Sung Min Park, z3278712
* @author Luna Pradhananga, z3358423 
*
*/
@SuppressWarnings("serial")
public class Timer extends JLabel implements Runnable {
	private int time, minutes, seconds;
	public Timer() {
		Dimension size = new Dimension(48, 30);
		setMinimumSize(size);
		setPreferredSize(size);
		setFont(new Font("Times New Roman", Font.BOLD, 18)); // set attributes of font.
		time = 0;
		minutes = 0;
		seconds = 0;
		//setForeground(new Color(239, 233, 85));
	}
	
	/**
	 * This method returns total time in seconds, used for scoring
	 */
	public int getTime(){
		return time;
	}
	
	/**
	 * This method runs the thread which sleeps for 1000ms to simulate time keeping
	 */
	@Override
	public void run() {
		Thread delay = new Thread();
		String timeStr;
		for (; ;) {
			if (seconds > 60) {
				minutes += 1;
				seconds -= 60;
			}
			timeStr = String.format("%02d:%02d",minutes,seconds);
			setText(timeStr);
			seconds += 1;
			time += 1;
			try {
				delay.sleep(1000); //delays for 1 second
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}