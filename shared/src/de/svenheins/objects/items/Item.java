package de.svenheins.objects.items;

import java.math.BigInteger;

import de.svenheins.main.GameStates;
import de.svenheins.messages.ITEMCODE;
import de.svenheins.objects.Entity;
import de.svenheins.objects.items.equipment.bodyarmors.Cloak;
import de.svenheins.objects.items.materials.Wood;

public abstract class Item implements Cloneable {
	public static final int[] tileSetX = new int[]{0,0,0,0, GameStates.itemTileWidth, GameStates.itemTileWidth, 2*GameStates.itemTileWidth, 2*GameStates.itemTileWidth};
	public static final int[] tileSetY = new int[]{0,GameStates.itemTileHeight,GameStates.itemTileHeight*2,GameStates.itemTileHeight*3, 0,GameStates.itemTileHeight*2,0,GameStates.itemTileHeight*2};
	public static final int[] tileSetWidth = new int[]{GameStates.itemTileWidth,GameStates.itemTileWidth,GameStates.itemTileWidth,GameStates.itemTileWidth,GameStates.itemTileWidth*2, GameStates.itemTileWidth*2, GameStates.itemTileWidth*2, GameStates.itemTileWidth*2};
	public static final int[] tileSetHeight = new int[]{GameStates.itemTileHeight,GameStates.itemTileHeight,GameStates.itemTileHeight,GameStates.itemTileHeight,GameStates.itemTileHeight*2, GameStates.itemTileHeight*2, GameStates.itemTileHeight*2, GameStates.itemTileHeight*2};
	
	private int count;
	private int capacity;
	private String name;
	private BigInteger id;
	private ITEMCODE itemCode;
	private Entity entity;
	private long creationTime;
	private float[] attributes;
	protected float x;
	protected float y;
	protected boolean visible;

	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}
	
	public Item(BigInteger id)  {
		this.id = id;
//		this.setItemEntity(itemEntity);
		this.setCreationTime(System.currentTimeMillis());
		this.setVisible(true);
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

	public Entity getEntity() {
		return entity;
	}

	public void setEntity(Entity entity) {
		this.entity = entity;
	}

	public long getCreationTime() {
		return creationTime;
	}

	public void setCreationTime(long creationTime) {
		this.creationTime = creationTime;
	}

	public float[] getAttributes() {
		return attributes;
	}

	public void setAttributes(float[] attributes) {
		this.attributes = attributes;
	}
	
	public static Item getItem(ITEMCODE itemCode, BigInteger id, String name, int count, int capacity, float x, float y, long creationTime, float[] states) {
		Item retItem = null;
		switch (itemCode) {
		case WOOD:
			retItem = new Wood(id, x, y);
			retItem.setCount(count);
			break;
		case STONE:
			retItem = null;
			break;
			
		case BODY:
			if (name.equals("Cloak")) {
				retItem = new Cloak(id, x, y);
				retItem.setCount(count);
			}
			break;
		default: ;
		}
		return retItem;
	}
	
	public static Item createItem(ITEMCODE itemCode, BigInteger id, float x, float y) {
		Item retItem = null;
		switch (itemCode) {
		case WOOD:
			retItem = new Wood(id, x, y);
			retItem.setCount((int) (Math.random()*20)+20);
			break;
		case STONE:
			retItem = null;
			break;
			
		case BODY:
//			if (name.equals("Cloak")) {
				retItem = new Cloak(id, x, y);
				retItem.setCount(1);
//			}
			break;
		default: ;
		}
		return retItem;
	}

	public float getX() {
		return x;
	}

	public void setX(float x) {
		this.x = x;
	}

	public float getY() {
		return y;
	}

	public void setY(float y) {
		this.y = y;
	}
	
	public void setVisible(boolean b) {
		visible = b;
	}
	
	public boolean isVisible() {
		return visible;
	}
}
