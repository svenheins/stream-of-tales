package de.svenheins.objects;

import java.math.BigInteger;
import java.util.HashMap;

import com.sun.sgs.app.AppContext;
import com.sun.sgs.app.ManagedReference;

import de.svenheins.main.GameStates;
import de.svenheins.messages.ITEMCODE;
import de.svenheins.messages.OBJECTCODE;
import de.svenheins.objects.items.Stackable;

public class ServerContainer extends WorldObject {
	private static final long serialVersionUID = 1L;
	
	private HashMap<BigInteger, ManagedReference<ServerItem>> itemList;
	private int containerWidth;
	private int containerHeight;
	private BigInteger[][] containerArray;
	private OBJECTCODE containerType;
	private ITEMCODE allowedItems;
	
	public ServerContainer(int width, int height, OBJECTCODE containerType, ITEMCODE allowedItems) {
		this.setContainerHeight(height);
		this.setContainerWidth(width);
		itemList = new HashMap<BigInteger, ManagedReference<ServerItem>>();
		containerArray = new BigInteger[height][width];
		for (int i = 0; i < height; i ++) {
			for (int j = 0; j < width; j ++) {
				containerArray[i][j] = BigInteger.valueOf(-1);
			}
		}
		this.setContainerType(containerType);
		this.setAllowedItems(allowedItems);
	}
	
	/** is there a free field??? */
	public boolean hasAvailableField() {
		if (itemList.values().size() < width*height) {
			return true;
		} else {
			// here we have only the possibility, that a stack is not yet full
			for (BigInteger id : itemList.keySet() ) {
				if (itemList.get(id).get().getCount() < itemList.get(id).get().getCapacity()) {
					return true;
				}
			}
			return false;
		}
			
	}
	
	/** is there a free field??? */
	public boolean hasEmptyField() {
		if (itemList.values().size() < containerWidth*containerHeight) {
			return true;
		} else {
			return false;
		}
			
	}
	
	/** addItem on an optimal spot */
	public ServerItem addItem(ServerItem item) {
//		String name = item.getName();
		
		int tempCount = item.getCount();
		if (item instanceof Stackable ) {
			if (item.getCount() < item.getCapacity() || !this.hasEmptyField()) {
				for (ManagedReference<ServerItem> loopItemRef: itemList.values()) {
					ServerItem loopItem = loopItemRef.get();
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
				/** here we can just pass the item to the next field */
			}
		} // else we cannot stack the item, so we have to search for a free slot
		item.setCount(tempCount);
		if (item.getCount() > 0) {
			if (this.hasEmptyField()) {
				for (int i = 0; i < containerHeight; i ++) {
					for (int j = 0; j < containerWidth; j ++) {
						if (containerArray[i][j].equals(BigInteger.valueOf(-1)) && item.getCount() > 0) {
//							System.out.println("put into row = "+i+" | col =  "+j);
//							BigInteger newItemID = ItemManager.getMaxID().add(addBigInteger);
//							System.out.println("new id: "+newItemID);
//							try {
								item.getItemEntity().getForUpdate().setX(GameStates.inventoryDistToFrameX + j*(2*GameStates.inventorySlotDistX+GameStates.inventoryItemTileWidth)+GameStates.inventorySlotDistX);
								item.getItemEntity().getForUpdate().setY(GameStates.inventoryDistToFrameY + i*(2*GameStates.inventorySlotDistY+GameStates.inventoryFontDistanceY+GameStates.inventoryItemTileHeight)+GameStates.inventorySlotDistY);	
								itemList.put(item.getId(), AppContext.getDataManager().createReference(new ServerItem(item.getId(), item.getItemCode(), item.getName(), item.getItemEntity().get(), item.getCount(), item.getCapacity(), item.getStates())));
//							} catch (CloneNotSupportedException e) {
//								e.printStackTrace();
//							}
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
			for (int i = 0; i < containerHeight; i ++) {
				for (int j = 0; j < containerWidth; j ++) {
					if (containerArray[i][j].equals(id))
					containerArray[i][j] = BigInteger.valueOf(-1);
				}
			}
			return true;
		} else return false;
	}
	
	public HashMap<BigInteger, ManagedReference<ServerItem>> getItemList() {
		return itemList;
	}
	public void setItemList(HashMap<BigInteger, ManagedReference<ServerItem>> itemList) {
		this.itemList = itemList;
	}

	public BigInteger[][] getContainerArray() {
		return containerArray;
	}

	public void setContainerArray(BigInteger[][] containerArray) {
		this.containerArray = containerArray;
	}
	
	public int getContainerWidth() {
		return containerWidth;
	}

	public void setContainerWidth(int containerWidth) {
		this.containerWidth = containerWidth;
	}

	public int getContainerHeight() {
		return containerHeight;
	}

	public void setContainerHeight(int containerHeight) {
		this.containerHeight = containerHeight;
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
}
