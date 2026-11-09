/* Copyright (C) Sven Heins - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * author: Sven Heins
 * date: 05.07.2015
 */
package de.svenheins.objects.items.materials;

import java.math.BigInteger;

import de.svenheins.main.GameStates;
import de.svenheins.messages.ITEMCODE;
import de.svenheins.objects.Entity;
import de.svenheins.objects.TileSet;
import de.svenheins.objects.items.Item;
import de.svenheins.objects.items.Material;

public class Soil extends Material {
	
	public Soil() {
		super(ITEMCODE.SOIL, "Soil");
		this.setStandardCount(10);
		this.setCapacity(GameStates.itemMaterialCapacity);
	}

	public Soil(BigInteger id, String room, float x, float y) {
		super(id, room);
		this.setItemCode(ITEMCODE.SOIL);
		this.setCount(1);
		this.setCapacity(GameStates.itemMaterialCapacity);
		this.setName("Soil");
//		TileSet woodTileSet = new TileSet(GameStates.standardTilePathItems+"wood2.png", this.getName(), GameStates.itemTileWidth, GameStates.itemTileHeight);
		TileSet woodTileSet = new TileSet(GameStates.standardTilePathItems+this.getName()+".png", this.getName(), Item.tileSetX, Item.tileSetY, Item.tileSetWidth, Item.tileSetHeight);
		Entity itemEntity = new Entity(woodTileSet, this.getName(), room, id, x, y, GameStates.animationDelayItems);
		this.setX(x);
		this.setY(y);
		this.setEntity(itemEntity);
		this.setStandardCount(10);
	}

	
	
	@Override
	public void place() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void use() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void add() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void add(int count) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void remove() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void remove(int count) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void defineIngredients() {
//		this.addIngredient("wood", 10);
//		this.addIngredient("wood", 10);
	}

	@Override
	protected void defineCraftList() {
//		this.addCraftItem("WoodenSword", 1);
//		this.addCraftItem("LeatherGlove", 1);
	}
	
}