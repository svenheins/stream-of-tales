/* Copyright (C) Sven Heins - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * author: Sven Heins
 * date: 05.07.2015
 */
package de.svenheins.threads;

import java.awt.Color;
import java.awt.Point;
import java.io.File;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.Random;

import de.svenheins.functions.MyUtil;
import de.svenheins.main.GUI;
import de.svenheins.main.GameModus;
import de.svenheins.main.GamePanel;
import de.svenheins.main.GameStates;
import de.svenheins.main.GameWindow;
import de.svenheins.main.MapType;
import de.svenheins.main.MiniMapColor;
import de.svenheins.managers.MapManager;
import de.svenheins.managers.ObjectMapManager;
import de.svenheins.managers.UndergroundMapManager;
import de.svenheins.objects.LocalMap;
import de.svenheins.objects.LocalUndergroundMap;

public class MapUpdateThread implements Runnable {
	private long duration = 0;
	private long oldTime = System.currentTimeMillis();
	private final int sleepingTime = 75;
	private int mapLoadCount = 0;
	private final int limitLoadMaps = 20;
	private final int limitRetryLoadMaps = 10;

	// private boolean lockedLoadHoleFromSaveMap = true;

	@Override
	public void run() {
		while (GUI.running) {
			mapLoadCount = 0;

			duration = System.currentTimeMillis() - oldTime;
			
			
			if (GameModus.modus == GameModus.GAME) {
				/** save all maps that need to be saved */
				if (GameWindow.gw.isMapMaster()) {
					List<MapManager> tempMapManagerList = new ArrayList<MapManager>(
							GameWindow.gw.getMapManagers().values());
					try {
						for (MapManager mapManager : tempMapManagerList) {
							mapSaveUpdateRun(mapManager);
						}
					} catch (ConcurrentModificationException ex) {
						ex.printStackTrace();
					}
					List<ObjectMapManager> tempObjectMapManagerList = new ArrayList<ObjectMapManager>(
							GameWindow.gw.getObjectMapManagers().values());
					try {
						for (ObjectMapManager objectMapManager : tempObjectMapManagerList) {
							mapSaveUpdateRun(objectMapManager);
						}
					} catch (ConcurrentModificationException ex) {
						ex.printStackTrace();
					}
					List<ObjectMapManager> tempObjectMapManagerListLowerRoom = new ArrayList<ObjectMapManager>(
							GameWindow.gw.getObjectMapManagersLowerRoom()
									.values());
					try {
						for (ObjectMapManager objectMapManager : tempObjectMapManagerListLowerRoom) {
							mapSaveUpdateRunLowerRoom(objectMapManager);
						}
					} catch (ConcurrentModificationException ex) {
						ex.printStackTrace();
					}
					List<UndergroundMapManager> tempUndergroundMapManagerList = new ArrayList<UndergroundMapManager>(
							GameWindow.gw.getUndergroundMapManagers().values());
					try {
						for (UndergroundMapManager undergroundMapManager : tempUndergroundMapManagerList) {
							mapSaveUpdateRun(undergroundMapManager);
						}
					} catch (ConcurrentModificationException ex) {
						ex.printStackTrace();
					}

				}

				// int localWidthUnderground = GameStates.ugrMapWidth *
				// GameStates.ugrMapTileSetWidth;
				// int localHeightUnderground = GameStates.ugrMapHeight *
				// GameStates.ugrMapTileSetHeight;
				// int latticePointXUnderground = (int) Math.floor( (float)
				// (GamePanel.gp.getPlayerEntity().getX()+localWidthUnderground/2)
				// / (localWidthUnderground)) * localWidthUnderground;
				// int latticePointYUnderground = (int) Math.floor( (float)
				// (GamePanel.gp.getPlayerEntity().getY()+localHeightUnderground/2)
				// / (localHeightUnderground)) * localHeightUnderground;

				int localWidth = GameStates.mapWidth
						* GameStates.mapTileSetWidth;
				int localHeight = GameStates.mapHeight
						* GameStates.mapTileSetHeight;
				int latticePointX = (int) Math.floor((float) (GamePanel.gp.getPlayerEntity().getX() + localWidth / 2)/ (localWidth)) * localWidth;
				int latticePointY = (int) Math.floor((float) (GamePanel.gp.getPlayerEntity().getY() + localHeight / 2)/ (localHeight)) * localHeight;

				List<UndergroundMapManager> tempUndergroundMapManagerList = new ArrayList<UndergroundMapManager>(
						GameWindow.gw.getUndergroundMapManagers().values());
				try {
					for (UndergroundMapManager undergroundMapManager : tempUndergroundMapManagerList) {
						for (int runX = -GameStates.factorOfViewDeleteDistance; runX <= GameStates.factorOfViewDeleteDistance - 1; runX++) {
							for (int runY = -GameStates.factorOfViewDeleteDistance; runY <= GameStates.factorOfViewDeleteDistance - 1; runY++) {
								Point point = new Point(latticePointX + runX* localWidth, latticePointY + runY* localHeight);
								Point moduloPoint = new Point(	((point.x / GameStates.mapTotalWidth)% GamePanel.gp.getMiniMap().getWidth() + GamePanel.gp.getMiniMap().getWidth())	% GamePanel.gp.getMiniMap().getWidth()* GameStates.mapTotalWidth,
										((point.y / GameStates.mapTotalHeight)	% GamePanel.gp.getMiniMap().getHeight() + GamePanel.gp.getMiniMap().getHeight())% GamePanel.gp.getMiniMap().getHeight()* GameStates.mapTotalHeight);
								mapLoad(undergroundMapManager, moduloPoint.x,moduloPoint.y);
								if (mapLoadCount > limitLoadMaps) {
									break;
								}
							}
							if (mapLoadCount > limitLoadMaps) {
								break;
							}
						}
						if (mapLoadCount > limitLoadMaps) {
							break;
						}
					}
				} catch (ConcurrentModificationException ex) {
					ex.printStackTrace();
				}

				/**
				 * ObjectMapManagers
				 */
				List<ObjectMapManager> tempObjectMapManagerList = new ArrayList<ObjectMapManager>(
						GameWindow.gw.getObjectMapManagers().values());
				try {
					for (ObjectMapManager mapManager : tempObjectMapManagerList) {
						for (int runX = -GameStates.factorOfViewDeleteDistance; runX <= GameStates.factorOfViewDeleteDistance - 1; runX++) {
							for (int runY = -GameStates.factorOfViewDeleteDistance; runY <= GameStates.factorOfViewDeleteDistance - 1; runY++) {
								ObjectMapManager mapManagerOverlay;
								if (mapManager.getPaintLayer().equals("tree1"))
									mapManagerOverlay = GameWindow.gw.getObjectMapManagers().get("overlayTree1");
								else
									mapManagerOverlay = GameWindow.gw.getObjectMapManagers().get("overlayTree2");

								Point point = new Point(latticePointX + runX* localWidth, latticePointY + runY* localHeight);
								Point moduloPoint = new Point(	((point.x / GameStates.mapTotalWidth)% GamePanel.gp.getMiniMap().getWidth() + GamePanel.gp.getMiniMap().getWidth())	% GamePanel.gp.getMiniMap().getWidth()* GameStates.mapTotalWidth,
										((point.y / GameStates.mapTotalHeight)% GamePanel.gp.getMiniMap().getHeight() + GamePanel.gp.getMiniMap().getHeight())% GamePanel.gp.getMiniMap().getHeight()* GameStates.mapTotalHeight);
								// mapLoad(mapManager, mapManagerOverlay,
								// latticePointX + runX*localWidth,
								// latticePointY + runY*localHeight);
								mapLoad(mapManager, mapManagerOverlay,moduloPoint.x, moduloPoint.y);

								if (mapLoadCount > limitLoadMaps) {
									break;
								}
							}
							if (mapLoadCount > limitLoadMaps) {
								break;
							}
						}
						if (mapLoadCount > limitLoadMaps) {
							break;
						}
					}
				} catch (ConcurrentModificationException ex) {
					ex.printStackTrace();
				}

				/**
				 * MapManagers
				 */
				List<MapManager> tempMapManagerList = new ArrayList<MapManager>(
						GameWindow.gw.getMapManagers().values());
				try {
					for (MapManager mapManager : tempMapManagerList) {
						for (int runX = -GameStates.factorOfViewDeleteDistance; runX <= GameStates.factorOfViewDeleteDistance - 1; runX++) {
							for (int runY = -GameStates.factorOfViewDeleteDistance; runY <= GameStates.factorOfViewDeleteDistance - 1; runY++) {
								Point point = new Point(latticePointX + runX* localWidth, latticePointY + runY* localHeight);
								Point moduloPoint = new Point(((point.x / GameStates.mapTotalWidth)	% GamePanel.gp.getMiniMap().getWidth() + GamePanel.gp.getMiniMap().getWidth())% GamePanel.gp.getMiniMap().getWidth()* GameStates.mapTotalWidth,
										((point.y / GameStates.mapTotalHeight)% GamePanel.gp.getMiniMap().getHeight() + GamePanel.gp.getMiniMap().getHeight())% GamePanel.gp.getMiniMap().getHeight()* GameStates.mapTotalHeight);
								// System.out.println("runX = "+runX +
								// " runY = "+runY);
								// System.out.println("pointX = "+point.x +
								// " pointY = "+point.y);
								// System.out.println("modX = "+moduloPoint.x +
								// " modY = "+moduloPoint.y);
								mapLoad(mapManager, moduloPoint.x,	moduloPoint.y);

								if (mapLoadCount > limitLoadMaps) {
									break;
								}
							}
							if (mapLoadCount > limitLoadMaps) {
								break;
							}
						}
						if (mapLoadCount > limitLoadMaps) {
							break;
						}
					}
				} catch (ConcurrentModificationException ex) {
					ex.printStackTrace();
				}

				List<ObjectMapManager> tempObjectMapManagerListLowerRoom = new ArrayList<ObjectMapManager>(
						GameWindow.gw.getObjectMapManagersLowerRoom().values());
				try {
					for (ObjectMapManager mapManager : tempObjectMapManagerListLowerRoom) {
						for (int runX = -GameStates.factorOfViewDeleteDistance; runX <= GameStates.factorOfViewDeleteDistance - 1; runX++) {
							for (int runY = -GameStates.factorOfViewDeleteDistance; runY <= GameStates.factorOfViewDeleteDistance - 1; runY++) {
								ObjectMapManager mapManagerOverlay;
								if (mapManager.getPaintLayer().equals("tree1"))
									mapManagerOverlay = GameWindow.gw.getObjectMapManagersLowerRoom().get("overlayTree1");
								else
									mapManagerOverlay = GameWindow.gw.getObjectMapManagersLowerRoom().get("overlayTree2");

								Point point = new Point(latticePointX + runX* localWidth, latticePointY + runY* localHeight);
								Point moduloPoint = new Point(	((point.x / GameStates.mapTotalWidth)% GamePanel.gp.getMiniMap().getWidth() + GamePanel.gp.getMiniMap().getWidth())	% GamePanel.gp.getMiniMap().getWidth()* GameStates.mapTotalWidth,
										((point.y / GameStates.mapTotalHeight)% GamePanel.gp.getMiniMap().getHeight() + GamePanel.gp.getMiniMap().getHeight())	% GamePanel.gp.getMiniMap().getHeight()	* GameStates.mapTotalHeight);

								mapLoadLowerRoom(mapManager, mapManagerOverlay,	moduloPoint.x, moduloPoint.y);
								if (mapLoadCount > limitLoadMaps) {
									break;
								}
							}
							if (mapLoadCount > limitLoadMaps) {
								break;
							}
						}
						if (mapLoadCount > limitLoadMaps) {
							break;
						}
					}
				} catch (ConcurrentModificationException ex) {
					ex.printStackTrace();
				}

			}
			GamePanel.gp.setPaintingBlocked(false);
			try {
				Thread.sleep(sleepingTime);

			} catch (InterruptedException exception) {
				System.out.println(exception);
			}

		}
	}

