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
import com.sun.sgs.app.util.ScalableHashSet;

import de.svenheins.main.GameStates;
import de.svenheins.managers.EntityManager;
import de.svenheins.managers.SpaceManager;
import de.svenheins.messages.OBJECTCODE;
import de.svenheins.messages.ServerMessages;
import de.svenheins.objects.Entity;
import de.svenheins.objects.ServerAgent;
import de.svenheins.objects.ServerAgentEmployee;
import de.svenheins.objects.ServerAgentEntrepreneur;
import de.svenheins.objects.ServerEntity;
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
    private final Set<ManagedReference<ServerEntity>> entities =
        new HashSet<ManagedReference<ServerEntity>>();
    
    private static List<ManagedReference<ServerEntity>> entitiesArray = null;
    
//    /** The set of sprites in this room. */
//    private final Set<ManagedReference<ServerSprite>> sprites =
//        new HashSet<ManagedReference<ServerSprite>>();
    
    /** The set of sprites in this room. */
    private final Set<ManagedReference<ServerSpace>> spaces =
        new HashSet<ManagedReference<ServerSpace>>();
    
    private static List<ManagedReference<ServerSpace>> spacesArray = null;

    /** The set of players in this room. */
    private final Set<ManagedReference<WorldPlayer>> players =
        new HashSet<ManagedReference<WorldPlayer>>();
    
//    private Iterator<ManagedReference<ServerEntity>> entityIterator;
//    private int entityIteratorIndex;
    

    private long duration, last; 
	private long millis, frames;

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
    public boolean addEntity(ServerEntity entity) {
        logger.log(Level.INFO, "{0} placed in {1}",
            new Object[] { entity, this });

        DataManager dataManager = AppContext.getDataManager();
        dataManager.markForUpdate(this);

        entity.setRoom(this);
        
        ManagedReference<ServerEntity> refEnt = dataManager.createReference(entity);
        BigInteger entID = dataManager.getObjectId(entity);
        refEnt.getForUpdate().setId(entID);
        
        if (entities.add(refEnt) == true) {
        	entitiesArray = new ArrayList<ManagedReference<ServerEntity>>(entities);
        	
        	//logger.log(Level.INFO, "entity placed");
        	
        	return true;
        } else
        	return false;
    }
    
    /**
     * Adds an sprite to this room.
     * 
     * @param item the entity to add to this room.
     * @return {@code true} if the item was added to the room
     */
