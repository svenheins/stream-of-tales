package de.svenheins.managers;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.svenheins.objects.Entity;


public class PlayerManager{
	public static HashMap<BigInteger, Entity> playerList = new HashMap<BigInteger, Entity>();
	
	public static List<BigInteger> idList = new ArrayList<BigInteger>();
	
	
	public static boolean remove(BigInteger index) {
		if (playerList.containsKey(index)) {
			playerList.remove(index);
			idList.remove(index);
			return true;
		} else {
			return false;
		}
	}
	
//	public static void remove(Entity entity) {
//		playerList.remove(entity);
//	}
	
	public static boolean add(Entity entity){
		if (playerList.containsKey(entity.getId())) {
			return false;
		} else {
			playerList.put(entity.getId(), entity);
			idList.add(entity.getId());
			return true;
		}
	}
	
	public static Entity get(BigInteger index){
		try {
			return (Entity) playerList.get(index);
//			return (Entity) entityList.values().toArray()[index];
		}
		catch(IndexOutOfBoundsException e){
			//System.out.println("Null-Object returned");
			return null;
		}
	}
	
	/** update the Entity with the ID objectId */
	public static void updatePlayer(BigInteger objectId, float objectX,
			float objectY, float objectMX, float objectMY) {
		if (PlayerManager.playerList.containsKey(objectId)) {
			Entity entity = PlayerManager.playerList.get(objectId);
			if (entity != null) {
				entity.setX(objectX);
				entity.setY(objectY);
				entity.setMovement(objectMX, objectMY);
				PlayerManager.playerList.put(objectId, entity);
			}
		} else
		{
			PlayerManager.add(new Entity("ship.png", objectId, objectX, objectY, objectMX, objectMY));
		}
	}
	
	public static boolean overwrite(Entity entity){
		if (!playerList.containsKey(entity.getId())) {
			/** do nothing if key is not yet set*/
			return false;
		} else {
			playerList.put(entity.getId(), entity);
			idList.add(entity.getId());
			return true;
		}
	}
	
	public static int size(){
		return playerList.size();
	}
	
	public static boolean contains(Entity entity) {
		return playerList.containsValue(entity);
	}
	
	
	/** compare the idList with a given int[] and return the difference */
	public static void createPlayersFromArray(Entity[] entityArray) {
		if(entityArray.length > 0) {
			for (int i =0; i< entityArray.length; i++) {
				if (playerList.containsKey(entityArray[i].getId()) ) System.out.println("Doppelte ID: " + entityArray[i].getId());
				playerList.put(entityArray[i].getId(), entityArray[i]);
				idList.add(entityArray[i].getId());
			}
		}
	}
	
	public static void emptyAll() {
		playerList = new HashMap<BigInteger, Entity>();
//		sortedEntityList = new ArrayList<Entity>();
		idList = new ArrayList<BigInteger>();
	}
}
