package de.svenheins.threads;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;

import de.svenheins.functions.MyMath;
import de.svenheins.main.AttributeType;
import de.svenheins.main.GUI;
import de.svenheins.main.GameModus;
import de.svenheins.main.GamePanel;
import de.svenheins.managers.AgentManager;
import de.svenheins.managers.AreaInfluenceManager;
import de.svenheins.objects.AreaInfluence;
import de.svenheins.objects.ItemInfluence;
import de.svenheins.objects.agents.Agent;

public class AreaInfluenceThread implements Runnable {

	@Override
	public void run() {
		while (GUI.running) {
//			duration = System.currentTimeMillis() - last;
//			last = System.currentTimeMillis();
			if(GameModus.modus == GameModus.GAME) {
				long timeNow = System.currentTimeMillis();
				int index = 0;
				int[] deleteIndex = new int[AreaInfluenceManager.areaInfluenceList.size()];
				HashMap<BigInteger, AreaInfluence> tempAreaInfluenceList = new HashMap<BigInteger, AreaInfluence>(AreaInfluenceManager.areaInfluenceList);
				ArrayList<BigInteger> tempIDList = new ArrayList<>(AreaInfluenceManager.idList);
				
//				for(AreaInfluence areaInfluence: tempAreaInfluenceList) {
				AreaInfluence areaInfluence;
				for(BigInteger tempID: tempIDList) {
					areaInfluence = tempAreaInfluenceList.get(tempID);
//					System.out.println(areaInfluence.getAttributes()[AttributeType.MX.ordinal()]);
					/** first check if the influence has not ended yet and is no instant influence */
					if ( ((areaInfluence.getTimeBegin() <= timeNow) && (areaInfluence.getTimeEnd() >= timeNow)) || (areaInfluence.getTimeBegin() == areaInfluence.getTimeEnd())) {
						/** the influence is either continuous, has started and NOT yet ended OR the influence is instantaneous */
						/** ACTIVE INFLUENCE AREA!!! */
						/** check if player is target of the influence */
						if (areaInfluence.getLocalObject().collides(GamePanel.gp.getPlayerEntity())) {
							if ( (!areaInfluence.isExclusive() && GamePanel.gp.getPlayerEntity().getGroupName().equals(areaInfluence.getGroupName())) ||
									(areaInfluence.isExclusive() && !GamePanel.gp.getPlayerEntity().getGroupName().equals(areaInfluence.getGroupName()))) {
								/** player is in the relevant group, that is targeted by the influence */
//								System.out.println("player is influenced: MX="+ areaInfluence.getAttributes()[AttributeType.MX.ordinal()]);
								GamePanel.gp.getPlayerEntity().setTempTotalAreaInfluences(MyMath.addFloatArrays(GamePanel.gp.getPlayerEntity().getTempTotalAreaInfluences(), areaInfluence.getAttributes()));
							} else {
								/** player is NOT in the relevant group, that is targeted by the influence
								 * -> ignore influence! */
							}
						} else {
							/** out of boundaries -> player is not a target of the influence */
						}
						
						/** check if the agents are target of the influence */
						for (Agent agent: AgentManager.agentList.values()) {
							if (areaInfluence.getLocalObject().collides(agent)) {
								agent.setTempTotalAreaInfluences(MyMath.addFloatArrays(agent.getTempTotalAreaInfluences(), areaInfluence.getAttributes()));
							} else {
								/** out of boundaries -> agent is not a target of the influence */
							}
						}
					} else {
						/** the influence has not yet started, or has already ended and is not istantaneous
						 * -> do nothing, because the removal happens later */
					}
					
					/** finally remove the instantaneous influence (if applicable) or ended influence*/
					if (areaInfluence.getTimeBegin() == areaInfluence.getTimeEnd()) {
						/** instantaneous influence */
						deleteIndex[index] = 1;
					} else if (areaInfluence.getTimeEnd() < timeNow) {
						/** ended continuous influence */
						deleteIndex[index] = 1;
					}
					
					index += 1; // index of the next iterator element 
				}
				
				/** delete the invalid areaInfluences */
				for (int i = deleteIndex.length-1; i>= 0; i--) {
					if (deleteIndex[i] == 1) {
						tempAreaInfluenceList.remove(tempIDList.get(i));
						tempIDList.remove(i);
					}
				}
				/** update the AreaInfluenceList of the AreaInfluenceManager */
				AreaInfluenceManager.areaInfluenceList = tempAreaInfluenceList;
				AreaInfluenceManager.idList = tempIDList;
				
				/** finally determine the totalInfluence of each playerEntity and reset the temporarily variables */
				GamePanel.gp.getPlayerEntity().setTotalAreaInfluences(GamePanel.gp.getPlayerEntity().getTempTotalAreaInfluences());
				GamePanel.gp.getPlayerEntity().setTempTotalAreaInfluences(new float[AttributeType.values().length]);
				
				/** now for each agent determine the totalInfluence and reset the temporarily variables */
				for (Agent agent: AgentManager.agentList.values()) {
					agent.setTotalAreaInfluences(agent.getTempTotalAreaInfluences());
					agent.setTempTotalAreaInfluences(new float[AttributeType.values().length]);
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
