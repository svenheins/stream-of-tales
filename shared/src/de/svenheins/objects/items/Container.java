/* Copyright (C) Sven Heins - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * author: Sven Heins
 * date: 05.07.2015
 */
package de.svenheins.objects.items;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import de.svenheins.main.EntityStates;
import de.svenheins.main.GameStates;
import de.svenheins.messages.ITEMCODE;
import de.svenheins.messages.OBJECTCODE;
import de.svenheins.objects.items.Item;

public class Container {
	private HashMap<BigInteger, Item> itemList = new HashMap<BigInteger, Item>();
	private int width;
	private int height;
	private BigInteger[][] containerArray;
	private OBJECTCODE containerType;
	private ITEMCODE allowedItems;
//	private BigInteger maxID;
	
	public Container(int width, int height, OBJECTCODE containerType, ITEMCODE allowedItems) {
		this.setHeight(height);
		this.setWidth(width);
		containerArray = new BigInteger[height][width];
		for (int i = 0; i < height; i ++) {
			for (int j = 0; j < width; j ++) {
				containerArray[i][j] = BigInteger.valueOf(-1);
			}
		}
		itemList = new HashMap<BigInteger, Item>();
		this.setContainerType(containerType);
		this.setAllowedItems(allowedItems);
	}
	
	
	/** is there a free field??? */
	public boolean hasAvailableField() {
		if (itemList.values().size() < width*height) {
			return true;
		} else {
			List<BigInteger> tempIDList = new ArrayList<BigInteger>(itemList.keySet());
			// here we have only the possibillity, that a stack is not yet full
			for (BigInteger id : tempIDList ) {
				if (itemList.get(id).getCount() < itemList.get(id).getCapacity()) {
					return true;
				}
			}
			return false;
		}
			
	}
	
	/** is there a free field??? */
	public int countItems(String itemName) {
		int retValue = 0;
		List<BigInteger> tempIDList = new ArrayList<BigInteger>(itemList.keySet());
		for (BigInteger id : tempIDList ) {
			Item item = itemList.get(id);
			if (item.getName().equals(itemName)) {
				retValue += item.getCount();
			}
		}			
		return retValue;
	}
	
	/** is there a free field??? */
	public void removeCountItems(String itemName, int count) {
		int tempCount = count;
		List<BigInteger> tempIDList = new ArrayList<BigInteger>(itemList.keySet());
		List<BigInteger> deleteList = new ArrayList<BigInteger>();
		for (BigInteger id : tempIDList ) {
			Item item = itemList.get(id);
			if (item.getName().equals(itemName)) {
				if (tempCount < item.getCount()) {
					item.setCount(item.getCount()-tempCount);
					tempCount = 0;
				}
				if (tempCount >= item.getCount()) {
					tempCount -= item.getCount();
					deleteList.add(item.getId());
				}
			}
			
			if (tempCount > 0) {
				continue;
			} else break;
		}	
		for (BigInteger id : deleteList) {
			this.removeItem(id);
		}
	}
	
	/** is there a free field??? */
	public ArrayList<ITEMCODE> getAvailableItemFields() {
		ArrayList<ITEMCODE> retList = new ArrayList<ITEMCODE>();
		if (itemList.values().size() < width*height) {
			for (ITEMCODE itemCode : ITEMCODE.values()) {
				retList.add(itemCode);
			}
		} else {
			List<BigInteger> tempIDList = new ArrayList<BigInteger>(itemList.keySet());
			// here we have only the possibillity, that a stack is not yet full
			for (BigInteger id : tempIDList ) {
				if (itemList.get(id).getCount() < itemList.get(id).getCapacity()) {
					retList.add(itemList.get(id).getItemCode());
				}
			}
		}
		return retList;
	}
	
	/** is there a free field??? */
	public boolean hasEmptyField() {
		if (itemList.values().size() < width*height) {
			return true;
		} else {
			return false;
		}
			
	}
	
