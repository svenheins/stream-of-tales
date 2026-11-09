/* Copyright (C) Sven Heins - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * author: Sven Heins
 * date: 05.07.2015
 */
package de.svenheins.objects;

import java.util.Comparator;

public class ZIndexObjectComparator implements Comparator<LocalObject>{

	@Override
	public int compare(LocalObject z1, LocalObject z2) {
		if(z1 != null && z2 != null) {
			if (z1.getZIndex()<z2.getZIndex()) return -1;
			if (z1.getZIndex()>z2.getZIndex()) return 1;
		}
		return 0;
	}
	

}
