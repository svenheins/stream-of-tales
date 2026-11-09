/* Copyright (C) Sven Heins - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * author: Sven Heins
 * date: 05.07.2015
 */
package de.svenheins.managers;

import java.awt.Point;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.imageio.ImageIO;

import de.svenheins.functions.MyStrings;
import de.svenheins.functions.MyUtil;
import de.svenheins.main.GameStates;
import de.svenheins.main.TileDimensions;
import de.svenheins.main.TileType;
import de.svenheins.objects.InteractionArea;
import de.svenheins.objects.InteractionTile;
import de.svenheins.objects.LocalMap;
import de.svenheins.objects.WorldLatticePosition;

public class ObjectMapManager {
	public HashMap<Point, LocalMap> localMapList = new HashMap<Point, LocalMap>();
	
	public HashMap<Point, String> localMapFileList = new HashMap<Point, String>();
	
	public List<Point> pointList = new ArrayList<Point>();
	public String paintLayer;
	public ArrayList<Point> changedList = new ArrayList<Point>();
	/** stayList is used to prevent GamePanel delete the map in the paint method if its too far away*/
	public ArrayList<Point> stayList = new ArrayList<Point>();
	private int mapWidth, mapHeight;
	
	public ObjectMapManager(String paintLayer, int mapWidth, int mapHeight) {
		this.setMapWidth(mapWidth);
		this.setMapHeight(mapHeight);
		this.paintLayer = paintLayer;
		changedList = new ArrayList<Point>();
		stayList = new ArrayList<Point>();
		
	}
	
	public void remove(Point point) {
		Point moduloPoint = new Point(((point.x/GameStates.mapTotalWidth) % this.getMapWidth() +this.getMapWidth()) % this.getMapWidth() *GameStates.mapTotalWidth, ((point.y/GameStates.mapTotalHeight) % this.getMapHeight() +this.getMapHeight()) % this.getMapHeight() *GameStates.mapTotalHeight);
		pointList.remove(moduloPoint);
		localMapList.remove(moduloPoint);
	}
	
	
	public boolean add(LocalMap localMap){
		if (localMapList.containsKey(localMap.getOrigin())) {
			return false;
		} else {
			localMapList.put(localMap.getOrigin(), localMap);
			pointList.add(localMap.getOrigin());
//			System.out.println("SpaceManager added new space: "+ space.getId());
			return true;
		}
	}
	
	public LocalMap get(Point point){
		try {
			Point moduloPoint = new Point(((point.x/GameStates.mapTotalWidth) % this.getMapWidth() +this.getMapWidth()) % this.getMapWidth() *GameStates.mapTotalWidth, ((point.y/GameStates.mapTotalHeight) % this.getMapHeight() +this.getMapHeight()) % this.getMapHeight() *GameStates.mapTotalHeight);
			return (LocalMap) localMapList.get(moduloPoint);
//			return (Space) spaceList.values().toArray()[index];
		}
		catch(NullPointerException e){
			System.out.println("Null-Object returned");
			return null;
		}
	}
	
	public  boolean overwrite(LocalMap localMap){
		if (!localMapList.containsKey(localMap.getOrigin())) {
			/** do nothing if key is not yet set*/
			return false;
		} else {
			localMapList.put(localMap.getOrigin(), localMap);
//			idList.add(space.getId());
			return true;
		}
	}
	
	
	public  int size(){
		return localMapList.size();
	}

	
	public  boolean contains(LocalMap localMap) {
		return localMapList.containsValue(localMap);
	}
	
	public boolean contains(Point point) {
		Point moduloPoint = new Point(((point.x/GameStates.mapTotalWidth) % this.getMapWidth() +this.getMapWidth()) % this.getMapWidth() *GameStates.mapTotalWidth, ((point.y/GameStates.mapTotalHeight) % this.getMapHeight() +this.getMapHeight()) % this.getMapHeight() *GameStates.mapTotalHeight);
		return pointList.contains(moduloPoint);
	}
	

	/** initialize the map */
	public void createMap(Point point) {
		Point moduloPoint = new Point(((point.x/GameStates.mapTotalWidth) % this.getMapWidth() +this.getMapWidth()) % this.getMapWidth() *GameStates.mapTotalWidth, ((point.y/GameStates.mapTotalHeight) % this.getMapHeight() +this.getMapHeight()) % this.getMapHeight() *GameStates.mapTotalHeight);
		int mapArray[][] = new int[GameStates.mapWidth][GameStates.mapHeight];
		LocalMap map = new LocalMap(mapArray,moduloPoint);
		localMapList.put(map.getOrigin(), map);
		if (!pointList.contains(map.getOrigin()))
			pointList.add(map.getOrigin());
//		this.addChangedList(point);
	}
	
	/** initialize the map and store only paintType values*/
	public void createPaintedMap(Point point, int paintType) {
		Point moduloPoint = new Point(((point.x/GameStates.mapTotalWidth) % this.getMapWidth() +this.getMapWidth()) % this.getMapWidth() *GameStates.mapTotalWidth, ((point.y/GameStates.mapTotalHeight) % this.getMapHeight() +this.getMapHeight()) % this.getMapHeight() *GameStates.mapTotalHeight);
		int mapArray[][] = new int[GameStates.mapWidth][GameStates.mapHeight];
		for (int i = 0; i < mapArray[0].length;i++) {
			for(int j = 0; j < mapArray.length;j++) {
				mapArray[j][i]=paintType;
			}
		}
		LocalMap map = new LocalMap(mapArray,moduloPoint);
		localMapList.put(map.getOrigin(), map);
		if (!pointList.contains(map.getOrigin()))
			pointList.add(map.getOrigin());
//		this.addChangedList(point);
	}
	
	/** initialize the map and store only paintType values*/
	public void createFramedPaintedMap(Point point, int paintType) {
		Point moduloPoint = new Point(((point.x/GameStates.mapTotalWidth) % this.getMapWidth() +this.getMapWidth()) % this.getMapWidth() *GameStates.mapTotalWidth, ((point.y/GameStates.mapTotalHeight) % this.getMapHeight() +this.getMapHeight()) % this.getMapHeight() *GameStates.mapTotalHeight);
		int mapArray[][] = new int[GameStates.mapWidth][GameStates.mapHeight];
		for (int i = 0; i < mapArray[0].length;i++) {
			mapArray[0][i] = paintType+1;
//			mapArray[1][i] = paintType+1;
			mapArray[GameStates.mapWidth-1][i] = paintType-1;
//			mapArray[GameStates.mapWidth-2][i] = paintType-1;
		}
		for (int j = 0; j < mapArray.length;j++) {
			if ((j >0) && (j<GameStates.mapWidth-1)) mapArray[j][0] = paintType+GameStates.mapTileSetWidth;
			if ((j >0) && (j<GameStates.mapWidth-1)) mapArray[j][1] = paintType+2*GameStates.mapTileSetWidth;
//			if ((j >1) && (j<GameStates.mapWidth-2)) mapArray[j][2] = paintType+2*GameStates.mapTileSetWidth;
//			mapArray[j][GameStates.mapHeight-1] = paintType;
			if ((j >0) && (j<GameStates.mapWidth-1)) mapArray[j][GameStates.mapHeight-1] = paintType-GameStates.mapTileSetWidth;
			
		}
		
		/**create the local Map */
		LocalMap map = new LocalMap(mapArray,moduloPoint);
		
		localMapList.put(map.getOrigin(), map);
		if (!pointList.contains(map.getOrigin()))
			pointList.add(map.getOrigin());
		else {
			System.out.println("CreateFramedPaintedMap : map is already in the localMapList!");
		}
		
	}
	
	/** paint onto the map */
	public void paintTiles(Point mapCoordinates, Point localPoint, int tile) {
		if (mapCoordinates != null) {
		
		int localX = localPoint.x;
		int localY = localPoint.y;

		/** correction for boundary values */
		if (localX > GameStates.mapWidth-2) localX =GameStates.mapWidth-2; 
		if (localY > GameStates.mapHeight-2) localY =GameStates.mapHeight-2; 
		if (localX < 2) localX = 2; 
		if (localY < 2) localY = 2; 
		if ( localX % 2 != 0 ) {
			if (localX < GameStates.mapWidth-4)
				localX++;
			else localX--;
		}
		if (localY % 2 != 0 ) {
			if (localY < GameStates.mapHeight-4)
				localY++;
			else localY--;
		}

		
		/** check if the map is already existing */
		if (this.get(mapCoordinates) != null) {
			/** choose the correct ObjectMapManager 
			 * first: ObjectMapManager: tree1*/
			if (localX % 4 == 0) {
				if (this.getPaintLayer().equals("tree1")) {
					/** only paint if emtpy */
					if (this.get(mapCoordinates).getTile(localX, localY) == 0) {
						/** get the tile dimensions */
						TileDimensions tileDimensions = LocalMap.getTileDimensions(tile);
						if (tileDimensions == TileDimensions.RowCol1x1) {
							this.get(mapCoordinates).getLocalMap()[localX][localY] = tile;
						} else {
							if (tileDimensions == TileDimensions.RowCol2x2) this.get(mapCoordinates).setUl(localX, localY, 0);
							else this.get(mapCoordinates).setUl(localX, localY, tile);
							this.get(mapCoordinates).setUr(localX, localY, tile);
							this.get(mapCoordinates).setDl(localX, localY, 0);
							this.get(mapCoordinates).setDr(localX, localY, 0);
							this.get(mapCoordinates).setIdByCornersObject(localX, localY, tile, tileDimensions);
							this.adjustSurrounding(this.get(mapCoordinates), localX, localY, tile, tileDimensions);
						}
					}
				} else {
					// not OK
				}
				/** Overlay of tree1 */
				if (this.getPaintLayer().equals("overlayTree1")) {
					/** only paint if emtpy */
					if (this.get(mapCoordinates).getTile(localX, localY) == 0) {
						TileDimensions tileDimensions = LocalMap.getTileDimensions(tile+2*GameStates.mapTileSetWidth);
		//				System.out.println("Painting with "+tileDimensions);
						if (tileDimensions == TileDimensions.RowCol1x1) {
							this.get(mapCoordinates).getLocalMap()[localX][localY] = tile;
						} else {
							if (tileDimensions == TileDimensions.RowCol2x2) this.get(mapCoordinates).setUl(localX, localY, 0);
							else this.get(mapCoordinates).setUl(localX, localY, tile);
							this.get(mapCoordinates).setUr(localX, localY, tile);
							this.get(mapCoordinates).setDl(localX, localY, 0);
							this.get(mapCoordinates).setDr(localX, localY, 0);
							this.get(mapCoordinates).setIdByCornersObject(localX, localY, tile, tileDimensions);
							this.adjustSurrounding(this.get(mapCoordinates), localX, localY, tile, tileDimensions);
						}
					}
				} else {
					// not OK
				}
			}
			/** second: ObjectMapManager: tree2*/
			if (localX % 4 == 2) {
				if (this.getPaintLayer().equals("tree2")) {
					/** only paint if emtpy */
					if (this.get(mapCoordinates).getTile(localX, localY) == 0) {
						TileDimensions tileDimensions = LocalMap.getTileDimensions(tile);
		//				System.out.println("Painting with "+tileDimensions);
						if (tileDimensions == TileDimensions.RowCol1x1) {
							this.get(mapCoordinates).getLocalMap()[localX][localY] = tile;
						} else {
							this.get(mapCoordinates).setUl(localX, localY, tile);
							if (tileDimensions == TileDimensions.RowCol2x2) this.get(mapCoordinates).setUl(localX, localY, 0);
							this.get(mapCoordinates).setUr(localX, localY, tile);
							this.get(mapCoordinates).setDl(localX, localY, 0);
							this.get(mapCoordinates).setDr(localX, localY, 0);
							this.get(mapCoordinates).setIdByCornersObject(localX, localY, tile, tileDimensions);
							this.adjustSurrounding(this.get(mapCoordinates), localX, localY, tile, tileDimensions);
						} 
					}
				} else {
					// not OK
				}
				/** Overlay of tree2 */
				if (this.getPaintLayer().equals("overlayTree2")) {
					/** only paint if emtpy */
					if (this.get(mapCoordinates).getTile(localX, localY) == 0) {
						TileDimensions tileDimensions = LocalMap.getTileDimensions(tile+2*GameStates.mapTileSetWidth);
		//				System.out.println("Painting with "+tileDimensions);
						if (tileDimensions == TileDimensions.RowCol1x1) {
							this.get(mapCoordinates).getLocalMap()[localX][localY] = tile;
						} else {
							this.get(mapCoordinates).setUl(localX, localY, tile);
							if (tileDimensions == TileDimensions.RowCol2x2) this.get(mapCoordinates).setUl(localX, localY, 0);
							this.get(mapCoordinates).setUr(localX, localY, tile);
							this.get(mapCoordinates).setDl(localX, localY, 0);
							this.get(mapCoordinates).setDr(localX, localY, 0);
							this.get(mapCoordinates).setIdByCornersObject(localX, localY, tile, tileDimensions);
							this.adjustSurrounding(this.get(mapCoordinates), localX, localY, tile, tileDimensions);
						}
					}
				} else {
					// not OK
				}
			}
		}
		}
		
	}
	
	/** paint onto the map */
	public void paintFluidTiles(Point mapCoordinates, Point localPoint, int tile) {
		if (mapCoordinates != null) {
		
		int localX = localPoint.x;
		int localY = localPoint.y;

		/** correction for boundary values */
		if (localX > GameStates.mapWidth-1) localX =GameStates.mapWidth-1; 
		if (localY > GameStates.mapHeight-1) localY =GameStates.mapHeight-1; 
		if (localX < 1) localX = 1; 
		if (localY < 1) localY = 1; 
//		if ( localX % 2 != 0 ) {
//			if (localX < GameStates.mapWidth-4)
//				localX++;
//			else localX--;
//		}
//		if (localY % 2 != 0 ) {
//			if (localY < GameStates.mapHeight-4)
//				localY++;
//			else localY--;
//		}

		
		/** check if the map is already existing */
		if (this.get(mapCoordinates) != null) {
			/** choose the correct ObjectMapManager 
			 * first: ObjectMapManager: tree1*/
//			if (localX % 4 == 0) {
				if (this.getPaintLayer().equals("tree1") || this.getPaintLayer().equals("overlayTree1")) {
					/** FLUID OBJECTS */
					/** define the relevant checkPositions */
					int checkPositionX = localX;
					if ( checkPositionX % 2 != 0 ) {
						if (checkPositionX < GameStates.mapWidth-1)
							checkPositionX++;
						else checkPositionX--;
					}
					int checkPositionY = localY;
					if ( checkPositionY % 2 != 0 ) {
						if (checkPositionY < GameStates.mapHeight-1)
							checkPositionY++;
						else checkPositionY--;
					}

//					createFramedPaintedMap(mapCoordinates, tile);
					this.get(mapCoordinates).setTile(localX, localY, tile);
					this.adjustSurrounding(this.get(mapCoordinates), localX, localY, tile, TileDimensions.Fluid);
					
					if (LocalMap.getPaintType(this.get(mapCoordinates).getTile(checkPositionX, checkPositionY)) == 0) {
						this.get(mapCoordinates).setTile(checkPositionX, checkPositionY, 1);
					}
				} else {
					// not OK
				}
				
			}
//			/** second: ObjectMapManager: tree2*/
//			if (localX % 4 == 2) {
//				if (this.getPaintLayer().equals("tree2") || this.getPaintLayer().equals("overlayTree2")) {
//					/** FLUID OBJECTS */
//					/** define the relevant checkPositions */
//					int checkPositionX = localX;
//					if ( checkPositionX % 2 != 0 ) {
//						if (checkPositionX < GameStates.mapWidth-1)
//							checkPositionX++;
//						else checkPositionX--;
//					}
//					int checkPositionY = localY;
//					if ( checkPositionY % 2 != 0 ) {
//						if (checkPositionY < GameStates.mapHeight-1)
//							checkPositionY++;
//						else checkPositionY--;
//					}
//
//					this.get(mapCoordinates).setTile(localX, localY, tile);
//					this.adjustSurrounding(this.get(mapCoordinates), localX, localY, tile, TileDimensions.Fluid);
//					
//					if (LocalMap.getPaintType(this.get(mapCoordinates).getTile(checkPositionX, checkPositionY)) == 0) {
//						this.get(mapCoordinates).setTile(checkPositionX, checkPositionY, 1);
//					}
//				} else {
//					// not OK
//				}
//			}		
		
		}
	}
	
	
//	/** paint onto the map */
//	public void paintTiles(Point mapCoordinates, Point localPoint, int tile) {
//		if (mapCoordinates != null) {
//		
//		int localX = localPoint.x;
//		int localY = localPoint.y;
//
//		/** correction for boundary values */
//		if (localX > GameStates.mapWidth-2) localX =GameStates.mapWidth-2; 
//		if (localY > GameStates.mapHeight-2) localY =GameStates.mapHeight-2; 
//		if (localX < 2) localX = 2; 
//		if (localY < 2) localY = 2; 
//		if ( localX % 2 != 0 ) {
//			if (localX < GameStates.mapWidth-4)
//				localX++;
//			else localX--;
//		}
//		if (localY % 2 != 0 ) {
//			if (localY < GameStates.mapHeight-4)
//				localY++;
//			else localY--;
//		}
//
//		
//		/** check if the map is already existing */
//		if (this.get(mapCoordinates) != null) {
//			/** choose the correct ObjectMapManager 
//			 * first: ObjectMapManager: tree1*/
//			if (localX % 4 == 0) {
//				if (this.getPaintLayer().equals("tree1")) {
//					/** only paint if emtpy */
//					if (this.get(mapCoordinates).getTile(localX, localY) == 0) {
//						/** get the tile dimensions */
//						TileDimensions tileDimensions = LocalMap.getTileDimensions(tile);
//						if (tileDimensions == TileDimensions.RowCol1x1) {
//							this.get(mapCoordinates).getLocalMap()[localX][localY] = tile;
//						} else {
//							if (tileDimensions == TileDimensions.RowCol2x2) this.get(mapCoordinates).setUl(localX, localY, 0);
//							else this.get(mapCoordinates).setUl(localX, localY, tile);
//							this.get(mapCoordinates).setUr(localX, localY, tile);
//							this.get(mapCoordinates).setDl(localX, localY, 0);
//							this.get(mapCoordinates).setDr(localX, localY, 0);
//							this.get(mapCoordinates).setIdByCornersObject(localX, localY, tile, tileDimensions);
//							this.adjustSurrounding(this.get(mapCoordinates), localX, localY, tile, tileDimensions);
//						}
//					}
//				} else {
//					// not OK
//				}
//				/** Overlay of tree1 */
//				if (this.getPaintLayer().equals("overlayTree1")) {
//					/** only paint if emtpy */
//					if (this.get(mapCoordinates).getTile(localX, localY) == 0) {
//						TileDimensions tileDimensions = LocalMap.getTileDimensions(tile+2*GameStates.mapTileSetWidth);
//		//				System.out.println("Painting with "+tileDimensions);
//						if (tileDimensions == TileDimensions.RowCol1x1) {
//							this.get(mapCoordinates).getLocalMap()[localX][localY] = tile;
//						} else {
//							if (tileDimensions == TileDimensions.RowCol2x2) this.get(mapCoordinates).setUl(localX, localY, 0);
//							else this.get(mapCoordinates).setUl(localX, localY, tile);
//							this.get(mapCoordinates).setUr(localX, localY, tile);
//							this.get(mapCoordinates).setDl(localX, localY, 0);
//							this.get(mapCoordinates).setDr(localX, localY, 0);
//							this.get(mapCoordinates).setIdByCornersObject(localX, localY, tile, tileDimensions);
//							this.adjustSurrounding(this.get(mapCoordinates), localX, localY, tile, tileDimensions);
//						}
//					}
//				} else {
//					// not OK
//				}
//			}
//			/** second: ObjectMapManager: tree2*/
//			if (localX % 4 == 2) {
//				if (this.getPaintLayer().equals("tree2")) {
//					/** only paint if emtpy */
//					if (this.get(mapCoordinates).getTile(localX, localY) == 0) {
//						TileDimensions tileDimensions = LocalMap.getTileDimensions(tile);
//		//				System.out.println("Painting with "+tileDimensions);
//						if (tileDimensions == TileDimensions.RowCol1x1) {
//							this.get(mapCoordinates).getLocalMap()[localX][localY] = tile;
//						} else {
//							this.get(mapCoordinates).setUl(localX, localY, tile);
//							if (tileDimensions == TileDimensions.RowCol2x2) this.get(mapCoordinates).setUl(localX, localY, 0);
//							this.get(mapCoordinates).setUr(localX, localY, tile);
//							this.get(mapCoordinates).setDl(localX, localY, 0);
//							this.get(mapCoordinates).setDr(localX, localY, 0);
//							this.get(mapCoordinates).setIdByCornersObject(localX, localY, tile, tileDimensions);
//							this.adjustSurrounding(this.get(mapCoordinates), localX, localY, tile, tileDimensions);
//						} 
//					}
//				} else {
//					// not OK
//				}
//				/** Overlay of tree2 */
//				if (this.getPaintLayer().equals("overlayTree2")) {
//					/** only paint if emtpy */
//					if (this.get(mapCoordinates).getTile(localX, localY) == 0) {
//						TileDimensions tileDimensions = LocalMap.getTileDimensions(tile+2*GameStates.mapTileSetWidth);
//		//				System.out.println("Painting with "+tileDimensions);
//						if (tileDimensions == TileDimensions.RowCol1x1) {
//							this.get(mapCoordinates).getLocalMap()[localX][localY] = tile;
//						} else {
//							this.get(mapCoordinates).setUl(localX, localY, tile);
//							if (tileDimensions == TileDimensions.RowCol2x2) this.get(mapCoordinates).setUl(localX, localY, 0);
//							this.get(mapCoordinates).setUr(localX, localY, tile);
//							this.get(mapCoordinates).setDl(localX, localY, 0);
//							this.get(mapCoordinates).setDr(localX, localY, 0);
//							this.get(mapCoordinates).setIdByCornersObject(localX, localY, tile, tileDimensions);
//							this.adjustSurrounding(this.get(mapCoordinates), localX, localY, tile, tileDimensions);
//						}
//					}
//				} else {
//					// not OK
//				}
//			}
//		}
//		}
//		
//	}
	
