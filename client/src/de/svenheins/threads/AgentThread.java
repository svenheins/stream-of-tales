/* Copyright (C) Sven Heins - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * author: Sven Heins
 * date: 05.07.2015
 */
package de.svenheins.threads;

import java.awt.Point;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Random;

import com.sun.sgs.client.ClientChannel;

import de.svenheins.functions.MyMath;
import de.svenheins.functions.MyUtil;
import de.svenheins.main.AttributeType;
import de.svenheins.main.GUI;
import de.svenheins.main.GameModus;
import de.svenheins.main.GamePanel;
import de.svenheins.main.GameStates;
import de.svenheins.main.GameWindow;
import de.svenheins.main.Groups;
import de.svenheins.main.SoundEffect;
import de.svenheins.main.WorldType;
import de.svenheins.managers.AgentManager;
import de.svenheins.managers.AreaInfluenceManager;
import de.svenheins.managers.ItemManager;
import de.svenheins.managers.LightManager;
import de.svenheins.managers.PlayerAgentManager;
import de.svenheins.managers.PlayerAgentManagerManager;
import de.svenheins.managers.PlayerManager;
import de.svenheins.managers.TileSetManager;
import de.svenheins.messages.ClientMessages;
import de.svenheins.objects.PlayerEntity;
import de.svenheins.objects.TileSet;
import de.svenheins.objects.WorldPosition;
import de.svenheins.objects.agents.Agent;
import de.svenheins.objects.agents.AggressiveAgent;
import de.svenheins.objects.agents.goals.Goal;
import de.svenheins.objects.items.Item;
import de.svenheins.objects.items.ItemFactoryCollection;

public class AgentThread implements Runnable {
	private final int sleepingTime = 1;
	private long delayBetweenAgentCreation = 500;
	private long lastAgentCreation = 0;
	