	/** addItem on an optimal spot */
	public Item addItem(Item item) {
//		String name = item.getName();
		
		int tempCount = item.getCount();
		if (item instanceof Stackable ) {
			if (item.getCount() < item.getCapacity() || !this.hasEmptyField()) {
//				System.out.println("item is under capacity "+item.getCount()+" VS "+item.getCapacity());
				for (Item loopItem: itemList.values()) {
					if (loopItem.getItemCode() == item.getItemCode()) {
						if ( loopItem.getCount() < loopItem.getCapacity() ) {
							if (tempCount <= loopItem.getCapacity()-loopItem.getCount()) {
								loopItem.setCount(loopItem.getCount()+tempCount);
								tempCount = 0;
	//							System.out.println("itemStack is now "+loopItem.getCount()+" and item has still "+tempCount);
							} else {
								tempCount = tempCount - (loopItem.getCapacity()-loopItem.getCount());
								loopItem.setCount(loopItem.getCapacity());
	//							System.out.println("rest is now "+tempCount);
							}
						} // else we cannot add any more to this stack
					} // else its another type of item
				} // end loop
			} else {
//				System.out.println("item is not under capacity "+item.getCount()+" VS "+item.getCapacity());
				/** here we can just pass the item to the next field */
			}
		} // else we cannot stack the item, so we have to search for a free slot
		item.setCount(tempCount);
		if (item.getCount() > 0) {
			if (this.hasEmptyField()) {
				for (int i = 0; i < height; i ++) {
					for (int j = 0; j < width; j ++) {
						if (containerArray[i][j].equals(BigInteger.valueOf(-1)) && item.getCount() > 0) {
//							System.out.println("put into row = "+i+" | col =  "+j);
//							BigInteger newItemID = ItemManager.getMaxID().add(addBigInteger);
//							System.out.println("new id: "+newItemID);
							try {
								item.getEntity().setX(GameStates.inventoryDistToFrameX + j*(2*GameStates.inventorySlotDistX+GameStates.inventoryItemTileWidth)+GameStates.inventorySlotDistX);
								item.getEntity().setY(GameStates.inventoryDistToFrameY + i*(2*GameStates.inventorySlotDistY+GameStates.inventoryFontDistanceY+GameStates.inventoryItemTileHeight)+GameStates.inventorySlotDistY);
								item.setX(item.getEntity().getX());
								item.setY(item.getEntity().getY());
								item.getEntity().setContinuousState(EntityStates.INVENTORY_SIMPLE);
								itemList.put(item.getId(), (Item) item.clone());
								itemList.get(item.getId()).setVisible(true);
							} catch (CloneNotSupportedException e) {
								e.printStackTrace();
							}
//							itemList.get(newItemID).setId(newItemID);
							containerArray[i][j] = item.getId();
							item.setCount(0);
							break;
						} // else this container is not empty
					} // cols
					if (!(item.getCount() > 0)) break;
				} // rows
				return null;
			} else {
				// item cannot be placed in this container
			}
		} else return null;// else the item is empty
		
		return item;
	}
	
	public boolean removeItem(BigInteger id) {
		if ( itemList.containsKey(id)) {
			itemList.remove(id);
			for (int i = 0; i < height; i ++) {
				for (int j = 0; j < width; j ++) {
					if (containerArray[i][j].equals(id))
					containerArray[i][j] = BigInteger.valueOf(-1);
				}
			}
			return true;
		} else return false;
	}
	
	public HashMap<BigInteger, Item> getItemList() {
		return itemList;
	}
	public void setItemList(HashMap<BigInteger, Item> itemList) {
		this.itemList = itemList;
	}
	
	
	public int getWidth() {
		return width;
	}
	public void setWidth(int width) {
		this.width = width;
	}
	public int getHeight() {
		return height;
	}
	public void setHeight(int height) {
		this.height = height;
	}

	public BigInteger[][] getContainerArray() {
		return containerArray;
	}

	public void setContainerArray(BigInteger[][] containerArray) {
		this.containerArray = containerArray;
	}

	public OBJECTCODE getContainerType() {
		return containerType;
	}

	public void setContainerType(OBJECTCODE containerType) {
		this.containerType = containerType;
	}

	public ITEMCODE getAllowedItems() {
		return allowedItems;
	}

	public void setAllowedItems(ITEMCODE allowedItems) {
		this.allowedItems = allowedItems;
	}


	public BigInteger getMaxID() {
		BigInteger retValue = BigInteger.valueOf(-1);
		for (BigInteger id: itemList.keySet()) {
			if (id.compareTo(retValue) >= 0) {
				retValue = id;
			}
		}
		return retValue;
	}
	
	/** reset the complete container*/
	public void reset() {
		containerArray = new BigInteger[height][width];
		for (int i = 0; i < height; i ++) {
			for (int j = 0; j < width; j ++) {
				containerArray[i][j] = BigInteger.valueOf(-1);
			}
		}
		this.setItemList(new HashMap<BigInteger, Item>());
	}
}
