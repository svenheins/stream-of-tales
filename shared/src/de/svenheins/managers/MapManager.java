package de.svenheins.managers;

import java.awt.Point;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.svenheins.main.GameStates;
import de.svenheins.objects.LocalMap;
import de.svenheins.objects.Tile;

public class MapManager {
	public static HashMap<Point, LocalMap> localMapList = new HashMap<Point, LocalMap>();
	
	public static List<Point> pointList = new ArrayList<Point>();
	
	public static void remove(Point point) {
		pointList.remove(point);
		localMapList.remove(point);
	}
	
	
	public static boolean add(LocalMap localMap){
		if (localMapList.containsKey(localMap.getOrigin())) {
			return false;
		} else {
			localMapList.put(localMap.getOrigin(), localMap);
			pointList.add(localMap.getOrigin());
//			System.out.println("SpaceManager added new space: "+ space.getId());
			return true;
		}
	}
	
	public static LocalMap get(Point point){
		try {
			return (LocalMap) localMapList.get(point);
//			return (Space) spaceList.values().toArray()[index];
		}
		catch(IndexOutOfBoundsException e){
			//System.out.println("Null-Object returned");
			return null;
		}
	}
	
	public static boolean overwrite(LocalMap localMap){
		if (!localMapList.containsKey(localMap.getOrigin())) {
			/** do nothing if key is not yet set*/
			return false;
		} else {
			localMapList.put(localMap.getOrigin(), localMap);
//			idList.add(space.getId());
			return true;
		}
	}
	
	
	public static int size(){
		return localMapList.size();
	}

	
	public static boolean contains(LocalMap localMap) {
		return localMapList.containsValue(localMap);
	}
	
	public static boolean contains(Point point) {
		return pointList.contains(point);
	}
	

	public static void createMap(Point point) {
		Tile mapArray[][]=new Tile[GameStates.mapWidth][GameStates.mapHeight];
//		for (int i = 0; i < mapArray.length; i++) {
//			for (int j = 0; j < mapArray[0].length; j++) {
//				mapArray[i][j] = ;
//			}
//		}
		LocalMap map = new LocalMap(mapArray,"tilesets/maps/snow2.png", point);
//		System.out.println("point: "+point.x+" " +point.y);
		localMapList.put(map.getOrigin(), map);
		pointList.add(map.getOrigin());
	}
	
	public static LocalMap createMapIfNonExistent(Point point) {
		if ( !pointList.contains(point)) {
			createMap(point);
		}
		return get(point);
	}
	
	
	public static void adjustSurrounding(LocalMap localMap, int localX, int localY) {
//		if (localX <= 1) {
//			if (localY <= 1) {
//				/** upperLeft corner */
//				setTileCorners(localMap, localX+1, localY+1);
//			} else if (localY >= localMap.getLocalMap()[0].length-2) {
//				/** upperRight corner */
//				setTileCorners(localMap, localX-1, localY+1);
//			} else {
//				/** upper field */
//				setTileCorners(localMap, localX, localY+1);
//			}
//		} else if (localX >= localMap.getLocalMap().length-2) {
//			if (localY <= 1) {
//				/** downLeft corner */
//				setTileCorners(localMap, localX+1, localY-1);
//			} else if (localY >= localMap.getLocalMap()[0].length-2) {
//				/** downRight corner */
//				setTileCorners(localMap, localX-1, localY-1);
//			} else {
//				/** down field */
//				setTileCorners(localMap, localX, localY-1);
//			}
//		} else {
//			if (localY <= 1) {
//				/** left field */
//				setTileCorners(localMap, localX+1, localY);
//			} else if (localY >= localMap.getLocalMap()[0].length-2) {
//				/** right field */
//				setTileCorners(localMap, localX-1, localY);
//			} else {
//				/** middle field */
				
				setTileCorners(localMap, localX-1, localY-1);
				setTileCorners(localMap, localX, localY-1);
				setTileCorners(localMap, localX+1, localY-1);
				setTileCorners(localMap, localX-1, localY);
				//setTileCorners(localMap, localX, localY);
				setTileCorners(localMap, localX+1, localY);
				setTileCorners(localMap, localX-1, localY+1);
				setTileCorners(localMap, localX, localY+1);
				setTileCorners(localMap, localX+1, localY+1);
				
//				localMap.setTile(localX-1, localY-1, 45);
//				if ((localMap.getTile(localX, localY-2) == 62) || (localMap.getTile(localX, localY-1) == 62)) localMap.setTile(localX, localY-1, 62); else localMap.setTile(localX, localY-1, 46);
//				localMap.setTile(localX+1, localY-1, 47);
//				localMap.setTile(localX-1, localY, 61);
////				localMap.setTile(localX, localY, 46);
//				localMap.setTile(localX+1, localY, 63);
//				localMap.setTile(localX-1, localY+1, 77);
//				localMap.setTile(localX, localY+1, 78);
//				localMap.setTile(localX+1, localY+1, 79);
//			}
//		}
		
	}
	
