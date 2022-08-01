package de.svenheins.objects.items.materials;

import java.math.BigInteger;

import de.svenheins.main.GameStates;
import de.svenheins.messages.ITEMCODE;
import de.svenheins.objects.Entity;
import de.svenheins.objects.TileSet;
import de.svenheins.objects.items.Item;
import de.svenheins.objects.items.Material;

public class Wood extends Material {
	


	public Wood(BigInteger id, float x, float y) {
		super(id);
		this.setItemCode(ITEMCODE.WOOD);
		this.setCount(1);
		this.setCapacity(GameStates.itemMaterialCapacity);
		this.setName("wood");
//		TileSet woodTileSet = new TileSet(GameStates.standardTilePathItems+"wood2.png", this.getName(), GameStates.itemTileWidth, GameStates.itemTileHeight);
		TileSet woodTileSet = new TileSet(GameStates.standardTilePathItems+"wood2.png", this.getName(), Item.tileSetX, Item.tileSetY, Item.tileSetWidth, Item.tileSetHeight);
		Entity itemEntity = new Entity(woodTileSet, this.getName(), id, x, y, GameStates.animationDelayItems);
		this.setItemEntity(itemEntity);
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

}
