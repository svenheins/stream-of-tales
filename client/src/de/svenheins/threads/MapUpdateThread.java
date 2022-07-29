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
import de.svenheins.managers.ObjectMapManager;
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
		
		
		while (GUI.running) {
			
			duration = System.currentTimeMillis() - oldTime;
			
			if (GameModus.modus == GameModus.GAME) {
				int localWidth = GameStates.mapWidth * GameStates.mapTileSetWidth;
				int localHeight = GameStates.mapHeight * GameStates.mapTileSetHeight;
				int latticePointX = (int) Math.floor( (float) GamePanel.gp.getPlayerEntity().getX() / (localWidth)) * localWidth;
				int latticePointY = (int) Math.floor( (float) GamePanel.gp.getPlayerEntity().getY() / (localHeight)) * localHeight;
//				int localX = (int) Math.floor( (float) (correctedPoint.x - latticePointX )/ GameStates.mapTileSetWidth);
//				int localY = (int) Math.floor( (float) (correctedPoint.y - latticePointY )/ GameStates.mapTileSetHeight);
				for (MapManager mapManager: GameWindow.gw.getMapManagers().values()) {
					mapLoad(mapManager, latticePointX, latticePointY-localHeight);
					mapLoad(mapManager, latticePointX-localWidth, latticePointY-localHeight);
					mapLoad(mapManager, latticePointX+localWidth, latticePointY-localHeight);
					mapLoad(mapManager, latticePointX, latticePointY);
					mapLoad(mapManager, latticePointX-localWidth, latticePointY);
					mapLoad(mapManager, latticePointX+localWidth, latticePointY);
					mapLoad(mapManager, latticePointX, latticePointY+localHeight);
					mapLoad(mapManager, latticePointX-localWidth, latticePointY+localHeight);
					mapLoad(mapManager, latticePointX+localWidth, latticePointY+localHeight);
					
					/** additionally loaded files */
					mapLoad(mapManager, latticePointX, latticePointY-2*localHeight);
					mapLoad(mapManager, latticePointX-localWidth, latticePointY-2*localHeight);
					mapLoad(mapManager, latticePointX+localWidth, latticePointY-2*localHeight);
					mapLoad(mapManager, latticePointX-2*localWidth, latticePointY-2*localHeight);
					mapLoad(mapManager, latticePointX+2*localWidth, latticePointY-2*localHeight);
					
					mapLoad(mapManager, latticePointX-2*localWidth, latticePointY);
					mapLoad(mapManager, latticePointX+2*localWidth, latticePointY);
					
					mapLoad(mapManager, latticePointX, latticePointY+2*localHeight);
					mapLoad(mapManager, latticePointX-localWidth, latticePointY+2*localHeight);
					mapLoad(mapManager, latticePointX+localWidth, latticePointY+2*localHeight);
					mapLoad(mapManager, latticePointX-2*localWidth, latticePointY+2*localHeight);
					mapLoad(mapManager, latticePointX+2*localWidth, latticePointY+2*localHeight);
				}
				for (ObjectMapManager mapManager: GameWindow.gw.getObjectMapManagers().values()) {
					mapLoad(mapManager, latticePointX, latticePointY-localHeight);
					mapLoad(mapManager, latticePointX-localWidth, latticePointY-localHeight);
					mapLoad(mapManager, latticePointX+localWidth, latticePointY-localHeight);
					mapLoad(mapManager, latticePointX, latticePointY);
					mapLoad(mapManager, latticePointX-localWidth, latticePointY);
					mapLoad(mapManager, latticePointX+localWidth, latticePointY);
					mapLoad(mapManager, latticePointX, latticePointY+localHeight);
					mapLoad(mapManager, latticePointX-localWidth, latticePointY+localHeight);
					mapLoad(mapManager, latticePointX+localWidth, latticePointY+localHeight);
					/** additionally loaded files */
					mapLoad(mapManager, latticePointX, latticePointY-2*localHeight);
					mapLoad(mapManager, latticePointX-localWidth, latticePointY-2*localHeight);
					mapLoad(mapManager, latticePointX+localWidth, latticePointY-2*localHeight);
					mapLoad(mapManager, latticePointX-2*localWidth, latticePointY-2*localHeight);
					mapLoad(mapManager, latticePointX+2*localWidth, latticePointY-2*localHeight);
					
					mapLoad(mapManager, latticePointX-2*localWidth, latticePointY);
					mapLoad(mapManager, latticePointX+2*localWidth, latticePointY);
					
					mapLoad(mapManager, latticePointX, latticePointY+2*localHeight);
					mapLoad(mapManager, latticePointX-localWidth, latticePointY+2*localHeight);
					mapLoad(mapManager, latticePointX+localWidth, latticePointY+2*localHeight);
					mapLoad(mapManager, latticePointX-2*localWidth, latticePointY+2*localHeight);
					mapLoad(mapManager, latticePointX+2*localWidth, latticePointY+2*localHeight);
				}
			}
			
//			System.out.println("input-thread runs");
			if((GameModus.modus == GameModus.GAME) && (GameWindow.gw.getPlayerName().equals(GameWindow.gw.getGameMasterName()))) {
				for (MapManager mapManager: GameWindow.gw.getMapManagers().values()) {
					mapSaveUpdateRun(mapManager);
				}
				for (ObjectMapManager objectMapManager: GameWindow.gw.getObjectMapManagers().values()) {
					mapSaveUpdateRun(objectMapManager);
				}
			}
			
			try {
				Thread.sleep(sleepingTime);
			}
			catch(InterruptedException exception) {
				System.out.println(exception);
			}
			
		}
		
	}
	
	public void mapSaveUpdateRun(MapManager mapManager) {
		if (GameWindow.gw.isLoggedIn() && GamePanel.gp.isInitializedPlayer() && (GameWindow.gw.getPlayerName().equals(GameWindow.gw.getGameMasterName())) ) {
			List<Point> idListTempMaps = new ArrayList<Point>(mapManager.pointList);
			for (Point p: idListTempMaps){
				LocalMap localMap = mapManager.get(p);
				if(localMap != null) {
					mapManager.save(localMap, GameStates.standardMapFolder+GameWindow.gw.getPlayerName()+"/"+mapManager.getPaintLayer()+"_"+p.x+"_"+p.y+".map");
				}
				else
					GameWindow.gw.gameInfoConsole.appendInfo("Couldn't write Map");
			}
			
		}
	}
	
	public void mapSaveUpdateRun(ObjectMapManager mapManager) {
		if (GameWindow.gw.isLoggedIn() && GamePanel.gp.isInitializedPlayer() && (GameWindow.gw.getPlayerName().equals(GameWindow.gw.getGameMasterName()))) {
			List<Point> idListTempMaps = new ArrayList<Point>(mapManager.pointList);
			for (Point p: idListTempMaps){
				LocalMap localMap = mapManager.get(p);
				if(localMap != null) {
					mapManager.save(localMap, GameStates.standardMapFolder+GameWindow.gw.getPlayerName()+"/"+mapManager.getPaintLayer()+"_"+p.x+"_"+p.y+".map");
				}
				else
					GameWindow.gw.gameInfoConsole.appendInfo("Couldn't write Map");
			}
			
		}
	}
	
	public void mapLoad(MapManager mapManager, int x, int y) {
		if (!mapManager.contains(new Point(x, y))) {
			/** search in folder */
			mapManager.loadFromFileSystem(GameWindow.gw.getPlayerName(), x, y);
//			System.out.println("Loaded x="+x+" and y="+y);
		}
	}
	
	public void mapLoad(ObjectMapManager mapManager, int x, int y) {
		if (!mapManager.contains(new Point(x, y))) {
			/** search in folder */
			mapManager.loadFromFileSystem(GameWindow.gw.getPlayerName(), x, y);
//			System.out.println("Loaded x="+x+" and y="+y);
		}
	}

}
