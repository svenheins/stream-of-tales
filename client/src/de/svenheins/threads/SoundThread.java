/* Copyright (C) Sven Heins - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * author: Sven Heins
 * date: 05.07.2015
 */
package de.svenheins.threads;

import java.util.ArrayList;

import de.svenheins.main.GUI;
import de.svenheins.main.GameModus;
import de.svenheins.main.GameStates;
import de.svenheins.main.GameWindow;
import de.svenheins.main.MusicName;
import de.svenheins.main.MusicType;
import de.svenheins.main.SoundEffect;

public class SoundThread implements Runnable {
	
	@Override
	public void run() {
		while (GUI.running) {
//			System.out.println("sound-thread runs");
			if (GameWindow.gw.sound.isOn()) {
				if(GameModus.modus == GameModus.MAINMENU) {
					mainMenuRun();
				} else if (GameModus.modus == GameModus.GAME) {
					gameRun();
				}
			} else {
				GameWindow.gw.sound.setAllMusicEmpty();
			}
			
			try {
				Thread.sleep(100);
			}
			catch(InterruptedException exception) {
				System.out.println(exception);
			}
		}
	}
	
	private void updateMusic() {
		for (MusicType musicType: MusicType.values()) {
			MusicName musicName = GameWindow.gw.sound.getMusic(musicType);
			if (musicName != MusicName.EMPTY) {
				if (GameWindow.gw.sound.isActive(musicType, musicName)) {
					// OK, musicName is already running under the musicType
				} else {
					// we have to start a thread with the new music
//					System.out.println("Start music thread...");
					GameWindow.gw.sound.setActive(musicType, musicName);
					boolean fadingIn = (musicType == MusicType.MOVINGSOUND) ? false : true;
					double musicVolume = GameStates.getVolumeByMusicType(musicType); 
					(new MusicThread(musicType, musicName, musicVolume, fadingIn)).start();
				}
			}
		}
	}
	
	private void mainMenuRun() {
		if (GameWindow.gw.sound.hasStartingSoundEffects()) {
//			System.out.println("starting sound: ");
			for (SoundEffect soundEffect : GameWindow.gw.sound.getStartSoundEffectList()) {
//				System.out.println(soundEffect);
				(new SoundEffectThread(soundEffect)).start();
			}
		}
		GameWindow.gw.sound.clearStartingSoundEffects();
		updateMusic();
	}
	
	private void gameRun() {
		if (GameWindow.gw.sound.hasStartingSoundEffects()) {
			ArrayList<SoundEffect> tempSoundEffectList = new ArrayList<>(GameWindow.gw.sound.getStartSoundEffectList());
//			System.out.println("starting sound: ");
			for (SoundEffect soundEffect : tempSoundEffectList) {
//				System.out.println(soundEffect);
//				SoundEffectThread set = new SoundEffectThread(soundEffect);
//				if (set != null) {
//					set.start();
//				}
				(new SoundEffectThread(soundEffect)).start();
			}
			GameWindow.gw.sound.clearStartingSoundEffects();
		}
		
		updateMusic();
	}

}
