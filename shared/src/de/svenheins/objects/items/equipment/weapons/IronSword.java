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

public class IronSword extends Weapon{
	public IronSword(BigInteger id, String room, float x, float y) {
		super(id, "IronSword", room, x, y);
	}

	public IronSword() {
		super("IronSword");
	}

	@Override
	protected void configStandardAttributes() {
		standardAttributes[AttributeType.STRENGH.ordinal()] = 12.0f;
		standardAttributes[AttributeType.ATTACKSQUARESIZE.ordinal()] = 16.0f;
		standardAttributes[AttributeType.ATTACKDISTANCE.ordinal()] = 8.0f;
		this.setLevel(Integer.valueOf(3));
	}
	
	@Override
	protected void defineIngredients() {
		this.addIngredient("Stone", 10);
	}
}
