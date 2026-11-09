/* Copyright (C) Sven Heins - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * author: Sven Heins
 * date: 05.07.2015
 */
package de.svenheins.objects.items;

import java.math.BigInteger;
import de.svenheins.functions.MyMath;
import de.svenheins.main.AttributeType;
import de.svenheins.main.GameStates;
import de.svenheins.objects.Entity;
import de.svenheins.objects.TileSet;

public abstract class Equipment extends Item implements Equipable {

	
	public Equipment(BigInteger id, String name, String room, float x, float y) {
		super(id, room);
		this.setName(name);
		TileSet itemTileSet = new TileSet(GameStates.standardTilePathItems+this.getName()+".png", this.getName(), Item.tileSetX, Item.tileSetY, Item.tileSetWidth, Item.tileSetHeight);
		Entity itemEntity = new Entity(itemTileSet, this.getName(), room, id, x, y, GameStates.animationDelayItems);
		this.setX(x);
		this.setY(y);
		this.setEntity(itemEntity);
		this.setLevel(Integer.valueOf(1));
	}
	
	public Equipment(String name) {
		super(BigInteger.valueOf(0), "empty room of standard constructor in equipment");
		this.setName(name);
		TileSet itemTileSet = new TileSet(GameStates.standardTilePathItems+this.getName()+".png", this.getName(), Item.tileSetX, Item.tileSetY, Item.tileSetWidth, Item.tileSetHeight);
		Entity itemEntity = new Entity(itemTileSet, this.getName(), room, id, 0, 0, GameStates.animationDelayItems);
		this.setX(0);
		this.setY(0);
		this.setEntity(itemEntity);
	}
	
	
	@Override
	protected void rerollAttributes() {
		this.setAttributes(MyMath.getTrimmedGaussianArray(standardAttributes));
		/** first round to one cent of the original value */
		for (int i = 0; i < AttributeType.values().length; i++) {
			this.getAttributes()[i] = (float)(Math.round(this.getAttributes()[i]* 100))/100;
		}
		/** now round the decimal values for maxlife, maxmana, strengh, armor */
		this.getAttributes()[AttributeType.MAXLIFE.ordinal()] = Math.round(this.getAttributes()[AttributeType.MAXLIFE.ordinal()]);
		this.getAttributes()[AttributeType.MAXMANA.ordinal()] = Math.round(this.getAttributes()[AttributeType.MAXMANA.ordinal()]);
		this.getAttributes()[AttributeType.ARMOR.ordinal()] = Math.round(this.getAttributes()[AttributeType.ARMOR.ordinal()]);
		this.getAttributes()[AttributeType.CRAFTLEVEL.ordinal()] = Math.round(this.getAttributes()[AttributeType.CRAFTLEVEL.ordinal()]);
		this.getAttributes()[AttributeType.STRENGH.ordinal()] = Math.round(this.getAttributes()[AttributeType.STRENGH.ordinal()]);
		this.getAttributes()[AttributeType.MAXSPEED.ordinal()] = Math.round(this.getAttributes()[AttributeType.MAXSPEED.ordinal()]);
		this.getAttributes()[AttributeType.LIFEREGENERATION.ordinal()] = Math.round(this.getAttributes()[AttributeType.LIFEREGENERATION.ordinal()]);
		this.getAttributes()[AttributeType.MANAREGENERATION.ordinal()] = Math.round(this.getAttributes()[AttributeType.MANAREGENERATION.ordinal()]);
		this.getAttributes()[AttributeType.ATTACKSQUARESIZE.ordinal()] = Math.round(this.getAttributes()[AttributeType.ATTACKSQUARESIZE.ordinal()]);
		this.getAttributes()[AttributeType.ATTACKDISTANCE.ordinal()] = Math.round(this.getAttributes()[AttributeType.ATTACKDISTANCE.ordinal()]);
		this.getAttributes()[AttributeType.WILLPOWER.ordinal()] = Math.round(this.getAttributes()[AttributeType.WILLPOWER.ordinal()]);

	}

}
