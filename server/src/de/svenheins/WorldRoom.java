/*
 * Copyright 2007-2010 Sun Microsystems, Inc.
 *
 * This file is part of Project Darkstar Server.
 *
 * Project Darkstar Server is free software: you can redistribute it
 * and/or modify it under the terms of the GNU General Public License
 * version 2 as published by the Free Software Foundation and
 * distributed hereunder to you.
 *
 * Project Darkstar Server is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * --
 */

package de.svenheins;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.sun.sgs.app.AppContext;
import com.sun.sgs.app.DataManager;
import com.sun.sgs.app.ManagedReference;
import com.sun.sgs.app.Task;
import com.sun.sgs.app.util.ScalableHashMap;
import com.sun.sgs.app.util.ScalableHashSet;

import de.svenheins.main.GameStates;
import de.svenheins.managers.EntityManager;
import de.svenheins.managers.PlayerManager;
import de.svenheins.managers.SpaceManager;
import de.svenheins.messages.OBJECTCODE;
import de.svenheins.messages.ServerMessages;
import de.svenheins.objects.Entity;
import de.svenheins.objects.ServerAgent;
import de.svenheins.objects.ServerAgentEmployee;
import de.svenheins.objects.ServerAgentEntrepreneur;
import de.svenheins.objects.ServerEntity;
import de.svenheins.objects.ServerRegion;
import de.svenheins.objects.ServerSpace;
import de.svenheins.objects.Space;
import de.svenheins.objects.WorldObject;

/**
 * Represents a room in the {@link World} example MUD.
 */
/**
 * @author Sven Heins
 *
 */
public class WorldRoom extends WorldObject
{
    /** The version of the serialized form of this class. */
    private static final long serialVersionUID = 1L;

    /** The {@link Logger} for this class. */
    private static final Logger logger =
        Logger.getLogger(WorldRoom.class.getName());

    /** The set of items in this room. */
    private final Set<ManagedReference<WorldObject>> items =
        new HashSet<ManagedReference<WorldObject>>();
    
    /** The set of entities in this room. */
    private final ManagedReference<ScalableHashMap<BigInteger, ManagedReference<ServerEntity>>> entities;// =
//        new ManagedReference<ScalableHashMap<BigInteger, ManagedReference<ServerEntity>>>();
    
//    private static List<ManagedReference<ServerEntity>> entitiesArray = null;
    
    /** The set of sprites in this room. */
    private final HashMap<BigInteger, ManagedReference<ServerSpace>> spaces =
        new HashMap<BigInteger, ManagedReference<ServerSpace>>();
    
//    private static List<ManagedReference<ServerSpace>> spacesArray = null;

    /** The set of players in this room. */
    private final HashMap<BigInteger, ManagedReference<WorldPlayer>> players =
        new HashMap<BigInteger, ManagedReference<WorldPlayer>>();
    

    private long duration, last; 
	private long millis, frames;

	private BigInteger lastAddedSpaceID;

	private boolean hasReceivedNewSpace;

    /**
     * Creates a new room with the given name and description, initially
     * empty of items and players.
     *
     * @param name the name of this room
     * @param description a description of this room
     */
    public WorldRoom(String name, String description, float x, float y) {
        super(name, description, x, y);
        this.last = System.currentTimeMillis();
//        this.entityIteratorIndex = 0;
        DataManager dm = AppContext.getDataManager();
        ScalableHashMap<BigInteger, ManagedReference<ServerEntity>> tempEntities = new ScalableHashMap<BigInteger, ManagedReference<ServerEntity>>();
        entities = dm.createReference(tempEntities);
    }

