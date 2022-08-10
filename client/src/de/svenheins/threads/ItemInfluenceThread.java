package de.svenheins.threads;

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
				for (Agent agent : AgentManager.agentList.values()) {
					agent.calculateTotalItemInfluence();
				}
			}
			
			try {
				Thread.sleep(200);
			}
			catch(InterruptedException exception) {
				System.out.println(exception);
			}
		}
	}

}
