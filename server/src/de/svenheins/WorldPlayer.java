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


import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;

//import com.sun.darkstar.example.snowman.common.protocol.enumn.EOPCODE;
import com.sun.sgs.app.AppContext;
import com.sun.sgs.app.Channel;
import com.sun.sgs.app.ChannelManager;
import com.sun.sgs.app.ClientSession;
import com.sun.sgs.app.ClientSessionListener;
import com.sun.sgs.app.DataManager;
import com.sun.sgs.app.ManagedReference;
import com.sun.sgs.app.NameNotBoundException;

import de.svenheins.managers.EntityManager;
import de.svenheins.managers.PlayerManager;
import de.svenheins.managers.SpaceManager;
import de.svenheins.messages.OBJECTCODE;
import de.svenheins.messages.OPCODE;
import de.svenheins.messages.ServerMessages;
import de.svenheins.objects.Entity;
import de.svenheins.objects.Space;
import de.svenheins.objects.WorldObject;

/**
 * Represents a player in the {@link World} example MUD.
 */
public class WorldPlayer
    extends WorldObject
    implements ClientSessionListener
{
    /** The version of the serialized form of this class. */
    private static final long serialVersionUID = 1L;

    /** The {@link Logger} for this class. */
    private static final Logger logger =
        Logger.getLogger(WorldPlayer.class.getName());

    /** The message encoding. */
    public static final String MESSAGE_CHARSET = "UTF-8";

    /** The prefix for player bindings in the {@code DataManager}. */
    protected static final String PLAYER_BIND_PREFIX = "Player.";

    /** is ready??*/
    private boolean ready;
    
    private boolean initializing;
    
    /** The {@code ClientSession} for this player, or null if logged out. */
    private ManagedReference<ClientSession> currentSessionRef = null;

    /** The {@link WorldRoom} this player is in, or null if none. */
    private ManagedReference<WorldRoom> currentRoomRef = null;
    
    /**
     * Reference to the game channel used for communications
     */
    private ManagedReference<Channel> channelRef = null;

    /**
     * Find or create the player object for the given session, and mark
     * the player as logged in on that session.
     *
     * @param session which session to find or create a player for
     * @return a player for the given session
     */
    public static WorldPlayer loggedIn(ClientSession session, ManagedReference<Channel> channelRef) {
        String playerBinding = PLAYER_BIND_PREFIX + session.getName();
        BigInteger id = BigInteger.valueOf(playerBinding.hashCode());

        // try to find player object, if non existent then create
        DataManager dataMgr = AppContext.getDataManager();
        WorldPlayer player;

        try {
            player = (WorldPlayer) dataMgr.getBinding(playerBinding);
        } catch (NameNotBoundException ex) {
            // this is a new player
            player = new WorldPlayer(playerBinding);
//        	player = new WorldPlayer("ship.png");
            logger.log(Level.INFO, "New player created: {0}", player);
            dataMgr.setBinding(playerBinding, player);
        }
        player.setSession(session, channelRef);
        player.setId(id);
        Entity playerEntity = new Entity("ship.png", player.getId(), player.getX(), player.getY(), player.getHorizontalMovement(), player.getVerticalMovement());
        if (PlayerManager.add(playerEntity)) {
	        logger.log(Level.INFO, "New player created: {0}", player.getName());
	        logger.log(Level.INFO, "# of players: "+PlayerManager.size());
        }
        return player;
    }

    /**
     * Creates a new {@code WorldPlayer} with the given name.
     *
     * @param name the name of this player
     */
    protected WorldPlayer(String name) {
        super(name, "PlayerName", 0, 0);
        ready = false;
        initializing= false;
    }

    /**
     * Returns the session for this listener.
     * 
     * @return the session for this listener
     */
    protected ClientSession getSession() {
        if (currentSessionRef == null) {
            return null;
        }

        return currentSessionRef.get();
    }

    /**
     * Mark this player as logged in on the given session.
     *
     * @param session the session this player is logged in on
     */
    protected void setSession(ClientSession session, ManagedReference<Channel> channelRef) {
        DataManager dataMgr = AppContext.getDataManager();
        dataMgr.markForUpdate(this);

        if (session != null && channelRef != null) {
        	currentSessionRef = dataMgr.createReference(session);
            logger.log(Level.INFO,
                "Set session for {0} to {1}",
                new Object[] { this, session });
            // Join the session to all channels.  We obtain the channel
            // in two different ways, by reference and by name.
            ChannelManager channelMgr = AppContext.getChannelManager();
            
            // We were passed a reference to the first channel.
            this.channelRef = channelRef;
            this.channelRef.get().join(session);
            
            // We look up the second channel by name.
            Channel channel2 = channelMgr.getChannel(World.CHANNEL_2_NAME);
            channel2.join(session);
           
        }
        
    }

    /**
     * Handles a player entering a room.
     *
     * @param room the room for this player to enter
     */
    public void enter(WorldRoom room) {
        logger.log(Level.INFO, "{0} enters {1}",
            new Object[] { this, room }
        );
        room.addPlayer(this);
        setRoom(room);
    }
    
    /** {@inheritDoc} */
    public void send(ByteBuffer byteBuffer) {
        currentSessionRef.get().send(byteBuffer);
    }
    
    public void sendAll(ByteBuffer byteBuffer) {
    	channelRef.get().send(null, byteBuffer);
    }

    /** {@inheritDoc} */
    public void receivedMessage(ByteBuffer message) {
        //TODO: neu schreiben, ersten Byte vom ByteBuffer holen und in enum konvertieren
    	// dann: receivedMessage(ENUM, ByteBuffer)
    	OPCODE code = this.getOpCode(message);
    	this.receivedMessage(code, message);
    	
    	
//    	String command = decodeString(message);

//        logger.log(Level.INFO,
//            "{0} received command: {1}",
//            new Object[] { this, command }
//        );

//        if (command.equalsIgnoreCase("look")) {
//            String reply = getRoom().look(this);
//            getSession().send(encodeString(reply));
//        } else  if (command.equalsIgnoreCase("+")) {
//        	String reply = getRoom().addOne(this);
//            getSession().send(encodeString(reply));
//        } else  if (command.equalsIgnoreCase("eye")) {
//        	String reply = "eye:"+getRoom().getEye(this);
//            getSession().send(encodeString(reply));
//        } else {
//            logger.log(Level.WARNING,
//                "{0} unknown command: {1}",
//                new Object[] { this, command }
//            );
            // We could disconnect the rogue player at this point.
            //currentSession.disconnect();
//        }
    }

	public void receivedMessage(OPCODE opCode, ByteBuffer message) {
//		OBJECTCODE objCode = getObjectCode(message);
		switch (opCode) {
//			case EYE:
//				int id = message.getInt();
//				float[] state = getRoom().getEntityState(this, id);
//				getSession().send(ServerMessages.sendEntityState(id, state[0], state[1], state[2], state[3]));
//				break;
//			case SPACE:
//				int space_id = message.getInt();
//				float[] space_state = getRoom().getSpaceState(this, space_id);
//				getSession().send(ServerMessages.sendSpaceState(space_id, space_state[0], space_state[1], space_state[2], space_state[3]));
//				break;
//			case OBJECTSTATE:
//				int object_id = message.getInt();
//				OBJECTCODE objCode = OBJECTCODE.values()[message.getInt()];
//				/** get the six object-states: x,y,mx,my,width,height */
//				float[] object_state = getRoom().getObjectState(objCode, this, object_id);
//				getSession().send(ServerMessages.sendObjectState(objCode, object_id, object_state));
//				break;
			case INITENTITIES:
				if(!this.isInitializing()) {
					setInitializing(true);
					int countEntities = currentRoomRef.get().getCountEntities();
//					logger.log(Level.INFO, "countEntities = "+countEntities);
					int end = 0;
					Entity[] entityArray;
	//				entityArray = getRoom().getEntities(this, 0, countEntities);
	//				getSession().send(ServerMessages.sendEntities(entityArray));
					for (int begin = 0; end<countEntities; begin+=200) {
	//					begin = i*(countEntities/countPackets);
	//					end= (i+1)*(countEntities/ countPackets);
						end = begin+200;
						if (end> countEntities) end = countEntities;
						entityArray = getRoom().getEntities(this, begin, end);
//						logger.log(Level.INFO, "got entityArray: "+ entityArray[0].getName());

						getSession().send(ServerMessages.sendEntities(entityArray));
//						logger.log(Level.INFO, "packet send");
					
					}
					this.ready = true;
					this.setInitializing(false);
				}
				break;
			case INITSPACES:
				int countSpaces = currentRoomRef.get().getCountSpaces();
				Space[] spaceArray;
				int end = 0;
				for (int begin = 0; end<countSpaces; begin+=200) {
					end = begin+200;
					if (end> countSpaces) end = countSpaces;
					spaceArray = getRoom().getSpaces(this, begin, end);
//										logger.log(Level.INFO, "got entityArray: "+ entityArray[0].getName());

					getSession().send(ServerMessages.sendSpaces(spaceArray));
//										logger.log(Level.INFO, "packet send");
				
				}
				break;
//				OBJECTCODE objInitCode = OBJECTCODE.values()[message.getInt()];
//				/** get the six object-states: x,y,mx,my,width,height */
//				if (objInitCode == OBJECTCODE.ENTITY) {
//					Entity[] entityArray = getRoom().getEntities(this);
//					getSession().send(ServerMessages.sendEntities(entityArray));
//				} else if (objInitCode == OBJECTCODE.SPACE) {
//					Space[] spaceArray = getRoom().getSpaces(this);
//					getSession().send(ServerMessages.sendSpaces(spaceArray));
//				}
				
//				logger.log(Level.INFO,
//		                "Ids: {0} and {1}",
//		                new Object[] {ids[0], ids[1]}
//		            );
//				break;
//			case OBJECTNAME:
//				int objectNameId = message.getInt();
//				OBJECTCODE objCodeName = OBJECTCODE.values()[message.getInt()];
//				/** get the six object-states: x,y,mx,my,width,height */
//				String objectName = getRoom().getObjectName(objCodeName, this, objectNameId);
//				getSession().send(ServerMessages.sendObjectName(objCodeName, objectNameId, objectName));
//				break;
			case EDIT_OBJECT:
				OBJECTCODE objCode = OBJECTCODE.values()[message.getInt()];
//				byte[] bigByte = new byte[message.getInt()];
//				for (int i =0; i<bigByte.length; i++) {
//					bigByte[i] = message.get();
//				}
	    		BigInteger objectId = BigInteger.valueOf(message.getLong());
	    		float objectX = message.getFloat();
	    		float objectY = message.getFloat();
	    		float objectMX = message.getFloat();
	    		float objectMY = message.getFloat();
	    		float objectWidth = message.getFloat();
	    		float objectHeight = message.getFloat();
	    		// Update the entities/ spaces of the WorldRoom
	    		if (objCode == OBJECTCODE.SPACE) {
	    			SpaceManager.updateSpace(objectId, objectX, objectY, objectMX, objectMY);
	    			getRoom().editSpace(objectId, new float[]{objectX, objectY, objectMX, objectMY, objectWidth, objectHeight});
	    		}
	    		if (objCode == OBJECTCODE.ENTITY) {
	    			EntityManager.updateEntity(objectId, objectX, objectY, objectMX, objectMY);
	    			getRoom().editEntity(objectId, new float[]{objectX, objectY, objectMX, objectMY, objectWidth, objectHeight});
	    		}
	    		if (objCode == OBJECTCODE.PLAYER) {
	    			PlayerManager.updatePlayer(this.getId(), objectX, objectY, objectMX, objectMY);
	    			getRoom().editPlayer(this.getId(), new float[]{objectX, objectY, objectMX, objectMY, objectWidth, objectHeight});
	    		}
	    		
				break;
			case RESPAWN:
	//		    int respawnId = packet.getInt();
	//		    float respawnX = packet.getFloat();
	//		    float respawnY = packet.getFloat();
	//		    logger.log(Level.FINEST, "Processing {0} packet : {1}, {2}, {3}",
	//		               new Object[]{code, respawnId, respawnX, respawnY});
	//		    unit.respawn(respawnId,
	//		                 respawnX,
	//		                 respawnY);
			    break;
			case CHAT:
	//		    int sourceID = message.getInt();
	//		    byte[] messageBytes = new byte[message.getInt()];
	//		    message.get(messageBytes);
	//		    String strMessage = new String(messageBytes);
	//		    logger.log(Level.FINEST, "Processing {0} packet : {1}, {2}",
	//		               new Object[]{opCode, sourceID, strMessage});
	//		    unit.chatMessage(sourceID,
	//		                     strMessage);
				break;
			default:
			    //divert to common parser
	//		    this.parseCommonPacket(opCode, message);
				;
			}
	}

    /** {@inheritDoc} */
    public void disconnected(boolean graceful) {
        setSession(null, null);
        logger.log(Level.INFO, "Disconnected: {0}", this);
        getRoom().removePlayer(this);
        PlayerManager.remove(this.getId());
        
        
        setRoom(null);
    }

    /**
     * Returns the room this player is currently in, or {@code null} if
     * this player is not in a room.
     * <p>
     * @return the room this player is currently in, or {@code null}
     */
    protected WorldRoom getRoom() {
        if (currentRoomRef == null) {
            return null;
        }

        return currentRoomRef.get();
    }

    /**
     * Sets the room this player is currently in.  If the room given
     * is null, marks the player as not in any room.
     * <p>
     * @param room the room this player should be in, or {@code null}
     */
    protected void setRoom(WorldRoom room) {
        DataManager dataManager = AppContext.getDataManager();
        dataManager.markForUpdate(this);

        if (room == null) {
            currentRoomRef = null;
            return;
        }

        currentRoomRef = dataManager.createReference(room);
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder(getName());
        buf.append('@');
        if (getSession() == null) {
            buf.append("null");
        } else {
            buf.append(currentSessionRef.getId());
        }
        return buf.toString();
    }

    /**
     * Encodes a {@code String} into a {@link ByteBuffer}.
     *
     * @param s the string to encode
     * @return the {@code ByteBuffer} which encodes the given string
     */
    protected static ByteBuffer encodeString(String s) {
        try {
            return ByteBuffer.wrap(s.getBytes(MESSAGE_CHARSET));
        } catch (UnsupportedEncodingException e) {
            throw new Error("Required character set " + MESSAGE_CHARSET +
                " not found", e);
        }
    }
    
    
    /**
     * Encodes a {@code Integer} into a {@link ByteBuffer}.
     *
     * @param i the Integer to encode
     * @return the {@code ByteBuffer} which encodes the given Integer
     */
    protected static ByteBuffer encodeInteger(int i){
    	ByteBuffer bb = ByteBuffer.allocate(1024);
    	bb.asIntBuffer().put(i);
        return bb;
        
    }

    /**
     * Decodes a message into a {@code String}.
     *
     * @param message the message to decode
     * @return the decoded string
     */
    protected static String decodeString(ByteBuffer message) {
        try {
            byte[] bytes = new byte[message.remaining()];
            message.get(bytes);
            return new String(bytes, MESSAGE_CHARSET);
        } catch (UnsupportedEncodingException e) {
            throw new Error("Required character set " + MESSAGE_CHARSET +
                " not found", e);
        }
    }
    
    /**
     * Decodes a message into a {@code int}.
     *
     * @param message the int to decode
     * @return the decoded int
     */
    protected static int decodeInteger(ByteBuffer message) {
        return message.getInt();
    }
    
    /**
     * Get the OPCODE from the packet
     * @param packet
     * @return
     */
    private OPCODE getOpCode(ByteBuffer packet) 
    {
        byte opbyte = packet.get();
        if ((opbyte < 0) || (opbyte > OPCODE.values().length - 1)) {
            logger.severe("Unknown op value: " + opbyte);
            return null;
        }
        OPCODE code = OPCODE.values()[opbyte];
        
        return code;
    }
    
//    /**
//     * Get the OPCODE from the packet
//     * @param packet
//     * @return
//     */
//    private OBJECTCODE getObjectCode(ByteBuffer packet) 
//    {
//       // byte opbyte = packet.get();
//        byte objbyte = packet.get(1);
//        if ((objbyte < 0) || (objbyte > OBJECTCODE.values().length - 1)) {
//            logger.severe("Unknown op value: " + objbyte);
//            return null;
//        }
//        OBJECTCODE code = OBJECTCODE.values()[objbyte];
//        
//        return code;
//    }
    
    public boolean isReady() {
    	return ready;
    }
    
    public void setReady(boolean bool) {
    	this.ready = bool;
    }
    
    public boolean isInitializing() {
    	return initializing;
    }
    
    public void setInitializing(boolean init) {
    	this.initializing=init;
    }
}