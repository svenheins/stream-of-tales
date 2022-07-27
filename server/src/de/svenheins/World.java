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

/**
 * A tiny sample MUD application for the Project Darkstar Server.
 * <p>
 * There is a Room.  In the Room there is a Sword...
 */
public class World
    implements Serializable, AppListener
{
    /** The version of the serialized form of this class. */
    private static final long serialVersionUID = 1L;

    
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
    private ManagedReference<WorldRoom> roomRef = null;

    /**
     * {@inheritDoc}
     * <p>
     * Creates the world within the MUD.
     */
    public void initialize(Properties props) {
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

        // Create the sword
        WorldObject sword =
            new WorldObject("Shiny Sword", "a shiny sword.", 1.1212, 300);
        // Create the chair
        WorldObject chair =
            new WorldObject("Wooden Chair", "an old chair", 100, 3.555);

        // Put the Sword to the Room
        room.addItem(sword);
        room.addItem(chair);

        // Keep a reference to the Room
        setRoom(room);

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

        roomRef = dataManager.createReference(room);
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
}