    /**
     * Adds an item to this room.
     * 
     * @param item the item to add to this room.
     * @return {@code true} if the item was added to the room
     */
    public boolean addItem(WorldObject item) {
        logger.log(Level.INFO, "{0} placed in {1}",
            new Object[] { item, this });

        // NOTE: we can't directly save the item in the list, or
        // we'll end up with a local copy of the item. Instead, we
        // must save a ManagedReference to the item.

        DataManager dataManager = AppContext.getDataManager();
        dataManager.markForUpdate(this);

        return items.add(dataManager.createReference(item));
    }
    
    
    /**
     * Adds an entity to this room.
     * 
     * @param item the entity to add to this room.
     * @return {@code true} if the item was added to the room
     */
    public void addEntity(ServerEntity entity) {
    	DataManager dataManager = AppContext.getDataManager();
        dataManager.markForUpdate(this);
        
        ManagedReference<ServerEntity> refEnt = dataManager.createReference(entity);
        BigInteger entID = dataManager.getObjectId(entity);
        refEnt.getForUpdate().setId(entID);
    	logger.log(Level.INFO, "{0} placed in {1}",
                    new Object[] { entity, this });
	        
        entity.setRoom(this);
        entities.get().put(refEnt.getId(),refEnt);
//        entitiesArray = new ArrayList<ManagedReference<ServerEntity>>(entities.values());
    }
    
    /**
     * Edits an entity in this room.
     * 
     * @param id: entity to edit
     * @return {@code true} if the entity was edited with success
     */
    public boolean editEntity(BigInteger id, float[] state) {
       if ( entities.get().containsKey(id)) {
    	   ServerEntity entity = entities.get().get(id).getForUpdate();
    	   entity.setX(state[0]);
    	   entity.setY(state[1]);
    	   entity.setMovement(state[2], state[3]);
    	   entity.setHeight(state[4]);
    	   entity.setWidth(state[5]);
    	   //entities.put(id, entity);
    	   return true;
       } else {
    	   return false;
       }
    	   
    }
    
    
    /**
     * Adds an space to this room.
     * 
     * @param the space to add to this room.
     * @return {@code true} if the space was added to the room
     */
    public void addSpace(ServerSpace space) {
        logger.log(Level.INFO, "{0} placed in {1}",
            new Object[] { space, this });
        //if (!spaces.containsKey(space.getId())) {
	        DataManager dataManager = AppContext.getDataManager();
	        dataManager.markForUpdate(this);
	        
	        ManagedReference<ServerSpace> refSpace = dataManager.createReference(space);
	        BigInteger spaceID = dataManager.getObjectId(space);
	        refSpace.getForUpdate().setId(spaceID);
	        
	        spaces.put(spaceID, refSpace);
	        this.setLastAddedSpaceID(spaceID);
	        logger.log(Level.INFO, "created new ID: {0}",
	                new Object[] { spaceID});
	        
	        this.setHasReceivedNewSpace(true);
	        
//        } else {
//        	logger.log(Level.INFO, "ID={0} is duplicated in {1} -> Nothing added to room",
//                    new Object[] { space.getId(), this });
//        }
//	        spacesArray = new ArrayList<ManagedReference<ServerSpace>>(spaces.values());
	        	//logger.log(Level.INFO, "entity placed");
//	        return true;
//	     } else
//	        	return false;
//        return spaces.add(dataManager.createReference(space));
    }
    
    /**
     * Edits an entity in this room.
     * 
     * @param id: entity to edit
     * @return {@code true} if the entity was edited with success
     */
    public boolean editSpace(BigInteger id, float[] state) {
       if ( spaces.containsKey(id)) {
    	   ServerSpace space = spaces.get(id).getForUpdate();
//    	   space.setX(state[0]);
//    	   space.setY(state[1]);
    	   space.setAllXY(state[0], state[1]);
    	   space.setMovement(state[2], state[3]);
    	   space.setHeight(state[4]);
    	   space.setWidth(state[5]);
    	   //entities.put(id, entity);
    	   return true;
       } else {
    	   return false;
       }
    	   
    }

