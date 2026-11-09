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

public class Stone extends Material {
	
	public Stone() {
		super(ITEMCODE.STONE, "Stone");
		this.setStandardCount(5);
		this.setCapacity(GameStates.itemMaterialCapacity);
	}

	public Stone(BigInteger id, String room, float x, float y) {
		super(id, room);
		this.setItemCode(ITEMCODE.STONE);
		this.setCount(1);
		this.setCapacity(GameStates.itemMaterialCapacity);
		this.setName("Stone");
//		TileSet woodTileSet = new TileSet(GameStates.standardTilePathItems+"wood2.png", this.getName(), GameStates.itemTileWidth, GameStates.itemTileHeight);
		TileSet stoneTileSet = new TileSet(GameStates.standardTilePathItems+this.getName()+".png", this.getName(), Item.tileSetX, Item.tileSetY, Item.tileSetWidth, Item.tileSetHeight);
		Entity itemEntity = new Entity(stoneTileSet, this.getName(), room, id, x, y, GameStates.animationDelayItems);
		this.setX(x);
		this.setY(y);
		this.setEntity(itemEntity);
		this.setStandardCount(5);
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
		
	}

	@Override
	protected void defineCraftList() {
		this.addCraftItem("IronSword", 1);
		this.addCraftItem("StoneAxe", 1);
	}
}
