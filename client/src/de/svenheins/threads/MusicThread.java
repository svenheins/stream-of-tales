/* Copyright (C) Sven Heins - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * author: Sven Heins
 * date: 05.07.2015
 */
package de.svenheins.threads;

import javafx.scene.media.MediaPlayer;
import de.svenheins.main.GameStates;
import de.svenheins.main.GameWindow;
import de.svenheins.main.MusicName;
import de.svenheins.main.MusicType;
import de.svenheins.managers.MusicManager;

public class MusicThread implements Runnable{
	 	private volatile Thread threadManager;
		private long interval = 100;
		private MediaPlayer mediaPlayer;
		private double maxVolume;
		private double volume;
		private boolean fadingOut;
		private boolean fadingIn;
		private long fadingOutStart;
		private long fadingOutDuration;
		private long fadingInStart;
		private long fadingInDuration;
		private boolean fadedOut;
		private MusicType musicType;
		private MusicName musicName;
	 
		public MusicThread(MusicType musicType, MusicName musicName, double maxVolume, boolean fadingIn) {
			 this.mediaPlayer = MusicManager.get(musicName);
			 this.setMusicType(musicType);
			 this.setMusicName(musicName);
			 this.setMaxVolume(maxVolume*mediaPlayer.getVolume());
			 this.setFadingIn(fadingIn);
			 this.setFadingInDuration(GameStates.fadeInDuration);
		}

	    public void stop() {
	    	this.mediaPlayer.stop();
	    	GameWindow.gw.sound.setInactive(musicType, musicName);
	    	threadManager = null;
	    }
	    
	    /** start the thread */
	    public void start() {
	        threadManager = new Thread(this);
	        threadManager.start();
	        // always loop music
	        if (this.fadingIn) {
	        	if (this.getFadingInDuration() > 0) {
		        	this.mediaPlayer.setVolume(0);
		        	this.setFadingInStart(System.currentTimeMillis());
	        	} else {
	        		this.mediaPlayer.setVolume(maxVolume);
	        		this.fadingIn = false;
	        	}
	        }
	        this.mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
	        this.mediaPlayer.play();
	    }
	    
	    /** fade out start */
	    public void startFadeOut(long duration) {
	    	if (duration > 0) {
		    	this.setFadingOutDuration(duration);
		    	this.setFadingOutStart(System.currentTimeMillis());
		    	this.setFadingOut(true);
	    	} else {
	    		System.out.println("Error: duration must be > 0");
	    	}
	    }
	    
	    public void updateFading() {
	    	long timeNow = System.currentTimeMillis();
	    	/** fading Out */
	    	if (this.fadingOut) {
	    		if (timeNow - fadingOutStart > fadingOutDuration) {
	    			this.mediaPlayer.setVolume(0);
	    			this.fadedOut = true;
	    			this.fadingOut = false;
//	    			System.out.println("faded out: "+this.getMusicName());
	    		} else {
	    			if (fadingOutDuration != 0) {
	    				double relativeVolume = (1- (double)((timeNow - fadingOutStart))/fadingOutDuration)*maxVolume;
	    				this.mediaPlayer.setVolume(relativeVolume);
	    			}
	    		}
	    	/** fading in */
	    	} else if (this.fadingIn) {
	    		if (timeNow - fadingInStart > fadingInDuration) {
	    			this.mediaPlayer.setVolume(maxVolume);
	    			this.setFadingIn(false);
//	    			System.out.println("faded in: "+this.getMusicName());
	    		} else {
	    			if (fadingInDuration != 0) {
	    				double relativeVolume = ((double)((timeNow - fadingInStart))/fadingInDuration)*maxVolume;
	    				this.mediaPlayer.setVolume(relativeVolume);
	    			}
	    		}
	    	} else {
	    		// just let it play
	    	}
	    }
	    
	    public void update() {
	    	/** if this music is the actual playing music */
	    	if (GameWindow.gw.sound.getMusic(getMusicType()).equals(getMusicName())) {
//	    		System.out.println("i'm playing");
	    		/** prevent fading out -> fade back in */
	    		if (fadingOut) {
	    			fadingInStart = fadingOutStart;
	    			fadingInDuration = (long) ((((float) (System.currentTimeMillis()-fadingOutStart)/fadingOutDuration))*GameStates.fadeInDuration);
	    			this.setFadingIn(true);
	    			this.setFadingOut(false);
//	    			System.out.println("fading back in: "+this.getMusicName());
	    		} else {
	    			// everything is fine -> continue fading in or playing the track */
	    		}
	    	/** else this is not the actual playing music for this kind of musicType */
	    	} else {
	    		if (!fadingOut) {
//	    			System.out.println("fading out: "+this.getMusicName());
	    			if (this.getMusicType() == MusicType.MOVINGSOUND) {
	    				this.startFadeOut(GameStates.fadeOutDurationMovingsound);
	    			} else {
	    				this.startFadeOut(GameStates.fadeOutDuration);
	    			}
	    			
	    		}
	    	}
	    }
	    
	    
	    /** main run-loop */
	    public void run() {
	    	Thread thisThread = Thread.currentThread();
	        while (threadManager == thisThread) {
	        	GameWindow.gw.sound.setActive(musicType, musicName);
	            try {
	                thisThread.sleep(interval);
	            } catch (InterruptedException e){
	            }
	            /** update fadingIn, fadingOut or just let it play */
	            updateFading();
	            if (!this.isFadedOut()) {
	            	// just let it run!
	            } else {
	            	this.stop();
	            }
	            update();
	        }
	    }

		public double getVolume() {
			return volume;
		}

		public void setVolume(double volume) {
			this.volume = volume;
		}


		public double getMaxVolume() {
			return maxVolume;
		}

		public void setMaxVolume(double maxVolume) {
			this.maxVolume = maxVolume;
		}

		public boolean isFadedOut() {
			return fadedOut;
		}

		public void setFadedOut(boolean fadedOut) {
			this.fadedOut = fadedOut;
		}

		public boolean isFadingOut() {
			return fadingOut;
		}

		public void setFadingOut(boolean fadingOut) {
			this.fadingOut = fadingOut;
		}

		public boolean isFadingIn() {
			return fadingIn;
		}

		public void setFadingIn(boolean fadingIn) {
			this.fadingIn = fadingIn;
		}

		public long getFadingOutStart() {
			return fadingOutStart;
		}

		public void setFadingOutStart(long fadingOutStart) {
			this.fadingOutStart = fadingOutStart;
		}

		public long getFadingOutDuration() {
			return fadingOutDuration;
		}

		public void setFadingOutDuration(long fadingOutDuration) {
			this.fadingOutDuration = fadingOutDuration;
		}

		public long getFadingInStart() {
			return fadingInStart;
		}

		public void setFadingInStart(long fadingInStart) {
			this.fadingInStart = fadingInStart;
		}

		public long getFadingInDuration() {
			return fadingInDuration;
		}

		public void setFadingInDuration(long fadingInDuration) {
			this.fadingInDuration = fadingInDuration;
		}

		public MusicType getMusicType() {
			return musicType;
		}

		public void setMusicType(MusicType musicType) {
			this.musicType = musicType;
		}

		public MusicName getMusicName() {
			return musicName;
		}

		public void setMusicName(MusicName musicName) {
			this.musicName = musicName;
		}
}
