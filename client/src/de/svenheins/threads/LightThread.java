package de.svenheins.threads;

import java.awt.Rectangle;

import de.svenheins.main.GUI;
import de.svenheins.main.GameModus;
import de.svenheins.main.GamePanel;
import de.svenheins.main.GameStates;
import de.svenheins.managers.LightManager;

public class LightThread implements Runnable {
	private final int sleepingTime = 200;
	
	@Override
	public void run() {
		while (GUI.running) {
//			duration = System.currentTimeMillis() - last;
//			last = System.currentTimeMillis();
//			System.out.println("input-thread runs");
			if(GameModus.modus == GameModus.GAME) {
				calculateLight();
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
		Rectangle lightRect = new Rectangle((int)(GamePanel.gp.getPlayerEntity().getX()-((float)GameStates.lightMapWidth/2) - (GamePanel.gp.getPlayerEntity().getX() % GameStates.mapTileSetWidth)), (int)(GamePanel.gp.getPlayerEntity().getY()-((float)GameStates.lightMapHeight/2) - (GamePanel.gp.getPlayerEntity().getY() % GameStates.mapTileSetHeight)), GameStates.lightMapWidth, GameStates.lightMapHeight);
		GamePanel.gp.setLightMap(LightManager.getLightMap(lightRect));
	}

}
