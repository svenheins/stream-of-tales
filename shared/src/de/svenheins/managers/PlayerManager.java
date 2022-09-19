package de.svenheins.managers;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.svenheins.main.EntityStates;
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
		    	playerEntity.setWidth(width);
		    	playerEntity.setHeight(height);
		    	playerEntity.setCountry(country);
		    	playerEntity.setGroupName(groupName);
		    	playerEntity.setExperience(experience);	
		    	/** set to old attributes */
		    	playerEntity.setAttributes(entity.getAttributes());
		    	System.out.println("updated player");
				
				PlayerManager.playerList.put(objectId, playerEntity);
			}
		} 
		else {
			System.out.println("didnt find it!");
		}
	}
	
	/** update the Entity with the ID objectId */
	public static void updatePlayerState(BigInteger objectId, EntityStates orientation, EntityStates singleState, EntityStates continuousState) {
		if (PlayerManager.playerList.containsKey(objectId)) {
			PlayerEntity entity = PlayerManager.playerList.get(objectId);
			entity.setLastSeen(System.currentTimeMillis());
			if (entity != null) {
				/** update states of entity */
				entity.setOrientation(orientation);
				entity.setSingleState(singleState);
				entity.setContinuousState(continuousState);
				entity.setChangedStates(true);
				/** determine animation for the new states */ 
				determineAnimation(entity);
				/** replace the entity */
				playerList.put(objectId, entity);
			}
		} 
		else {
//			System.out.println("didnt find it!");
		}
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
	
	/** determine animation 
	 * priority has the singleState, which is responsible for the short singleAnimation
	 * after this one ends, the standard continuous animation is triggered
	 * */
	public static void determineAnimation(PlayerEntity entity) {
		if (entity.getSingleState() != EntityStates.EMPTY) {
			switch(entity.getSingleState()) {
			case ATTACKING:
				switch(entity.getOrientation()) {
				case LEFT:
					entity.setSingleAnimation(AnimationManager.manager.getAnimation("attacking_l", entity.getTileSet(), GameStates.ani_attacking_l_start, GameStates.ani_attacking_l_end, entity.getAnimation().getTimeBetweenAnimation()));
					entity.getSingleAnimation().start();
					break;
				case RIGHT:
					entity.setSingleAnimation(AnimationManager.manager.getAnimation("attacking_r", entity.getTileSet(), GameStates.ani_attacking_r_start, GameStates.ani_attacking_r_end, entity.getAnimation().getTimeBetweenAnimation()));
					entity.getSingleAnimation().start();
					break;
				case UP:
					entity.setSingleAnimation(AnimationManager.manager.getAnimation("attacking_u", entity.getTileSet(), GameStates.ani_attacking_u_start, GameStates.ani_attacking_u_end, entity.getAnimation().getTimeBetweenAnimation()));
					entity.getSingleAnimation().start();
					break;
				case DOWN:
					entity.setSingleAnimation(AnimationManager.manager.getAnimation("attacking_d", entity.getTileSet(), GameStates.ani_attacking_d_start, GameStates.ani_attacking_d_end, entity.getAnimation().getTimeBetweenAnimation()));
					entity.getSingleAnimation().start();
					break;
				default: ;
				}
				break;
			}
		} else {
			switch(entity.getContinuousState()) {
			case STANDING:
				switch(entity.getOrientation()) {
				case LEFT:
					entity.setAnimation(AnimationManager.manager.getAnimation("standing_l", entity.getTileSet(), GameStates.ani_standing_l_start, GameStates.ani_standing_l_end, entity.getAnimation().getTimeBetweenAnimation()));
					break;
				case RIGHT:
					entity.setAnimation(AnimationManager.manager.getAnimation("standing_r", entity.getTileSet(), GameStates.ani_standing_r_start, GameStates.ani_standing_r_end, entity.getAnimation().getTimeBetweenAnimation()));
					break;
				case UP:
					entity.setAnimation(AnimationManager.manager.getAnimation("standing_u", entity.getTileSet(), GameStates.ani_standing_u_start, GameStates.ani_standing_u_end, entity.getAnimation().getTimeBetweenAnimation()));
					break;
				case DOWN:
					entity.setAnimation(AnimationManager.manager.getAnimation("standing_d", entity.getTileSet(), GameStates.ani_standing_d_start, GameStates.ani_standing_d_end, entity.getAnimation().getTimeBetweenAnimation()));
					break;
				default: ;
				}
				break;
			case WALKING:
				switch(entity.getOrientation()) {
				case LEFT:
					entity.setAnimation(AnimationManager.manager.getAnimation("walking_l", entity.getTileSet(), GameStates.ani_walking_l_start, GameStates.ani_walking_l_end, entity.getAnimation().getTimeBetweenAnimation()));
					break;
				case RIGHT:
					entity.setAnimation(AnimationManager.manager.getAnimation("walking_r", entity.getTileSet(), GameStates.ani_walking_r_start, GameStates.ani_walking_r_end, entity.getAnimation().getTimeBetweenAnimation()));
					break;
				case UP:
					entity.setAnimation(AnimationManager.manager.getAnimation("walking_u", entity.getTileSet(), GameStates.ani_walking_u_start, GameStates.ani_walking_u_end, entity.getAnimation().getTimeBetweenAnimation()));
					break;
				case DOWN:
					entity.setAnimation(AnimationManager.manager.getAnimation("walking_d", entity.getTileSet(), GameStates.ani_walking_d_start, GameStates.ani_walking_d_end, entity.getAnimation().getTimeBetweenAnimation()));
					break;
				default: ;
				}
				break;
			case SLEEPING:
				switch(entity.getOrientation()) {
				case LEFT:
					entity.setAnimation(AnimationManager.manager.getAnimation("sleeping_l", entity.getTileSet(), GameStates.ani_sleeping_l_start, GameStates.ani_sleeping_l_end, entity.getAnimation().getTimeBetweenAnimation()));
					break;
				case RIGHT:
					entity.setAnimation(AnimationManager.manager.getAnimation("sleeping_r", entity.getTileSet(), GameStates.ani_sleeping_r_start, GameStates.ani_sleeping_r_end, entity.getAnimation().getTimeBetweenAnimation()));
					break;
				case UP:
					entity.setAnimation(AnimationManager.manager.getAnimation("sleeping_u", entity.getTileSet(), GameStates.ani_sleeping_u_start, GameStates.ani_sleeping_u_end, entity.getAnimation().getTimeBetweenAnimation()));
					break;
				case DOWN:
					entity.setAnimation(AnimationManager.manager.getAnimation("sleeping_d", entity.getTileSet(), GameStates.ani_sleeping_d_start, GameStates.ani_sleeping_d_end, entity.getAnimation().getTimeBetweenAnimation()));
					break;
				default: ;
				}
				break;
			default: ;
					
			}
		}
	}
}
