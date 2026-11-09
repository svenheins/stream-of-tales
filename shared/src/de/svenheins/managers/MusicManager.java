/* Copyright (C) Sven Heins - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * author: Sven Heins
 * date: 05.07.2015
 */
package de.svenheins.managers;

import java.util.HashMap;

import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import de.svenheins.main.MusicName;
import de.svenheins.objects.MusicData;

public class MusicManager {
	private static HashMap<MusicName, MusicData> musicDataList = new HashMap<MusicName, MusicData>();
	
	public static void init() {
		for (MusicName musicName: MusicName.values()) {
			switch(musicName) {
			case MAINMENU: {
				MusicData musicData = new MusicData("218744__edtijo__epic-orchestra.mp3");
				musicDataList.put(musicName, musicData);
				break;
			}
			case GAME: {
				MusicData musicData = new MusicData("game.mp3");
				musicDataList.put(musicName, musicData);
				break;
			}
			case GAMEMASTER: {
				MusicData musicData = new MusicData("mainmenu.mp3");
				musicDataList.put(musicName, musicData);
				break;
			}
			case DUNGEON: {
				MusicData musicData = new MusicData("204106_edtijo_creepy-horror-ambiant.mp3");
				musicDataList.put(musicName, musicData);
				break;
			}
			case FOOTSTEPS: {
				MusicData musicData = new MusicData("149233__conleec__foley-footsteps-grass-001_40.mp3");
				musicDataList.put(musicName, musicData);
				break;
			}
			case FORESTBIRDS: {
				MusicData musicData = new MusicData("156826__kvgarlic__woodthrushinmorningshawneeforestmay272012.mp3");
				musicDataList.put(musicName, musicData);
				break;
			}
			case NIGHTOWL: {
				MusicData musicData = new MusicData("203598__raoul-slayer__cricketsandtawnyowl_4.mp3");
				musicDataList.put(musicName, musicData);
				break;
			}
			case CAMPFIRE: {
				MusicData musicData = new MusicData("234288__leosalom__fireplace.mp3");
				musicDataList.put(musicName, musicData);
				break;
			}
			
			
			default: {
				break;
			}
			}
		}
	}
	
	public static MusicData getMusicData(MusicName musicName) {
		if (musicDataList.containsKey(musicName)) {
			return musicDataList.get(musicName);
		} else {
			return null;
		}
	}
	
	/** return either the soundEffect or null (if its not in the list) */
	public static MediaPlayer get(MusicName musicName) {		
		MediaPlayer mediaPlayer = new MediaPlayer(getMusicData(musicName).getMedia());	
		
		/** some corrections for the looping */
		switch (musicName) {
		case MAINMENU: {
			mediaPlayer.setVolume(0.4);
			break;
		}
		case GAME: {
			mediaPlayer.setStartTime(new Duration(0));
			mediaPlayer.setStopTime(new Duration(20425));
			mediaPlayer.setVolume(0.8);
			break;
		}
		case DUNGEON: {
			mediaPlayer.setVolume(0.3);
			break;
		}
		case GAMEMASTER: {
			mediaPlayer.setVolume(0.6);
			break;
		}
		case FOOTSTEPS: {
			mediaPlayer.setVolume(0.4);
			break;
		}
		case CAMPFIRE: {
			mediaPlayer.setVolume(0.2);
			break;
		}
		default: {
			break;
		}
		}
		
		return mediaPlayer;
	}
	
//	/** some configurations for different tracks (loop config... */
//	public void correct() {
//		
//	}
}