	/**
	 * MapManager mapSaveUpdateRun
	 * 
	 * @param mapManager
	 */
	public void mapSaveUpdateRun(MapManager mapManager) {
		if (GameWindow.gw.isLoggedIn() && GamePanel.gp.isInitializedPlayer()
				&& (GameWindow.gw.isMapMaster())) {
			GamePanel.gp.setPaintingBlocked(true);
			List<Point> idListTempMaps = new ArrayList<Point>(
					mapManager.getChangedList());
			for (Point p : idListTempMaps) {
				LocalMap localMap = mapManager.get(p);
				if (localMap != null) {
					String mapFileName = mapManager.getPaintLayer() + "_" + p.x
							+ "_" + p.y + ".map";
					mapManager.save(localMap, GameStates.standardMapFolder
							+ GameWindow.gw.getPlayerName() + "/"
							+ GamePanel.gp.getPlayerEntity().getRoom() + "/"
							+ mapFileName);

					/** do not instantly send maps but create the sendQueue */
					GameWindow.gw.addSendMapListEntry(GamePanel.gp
							.getPlayerEntity().getRoom() + "/" + mapFileName);
					mapManager.removeStayList(p);

					if (mapManager.getPaintLayer().equals("hole")) {
						String lowerRoom = GameWindow.gw
								.getLowerRoom(GamePanel.gp.getPlayerEntity()
										.getRoom());
						boolean createMapFolderSuccess = (new File(
								GameStates.standardMapFolder + "/"
										+ GameWindow.gw.getMapMasterName()
										+ "/" + lowerRoom)).mkdirs();
						if (!createMapFolderSuccess) {
							// Directory creation failed
							System.out
									.println("Error at creating lower map-folder!");
						}
						String mapFileNameLightTile = "cobble" + "_" + p.x
								+ "_" + p.y + ".map";
						LocalMap localMapLightTile = new LocalMap(localMap,
								GameStates.holeTile, GameStates.lightTile);
						MapManager lowerMapManager = new MapManager("cobble",
								GamePanel.gp.getMiniMap().getWidth(),
								GamePanel.gp.getMiniMap().getHeight());
						lowerMapManager.save(
								localMapLightTile,
								GameStates.standardMapFolder
										+ GameWindow.gw.getPlayerName()
										+ "/"
										+ GameWindow.gw
												.getLowerRoom(GamePanel.gp
														.getPlayerEntity()
														.getRoom()) + "/"
										+ mapFileNameLightTile);
					}
				} else {
					System.out
							.println("Mapmanager localMap == null -> Couldn't write Map");
					// mapManager.removeStayList(p);
				}
			}
			mapManager.emptyChangedList();
		}
	}