    /**
     * Adds a player to this room.
     *
     * @param player the player to add
     * @return {@code true} if the player was added to the room
     */
    public void addPlayer(WorldPlayer player) {
        logger.log(Level.INFO, "{0} enters {1}",
            new Object[] { player, this });
//        if ( !players.containsValue(player)) {
	        DataManager dataManager = AppContext.getDataManager();
	        dataManager.markForUpdate(this);
	        
	        ManagedReference<WorldPlayer> refPlayer = dataManager.createReference(player);
	        BigInteger playerID = dataManager.getObjectId(player);
	        refPlayer.getForUpdate().setId(playerID);
	        
	        players.put(refPlayer.getId(), refPlayer);
	        
	        Entity playerEntity = new Entity("ship.png", playerID, player.getX(), player.getY(), player.getMX(), player.getMY());
	        
	        if (PlayerManager.add(playerEntity)) {
	        	logger.log(Level.INFO, "{0} is added to the PlayerManager",
	                    new Object[] { player});
	        } else
	        {
	        	logger.log(Level.INFO, "{0} is NOT added to the PlayerManager",
	                    new Object[] { player});
	        }
//        }
//        return players.add(dataManager.createReference(player));
    }
    
    /**
     * Edits an entity in this room.
     * 
     * @param id: entity to edit
     * @return {@code true} if the entity was edited with success
     */
    public boolean editPlayer(BigInteger id, float[] state) {
       if ( players.containsKey(id)) {
    	   WorldPlayer player = players.get(id).getForUpdate();
    	   player.setX(state[0]);
    	   player.setY(state[1]);
    	   player.setMovement(state[2], state[3]);
    	   player.setHeight(state[4]);
    	   player.setWidth(state[5]);
    	   
    	   /** PlayerManager update */
    	   PlayerManager.get(id).setX(state[0]);
    	   PlayerManager.get(id).setY(state[1]);
    	   PlayerManager.get(id).setMovement(state[2], state[3]);
    	   PlayerManager.get(id).setHeight(state[4]);
    	   PlayerManager.get(id).setWidth(state[5]);
    	   //entities.put(id, entity);
    	   return true;
       } else {
    	   return false;
       }
    	   
    }

    /**
     * Removes a player from this room.
     *
     * @param player the player to remove
     * @return {@code true} if the player was in the room
     */
    public boolean removePlayer(WorldPlayer player) {
        logger.log(Level.INFO, "{0} leaves {1}",
            new Object[] { player, this });
        if (players.containsKey(player.getId())) {
	        DataManager dataManager = AppContext.getDataManager();
	        dataManager.markForUpdate(this);
	
	//        return players.get(player.getId()).remove(dataManager.createReference(player));
	        players.remove(player.getId());
	        PlayerManager.remove(player.getId());
	        this.updateSendLogout(player.getId());
	        return true;
        } else {
        	logger.log(Level.INFO, "{0} with ID={1} was not found inside the HashMap players",
                    new Object[] { player, player.getId() });
        	return false;
        }
    }


    /**
     * Returns a list of players in this room excluding the given
     * player.
     *
     * @param player the player to exclude
     * @return the list of players
     */
//    private List<WorldPlayer>
//            getPlayersExcluding(WorldPlayer player)
//    {
//        if (players.isEmpty()) {
//            return Collections.emptyList();
//        }
//
//        ArrayList<WorldPlayer> otherPlayers =
//            new ArrayList<WorldPlayer>(players.size());
//
//        for (ManagedReference<WorldPlayer> playerRef : players) {
//            WorldPlayer other = playerRef.get();
//            if (!player.equals(other)) {
//                otherPlayers.add(other);
//            }
//        }
//
//        return Collections.unmodifiableList(otherPlayers);
//    }
    
    
    /** return Spaces*/
    public Space[] getSpaces(WorldPlayer worldPlayer, int begin, int end) {
    	Space[] objectList = new Space[end-begin];
    	int objectCounter = 0;
    	Space realSpace;
    	/** for each entity add the corresponding id to the intList */
    	for (int i = begin; i<end; i++) {
//			ServerSpace s_space = space.get();
			realSpace = SpaceManager.get(SpaceManager.idList.get(i));
			objectList[objectCounter] = realSpace;
			objectCounter++;
		}
    	return objectList;    	
    }
    