	public LocalMap createMapIfNonExistent(Point point) {
		Point moduloPoint = new Point(((point.x/GameStates.mapTotalWidth) % this.getMapWidth() +this.getMapWidth()) % this.getMapWidth() *GameStates.mapTotalWidth, ((point.y/GameStates.mapTotalHeight) % this.getMapHeight() +this.getMapHeight()) % this.getMapHeight() *GameStates.mapTotalHeight);
		if ( !pointList.contains(moduloPoint)) {
			createMap(moduloPoint);
		}
		return get(moduloPoint);
	}
	
	
	public void adjustSurrounding(LocalMap localMap, int localX, int localY, int paintType, TileDimensions tileDim) {
		switch (tileDim) {
		case RowCol2x3: {
			setTileCorners(localMap, localX, localY-1, paintType, tileDim);
			setTileCorners(localMap, localX-1, localY, paintType, tileDim);
			setTileCorners(localMap, localX+1, localY, paintType, tileDim);			
			setTileCorners(localMap, localX-1, localY-1, paintType, tileDim);
			setTileCorners(localMap, localX+1, localY-1, paintType, tileDim);
			break;
		}
		case RowCol2x2: {
			setTileCorners(localMap, localX+1, localY, paintType, tileDim);
			setTileCorners(localMap, localX+1, localY-1, paintType, tileDim);
			setTileCorners(localMap, localX, localY-1, paintType, tileDim);
			break;
		}
		case RowCol1x2: {
			setTileCorners(localMap, localX+1, localY, paintType, tileDim);
			break;
		}
		case RowCol2x1: {
			setTileCorners(localMap, localX, localY-1, paintType, tileDim);
			break;
		}
		case Fluid: {
			setFluidCorners(localMap, localX, localY-1, paintType, tileDim);
			setFluidCorners(localMap, localX-1, localY, paintType, tileDim);
			setFluidCorners(localMap, localX-1, localY+1, paintType, tileDim);
//			if (LocalMap.isFluidBottom(localMap.getTile(localX-1, localY))) {
//				
//			}
			setFluidCorners(localMap, localX+1, localY, paintType, tileDim);
			setFluidCorners(localMap, localX+1, localY+1, paintType, tileDim);
			setFluidCorners(localMap, localX, localY, paintType, tileDim);
			setFluidCorners(localMap, localX, localY+1, paintType, tileDim);
			/** important: the lower part is not dependent on the center!!!!!! */
			break;
		}
		default: {
			break;
		}
		}

//		setTileCorners(localMap, localX, localY-1, paintType);
//		setTileCorners(localMap, localX-1, localY, paintType);
//		setTileCorners(localMap, localX+1, localY, paintType);
//		
//		setTileCorners(localMap, localX-1, localY-1, paintType);
//		setTileCorners(localMap, localX+1, localY-1, paintType);
	}
	
//	/** Set Id by corners for (2 x 2) Objects with an Overlay (e.g. thin trees) */
//	/** center is the upper left corner Tile of the object */
//public void adjustSurrounding2x2(LocalMap localMap, int localX, int localY, int paintType) {
//		
//		setTileCorners(localMap, localX+1, localY, paintType);
//		setTileCorners(localMap, localX, localY+1, paintType);
//		setTileCorners(localMap, localX+1, localY+1, paintType);
//		
////		setTileCorners(localMap, localX-1, localY-1, paintType);
////		setTileCorners(localMap, localX+1, localY-1, paintType);
//		
//		//setTileCorners(localMap, localX, localY);
//		
////		setTileCorners(localMap, localX-1, localY+1, paintType);
////		setTileCorners(localMap, localX, localY+1, paintType);
////		setTileCorners(localMap, localX+1, localY+1, paintType);
//	}
//
///** Set Id by corners for (1 x 2) Objects without an Overlay (e.g. large chest) */
///** center is the left side Tile of the object */
//public void adjustSurrounding1x2(LocalMap localMap, int localX, int localY, int paintType) {
//	setTileCorners(localMap, localX+1, localY, paintType);
//}
//
//
///** Set Id by corners for (2 x 1) Objects without an Overlay (e.g. large sign/ thin and large tree) */
///** center is the lower Tile of the object */
//public void adjustSurrounding2x1(LocalMap localMap, int localX, int localY, int paintType) {
//	setTileCorners(localMap, localX, localY-1, paintType);
//}
	
	public void deleteSurrounding(LocalMap localMap, int localX, int localY, int paintType, TileDimensions tileDim) {
		Point origin = localMap.getOrigin();
		LocalMap ulMap;
		LocalMap uMap;
		LocalMap urMap;
		LocalMap lMap;
		LocalMap rMap;
		LocalMap centerMap;
		LocalMap dlMap;
		LocalMap dMap;
		LocalMap drMap;
		Point ulTile;
		Point uTile;
		Point urTile;
		Point lTile;
		Point rTile;
		Point centerTile;
		Point dlTile;
		Point dTile;
		Point drTile;
		
		int maxIndexX = GameStates.mapWidth-1;
		int maxIndexY = GameStates.mapHeight-1;
		
		if (localX <= 0) {
			if (localX == 0) {
				if (localY <= 0) {
					if (localY == 0) {
						// 1
						ulMap = createMapIfNonExistent(getUpperLeftMap(origin));
						uMap = urMap = createMapIfNonExistent(getUpperMap(origin));
						lMap = dlMap = createMapIfNonExistent(getLeftMap(origin));
						centerMap = dMap = drMap = rMap = localMap;
						ulTile = new Point(maxIndexX, maxIndexY);
						uTile = new Point(0, maxIndexY);
						urTile = new Point(1, maxIndexY);
						lTile = new Point(maxIndexX, 0);
						centerTile = new Point(0, 0);
						rTile = new Point(1, 0);
						dlTile = new Point(maxIndexX, 1);
						dTile = new Point(0, 1);
						drTile = new Point(1, 1);
					} else {
						// 2
						ulMap = lMap = createMapIfNonExistent(getUpperLeftMap(origin));
						uMap = urMap = centerMap = rMap = createMapIfNonExistent(getUpperMap(origin));
						dlMap = createMapIfNonExistent(getLeftMap(origin));
						dMap = drMap = localMap;
						ulTile = new Point(maxIndexX, maxIndexY-1);
						uTile = new Point(0, maxIndexY-1);
						urTile = new Point(1, maxIndexY-1);
						lTile = new Point(maxIndexX, maxIndexY);
						centerTile = new Point(0, maxIndexY);
						rTile = new Point(1, maxIndexY);
						dlTile = new Point(maxIndexX, 0);
						dTile = new Point(0, 0);
						drTile = new Point(1, 0);
					}
				} else if (localY >= GameStates.mapHeight-1) {
					if (localY == GameStates.mapHeight-1) {
						// 3
						ulMap = lMap = createMapIfNonExistent(getLeftMap(origin));
						uMap = urMap = centerMap = rMap = localMap;
						dlMap = createMapIfNonExistent(getLowerLeftMap(origin));
						dMap = drMap = createMapIfNonExistent(getLowerMap(origin));
						ulTile = new Point(maxIndexX, maxIndexY-1);
						uTile = new Point(0, maxIndexY-1);
						urTile = new Point(1, maxIndexY-1);
						lTile = new Point(maxIndexX, maxIndexY);
						centerTile = new Point(0, maxIndexY);
						rTile = new Point(1, maxIndexY);
						dlTile = new Point(maxIndexX, 0);
						dTile = new Point(0, 0);
						drTile = new Point(1, 0);
					} else {
						// 4
						ulMap = createMapIfNonExistent(getLeftMap(origin));
						uMap = urMap = localMap;
						lMap = dlMap = createMapIfNonExistent(getLowerLeftMap(origin));
						centerMap = rMap = dMap = drMap = createMapIfNonExistent(getLowerMap(origin));
						ulTile = new Point(maxIndexX, maxIndexY);
						uTile = new Point(0, maxIndexY);
						urTile = new Point(1, maxIndexY);
						lTile = new Point(maxIndexX, 0);
						centerTile = new Point(0, 0);
						rTile = new Point(1, 0);
						dlTile = new Point(maxIndexX, 1);
						dTile = new Point(0, 1);
						drTile = new Point(1, 1);
					}
				} else {
					// 5
					ulMap = lMap = dlMap = createMapIfNonExistent(getLeftMap(origin));
					uMap = urMap = centerMap = rMap = dMap = drMap = localMap;
					ulTile = new Point(maxIndexX, localY-1);
					uTile = new Point(0, localY-1);
					urTile = new Point(1, localY-1);
					lTile = new Point(maxIndexX, localY);
					centerTile = new Point(0, localY);
					rTile = new Point(1, localY);
					dlTile = new Point(maxIndexX, localY+1);
					dTile = new Point(0, localY+1);
					drTile = new Point(1, localY+1);
				}
			} else {
				if (localY <= 0) {
					if (localY == 0) {
						// 6
						ulMap = uMap = createMapIfNonExistent(getUpperLeftMap(origin));
						urMap = createMapIfNonExistent(getUpperMap(origin));
						lMap = centerMap = dlMap = dMap = createMapIfNonExistent(getLeftMap(origin));
						rMap = drMap = localMap;
						ulTile = new Point(maxIndexX-1, maxIndexY);
						uTile = new Point(maxIndexX, maxIndexY);
						urTile = new Point(0, maxIndexY);
						lTile = new Point(maxIndexX-1, 0);
						centerTile = new Point(maxIndexX, 0);
						rTile = new Point(0, 0);
						dlTile = new Point(maxIndexX-1, 1);
						dTile = new Point(maxIndexX, 1);
						drTile = new Point(0, 1);
					} else {
						// 7
						ulMap = uMap = lMap = centerMap = createMapIfNonExistent(getUpperLeftMap(origin));
						urMap = rMap = createMapIfNonExistent(getUpperMap(origin));
						dlMap = dMap = createMapIfNonExistent(getLeftMap(origin));
						drMap = localMap;
						ulTile = new Point(maxIndexX-1, maxIndexY-1);
						uTile = new Point(maxIndexX, maxIndexY-1);
						urTile = new Point(0, maxIndexY-1);
						lTile = new Point(maxIndexX-1, maxIndexY);
						centerTile = new Point(maxIndexX, maxIndexY);
						rTile = new Point(0, maxIndexY);
						dlTile = new Point(maxIndexX-1, 0);
						dTile = new Point(maxIndexX, 0);
						drTile = new Point(0, 0);
					}
				} else if (localY >= GameStates.mapHeight-1) {
					if (localY == GameStates.mapHeight-1) {
						// 8
						ulMap = uMap = lMap = centerMap = createMapIfNonExistent(getLeftMap(origin));
						urMap = rMap = localMap;
						dlMap = dMap = createMapIfNonExistent(getLowerLeftMap(origin));
						drMap = createMapIfNonExistent(getLowerMap(origin));
						ulTile = new Point(maxIndexX-1, maxIndexY-1);
						uTile = new Point(maxIndexX, maxIndexY-1);
						urTile = new Point(0, maxIndexY-1);
						lTile = new Point(maxIndexX-1, maxIndexY);
						centerTile = new Point(maxIndexX, maxIndexY);
						rTile = new Point(0, maxIndexY);
						dlTile = new Point(maxIndexX-1, 0);
						dTile = new Point(maxIndexX, 0);
						drTile = new Point(0, 0);
					} else {
						// 9
						ulMap = uMap = createMapIfNonExistent(getLeftMap(origin));
						urMap = localMap;
						lMap = centerMap = dlMap = dMap = createMapIfNonExistent(getLowerLeftMap(origin));
						rMap = drMap = createMapIfNonExistent(getLowerMap(origin));
						ulTile = new Point(maxIndexX-1, maxIndexY);
						uTile = new Point(maxIndexX, maxIndexY);
						urTile = new Point(0, maxIndexY);
						lTile = new Point(maxIndexX-1, 0);
						centerTile = new Point(maxIndexX, 0);
						rTile = new Point(0, 0);
						dlTile = new Point(maxIndexX-1, 1);
						dTile = new Point(maxIndexX, 1);
						drTile = new Point(0, 1);
					}
				} else {
					// 10
					ulMap = uMap = lMap = centerMap = dlMap = dMap = createMapIfNonExistent(getLeftMap(origin));
					urMap = rMap = drMap = localMap;
					ulTile = new Point(maxIndexX-1, localY-1);
					uTile = new Point(maxIndexX, localY-1);
					urTile = new Point(0, localY-1);
					lTile = new Point(maxIndexX-1, localY);
					centerTile = new Point(maxIndexX, localY);
					rTile = new Point(0, localY);
					dlTile = new Point(maxIndexX-1, localY+1);
					dTile = new Point(maxIndexX, localY+1);
					drTile = new Point(0, localY+1);
				}
			}
		} else if (localX >= GameStates.mapWidth-1) {
			if (localX == GameStates.mapWidth-1) {
				if (localY <= 0) {
					if (localY == 0) {
						// 11
						ulMap = uMap = createMapIfNonExistent(getUpperMap(origin));
						urMap = createMapIfNonExistent(getUpperRightMap(origin));
						lMap = centerMap = dlMap = dMap = localMap;
						rMap = drMap = createMapIfNonExistent(getRightMap(origin));
						ulTile = new Point(maxIndexX-1, maxIndexY);
						uTile = new Point(maxIndexX, maxIndexY);
						urTile = new Point(0, maxIndexY);
						lTile = new Point(maxIndexX-1, 0);
						centerTile = new Point(maxIndexX, 0);
						rTile = new Point(0, 0);
						dlTile = new Point(maxIndexX-1, 1);
						dTile = new Point(maxIndexX, 1);
						drTile = new Point(0, 1);
					} else {
						// 12
						ulMap = uMap = lMap = centerMap = createMapIfNonExistent(getUpperMap(origin));
						urMap = rMap = createMapIfNonExistent(getUpperRightMap(origin));
						dlMap = dMap = localMap;
						drMap = createMapIfNonExistent(getLeftMap(origin));
						ulTile = new Point(maxIndexX-1, maxIndexY-1);
						uTile = new Point(maxIndexX, maxIndexY-1);
						urTile = new Point(0, maxIndexY-1);
						lTile = new Point(maxIndexX-1, maxIndexY);
						centerTile = new Point(maxIndexX, maxIndexY);
						rTile = new Point(0, maxIndexY);
						dlTile = new Point(maxIndexX-1, 0);
						dTile = new Point(maxIndexX, 0);
						drTile = new Point(0, 0);
					}
				} else if (localY >= GameStates.mapHeight-1) {
					if (localY == GameStates.mapHeight-1) {
						// 13
						ulMap = uMap = lMap = centerMap = localMap;
						urMap = rMap = createMapIfNonExistent(getRightMap(origin));
						dlMap = dMap = createMapIfNonExistent(getLowerMap(origin));
						drMap = createMapIfNonExistent(getLowerRightMap(origin));
						ulTile = new Point(maxIndexX-1, maxIndexY-1);
						uTile = new Point(maxIndexX, maxIndexY-1);
						urTile = new Point(0, maxIndexY-1);
						lTile = new Point(maxIndexX-1, maxIndexY);
						centerTile = new Point(maxIndexX, maxIndexY);
						rTile = new Point(0, maxIndexY);
						dlTile = new Point(maxIndexX-1, 0);
						dTile = new Point(maxIndexX, 0);
						drTile = new Point(0, 0);
					} else {
						// 14
						ulMap = uMap = localMap;
						urMap = createMapIfNonExistent(getRightMap(origin));
						lMap = centerMap = dlMap = dMap = createMapIfNonExistent(getLowerMap(origin));
						rMap = drMap = createMapIfNonExistent(getLowerRightMap(origin));
						ulTile = new Point(maxIndexX-1, maxIndexY);
						uTile = new Point(maxIndexX, maxIndexY);
						urTile = new Point(0, maxIndexY);
						lTile = new Point(maxIndexX-1, 0);
						centerTile = new Point(maxIndexX, 0);
						rTile = new Point(0, 0);
						dlTile = new Point(maxIndexX-1, 1);
						dTile = new Point(maxIndexX, 1);
						drTile = new Point(0, 1);
					}
				} else {
					// 15
					ulMap = uMap = lMap = centerMap = dlMap = dMap = localMap;
					urMap = rMap = drMap = createMapIfNonExistent(getRightMap(origin));
					ulTile = new Point(maxIndexX-1, localY-1);
					uTile = new Point(maxIndexX, localY-1);
					urTile = new Point(0, localY-1);
					lTile = new Point(maxIndexX-1, localY);
					centerTile = new Point(maxIndexX, localY);
					rTile = new Point(0, localY);
					dlTile = new Point(maxIndexX-1, localY+1);
					dTile = new Point(maxIndexX, localY+1);
					drTile = new Point(0, localY+1);
				}
			} else {
				if (localY <= 0) {
					if (localY == 0) {
						// 16
						ulMap = createMapIfNonExistent(getUpperMap(origin));
						uMap = urMap = createMapIfNonExistent(getUpperRightMap(origin));
						lMap = dlMap = localMap;
						centerMap = rMap = dMap = drMap = createMapIfNonExistent(getRightMap(origin));
						ulTile = new Point(maxIndexX, maxIndexY);
						uTile = new Point(0, maxIndexY);
						urTile = new Point(1, maxIndexY);
						lTile = new Point(maxIndexX, 0);
						centerTile = new Point(0, 0);
						rTile = new Point(1, 0);
						dlTile = new Point(maxIndexX, 1);
						dTile = new Point(0, 1);
						drTile = new Point(1, 1);
					} else {
						// 17
						ulMap = lMap = createMapIfNonExistent(getUpperMap(origin));
						uMap = urMap = centerMap = rMap = createMapIfNonExistent(getUpperRightMap(origin));
						dlMap = localMap;
						dMap = drMap = createMapIfNonExistent(getRightMap(origin));
						ulTile = new Point(maxIndexX, maxIndexY-1);
						uTile = new Point(0, maxIndexY-1);
						urTile = new Point(1, maxIndexY-1);
						lTile = new Point(maxIndexX, maxIndexY);
						centerTile = new Point(0, maxIndexY);
						rTile = new Point(1, maxIndexY);
						dlTile = new Point(maxIndexX, 0);
						dTile = new Point(0, 0);
						drTile = new Point(1, 0);
					}
				} else if (localY >= GameStates.mapHeight-1) {
					if (localY == GameStates.mapHeight-1) {
						// 18
						ulMap = lMap = localMap;
						uMap = urMap = centerMap = rMap = createMapIfNonExistent(getRightMap(origin));
						dlMap = createMapIfNonExistent(getLowerMap(origin));
						dMap = drMap = createMapIfNonExistent(getLowerRightMap(origin));
						ulTile = new Point(maxIndexX, maxIndexY-1);
						uTile = new Point(0, maxIndexY-1);
						urTile = new Point(1, maxIndexY-1);
						lTile = new Point(maxIndexX, maxIndexY);
						centerTile = new Point(0, maxIndexY);
						rTile = new Point(1, maxIndexY);
						dlTile = new Point(maxIndexX, 0);
						dTile = new Point(0, 0);
						drTile = new Point(1, 0);
					} else {
						// 19
						ulMap = localMap;
						uMap = urMap = createMapIfNonExistent(getRightMap(origin));
						lMap = dlMap = createMapIfNonExistent(getLowerMap(origin));
						centerMap = rMap = dMap = drMap = createMapIfNonExistent(getLowerRightMap(origin));
						ulTile = new Point(maxIndexX, maxIndexY);
						uTile = new Point(0, maxIndexY);
						urTile = new Point(1, maxIndexY);
						lTile = new Point(maxIndexX, 0);
						centerTile = new Point(0, 0);
						rTile = new Point(1, 0);
						dlTile = new Point(maxIndexX, 1);
						dTile = new Point(0, 1);
						drTile = new Point(1, 1);
					}
				} else {
					// 20
					ulMap = lMap = dlMap = localMap;
					uMap = urMap = centerMap = rMap = dMap = drMap = createMapIfNonExistent(getRightMap(origin)); 
					ulTile = new Point(maxIndexX, localY-1);
					uTile = new Point(0, localY-1);
					urTile = new Point(1, localY-1);
					lTile = new Point(maxIndexX, localY);
					centerTile = new Point(0, localY);
					rTile = new Point(1, localY);
					dlTile = new Point(maxIndexX, localY+1);
					dTile = new Point(0, localY+1);
					drTile = new Point(1, localY+1);
				}
			}
		} else {
			if (localY <= 0) {
				if (localY == 0) {
					// 21
					ulMap = uMap = urMap = createMapIfNonExistent(getUpperMap(origin));
					lMap = centerMap = rMap = dlMap = dMap = drMap = localMap;
					ulTile = new Point(localX-1, maxIndexY);
					uTile = new Point(localX, maxIndexY);
					urTile = new Point(localX+1, maxIndexY);
					lTile = new Point(localX-1, 0);
					centerTile = new Point(localX, 0);
					rTile = new Point(localX+1, 0);
					dlTile = new Point(localX-1, 1);
					dTile = new Point(localX, 1);
					drTile = new Point(localX+1, 1);
				} else {
					// 22
					ulMap = uMap = urMap = lMap = centerMap = rMap = createMapIfNonExistent(getUpperMap(origin));
					dlMap = dMap = drMap = localMap;
					ulTile = new Point(localX-1, maxIndexY-1);
					uTile = new Point(localX, maxIndexY-1);
					urTile = new Point(localX+1, maxIndexY-1);
					lTile = new Point(localX-1, maxIndexY);
					centerTile = new Point(localX, maxIndexY);
					rTile = new Point(localX+1, maxIndexY);
					dlTile = new Point(localX-1, 0);
					dTile = new Point(localX, 0);
					drTile = new Point(localX+1, 0);
				}
			} else if (localY >= GameStates.mapHeight-1) {
				if (localY == GameStates.mapHeight-1) {
					// 23
					ulMap = uMap = urMap = lMap = centerMap = rMap = localMap;
					dlMap = dMap = drMap = createMapIfNonExistent(getLowerMap(origin));
					ulTile = new Point(localX-1, maxIndexY-1);
					uTile = new Point(localX, maxIndexY-1);
					urTile = new Point(localX+1, maxIndexY-1);
					lTile = new Point(localX-1, maxIndexY);
					centerTile = new Point(localX, maxIndexY);
					rTile = new Point(localX+1, maxIndexY);
					dlTile = new Point(localX-1, 0);
					dTile = new Point(localX, 0);
					drTile = new Point(localX+1, 0);
				} else {
					// 24
					ulMap = uMap = urMap = localMap;
					lMap = centerMap = rMap = dlMap = dMap = drMap = createMapIfNonExistent(getLowerMap(origin));
					ulTile = new Point(localX-1, maxIndexY);
					uTile = new Point(localX, maxIndexY);
					urTile = new Point(localX+1, maxIndexY);
					lTile = new Point(localX-1, 0);
					centerTile = new Point(localX, 0);
					rTile = new Point(localX+1, 0);
					dlTile = new Point(localX-1, 1);
					dTile = new Point(localX, 1);
					drTile = new Point(localX+1, 1);
				}
			} else {
				// 25
				ulMap = uMap = urMap = lMap = centerMap = rMap = dlMap = dMap = drMap = localMap;
				ulTile = new Point(localX-1,localY-1);
				uTile = new Point(localX, localY-1);
				urTile = new Point(localX+1, localY-1);
				lTile = new Point(localX-1, localY);
				centerTile = new Point(localX, localY);
				rTile = new Point(localX+1, localY);
				dlTile = new Point(localX-1, localY+1);
				dTile = new Point(localX, localY+1);
				drTile = new Point(localX+1, localY+1);
			}
		}
		
		switch (tileDim) {
		case RowCol2x3: {
			/** delete all tiles in environment **/
			/** here the down tiles are missing because they are not part of the 2x3 objects */
			centerMap.setTile(centerTile.x, centerTile.y, 0 ); // new Tile(0, false, false, false, false));
//			int ulPaintType = LocalMap.getPaintType(ulMap.getTile(ulTile.x, ulTile.y));
			ulMap.setTile(ulTile.x, ulTile.y, 0 ); // new Tile(0, false, false, false, false));
			ulMap.setDr(ulTile.x, ulTile.y, 0 );
//			int uPaintType = LocalMap.getPaintType(uMap.getTile(uTile.x, uTile.y));
			uMap.setTile(uTile.x, uTile.y, 0 ); // new Tile(0, false, false, false, false));
			uMap.setDl(uTile.x, uTile.y, 0 );
			uMap.setDr(uTile.x, uTile.y, 0 );
//			int urPaintType = LocalMap.getPaintType(urMap.getTile(urTile.x, urTile.y));
			urMap.setTile(urTile.x, urTile.y, 0 ); // new Tile(0, false, false, false, false));
			urMap.setDl(urTile.x, urTile.y, 0 );
			int lPaintType = LocalMap.getPaintType(lMap.getTile(lTile.x, lTile.y));
			lMap.setTile(lTile.x, lTile.y, 0 ); // new Tile(0, false, false, false, false));
			lMap.setUr(lTile.x, lTile.y, 0 );
			lMap.setDr(lTile.x, lTile.y, 0 );
			int rPaintType = LocalMap.getPaintType(rMap.getTile(rTile.x, rTile.y));
			rMap.setTile(rTile.x, rTile.y, 0 ); // new Tile(0, false, false, false, false));
			rMap.setUl(rTile.x, rTile.y, 0 );
			rMap.setDl(rTile.x, rTile.y, 0 );
			
			/** and adjust the surrounding */
			adjustSurrounding(lMap, lTile.x, lTile.y, lPaintType, tileDim);
			adjustSurrounding(rMap, rTile.x, rTile.y, rPaintType, tileDim);
			break;
		}
		case RowCol2x2: {
			/** delete all tiles in environment **/
			/** here the down tiles are missing because they are not part of the 2x3 objects */
			centerMap.setTile(centerTile.x, centerTile.y, 0 ); // new Tile(0, false, false, false, false));
//			int ulPaintType = LocalMap.getPaintType(ulMap.getTile(ulTile.x, ulTile.y));
//			ulMap.setTile(ulTile.x, ulTile.y, 0 ); // new Tile(0, false, false, false, false));
//			ulMap.setDr(ulTile.x, ulTile.y, 0 );
//			int uPaintType = LocalMap.getPaintType(uMap.getTile(uTile.x, uTile.y));
			uMap.setTile(uTile.x, uTile.y, 0 ); // new Tile(0, false, false, false, false));
			uMap.setDl(uTile.x, uTile.y, 0 );
			uMap.setDr(uTile.x, uTile.y, 0 );
//			int urPaintType = LocalMap.getPaintType(urMap.getTile(urTile.x, urTile.y));
			urMap.setTile(urTile.x, urTile.y, 0 ); // new Tile(0, false, false, false, false));
			urMap.setDl(urTile.x, urTile.y, 0 );
//			int lPaintType = LocalMap.getPaintType(lMap.getTile(lTile.x, lTile.y));
//			lMap.setTile(lTile.x, lTile.y, 0 ); // new Tile(0, false, false, false, false));
//			lMap.setUr(lTile.x, lTile.y, 0 );
//			lMap.setDr(lTile.x, lTile.y, 0 );
			int rPaintType = LocalMap.getPaintType(rMap.getTile(rTile.x, rTile.y));
			rMap.setTile(rTile.x, rTile.y, 0 ); // new Tile(0, false, false, false, false));
			rMap.setUl(rTile.x, rTile.y, 0 );
			rMap.setDl(rTile.x, rTile.y, 0 );
			
			/** and adjust the surrounding */
//			adjustSurrounding(lMap, lTile.x, lTile.y, lPaintType, tileDim);
			adjustSurrounding(rMap, rTile.x, rTile.y, rPaintType, tileDim);
			break;
		}
		case RowCol1x1: {
			/** delete all tiles in environment **/
			/** here the down tiles are missing because they are not part of the 2x3 objects */
			centerMap.setTile(centerTile.x, centerTile.y, 0 ); // new Tile(0, false, false, false, false));
			break;
		}
		case Fluid: {
			/** delete all tiles in environment **/
			/** here the down tiles are missing because they are not part of the 2x3 objects */
			centerMap.setTile(centerTile.x, centerTile.y, 0 ); // new Tile(0, false, false, false, false));
			adjustSurrounding(centerMap, centerTile.x, centerTile.y, LocalMap.getPaintType(centerMap.getTile(centerTile.x, centerTile.y)), tileDim);
			break;
		}
		default: {
			break;
		}
		}
		
//		/** delete all tiles in environment **/
//		centerMap.setTile(centerTile.x, centerTile.y, 0 ); // new Tile(0, false, false, false, false));
//		int ulPaintType = LocalMap.getPaintType(ulMap.getTile(ulTile.x, ulTile.y));
//		ulMap.setTile(ulTile.x, ulTile.y, 0 ); // new Tile(0, false, false, false, false));
//		ulMap.setDr(ulTile.x, ulTile.y, 0 );
//		int uPaintType = LocalMap.getPaintType(uMap.getTile(uTile.x, uTile.y));
//		uMap.setTile(uTile.x, uTile.y, 0 ); // new Tile(0, false, false, false, false));
//		uMap.setDl(uTile.x, uTile.y, 0 );
//		uMap.setDr(uTile.x, uTile.y, 0 );
//		int urPaintType = LocalMap.getPaintType(urMap.getTile(urTile.x, urTile.y));
//		urMap.setTile(urTile.x, urTile.y, 0 ); // new Tile(0, false, false, false, false));
//		urMap.setDl(urTile.x, urTile.y, 0 );
//		int lPaintType = LocalMap.getPaintType(lMap.getTile(lTile.x, lTile.y));
//		lMap.setTile(lTile.x, lTile.y, 0 ); // new Tile(0, false, false, false, false));
//		lMap.setUr(lTile.x, lTile.y, 0 );
//		lMap.setDr(lTile.x, lTile.y, 0 );
//		int rPaintType = LocalMap.getPaintType(rMap.getTile(rTile.x, rTile.y));
//		rMap.setTile(rTile.x, rTile.y, 0 ); // new Tile(0, false, false, false, false));
//		rMap.setUl(rTile.x, rTile.y, 0 );
//		rMap.setDl(rTile.x, rTile.y, 0 );
//		int dlPaintType = LocalMap.getPaintType(dlMap.getTile(dlTile.x, dlTile.y));
//		dlMap.setTile(dlTile.x, dlTile.y, 0 ); // new Tile(0, false, false, false, false));
//		dlMap.setUr(dlTile.x, dlTile.y, 0 );
//		int dPaintType = LocalMap.getPaintType(dMap.getTile(dTile.x, dTile.y));
//		dMap.setTile(dTile.x, dTile.y, 0 ); // new Tile(0, false, false, false, false));
//		dMap.setUr(dTile.x, dTile.y, 0 );
//		dMap.setUl(dTile.x, dTile.y, 0 );
//		int drPaintType = LocalMap.getPaintType(drMap.getTile(drTile.x, drTile.y));
//		drMap.setTile(drTile.x, drTile.y, 0 ); // new Tile(0, false, false, false, false));
//		drMap.setUl(drTile.x, drTile.y, 0 );
//		
//		/** and adjust the surrounding */
//		adjustSurrounding(uMap, uTile.x, uTile.y, uPaintType, tileDim);
//		adjustSurrounding(lMap, lTile.x, lTile.y, lPaintType, tileDim);
//		adjustSurrounding(rMap, rTile.x, rTile.y, rPaintType, tileDim);
//		adjustSurrounding(dMap, dTile.x, dTile.y, dPaintType, tileDim);
//		
//		adjustSurrounding(ulMap, ulTile.x, ulTile.y, ulPaintType, tileDim);
//		adjustSurrounding(urMap, urTile.x, urTile.y, urPaintType, tileDim);		
//		adjustSurrounding(dlMap, dlTile.x, dlTile.y, dlPaintType, tileDim);
//		adjustSurrounding(drMap, drTile.x, drTile.y, drPaintType, tileDim);
	}
	
	
	
	
	