	/**
	 * ObjectMapManager mapSaveUpdateRun
	 * 
	 * @param mapManager
	 */
	public void mapSaveUpdateRun(ObjectMapManager mapManager) {
		if (GameWindow.gw.isLoggedIn() && GamePanel.gp.isInitializedPlayer()
				&& (GameWindow.gw.isMapMaster())) {
			GamePanel.gp.setPaintingBlocked(true);
			List<Point> idListTempMaps = new ArrayList<Point>(
					mapManager.getChangedList());
			// System.out.println("ObjectMaps Size = "+idListTempMaps.size());
			for (Point p : idListTempMaps) {
				LocalMap localMap = mapManager.get(p);
				if (localMap != null) {
					String mapFileName = mapManager.getPaintLayer() + "_" + p.x
							+ "_" + p.y + ".map";
					mapManager.save(localMap, GameStates.standardMapFolder
							+ GameWindow.gw.getPlayerName() + "/"
							+ GamePanel.gp.getPlayerEntity().getRoom() + "/"
							+ mapFileName);
					GameWindow.gw.addSendMapListEntry(GamePanel.gp
							.getPlayerEntity().getRoom() + "/" + mapFileName);
					mapManager.removeStayList(p);
				} else {
					System.out
							.println("ObjectMapmanager localMap == null -> Couldn't write Map: "
									+ mapManager.paintLayer);
					// mapManager.removeStayList(p);
				}
			}
			mapManager.emptyChangedList();
		}
	}

	/**
	 * ObjectMapManager - LowerRoom mapSaveUpdateRun
	 * 
	 * @param mapManager
	 */
	public void mapSaveUpdateRunLowerRoom(ObjectMapManager mapManager) {
		if (GameWindow.gw.isLoggedIn() && GamePanel.gp.isInitializedPlayer()
				&& (GameWindow.gw.isMapMaster())) {
			GamePanel.gp.setPaintingBlocked(true);
			List<Point> idListTempMaps = new ArrayList<Point>(
					mapManager.getChangedList());
			// System.out.println("ObjectMaps Size = "+idListTempMaps.size());
			for (Point p : idListTempMaps) {
				// System.out.println("saving in mapupdateThread lowerRoom");
				LocalMap localMap = mapManager.get(p);
				if (localMap != null) {
					String mapFileName = mapManager.getPaintLayer() + "_" + p.x
							+ "_" + p.y + ".map";
					mapManager.save(
							localMap,
							GameStates.standardMapFolder
									+ GameWindow.gw.getPlayerName()
									+ "/"
									+ GameWindow.gw.getLowerRoom(GamePanel.gp
											.getPlayerEntity().getRoom()) + "/"
									+ mapFileName);

					// TODO: adjust the sendMapList: the room must be specified
					GameWindow.gw.addSendMapListEntry(GameWindow.gw
							.getLowerRoom(GamePanel.gp.getPlayerEntity()
									.getRoom())
							+ "/" + mapFileName);
					mapManager.removeStayList(p);

				} else {
					System.out
							.println("Lower Room ObjectMapmanager localMap == null -> Couldn't write Map: "
									+ mapManager.paintLayer);
					// mapManager.removeStayList(p);
				}
			}
			mapManager.emptyChangedList();
		}
	}

