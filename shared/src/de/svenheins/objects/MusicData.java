/* Copyright (C) Sven Heins - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * author: Sven Heins
 * date: 05.07.2015
 */
package de.svenheins.objects;

import java.net.URL;

import javafx.scene.media.Media;
import de.svenheins.main.GameStates;

public class MusicData {
	private String fileName;
	private Media hit;
	
	public MusicData(String songName) {
		fileName = songName;
		URL fileNameURL=this.getClass().getResource(GameStates.standardMusicPath+fileName);
		hit = new Media(fileNameURL.toString());
	}
	
	public Media getMedia() {
		return hit;
	}
}