	public void setTileCorners(LocalMap localMap, int localX, int localY, int paintType, TileDimensions tileDimensions) throws NullPointerException {
		if (localMap != null) {
		Point origin = localMap.getOrigin();
		LocalMap ulMap;
		LocalMap uMap;
		LocalMap urMap;
		LocalMap lMap;
		LocalMap rMap;
		LocalMap centerMap;
		LocalMap dlMap;
		LocalMap dMap;
		LocalMap drMap;
		Point ulTile;
		Point uTile;
		Point urTile;
		Point lTile;
		Point rTile;
		Point centerTile;
		Point dlTile;
		Point dTile;
		Point drTile;
		
		int maxIndexX = GameStates.mapWidth-1;
		int maxIndexY = GameStates.mapHeight-1;
		
		if (localX <= 0) {
			if (localX == 0) {
				if (localY <= 0) {
					if (localY == 0) {
						// 1
						ulMap = createMapIfNonExistent(getUpperLeftMap(origin));
						uMap = urMap = createMapIfNonExistent(getUpperMap(origin));
						lMap = dlMap = createMapIfNonExistent(getLeftMap(origin));
						centerMap = dMap = drMap = rMap = localMap;
						ulTile = new Point(maxIndexX, maxIndexY);
						uTile = new Point(0, maxIndexY);
						urTile = new Point(1, maxIndexY);
						lTile = new Point(maxIndexX, 0);
						centerTile = new Point(0, 0);
						rTile = new Point(1, 0);
						dlTile = new Point(maxIndexX, 1);
						dTile = new Point(0, 1);
						drTile = new Point(1, 1);
					} else {
						// 2
						ulMap = lMap = createMapIfNonExistent(getUpperLeftMap(origin));
						uMap = urMap = centerMap = rMap = createMapIfNonExistent(getUpperMap(origin));
						dlMap = createMapIfNonExistent(getLeftMap(origin));
						dMap = drMap = localMap;
						ulTile = new Point(maxIndexX, maxIndexY-1);
						uTile = new Point(0, maxIndexY-1);
						urTile = new Point(1, maxIndexY-1);
						lTile = new Point(maxIndexX, maxIndexY);
						centerTile = new Point(0, maxIndexY);
						rTile = new Point(1, maxIndexY);
						dlTile = new Point(maxIndexX, 0);
						dTile = new Point(0, 0);
						drTile = new Point(1, 0);
					}
				} else if (localY >= GameStates.mapHeight-1) {
					if (localY == GameStates.mapHeight-1) {
						// 3
						ulMap = lMap = createMapIfNonExistent(getLeftMap(origin));
						uMap = urMap = centerMap = rMap = localMap;
						dlMap = createMapIfNonExistent(getLowerLeftMap(origin));
						dMap = drMap = createMapIfNonExistent(getLowerMap(origin));
						ulTile = new Point(maxIndexX, maxIndexY-1);
						uTile = new Point(0, maxIndexY-1);
						urTile = new Point(1, maxIndexY-1);
						lTile = new Point(maxIndexX, maxIndexY);
						centerTile = new Point(0, maxIndexY);
						rTile = new Point(1, maxIndexY);
						dlTile = new Point(maxIndexX, 0);
						dTile = new Point(0, 0);
						drTile = new Point(1, 0);
					} else {
						// 4
						ulMap = createMapIfNonExistent(getLeftMap(origin));
						uMap = urMap = localMap;
						lMap = dlMap = createMapIfNonExistent(getLowerLeftMap(origin));
						centerMap = rMap = dMap = drMap = createMapIfNonExistent(getLowerMap(origin));
						ulTile = new Point(maxIndexX, maxIndexY);
						uTile = new Point(0, maxIndexY);
						urTile = new Point(1, maxIndexY);
						lTile = new Point(maxIndexX, 0);
						centerTile = new Point(0, 0);
						rTile = new Point(1, 0);
						dlTile = new Point(maxIndexX, 1);
						dTile = new Point(0, 1);
						drTile = new Point(1, 1);
					}
				} else {
					// 5
					ulMap = lMap = dlMap = createMapIfNonExistent(getLeftMap(origin));
					uMap = urMap = centerMap = rMap = dMap = drMap = localMap;
					ulTile = new Point(maxIndexX, localY-1);
					uTile = new Point(0, localY-1);
					urTile = new Point(1, localY-1);
					lTile = new Point(maxIndexX, localY);
					centerTile = new Point(0, localY);
					rTile = new Point(1, localY);
					dlTile = new Point(maxIndexX, localY+1);
					dTile = new Point(0, localY+1);
					drTile = new Point(1, localY+1);
				}
			} else {
				if (localY <= 0) {
					if (localY == 0) {
						// 6
						ulMap = uMap = createMapIfNonExistent(getUpperLeftMap(origin));
						urMap = createMapIfNonExistent(getUpperMap(origin));
						lMap = centerMap = dlMap = dMap = createMapIfNonExistent(getLeftMap(origin));
						rMap = drMap = localMap;
						ulTile = new Point(maxIndexX-1, maxIndexY);
						uTile = new Point(maxIndexX, maxIndexY);
						urTile = new Point(0, maxIndexY);
						lTile = new Point(maxIndexX-1, 0);
						centerTile = new Point(maxIndexX, 0);
						rTile = new Point(0, 0);
						dlTile = new Point(maxIndexX-1, 1);
						dTile = new Point(maxIndexX, 1);
						drTile = new Point(0, 1);
					} else {
						// 7
						ulMap = uMap = lMap = centerMap = createMapIfNonExistent(getUpperLeftMap(origin));
						urMap = rMap = createMapIfNonExistent(getUpperMap(origin));
						dlMap = dMap = createMapIfNonExistent(getLeftMap(origin));
						drMap = localMap;
						ulTile = new Point(maxIndexX-1, maxIndexY-1);
						uTile = new Point(maxIndexX, maxIndexY-1);
						urTile = new Point(0, maxIndexY-1);
						lTile = new Point(maxIndexX-1, maxIndexY);
						centerTile = new Point(maxIndexX, maxIndexY);
						rTile = new Point(0, maxIndexY);
						dlTile = new Point(maxIndexX-1, 0);
						dTile = new Point(maxIndexX, 0);
						drTile = new Point(0, 0);
					}
				} else if (localY >= GameStates.mapHeight-1) {
					if (localY == GameStates.mapHeight-1) {
						// 8
						ulMap = uMap = lMap = centerMap = createMapIfNonExistent(getLeftMap(origin));
						urMap = rMap = localMap;
						dlMap = dMap = createMapIfNonExistent(getLowerLeftMap(origin));
						drMap = createMapIfNonExistent(getLowerMap(origin));
						ulTile = new Point(maxIndexX-1, maxIndexY-1);
						uTile = new Point(maxIndexX, maxIndexY-1);
						urTile = new Point(0, maxIndexY-1);
						lTile = new Point(maxIndexX-1, maxIndexY);
						centerTile = new Point(maxIndexX, maxIndexY);
						rTile = new Point(0, maxIndexY);
						dlTile = new Point(maxIndexX-1, 0);
						dTile = new Point(maxIndexX, 0);
						drTile = new Point(0, 0);
					} else {
						// 9
						ulMap = uMap = createMapIfNonExistent(getLeftMap(origin));
						urMap = localMap;
						lMap = centerMap = dlMap = dMap = createMapIfNonExistent(getLowerLeftMap(origin));
						rMap = drMap = createMapIfNonExistent(getLowerMap(origin));
						ulTile = new Point(maxIndexX-1, maxIndexY);
						uTile = new Point(maxIndexX, maxIndexY);
						urTile = new Point(0, maxIndexY);
						lTile = new Point(maxIndexX-1, 0);
						centerTile = new Point(maxIndexX, 0);
						rTile = new Point(0, 0);
						dlTile = new Point(maxIndexX-1, 1);
						dTile = new Point(maxIndexX, 1);
						drTile = new Point(0, 1);
					}
				} else {
					// 10
					ulMap = uMap = lMap = centerMap = dlMap = dMap = createMapIfNonExistent(getLeftMap(origin));
					urMap = rMap = drMap = localMap;
					ulTile = new Point(maxIndexX-1, localY-1);
					uTile = new Point(maxIndexX, localY-1);
					urTile = new Point(0, localY-1);
					lTile = new Point(maxIndexX-1, localY);
					centerTile = new Point(maxIndexX, localY);
					rTile = new Point(0, localY);
					dlTile = new Point(maxIndexX-1, localY+1);
					dTile = new Point(maxIndexX, localY+1);
					drTile = new Point(0, localY+1);
				}
			}
		} else if (localX >= GameStates.mapWidth-1) {
			if (localX == GameStates.mapWidth-1) {
				if (localY <= 0) {
					if (localY == 0) {
						// 11
						ulMap = uMap = createMapIfNonExistent(getUpperMap(origin));
						urMap = createMapIfNonExistent(getUpperRightMap(origin));
						lMap = centerMap = dlMap = dMap = localMap;
						rMap = drMap = createMapIfNonExistent(getRightMap(origin));
						ulTile = new Point(maxIndexX-1, maxIndexY);
						uTile = new Point(maxIndexX, maxIndexY);
						urTile = new Point(0, maxIndexY);
						lTile = new Point(maxIndexX-1, 0);
						centerTile = new Point(maxIndexX, 0);
						rTile = new Point(0, 0);
						dlTile = new Point(maxIndexX-1, 1);
						dTile = new Point(maxIndexX, 1);
						drTile = new Point(0, 1);
					} else {
						// 12
						ulMap = uMap = lMap = centerMap = createMapIfNonExistent(getUpperMap(origin));
						urMap = rMap = createMapIfNonExistent(getUpperRightMap(origin));
						dlMap = dMap = localMap;
						drMap = createMapIfNonExistent(getLeftMap(origin));
						ulTile = new Point(maxIndexX-1, maxIndexY-1);
						uTile = new Point(maxIndexX, maxIndexY-1);
						urTile = new Point(0, maxIndexY-1);
						lTile = new Point(maxIndexX-1, maxIndexY);
						centerTile = new Point(maxIndexX, maxIndexY);
						rTile = new Point(0, maxIndexY);
						dlTile = new Point(maxIndexX-1, 0);
						dTile = new Point(maxIndexX, 0);
						drTile = new Point(0, 0);
					}
				} else if (localY >= GameStates.mapHeight-1) {
					if (localY == GameStates.mapHeight-1) {
						// 13
						ulMap = uMap = lMap = centerMap = localMap;
						urMap = rMap = createMapIfNonExistent(getRightMap(origin));
						dlMap = dMap = createMapIfNonExistent(getLowerMap(origin));
						drMap = createMapIfNonExistent(getLowerRightMap(origin));
						ulTile = new Point(maxIndexX-1, maxIndexY-1);
						uTile = new Point(maxIndexX, maxIndexY-1);
						urTile = new Point(0, maxIndexY-1);
						lTile = new Point(maxIndexX-1, maxIndexY);
						centerTile = new Point(maxIndexX, maxIndexY);
						rTile = new Point(0, maxIndexY);
						dlTile = new Point(maxIndexX-1, 0);
						dTile = new Point(maxIndexX, 0);
						drTile = new Point(0, 0);
					} else {
						// 14
						ulMap = uMap = localMap;
						urMap = createMapIfNonExistent(getRightMap(origin));
						lMap = centerMap = dlMap = dMap = createMapIfNonExistent(getLowerMap(origin));
						rMap = drMap = createMapIfNonExistent(getLowerRightMap(origin));
						ulTile = new Point(maxIndexX-1, maxIndexY);
						uTile = new Point(maxIndexX, maxIndexY);
						urTile = new Point(0, maxIndexY);
						lTile = new Point(maxIndexX-1, 0);
						centerTile = new Point(maxIndexX, 0);
						rTile = new Point(0, 0);
						dlTile = new Point(maxIndexX-1, 1);
						dTile = new Point(maxIndexX, 1);
						drTile = new Point(0, 1);
					}
				} else {
					// 15
					ulMap = uMap = lMap = centerMap = dlMap = dMap = localMap;
					urMap = rMap = drMap = createMapIfNonExistent(getRightMap(origin));
					ulTile = new Point(maxIndexX-1, localY-1);
					uTile = new Point(maxIndexX, localY-1);
					urTile = new Point(0, localY-1);
					lTile = new Point(maxIndexX-1, localY);
					centerTile = new Point(maxIndexX, localY);
					rTile = new Point(0, localY);
					dlTile = new Point(maxIndexX-1, localY+1);
					dTile = new Point(maxIndexX, localY+1);
					drTile = new Point(0, localY+1);
				}
			} else {
				if (localY <= 0) {
					if (localY == 0) {
						// 16
						ulMap = createMapIfNonExistent(getUpperMap(origin));
						uMap = urMap = createMapIfNonExistent(getUpperRightMap(origin));
						lMap = dlMap = localMap;
						centerMap = rMap = dMap = drMap = createMapIfNonExistent(getRightMap(origin));
						ulTile = new Point(maxIndexX, maxIndexY);
						uTile = new Point(0, maxIndexY);
						urTile = new Point(1, maxIndexY);
						lTile = new Point(maxIndexX, 0);
						centerTile = new Point(0, 0);
						rTile = new Point(1, 0);
						dlTile = new Point(maxIndexX, 1);
						dTile = new Point(0, 1);
						drTile = new Point(1, 1);
					} else {
						// 17
						ulMap = lMap = createMapIfNonExistent(getUpperMap(origin));
						uMap = urMap = centerMap = rMap = createMapIfNonExistent(getUpperRightMap(origin));
						dlMap = localMap;
						dMap = drMap = createMapIfNonExistent(getRightMap(origin));
						ulTile = new Point(maxIndexX, maxIndexY-1);
						uTile = new Point(0, maxIndexY-1);
						urTile = new Point(1, maxIndexY-1);
						lTile = new Point(maxIndexX, maxIndexY);
						centerTile = new Point(0, maxIndexY);
						rTile = new Point(1, maxIndexY);
						dlTile = new Point(maxIndexX, 0);
						dTile = new Point(0, 0);
						drTile = new Point(1, 0);
					}
				} else if (localY >= GameStates.mapHeight-1) {
					if (localY == GameStates.mapHeight-1) {
						// 18
						ulMap = lMap = localMap;
						uMap = urMap = centerMap = rMap = createMapIfNonExistent(getRightMap(origin));
						dlMap = createMapIfNonExistent(getLowerMap(origin));
						dMap = drMap = createMapIfNonExistent(getLowerRightMap(origin));
						ulTile = new Point(maxIndexX, maxIndexY-1);
						uTile = new Point(0, maxIndexY-1);
						urTile = new Point(1, maxIndexY-1);
						lTile = new Point(maxIndexX, maxIndexY);
						centerTile = new Point(0, maxIndexY);
						rTile = new Point(1, maxIndexY);
						dlTile = new Point(maxIndexX, 0);
						dTile = new Point(0, 0);
						drTile = new Point(1, 0);
					} else {
						// 19
						ulMap = localMap;
						uMap = urMap = createMapIfNonExistent(getRightMap(origin));
						lMap = dlMap = createMapIfNonExistent(getLowerMap(origin));
						centerMap = rMap = dMap = drMap = createMapIfNonExistent(getLowerRightMap(origin));
						ulTile = new Point(maxIndexX, maxIndexY);
						uTile = new Point(0, maxIndexY);
						urTile = new Point(1, maxIndexY);
						lTile = new Point(maxIndexX, 0);
						centerTile = new Point(0, 0);
						rTile = new Point(1, 0);
						dlTile = new Point(maxIndexX, 1);
						dTile = new Point(0, 1);
						drTile = new Point(1, 1);
					}
				} else {
					// 20
					ulMap = lMap = dlMap = localMap;
					uMap = urMap = centerMap = rMap = dMap = drMap = createMapIfNonExistent(getRightMap(origin)); 
					ulTile = new Point(maxIndexX, localY-1);
					uTile = new Point(0, localY-1);
					urTile = new Point(1, localY-1);
					lTile = new Point(maxIndexX, localY);
					centerTile = new Point(0, localY);
					rTile = new Point(1, localY);
					dlTile = new Point(maxIndexX, localY+1);
					dTile = new Point(0, localY+1);
					drTile = new Point(1, localY+1);
				}
			}
		} else {
			if (localY <= 0) {
				if (localY == 0) {
					// 21
					ulMap = uMap = urMap = createMapIfNonExistent(getUpperMap(origin));
					lMap = centerMap = rMap = dlMap = dMap = drMap = localMap;
					ulTile = new Point(localX-1, maxIndexY);
					uTile = new Point(localX, maxIndexY);
					urTile = new Point(localX+1, maxIndexY);
					lTile = new Point(localX-1, 0);
					centerTile = new Point(localX, 0);
					rTile = new Point(localX+1, 0);
					dlTile = new Point(localX-1, 1);
					dTile = new Point(localX, 1);
					drTile = new Point(localX+1, 1);
				} else {
					// 22
					ulMap = uMap = urMap = lMap = centerMap = rMap = createMapIfNonExistent(getUpperMap(origin));
					dlMap = dMap = drMap = localMap;
					ulTile = new Point(localX-1, maxIndexY-1);
					uTile = new Point(localX, maxIndexY-1);
					urTile = new Point(localX+1, maxIndexY-1);
					lTile = new Point(localX-1, maxIndexY);
					centerTile = new Point(localX, maxIndexY);
					rTile = new Point(localX+1, maxIndexY);
					dlTile = new Point(localX-1, 0);
					dTile = new Point(localX, 0);
					drTile = new Point(localX+1, 0);
				}
			} else if (localY >= GameStates.mapHeight-1) {
				if (localY == GameStates.mapHeight-1) {
					// 23
					ulMap = uMap = urMap = lMap = centerMap = rMap = localMap;
					dlMap = dMap = drMap = createMapIfNonExistent(getLowerMap(origin));
					ulTile = new Point(localX-1, maxIndexY-1);
					uTile = new Point(localX, maxIndexY-1);
					urTile = new Point(localX+1, maxIndexY-1);
					lTile = new Point(localX-1, maxIndexY);
					centerTile = new Point(localX, maxIndexY);
					rTile = new Point(localX+1, maxIndexY);
					dlTile = new Point(localX-1, 0);
					dTile = new Point(localX, 0);
					drTile = new Point(localX+1, 0);
				} else {
					// 24
					ulMap = uMap = urMap = localMap;
					lMap = centerMap = rMap = dlMap = dMap = drMap = createMapIfNonExistent(getLowerMap(origin));
					ulTile = new Point(localX-1, maxIndexY);
					uTile = new Point(localX, maxIndexY);
					urTile = new Point(localX+1, maxIndexY);
					lTile = new Point(localX-1, 0);
					centerTile = new Point(localX, 0);
					rTile = new Point(localX+1, 0);
					dlTile = new Point(localX-1, 1);
					dTile = new Point(localX, 1);
					drTile = new Point(localX+1, 1);
				}
			} else {
				// 25
				ulMap = uMap = urMap = lMap = centerMap = rMap = dlMap = dMap = drMap = localMap;
				ulTile = new Point(localX-1,localY-1);
				uTile = new Point(localX, localY-1);
				urTile = new Point(localX+1, localY-1);
				lTile = new Point(localX-1, localY);
				centerTile = new Point(localX, localY);
				rTile = new Point(localX+1, localY);
				dlTile = new Point(localX-1, localY+1);
				dTile = new Point(localX, localY+1);
				drTile = new Point(localX+1, localY+1);
			}
		}
	
//		boolean left = (lMap != null) ? LocalMap.getPaintType(lMap.getTile(lTile.x, lTile.y))== paintType : false;//( dlUr || dUl  || lDr );// && (dlTile.getId()!= 0);
//		boolean up = (uMap != null) ? LocalMap.getPaintType(uMap.getTile(uTile.x, uTile.y))== paintType : false;//(ulDr|| uDl || lUr );// && (ulTile.getId()!= 0);
//		boolean right = (rMap != null) ? LocalMap.getPaintType(rMap.getTile(rTile.x,  rTile.y))== paintType : false;//( urDl ||uDr || rUl);// && (urTile.getId()!= 0);
//		boolean down = (dMap!=null) ? LocalMap.getPaintType(dMap.getTile(dTile.x, dTile.y))== paintType : false;//( drUl||  dUr || rDl );// && (drTile.getId()!= 0);
//		
		
		
		boolean dlUr, dUl, lDr, drUl, dUr, rDl, urDl, uDr, rUl, ulDr, uDl, lUr;
		dlUr = (dlMap != null) ? LocalMap.getPaintType(dlMap.getUr(dlTile.x, dlTile.y)) == paintType : false;
		dUl = (dMap != null) ? LocalMap.getPaintType(dMap.getUl(dTile.x, dTile.y)) == paintType : false;
		lDr = (lMap != null) ? LocalMap.getPaintType(lMap.getDr(lTile.x, lTile.y)) == paintType : false;
		drUl = (drMap != null) ? LocalMap.getPaintType(drMap.getUl(drTile.x, drTile.y)) == paintType : false;
		dUr = (dMap != null) ? LocalMap.getPaintType(dMap.getUr(dTile.x, dTile.y)) == paintType : false;
		rDl = (rMap != null) ? LocalMap.getPaintType(rMap.getDl(rTile.x, rTile.y)) == paintType : false;
		urDl = (urMap != null) ? LocalMap.getPaintType(urMap.getDl(urTile.x, urTile.y)) == paintType : false;
		uDr = (uMap != null) ? LocalMap.getPaintType(uMap.getDr(uTile.x, uTile.y)) == paintType : false;
		rUl = (rMap != null) ? LocalMap.getPaintType(rMap.getUl(rTile.x, rTile.y)) == paintType : false;
		ulDr = (ulMap != null) ? LocalMap.getPaintType(ulMap.getDr(ulTile.x, ulTile.y)) == paintType : false;
		uDl = (uMap != null) ? LocalMap.getPaintType(uMap.getDl(uTile.x, uTile.y)) == paintType : false;
		lUr = (lMap != null) ? LocalMap.getPaintType(lMap.getUr(lTile.x, lTile.y)) == paintType : false;

//		if (dlMap != null) dlUr =  (dlMap.getUr(dlTile.x, dlTile.y) != 0); else dlUr = false;
//		if (dMap != null) dUl =  (dMap.getUl(dTile.x, dTile.y) != 0); else dUl = false;
//		if (lMap != null) lDr =  (lMap.getDr(lTile.x, lTile.y) != 0); else lDr = false;
//		if (drMap != null) drUl =  (drMap.getUl(drTile.x, drTile.y) != 0); else drUl = false;
//		if (dMap != null) dUr =  (dMap.getUr(dTile.x, dTile.y) != 0); else dUr = false;
//		if (rMap != null) rDl =  (rMap.getDl(rTile.x, rTile.y) != 0); else rDl = false;
//		if (urMap != null) urDl = (urMap.getDl(urTile.x, urTile.y) != 0) ; else urDl = false;
//		if (uMap != null) uDr = (uMap.getDr(uTile.x, uTile.y) != 0)  ; else uDr = false;
//		if (rMap != null) rUl = (rMap.getUl(rTile.x, rTile.y) != 0); else rUl = false;
//		if (ulMap != null) ulDr = (ulMap.getDr(ulTile.x, ulTile.y) != 0)  ; else ulDr = false;
//		if (uMap != null) uDl = (uMap.getDl(uTile.x, uTile.y) != 0) ; else uDl = false;
//		if (lMap != null) lUr = (lMap.getUr(lTile.x, lTile.y) != 0); else lUr = false;
		
//		if (dlMap != null) dlUr =  (dlMap.getUr(dlTile.x, dlTile.y) != 0); else dlUr = false;
//		if (dMap != null) dUl =  (dMap.getUl(dTile.x, dTile.y) != 0); else dUl = false;
//		if (lMap != null) lDr =  (lMap.getDr(lTile.x, lTile.y) != 0); else lDr = false;
//		if (drMap != null) drUl =  (drMap.getUl(drTile.x, drTile.y) != 0); else drUl = false;
//		if (dMap != null) dUr =  (dMap.getUr(dTile.x, dTile.y) != 0); else dUr = false;
//		if (rMap != null) rDl =  (rMap.getDl(rTile.x, rTile.y) != 0); else rDl = false;
//		if (urMap != null) urDl = (urMap.getDl(urTile.x, urTile.y) != 0) ; else urDl = false;
//		if (uMap != null) uDr = (uMap.getDr(uTile.x, uTile.y) != 0)  ; else uDr = false;
//		if (rMap != null) rUl = (rMap.getUl(rTile.x, rTile.y) != 0); else rUl = false;
//		if (ulMap != null) ulDr = (ulMap.getDr(ulTile.x, ulTile.y) != 0)  ; else ulDr = false;
//		if (uMap != null) uDl = (uMap.getDl(uTile.x, uTile.y) != 0) ; else uDl = false;
//		if (lMap != null) lUr = (lMap.getUr(lTile.x, lTile.y) != 0); else lUr = false;
		
		
		boolean dl = ( dlUr || dUl  || lDr );// && (dlTile.getId()!= 0);
		boolean dr = ( drUl||  dUr || rDl );// && (drTile.getId()!= 0);
		boolean ur = ( urDl ||uDr || rUl);// && (urTile.getId()!= 0);
		boolean ul = (ulDr|| uDl || lUr );// && (ulTile.getId()!= 0);
		
//		boolean dlUr, dUl, lDr, drUl, dUr, rDl, rdDl, uDr, rUl, ulDr, uDl, lUr;
//		if (dlMap != null) dlUr =  (dlMap.getUr(dlTile.x, dlTile.y) != 0)
//		
//		boolean dl = ( dlUr || (dMap.getUl(dTile.x, dTile.y) != 0) || (lMap.getDr(lTile.x, lTile.y) != 0) );// && (dlTile.getId()!= 0);
//		boolean dr = ((drMap.getUl(drTile.x, drTile.y) != 0) || (dMap.getUr(dTile.x, dTile.y) != 0) || (rMap.getDl(rTile.x, rTile.y) != 0) );// && (drTile.getId()!= 0);
//		boolean ur = ((urMap.getDl(urTile.x, urTile.y) != 0) || (uMap.getDr(uTile.x, uTile.y) != 0) || (rMap.getUl(rTile.x, rTile.y) != 0) );// && (urTile.getId()!= 0);
//		boolean ul = ((ulMap.getDr(ulTile.x, ulTile.y) != 0) || (uMap.getDl(uTile.x, uTile.y) != 0) || (lMap.getUr(lTile.x, lTile.y) != 0) );// && (ulTile.getId()!= 0);
		
		
//		boolean dl = ((dlMap.getUr(dlTile.x, dlTile.y) != -1) || dTile.hasUl() || lTile.hasDr());// && (dlTile.getId()!= 0);
//		boolean dr = (drTile.hasUl() || dTile.hasUr() || rTile.hasDl());// && (drTile.getId()!= 0);
//		boolean ur = (urTile.hasDl() || uTile.hasDr() || rTile.hasUl());// && (urTile.getId()!= 0);
//		boolean ul = (ulTile.hasDr() || uTile.hasDl() || lTile.hasUr());// && (ulTile.getId()!= 0);
		
//		Tile tile = new Tile(0,ul, ur, dl, dr);
//		tile.setIdByCorners();
		
		if (LocalMap.getPaintType(centerMap.getTile(centerTile.x, centerTile.y)) == paintType || LocalMap.getPaintType(centerMap.getTile(centerTile.x, centerTile.y)) ==0 ) {
			if (dl)	{
				centerMap.setDl(centerTile.x, centerTile.y, paintType);
	//			System.out.println("dl "+centerTile.x+" "+centerTile.y);
			}
			else centerMap.setDl(centerTile.x, centerTile.y, 0);
			if (dr)	{
				centerMap.setDr(centerTile.x, centerTile.y, paintType);
	//			System.out.println("dr "+centerTile.x+" "+centerTile.y);
			}
			else centerMap.setDr(centerTile.x, centerTile.y, 0);
			if (ul)	{
				centerMap.setUl(centerTile.x, centerTile.y, paintType);
	//			System.out.println("ul "+centerTile.x+" "+centerTile.y);
			}
			else centerMap.setUl(centerTile.x, centerTile.y, 0);
			if (ur)	{
				centerMap.setUr(centerTile.x, centerTile.y, paintType);
	//			System.out.println("ur "+centerTile.x+" "+centerTile.y);
			}
			else centerMap.setUr(centerTile.x, centerTile.y, 0);
			
	//		System.out.println("DR: "+centerMap.getDr(centerTile.x, centerTile.y));
			centerMap.setIdByCornersObject(centerTile.x, centerTile.y, paintType, tileDimensions);
	//		if (tile.getId() == 0) tile = null;
	//		centerMap.setTile(centerTile.x, centerTile.y, tile);
		}
		} else {
			throw new NullPointerException();
		}
	}
	
	
	/** set fluid corners */
	public void setFluidCorners(LocalMap localMap, int localX, int localY, int paintType, TileDimensions tileDimensions) throws NullPointerException {
		if (localMap != null) {
		Point origin = localMap.getOrigin();
		LocalMap uMap;
		LocalMap lMap;
		LocalMap rMap;
		LocalMap centerMap;
		LocalMap dMap;
		Point uTile;
		Point lTile;
		Point rTile;
		Point centerTile;
		Point dTile;
		
		int maxIndexX = GameStates.mapWidth-1;
		int maxIndexY = GameStates.mapHeight-1;
		
		if (localX <= 0) {
			if (localX == 0) {
				if (localY <= 0) {
					if (localY == 0) {
						// 1
						uMap = createMapIfNonExistent(getUpperMap(origin));
						lMap = createMapIfNonExistent(getLeftMap(origin));
						centerMap = dMap = rMap = localMap;
						uTile = new Point(0, maxIndexY);
						lTile = new Point(maxIndexX, 0);
						centerTile = new Point(0, 0);
						rTile = new Point(1, 0);
						dTile = new Point(0, 1);
					} else {
						// 2
						lMap = createMapIfNonExistent(getUpperLeftMap(origin));
						uMap = centerMap = rMap = createMapIfNonExistent(getUpperMap(origin));
						dMap = localMap;
						uTile = new Point(0, maxIndexY-1);
						lTile = new Point(maxIndexX, maxIndexY);
						centerTile = new Point(0, maxIndexY);
						rTile = new Point(1, maxIndexY);
						dTile = new Point(0, 0);
					}
				} else if (localY >= GameStates.mapHeight-1) {
					if (localY == GameStates.mapHeight-1) {
						// 3
						lMap = createMapIfNonExistent(getLeftMap(origin));
						uMap = centerMap = rMap = localMap;
						dMap = createMapIfNonExistent(getLowerMap(origin));
						uTile = new Point(0, maxIndexY-1);
						lTile = new Point(maxIndexX, maxIndexY);
						centerTile = new Point(0, maxIndexY);
						rTile = new Point(1, maxIndexY);
						dTile = new Point(0, 0);
					} else {
						// 4
						uMap = localMap;
						lMap = createMapIfNonExistent(getLowerLeftMap(origin));
						centerMap = rMap = dMap = createMapIfNonExistent(getLowerMap(origin));
						uTile = new Point(0, maxIndexY);
						lTile = new Point(maxIndexX, 0);
						centerTile = new Point(0, 0);
						rTile = new Point(1, 0);
						dTile = new Point(0, 1);
					}
				} else {
					// 5
					lMap = createMapIfNonExistent(getLeftMap(origin));
					uMap = centerMap = rMap = dMap = localMap;
					uTile = new Point(0, localY-1);
					lTile = new Point(maxIndexX, localY);
					centerTile = new Point(0, localY);
					rTile = new Point(1, localY);
					dTile = new Point(0, localY+1);
				}
			} else {
				if (localY <= 0) {
					if (localY == 0) {
						// 6
						uMap = createMapIfNonExistent(getUpperLeftMap(origin));
						lMap = centerMap = dMap = createMapIfNonExistent(getLeftMap(origin));
						rMap = localMap;
						uTile = new Point(maxIndexX, maxIndexY);
						lTile = new Point(maxIndexX-1, 0);
						centerTile = new Point(maxIndexX, 0);
						rTile = new Point(0, 0);
						dTile = new Point(maxIndexX, 1);
					} else {
						// 7
						uMap = lMap = centerMap = createMapIfNonExistent(getUpperLeftMap(origin));
						rMap = createMapIfNonExistent(getUpperMap(origin));
						dMap = createMapIfNonExistent(getLeftMap(origin));
						uTile = new Point(maxIndexX, maxIndexY-1);
						lTile = new Point(maxIndexX-1, maxIndexY);
						centerTile = new Point(maxIndexX, maxIndexY);
						rTile = new Point(0, maxIndexY);
						dTile = new Point(maxIndexX, 0);
					}
				} else if (localY >= GameStates.mapHeight-1) {
					if (localY == GameStates.mapHeight-1) {
						// 8
						uMap = lMap = centerMap = createMapIfNonExistent(getLeftMap(origin));
						rMap = localMap;
						dMap = createMapIfNonExistent(getLowerLeftMap(origin));
						uTile = new Point(maxIndexX, maxIndexY-1);
						lTile = new Point(maxIndexX-1, maxIndexY);
						centerTile = new Point(maxIndexX, maxIndexY);
						rTile = new Point(0, maxIndexY);
						dTile = new Point(maxIndexX, 0);
					} else {
						// 9
						uMap = createMapIfNonExistent(getLeftMap(origin));
						lMap = centerMap = dMap = createMapIfNonExistent(getLowerLeftMap(origin));
						rMap = createMapIfNonExistent(getLowerMap(origin));
						uTile = new Point(maxIndexX, maxIndexY);
						lTile = new Point(maxIndexX-1, 0);
						centerTile = new Point(maxIndexX, 0);
						rTile = new Point(0, 0);
						dTile = new Point(maxIndexX, 1);
					}
				} else {
					// 10
					uMap = lMap = centerMap = dMap = createMapIfNonExistent(getLeftMap(origin));
					rMap = localMap;
					uTile = new Point(maxIndexX, localY-1);
					lTile = new Point(maxIndexX-1, localY);
					centerTile = new Point(maxIndexX, localY);
					rTile = new Point(0, localY);
					dTile = new Point(maxIndexX, localY+1);
				}
			}
		} else if (localX >= GameStates.mapWidth-1) {
			if (localX == GameStates.mapWidth-1) {
				if (localY <= 0) {
					if (localY == 0) {
						// 11
						uMap = createMapIfNonExistent(getUpperMap(origin));
						lMap = centerMap = dMap = localMap;
						rMap = createMapIfNonExistent(getRightMap(origin));
						uTile = new Point(maxIndexX, maxIndexY);
						lTile = new Point(maxIndexX-1, 0);
						centerTile = new Point(maxIndexX, 0);
						rTile = new Point(0, 0);
						dTile = new Point(maxIndexX, 1);
					} else {
						// 12
						uMap = lMap = centerMap = createMapIfNonExistent(getUpperMap(origin));
						rMap = createMapIfNonExistent(getUpperRightMap(origin));
						dMap = localMap;
						uTile = new Point(maxIndexX, maxIndexY-1);
						lTile = new Point(maxIndexX-1, maxIndexY);
						centerTile = new Point(maxIndexX, maxIndexY);
						rTile = new Point(0, maxIndexY);
						dTile = new Point(maxIndexX, 0);
					}
				} else if (localY >= GameStates.mapHeight-1) {
					if (localY == GameStates.mapHeight-1) {
						// 13
						uMap = lMap = centerMap = localMap;
						rMap = createMapIfNonExistent(getRightMap(origin));
						dMap = createMapIfNonExistent(getLowerMap(origin));
						uTile = new Point(maxIndexX, maxIndexY-1);
						lTile = new Point(maxIndexX-1, maxIndexY);
						centerTile = new Point(maxIndexX, maxIndexY);
						rTile = new Point(0, maxIndexY);
						dTile = new Point(maxIndexX, 0);
					} else {
						// 14
						uMap = localMap;
						lMap = centerMap = dMap = createMapIfNonExistent(getLowerMap(origin));
						rMap = createMapIfNonExistent(getLowerRightMap(origin));
						uTile = new Point(maxIndexX, maxIndexY);
						lTile = new Point(maxIndexX-1, 0);
						centerTile = new Point(maxIndexX, 0);
						rTile = new Point(0, 0);
						dTile = new Point(maxIndexX, 1);
					}
				} else {
					// 15
					uMap = lMap = centerMap = dMap = localMap;
					rMap = createMapIfNonExistent(getRightMap(origin));
					uTile = new Point(maxIndexX, localY-1);
					lTile = new Point(maxIndexX-1, localY);
					centerTile = new Point(maxIndexX, localY);
					rTile = new Point(0, localY);
					dTile = new Point(maxIndexX, localY+1);
				}
			} else {
				if (localY <= 0) {
					if (localY == 0) {
						// 16
						uMap = createMapIfNonExistent(getUpperRightMap(origin));
						lMap = localMap;
						centerMap = rMap = dMap = createMapIfNonExistent(getRightMap(origin));
						uTile = new Point(0, maxIndexY);
						lTile = new Point(maxIndexX, 0);
						centerTile = new Point(0, 0);
						rTile = new Point(1, 0);
						dTile = new Point(0, 1);
					} else {
						// 17
						lMap = createMapIfNonExistent(getUpperMap(origin));
						uMap = centerMap = rMap = createMapIfNonExistent(getUpperRightMap(origin));
						dMap = createMapIfNonExistent(getRightMap(origin));
						uTile = new Point(0, maxIndexY-1);
						lTile = new Point(maxIndexX, maxIndexY);
						centerTile = new Point(0, maxIndexY);
						rTile = new Point(1, maxIndexY);
						dTile = new Point(0, 0);
					}
				} else if (localY >= GameStates.mapHeight-1) {
					if (localY == GameStates.mapHeight-1) {
						// 18
						lMap = localMap;
						uMap = centerMap = rMap = createMapIfNonExistent(getRightMap(origin));
						dMap = createMapIfNonExistent(getLowerRightMap(origin));
						uTile = new Point(0, maxIndexY-1);
						lTile = new Point(maxIndexX, maxIndexY);
						centerTile = new Point(0, maxIndexY);
						rTile = new Point(1, maxIndexY);
						dTile = new Point(0, 0);
					} else {
						// 19
						uMap = createMapIfNonExistent(getRightMap(origin));
						lMap = createMapIfNonExistent(getLowerMap(origin));
						centerMap = rMap = dMap = createMapIfNonExistent(getLowerRightMap(origin));
						uTile = new Point(0, maxIndexY);
						lTile = new Point(maxIndexX, 0);
						centerTile = new Point(0, 0);
						rTile = new Point(1, 0);
						dTile = new Point(0, 1);
					}
				} else {
					// 20
					lMap = localMap;
					uMap = centerMap = rMap = dMap = createMapIfNonExistent(getRightMap(origin)); 
					uTile = new Point(0, localY-1);
					lTile = new Point(maxIndexX, localY);
					centerTile = new Point(0, localY);
					rTile = new Point(1, localY);
					dTile = new Point(0, localY+1);
				}
			}
		} else {
			if (localY <= 0) {
				if (localY == 0) {
					// 21
					uMap = createMapIfNonExistent(getUpperMap(origin));
					lMap = centerMap = rMap = dMap = localMap;
					uTile = new Point(localX, maxIndexY);
					lTile = new Point(localX-1, 0);
					centerTile = new Point(localX, 0);
					rTile = new Point(localX+1, 0);
					dTile = new Point(localX, 1);
				} else {
					// 22
					uMap = lMap = centerMap = rMap = createMapIfNonExistent(getUpperMap(origin));
					dMap = localMap;
					uTile = new Point(localX, maxIndexY-1);
					lTile = new Point(localX-1, maxIndexY);
					centerTile = new Point(localX, maxIndexY);
					rTile = new Point(localX+1, maxIndexY);
					dTile = new Point(localX, 0);
				}
			} else if (localY >= GameStates.mapHeight-1) {
				if (localY == GameStates.mapHeight-1) {
					// 23
					uMap = lMap = centerMap = rMap = localMap;
					dMap = createMapIfNonExistent(getLowerMap(origin));
					uTile = new Point(localX, maxIndexY-1);
					lTile = new Point(localX-1, maxIndexY);
					centerTile = new Point(localX, maxIndexY);
					rTile = new Point(localX+1, maxIndexY);
					dTile = new Point(localX, 0);
				} else {
					// 24
					uMap = localMap;
					lMap = centerMap = rMap = dMap = createMapIfNonExistent(getLowerMap(origin));
					uTile = new Point(localX, maxIndexY);
					lTile = new Point(localX-1, 0);
					centerTile = new Point(localX, 0);
					rTile = new Point(localX+1, 0);
					dTile = new Point(localX, 1);
				}
			} else {
				// 25
				uMap = lMap = centerMap = rMap = dMap = localMap;
				uTile = new Point(localX, localY-1);
				lTile = new Point(localX-1, localY);
				centerTile = new Point(localX, localY);
				rTile = new Point(localX+1, localY);
				dTile = new Point(localX, localY+1);
			}
		}
	
//		boolean dlUr, dUl, lDr, drUl, dUr, rDl, urDl, uDr, rUl, ulDr, uDl, lUr;
//		if (dlMap != null) dlUr =  (dlMap.getUr(dlTile.x, dlTile.y) != 0); else dlUr = false;
//		if (dMap != null) dUl =  (dMap.getUl(dTile.x, dTile.y) != 0); else dUl = false;
//		if (lMap != null) lDr =  (lMap.getDr(lTile.x, lTile.y) != 0); else lDr = false;
//		if (drMap != null) drUl =  (drMap.getUl(drTile.x, drTile.y) != 0); else drUl = false;
//		if (dMap != null) dUr =  (dMap.getUr(dTile.x, dTile.y) != 0); else dUr = false;
//		if (rMap != null) rDl =  (rMap.getDl(rTile.x, rTile.y) != 0); else rDl = false;
//		if (urMap != null) urDl = (urMap.getDl(urTile.x, urTile.y) != 0) ; else urDl = false;
//		if (uMap != null) uDr = (uMap.getDr(uTile.x, uTile.y) != 0)  ; else uDr = false;
//		if (rMap != null) rUl = (rMap.getUl(rTile.x, rTile.y) != 0); else rUl = false;
//		if (ulMap != null) ulDr = (ulMap.getDr(ulTile.x, ulTile.y) != 0)  ; else ulDr = false;
//		if (uMap != null) uDl = (uMap.getDl(uTile.x, uTile.y) != 0) ; else uDl = false;
//		if (lMap != null) lUr = (lMap.getUr(lTile.x, lTile.y) != 0); else lUr = false;
		
		boolean left = (lMap != null) ? LocalMap.getPaintType(lMap.getTile(lTile.x, lTile.y))== paintType : false;//( dlUr || dUl  || lDr );// && (dlTile.getId()!= 0);
		boolean up = (uMap != null) ? LocalMap.getPaintType(uMap.getTile(uTile.x, uTile.y))== paintType : false;//(ulDr|| uDl || lUr );// && (ulTile.getId()!= 0);
		boolean right = (rMap != null) ? LocalMap.getPaintType(rMap.getTile(rTile.x,  rTile.y))== paintType : false;//( urDl ||uDr || rUl);// && (urTile.getId()!= 0);
		boolean down = (dMap!=null) ? LocalMap.getPaintType(dMap.getTile(dTile.x, dTile.y))== paintType : false;//( drUl||  dUr || rDl );// && (drTile.getId()!= 0);
		
//		centerMap.setIdByCornersObject(centerTile.x, centerTile.y, paintType, tileDimensions);
		if (LocalMap.getPaintType(centerMap.getTile(centerTile.x, centerTile.y)) == paintType) {
			if (left)	{
				centerMap.setDl(centerTile.x, centerTile.y, paintType);
//				System.out.println("dl "+centerTile.x+" "+centerTile.y);
			}
			else centerMap.setDl(centerTile.x, centerTile.y, 0);
			if (up)	{
				centerMap.setUl(centerTile.x, centerTile.y, paintType);
//				System.out.println("ul "+centerTile.x+" "+centerTile.y);
			}
			else centerMap.setUl(centerTile.x, centerTile.y, 0);
			if (right)	{
				centerMap.setUr(centerTile.x, centerTile.y, paintType);
//				System.out.println("ur "+centerTile.x+" "+centerTile.y);
			}
			else centerMap.setUr(centerTile.x, centerTile.y, 0);
			if (down)	{
				centerMap.setDr(centerTile.x, centerTile.y, paintType);
//				System.out.println("dr "+centerTile.x+" "+centerTile.y);
			}
			else centerMap.setDr(centerTile.x, centerTile.y, 0);
			
			/** set the correct int value */
			centerMap.setIdByCornersFluid(centerTile.x, centerTile.y, paintType);
			
					
		} else {
			/** nothing to do */
		}
		
		/** change addons */
		if (LocalMap.isFluidBottom(uMap.getTile(uTile.x, uTile.y))) {
			/** set as Addon */
			centerMap.setTile(centerTile.x, centerTile.y, uMap.getTile(uTile.x, uTile.y)+GameStates.tileSetWidth);
			
		} 
		/** if upper tile is nothing substantial AND this is an addon */
		if ((uMap.getTile(uTile.x, uTile.y) <=1 || LocalMap.isAddon(uMap.getTile(uTile.x, uTile.y))) && LocalMap.isAddon(centerMap.getTile(centerTile.x, centerTile.y)) ) {
			centerMap.setTile(centerTile.x, centerTile.y, 0);	
		}	

		} else {
			throw new NullPointerException();
		}
	} /** end of fluid corners */
	
	
	
public Point getUpperLeftMap(Point origin) {
		
		Point point = new Point(origin.x-GameStates.mapWidth*GameStates.mapTileSetWidth, origin.y-GameStates.mapHeight*GameStates.mapTileSetHeight);
		return new Point(((point.x/GameStates.mapTotalWidth) % this.getMapWidth() +this.getMapWidth()) % this.getMapWidth() *GameStates.mapTotalWidth, ((point.y/GameStates.mapTotalHeight) % this.getMapHeight() +this.getMapHeight()) % this.getMapHeight() *GameStates.mapTotalHeight);
		
	}
	