    /** return Entities*/
    public Entity[] getEntities(WorldPlayer worldPlayer, int begin, int end) {
    	Entity[] objectList = new Entity[end-begin];
    	int objectCounter = 0;
    	/** for each entity add the corresponding id to the intList */
//    	entitiesArray = new ArrayList<ManagedReference<ServerEntity>>(entities);
		Entity realEntity;
    	for (int i = begin; i<end; i++) {
//			ManagedReference<ServerEntity> entity = entitiesArray.get(i);
			realEntity = EntityManager.get(EntityManager.idList.get(i));//new Entity(entity.get().getName(), entity.get().getId(), 0, 0);
			objectList[objectCounter] = realEntity;
			objectCounter++;
		}
    	return objectList;    	
    }
    
    public ManagedReference<ScalableHashMap<BigInteger, ManagedReference<ServerEntity>>> getEntities() {
    	return entities;
    }
    
    public HashMap<BigInteger, ManagedReference<ServerSpace>> getSpaces() {
    	return spaces;
    }
    
    
    /**
     * Updates all objects in the WorldRoom
     * @param delta: time that passed until the last update-process
     */
    public void updateEntities(int begin, int end) {
    	long timestamp;
//		entitiesArray = new ArrayList<ManagedReference<ServerEntity>>(entities.values());
		Entity realEntity;
		for (int i = begin; i<end; i++) {
//			ManagedReference<ServerEntity> entity = entitiesArray.get(i);
			ServerEntity se = entities.get().get(EntityManager.idList.get(i)).getForUpdate(); 
//					EntityManager.get(EntityManager.idList.get(i)); 
//    		ServerEntity se = entity.getForUpdate();
    		
    		timestamp = System.currentTimeMillis();
    		se.move(timestamp);
    		
			if(se instanceof ServerAgentEntrepreneur) {
//				logger.log(Level.INFO, "Agent gefunden!");
				((ServerAgentEntrepreneur) se).updateLocation();
			} else if(se instanceof ServerAgentEmployee) {
//					logger.log(Level.INFO, "Agent gefunden!");
				((ServerAgentEmployee) se).updateLocation();
			} else {
				if(se.getX() < 0) {se.setHorizontalMovement(Math.abs(se.getHorizontalMovement()));}
				if(se.getX() > GameStates.getWidth()) {se.setHorizontalMovement(-Math.abs(se.getHorizontalMovement()));}
				if(se.getY() < 0) {se.setVerticalMovement(Math.abs(se.getVerticalMovement()));}
				if(se.getY() > GameStates.getHeight()) {se.setVerticalMovement(-Math.abs(se.getVerticalMovement()));}
			}
			realEntity = new Entity(se.getName(), se.getId(), se.getX(), se.getY(), se.getHorizontalMovement(), se.getVerticalMovement());
    		if ( EntityManager.overwrite(realEntity) ) {
//    			logger.log(Level.INFO, "");//, new Object[] {se.getY(),  GameStates.getHeight()});
    		} else {
    			logger.log(Level.INFO, "Overwrite error - space is not existent!");
    		}
    	}
    	
    }
    
    public void updateSpaces(int begin, int end) {
    	long timestamp;
//    	spacesArray = new ArrayList<ManagedReference<ServerSpace>>(spaces.values());

//		for (int i = begin; i<end; i++) {
    	for (int i = 0; i< SpaceManager.size(); i++) {
//			ManagedReference<ServerSpace> space = spacesArray.get(i);
    		ServerSpace s_space = spaces.get(SpaceManager.idList.get(i)).getForUpdate();//space.getForUpdate();
    		
    		if (s_space instanceof ServerRegion) {
    			/** update dimensions */
    			Space realSpace = new Space(s_space.getPolygon(), (int) s_space.getX(), (int) s_space.getY(), s_space.getName(), s_space.getId(), s_space.getRGB(), s_space.isFilled(), s_space.getTrans(), s_space.getScale());
    			if ( SpaceManager.overwrite(realSpace) ) {
//        			logger.log(Level.INFO, "");//, new Object[] {se.getY(),  GameStates.getHeight()});
        		} else {
        			logger.log(Level.INFO, "Overwrite error - space is not existent!");
        		}
    		} else {
    			timestamp = System.currentTimeMillis();
        		/** Spaces can move without limitations (_IF_ they move) */
//        		if(s_space.getHorizontalMovement() != 0 || s_space.getVerticalMovement()!=0) 
        		s_space.move(timestamp);
        		Space realSpace = new Space(s_space.getPolygon(), (int) s_space.getX(), (int) s_space.getY(), s_space.getName(), s_space.getId(), s_space.getRGB(), s_space.isFilled(), s_space.getTrans(), s_space.getScale());
        		if ( SpaceManager.overwrite(realSpace) ) {
//        			logger.log(Level.INFO, "");//, new Object[] {se.getY(),  GameStates.getHeight()});
        		} else {
        			logger.log(Level.INFO, "Overwrite error - space is not existent!");
        		}
    		}
    		
    	}
    }

