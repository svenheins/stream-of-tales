/* Copyright (C) Sven Heins - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * author: Sven Heins
 * date: 05.07.2015
 */
package de.svenheins.threads;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.List;

import de.svenheins.functions.MyMath;
import de.svenheins.main.AttributeType;
import de.svenheins.main.GUI;
import de.svenheins.main.GameModus;
import de.svenheins.main.GamePanel;
import de.svenheins.managers.AgentManager;
import de.svenheins.managers.AreaInfluenceManager;
import de.svenheins.objects.AreaInfluence;
import de.svenheins.objects.LocalObject;
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
//				System.out.println("Creating tempAreaInfluenceList");
				HashMap<BigInteger, AreaInfluence> tempAreaInfluenceList = new HashMap<BigInteger, AreaInfluence>(AreaInfluenceManager.areaInfluenceList);
				ArrayList<BigInteger> tempIDList = new ArrayList<>(AreaInfluenceManager.idList);
				int[] deleteIndex = new int[tempIDList.size()];
				
//				System.out.println("Before : tempAreainfluence.size = "+tempAreaInfluenceList.size());
//				System.out.println("Before : areainfluence.size = "+AreaInfluenceManager.areaInfluenceList.size());
//				for(AreaInfluence areaInfluence: tempAreaInfluenceList) {
				AreaInfluence areaInfluence;
				for(BigInteger tempID: tempIDList) {
					areaInfluence = tempAreaInfluenceList.get(tempID);
					if (areaInfluence != null) {
	//					System.out.println("processing ID = "+areaInfluence.getId().longValue() + " with attribute = "+areaInfluence.getAttributes()[AttributeType.LIFE.ordinal()]);
						/** first check if the influence has not ended yet and is no instant influence */
						if ( ((areaInfluence.getTimeBegin() <= timeNow) && (areaInfluence.getTimeEnd() >= timeNow)) || (areaInfluence.getTimeBegin() == areaInfluence.getTimeEnd())) {
							/** the influence is either continuous, has started and NOT yet ended OR the influence is instantaneous */
							/** ACTIVE INFLUENCE AREA!!! */
	//						System.out.println("Active Influence!");
							/** check if player is target of the influence */
							if (areaInfluence.getLocalObject().collides(GamePanel.gp.getPlayerEntity())) {
								if ( (!areaInfluence.isExclusive() && GamePanel.gp.getPlayerEntity().getGroupName().equals(areaInfluence.getGroupName())) ||
										(areaInfluence.isExclusive() && !GamePanel.gp.getPlayerEntity().getGroupName().equals(areaInfluence.getGroupName()))) {
									/** player is in the relevant group, that is targeted by the influence */
	//								System.out.println("player is influenced: MX="+ areaInfluence.getAttributes()[AttributeType.MX.ordinal()]);
									GamePanel.gp.getPlayerEntity().setTempTotalAreaInfluences(MyMath.addFloatArrays(GamePanel.gp.getPlayerEntity().getTempTotalAreaInfluences(), areaInfluence.getAttributes()));
									if (areaInfluence.getAttributes()[AttributeType.LIFE.ordinal()] < 0) {
										GamePanel.gp.getPlayerEntity().setDamagedBy(areaInfluence.getCreator());
										System.out.println("Agent "+GamePanel.gp.getPlayerEntity().getName()+" was damaged by "+areaInfluence.getCreator());
									} else if (areaInfluence.getAttributes()[AttributeType.LIFE.ordinal()] > 0) {
										GamePanel.gp.getPlayerEntity().setHealedBy(areaInfluence.getCreator());
									} else {
										// nothing interesting
									}
								} else {
									/** player is NOT in the relevant group, that is targeted by the influence
									 * -> ignore influence! */
								}
							} else {
								/** out of boundaries -> player is not a target of the influence */
							}
							
							/** check if the agents are target of the influence */
							List<Agent> tempAgentList = new ArrayList<Agent>(AgentManager.agentList.values());
							for (Agent agent: tempAgentList) {
								if (agent != null) {
									LocalObject agentCollisionBox = new LocalObject(BigInteger.valueOf(0), "", agent.getRoom(), agent.getX(), agent.getY()+agent.getHeight()/2, agent.getWidth(), agent.getHeight()/2, 0, 0, 0, 0);
									if (areaInfluence.getLocalObject().collides(agentCollisionBox)) {
										if ( (!areaInfluence.isExclusive() && agent.getGroupName().equals(areaInfluence.getGroupName())) ||
												(areaInfluence.isExclusive() && !agent.getGroupName().equals(areaInfluence.getGroupName()))) {
	//										System.out.println("Agent "+agent.getName()+" with ID = "+agent.getId().longValue() + " is influenced");
											
											agent.setTempTotalAreaInfluences(MyMath.addFloatArrays(agent.getTempTotalAreaInfluences(), areaInfluence.getAttributes()));
											
											if (areaInfluence.getAttributes()[AttributeType.LIFE.ordinal()] < 0) {
												agent.setDamagedBy(areaInfluence.getCreator());
												System.out.println("Agent "+agent.getName()+" was damaged by "+areaInfluence.getCreator());
											} else if (areaInfluence.getAttributes()[AttributeType.LIFE.ordinal()] > 0) {
												agent.setHealedBy(areaInfluence.getCreator());
											} else {
												// nothing interesting
											}
										} else {
											/** not target of influence */
										}
									} else {
										/** out of boundaries -> agent is not a target of the influence */
									}
								}
							}
							
							
	//						List<BigInteger> idListTempPlayers = new ArrayList<BigInteger>(PlayerManager.idList);
	//						for (BigInteger i: idListTempPlayers) {
	//							PlayerEntity e= PlayerManager.get(i);
	//							if (e != null) {
	//								if (areaInfluence.getLocalObject().collides(e)) {
	//									e.setTempTotalAreaInfluences(MyMath.addFloatArrays(e.getTempTotalAreaInfluences(), areaInfluence.getAttributes()));
	////									System.out.println("player " + e.getName() + " gets " + e.getTempTotalAreaInfluences()[AttributeType.MX.ordinal()]);
	//								}
	//							}
//						}
						
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
					}
					
					index += 1; // index of the next iterator element 
				}
//				System.out.println("After : areainfluence.size = "+AreaInfluenceManager.areaInfluenceList.size());
//				int tempSizeBeforeDeleting = tempAreaInfluenceList.size();
//				int sizeBeforeDeleting = AreaInfluenceManager.areaInfluenceList.size();
				/** delete the invalid areaInfluences */
				for (int i = deleteIndex.length-1; i>= 0; i--) {
					if (deleteIndex[i] == 1) {
						tempAreaInfluenceList.remove(tempIDList.get(i));
						tempIDList.remove(i);
//						System.out.println("removed index = "+i);
					}
				}
//				int sizeNow = AreaInfluenceManager.areaInfluenceList.size();
//				if ( tempSizeBeforeDeleting == sizeNow && tempSizeBeforeDeleting == sizeBeforeDeleting) {
				/** update the AreaInfluenceList of the AreaInfluenceManager */
				AreaInfluenceManager.areaInfluenceList = tempAreaInfluenceList;
				AreaInfluenceManager.idList = tempIDList;
//					System.out.println("After Equalizing : areainfluence.size = "+AreaInfluenceManager.areaInfluenceList.size());
//				} else {
////					System.out.println("Error: size of areaInfluence Lists is not matching! temp = "+tempSizeBeforeDeleting+" vs sizaAreaInfluence = "+sizeNow);
//				}
				
				if (AreaInfluenceManager.areaInfluenceList.size() <= 0) {
					AreaInfluenceManager.setMaxIDValue(BigInteger.valueOf(0));
				}
				
				/** finally determine the totalInfluence of each playerEntity and reset the temporarily variables */
				GamePanel.gp.getPlayerEntity().setTotalAreaInfluences(GamePanel.gp.getPlayerEntity().getTempTotalAreaInfluences());
				GamePanel.gp.getPlayerEntity().setTempTotalAreaInfluences(new float[AttributeType.values().length]);
				
				/** now for each agent determine the totalInfluence and reset the temporarily variables */
				ArrayList<Agent> tempAgentList = new ArrayList<Agent>();
				try {
					tempAgentList = new ArrayList<Agent>(AgentManager.agentList.values());
				} catch (ConcurrentModificationException ex) {
					ex.printStackTrace();
				}
				
				for (Agent agent: tempAgentList) {
					agent.setTotalAreaInfluences(agent.getTempTotalAreaInfluences());
					agent.setTempTotalAreaInfluences(new float[AttributeType.values().length]);
//					if (agent.getTotalAreaInfluences()[AttributeType.LIFE.ordinal()] != 0 )
//						System.out.println("Life = "+agent.getTotalAreaInfluences()[AttributeType.LIFE.ordinal()]);
				}
				
//				/** for all other players update areaInfluence */
//				List<BigInteger> idListTempPlayers = new ArrayList<BigInteger>(PlayerManager.idList);
//				for (BigInteger i: idListTempPlayers) {
//					PlayerEntity e= PlayerManager.get(i);
//					if (e != null) {
//						e.setTotalAreaInfluences(e.getTempTotalAreaInfluences());
//						e.setTempTotalAreaInfluences(new float[AttributeType.values().length]);
////						System.out.println("player " + e.getName() + " HAS " + e.getTotalAreaInfluences()[AttributeType.MX.ordinal()]);
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