	@Override
	public void run() {
		while (GUI.running) {
//			duration = System.currentTimeMillis() - last;
//			last = System.currentTimeMillis();
//			System.out.println("input-thread runs");
			if(GameModus.modus == GameModus.GAME) {
				if (AgentManager.size() > 0) {
					runAgents();
				}
				
				/** create new agents */
				if (AgentManager.countAgents(GamePanel.gp.getPlayerEntity().getRoom()) < 5 /**&& GameWindow.gw.getWorldType(GamePanel.gp.getPlayerEntity().getRoom()) == WorldType.BASE*/) {
					long timeNow = System.currentTimeMillis();
					if (timeNow - lastAgentCreation > delayBetweenAgentCreation) {
//						System.out.println("Create Agent");
						lastAgentCreation = timeNow;
						Random rand = new Random();
						float randValue = (float) ((rand.nextFloat()*2*Math.PI));//(float) ((rand.nextFloat()*Math.PI/2)+ Math.PI/4);
						float plusMinusX = (float) Math.sin(randValue);
						float plusMinusY = (float) Math.cos(randValue);
						float randomShiftX = plusMinusX * 1000;// + rand.nextFloat()*500;
						float randomShiftY = plusMinusY * 1000;// + rand.nextFloat()*500;
						BigInteger agentID = AgentManager.getMaxID().add(BigInteger.valueOf(1));
						float x = GamePanel.gp.getPlayerEntity().getX() + randomShiftX;
						float y = GamePanel.gp.getPlayerEntity().getY() + randomShiftY;
						/* randomly variate the agent-types */
						TileSet tileSetAggressiveAgent;
						AggressiveAgent aggressiveAgent;
						float diceChooseAggressiveAgent = rand.nextFloat()-1.0f;
						if (diceChooseAggressiveAgent > 0.99) {
							// Big
							tileSetAggressiveAgent = TileSetManager.manager.getTileSet(GameStates.nameDarkWolfBig);
							aggressiveAgent = new AggressiveAgent(tileSetAggressiveAgent, "Dark Wolf",GamePanel.gp.getPlayerEntity().getRoom(), agentID , x, y, GameStates.animationDelay, false);
						} else if (diceChooseAggressiveAgent > 0.98) {
							// Big
							tileSetAggressiveAgent = TileSetManager.manager.getTileSet(GameStates.nameGreyWolfBig);
							aggressiveAgent = new AggressiveAgent(tileSetAggressiveAgent, "Dark Wolf",GamePanel.gp.getPlayerEntity().getRoom(), agentID , x, y, GameStates.animationDelay, false);
						} else if (diceChooseAggressiveAgent > 0.95) {
							// Big
							tileSetAggressiveAgent = TileSetManager.manager.getTileSet(GameStates.nameWolfBig);
							aggressiveAgent = new AggressiveAgent(tileSetAggressiveAgent, "Dark Wolf",GamePanel.gp.getPlayerEntity().getRoom(), agentID , x, y, GameStates.animationDelay, false);
						} else if (diceChooseAggressiveAgent > 0.90) {
							// DarkWolf
							tileSetAggressiveAgent = TileSetManager.manager.getTileSet(GameStates.nameDarkWolf);
							aggressiveAgent = new AggressiveAgent(tileSetAggressiveAgent, "Dark Wolf",GamePanel.gp.getPlayerEntity().getRoom(), agentID , x, y, GameStates.animationDelay, false);
						} else if (diceChooseAggressiveAgent > 0.8) {
							// GreyWolf
							tileSetAggressiveAgent = TileSetManager.manager.getTileSet(GameStates.nameGreyWolf);
							aggressiveAgent = new AggressiveAgent(tileSetAggressiveAgent, "Greywolf",GamePanel.gp.getPlayerEntity().getRoom(), agentID , x, y, GameStates.animationDelay, false);
						} else if (diceChooseAggressiveAgent > 0.5) {
							// Rabbit
							tileSetAggressiveAgent = TileSetManager.manager.getTileSet(GameStates.nameRabbit);
							aggressiveAgent = new AggressiveAgent(tileSetAggressiveAgent, "Bunny",GamePanel.gp.getPlayerEntity().getRoom(), agentID , x, y, 80, true);
						} else {
							// normal brownish wolf
							tileSetAggressiveAgent = TileSetManager.manager.getTileSet(GameStates.nameWolf);
							aggressiveAgent = new AggressiveAgent(tileSetAggressiveAgent, "Wolf",GamePanel.gp.getPlayerEntity().getRoom(), agentID , x, y, GameStates.animationDelay, false);
						}
						
//						aggressiveAgent.setGroupName(aggressiveAgent.getName());
//						aggressiveAgent.getAttack().setGroupName(aggressiveAgent.getGroupName());
//						aggressiveAgent.getAttack().setExclusive(true);
						
						
//						Goal aggressiveAgentGoal = new Goal(GamePanel.gp.getPlayerEntity());
//						aggressiveAgentGoal.setAttackModus(true);
//						aggressiveAgent.setActualGoal(aggressiveAgentGoal);
//						Goal zeroGoal = new Goal(new WorldPosition(0, 0));
//						aggressiveAgent.setActualGoal(zeroGoal);
//						aggressiveAgent.addGoal(zeroGoal);
						
						aggressiveAgent.getAttack().setCreator(aggressiveAgent.getAttack().getCreator()+";player;"+GamePanel.gp.getPlayerEntity().getName()+";"+GamePanel.gp.getPlayerEntity().getId().toString());
						AgentManager.add(aggressiveAgent);
					}
				}
				
				
			}
			
			try {
				Thread.sleep(sleepingTime);
			}
			catch(InterruptedException exception) {
				System.out.println("Error: ");
				System.out.println(exception);
			}
		}
	}

