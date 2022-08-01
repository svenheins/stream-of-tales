package de.svenheins.managers;


import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.svenheins.objects.items.Item;

public class ItemManager {
	public static HashMap<BigInteger, Item> itemList = new HashMap<BigInteger, Item>();
	public static List<BigInteger> idList = new ArrayList<BigInteger>();
	private static BigInteger maxIDValue = BigInteger.valueOf(0);
	
	public static void remove(BigInteger index) throws IllegalArgumentException {
		try {
			itemList.remove(index);
			idList.remove(index);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
		
	}
	
	public static boolean add(Item item) {
		if (itemList.containsKey(item.getId())) {
			return false;
		} else {
			itemList.put(item.getId(), item);
			idList.add(item.getId());
			if (maxIDValue.compareTo(item.getId()) < 0) {
				maxIDValue = item.getId();
			}
			return true;
		}
	}
	
	public static Item get(BigInteger index){
		try {
			return itemList.get(index);
		}
		catch(IndexOutOfBoundsException e){
			return null;
		}
		
	}
	
	public static boolean overwrite(Item item){
		if (!itemList.containsKey(item.getId())) {
			/** do nothing if key is not yet set*/
			return false;
		} else {
			itemList.put(item.getId(), item);
			idList.add(item.getId());
			return true;
		}
	}
	
	public static int size(){
		return itemList.size();
	}
	
	public static boolean contains(Item item) {
		return itemList.containsValue(item);
	}
	
	
	
	public static void clear() {
		itemList.clear();
		idList.clear();
	}

	public static BigInteger getMaxIDValue() {
		return maxIDValue;
	}

	public static void setMaxIDValue(BigInteger maxIDValue) {
		ItemManager.maxIDValue = maxIDValue;
	}
}