	public static void setTileCorners(LocalMap localMap, int localX, int localY) {
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
		
		Tile ulTile;
		Tile uTile;
		Tile urTile;
		Tile lTile;
		Tile rTile;
		Point centerTile;
		Tile dlTile;
		Tile dTile;
		Tile drTile;
		
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
						ulTile = ulMap.getTile(maxIndexX, maxIndexY);
						uTile = uMap.getTile(0, maxIndexY);
						urTile = urMap.getTile(1, maxIndexY);
						lTile = lMap.getTile(maxIndexX, 0);
						centerTile = new Point(0, 0);
						rTile = rMap.getTile(1, 0);
						dlTile = dlMap.getTile(maxIndexX, 1);
						dTile = dMap.getTile(0, 1);
						drTile = drMap.getTile(1, 1);
					} else {
						// 2
						ulMap = lMap = createMapIfNonExistent(getUpperLeftMap(origin));
						uMap = urMap = centerMap = rMap = createMapIfNonExistent(getUpperMap(origin));
						dlMap = createMapIfNonExistent(getLeftMap(origin));
						dMap = drMap = localMap;
						ulTile = ulMap.getTile(maxIndexX, maxIndexY-1);
						uTile = uMap.getTile(0, maxIndexY-1);
						urTile = urMap.getTile(1, maxIndexY-1);
						lTile = lMap.getTile(maxIndexX, maxIndexY);
						centerTile = new Point(0, maxIndexY);
						rTile = rMap.getTile(1, maxIndexY);
						dlTile = dlMap.getTile(maxIndexX, 0);
						dTile = dMap.getTile(0, 0);
						drTile = drMap.getTile(1, 0);
					}
				} else if (localY >= GameStates.mapHeight-1) {
					if (localY == GameStates.mapHeight-1) {
						// 3
						ulMap = lMap = createMapIfNonExistent(getLeftMap(origin));
						uMap = urMap = centerMap = rMap = localMap;
						dlMap = createMapIfNonExistent(getLowerLeftMap(origin));
						dMap = drMap = createMapIfNonExistent(getLowerMap(origin));
						ulTile = ulMap.getTile(maxIndexX, maxIndexY-1);
						uTile = uMap.getTile(0, maxIndexY-1);
						urTile = urMap.getTile(1, maxIndexY-1);
						lTile = lMap.getTile(maxIndexX, maxIndexY);
						centerTile = new Point(0, maxIndexY);
						rTile = rMap.getTile(1, maxIndexY);
						dlTile = dlMap.getTile(maxIndexX, 0);
						dTile = dMap.getTile(0, 0);
						drTile = drMap.getTile(1, 0);
					} else {
						// 4
						ulMap = createMapIfNonExistent(getLeftMap(origin));
						uMap = urMap = localMap;
						lMap = dlMap = createMapIfNonExistent(getLowerLeftMap(origin));
						centerMap = rMap = dMap = drMap = createMapIfNonExistent(getLowerMap(origin));
						ulTile = ulMap.getTile(maxIndexX, maxIndexY);
						uTile = uMap.getTile(0, maxIndexY);
						urTile = urMap.getTile(1, maxIndexY);
						lTile = lMap.getTile(maxIndexX, 0);
						centerTile = new Point(0, 0);
						rTile = rMap.getTile(1, 0);
						dlTile = dlMap.getTile(maxIndexX, 1);
						dTile = dMap.getTile(0, 1);
						drTile = drMap.getTile(1, 1);
					}
				} else {
					// 5
					ulMap = lMap = dlMap = createMapIfNonExistent(getLeftMap(origin));
					uMap = urMap = centerMap = rMap = dMap = drMap = localMap;
					ulTile = ulMap.getTile(maxIndexX, localY-1);
					uTile = uMap.getTile(0, localY-1);
					urTile = urMap.getTile(1, localY-1);
					lTile = lMap.getTile(maxIndexX, localY);
					centerTile = new Point(0, localY);
					rTile = rMap.getTile(1, localY);
					dlTile = dlMap.getTile(maxIndexX, localY+1);
					dTile = dMap.getTile(0, localY+1);
					drTile = drMap.getTile(1, localY+1);
				}
			} else {
				if (localY <= 0) {
					if (localY == 0) {
						// 6
						ulMap = uMap = createMapIfNonExistent(getUpperLeftMap(origin));
						urMap = createMapIfNonExistent(getUpperMap(origin));
						lMap = centerMap = dlMap = dMap = createMapIfNonExistent(getLeftMap(origin));
						rMap = drMap = localMap;
						ulTile = ulMap.getTile(maxIndexX-1, maxIndexY);
						uTile = uMap.getTile(maxIndexX, maxIndexY);
						urTile = urMap.getTile(0, maxIndexY);
						lTile = lMap.getTile(maxIndexX-1, 0);
						centerTile = new Point(maxIndexX, 0);
						rTile = rMap.getTile(0, 0);
						dlTile = dlMap.getTile(maxIndexX-1, 1);
						dTile = dMap.getTile(maxIndexX, 1);
						drTile = drMap.getTile(0, 1);
					} else {
						// 7
						ulMap = uMap = lMap = centerMap = createMapIfNonExistent(getUpperLeftMap(origin));
						urMap = rMap = createMapIfNonExistent(getUpperMap(origin));
						dlMap = dMap = createMapIfNonExistent(getLeftMap(origin));
						drMap = localMap;
						ulTile = ulMap.getTile(maxIndexX-1, maxIndexY-1);
						uTile = uMap.getTile(maxIndexX, maxIndexY-1);
						urTile = urMap.getTile(0, maxIndexY-1);
						lTile = lMap.getTile(maxIndexX-1, maxIndexY);
						centerTile = new Point(maxIndexX, maxIndexY);
						rTile = rMap.getTile(0, maxIndexY);
						dlTile = dlMap.getTile(maxIndexX-1, 0);
						dTile = dMap.getTile(maxIndexX, 0);
						drTile = drMap.getTile(0, 0);
					}
				} else if (localY >= GameStates.mapHeight-1) {
					if (localY == GameStates.mapHeight-1) {
						// 8
						ulMap = uMap = lMap = centerMap = createMapIfNonExistent(getLeftMap(origin));
						urMap = rMap = localMap;
						dlMap = dMap = createMapIfNonExistent(getLowerLeftMap(origin));
						drMap = createMapIfNonExistent(getLowerMap(origin));
						ulTile = ulMap.getTile(maxIndexX-1, maxIndexY-1);
						uTile = uMap.getTile(maxIndexX, maxIndexY-1);
						urTile = urMap.getTile(0, maxIndexY-1);
						lTile = lMap.getTile(maxIndexX-1, maxIndexY);
						centerTile = new Point(maxIndexX, maxIndexY);
						rTile = rMap.getTile(0, maxIndexY);
						dlTile = dlMap.getTile(maxIndexX-1, 0);
						dTile = dMap.getTile(maxIndexX, 0);
						drTile = drMap.getTile(0, 0);
					} else {
						// 9
						ulMap = uMap = createMapIfNonExistent(getLeftMap(origin));
						urMap = localMap;
						lMap = centerMap = dlMap = dMap = createMapIfNonExistent(getLowerLeftMap(origin));
						rMap = drMap = createMapIfNonExistent(getLowerMap(origin));
						ulTile = ulMap.getTile(maxIndexX-1, maxIndexY);
						uTile = uMap.getTile(maxIndexX, maxIndexY);
						urTile = urMap.getTile(0, maxIndexY);
						lTile = lMap.getTile(maxIndexX-1, 0);
						centerTile = new Point(maxIndexX, 0);
						rTile = rMap.getTile(0, 0);
						dlTile = dlMap.getTile(maxIndexX-1, 1);
						dTile = dMap.getTile(maxIndexX, 1);
						drTile = drMap.getTile(0, 1);
					}
				} else {
					// 10
					ulMap = uMap = lMap = centerMap = dlMap = dMap = createMapIfNonExistent(getLeftMap(origin));
					urMap = rMap = drMap = localMap;
					ulTile = ulMap.getTile(maxIndexX-1, localY-1);
					uTile = uMap.getTile(maxIndexX, localY-1);
					urTile = urMap.getTile(0, localY-1);
					lTile = lMap.getTile(maxIndexX-1, localY);
					centerTile = new Point(maxIndexX, localY);
					rTile = rMap.getTile(0, localY);
					dlTile = dlMap.getTile(maxIndexX-1, localY+1);
					dTile = dMap.getTile(maxIndexX, localY+1);
					drTile = drMap.getTile(0, localY+1);
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
						ulTile = ulMap.getTile(maxIndexX-1, maxIndexY);
						uTile = uMap.getTile(maxIndexX, maxIndexY);
						urTile = urMap.getTile(0, maxIndexY);
						lTile = lMap.getTile(maxIndexX-1, 0);
						centerTile = new Point(maxIndexX, 0);
						rTile = rMap.getTile(0, 0);
						dlTile = dlMap.getTile(maxIndexX-1, 1);
						dTile = dMap.getTile(maxIndexX, 1);
						drTile = drMap.getTile(0, 1);
					} else {
						// 12
						ulMap = uMap = lMap = centerMap = createMapIfNonExistent(getUpperMap(origin));
						urMap = rMap = createMapIfNonExistent(getUpperRightMap(origin));
						dlMap = dMap = localMap;
						drMap = createMapIfNonExistent(getLeftMap(origin));
						ulTile = ulMap.getTile(maxIndexX-1, maxIndexY-1);
						uTile = uMap.getTile(maxIndexX, maxIndexY-1);
						urTile = urMap.getTile(0, maxIndexY-1);
						lTile = lMap.getTile(maxIndexX-1, maxIndexY);
						centerTile = new Point(maxIndexX, maxIndexY);
						rTile = rMap.getTile(0, maxIndexY);
						dlTile = dlMap.getTile(maxIndexX-1, 0);
						dTile = dMap.getTile(maxIndexX, 0);
						drTile = drMap.getTile(0, 0);
					}
				} else if (localY >= GameStates.mapHeight-1) {
					if (localY == GameStates.mapHeight-1) {
						// 13
						ulMap = uMap = lMap = centerMap = localMap;
						urMap = rMap = createMapIfNonExistent(getRightMap(origin));
						dlMap = dMap = createMapIfNonExistent(getLowerMap(origin));
						drMap = createMapIfNonExistent(getLowerRightMap(origin));
						ulTile = ulMap.getTile(maxIndexX-1, maxIndexY-1);
						uTile = uMap.getTile(maxIndexX, maxIndexY-1);
						urTile = urMap.getTile(0, maxIndexY-1);
						lTile = lMap.getTile(maxIndexX-1, maxIndexY);
						centerTile = new Point(maxIndexX, maxIndexY);
						rTile = rMap.getTile(0, maxIndexY);
						dlTile = dlMap.getTile(maxIndexX-1, 0);
						dTile = dMap.getTile(maxIndexX, 0);
						drTile = drMap.getTile(0, 0);
					} else {
						// 14
						ulMap = uMap = localMap;
						urMap = createMapIfNonExistent(getRightMap(origin));
						lMap = centerMap = dlMap = dMap = createMapIfNonExistent(getLowerMap(origin));
						rMap = drMap = createMapIfNonExistent(getLowerRightMap(origin));
						ulTile = ulMap.getTile(maxIndexX-1, maxIndexY);
						uTile = uMap.getTile(maxIndexX, maxIndexY);
						urTile = urMap.getTile(0, maxIndexY);
						lTile = lMap.getTile(maxIndexX-1, 0);
						centerTile = new Point(maxIndexX, 0);
						rTile = rMap.getTile(0, 0);
						dlTile = dlMap.getTile(maxIndexX-1, 1);
						dTile = dMap.getTile(maxIndexX, 1);
						drTile = drMap.getTile(0, 1);
					}
				} else {
					// 15
					ulMap = uMap = lMap = centerMap = dlMap = dMap = localMap;
					urMap = rMap = drMap = createMapIfNonExistent(getRightMap(origin));
					ulTile = ulMap.getTile(maxIndexX-1, localY-1);
					uTile = uMap.getTile(maxIndexX, localY-1);
					urTile = urMap.getTile(0, localY-1);
					lTile = lMap.getTile(maxIndexX-1, localY);
					centerTile = new Point(maxIndexX, localY);
					rTile = rMap.getTile(0, localY);
					dlTile = dlMap.getTile(maxIndexX-1, localY+1);
					dTile = dMap.getTile(maxIndexX, localY+1);
					drTile = drMap.getTile(0, localY+1);
				}
			} else {
				if (localY <= 0) {
					if (localY == 0) {
						// 16
						ulMap = createMapIfNonExistent(getUpperMap(origin));
						uMap = urMap = createMapIfNonExistent(getUpperRightMap(origin));
						lMap = dlMap = localMap;
						centerMap = rMap = dMap = drMap = createMapIfNonExistent(getRightMap(origin));
						ulTile = ulMap.getTile(maxIndexX, maxIndexY);
						uTile = uMap.getTile(0, maxIndexY);
						urTile = urMap.getTile(1, maxIndexY);
						lTile = lMap.getTile(maxIndexX, 0);
						centerTile = new Point(0, 0);
						rTile = rMap.getTile(1, 0);
						dlTile = dlMap.getTile(maxIndexX, 1);
						dTile = dMap.getTile(0, 1);
						drTile = drMap.getTile(1, 1);
					} else {
						// 17
						ulMap = lMap = createMapIfNonExistent(getUpperMap(origin));
						uMap = urMap = centerMap = rMap = createMapIfNonExistent(getUpperRightMap(origin));
						dlMap = localMap;
						dMap = drMap = createMapIfNonExistent(getRightMap(origin));
						ulTile = ulMap.getTile(maxIndexX, maxIndexY-1);
						uTile = uMap.getTile(0, maxIndexY-1);
						urTile = urMap.getTile(1, maxIndexY-1);
						lTile = lMap.getTile(maxIndexX, maxIndexY);
						centerTile = new Point(0, maxIndexY);
						rTile = rMap.getTile(1, maxIndexY);
						dlTile = dlMap.getTile(maxIndexX, 0);
						dTile = dMap.getTile(0, 0);
						drTile = drMap.getTile(1, 0);
					}
				} else if (localY >= GameStates.mapHeight-1) {
					if (localY == GameStates.mapHeight-1) {
						// 18
						ulMap = lMap = localMap;
						uMap = urMap = centerMap = rMap = createMapIfNonExistent(getRightMap(origin));
						dlMap = createMapIfNonExistent(getLowerMap(origin));
						dMap = drMap = createMapIfNonExistent(getLowerRightMap(origin));
						ulTile = ulMap.getTile(maxIndexX, maxIndexY-1);
						uTile = uMap.getTile(0, maxIndexY-1);
						urTile = urMap.getTile(1, maxIndexY-1);
						lTile = lMap.getTile(maxIndexX, maxIndexY);
						centerTile = new Point(0, maxIndexY);
						rTile = rMap.getTile(1, maxIndexY);
						dlTile = dlMap.getTile(maxIndexX, 0);
						dTile = dMap.getTile(0, 0);
						drTile = drMap.getTile(1, 0);
					} else {
						// 19
						ulMap = localMap;
						uMap = urMap = createMapIfNonExistent(getRightMap(origin));
						lMap = dlMap = createMapIfNonExistent(getLowerMap(origin));
						centerMap = rMap = dMap = drMap = createMapIfNonExistent(getLowerRightMap(origin));
						ulTile = ulMap.getTile(maxIndexX, maxIndexY);
						uTile = uMap.getTile(0, maxIndexY);
						urTile = urMap.getTile(1, maxIndexY);
						lTile = lMap.getTile(maxIndexX, 0);
						centerTile = new Point(0, 0);
						rTile = rMap.getTile(1, 0);
						dlTile = dlMap.getTile(maxIndexX, 1);
						dTile = dMap.getTile(0, 1);
						drTile = drMap.getTile(1, 1);
					}
				} else {
					// 20
					ulMap = lMap = dlMap = localMap;
					uMap = urMap = centerMap = rMap = dMap = drMap = createMapIfNonExistent(getRightMap(origin)); 
					ulTile = ulMap.getTile(maxIndexX, localY-1);
					uTile = uMap.getTile(0, localY-1);
					urTile = urMap.getTile(1, localY-1);
					lTile = lMap.getTile(maxIndexX, localY);
					centerTile = new Point(0, localY);
					rTile = rMap.getTile(1, localY);
					dlTile = dlMap.getTile(maxIndexX, localY+1);
					dTile = dMap.getTile(0, localY+1);
					drTile = drMap.getTile(1, localY+1);
				}
			}
		} else {
			if (localY <= 0) {
				if (localY == 0) {
					// 21
					ulMap = uMap = urMap = createMapIfNonExistent(getUpperMap(origin));
					lMap = centerMap = rMap = dlMap = dMap = drMap = localMap;
					ulTile = ulMap.getTile(localX-1, maxIndexY);
					uTile = uMap.getTile(localX, maxIndexY);
					urTile = urMap.getTile(localX+1, maxIndexY);
					lTile = lMap.getTile(localX-1, 0);
					centerTile = new Point(localX, 0);
					rTile = rMap.getTile(localX+1, 0);
					dlTile = dlMap.getTile(localX-1, 1);
					dTile = dMap.getTile(localX, 1);
					drTile = drMap.getTile(localX+1, 1);
				} else {
					// 22
					ulMap = uMap = urMap = lMap = centerMap = rMap = createMapIfNonExistent(getUpperMap(origin));
					dlMap = dMap = drMap = localMap;
					ulTile = ulMap.getTile(localX-1, maxIndexY-1);
					uTile = uMap.getTile(localX, maxIndexY-1);
					urTile = urMap.getTile(localX+1, maxIndexY-1);
					lTile = lMap.getTile(localX-1, maxIndexY);
					centerTile = new Point(localX, maxIndexY);
					rTile = rMap.getTile(localX+1, maxIndexY);
					dlTile = dlMap.getTile(localX-1, 0);
					dTile = dMap.getTile(localX, 0);
					drTile = drMap.getTile(localX+1, 0);
				}
			} else if (localY >= GameStates.mapHeight-1) {
				if (localY == GameStates.mapHeight-1) {
					// 23
					ulMap = uMap = urMap = lMap = centerMap = rMap = localMap;
					dlMap = dMap = drMap = createMapIfNonExistent(getLowerMap(origin));
					ulTile = ulMap.getTile(localX-1, maxIndexY-1);
					uTile = uMap.getTile(localX, maxIndexY-1);
					urTile = urMap.getTile(localX+1, maxIndexY-1);
					lTile = lMap.getTile(localX-1, maxIndexY);
					centerTile = new Point(localX, maxIndexY);
					rTile = rMap.getTile(localX+1, maxIndexY);
					dlTile = dlMap.getTile(localX-1, 0);
					dTile = dMap.getTile(localX, 0);
					drTile = drMap.getTile(localX+1, 0);
				} else {
					// 24
					ulMap = uMap = urMap = localMap;
					lMap = centerMap = rMap = dlMap = dMap = drMap = createMapIfNonExistent(getLowerMap(origin));
					ulTile = ulMap.getTile(localX-1, maxIndexY);
					uTile = uMap.getTile(localX, maxIndexY);
					urTile = urMap.getTile(localX+1, maxIndexY);
					lTile = lMap.getTile(localX-1, 0);
					centerTile = new Point(localX, 0);
					rTile = rMap.getTile(localX+1, 0);
					dlTile = dlMap.getTile(localX-1, 1);
					dTile = dMap.getTile(localX, 1);
					drTile = drMap.getTile(localX+1, 1);
				}
			} else {
				// 25
				ulMap = uMap = urMap = lMap = centerMap = rMap = dlMap = dMap = drMap = localMap;
				ulTile = ulMap.getTile(localX-1,localY-1);
				uTile = uMap.getTile(localX, localY-1);
				urTile = urMap.getTile(localX+1, localY-1);
				lTile = lMap.getTile(localX-1, localY);
				centerTile = new Point(localX, localY);
				rTile = rMap.getTile(localX+1, localY);
				dlTile = dlMap.getTile(localX-1, localY+1);
				dTile = dMap.getTile(localX, localY+1);
				drTile = drMap.getTile(localX+1, localY+1);
			}
		}
		
		
