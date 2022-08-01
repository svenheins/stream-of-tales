package de.svenheins.objects.items.equipment.bodyarmors;

import java.math.BigInteger;

import de.svenheins.main.GameStates;
import de.svenheins.objects.Entity;
import de.svenheins.objects.TileSet;
import de.svenheins.objects.items.Item;
import de.svenheins.objects.items.equipment.BodyArmor;

public class Cloak extends BodyArmor{
	private float[] standardStates = new float[]{0.5f, 0.5f, 0.5f};

	public Cloak(BigInteger id, float x, float y, float[] states) {
		super(id);
		this.setName("Cloak");
//		TileSet woodTileSet = new TileSet(GameStates.standardTilePathItems+"wood2.png", this.getName(), GameStates.itemTileWidth, GameStates.itemTileHeight);
		TileSet cloakTileSet = new TileSet(GameStates.standardTilePathItems+"cloak.png", this.getName(), Item.tileSetX, Item.tileSetY, Item.tileSetWidth, Item.tileSetHeight);
		Entity itemEntity = new Entity(cloakTileSet, this.getName(), id, x, y, GameStates.animationDelayItems);
		this.setX(x);
		this.setY(y);
		this.setEntity(itemEntity);
		this.setStates(states);
		
	}

	public Cloak(BigInteger id, float x, float y) {
		super(id);
		this.setName("Cloak");
//		TileSet woodTileSet = new TileSet(GameStates.standardTilePathItems+"wood2.png", this.getName(), GameStates.itemTileWidth, GameStates.itemTileHeight);
		TileSet cloakTileSet = new TileSet(GameStates.standardTilePathItems+"cloak.png", this.getName(), Item.tileSetX, Item.tileSetY, Item.tileSetWidth, Item.tileSetHeight);
		Entity itemEntity = new Entity(cloakTileSet, this.getName(), id, x, y, GameStates.animationDelayItems);
		this.setX(x);
		this.setY(y);
		this.setEntity(itemEntity);
		
		
		this.setStates(standardStates);
		
	}
}
