package de.svenheins.threads;

import java.math.BigInteger;

import de.svenheins.main.GUI;
import de.svenheins.main.GameModus;
import de.svenheins.main.GameWindow;
import de.svenheins.managers.AgentManager;

public class AgentThread implements Runnable {
	private final int sleepingTime = 5;
	
	@Override
	public void run() {
		while (GUI.running) {
//			duration = System.currentTimeMillis() - last;
//			last = System.currentTimeMillis();
//			System.out.println("input-thread runs");
			if(GameModus.modus == GameModus.GAME) {
				runAgents();
			}
			
			try {
				Thread.sleep(sleepingTime);
			}
			catch(InterruptedException exception) {
				System.out.println(exception);
			}
		}
	}

	private void runAgents() {
		AgentManager.runNextAgent(GameWindow.gw.getObjectMapManagers().get("tree1"), GameWindow.gw.getObjectMapManagers().get("tree2"));
//		for (BigInteger id : AgentManager.idList) {
//			AgentManager.get(id).run();
//		}
			
	}

}
