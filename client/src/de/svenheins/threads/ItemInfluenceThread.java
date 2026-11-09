/* Copyright (C) Sven Heins - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * author: Sven Heins
 * date: 05.07.2015
 */
package de.svenheins.threads;

import java.math.BigInteger;
import java.util.ArrayList;

import de.svenheins.main.GUI;
import de.svenheins.main.GameModus;
import de.svenheins.main.GamePanel;
import de.svenheins.managers.AgentManager;
import de.svenheins.objects.agents.Agent;

public class ItemInfluenceThread implements Runnable {


	
	@Override
	public void run() {
		while (GUI.running) {
			if(GameModus.modus == GameModus.GAME) {
				/** apply all itemsInfluences of the playerEntity */
				GamePanel.gp.getPlayerEntity().calculateTotalItemInfluence();
			
				/** loop through agents and apply all itemInfluences of them */
				ArrayList<BigInteger> tempAgentIDList = new ArrayList<BigInteger>(AgentManager.idList);
				for (BigInteger agentID : tempAgentIDList) {
					Agent agent = AgentManager.get(agentID);
					if(agent != null) {
						agent.calculateTotalItemInfluence();
					}
				}
			}
			
			try {
				Thread.sleep(100);
			}
			catch(InterruptedException exception) {
				System.out.println(exception);
			}
		}
	}

}
