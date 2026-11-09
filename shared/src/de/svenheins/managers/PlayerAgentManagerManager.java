/* Copyright (C) Sven Heins - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * author: Sven Heins
 * date: 05.07.2015
 */
package de.svenheins.managers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/** manages the PlayerAgentManagers 
 * each player has a list of agents, that he is sending to the other players 
 * they */ 
public class PlayerAgentManagerManager {
	public static HashMap<String, PlayerAgentManager> playerAgentManagerList = new HashMap<String, PlayerAgentManager>();
	public static List<String> nameList = new ArrayList<String>();
	
	public static boolean remove(String name) {
		if (playerAgentManagerList.containsKey(name)) {
			playerAgentManagerList.remove(name);
			nameList.remove(name);
			return true;
		} else {
			return false;
		}
	}
	
	
	/** remova all agents from this playerManager */
	public static void empty(String name) {
		if (playerAgentManagerList.containsKey(name)) {
//			System.out.println("empty playerAgentManager: "+name);
			PlayerAgentManager playerAgentManager = new PlayerAgentManager(name);
			overwrite(name, playerAgentManager);
		} else {
			System.out.println("no PlayerAgentManager with this name: "+name);
		}
	}
	
	/** overwrite an existing playerAgentManager */
	public static boolean overwrite(String name, PlayerAgentManager playerAgentManager) {
		if (!playerAgentManagerList.containsKey(name)) {
			/** do nothing if key is not yet set*/
			return false;
		} else {
			playerAgentManagerList.put(name, playerAgentManager);
			return true;
		}
	}
	
	public static boolean add(PlayerAgentManager playerAgentManager){
		if (playerAgentManagerList.containsKey(playerAgentManager.getName()) ) {
//			if (playerList.containsKey(entity.getId()) || entity.getId().signum()==-1 ) {	
			return false;
		} else {
			playerAgentManagerList.put(playerAgentManager.getName(), playerAgentManager);
			nameList.add(playerAgentManager.getName());
			return true;
		}
	}
	
	public static PlayerAgentManager get(String name){
		try {
			return (PlayerAgentManager) playerAgentManagerList.get(name);
//			return (Entity) entityList.values().toArray()[index];
		}
		catch(IndexOutOfBoundsException e){
			//System.out.println("Null-Object returned");
			return null;
		}
	}
	
	public static int size(){
		return playerAgentManagerList.size();
	}
	
	public static boolean contains(PlayerAgentManager playerAgentManager) {
		return playerAgentManagerList.containsValue(playerAgentManager);
	}
	
	public static boolean contains(String name) {
		return playerAgentManagerList.containsKey(name);
	}
	
	public static void emptyAll() {
		playerAgentManagerList = new HashMap<String, PlayerAgentManager>();
		nameList = new ArrayList<String>();
	}
}