	/**
	 * UndergroundManager mapSaveUpdateRun
	 * 
	 * @param mapManager
	 */
	public void mapSaveUpdateRun(UndergroundMapManager mapManager) {
		if (GameWindow.gw.isLoggedIn() && GamePanel.gp.isInitializedPlayer()
				&& (GameWindow.gw.isMapMaster())) {
			GamePanel.gp.setPaintingBlocked(true);
			List<Point> idListTempMaps = new ArrayList<Point>(
					mapManager.getChangedList());
			for (Point p : idListTempMaps) {
				LocalUndergroundMap localMap = mapManager.get(p);
				if (localMap != null) {
					String mapFileName = mapManager.getPaintLayer() + "_" + p.x
							+ "_" + p.y + ".map";
					mapManager.save(localMap, GameStates.standardMapFolder
							+ GameWindow.gw.getPlayerName() + "/"
							+ GamePanel.gp.getPlayerEntity().getRoom() + "/"
							+ mapFileName);
					GameWindow.gw.addSendMapListEntry(GamePanel.gp
							.getPlayerEntity().getRoom() + "/" + mapFileName);
					mapManager.removeStayList(p);
				} else {
					System.out
							.println("Underground MapmanagerlocalMap == null -> Couldn't write Map");
					// mapManager.removeStayList(p);
				}
			}
			mapManager.emptyChangedList();
		}
	}

	public void mapLoad(MapManager mapManager, int x, int y) {
		Point p = new Point(x, y);
		if (!mapManager.contains(p)) {
			GamePanel.gp.setPaintingBlocked(true);

			/** search in folder */
			if (mapManager.loadFromFileSystem(GameWindow.gw.getMapMasterName(),
					x, y) || GameWindow.gw.isInitingLocalMapFileList()) {
				// OK
				// System.out.println("got from file: "+x+"|"+y);
			} else {
				if (GameWindow.gw.isMapMaster()) {
					// we have to create it!
					// Point p = new Point(x,y);
					// System.out.println("before: Mapmanager GRASS created "+p.x+"|"+p.y);
					switch (GameWindow.gw.getWorldType(GamePanel.gp.getPlayerEntity().getRoom())) {
					case BASE: {
						/**
						 * and create the overlayManager depending on the actual
						 * objectMapManager
						 */
						if (mapManager.getPaintLayer().equals("grass")) {
							mapManager.createMap(p);
							// System.out.println("Mapmanager GRASS created "+p.x+"|"+p.y);
							Color color = GamePanel.gp.getMiniMap().getMap()[p.y
									/ GameStates.mapTotalHeight][p.x
									/ GameStates.mapTotalWidth];
							MiniMapColor miniMapColor = MyUtil
									.getClosestColor(color);
							int grassCount = 0;
							if (miniMapColor.equals(MiniMapColor.GREEN))
								grassCount = (int) ((color
										.getColorComponents(null)[1] * 100) / GameStates.scaleGenerationCountObjects);// (Math.random()*40+100/GameStates.standardZoomFactor);

							int[] grassX = MyUtil.createIntArray(grassCount, 2,
									GameStates.mapWidth - 1);
							int[] grassY = MyUtil.createIntArray(grassCount, 2,
									GameStates.mapHeight - 1);

							// GamePanel.gp.setSmallPaint(true);
							mapManager.paintTilesOnMap(p, grassX, grassY,
									GameStates.grassTile);

							mapManager.get(p).setInitialized(true);
							mapManager.addStayList(p);
							mapManager.addChangedList(p);
							mapLoadCount++;
						} else {

						}
						break;
					}
					case UNDERGROUND_A: {
						break;

					}
					case UNDERGROUND_B: {
						break;
					}
					case UNDERGROUND_C: {
						break;
					}
					default:
						break;
					}

				}

			}

		} else {

			if (mapManager.get(p) != null && mapManager.get(p).isInitialized()) {
				// OK

				// System.out.println("Existing NOT NULL and Initialized: Mapmanager GRASS "+p.x+"|"+p.y);
			} else {
				mapManager.remove(p);
				// System.out.println("Existing: NULL Mapmanager GRASS "+p.x+"|"+p.y);
			}
		}
	}

	public void mapLoad(ObjectMapManager objectMapManager,
			ObjectMapManager objectMapManagerOverlay, int x, int y) {
		Point p = new Point(x, y);
		if (!objectMapManager.contains(p)) {
			GamePanel.gp.setPaintingBlocked(true);
			// mapLoadCount++;
			/** search in folder */
			if ((objectMapManager.loadFromFileSystem(
					GameWindow.gw.getMapMasterName(), x, y))
					|| GameWindow.gw.isInitingLocalMapFileList()) {
				// OK, got the map from file system
//				System.out.println("Got map from filesystem: " + x + "|" + y);
//				System.out.println("Initing: "
//						+ GameWindow.gw.isInitingLocalMapFileList());
			} else {
				if (GameWindow.gw.isMapMaster()) {
					// we have to create it!

					switch (GameWindow.gw.getWorldType(GamePanel.gp.getPlayerEntity().getRoom())) {
					case BASE: {
						paintOverWorldMap(objectMapManager, objectMapManagerOverlay, p);

						break;
					}
					case UNDERGROUND_A: {
					
//						 paintRandomOnUndergroundA(objectMapManager,objectMapManagerOverlay,
						// x, y);
						loadUnderground(objectMapManager, objectMapManagerOverlay, p, GameStates.fluidSoilTile);
						
//						MapType mapType = GamePanel.gp.getDungeonMiniMap().getMapType( (int) (p.y/GameStates.mapTotalHeight), (int) (p.x/GameStates.mapTotalWidth));
//						int mapTypeInt = mapType.ordinal()+1;
//						int randInt = 2+ (int) (Math.random()*6);
////						if (mapTypeInt >13) randInt = 1;
////						System.out.println("got "+mapTypeInt+" version"+randInt+" from "+(int) (p.x/GameStates.mapTotalWidth)+"|"+(int) (p.y/GameStates.mapTotalHeight));
//						objectMapManager.loadPresetFromFileSystem(GameWindow.gw.getMapMasterName(), mapTypeInt, randInt,p, GameStates.fluidSoilTile);
//						objectMapManagerOverlay.loadPresetFromFileSystem(GameWindow.gw.getMapMasterName(), mapTypeInt, randInt,p, GameStates.fluidSoilTile);
////						paintFluidRandomOnUndergroundA(objectMapManager, objectMapManagerOverlay, x, y);
//						// paintFluidFrameOnUndergroundA(objectMapManager,objectMapManagerOverlay,
//						// x, y);
//						if (objectMapManager.get(p) != null & objectMapManagerOverlay.get(p) !=null) {
//							objectMapManager.get(p).setInitialized(true);
//							objectMapManagerOverlay.get(p).setInitialized(true);
//							objectMapManager.addStayList(p);
//							objectMapManagerOverlay.addStayList(p);
//	
//							objectMapManager.addChangedList(p);
//							objectMapManagerOverlay.addChangedList(p);
//							mapLoadCount++;
//						}	
						
						break;

					}
					case UNDERGROUND_B: {
						loadUnderground(objectMapManager, objectMapManagerOverlay, p, GameStates.fluidStoneTile);
//						paintFluidFrameOnUndergroundA(objectMapManager,	objectMapManagerOverlay, x, y);
						break;
					}
					case UNDERGROUND_C: {
						paintFluidFrameOnUndergroundA(objectMapManager,
								objectMapManagerOverlay, x, y);
						break;
					}
					default:
						paintFluidFrameOnUndergroundA(objectMapManager,
								objectMapManagerOverlay, x, y);
						break;
					}
				}
			}
		} else {
			/**
			 * check if any of the maps are not yet initialized or broken
			 */
			if (objectMapManager.get(p) != null
					&& objectMapManager.get(p).isInitialized()) {// &&
																	// objectMapManagerOverlay.get(p)
																	// != null
																	// &&
																	// objectMapManagerOverlay.get(p).isInitialized())
																	// {
				// OK

				// System.out.println("Existing NOT NULL and Initialized: ObjectMapmanager "+p.x+"|"+p.y);
			} else {
				objectMapManager.remove(p);
				// objectMapManagerOverlay.remove(p);
				System.out.println("Existing: NULL ObjectMapmanager " + p.x
						+ "|" + p.y);
			}

		}
	}

