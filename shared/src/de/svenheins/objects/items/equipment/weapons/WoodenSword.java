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

public class WoodenSword extends Weapon{
	public WoodenSword(BigInteger id, String room, float x, float y) {
		super(id, "WoodenSword", room, x, y);
	}

	public WoodenSword() {
		super("WoodenSword");
	}

	@Override
	protected void configStandardAttributes() {
		standardAttributes[AttributeType.STRENGH.ordinal()] = 8.0f;
		standardAttributes[AttributeType.ATTACKSQUARESIZE.ordinal()] = 16.0f;
		standardAttributes[AttributeType.ATTACKDISTANCE.ordinal()] = 8.0f;
		standardAttributes[AttributeType.INTERACTWOOD.ordinal()] = 0.0f;
//		standardAttributes[AttributeType.CRAFTLEVEL.ordinal()] = 1.0f;
		this.setLevel(Integer.valueOf(1));
	}

	@Override
	protected void defineIngredients() {
		this.addIngredient("Wood", 5);
//		this.addIngredient(itemName, count);
//		this.addIngredient("stone", 1);
	}
}
