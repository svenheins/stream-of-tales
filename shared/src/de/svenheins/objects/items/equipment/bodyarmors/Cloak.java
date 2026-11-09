/* Copyright (C) Sven Heins - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * author: Sven Heins
 * date: 05.07.2015
 */
package de.svenheins.objects.items.equipment.bodyarmors;

import java.math.BigInteger;

import de.svenheins.main.AttributeType;
import de.svenheins.objects.items.equipment.BodyArmor;

public class Cloak extends BodyArmor{

	public Cloak(BigInteger id, String room, float x, float y) {
		super(id, "Cloak", room, x, y);
	}
	
	public Cloak() {
		super("Cloak");
	}


@Override
protected void configStandardAttributes() {
	standardAttributes[AttributeType.ARMOR.ordinal()] = 3.0f;
	setLevel(Integer.valueOf(1));
}

@Override
protected void defineIngredients() {
	this.addIngredient("Wood", 10);
}


	
}