	public void mapLoadLowerRoom(ObjectMapManager objectMapManager,
			ObjectMapManager objectMapManagerOverlay, int x, int y) {
		if (!objectMapManager.contains(new Point(x, y))) {
			// System.out.println("mapLoadLowerRoom");
			GamePanel.gp.setPaintingBlocked(true);
			// mapLoadCount++;
			/** search in folder */
			if (objectMapManager.loadFromFileSystem(GameWindow.gw.getMapMasterName(), x, y)	|| GameWindow.gw.isInitingLocalMapFileList()) {
				// OK, got the map from file system

			} else {
				if (GameWindow.gw.isMapMaster()) {
					// we have to create it!
					Point p = new Point(x, y);
					switch (GameWindow.gw.getWorldType(GameWindow.gw.getLowerRoom(GamePanel.gp.getPlayerEntity().getRoom()))) {
					case BASE: {
						paintOverWorldMap(objectMapManager,objectMapManagerOverlay, p);
						break;
					}
					case UNDERGROUND_A: {
						/** start with the lowerRoom */
						// paintRandomOnUndergroundA(objectMapManager,objectMapManagerOverlay,
						// x, y);
						loadUnderground(objectMapManager, objectMapManagerOverlay, p, GameStates.fluidSoilTile);
						
						
						
//						MapType mapType = GamePanel.gp.getDungeonMiniMap().getMapType( (int) (p.y/GameStates.mapTotalHeight), (int) (p.x/GameStates.mapTotalWidth));
//						int mapTypeInt = mapType.ordinal()+1;
//						int randInt = 2+ (int) (Math.random()*6);
////						if (mapTypeInt >13) randInt = 1;
////						System.out.println("got "+mapTypeInt+" version"+randInt+" from "+(int) (p.x/GameStates.mapTotalWidth)+"|"+(int) (p.y/GameStates.mapTotalHeight));
//						objectMapManager.loadPresetFromFileSystem(GameWindow.gw.getMapMasterName(), mapTypeInt, randInt,p, GameStates.fluidSoilTile);
//						objectMapManagerOverlay.loadPresetFromFileSystem(GameWindow.gw.getMapMasterName(), mapTypeInt, randInt,p, GameStates.fluidSoilTile);
////						paintFluidRandomOnUndergroundA(objectMapManager,objectMapManagerOverlay, x, y);
//						// paintFluidFrameOnUndergroundA(objectMapManager,objectMapManagerOverlay,
//						// x, y);
//						if (objectMapManager.get(p) != null & objectMapManagerOverlay.get(p) !=null) {
//							objectMapManager.get(p).setInitialized(true);
//							objectMapManagerOverlay.get(p).setInitialized(true);
//							objectMapManager.addStayList(p);
//							objectMapManagerOverlay.addStayList(p);
//	
//							objectMapManager.addChangedList(p);
//							objectMapManagerOverlay.addChangedList(p);
//							mapLoadCount++;
//						}						
						break;

					}
					case UNDERGROUND_B: {
						loadUnderground(objectMapManager, objectMapManagerOverlay, p, GameStates.fluidStoneTile);
//						paintFluidFrameOnUndergroundA(objectMapManager,	objectMapManagerOverlay, x, y);
						break;
					}
					case UNDERGROUND_C: {
						paintFluidFrameOnUndergroundA(objectMapManager,
								objectMapManagerOverlay, x, y);
						break;
					}
					default:
						paintFluidFrameOnUndergroundA(objectMapManager,
								objectMapManagerOverlay, x, y);
						break;
					}
				}
			}
		}
	}
	
	public void loadUnderground(ObjectMapManager objectMapManager, ObjectMapManager objectMapManagerOverlay, Point p, int paintType) {
		MapType mapType = GamePanel.gp.getDungeonMiniMap().getMapType( (int) (p.y/GameStates.mapTotalHeight), (int) (p.x/GameStates.mapTotalWidth));
		int mapTypeInt = mapType.ordinal()+1;
		int randInt = 2+ (int) (Math.random()*6);
//		if (mapTypeInt >13) randInt = 1;
//		System.out.println("got "+mapTypeInt+" version"+randInt+" from "+(int) (p.x/GameStates.mapTotalWidth)+"|"+(int) (p.y/GameStates.mapTotalHeight));
		objectMapManager.loadPresetFromFileSystem(GameWindow.gw.getMapMasterName(), mapTypeInt, randInt,p, paintType);
		objectMapManagerOverlay.loadPresetFromFileSystem(GameWindow.gw.getMapMasterName(), mapTypeInt, randInt,p, paintType);
//		paintFluidRandomOnUndergroundA(objectMapManager,objectMapManagerOverlay, x, y);
		// paintFluidFrameOnUndergroundA(objectMapManager,objectMapManagerOverlay,
		// x, y);

		if (objectMapManager.get(p) != null & objectMapManagerOverlay.get(p) !=null) {
			objectMapManager.get(p).setInitialized(true);
			objectMapManagerOverlay.get(p).setInitialized(true);
			objectMapManager.addStayList(p);
			objectMapManagerOverlay.addStayList(p);

			objectMapManager.addChangedList(p);
			objectMapManagerOverlay.addChangedList(p);
			mapLoadCount++;
		}	

//		objectMapManager.get(p).setInitialized(true);
//		objectMapManagerOverlay.get(p).setInitialized(true);
//		objectMapManager.addStayList(p);
//		objectMapManagerOverlay.addStayList(p);
//
//		objectMapManager.addChangedList(p);
//		objectMapManagerOverlay.addChangedList(p);
//		mapLoadCount++;
	}