	public Point getUpperMap(Point origin) {
		return new Point(origin.x, origin.y-GameStates.mapHeight*GameStates.mapTileSetHeight);
	}
	
	public Point getUpperRightMap(Point origin) {
		Point point =  new Point(origin.x+GameStates.mapWidth*GameStates.mapTileSetWidth, origin.y-GameStates.mapHeight*GameStates.mapTileSetHeight);
		return new Point(((point.x/GameStates.mapTotalWidth) % this.getMapWidth() +this.getMapWidth()) % this.getMapWidth() *GameStates.mapTotalWidth, ((point.y/GameStates.mapTotalHeight) % this.getMapHeight() +this.getMapHeight()) % this.getMapHeight() *GameStates.mapTotalHeight);
		
	}
	
	public Point getLeftMap(Point origin) {
		Point point = new Point(origin.x-GameStates.mapWidth*GameStates.mapTileSetWidth, origin.y);
		return new Point(((point.x/GameStates.mapTotalWidth) % this.getMapWidth() +this.getMapWidth()) % this.getMapWidth() *GameStates.mapTotalWidth, ((point.y/GameStates.mapTotalHeight) % this.getMapHeight() +this.getMapHeight()) % this.getMapHeight() *GameStates.mapTotalHeight);
		
	}
	
