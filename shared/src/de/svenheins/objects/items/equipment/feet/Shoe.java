/* Copyright (C) Sven Heins - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * author: Sven Heins
 * date: 05.07.2015
 */
package de.svenheins.objects.items.equipment.feet;

import java.math.BigInteger;

import de.svenheins.main.AttributeType;
import de.svenheins.objects.items.equipment.Feet;

public class Shoe extends Feet {


	public Shoe(BigInteger id, String room, float x, float y) {
		super(id, "Shoe", room, x, y);	
	}
	
	public Shoe() {
		super("Shoe");
	}

	@Override
	protected void configStandardAttributes() {
		standardAttributes[AttributeType.ARMOR.ordinal()] = 2.0f;
		standardAttributes[AttributeType.MAXSPEED.ordinal()] = 50.0f;
		this.setLevel(Integer.valueOf(2));
	}
	
	@Override
	protected void defineIngredients() {
		this.addIngredient("Wood", 10);
	}
}
