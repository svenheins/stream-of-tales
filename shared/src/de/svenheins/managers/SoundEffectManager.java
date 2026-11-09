/* Copyright (C) Sven Heins - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * author: Sven Heins
 * date: 05.07.2015
 */
package de.svenheins.managers;

import java.util.HashMap;

import de.svenheins.main.SoundEffect;
import de.svenheins.objects.SoundData;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public class SoundEffectManager {
	private static HashMap<SoundEffect, SoundData> soundDataList = new HashMap<SoundEffect, SoundData>();
	
	
	public static void init() {
		for (SoundEffect soundEffect: SoundEffect.values()) {
			switch(soundEffect) {
			case SWORD: {
				SoundData soundData = new SoundData("swoosh.mp3");//"201216__pasdepoisson__swoosh.wav");//"sword.wav");
				soundDataList.put(soundEffect, soundData);
				break;
			}
			case HURT: {
				SoundData soundData = new SoundData("standardPlayerHurt.mp3");
				soundDataList.put(soundEffect, soundData);
				break;
			}
			case BUTTON: {
				SoundData soundData = new SoundData("drum.mp3");
				soundDataList.put(soundEffect, soundData);
				break;
			}
			case STANDARDPLAYER_ATTACK: {
				SoundData soundData = new SoundData("swoosh.mp3");//"201216__pasdepoisson__swoosh.wav");//"standardPlayerAttack.wav");
				soundDataList.put(soundEffect, soundData);
				break;
			}
			case WOLF_ATTACK: {
				SoundData soundData = new SoundData("190595__jaylew1987__dog-snarl-self-made_leiser.mp3");//"standardPlayerAttack.wav");
				soundDataList.put(soundEffect, soundData);
				break;
			}
			case BIG_SWOOSH: {
				SoundData soundData = new SoundData("237980__paul368__very-big-whoosh.mp3");
				soundDataList.put(soundEffect, soundData);
				break;
			}
			case STANDARDPLAYER_HURT: {
				SoundData soundData = new SoundData("standardPlayerHurt.mp3");
				soundDataList.put(soundEffect, soundData);
				break;
			}
			case WOLF_HURT: {
				SoundData soundData = new SoundData("72579__moffet__whimpering-and-barking_leiser.mp3");
				soundDataList.put(soundEffect, soundData);
				break;
			}
			case HIT_SOIL: {
				SoundData soundData = new SoundData("194456__eneasz__wet-slop-plop.mp3");
				soundDataList.put(soundEffect, soundData);
				break;
			}
			case HIT_STONE: {
				SoundData soundData = new SoundData("231751__k06a__stone-and-cup-hit.mp3");
				soundDataList.put(soundEffect, soundData);
				break;
			}
			case HIT_WOOD: {
				SoundData soundData = new SoundData("151972__amsempl__chop-wood_leiser.mp3");
				soundDataList.put(soundEffect, soundData);
				break;
			}
			case COLLECT_ITEM: {
				SoundData soundData = new SoundData("264121__kessir__opening-a-small-bottle-filled-with-liquid-and-shaking-it.mp3");
				soundDataList.put(soundEffect, soundData);
				break;
			}
			case PLACE_OBJECT: {
				SoundData soundData = new SoundData("188797__qubodup__mouth-pop_2.mp3");
				soundDataList.put(soundEffect, soundData);
				break;
			}
			case EATING: {
				SoundData soundData = new SoundData("169761__niwki__eating-apple_leiser.mp3");
				soundDataList.put(soundEffect, soundData);
				break;
			}
			case DRINKING: {
				SoundData soundData = new SoundData("71233__handfan__drinking-01.mp3");
				soundDataList.put(soundEffect, soundData);
				break;
			}
			default: {
				break;
			}
			}
			
			MediaPlayer testPlayer = SoundEffectManager.get(soundEffect);
		}
	}
	
	public static SoundData getSoundData(SoundEffect soundEffect) {
		if (soundDataList.containsKey(soundEffect)) {
			return soundDataList.get(soundEffect);
		} else {
			return null;
		}
	}
	
	/** return either the soundEffect or null (if its not in the list) */
	public static MediaPlayer get(SoundEffect soundEffect) {
		SoundData tempSoundData = getSoundData(soundEffect);
		if (tempSoundData != null) {
			Media media = getSoundData(soundEffect).getMedia();
			if (media != null) {
				MediaPlayer mediaPlayer = new MediaPlayer(getSoundData(soundEffect).getMedia());	
				
				/** some corrections for the looping */
				switch (soundEffect) {
				case STANDARDPLAYER_ATTACK: {
					mediaPlayer.setVolume(0.5);
					break;
				}
				case STANDARDPLAYER_HURT: {
					mediaPlayer.setVolume(0.5);
					break;
				}
				default: {
					break;
				}
				}
				
				return mediaPlayer;
			} else {
				return null;
			}
			
			
			
		} else {
			return null;
		}
	}
}
