/* Copyright (C) Sven Heins - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * author: Sven Heins
 * date: 05.07.2015
 */
package de.svenheins.objects.items.equipment.shields;

import java.math.BigInteger;

import de.svenheins.main.AttributeType;
import de.svenheins.objects.items.equipment.Shield;

public class LeatherShield extends Shield{

	public LeatherShield(BigInteger id, String room, float x,float y) {
		super(id, "LeatherShield", room, x, y);
	}

	public LeatherShield() {
		super("LeatherShield");
	}
	
	@Override
	protected void configStandardAttributes() {
		standardAttributes[AttributeType.ARMOR.ordinal()] = 4.0f;
		this.setLevel(Integer.valueOf(2));
	}

	@Override
	protected void defineIngredients() {
		this.addIngredient("Wood", 10);
	}
}
