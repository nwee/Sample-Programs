package gui;

import java.applet.Applet;
import java.applet.AudioClip;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public class Sound {
	static Map<String, Clip> clipMap;

	//Music
	URL battle = getClass().getResource("/data/battle.wav");
	URL menu = getClass().getResource("/data/menu.wav");
	URL options = getClass().getResource("/data/options.wav");
	AudioClip musicClip;
	
	public Sound() {
		clipMap = new HashMap<String, Clip>();
		musicClip = null;
	}
	
	public void playClear(int numLinesCleared) {
		playEffect("clear", numLinesCleared + 1);
	}
	
	public void playSolidify() {
		playEffect("solidify", 2);
	}
	
	public void playSwap() {
		playEffect("swap", 2);
	}
	
	public void playBomb() {
		playEffect("bomb");
	}
	
	public void playEffect(String audioName) {
		playEffect(audioName, 1);
	}
	
	public void playEffect(String audioName, int nTimes) {
		Clip clip = clipMap.get(audioName);
		if (clip == null) {
			try {
				InputStream inputStream = getClass().getResourceAsStream("/data/" + audioName + ".wav");
				AudioInputStream audioStream = AudioSystem.getAudioInputStream(inputStream);
				clip = AudioSystem.getClip();
				clip.open(audioStream);
				clipMap.put(audioName, clip);
			} catch (Exception e) {
				return;
			}
		}
		
		if (clip.isRunning())
			clip.stop();   // Stop the player if it is still running
			
		clip.setFramePosition(0); // rewind to the beginning
		clip.loop(nTimes - 1);
	}
	
	public void playBattle(){
		playMusic(battle);
	}
	
	public void playMenu(){
		playMusic(menu);
	}
	
	public void playOptions(){
		playMusic(options);
	}
	
	public void playMusic(URL music)  {
		if (musicClip != null) {
			musicClip.stop();
			musicClip = null;
		}
		
		musicClip = Applet.newAudioClip(music);
		musicClip.loop();
	}
	
	public void stopMusic()  {
		musicClip.stop();
	}
}
