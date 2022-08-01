package de.svenheins.managers;


import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.svenheins.objects.items.WorldItem;

public class WorldItemManager {
	public static HashMap<BigInteger, WorldItem> worldItemList = new HashMap<BigInteger, WorldItem>();
	public static List<BigInteger> idList = new ArrayList<BigInteger>();
	
	public static void remove(BigInteger index) throws IllegalArgumentException {
		try {
			worldItemList.remove(index);
			idList.remove(index);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
		
	}
	
	public static boolean add(WorldItem worldItem) {
		if (worldItemList.containsKey(worldItem.getId())) {
			return false;
		} else {
			worldItemList.put(worldItem.getId(), worldItem);
			idList.add(worldItem.getId());
			return true;
		}
	}
	
	public static WorldItem get(BigInteger index){
		try {
			return (WorldItem) worldItemList.get(index);
		}
		catch(IndexOutOfBoundsException e){
			return null;
		}
		
	}
	
	public static boolean overwrite(WorldItem worldItem){
		if (!worldItemList.containsKey(worldItem.getId())) {
			/** do nothing if key is not yet set*/
			return false;
		} else {
			worldItemList.put(worldItem.getId(), worldItem);
			idList.add(worldItem.getId());
			return true;
		}
	}
	
	public static int size(){
		return worldItemList.size();
	}
	
	public static boolean contains(WorldItem worldItem) {
		return worldItemList.containsValue(worldItem);
	}
	
	
	
	public static void clear() {
		worldItemList.clear();
		idList.clear();
	}
	
	public static BigInteger getMaxID() {
		BigInteger retValue = BigInteger.valueOf(0);
		if (idList.size() > 0) {
			for (BigInteger id : idList) {
				if (id.compareTo(retValue) > 0) retValue = id;
			}
		}
		return retValue;
	}
}
