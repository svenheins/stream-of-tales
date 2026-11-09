/* Copyright (C) Sven Heins - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * author: Sven Heins
 * date: 05.07.2015
 */
package de.svenheins.managers;

import de.svenheins.objects.TextEffect;

public class RewardManager {

	public static void sendKillExperience(int exp, String player, String victim, boolean killedPlayer) {
		String[] playerParts = player.split(";");
		
		if (playerParts.length == 3) {
			if (playerParts[0].equals("player")) {
//				String playerName = playerParts[1];
				if (victim.split(": ").length > 1) {
					String playerName = playerParts[1];
					TextEffectManager.add(new TextEffect("Player "+playerName+" killed "+victim));
				}
//				TextEffectManager.add(new TextEffect("Player "+playerName+" killed "+victim));
			}
			if (playerParts[0].equals("agent")) {
				String[] agentName = playerParts[1].split(": ");
				if (agentName.length > 1) {
					/*** twitchViewerAgent*/
					String viewer = agentName[0];
					TextEffectManager.add(new TextEffect("Viewer "+viewer+" killed "+victim+" and got "+exp+" experience!"));
					StreamViewerManager.get(viewer).addCountKilledEnemies(1);
					StreamViewerManager.get(viewer).addExp(exp);
					if (killedPlayer) StreamViewerManager.get(viewer).addCountKilledPlayers(1);
				} else {
					/** normal agent */
					TextEffectManager.add(new TextEffect("Agent "+playerParts[1]+" killed "+victim+"!"));
				}
			}
		} else if (playerParts.length == 6) {
			if (playerParts[0].equals("player")) {
				if (victim.split(": ").length > 1) {
					String playerName = playerParts[1];
					TextEffectManager.add(new TextEffect("Player "+playerName+" killed "+victim));
				}
//				String playerName = playerParts[1];
//				TextEffectManager.add(new TextEffect("Player "+playerName+" killed "+victim));
			}
			if (playerParts[0].equals("agent")) {
				String[] agentName = playerParts[1].split(": ");
				if (agentName.length > 1) {
					/*** twitchViewerAgent*/
					String viewer = agentName[0];
//					System.out.println();
					TextEffectManager.add(new TextEffect("Viewer "+viewer+" killed "+victim+" and got "+exp+" experience!"));
					
					if (StreamViewerManager.get(viewer) != null) {
						StreamViewerManager.get(viewer).addCountKilledEnemies(1);
						StreamViewerManager.get(viewer).addExp(exp);
						if (killedPlayer) StreamViewerManager.get(viewer).addCountKilledPlayers(1);
					}
				} else {
					/** normal agent */
					TextEffectManager.add(new TextEffect("Agent "+playerParts[1]+" killed "+victim+"!"));
				}
			}
		} else {
			System.out.println("player has wrong format: "+playerParts.length + " "+player);
			// wrong format of player
		}
	}
}
