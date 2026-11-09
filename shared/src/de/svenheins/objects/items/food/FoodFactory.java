/* Copyright (C) Sven Heins - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * author: Sven Heins
 * date: 05.07.2015
 */
package de.svenheins.objects.items.food;

import de.svenheins.objects.items.ItemFactory;

public class FoodFactory extends ItemFactory {

	@Override
	protected void createItems() {
		itemList.put("Apple", new Apple());
		itemList.put("SmallPotionOfLife", new SmallPotionOfLife());
	}
}
