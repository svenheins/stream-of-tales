/* Copyright (C) Sven Heins - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * author: Sven Heins
 * date: 05.07.2015
 */
package de.svenheins.objects.items.equipment.weapons;

import java.math.BigInteger;

import de.svenheins.main.AttributeType;
import de.svenheins.objects.items.equipment.Weapon;

public class StoneAxe extends Weapon {
	public StoneAxe(BigInteger id, String room, float x, float y) {
		super(id, "StoneAxe", room, x, y);
	}

	public StoneAxe() {
		super("StoneAxe");
	}

	@Override
	protected void configStandardAttributes() {
		standardAttributes[AttributeType.STRENGH.ordinal()] = 8.0f;
		standardAttributes[AttributeType.ATTACKSQUARESIZE.ordinal()] = 16.0f;
		standardAttributes[AttributeType.ATTACKDISTANCE.ordinal()] = 8.0f;
		standardAttributes[AttributeType.INTERACTWOOD.ordinal()] = 40.0f;
		standardAttributes[AttributeType.INTERACTSTONE.ordinal()] = 30.0f;
		standardAttributes[AttributeType.INTERACTSOIL.ordinal()] = 60.0f;
//		standardAttributes[AttributeType.CRAFTLEVEL.ordinal()] = 1.0f;
		this.setLevel(Integer.valueOf(1));
	}

	@Override
	protected void defineIngredients() {
		this.addIngredient("Wood", 4);
//		this.addIngredient(itemName, count);
		this.addIngredient("Stone", 3);
	}
}
