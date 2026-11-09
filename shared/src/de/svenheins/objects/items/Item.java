/* Copyright (C) Sven Heins - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * author: Sven Heins
 * date: 05.07.2015
 */
package de.svenheins.objects.items;

import java.math.BigInteger;
import java.util.HashMap;

import de.svenheins.main.AttributeType;
import de.svenheins.main.GameStates;
import de.svenheins.main.TileSize;
import de.svenheins.messages.ITEMCODE;
import de.svenheins.objects.Entity;
import de.svenheins.objects.LocalObject;
import de.svenheins.objects.items.equipment.amulets.GoldenAmulet;
import de.svenheins.objects.items.equipment.bodyarmors.Cloak;
import de.svenheins.objects.items.equipment.feet.Shoe;
import de.svenheins.objects.items.equipment.gloves.LeatherGlove;
import de.svenheins.objects.items.equipment.helmets.BlondHair;
import de.svenheins.objects.items.equipment.helmets.LeatherHelmet;
import de.svenheins.objects.items.equipment.shields.LeatherShield;
import de.svenheins.objects.items.equipment.shirts.Greenshirt;
import de.svenheins.objects.items.equipment.trousers.Leatherpants;
import de.svenheins.objects.items.equipment.weapons.IronSword;
import de.svenheins.objects.items.equipment.weapons.WoodenSword;
import de.svenheins.objects.items.materials.ArtifactStone;
import de.svenheins.objects.items.materials.Stone;
import de.svenheins.objects.items.materials.Wood;

public abstract class Item extends LocalObject implements Cloneable {
	public static final int[] tileSetX = new int[]{0,0,0,0, GameStates.itemTileWidth, GameStates.itemTileWidth, 2*GameStates.itemTileWidth, 2*GameStates.itemTileWidth};
	public static final int[] tileSetY = new int[]{0,GameStates.itemTileHeight,GameStates.itemTileHeight*2,GameStates.itemTileHeight*3, 0,GameStates.itemTileHeight*2,0,GameStates.itemTileHeight*2};
	public static final int[] tileSetWidth = new int[]{GameStates.itemTileWidth,GameStates.itemTileWidth,GameStates.itemTileWidth,GameStates.itemTileWidth,GameStates.itemTileWidth*2, GameStates.itemTileWidth*2, GameStates.itemTileWidth*2, GameStates.itemTileWidth*2};
	public static final int[] tileSetHeight = new int[]{GameStates.itemTileHeight,GameStates.itemTileHeight,GameStates.itemTileHeight,GameStates.itemTileHeight,GameStates.itemTileHeight*2, GameStates.itemTileHeight*2, GameStates.itemTileHeight*2, GameStates.itemTileHeight*2};
	private HashMap<String, Integer> ingredients;
	
	private int count;
	private int capacity;
//	private String name;
//	private BigInteger id;
//	protected float x;
//	protected float y;
	private ITEMCODE itemCode;
	private Entity entity;
	private long creationTime;
	private float[] attributes;
	protected float[] standardAttributes;
	private String comment;

