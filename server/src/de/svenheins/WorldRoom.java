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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.sun.sgs.app.AppContext;
import com.sun.sgs.app.DataManager;
import com.sun.sgs.app.ManagedReference;
import com.sun.sgs.app.util.ScalableHashMap;

import de.svenheins.main.GameStates;
import de.svenheins.managers.PlayerManager;
import de.svenheins.managers.ServerTextureManager;
import de.svenheins.managers.SpriteManager;
import de.svenheins.managers.TileSetManager;
import de.svenheins.messages.OBJECTCODE;
import de.svenheins.messages.ServerMessages;
import de.svenheins.objects.AreaInfluence;
import de.svenheins.objects.Entity;
import de.svenheins.objects.GrowthSpot;
import de.svenheins.objects.InteractionTile;
import de.svenheins.objects.LocalObject;
import de.svenheins.objects.PlayerEntity;
import de.svenheins.objects.ServerAgentEmployee;
import de.svenheins.objects.ServerAgentEntrepreneur;
import de.svenheins.objects.ServerAreaInfluence;
import de.svenheins.objects.ServerContainer;
import de.svenheins.objects.ServerEntity;
import de.svenheins.objects.ServerGrowthSpot;
import de.svenheins.objects.ServerItem;
import de.svenheins.objects.ServerPlayer;
import de.svenheins.objects.ServerRegion;
import de.svenheins.objects.ServerSpace;
import de.svenheins.objects.ServerSprite;
import de.svenheins.objects.Space;
import de.svenheins.objects.Sprite;
import de.svenheins.objects.TileSet;
import de.svenheins.objects.WorldObject;
import de.svenheins.objects.items.Item;
import de.svenheins.objects.items.ItemFactoryCollection;

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

//    /** The set of items in this room. */
//    private final Set<ManagedReference<WorldObject>> items =
//        new HashSet<ManagedReference<WorldObject>>();
    
    /** The set of entities in this room. */
    private final ManagedReference<ScalableHashMap<BigInteger, ManagedReference<ServerEntity>>> entities;
    // =
//        new ManagedReference<ScalableHashMap<BigInteger, ManagedReference<ServerEntity>>>();
    
//    private static List<ManagedReference<ServerEntity>> entitiesArray = null;
    
    /** The set of sprites in this room. */
    private final ManagedReference<ScalableHashMap<BigInteger, ManagedReference<ServerSpace>>> spaces;// =
//        new HashMap<BigInteger, ManagedReference<ServerSpace>>();
    
    private final ManagedReference<ScalableHashMap<BigInteger, ManagedReference<ServerItem>>> itemList;
    private BigInteger maxItemIndex;
    private final ManagedReference<ScalableHashMap<BigInteger, ManagedReference<ServerAreaInfluence>>> areaInfluenceList;
    private final ManagedReference<ScalableHashMap<BigInteger, ManagedReference<ServerAreaInfluence>>> areaInfluenceSendList;
    private BigInteger maxAreaInfluenceIndex;
    private final ManagedReference<ScalableHashMap<BigInteger, ManagedReference<ServerGrowthSpot>>> growthSpotList;
    private final ManagedReference<ScalableHashMap<BigInteger, ManagedReference<ServerGrowthSpot>>> growthSpotSendList;
    private BigInteger maxGrowthSpotIndex;
    
    private final ManagedReference<ScalableHashMap<String, ManagedReference<ServerContainer>>> containerList;
    private final ManagedReference<ScalableHashMap<String, ManagedReference<ServerContainer>>> containerUseList;
    private final ManagedReference<ScalableHashMap<String, ManagedReference<ServerContainer>>> containerEqBodyList;
    private final ManagedReference<ScalableHashMap<String, ManagedReference<ServerContainer>>> containerEqWeaponList;
    private final ManagedReference<ScalableHashMap<String, ManagedReference<ServerContainer>>> containerEqHelmetList;
    private final ManagedReference<ScalableHashMap<String, ManagedReference<ServerContainer>>> containerEqFeetList;
    private final ManagedReference<ScalableHashMap<String, ManagedReference<ServerContainer>>> containerEqShieldList;
    private final ManagedReference<ScalableHashMap<String, ManagedReference<ServerContainer>>> containerEqAmuletList;
    private final ManagedReference<ScalableHashMap<String, ManagedReference<ServerContainer>>> containerEqGloveList;
    private final ManagedReference<ScalableHashMap<String, ManagedReference<ServerContainer>>> containerEqShirtList;
    private final ManagedReference<ScalableHashMap<String, ManagedReference<ServerContainer>>> containerEqTrousersList;
    
//    private static List<ManagedReference<ServerSpace>> spacesArray = null;


	/** The set of players in this room. */
    private final HashMap<BigInteger, ManagedReference<WorldPlayer>> players =
        new HashMap<BigInteger, ManagedReference<WorldPlayer>>();
    
    /** the set of players, that are just initializing */
    private final HashMap<BigInteger, Iterator<BigInteger>> playerInitializingEntities = new HashMap<BigInteger, Iterator<BigInteger>>();
    private final HashMap<BigInteger, Iterator<BigInteger>> playerInitializingItems = new HashMap<BigInteger, Iterator<BigInteger>>();
    private final HashMap<BigInteger, Iterator<BigInteger>> playerInitializingAreaInfluences = new HashMap<BigInteger, Iterator<BigInteger>>();
    private final HashMap<BigInteger, Iterator<BigInteger>> playerInitializingGrowthSpots = new HashMap<BigInteger, Iterator<BigInteger>>();
    
    
    /** The set of corresponding player-entities in this room. */
    private final ManagedReference<ScalableHashMap<String, ManagedReference<ServerPlayer>>> serverPlayers;// = new HashMap<BigInteger, ManagedReference<ServerPlayer>>();
    
    
    
    

    private long duration, last; 
	private long millis, frames;

	private BigInteger lastAddedSpaceID;

	private boolean hasReceivedNewSpace;
	
	private long worldTime;

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
        this.setWorldTime(0);
//        this.entityIteratorIndex = 0;
        DataManager dm = AppContext.getDataManager();
        ScalableHashMap<BigInteger, ManagedReference<ServerEntity>> tempEntities = new ScalableHashMap<BigInteger, ManagedReference<ServerEntity>>();
        entities = dm.createReference(tempEntities);
        
        ScalableHashMap<BigInteger, ManagedReference<ServerSpace>> tempSpaces = new ScalableHashMap<BigInteger, ManagedReference<ServerSpace>>();
        spaces = dm.createReference(tempSpaces);
        
        ScalableHashMap<String, ManagedReference<ServerPlayer>> tempPlayers = new ScalableHashMap<String, ManagedReference<ServerPlayer>>();
        serverPlayers = dm.createReference(tempPlayers);
        
        ScalableHashMap<BigInteger, ManagedReference<ServerItem>> tempItemList = new ScalableHashMap<BigInteger, ManagedReference<ServerItem>>();
        itemList = dm.createReference(tempItemList);
//        BigInteger tempMaxItemID = BigInteger.valueOf(0);
        maxItemIndex = BigInteger.valueOf(0);
        
		ScalableHashMap<BigInteger, ManagedReference<ServerAreaInfluence>> tempAreaInfluenceList = new ScalableHashMap<BigInteger, ManagedReference<ServerAreaInfluence>>();
        areaInfluenceList = dm.createReference(tempAreaInfluenceList);
        ScalableHashMap<BigInteger, ManagedReference<ServerAreaInfluence>> tempAreaInfluenceSendList = new ScalableHashMap<BigInteger, ManagedReference<ServerAreaInfluence>>();
        areaInfluenceSendList = dm.createReference(tempAreaInfluenceSendList);
        
//        BigInteger tempMaxItemID = BigInteger.valueOf(0);
        maxAreaInfluenceIndex = BigInteger.valueOf(0);
        
        ScalableHashMap<BigInteger, ManagedReference<ServerGrowthSpot>> tempGrowthSpotList = new ScalableHashMap<BigInteger, ManagedReference<ServerGrowthSpot>>();
        growthSpotList = dm.createReference(tempGrowthSpotList);
        ScalableHashMap<BigInteger, ManagedReference<ServerGrowthSpot>> tempGrowthSpotSendList = new ScalableHashMap<BigInteger, ManagedReference<ServerGrowthSpot>>();
        growthSpotSendList = dm.createReference(tempGrowthSpotSendList);
        
//        BigInteger tempMaxItemID = BigInteger.valueOf(0);
        maxGrowthSpotIndex = BigInteger.valueOf(0);
        
        ScalableHashMap<String, ManagedReference<ServerContainer>> tempContainerList = new ScalableHashMap<String, ManagedReference<ServerContainer>>();
        containerList = dm.createReference(tempContainerList);
        
        ScalableHashMap<String, ManagedReference<ServerContainer>> tempContainerUseList = new ScalableHashMap<String, ManagedReference<ServerContainer>>();
        containerUseList = dm.createReference(tempContainerUseList);
        
        ScalableHashMap<String, ManagedReference<ServerContainer>> tempContainerEqBodyList = new ScalableHashMap<String, ManagedReference<ServerContainer>>();
        containerEqBodyList = dm.createReference(tempContainerEqBodyList);
        ScalableHashMap<String, ManagedReference<ServerContainer>> tempContainerEqWeaponList = new ScalableHashMap<String, ManagedReference<ServerContainer>>();
        containerEqWeaponList = dm.createReference(tempContainerEqWeaponList);
        ScalableHashMap<String, ManagedReference<ServerContainer>> tempContainerEqHelmetList = new ScalableHashMap<String, ManagedReference<ServerContainer>>();
        containerEqHelmetList = dm.createReference(tempContainerEqHelmetList);
        ScalableHashMap<String, ManagedReference<ServerContainer>> tempContainerEqFeetList = new ScalableHashMap<String, ManagedReference<ServerContainer>>();
        containerEqFeetList = dm.createReference(tempContainerEqFeetList);
        ScalableHashMap<String, ManagedReference<ServerContainer>> tempContainerEqShieldList = new ScalableHashMap<String, ManagedReference<ServerContainer>>();
        containerEqShieldList = dm.createReference(tempContainerEqShieldList);
        ScalableHashMap<String, ManagedReference<ServerContainer>> tempContainerEqAmuletList = new ScalableHashMap<String, ManagedReference<ServerContainer>>();
        containerEqAmuletList = dm.createReference(tempContainerEqAmuletList);
        ScalableHashMap<String, ManagedReference<ServerContainer>> tempContainerEqGloveList = new ScalableHashMap<String, ManagedReference<ServerContainer>>();
        containerEqGloveList = dm.createReference(tempContainerEqGloveList);
        ScalableHashMap<String, ManagedReference<ServerContainer>> tempContainerEqShirtList = new ScalableHashMap<String, ManagedReference<ServerContainer>>();
        containerEqShirtList = dm.createReference(tempContainerEqShirtList);
        ScalableHashMap<String, ManagedReference<ServerContainer>> tempContainerEqTrousersList = new ScalableHashMap<String, ManagedReference<ServerContainer>>();
        containerEqTrousersList = dm.createReference(tempContainerEqTrousersList);
    }

