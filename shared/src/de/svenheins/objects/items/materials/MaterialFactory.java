/* Copyright (C) Sven Heins - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * author: Sven Heins
 * date: 05.07.2015
 */
package de.svenheins.objects.items.materials;

import de.svenheins.objects.items.ItemFactory;

public class MaterialFactory extends ItemFactory {

	@Override
	protected void createItems() {
		itemList.put("Wood", new Wood());
		itemList.put("Stone", new Stone());
		itemList.put("Shovel", new Shovel());
		itemList.put("Soil", new Soil());
		itemList.put("Campfire", new Campfire());
		itemList.put("ArtifactStone", new ArtifactStone());
	}
}
