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
