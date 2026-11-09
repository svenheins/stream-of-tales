/* Copyright (C) Sven Heins - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * author: Sven Heins
 * date: 05.07.2015
 */
package de.svenheins.objects.items;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

public abstract class ItemFactory {
	protected HashMap<String, Item> itemList = new HashMap<String, Item>();
	
	public ItemFactory() {
		this.createItems();
	}
	
	public HashMap<String, Item> getItemList() {
		return itemList;
	}
	
	protected abstract void createItems();

	/** get a random instance of an item, whose level is smaller or equal to "level"*/
	protected Item getRandom(Integer level) {
		Item retItem = null;
		int maxTries = 30;
		int indexTries = 0;
		while(retItem == null && indexTries <maxTries) {
			int index = (int) (Math.random()*itemList.keySet().size());
//			System.out.println("Dice = "+index+ " maximum: "+itemList.keySet().size());
			Object[] items = itemList.values().toArray();
			Item foundItem = (Item) items[index];
			if (foundItem!= null) {
				if (foundItem.getLevel().compareTo(level)<=0) retItem= foundItem;
			}
			indexTries++;
		}
		if (retItem != null)
		return getItem(retItem.getName());
		else return null;
	}
	
	/** get an instance of an item */
	public Item getItem(String name) {
		Class<?> itemClass = null;
		try {
			itemClass = Class.forName(itemList.get(name).getClass().getName());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		Constructor<?> itemConstructor = null;
		try {
			itemConstructor = itemClass.getConstructor();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		}
		Object object = null;
		try {
			object = itemConstructor.newInstance(new Object[] {});
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		Item retItem = (Item) object;
		return retItem;
	}
}
