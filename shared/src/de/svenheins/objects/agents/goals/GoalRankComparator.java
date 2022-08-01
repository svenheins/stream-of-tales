package de.svenheins.objects.agents.goals;

import java.util.Comparator;

public class GoalRankComparator implements Comparator<Goal>{

	@Override
	public int compare(Goal g1, Goal g2) {
		if(g1 != null && g2 != null) {
			if (g1.getRank()<g2.getRank()) return -1;
			if (g1.getRank()>g2.getRank()) return 1;
		}
		return 0;
	}

}