	public void paintOverWorldMap(ObjectMapManager objectMapManager,ObjectMapManager objectMapManagerOverlay, Point p) {
		/**
		 * and create the overlayManager depending on the actual
		 * objectMapManager
		 */
		objectMapManager.createMap(p);
		objectMapManagerOverlay.createMap(p);

		int bigBushCount = (int) (Math.random() * 1 + 10 / GameStates.scaleGenerationCountObjects);
		int treeCount = (int) (Math.random() * 1 + 30 / GameStates.scaleGenerationCountObjects);
		int stoneCount = (int) (Math.random() * 6 / GameStates.scaleGenerationCountObjects);
		int smallTreeCount = (int) (Math.random() * 40 / GameStates.scaleGenerationCountObjects);
		int smallStoneCount = (int) (Math.random() * 10 / GameStates.scaleGenerationCountObjects);
		int smallBushesCount = (int) (Math.random() * 30 / GameStates.scaleGenerationCountObjects);

		int collectStoneTile1Count = (int) (Math.random() * 2
				/ GameStates.scaleGenerationCountObjects + 10 / GameStates.scaleGenerationCountObjects);
		int collectStoneTile2Count = (int) (Math.random() * 2
				/ GameStates.scaleGenerationCountObjects + 10 / GameStates.scaleGenerationCountObjects);
		int collectStoneTile3Count = (int) (Math.random() * 2
				/ GameStates.scaleGenerationCountObjects + 10 / GameStates.scaleGenerationCountObjects);

		int[] bigBushX = MyUtil.createIntArray(bigBushCount, 2,
				GameStates.mapWidth - 1);
		int[] bigBushY = MyUtil.createIntArray(bigBushCount, 2,
				GameStates.mapHeight - 1);
		int[] treeX = MyUtil.createIntArray(treeCount, 2,
				GameStates.mapWidth - 1);
		int[] treeY = MyUtil.createIntArray(treeCount, 2,
				GameStates.mapHeight - 1);
		int[] stoneX = MyUtil.createIntArray(stoneCount, 2,
				GameStates.mapWidth - 1);
		int[] stoneY = MyUtil.createIntArray(stoneCount, 2,
				GameStates.mapHeight - 1);
		int[] smallTreeX = MyUtil.createIntArray(smallTreeCount, 2,
				GameStates.mapWidth - 1);
		int[] smallTreeY = MyUtil.createIntArray(smallTreeCount, 2,
				GameStates.mapHeight - 1);
		int[] smallStoneX = MyUtil.createIntArray(smallStoneCount, 2,
				GameStates.mapWidth - 1);
		int[] smallStoneY = MyUtil.createIntArray(smallStoneCount, 2,
				GameStates.mapHeight - 1);
		int[] smallBushX = MyUtil.createIntArray(smallBushesCount, 2,
				GameStates.mapWidth - 1);
		int[] smallBushY = MyUtil.createIntArray(smallBushesCount, 2,
				GameStates.mapHeight - 1);

		int[] stone1X = MyUtil.createIntArray(collectStoneTile1Count, 2,
				GameStates.mapWidth - 1);
		int[] stone1Y = MyUtil.createIntArray(collectStoneTile1Count, 2,
				GameStates.mapHeight - 1);
		int[] stone2X = MyUtil.createIntArray(collectStoneTile2Count, 2,
				GameStates.mapWidth - 1);
		int[] stone2Y = MyUtil.createIntArray(collectStoneTile2Count, 2,
				GameStates.mapHeight - 1);
		int[] stone3X = MyUtil.createIntArray(collectStoneTile3Count, 2,
				GameStates.mapWidth - 1);
		int[] stone3Y = MyUtil.createIntArray(collectStoneTile3Count, 2,
				GameStates.mapHeight - 1);

		objectMapManager.paintTilesOnMap(p, bigBushX, bigBushY,
				GameStates.bigBushTile);
		objectMapManagerOverlay.paintTilesOnMap(p, bigBushX, bigBushY,
				GameStates.bigBushTile - GameStates.mapTileSetWidth * 2);
		objectMapManager.paintTilesOnMap(p, treeX, treeY, GameStates.treeTile);
		objectMapManagerOverlay.paintTilesOnMap(p, treeX, treeY,
				GameStates.treeTile - GameStates.mapTileSetWidth * 2);
		objectMapManager.paintTilesOnMap(p, stoneX, stoneY,
				GameStates.stoneTile);
		objectMapManagerOverlay.paintTilesOnMap(p, stoneX, stoneY,
				GameStates.stoneTile - GameStates.mapTileSetWidth * 2);
		objectMapManager.paintTilesOnMap(p, smallTreeX, smallTreeY,
				GameStates.smallTreeTile);
		objectMapManagerOverlay.paintTilesOnMap(p, smallTreeX, smallTreeY,
				GameStates.smallTreeTile - GameStates.mapTileSetWidth * 2);
		objectMapManager.paintTilesOnMap(p, smallStoneX, smallStoneY,
				GameStates.smallStoneTile);
		objectMapManagerOverlay.paintTilesOnMap(p, smallStoneX, smallStoneY,
				GameStates.smallStoneTile - GameStates.mapTileSetWidth * 2);
		objectMapManager.paintTilesOnMap(p, smallBushX, smallBushY,
				GameStates.smallBushTile);

		objectMapManager.paintTilesOnMap(p, stone1X, stone1Y,
				GameStates.collectStoneTile1);
		objectMapManager.paintTilesOnMap(p, stone2X, stone2Y,
				GameStates.collectStoneTile2);
		objectMapManager.paintTilesOnMap(p, stone3X, stone3Y,
				GameStates.collectStoneTile3);

		objectMapManager.get(p).setInitialized(true);
		objectMapManagerOverlay.get(p).setInitialized(true);
		objectMapManager.addStayList(p);
		objectMapManagerOverlay.addStayList(p);

		objectMapManager.addChangedList(p);
		objectMapManagerOverlay.addChangedList(p);
		mapLoadCount++;
	}

