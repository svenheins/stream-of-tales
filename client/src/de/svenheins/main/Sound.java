/* Copyright (C) Sven Heins - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * author: Sven Heins
 * date: 05.07.2015
 */
package de.svenheins.main;

import java.util.ArrayList;
import java.util.HashMap;

/** this class administrates the incoming soundEffects as well as changing music */
public class Sound {
	private ArrayList<SoundEffect> startSoundEffectList = new ArrayList<SoundEffect>();
	private HashMap<MusicType, MusicName> musicList = new HashMap<MusicType, MusicName>();
	private HashMap<MusicType, HashMap<MusicName, Boolean>> activeMusicType = new HashMap<MusicType, HashMap<MusicName,Boolean>>();
	private boolean on;
	
	public Sound() {
		this.init();
	}
	
	public void init() {
		this.setOn(true);
		this.setAllMusicEmpty();
	}
	
	public void setAllMusicEmpty() {
		for (MusicType musicType : MusicType.values()) {
			musicList.put(musicType, MusicName.EMPTY);
			for (MusicName musicName : MusicName.values()) {
				HashMap<MusicName, Boolean> musicNameActivated = new HashMap<MusicName, Boolean>();
				musicNameActivated.put(musicName, Boolean.FALSE);
				activeMusicType.put(musicType, musicNameActivated);
			}
		}
	}
	
	public void setActive(MusicType musicType, MusicName musicName) {
//		System.out.println("activated music "+musicName+" in type: "+musicType);
		activeMusicType.get(musicType).put(musicName, Boolean.TRUE);
	}
	
	public void startMusic(MusicType musicType, MusicName musicName) {
		musicList.put(musicType, musicName);
	}
	
	public boolean isActive(MusicType musicType, MusicName musicName) {
		return (getActiveMusicType().get(musicType).get(musicName) == Boolean.TRUE);
	}
	
	
	public void setInactive(MusicType musicType, MusicName musicName) {
		activeMusicType.get(musicType).put(musicName, Boolean.FALSE);
	}
	
	public MusicName getMusic(MusicType musicType) {
		if (musicList.containsKey(musicType)) {
			return musicList.get(musicType);
		} else {
			return null;
		}
	}
	
	public void addStartingSoundEffect(SoundEffect soundEffect ) {
//		System.out.println("added starting sound");
		startSoundEffectList.add(soundEffect);
	}
	
	public void removeStartingSoundEffect(int index) {
		this.startSoundEffectList.remove(index);
	}
	
	public void clearStartingSoundEffects() {
		startSoundEffectList = new ArrayList<SoundEffect>();
	}
	
	public boolean hasStartingSoundEffects() {
		return (startSoundEffectList.size() >0);
	}
	
	public ArrayList<SoundEffect> getStartSoundEffectList() {
		return startSoundEffectList;
	}

	public HashMap<MusicType, MusicName> getMusicList() {
		return musicList;
	}

	public void setMusicList(HashMap<MusicType, MusicName> musicList) {
		this.musicList = musicList;
	}

	public HashMap<MusicType, HashMap<MusicName, Boolean>> getActiveMusicType() {
		return activeMusicType;
	}

	public void setActiveMusicType(HashMap<MusicType, HashMap<MusicName, Boolean>> activeMusicType) {
		this.activeMusicType = activeMusicType;
	}

	public boolean isOn() {
		return on;
	}

	public void setOn(boolean on) {
		this.on = on;
	}
	

}
