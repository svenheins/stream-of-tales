package de.svenheins.objects.items.equipment;

import java.math.BigInteger;

import de.svenheins.messages.ITEMCODE;
import de.svenheins.objects.Entity;
import de.svenheins.objects.items.Equipment;

public class BodyArmor extends Equipment {

	public BodyArmor(BigInteger id) {
		super(id);
		this.setItemCode(ITEMCODE.BODY);
		this.setCapacity(1);
		this.setCount(1);
	}

	@Override
	public void equip() {
		
		
	}

	@Override
	public void dequip() {
	
		
	}

}
