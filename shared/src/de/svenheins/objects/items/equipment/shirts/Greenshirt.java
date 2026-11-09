/* Copyright (C) Sven Heins - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * author: Sven Heins
 * date: 05.07.2015
 */
package de.svenheins.objects.items.equipment.shirts;

import java.math.BigInteger;

import de.svenheins.main.AttributeType;
import de.svenheins.objects.items.equipment.Shirt;

public class Greenshirt extends Shirt{

	public Greenshirt(BigInteger id,String room, float x, float y) {
		super(id, "Greenshirt", room, x, y);
	}
	
	public Greenshirt() {
		super("Greenshirt");
	}

	@Override
	protected void configStandardAttributes() {
		standardAttributes[AttributeType.ARMOR.ordinal()] = 2.0f;
		this.setLevel(Integer.valueOf(1));
	}
	
	@Override
	protected void defineIngredients() {
		this.addIngredient("Wood", 10);
	}
}
