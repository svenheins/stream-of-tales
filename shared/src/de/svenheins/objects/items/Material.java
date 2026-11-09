/* Copyright (C) Sven Heins - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * author: Sven Heins
 * date: 05.07.2015
 */
package de.svenheins.objects.items;

import java.math.BigInteger;
import java.util.HashMap;
import de.svenheins.functions.MyMath;
import de.svenheins.main.GameStates;
import de.svenheins.messages.ITEMCODE;
import de.svenheins.objects.Entity;
import de.svenheins.objects.TileSet;

public abstract class Material extends Item implements Stackable, Placeable, Usable {
	private int standardCount = 1;
	private HashMap<String, Integer> craftList;
	
	public Material(BigInteger id, String room) {
		super(id, room);
		this.setCount(1);
		this.setCraftList(new HashMap<String, Integer>());
		this.setCapacity(GameStates.itemMaterialCapacity);
		this.defineCraftList();
	}
	
	public Material(ITEMCODE itemCode, String name) {
		super(BigInteger.valueOf(0), "");
		this.setItemCode(itemCode);
		this.setCount(1);
		this.setCapacity(GameStates.itemMaterialCapacity);
		this.setX(0);
		this.setY(0);
		this.setName(name);
		TileSet materialTileSet = new TileSet(GameStates.standardTilePathItems+name+".png", this.getName(), Item.tileSetX, Item.tileSetY, Item.tileSetWidth, Item.tileSetHeight);
		Entity itemEntity = new Entity(materialTileSet, this.getName(), this.getRoom(), this.getId(), 0, 0, GameStates.animationDelayItems);
		this.setEntity(itemEntity);
		this.setCraftList(new HashMap<String, Integer>());
		this.defineCraftList();
	}
	
	public void rerollAttributes() {
		this.setCount((int) MyMath.getTrimmedGaussian(this.getStandardCount()));
	}

	public int getStandardCount() {
		return standardCount;
	}

	public void setStandardCount(int standardCount) {
		this.standardCount = standardCount;
	}
	
	@Override
	protected void configStandardAttributes() {
		/** nothing to do here, because material has no attributes */
	}

	public HashMap<String, Integer> getCraftList() {
		return craftList;
	}

	public void setCraftList(HashMap<String, Integer> craftList) {
		this.craftList = craftList;
	}
	
	protected abstract void defineCraftList();
	
	public void addCraftItem(String itemName, int count) {
		this.craftList.put(itemName, Integer.valueOf(count));
	}
	
	public void removeCraftItem(String itemName) {
		this.craftList.remove(itemName);
	}

}
