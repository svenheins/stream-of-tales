package de.svenheins.threads;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import com.sun.sgs.client.ClientChannel;

import de.svenheins.main.GUI;
import de.svenheins.main.GameModus;
import de.svenheins.main.GamePanel;
import de.svenheins.main.GameStates;
import de.svenheins.main.GameWindow;
import de.svenheins.managers.MapManager;
import de.svenheins.managers.SpaceManager;
import de.svenheins.messages.ClientMessages;
import de.svenheins.messages.OBJECTCODE;
import de.svenheins.objects.LocalMap;
import de.svenheins.objects.Player;
import de.svenheins.objects.PlayerEntity;
import de.svenheins.objects.Space;

public class MapUpdateThread implements Runnable {
	private long duration = 0;
	private long oldTime = System.currentTimeMillis();
	private PlayerEntity playerEntity;
	private float playerOldMX = 0;
	private float playerOldMY = 0;
	private final int sleepingTime = 500;
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
		
		while (GUI.running) {
//			duration = System.currentTimeMillis() - last;
			duration = System.currentTimeMillis() - oldTime;
			
//			System.out.println("input-thread runs");
			if((GameModus.modus == GameModus.GAME) && (GameWindow.gw.getPlayerName().equals(GameWindow.gw.getGameMasterName()))) {
				mapUpdateRun();
			}
			
			try {
				Thread.sleep(sleepingTime);
			}
			catch(InterruptedException exception) {
				System.out.println(exception);
			}
			
		}
		
	}
	
	public void mapUpdateRun() {
		if (GameWindow.gw.isLoggedIn() && GamePanel.gp.isInitializedPlayer()) {
			List<Point> idListTempMaps = new ArrayList<Point>(MapManager.pointList);
			for (Point p: idListTempMaps){
				LocalMap localMap = MapManager.get(p);
				if(localMap != null) {
					MapManager.save(localMap, GameStates.standardMapFolder+GameWindow.gw.getPlayerName()+"/"+p.x+"_"+p.y+".map");
				}
				else
					GameWindow.gw.gameInfoConsole.appendInfo("Couldn't write Map");
			}
		}
	}
	
	

}