	public void mapLoad(UndergroundMapManager undergroundMapManager, int x,	int y) {
		Point p = new Point(x, y);
		if (!undergroundMapManager.contains(p)) {
			GamePanel.gp.setPaintingBlocked(true);
			/** search in folder */
			if (undergroundMapManager.loadFromFileSystem(GameWindow.gw.getMapMasterName(), x, y)|| GameWindow.gw.isInitingLocalMapFileList()) {
				// OK
			} else {
				if (GameWindow.gw.isMapMaster()) {
					// we have to get a new map

					int undergroundPaintType = GameWindow.gw.getUndergroundPaintTypeOfWorldType(GameWindow.gw.getWorldType(GamePanel.gp.getPlayerEntity().getRoom()));
					undergroundMapManager.createFullMap(p, undergroundPaintType);
					undergroundMapManager.get(p).setInitialized(true);
					undergroundMapManager.addStayList(p);
					undergroundMapManager.addChangedList(p);
					mapLoadCount++;
				}
			}

		} else {
			if (undergroundMapManager.get(p) != null
					&& undergroundMapManager.get(p).isInitialized()) {
				// OK

				// System.out.println("Existing NOT NULL and Initialized: Mapmanager GRASS "+p.x+"|"+p.y);
			} else {
				undergroundMapManager.remove(p);
				// System.out.println("Existing: NULL Mapmanager GRASS "+p.x+"|"+p.y);
			}
		}
	}

	// public void paintFrameOnUndergroundA(ObjectMapManager objectMapManager,
	// int x, int y) {
	// Point p = new Point(x,y);
	// if (objectMapManager.getPaintLayer().equals("tree1")) {
	// objectMapManager.getPaintFullTilesOnMap2x3Frame(p, "tree1",
	// GameStates.soilBlockTile);
	// GameWindow.gw.getObjectMapManagersLowerRoom().get("overlayTree1").getPaintFullTilesOnMap2x3Frame(p,
	// "overlayTree1", GameStates.soilBlockTile-2*GameStates.tileSetWidth);
	//
	// objectMapManager.addStayList(p);
	// GameWindow.gw.getObjectMapManagersLowerRoom().get("overlayTree1").addStayList(p);
	//
	// objectMapManager.addChangedList(p);
	// GameWindow.gw.getObjectMapManagersLowerRoom().get("overlayTree1").addChangedList(p);
	// mapLoadCount++;
	// }
	// if (objectMapManager.getPaintLayer().equals("tree2")) {
	// objectMapManager.getPaintFullTilesOnMap2x3Frame(p, "tree2",
	// GameStates.soilBlockTile);
	// GameWindow.gw.getObjectMapManagersLowerRoom().get("overlayTree2").getPaintFullTilesOnMap2x3Frame(p,
	// "overlayTree2", GameStates.soilBlockTile-2*GameStates.tileSetWidth);
	//
	// objectMapManager.addStayList(p);
	// GameWindow.gw.getObjectMapManagersLowerRoom().get("overlayTree2").addStayList(p);
	//
	// objectMapManager.addChangedList(p);
	// GameWindow.gw.getObjectMapManagersLowerRoom().get("overlayTree2").addChangedList(p);
	// mapLoadCount++;
	// }
	// }

	public void paintRandomOnUndergroundA(ObjectMapManager objectMapManager,
			ObjectMapManager objectMapManagerOverlay, int x, int y) {
		Point p = new Point(x, y);
		if (objectMapManager.getPaintLayer().equals("tree1")) {
			objectMapManager.createMap(p);
			objectMapManagerOverlay.createMap(p);

			int soilBlockTileCount = (int) (Math.random() * 10
					/ GameStates.scaleGenerationCountObjects + 60 / GameStates.scaleGenerationCountObjects);
			int collectStoneTile1Count = (int) (Math.random() * 5
					/ GameStates.scaleGenerationCountObjects + 4 / GameStates.scaleGenerationCountObjects);
			int collectStoneTile2Count = (int) (Math.random() * 5
					/ GameStates.scaleGenerationCountObjects + 4 / GameStates.scaleGenerationCountObjects);
			int collectStoneTile3Count = (int) (Math.random() * 5
					/ GameStates.scaleGenerationCountObjects + 4 / GameStates.scaleGenerationCountObjects);
			int[] soilBlockX = MyUtil.createIntArray(soilBlockTileCount, 2,
					GameStates.mapWidth - 1);
			int[] soilBlockY = MyUtil.createIntArray(soilBlockTileCount, 2,
					GameStates.mapHeight - 1);
			int[] stone1X = MyUtil.createIntArray(collectStoneTile1Count, 2,
					GameStates.mapWidth - 1);
			int[] stone1Y = MyUtil.createIntArray(collectStoneTile1Count, 2,
					GameStates.mapHeight - 1);
			int[] stone2X = MyUtil.createIntArray(collectStoneTile2Count, 2,
					GameStates.mapWidth - 1);
			int[] stone2Y = MyUtil.createIntArray(collectStoneTile2Count, 2,
					GameStates.mapHeight - 1);
			int[] stone3X = MyUtil.createIntArray(collectStoneTile3Count, 2,
					GameStates.mapWidth - 1);
			int[] stone3Y = MyUtil.createIntArray(collectStoneTile3Count, 2,
					GameStates.mapHeight - 1);

			objectMapManager.paintTilesOnMap(p, soilBlockX, soilBlockY,
					GameStates.soilBlockTile);
			objectMapManagerOverlay.paintTilesOnMap(p, soilBlockX, soilBlockY,
					GameStates.soilBlockTile - GameStates.mapTileSetWidth * 2);
			objectMapManager.paintTilesOnMap(p, stone1X, stone1Y,
					GameStates.collectStoneTile1);
			objectMapManager.paintTilesOnMap(p, stone2X, stone2Y,
					GameStates.collectStoneTile2);
			objectMapManager.paintTilesOnMap(p, stone3X, stone3Y,
					GameStates.collectStoneTile3);

			objectMapManager.get(p).setInitialized(true);
			objectMapManagerOverlay.get(p).setInitialized(true);
			objectMapManager.addStayList(p);
			objectMapManagerOverlay.addStayList(p);

			objectMapManager.addChangedList(p);
			objectMapManagerOverlay.addChangedList(p);
			mapLoadCount++;
		}
		if (objectMapManager.getPaintLayer().equals("tree2")) {
			objectMapManager.createMap(p);
			objectMapManagerOverlay.createMap(p);

			int soilBlockTileCount = (int) (Math.random() * 10
					/ GameStates.scaleGenerationCountObjects + 60 / GameStates.scaleGenerationCountObjects);
			int[] soilBlockX = MyUtil.createIntArray(soilBlockTileCount, 0,
					GameStates.mapWidth);
			int[] soilBlockY = MyUtil.createIntArray(soilBlockTileCount, 0,
					GameStates.mapHeight);

			objectMapManager.paintTilesOnMap(p, soilBlockX, soilBlockY,
					GameStates.soilBlockTile);
			objectMapManagerOverlay.paintTilesOnMap(p, soilBlockX, soilBlockY,
					GameStates.soilBlockTile - GameStates.mapTileSetWidth * 2);

			objectMapManager.addStayList(p);
			objectMapManagerOverlay.addStayList(p);

			objectMapManager.addChangedList(p);
			objectMapManagerOverlay.addChangedList(p);
			mapLoadCount++;
		}
	}

