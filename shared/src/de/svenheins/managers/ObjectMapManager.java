package de.svenheins.managers;

import java.awt.Point;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.svenheins.functions.MyUtil;
import de.svenheins.main.GameStates;
import de.svenheins.objects.LocalMap;

public class ObjectMapManager {
	public HashMap<Point, LocalMap> localMapList = new HashMap<Point, LocalMap>();
	
	public List<Point> pointList = new ArrayList<Point>();
	public String paintLayer;
	public ArrayList<Point> changedList = new ArrayList<Point>();
	
	public ObjectMapManager(String paintLayer) {
		this.paintLayer = paintLayer;
	}
	
	public void remove(Point point) {
		pointList.remove(point);
		localMapList.remove(point);
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
			return (LocalMap) localMapList.get(point);
//			return (Space) spaceList.values().toArray()[index];
		}
		catch(IndexOutOfBoundsException e){
			//System.out.println("Null-Object returned");
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
		return pointList.contains(point);
	}
	

	public void createMap(Point point) {
		int mapArray[][] = new int[GameStates.mapWidth][GameStates.mapHeight];
		LocalMap map = new LocalMap(mapArray, point);
		localMapList.put(map.getOrigin(), map);
		pointList.add(map.getOrigin());
	}
	
	public LocalMap createMapIfNonExistent(Point point) {
		if ( !pointList.contains(point)) {
			createMap(point);
		}
		return get(point);
	}
	
	
	public void adjustSurrounding(LocalMap localMap, int localX, int localY, int paintType) {
		
		setTileCorners(localMap, localX, localY-1, paintType);
		setTileCorners(localMap, localX-1, localY, paintType);
		setTileCorners(localMap, localX+1, localY, paintType);
		
		setTileCorners(localMap, localX-1, localY-1, paintType);
		setTileCorners(localMap, localX+1, localY-1, paintType);
		
		//setTileCorners(localMap, localX, localY);
		
//		setTileCorners(localMap, localX-1, localY+1, paintType);
//		setTileCorners(localMap, localX, localY+1, paintType);
//		setTileCorners(localMap, localX+1, localY+1, paintType);
	}
	
	
	public void deleteSurrounding(LocalMap localMap, int localX, int localY, int paintType) {
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
		
		/** delete all tiles in environment **/
		centerMap.setTile(centerTile.x, centerTile.y, 0 ); // new Tile(0, false, false, false, false));
		ulMap.setTile(ulTile.x, ulTile.y, 0 ); // new Tile(0, false, false, false, false));
		ulMap.setDr(ulTile.x, ulTile.y, 0 );
		uMap.setTile(uTile.x, uTile.y, 0 ); // new Tile(0, false, false, false, false));
		uMap.setDl(uTile.x, uTile.y, 0 );
		uMap.setDr(uTile.x, uTile.y, 0 );
		urMap.setTile(urTile.x, urTile.y, 0 ); // new Tile(0, false, false, false, false));
		uMap.setDl(urTile.x, urTile.y, 0 );
		lMap.setTile(lTile.x, lTile.y, 0 ); // new Tile(0, false, false, false, false));
		lMap.setUr(lTile.x, lTile.y, 0 );
		lMap.setDr(lTile.x, lTile.y, 0 );
		rMap.setTile(rTile.x, rTile.y, 0 ); // new Tile(0, false, false, false, false));
		rMap.setUl(rTile.x, rTile.y, 0 );
		rMap.setDl(rTile.x, rTile.y, 0 );
		dlMap.setTile(dlTile.x, dlTile.y, 0 ); // new Tile(0, false, false, false, false));
		dlMap.setUr(dlTile.x, dlTile.y, 0 );
		dMap.setTile(dTile.x, dTile.y, 0 ); // new Tile(0, false, false, false, false));
		dMap.setUr(dTile.x, dTile.y, 0 );
		dMap.setUl(dTile.x, dTile.y, 0 );
		drMap.setTile(drTile.x, drTile.y, 0 ); // new Tile(0, false, false, false, false));
		drMap.setUl(drTile.x, drTile.y, 0 );
		/** and adjust the surrounding */
		
		adjustSurrounding(uMap, uTile.x, uTile.y, paintType);
		adjustSurrounding(lMap, lTile.x, lTile.y, paintType);
		adjustSurrounding(rMap, rTile.x, rTile.y, paintType);
		adjustSurrounding(dMap, dTile.x, dTile.y, paintType);
		
		adjustSurrounding(ulMap, ulTile.x, ulTile.y, paintType);
		adjustSurrounding(urMap, urTile.x, urTile.y, paintType);		
		adjustSurrounding(dlMap, dlTile.x, dlTile.y, paintType);
		adjustSurrounding(drMap, drTile.x, drTile.y, paintType);
	}
	
	
	public void setTileCorners(LocalMap localMap, int localX, int localY, int paintType) {
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
	
		boolean dl = ((dlMap.getUr(dlTile.x, dlTile.y) != 0) || (dMap.getUl(dTile.x, dTile.y) != 0) || (lMap.getDr(lTile.x, lTile.y) != 0) );// && (dlTile.getId()!= 0);
		boolean dr = ((drMap.getUl(drTile.x, drTile.y) != 0) || (dMap.getUr(dTile.x, dTile.y) != 0) || (rMap.getDl(rTile.x, rTile.y) != 0) );// && (drTile.getId()!= 0);
		boolean ur = ((urMap.getDl(urTile.x, urTile.y) != 0) || (uMap.getDr(uTile.x, uTile.y) != 0) || (rMap.getUl(rTile.x, rTile.y) != 0) );// && (urTile.getId()!= 0);
		boolean ul = ((ulMap.getDr(ulTile.x, ulTile.y) != 0) || (uMap.getDl(uTile.x, uTile.y) != 0) || (lMap.getUr(lTile.x, lTile.y) != 0) );// && (ulTile.getId()!= 0);
		
//		boolean dl = ((dlMap.getUr(dlTile.x, dlTile.y) != -1) || dTile.hasUl() || lTile.hasDr());// && (dlTile.getId()!= 0);
//		boolean dr = (drTile.hasUl() || dTile.hasUr() || rTile.hasDl());// && (drTile.getId()!= 0);
//		boolean ur = (urTile.hasDl() || uTile.hasDr() || rTile.hasUl());// && (urTile.getId()!= 0);
//		boolean ul = (ulTile.hasDr() || uTile.hasDl() || lTile.hasUr());// && (ulTile.getId()!= 0);
		
//		Tile tile = new Tile(0,ul, ur, dl, dr);
//		tile.setIdByCorners();
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
		centerMap.setIdByCornersObject(centerTile.x, centerTile.y, paintType);
//		if (tile.getId() == 0) tile = null;
//		centerMap.setTile(centerTile.x, centerTile.y, tile);
	}
	
	
	public Point getUpperLeftMap(Point origin) {
		return new Point(origin.x-GameStates.mapWidth*GameStates.mapTileSetWidth, origin.y-GameStates.mapHeight*GameStates.mapTileSetHeight);
	}
	
