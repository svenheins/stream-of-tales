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

import java.awt.GraphicsEnvironment;
import java.io.File;
import java.io.Serializable;
import java.math.BigInteger;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.sun.sgs.app.AppContext;
import com.sun.sgs.app.AppListener;
import com.sun.sgs.app.Channel;
import com.sun.sgs.app.ChannelManager;
import com.sun.sgs.app.ClientSession;
import com.sun.sgs.app.ClientSessionListener;
import com.sun.sgs.app.DataManager;
import com.sun.sgs.app.Delivery;
import com.sun.sgs.app.ManagedObject;
import com.sun.sgs.app.ManagedReference;
import com.sun.sgs.app.Task;
import com.sun.sgs.app.TaskManager;

import de.svenheins.functions.MyUtil;
import de.svenheins.main.GameStates;
import de.svenheins.managers.EntityManager;
import de.svenheins.managers.PlayerManager;
import de.svenheins.managers.RessourcenManager;
import de.svenheins.managers.ServerTextureManager;
import de.svenheins.managers.SpaceManager;
import de.svenheins.managers.SpriteManager;
//import de.svenheins.managers.TextureManager;
import de.svenheins.objects.Entity;
import de.svenheins.objects.ServerAgent;
import de.svenheins.objects.ServerAgentEmployee;
import de.svenheins.objects.ServerAgentEntrepreneur;
import de.svenheins.objects.ServerEntity;
import de.svenheins.objects.ServerRegion;
import de.svenheins.objects.ServerSpace;
import de.svenheins.objects.ServerSprite;
import de.svenheins.objects.Space;
import de.svenheins.objects.Sprite;
import de.svenheins.objects.TileSet;

/**
 * World-Object
 * -> here everything starts
 */
