/* Copyright (C) Sven Heins - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * author: Sven Heins
 * date: 05.07.2015
 */
package de.svenheins.objects.items.equipment.helmets;

import java.math.BigInteger;

import de.svenheins.main.AttributeType;
import de.svenheins.objects.items.equipment.Helmet;

public class BlondHair extends Helmet{

	public BlondHair(BigInteger id, String room, float x, float y) {
		super(id, "BlondHair", room, x, y);
	}
	
	public BlondHair() {
		super("BlondHair");
	}

	@Override
	protected void configStandardAttributes() {
		standardAttributes[AttributeType.MAXMANA.ordinal()] = 2.0f;
	}

	@Override
	protected void defineIngredients() {
		this.addIngredient("Wood", 10);
	}
}
