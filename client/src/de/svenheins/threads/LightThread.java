/* Copyright (C) Sven Heins - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * author: Sven Heins
 * date: 05.07.2015
 */
package de.svenheins.threads;

import java.awt.Rectangle;
import de.svenheins.main.GUI;
import de.svenheins.main.GameModus;
import de.svenheins.main.GamePanel;
import de.svenheins.main.GameStates;
import de.svenheins.managers.LightManager;

public class LightThread implements Runnable {
	private final int sleepingTime = 20;
	
	
	@Override
	public void run() {
		while (GUI.running) {
//			duration = System.currentTimeMillis() - last;
//			last = System.currentTimeMillis();
//			System.out.println("input-thread runs");
			if(GameModus.modus == GameModus.GAME) {
				calculateLight();
				
//				MapManager mapManager = GameWindow.gw.getMapManagers().get("cobble");
//				for (int runX = -GameStates.factorOfViewDeleteDistance; runX <= GameStates.factorOfViewDeleteDistance; runX++) {
//					for (int runY = -GameStates.factorOfViewDeleteDistance; runY <= GameStates.factorOfViewDeleteDistance; runY++) {
//						int[][] localMap = mapManager.get(new Point(runX, runY)).getLocalMap();
//						for (int i = 0; i< localMap.length; i++) {
//							for (int j = 0; j < localMap[0].length; j++) {
//								if (localMap[i][j] == GameStates.lightTile) {
//									int standardLight = GameStates.maxLightIntensityCutoff + 2;
//									BigInteger id = LightManager.maxIDValue;
//									id = id.add(BigInteger.ONE);
//									Light light1 = new Light(id, standardLight,	new Point(runX+GameStates.mapTileSetWidth*i, runY+GameStates.mapTileSetHeight*j));
//									LightManager.add(light1);
//								}
//							}
//							
//						}
//					}
//				}	
				
			}
			
			try {
				Thread.sleep(sleepingTime);
			}
			catch(InterruptedException exception) {
				System.out.println(exception);
			}
		}
	}

	private void calculateLight() {
//		Rectangle lightRect = new Rectangle((int)(GamePanel.gp.getPlayerEntity().getX()+GamePanel.gp.getPlayerEntity().getWidth()/2-((float)GameStates.lightMapWidth/2) - ((GamePanel.gp.getPlayerEntity().getX()+GamePanel.gp.getPlayerEntity().getWidth()/2) % GameStates.mapTileSetWidth)), (int)(GamePanel.gp.getPlayerEntity().getY()+GamePanel.gp.getPlayerEntity().getHeight()/2-((float)GameStates.lightMapHeight/2) - ((GamePanel.gp.getPlayerEntity().getY()+GamePanel.gp.getPlayerEntity().getHeight()/2) % GameStates.mapTileSetHeight)), GameStates.lightMapWidth, GameStates.lightMapHeight);
		Rectangle lightRect = new Rectangle((int) (GamePanel.gp.getPlayerEntity().getX()+GamePanel.gp.getPlayerEntity().getWidth()/2	- ((float) GameStates.lightMapWidth / 2) - ((GamePanel.gp.getPlayerEntity().getX()+GamePanel.gp.getPlayerEntity().getWidth()/2) % GameStates.mapTileSetWidth)),	(int) (GamePanel.gp.getPlayerEntity().getY()+GamePanel.gp.getPlayerEntity().getHeight()/2- ((float) GameStates.lightMapHeight / 2) - ((GamePanel.gp.getPlayerEntity().getY()+GamePanel.gp.getPlayerEntity().getHeight()/2)% GameStates.mapTileSetHeight)),GameStates.lightMapWidth, GameStates.lightMapHeight);
		GamePanel.gp.setLightMap(LightManager.getLightMap(lightRect));
	}

}