	public Point getUpperMap(Point origin) {
		return new Point(origin.x, origin.y-GameStates.mapHeight*GameStates.mapTileSetHeight);
	}
	
	public Point getUpperRightMap(Point origin) {
		return new Point(origin.x+GameStates.mapWidth*GameStates.mapTileSetWidth, origin.y-GameStates.mapHeight*GameStates.mapTileSetHeight);
	}
	
	public Point getLeftMap(Point origin) {
		return new Point(origin.x-GameStates.mapWidth*GameStates.mapTileSetWidth, origin.y);
	}
	
	public Point getRightMap(Point origin) {
		return new Point(origin.x+GameStates.mapWidth*GameStates.mapTileSetWidth, origin.y);
	}
	
	public static Point getLowerLeftMap(Point origin) {
		return new Point(origin.x-GameStates.mapWidth*GameStates.mapTileSetWidth, origin.y+GameStates.mapHeight*GameStates.mapTileSetHeight);
	}
	
	public static Point getLowerMap(Point origin) {
		return new Point(origin.x, origin.y+GameStates.mapHeight*GameStates.mapTileSetHeight);
	}
	
	public Point getLowerRightMap(Point origin) {
		return new Point(origin.x+GameStates.mapWidth*GameStates.mapTileSetWidth, origin.y+GameStates.mapHeight*GameStates.mapTileSetHeight);
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
//			System.out.println("wrote "+localMap.getOrigin().x +" "+localMap.getOrigin().y);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	public LocalMap loadMap(String fileName) {
		try {
			FileInputStream datei=new FileInputStream(fileName);
			BufferedInputStream buf=new BufferedInputStream(datei);
			ObjectInputStream readStream = new ObjectInputStream(buf);
			int[][] localMap = (int[][]) readStream.readObject();
			int[][] ulArray = (int[][]) readStream.readObject();
			int[][] urArray = (int[][]) readStream.readObject();
			int[][] dlArray = (int[][]) readStream.readObject();
			int[][] drArray = (int[][]) readStream.readObject();
			Point origin = (Point) readStream.readObject();
//			String tileSetFileName = (String) readStream.readObject();
			String paintLayer = (String) readStream.readObject();
			readStream.close();
			
//			System.out.println(origin.x +" "+origin.y);
			return new LocalMap(localMap, ulArray, urArray, dlArray, drArray,origin, paintLayer);
			
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
	
	public void loadFromFileSystem(String playerName , int x, int y) {
		ArrayList<String> listOfFiles = new ArrayList<String>();
		File folder = new File(GameStates.standardMapFolder+playerName);
		listOfFiles = MyUtil.listFilesForFolder(folder);
		for (String mapFile: listOfFiles) {
			/** get origins and save the maps under the corresponding origin */
			/** only load if its the right paintLayer */
			if (mapFile.startsWith(getPaintLayer()) && mapFile.contains("_"+x+"_") && mapFile.contains("_"+y+".")) {
				LocalMap lMap = loadMap(GameStates.standardMapFolder+playerName+"/"+mapFile);
				localMapList.put(lMap.getOrigin(), lMap);
				pointList.add(lMap.getOrigin());
			}
		}
	}
	
	public void setPaintLayer(String paintLayer) {
		this.paintLayer = paintLayer;
	}
	
	public String getPaintLayer() {
		return this.paintLayer;
	}
	
	public boolean checkCollision(Point p, int x, int y) {
		if (localMapList.containsKey(p)) {
			if (localMapList.get(p).getTile(x, y) != 0) return true;
			else return false;
		} else return false;
	}
	
	public byte[] LocalMap2ByteArray(String playerName, String paintLayer, Point p) {
		FileInputStream fileInputStream=null;
        File file = new File(GameStates.standardMapFolder+playerName+"/"+paintLayer+"_"+p.x+"_"+p.y+".map");
        byte[] mapBytes = new byte[(int) file.length()];

        try {
            //convert file into array of bytes
	    fileInputStream = new FileInputStream(file);
	    fileInputStream.read(mapBytes);
	    fileInputStream.close();
 
//	    for (int i = 0; i < mapBytes.length; i++) {
//	       	System.out.print((char)mapBytes[i]);
//            }
 
	    System.out.println("prepared File "+ GameStates.standardMapFolder+playerName+"/"+paintLayer+"_"+p.x+"_"+p.y+".map"+" with "+mapBytes.length+" bytes");
        } catch(Exception e){
        	e.printStackTrace();
        }
        return mapBytes;
	}
	
	
	public void addChangedList(Point p) {
		if (!changedList.contains(p)) this.changedList.add(p);
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
}
