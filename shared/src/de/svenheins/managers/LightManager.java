/* Copyright (C) Sven Heins - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * author: Sven Heins
 * date: 05.07.2015
 */
package de.svenheins.managers;

import java.awt.Point;
import java.awt.Rectangle;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.svenheins.functions.MyMath;
import de.svenheins.main.GameStates;
import de.svenheins.objects.Light;

public class LightManager {
	public static HashMap<BigInteger, Light> lightList = new HashMap<BigInteger, Light>();
	public static List<BigInteger> idList = new ArrayList<BigInteger>();
	public static BigInteger maxIDValue = BigInteger.valueOf(0);

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
//			System.out.println("LightManager couldn't add light");
			return false;
		} else {
			lightList.put(light.getId(), light);
			idList.add(light.getId());
			if (maxIDValue.compareTo(light.getId()) < 0) {
				maxIDValue = light.getId();
			}
//			System.out.println("added light " + light.getId() + " x="+light.getLocation().x+" y="+light.getLocation().y);
			return true;
		}
	}

	public static Light get(BigInteger index) {
		try {
			return (Light) lightList.get(index);
		} catch (IndexOutOfBoundsException e) {
			return null;
		}

	}

	public static boolean overwrite(Light light) {
		if (!lightList.containsKey(light.getId())) {
			/** do nothing if key is not yet set */
			return false;
		} else {
			lightList.put(light.getId(), light);
			idList.add(light.getId());
			return true;
		}
	}

	public static int size() {
		return lightList.size();
	}

	public static boolean contains(Light light) {
		return lightList.containsValue(light);
	}

	public static ArrayList<Light> getLightsOfRegion(Rectangle rect) {
		ArrayList<Light> retLightList = new ArrayList<Light>();
		rect.setBounds(rect.x, rect.y, rect.width - GameStates.mapTileSetWidth,
				rect.height - GameStates.mapTileSetHeight);
		ArrayList<BigInteger> tempIDList = new ArrayList<BigInteger>(idList);
		for (BigInteger id : tempIDList) {
			if (lightList.get(id) != null) {
				if (rect.contains(new Point(lightList.get(id).getLocation().x,
						lightList.get(id).getLocation().y))) {
					retLightList.add(lightList.get(id));
				}
			}
		}
		return retLightList;
	}

	/** get the LightMap (int Array) in a specific Region (rectangle) */
	public static int[][] getLightMap(Rectangle rect) {
		/** the retArray has to cover whole the rectangle */
		int retArray[][] = new int[(int) Math.ceil(rect.width
				/ GameStates.lightTileWidth)][(int) Math.ceil(rect.height
				/ GameStates.lightTileHeight)];
		ArrayList<Point> indize = new ArrayList<Point>();
		for (Light light : getLightsOfRegion(rect)) {
			Point p = new Point(
					(int) Math.floor((float) (light.getLocation().x - rect.x)
							/ GameStates.lightTileWidth),
					(int) Math.floor((float) (light.getLocation().y - rect.y)
							/ GameStates.lightTileHeight));
			indize.add(p);
			retArray[p.x][p.y] = light.getBrightness();
		}
		while (!indize.isEmpty()) {
			if (retArray[indize.get(0).x][indize.get(0).y] > 1) {
				int actualValue = retArray[indize.get(0).x][indize.get(0).y] - 1;
				if (indize.get(0).y > 0) {
					// upper
					if ((retArray[indize.get(0).x][indize.get(0).y - 1]) == 0) {
						retArray[indize.get(0).x][indize.get(0).y - 1] = actualValue;
						if (actualValue > 1)
							indize.add(new Point(indize.get(0).x,
									indize.get(0).y - 1));
					}
				}
				if (indize.get(0).y < retArray[0].length - 1) {
					// lower
					if ((retArray[indize.get(0).x][indize.get(0).y + 1]) == 0) {
						retArray[indize.get(0).x][indize.get(0).y + 1] = actualValue;
						if (actualValue > 1)
							indize.add(new Point(indize.get(0).x,
									indize.get(0).y + 1));
					}
				}
				if (indize.get(0).x > 0) {
					// left
					if ((retArray[indize.get(0).x - 1][indize.get(0).y]) == 0) {
						retArray[indize.get(0).x - 1][indize.get(0).y] = actualValue;
						if (actualValue > 1)
							indize.add(new Point(indize.get(0).x - 1, indize
									.get(0).y));
					}
				}
				if (indize.get(0).x < retArray.length - 1) {
					// right
					if ((retArray[indize.get(0).x + 1][indize.get(0).y]) == 0) {
						retArray[indize.get(0).x + 1][indize.get(0).y] = actualValue;
						if (actualValue > 1)
							indize.add(new Point(indize.get(0).x + 1, indize
									.get(0).y));
					}
				}
				if (actualValue >= GameStates.maxLightIntensityCutoff - 1) {
					/** additionally add corners */
					if ((indize.get(0).y > 0) && (indize.get(0).x > 0)) {
						// upper left
						if ((retArray[indize.get(0).x - 1][indize.get(0).y - 1]) == 0) {
							retArray[indize.get(0).x - 1][indize.get(0).y - 1] = actualValue;
							if (actualValue > 1)
								indize.add(new Point(indize.get(0).x - 1,
										indize.get(0).y - 1));
						}
					}
					if ((indize.get(0).y < retArray[0].length - 1)
							&& (indize.get(0).x > 0)) {
						// lower left
						if ((retArray[indize.get(0).x - 1][indize.get(0).y + 1]) == 0) {
							retArray[indize.get(0).x - 1][indize.get(0).y + 1] = actualValue;
							if (actualValue > 1)
								indize.add(new Point(indize.get(0).x - 1,
										indize.get(0).y + 1));
						}
					}
					if ((indize.get(0).y < retArray[0].length - 1)
							&& (indize.get(0).x < retArray.length - 1)) {
						// lower right
						if ((retArray[indize.get(0).x + 1][indize.get(0).y + 1]) == 0) {
							retArray[indize.get(0).x + 1][indize.get(0).y + 1] = actualValue;
							if (actualValue > 1)
								indize.add(new Point(indize.get(0).x + 1,
										indize.get(0).y + 1));
						}
					}
					if ((indize.get(0).y > 0)
							&& (indize.get(0).x < retArray.length - 1)) {
						// upper right
						if ((retArray[indize.get(0).x + 1][indize.get(0).y - 1]) == 0) {
							retArray[indize.get(0).x + 1][indize.get(0).y - 1] = actualValue;
							if (actualValue > 1)
								indize.add(new Point(indize.get(0).x + 1,
										indize.get(0).y - 1));
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

	public static boolean hasLightAtPosition(int x, int y, int totalX, int totalY) {
		boolean retValue = false;
		Light light = null;
		Point lightPos = new Point(x - (x % GameStates.lightTileWidth), y
				- (y % GameStates.lightTileHeight));
		for (BigInteger id : idList) {
			light = lightList.get(id);
			if ((light.getLocation().x != lightPos.x && light.getLocation().x != lightPos.x - totalX && light.getLocation().x != lightPos.x +totalX)
					|| (light.getLocation().y != lightPos.y && light.getLocation().y != lightPos.y -totalY && light.getLocation().y != lightPos.y+totalY)) {
				// nothing
			} else
				return true;
		}
		return retValue;
	}

	public static BigInteger whichLightAtPosition(int x, int y) {
		BigInteger retValue = null;
		Light light = null;
		Point lightPos = new Point(x - (x % GameStates.lightTileWidth), y
				- (y % GameStates.lightTileHeight));
		for (BigInteger id : idList) {
			light = lightList.get(id);
			if (light.getLocation().x != lightPos.x
					|| light.getLocation().y != lightPos.y) {
				// nothing
			} else
				return id;
		}
		return retValue;
	}

	public static BigInteger whichLightIsClosest(int x, int y) {
		BigInteger retValue = null;
		Light light = null;
		double minDistance = Double.MAX_VALUE;
		Point point = new Point(x, y);
		double tempDistance = Double.MAX_VALUE;
		ArrayList<BigInteger> tempIdList = new ArrayList<BigInteger>(idList);
		for (BigInteger id : tempIdList) {
			light = lightList.get(id);
			tempDistance = MyMath.getDistance(light.getLocation(), point);
			if (tempDistance < minDistance) {
				// nothing
				retValue = light.getId();
				minDistance = tempDistance;
			} else
				;
		}
		return retValue;
	}

	public static BigInteger whichLightIsClosestExcept(int x, int y,
			BigInteger exclude) {
		BigInteger retValue = null;
		Light light = null;
		double minDistance = Double.MAX_VALUE;
		Point point = new Point(x, y);
		double tempDistance = Double.MAX_VALUE;
		ArrayList<BigInteger> tempIdList = new ArrayList<BigInteger>(idList);
		for (BigInteger id : tempIdList) {
			if (id.compareTo(exclude) != 0) {
				light = lightList.get(id);
				tempDistance = MyMath.getDistance(light.getLocation(), point);
				if (tempDistance < minDistance) {
					// nothing
					retValue = light.getId();
					minDistance = tempDistance;
				} else
					;
			}
		}
		return retValue;
	}

}
