package de.svenheins.managers;

import java.awt.Point;
import java.awt.Rectangle;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.svenheins.main.GameStates;
import de.svenheins.objects.Light;

public class LightManager {
	public static HashMap<BigInteger, Light> lightList = new HashMap<BigInteger, Light>();
	public static List<BigInteger> idList = new ArrayList<BigInteger>();
	
	public static void remove(BigInteger index) throws IllegalArgumentException {
		try {
			lightList.remove(index);
			idList.remove(index);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
		
	}
	
	public static boolean add(Light light) {
		if (lightList.containsKey(light.getId())) {
			return false;
		} else {
			lightList.put(light.getId(), light);
			idList.add(light.getId());
			return true;
		}
	}
	
	public static Light get(BigInteger index){
		try {
			return (Light) lightList.get(index);
		}
		catch(IndexOutOfBoundsException e){
			return null;
		}
		
	}
	
	public static boolean overwrite(Light light){
		if (!lightList.containsKey(light.getId())) {
			/** do nothing if key is not yet set*/
			return false;
		} else {
			lightList.put(light.getId(), light);
			idList.add(light.getId());
			return true;
		}
	}
	
	public static int size(){
		return lightList.size();
	}
	
	public static boolean contains(Light light) {
		return lightList.containsValue(light);
	}
	
	public static ArrayList<Light> getLightsOfRegion(Rectangle rect) {
		ArrayList<Light> retLightList = new ArrayList<Light>();
		rect.setBounds(rect.x, rect.y, rect.width-GameStates.mapTileSetWidth, rect.height-GameStates.mapTileSetHeight);
		for (BigInteger id: idList) {
			if (rect.contains(new Point (lightList.get(id).getLocation().x, lightList.get(id).getLocation().y ))) {
				retLightList.add(lightList.get(id));
			}
		}
		return retLightList;
	}
	
	
	/** get the LightMap (int Array) in a specific Region (rectangle) */
	public static int[][] getLightMap(Rectangle rect) {
		/** the retArray has to cover whole the rectangle */
		int retArray[][] = new int[(int) Math.ceil(rect.width/GameStates.lightTileWidth)][(int) Math.ceil(rect.height/GameStates.lightTileHeight)];
		ArrayList<Point> indize = new ArrayList<Point>();
		for (Light light : getLightsOfRegion(rect)) {
			Point p = new Point((int) Math.floor((float) (light.getLocation().x-rect.x)/GameStates.lightTileWidth), (int) Math.floor((float)(light.getLocation().y-rect.y)/GameStates.lightTileHeight));
			indize.add(p);
			retArray[p.x][p.y] = light.getBrightness();
		}
		while(!indize.isEmpty()) {
			if (retArray[indize.get(0).x][indize.get(0).y] > 1) {
				int actualValue = retArray[indize.get(0).x][indize.get(0).y] - 1;
				if (indize.get(0).y > 0) {
					//  upper
					if ((retArray[indize.get(0).x][indize.get(0).y-1]) == 0) {
						retArray[indize.get(0).x][indize.get(0).y-1] = actualValue;
						if (actualValue > 1)
						indize.add(new Point(indize.get(0).x, indize.get(0).y-1));
					}				
				}
				if (indize.get(0).y < retArray[0].length-1) {
					// lower
					if ((retArray[indize.get(0).x][indize.get(0).y+1]) == 0) {
						retArray[indize.get(0).x][indize.get(0).y+1] = actualValue;
						if (actualValue > 1)
						indize.add(new Point(indize.get(0).x, indize.get(0).y+1));
					}
				}
				if (indize.get(0).x > 0 ) {
					// left
					if ((retArray[indize.get(0).x-1][indize.get(0).y]) == 0) {
						retArray[indize.get(0).x-1][indize.get(0).y] = actualValue;
						if (actualValue > 1)
						indize.add(new Point(indize.get(0).x-1, indize.get(0).y));
					}
				}
				if (indize.get(0).x < retArray.length-1 ) {
					// right
					if ((retArray[indize.get(0).x+1][indize.get(0).y]) == 0) {
						retArray[indize.get(0).x+1][indize.get(0).y] = actualValue;
						if (actualValue > 1)
						indize.add(new Point(indize.get(0).x+1, indize.get(0).y));
					}
				}
				if (actualValue >= GameStates.maxLightIntenityCutoff-1) {
					/** additionally add corners */
					if ( (indize.get(0).y > 0) && (indize.get(0).x > 0 )) {
						// upper left
						if ((retArray[indize.get(0).x-1][indize.get(0).y-1]) == 0) {
							retArray[indize.get(0).x-1][indize.get(0).y-1] = actualValue;
							if (actualValue > 1)
							indize.add(new Point(indize.get(0).x-1, indize.get(0).y-1));
						}
					}
					if ( (indize.get(0).y < retArray[0].length-1) && (indize.get(0).x > 0 )) {
						// lower left
						if ((retArray[indize.get(0).x-1][indize.get(0).y+1]) == 0) {
							retArray[indize.get(0).x-1][indize.get(0).y+1] = actualValue;
							if (actualValue > 1)
							indize.add(new Point(indize.get(0).x-1, indize.get(0).y+1));
						}
					}
					if ( (indize.get(0).y < retArray[0].length-1) && (indize.get(0).x < retArray.length-1 )) {
						// lower right
						if ((retArray[indize.get(0).x+1][indize.get(0).y+1]) == 0) {
							retArray[indize.get(0).x+1][indize.get(0).y+1] = actualValue;
							if (actualValue > 1)
							indize.add(new Point(indize.get(0).x+1, indize.get(0).y+1));
						}
					}
					if ((indize.get(0).y > 0) && (indize.get(0).x < retArray.length-1 )) {
						// upper right
						if ((retArray[indize.get(0).x+1][indize.get(0).y-1]) == 0) {
							retArray[indize.get(0).x+1][indize.get(0).y-1] = actualValue;
							if (actualValue > 1)
							indize.add(new Point(indize.get(0).x+1, indize.get(0).y-1));
						}
					}
				}
			}
			indize.remove(0);
		}
		
		return retArray;
	}
	
	public static void clear() {
		lightList.clear();
		idList.clear();
	}

}
