/* Copyright (C) Sven Heins - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * author: Sven Heins
 * date: 05.07.2015
 */
package de.svenheins.objects.items.equipment;

import de.svenheins.objects.items.ItemFactory;
import de.svenheins.objects.items.equipment.helmets.BlondHair;
import de.svenheins.objects.items.equipment.helmets.LeatherHelmet;
import de.svenheins.objects.items.equipment.helmets.ShortBlondHair;

public class HelmetFactory extends ItemFactory  {
	@Override
	protected void createItems() {
		itemList.put("BlondHair", new BlondHair());
		itemList.put("ShortBlondHair", new ShortBlondHair());
		itemList.put("LeatherHelmet", new LeatherHelmet());
	}
}
