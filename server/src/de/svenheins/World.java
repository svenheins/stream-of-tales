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
import java.io.Serializable;
import java.util.Properties;
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
import com.sun.sgs.app.ManagedReference;
import com.sun.sgs.app.Task;
import com.sun.sgs.app.TaskManager;

import de.svenheins.main.GameStates;
import de.svenheins.managers.SpriteManager;
import de.svenheins.objects.ServerEntity;
import de.svenheins.objects.ServerSpace;
import de.svenheins.objects.ServerSprite;
import de.svenheins.objects.Space;
import de.svenheins.objects.Sprite;
import de.svenheins.objects.WorldObject;

/**
 * A tiny sample MUD application for the Project Darkstar Server.
 * <p>
 * There is a Room.  In the Room there is a Sword...
 */
public class World
    implements Serializable, AppListener, Task
{
    /** The version of the serialized form of this class. */
    private static final long serialVersionUID = 1L;

    /** The delay before the first run of the task. */
    public static final int DELAY_MS = 2500;
    public static final int DELAY_MS_WORLDUPDATE = 2500;

    /** The time to wait before repeating the task. */
    public static final int PERIOD_MS = 50;
    public static final int PERIOD_MS_WORLDUPDATE = 50;

    /** A reference to our subtasks, a {@link TrivialTimedTask}.  */
    private ManagedReference<TrivialTimedTask> subTaskRef = null;
    private ManagedReference<UpdateWorldTask> updateWorldTask = null;
    private ManagedReference<SendUpdatePlayersTask> sendUpdatePlayersTask = null;
    private UpdateWorldTaskSimple uwtSimple = null;
    
    /** The name of the first channel {@value #CHANNEL_1_NAME} */
    static final String CHANNEL_1_NAME = "Foo";
    /** The name of the second channel {@value #CHANNEL_2_NAME} */
    static final String CHANNEL_2_NAME = "Bar";
    
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
 

    /**
     * {@inheritDoc}
     * <p>
     * Creates the world within the MUD.
     */
    public void initialize(Properties props) {
    	// Hold onto the task (as a managed reference)
        setSubTask(new TrivialTimedTask());
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        //device =ge.getDefaultScreenDevice();
        GameStates.setWidth(ge.getMaximumWindowBounds().width);
        GameStates.setHeight(ge.getMaximumWindowBounds().height);

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
    	
    	
    	logger.info("Initializing World");

        // Create the Room
        WorldRoom room =
            new WorldRoom("Plain Room", "a nondescript room", 100.222, 300.333);
        WorldRoom room2 =
                new WorldRoom("Plain Room", "a nondescript room", 100.222, 300.333);

        // Create the sword
        WorldObject sword =
            new WorldObject("Shiny Sword", "a shiny sword.", 1.1212, 300);
        // Create the chair
        WorldObject chair =
            new WorldObject("Wooden Chair", "an old chair", 100, 3.555);
        
        // Create IngameObject
        //SimpleWorldObject p = new SimpleWorldObject();
        
        // Sprite object
        //Image img = (new ImageIcon(getClass().getResource(GamePanel.resourcePath+"images/"+"eye.png"))).getImage();
        String spriteString = "eye.png";
        Sprite sprite = SpriteManager.manager.getSprite(spriteString);
        ServerSprite s_sprite = new ServerSprite(spriteString, sprite.getHeight(), sprite.getWidth());
        String spriteString2 = "ship.png";
        Sprite sprite2 = SpriteManager.manager.getSprite(spriteString2);
        ServerSprite s_sprite2 = new ServerSprite(spriteString2, sprite2.getHeight(), sprite2.getWidth());
        int id = 1;
        double x = 100;
        double y = 100;
        double mx = 30;
        double my= 45;
        ServerEntity s_entity = new ServerEntity(s_sprite, id, x, y, mx, my);
        ServerEntity s_entity2 = new ServerEntity(s_sprite2, id+1, x+50, y+30, 20, 35);
        ServerEntity s_entity3 = new ServerEntity(s_sprite, id+2, x+50, y+30, 40, 30);
        
        String iterativSpriteString = "eye.png";
        Sprite it_sprite = SpriteManager.manager.getSprite(iterativSpriteString);
        ServerSprite it_s_sprite = new ServerSprite(iterativSpriteString, it_sprite.getHeight(), it_sprite.getWidth());
        ServerEntity it_s_entity;
        int numObjects = 50;
        double it_x, it_y, it_mx, it_my;
        for (int i = 0; i<numObjects; i++) {
        	it_x = Math.random()*GameStates.getWidth()-it_s_sprite.getWidth();
        	it_y = Math.random()*GameStates.getHeight()-it_s_sprite.getHeight();
        	it_mx = Math.random()*50+20;
        	it_my = Math.random()*50+20;
        	it_s_entity = new ServerEntity(it_s_sprite, i+2000, it_x, it_y, it_mx, it_my);
        	room.addEntity(it_s_entity);
        }
        
        /** add to room2 */
        String iterativSpriteString2 = "Qualle.png";
        Sprite it_sprite2 = SpriteManager.manager.getSprite(iterativSpriteString2);
        ServerSprite it_s_sprite2 = new ServerSprite(iterativSpriteString2, it_sprite2.getHeight(), it_sprite2.getWidth());
        ServerEntity it_s_entity2;
        int numObjects2 = 50;
        double it_x2, it_y2, it_mx2, it_my2;
        for (int i = 0; i<numObjects2; i++) {
        	it_x2 = Math.random()*GameStates.getWidth()-it_s_sprite2.getWidth();
        	it_y2 = Math.random()*GameStates.getHeight()-it_s_sprite2.getHeight();
        	it_mx2 = Math.random()*30+20;
        	it_my2 = Math.random()*30+20;
        	it_s_entity2 = new ServerEntity(it_s_sprite2, i+1000, it_x2, it_y2, it_mx2, it_my2);
        	room.addEntity(it_s_entity2);
        }
        
        String spaceString = "Zeichnung.svg";
        String spaceString2 = "Quadrat.svg";
        Space space = new Space(spaceString, 1, new int[]{250, 160, 40}, false, 1.0f);
        space.setMovement(10, 13);
        ServerSpace s_space = new ServerSpace(space);
        Space space2 = new Space(spaceString2, 2, new int[]{10, 160, 40}, true, 0.5f);
        space2.setMovement(100, 130);
        ServerSpace s_space2 = new ServerSpace(space2);
        Space space3 = new Space(spaceString, 3,new int[]{110, 60, 140}, true, 0.2f);
        space3.setMovement(80, 120);
        ServerSpace s_space3 = new ServerSpace(space3);
        Space space4 = new Space(spaceString, 4, new int[]{50, 240, 140}, true, 0.75f);
        space4.setMovement(250, 200);
        ServerSpace s_space4 = new ServerSpace(space4);
        
        
        // Put the Sword to the Room
        room.addItem(sword);
        room.addItem(chair);
        //room.addItem(p);
        
        //room.addImage(img);
//        room.addSprite(s_sprite);
        room.addEntity(s_entity);
        room.addEntity(s_entity2);
        room.addEntity(s_entity3);
        room.addSpace(s_space);
        room.addSpace(s_space2);
        room.addSpace(s_space3);
        room.addSpace(s_space4);
        

        // Keep a reference to the Room
        setRoom(room);
        
        setUpdateWorldTask(new UpdateWorldTask(room));
        setSendUpdatePlayersTask(new SendUpdatePlayersTask(room));
        uwtSimple = new UpdateWorldTaskSimple(room);

//        setUpdateWorldTask(new UpdateWorldTask(room2));
//        setSendUpdatePlayersTask(new SendUpdatePlayersTask(room2));

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
        // Get the subTask (from the ManagedReference that holds it)
        TrivialTimedTask subTask = getSubTask();
        
        if (subTask == null) {
            logger.log(Level.WARNING, "subTask is null");
            return;
        }
        
        // Delegate to the subTask's run() method
        subTask.run();
        
        //if((System.currentTimeMillis() % PERIOD_MS_WORLDUPDATE)-PERIOD_MS > PERIOD_MS_WORLDUPDATE/2){
//		    UpdateWorldTask uWT = getUpdateWorldTask();
//		    if (uWT == null) {
//		        logger.log(Level.WARNING, "updateWorldTask is null");
//		        return;
//		    }
//		    uWT.run();
//		    if (uwtSimple == null) {
//		        logger.log(Level.WARNING, "updateWorldTask is null");
//		        return;
//		    }
		    uwtSimple.run();
		    SendUpdatePlayersTask supTask = getSendUpdatePlayersTask();
		    if (supTask == null) {
		        logger.log(Level.WARNING, "sendUpdatePlayersTask is null");
		        return;
		    }
		    supTask.run();
        //}
    }
	
	
	/**
     * Gets the subtask this task delegates to.  Dereferences a
     * {@link ManagedReference} in this object that holds the subtask.
     * <p>
     * This null-check idiom is common when getting a ManagedReference.
     *
     * @return the subtask this task delegates to, or null if none is set
     */
    public TrivialTimedTask getSubTask() {
        if (subTaskRef == null) {
            return null;
        }

        return subTaskRef.get();
    }

    /**
     * Sets the subtask this task delegates to.  Stores the subtask
     * as a {@link ManagedReference} in this object.
     * <p>
     * This null-check idiom is common when setting a ManagedReference,
     * since {@link DataManager#createReference createReference} does
     * not accept null parameters.
     *
     * @param subTask the subtask this task should delegate to,
     *        or null to clear the subtask
     */
    public void setSubTask(TrivialTimedTask subTask) {
        if (subTask == null) {
            subTaskRef = null;
            return;
        }
        DataManager dataManager = AppContext.getDataManager();
        subTaskRef = dataManager.createReference(subTask);
    }
    
    private void setUpdateWorldTask(UpdateWorldTask uWT) {
		// TODO Auto-generated method stub
    	 if (uWT == null) {
             updateWorldTask = null;
             return;
         }
         DataManager dataManager = AppContext.getDataManager();
         this.updateWorldTask = dataManager.createReference(uWT);
	}
    
    public UpdateWorldTask getUpdateWorldTask() {
        if (updateWorldTask == null) {
            return null;
        }

        return updateWorldTask.get();
    }
    
    private void setSendUpdatePlayersTask(SendUpdatePlayersTask supTask) {
		// TODO Auto-generated method stub
    	 if (supTask == null) {
    		 sendUpdatePlayersTask = null;
             return;
         }
         DataManager dataManager = AppContext.getDataManager();
         this.sendUpdatePlayersTask = dataManager.createReference(supTask);
	}
    
    public SendUpdatePlayersTask getSendUpdatePlayersTask() {
        if (sendUpdatePlayersTask == null) {
            return null;
        }

        return sendUpdatePlayersTask.get();
    }
}