/* Copyright (C) Sven Heins - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * author: Sven Heins
 * date: 05.07.2015
 */
package de.svenheins.objects.items.equipment;

import de.svenheins.objects.items.ItemFactory;
import de.svenheins.objects.items.equipment.trousers.Leatherpants;

public class TrousersFactory extends ItemFactory {

	@Override
	protected void createItems() {
//		itemList.add(new Cloak(BigInteger.valueOf(0), 0, 0));
		itemList.put("Leatherpants", new Leatherpants());
	}

}
