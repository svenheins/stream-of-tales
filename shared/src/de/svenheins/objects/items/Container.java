package de.svenheins.objects.items;

import java.math.BigInteger;
import java.util.HashMap;

public class Container {
	private HashMap<BigInteger, Item> itemList = new HashMap<BigInteger, Item>();
	private int width;
	private int height;
	private BigInteger[][] containerArray;
	
	public Container(int width, int height) {
		this.setHeight(height);
		this.setWidth(width);
		containerArray = new BigInteger[height][width];
		for (int i = 0; i < height; i ++) {
			for (int j = 0; j < width; j ++) {
				containerArray[i][j] = BigInteger.valueOf(-1);
			}
		}
		
	}
	
	/** is there a free field??? */
	public boolean hasFreeField() {
		if (itemList.size() < width*height) {
			return true;
		} else
			return false;
	}
	
	/** addItem on an optimal spot */
	public Item addItem(Item item) {
		String name = item.getName();
		int tempCount = item.getCount();
		if (item instanceof Stackable ) {
			for (Item loopItem: itemList.values()) {
				if (loopItem.getName().equals(name)) {
					if ( loopItem.getCount() < loopItem.getCapacity() ) {
						if (tempCount <= loopItem.getCapacity()-loopItem.getCount()) {
							loopItem.setCapacity(loopItem.getCapacity()+tempCount);
							tempCount = 0;
						} else {
							tempCount = tempCount - (loopItem.getCapacity()-loopItem.getCount());
							loopItem.setCount(loopItem.getCapacity());
						}
					} // else we cannot add any more to this stack
				} // else its another type of item
			} // end loop
		} // else we cannot stack the item, so we have to search for a free slot
		if (item.getCount() > 0) {
			if (this.hasFreeField()) {
				for (int i = 0; i < height; i ++) {
					for (int j = 0; j < width; j ++) {
						if (containerArray[i][j].compareTo(BigInteger.valueOf(-1)) == 0) {
							itemList.put(item.getId(), item);
							containerArray[i][j] = item.getId();
							item.setCount(0);
						} // else this container is not empty
					} // cols
				} // rows
			} else {
				// item cannot be placed in this container fully
			}
		} // else the item is empty
		return item;
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
}
