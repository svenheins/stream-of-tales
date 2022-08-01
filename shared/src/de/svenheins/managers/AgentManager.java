package de.svenheins.managers;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import de.svenheins.main.EntityStates;
import de.svenheins.main.GameStates;
import de.svenheins.objects.LocalObject;
import de.svenheins.objects.PlayerEntity;
import de.svenheins.objects.TileSet;
import de.svenheins.objects.ZIndexObjectComparator;
import de.svenheins.objects.agents.Agent;

public class AgentManager {
	public static HashMap<BigInteger, Agent> agentList = new HashMap<BigInteger, Agent>();
//	public static Comparator<LocalObject> comparator = new ZIndexObjectComparator();
	public static List<BigInteger> idList = new ArrayList<BigInteger>();
	
	public static Iterator<BigInteger> updateAgentList = agentList.keySet().iterator();
	
	public static void remove(BigInteger index) throws IllegalArgumentException {
		try {
			agentList.remove(index);
			idList.remove(index);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
		
	}
	
	public static boolean add(Agent agent) {
		if (agentList.containsKey(agent.getId())) {
			return false;
		} else {
			agentList.put(agent.getId(), agent);
			idList.add(agent.getId());
			return true;
		}
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
	public static void runNextAgent() {
		Agent agent = null;
		BigInteger agentID = null;
		if (updateAgentList.hasNext()) {
			agentID = updateAgentList.next();
			agent = agentList.get(agentID);
		} else {
			updateAgentList = agentList.keySet().iterator();
			if (updateAgentList.hasNext()) {
				agentID = updateAgentList.next();
				agent = agentList.get(agentID);
			} else {
				// empty agentList
			}
		}
		
		if (agent != null) {
			agent.run();
			AgentManager.agentList.put(agentID, agent);
		}
	}

	/** update the Entity with the ID objectId */
	public static void updateAgent(BigInteger objectId, float objectX,
			float objectY, float objectMX, float objectMY) {
		Agent agent = agentList.get(objectId);
		if (agent != null) {
			agent.setX(objectX);
			agent.setY(objectY);
			agent.setMovement(objectMX, objectMY);
			AgentManager.agentList.put(objectId, agent);
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
