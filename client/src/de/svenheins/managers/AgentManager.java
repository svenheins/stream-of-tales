/* Copyright (C) Sven Heins - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * author: Sven Heins
 * date: 05.07.2015
 */
package de.svenheins.managers;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import de.svenheins.main.AttributeType;
import de.svenheins.main.EntityStates;
import de.svenheins.main.GameStates;
import de.svenheins.objects.Entity;
import de.svenheins.objects.TileSet;
import de.svenheins.objects.WorldPosition;
import de.svenheins.objects.agents.Agent;
import de.svenheins.objects.agents.AggressiveAgent;

public class AgentManager {
	public static HashMap<BigInteger, Agent> agentList = new HashMap<BigInteger, Agent>();
//	public static Comparator<LocalObject> comparator = new ZIndexObjectComparator();
	public static List<BigInteger> idList = new ArrayList<BigInteger>();
	public static Agent actualAgent;
	private static HashMap<WorldPosition, Integer> dropList = new HashMap<WorldPosition, Integer>(); 
	public static Iterator<BigInteger> updateAgentList;// = agentList.keySet().iterator();
	public static HashMap<String, BigInteger> ircUserAgentList = new HashMap<String, BigInteger>();
	public static ArrayList<String> messages = new ArrayList<String>();
	
	public static void remove(BigInteger index) throws IllegalArgumentException {
		try {
//			TODO: fix this: concurrent modfication
			if (ircUserAgentList.containsValue(index)) {
				ArrayList<String> deleteList = new ArrayList<String>();
				for (String ircUserName : ircUserAgentList.keySet()) {
					if (index.compareTo(ircUserAgentList.get(ircUserName)) == 0) {
						deleteList.add(ircUserName);
					}
				}
				/** now safely delete the candidates */
				for (String delUserName : deleteList) {
					ircUserAgentList.remove(delUserName);
				}
			}
			agentList.remove(index);
			idList.remove(index);
			updateAgentList = agentList.keySet().iterator();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
		SharedAgentManager.size = AgentManager.size();
	}
	
	/** get max id */
	public static BigInteger getMaxID() {
		BigInteger maxID = BigInteger.valueOf(0);
		for (BigInteger id : idList) {
			if (id.compareTo(maxID)>0) maxID = id;
		}
		return maxID;
	}
	
	/** count Agents in room */
	public static int countAgents(String room) {
		int retValue = 0;
		for (BigInteger id : idList) {
			if (agentList.get(id).getRoom().equals(room)) retValue++;
		}
		return retValue;
	}
	
	/** remove a drop from the list */
	public static void removeDrop(WorldPosition worldPosition) throws IllegalArgumentException {
		try {
			dropList.remove(worldPosition);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
		
	}
	
	public static boolean add(Agent agent) {
		if (agentList.containsKey(agent.getId())) {
			SharedAgentManager.size = AgentManager.size();
			return false;
		} else {
			agentList.put(agent.getId(), agent);
			idList.add(agent.getId());
			updateAgentList = agentList.keySet().iterator();
			SharedAgentManager.size = AgentManager.size();
			return true;
		}
		
	}
	
	/** give the agentID to the userName */
	public static void addIRCUserAgent(String userName, BigInteger agentID) {
		ircUserAgentList.put(userName, agentID);
	}
	
	public static void addDrop(WorldPosition worldPosition, Integer level) {
		dropList.put(worldPosition, level);
	}
	
	public static Agent get(BigInteger index){
		try {
			return (Agent) agentList.get(index);
		}
		catch(IndexOutOfBoundsException e){
			return null;
		}
		
	}
	
	public static boolean overwrite(Agent agent){
		if (!agentList.containsKey(agent.getId())) {
			/** do nothing if key is not yet set*/
			return false;
		} else {
			agentList.put(agent.getId(), agent);
			idList.add(agent.getId());
			updateAgentList = agentList.keySet().iterator();
			return true;
		}
	}
	
	public static int size(){
		return agentList.size();
	}
	
	public static boolean contains(Agent agent) {
		return agentList.containsValue(agent);
	}
	
	/** update the Entity with the ID objectId */
	public static Agent runNextAgent(ObjectMapManager objectMapManager1, ObjectMapManager objectMapManager2) {
		Agent agent = null;
		BigInteger agentID = null;
		long timeNow = System.currentTimeMillis();
		/** take the next agent, who has a valid Goal */
//		for (int i = 0; i < agentList.size(); i++) {
//		Iterator<BigInteger> tempUpdateAgentList = new Iterator<BigInteger>(updateAgentList);
		
		if (updateAgentList.hasNext()) {
			try {
				agentID = updateAgentList.next();
				agent = agentList.get(agentID);
				if (agent instanceof AggressiveAgent) {
					agent = (AggressiveAgent) agent;
				}
//				System.out.println("agent aggressive " + (agent instanceof AggressiveAgent));
			} catch (ConcurrentModificationException ex) {
				ex.printStackTrace();
			}
				if (agent.isAlive()) {
					
				} else {
					/** check if the old agent is dead */
					boolean deleteDeadAgent = false;
					BigInteger deleteAgentID = BigInteger.valueOf(0);
					if ( !agent.isAlive()) {
						deleteDeadAgent = true;
						deleteAgentID = agentID;
					}
					/** remove the old dead agent */
					if (deleteDeadAgent && timeNow - agent.getDeadSince() > GameStates.timeAfterDeathTillDeletion) {
						remove(deleteAgentID);
					}
					/** take the next agent */
					if (updateAgentList.hasNext()) {
						agentID = updateAgentList.next();
						agent = agentList.get(agentID);
					}
					
				}
			} else {
				updateAgentList = agentList.keySet().iterator();
			}
		
//		}
		
		if (agent != null) {
//			System.out.println("run agent...");
			if ( agent.getAttributes()[AttributeType.LIFE.ordinal()] > 0 && agent.isAlive() ) {
//				System.out.println("run agent from agent manager");
				if (agent instanceof AggressiveAgent) {
					((AggressiveAgent)agent).run(objectMapManager1, objectMapManager2);
				}
				else agent.run(objectMapManager1, objectMapManager2);
				
				if (agent.getMessageBoard().size() > 0) {
					if (agent.getInfoIcon() != null) {
						// OK, there is already one icon 
						agent.getInfoIcon().setX(agent.getX()+agent.getWidth()/2-agent.getInfoIcon().getWidth()/2);
						agent.getInfoIcon().setY(agent.getY()-agent.getInfoIcon().getHeight()-GameStates.agentInfoIconDistanceY);
					} else {
						TileSet infoIconTileSet = TileSetManager.manager.getTileSet(GameStates.agentInfoIconName);
						Entity infoIcon = new Entity(infoIconTileSet, agent.getName(), agent.getRoom(), agent.getId(), 0, 0, agent.getAnimation().getTimeBetweenAnimation());
						agent.setInfoIcon(infoIcon);
					}
				} else {
					agent.setInfoIcon(null);
				}
			} else {
				if (agent.isAlive()) {
					/** first determine a spawning item */
					WorldPosition worldPosition = new WorldPosition(agent.getRoom(),(int) (agent.getX() + agent.getWidth()/2) , (int) (agent.getY()+agent.getHeight()/2)) ;
					addDrop(worldPosition, new Integer(agent.getLevel()));
//					System.out.println("Drop has level: "+agent.getLevel());
					agent.setAlive(false);
					agent.setDeadSince(System.currentTimeMillis());
					agent.setContinuousState(EntityStates.DEAD);
					agent.setChangedStates(true);
					agent.setCollided(0);
					agent.setMovement(-agent.getTotalAreaInfluences()[AttributeType.MX.ordinal()], -agent.getTotalAreaInfluences()[AttributeType.MY.ordinal()]);
					agent.resetInfluences();
					
					String[] senderName = agent.getName().split(": ");
					if (senderName.length>1) {
						messages.add(""+senderName[0]+", sorry but your "+senderName[1]+" just died... BibleThump create a new one!");
					}
					
					RewardManager.sendKillExperience(GameStates.streamExperienceForKillingAgent+GameStates.streamExperienceForKillingAgentPerLevel*agent.getLevel(), agent.getDamagedBy(), agent.getName(), false);
					 
				} else {
					agent.setMovement(-agent.getTotalAreaInfluences()[AttributeType.MX.ordinal()], -agent.getTotalAreaInfluences()[AttributeType.MY.ordinal()]);
					BigInteger deleteAgentID = agentID;
					if (timeNow - agent.getDeadSince() > GameStates.timeAfterDeathTillDeletion) {
							remove(deleteAgentID);
					}
					
				}
				
				/** then remove the agent from the list */
//				remove(agentID);
			}
		} else {
//			updateAgentList = agentList.keySet().iterator();
		}

			
			
//			AgentManager.agentList.put(agentID, agent);

		return agent;
	}

	/** update the Entity with the ID objectId */
	public static void updateAgent(BigInteger objectId, float objectX,
			float objectY, float objectMX, float objectMY) {
		Agent agent = agentList.get(objectId);
		if (agent != null) {
			agent.setX(objectX);
			agent.setY(objectY);
			agent.setMovement(objectMX, objectMY);
//			AgentManager.agentList.put(objectId, agent);
		}
	}
	
//	/** update the Entity with the ID objectId */
//	public static void updateAgentAddons(BigInteger objectId, String playerName, String tileName, String tilePathName, int width, int height, String country, String groupName, int experience) {
//		if (agentList.containsKey(objectId)) {
//			Agent agent = agentList.get(objectId);
//			agent.setLastSeen(System.currentTimeMillis());
//			if (agent != null) {
//				System.out.println("got tileset: "+tilePathName + " from player "+ playerName);
//				TileSet tileSet = TileSetManager.manager.getTileSet(tilePathName);
//		    	
//		    	Agent newAgent = new Agent(tileSet, playerName, objectId, 0, 0, GameStates.animationDelay);
//		    	playerEntity.setWidth(width);
//		    	playerEntity.setHeight(height);
//		    	playerEntity.setCountry(country);
//		    	playerEntity.setGroupName(groupName);
//		    	playerEntity.setExperience(experience);				
//		    	System.out.println("updated player");
//				
//				PlayerManager.playerList.put(objectId, playerEntity);
//			}
//		} 
//		else {
//			System.out.println("didnt find it!");
//		}
//	}
//	
//	/** update the Entity with the ID objectId */
//	public static void updatePlayerState(BigInteger objectId, EntityStates orientation, EntityStates singleState, EntityStates continuousState) {
//		if (PlayerManager.playerList.containsKey(objectId)) {
//			PlayerEntity entity = PlayerManager.playerList.get(objectId);
//			entity.setLastSeen(System.currentTimeMillis());
//			if (entity != null) {
//				/** update states of entity */
//				entity.setOrientation(orientation);
//				entity.setSingleState(singleState);
//				entity.setContinuousState(continuousState);
//				entity.setChangedStates(true);
//				/** determine animation for the new states */ 
//				determineAnimation(entity);
//				/** replace the entity */
//				playerList.put(objectId, entity);
//			}
//		} 
//		else {
////			System.out.println("didnt find it!");
//		}
//	}
	
	public static void emptyAll() {
		agentList = new HashMap<BigInteger, Agent>();
		idList = new ArrayList<BigInteger>();
	}

	public static HashMap<WorldPosition, Integer> getDropList() {
		return dropList;
	}

	public static void setDropList(HashMap<WorldPosition, Integer> dropList) {
		AgentManager.dropList = dropList;
	}
	
	public static void clearDropList() {
		dropList = new HashMap<WorldPosition, Integer>();
	}

//	public BigInteger takeFirstUpdateAgent() {
//		BigInteger retID =  null;
//		if (updateAgentList.size() > 0) {
//			retID = updateAgentList.get(0);
//			updateAgentList.remove(0);
//		} 
//		return retID;
//		
//	}
//	
//	public void addUpdateAgentListEntry(BigInteger addID) {
////		System.out.println(mapFile);
//		if (!this.updateAgentList.contains(addID)) this.updateAgentList.add(addID);
//	}
}
