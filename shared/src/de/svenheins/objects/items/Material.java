package de.svenheins.objects.items;

import java.math.BigInteger;

import de.svenheins.messages.ITEMCODE;
import de.svenheins.objects.Entity;

public abstract class Material extends Item implements Stackable, Placeable, Usable {

	public Material(BigInteger id) {
		super(id);
	}

}