//    public boolean addSprite(ServerSprite sprite) {
//        logger.log(Level.INFO, "{0} placed in {1}",
//            new Object[] { sprite, this });
//
//        DataManager dataManager = AppContext.getDataManager();
//        dataManager.markForUpdate(this);
//
//        return sprites.add(dataManager.createReference(sprite));
//    }
    
    
    /**
     * Adds an space to this room.
     * 
     * @param the space to add to this room.
     * @return {@code true} if the space was added to the room
     */
    public boolean addSpace(ServerSpace space) {
        logger.log(Level.INFO, "{0} placed in {1}",
            new Object[] { space, this });

        DataManager dataManager = AppContext.getDataManager();
        dataManager.markForUpdate(this);
        
        ManagedReference<ServerSpace> refSpace = dataManager.createReference(space);
        BigInteger spaceID = dataManager.getObjectId(space);
        refSpace.getForUpdate().setId(spaceID);

        if (spaces.add(refSpace) == true) {
        	spacesArray = new ArrayList<ManagedReference<ServerSpace>>(spaces);
        	//logger.log(Level.INFO, "entity placed");
        	return true;
        } else
        	return false;
//        return spaces.add(dataManager.createReference(space));
    }

    /**
     * Adds a player to this room.
     *
     * @param player the player to add
     * @return {@code true} if the player was added to the room
     */
    public boolean addPlayer(WorldPlayer player) {
        logger.log(Level.INFO, "{0} enters {1}",
            new Object[] { player, this });

        DataManager dataManager = AppContext.getDataManager();
        dataManager.markForUpdate(this);

        return players.add(dataManager.createReference(player));
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

        DataManager dataManager = AppContext.getDataManager();
        dataManager.markForUpdate(this);

        return players.remove(dataManager.createReference(player));
    }

    /**
     * Returns a description of what the given player sees in this room.
     *
     * @param looker the player looking in this room
     * @return a description of what the given player sees in this room
     */
    public String look(WorldPlayer looker) {
        logger.log(Level.INFO, "{0} looks at {1}",
            new Object[] { looker, this });

        StringBuilder output = new StringBuilder();
        output.append("You are in ").append(getDescription()).append(".\n");

        List<WorldPlayer> otherPlayers =
            getPlayersExcluding(looker);

        if (!otherPlayers.isEmpty()) {
            output.append("Also in here are ");
            appendPrettyList(output, otherPlayers);
            output.append(".\n");
        }

        if (!items.isEmpty()) {
            output.append("On the floor you see:\n");
            for (ManagedReference<WorldObject> itemRef : items) {
                WorldObject item = itemRef.get();
                float x = item.getX();
                float y = item.getY();
                output.append(item.getDescription()).append(" at position ").append(x).append(' ').append(y).append('\n');
            }
        }
//        if (!sprites.isEmpty()) {
//            output.append("And you see the following sprites:\n");
//            for (ManagedReference<ServerSprite> spriteRef : sprites) {
//            	ServerSprite sprite = spriteRef.get();
//                float h = sprite.getHeight();
//                float w = sprite.getWidth();
//                output.append("Height: ").append(h).append(" Width: ").append(w).append('\n');
//            }
//        }
        if (!entities.isEmpty()) {
            output.append("And you see the following entities:\n");
            for (ManagedReference<ServerEntity> entityRef : entities) {
            	ServerEntity entity = entityRef.get();
                float h = entity.getHeight();
                float w = entity.getWidth();
                float x = entity.getX();
                float y = entity.getY();
                output.append("Height: ").append(h).append(" Width: ").append(w).append(" X: ").append(x).append(" Y: ").append(y).append('\n');
            }
        }
        
        return output.toString();
    }

    /**
     * Appends the names of the {@code WorldObject}s in the list
     * to the builder, separated by commas, with an "and" before the final
     * item.
     *
     * @param builder the {@code StringBuilder} to append to
     * @param list the list of items to format
     */
    private void appendPrettyList(StringBuilder builder,
        List<? extends WorldObject> list)
    {
        if (list.isEmpty()) {
            return;
        }

        int lastIndex = list.size() - 1;
        WorldObject last = list.get(lastIndex);

        Iterator<? extends WorldObject> it =
            list.subList(0, lastIndex).iterator();
        if (it.hasNext()) {
            WorldObject other = it.next();
            builder.append(other.getName());
            while (it.hasNext()) {
                other = it.next();
                builder.append(" ,");
                builder.append(other.getName());
            }
            builder.append(" and ");
        }
        builder.append(last.getName());
    }

    /**
     * Returns a list of players in this room excluding the given
     * player.
     *
     * @param player the player to exclude
     * @return the list of players
     */
    private List<WorldPlayer>
            getPlayersExcluding(WorldPlayer player)
    {
        if (players.isEmpty()) {
            return Collections.emptyList();
        }

        ArrayList<WorldPlayer> otherPlayers =
            new ArrayList<WorldPlayer>(players.size());

        for (ManagedReference<WorldPlayer> playerRef : players) {
            WorldPlayer other = playerRef.get();
            if (!player.equals(other)) {
                otherPlayers.add(other);
            }
        }

        return Collections.unmodifiableList(otherPlayers);
    }
    
    
//    public float[] getEntityState(WorldPlayer looker, int entityId) {
//    	float[] retState = new float[]{0,0,0,0};
//        for (ManagedReference<ServerEntity> entity : entities) {
//        	//TODO: ineffective
//        	if (entity.get().getId() == entityId) {
//        		retState = new float[]{entity.get().getX(), entity.get().getY(),entity.get().getHorizontalMovement(),entity.get().getVerticalMovement()};
//        	}
//        }
//    	return retState;
//    }
//
//    
//    public float[] getSpaceState(WorldPlayer looker, int spaceId) {
//    	float[] retState = new float[]{0,0,0,0};
//        for (ManagedReference<ServerSpace> space : spaces) {
//        	//TODO: ineffective
//        	if (space.get().getId() == spaceId) {
//        		retState = new float[]{space.get().getX(), space.get().getY(),space.get().getHorizontalMovement(),space.get().getVerticalMovement()};
//        	}
//        }
//    	return retState;
//    }
    