//		if (localMap.getTile(localX-1, localY-1) == null) localMap.setTile(localX-1, localY-1, new Tile(0, false, false, false, false ));
//		if (localMap.getTile(localX+1, localY+1) == null) localMap.setTile(localX+1, localY+1, new Tile(0, false, false, false, false ));
//		if (localMap.getTile(localX-1, localY+1) == null) localMap.setTile(localX-1, localY+1, new Tile(0, false, false, false, false ));
//		if (localMap.getTile(localX+1, localY-1) == null) localMap.setTile(localX+1, localY-1, new Tile(0, false, false, false, false ));
//		if (localMap.getTile(localX, localY-1) == null) localMap.setTile(localX, localY-1, new Tile(0, false, false, false, false ));
//		if (localMap.getTile(localX, localY+1) == null) localMap.setTile(localX, localY+1, new Tile(0, false, false, false, false ));
//		if (localMap.getTile(localX+1, localY) == null) localMap.setTile(localX+1, localY, new Tile(0, false, false, false, false ));
//		if (localMap.getTile(localX-1, localY) == null) localMap.setTile(localX-1, localY, new Tile(0, false, false, false, false ));
//		boolean dl = localMap.getTile(localX-1, localY+1).hasUr() || localMap.getTile(localX, localY+1).hasUl() || localMap.getTile(localX-1, localY).hasDr();
//		boolean dr = localMap.getTile(localX+1, localY+1).hasUl() || localMap.getTile(localX, localY+1).hasUr() || localMap.getTile(localX+1, localY).hasDl();
//		boolean ur = localMap.getTile(localX+1, localY-1).hasDl() || localMap.getTile(localX, localY-1).hasDr() || localMap.getTile(localX+1, localY).hasUl();
//		boolean ul = localMap.getTile(localX-1, localY-1).hasDr() || localMap.getTile(localX, localY-1).hasDl() || localMap.getTile(localX-1, localY).hasUr();
		
		boolean dl = dlTile.hasUr() || dTile.hasUl() || lTile.hasDr();
		boolean dr = drTile.hasUl() || dTile.hasUr() || rTile.hasDl();
		boolean ur = urTile.hasDl() || uTile.hasDr() || rTile.hasUl();
		boolean ul = ulTile.hasDr() || uTile.hasDl() || lTile.hasUr();
		
		Tile tile = new Tile(0,ul, ur, dl, dr);
		tile.setIdByCorners();
		centerMap.setTile(centerTile.x, centerTile.y, tile);
	}
	
	
	public static Point getUpperLeftMap(Point origin) {
		return new Point(origin.x-GameStates.mapWidth*GameStates.mapTileSetWidth, origin.y-GameStates.mapHeight*GameStates.mapTileSetHeight);
	}
	
	public static Point getUpperMap(Point origin) {
		return new Point(origin.x, origin.y-GameStates.mapHeight*GameStates.mapTileSetHeight);
	}
	
	public static Point getUpperRightMap(Point origin) {
		return new Point(origin.x+GameStates.mapWidth*GameStates.mapTileSetWidth, origin.y-GameStates.mapHeight*GameStates.mapTileSetHeight);
	}
	
	public static Point getLeftMap(Point origin) {
		return new Point(origin.x-GameStates.mapWidth*GameStates.mapTileSetWidth, origin.y);
	}
	
	public static Point getRightMap(Point origin) {
		return new Point(origin.x+GameStates.mapWidth*GameStates.mapTileSetWidth, origin.y);
	}
	
	public static Point getLowerLeftMap(Point origin) {
		return new Point(origin.x-GameStates.mapWidth*GameStates.mapTileSetWidth, origin.y+GameStates.mapHeight*GameStates.mapTileSetHeight);
	}
	
	public static Point getLowerMap(Point origin) {
		return new Point(origin.x, origin.y+GameStates.mapHeight*GameStates.mapTileSetHeight);
	}
	
	public static Point getLowerRightMap(Point origin) {
		return new Point(origin.x+GameStates.mapWidth*GameStates.mapTileSetWidth, origin.y+GameStates.mapHeight*GameStates.mapTileSetHeight);
	}
	