	protected boolean visible;
	private Integer level;

	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}
	
	public Item(BigInteger id, String room)  {
		this.id = id;
		this.setRoom(room);
//		this.setItemEntity(itemEntity);
		this.setCreationTime(System.currentTimeMillis());
		this.setVisible(true);
		this.setComment("");
		this.setLevel(Integer.valueOf(1));
		this.setIngredients(new HashMap<String, Integer>());
		standardAttributes = new float[AttributeType.values().length];
		this.setAttributes(new float[AttributeType.values().length] );
		this.configStandardAttributes();
		this.rerollAttributes();
		this.defineIngredients();
	}
	
	
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}

	public int getCapacity() {
		return capacity;
	}
	public void setCapacity(int capacity) {
		this.capacity = capacity;
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
	
	protected abstract void rerollAttributes();
	protected abstract void defineIngredients();
	protected abstract void configStandardAttributes();
	
//	public static Item getItemmmmmmmmmmmmmmmmmmmm(ITEMCODE itemCode, BigInteger id, String name, String room, int count, int capacity, float x, float y, long creationTime, float[] states) {
//		Item retItem = null;
//		switch (itemCode) {
//		case WOOD:
//			retItem = new Wood(id, room, x, y);
//			retItem.setCount(count);
//			break;
//		case STONE:
//			retItem = new Stone(id, room, x, y);
//			retItem.setCount(count);
//			break;
//		case ARTIFACTSTONE:
//			retItem = new ArtifactStone(id, room, x, y);
//			retItem.setCount(count);
//			break;	
//		case BODY:
//			if (name.equals("Cloak")) {
//				retItem = new Cloak(id, room, x, y);
//				retItem.setAttributes(states);
//				retItem.setCount(count);
//			}
//			break;
//		case FEET:
//			if (name.equals("Shoe")) {
//				retItem = new Shoe(id, room,  x, y);
//				retItem.setCount(count);
//			}
//			break;
//		case TROUSERS:
//			retItem = new Leatherpants(id, room, x, y);
//			retItem.setCount(1);
//			break;
//		case SHIRT:
//			retItem = new Greenshirt(id, room, x, y);
//			retItem.setCount(1);
//			break;
//		case GLOVE:
//			retItem = new LeatherGlove(id, room, x, y);
//			retItem.setCount(1);
//			break;
//		case HELMET:
//			if (name.equals("BlondHair")) {
//				retItem = new BlondHair(id, room, x, y);
//				retItem.setCount(1);
//			}
//			if (name.equals("LeatherHelmet")) {
//				retItem = new LeatherHelmet(id, room, x, y);
//				retItem.setCount(1);
//			}
//			break;
//		case WEAPON:
//			if (name.equals("WoodenSword")) {
//				retItem = new WoodenSword(id, room, x, y);
//				retItem.setCount(1);
//			}
//			if (name.equals("IronSword")) {
//				retItem = new IronSword(id, room, x, y);
//				retItem.setCount(1);
//			}
//			
//			break;
//		case SHIELD:
//			retItem = new LeatherShield(id, room, x, y);
//			retItem.setCount(1);
//			break;
//		case AMULET:
//			retItem = new GoldenAmulet(id, room, x, y);
//			retItem.setCount(1);
//			break;
//		default: ;
//		}
//		if (retItem != null) {
//			retItem.setAttributes(states);
//		}
//		return retItem;
//	}
//	
//	public static Item createItemmmmmmmmmmmmmmmmmmmmmmmmmm(ITEMCODE itemCode, String room, TileSize tileSize, BigInteger id, float x, float y) {
//		Item retItem = null;
//		int count =1;
//		switch (tileSize) {
//		case ONE: count = 1; break;
//		case TWO: count = 2; break;
//		case THREE: count = 3;  break;
//		case TINY: count = (int)(Math.random()*2 + 2); break;
//		case SMALL: count = (int)(Math.random()*5 + 5); break;
//		case MEDIUM: count = (int)(Math.random()*10 + 10); break;
//		case BIG: count = (int)(Math.random()*20 + 20); break;
//		case HUGE: count = (int)(Math.random()*20 + 30); break;
//		case FULL: count = GameStates.itemMaterialCapacity;  break;
//		default: break;
//		}
//		switch (itemCode) {
//		case WOOD:
//			retItem = new Wood(id, room, x, y);
//			retItem.setCount(count);
//			break;
//		case STONE:
//			retItem = new Stone(id, room, x, y);
//			retItem.setCount(count);
//			break;
//			
//		case BODY:
////			if (name.equals("Cloak")) {
//				retItem = new Cloak(id, room, x, y);
//				retItem.setCount(1);
////			}
//			break;
//		case FEET:
////			if (name.equals("Cloak")) {
//				retItem = new Shoe(id, room, x, y);
//				retItem.setCount(1);
////			}
//			break;
//		default: ;
//		}
//		return retItem;
//	}
//	
//	public static Item createRandomItemmmmmmmmmmmmmm(Integer level, BigInteger id, String room, float x, float y) {
//		Item retItem = null;
//		int randomDice = (int) (Math.random()*ITEMCODE.values().length);
////		System.out.println("create Random Item = " +randomDice +"ITEMCODE= "+ITEMCODE.values()[randomDice]);
//		ITEMCODE itemCode = ITEMCODE.values()[randomDice];
//		switch (itemCode) {
//		case WOOD:
//			retItem = new Wood(id, room, x, y);
//			retItem.setCount((int) (Math.random()*20)+20);
//			break;
//		case STONE:
//			retItem = new Stone(id, room, x, y);
//			retItem.setCount((int) (Math.random()*10)+10);
//			break;
//			
//		case BODY:
////			if (name.equals("Cloak")) {
//				retItem = new Cloak(id, room, x, y);
//				retItem.setCount(1);
////			}
//			break;
//		case FEET:
////			if (name.equals("Cloak")) {
//				retItem = new Shoe(id, room, x, y);
//				retItem.setCount(1);
////			}
//			break;
//		case TROUSERS:
//			retItem = new Leatherpants(id, room, x, y);
//			retItem.setCount(1);
//			break;
//		case SHIRT:
//			retItem = new Greenshirt(id, room, x, y);
//			retItem.setCount(1);
//			break;
//		case GLOVE:
//			retItem = new LeatherGlove(id, room, x, y);
//			retItem.setCount(1);
//			break;
//		case HELMET:
//			retItem = new LeatherHelmet(id, room, x, y);
//			retItem.setCount(1);
//			break;
//		case WEAPON:
//			retItem = new IronSword(id, room, x, y);
//			retItem.setCount(1);
//			break;
//		case SHIELD:
//			retItem = new LeatherShield(id, room, x, y);
//			retItem.setCount(1);
//			break;
//		case AMULET:
//			retItem = new GoldenAmulet(id, room, x, y);
//			retItem.setCount(1);
//			break;
//		default: ;
//		}
//		return retItem;
//	}

//	public float getX() {
//		return x;
//	}
//
//	public void setX(float x) {
//		this.x = x;
//	}
//
//	public float getY() {
//		return y;
//	}
//
//	public void setY(float y) {
//		this.y = y;
//	}
	
	public void setVisible(boolean b) {
		visible = b;
	}
	
	public boolean isVisible() {
		return visible;
	}

	public Integer getLevel() {
		return level;
	}

	public void setLevel(Integer level) {
		this.level = level;
	}

	public HashMap<String, Integer> getIngredients() {
		return ingredients;
	}

	public void setIngredients(HashMap<String, Integer> hashMap) {
		this.ingredients = hashMap;
	}
	
	public void addIngredient(String itemName, int count) {
		this.ingredients.put(itemName, Integer.valueOf(count));
	}
	
	public void removeIngredient(String itemName) {
		this.ingredients.remove(itemName);
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}
	
//	public float getX() {
//		if (this.getEntity() != null) {
//			return this.getEntity().getX();
//		} else {
//			return this.getX();
//		}
//	}
//	
//	public float getY() {
//		if (this.getEntity() != null) {
//			return this.getEntity().getY();
//		} else {
//			return this.getY();
//		}
//	}
//	
//	public void setX(float x ) {
//		if (this.getEntity()!=null) {
//			this.getEntity().setX(x);
//			this.setX(x);
//		} else {
//			this.setX(x);
//		}
//	}
//	
//	public void setY(float y ) {
//		if (this.getEntity()!=null) {
//			this.getEntity().setY(y);
//			this.setY(y);
//		} else {
//			this.setY(y);
//		}
//	}
}