//    /**
//     * 
//     * @param objCode 
//     * @param worldPlayer: the player who asked for the status
//     * @param object_id: the object-id that was requested
//     * @return the status of the object: x, y, mx (velocity in x direction), my (velocity in y direction), width, height
//     */
//    public float[] getObjectState(OBJECTCODE objCode, WorldPlayer worldPlayer, int object_id) {
//		float[] retState = new float[6];
//		switch (objCode) {
//			case SPACE:
//			{
//				for (ManagedReference<ServerSpace> space : spaces) {
//					//TODO: ineffective
//					if (space.get().getId() == object_id) {
//						retState = new float[]{space.get().getX(), space.get().getY(),space.get().getHorizontalMovement(),space.get().getVerticalMovement(), space.get().getWidth(), space.get().getHeight()};
//					}
//				}
//				break;
//			}
//			case ENTITY:
//			{
//				for (ManagedReference<ServerEntity> entity : entities) {
//					//TODO: ineffective
//					if (entity.get().getId() == object_id) {
//		        		retState = new float[]{entity.get().getX(), entity.get().getY(),entity.get().getHorizontalMovement(),entity.get().getVerticalMovement(), entity.get().getWidth(), entity.get().getHeight()};
//		        	}
//				}
//				break;
//			}
//		}
//    	return retState;
//	}
//    
//    /** return Name of Object */
//    public String getObjectName(OBJECTCODE objCode, WorldPlayer worldPlayer, int object_id) {
//		String retName = "";
//		switch (objCode) {
//			case SPACE:
//			{
//				for (ManagedReference<ServerSpace> space : spaces) {
//					//TODO: ineffective
//					if (space.get().getId() == object_id) {
//						retName = space.get().getName();
//					}
//				}
//				break;
//			}
//			case ENTITY:
//			{
//				for (ManagedReference<ServerEntity> entity : entities) {
//					//TODO: ineffective
//					if (entity.get().getId() == object_id) {
//						retName = entity.get().getName();	
//					}
//				}
//				break;
//			}
//		}
//    	return retName;
//	}
    
