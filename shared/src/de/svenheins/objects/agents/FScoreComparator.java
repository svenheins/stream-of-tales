/* Copyright (C) Sven Heins - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * author: Sven Heins
 * date: 05.07.2015
 */
package de.svenheins.objects.agents;

import java.util.Comparator;

public class FScoreComparator implements Comparator<PathTile>{

	@Override
	public int compare(PathTile pTile1, PathTile pTile2) {
		if(pTile1 != null && pTile2 != null) {
			if (pTile1.getFScore()<pTile2.getFScore()) return -1;
			if (pTile1.getFScore()>pTile2.getFScore()) return 1;
		}
		return 0;
	}

}
