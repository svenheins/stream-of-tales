/* Copyright (C) Sven Heins - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * author: Sven Heins
 * date: 05.07.2015
 */
package de.svenheins.threads;

import java.util.HashMap;
import de.svenheins.main.GUI;
import de.svenheins.main.GameModus;
import de.svenheins.main.GameStates;
import de.svenheins.main.GameWindow;
import de.svenheins.managers.StreamViewerManager;
import de.svenheins.managers.TextEffectManager;
import de.svenheins.objects.StreamViewer;
import de.svenheins.objects.TextEffect;

public class StreamViewerThread implements Runnable {
	public int sleepingTime = 100;

	@Override
	public void run() {
		while (GUI.running) {
//			duration = System.currentTimeMillis() - last;
//			last = System.currentTimeMillis();
//			System.out.println("input-thread runs");
			if(GameModus.modus == GameModus.GAME) {
				if (StreamViewerManager.size() > 0) {
					HashMap<String, StreamViewer> tempStreamViewerList = StreamViewerManager.streamViewerList;
					long timeNow = System.currentTimeMillis();
					for (StreamViewer streamViewer: tempStreamViewerList.values()) {
						if (timeNow - streamViewer.getLastUpdateTime() > GameStates.streamTimeInterval) {
							int oldLevel = streamViewer.getLevel();
							streamViewer.setLastUpdateTime(timeNow);
							streamViewer.setGold(streamViewer.getGold()+GameStates.streamGoldPerInterval);
							streamViewer.setExperience(streamViewer.getExperience()+GameStates.streamExperiencePerInterval);
							streamViewer.updateLevelByExperience();
							if (streamViewer.getLevel() > oldLevel) {
								TextEffectManager.add(new TextEffect("Level up! "+streamViewer.getName()+", you are now Level "+streamViewer.getLevel()+"!"));
							}
							String[] colNames = new String[]{"name", "gold", "experience"};
							String[] values = new String[]{streamViewer.getName(), ""+streamViewer.getGold(), ""+streamViewer.getExperience()};
							if (!GameWindow.gw.getMySQL().contains("name", streamViewer.getName())) {
								// first entry
								GameWindow.gw.getMySQL().insert(colNames, values);
							} else {
								colNames = new String[]{"name", "comment", "experience", "level", "gold", "countPro", "countContra", "countKilledEnemies", "countKilledPlayers","countIngameMessages",  "countDroppedItems", "red", "green", "blue"};
								values = new String[]{"'"+streamViewer.getName()+"'", "'"+streamViewer.getComment()+"'", ""+streamViewer.getExperience(), ""+streamViewer.getLevel(), ""+streamViewer.getGold(),""+ streamViewer.getCountPro(), ""+streamViewer.getCountContra(), ""+streamViewer.getCountKilledEnemies(), ""+streamViewer.getCountKilledPlayers(), ""+streamViewer.getCountIngameMessages(), ""+streamViewer.getCountDroppedItems(), ""+streamViewer.getRed(), ""+ streamViewer.getGreen(), ""+streamViewer.getBlue()};
								GameWindow.gw.getMySQL().update("name", streamViewer.getName(), colNames, values);
							}
						}
					}
				}
			}
			
			try {
				Thread.sleep(sleepingTime);
			}
			catch(InterruptedException exception) {
				System.out.println("Error: ");
				System.out.println(exception);
			}
		}
	}

}