//    /** return Ids for Entities and Spaces*/
//    public LocalObject[] getLocalObjects(OBJECTCODE objCode, WorldPlayer worldPlayer) {
//    	ArrayList<LocalObject> objectList = new ArrayList<LocalObject>();
//    	switch (objCode) {
//    	case ENTITY:
////    		Entity[] retObjects = ;
//    		/** for each entity add the corresponding id to the intList */
//    		for (ManagedReference<ServerEntity> entity : entities) {
//				Entity realEntity = new Entity(entity.get().getName(), entity.get().getId(), 0, 0);
//    			objectList.add(realEntity);
//			}
////    		ret = new int[intList.size()];
////    	    for (int i=0; i < ret.length; i++)
////    	    {
////    	        ret[i] = intList.get(i).intValue();
////    	    }	
//    		break;
//    	case SPACE:
//    		for (ManagedReference<ServerSpace> space : spaces) {
//    			ServerSpace s_space = space.get();
//    			//Color color = new Color(s_space.getRGB()[0], s_space.getRGB()[1], s_space.getRGB()[2]);
//    			Space realSpace = new Space(s_space.getName(), s_space.getId(), s_space.getRGB(), s_space.isFilled(), s_space.getTrans());
//    			objectList.add(realSpace);
//			}
////    		ret = new int[intList.size()];
////    	    for (int i=0; i < ret.length; i++)
////    	    {
////    	        ret[i] = intList.get(i).intValue();
////    	    }
//    		break;
//    	default:
//    			;
//    	}
//    	return (LocalObject[]) objectList.toArray();    	
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
    
    public Set<ManagedReference<ServerEntity>> getEntities() {
    	return entities;
    }
    
    public Set<ManagedReference<ServerSpace>> getSpaces() {
    	return spaces;
    }
    
    
    /**
     * Updates all objects in the WorldRoom
     * @param delta: time that passed until the last update-process
     */
    public void updateEntities(int begin, int end) {
    	long timestamp;
//		entityIterator = entities.iterator();
//		while (entityIterator.hasNext()) {
//			ServerEntity se = entityIterator.next().getForUpdate();
//			entityIteratorIndex++;
//			if(se.getHorizontalMovement() != 0 || se.getVerticalMovement()!=0) se.move(duration);
//			if(se.getX() < 0) {se.setHorizontalMovement(Math.abs(se.getHorizontalMovement()));}
//			if(se.getX() > GameStates.getWidth()) {se.setHorizontalMovement(-Math.abs(se.getHorizontalMovement()));}
//			if(se.getY() < 0) {se.setVerticalMovement(Math.abs(se.getVerticalMovement()));}
//			if(se.getY() > GameStates.getHeight()) {se.setVerticalMovement(-Math.abs(se.getVerticalMovement()));}
//			
//			if (!AppContext.getTaskManager().shouldContinue()) {
//			    AppContext.getTaskManager().scheduleTask(task.get());
//			    return;
//			}
//		}
//		entityIteratorIndex = 0;
//		entityIterator = entities.iterator();
//		if (entitiesArray.size() == 0 || entitiesArray.get(0) == null) {
		entitiesArray = new ArrayList<ManagedReference<ServerEntity>>(entities);
//		}
		Entity realEntity;
		for (int i = begin; i<end; i++) {
			ManagedReference<ServerEntity> entity = entitiesArray.get(i);
    		ServerEntity se = entity.getForUpdate();
    		
    		timestamp = System.currentTimeMillis();
    		se.move(timestamp);
    		
//    		if(se.getHorizontalMovement() != 0 || se.getVerticalMovement()!=0) se.move(duration);
//    		logger.log(Level.INFO, "y: {0}  height: {1}",
//    				new Object[] {se.getY(),  GameStates.getHeight()});
			
			
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
			realEntity = new Entity(se.getName(), se.getId(), se.getX(), se.getY());
    		if ( EntityManager.overwrite(realEntity) ) {
//    			logger.log(Level.INFO, "");//, new Object[] {se.getY(),  GameStates.getHeight()});
    		} else {
    			logger.log(Level.INFO, "Overwrite error - space is not existent!");
    		}
    	}
    	
//    	long durationOfUpdate = System.currentTimeMillis()-last;
//    	logger.log(Level.INFO, "update needs {0} millis",
//                new Object[] { durationOfUpdate });
    }
    
    public void updateSpaces(int begin, int end) {
    	long timestamp;
//    	for (ManagedReference<ServerSpace> space : spaces) {
//    		ServerSpace s_space = space.getForUpdate();
//    		if(s_space.getHorizontalMovement() != 0 || s_space.getVerticalMovement()!=0) s_space.move(duration);
//			if(s_space.getX() < 0) {s_space.setHorizontalMovement(Math.abs(s_space.getHorizontalMovement()));}
//			if(s_space.getX()+s_space.getWidth() > GameStates.getWidth()) {s_space.setHorizontalMovement(-Math.abs(s_space.getHorizontalMovement()));}
//			if(s_space.getY() < 0) {s_space.setVerticalMovement(Math.abs(s_space.getVerticalMovement()));}
//			if(s_space.getY()+s_space.getHeight() > GameStates.getHeight()) {s_space.setVerticalMovement(-Math.abs(s_space.getVerticalMovement()));}
//    	}
    	
    	spacesArray = new ArrayList<ManagedReference<ServerSpace>>(spaces);
//		}
		for (int i = begin; i<end; i++) {
			ManagedReference<ServerSpace> space = spacesArray.get(i);
    		ServerSpace s_space = space.getForUpdate();
    		
    		timestamp = System.currentTimeMillis();
    		/** Spaces can move without limitations (_IF_ they move) */
    		if(s_space.getHorizontalMovement() != 0 || s_space.getVerticalMovement()!=0) s_space.move(timestamp);
    		Space realSpace = new Space(s_space.getName(), s_space.getId(), s_space.getRGB(), s_space.isFilled(), s_space.getTrans(), s_space.getScale());
    		if ( SpaceManager.overwrite(realSpace) ) {
//    			logger.log(Level.INFO, "");//, new Object[] {se.getY(),  GameStates.getHeight()});
    		} else {
    			logger.log(Level.INFO, "Overwrite error - space is not existent!");
    		}
    			
    			    		
////    		logger.log(Level.INFO, "y: {0}  height: {1}",
////    				new Object[] {se.getY(),  GameStates.getHeight()});
//			if(s_space.getX() < 0) {s_space.setHorizontalMovement(Math.abs(s_space.getHorizontalMovement()));}
//			if(s_space.getX() > GameStates.getWidth()) {s_space.setHorizontalMovement(-Math.abs(s_space.getHorizontalMovement()));}
//			if(s_space.getY() < 0) {s_space.setVerticalMovement(Math.abs(s_space.getVerticalMovement()));}
//			if(s_space.getY() > GameStates.getHeight()) {s_space.setVerticalMovement(-Math.abs(s_space.getVerticalMovement()));}
    	}
    }

    /** send all Entities to all Players */
    public void updateSendPlayersEntities(int begin, int end) {
    	for (int i = begin; i<end; i++) {
			ManagedReference<ServerEntity> entity = entitiesArray.get(i);
    		for (ManagedReference<WorldPlayer> player : players) {
    			if (player.get().isReady()) {
	    			BigInteger object_id = entity.get().getId();
	    			// get the six object-states: x,y,mx,my,width,height
	    			float[] object_state = new float[]{entity.get().getX(), entity.get().getY(),entity.get().getHorizontalMovement(),entity.get().getVerticalMovement(), entity.get().getWidth(), entity.get().getHeight()};
	    			player.get().getSession().send(ServerMessages.sendObjectState(OBJECTCODE.ENTITY, object_id, object_state));
    			}
    		}
    	}
    	
//    	long durationOfUpdate = System.currentTimeMillis()-last;
//    	logger.log(Level.INFO, "sending needs {0} millis",
//                new Object[] { durationOfUpdate });
    }

    
    public void updateSendPlayersSpaces(long delta, int begin, int end) {
    	for (int i = begin; i<end; i++) {
			ManagedReference<ServerSpace> space = spacesArray.get(i);
    		for (ManagedReference<WorldPlayer> player : players) {
    			if (player.get().isReady()) {
	    			BigInteger object_id = space.get().getId();
	    			// get the six object-states: x,y,mx,my,width,height
	    			float[] object_state = new float[]{space.get().getX(), space.get().getY(),space.get().getHorizontalMovement(),space.get().getVerticalMovement(), space.get().getWidth(), space.get().getHeight()};
	    			player.get().getSession().send(ServerMessages.sendObjectState(OBJECTCODE.SPACE, object_id, object_state));
    			}
    		}
    	}
    	
//    	for (ManagedReference<ServerSpace> space : spaces) {
//    		for (ManagedReference<WorldPlayer> player : players) {
//    			int object_id = space.get().getId();
//    			// get the six object-states: x,y,mx,my,width,height
//    			float[] object_state = new float[]{space.get().getX(), space.get().getY(),space.get().getHorizontalMovement(),space.get().getVerticalMovement(), space.get().getWidth(), space.get().getHeight()};
//	        	
//    			player.get().getSession().send(ServerMessages.sendObjectState(OBJECTCODE.SPACE, object_id, object_state));
//    		}
//    	}
    }
    
    