//    /**
//     * Adds an item to this room.
//     * 
//     * @param item the item to add to this room.
//     * @return {@code true} if the item was added to the room
//     */
//    public boolean addItem(WorldObject item) {
//        logger.log(Level.INFO, "{0} placed in {1}",
//            new Object[] { item, this });
//
//        // NOTE: we can't directly save the item in the list, or
//        // we'll end up with a local copy of the item. Instead, we
//        // must save a ManagedReference to the item.
//
//        DataManager dataManager = AppContext.getDataManager();
//        dataManager.markForUpdate(this);
//
//        return items.add(dataManager.createReference(item));
//    }
    
    
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
        entity.setWorldRoom(this);
        entities.get().put(refEnt.getId(),refEnt);
//        entitiesArray = new ArrayList<ManagedReference<ServerEntity>>(entities.values());
    }
    
    /** add itemID into room */
    public void addItem(BigInteger itemID, ServerItem serverItem) {
    	DataManager dataManager = AppContext.getDataManager();
        dataManager.markForUpdate(this);
//        ManagedReference<BigInteger> tempItemID = dataManager.createReference(itemID);
        if (maxItemIndex.compareTo(itemID) < 0) {
        	maxItemIndex = itemID;
        }
        ManagedReference<ServerItem> tempServerItem = dataManager.createReference(serverItem);
        if (!itemList.get().containsKey(itemID)) {
        	itemList.getForUpdate().put(itemID, tempServerItem);
//        	System.out.println("itemlist size: "+itemList.get().size());
        }
    }
    
    /** only remove the item for the players who cannot see the item */
    public void removeItem(BigInteger itemID) {
    	this.getItemList().getForUpdate().remove(itemID);
		for (BigInteger playerIds : players.keySet()) {
			/*** check if player does exist */
			if (players.get(playerIds) != null) {
				this.players.get(playerIds).get().getSession().send(ServerMessages.sendDelete(OBJECTCODE.ITEM, itemID));
			} else {
				// player does not exist
			}
		}
	}
    
    /** add itemID into room */
    public void addAreaInfluence(BigInteger areaInfluenceID, ServerAreaInfluence serverAreaInfluence) {
    	DataManager dataManager = AppContext.getDataManager();
        dataManager.markForUpdate(this);
        if (maxAreaInfluenceIndex.compareTo(areaInfluenceID) < 0) {
        	maxAreaInfluenceIndex = areaInfluenceID;
        }
//        System.out.println("room adds areaInfluence: "+areaInfluenceID.longValue());
//        areaInfluenceID = maxAreaInfluenceIndex.add(BigInteger.valueOf(1));
//        maxAreaInfluenceIndex = areaInfluenceID;
        
        ManagedReference<ServerAreaInfluence> tempServerAreaInfluence = dataManager.createReference(serverAreaInfluence);
        if (!areaInfluenceList.get().containsKey(areaInfluenceID)) {
        	/** prepare the areaInfluenceSendList */
        	if (!areaInfluenceSendList.get().containsKey(areaInfluenceID)) {
        		areaInfluenceSendList.getForUpdate().put(areaInfluenceID, tempServerAreaInfluence);
        	}
        	areaInfluenceList.getForUpdate().put(areaInfluenceID, tempServerAreaInfluence);
//        	System.out.println("areaInfluences: size: "+areaInfluenceList.get().size());
        	
        	ServerAreaInfluence e = serverAreaInfluence;
            e.setId(areaInfluenceID);
            WorldObject worldObject = e.getWorldObject().get();
            LocalObject localObject = new LocalObject(e.getId(), worldObject.getName(), e.getRoom(), worldObject.getX(), worldObject.getY(), worldObject.getWidth(), worldObject.getHeight(), worldObject.getMX(), worldObject.getMY(), worldObject.getZIndex(), worldObject.getSpeed());
            AreaInfluence areaInfluence = new AreaInfluence(e.getId(), e.getTimeBegin(), e.getTimeEnd(), localObject, e.getGroupName(), e.isExclusive(), e.getAttributes(), e.getPriority(), e.getCreator());
             
            /** send areaInfluence to all players */
            for (BigInteger playerIds : players.keySet()) {
     			/*** check if player does exist */
     			if (players.get(playerIds) != null) {
//     				System.out.println("send areaInfluence: "+areaInfluenceID.longValue()+" room = "+e.getRoom());
     				this.players.get(playerIds).get().getSession().send(ServerMessages.sendAreaInfluence(areaInfluence, maxAreaInfluenceIndex));
     			} else {
     				// player does not exist
     			}
     		}
            areaInfluenceSendList.getForUpdate().remove(areaInfluenceID);
        } else {
        	/** error: areaInfluence is already in the manager */
//        	System.out.println("Error: AreaInfluence is already in the manager!");
        }
        
       
    }
    
    /** only remove the item for the players who cannot see the item */
    public void removeAreaInfluence(BigInteger areaInfluenceID) {
    	this.getAreaInfluenceList().getForUpdate().remove(areaInfluenceID);
		for (BigInteger playerIds : players.keySet()) {
			/*** check if player does exist */
			if (players.get(playerIds) != null) {
				this.players.get(playerIds).get().getSession().send(ServerMessages.sendDelete(OBJECTCODE.AREAINFLUENCE, areaInfluenceID));
			} else {
				// player does not exist
			}
		}
	}
    
    
    /** add itemID into room */
    public void addGrowthSpot(BigInteger growthSpotID, ServerGrowthSpot serverGrowthSpot) {
    	DataManager dataManager = AppContext.getDataManager();
        dataManager.markForUpdate(this);
        if (maxGrowthSpotIndex.compareTo(growthSpotID) < 0) {
        	maxGrowthSpotIndex = growthSpotID;
        }
        
        ManagedReference<ServerGrowthSpot> tempServerGrowthSpot = dataManager.createReference(serverGrowthSpot);
        if (!growthSpotList.get().containsKey(growthSpotID)) {
        	/** prepare the growthSpotSendList */
        	if (!growthSpotSendList.get().containsKey(growthSpotID)) {
        		growthSpotSendList.getForUpdate().put(growthSpotID, tempServerGrowthSpot);
        	}
        	growthSpotList.getForUpdate().put(growthSpotID, tempServerGrowthSpot);
//        	System.out.println("areaInfluences: size: "+areaInfluenceList.get().size());
        	
        	ServerGrowthSpot e = serverGrowthSpot;
            e.setId(growthSpotID);
            
            GrowthSpot growthSpot = new GrowthSpot(e.getId(), e.getTileType(), e.getLocalX(), e.getLocalY(), e.getMapTileX(), e.getMapTileY(), e.getRoom(), e.getTimeBegin(), e.getTimeEnd(), e.getAttributes(), e.getCreator());
             
            /** send areaInfluence to all players */
            for (BigInteger playerIds : players.keySet()) {
     			/*** check if player does exist */
     			if (players.get(playerIds) != null) {
//     				System.out.println("send areaInfluence: "+areaInfluenceID.longValue()+" room = "+e.getRoom());
     				this.players.get(playerIds).get().getSession().send(ServerMessages.sendGrowthSpot(growthSpot, maxGrowthSpotIndex));
     			} else {
     				// player does not exist
     			}
     		}
            growthSpotSendList.getForUpdate().remove(growthSpotID);
        } else {
        	/** error: areaInfluence is already in the manager */
//        	System.out.println("Error: AreaInfluence is already in the manager!");
        }
        
       
    }
    
    /** only remove the item for the players who cannot see the item */
    public void removeGrowthSpot(BigInteger growthSpotID) {
    	this.getGrowthSpotList().getForUpdate().remove(growthSpotID);
		for (BigInteger playerIds : players.keySet()) {
			/*** check if player does exist */
			if (players.get(playerIds) != null) {
				this.players.get(playerIds).get().getSession().send(ServerMessages.sendDelete(OBJECTCODE.GROWTHSPOT, growthSpotID));
			} else {
				// player does not exist
			}
		}
	}
    
    /**
     * Adds an serverPlayer to this room.
     * 
     * @param add serverPlayer to this room.
     * @return {@code true} if the item was added to the room
     */
    public void addServerPlayer(ServerPlayer serverPlayer, String playerName, BigInteger playerID) {
    	DataManager dataManager = AppContext.getDataManager();
        dataManager.markForUpdate(this);
        
        ManagedReference<ServerPlayer> refPlayer = dataManager.createReference(serverPlayer);
//        BigInteger playerID = dataManager.getObjectId(serverPlayer);
        refPlayer.getForUpdate().setId(playerID);
    	logger.log(Level.INFO, "ServerPlayer {0} placed in {1}",
                    new Object[] { serverPlayer.getName(), this });
	        
        serverPlayer.setWorldRoom(this);
        serverPlayers.get().put(playerName,refPlayer);
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
//    	   entity.setHeight(state[4]);
//    	   entity.setWidth(state[5]);
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
	        
	        spaces.get().put(spaceID, refSpace);
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
       if ( spaces.get().containsKey(id)) {
    	   ServerSpace space = spaces.get().get(id).getForUpdate();
//    	   space.setX(state[0]);
//    	   space.setY(state[1]);
    	   space.setAllXY(state[0], state[1]);
    	   space.setMovement(state[2], state[3]);
//    	   space.setHeight(state[4]);
//    	   space.setWidth(state[5]);
    	   //entities.put(id, entity);
    	   return true;
       } else {
    	   return false;
       }
    	   
    }
    
    /**
     * Edits an entity in this room.
     * 
     * @param id: entity to edit
     * @return {@code true} if the entity was edited with success
     */
    public boolean editSpaceAddons(BigInteger id, String textureName, int[] rgb, float trans, int filled, float scale, float area) {
       if ( spaces.get().containsKey(id)) {
    	   ServerSpace space = spaces.get().get(id).getForUpdate();
//    	   space.setX(state[0]);
//    	   space.setY(state[1]);
    	   space.setTexture(textureName);
    	   space.setRGB(rgb);
    	   space.setTrans(trans);
    	   if (filled == 0) space.setFilled(false); else space.setFilled(true);
    	   space.scale(scale);
    	   space.setArea(area);
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

//        }       
	        String playerName = player.getName().substring(player.getName().indexOf(".")+1, player.getName().length());
	        
	        PlayerEntity playerEntity = null;
	        if (!serverPlayers.get().containsKey(playerName)) {
		        String serverSpriteString = GameStates.tileNameStandardPlayer;
		        Sprite sprite = SpriteManager.manager.getSprite(serverSpriteString);
		        ServerSprite s_sprite = new ServerSprite(serverSpriteString, sprite.getHeight(), sprite.getWidth());
		        ServerPlayer s_player;
		        String standardRoom = playerName + "0000";
		       	s_player = new ServerPlayer(s_sprite, playerName, standardRoom, refPlayer.getId(),playerName, GameStates.nameStandardPlayer, GameStates.tileNameStandardPlayer, 0, 0, 0, 0);
		       	s_player.setHairName("ShortBlondHair");
//		       	s_player.setName(playerName);
//		       	s_player.setTileSetPathName(GameStates.standardTileNamePlayer);
//		       	s_player.setTileSetName("shipTileName");
		        this.addServerPlayer(s_player, playerName, refPlayer.getId());
//		        ServerContainer playerContainer = new ServerContainer(GameStates.inventoryWidthPlayer, GameStates.inventoryHeightPlayer, OBJECTCODE.CONTAINER_MAIN);
		        this.containerList.getForUpdate().put(playerName, s_player.getInventory());
		        this.containerUseList.getForUpdate().put(playerName, s_player.getInventoryUse());
		        this.containerEqBodyList.getForUpdate().put(playerName, s_player.getInventoryEqBody());
		        this.containerEqWeaponList.getForUpdate().put(playerName, s_player.getInventoryEqWeapon());
		        this.containerEqHelmetList.getForUpdate().put(playerName, s_player.getInventoryEqHelmet());
		        this.containerEqFeetList.getForUpdate().put(playerName, s_player.getInventoryEqFeet());
		        this.containerEqShieldList.getForUpdate().put(playerName, s_player.getInventoryEqShield());
		        this.containerEqAmuletList.getForUpdate().put(playerName, s_player.getInventoryEqAmulet());
		        this.containerEqGloveList.getForUpdate().put(playerName, s_player.getInventoryEqGlove());
		        this.containerEqShirtList.getForUpdate().put(playerName, s_player.getInventoryEqShirt());
		        this.containerEqTrousersList.getForUpdate().put(playerName, s_player.getInventoryEqTrousers());
		        
		        String armorName = "";
		        Entity armorEntity = null;
		        String weaponName = "";
		        Entity weaponEntity = null;
		        String helmetName = "";
		        Entity helmetEntity = null;
		        String feetName = "";
		        Entity feetEntity = null;
		        String shieldName = "";
		        Entity shieldEntity = null;
		        String amuletName = "";
		        Entity amuletEntity = null;
		        String gloveName = "";
		        Entity gloveEntity = null;
		        String shirtName = "";
		        Entity shirtEntity = null;
		        String trousersName = "";
		        Entity trousersEntity = null;
		        String hairName = "ShortBlondHair";
		        if (this.getContainerEqBodyList().get().get(playerName).get().getItemList().values().iterator().hasNext()) {
		        	armorName = this.getContainerEqBodyList().get().get(playerName).get().getItemList().values().iterator().next().get().getName();
		        	armorEntity = new Entity(TileSetManager.manager.getTileSet(GameStates.itemEquipPrefix+s_player.getTileSetName()+armorName), GameStates.itemEquipPrefix+s_player.getTileSetName()+armorName, s_player.getRoom(), BigInteger.valueOf(0), 0, 0, GameStates.animationDelay);      	
		        }
		        if (this.getContainerEqWeaponList().get().get(playerName).get().getItemList().values().iterator().hasNext()) {
		        	weaponName = this.getContainerEqWeaponList().get().get(playerName).get().getItemList().values().iterator().next().get().getName();
		        	weaponEntity = new Entity(TileSetManager.manager.getTileSet(GameStates.itemEquipPrefix+s_player.getTileSetName()+weaponName), GameStates.itemEquipPrefix+s_player.getTileSetName()+weaponName, s_player.getRoom(), BigInteger.valueOf(0), 0, 0, GameStates.animationDelay);      	
		        }
		        if (this.getContainerEqHelmetList().get().get(playerName).get().getItemList().values().iterator().hasNext()) {
		        	helmetName = this.getContainerEqHelmetList().get().get(playerName).get().getItemList().values().iterator().next().get().getName();
		        	helmetEntity = new Entity(TileSetManager.manager.getTileSet(GameStates.itemEquipPrefix+s_player.getTileSetName()+helmetName), GameStates.itemEquipPrefix+s_player.getTileSetName()+helmetName, s_player.getRoom(), BigInteger.valueOf(0), 0, 0, GameStates.animationDelay);      	
		        }
		        if (this.getContainerEqFeetList().get().get(playerName).get().getItemList().values().iterator().hasNext()) {
		        	feetName = this.getContainerEqFeetList().get().get(playerName).get().getItemList().values().iterator().next().get().getName();
		        	feetEntity = new Entity(TileSetManager.manager.getTileSet(GameStates.itemEquipPrefix+s_player.getTileSetName()+feetName), GameStates.itemEquipPrefix+s_player.getTileSetName()+feetName,s_player.getRoom(),  BigInteger.valueOf(0), 0, 0, GameStates.animationDelay);      	
		        }
		        if (this.getContainerEqShieldList().get().get(playerName).get().getItemList().values().iterator().hasNext()) {
		        	shieldName = this.getContainerEqShieldList().get().get(playerName).get().getItemList().values().iterator().next().get().getName();
		        	shieldEntity = new Entity(TileSetManager.manager.getTileSet(GameStates.itemEquipPrefix+s_player.getTileSetName()+shieldName), GameStates.itemEquipPrefix+s_player.getTileSetName()+shieldName, s_player.getRoom(), BigInteger.valueOf(0), 0, 0, GameStates.animationDelay);      	
		        }
		        if (this.getContainerEqAmuletList().get().get(playerName).get().getItemList().values().iterator().hasNext()) {
		        	amuletName = this.getContainerEqAmuletList().get().get(playerName).get().getItemList().values().iterator().next().get().getName();
		        	amuletEntity = new Entity(TileSetManager.manager.getTileSet(GameStates.itemEquipPrefix+s_player.getTileSetName()+amuletName), GameStates.itemEquipPrefix+s_player.getTileSetName()+amuletName,s_player.getRoom(),  BigInteger.valueOf(0), 0, 0, GameStates.animationDelay);      	
		        }
		        if (this.getContainerEqGloveList().get().get(playerName).get().getItemList().values().iterator().hasNext()) {
		        	gloveName = this.getContainerEqGloveList().get().get(playerName).get().getItemList().values().iterator().next().get().getName();
		        	gloveEntity = new Entity(TileSetManager.manager.getTileSet(GameStates.itemEquipPrefix+s_player.getTileSetName()+gloveName), GameStates.itemEquipPrefix+s_player.getTileSetName()+gloveName,s_player.getRoom(),  BigInteger.valueOf(0), 0, 0, GameStates.animationDelay);      	
		        }
		        if (this.getContainerEqShirtList().get().get(playerName).get().getItemList().values().iterator().hasNext()) {
		        	shirtName = this.getContainerEqShirtList().get().get(playerName).get().getItemList().values().iterator().next().get().getName();
		        	shirtEntity = new Entity(TileSetManager.manager.getTileSet(GameStates.itemEquipPrefix+s_player.getTileSetName()+shirtName), GameStates.itemEquipPrefix+s_player.getTileSetName()+shirtName, s_player.getRoom(), BigInteger.valueOf(0), 0, 0, GameStates.animationDelay);      	
		        }
		        if (this.getContainerEqTrousersList().get().get(playerName).get().getItemList().values().iterator().hasNext()) {
		        	trousersName = this.getContainerEqTrousersList().get().get(playerName).get().getItemList().values().iterator().next().get().getName();
		        	trousersEntity = new Entity(TileSetManager.manager.getTileSet(GameStates.itemEquipPrefix+s_player.getTileSetName()+trousersName), GameStates.itemEquipPrefix+s_player.getTileSetName()+trousersName, s_player.getRoom(), BigInteger.valueOf(0), 0, 0, GameStates.animationDelay);      	
		        }

		        TileSet tile = TileSetManager.manager.getTileSet(s_player.getTileSetName());
		        playerEntity = new PlayerEntity(tile, playerName, s_player.getRoom(), player.getId(), player.getX(), player.getY(), GameStates.animationDelay);
		        playerEntity.setArmorEntity(armorEntity);
		        playerEntity.setWeaponEntity(weaponEntity);
		        playerEntity.setHelmetEntity(helmetEntity);
		        playerEntity.setFeetEntity(feetEntity);
		        playerEntity.setShieldEntity(shieldEntity);
		        playerEntity.setAmuletEntity(amuletEntity);
		        playerEntity.setGloveEntity(gloveEntity);
		        playerEntity.setShirtEntity(shirtEntity);
		        playerEntity.setTrousersEntity(trousersEntity);
		        playerEntity.updateVisualsHair(hairName);
		        
	        } else {
	        	logger.log(Level.INFO, "{0} with ID {1} exists already in room {2}",
	                    new Object[] { playerName, refPlayer.getId(), this });
	        	ServerPlayer s_player = serverPlayers.get().get(playerName).get();
	        	s_player.setId(refPlayer.getId());

	        	String armorName = "";
		        Entity armorEntity = null;
		        String weaponName = "";
		        Entity weaponEntity = null;
		        String helmetName = "";
		        Entity helmetEntity = null;
		        String feetName = "";
		        Entity feetEntity = null;
		        String shieldName = "";
		        Entity shieldEntity = null;
		        String amuletName = "";
		        Entity amuletEntity = null;
		        String gloveName = "";
		        Entity gloveEntity = null;
		        String shirtName = "";
		        Entity shirtEntity = null;
		        String trousersName = "";
		        Entity trousersEntity = null;
		        if (this.getContainerEqBodyList().get().get(playerName).get().getItemList().values().iterator().hasNext()) {
		        	armorName = this.getContainerEqBodyList().get().get(playerName).get().getItemList().values().iterator().next().get().getName();
		        	armorEntity = new Entity(TileSetManager.manager.getTileSet(GameStates.itemEquipPrefix+s_player.getTileSetName()+armorName), GameStates.itemEquipPrefix+s_player.getTileSetName()+armorName, s_player.getRoom(), BigInteger.valueOf(0), 0, 0, GameStates.animationDelay);      	
		        }
		        if (this.getContainerEqWeaponList().get().get(playerName).get().getItemList().values().iterator().hasNext()) {
		        	weaponName = this.getContainerEqWeaponList().get().get(playerName).get().getItemList().values().iterator().next().get().getName();
		        	weaponEntity = new Entity(TileSetManager.manager.getTileSet(GameStates.itemEquipPrefix+s_player.getTileSetName()+weaponName), GameStates.itemEquipPrefix+s_player.getTileSetName()+weaponName, s_player.getRoom(), BigInteger.valueOf(0), 0, 0, GameStates.animationDelay);      	
		        }
		        if (this.getContainerEqHelmetList().get().get(playerName).get().getItemList().values().iterator().hasNext()) {
		        	helmetName = this.getContainerEqHelmetList().get().get(playerName).get().getItemList().values().iterator().next().get().getName();
		        	helmetEntity = new Entity(TileSetManager.manager.getTileSet(GameStates.itemEquipPrefix+s_player.getTileSetName()+helmetName), GameStates.itemEquipPrefix+s_player.getTileSetName()+helmetName,s_player.getRoom(),  BigInteger.valueOf(0), 0, 0, GameStates.animationDelay);      	
		        }
		        if (this.getContainerEqFeetList().get().get(playerName).get().getItemList().values().iterator().hasNext()) {
		        	feetName = this.getContainerEqFeetList().get().get(playerName).get().getItemList().values().iterator().next().get().getName();
		        	feetEntity = new Entity(TileSetManager.manager.getTileSet(GameStates.itemEquipPrefix+s_player.getTileSetName()+feetName), GameStates.itemEquipPrefix+s_player.getTileSetName()+feetName,s_player.getRoom(),  BigInteger.valueOf(0), 0, 0, GameStates.animationDelay);      	
		        }
		        if (this.getContainerEqShieldList().get().get(playerName).get().getItemList().values().iterator().hasNext()) {
		        	shieldName = this.getContainerEqShieldList().get().get(playerName).get().getItemList().values().iterator().next().get().getName();
		        	shieldEntity = new Entity(TileSetManager.manager.getTileSet(GameStates.itemEquipPrefix+s_player.getTileSetName()+shieldName), GameStates.itemEquipPrefix+s_player.getTileSetName()+shieldName, s_player.getRoom(), BigInteger.valueOf(0), 0, 0, GameStates.animationDelay);      	
		        }
		        if (this.getContainerEqAmuletList().get().get(playerName).get().getItemList().values().iterator().hasNext()) {
		        	amuletName = this.getContainerEqAmuletList().get().get(playerName).get().getItemList().values().iterator().next().get().getName();
		        	amuletEntity = new Entity(TileSetManager.manager.getTileSet(GameStates.itemEquipPrefix+s_player.getTileSetName()+amuletName), GameStates.itemEquipPrefix+s_player.getTileSetName()+amuletName, s_player.getRoom(), BigInteger.valueOf(0), 0, 0, GameStates.animationDelay);      	
		        }
		        if (this.getContainerEqGloveList().get().get(playerName).get().getItemList().values().iterator().hasNext()) {
		        	gloveName = this.getContainerEqGloveList().get().get(playerName).get().getItemList().values().iterator().next().get().getName();
		        	gloveEntity = new Entity(TileSetManager.manager.getTileSet(GameStates.itemEquipPrefix+s_player.getTileSetName()+gloveName), GameStates.itemEquipPrefix+s_player.getTileSetName()+gloveName, s_player.getRoom(), BigInteger.valueOf(0), 0, 0, GameStates.animationDelay);      	
		        }
		        if (this.getContainerEqShirtList().get().get(playerName).get().getItemList().values().iterator().hasNext()) {
		        	shirtName = this.getContainerEqShirtList().get().get(playerName).get().getItemList().values().iterator().next().get().getName();
		        	shirtEntity = new Entity(TileSetManager.manager.getTileSet(GameStates.itemEquipPrefix+s_player.getTileSetName()+shirtName), GameStates.itemEquipPrefix+s_player.getTileSetName()+shirtName, s_player.getRoom(), BigInteger.valueOf(0), 0, 0, GameStates.animationDelay);      	
		        }
		        if (this.getContainerEqTrousersList().get().get(playerName).get().getItemList().values().iterator().hasNext()) {
		        	trousersName = this.getContainerEqTrousersList().get().get(playerName).get().getItemList().values().iterator().next().get().getName();
		        	trousersEntity = new Entity(TileSetManager.manager.getTileSet(GameStates.itemEquipPrefix+s_player.getTileSetName()+trousersName), GameStates.itemEquipPrefix+s_player.getTileSetName()+trousersName, s_player.getRoom(), BigInteger.valueOf(0), 0, 0, GameStates.animationDelay);      	
		        }
	        	
		        
	        	TileSet tile = TileSetManager.manager.getTileSet(s_player.getTileSetName());//TileSetManager.manager.getTileSetByPath(s_player.getTileSetName(), s_player.getTileSetPathName());
		        playerEntity = new PlayerEntity(tile, playerName, s_player.getRoom(), s_player.getId(), s_player.getX(), s_player.getY(), GameStates.animationDelay);
		        
		        playerEntity.setArmorEntity(armorEntity);
		        playerEntity.setWeaponEntity(weaponEntity);
		        playerEntity.setHelmetEntity(helmetEntity);
		        playerEntity.setFeetEntity(feetEntity);
		        playerEntity.setShieldEntity(shieldEntity);
		        playerEntity.setAmuletEntity(amuletEntity);
		        playerEntity.setGloveEntity(gloveEntity);
		        playerEntity.setShirtEntity(shirtEntity);
		        playerEntity.setTrousersEntity(trousersEntity);
		        playerEntity.updateVisualsHair(s_player.getHairName());
		        
//		        ServerContainer s_container = containerList.get().get(playerName).get();
//		        playerEntity.setInventory(new Con);
//		        System.out.println(s_player.getX());
	        }
//	        ServerPlayer serverPlayer = new ServerPlayer(sprite, playerID, millis, last, frames, duration)
//	        this.addServerPlayer(serverPlayer, refPlayer.getId());
	        
	        
	        
//	        System.out.println("playerID at add: "+player.getId());
//	        Entity playerEntity = new Entity("ship.png", playerID, player.getX(), player.getY(), player.getMX(), player.getMY());
	        
	        if (PlayerManager.add(playerEntity)) {
	        	logger.log(Level.INFO, "{0} is added to the PlayerManager",
	                    new Object[] { player});
	        } else
	        {
	        	logger.log(Level.INFO, "{0} is NOT added to the PlayerManager",
	                    new Object[] { player});
	        }
	        
	        if (ServerTextureManager.manager.containsPlayer(playerName)) {
	        	logger.log(Level.INFO, "Player deleted from ServerTextureManager: {0}", player.getName());
	        	ServerTextureManager.manager.removePlayer(playerName);
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
//    	   player.setHeight(state[4]);
//    	   player.setWidth(state[5]);
    	   
    	   /** PlayerManager update */
    	   PlayerManager.get(id).setX(state[0]);
    	   PlayerManager.get(id).setY(state[1]);
    	   PlayerManager.get(id).setMovement(state[2], state[3]);
    	   
    	   String playerName = PlayerManager.get(id).getName();
    	   /** ServerPlayers update */
    	   ServerPlayer changePlayer = serverPlayers.get().get(playerName).getForUpdate();
    	   changePlayer.setX(state[0]);
    	   changePlayer.setY(state[1]);
    	   changePlayer.setMovement(state[2], state[3]);
//    	   System.out.println(state[0]);
//    	   PlayerManager.get(id).setHeight(state[4]);
//    	   PlayerManager.get(id).setWidth(state[5]);
    	   //entities.put(id, entity);
    	   return true;
       } else {
    	   return false;
       }
    	   
    }
    
    /**
     * Edits an entity in this room.
     * 
     * @param id: entity to edit
     * @return {@code true} if the entity was edited with success
     */
    public boolean editPlayerAddons(BigInteger playerId, String playerName, String tileName, String tilePathName, int tileWidth, int tileHeight, String country, String groupName, int experience) {
       if ( serverPlayers.get().containsKey(playerName)) {
    	   /** ServerPlayers update */
    	   ServerPlayer changePlayer = serverPlayers.get().get(playerName).getForUpdate();
    	   changePlayer.setId(playerId);
    	   changePlayer.setTileSetName(tileName);
    	   changePlayer.setTileSetPathName(tilePathName);
    	   changePlayer.setWidth(tileWidth);
    	   changePlayer.setHeight(tileHeight);
    	   changePlayer.setCountry(country);
    	   changePlayer.setGroupName(groupName);
    	   changePlayer.setExperience(experience);
//    	   changePlayer.setMovement(state[2], state[3]);
//    	   System.out.println(state[0]);
//    	   PlayerManager.get(id).setHeight(state[4]);
//    	   PlayerManager.get(id).setWidth(state[5]);
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
	
//	        player.emptySpaceChannels();	        
	//        return players.get(player.getId()).remove(dataManager.createReference(player));
	        players.remove(player.getId());
	        PlayerManager.remove(player.getId());
	        this.updateSendLogout(player.getId());
	        this.playerInitializingEntities.remove(player.getId());
	        this.playerInitializingItems.remove(player.getId());
	        this.playerInitializingAreaInfluences.remove(player.getId());
	        this.playerInitializingGrowthSpots.remove(player.getId());
	        String playerName = player.getName().substring(player.getName().indexOf(".")+1, player.getName().length());
	        if (ServerTextureManager.manager.containsPlayer(playerName)) {
	        	logger.log(Level.INFO, "Player deleted from ServerTextureManager: {0}", player.getName());
	        	ServerTextureManager.manager.removePlayer(playerName);
	        }
	        return true;
        } else {
        	logger.log(Level.INFO, "{0} with ID={1} was not found inside the HashMap players",
                    new Object[] { player, player.getId() });
        	return false;
        }
    }

    public void removeAllPlayers() {
    	players.clear();
    	PlayerManager.emptyAll();
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
    	for (BigInteger spaceId : spaces.get().keySet()) {
			ServerSpace s_space = spaces.get().get(spaceId).get();
			realSpace = new Space(s_space.getPolygon(), (int) s_space.getX(),(int)  s_space.getY(), s_space.getName(), spaceId, s_space.getRGB(), s_space.isFilled(), s_space.getTrans(), s_space.getScale(), s_space.getArea(), s_space.getTextureName());
//					SpaceManager.get(SpaceManager.idList.get(i));
			objectList[objectCounter] = realSpace;
			objectCounter++;
		}
    	return objectList;    	
    }
    
    /** return Spaces*/
    public PlayerEntity[] getPlayers(WorldPlayer worldPlayer, int begin, int end) {
    	PlayerEntity[] objectList = new PlayerEntity[end-begin];
    	int objectCounter = 0;
    	PlayerEntity realPlayer;
    	/** for each entity add the corresponding id to the intList */
    	for (int i = begin; i<end; i++) {
//    		PlayerManager.updatePlayer(this.getId(), objectX, objectY, objectMX, objectMY);
//			getRoom().editPlayer(this.getId(), new float[]{objectX, objectY, objectMX, objectMY});
    		realPlayer = PlayerManager.get(PlayerManager.idList.get(i));
//    		realPlayer.setId(serverPlayers.get().get(realPlayer.getName()).getId());
//    		System.out.println("ID: "+PlayerManager.idList.get(i)+" idlist-length = "+PlayerManager.idList.size());
			objectList[objectCounter] = realPlayer;
			objectCounter++;
		}
    	return objectList;    	
    }
    
    /** return Entities*/
    public Entity[] getEntities(WorldPlayer worldPlayer, int begin, int end, Iterator<BigInteger> itKeys) {
    	Entity[] objectList = new Entity[end-begin];
    	int objectCounter = 0;
    	/** for each entity add the corresponding id to the intList */
//    	entitiesArray = new ArrayList<ManagedReference<ServerEntity>>(entities);
		Entity realEntity;
//		Iterator<BigInteger> itKeys = entities.get().keySet().iterator();
		BigInteger actualID;
//		for(int i = 0; i< begin; i++) {
//			actualID = itKeys.next();
//		}
//    	for (BigInteger entId : entities.get().keySet()) {
    	for (int i = begin; i<end; i++) {
    		if (itKeys.hasNext()) {
    			actualID = itKeys.next();
				ServerEntity entity = entities.get().get(actualID).get();
				TileSet tileSet = TileSetManager.manager.getTileSetByPath(entity.getTileSetName(), entity.getTileSetPathName());
				realEntity = new Entity(tileSet, entity.getName(), worldPlayer.getRoom(), entity.getId(), entity.getX(), entity.getY(), GameStates.animationDelay);
				objectList[objectCounter] = realEntity;
				objectCounter++;
    		}
		}
    	return objectList;    	
    }
    
    /** return next count Entities*/
    public Entity[] getNextCountEntities(int count, Iterator<BigInteger> itKeys) {
    	Entity[] objectListTemp = new Entity[count];
    	int objectCounter = 0;
    	/** for each entity add the corresponding id to the intList */
		Entity realEntity;
		BigInteger actualID;
    	for (int i = 0; i<count; i++) {
    		if (itKeys.hasNext()) {
    			actualID = itKeys.next();
				ServerEntity entity = entities.get().get(actualID).get();
				TileSet tileSet = TileSetManager.manager.getTileSetByPath(entity.getTileSetName(), entity.getTileSetPathName());
				realEntity = new Entity(tileSet, entity.getName(), entity.getRoom(),  entity.getId(), entity.getX(), entity.getY(), GameStates.animationDelay);
				objectListTemp[objectCounter] = realEntity;
				objectCounter++;
    		} else {
    			Entity[] objectList = new Entity[objectCounter];
    			for (int j = 0; j < objectCounter; j++){
    				objectList[j] = objectListTemp[j];
    			}
    			return objectList;
    		}
		}
    	return objectListTemp;    	
    }
    
    /** return next count Entities*/
    public Item[] getNextCountItems(int count, Iterator<BigInteger> itKeys) {
    	Item[] objectListTemp = new Item[count];
    	int objectCounter = 0;
    	/** for each entity add the corresponding id to the intList */
		Item realItem;
		BigInteger actualID;
    	for (int i = 0; i<count; i++) {
    		if (itKeys.hasNext()) {
    			actualID = itKeys.next();
				ServerItem item = itemList.get().get(actualID).get();
//				System.out.println("got item id "+item.getId());
//				TileSet tileSet = TileSetManager.manager.getTileSetByPath(item.getItemEntity().get().getTileSetName(), item.getItemEntity().get().getTileSetPathName());
				realItem = ItemFactoryCollection.getItem(item.getItemCode(), item.getId(), item.getName(), item.getComment(), item.getRoom(), item.getCount(), item.getItemEntity().get().getX(), item.getItemEntity().get().getY(), item.getCreationTime(), item.getStates());
				objectListTemp[objectCounter] = realItem;
				objectCounter++;
    		} else {
    			Item[] objectList = new Item[objectCounter];
    			for (int j = 0; j < objectCounter; j++){
    				objectList[j] = objectListTemp[j];
    			}
    			return objectList;
    		}
		}
    	return objectListTemp;    	
    }
    
    /** return next count AreaInfluence*/
    public AreaInfluence[] getNextCountAreaInfluences(int count, Iterator<BigInteger> itKeys) {
    	AreaInfluence[] objectListTemp = new AreaInfluence[count];
    	int objectCounter = 0;
    	/** for each entity add the corresponding id to the intList */
    	AreaInfluence realAreaInfluence;
		BigInteger actualID;
    	for (int i = 0; i<count; i++) {
    		if (itKeys.hasNext()) {
    			actualID = itKeys.next();
				ServerAreaInfluence areaInfluence = areaInfluenceList.get().get(actualID).get();
				WorldObject worldObject = areaInfluence.getWorldObject().get();
//				System.out.println("got item id "+item.getId());
//				TileSet tileSet = TileSetManager.manager.getTileSetByPath(item.getItemEntity().get().getTileSetName(), item.getItemEntity().get().getTileSetPathName());
				realAreaInfluence = new AreaInfluence(areaInfluence.getId(), areaInfluence.getTimeBegin(), areaInfluence.getTimeEnd(), new LocalObject(areaInfluence.getId(), worldObject.getName(), worldObject.getRoom(), worldObject.getX(), worldObject.getY(), worldObject.getHeight(), worldObject.getWidth(), worldObject.getMX(), worldObject.getMY(), worldObject.getZIndex(), worldObject.getSpeed()), areaInfluence.getGroupName(), areaInfluence.isExclusive(), areaInfluence.getAttributes(), areaInfluence.getPriority(), areaInfluence.getCreator());
//						AreaInfluence.getAreaInfluence(areaInfluence.getItemCode(), item.getId(), item.getName(), item.getCount(), item.getCapacity(), item.getItemEntity().get().getX(), item.getItemEntity().get().getY(), item.getCreationTime(), item.getStates());
				objectListTemp[objectCounter] = realAreaInfluence;
				objectCounter++;
    		} else {
    			AreaInfluence[] objectList = new AreaInfluence[objectCounter];
    			for (int j = 0; j < objectCounter; j++){
    				objectList[j] = objectListTemp[j];
    			}
    			return objectList;
    		}
		}
    	return objectListTemp;    	
    }
    
    /** return next count AreaInfluence*/
    public GrowthSpot[] getNextCountGrowthSpots(int count, Iterator<BigInteger> itKeys) {
    	GrowthSpot[] objectListTemp = new GrowthSpot[count];
    	int objectCounter = 0;
    	/** for each entity add the corresponding id to the intList */
    	GrowthSpot realGrowthSpot;
		BigInteger actualID;
    	for (int i = 0; i<count; i++) {
    		if (itKeys.hasNext()) {
    			actualID = itKeys.next();
				ServerGrowthSpot growthSpot = growthSpotList.get().get(actualID).get();
//				System.out.println("got item id "+item.getId());
//				TileSet tileSet = TileSetManager.manager.getTileSetByPath(item.getItemEntity().get().getTileSetName(), item.getItemEntity().get().getTileSetPathName());
				realGrowthSpot = new GrowthSpot(growthSpot.getId(), growthSpot.getTileType(), growthSpot.getLocalX(), growthSpot.getLocalY(),growthSpot.getMapTileX(), growthSpot.getMapTileY(), growthSpot.getRoom(), growthSpot.getTimeBegin(), growthSpot.getTimeEnd(),growthSpot.getAttributes(),growthSpot.getCreator());
//						AreaInfluence.getAreaInfluence(areaInfluence.getItemCode(), item.getId(), item.getName(), item.getCount(), item.getCapacity(), item.getItemEntity().get().getX(), item.getItemEntity().get().getY(), item.getCreationTime(), item.getStates());
				objectListTemp[objectCounter] = realGrowthSpot;
				objectCounter++;
    		} else {
    			GrowthSpot[] objectList = new GrowthSpot[objectCounter];
    			for (int j = 0; j < objectCounter; j++){
    				objectList[j] = objectListTemp[j];
    			}
    			return objectList;
    		}
		}
    	return objectListTemp;    	
    }
    
    public ManagedReference<ScalableHashMap<BigInteger, ManagedReference<ServerEntity>>> getEntities() {
    	return entities;
    }
    
    public ManagedReference<ScalableHashMap<BigInteger, ManagedReference<ServerSpace>>> getSpaces() {
    	return spaces;
    }
    
    
    /**
     * Updates all objects in the WorldRoom
     * @param delta: time that passed until the last update-process
     */
    public void updateEntities(int begin, int end, Iterator<BigInteger> itKeys) {
    	long timestamp;
//		entitiesArray = new ArrayList<ManagedReference<ServerEntity>>(entities.values());
//    	Iterator<BigInteger> itKeys = entities.get().keySet().iterator();
    	BigInteger actualID;
//    	for(int i = 0; i< begin; i++) {
//    		actualID = itKeys.next();
//    	}
    	
//		Entity realEntity;
//		for (BigInteger entId : entities.get().keySet()) {
		for (int i = begin; i < end; i++ ) {
			if (itKeys.hasNext()) {
				actualID = itKeys.next();
	//			ManagedReference<ServerEntity> entity = entitiesArray.get(i);
				ServerEntity se = entities.get().get(actualID).getForUpdate(); 
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
				
	//			realEntity = new Entity(se.getName(), se.getId(), se.getX(), se.getY(), se.getHorizontalMovement(), se.getVerticalMovement());
	//    		if ( EntityManager.overwrite(realEntity) ) {
	////    			logger.log(Level.INFO, "");//, new Object[] {se.getY(),  GameStates.getHeight()});
	//    		} else {
	//    			logger.log(Level.INFO, "Overwrite error - space is not existent!");
	//    		}
			}
    	}
    	
    }
    
    /**
     * Updates all objects in the WorldRoom
     * @param delta: time that passed until the last update-process
     */
    public void updateItems(int begin, int end, Iterator<BigInteger> itKeys) {
    	long timestamp;
//		entitiesArray = new ArrayList<ManagedReference<ServerEntity>>(entities.values());
//    	Iterator<BigInteger> itKeys = entities.get().keySet().iterator();
    	BigInteger actualID;
//    	for(int i = 0; i< begin; i++) {
//    		actualID = itKeys.next();
//    	}
    	
//		Entity realEntity;
//		for (BigInteger entId : entities.get().keySet()) {
		for (int i = begin; i < end; i++ ) {
			if (itKeys.hasNext()) {
				actualID = itKeys.next();
	//			ManagedReference<ServerEntity> entity = entitiesArray.get(i);
				ServerItem si = itemList.get().get(actualID).getForUpdate(); 
	//					EntityManager.get(EntityManager.idList.get(i)); 
	//    		ServerEntity se = entity.getForUpdate();
	    		
	    		timestamp = System.currentTimeMillis();
	    		if (timestamp - si.getCreationTime() > GameStates.maximumLifeDurationItems) {
//	    			System.out.println("difference: " + (timestamp - si.getCreationTime()));
	    			removeItem(si.getId());
	    		}
	    		
			}
    	}
    }
    
    /**
     * Updates all objects in the WorldRoom
     * @param delta: time that passed until the last update-process
     */
    public void updateAreaInfluences(int begin, int end, Iterator<BigInteger> itKeys) {
    	long timeNow;
    	BigInteger actualID;
//    	System.out.println("ServerAreaInfluenceThread running, size = "+ areaInfluenceList.get().size());
    	List<BigInteger> deleteIDList = new ArrayList<BigInteger>();
		for (int i = begin; i < end; i++ ) {
//			System.out.println("updateAreInfluence: i="+i);
			if (itKeys.hasNext()) {
				actualID = itKeys.next();
				ServerAreaInfluence sae = areaInfluenceList.get().get(actualID).get(); 
//				System.out.println("ServerAreaInfluence: group="+sae.getGroupName()+" exclusive?-"+sae.isExclusive());
	    		
	    		timeNow = System.currentTimeMillis();
//	    		if ( ((sae.getTimeBegin() <= timeNow) && (sae.getTimeEnd() >= timeNow)) || (sae.getTimeBegin() == sae.getTimeEnd())) {
//	    			/** active areaInfluence **/
//	    		} else {
//	    			/** areaInfluence is not yet active */
//	    		}
	    		
	    		/** finally remove the instantaneous influence (if applicable) or ended influence*/
	    		if ( (sae.getTimeBegin() == sae.getTimeEnd()) && timeNow-sae.getTimeEnd() > GameStates.timespanDeleteInstantAreaInfluence) {
	    			/** instantaneous influence */
	    			deleteIDList.add(sae.getId());
	    		} else if (sae.getTimeEnd() < timeNow) {
	    			/** ended continuous influence */
	    			deleteIDList.add(sae.getId());
	    		}
	    		
			} else {
//				System.out.println("Update AreaInfluence: no next id");
			}
    	}
		
		/** delete all invalid areaInfluences */
		for ( BigInteger delID : deleteIDList) {
			/** only remove the areaInfluence if it has been sent to all players before */
			if (!areaInfluenceSendList.get().containsKey(delID)) {
//				System.out.println("UpdateAreInfluenceThread: removed areaInfluence with ID="+delID.longValue());
				removeAreaInfluence(delID);
			}
		}
		
		if (areaInfluenceList.get().size() <=0) {
			this.setMaxAreaInfluenceIndex(BigInteger.valueOf(0));
		}
    }
    
    public void updateGrowthSpots(int begin, int end, Iterator<BigInteger> itKeys) {
    	long timeNow;
    	BigInteger actualID;
    	List<BigInteger> deleteIDList = new ArrayList<BigInteger>();
		for (int i = begin; i < end; i++ ) {
//			System.out.println("updateAreInfluence: i="+i);
			if (itKeys.hasNext()) {
				actualID = itKeys.next();
				ServerGrowthSpot sgs = growthSpotList.get().get(actualID).get(); 
//				System.out.println("ServerAreaInfluence: group="+sae.getGroupName()+" exclusive?-"+sae.isExclusive());
	    		
	    		timeNow = System.currentTimeMillis();
//	    		if ( ((sae.getTimeBegin() <= timeNow) && (sae.getTimeEnd() >= timeNow)) || (sae.getTimeBegin() == sae.getTimeEnd())) {
//	    			/** active areaInfluence **/
//	    		} else {
//	    			/** areaInfluence is not yet active */
//	    		}
	    		
	    		/** finally remove the instantaneous influence (if applicable) or ended influence*/
	    		if ( (sgs.getTimeBegin() == sgs.getTimeEnd()) && timeNow-sgs.getTimeEnd() > GameStates.timespanDeleteInstantGrowthSpot) {
	    			/** instantaneous influence */
	    			deleteIDList.add(sgs.getId());
	    		} else if (sgs.getTimeEnd() < timeNow) {
	    			/** ended continuous influence */
	    			deleteIDList.add(sgs.getId());
	    		}
	    		
			} else {
//				System.out.println("Update AreaInfluence: no next id");
			}
    	}
		
		/** delete all invalid areaInfluences */
		for ( BigInteger delID : deleteIDList) {
			/** only remove the areaInfluence if it has been sent to all players before */
			if (!growthSpotSendList.get().containsKey(delID)) {
//				System.out.println("UpdateAreInfluenceThread: removed areaInfluence with ID="+delID.longValue());
				removeGrowthSpot(delID);
			}
		}
		
		if (areaInfluenceList.get().size() <=0) {
			this.setMaxAreaInfluenceIndex(BigInteger.valueOf(0));
		}
    }
    
    public void updateSpaces() {
    	long timestamp;
//    	spacesArray = new ArrayList<ManagedReference<ServerSpace>>(spaces.values());

//		for (int i = begin; i<end; i++) {
    	for (BigInteger spaceId : spaces.get().keySet()) {
//			ManagedReference<ServerSpace> space = spacesArray.get(i);
//    		ServerSpace s_space = new ServerSpace(SpaceManager.get(SpaceManager.idList.get(i)));
    		ServerSpace s_space = spaces.get().get(spaceId).getForUpdate();//space.getForUpdate();
    		
    		if (s_space instanceof ServerRegion) {
    			/** update dimensions */
    			// do nothing at all
//    			Space realSpace = new Space(s_space.getPolygon(), (int) s_space.getX(), (int) s_space.getY(), s_space.getName(), s_space.getId(), s_space.getRGB(), s_space.isFilled(), s_space.getTrans(), s_space.getScale(), s_space.getArea(), s_space.getTextureName());
//    			if ( SpaceManager.overwrite(realSpace) ) {
////        			logger.log(Level.INFO, "");//, new Object[] {se.getY(),  GameStates.getHeight()});
//        		} else {
//        			logger.log(Level.INFO, "Overwrite error - space is not existent!");
//        		}
    		} else {
    			timestamp = System.currentTimeMillis();
        		/** Spaces can move without limitations (_IF_ they move) */
//        		if(s_space.getHorizontalMovement() != 0 || s_space.getVerticalMovement()!=0) 
        		s_space.move(timestamp);
//        		Space realSpace = new Space(s_space.getPolygon(), (int) s_space.getX(), (int) s_space.getY(), s_space.getName(), s_space.getId(), s_space.getRGB(), s_space.isFilled(), s_space.getTrans(), s_space.getScale(), s_space.getArea(), s_space.getTextureName());
//        		if ( SpaceManager.overwrite(realSpace) ) {
////        			logger.log(Level.INFO, "");//, new Object[] {se.getY(),  GameStates.getHeight()});
//        		} else {
//        			logger.log(Level.INFO, "Overwrite error - space is not existent!");
//        		}
    		}
    		
    	}
    }

    /** send all Entities to all Players */
    public void updateSendPlayersEntities(int begin, int end, Iterator<BigInteger> itKeys) {
//    	Iterator<BigInteger> itKeys = entities.get().keySet().iterator();
    	BigInteger actualID; 
//    	for (int i = 0; i< begin; i++) {
//    		actualID = itKeys.next();
//    	}
    	for ( int i = begin; i < end; i++) {
//    	for (BigInteger entId : entities.get().keySet()) {
    		if ( itKeys.hasNext()) {
    			actualID = itKeys.next();
				ServerEntity entity = entities.get().get(actualID).get();//entitiesArray.get(i);
				for (BigInteger playerID : players.keySet()) {
					if (players.get(playerID).get().isReady()) {
		    			BigInteger object_id = entity.getId();
		    			// get the six object-states: x,y,mx,my,width,height
		    			float[] object_state = new float[]{entity.getX(), entity.getY(),entity.getHorizontalMovement(),entity.getVerticalMovement(), entity.getWidth(), entity.getHeight()};
		    			players.get(playerID).get().getSession().send(ServerMessages.sendObjectState(OBJECTCODE.ENTITY, object_id, object_state));
		    			
	    			}
				}
//			if ( itKeys.hasNext()) actualID = itKeys.next();
    		}
    	}
    	
//    	long durationOfUpdate = System.currentTimeMillis()-last;
//    	logger.log(Level.INFO, "sending needs {0} millis",
//                new Object[] { durationOfUpdate });
    }

    
    public void updateSendPlayersSpaces() {
//    	for (int i = begin; i<end; i++) {
    	for (BigInteger spaceId : spaces.get().keySet()) {
//    		ServerSpace space = new ServerSpace(SpaceManager.get(SpaceManager.idList.get(i)));
    		ServerSpace space = spaces.get().get(spaceId).get(); //spacesArray.get(i);
			for (BigInteger playerID : players.keySet()) {
				if (players.get(playerID).get().isReady() && players.get(playerID).get().getSession().isConnected()) {
	    			BigInteger object_id = space.getId();
	    			// get the six object-states: x,y,mx,my,width,height
	    			float[] object_state = new float[]{space.getX(), space.getY(),space.getHorizontalMovement(),space.getVerticalMovement(), space.getWidth(), space.getHeight()};
	    			players.get(playerID).get().getSession().send(ServerMessages.sendObjectState(OBJECTCODE.SPACE, object_id, object_state));
    			}
			}
    	}
    }
    
    
    public int getCountEntities() {
    	return this.entities.get().size();
    }
    
    public int getCountSpaces() {
    	return this.spaces.get().size();
    }
    
    public int getCountPlayers() {
    	return this.players.size();
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
	
	/** ask playerBID for unknown playerAgents of playerAID */
	public void askForUnknownPlayerAgentsPlayerAToPlayerB(BigInteger playerAID, BigInteger playerBID, ArrayList<BigInteger> idList) {
		players.get(playerBID).get().getSession().send(ServerMessages.askForPlayerAgentsPlayerAToPlayerB(playerAID, playerBID, idList));
	}
	
	/** player */
	public void sendUnknownPlayerAgents(BigInteger toPlayerID, String fromPlayerName, ArrayList<PlayerEntity> agentList) {
		players.get(toPlayerID).get().getSession().send(ServerMessages.sendUnknownPlayerAgents(toPlayerID, fromPlayerName, agentList));
	}
	
	public void updateSendLogout(BigInteger playerID) {
		/** TODO: LOGOUT message */
		for(BigInteger playerIDto: players.keySet()) {
			if (players.get(playerIDto).get().isReady() && playerIDto != playerID) {
				players.get(playerIDto).get().getSession().send(ServerMessages.sendDelete(OBJECTCODE.PLAYER, playerID));
			}
		}

	}
	
	public void editPlayerRoom(BigInteger playerID, String playerName, String room) {
		for(BigInteger playerIDto: players.keySet()) {
			if (players.get(playerIDto).get().isReady() && playerIDto != playerID) {
				players.get(playerIDto).get().getSession().send(ServerMessages.editPlayerRoom(playerID, playerName, room));
			}
		}
	}
	
	/** send init process if we got new spaces */
	public void updateSendInitSpaces() {
		for(BigInteger playerIDto: players.keySet()) {
			if (players.get(playerIDto).get().isReady()) {
				players.get(playerIDto).get().initSpaces();
			}
		}
	}
	
	public void sendTextureToPlayers( String textureName, String byPlayerName) {
		for(BigInteger playerIDto: players.keySet()) {
			WorldPlayer player = players.get(playerIDto).get();
			String playerName = player.getName().substring(player.getName().indexOf(".")+1, player.getName().length());
			if (player.isReady() && !playerName.equals(byPlayerName)) {
				logger.log(Level.INFO, "sending texture {0} to player {1}",
				            new Object[] { textureName, playerName });
				if (!ServerTextureManager.manager.getUploadTextureName(playerName).equals(textureName)) {
		    		ServerTextureManager.manager.prepareTextureForUpload(playerName, textureName);
		    	}
				player.startTextureDownload(textureName);
			}
		}
	}
	
	/** send init process if we got new spaces */
	public void sendEditSpaceAddons(BigInteger id, String textureName, int[] rgb, float trans, int filled, float scale, float area) {
		for(BigInteger playerIDto: players.keySet()) {
			if (players.get(playerIDto).get().isReady()) {
				players.get(playerIDto).get().sendEditSpaceAddons(id, textureName, rgb, trans, filled, scale, area);
			}
		}
	}
	
//	/** send init process if we got new spaces */
//	public void sendEditPlayerAddons(BigInteger id, String playerName, String tileName, String tilePathName, int tileWidth, int tileHeight, String country, String groupName, int experience) {
//		for(BigInteger playerIDto: players.keySet()) {
//			if (players.get(playerIDto).get().isReady() && playerIDto!= id) {
//				players.get(playerIDto).get().sendEditPlayerAddons(id, playerName, tileName, tilePathName, tileWidth, tileHeight, country, groupName, experience);
//			}
//		}
//	}
	
	/** send edit player equipment for individual appearance */
	public void sendEditPlayerEquipment(BigInteger id, String weaponName, String armorName, String helmetName, String feetName, String shieldName, String amuletName, String gloveName, String shirtName, String trousersName) {
		for(BigInteger playerIDto: players.keySet()) {
			if (players.get(playerIDto).get().isReady() && playerIDto!= id) {
				players.get(playerIDto).get().sendEditPlayerEquipment(id, weaponName, armorName, helmetName, feetName, shieldName, amuletName, gloveName, shirtName, trousersName);
			}
		}
	}

	public BigInteger getLastAddedSpaceID() {
		return lastAddedSpaceID;
	}

	public void setLastAddedSpaceID(BigInteger lastAddedSpaceID) {
		this.lastAddedSpaceID = lastAddedSpaceID;
	}
    

	
	public boolean getHasReceivedNewSpace() {
		return hasReceivedNewSpace;
	}
	
	public void setHasReceivedNewSpace(boolean hasReceivedNewSpace) {
		this.hasReceivedNewSpace = hasReceivedNewSpace;
	}
	
	public ServerPlayer getServerPlayer(String name) {
		return serverPlayers.get().get(name).get();
	}

//	public HashMap<BigInteger, Iterator<BigInteger>> getPlayerInitializingEntities() {
//		return playerInitializingEntities;
//	}
	
	public void addPlayerInitializingEntities(BigInteger playerID) {
		if (!this.playerInitializingEntities.containsKey(playerID)) {
			this.playerInitializingEntities.put(playerID, this.getEntities().get().keySet().iterator());
		}
	}
	
	public void removePlayerInitializingEntities(BigInteger playerID) {
		if (this.playerInitializingEntities.containsKey(playerID)) {
			this.playerInitializingEntities.remove(this.players.get(playerID));
		}
	}
	
	/** send count next entities to player, who need init */
	public void initializePlayersEntities() {
		Entity[] entityArray;
		for (BigInteger playerIds : playerInitializingEntities.keySet()) {
			/*** check if player does exist */
			if (players.get(playerIds) != null) {
				Iterator<BigInteger> itKeys = playerInitializingEntities.get(playerIds);
				entityArray = getNextCountEntities(20, itKeys);
				this.players.get(playerIds).get().getSession().send(ServerMessages.sendEntities(entityArray));
				if (itKeys.hasNext()) { 
					this.playerInitializingEntities.put(playerIds, itKeys);
				} else {
					this.playerInitializingEntities.remove(playerIds);
					this.players.get(playerIds).get().setReady(true);
					this.players.get(playerIds).get().setInitializingEntities(false);
				}
			} else {
				this.playerInitializingEntities.remove(playerIds);
			}
		}
		
	}
	
	public HashMap<BigInteger, Iterator<BigInteger>> getPlayerInitializingItems() {
		return playerInitializingItems;
	}
	
	public void addPlayerInitializingItems(BigInteger playerID) {
		if (!this.playerInitializingItems.containsKey(playerID)) {
			this.playerInitializingItems.put(playerID, this.getItemList().get().keySet().iterator());
		}
	}
	
	public void removePlayerInitializingItems(BigInteger playerID) {
		if (this.playerInitializingItems.containsKey(playerID)) {
			this.playerInitializingItems.remove(this.players.get(playerID));
		}
	}
	
	/** send count next entities to player, who need init */
	public void initializePlayersItems() {
		Item[] itemArray;
//		System.out.println("sending");
		for (BigInteger playerIds : playerInitializingItems.keySet()) {
//			System.out.println("ID: "+playerIds);
			/*** check if player does exist */
			if (players.get(playerIds) != null) {
//				System.out.println("got player!");
				Iterator<BigInteger> itKeys = playerInitializingItems.get(playerIds);
				
//				System.out.println("itKeys has next ? : "+itKeys.hasNext());
				itemArray = getNextCountItems(10, itKeys);
				int realLength = 0;
				for (int i = 0; i < itemArray.length; i++) {
					if ( itemArray[i] == null) {
						realLength = i;
						break;
					}
					realLength = i+1;
				}
//				System.out.println("realLength = "+realLength);
				Item[] realItems = new Item[realLength];
				for (int i = 0; i <realLength; i++) {
					realItems[i] = itemArray[i];
				}
				
				this.players.get(playerIds).get().getSession().send(ServerMessages.sendItems(realItems));
				if (itKeys.hasNext()) { 
					this.playerInitializingItems.put(playerIds, itKeys);
				} else {
					this.playerInitializingItems.remove(playerIds);
					this.players.get(playerIds).get().setReady(true);
					this.players.get(playerIds).get().setInitializingItems(false);
				}				
				
//				Item singleItem = null;
//				if (itemArray != null && itemArray.length>= 1) {
//					singleItem = itemArray[0];
//					System.out.println("got item!");
//				}
//				if (singleItem != null) {
//					System.out.println("itemID: "+singleItem.getId());
//					this.players.get(playerIds).get().getSession().send(ServerMessages.addCompleteItem(singleItem.getItemCode(), singleItem.getId(), singleItem.getName(), singleItem.getItemEntity().getX(), singleItem.getItemEntity().getY(), singleItem.getCount(), singleItem.getStates()));
//					if (itKeys.hasNext()) { 
//						this.playerInitializingItems.put(playerIds, itKeys);
//					} else {
//						this.playerInitializingItems.remove(playerIds);
//						this.players.get(playerIds).get().setReady(true);
//						this.players.get(playerIds).get().setInitializingItems(false);
//					}
//				}
			} else {
				this.playerInitializingItems.remove(playerIds);
			}
		}
		
	}

	
	public BigInteger getMaxItemIndex() {
		return maxItemIndex;
	}

	public void setMaxItemIndex(BigInteger maxItemIndex) {
		this.maxItemIndex = maxItemIndex;
	}

	public ManagedReference<ScalableHashMap<BigInteger, ManagedReference<ServerItem>>> getItemList() {
		return itemList;
	}
	
	public HashMap<BigInteger, Iterator<BigInteger>> getPlayerInitializingAreaInfluences() {
		return playerInitializingAreaInfluences;
	}
	
	public void addPlayerInitializingAreaInfluences(BigInteger playerID) {
		if (!this.playerInitializingAreaInfluences.containsKey(playerID)) {
			this.playerInitializingAreaInfluences.put(playerID, this.getAreaInfluenceList().get().keySet().iterator());
		}
	}
	
	public void removePlayerInitializingAreaInfluences(BigInteger playerID) {
		if (this.playerInitializingAreaInfluences.containsKey(playerID)) {
			this.playerInitializingAreaInfluences.remove(this.players.get(playerID));
		}
	}
	
	public HashMap<BigInteger, Iterator<BigInteger>> getPlayerInitializingGrowthSpots() {
		return playerInitializingGrowthSpots;
	}
	
	public void addPlayerInitializingGrowthSpots(BigInteger playerID) {
		if (!this.playerInitializingGrowthSpots.containsKey(playerID)) {
			this.playerInitializingGrowthSpots.put(playerID, this.getGrowthSpotList().get().keySet().iterator());
		}
	}
	
	public void removePlayerInitializingGrowthSpots(BigInteger playerID) {
		if (this.playerInitializingGrowthSpots.containsKey(playerID)) {
			this.playerInitializingGrowthSpots.remove(this.players.get(playerID));
		}
	}
	
	/** send count next areaInfluence to player, who need init */
	public void initializePlayersAreaInfluences() {
		AreaInfluence[] areaInfluenceArray;
//		System.out.println("sending");
		for (BigInteger playerIds : playerInitializingAreaInfluences.keySet()) {
//			System.out.println("ID: "+playerIds);
			/*** check if player does exist */
			if (players.get(playerIds) != null) {
//				System.out.println("got player!");
				Iterator<BigInteger> itKeys = playerInitializingAreaInfluences.get(playerIds);
				
//				System.out.println("itKeys has next ? : "+itKeys.hasNext());
				areaInfluenceArray = getNextCountAreaInfluences(10, itKeys);
				int realLength = 0;
				for (int i = 0; i < areaInfluenceArray.length; i++) {
					if ( areaInfluenceArray[i] == null) {
						realLength = i;
						break;
					}
					realLength = i+1;
				}
//				System.out.println("realLength = "+realLength);
				AreaInfluence[] realAreaInfluences = new AreaInfluence[realLength];
				for (int i = 0; i <realLength; i++) {
					realAreaInfluences[i] = areaInfluenceArray[i];
				}
				
				this.players.get(playerIds).get().getSession().send(ServerMessages.sendAreaInfluenceArray(realAreaInfluences, maxAreaInfluenceIndex));
				if (itKeys.hasNext()) { 
					this.playerInitializingAreaInfluences.put(playerIds, itKeys);
				} else {
					this.playerInitializingAreaInfluences.remove(playerIds);
					this.players.get(playerIds).get().setReady(true);
					this.players.get(playerIds).get().setInitializingAreaInfluences(false);
				}				
				
			} else {
				this.playerInitializingAreaInfluences.remove(playerIds);
			}
		}
		
	}
	
	/** send count next growthSpot to player, who need init */
	public void initializePlayersGrowthSpots() {
		GrowthSpot[] growthSpotArray;
//		System.out.println("sending");
		for (BigInteger playerIds : playerInitializingGrowthSpots.keySet()) {
//			System.out.println("ID: "+playerIds);
			/*** check if player does exist */
			if (players.get(playerIds) != null) {
//				System.out.println("got player!");
				Iterator<BigInteger> itKeys = playerInitializingGrowthSpots.get(playerIds);
				
//				System.out.println("itKeys has next ? : "+itKeys.hasNext());
				growthSpotArray = getNextCountGrowthSpots(10, itKeys);
				int realLength = 0;
				for (int i = 0; i < growthSpotArray.length; i++) {
					if ( growthSpotArray[i] == null) {
						realLength = i;
						break;
					}
					realLength = i+1;
				}
//				System.out.println("realLength = "+realLength);
				GrowthSpot[] realGrowthSpots = new GrowthSpot[realLength];
				for (int i = 0; i <realLength; i++) {
					realGrowthSpots[i] = growthSpotArray[i];
				}
				
				this.players.get(playerIds).get().getSession().send(ServerMessages.sendGrowthSpotArray(realGrowthSpots, maxGrowthSpotIndex));
				if (itKeys.hasNext()) { 
					this.playerInitializingGrowthSpots.put(playerIds, itKeys);
				} else {
					this.playerInitializingGrowthSpots.remove(playerIds);
					this.players.get(playerIds).get().setReady(true);
					this.players.get(playerIds).get().setInitializingGrowthSpots(false);
				}				
				
			} else {
				this.playerInitializingGrowthSpots.remove(playerIds);
			}
		}
		
	}
	
	/** add an item to the player Container */
	public void addItemToContainer(String playerName, OBJECTCODE containerType, ServerItem serverItem, int i, int j) {
		switch (containerType) {
		case CONTAINER_MAIN:
			if (this.containerList.get().get(playerName).get().getContainerArray()[i][j].equals(BigInteger.valueOf(-1))) {
				this.containerList.getForUpdate().get(playerName).getForUpdate().getItemList().remove(this.containerList.get().get(playerName).get().getContainerArray()[i][j]);
			}
			this.containerList.getForUpdate().get(playerName).getForUpdate().getItemList().put(serverItem.getId(), AppContext.getDataManager().createReference(serverItem));
			this.containerList.getForUpdate().get(playerName).getForUpdate().getContainerArray()[i][j] = serverItem.getId();
			break;
		case CONTAINER_EQUIPMENT_BODY:
			if (this.containerEqBodyList.get().get(playerName).get().getContainerArray()[i][j].equals(BigInteger.valueOf(-1))) {
				this.containerEqBodyList.getForUpdate().get(playerName).getForUpdate().getItemList().remove(this.containerEqBodyList.get().get(playerName).get().getContainerArray()[i][j]);
			}
			this.containerEqBodyList.getForUpdate().get(playerName).getForUpdate().getItemList().put(serverItem.getId(), AppContext.getDataManager().createReference(serverItem));
			this.containerEqBodyList.getForUpdate().get(playerName).getForUpdate().getContainerArray()[i][j] = serverItem.getId();
			break;
		case CONTAINER_EQUIPMENT_WEAPON:
			if (this.containerEqWeaponList.get().get(playerName).get().getContainerArray()[i][j].equals(BigInteger.valueOf(-1))) {
				this.containerEqWeaponList.getForUpdate().get(playerName).getForUpdate().getItemList().remove(this.containerEqWeaponList.get().get(playerName).get().getContainerArray()[i][j]);
			}
			this.containerEqWeaponList.getForUpdate().get(playerName).getForUpdate().getItemList().put(serverItem.getId(), AppContext.getDataManager().createReference(serverItem));
			this.containerEqWeaponList.getForUpdate().get(playerName).getForUpdate().getContainerArray()[i][j] = serverItem.getId();
			break;
		case CONTAINER_EQUIPMENT_HELMET:
			if (this.containerEqHelmetList.get().get(playerName).get().getContainerArray()[i][j].equals(BigInteger.valueOf(-1))) {
				this.containerEqHelmetList.getForUpdate().get(playerName).getForUpdate().getItemList().remove(this.containerEqHelmetList.get().get(playerName).get().getContainerArray()[i][j]);
			}
			this.containerEqHelmetList.getForUpdate().get(playerName).getForUpdate().getItemList().put(serverItem.getId(), AppContext.getDataManager().createReference(serverItem));
			this.containerEqHelmetList.getForUpdate().get(playerName).getForUpdate().getContainerArray()[i][j] = serverItem.getId();
			break;
		case CONTAINER_EQUIPMENT_FEET:
			if (this.containerEqFeetList.get().get(playerName).get().getContainerArray()[i][j].equals(BigInteger.valueOf(-1))) {
				this.containerEqFeetList.getForUpdate().get(playerName).getForUpdate().getItemList().remove(this.containerEqFeetList.get().get(playerName).get().getContainerArray()[i][j]);
			}
			this.containerEqFeetList.getForUpdate().get(playerName).getForUpdate().getItemList().put(serverItem.getId(), AppContext.getDataManager().createReference(serverItem));
			this.containerEqFeetList.getForUpdate().get(playerName).getForUpdate().getContainerArray()[i][j] = serverItem.getId();
			break;
		case CONTAINER_EQUIPMENT_SHIELD:
			if (this.containerEqShieldList.get().get(playerName).get().getContainerArray()[i][j].equals(BigInteger.valueOf(-1))) {
				this.containerEqShieldList.getForUpdate().get(playerName).getForUpdate().getItemList().remove(this.containerEqShieldList.get().get(playerName).get().getContainerArray()[i][j]);
			}
			this.containerEqShieldList.getForUpdate().get(playerName).getForUpdate().getItemList().put(serverItem.getId(), AppContext.getDataManager().createReference(serverItem));
			this.containerEqShieldList.getForUpdate().get(playerName).getForUpdate().getContainerArray()[i][j] = serverItem.getId();
			break;
		case CONTAINER_EQUIPMENT_AMULET:
			if (this.containerEqAmuletList.get().get(playerName).get().getContainerArray()[i][j].equals(BigInteger.valueOf(-1))) {
				this.containerEqAmuletList.getForUpdate().get(playerName).getForUpdate().getItemList().remove(this.containerEqAmuletList.get().get(playerName).get().getContainerArray()[i][j]);
			}
			this.containerEqAmuletList.getForUpdate().get(playerName).getForUpdate().getItemList().put(serverItem.getId(), AppContext.getDataManager().createReference(serverItem));
			this.containerEqAmuletList.getForUpdate().get(playerName).getForUpdate().getContainerArray()[i][j] = serverItem.getId();
			break;
		case CONTAINER_EQUIPMENT_GLOVE:
			if (this.containerEqGloveList.get().get(playerName).get().getContainerArray()[i][j].equals(BigInteger.valueOf(-1))) {
				this.containerEqGloveList.getForUpdate().get(playerName).getForUpdate().getItemList().remove(this.containerEqGloveList.get().get(playerName).get().getContainerArray()[i][j]);
			}
			this.containerEqGloveList.getForUpdate().get(playerName).getForUpdate().getItemList().put(serverItem.getId(), AppContext.getDataManager().createReference(serverItem));
			this.containerEqGloveList.getForUpdate().get(playerName).getForUpdate().getContainerArray()[i][j] = serverItem.getId();
			break;
		case CONTAINER_EQUIPMENT_SHIRT:
			if (this.containerEqShirtList.get().get(playerName).get().getContainerArray()[i][j].equals(BigInteger.valueOf(-1))) {
				this.containerEqShirtList.getForUpdate().get(playerName).getForUpdate().getItemList().remove(this.containerEqShirtList.get().get(playerName).get().getContainerArray()[i][j]);
			}
			this.containerEqShirtList.getForUpdate().get(playerName).getForUpdate().getItemList().put(serverItem.getId(), AppContext.getDataManager().createReference(serverItem));
			this.containerEqShirtList.getForUpdate().get(playerName).getForUpdate().getContainerArray()[i][j] = serverItem.getId();
			break;
		case CONTAINER_EQUIPMENT_TROUSERS:
			if (this.containerEqTrousersList.get().get(playerName).get().getContainerArray()[i][j].equals(BigInteger.valueOf(-1))) {
				this.containerEqTrousersList.getForUpdate().get(playerName).getForUpdate().getItemList().remove(this.containerEqTrousersList.get().get(playerName).get().getContainerArray()[i][j]);
			}
			this.containerEqTrousersList.getForUpdate().get(playerName).getForUpdate().getItemList().put(serverItem.getId(), AppContext.getDataManager().createReference(serverItem));
			this.containerEqTrousersList.getForUpdate().get(playerName).getForUpdate().getContainerArray()[i][j] = serverItem.getId();
			break;
		case CONTAINER_USE:
			if (this.containerUseList.get().get(playerName).get().getContainerArray()[i][j].equals(BigInteger.valueOf(-1))) {
				this.containerUseList.getForUpdate().get(playerName).getForUpdate().getItemList().remove(this.containerUseList.get().get(playerName).get().getContainerArray()[i][j]);
			}
			this.containerUseList.getForUpdate().get(playerName).getForUpdate().getItemList().put(serverItem.getId(), AppContext.getDataManager().createReference(serverItem));
			this.containerUseList.getForUpdate().get(playerName).getForUpdate().getContainerArray()[i][j] = serverItem.getId();
			break;
		default:
				;
		}
		
//		System.out.println("worldroom-update: x:"+serverItem.getItemEntity().get().getX()+" y:"+serverItem.getItemEntity().get().getY());
	}
	
	public ManagedReference<ScalableHashMap<String, ManagedReference<ServerContainer>>> getContainerList() {
		return containerList;
	}

	public ManagedReference<ScalableHashMap<String, ManagedReference<ServerContainer>>> getContainerUseList() {
		return containerUseList;
	}

	public ManagedReference<ScalableHashMap<String, ManagedReference<ServerContainer>>> getContainerEqBodyList() {
		return containerEqBodyList;
	}
	
	public void deleteMapObject( BigInteger playerID, InteractionTile interactionTile, String objectMapName, String objectOverlayMap) {
		players.get(playerID).get().getSession().send(ServerMessages.sendDeleteMapObject(interactionTile, objectMapName, objectOverlayMap));
	}

	public ManagedReference<ScalableHashMap<BigInteger, ManagedReference<ServerAreaInfluence>>> getAreaInfluenceList() {
		return areaInfluenceList;
	}

	public BigInteger getMaxAreaInfluenceIndex() {
		return maxAreaInfluenceIndex;
	}

	public void setMaxAreaInfluenceIndex(BigInteger maxAreaInfluenceIndex) {
		this.maxAreaInfluenceIndex = maxAreaInfluenceIndex;
	}

	public ManagedReference<ScalableHashMap<BigInteger, ManagedReference<ServerAreaInfluence>>> getAreaInfluenceSendList() {
		return areaInfluenceSendList;
	}
	
	public ManagedReference<ScalableHashMap<BigInteger, ManagedReference<ServerGrowthSpot>>> getGrowthSpotList() {
		return growthSpotList;
	}

	public BigInteger getMaxGrowthSpotIndex() {
		return maxGrowthSpotIndex;
	}

	public void setMaxGrowthSpotIndex(BigInteger maxGrowthSpotIndex) {
		this.maxGrowthSpotIndex = maxGrowthSpotIndex;
	}

	public ManagedReference<ScalableHashMap<BigInteger, ManagedReference<ServerGrowthSpot>>> getGrowthSpotSendList() {
		return growthSpotSendList;
	}

	public ManagedReference<ScalableHashMap<String, ManagedReference<ServerContainer>>> getContainerEqWeaponList() {
		return containerEqWeaponList;
	}

	public ManagedReference<ScalableHashMap<String, ManagedReference<ServerContainer>>> getContainerEqHelmetList() {
		return containerEqHelmetList;
	}

	public ManagedReference<ScalableHashMap<String, ManagedReference<ServerContainer>>> getContainerEqShieldList() {
		return containerEqShieldList;
	}

	public ManagedReference<ScalableHashMap<String, ManagedReference<ServerContainer>>> getContainerEqFeetList() {
		return containerEqFeetList;
	}

	public ManagedReference<ScalableHashMap<String, ManagedReference<ServerContainer>>> getContainerEqAmuletList() {
		return containerEqAmuletList;
	}

	public ManagedReference<ScalableHashMap<String, ManagedReference<ServerContainer>>> getContainerEqGloveList() {
		return containerEqGloveList;
	}

	public ManagedReference<ScalableHashMap<String, ManagedReference<ServerContainer>>> getContainerEqShirtList() {
		return containerEqShirtList;
	}

	public ManagedReference<ScalableHashMap<String, ManagedReference<ServerContainer>>> getContainerEqTrousersList() {
		return containerEqTrousersList;
	}

	public long getWorldTime() {
		return worldTime;
	}

	public void setWorldTime(long worldTime) {
		this.worldTime = worldTime;
	}

}