    /** send all Entities to all Players */
    public void updateSendPlayersEntities(int begin, int end) {
    	for (int i = begin; i<end; i++) {
			ManagedReference<ServerEntity> entity = entities.get().get(EntityManager.idList.get(i));//entitiesArray.get(i);
			for (BigInteger playerID : players.keySet()) {
				if (players.get(playerID).get().isReady()) {
	    			BigInteger object_id = entity.get().getId();
	    			// get the six object-states: x,y,mx,my,width,height
	    			float[] object_state = new float[]{entity.get().getX(), entity.get().getY(),entity.get().getHorizontalMovement(),entity.get().getVerticalMovement(), entity.get().getWidth(), entity.get().getHeight()};
	    			players.get(playerID).get().getSession().send(ServerMessages.sendObjectState(OBJECTCODE.ENTITY, object_id, object_state));
    			}
			}
//    		for (ManagedReference<WorldPlayer> player : players) {
//    			if (player.get().isReady()) {
//	    			BigInteger object_id = entity.get().getId();
//	    			// get the six object-states: x,y,mx,my,width,height
//	    			float[] object_state = new float[]{entity.get().getX(), entity.get().getY(),entity.get().getHorizontalMovement(),entity.get().getVerticalMovement(), entity.get().getWidth(), entity.get().getHeight()};
//	    			player.get().getSession().send(ServerMessages.sendObjectState(OBJECTCODE.ENTITY, object_id, object_state));
//    			}
//    		}
    	}
    	
//    	long durationOfUpdate = System.currentTimeMillis()-last;
//    	logger.log(Level.INFO, "sending needs {0} millis",
//                new Object[] { durationOfUpdate });
    }

    
    public void updateSendPlayersSpaces(int begin, int end) {
//    	for (int i = begin; i<end; i++) {
    	for ( int i = 0; i < SpaceManager.idList.size(); i++) {
			ServerSpace space = spaces.get(SpaceManager.idList.get(i)).get(); //spacesArray.get(i);
			for (BigInteger playerID : players.keySet()) {
				if (players.get(playerID).get().isReady()) {
	    			BigInteger object_id = space.getId();
	    			// get the six object-states: x,y,mx,my,width,height
	    			float[] object_state = new float[]{space.getX(), space.getY(),space.getHorizontalMovement(),space.getVerticalMovement(), space.getWidth(), space.getHeight()};
	    			players.get(playerID).get().getSession().send(ServerMessages.sendObjectState(OBJECTCODE.SPACE, object_id, object_state));
    			}
			}
//    		for (ManagedReference<WorldPlayer> player : players) {
//    			if (player.get().isReady()) {
//	    			BigInteger object_id = space.getId();
//	    			// get the six object-states: x,y,mx,my,width,height
//	    			float[] object_state = new float[]{space.getX(), space.getY(),space.getHorizontalMovement(),space.getVerticalMovement(), space.getWidth(), space.getHeight()};
//	    			player.get().getSession().send(ServerMessages.sendObjectState(OBJECTCODE.SPACE, object_id, object_state));
//    			}
//    		}
    	}
    }
    
    
    public int getCountEntities() {
    	return this.entities.get().size();
    }
    
    public int getCountSpaces() {
    	return this.spaces.size();
    }

