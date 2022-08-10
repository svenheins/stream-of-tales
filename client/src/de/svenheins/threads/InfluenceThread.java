package de.svenheins.threads;

import javax.swing.text.AbstractDocument.AttributeContext;

import de.svenheins.main.AttributeType;
import de.svenheins.main.GUI;
import de.svenheins.main.GameModus;
import de.svenheins.main.GamePanel;
import de.svenheins.managers.AgentManager;
import de.svenheins.managers.InteractionManager;
import de.svenheins.objects.agents.Agent;

public class InfluenceThread implements Runnable {
	private long duration; 
	private long last = System.currentTimeMillis();
	
	@Override
	public void run() {
		while (GUI.running) {
			duration = System.currentTimeMillis() - last;
			last = System.currentTimeMillis();
			if(GameModus.modus == GameModus.GAME) {
//				long timeNow = System.currentTimeMillis();
				/** player */
				
				GamePanel.gp.getPlayerEntity().updateAttributes(duration);
//				System.out.println("Life: "+GamePanel.gp.getPlayerEntity().getAttributes()[AttributeType.LIFE.ordinal()]);
//				System.out.println("LifeReg: "+GamePanel.gp.getPlayerEntity().getAttributes()[AttributeType.LIFEREGENERATION.ordinal()]);
//				System.out.println("MaxLife: "+GamePanel.gp.getPlayerEntity().getAttributes()[AttributeType.MAXLIFE.ordinal()]);
				
				/** agents */
				for (Agent agent : AgentManager.agentList.values()) {
					agent.updateAttributes(duration);
				}
			}
			
			try {
				Thread.sleep(50);
			}
			catch(InterruptedException exception) {
				System.out.println(exception);
			}
		}
	}

}