//    /** send all Entities-names and Spaces-names to all Players */
//    public void updateNameSendPlayers(long delta) {
//    	for (ManagedReference<ServerEntity> entity : entities) {
//    		for (ManagedReference<WorldPlayer> player : players) {
//    			int object_id = entity.get().getId();
//    			// get the six object-states: x,y,mx,my,width,height
//    			String objectName = this.getObjectName(OBJECTCODE.ENTITY, player.get(), object_id);
//    			player.get().getSession().send(ServerMessages.sendObjectName(OBJECTCODE.ENTITY, object_id, objectName));
//    		}
//    	}
//    	for (ManagedReference<ServerSpace> space : spaces) {
//    		for (ManagedReference<WorldPlayer> player : players) {
//    			int object_id = space.get().getId();
//    			// get the six object-states: x,y,mx,my,width,height
//    			String objectName = this.getObjectName(OBJECTCODE.SPACE, player.get(), object_id);
//    			player.get().getSession().send(ServerMessages.sendObjectName(OBJECTCODE.SPACE, object_id, objectName));
//    		}
//    	}
//    }
    
    public int getCountEntities() {
    	return this.entities.size();
    }
    
    public int getCountSpaces() {
    	return this.spaces.size();
    }
    
    /** Update entityArray */
    public void updateEntityArray() {
    	entitiesArray = new ArrayList<ManagedReference<ServerEntity>>(entities);
    }
    
    /** Update spacesArray */
    public void updateSpaceArray() {
    	spacesArray = new ArrayList<ManagedReference<ServerSpace>>(spaces);
    }
}