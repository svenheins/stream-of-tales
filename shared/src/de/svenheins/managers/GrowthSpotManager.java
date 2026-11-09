package de.svenheins.managers;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;

import de.svenheins.objects.GrowthSpot;

public class GrowthSpotManager{
	public static HashMap<BigInteger, GrowthSpot> growthSpotList = new HashMap<BigInteger, GrowthSpot>();
	public static ArrayList<BigInteger> idList = new ArrayList<BigInteger>();
	private static BigInteger maxIDValue = BigInteger.valueOf(0);
	

	public static boolean add(GrowthSpot growthSpot) {
		if (growthSpotList.containsKey(growthSpot.getId())) {
			return false;
		} else {
			growthSpotList.put(growthSpot.getId(), growthSpot);
			idList.add(growthSpot.getId());		

			if (maxIDValue.compareTo(growthSpot.getId()) < 0) {
				maxIDValue = growthSpot.getId();
			}
			return true;
		}
	}
	
	public static void addFirst(GrowthSpot growthSpot) {
		growthSpotList.put(growthSpot.getId(), growthSpot);
		idList.add(0, growthSpot.getId());

	}
	
	
	public static void remove(BigInteger id) throws IllegalArgumentException {
		try {
			growthSpotList.remove(id);
			idList.remove(id);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
		
		if (growthSpotList.size() <= 0) {
			maxIDValue = BigInteger.valueOf(0);
		}
	}
	
	public static GrowthSpot get(BigInteger id) {
		try {
			return growthSpotList.get(id);
		}
		catch(IndexOutOfBoundsException e){
			System.out.println(e);
			return null;
		}
	}
	
	public static boolean overwrite(GrowthSpot growthSpot){
		if (!growthSpotList.containsKey(growthSpot.getId())) {
			/** do nothing if key is not yet set*/
			return false;
		} else {
			growthSpotList.put(growthSpot.getId(), growthSpot);
			idList.add(growthSpot.getId());
			return true;
		}
	}
	
	public static boolean contains(GrowthSpot growthSpot) {
		return growthSpotList.containsValue(growthSpot);
	}
	
	public static int size() {
		return growthSpotList.size();
	}
	
	public static void clear() {
		growthSpotList.clear();
		idList.clear();
	}

	public static BigInteger getMaxIDValue() {
		return maxIDValue;
	}

	public static void setMaxIDValue(BigInteger maxIDValue) {
		GrowthSpotManager.maxIDValue = maxIDValue;
	}

}