public class World
    implements Serializable, AppListener, Task, ManagedObject
{
    /** The version of the serialized form of this class. */
    private static final long serialVersionUID = 1L;

    /** The delay before the first run of the task. */
    public static final int DELAY_MS = 4000;
    public static final int DELAY_MS_WORLDUPDATE = 4000;

    /** The time to wait before repeating the task. */
    public static final int PERIOD_MS = 200;
    public static final int PERIOD_MS_WORLDUPDATE = 200;
    
//    public static WorldRoom room;
    
    private int initStartIndex = 0;
    private int initEndIndex; 
    private int initPackageSize = 20;
    private boolean restartDuplicated = false;
	boolean runReady = true;

    private static List<ManagedReference<ServerEntity>> entitiesArray = null;
    private static List<ManagedReference<ServerSpace>> spacesArray = null;

    /** A Set reference to each of the Update Task and the sendTask */
    private Set<ManagedReference<SendUpdatePlayersTaskSimple>> supSimple = new HashSet<ManagedReference<SendUpdatePlayersTaskSimple>>();
    private Set<ManagedReference<UpdateWorldTaskSimple>> uwtSimple = new HashSet<ManagedReference<UpdateWorldTaskSimple>>();
    
    /** A Set reference to each of the Update Task and the sendTask */
    private Set<ManagedReference<SendUpdatePlayersSpaces>> supsSimple = new HashSet<ManagedReference<SendUpdatePlayersSpaces>>();
    private Set<ManagedReference<UpdateWorldSpaces>> uwsSimple = new HashSet<ManagedReference<UpdateWorldSpaces>>();
    
    /** A Set reference to each of the sendTask */
    private Set<ManagedReference<SendUpdatePlayers>> supPlayers = new HashSet<ManagedReference<SendUpdatePlayers>>();
    
    /** The name of the first channel {@value #CHANNEL_1_NAME} */
    static final String CHANNEL_1_NAME = "Foo";
    /** The name of the second channel {@value #CHANNEL_2_NAME} */
    static final String CHANNEL_2_NAME = "Bar";
    /** The name of the global chat channel {@value #CHANNEL_CHAT_GLOBAL} */
    static final String CHANNEL_CHAT_GLOBAL = "GLOBAL CHAT";
 
    /** 
     * The first {@link Channel}.  The second channel is looked up
     * by name.
     */
    private ManagedReference<Channel> channel1 = null;
    
    /** The {@link Logger} for this class. */
    private static final Logger logger =
        Logger.getLogger(World.class.getName());

    /** A reference to the one-and-only {@linkplain WorldRoom room}. */
    private ManagedReference<WorldRoom> roomRef;
    
    /**  The timestamp when this task was last run. */
    private long lastTimestamp = System.currentTimeMillis();

 

    /**
     * {@inheritDoc}
     * <p>
     * Creates the world within the MUD.
     */
    public void initialize(Properties props) {
    	/** first init */
    	GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GameStates.setWidth(ge.getMaximumWindowBounds().width);
        GameStates.setHeight(ge.getMaximumWindowBounds().height);
        
        /** init ressources */
//        initializeRessources();
        
        /** create an image-folder */
     	/** all non-existent ancestor directories are automatically created */
	    boolean success = (new File("./ressources/images")).mkdirs();
	    if (!success) {
	         // Directory creation failed
	    }
	    /** init the external images */
	    ServerTextureManager.manager.initExternalImages("./ressources/images/");
        
        this.initStartIndex = 0;
        
        PlayerManager.emptyAll();
        
//        this.initEndIndex = this.initStartIndex + this.initPackageSize;
        
//        /** Display Resolution */
//        GameStates.setWidth(640);
//        GameStates.setHeight(480);
    	
    	TaskManager taskManager = AppContext.getTaskManager();
        taskManager.schedulePeriodicTask(this, DELAY_MS, PERIOD_MS);
        
        // Channel-manager
    	ChannelManager channelMgr = AppContext.getChannelManager();
        
        // Create and keep a reference to the first channel.
        Channel c1 = channelMgr.createChannel(CHANNEL_1_NAME, 
                                              null, 
                                              Delivery.RELIABLE);
        channel1 = AppContext.getDataManager().createReference(c1);
        
        // We don't keep a reference to the second channel, to demonstrate
        // looking it up by name when needed.  Also, this channel uses a
        // {@link ChannelListener} to filter messages.
        channelMgr.createChannel(CHANNEL_2_NAME, 
                                 new WorldChannelListener(), 
                                 Delivery.RELIABLE);
        channelMgr.createChannel(CHANNEL_CHAT_GLOBAL, 
                new WorldChannelListener(), 
                Delivery.RELIABLE);
    	
    	logger.info("Initializing World");

    	initializeTileSetManager();
    	
        // Create the Room
        WorldRoom room = new WorldRoom("Plain Room", "a nondescript room", 100.222f, 300.333f);
        
        
        BigInteger entityIDIndex = BigInteger.valueOf(0);
        /** Create Entities */
        Entity realEntity;
        String iterativSpriteString = "eye.png";
        Sprite it_sprite = SpriteManager.manager.getSprite(iterativSpriteString);
        ServerSprite it_s_sprite = new ServerSprite(iterativSpriteString, it_sprite.getHeight(), it_sprite.getWidth());
        ServerEntity it_s_entity;
        int numObjects = 0;
        float it_x, it_y, it_mx, it_my;
        for (int i = 0; i<numObjects; i++) {
        	it_x = (float) (Math.random()*GameStates.getWidth()-it_s_sprite.getWidth());
        	it_y = (float) (Math.random()*GameStates.getHeight()-it_s_sprite.getHeight());
        	it_mx = (float) (Math.random()*50+0);
        	it_my = (float) (Math.random()*50+0);
        	it_s_entity = new ServerEntity(it_s_sprite, entityIDIndex.add(BigInteger.valueOf(1)), it_x, it_y, it_mx, it_my);
        	room.addEntity(it_s_entity);
        	/** Add Entity to the EntityManager, which allows to communicate fastly*/
        	realEntity = new Entity(it_s_entity.getName(), it_s_entity.getId(), 0, 0, 0, 0);
        	EntityManager.add(realEntity);
        	logger.log(Level.INFO, "EntityManager intitialized: count = " + EntityManager.size());
        }
        
        /** Create ServerAgents (extend Entities) */
        Entity realAgent;
        String iterativSpriteStringAgent = "standardShip_green.png";
        Sprite it_spriteAgent = SpriteManager.manager.getSprite(iterativSpriteStringAgent);
//        TileSet tileSetEnemy = new TileSet("eye12.png", "enemy", 10, 6);
        ServerSprite it_s_spriteAgent = new ServerSprite(iterativSpriteStringAgent, it_spriteAgent.getHeight(), it_spriteAgent.getWidth());
        ServerEntity it_s_entityAgent;
        int numAgentsEntrepreneur = 600;
//        float it_x, it_y, it_mx, it_my;
        for (int i = 0; i<numAgentsEntrepreneur; i++) {
        	it_x = (float) (Math.random()*2*GameStates.getWidth()-it_s_spriteAgent.getWidth());
        	it_y = (float) (Math.random()*2*GameStates.getHeight()-it_s_spriteAgent.getHeight());
        	it_mx = (float) (-10+Math.random()*30);//Math.random()*50+0;
        	it_my = (float) (-10+Math.random()*30);//Math.random()*50+0;
        	entityIDIndex = entityIDIndex.add(BigInteger.valueOf(1));
        	it_s_entityAgent = new ServerAgentEntrepreneur(it_s_spriteAgent, entityIDIndex, it_x, it_y, it_mx, it_my);
        	room.addEntity(it_s_entityAgent);
        	/** Add Entity to the EntityManager, which allows to communicate fastly*/
        	realAgent = new Entity(it_s_entityAgent.getName(), it_s_entityAgent.getId(), 0, 0, 0, 0);
        	if ( EntityManager.add(realAgent)) {
        		logger.log(Level.INFO, "EntityManager added successfully ID: " +realAgent.getId());
        	}
        	logger.log(Level.INFO, "EntityManager intitialized: count = " + EntityManager.size());
        }
        /** Create ServerEmployees (extend Entities) */
        Entity realAgentEmployee;
        String iterativSpriteStringAgentEmployee = "enemy.png";
        Sprite it_spriteAgentEmployee = SpriteManager.manager.getSprite(iterativSpriteStringAgentEmployee);
        ServerSprite it_s_spriteAgentEmployee = new ServerSprite(iterativSpriteStringAgentEmployee, it_spriteAgentEmployee.getHeight(), it_spriteAgentEmployee.getWidth());
        ServerEntity it_s_entityAgentEmployee;
        int numAgentsEmployee = 20;
//        float it_x, it_y, it_mx, it_my;
        for (int i = 0; i<numAgentsEmployee; i++) {
        	it_x = (float) (Math.random()*GameStates.getWidth()-it_s_spriteAgentEmployee.getWidth());
        	it_y = (float) (Math.random()*GameStates.getHeight()-it_s_spriteAgentEmployee.getHeight());
        	it_mx = 30;//Math.random()*50+0;
        	it_my = 30;//Math.random()*50+0;
        	entityIDIndex = entityIDIndex.add(BigInteger.valueOf(1));
        	it_s_entityAgentEmployee = new ServerAgentEmployee(it_s_spriteAgentEmployee, entityIDIndex, it_x, it_y, it_mx, it_my);
        	room.addEntity(it_s_entityAgentEmployee);
        	/** Add Entity to the EntityManager, which allows to communicate fastly*/
        	realAgentEmployee = new Entity(it_s_entityAgentEmployee.getName(), it_s_entityAgentEmployee.getId(), 0, 0, 0, 0);
        	if ( EntityManager.add(realAgentEmployee)) {
        		logger.log(Level.INFO, "EntityManager added successfully ID: " +realAgentEmployee.getId());
        	} else {
        		logger.log(Level.INFO, "EntityManager denied ID (duplicated): " +realAgentEmployee.getId());
        	}
        	logger.log(Level.INFO, "EntityManager intitialized: count = " + EntityManager.size());
        }
        
        /** 
         * ADD: Spaces
         */
        BigInteger spaceIds = BigInteger.valueOf(0);
        /** Create World-Plates */
        String hexaString = "Sechseck.svg";
        Space hexaSpace;
        int rowsHexagons = 10;
        int colsHexagons = 10;
        float hexX, hexY, hexMX, hexMY;
        float climateHexagon;
        int capacityHexagon;
        for (int i = 0; i<rowsHexagons*colsHexagons; i++) {
        	spaceIds = spaceIds.add(BigInteger.valueOf(1));
        	int[] rgb = MyUtil.niceColorGenerator();

            hexaSpace = new Space(hexaString, spaceIds, rgb, true, 1.0f, 2.0f);
            hexX = (float) (i % colsHexagons)*hexaSpace.getWidth();
            hexY = (float) ((int) (i / colsHexagons))*((float) hexaSpace.getHeight()*(0.75f));

            if ((i/colsHexagons) % 2 == 0) hexX += hexaSpace.getWidth()/2;
            hexMX = 0;//Math.random()*50+0;
            hexMY = 0;//Math.random()*50+0;
	      	hexaSpace.setAllXY(hexX, hexY);
	      	hexaSpace.setMovement(0, 0);
	      	climateHexagon = 0.0f;
	        capacityHexagon = 10;
	        ServerRegion s_hexaSpace = new ServerRegion(hexaSpace, climateHexagon, capacityHexagon);
	        room.addSpace(s_hexaSpace);
	        // DONT add to the SpaceManager, because we manage this in the room.addSpace already
	        hexaSpace.setId(s_hexaSpace.getId());
	        SpaceManager.add(hexaSpace);	
	        logger.log(Level.INFO, "SpacePlates intitialized: count = " + SpaceManager.size());
        }

        /** Keep a reference to the Room */
        setRoom(room);

        /** create Entity-Tasks */
        int begin = 0;
        int countPackets = 2;//(1+(room.getCountEntities()/2000));
        logger.log(Level.INFO, "countPackets: "+countPackets);
        int end = room.getCountEntities()/countPackets + (room.getCountEntities() % countPackets);
        logger.log(Level.INFO, "end: "+end);
        int packageSize = 20;
        /** UpdateTask (moving, collision, ...) and SendTask (send updates to all players)*/
        UpdateWorldTaskSimple uwt;
        SendUpdatePlayersTaskSimple sup;
        for (int i = 0; i< countPackets; i++) {
        	uwt = new UpdateWorldTaskSimple(room, begin, end, packageSize);
        	addUpdateTask(uwt);
        	sup = new SendUpdatePlayersTaskSimple(room, begin, end, packageSize);
        	addSendTask(sup);
        	begin = end;
        	logger.log(Level.INFO, "begin: "+begin);
        	end = end + (room.getCountEntities()/countPackets);
        	logger.log(Level.INFO, "end: "+end);
        }
       
        /** create Space-Tasks */
        begin = 0;
        countPackets = 1;//(1+(room.getCountSpaces()/200));
        packageSize = 10;
        logger.log(Level.INFO, "countPackets: "+countPackets);
        end = room.getCountSpaces()/countPackets + (room.getCountSpaces() % countPackets);
        logger.log(Level.INFO, "end: "+end);
        /** UpdateTask (moving, collision, ...) and SendTask (send updates to all players)*/
        UpdateWorldSpaces uws;
        SendUpdatePlayersSpaces sups;
        for (int i = 0; i < countPackets; i++ ) {
        	uws = new UpdateWorldSpaces(room, i, packageSize, countPackets);
        	addUpdateTaskSpaces(uws);
        	sups = new SendUpdatePlayersSpaces(room, i, packageSize, countPackets);
        	addSendTaskSpaces(sups);
        	begin = end;
        	logger.log(Level.INFO, "begin: "+begin);
        	end = end + (room.getCountSpaces()/countPackets);
        	logger.log(Level.INFO, "end: "+end);
        }
        
        /** create PlayersSend-Tasks */
        begin = 0;
        countPackets = 1;//(1+(room.getCountSpaces()/200));
        packageSize = 100;
        logger.log(Level.INFO, "countPackets: "+countPackets);
        end = 100;//room.getCountSpaces()/countPackets + (room.getCountSpaces() % countPackets);
        logger.log(Level.INFO, "end: "+end);
        /** UpdateTask (moving, collision, ...) and SendTask (send updates to all players)*/
//        UpdateWorldSpaces uws;
        SendUpdatePlayers supPlayer;
        for (int i = 0; i< countPackets; i++) {
        	supPlayer = new SendUpdatePlayers(room, begin, end, packageSize);
        	addSendTaskPlayers(supPlayer);
//        	sups = new SendUpdatePlayersSpaces(room, begin, end, packageSize);
//        	addSendTaskSpaces(sups);
        	begin = end;
        	logger.log(Level.INFO, "begin: "+begin);
        	end = end + packageSize;
        	logger.log(Level.INFO, "end: "+end);
        }
       
        logger.info("World Initialized");
    }

	/**
     * Gets the World's One True Room.
     * <p>
     * @return the room for this {@code World}
     */
    public WorldRoom getRoom() {
        if (roomRef == null) {
            return null;
        }

        return roomRef.get();
    }
    

    /**
     * Sets the World's One True Room to the given room.
     * <p>
     * @param room the room to set
     */
    public void setRoom(WorldRoom room) {
        DataManager dataManager = AppContext.getDataManager();

        if (room == null) {
            roomRef = null;
            return;
        }

        roomRef = (dataManager.createReference(room));
        
    }

    /**
     * {@inheritDoc}
     * <p>
     * Obtains the {@linkplain WorldPlayer player} for this
     * {@linkplain ClientSession session}'s user, and puts the
     * player into the One True Room for this {@code World}.
     */
    public ClientSessionListener loggedIn(ClientSession session) {
        logger.log(Level.INFO,
            "World Client login: {0}", session.getName());

        // Delegate to a factory method on WorldPlayer,
        // since player management really belongs in that class.
        WorldPlayer player = WorldPlayer.loggedIn(session, channel1);

        // Put player in room
        player.enter(getRoom());


        // return player object as listener to this client session
        return player;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Calls the run() method of the subtask set on this object.
     */
    public void run() throws Exception {

    	/** Check if the Server was down or has to be reinitialized*/
//        if (EntityManager.get(EntityManager.idList.get(roomRef.get().getEntities().size()-1)) == null || SpaceManager.get(EntityManager.idList.get(roomRef.get().getSpaces().size()-1)) == null) {
        /** if the Spaces are not initialized, the Entities have to be checked, too */
    	if (SpaceManager.size() < roomRef.get().getSpaces().get().size()) {	
    	/** check if the EntityManger is still in the filling process (so the last item should be null) */
//        	if (EntityManager.get(EntityManager.idList.get(roomRef.get().getEntities().size()-1)) == null) {
            if( EntityManager.size() < roomRef.get().getEntities().get().size()) {
        		if (EntityManager.entityList.size() == 0) {
            		logger.log(Level.INFO, "Init for the first time");
            		/** init for the first time */
                	GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
                    GameStates.setWidth(ge.getMaximumWindowBounds().width);
                    GameStates.setHeight(ge.getMaximumWindowBounds().height);
                    /** init EntityManager for the restart-case */
            		EntityManager.entityList = new HashMap<BigInteger, Entity>();
            		EntityManager.idList = new ArrayList<BigInteger>();
            		this.restartDuplicated = false;
            		
            		/** reset our indices and packageSize */
            		this.initStartIndex = 0;
            		this.initPackageSize = 1;
            		this.initEndIndex = 0;
            	}
            	/** we must create our entityArray in each step */
        		entitiesArray = new ArrayList<ManagedReference<ServerEntity>>(roomRef.get().getEntities().get().values());
        		logger.log(Level.INFO, "EntityArray-Size: "+ entitiesArray.size());
        		
        		/** set the actual endIndex */
        		this.initEndIndex = this.initStartIndex + this.initPackageSize;
            	if (this.initEndIndex > roomRef.get().getEntities().get().size()) this.initEndIndex = entitiesArray.size();

            	/** now loop through the package and add each Entity of the persistent data to the sending-relevant EntityManager */
	        	for (int i = this.initStartIndex; i<this.initEndIndex; i++) {
	    			ManagedReference<ServerEntity> entity = entitiesArray.get(i);
	        		ServerEntity se = entity.get();
	        		Entity realEntity = new Entity(se.getName(), se.getId(), 0, 0, 0, 0);
	            	/** after restart there can appear a duplicated entry */
	        		if(!EntityManager.add(realEntity)) {
	            		/** something went wrong -> RESTART!!! */
	            		this.restartDuplicated = true;
	            		logger.log(Level.INFO, "Doppelte ID: "+ realEntity.getId());
//	            		EntityManager.entityList = new HashMap<BigInteger, Entity>();
//	            		EntityManager.idList = new ArrayList<BigInteger>();
	            	}
	        	}
	        	/** Handle Duplicated Entries here */
	        	if(this.restartDuplicated && (initEndIndex > entitiesArray.size()-this.initPackageSize) ) {
	        		/** start once again */
	        		this.initStartIndex = 0;
            		this.initEndIndex = 0;
            		restartDuplicated = false;
	        	} else {
	        		/** don't restart */
	        		this.initStartIndex = EntityManager.size();
	        	}
	        	
	        	logger.log(Level.INFO, "EntityManager-Size: " +EntityManager.size()+"/"+entitiesArray.size());
	        	logger.log(Level.INFO, "EntityManager-IDLIST-Size: " +EntityManager.idList.size()+"/"+entitiesArray.size());

        	} else /**if (EntityManager.size() == roomRef.get().getEntities().size())*/ {
        		/** Now we are ready for the SpaceManager */
	        	logger.log(Level.INFO, "EntityManager READY; entityCount: "+EntityManager.size());
	        	/** init EntityManager for the restart-case */
	        	if (SpaceManager.spaceList.size() == 0) {
	        		SpaceManager.spaceList = new HashMap<BigInteger, Space>();
	        		SpaceManager.idList = new ArrayList<BigInteger>();
            		/** reset our indices and packageSize */
            		this.initStartIndex = 0;
            		this.initPackageSize = 1;
            		this.initEndIndex = 0;
            		this.restartDuplicated = false;
	        	}
	        	/** we must create our spacesArray in each step */
        		spacesArray = new ArrayList<ManagedReference<ServerSpace>>(roomRef.get().getSpaces().get().values());
	        	
        		/** set the actual endIndex */
        		this.initEndIndex = this.initStartIndex + this.initPackageSize;
            	if (this.initEndIndex > roomRef.get().getSpaces().get().size()) this.initEndIndex = roomRef.get().getSpaces().get().size();

            	/** now loop through the package and add each Entity of the persistent data to the sending-relevant EntityManager */
	        	for (int i = this.initStartIndex; i<this.initEndIndex; i++) {
	    			ManagedReference<ServerSpace> space = spacesArray.get(i);
	        		ServerSpace sp = space.get();
//	        		Space realSpace = new Space(sp.getName(), sp.getId(), sp.getRGB(), sp.isFilled(), sp.getTrans(), sp.getScale());
	        		Space realSpace = new Space(sp.getPolygon(), (int) sp.getX(), (int) sp.getY(), sp.getName(), sp.getId(), sp.getRGB(), sp.isFilled(), sp.getTrans(), sp.getScale(), sp.getArea(), sp.getTextureName());
	        		if(!SpaceManager.add(realSpace)) {
	            		/** something went wrong -> RESTART!!! */
	            		this.restartDuplicated = true;
	            		logger.log(Level.INFO, "Doppelte ID: "+ realSpace.getId());
//	            		EntityManager.entityList = new HashMap<BigInteger, Entity>();
//	            		EntityManager.idList = new ArrayList<BigInteger>();
	            	}
	            	logger.log(Level.INFO, "SpaceManager-Size: " +SpaceManager.size()+"/"+spacesArray.size());
	        	}
	        	if(this.restartDuplicated && (initEndIndex > spacesArray.size()-this.initPackageSize) ) {
	        		/** start once again */
	        		this.initStartIndex = 0;
            		this.initEndIndex = 0;
            		restartDuplicated = false;
	        	} else {
	        		/** don't restart */
	        		this.initStartIndex = SpaceManager.size();
	        		
	        	}
	        	
	        	/** if everything is ready */
	        	if (SpaceManager.size() == spacesArray.size()) {
	        		/** init the external images and so on */
	        	    ServerTextureManager.manager.initExternalImages(GameStates.externalImagesPath);
	        	    initializeTileSetManager();
	        	    PlayerManager.emptyAll();
	        	    roomRef.get().removeAllPlayers();
	        	    /** DONE */
	        	    logger.log(Level.INFO, "### EVERYTHING IS INITIALIZED ###");
	        	}
//	        	logger.log(Level.INFO, "Initializing SpaceManager");
//	        	for (ManagedReference<ServerSpace> space: roomRef.get().getSpaces()) {
//	//        		realSpace = new Space(, space.get().getId(), 0, 0);
//	        		realSpace = new Space(space.get().getName(), space.get().getId(),new int[]{110, 60, 140}, true, 0.5f, space.get().getScale());
//	            	SpaceManager.add(realSpace);
//	        	}
//	        	logger.log(Level.INFO, "SpaceManager READY; spaceCount: "+SpaceManager.size());
    		}
//        	}
        } else {
        	/** Here everything is already initialized, so we can start computing*/
	    	
        	
        	/** Start Entity Tasks */
        	UpdateWorldTaskSimple uwtReal;
	    	for(ManagedReference<UpdateWorldTaskSimple> uwt: uwtSimple) {
	    		uwtReal = uwt.get();
	    		uwtReal.run();
	    	}
	    	SendUpdatePlayersTaskSimple supReal;
	    	for (ManagedReference<SendUpdatePlayersTaskSimple> sup : supSimple) {
	    		supReal = sup.get();
	    		supReal.run();
	    	}
	    	
	    	/** Start Space Tasks */
	    	UpdateWorldSpaces uwsReal;
	    	for(ManagedReference<UpdateWorldSpaces> uws: uwsSimple) {
	    		uwsReal = uws.get();
	    		uwsReal.run();
	    	}
	    	SendUpdatePlayersSpaces supsReal;
	    	for (ManagedReference<SendUpdatePlayersSpaces> sups : supsSimple) {
	    		supsReal = sups.get();
	    		supsReal.run();
	    	}
	    	
	    	/** Start Players Task */
	    	SendUpdatePlayers supPlayer;
	    	for (ManagedReference<SendUpdatePlayers> supPl : supPlayers) {
	    		supPlayer = supPl.get();
	    		supPlayer.run();
	    	}
	    	
	 
	    	
        }
    }
	
    /** add an additional UpdateTask (size depends on Entities) */
	public boolean addUpdateTask(UpdateWorldTaskSimple uwt) {
	    logger.log(Level.INFO, "{0} placed in {1}",
	        new Object[] { uwt, this });
	
	    // NOTE: we can't directly save the item in the list, or
	    // we'll end up with a local copy of the item. Instead, we
	    // must save a ManagedReference to the item.
	
	    DataManager dataManager = AppContext.getDataManager();
	    dataManager.markForUpdate(this);
	
	    return uwtSimple.add(dataManager.createReference(uwt));
	}
	
	/** add an additional Space-UpdateTask (size depends on Spaces) */
	public boolean addUpdateTaskSpaces(UpdateWorldSpaces uws) {
	    logger.log(Level.INFO, "{0} placed in {1}",
	        new Object[] { uws, this });
	
	    // NOTE: we can't directly save the item in the list, or
	    // we'll end up with a local copy of the item. Instead, we
	    // must save a ManagedReference to the item.
	
	    DataManager dataManager = AppContext.getDataManager();
	    dataManager.markForUpdate(this);
	
	    return uwsSimple.add(dataManager.createReference(uws));
	}
	 
	 /** add an additional SendTask (size depends on Entities) */
	public boolean addSendTask(SendUpdatePlayersTaskSimple sup) {
        logger.log(Level.INFO, "{0} placed in {1}",
            new Object[] { sup, this });

        // NOTE: we can't directly save the item in the list, or
        // we'll end up with a local copy of the item. Instead, we
        // must save a ManagedReference to the item.

        DataManager dataManager = AppContext.getDataManager();
        dataManager.markForUpdate(this);

        return supSimple.add(dataManager.createReference(sup));
	}
	
	/** add an additional Spaces-SendTask (size depends on Spaces) */
	public boolean addSendTaskSpaces(SendUpdatePlayersSpaces sups) {
        logger.log(Level.INFO, "{0} placed in {1}",
            new Object[] { sups, this });

        // NOTE: we can't directly save the item in the list, or
        // we'll end up with a local copy of the item. Instead, we
        // must save a ManagedReference to the item.

        DataManager dataManager = AppContext.getDataManager();
        dataManager.markForUpdate(this);

        return supsSimple.add(dataManager.createReference(sups));
	}
	
	 /** add an additional SendTask (size depends on Entities) */
		public boolean addSendTaskPlayers(SendUpdatePlayers supPlayer) {
	        logger.log(Level.INFO, "{0} placed in {1}",
	            new Object[] { supPlayer, this });

	        // NOTE: we can't directly save the item in the list, or
	        // we'll end up with a local copy of the item. Instead, we
	        // must save a ManagedReference to the item.

	        DataManager dataManager = AppContext.getDataManager();
	        dataManager.markForUpdate(this);

	        return supPlayers.add(dataManager.createReference(supPlayer));
		}
		
		
		private void initializeRessources() {
			// Directory path to svg-files
			URL pathSVG = getClass().getResource(GameStates.resourcePath+"svg/");//GameStates.resourcePath+"svg/"; 
			 
			  String fileName;
			  File folder;
			try {
				folder = new File(pathSVG.toURI());
				File[] listOfFiles = folder.listFiles(); 
				 
				  for (int i = 0; i < listOfFiles.length; i++) 
				  {
				 
				   if (listOfFiles[i].isFile()) 
				   {
				   fileName = listOfFiles[i].getName();
				       if (fileName.endsWith(".svg") || fileName.endsWith(".SVG"))
				       {
				          // add to ArrayList
				    	   RessourcenManager.addSVG(fileName);
				        }
				     }
				  }
			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			  
			// Directory path to image-files
			URL pathImages = getClass().getResource(GameStates.resourcePath+"images/");//GameStates.resourcePath+"images/";
			  try {
				folder = new File(pathImages.toURI());
				File[] listOfFiles = folder.listFiles(); 
				 
				  for (int i = 0; i < listOfFiles.length; i++) 
				  {
				 
				   if (listOfFiles[i].isFile()) 
				   {
				   fileName = listOfFiles[i].getName();
				       if (fileName.endsWith(".png") || fileName.endsWith(".PNG"))
				       {
				          // add to ArrayList
				    	   RessourcenManager.addImage(fileName);
				        }
				     }
				  }
			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			  
		}
		
		
		public void initializeTileSetManager() {
			// just add all TileSets
			TileSet tileSet = new TileSet(GameStates.standardTileNamePlayer, "shipTileName", 50, 50);
			TileSet tileSet2 = new TileSet("tilesets/players/standardShip.png", "shipTileName2", 50, 50);
			TileSet tileSet_green = new TileSet("tilesets/players/standardShip_green.png", "shipTileName_green", 50, 50);
			TileSet tileSet_yellow = new TileSet("tilesets/players/standardShip_yellow.png", "shipTileName_yellow", 50, 50);
			TileSet tileSet_blue = new TileSet("tilesets/players/standardShip_blue.png", "shipTileName_blue", 50, 50);
			Entity playerEntity2 = new Entity(tileSet2, "localPlayer2", BigInteger.valueOf(0), 0, 0, GameStates.animationDelay);
			playerEntity2 = new Entity(tileSet, "localPlayer2", BigInteger.valueOf(0), 0, 0, GameStates.animationDelay);
			playerEntity2 = new Entity(tileSet_green, "localPlayer2", BigInteger.valueOf(0), 0, 0, GameStates.animationDelay);
			playerEntity2 = new Entity(tileSet_yellow, "localPlayer2", BigInteger.valueOf(0), 0, 0, GameStates.animationDelay);
			playerEntity2 = new Entity(tileSet_blue, "localPlayer2", BigInteger.valueOf(0), 0, 0, GameStates.animationDelay);
		}
		
	 
}