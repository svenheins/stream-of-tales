/* Copyright (C) Sven Heins - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * author: Sven Heins
 * date: 05.07.2015
 */
package de.svenheins.objects.items.equipment.amulets;

import java.math.BigInteger;

import de.svenheins.main.AttributeType;
import de.svenheins.objects.items.equipment.Amulet;

public class GoldenAmulet extends Amulet{

	public GoldenAmulet(BigInteger id, String room, float x,float y) {
		super(id, "GoldenAmulet", room, x, y);
	}
	
	public GoldenAmulet() {
		super("GoldenAmulet");
	}

	@Override
	protected void configStandardAttributes() {
		standardAttributes[AttributeType.MAXMANA.ordinal()] = 10.0f;
		standardAttributes[AttributeType.MAXLIFE.ordinal()] = 30.0f;
		standardAttributes[AttributeType.CRAFTLEVEL.ordinal()] = 1.0f;
		this.setLevel(Integer.valueOf(3));
	}

	@Override
	protected void defineIngredients() {
		this.addIngredient("Wood", 10);
	}
	
	
	

}
