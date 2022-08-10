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
import de.svenheins.managers.UndergroundMapManager;
import de.svenheins.messages.ClientMessages;
import de.svenheins.messages.OBJECTCODE;
import de.svenheins.objects.LocalMap;
import de.svenheins.objects.LocalUndergroundMap;
import de.svenheins.objects.Player;
import de.svenheins.objects.PlayerEntity;
import de.svenheins.objects.Space;

public class MapUpdateThread implements Runnable {
	private long duration = 0;
	private long oldTime = System.currentTimeMillis();
	private final int sleepingTime = 200;
	
	@Override
	public void run() {
		
		
		while (GUI.running) {
			
			duration = System.currentTimeMillis() - oldTime;
			
			if (GameModus.modus == GameModus.GAME) {
				
				/** save all maps that need to be saved */
				if (GameWindow.gw.isGameMaster()) {
					for (MapManager mapManager: GameWindow.gw.getMapManagers().values()) {
						mapSaveUpdateRun(mapManager);
					}
					for (ObjectMapManager objectMapManager: GameWindow.gw.getObjectMapManagers().values()) {
						mapSaveUpdateRun(objectMapManager);
					}
					for (UndergroundMapManager undergroundMapManager: GameWindow.gw.getUndergroundMapManagers().values()) {
						mapSaveUpdateRun(undergroundMapManager);
					}
				}
				
				int localWidth = GameStates.mapWidth * GameStates.mapTileSetWidth;
				int localHeight = GameStates.mapHeight * GameStates.mapTileSetHeight;
				int latticePointX = (int) Math.floor( (float) GamePanel.gp.getPlayerEntity().getX() / (localWidth)) * localWidth;
				int latticePointY = (int) Math.floor( (float) GamePanel.gp.getPlayerEntity().getY() / (localHeight)) * localHeight;
				for (MapManager mapManager: GameWindow.gw.getMapManagers().values()) {
					for (int runX = -GameStates.factorOfViewDeleteDistance; runX <= GameStates.factorOfViewDeleteDistance; runX++) {
						for (int runY = -GameStates.factorOfViewDeleteDistance; runY <= GameStates.factorOfViewDeleteDistance; runY++) {
							mapLoad(mapManager, latticePointX + runX*localWidth, latticePointY + runY*localHeight);
						}
					}
					
				}
				for (ObjectMapManager mapManager: GameWindow.gw.getObjectMapManagers().values()) {
					for (int runX = -GameStates.factorOfViewDeleteDistance; runX <= GameStates.factorOfViewDeleteDistance; runX++) {
						for (int runY = -GameStates.factorOfViewDeleteDistance; runY <= GameStates.factorOfViewDeleteDistance; runY++) {
							mapLoad(mapManager, latticePointX + runX*localWidth, latticePointY + runY*localHeight);
						}
					}
				}
				int localWidthUnderground = GameStates.ugrMapWidth * GameStates.ugrMapTileSetWidth;
				int localHeightUnderground = GameStates.ugrMapHeight * GameStates.ugrMapTileSetHeight;
				int latticePointXUnderground = (int) Math.floor( (float) GamePanel.gp.getPlayerEntity().getX() / (localWidthUnderground)) * localWidthUnderground;
				int latticePointYUnderground = (int) Math.floor( (float) GamePanel.gp.getPlayerEntity().getY() / (localHeightUnderground)) * localHeightUnderground;
				for (UndergroundMapManager undergroundMapManager: GameWindow.gw.getUndergroundMapManagers().values()) {
					for (int runX = -GameStates.factorOfViewDeleteDistance; runX <= GameStates.factorOfViewDeleteDistance; runX++) {
						for (int runY = -GameStates.factorOfViewDeleteDistance; runY <= GameStates.factorOfViewDeleteDistance; runY++) {
							mapLoad(undergroundMapManager, latticePointXUnderground + runX*localWidthUnderground, latticePointYUnderground + runY*localHeightUnderground);
						}
					}
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
		if (GameWindow.gw.isLoggedIn() && GamePanel.gp.isInitializedPlayer() && (GameWindow.gw.isGameMaster()) ) {
			List<Point> idListTempMaps = new ArrayList<Point>(mapManager.getChangedList());
			for (Point p: idListTempMaps){
				LocalMap localMap = mapManager.get(p);
				if(localMap != null) {
					String mapFileName = mapManager.getPaintLayer()+"_"+p.x+"_"+p.y+".map";
					mapManager.save(localMap, GameStates.standardMapFolder+GameWindow.gw.getPlayerName()+"/"+ mapFileName);
					
					/** do not instantly send maps but create the sendQueue */
					GameWindow.gw.addSendMapListEntry(mapFileName);
					mapManager.removeStayList(p);
				}
				else
					GameWindow.gw.gameInfoConsole.appendInfo("Couldn't write Map");
			}
			mapManager.emptyChangedList();
		}
	}
	
	public void mapSaveUpdateRun(ObjectMapManager mapManager) {
		if (GameWindow.gw.isLoggedIn() && GamePanel.gp.isInitializedPlayer() && (GameWindow.gw.isGameMaster())) {
			List<Point> idListTempMaps = new ArrayList<Point>(mapManager.getChangedList());
			for (Point p: idListTempMaps){
				LocalMap localMap = mapManager.get(p);
				if(localMap != null) {
					String mapFileName = mapManager.getPaintLayer()+"_"+p.x+"_"+p.y+".map";
					mapManager.save(localMap, GameStates.standardMapFolder+GameWindow.gw.getPlayerName()+"/"+mapFileName);
					GameWindow.gw.addSendMapListEntry(mapFileName);
					mapManager.removeStayList(p);
				}
				else
					GameWindow.gw.gameInfoConsole.appendInfo("Couldn't write Map");
			}
			mapManager.emptyChangedList();
		}
	}
	
	public void mapSaveUpdateRun(UndergroundMapManager mapManager) {
		if (GameWindow.gw.isLoggedIn() && GamePanel.gp.isInitializedPlayer() && (GameWindow.gw.isGameMaster())) {
			List<Point> idListTempMaps = new ArrayList<Point>(mapManager.getChangedList());
			for (Point p: idListTempMaps){
				LocalUndergroundMap localMap = mapManager.get(p);
				if(localMap != null) {
					String mapFileName = mapManager.getPaintLayer()+"_"+p.x+"_"+p.y+".map";
					mapManager.save(localMap, GameStates.standardMapFolder+GameWindow.gw.getPlayerName()+"/"+mapFileName);
					GameWindow.gw.addSendMapListEntry(mapFileName);
					mapManager.removeStayList(p);
				}
				else
					GameWindow.gw.gameInfoConsole.appendInfo("Couldn't write Map");
			}
			mapManager.emptyChangedList();
		}
	}
	
	public void mapLoad(MapManager mapManager, int x, int y) {
		if (!mapManager.contains(new Point(x, y))) {
			/** search in folder */
			mapManager.loadFromFileSystem(GameWindow.gw.getGameMasterName(), x, y);
		}
	}
	
	public void mapLoad(ObjectMapManager objectMapManager, int x, int y) {
		if (!objectMapManager.contains(new Point(x, y))) {
			/** search in folder */
			objectMapManager.loadFromFileSystem(GameWindow.gw.getGameMasterName(), x, y);
		}
	}
	
	public void mapLoad(UndergroundMapManager undergroundMapManager, int x, int y) {
		if (!undergroundMapManager.contains(new Point(x, y))) {
			/** search in folder */
			undergroundMapManager.loadFromFileSystem(GameWindow.gw.getGameMasterName(), x, y);
		}
	}
	
}
