/* Copyright (C) Sven Heins - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * author: Sven Heins
 * date: 05.07.2015
 */
package de.svenheins.threads;

import java.util.ArrayList;
import java.util.List;

import de.svenheins.main.AttributeType;
import de.svenheins.main.GUI;
import de.svenheins.main.GameModus;
import de.svenheins.main.GamePanel;
import de.svenheins.main.GameStates;
import de.svenheins.main.GameWindow;
import de.svenheins.main.SoundEffect;
import de.svenheins.managers.AgentManager;
import de.svenheins.managers.RewardManager;
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
				
//				float lifeBefore = GamePanel.gp.getPlayerEntity().getAttributes()[AttributeType.LIFE.ordinal()];
				GamePanel.gp.getPlayerEntity().updateAttributes(duration);
//				float lifeAfter = GamePanel.gp.getPlayerEntity().getAttributes()[AttributeType.LIFE.ordinal()];
//				if (lifeBefore>lifeAfter) GameWindow.gw.sound.addStartingSoundEffect(SoundEffect.STANDARDPLAYER_HURT);
				if (GamePanel.gp.getPlayerEntity().getSound().equals("hurting")) {
					GamePanel.gp.getPlayerEntity().setSound("");
					GameWindow.gw.sound.addStartingSoundEffect(SoundEffect.STANDARDPLAYER_HURT);
				}
				if (GamePanel.gp.getPlayerEntity().getAttributes()[AttributeType.LIFE.ordinal()] <= 0) {
					RewardManager.sendKillExperience(GameStates.streamExperienceForKillingPlayer, GamePanel.gp.getPlayerEntity().getDamagedBy(), GamePanel.gp.getPlayerEntity().getName(), true);
					GamePanel.gp.restartAfterDeath();
				}
//				System.out.println("Life: "+GamePanel.gp.getPlayerEntity().getAttributes()[AttributeType.LIFE.ordinal()]);
//				System.out.println("LifeReg: "+GamePanel.gp.getPlayerEntity().getAttributes()[AttributeType.LIFEREGENERATION.ordinal()]);
//				System.out.println("MaxLife: "+GamePanel.gp.getPlayerEntity().getAttributes()[AttributeType.MAXLIFE.ordinal()]);
				
				/** agents */
				List<Agent> tempAgentList = new ArrayList<Agent>(AgentManager.agentList.values());
				for (Agent agent : tempAgentList) {
					
					if (agent != null) {
//						float lifeBeforeAgent = agent.getAttributes()[AttributeType.LIFE.ordinal()]+1.0f-1.0f;
						agent.updateAttributes(duration);
						if (agent.getSound().equals("hurting")) {
							agent.setSound("");
							GameWindow.gw.sound.addStartingSoundEffect(SoundEffect.WOLF_HURT);
						}
						
//						float lifeAfterAgent = agent.getAttributes()[AttributeType.LIFE.ordinal()];
//						if (lifeBeforeAgent>lifeAfterAgent) GameWindow.gw.sound.addStartingSoundEffect(SoundEffect.WOLF_HURT);
					}
				}
				
//				/** players */
//				List<BigInteger> idListTempPlayers = new ArrayList<BigInteger>(PlayerManager.idList);
//				for (BigInteger i: idListTempPlayers) {
//					PlayerEntity e= PlayerManager.get(i);
//					if (e != null) {
//						e.updateAttributes(duration);
//					}
//				}
				
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
