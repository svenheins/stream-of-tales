/* Copyright (C) Sven Heins - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * author: Sven Heins
 * date: 05.07.2015
 */
package de.svenheins.objects.items.equipment;

import java.math.BigInteger;

import de.svenheins.messages.ITEMCODE;
import de.svenheins.objects.items.Equipment;

public abstract class Trousers extends Equipment {

	public Trousers(BigInteger id, String name, String room, float x, float y) {
		super(id, name, room, x,y );
		this.setItemCode(ITEMCODE.TROUSERS);
		this.setCapacity(1);
		this.setCount(1);
	}
	
	public Trousers(String name) {
		super(name);
		this.setItemCode(ITEMCODE.TROUSERS);
		this.setCapacity(1);
		this.setCount(1);
	}

	@Override
	public void equip() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dequip() {
		// TODO Auto-generated method stub
		
	}
}