	public void updateSendPlayers(int index, int endIndex) {
		if (players.size()<index+2 || players.size() <=1) {
			/** do nothing, because there are not enough players logged in */
		} else
		{
			WorldPlayer worldPlayer;
		//			if (endIndex > players.size()) endIndex = players.size();
//			for (int i = index; i<2; i++) {
			for(BigInteger playerIDfrom: players.keySet()) {
//				WorldPlayer worldPlayer = players.get(PlayerManager.idList.get(i)).get(); //spacesArray.get(i);
				for (BigInteger playerIDto : players.keySet()) {
					/** send only if ready && not the same player */
					if (players.get(playerIDto).get().isReady() && playerIDto != playerIDfrom) {
		    			BigInteger object_id = playerIDfrom;
		    			// get the six object-states: x,y,mx,my,width,height
		    			worldPlayer = players.get(playerIDfrom).get();
		    			float[] object_state = new float[]{worldPlayer.getX(), worldPlayer.getY(),worldPlayer.getHorizontalMovement(),worldPlayer.getVerticalMovement(), worldPlayer.getWidth(), worldPlayer.getHeight()};
		    			players.get(playerIDto).get().getSession().send(ServerMessages.sendObjectState(OBJECTCODE.PLAYER, object_id, object_state));
	    			}
				}
//	    	}
			}
		}
	}
	
	public void updateSendLogout(BigInteger playerID) {
		/** TODO: LOGOUT message */
		for(BigInteger playerIDto: players.keySet()) {
			if (players.get(playerIDto).get().isReady() && playerIDto != playerID) {
				players.get(playerIDto).get().getSession().send(ServerMessages.sendDelete(OBJECTCODE.PLAYER, playerID));
			}
		}
//		if (players.size()<index+2 || players.size() <=1) {
//			/** do nothing, because there are not enough players logged in */
//		} else
//		{
//			WorldPlayer worldPlayer;
//		//			if (endIndex > players.size()) endIndex = players.size();
////			for (int i = index; i<2; i++) {
//			for(BigInteger playerIDfrom: players.keySet()) {
////				WorldPlayer worldPlayer = players.get(PlayerManager.idList.get(i)).get(); //spacesArray.get(i);
//				for (BigInteger playerIDto : players.keySet()) {
//					/** send only if ready && not the same player */
//					if (players.get(playerIDto).get().isReady() && playerIDto != playerIDfrom) {
//		    			BigInteger object_id = playerIDfrom;
//		    			// get the six object-states: x,y,mx,my,width,height
//		    			worldPlayer = players.get(playerIDfrom).get();
//		    			float[] object_state = new float[]{worldPlayer.getX(), worldPlayer.getY(),worldPlayer.getHorizontalMovement(),worldPlayer.getVerticalMovement(), worldPlayer.getWidth(), worldPlayer.getHeight()};
//		    			players.get(playerIDto).get().getSession().send(ServerMessages.sendObjectState(OBJECTCODE.PLAYER, object_id, object_state));
//	    			}
//				}
////	    	}
//			}
//		}
	}
	
	/** send init process if we got new spaces */
	public void updateSendInitSpaces() {
		for(BigInteger playerIDto: players.keySet()) {
			if (players.get(playerIDto).get().isReady()) {
				players.get(playerIDto).get().initSpaces();
			}
		}
	}

	public BigInteger getLastAddedSpaceID() {
		return lastAddedSpaceID;
	}

	public void setLastAddedSpaceID(BigInteger lastAddedSpaceID) {
		this.lastAddedSpaceID = lastAddedSpaceID;
	}
    
//    /** Update entityArray */
//    public void updateEntityArray() {
//    	entitiesArray = new ArrayList<ManagedReference<ServerEntity>>(entities.values());
//    }
//    
//    /** Update spacesArray */
//    public void updateSpaceArray() {
//    	spacesArray = new ArrayList<ManagedReference<ServerSpace>>(spaces.values());
//    }
	
	public boolean getHasReceivedNewSpace() {
		return hasReceivedNewSpace;
	}
	
	public void setHasReceivedNewSpace(boolean hasReceivedNewSpace) {
		this.hasReceivedNewSpace = hasReceivedNewSpace;
	}
}