	/** paint fluid on the underground layer */
	public void paintFluidRandomOnUndergroundA(
			ObjectMapManager objectMapManager,
			ObjectMapManager objectMapManagerOverlay, int x, int y) {
		Point p = new Point(x, y);
		if (objectMapManager.getPaintLayer().equals("tree1")) {
			objectMapManager.createMap(p);
			objectMapManagerOverlay.createMap(p);
//			objectMapManager.createFramedPaintedMap(p, GameStates.fluidSoilTile);
//			objectMapManagerOverlay.createFramedPaintedMap(p, GameStates.fluidSoilTile- GameStates.mapTileSetWidth * 4);
			
			int soilBlockTileCount = 1;// (int)
										// (Math.random()*10/GameStates.scaleGenerationCountObjects
										// + 60/GameStates.scaleGenerationCountObjects);
			int[] soilBlockX = MyUtil.createIntArray(soilBlockTileCount, 2,
					GameStates.mapWidth - 2);
			int[] soilBlockY = MyUtil.createIntArray(soilBlockTileCount, 2,
					GameStates.mapHeight - 2);

			// objectMapManager.paintFluidTilesOnMap(p, soilBlockX, soilBlockY,
			// GameStates.fluidSoilTile);
			// objectMapManagerOverlay.paintFluidTilesOnMap(p, soilBlockX,
			// soilBlockY,
			// GameStates.fluidSoilTile-GameStates.mapTileSetWidth*4);
			objectMapManager.paintFluidFrameOnMap(p, soilBlockX, soilBlockY,
					GameStates.fluidSoilTile);
			objectMapManagerOverlay.paintFluidFrameOnMap(p, soilBlockX,
					soilBlockY, GameStates.fluidSoilTile
							- GameStates.mapTileSetWidth * 4);

			objectMapManager.get(p).setInitialized(true);
			objectMapManagerOverlay.get(p).setInitialized(true);
			objectMapManager.addStayList(p);
			objectMapManagerOverlay.addStayList(p);

			objectMapManager.addChangedList(p);
			objectMapManagerOverlay.addChangedList(p);
			mapLoadCount++;
		}
	}

	/** paint fluid on the underground layer */
	public void paintFluidFullOnUndergroundA(ObjectMapManager objectMapManager,
			ObjectMapManager objectMapManagerOverlay, int x, int y) {
		Point p = new Point(x, y);
		if (objectMapManager.getPaintLayer().equals("tree1")) {
			objectMapManager.createPaintedMap(p, GameStates.fluidSoilTile);
			objectMapManagerOverlay.createPaintedMap(p,
					GameStates.fluidSoilTile - GameStates.mapTileSetWidth * 4);

			objectMapManager.get(p).setInitialized(true);
			objectMapManagerOverlay.get(p).setInitialized(true);
			objectMapManager.addStayList(p);
			objectMapManagerOverlay.addStayList(p);

			objectMapManager.addChangedList(p);
			objectMapManagerOverlay.addChangedList(p);
			mapLoadCount++;
		}
	}

	/** paint fluid on the underground layer */
	public void paintFluidFrameOnUndergroundA(
			ObjectMapManager objectMapManager,
			ObjectMapManager objectMapManagerOverlay, int x, int y) {
		Point p = new Point(x, y);
		if (objectMapManager.getPaintLayer().equals("tree1")) {
			objectMapManager
					.createFramedPaintedMap(p, GameStates.fluidSoilTile);
			objectMapManagerOverlay.createFramedPaintedMap(p,
					GameStates.fluidSoilTile - GameStates.mapTileSetWidth * 4);

			objectMapManager.get(p).setInitialized(true);
			objectMapManagerOverlay.get(p).setInitialized(true);
			objectMapManager.addStayList(p);
			objectMapManagerOverlay.addStayList(p);

			objectMapManager.addChangedList(p);
			objectMapManagerOverlay.addChangedList(p);
			mapLoadCount++;
		}
		// if (objectMapManager.getPaintLayer().equals("tree2")) {
		// objectMapManager.createMap(p);
		// objectMapManagerOverlay.createMap(p);
		// objectMapManager.get(p).setInitialized(true);
		// objectMapManagerOverlay.get(p).setInitialized(true);
		// objectMapManager.addStayList(p);
		// objectMapManagerOverlay.addStayList(p);
		//
		// objectMapManager.addChangedList(p);
		// objectMapManagerOverlay.addChangedList(p);
		// mapLoadCount++;
		// }
	}

}
