package de.svenheins.managers;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.svenheins.main.GameStates;
import de.svenheins.objects.Entity;
import de.svenheins.objects.PlayerEntity;
import de.svenheins.objects.TileSet;


public class PlayerManager{
	public static HashMap<BigInteger, PlayerEntity> playerList = new HashMap<BigInteger, PlayerEntity>();
	public static List<BigInteger> idList = new ArrayList<BigInteger>();
//	public static HashMap<BigInteger, long > playerLastSeen = new HashMap<BigInteger, long>();
	
	
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
	
	public static boolean add(PlayerEntity entity){
		if (playerList.containsKey(entity.getId()) ) {
//			if (playerList.containsKey(entity.getId()) || entity.getId().signum()==-1 ) {	
			return false;
		} else {
			entity.setLastSeen(System.currentTimeMillis());
			playerList.put(entity.getId(), entity);
			idList.add(entity.getId());
//			System.out.println("PlayerManager.add ID: "+entity.getId());
			
			return true;
		}
	}
	
	public static PlayerEntity get(BigInteger index){
		try {
			return (PlayerEntity) playerList.get(index);
//			return (Entity) entityList.values().toArray()[index];
		}
		catch(IndexOutOfBoundsException e){
			//System.out.println("Null-Object returned");
			return null;
		}
	}
	
	public static PlayerEntity get(String playerName){
		PlayerEntity retEntity = null;
		for (BigInteger pId: playerList.keySet()) {
			if (playerList.get(pId).getName().equals(playerName)) retEntity = playerList.get(pId);
		}
		return retEntity;
	}
	
	/** update the Entity with the ID objectId */
	public static void updatePlayer(BigInteger objectId, float objectX,
			float objectY, float objectMX, float objectMY) {
		if (PlayerManager.playerList.containsKey(objectId)) {
			PlayerEntity entity = PlayerManager.playerList.get(objectId);
			entity.setLastSeen(System.currentTimeMillis());
			if (entity != null) {
				entity.setX(objectX);
				entity.setY(objectY);
				entity.setMovement(objectMX, objectMY);
				PlayerManager.playerList.put(objectId, entity);
			}
		} 
//		else
//		{
//			PlayerManager.add(new Entity("ship.png", objectId, objectX, objectY, objectMX, objectMY));
//		}
	}
	
	public static boolean overwrite(PlayerEntity entity){
		if (!playerList.containsKey(entity.getId())) {
			/** do nothing if key is not yet set*/
			return false;
		} else {
			entity.setLastSeen(System.currentTimeMillis());
			playerList.put(entity.getId(), entity);
			idList.add(entity.getId());
			return true;
		}
	}
	
	/** update the Entity with the ID objectId */
	public static void updatePlayerAddons(BigInteger objectId, String playerName, String tileName, String tilePathName, int width, int height, String country, String groupName, int experience) {
		if (PlayerManager.playerList.containsKey(objectId)) {
			PlayerEntity entity = PlayerManager.playerList.get(objectId);
			entity.setLastSeen(System.currentTimeMillis());
			if (entity != null) {
				System.out.println("got tileset: "+tilePathName + " from player "+ playerName);
				TileSet tileSet = TileSetManager.manager.getTileSet(tilePathName);
		    	
		    	PlayerEntity playerEntity = new PlayerEntity(tileSet, playerName, objectId, 0, 0, GameStates.animationDelay);
//		    	playerEntity.setTileSetName(tileName_add);
//		    	playerEntity.setTileSetPathName(tilePathName);
		    	playerEntity.setWidth(width);
		    	playerEntity.setHeight(height);
		    	playerEntity.setCountry(country);
		    	playerEntity.setGroupName(groupName);
		    	playerEntity.setExperience(experience);
//		    	PlayerManager.overwrite(playerEntity_overwrite);
				
		    	System.out.println("updated player");
				
				PlayerManager.playerList.put(objectId, playerEntity);
			}
		} 
		else {
			System.out.println("didnt find it!");
		}
//		else
//		{
//			PlayerManager.add(new Entity("ship.png", objectId, objectX, objectY, objectMX, objectMY));
//		}
	}
	
	public static int size(){
		return playerList.size();
	}
	
	public static boolean contains(PlayerEntity entity) {
		return playerList.containsValue(entity);
	}
	
	
	/** compare the idList with a given int[] and return the difference */
	public static void createPlayersFromArray(PlayerEntity[] entityArray) {
		if(entityArray.length > 0) {
			for (int i =0; i< entityArray.length; i++) {
				if (playerList.containsKey(entityArray[i].getId()) ) System.out.println("Doppelte ID: " + entityArray[i].getId());
				entityArray[i].setLastSeen(System.currentTimeMillis());
				playerList.put(entityArray[i].getId(), entityArray[i]);
				idList.add(entityArray[i].getId());
				System.out.println("IDs: " + entityArray[i].getId());
			}
		}
	}
	
	public static void emptyAll() {
		playerList = new HashMap<BigInteger, PlayerEntity>();
//		sortedEntityList = new ArrayList<Entity>();
		idList = new ArrayList<BigInteger>();
	}
}