	public Point getRightMap(Point origin) {
		Point point = new Point(origin.x+GameStates.mapWidth*GameStates.mapTileSetWidth, origin.y);
		return new Point(((point.x/GameStates.mapTotalWidth) % this.getMapWidth() +this.getMapWidth()) % this.getMapWidth() *GameStates.mapTotalWidth, ((point.y/GameStates.mapTotalHeight) % this.getMapHeight() +this.getMapHeight()) % this.getMapHeight() *GameStates.mapTotalHeight);
		
	}
	
	public Point getLowerLeftMap(Point origin) {
		Point point = new Point(origin.x-GameStates.mapWidth*GameStates.mapTileSetWidth, origin.y+GameStates.mapHeight*GameStates.mapTileSetHeight);
		return new Point(((point.x/GameStates.mapTotalWidth) % this.getMapWidth() +this.getMapWidth()) % this.getMapWidth() *GameStates.mapTotalWidth, ((point.y/GameStates.mapTotalHeight) % this.getMapHeight() +this.getMapHeight()) % this.getMapHeight() *GameStates.mapTotalHeight);
		
	}
	
	public Point getLowerMap(Point origin) {
		Point point = new Point(origin.x, origin.y+GameStates.mapHeight*GameStates.mapTileSetHeight);
		return new Point(((point.x/GameStates.mapTotalWidth) % this.getMapWidth() +this.getMapWidth()) % this.getMapWidth() *GameStates.mapTotalWidth, ((point.y/GameStates.mapTotalHeight) % this.getMapHeight() +this.getMapHeight()) % this.getMapHeight() *GameStates.mapTotalHeight);
		
	}
	