//	/** compare the idList with a given int[] and return the difference */
//	public static void createSpacesFromArray(Space[] spaceArray) {
//		if(spaceArray.length > 0) {
//			for (int i =0; i< spaceArray.length; i++) {
//				spaceList.put(spaceArray[i].getId(), spaceArray[i]);
//				if (!idList.contains(spaceArray[i].getId())) idList.add(spaceArray[i].getId());
//			}
//		}
//	}
	
	public static void emptyAll() {
		localMapList = new HashMap<Point, LocalMap>();
//		sortedSpaceList = new ArrayList<Space>();
		pointList = new ArrayList<Point>();
	}

	
//	/** get Maximum of ids */
//	public static BigInteger getMax(){
//		BigInteger tempMax = BigInteger.valueOf(-1);
//		for(BigInteger bId : idList) {
//			/** if we found a new maximum replace the old one **/
//			if (bId.compareTo(tempMax) > 0) {
//				tempMax = bId;
//			}
//		}
//		return tempMax;
//	}
	
//	/**
//     * Edits the addon parameters of a space
//     * 
//     * @param id: entity to edit
//     * @return {@code true} if the entity was edited with success
//     */
//    public static boolean editSpaceAddons(BigInteger id, String textureName, int[] rgb, float trans, int filled, float scale, float area) {
//       if ( spaceList.containsKey(id)) {
//    	   Space space = spaceList.get(id);
////    	   space.setX(state[0]);
////    	   space.setY(state[1]);
//    	   space.setTexture(textureName);
//    	   System.out.println("Space "+id +" gets the texture "+textureName);
//    	   space.setRGB(rgb);
//    	   space.setTrans(trans);
//    	   if (filled == 0) space.setFilled(false); else space.setFilled(true);
//    	   space.scale(scale);
//    	   space.setArea(area);
//    	   //entities.put(id, entity);
//    	   return true;
//       } else {
//    	   return false;
//       }
//    	   
//    }
}