	private void runAgents() {
		Agent agent = AgentManager.runNextAgent(GameWindow.gw.getObjectMapManagers().get("tree1"), GameWindow.gw.getObjectMapManagers().get("tree2"));

		/** inform the viewer, that his creature just died */
		if (AgentManager.messages.size() > 0) {
			ArrayList<String> tempMessages = new ArrayList<String>(AgentManager.messages);
			for (String message : tempMessages) {
				GameWindow.gw.getTwitchBot().sendSafeMessage(message);
//				TextEffectManager.add(new TextEffect(message));
			}
			AgentManager.messages = new ArrayList<String>();
			
		}
		
		
		
		if (agent != null) {
			if (agent instanceof AggressiveAgent) {
				/** Attack */
				if (((AggressiveAgent) agent).isAttacking()) {
					GameWindow.gw.sound.addStartingSoundEffect(SoundEffect.WOLF_ATTACK);
					((AggressiveAgent) agent).setAttacking(false);
					((AggressiveAgent) agent).getAttack().setId(AreaInfluenceManager.getMaxIDValue().add(GamePanel.gp.getPlayerEntity().getId()));
					GameWindow.gw.send(ClientMessages.addAreaInfluence(((AggressiveAgent) agent).getAttack()));
//					GameWindow.gw.gameInfoConsole.appendInfo("Agent "+agent.getName()+" Attacked with Life="+((AggressiveAgent) agent).getAttack().getAttributes()[AttributeType.LIFE.ordinal()]);
				}
				
				/** check if the goal is too far away, then go to the next goal */
				if (agent.getActualGoal() != null) {
					if (agent.getActualGoal().getEntity() != null) {
						if (agent.getActualGoal().getEntity() instanceof PlayerEntity) {
							if (agent.getDistanceCenter(agent.getActualGoal().getEntity().getX(), agent.getActualGoal().getEntity().getY()) > GameStates.agentEntityVisionDistance || (!agent.getActualGoal().getEntity().getRoom().equals(agent.getRoom()) && !((PlayerEntity) agent.getActualGoal().getEntity()).getGroupName().equals(agent.getGroupName()))){		
								agent.nextGoal();
							} else {
								PlayerEntity goalPlayerEntity = (PlayerEntity) agent.getActualGoal().getEntity();
								if (goalPlayerEntity.getAttributes()[AttributeType.LIFE.ordinal()]<= 0) {
									agent.nextGoal();
								}
							}
						} else {
							if (agent.getDistanceCenter(agent.getActualGoal().getEntity().getX(), agent.getActualGoal().getEntity().getY()) > GameStates.agentEntityVisionDistance || !agent.getActualGoal().getEntity().getRoom().equals(agent.getRoom())){		
								agent.nextGoal();
							}
						}
						
//						if (agent.getDistanceCenter(agent.getActualGoal().getEntity().getX(), agent.getActualGoal().getEntity().getY()) > GameStates.agentEntityVisionDistance || !agent.getActualGoal().getEntity().getRoom().equals(agent.getRoom())){		
//							agent.nextGoal();
//						} else if (agent.getActualGoal().getEntity() instanceof PlayerEntity) {
//							PlayerEntity goalPlayerEntity = (PlayerEntity) agent.getActualGoal().getEntity();
//							if (goalPlayerEntity.getAttributes()[AttributeType.LIFE.ordinal()]<= 0) {
//								agent.nextGoal();
//							}
//							
//						}
					}
				}
				
				
				/** Run Away From Fire! 
				 ** Agents, that are afraid of fire, run away if they are too close */
				if (!agent.getGroupName().equals(GamePanel.gp.getPlayerEntity().getGroupName())) {//(agent.getName().equals(GameStates.nameWolf) ||  agent.getName().equals(GameStates.nameGreyWolf)) {
					if (agent.getActualGoal() != null) {
						if (agent.getActualGoal().getEntity() != null) {
							Point agentPos = new Point ((int) agent.getX(), (int) agent.getY());
							BigInteger closestLightID = LightManager.whichLightIsClosestExcept(agentPos.x, agentPos.y, GamePanel.gp.getPlayerLight().getId());
							if (closestLightID != null ) {
								if (LightManager.get(closestLightID) != null) {
									double distToLight = MyMath.getDistance(LightManager.get(closestLightID).getLocation(), agentPos);
									if (distToLight < GameStates.agentAfraidOfLightDistance) {
										// run away!!!
										
										double directionX = ((float)(agent.getX() - LightManager.get(closestLightID).getLocation().x)/distToLight);
										double directionY = ((float)(agent.getY() - LightManager.get(closestLightID).getLocation().y)/distToLight);
										int goalX = (int) (agent.getX()+GameStates.agentRunAwayFromFire*directionX);
										int goalY = (int) (agent.getY()+GameStates.agentRunAwayFromFire*directionY);
										
				//						agent.addMessage("ayasaya");
										
										
										Goal runGoal = new Goal(new WorldPosition(goalX, goalY));
										agent.setGoalList(new ArrayList<Goal>());
			//							agent.nextGoal();
										agent.setActualGoal(runGoal);
										if (!agent.getGoalList().contains(runGoal))
											agent.addGoal(runGoal);
										((AggressiveAgent) agent).restartPathCalculation();
			//							agent.nextGoal();
										
									}
								}
							}
						}
					}
				}
				
				if (!agent.hasGoal()) {
					int countDifferentGroups = Groups.values().length;
					int randomValue = Math.abs(MyMath.random.nextInt()) % countDifferentGroups;
					Groups group = Groups.values()[randomValue];
					
					/** search a new goal */
					float candidateDistance = Float.MAX_VALUE;
					PlayerEntity goalEntity = null;
					float agentDistanceFromPlayer = agent.getDistance(GamePanel.gp.getPlayerEntity().getX(), GamePanel.gp.getPlayerEntity().getY());
					
					if (group == Groups.OTHERPLAYERSAGENTS && PlayerAgentManagerManager.playerAgentManagerList.values().size() <= 0) {
						group = Groups.PLAYERAGENTS;
					}
					if (group == Groups.OTHERPLAYERS && PlayerManager.size() <= 0) {
						group = Groups.PLAYERAGENTS;
					}
					
					switch (group) {
					case PLAYER : {
						if (!GamePanel.gp.getPlayerEntity().getGroupName().equals(agent.getGroupName()) && agentDistanceFromPlayer < GameStates.agentEntityVisionDistance && GamePanel.gp.getPlayerEntity().getRoom().equals(agent.getRoom())) {
							if (agentDistanceFromPlayer < candidateDistance)  {
								candidateDistance = agentDistanceFromPlayer;
								goalEntity = GamePanel.gp.getPlayerEntity();
							}
						} 
						/** if we found a suitable enemy-entity, set it as an attack goal */
						if (goalEntity != null) {
							Goal aggressiveAgentGoal = new Goal(goalEntity);
							aggressiveAgentGoal.setAttackModus(true);
							agent.setActualGoal(aggressiveAgentGoal);
						}
						break;
					}
					case PLAYERAGENTS : {
						ArrayList<PlayerEntity> playerList = new ArrayList<PlayerEntity>(AgentManager.agentList.values());
						int[] indexList = MyUtil.getRandomPermutation(playerList.size());
						for (int index : indexList){
							PlayerEntity player = playerList.get(index);
							agentDistanceFromPlayer = agent.getDistance(player.getX(), player.getY());
							if (!player.getGroupName().equals(agent.getGroupName()) && agentDistanceFromPlayer < GameStates.agentEntityVisionDistance && player.getRoom().equals(agent.getRoom())) {
								if (agentDistanceFromPlayer < candidateDistance)  {
									candidateDistance = agentDistanceFromPlayer;
									goalEntity = player;
								}
							}
						}
						/** if we found a suitable enemy-entity, set it as an attack goal */
						if (goalEntity != null) {
							Goal aggressiveAgentGoal = new Goal(goalEntity);
							aggressiveAgentGoal.setAttackModus(true);
							agent.setActualGoal(aggressiveAgentGoal);
						}
						break;
					}
					case OTHERPLAYERS : {
						ArrayList<PlayerEntity> playerList = new ArrayList<PlayerEntity>(PlayerManager.playerList.values());
						int[] indexList = MyUtil.getRandomPermutation(playerList.size());
						for (int index : indexList){
							PlayerEntity player = playerList.get(index);
							agentDistanceFromPlayer = agent.getDistance(player.getX(), player.getY());
							if (!player.getGroupName().equals(agent.getGroupName()) && agentDistanceFromPlayer < GameStates.agentEntityVisionDistance && player.getRoom().equals(agent.getRoom())) {
								if (agentDistanceFromPlayer < candidateDistance)  {
									candidateDistance = agentDistanceFromPlayer;
									goalEntity = player;
								}
							}
						}
						/** if we found a suitable enemy-entity, set it as an attack goal */
						if (goalEntity != null) {
							Goal aggressiveAgentGoal = new Goal(goalEntity);
							aggressiveAgentGoal.setAttackModus(true);
							agent.setActualGoal(aggressiveAgentGoal);
						}
						break;
					}
					case OTHERPLAYERSAGENTS : {
						ArrayList<PlayerAgentManager> playerAgentManagerList = new ArrayList<PlayerAgentManager>(PlayerAgentManagerManager.playerAgentManagerList.values());
//						for (PlayerAgentManager playerAgentManager : playerAgentManagerList) {
						int[] indexListAgentManager = MyUtil.getRandomPermutation(playerAgentManagerList.size());
						for (int indexAgentManager : indexListAgentManager){
							PlayerAgentManager playerAgentManager = playerAgentManagerList.get(indexAgentManager);
							ArrayList<PlayerEntity> playerList = new ArrayList<PlayerEntity>(playerAgentManager.getAgentList().values());
							int[] indexList = MyUtil.getRandomPermutation(playerList.size());
							for (int index : indexList){
								PlayerEntity player = playerList.get(index);
								agentDistanceFromPlayer = agent.getDistance(player.getX(), player.getY());
								if (!player.getGroupName().equals(agent.getGroupName()) && agentDistanceFromPlayer < GameStates.agentEntityVisionDistance && player.getRoom().equals(agent.getRoom())) {
									if (agentDistanceFromPlayer < candidateDistance)  {
										candidateDistance = agentDistanceFromPlayer;
										goalEntity = player;
									}
								}
							}
						}
						/** if we found a suitable enemy-entity, set it as an attack goal */
						if (goalEntity != null) {
							Goal aggressiveAgentGoal = new Goal(goalEntity);
							aggressiveAgentGoal.setAttackModus(true);
							agent.setActualGoal(aggressiveAgentGoal);
						}
						break;
					}
					default:  {
						break;
					}
					
					}
					
					/** if the goal entity is still null, search one of the following */
					if (goalEntity == null) {
						if (!GamePanel.gp.getPlayerEntity().getGroupName().equals(agent.getGroupName()) && agentDistanceFromPlayer < GameStates.agentEntityVisionDistance && GamePanel.gp.getPlayerEntity().getRoom().equals(agent.getRoom())) {
							if (agentDistanceFromPlayer < candidateDistance)  {
								candidateDistance = agentDistanceFromPlayer;
								goalEntity = GamePanel.gp.getPlayerEntity();
							}
						} 
						/** if we found a suitable enemy-entity, set it as an attack goal */
						if (goalEntity != null) {
							Goal aggressiveAgentGoal = new Goal(goalEntity);
							aggressiveAgentGoal.setAttackModus(true);
							agent.setActualGoal(aggressiveAgentGoal);
						}
						
						if (goalEntity == null) {
							ArrayList<PlayerEntity> playerList = new ArrayList<PlayerEntity>(PlayerManager.playerList.values());
							int[] indexList = MyUtil.getRandomPermutation(playerList.size());
							for (int index : indexList){
								PlayerEntity player = playerList.get(index);
								agentDistanceFromPlayer = agent.getDistance(player.getX(), player.getY());
								if (!player.getGroupName().equals(agent.getGroupName()) && agentDistanceFromPlayer < GameStates.agentEntityVisionDistance && player.getRoom().equals(agent.getRoom())) {
									if (agentDistanceFromPlayer < candidateDistance)  {
										candidateDistance = agentDistanceFromPlayer;
										goalEntity = player;
									}
								}
							}
							/** if we found a suitable enemy-entity, set it as an attack goal */
							if (goalEntity != null) {
								Goal aggressiveAgentGoal = new Goal(goalEntity);
								aggressiveAgentGoal.setAttackModus(true);
								agent.setActualGoal(aggressiveAgentGoal);
							}
							
						}
						
						if (goalEntity == null) {
							ArrayList<PlayerAgentManager> playerAgentManagerList = new ArrayList<PlayerAgentManager>(PlayerAgentManagerManager.playerAgentManagerList.values());
							int[] indexListAgentManager = MyUtil.getRandomPermutation(playerAgentManagerList.size());
							for (int indexAgentManager : indexListAgentManager){
								PlayerAgentManager playerAgentManager = playerAgentManagerList.get(indexAgentManager);
								ArrayList<PlayerEntity> playerList = new ArrayList<PlayerEntity>(playerAgentManager.getAgentList().values());
								int[] indexList = MyUtil.getRandomPermutation(playerList.size());
								for (int index : indexList){
									PlayerEntity player = playerList.get(index);
									agentDistanceFromPlayer = agent.getDistance(player.getX(), player.getY());
									if (!player.getGroupName().equals(agent.getGroupName()) && agentDistanceFromPlayer < GameStates.agentEntityVisionDistance && player.getRoom().equals(agent.getRoom())) {
										if (agentDistanceFromPlayer < candidateDistance)  {
											candidateDistance = agentDistanceFromPlayer;
											goalEntity = player;
										}
									}
								}
							}
							/** if we found a suitable enemy-entity, set it as an attack goal */
							if (goalEntity != null) {
								Goal aggressiveAgentGoal = new Goal(goalEntity);
								aggressiveAgentGoal.setAttackModus(true);
								agent.setActualGoal(aggressiveAgentGoal);
							}
						}
						
						
						if (goalEntity == null) {
							ArrayList<PlayerEntity> playerList = new ArrayList<PlayerEntity>(AgentManager.agentList.values());
							int[] indexList = MyUtil.getRandomPermutation(playerList.size());
							for (int index : indexList){
								PlayerEntity player = playerList.get(index);
								agentDistanceFromPlayer = agent.getDistance(player.getX(), player.getY());
								if (!player.getGroupName().equals(agent.getGroupName()) && agentDistanceFromPlayer < GameStates.agentEntityVisionDistance && player.getRoom().equals(agent.getRoom())) {
									if (agentDistanceFromPlayer < candidateDistance)  {
										candidateDistance = agentDistanceFromPlayer;
										goalEntity = player;
									}
								}
							}
							/** if we found a suitable enemy-entity, set it as an attack goal */
							if (goalEntity != null) {
								Goal aggressiveAgentGoal = new Goal(goalEntity);
								aggressiveAgentGoal.setAttackModus(true);
								agent.setActualGoal(aggressiveAgentGoal);
							}
						}
					} // end of goal entity == null
					
				}
				
				/** if there is still no enemy, go to a friend */
				if (!agent.hasGoal()) {
					/** search a new goal */
					float candidateDistance = Float.MAX_VALUE;
					PlayerEntity goalEntity = null;
					float agentDistanceFromPlayer = agent.getDistance(GamePanel.gp.getPlayerEntity().getX(), GamePanel.gp.getPlayerEntity().getY());
					if (GamePanel.gp.getPlayerEntity().getGroupName().equals(agent.getGroupName()) && agentDistanceFromPlayer < GameStates.agentEntityVisionDistance && GamePanel.gp.getPlayerEntity().getRoom().equals(agent.getRoom())) {
						if (agentDistanceFromPlayer < candidateDistance)  {
							candidateDistance = agentDistanceFromPlayer;
							goalEntity = GamePanel.gp.getPlayerEntity();
						}
					} 
					/** if we found a suitable enemy-entity, set it as an attack goal */
					if (goalEntity != null) {
						Goal friendlyGoal = new Goal(goalEntity);
						friendlyGoal.setAttackModus(false);
						agent.setActualGoal(friendlyGoal);
					}
					
					if (goalEntity == null) {
						for (PlayerEntity player : PlayerManager.playerList.values()) {
							agentDistanceFromPlayer = agent.getDistance(player.getX(), player.getY());
							if (player.getGroupName().equals(agent.getGroupName()) && agentDistanceFromPlayer < GameStates.agentEntityVisionDistance && player.getRoom().equals(agent.getRoom())) {
								if (agentDistanceFromPlayer < candidateDistance)  {
									candidateDistance = agentDistanceFromPlayer;
									goalEntity = player;
								}
							}
						}
						/** if we found a suitable enemy-entity, set it as an attack goal */
						if (goalEntity != null) {
							Goal friendlyGoal = new Goal(goalEntity);
							friendlyGoal.setAttackModus(false);
							agent.setActualGoal(friendlyGoal);
						}
					}
					
					if (goalEntity == null) {
						for (PlayerAgentManager playerAgentManager : PlayerAgentManagerManager.playerAgentManagerList.values()) {
							for (PlayerEntity player : playerAgentManager.getAgentList().values()) {
								agentDistanceFromPlayer = agent.getDistance(player.getX(), player.getY());
								if (player.getGroupName().equals(agent.getGroupName()) && agentDistanceFromPlayer < GameStates.agentEntityVisionDistance && player.getRoom().equals(agent.getRoom())) {
									if (agentDistanceFromPlayer < candidateDistance)  {
										candidateDistance = agentDistanceFromPlayer;
										goalEntity = player;
									}
								}
							}
						}
						/** if we found a suitable enemy-entity, set it as an attack goal */
						if (goalEntity != null) {
							Goal friendlyGoal = new Goal(goalEntity);
							friendlyGoal.setAttackModus(false);
							agent.setActualGoal(friendlyGoal);
						}
					}
					
					if (goalEntity == null) {
						for (PlayerEntity player : AgentManager.agentList.values() ){
							/** skip the agent itself */
							if (player.getId().compareTo(agent.getId())== 0) continue;
							agentDistanceFromPlayer = agent.getDistance(player.getX(), player.getY());
							if (player.getGroupName().equals(agent.getGroupName()) && agentDistanceFromPlayer < GameStates.agentEntityVisionDistance && player.getRoom().equals(agent.getRoom())) {
								if (agentDistanceFromPlayer < candidateDistance)  {
									candidateDistance = agentDistanceFromPlayer;
									goalEntity = player;
								}
							}
						}
						/** if we found a suitable enemy-entity, set it as an attack goal */
						if (goalEntity != null) {
							Goal friendlyGoal = new Goal(goalEntity);
							friendlyGoal.setAttackModus(false);
							agent.setActualGoal(friendlyGoal);
						}
					}
					
				}
				
			}
			
			/** delete agents, that are far away or in another room */
//			if (agent.getDistance(GamePanel.gp.getPlayerEntity().getX(), GamePanel.gp.getPlayerEntity().getY()) > GameStates.agentDeleteDistance  || (!agent.getRoom().equals(GamePanel.gp.getPlayerEntity().getRoom()) && !agent.getGroupName().equals(GamePanel.gp.getPlayerEntity().getGroupName()) )) {
//				agent.getAttributes()[AttributeType.LIFE.ordinal()]= -1;
//				AgentManager.remove(agent.getId());
//			}
		} else {
			// agent is null
		}
		
		/** drops */
		if (AgentManager.getDropList().size() > 0) {
			BigInteger itemId;
			for (WorldPosition worldPosition : AgentManager.getDropList().keySet()) {
				itemId  = ItemManager.getMaxIDValue().add(GamePanel.gp.getPlayerEntity().getId());
				ItemManager.setMaxIDValue(itemId);
				Item dropItem = ItemFactoryCollection.createRandomItem(AgentManager.getDropList().get(worldPosition), itemId, worldPosition.getRoom(), worldPosition.getX()+(int) (Math.random()*(GameStates.mapTileSetWidth-GameStates.itemTileWidth)), worldPosition.getY()+(int) (Math.random()*(GameStates.mapTileSetHeight-GameStates.itemTileHeight)));
				
//				System.out.println("added new wood object: "+itemId);
				
				/** send the complete Item to all players of the channel */
				if (dropItem != null && GameWindow.gw.isLoggedIn() && GamePanel.gp.isInitializedPlayer()) {
					/** first send to server for the itemList */
					GameWindow.gw.send(ClientMessages.addItem(dropItem.getId(), dropItem.getItemCode(), dropItem.getCount(), dropItem.getCapacity(), dropItem.getEntity().getX(), dropItem.getEntity().getY(), dropItem.getEntity().getMX(), dropItem.getEntity().getMY(), dropItem.getName(), dropItem.getComment(), dropItem.getRoom(), dropItem.getEntity().getTileSet().getFileName(), dropItem.getEntity().getName(), dropItem.getAttributes()));
					for (String channelName : GameWindow.gw.getSpaceChannels().values()) {
						ClientChannel channel = GameWindow.gw.getChannelByName(channelName);
						try {
							channel.send(ClientMessages.addCompleteItem(dropItem.getItemCode(), dropItem.getId(), dropItem.getName(), dropItem.getComment(), dropItem.getRoom(), dropItem.getEntity().getX(), dropItem.getEntity().getY(), dropItem.getCount(), dropItem.getAttributes()));
						} catch (IOException e) {
							e.printStackTrace();
						}	
					}
				}
			}
			AgentManager.clearDropList();
		}
		
		
	}

}
