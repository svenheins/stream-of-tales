package de.svenheins.objects.items;

import java.math.BigInteger;

import de.svenheins.main.GameStates;
import de.svenheins.messages.ITEMCODE;
import de.svenheins.objects.Entity;

public abstract class Item implements Cloneable {
	public static final int[] tileSetX = new int[]{0,0,0,0, GameStates.itemTileWidth, GameStates.itemTileWidth, GameStates.itemTileWidth, GameStates.itemTileWidth};
	public static final int[] tileSetY = new int[]{0,GameStates.itemTileHeight,GameStates.itemTileHeight*2,GameStates.itemTileHeight*3, 0,GameStates.itemTileHeight,GameStates.itemTileHeight*2,GameStates.itemTileHeight*3};
	public static final int[] tileSetWidth = new int[]{GameStates.itemTileWidth,GameStates.itemTileWidth,GameStates.itemTileWidth,GameStates.itemTileWidth,GameStates.itemTileWidth, GameStates.itemTileWidth, GameStates.itemTileWidth, GameStates.itemTileWidth};
	public static final int[] tileSetHeight = new int[]{GameStates.itemTileHeight,GameStates.itemTileHeight,GameStates.itemTileHeight,GameStates.itemTileHeight,GameStates.itemTileHeight, GameStates.itemTileHeight, GameStates.itemTileHeight, GameStates.itemTileHeight};
	
	private int count;
	private int capacity;
	private String name;
	private BigInteger id;
	private ITEMCODE itemCode;
	private Entity itemEntity;
	private long creationTime;

	protected Object clone() throws CloneNotSupportedException {
		return super.clone();
	}
	
	public Item(BigInteger id)  {
		this.id = id;
//		this.setItemEntity(itemEntity);
		this.setCreationTime(System.currentTimeMillis());
	}
	
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getCapacity() {
		return capacity;
	}
	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}
	public BigInteger getId() {
		return id;
	}
	public void setId(BigInteger id) {
		this.id = id;
	}
	public ITEMCODE getItemCode() {
		return itemCode;
	}
	public void setItemCode(ITEMCODE itemCode) {
		this.itemCode = itemCode;
	}

	public Entity getItemEntity() {
		return itemEntity;
	}

	public void setItemEntity(Entity itemEntity) {
		this.itemEntity = itemEntity;
	}

	public long getCreationTime() {
		return creationTime;
	}

	public void setCreationTime(long creationTime) {
		this.creationTime = creationTime;
	}
	
}
