/* Copyright (C) Sven Heins - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * author: Sven Heins
 * date: 05.07.2015
 */
package de.svenheins.threads;

import de.svenheins.main.SoundEffect;
import de.svenheins.managers.SoundEffectManager;
import javafx.scene.media.MediaPlayer;

public class SoundEffectThread implements Runnable{
	 private volatile Thread threadManager;
	 private long interval = 200;
	 private MediaPlayer mediaPlayer;
	 
	 public SoundEffectThread(SoundEffect soundEffect) {
		 this.mediaPlayer = SoundEffectManager.get(soundEffect);
		 if (this.mediaPlayer != null) {
	        	this.mediaPlayer.play();
	     }
	 }

	    public void stop() {
	    	if (this.mediaPlayer != null) {
	    		this.mediaPlayer.stop();
	    	}
	    	threadManager = null;
	    }
	    
	    public void start() {
	        threadManager = new Thread(this);
	        threadManager.start();
	    }
	    
	    public void run() {
	    	Thread thisThread = Thread.currentThread();
	        while (threadManager == thisThread) {
	        	if (this.mediaPlayer !=null) {
		            if (this.mediaPlayer.getMedia() !=null) {
			            if (this.mediaPlayer.getCurrentTime().toMillis() < this.mediaPlayer.getMedia().getDuration().toMillis()) {
			            	// just let it run!
			            } else {
			            	this.stop();
			            }
		            } else {
		            	this.stop();
		            }
	            }  else {
	            	this.stop();
	            }
	        	
	            try {
	                thisThread.sleep(interval);
	            } catch (InterruptedException e){
	            }
	            
//	            repaint();
	        }
	    }
}
