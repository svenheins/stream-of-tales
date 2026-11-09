/* Copyright (C) Sven Heins - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * author: Sven Heins
 * date: 05.07.2015
 */
package de.svenheins.objects.items.food;

import java.math.BigInteger;

import de.svenheins.main.AttributeType;
import de.svenheins.main.GameStates;
import de.svenheins.main.Priority;
import de.svenheins.messages.ITEMCODE;
import de.svenheins.objects.Entity;
import de.svenheins.objects.ItemInfluence;
import de.svenheins.objects.TileSet;
import de.svenheins.objects.items.Food;
import de.svenheins.objects.items.Item;

public class SmallPotionOfLife extends Food {
	public SmallPotionOfLife() {
		super(ITEMCODE.SMALLPOTIONOFLIFE, "SmallPotionOfLife");
		this.setFluid(true);
		this.setStandardCount(10);
		this.setCapacity(GameStates.itemMaterialCapacity);
	}

	public SmallPotionOfLife(BigInteger id, String room, float x, float y) {
		super(id, room);
		this.setFluid(true);
		this.setItemCode(ITEMCODE.SMALLPOTIONOFLIFE);
		this.setCount(1);
		this.setCapacity(GameStates.itemMaterialCapacity);
		this.setName("SmallPotionOfLife");
//		TileSet woodTileSet = new TileSet(GameStates.standardTilePathItems+"wood2.png", this.getName(), GameStates.itemTileWidth, GameStates.itemTileHeight);
		TileSet woodTileSet = new TileSet(GameStates.standardTilePathItems+"smallpotionoflife.png", this.getName(), Item.tileSetX, Item.tileSetY, Item.tileSetWidth, Item.tileSetHeight);
		Entity itemEntity = new Entity(woodTileSet, this.getName(), room, id, x, y, GameStates.animationDelayItems);
		this.setX(x);
		this.setY(y);
		this.setEntity(itemEntity);
		this.setStandardCount(10);
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
//		this.addCraftItem("WoodenSword", 1);
//		this.addCraftItem("Shovel", 10);
//		this.addCraftItem("LeatherGlove", 1);
	}

	@Override
	protected void defineEffect() {
		float[] attributes = new float[AttributeType.values().length];
		attributes[AttributeType.LIFEREGENERATION.ordinal()] = 10.0f;
		ItemInfluence tempEffect = new ItemInfluence(0, 0, attributes, Priority.MEDIUM);
		this.setEffect(tempEffect);
		this.setEffectDuration(3000);
	}
}
