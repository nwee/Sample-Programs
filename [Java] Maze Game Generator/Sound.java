import java.applet.Applet;
import java.applet.AudioClip;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
/**
* This class is to play sound files 
* 
* @author Nelson Wee, z3352078
* @author Renmark Marte, z3464929
* @author Sung Min Park, z3278712
* @author Luna Pradhananga, z3358423 
*
*/
public class Sound {
	static Map<String, Clip> clipMap;


	URL theme = getClass().getResource("options.wav");
	URL menu = getClass().getResource("menu.wav");
	AudioClip musicClip;
	

	public Sound() {
		clipMap = new HashMap<String, Clip>();
		musicClip = null;
	}
	
	/**
	 *  Play theme music
	 */
	public void playTheme(){
		playMusic(theme);
	}
	
	/**
	 * Play menu music
	 */
	public void playMenu(){
		playMusic(menu);
	}
	
	/**
	 * Loop the music file
	 * @param music music file
	 */
	public void playMusic(URL music)  {
		if (musicClip != null) {
			musicClip.stop();
			musicClip = null;
		}
		musicClip = Applet.newAudioClip(music);
		musicClip.loop();
	}
	
	/**
	 *  Stop the music
	 */
	public void stopMusic()  {
		musicClip.stop();
	}
	
}