	public Point getLowerRightMap(Point origin) {
		Point point =  new Point(origin.x+GameStates.mapWidth*GameStates.mapTileSetWidth, origin.y+GameStates.mapHeight*GameStates.mapTileSetHeight);
		return new Point(((point.x/GameStates.mapTotalWidth) % this.getMapWidth() +this.getMapWidth()) % this.getMapWidth() *GameStates.mapTotalWidth, ((point.y/GameStates.mapTotalHeight) % this.getMapHeight() +this.getMapHeight()) % this.getMapHeight() *GameStates.mapTotalHeight);
		
	}

	
	public void emptyAll() {
		localMapList = new HashMap<Point, LocalMap>();
		pointList = new ArrayList<Point>();
	}

	public void save(LocalMap localMap, String fileName) {
		try {
			FileOutputStream datei=new FileOutputStream(fileName);
			BufferedOutputStream buf=new BufferedOutputStream(datei);
			ObjectOutputStream writeStream = new ObjectOutputStream(buf);
			writeStream.writeObject(localMap.getLocalMap());
			writeStream.writeObject(localMap.getUlArray());
			writeStream.writeObject(localMap.getUrArray());
			writeStream.writeObject(localMap.getDlArray());
			writeStream.writeObject(localMap.getDrArray());
			writeStream.writeObject(localMap.getOrigin());
//			writeStream.writeObject(localMap.getTileSetFileName());
			writeStream.writeObject(localMap.getPaintLayer());
			writeStream.close();
			
			localMapFileList.put(localMap.getOrigin(), fileName);
//			System.out.println("wrote "+localMap.getOrigin().x +" "+localMap.getOrigin().y);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	public static LocalMap loadMap(String fileName) {
		try {
			File testFile = new File(fileName);
			if (testFile.exists()) {
				// OK
			} else return null;
			
			FileInputStream fileInputStream =new FileInputStream(fileName);
			BufferedInputStream buf=new BufferedInputStream(fileInputStream);
			ObjectInputStream readStream = new ObjectInputStream(buf);
			try {
				int[][] localMap = (int[][]) readStream.readObject();
				int[][] ulArray = (int[][]) readStream.readObject();
				int[][] urArray = (int[][]) readStream.readObject();
				int[][] dlArray = (int[][]) readStream.readObject();
				int[][] drArray = (int[][]) readStream.readObject();
				Point origin = (Point) readStream.readObject();
//				String tileSetFileName = (String) readStream.readObject();
				String paintLayer = (String) readStream.readObject();
				readStream.close();
				
//				System.out.println(origin.x +" "+origin.y);
				return new LocalMap(localMap, ulArray, urArray, dlArray, drArray,origin, paintLayer, true);
			} catch (EOFException e) {
				e.printStackTrace();
				return null;
			}	
			
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return null;
		} 	
		
			
	}
	
	public void loadLocalMaps(String playerName) {
		/** loop through mapfolder and get every map that ends on ".map" and begins with username */
		ArrayList<String> listOfFiles = new ArrayList<String>();
		File folder = new File(GameStates.standardMapFolder+playerName);
		listOfFiles = MyUtil.listFilesForFolder(folder);
		for (String mapFile: listOfFiles) {
			/** get origins and save the maps under the corresponding origin */
			/** only load if its the right paintLayer */
			if (mapFile.startsWith(this.getPaintLayer())) {
				LocalMap lMap = loadMap(GameStates.standardMapFolder+playerName+"/"+mapFile);
				localMapList.put(lMap.getOrigin(), lMap);
				pointList.add(lMap.getOrigin());
			}
		}
	}
	
	
	
	public boolean loadFromFileSystem(String playerName, int x, int y) {
		boolean retValue = false;
		Point moduloPoint = new Point(((x/GameStates.mapTotalWidth) % this.getMapWidth()+this.getMapWidth()) % this.getMapWidth()*GameStates.mapTotalWidth, ((y/GameStates.mapTotalHeight) % this.getMapHeight()+this.getMapHeight())%this.getMapHeight()*GameStates.mapTotalHeight);
		String mapFile = null; 
		if (localMapFileList.containsKey(moduloPoint)) mapFile = localMapFileList.get(moduloPoint);
		if (mapFile != null) {
			LocalMap lMap = loadMap(mapFile);
			if (lMap != null) {
				localMapList.put(lMap.getOrigin(), lMap);
				pointList.add(lMap.getOrigin());
				retValue =  true;
			} else
				retValue= false;
		} else {
			retValue= false;
		}
		return retValue;
	}
	

	/** load a prepared map from the disk */
	public boolean loadPresetFromFileSystem(String playerName, int x, int y, Point p, int tileType) {
		boolean retValue = false;
		Point point = new Point(x,y);
		String mapFile = null; 
		if (localMapFileList.containsKey(point)) mapFile = localMapFileList.get(point);
		if (mapFile != null) {
			LocalMap lMap = loadMap(mapFile);
			if (lMap != null) {
				lMap.setOrigin(p);
				int tileShift = tileType - GameStates.fluidSoilTile;
				if (Math.abs(tileShift) >1) {
					for (int i = 0; i < lMap.getLocalMap()[0].length;i++) {
						for(int j = 0; j < lMap.getLocalMap().length;j++) {
							if (lMap.getLocalMap()[j][i] >1) { 
								lMap.getLocalMap()[j][i] += tileShift;
							} 
						}
					}
				}
				
				if (this.getPaintLayer().equals("tree1")) {
					/** add some objects (stones for example) */
					int collectStoneTile1Count = (int) (Math.random() * 2/ GameStates.scaleGenerationCountObjects + 10 / GameStates.scaleGenerationCountObjects);
					int collectStoneTile2Count = (int) (Math.random() * 2/ GameStates.scaleGenerationCountObjects + 10 / GameStates.scaleGenerationCountObjects);
					int collectStoneTile3Count = (int) (Math.random() * 2/ GameStates.scaleGenerationCountObjects + 10 / GameStates.scaleGenerationCountObjects);
					int collectArtifactStoneTile1Count = (int) (Math.random() * 2/ GameStates.scaleGenerationCountObjects + 1 / GameStates.scaleGenerationCountObjects);
					int[] stone1X = MyUtil.createIntArray(collectStoneTile1Count, 2,GameStates.mapWidth - 1);
					int[] stone1Y = MyUtil.createIntArray(collectStoneTile1Count, 2,GameStates.mapHeight - 1);
					int[] stone2X = MyUtil.createIntArray(collectStoneTile2Count, 2,GameStates.mapWidth - 1);
					int[] stone2Y = MyUtil.createIntArray(collectStoneTile2Count, 2,GameStates.mapHeight - 1);
					int[] stone3X = MyUtil.createIntArray(collectStoneTile3Count, 2,GameStates.mapWidth - 1);
					int[] stone3Y = MyUtil.createIntArray(collectStoneTile3Count, 2,GameStates.mapHeight - 1);
					int[] artifactStone1X = MyUtil.createIntArray(collectArtifactStoneTile1Count, 2,GameStates.mapWidth - 1);
					int[] artifactStone1Y = MyUtil.createIntArray(collectArtifactStoneTile1Count, 2,GameStates.mapHeight - 1);
					for (int i = 0; i< collectStoneTile1Count;i++) {
						if (lMap.getLocalMap()[stone1X[i]][stone1Y[i]] == 0) {
							lMap.getLocalMap()[stone1X[i]][stone1Y[i]] = GameStates.collectStoneTile1;
						}
					}
					for (int i = 0; i< collectStoneTile2Count;i++) {
						if (lMap.getLocalMap()[stone2X[i]][stone2Y[i]] == 0) {
							lMap.getLocalMap()[stone2X[i]][stone2Y[i]] = GameStates.collectStoneTile2;
						}
					}
					for (int i = 0; i< collectStoneTile3Count;i++) {
						if (lMap.getLocalMap()[stone3X[i]][stone3Y[i]] == 0) {
							lMap.getLocalMap()[stone3X[i]][stone3Y[i]] = GameStates.collectStoneTile3;
						}
					}
					
					for (int i = 0; i< collectArtifactStoneTile1Count;i++) {
						if (lMap.getLocalMap()[artifactStone1X[i]][artifactStone1Y[i]] == 0) {
							lMap.getLocalMap()[artifactStone1X[i]][artifactStone1Y[i]] = GameStates.collectArtifactStoneTile1;
						}
					}
				}
				
				localMapList.put(p, lMap);
				pointList.add(p);
				retValue =  true;
			} else
				retValue= false;
		} else {
			retValue= false;
		}
		return retValue;
	}
	
	
	/** paint the tiles of int tile to some spots on the map */
	public void paintTilesOnMap(Point point, int[] x, int[] y, int tile) {
		if (localMapList.get(point) != null) {
			if (x.length != y.length) {
				System.out.println("Wrong dimension in paintTilesOnMap in ObjectMapManager");
			} else {
				for (int i = 0; i < x.length; i ++) {
					paintTiles(point, new Point(x[i], y[i]), tile);	
				}		
			}
		} else {
			createMap(point);
			if (x.length != y.length) {
				System.out.println("Wrong dimension in paintTilesOnMap in ObjectMapManager");
			} else {
				System.out.println("ObjectMapManager: needed to create map first");
				if (localMapList.get(point) != null) {
					for (int i = 0; i < x.length; i ++) {
						paintTiles(point, new Point(x[i], y[i]), tile);
					}
					
				}
			}
		}
//		this.addChangedList(point);
	}
	
	/** paint the tiles of int tile to some spots on the map */
	public void paintFluidTilesOnMap(Point point, int[] x, int[] y, int tile) {
		if (localMapList.get(point) != null) {
			if (x.length != y.length) {
				System.out.println("Wrong dimension in paintTilesOnMap in ObjectMapManager");
			} else {
//				System.out.println("Paint on existing point "+point.x+"|"+point.y);
				for (int i = 0; i < x.length; i ++) {
					paintFluidTiles(point, new Point(x[i], y[i]), tile);	
				}		
			}
		} else {
			createMap(point);
			if (x.length != y.length) {
				System.out.println("Wrong dimension in paintTilesOnMap in ObjectMapManager");
			} else {
				System.out.println("ObjectMapManager: needed to create map first");
				if (localMapList.get(point) != null) {
					for (int i = 0; i < x.length; i ++) {
						paintFluidTiles(point, new Point(x[i], y[i]), tile);
					}
					
				}
			}
		}
//		this.addChangedList(point);
	}
	
	/** paint the tiles of int tile to some spots on the map */
	public void paintFluidFrameOnMap(Point point, int[] x, int[] y, int tile) {
		if (localMapList.get(point) != null) {
			if (x.length != y.length) {
				System.out.println("Wrong dimension in paintTilesOnMap in ObjectMapManager");
			} else {
//				System.out.println("FluidFrame painting just some tiles");
//				createFramedPaintedMap(point, tile);
				for (int i = 0; i < x.length; i ++) {
					paintFluidTiles(point, new Point(x[i], y[i]), tile);	
				}		
			}
		} else {
			createFramedPaintedMap(point, tile);
			if (x.length != y.length) {
				System.out.println("Wrong dimension in paintTilesOnMap in ObjectMapManager");
			} else {
				System.out.println("ObjectMapManager: needed to create map first");
				if (localMapList.get(point) != null) {
					for (int i = 0; i < x.length; i ++) {
						paintFluidTiles(point, new Point(x[i], y[i]), tile);
					}
					
				}
			}
		}
//		this.addChangedList(point);
	}
	
	/** paint the complete map with one tile type */
	public void paintFullTilesOnMap(Point point, int tile) {
		TileDimensions tileDimensions = TileDimensions.RowCol2x2;
		if (this.getPaintLayer().equals("tree1") || this.getPaintLayer().equals("overlayTree1")) {
			for (int i = 0; i < GameStates.mapWidth; i++) {
				for (int j = 0; j < GameStates.mapHeight; j++) {
					if (i%4 == 0 && j%2 == 0) {
						this.get(point).setUl(i, j, 0);
						this.get(point).setUr(i, j, tile);
						this.get(point).setDl(i, j, 0);
						this.get(point).setDr(i, j, 0);
						this.get(point).setIdByCornersObject(i, j, tile, tileDimensions);
					} else if (i%4 == 1 && j%2 == 0){
						this.get(point).setUl(i, j, tile);
						this.get(point).setUr(i, j, 0);
						this.get(point).setDl(i, j, 0);
						this.get(point).setDr(i, j, 0);
						this.get(point).setIdByCornersObject(i, j, tile, tileDimensions);
					} else if (i%4 == 0 && j%2 == 1) {
						this.get(point).setUl(i, j, 0);
						this.get(point).setUr(i, j, 0);
						this.get(point).setDl(i, j, 0);
						this.get(point).setDr(i, j, tile);
						this.get(point).setIdByCornersObject(i, j, tile, tileDimensions);
					} else if (i%4 == 1 && j%2 == 1) {
						this.get(point).setUl(i, j, 0);
						this.get(point).setUr(i, j, 0);
						this.get(point).setDl(i, j, tile);
						this.get(point).setDr(i, j, 0);
						this.get(point).setIdByCornersObject(i, j, tile, tileDimensions);
					}
				}
			}
		} else if (this.getPaintLayer().equals("tree2") || this.getPaintLayer().equals("overlayTree2")) {
			for (int i = 0; i < GameStates.mapWidth; i++) {
				for (int j = 0; j < GameStates.mapHeight; j++) {
					if (i%4 == 2 && j%2 == 1) {
						this.get(point).setUl(i, j, 0);
						this.get(point).setUr(i, j, tile);
						this.get(point).setDl(i, j, 0);
						this.get(point).setDr(i, j, 0);
						this.get(point).setIdByCornersObject(i, j, tile, tileDimensions);
					} else if (i%4 == 3 && j%2 == 1){
						this.get(point).setUl(i, j, tile);
						this.get(point).setUr(i, j, 0);
						this.get(point).setDl(i, j, 0);
						this.get(point).setDr(i, j, 0);
						this.get(point).setIdByCornersObject(i, j, tile, tileDimensions);
					} else if (i%4 == 2 && j%2 == 0) {
						this.get(point).setUl(i, j, 0);
						this.get(point).setUr(i, j, 0);
						this.get(point).setDl(i, j, 0);
						this.get(point).setDr(i, j, tile);
						this.get(point).setIdByCornersObject(i, j, tile, tileDimensions);
					} else if (i%4 == 3 && j%2 == 0) {
						this.get(point).setUl(i, j, 0);
						this.get(point).setUr(i, j, 0);
						this.get(point).setDl(i, j, tile);
						this.get(point).setDr(i, j, 0);
						this.get(point).setIdByCornersObject(i, j, tile, tileDimensions);
					}
				} 
			}
		} else {
			// ERROR!!!
			System.out.println("Wrong Paint Layer for this objectMapManager");
		}
	}
	
	
	public static LocalMap getPaintFullTilesOnMapExcept2x2(Point point, String paintLayer, int tile, int[][] exceptMap, boolean existedBefore, LocalMap oldMap) {
		TileDimensions tileDimensions = TileDimensions.RowCol2x2;
//		int mapArray[][] = oldMap.getLocalMap();
		LocalMap map = oldMap;
		if (paintLayer.equals("tree1") || paintLayer.equals("overlayTree1")) {
			for (int i = 0; i < GameStates.mapWidth; i++) {
				for (int j = 0; j < GameStates.mapHeight; j++) {
					if ((exceptMap[i][j] == 0 || !LocalMap.isPartOfHole(exceptMap[i][j])) && !existedBefore) {
						if (i%4 == 0 && j%2 == 0) {
							map.setUl(i, j, 0);
							map.setUr(i, j, tile);
							map.setDl(i, j, 0);
							map.setDr(i, j, 0);
							map.setIdByCornersObject(i, j, tile, tileDimensions);
						} else if (i%4 == 1 && j%2 == 0){
							map.setUl(i, j, tile);
							map.setUr(i, j, 0);
							map.setDl(i, j, 0);
							map.setDr(i, j, 0);
							map.setIdByCornersObject(i, j, tile, tileDimensions);
						} else if (i%4 == 0 && j%2 == 1) {
							map.setUl(i, j, 0);
							map.setUr(i, j, 0);
							map.setDl(i, j, 0);
							map.setDr(i, j, tile);
							map.setIdByCornersObject(i, j, tile, tileDimensions);
						} else if (i%4 == 1 && j%2 == 1) {
							map.setUl(i, j, 0);
							map.setUr(i, j, 0);
							map.setDl(i, j, tile);
							map.setDr(i, j, 0);
							map.setIdByCornersObject(i, j, tile, tileDimensions);
						}
					} else if (exceptMap[i][j] != 0 && LocalMap.isPartOfHole(exceptMap[i][j])) {
						/** else only update the new holes */						
						if (i%4 == 0 && j%2 == 0) {
							map.setUl(i, j, 0);
							map.setUr(i, j, 0);
							map.setDl(i, j, 0);
							map.setDr(i, j, 0);
//							map.setIdByCornersObject(i, j, tile, tileDimensions);
							map.setTile(i, j, 0);
						} else if (i%4 == 1 && j%2 == 0){
							map.setUl(i, j, 0);
							map.setUr(i, j, 0);
							map.setDl(i, j, 0);
							map.setDr(i, j, 0);
//							map.setIdByCornersObject(i, j, tile, tileDimensions);
							map.setTile(i, j, 0);
						} else if (i%4 == 0 && j%2 == 1) {
							map.setUl(i, j, 0);
							map.setUr(i, j, 0);
							map.setDl(i, j, 0);
							map.setDr(i, j, 0);
//							map.setIdByCornersObject(i, j, tile, tileDimensions);
							map.setTile(i, j, 0);
						} else if (i%4 == 1 && j%2 == 1) {
							map.setUl(i, j, 0);
							map.setUr(i, j, 0);
							map.setDl(i, j, 0);
							map.setDr(i, j, 0);
//							map.setIdByCornersObject(i, j, tile, tileDimensions);
							map.setTile(i, j, 0);
						}
					}
				}
			}
		} else if (paintLayer.equals("tree2") || paintLayer.equals("overlayTree2")) {
			for (int i = 0; i < GameStates.mapWidth; i++) {
				for (int j = 0; j < GameStates.mapHeight; j++) {
					if ((exceptMap[i][j] == 0 || !LocalMap.isPartOfHole(exceptMap[i][j]))&& !existedBefore) {
						if (i%4 == 2 && j%2 == 0) {
							map.setUl(i, j, 0);
							map.setUr(i, j, tile);
							map.setDl(i, j, 0);
							map.setDr(i, j, 0);
							map.setIdByCornersObject(i, j, tile, tileDimensions);
						} else if (i%4 == 3 && j%2 == 0){
							map.setUl(i, j, tile);
							map.setUr(i, j, 0);
							map.setDl(i, j, 0);
							map.setDr(i, j, 0);
							map.setIdByCornersObject(i, j, tile, tileDimensions);
						} else if (i%4 == 2 && j%2 == 1) {
							map.setUl(i, j, 0);
							map.setUr(i, j, 0);
							map.setDl(i, j, 0);
							map.setDr(i, j, tile);
							map.setIdByCornersObject(i, j, tile, tileDimensions);
						} else if (i%4 == 3 && j%2 == 1) {
							map.setUl(i, j, 0);
							map.setUr(i, j, 0);
							map.setDl(i, j, tile);
							map.setDr(i, j, 0);
							map.setIdByCornersObject(i, j, tile, tileDimensions);
						}
					} else if (exceptMap[i][j] != 0 && LocalMap.isPartOfHole(exceptMap[i][j])) {
						/** else only update the new holes */
						if (i%4 == 2 && j%2 == 0) {
							map.setUl(i, j, 0);
							map.setUr(i, j, 0);
							map.setDl(i, j, 0);
							map.setDr(i, j, 0);
//							map.setIdByCornersObject(i, j, tile, tileDimensions);
							map.setTile(i, j, 0);
						} else if (i%4 == 3 && j%2 == 0){
							map.setUl(i, j, 0);
							map.setUr(i, j, 0);
							map.setDl(i, j, 0);
							map.setDr(i, j, 0);
//							map.setIdByCornersObject(i, j, tile, tileDimensions);
							map.setTile(i, j, 0);
						} else if (i%4 == 2 && j%2 == 1) {
							map.setUl(i, j, 0);
							map.setUr(i, j, 0);
							map.setDl(i, j, 0);
							map.setDr(i, j, 0);
//							map.setIdByCornersObject(i, j, tile, tileDimensions);
							map.setTile(i, j, 0);
						} else if (i%4 == 3 && j%2 == 1) {
							map.setUl(i, j, 0);
							map.setUr(i, j, 0);
							map.setDl(i, j, 0);
							map.setDr(i, j, 0);
//							map.setIdByCornersObject(i, j, tile, tileDimensions);
							map.setTile(i, j, 0);
						}
					}
				} 
			}
		} else {
			// ERROR!!!
			System.out.println("Wrong Paint Layer for this objectMapManager");
		}
		return map;
	}
	
	
//	public LocalMap getPaintFullTilesOnMapExcept2x3(Point point, String paintLayer, int tile, int[][] exceptMap, boolean existedBefore, LocalMap oldMap) {
//		TileDimensions tileDimensions = TileDimensions.RowCol2x3;
////		int mapArray[][] = oldMap.getLocalMap();
//		LocalMap map = oldMap;
//		this.add(map);
//		if (paintLayer.equals("tree1") || paintLayer.equals("overlayTree1")) {
//			for (int i = 0; i < GameStates.mapWidth; i++) {
//				for (int j = 0; j < GameStates.mapHeight; j++) {
//					if ((exceptMap[i][j] == 0 || !LocalMap.isPartOfHole(exceptMap[i][j])) && !existedBefore) {
//						if (i%4 == 0 && j%2 == 0) {
//							map.setUl(i, j, tile);
//							map.setUr(i, j, tile);
//							map.setDl(i, j, 0);
//							map.setDr(i, j, 0);
//							map.setIdByCornersObject(i, j, tile, tileDimensions);
//						} else if (i%4 == 1 && j%2 == 0){
//							map.setUl(i, j, tile);
//							map.setUr(i, j, 0);
//							map.setDl(i, j, 0);
//							map.setDr(i, j, 0);
//							map.setIdByCornersObject(i, j, tile, tileDimensions);
//						} else if (i%4 == 0 && j%2 == 1) {
//							map.setUl(i, j, 0);
//							map.setUr(i, j, 0);
//							map.setDl(i, j, tile);
//							map.setDr(i, j, tile);
//							map.setIdByCornersObject(i, j, tile, tileDimensions);
//						} else if (i%4 == 1 && j%2 == 1) {
//							map.setUl(i, j, 0);
//							map.setUr(i, j, 0);
//							map.setDl(i, j, tile);
//							map.setDr(i, j, 0);
//							map.setIdByCornersObject(i, j, tile, tileDimensions);
//						} else if (i%4 == 3 && j%2 == 0) {
//							map.setUl(i, j, 0);
//							map.setUr(i, j, tile);
//							map.setDl(i, j, 0);
//							map.setDr(i, j, 0);
//							map.setIdByCornersObject(i, j, tile, tileDimensions);
//						} else if (i%4 == 3 && j%2 == 1) {
//							map.setUl(i, j, 0);
//							map.setUr(i, j, 0);
//							map.setDl(i, j, 0);
//							map.setDr(i, j, tile);
//							map.setIdByCornersObject(i, j, tile, tileDimensions);
//						}
//					} else if (exceptMap[i][j] != 0 && LocalMap.isPartOfHole(exceptMap[i][j])) {
//						/** else only update the new holes */						
//						if (i%4 == 0 && j%2 == 0) {
//							map.setUl(i, j, 0);
//							map.setUr(i, j, 0);
//							map.setDl(i, j, 0);
//							map.setDr(i, j, 0);
//							map.setIdByCornersObject(i, j, tile, tileDimensions);
//							this.deleteSurrounding(this.get(point), i, j, tile, tileDimensions);
//						} 
////						else if (i%4 == 1 && j%2 == 0){
////							map.setUl(i, j, 0);
////							map.setUr(i, j, 0);
////							map.setDl(i, j, 0);
////							map.setDr(i, j, 0);
//////							map.setIdByCornersObject(i, j, tile, tileDimensions);
////							map.setTile(i, j, 0);
////						} else if (i%4 == 0 && j%2 == 1) {
////							map.setUl(i, j, 0);
////							map.setUr(i, j, 0);
////							map.setDl(i, j, 0);
////							map.setDr(i, j, 0);
//////							map.setIdByCornersObject(i, j, tile, tileDimensions);
////							map.setTile(i, j, 0);
////						
////						} else if (i%4 == 1 && j%2 == 1) {
////							map.setUl(i, j, 0);
////							map.setUr(i, j, 0);
////							map.setDl(i, j, 0);
////							map.setDr(i, j, 0);
//////							map.setIdByCornersObject(i, j, tile, tileDimensions);
////							map.setTile(i, j, 0);
////						} else if (i%4 == 3 && j%2 == 0) {
////							map.setUl(i, j, 0);
////							map.setUr(i, j, 0);
////							map.setDl(i, j, 0);
////							map.setDr(i, j, 0);
////							map.setTile(i, j, 0);
////						} else if (i%4 == 3 && j%2 == 1) {
////							map.setUl(i, j, 0);
////							map.setUr(i, j, 0);
////							map.setDl(i, j, 0);
////							map.setDr(i, j, 0);
////							map.setTile(i, j, 0);
////						}
//					}
//				}
//			}
//		} else if (paintLayer.equals("tree2") || paintLayer.equals("overlayTree2")) {
//			for (int i = 0; i < GameStates.mapWidth; i++) {
//				for (int j = 0; j < GameStates.mapHeight; j++) {
//					if ((exceptMap[i][j] == 0 || !LocalMap.isPartOfHole(exceptMap[i][j]))&& !existedBefore) {
//						if (i%4 == 2 && j%2 == 0) {
//							map.setUl(i, j, tile);
//							map.setUr(i, j, tile);
//							map.setDl(i, j, 0);
//							map.setDr(i, j, 0);
//							map.setIdByCornersObject(i, j, tile, tileDimensions);
//						} else if (i%4 == 3 && j%2 == 0){
//							map.setUl(i, j, tile);
//							map.setUr(i, j, 0);
//							map.setDl(i, j, 0);
//							map.setDr(i, j, 0);
//							map.setIdByCornersObject(i, j, tile, tileDimensions);
//						} else if (i%4 == 2 && j%2 == 1) {
//							map.setUl(i, j, 0);
//							map.setUr(i, j, 0);
//							map.setDl(i, j, tile);
//							map.setDr(i, j, tile);
//							map.setIdByCornersObject(i, j, tile, tileDimensions);
//						} else if (i%4 == 3 && j%2 == 1) {
//							map.setUl(i, j, 0);
//							map.setUr(i, j, 0);
//							map.setDl(i, j, tile);
//							map.setDr(i, j, 0);
//							map.setIdByCornersObject(i, j, tile, tileDimensions);
//						} else if (i%4 == 1 && j%2 == 0) {
//							map.setUl(i, j, 0);
//							map.setUr(i, j, tile);
//							map.setDl(i, j, 0);
//							map.setDr(i, j, 0);
//							map.setIdByCornersObject(i, j, tile, tileDimensions);
//						} else if (i%4 == 1 && j%2 == 1) {
//							map.setUl(i, j, 0);
//							map.setUr(i, j, 0);
//							map.setDl(i, j, 0);
//							map.setDr(i, j, tile);
//							map.setIdByCornersObject(i, j, tile, tileDimensions);
//						}
//					} else if (exceptMap[i][j] != 0 && LocalMap.isPartOfHole(exceptMap[i][j])) {
//						/** else only update the new holes */
//						if (i%4 == 2 && j%2 == 0) {
//							map.setUl(i, j, 0);
//							map.setUr(i, j, 0);
//							map.setDl(i, j, 0);
//							map.setDr(i, j, 0);							
//							map.setIdByCornersObject(i, j, tile, tileDimensions);
//							this.deleteSurrounding(this.get(point), i, j, tile, tileDimensions);
//						} 
////						else if (i%4 == 3 && j%2 == 0){
////							map.setUl(i, j, 0);
////							map.setUr(i, j, 0);
////							map.setDl(i, j, 0);
////							map.setDr(i, j, 0);
//////							map.setIdByCornersObject(i, j, tile, tileDimensions);
////							map.setTile(i, j, 0);
////						} else if (i%4 == 2 && j%2 == 1) {
////							map.setUl(i, j, 0);
////							map.setUr(i, j, 0);
////							map.setDl(i, j, 0);
////							map.setDr(i, j, 0);
//////							map.setIdByCornersObject(i, j, tile, tileDimensions);
////							map.setTile(i, j, 0);
////						} else if (i%4 == 3 && j%2 == 1) {
////							map.setUl(i, j, 0);
////							map.setUr(i, j, 0);
////							map.setDl(i, j, 0);
////							map.setDr(i, j, 0);
//////							map.setIdByCornersObject(i, j, tile, tileDimensions);
////							map.setTile(i, j, 0);
////						} else if (i%4 == 1 && j%2 == 0) {
////							map.setUl(i, j, 0);
////							map.setUr(i, j, 0);
////							map.setDl(i, j, 0);
////							map.setDr(i, j, 0);
//////							map.setIdByCornersObject(i, j, tile, tileDimensions);
////							map.setTile(i, j, 0);
////						} else if (i%4 == 1 && j%2 == 1) {
////							map.setUl(i, j, 0);
////							map.setUr(i, j, 0);
////							map.setDl(i, j, 0);
////							map.setDr(i, j, 0);
//////							map.setIdByCornersObject(i, j, tile, tileDimensions);
////							map.setTile(i, j, 0);
////						}
//					}
//				} 
//			}
//		} else {
//			// ERROR!!!
//			System.out.println("Wrong Paint Layer for this objectMapManager");
//		}
//		return map;
//	}
	
	public void getPaintFullTilesOnMap2x3Frame(Point point, String paintLayer, int tile) {
		TileDimensions tileDimensions = TileDimensions.RowCol2x3;
		createMap(point);
		LocalMap map = this.get(point);
		if (paintLayer.equals("tree1") || paintLayer.equals("overlayTree1")) {
			for (int i = 0; i < GameStates.mapWidth; i++) {
				for (int j = 0; j < GameStates.mapHeight; j++) {
						if (i == 4 && j%2 == 0) {
							map.setUl(i, j, tile);
							map.setUr(i, j, tile);
							map.setDl(i, j, 0);
							map.setDr(i, j, 0);
							map.setIdByCornersObject(i, j, tile, tileDimensions);
							this.adjustSurrounding(map, i, j, tile, TileDimensions.RowCol2x3);
						} else if (i % 4 == 0 && j == 4){
							map.setUl(i, j, tile);
							map.setUr(i, j, tile);
							map.setDl(i, j, 0);
							map.setDr(i, j, 0);
							map.setIdByCornersObject(i, j, tile, tileDimensions);
							this.adjustSurrounding(map, i, j, tile, TileDimensions.RowCol2x3);
						}
				}
			}
		} else if (paintLayer.equals("tree2") || paintLayer.equals("overlayTree2")) {
				for (int i = 0; i < GameStates.mapWidth; i++) {
					for (int j = 0; j < GameStates.mapHeight; j++) {
						if (i == 4 && j%2 == 0) {
							map.setUl(i, j, tile);
							map.setUr(i, j, tile);
							map.setDl(i, j, 0);
							map.setDr(i, j, 0);
							map.setIdByCornersObject(i, j, tile, tileDimensions);
							this.adjustSurrounding(map, i, j, tile, TileDimensions.RowCol2x3);
						} else if (i % 4 == 2 && j == 4){
							map.setUl(i, j, tile);
							map.setUr(i, j, tile);
							map.setDl(i, j, 0);
							map.setDr(i, j, 0);
							map.setIdByCornersObject(i, j, tile, tileDimensions);
							this.adjustSurrounding(map, i, j, tile, TileDimensions.RowCol2x3);
						}
					}
				}
		} else {
			// ERROR!!!
			System.out.println("Wrong Paint Layer for this objectMapManager");
		}
	}
	
	public void getPaintFullTilesOnMap2x3(Point point, String paintLayer, int tile) {
		TileDimensions tileDimensions = TileDimensions.RowCol2x3;
		createMap(point);
		LocalMap map = this.get(point);
		if (paintLayer.equals("tree1") || paintLayer.equals("overlayTree1")) {
			for (int i = 0; i < GameStates.mapWidth; i++) {
				for (int j = 0; j < GameStates.mapHeight; j++) {
//					if ((exceptMap[i][j] == 0 || !LocalMap.isPartOfHole(exceptMap[i][j])) && !existedBefore) {
						if (i%4 == 0 && j%2 == 0) {
							map.setUl(i, j, tile);
							map.setUr(i, j, tile);
							map.setDl(i, j, 0);
							map.setDr(i, j, 0);
							map.setIdByCornersObject(i, j, tile, tileDimensions);
						} else if (i%4 == 1 && j%2 == 0){
							map.setUl(i, j, tile);
							map.setUr(i, j, 0);
							map.setDl(i, j, 0);
							map.setDr(i, j, 0);
							map.setIdByCornersObject(i, j, tile, tileDimensions);
						} else if (i%4 == 0 && j%2 == 1) {
							map.setUl(i, j, 0);
							map.setUr(i, j, 0);
							map.setDl(i, j, tile);
							map.setDr(i, j, tile);
							map.setIdByCornersObject(i, j, tile, tileDimensions);
						} else if (i%4 == 1 && j%2 == 1) {
							map.setUl(i, j, 0);
							map.setUr(i, j, 0);
							map.setDl(i, j, tile);
							map.setDr(i, j, 0);
							map.setIdByCornersObject(i, j, tile, tileDimensions);
						} else if (i%4 == 3 && j%2 == 0) {
							map.setUl(i, j, 0);
							map.setUr(i, j, tile);
							map.setDl(i, j, 0);
							map.setDr(i, j, 0);
							map.setIdByCornersObject(i, j, tile, tileDimensions);
						} else if (i%4 == 3 && j%2 == 1) {
							map.setUl(i, j, 0);
							map.setUr(i, j, 0);
							map.setDl(i, j, 0);
							map.setDr(i, j, tile);
							map.setIdByCornersObject(i, j, tile, tileDimensions);
						}
					
				}
			}
		} else if (paintLayer.equals("tree2") || paintLayer.equals("overlayTree2")) {
			for (int i = 0; i < GameStates.mapWidth; i++) {
				for (int j = 0; j < GameStates.mapHeight; j++) {
						if (i%4 == 2 && j%2 == 0) {
							map.setUl(i, j, tile);
							map.setUr(i, j, tile);
							map.setDl(i, j, 0);
							map.setDr(i, j, 0);
							map.setIdByCornersObject(i, j, tile, tileDimensions);
						} else if (i%4 == 3 && j%2 == 0){
							map.setUl(i, j, tile);
							map.setUr(i, j, 0);
							map.setDl(i, j, 0);
							map.setDr(i, j, 0);
							map.setIdByCornersObject(i, j, tile, tileDimensions);
						} else if (i%4 == 2 && j%2 == 1) {
							map.setUl(i, j, 0);
							map.setUr(i, j, 0);
							map.setDl(i, j, tile);
							map.setDr(i, j, tile);
							map.setIdByCornersObject(i, j, tile, tileDimensions);
						} else if (i%4 == 3 && j%2 == 1) {
							map.setUl(i, j, 0);
							map.setUr(i, j, 0);
							map.setDl(i, j, tile);
							map.setDr(i, j, 0);
							map.setIdByCornersObject(i, j, tile, tileDimensions);
						} else if (i%4 == 1 && j%2 == 0) {
							map.setUl(i, j, 0);
							map.setUr(i, j, tile);
							map.setDl(i, j, 0);
							map.setDr(i, j, 0);
							map.setIdByCornersObject(i, j, tile, tileDimensions);
						} else if (i%4 == 1 && j%2 == 1) {
							map.setUl(i, j, 0);
							map.setUr(i, j, 0);
							map.setDl(i, j, 0);
							map.setDr(i, j, tile);
							map.setIdByCornersObject(i, j, tile, tileDimensions);
						}
				}
			}
		} else {
			// ERROR!!!
			System.out.println("Wrong Paint Layer for this objectMapManager");
		}
	}
	
//	public static LocalMap getPaintFullTilesOnMapExcept(Point point, String paintLayer, int tile, int[][] exceptMap, boolean existedBefore, LocalMap oldMap) {
//		TileDimensions tileDimensions = TileDimensions.RowCol2x2;
////		int mapArray[][] = oldMap.getLocalMap();
//		LocalMap map = oldMap;
//		if (paintLayer.equals("tree1") || paintLayer.equals("overlayTree1")) {
//			for (int i = 0; i < GameStates.mapWidth; i++) {
//				for (int j = 0; j < GameStates.mapHeight; j++) {
//					if ((exceptMap[i][j] == 0 || !LocalMap.isPartOfHole(exceptMap[i][j])) && !existedBefore) {
//						if (i%4 == 0 && j%2 == 0) {
//							map.setUl(i, j, 0);
//							map.setUr(i, j, tile);
//							map.setDl(i, j, 0);
//							map.setDr(i, j, 0);
//							map.setIdByCornersObject(i, j, tile, tileDimensions);
//						} else if (i%4 == 1 && j%2 == 0){
//							map.setUl(i, j, tile);
//							map.setUr(i, j, 0);
//							map.setDl(i, j, 0);
//							map.setDr(i, j, 0);
//							map.setIdByCornersObject(i, j, tile, tileDimensions);
//						} else if (i%4 == 0 && j%2 == 1) {
//							map.setUl(i, j, 0);
//							map.setUr(i, j, 0);
//							map.setDl(i, j, 0);
//							map.setDr(i, j, tile);
//							map.setIdByCornersObject(i, j, tile, tileDimensions);
//						} else if (i%4 == 1 && j%2 == 1) {
//							map.setUl(i, j, 0);
//							map.setUr(i, j, 0);
//							map.setDl(i, j, tile);
//							map.setDr(i, j, 0);
//							map.setIdByCornersObject(i, j, tile, tileDimensions);
//						}
//					} else if (exceptMap[i][j] != 0 && LocalMap.isPartOfHole(exceptMap[i][j])) {
//						/** else only update the new holes */						
//						if (i%4 == 0 && j%2 == 1) {
//							map.setUl(i, j, 0);
//							map.setUr(i, j, 0);
//							map.setDl(i, j, 0);
//							map.setDr(i, j, 0);
//							map.setTile(i, j, 0);
//							map.setUl(i+1, j, 0);
//							map.setUr(i+1, j, 0);
//							map.setDl(i+1, j, 0);
//							map.setDr(i+1, j, 0);
//							map.setTile(i+1, j, 0);
//							map.setUl(i, j-1, 0);
//							map.setUr(i, j-1, 0);
//							map.setDl(i, j-1, 0);
//							map.setDr(i, j-1, 0);
//							map.setTile(i, j-1, 0);
//							map.setUl(i+1, j-1, 0);
//							map.setUr(i+1, j-1, 0);
//							map.setDl(i+1, j-1, 0);
//							map.setDr(i+1, j-1, 0);
//							map.setTile(i+1, j-1, 0);
//						}
//					}
//				}
//			}
//		} else if (paintLayer.equals("tree2") || paintLayer.equals("overlayTree2")) {
//			for (int i = 0; i < GameStates.mapWidth; i++) {
//				for (int j = 0; j < GameStates.mapHeight; j++) {
//					if ((exceptMap[i][j] == 0 || !LocalMap.isPartOfHole(exceptMap[i][j]))&& !existedBefore) {
//						if (i%4 == 2 && j%2 == 0) {
//							map.setUl(i, j, 0);
//							map.setUr(i, j, tile);
//							map.setDl(i, j, 0);
//							map.setDr(i, j, 0);
//							map.setIdByCornersObject(i, j, tile, tileDimensions);
//						} else if (i%4 == 3 && j%2 == 0){
//							map.setUl(i, j, tile);
//							map.setUr(i, j, 0);
//							map.setDl(i, j, 0);
//							map.setDr(i, j, 0);
//							map.setIdByCornersObject(i, j, tile, tileDimensions);
//						} else if (i%4 == 2 && j%2 == 1) {
//							map.setUl(i, j, 0);
//							map.setUr(i, j, 0);
//							map.setDl(i, j, 0);
//							map.setDr(i, j, tile);
//							map.setIdByCornersObject(i, j, tile, tileDimensions);
//						} else if (i%4 == 3 && j%2 == 1) {
//							map.setUl(i, j, 0);
//							map.setUr(i, j, 0);
//							map.setDl(i, j, tile);
//							map.setDr(i, j, 0);
//							map.setIdByCornersObject(i, j, tile, tileDimensions);
//						}
//					} else if (exceptMap[i][j] != 0 && LocalMap.isPartOfHole(exceptMap[i][j])) {
//						/** else only update the new holes */
//						if (i%4 == 2 && j%2 == 1) {
//							map.setUl(i, j, 0);
//							map.setUr(i, j, 0);
//							map.setDl(i, j, 0);
//							map.setDr(i, j, 0);
//							map.setTile(i, j, 0);
//							map.setUl(i+1, j, 0);
//							map.setUr(i+1, j, 0);
//							map.setDl(i+1, j, 0);
//							map.setDr(i+1, j, 0);
//							map.setTile(i+1, j, 0);
//							map.setUl(i, j-1, 0);
//							map.setUr(i, j-1, 0);
//							map.setDl(i, j-1, 0);
//							map.setDr(i, j-1, 0);
//							map.setTile(i, j-1, 0);
//							map.setUl(i+1, j-1, 0);
//							map.setUr(i+1, j-1, 0);
//							map.setDl(i+1, j-1, 0);
//							map.setDr(i+1, j-1, 0);
//							map.setTile(i+1, j-1, 0);
//						}
//					}
//				} 
//			}
//		} else {
//			// ERROR!!!
//			System.out.println("Wrong Paint Layer for this objectMapManager");
//		}
//		return map;
//	}
	
//	public static LocalMap getPaintFullTilesOnMapExcept(Point point, String paintLayer, int tile, int[][] exceptMap, boolean existedBefore, LocalMap oldMap) {
//		TileDimensions tileDimensions = TileDimensions.RowCol2x2;
////		int mapArray[][] = oldMap.getLocalMap();
//		LocalMap map = oldMap;
////		tile += GameStates.tileSetWidth;
//		if (paintLayer.equals("tree1") || paintLayer.equals("overlayTree1")) {
//			for (int i = 0; i < GameStates.mapWidth; i=i+4) {
//				for (int j = 0; j < GameStates.mapHeight; j= j+2) {
//					if ((exceptMap[i][j] == 0 || !LocalMap.isPartOfHole(exceptMap[i][j])) && !existedBefore) {
//						map.setUl(i, j, 0);
//						map.setUr(i, j, tile);
//						map.setDl(i, j, 0);
//						map.setDr(i, j, 0);
//						map.setIdByCornersObject(i, j, tile, tileDimensions);
//						map.setUl(i+1, j, tile);
//						map.setUr(i+1, j, 0);
//						map.setDl(i+1, j, 0);
//						map.setDr(i+1, j, 0);
//						map.setIdByCornersObject(i+1, j, tile, tileDimensions);
//						map.setUl(i, j+1, 0);
//						map.setUr(i, j+1, 0);
//						map.setDl(i, j+1, 0);
//						map.setDr(i, j+1, tile);
//						map.setIdByCornersObject(i, j+1, tile, tileDimensions);
//						map.setUl(i+1, j+1, 0);
//						map.setUr(i+1, j+1, 0);
//						map.setDl(i+1, j+1, tile);
//						map.setDr(i+1, j+1, 0);
//						map.setIdByCornersObject(i+1, j+1, tile, tileDimensions);
//					} else if (exceptMap[i][j] != 0 && LocalMap.isPartOfHole(exceptMap[i][j])) {
//						/** else only update the new holes */						
//						map.setUl(i, j, 0);
//						map.setUr(i, j, 0);
//						map.setDl(i, j, 0);
//						map.setDr(i, j, 0);
//						map.setTile(i, j, 0);
//						map.setUl(i+1, j, 0);
//						map.setUr(i+1, j, 0);
//						map.setDl(i+1, j, 0);
//						map.setDr(i+1, j, 0);
//						map.setTile(i+1, j, 0);
//						map.setUl(i, j+1, 0);
//						map.setUr(i, j+1, 0);
//						map.setDl(i, j+1, 0);
//						map.setDr(i, j+1, 0);
//						map.setTile(i, j+1, 0);
//						map.setUl(i+1, j+1, 0);
//						map.setUr(i+1, j+1, 0);
//						map.setDl(i+1, j+1, 0);
//						map.setDr(i+1, j+1, 0);
//						map.setTile(i+1, j+1, 0);
//					}
//				}
//			}
//		} else if (paintLayer.equals("tree2") || paintLayer.equals("overlayTree2")) {
//			for (int i = 2; i < GameStates.mapWidth; i = i+4) {
//				for (int j = 0; j < GameStates.mapHeight; j = j+2) {
//					if ((exceptMap[i][j] == 0 || !LocalMap.isPartOfHole(exceptMap[i][j]))&& !existedBefore) {
//						map.setUl(i, j, 0);
//						map.setUr(i, j, tile);
//						map.setDl(i, j, 0);
//						map.setDr(i, j, 0);
//						map.setIdByCornersObject(i, j, tile, tileDimensions);
//						map.setUl(i+1, j, tile);
//						map.setUr(i+1, j, 0);
//						map.setDl(i+1, j, 0);
//						map.setDr(i+1, j, 0);
//						map.setIdByCornersObject(i+1, j, tile, tileDimensions);
//						map.setUl(i, j+1, 0);
//						map.setUr(i, j+1, 0);
//						map.setDl(i, j+1, 0);
//						map.setDr(i, j+1, tile);
//						map.setIdByCornersObject(i, j+1, tile, tileDimensions);
//						map.setUl(i+1, j+1, 0);
//						map.setUr(i+1, j+1, 0);
//						map.setDl(i+1, j+1, tile);
//						map.setDr(i+1, j+1, 0);
//						map.setIdByCornersObject(i+1, j+1, tile, tileDimensions);
//					} else if (exceptMap[i][j] != 0 && LocalMap.isPartOfHole(exceptMap[i][j])) {
//						/** else only update the new holes */						
//						map.setUl(i, j, 0);
//						map.setUr(i, j, 0);
//						map.setDl(i, j, 0);
//						map.setDr(i, j, 0);
//						map.setTile(i, j, 0);
//						map.setUl(i+1, j, 0);
//						map.setUr(i+1, j, 0);
//						map.setDl(i+1, j, 0);
//						map.setDr(i+1, j, 0);
//						map.setTile(i+1, j, 0);
//						map.setUl(i, j+1, 0);
//						map.setUr(i, j+1, 0);
//						map.setDl(i, j+1, 0);
//						map.setDr(i, j+1, 0);
//						map.setTile(i, j+1, 0);
//						map.setUl(i+1, j+1, 0);
//						map.setUr(i+1, j+1, 0);
//						map.setDl(i+1, j+1, 0);
//						map.setDr(i+1, j+1, 0);
//						map.setTile(i+1, j+1, 0);
//					}
//				} 
//			}
//		} else {
//			// ERROR!!!
//			System.out.println("Wrong Paint Layer for this objectMapManager");
//		}
//		return map;
//	}
	
	/** paint the complete map with one tile type */
	public LocalMap getPaintFullTilesOnMap(Point point, String paintLayer, int tile) {
		Point moduloPoint = new Point(((point.x/GameStates.mapTotalWidth) % this.getMapWidth() +this.getMapWidth()) % this.getMapWidth() *GameStates.mapTotalWidth, ((point.y/GameStates.mapTotalHeight) % this.getMapHeight() +this.getMapHeight()) % this.getMapHeight() *GameStates.mapTotalHeight);
		int mapArray[][] = new int[GameStates.mapWidth][GameStates.mapHeight];
		LocalMap map = new LocalMap(mapArray, moduloPoint);
		TileDimensions tileDimensions = TileDimensions.RowCol2x2;
		if (paintLayer.equals("tree1") || paintLayer.equals("overlayTree1")) {
			for (int i = 0; i < GameStates.mapWidth; i++) {
				for (int j = 0; j < GameStates.mapHeight; j++) {
					if (i%4 == 0 && j%2 == 0) {
						map.setUl(i, j, 0);
						map.setUr(i, j, tile);
						map.setDl(i, j, 0);
						map.setDr(i, j, 0);
						map.setIdByCornersObject(i, j, tile, tileDimensions);
					} else if (i%4 == 1 && j%2 == 0){
						map.setUl(i, j, tile);
						map.setUr(i, j, 0);
						map.setDl(i, j, 0);
						map.setDr(i, j, 0);
						map.setIdByCornersObject(i, j, tile, tileDimensions);
					} else if (i%4 == 0 && j%2 == 1) {
						map.setUl(i, j, 0);
						map.setUr(i, j, 0);
						map.setDl(i, j, 0);
						map.setDr(i, j, tile);
						map.setIdByCornersObject(i, j, tile, tileDimensions);
					} else if (i%4 == 1 && j%2 == 1) {
						map.setUl(i, j, 0);
						map.setUr(i, j, 0);
						map.setDl(i, j, tile);
						map.setDr(i, j, 0);
						map.setIdByCornersObject(i, j, tile, tileDimensions);
					}
				}
			}
		} else if (paintLayer.equals("tree2") || paintLayer.equals("overlayTree2")) {
			for (int i = 0; i < GameStates.mapWidth; i++) {
				for (int j = 0; j < GameStates.mapHeight; j++) {
					if (i%4 == 2 && j%2 == 1) {
						map.setUl(i, j, 0);
						map.setUr(i, j, tile);
						map.setDl(i, j, 0);
						map.setDr(i, j, 0);
						map.setIdByCornersObject(i, j, tile, tileDimensions);
					} else if (i%4 == 3 && j%2 == 1){
						map.setUl(i, j, tile);
						map.setUr(i, j, 0);
						map.setDl(i, j, 0);
						map.setDr(i, j, 0);
						map.setIdByCornersObject(i, j, tile, tileDimensions);
					} else if (i%4 == 2 && j%2 == 0) {
						map.setUl(i, j, 0);
						map.setUr(i, j, 0);
						map.setDl(i, j, 0);
						map.setDr(i, j, tile);
						map.setIdByCornersObject(i, j, tile, tileDimensions);
					} else if (i%4 == 3 && j%2 == 0) {
						map.setUl(i, j, 0);
						map.setUr(i, j, 0);
						map.setDl(i, j, tile);
						map.setDr(i, j, 0);
						map.setIdByCornersObject(i, j, tile, tileDimensions);
					}
				} 
			}
		} else {
			// ERROR!!!
			System.out.println("Wrong Paint Layer for this objectMapManager");
		}
		return map;
	}
	
	public void setPaintLayer(String paintLayer) {
		this.paintLayer = paintLayer;
	}
	
	public String getPaintLayer() {
		return this.paintLayer;
	}
	
	public int checkCollision(Point point, int x, int y) {
//		int retValue = 0;
		Point p = new Point(((point.x/GameStates.mapTotalWidth) % this.getMapWidth() +this.getMapWidth()) % this.getMapWidth() *GameStates.mapTotalWidth, ((point.y/GameStates.mapTotalHeight) % this.getMapHeight() +this.getMapHeight()) % this.getMapHeight() *GameStates.mapTotalHeight);
		
		if (localMapList.containsKey(p)) {
			try {
				if (LocalMap.getCollisionValue(localMapList.get(p).getTile(x, y)) != 0) return localMapList.get(p).getTile(x, y);
				else ;//return false;
			} catch (NullPointerException e) {
				e.printStackTrace();
//				return false;
			}
//			return false;
		} 
		return 0;
	}
	
	public byte[] LocalMap2ByteArray(String playerName, String room, String paintLayer, Point p) {
		FileInputStream fileInputStream=null;
		File fileFolder = new File(GameStates.standardMapFolder+playerName+"/"+room+"/");
		if (fileFolder.exists()) {
			// OK, folder exists
		} else {
			boolean success = (new File(GameStates.standardMapFolder+playerName+"/"+room+"/")).mkdirs();
		    if (!success) {
		         // Directory creation failed
		    }
		}
        File file = new File(GameStates.standardMapFolder+playerName+"/"+room+"/"+paintLayer+"_"+p.x+"_"+p.y+".map");
        byte[] mapBytes = new byte[(int) file.length()];

        try {
            //convert file into array of bytes
	    fileInputStream = new FileInputStream(file);
	    fileInputStream.read(mapBytes);
	    fileInputStream.close();
 
//	    for (int i = 0; i < mapBytes.length; i++) {
//	       	System.out.print((char)mapBytes[i]);
//            }
 
//	    System.out.println("prepared File "+ GameStates.standardMapFolder+playerName+"/"+paintLayer+"_"+p.x+"_"+p.y+".map"+" with "+mapBytes.length+" bytes");
        } catch(Exception e){
        	e.printStackTrace();
        }
        return mapBytes;
	}
	
	
	public void addChangedList(Point p) {
		Point moduloPoint = new Point(((p.x/GameStates.mapTotalWidth) % this.getMapWidth()+this.getMapWidth()) % this.getMapWidth()*GameStates.mapTotalWidth, ((p.y/GameStates.mapTotalHeight) % this.getMapHeight()+this.getMapHeight())%this.getMapHeight()*GameStates.mapTotalHeight);
		if (!changedList.contains(moduloPoint)) this.changedList.add(moduloPoint);
	}
	
	public void removeStayList(Point p) {
		Point moduloPoint = new Point(((p.x/GameStates.mapTotalWidth) % this.getMapWidth()+this.getMapWidth()) % this.getMapWidth()*GameStates.mapTotalWidth, ((p.y/GameStates.mapTotalHeight) % this.getMapHeight()+this.getMapHeight())%this.getMapHeight()*GameStates.mapTotalHeight);
		if (!stayList.contains(moduloPoint)) this.stayList.remove(moduloPoint);
	}
	
	public void setChangedList(  ArrayList<Point> list) {
		this.changedList = list;
	}
	
	public ArrayList<Point> getChangedList() {
		return changedList;
	}
	
	public void emptyChangedList() {
		changedList = new ArrayList<Point>();
	}
	
	public void emptyStayList() {
		stayList = new ArrayList<Point>();
	}
	
	public void addStayList(Point p) {
		Point moduloPoint = new Point(((p.x/GameStates.mapTotalWidth) % this.getMapWidth()+this.getMapWidth()) % this.getMapWidth()*GameStates.mapTotalWidth, ((p.y/GameStates.mapTotalHeight) % this.getMapHeight()+this.getMapHeight())%this.getMapHeight()*GameStates.mapTotalHeight);
		if (!stayList.contains(moduloPoint)) this.stayList.add(moduloPoint);
	}
	
	public void setStayList(  ArrayList<Point> stayList) {
		this.stayList = stayList;
	}
	
	public ArrayList<Point> getStayList() {
		return stayList;
	}

	public void initLocalMapFileList(String playerName, String room) {
		ArrayList<String> listOfFiles = new ArrayList<String>();
		File folder = new File(GameStates.standardMapFolder+playerName+"/"+room);
		listOfFiles = MyUtil.listFilesForFolder(folder);
		for (String mapFile: listOfFiles) {
			/** get origins and save the maps under the corresponding origin */
			/** only load if its the right paintLayer */
			if (mapFile.startsWith(getPaintLayer())) { // && mapFile.contains("_"+x+"_") && mapFile.contains("_"+y+".")) {
				LocalMap lMap = loadMap(GameStates.standardMapFolder+playerName+"/"+room+"/"+mapFile);
//				localMapList.put(lMap.getOrigin(), lMap);
				localMapFileList.put(lMap.getOrigin(),GameStates.standardMapFolder+ playerName+"/"+room+"/"+mapFile);
//				pointList.add(lMap.getOrigin());
			}
		}
		
		/** folder for the preset tiles */
		folder = new File(GameStates.standardMapFolder+"Presets");
//		folder = new File(this.getClass().getResource(GameStates.resourcePathPresets).getPath());
				
		listOfFiles = MyUtil.listFilesForFolder(folder);
		for (String mapFile: listOfFiles) {
			/** get origins and save the maps under the corresponding origin */
			/** only load if its the right paintLayer */
			if (mapFile.startsWith(getPaintLayer())) { // && mapFile.contains("_"+x+"_") && mapFile.contains("_"+y+".")) {
//				LocalMap lMap = loadMap(GameStates.standardMapFolder+"Presets"+"/"+mapFile);
				localMapFileList.put(MyStrings.getPointFromMapFile(mapFile),GameStates.standardMapFolder+"Presets"+"/"+mapFile);
//				localMapFileList.put(MyStrings.getPointFromMapFile(mapFile),this.getClass().getResource(GameStates.resourcePathPresets).getPath()+mapFile);
			}
		}
	}
	
	public void putMapFileName(Point p, String fileName) {
		localMapFileList.put(p, fileName);
	}
	
	/**
	 * 
	 * @param interactionArea
	 * @return interactions, that reached the LIMIT
	 */
	public ArrayList<InteractionTile> interact(InteractionArea interactionArea) {
		ArrayList<InteractionTile> retList = new ArrayList<InteractionTile>();
		int[] interactValues;
		for (WorldLatticePosition wLP : interactionArea.getInfluencedInteractionTilePositions()) {
//			System.out.println("interact: x="+worldLatticePosition.getLocalX()+" y= "+worldLatticePosition.getLocalY());
			WorldLatticePosition worldLatticePosition = wLP;
			Point p = wLP.getMapCoordinates();
			Point moduloPoint = new Point(((p.x/GameStates.mapTotalWidth) % this.getMapWidth()+this.getMapWidth()) % this.getMapWidth()*GameStates.mapTotalWidth, ((p.y/GameStates.mapTotalHeight) % this.getMapHeight()+this.getMapHeight())%this.getMapHeight()*GameStates.mapTotalHeight);
			
			worldLatticePosition.setMapCoordinates(moduloPoint);
			
			if (localMapList.containsKey(worldLatticePosition.getMapCoordinates())) {		
				int tile = localMapList.get(worldLatticePosition.getMapCoordinates()).getLocalMap()[worldLatticePosition.getLocalX()][worldLatticePosition.getLocalY()];
				if (tile > 1) {
					/** calculate index, that is not null and is the TARGET of the interaction */
					TileType interactionIndex = LocalMap.getTileType(LocalMap.getPaintType(tile));
					
					InteractionArea realInteraction = new InteractionArea(interactionArea.getPosition(), new int[interactionArea.getValues().length], interactionArea.getWidth(), interactionArea.getHeight());
					realInteraction.getValues()[interactionIndex.ordinal()] = interactionArea.getValues()[interactionIndex.ordinal()];
					interactValues = InteractionManager.interact(realInteraction, worldLatticePosition);
//					System.out.println("wood " +interactValues[TileType.TREE.ordinal()]+" stone "+interactValues[TileType.STONE.ordinal()]);
					if (interactValues != null ) {
						if (interactionIndex == TileType.COLLECT) {
							retList.add(InteractionManager.interactionList.get(worldLatticePosition));
							InteractionManager.sound = "collect";
						} else {
							for (int i = 0; i < interactValues.length; i++) {
								/** if the limit is reached, add the interaction to the return List */
								if (interactValues[i] >= LocalMap.getTileLimit(LocalMap.getPaintType(tile) )) {  //GameStates.interactionLimit) {
									retList.add(InteractionManager.interactionList.get(worldLatticePosition));
								}
								
								if (TileType.values()[i] == TileType.STONE && interactValues[i] > 0) {
									InteractionManager.sound = "stone";
								}
								if (TileType.values()[i] == TileType.TREE && interactValues[i] > 0) {
									InteractionManager.sound = "wood";
								}
								if (TileType.values()[i] == TileType.EARTH && interactValues[i] > 0) {
									InteractionManager.sound = "soil";
								}
							}
						}
					
					}
				}	
			}
		}
		return retList;
	}
	
	public void setMapWidth(int mapWidth) {
		this.mapWidth = mapWidth;
	}
	
	public int getMapWidth() {
		return this.mapWidth;
	}
	
	public void setMapHeight(int mapHeight) {
		this.mapHeight = mapHeight;
	}
	
	public int getMapHeight() {
		return this.mapHeight;
	}
}
