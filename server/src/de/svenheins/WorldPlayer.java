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


import java.awt.Point;
import java.awt.Polygon;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
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

import de.svenheins.main.AttributeType;
import de.svenheins.main.EntityStates;
import de.svenheins.main.GameStates;
import de.svenheins.main.Priority;
import de.svenheins.managers.EntityManager;
import de.svenheins.managers.PlayerManager;
import de.svenheins.managers.ServerTextureManager;
import de.svenheins.managers.SpaceManager;
import de.svenheins.managers.SpriteManager;
import de.svenheins.managers.TileSetManager;
import de.svenheins.messages.ITEMCODE;
import de.svenheins.messages.OBJECTCODE;
import de.svenheins.messages.OPCODE;
import de.svenheins.messages.ServerMessages;
import de.svenheins.objects.InteractionTile;
import de.svenheins.objects.PlayerEntity;
import de.svenheins.objects.ServerAreaInfluence;
import de.svenheins.objects.ServerContainer;
import de.svenheins.objects.ServerEntity;
import de.svenheins.objects.ServerItem;
import de.svenheins.objects.ServerPlayer;
import de.svenheins.objects.ServerSpace;
import de.svenheins.objects.ServerSprite;
import de.svenheins.objects.Space;
import de.svenheins.objects.Sprite;
import de.svenheins.objects.WorldLatticePosition;
import de.svenheins.objects.WorldObject;
import de.svenheins.objects.items.Item;
import de.svenheins.objects.items.ItemFactoryCollection;

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
    
    private boolean initializingEntities;
    private boolean initializingItems;
    private boolean initializingAreaInfluences;
    private boolean initializingGrowthSpots;
    
    private boolean readyForNextMessage= true;
    
    /** The {@code ClientSession} for this player, or null if logged out. */
    private ManagedReference<ClientSession> currentSessionRef = null;

    /** The {@link WorldRoom} this player is in, or null if none. */
    private ManagedReference<WorldRoom> currentRoomRef = null;
    
    /** channel-map */
    private final HashMap<BigInteger, String> spaceChannels =
            new HashMap<BigInteger, String>();
    
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
//        BigInteger id = BigInteger.valueOf(playerBinding.hashCode());
//        logger.log(Level.INFO, "New ID created: {0}", new Object[]{id});
        
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
        player.setInitializingEntities(false);
        player.setInitializingItems(false);
        player.setInitializingAreaInfluences(false);
        player.setInitializingGrowthSpots(false);
//        player.setId(id);
        
        String playerName = player.getName().substring(player.getName().indexOf(".")+1, player.getName().length());
//        TileSet tile = TileSetManager.manager.getTileSet("ship.png");
////        Entity playerEntity = new Entity(tile, playerName, player.getId(), player.getX(), player.getY(), 300);
//        Entity playerEntity = new Entity(tile, playerName, id, player.getX(), player.getY(), 300);
//        if (PlayerManager.add(playerEntity)) {
//	        logger.log(Level.INFO, "New player created: {0}, ID = {1}", new Object[]{player.getName(), player.getId()});
//	        logger.log(Level.INFO, "# of players: "+PlayerManager.size());
//	        logger.log(Level.INFO, "TileSet-Name "+PlayerManager.get(id).getTileSet().getName());
//        }
        
        if (ServerTextureManager.manager.containsPlayer(playerName)) {
        	logger.log(Level.INFO, "Player deleted from ServerTextureManager: {0}", player.getName());
        	ServerTextureManager.manager.removePlayer(playerName);
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
        initializingEntities= false;
        initializingItems= false;
        initializingAreaInfluences = false;
        initializingGrowthSpots = false;
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
            String playerName = this.getName().substring(this.getName().indexOf(".")+1, this.getName().length());
            String channelAdd = playerName.substring(0,1);
            
            if (channelAdd.toLowerCase().equals("u")) {
            	logger.log(Level.INFO,
                        "Set extra channel for {0} to {1}",
                        new Object[] { this, World.CHANNEL_2_NAME });
	            Channel channel2 = channelMgr.getChannel(World.CHANNEL_2_NAME);
	            channel2.join(session);
            }
            
            /** add global chat channel */
            Channel channelChatGlobal = channelMgr.getChannel(World.CHANNEL_CHAT_GLOBAL);
            channelChatGlobal.join(session);
        }
    }
    
    /** register for spaceChannel with ID = spaceId */
    public void joinSpaceChannel(BigInteger spaceId) {
    	if(!spaceChannels.containsKey(spaceId)) {
    		String channelName = "SpaceChannel_"+spaceId;
    		this.spaceChannels.put(spaceId, channelName);
	    	logger.log(Level.INFO,
	                "Adding {0} to {1}",
	                new Object[] { this, channelName });
	    	ChannelManager channelMgr = AppContext.getChannelManager();
	    	Channel spaceChannel = channelMgr.getChannel(channelName);
	    	spaceChannel.join(currentSessionRef.get());
    	}
    }
    
    /** leave spaceChannel with ID = spaceId */
    public void leaveSpaceChannel(BigInteger spaceId) {
    	if(spaceChannels.containsKey(spaceId)) {
    		String channelName = "SpaceChannel_"+spaceId;
    		this.spaceChannels.remove(spaceId);
	    	logger.log(Level.INFO,
	                "{0} is leaving {1}",
	                new Object[] { this, channelName });
	    	ChannelManager channelMgr = AppContext.getChannelManager();
	    	Channel spaceChannel = channelMgr.getChannel(channelName);
	    	spaceChannel.leave(currentSessionRef.get());
	    	
    	}
    }
    
    /** clear SpaceChannels */
    public void emptySpaceChannels() {
    	if (!spaceChannels.isEmpty()) {
    		for(BigInteger spaceId : spaceChannels.keySet()) {
    			leaveSpaceChannel(spaceId);
    		}
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
        setWorldRoom(room);
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
//            String reply = getWorldRoom().look(this);
//            getSession().send(encodeString(reply));
//        } else  if (command.equalsIgnoreCase("+")) {
//        	String reply = getWorldRoom().addOne(this);
//            getSession().send(encodeString(reply));
//        } else  if (command.equalsIgnoreCase("eye")) {
//        	String reply = "eye:"+getWorldRoom().getEye(this);
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
		String thisPlayerName = this.getName().substring(this.getName().indexOf(".")+1, this.getName().length());
		switch (opCode) {
//			case EYE:
//				int id = message.getInt();
//				float[] state = getWorldRoom().getEntityState(this, id);
//				getSession().send(ServerMessages.sendEntityState(id, state[0], state[1], state[2], state[3]));
//				break;
//			case SPACE:
//				int space_id = message.getInt();
//				float[] space_state = getWorldRoom().getSpaceState(this, space_id);
//				getSession().send(ServerMessages.sendSpaceState(space_id, space_state[0], space_state[1], space_state[2], space_state[3]));
//				break;
//			case OBJECTSTATE:
//				int object_id = message.getInt();
//				OBJECTCODE objCode = OBJECTCODE.values()[message.getInt()];
//				/** get the six object-states: x,y,mx,my,width,height */
//				float[] object_state = getWorldRoom().getObjectState(objCode, this, object_id);
//				getSession().send(ServerMessages.sendObjectState(objCode, object_id, object_state));
//				break;
			case INITENTITIES:{
				if(!this.isInitializingEntities()) {
					/** add playerId in the initializing HashMap<BigInteger, Iterator<BigInteger>> of the room */
					this.getWorldRoom().addPlayerInitializingEntities(this.getId());
					
					setInitializingEntities(true);
//					int countEntities = currentRoomRef.get().getCountEntities();
//					int end = 0;
//					Entity[] entityArray;
//					Iterator<BigInteger> itKeys = getWorldRoom().getEntities().get().keySet().iterator();
//					
//					for (int begin = 0; end<countEntities; begin+=10) {
//						end = begin+10;
//						if (end> countEntities) end = countEntities;
//						entityArray = getWorldRoom().getEntities(this, begin, end, itKeys);
//						getSession().send(ServerMessages.sendEntities(entityArray));
//					
//					}
					
				}
				break;}
			case INITITEMS:{
				if(!this.isInitializingItems()) {
					/** add playerId in the initializing HashMap<BigInteger, Iterator<BigInteger>> of the room */
					this.getWorldRoom().addPlayerInitializingItems(this.getId());
					
					setInitializingItems(true);					
				}
				
				break;
			}
			case INITAREAINFLUENCES: {
				if(!this.isInitializingAreaInfluences()) {
					/** add playerId in the initializing HashMap<BigInteger, Iterator<BigInteger>> of the room */
					this.getWorldRoom().addPlayerInitializingAreaInfluences(this.getId());
					
					setInitializingAreaInfluences(true);					
				}
				
				break;
			}
			case INITGROWTHSPOTS: {
				if(!this.isInitializingGrowthSpots()) {
					/** add playerId in the initializing HashMap<BigInteger, Iterator<BigInteger>> of the room */
					this.getWorldRoom().addPlayerInitializingGrowthSpots(this.getId());
					
					setInitializingGrowthSpots(true);					
				}
				
				break;
			}
			case INITSPACES:{
				this.initSpaces();
				break;}

			case INITPLAYERS:{
				this.initPlayers();
				break;
			}
			case INITME: {
				this.initMe();
				break; }
			case LOGOUT: {
				this.disconnected(true);
				break;
			}
			case JOINSPACECHANNEL: {
				BigInteger spaceId = BigInteger.valueOf(message.getLong());
				this.joinSpaceChannel(spaceId);
				break; }
			case LEAVESPACECHANNEL: {
				BigInteger spaceIdLeave = BigInteger.valueOf(message.getLong());
				this.leaveSpaceChannel(spaceIdLeave);
				break; }
			case PLAYERDATA: {
				BigInteger playerId = BigInteger.valueOf(message.getLong());
				this.sendPlayerData(playerId);
//				this.sendPlayersMyUpdatedEquipment(thisPlayerName);
				break; }
//			case EDIT_PLAYER_ADDONS: {
//				/** ID */
//				BigInteger objectId_player = BigInteger.valueOf(message.getLong()); // 8
//		        
//				byte[] nameBytes_player = new byte[message.getInt()];
//    			message.get(nameBytes_player);
//    			String name_player = new String(nameBytes_player); // name
//    			byte[] tileNameBytes_player = new byte[message.getInt()];
//    			message.get(tileNameBytes_player);
//    			String tileName = new String(tileNameBytes_player); // name
//    			byte[] tilePathNameBytes_player = new byte[message.getInt()];
//    			message.get(tilePathNameBytes_player);
//    			String tilePathName = new String(tilePathNameBytes_player); // name
//		    	int tileWidth = message.getInt(); // 4 
//		    	int tileHeight = message.getInt(); // 4 
//		    	byte[] countryBytes = new byte[message.getInt()];
//    			message.get(countryBytes);
//    			String country = new String(countryBytes); // name
//    			byte[] groupNameBytes = new byte[message.getInt()];
//    			message.get(groupNameBytes);
//    			String groupName = new String(groupNameBytes); // name
//		    	int experience = message.getInt(); // 4
//		    	
//		    	PlayerManager.updatePlayerAddons(objectId_player, thisPlayerName, tileName, tilePathName, tileWidth, tileHeight, country, groupName, experience);
//		    	this.getWorldRoom().editPlayerAddons(objectId_player, thisPlayerName, tileName, tilePathName, tileWidth, tileHeight, country, groupName, experience);
//		    	this.getWorldRoom().sendEditPlayerAddons(objectId_player, thisPlayerName, tileName, tilePathName, tileWidth, tileHeight, country, groupName, experience);
//		    	
//		    	
//				break; }
			case OBJECTSTATE: {
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
//	    		float objectWidth = message.getFloat();
//	    		float objectHeight = message.getFloat();
	    		// Update the entities/ spaces of the WorldRoom
	    		if (objCode == OBJECTCODE.SPACE) {
	    			SpaceManager.updateSpace(objectId, objectX, objectY, objectMX, objectMY);
	    			getWorldRoom().editSpace(objectId, new float[]{objectX, objectY, objectMX, objectMY});
	    		}
	    		if (objCode == OBJECTCODE.ENTITY) {
	    			EntityManager.updateEntity(objectId, objectX, objectY, objectMX, objectMY);
	    			getWorldRoom().editEntity(objectId, new float[]{objectX, objectY, objectMX, objectMY});
	    		}
	    		if (objCode == OBJECTCODE.PLAYER) {
	    			PlayerManager.updatePlayer(this.getId(), objectX, objectY, objectMX, objectMY);
	    			getWorldRoom().editPlayer(this.getId(), new float[]{objectX, objectY, objectMX, objectMY});
	    		}
	    		
				break;
			}
				/** parse upload message */
			case UPLOAD_OBJECT: {
				OBJECTCODE objCodeUpload = OBJECTCODE.values()[message.getInt()];
				if(objCodeUpload == OBJECTCODE.SPACE) {
					BigInteger id = BigInteger.valueOf(message.getLong());
//					System.out.println("got ID: "+id);
					byte[] nameBytes = new byte[message.getInt()];
	    			message.get(nameBytes);
	    			String name = new String(nameBytes); // name
		    		int lengthOfPubCoord = message.getInt();
		    		int[] pubXCoord = new int[lengthOfPubCoord];
		    		int[] pubYCoord = new int[lengthOfPubCoord];
		    		for (int i = 0; i < lengthOfPubCoord; i++) {
		    			pubXCoord[i] = message.getInt();
		    			pubYCoord[i] = message.getInt();
		    		}
		    		int[] rgb = new int[]{message.getInt(),message.getInt(),message.getInt()};
		    		float trans = message.getFloat() ; // 4 Bytes
		    		int filledInt = message.getInt();
		    		boolean filled;
		    		if (filledInt == 1) filled = true; else filled = false; 
		    		float scale = message.getFloat() ;
		            float area = message.getFloat() ;
		            
		            byte[] textureNameBytes = new byte[message.getInt()];
	    			message.get(textureNameBytes);
	    			String textureName = new String(textureNameBytes); // name
		            
		            int polyX = message.getInt();
		            int polyY = message.getInt();
		    		
		            int numberOfPolygons = message.getInt();
		            ArrayList<Polygon> polygon = new ArrayList<Polygon>();
		            Polygon addPolygon;
		    		for (int i = 0; i < numberOfPolygons; i++) {
		    			int numberOfActualPolygon = message.getInt();
//		    			System.out.println("number of edges: "+ numberOfActualPolygon);
		    			int[] xpoints = new int[numberOfActualPolygon];
		    			int[] ypoints = new int[numberOfActualPolygon];
		    			for (int j = 0; j < numberOfActualPolygon; j++) {
		    				xpoints[j] = message.getInt();
		    				ypoints[j] = message.getInt();
		    			}
		    			addPolygon = new Polygon(xpoints, ypoints, numberOfActualPolygon);
		    			polygon.add(addPolygon);
		    		}
					
		    		/** now everything is well prepared */
		    		Space spaceAdd = new Space(polygon, polyX, polyY, name, id, rgb, filled, trans, scale, area, textureName);
		    		//if (polygon.size() > 1) System.out.println(polygon.get(1).npoints);
//		    		SpaceManager.add(spaceAdd);
		    		/** first create the world object then the temporarily SpaceManager-Object */
		    		getWorldRoom().addSpace(new ServerSpace(spaceAdd));
		    		/** now we got a new ID saved in the room */
		    		spaceAdd.setId(getWorldRoom().getLastAddedSpaceID());
//		    		System.out.println("last ID="+ spaceAdd.getId());
			        SpaceManager.add(spaceAdd);	
			        
		    		//ServerSpace s_space =ServerRegion getWorldRoom().getSpaces().
		    		//Space spaceAddManager = new Space()
				}
				
				break; }
			case INITTEXTURES: {
//				if (!ServerTextureManager.manager.containsPlayer(thisPlayerName) || ServerTextureManager.manager.getLengthOfUploadTexture(thisPlayerName)>0) {
				ServerTextureManager.manager.createPlayerUploadTexture(thisPlayerName);	
				logger.log(Level.INFO,
			                "Player {0} will get textures now!!!",
			                new Object[] { thisPlayerName});
					this.sendAvailableTextures();
					
					
//				} else {
//					logger.log(Level.INFO,
//			                "Player {0} is already initialized for the textures-upload!",
//			                new Object[] { thisPlayerName});
//				}
					
				break; }
				/** parse upload Texture */
			case UPLOAD_TEXTURE: {
				byte[] nameBytes = new byte[message.getInt()];
	    		message.get(nameBytes);
	    		String name = new String(nameBytes); // name
		    	int packetId = message.getInt(); // packet id
		    	int countPackets = message.getInt(); // packet count
		    	int sizeOfActualPacket = message.getInt(); // packet size
		    	/** get the image bytes */
		    	byte[] imageBytes = new byte[sizeOfActualPacket];
		    	message.get(imageBytes);
		    	/** get Player name */
		    	byte[] playerNameBytes = new byte[message.getInt()];
	    		message.get(playerNameBytes);
	    		String playerName = new String(playerNameBytes); // name of sending player
		    	
	    		logger.log(Level.INFO, "got the packet={0} for texture {1} from player {2} with {3} bytes",
	    	            new Object[] { packetId, name, playerName, sizeOfActualPacket });
	    		
	    		if (!ServerTextureManager.manager.contains(name)) {
		    		/** init in the first step */
		    		if(packetId == 0) {
		    			ServerTextureManager.manager.initDownload(name, countPackets, playerName);
		    		}
		    		ServerTextureManager.manager.getPartOfDownload(name, packetId, imageBytes, playerName);
		    		
		    		if (packetId < countPackets-1) {
		    			/** send the "received!!"-message if there are textures remaining */
		    			getSession().send(ServerMessages.sendReadyForNextTexturePacket(this.getName(), packetId));
		    		} else if (packetId == countPackets-1) {
		    			/** texture "name" is ready to send it to other players except player "playerName" */
		    			logger.log(Level.INFO, "texture {0} is ready and will now be sent to players except {1}",
			    	            new Object[] { name, playerName});
		    			this.getWorldRoom().sendTextureToPlayers(name, playerName);
		    		}
	    		} else {
	    			logger.log(Level.INFO, "got an upload request for {0} by player {1} but denied it, because the texture is already there!",
		    	            new Object[] { name, playerName});
	    			this.getWorldRoom().sendTextureToPlayers(name, playerName);
	    		}
	    		
				break; }
			case READY_FOR_NEXT_TEXTURE_PACKET: {
				byte[] playerNameBytes_Ready = new byte[message.getInt()];
				message.get(playerNameBytes_Ready);
				String namePlayer = new String(playerNameBytes_Ready); // name
	    		int oldPacket = message.getInt();
	    		System.out.println("OK, packet "+oldPacket +" is ready, sending next one!");
	    		
	    		/** get next Packet and send it to the server */			
	    		byte[] imagePacket = ServerTextureManager.manager.getTexturePacket(thisPlayerName, oldPacket+1);
	    		String textureName = ServerTextureManager.manager.getUploadTextureName(thisPlayerName);
	    		/** send the next packet */
	    		getSession().send(ServerMessages.uploadTexture(textureName, oldPacket+1, ServerTextureManager.manager.getNumberOfPacketsUploadTexture(thisPlayerName) , imagePacket.length, imagePacket, getSession().getName()));
	    		
	    		break;
				}
			case READY_FOR_NEXT_TEXTURE: {
				
				sendAvailableTextures();
				
				break; }
			case SEND_MISSING_TEXTURES: {
				if (this.isReadyForNextMessage()) {
					this.setReadyForNextMessage(false);
					ArrayList<String> textureNames = new ArrayList<String>();
		    		int countTextures = message.getInt();
		    		for (int i = 0; i < countTextures; i++) {
		    			byte[] nameTextureBytes = new byte[message.getInt()];
		    			message.get(nameTextureBytes);
		    			String nameTexture = new String(nameTextureBytes); // name
		    			textureNames.add(nameTexture);
		    			logger.log(Level.INFO, "missing texture: {0}",
			    	            new Object[] { textureNames});
		    		}

			    	String texture = textureNames.get(0);
			    	if (!ServerTextureManager.manager.getUploadTextureName(thisPlayerName).equals(texture)) {
			    		ServerTextureManager.manager.prepareTextureForUpload(thisPlayerName, texture);
			    	}

			    	getSession().send(ServerMessages.sendTextureStart(thisPlayerName, texture));
				}

				break; }
			case EDIT_SPACE_ADDONS: {
				/** get the message */
				BigInteger id = BigInteger.valueOf(message.getLong()); // 8 Bytes
				byte[] textureNameBytes = new byte[message.getInt()];
				message.get(textureNameBytes);
				String nameTexture = new String(textureNameBytes); // name
				int[] rgb = new int[3];
		    	rgb[0] = message.getInt();
		    	rgb[1] = message.getInt();
		    	rgb[2] = message.getInt();
		    	float trans = message.getFloat();
		    	int filled = message.getInt();
		    	float scale = message.getFloat();
		    	float area = message.getFloat();
		    	
		    	/** change the corresponding serverspace and space and send an update to players */
		    	this.getWorldRoom().editSpaceAddons(id, nameTexture, rgb, trans, filled, scale, area);
		    	//SpaceManager.editSpaceAddons(id, nameTexture, rgb, trans, filled, scale, area);
		    	this.getWorldRoom().sendEditSpaceAddons(id, nameTexture, rgb, trans, filled, scale, area);
		    	
				break; }
			case ADDITEM: {
				BigInteger addItemID = BigInteger.valueOf(message.getLong()); // 8 Bytes
				ITEMCODE itemCode_ADDITEM = getItemCode(message); // 1 Byte
				int count_ADDITEM = message.getInt(); // 4 Bytes
				int capacity_ADDITEM = message.getInt(); // 4
				float x_ADDITEM = message.getFloat(); // 4
				float y_ADDITEM= message.getFloat(); // 4
				float mx_ADDITEM= message.getFloat(); // 4
				float my_ADDITEM= message.getFloat(); // 4
				int itemStatesLength_ADDITEM; // 4
				float[] itemStates_ADDITEM; // 4*Length
				
				byte[] itemName_ADDITEM_Bytes = new byte[message.getInt()];
				message.get(itemName_ADDITEM_Bytes);
				String itemName_ADDITEM = new String(itemName_ADDITEM_Bytes); // name
				
				byte[] commentBytes = new byte[message.getInt()];
				message.get(commentBytes);
				String comment = new String(commentBytes); // comment
				
				byte[] itemRoomBytes = new byte[message.getInt()];
				message.get(itemRoomBytes);
				String itemRoom = new String(itemRoomBytes); // room

				byte[] spriteString_ADDITEM_Bytes = new byte[message.getInt()];
				message.get(spriteString_ADDITEM_Bytes);
				String spriteString_ADDITEM = new String(spriteString_ADDITEM_Bytes); // name
				
				byte[] spriteShortName_ADDITEM_Bytes = new byte[message.getInt()];
				message.get(spriteShortName_ADDITEM_Bytes);
				String spriteShortName_ADDITEM = new String(spriteShortName_ADDITEM_Bytes); // name
				
		        Sprite sprite_ADDITEM = SpriteManager.manager.getSprite(spriteString_ADDITEM);
		        ServerSprite serverSprite_ADDITEM = new ServerSprite(spriteString_ADDITEM, sprite_ADDITEM.getHeight(), sprite_ADDITEM.getWidth());
		        ServerEntity serverEntity_ADDITEM;
		        
		        serverEntity_ADDITEM = new ServerEntity(serverSprite_ADDITEM, itemName_ADDITEM, itemRoom, addItemID, spriteShortName_ADDITEM, spriteString_ADDITEM, x_ADDITEM, y_ADDITEM, mx_ADDITEM, my_ADDITEM);
	        	
		        itemStatesLength_ADDITEM = message.getInt();
		        itemStates_ADDITEM = new float[itemStatesLength_ADDITEM];
		        for (int i = 0; i < itemStatesLength_ADDITEM; i ++) {
		        	itemStates_ADDITEM[i] = message.getFloat();
		        }
		        
//		        System.out.println("Server got ="+itemStates_ADDITEM[AttributeType.LIFEREGENERATION.ordinal()]);
				this.getWorldRoom().addItem(addItemID, new ServerItem(addItemID, itemCode_ADDITEM, itemName_ADDITEM, comment, itemRoom,  serverEntity_ADDITEM, count_ADDITEM, capacity_ADDITEM, itemStates_ADDITEM ));
				break; }
			case TAKEITEM: {
				BigInteger takeItemID = BigInteger.valueOf(message.getLong()); // 8 Bytes
				if (this.getWorldRoom().getItemList().get().containsKey(takeItemID)) {
					getSession().send(ServerMessages.sendTakeItem(takeItemID));
					
//					logger.log(Level.INFO, "taking item {0} by player {1}",
//		    	            new Object[] { takeItemID, thisPlayerName});					
				} else {
//					logger.log(Level.INFO, "taking item {0} by player {1} was not successful, because item does noch exist",
//		    	            new Object[] { takeItemID, thisPlayerName});
				}
				break; }
			case TOOKITEM: {
				BigInteger tookItemID = BigInteger.valueOf(message.getLong()); // 8 Bytes
//				logger.log(Level.INFO, "took item {0} by player {1}",
//	    	            new Object[] { tookItemID, thisPlayerName});
				this.getWorldRoom().removeItem(tookItemID);
				break;
			}
			case ADDITEMTOCONTAINER: {
				OBJECTCODE containerType = getObjectCode(message); // 1
				BigInteger addItemID = BigInteger.valueOf(message.getLong()); // 8 Bytes
				ITEMCODE itemCode_ADDITEM = getItemCode(message); // 1 Byte
				int count_ADDITEM = message.getInt(); // 4 Bytes
				int capacity_ADDITEM = message.getInt(); // 4
				float x_ADDITEM = message.getFloat(); // 4
				float y_ADDITEM= message.getFloat(); // 4
				float mx_ADDITEM= message.getFloat(); // 4
				float my_ADDITEM= message.getFloat(); // 4
				int itemStatesLength_ADDITEM; // 4
				float[] itemStates_ADDITEM; // 4*Length
				int containerXPos;
				int containerYPos;
				
				byte[] itemName_ADDITEM_Bytes = new byte[message.getInt()];
				message.get(itemName_ADDITEM_Bytes);
				String itemName_ADDITEM = new String(itemName_ADDITEM_Bytes); // name
				
				byte[] commentBytes = new byte[message.getInt()];
				message.get(commentBytes);
				String comment = new String(commentBytes); // comment
				
				byte[] itemRoomBytes = new byte[message.getInt()];
				message.get(itemRoomBytes);
				String itemRoom = new String(itemRoomBytes); // room

				byte[] spriteString_ADDITEM_Bytes = new byte[message.getInt()];
				message.get(spriteString_ADDITEM_Bytes);
				String spriteString_ADDITEM = new String(spriteString_ADDITEM_Bytes); // name
				
				byte[] spriteShortName_ADDITEM_Bytes = new byte[message.getInt()];
				message.get(spriteShortName_ADDITEM_Bytes);
				String spriteShortName_ADDITEM = new String(spriteShortName_ADDITEM_Bytes); // name
				
		        Sprite sprite_ADDITEM = SpriteManager.manager.getSprite(spriteString_ADDITEM);
		        ServerSprite serverSprite_ADDITEM = new ServerSprite(spriteString_ADDITEM, sprite_ADDITEM.getHeight(), sprite_ADDITEM.getWidth());
		        ServerEntity serverEntity_ADDITEM;
		        
		        serverEntity_ADDITEM = new ServerEntity(serverSprite_ADDITEM, itemName_ADDITEM, itemRoom, addItemID, spriteShortName_ADDITEM, spriteString_ADDITEM, x_ADDITEM, y_ADDITEM, mx_ADDITEM, my_ADDITEM);
//		        System.out.println("x:"+x_ADDITEM+" y:"+y_ADDITEM);
		        
		        itemStatesLength_ADDITEM = message.getInt();
		        itemStates_ADDITEM = new float[itemStatesLength_ADDITEM];
		        for (int i = 0; i < itemStatesLength_ADDITEM; i ++) {
		        	itemStates_ADDITEM[i] = message.getFloat();
		        }
		        
		        containerXPos = message.getInt();
		        containerYPos = message.getInt();
		        
				this.getWorldRoom().addItemToContainer(thisPlayerName, containerType, new ServerItem(addItemID, itemCode_ADDITEM, itemName_ADDITEM, comment, itemRoom, serverEntity_ADDITEM, count_ADDITEM, capacity_ADDITEM, itemStates_ADDITEM ), containerXPos, containerYPos);
//				logger.log(Level.INFO, "added item {0} into the field ({1}|{2} of {3})",
//	    	            new Object[] { addItemID, containerXPos, containerYPos, containerType});
				
				sendPlayersMyUpdatedEquipment(thisPlayerName);
				break;}
			case CLEARCONTAINERPOSITION:
			{	
				OBJECTCODE containerType = getObjectCode(message);
				int containerXPos = message.getInt();
				int containerYPos = message.getInt();
				switch (containerType) {
				case CONTAINER_MAIN:
					BigInteger itemID = this.getWorldRoom().getContainerList().get().get(thisPlayerName).getForUpdate().getContainerArray()[containerXPos][containerYPos];
					this.getWorldRoom().getContainerList().get().get(thisPlayerName).getForUpdate().removeItem(itemID);
					break;
				case CONTAINER_EQUIPMENT_BODY:
					BigInteger itemIDBODY = this.getWorldRoom().getContainerEqBodyList().get().get(thisPlayerName).getForUpdate().getContainerArray()[containerXPos][containerYPos];
					this.getWorldRoom().getContainerEqBodyList().get().get(thisPlayerName).getForUpdate().removeItem(itemIDBODY);
					sendPlayersMyUpdatedEquipment(thisPlayerName);
					break;
				case CONTAINER_EQUIPMENT_WEAPON:
					BigInteger itemIDWEAPON = this.getWorldRoom().getContainerEqWeaponList().get().get(thisPlayerName).getForUpdate().getContainerArray()[containerXPos][containerYPos];
					this.getWorldRoom().getContainerEqWeaponList().get().get(thisPlayerName).getForUpdate().removeItem(itemIDWEAPON);
					sendPlayersMyUpdatedEquipment(thisPlayerName);
					break;
				case CONTAINER_EQUIPMENT_HELMET:
					BigInteger itemIDHELMET = this.getWorldRoom().getContainerEqHelmetList().get().get(thisPlayerName).getForUpdate().getContainerArray()[containerXPos][containerYPos];
					this.getWorldRoom().getContainerEqHelmetList().get().get(thisPlayerName).getForUpdate().removeItem(itemIDHELMET);
					sendPlayersMyUpdatedEquipment(thisPlayerName);
					break;
				case CONTAINER_EQUIPMENT_FEET:
					BigInteger itemIDFEET = this.getWorldRoom().getContainerEqFeetList().get().get(thisPlayerName).getForUpdate().getContainerArray()[containerXPos][containerYPos];
					this.getWorldRoom().getContainerEqFeetList().get().get(thisPlayerName).getForUpdate().removeItem(itemIDFEET);
					sendPlayersMyUpdatedEquipment(thisPlayerName);
					break;
				case CONTAINER_EQUIPMENT_SHIELD:
					BigInteger itemIDSHIELD = this.getWorldRoom().getContainerEqShieldList().get().get(thisPlayerName).getForUpdate().getContainerArray()[containerXPos][containerYPos];
					this.getWorldRoom().getContainerEqShieldList().get().get(thisPlayerName).getForUpdate().removeItem(itemIDSHIELD);
					sendPlayersMyUpdatedEquipment(thisPlayerName);
					break;
				case CONTAINER_EQUIPMENT_AMULET:
					BigInteger itemIDAmulet = this.getWorldRoom().getContainerEqAmuletList().get().get(thisPlayerName).getForUpdate().getContainerArray()[containerXPos][containerYPos];
					this.getWorldRoom().getContainerEqAmuletList().get().get(thisPlayerName).getForUpdate().removeItem(itemIDAmulet);
					sendPlayersMyUpdatedEquipment(thisPlayerName);
					break;
				case CONTAINER_EQUIPMENT_GLOVE:
					BigInteger itemIDGlove = this.getWorldRoom().getContainerEqGloveList().get().get(thisPlayerName).getForUpdate().getContainerArray()[containerXPos][containerYPos];
					this.getWorldRoom().getContainerEqGloveList().get().get(thisPlayerName).getForUpdate().removeItem(itemIDGlove);
					sendPlayersMyUpdatedEquipment(thisPlayerName);
					break;
				case CONTAINER_EQUIPMENT_SHIRT:
					BigInteger itemIDShirt = this.getWorldRoom().getContainerEqShirtList().get().get(thisPlayerName).getForUpdate().getContainerArray()[containerXPos][containerYPos];
					this.getWorldRoom().getContainerEqShirtList().get().get(thisPlayerName).getForUpdate().removeItem(itemIDShirt);
					sendPlayersMyUpdatedEquipment(thisPlayerName);
					break;
				case CONTAINER_EQUIPMENT_TROUSERS:
					BigInteger itemIDTrousers = this.getWorldRoom().getContainerEqTrousersList().get().get(thisPlayerName).getForUpdate().getContainerArray()[containerXPos][containerYPos];
					this.getWorldRoom().getContainerEqTrousersList().get().get(thisPlayerName).getForUpdate().removeItem(itemIDTrousers);
					sendPlayersMyUpdatedEquipment(thisPlayerName);
					break;
				case CONTAINER_USE:
					BigInteger itemIDUse = this.getWorldRoom().getContainerUseList().get().get(thisPlayerName).getForUpdate().getContainerArray()[containerXPos][containerYPos];
					this.getWorldRoom().getContainerUseList().get().get(thisPlayerName).getForUpdate().removeItem(itemIDUse);
					break;
				default:;
				}
				
//				logger.log(Level.INFO, "deleted item {0} from the field ({1}|{2})",
//	    	            new Object[] { itemID, containerXPos, containerYPos});
				break;
			}
			case GET_NEXT_ITEM: {
				BigInteger playerID = BigInteger.valueOf(message.getLong());
				OBJECTCODE containerType = getObjectCode(message);
				int fieldX = message.getInt();
				int fieldY = message.getInt();
				int tempFieldX=0;
				int tempFieldY=0;
				int nextFieldX=0;
				int nextFieldY=0;
				
				ServerContainer playerContainer = null;
				switch (containerType) {
				case CONTAINER_MAIN:
					playerContainer = this.getWorldRoom().getContainerList().get().get(thisPlayerName).get();
					break;
				case CONTAINER_EQUIPMENT_BODY:
					playerContainer = this.getWorldRoom().getContainerEqBodyList().get().get(thisPlayerName).get();
					break;
				case CONTAINER_EQUIPMENT_WEAPON:
					playerContainer = this.getWorldRoom().getContainerEqWeaponList().get().get(thisPlayerName).get();
					break;
				case CONTAINER_EQUIPMENT_HELMET:
					playerContainer = this.getWorldRoom().getContainerEqHelmetList().get().get(thisPlayerName).get();
					break;
				case CONTAINER_EQUIPMENT_FEET:
					playerContainer = this.getWorldRoom().getContainerEqFeetList().get().get(thisPlayerName).get();
					break;
				case CONTAINER_EQUIPMENT_SHIELD:
					playerContainer = this.getWorldRoom().getContainerEqShieldList().get().get(thisPlayerName).get();
					break;
				case CONTAINER_EQUIPMENT_AMULET:
					playerContainer = this.getWorldRoom().getContainerEqAmuletList().get().get(thisPlayerName).get();
					break;
				case CONTAINER_EQUIPMENT_GLOVE:
					playerContainer = this.getWorldRoom().getContainerEqGloveList().get().get(thisPlayerName).get();
					break;
				case CONTAINER_EQUIPMENT_SHIRT:
					playerContainer = this.getWorldRoom().getContainerEqShirtList().get().get(thisPlayerName).get();
					break;
				case CONTAINER_EQUIPMENT_TROUSERS:
					playerContainer = this.getWorldRoom().getContainerEqTrousersList().get().get(thisPlayerName).get();
					break;
				case CONTAINER_USE:
					playerContainer = this.getWorldRoom().getContainerUseList().get().get(thisPlayerName).get();
//					playerContainer = this.getWorldRoom().getContainerList().get().get(thisPlayerName).get();
					break;
				default:
//					playerContainer = this.getWorldRoom().getContainerList().get().get(thisPlayerName).get();
					
				}
//				ServerContainer playerContainer = this.getWorldRoom().getContainerList().get().get(thisPlayerName).get();
		        boolean foundItem = false;
		        ManagedReference<ServerItem> itemRef = null;
		        if (fieldX < playerContainer.getContainerWidth()-1) {
		        	tempFieldX = fieldX +1;
		        	tempFieldY = fieldY;
		        } else {
		        	if (fieldY < playerContainer.getContainerHeight()-1) {
		        		tempFieldY = fieldY +1;
		        		tempFieldX = 0;
		        	} else {
		        		// do nothing, because its the last element of the container
		        		tempFieldX = fieldX+1;
		        		tempFieldY = fieldY+1;
		        	}
		        }
		        for(int i = tempFieldY; i <playerContainer.getContainerHeight(); i++) {
		        	for (int j = tempFieldX; j < playerContainer.getContainerWidth(); j++) {
		        		if (playerContainer.getItemList().get(playerContainer.getContainerArray()[i][j]) != null) {
		        			foundItem = true;
		        			itemRef = playerContainer.getItemList().get(playerContainer.getContainerArray()[i][j]);
		        			nextFieldX = j;
		        			nextFieldY = i;
		        			break;
		        		}
		        	}
		        	if (foundItem) break;
		        }
		        if (foundItem && itemRef != null) {
		        	// send first found item
//		        	System.out.println("send next item: x="+nextFieldX+" y="+nextFieldY);
		        	Item item = ItemFactoryCollection.getItem(itemRef.get().getItemCode(), itemRef.get().getId(), itemRef.get().getName(), itemRef.get().getComment(), itemRef.get().getRoom(), itemRef.get().getCount(), itemRef.get().getX(), itemRef.get().getY(), itemRef.get().getCreationTime(), itemRef.get().getStates());
					getSession().send(ServerMessages.sendItemField(playerID, containerType, item, nextFieldX, nextFieldY));
		        } else {
		        	// nothing should be send to player
		        }    
				break;
			}
			case ADDAREAINFLUENCE: {
				Priority priority = getPriority(message); // 1 Byte

				BigInteger id = BigInteger.valueOf(message.getLong()); // 8 Bytes

		        long timeBegin = message.getLong();
		        long timeEnd = message.getLong();
		        byte[] groupNameBytes = new byte[message.getInt()];
				message.get(groupNameBytes);
				String groupName = new String(groupNameBytes); // name
				byte[] roomBytes = new byte[message.getInt()];
				message.get(roomBytes);
				String room = new String(roomBytes); // name
				
				byte[] creatorBytes = new byte[message.getInt()];
				message.get(creatorBytes);
				String creator = new String(creatorBytes); // name
				
				boolean exclusive;
				if (message.getInt() == 1) exclusive = true; else exclusive = false;
				
				float x = message.getFloat(); // 4
				float y= message.getFloat(); // 4
				float width = message.getFloat(); // 4
				float height= message.getFloat(); // 4
				float mx= message.getFloat(); // 4
				float my= message.getFloat(); // 4
				int attributeLength = message.getInt(); // 4
				float[] attributes = new float[attributeLength]; // 4*Length
		        for (int i = 0; i < attributeLength; i ++) {
		        	attributes[i] = message.getFloat();
		        }
		        
		        WorldObject worldObject = new WorldObject(x, y, width, height, mx, my);
		        ServerAreaInfluence serverAreaInfluence = new ServerAreaInfluence(id, timeBegin, timeEnd, groupName, room,  exclusive, worldObject, attributes, priority, creator);
		        
//		        System.out.println("Server got areaInfluence with room="+room);//+attributes[AttributeType.LIFE.ordinal()]+" width="+worldObject.getWidth());
				this.getWorldRoom().addAreaInfluence(id, serverAreaInfluence);
				break; }
			case ADDGROWTHSPOT: {
				Priority priority = getPriority(message); // 1 Byte

				BigInteger id = BigInteger.valueOf(message.getLong()); // 8 Bytes

		        long timeBegin = message.getLong();
		        long timeEnd = message.getLong();
		        byte[] groupNameBytes = new byte[message.getInt()];
				message.get(groupNameBytes);
				String groupName = new String(groupNameBytes); // name
				byte[] roomBytes = new byte[message.getInt()];
				message.get(roomBytes);
				String room = new String(roomBytes); // name
				
				byte[] creatorBytes = new byte[message.getInt()];
				message.get(creatorBytes);
				String creator = new String(creatorBytes); // name
				
				boolean exclusive;
				if (message.getInt() == 1) exclusive = true; else exclusive = false;
				
				float x = message.getFloat(); // 4
				float y= message.getFloat(); // 4
				float width = message.getFloat(); // 4
				float height= message.getFloat(); // 4
				float mx= message.getFloat(); // 4
				float my= message.getFloat(); // 4
				int attributeLength = message.getInt(); // 4
				float[] attributes = new float[attributeLength]; // 4*Length
		        for (int i = 0; i < attributeLength; i ++) {
		        	attributes[i] = message.getFloat();
		        }
		        
		        WorldObject worldObject = new WorldObject(x, y, width, height, mx, my);
		        ServerAreaInfluence serverAreaInfluence = new ServerAreaInfluence(id, timeBegin, timeEnd, groupName, room,  exclusive, worldObject, attributes, priority, creator);
		        
//		        System.out.println("Server got areaInfluence with room="+room);//+attributes[AttributeType.LIFE.ordinal()]+" width="+worldObject.getWidth());
				this.getWorldRoom().addAreaInfluence(id, serverAreaInfluence);
				break; }
			case DELETE_MAPOBJECT:
			{
				int localX = message.getInt();
				int localY = message.getInt();
				int mapX = message.getInt();
				int mapY = message.getInt();
				BigInteger gmID = BigInteger.valueOf(message.getLong());
				byte[] tileRoomName_Bytes = new byte[message.getInt()];
				message.get(tileRoomName_Bytes);
				String tileRoomName = new String(tileRoomName_Bytes); // name
				byte[] gmName_Bytes = new byte[message.getInt()];
				message.get(gmName_Bytes);
				String gmName = new String(gmName_Bytes); // name
				byte[] roomName_Bytes = new byte[message.getInt()];
				message.get(roomName_Bytes);
				String roomName = new String(roomName_Bytes); // name
				byte[] objectMapNameName_Bytes = new byte[message.getInt()];
				message.get(objectMapNameName_Bytes);
				String objectMapName = new String(objectMapNameName_Bytes); // name
				byte[] objectOverlayMapNameName_Bytes = new byte[message.getInt()];
				message.get(objectOverlayMapNameName_Bytes);
				String objectOverlayMapName = new String(objectOverlayMapNameName_Bytes); // name
				
				
				int valueLength = message.getInt();
				int[] values = new int[valueLength];
				for (int i = 0; i< valueLength; i++) {
					values[i] = message.getInt();
				}
					
				InteractionTile iTile = new InteractionTile(new WorldLatticePosition(new Point(mapX, mapY), localX, localY));
				iTile.setValues(values);
				iTile.getPosition().setRoom(roomName);
				this.getWorldRoom().deleteMapObject(gmID, iTile, objectMapName, objectOverlayMapName);
		    	
				break;
			}
			case SENDMISSINGPLAYERAGENTS: {
//				buffer.putLong(playerID.longValue());
				BigInteger requestedPlayerID = BigInteger.valueOf(message.getLong());
//		        buffer.putInt(sourcePlayerName.length()); // 4
//		    	buffer.put(sourcePlayerName.getBytes()); // playerName.length()
				byte[] requestedPlayerNameBytes = new byte[message.getInt()];
				message.get(requestedPlayerNameBytes);
				String requestedPlayerName = new String(requestedPlayerNameBytes); // playerName
//		    	buffer.putInt(sendUnknownPlayerAgentIDs.size()); // 4
				int unknownPlayerAgentsLength = message.getInt();
				ArrayList<BigInteger> unknownPlayerAgents = new ArrayList<BigInteger>();
		        /** get all playerAgents of the specific class */
		        for (int i = 0; i < unknownPlayerAgentsLength; i++) {
//		    		buffer.putLong(id.longValue()); // 8
		        	unknownPlayerAgents.add(BigInteger.valueOf(message.getLong()));
		    	}
		        /** send askFor from playerA to playerB*/
		        this.getWorldRoom().askForUnknownPlayerAgentsPlayerAToPlayerB(this.getId(), requestedPlayerID, unknownPlayerAgents);
				
				break;
			}
			case SENDCOMPLETEPLAYERAGENTS: {
				BigInteger toPlayerID = BigInteger.valueOf(message.getLong());
	    		byte[] nameBytes = new byte[message.getInt()];
				message.get(nameBytes);
				String namePlayer = new String(nameBytes); // name
				
//				if (namePlayer.equals(GameWindow.gw.getPlayerName())) {
//					//STOP - its my own playerAgentList
//				} else {
				
		    		int playerAgentsLength = message.getInt(); // 4;
		    		ArrayList<PlayerEntity> playerAgentsList = new ArrayList<PlayerEntity>();
		    		
		    		/** here come all the agent properties */
		    		PlayerEntity e;
		    		byte[] agentNameBytes;
		    		String agentName;
		    		BigInteger id;
		    		byte[] tileSetNameBytes;
		    		String tileSetName;
		    		float x, y, width, height, mx, my;
		    		int attributeLength;
		    		float[] attributes = new float[AttributeType.values().length];
		    		EntityStates orientation;
	            	EntityStates singleState;
	            	EntityStates continuousState;
	            	byte[] roomBytes;
					String room;
		    		
		    		/** get every playerAgent */
		    		for (int i = 0; i < playerAgentsLength; i++) {
		//        		buffer.putLong(id.longValue()); // 8
		        		id = BigInteger.valueOf(message.getLong());
		//        		buffer.putInt(name.length()); // 4
		//            	buffer.put(name.getBytes()); // name.length
		            	agentNameBytes = new byte[message.getInt()];
		            	message.get(agentNameBytes);
		    			agentName = new String(agentNameBytes); // agentName
		    			roomBytes = new byte[message.getInt()];
						message.get(roomBytes);
						room = new String(roomBytes); // name
		//            	buffer.putInt(e.getTileSet().getName().length()); // 4
		//            	buffer.put(e.getTileSet().getName().getBytes()); // name.length
		            	tileSetNameBytes = new byte[message.getInt()];
		            	message.get(tileSetNameBytes);
		    			tileSetName = new String(tileSetNameBytes); // tileSetName
		    			orientation = getEntityStates(message);
		    			singleState = getEntityStates(message);
		    			continuousState = getEntityStates(message);
		//            	buffer.putFloat(e.getX()); // 4
		            	x = message.getFloat();
		//            	buffer.putFloat(e.getY()); // 4
		            	y = message.getFloat();
		//            	buffer.putFloat(e.getWidth()); // 4
		            	width = message.getFloat();
		//            	buffer.putFloat(e.getHeight()); // 4
		            	height = message.getFloat();
		            	//buffer.putFloat(e.getMX()); // 4
		            	mx = message.getFloat();
		//            	buffer.putFloat(e.getMY()); // 4
		            	my = message.getFloat();
		//            	buffer.putInt(e.getAttributes().length);
		            	attributeLength = message.getInt();
		            	if (attributeLength != attributes.length) {
		            		//ERROR!!!
		            		System.out.println("Wrong length of attributes!!!");
		            		break;
		            	} else {
		            		for ( int j = 0; j < attributeLength; j++) {
		//                		buffer.putFloat(e.getAttributes()[j]);
		                		attributes[j] = message.getFloat();
		                	}
		            	}
		            	e = new PlayerEntity(TileSetManager.manager.getTileSet(tileSetName), agentName, room, id, x, y, GameStates.animationDelay);
		            	e.setWidth(width);
		            	e.setHeight(height);
		            	e.setMX(mx);
		            	e.setMY(my);
		            	e.setAttributes(attributes);
		            	e.setOrientation(orientation);
		            	e.setSingleState(singleState);
		            	e.setContinuousState(continuousState);
		            	playerAgentsList.add(e);
		    		}
		    		
		    		/** worldRoom send the message to the correct session */
			        this.getWorldRoom().sendUnknownPlayerAgents(toPlayerID, namePlayer, playerAgentsList);
					
					
//		    		/** add all playerAgents to the new agentList/ idList */
//		    		HashMap<BigInteger, PlayerEntity> agentList = new HashMap<BigInteger, PlayerEntity>();
//		    		ArrayList<BigInteger> idList = new ArrayList<BigInteger>();
//		    		for (int i = 0; i < playerAgentsList.size(); i++) {
//		    			BigInteger agentID = playerAgentsList.get(i).getId();
//		    			agentList.put(agentID, playerAgentsList.get(i));
//		    			idList.add(agentID);
//		    		}
//		    		
//		    		/** configure the new/ updated playerAgentManager */
//		    		PlayerAgentManager playerAgentManager = new PlayerAgentManager(namePlayer);
//		    		playerAgentManager.setAgentList(agentList);
//		    		playerAgentManager.setIdList(idList);
//		    		/** and add it to the PlayerAgentManagerManager */
//		    		if (PlayerAgentManagerManager.contains(namePlayer)) {
//		    			PlayerAgentManagerManager.overwrite(namePlayer, playerAgentManager);
//		    		} else {
//		    			PlayerAgentManagerManager.add(playerAgentManager);
//		    		}
//		    		GameWindow.gw.gameInfoConsole.appendLine("Player "+namePlayer+" send "+ playerAgentsLength+" agents.");
//				}
	    		break;
	    	}
			case EDIT_PLAYER_ROOM: {
				BigInteger playerID = BigInteger.valueOf(message.getLong());
				byte[] playerNameByte = new byte[message.getInt()];
            	message.get(playerNameByte);
				String playerName = new String(playerNameByte); 
				byte[] roomNameByte = new byte[message.getInt()];
            	message.get(roomNameByte);
				String room = new String(roomNameByte); 
				this.getWorldRoom().editPlayerRoom(playerID, playerName, room);
				break;
			}
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

	
	public void sendAvailableTextures() {
		ArrayList<String> externalTextures;
		externalTextures = ServerTextureManager.manager.listExternalImages(GameStates.externalImagesPath);
		getSession().send(ServerMessages.sendAvailableTextures(externalTextures));	
		this.setReadyForNextMessage(true);
	}

	public void initSpaces() {
		// TODO Auto-generated method stub
    	int countSpaces = currentRoomRef.get().getCountSpaces();
		Space[] spaceArray;
		int end = 0;
		for (int begin = 0; end<countSpaces; begin+=200) {
			end = begin+200;
			if (end> countSpaces) end = countSpaces;
			spaceArray = getWorldRoom().getSpaces(this, begin, end);
//								logger.log(Level.INFO, "got entityArray: "+ entityArray[0].getName());

			getSession().send(ServerMessages.sendSpaces(spaceArray));
//								logger.log(Level.INFO, "packet send");
		
		}
	}
	
	public void initPlayers() {
    	int countPlayers = currentRoomRef.get().getCountPlayers();
		PlayerEntity[] playerArray;
		int end = 0;
		for (int begin = 0; end<countPlayers; begin+=200) {
			end = begin+200;
			if (end> countPlayers) end = countPlayers;
			playerArray = getWorldRoom().getPlayers(this, begin, end);
//								logger.log(Level.INFO, "got entityArray: "+ entityArray[0].getName());

			getSession().send(ServerMessages.sendPlayers(playerArray));
//								logger.log(Level.INFO, "packet send");
		
		}
		
		
	}
	
	/** initialize the player himself */
	public void initMe() {
		String thisPlayerName = this.getName().substring(this.getName().indexOf(".")+1, this.getName().length());
		ServerPlayer s_player = getWorldRoom().getServerPlayer(thisPlayerName);

		BigInteger playerId = s_player.getId();
		String tileName = s_player.getTileSetName();
		String tilePathName = s_player.getTileSetPathName();
		String groupName = s_player.getGroupName();
		String room = s_player.getRoom();
		long firstServerLogin = s_player.getFirstServerLogin();
		int experience = s_player.getExperience();
		String country = s_player.getCountry();
		
		float x = s_player.getX();
		float y = s_player.getY();
		float mx = s_player.getMX();
		float my = s_player.getMY();
		
		ServerContainer serverInventory = this.getWorldRoom().getContainerList().get().get(thisPlayerName).get();
		ServerContainer serverInventoryUse = this.getWorldRoom().getContainerUseList().get().get(thisPlayerName).get();
		ServerContainer serverInventoryEqBody = this.getWorldRoom().getContainerEqBodyList().get().get(thisPlayerName).get();
		ServerContainer serverInventoryEqWeapon = this.getWorldRoom().getContainerEqWeaponList().get().get(thisPlayerName).get();
		ServerContainer serverInventoryEqHelmet = this.getWorldRoom().getContainerEqHelmetList().get().get(thisPlayerName).get();
		ServerContainer serverInventoryEqFeet = this.getWorldRoom().getContainerEqFeetList().get().get(thisPlayerName).get();
		ServerContainer serverInventoryEqShield = this.getWorldRoom().getContainerEqShieldList().get().get(thisPlayerName).get();
		ServerContainer serverInventoryEqAmulet = this.getWorldRoom().getContainerEqAmuletList().get().get(thisPlayerName).get();
		ServerContainer serverInventoryEqGlove = this.getWorldRoom().getContainerEqGloveList().get().get(thisPlayerName).get();
		ServerContainer serverInventoryEqShirt = this.getWorldRoom().getContainerEqShirtList().get().get(thisPlayerName).get();
		ServerContainer serverInventoryEqTrousers = this.getWorldRoom().getContainerEqTrousersList().get().get(thisPlayerName).get();
		//s_player.getInventory().get();
//		Container inventory = new Container(serverInventory.getContainerWidth(), serverInventory.getContainerHeight(), serverInventory.getContainerType());
//		inventory.setContainerArray(serverInventory.getContainerArray());
//		
//		HIER MUSS NOCH DIE itemList gezogen werden!!! (for-loop)
//		inventory.setItemList(serverInventory.getItemList());
//		Item item;
//		for (ManagedReference<ServerItem> itemRef : serverInventory.getItemList().values()) {
//			item = Item.getItem(itemRef.get().getItemCode(), itemRef.get().getId(), itemRef.get().getName(), itemRef.get().getCount(), itemRef.get().getCapacity(), itemRef.get().getX(), itemRef.get().getY(), itemRef.get().getCreationTime(), itemRef.get().getStates());
//			inventory.getItemList().put(item.getId(), item);
//		}
		
		BigInteger maxItemID = this.getWorldRoom().getMaxItemIndex();
		BigInteger maxAreaInfluenceID = this.getWorldRoom().getMaxAreaInfluenceIndex();
		BigInteger maxGrowthSpotID = this.getWorldRoom().getMaxGrowthSpotIndex();
		long sendWorldTime = this.getWorldRoom().getWorldTime();
        getSession().send(ServerMessages.sendMe(playerId, tileName, tilePathName, groupName, room , firstServerLogin, experience, country, x,y,mx,my, maxItemID, maxAreaInfluenceID, maxGrowthSpotID, sendWorldTime));
//      getSession().send(ServerMessages.sendContainer(playerId, inventory));
        boolean foundItem;
        int fieldX;
        int fieldY;
        ManagedReference<ServerItem> itemRef;
        
        
        foundItem = false;
        fieldX = 0;
        fieldY = 0;
        itemRef = null;
        for(int i = 0; i <serverInventory.getContainerHeight(); i++) {
        	for (int j = 0; j < serverInventory.getContainerWidth(); j++) {
        		if (serverInventory.getItemList().get(serverInventory.getContainerArray()[i][j]) != null) {
        			foundItem = true;
        			itemRef = serverInventory.getItemList().get(serverInventory.getContainerArray()[i][j]);
        			fieldX = j;
        			fieldY = i;
        			break;
        		}
        	}
        	if (foundItem) break;
        }
        if (foundItem && itemRef != null) {
        	// send first found item
        	Item item = ItemFactoryCollection.getItem(itemRef.get().getItemCode(), itemRef.get().getId(), itemRef.get().getName(), itemRef.get().getComment(), itemRef.get().getRoom(), itemRef.get().getCount(), itemRef.get().getX(), itemRef.get().getY(), itemRef.get().getCreationTime(), itemRef.get().getStates());
			getSession().send(ServerMessages.sendItemField(playerId, OBJECTCODE.CONTAINER_MAIN, item, fieldX, fieldY));
        } else {
        	// nothing should be send to player
        }    
        
        /** NEXT CONTAINER */
        foundItem = false;
        fieldX = 0;
        fieldY = 0;
        itemRef = null;
        for(int i = 0; i <serverInventoryUse.getContainerHeight(); i++) {
        	for (int j = 0; j < serverInventoryUse.getContainerWidth(); j++) {
        		if (serverInventoryUse.getItemList().get(serverInventoryUse.getContainerArray()[i][j]) != null) {
        			foundItem = true;
        			itemRef = serverInventoryUse.getItemList().get(serverInventoryUse.getContainerArray()[i][j]);
        			fieldX = j;
        			fieldY = i;
        			break;
        		}
        	}
        	if (foundItem) break;
        }
        if (foundItem && itemRef != null) {
        	Item item = ItemFactoryCollection.getItem(itemRef.get().getItemCode(), itemRef.get().getId(), itemRef.get().getName(), itemRef.get().getComment(),itemRef.get().getRoom(),  itemRef.get().getCount(),  itemRef.get().getX(), itemRef.get().getY(), itemRef.get().getCreationTime(), itemRef.get().getStates());
			getSession().send(ServerMessages.sendItemField(playerId, OBJECTCODE.CONTAINER_USE, item, fieldX, fieldY));
        } else {
        	// nothing should be send to player
        }  
        /** end container */
        
        
        /** NEXT CONTAINER */
        foundItem = false;
        fieldX = 0;
        fieldY = 0;
        itemRef = null;
        for(int i = 0; i <serverInventoryEqBody.getContainerHeight(); i++) {
        	for (int j = 0; j < serverInventoryEqBody.getContainerWidth(); j++) {
        		if (serverInventoryEqBody.getItemList().get(serverInventoryEqBody.getContainerArray()[i][j]) != null) {
        			foundItem = true;
        			itemRef = serverInventoryEqBody.getItemList().get(serverInventoryEqBody.getContainerArray()[i][j]);
        			fieldX = j;
        			fieldY = i;
        			break;
        		}
        	}
        	if (foundItem) break;
        }
        if (foundItem && itemRef != null) {
        	Item item = ItemFactoryCollection.getItem(itemRef.get().getItemCode(), itemRef.get().getId(), itemRef.get().getName(),itemRef.get().getComment(), itemRef.get().getRoom(), itemRef.get().getCount(),  itemRef.get().getX(), itemRef.get().getY(), itemRef.get().getCreationTime(), itemRef.get().getStates());
			getSession().send(ServerMessages.sendItemField(playerId, OBJECTCODE.CONTAINER_EQUIPMENT_BODY, item, fieldX, fieldY));
        } else {
        	// nothing should be send to player
        }  
        
        /** NEXT CONTAINER */
        foundItem = false;
        fieldX = 0;
        fieldY = 0;
        itemRef = null;
        for(int i = 0; i <serverInventoryEqWeapon.getContainerHeight(); i++) {
        	for (int j = 0; j < serverInventoryEqWeapon.getContainerWidth(); j++) {
        		if (serverInventoryEqWeapon.getItemList().get(serverInventoryEqWeapon.getContainerArray()[i][j]) != null) {
        			foundItem = true;
        			itemRef = serverInventoryEqWeapon.getItemList().get(serverInventoryEqWeapon.getContainerArray()[i][j]);
        			fieldX = j;
        			fieldY = i;
        			break;
        		}
        	}
        	if (foundItem) break;
        }
        if (foundItem && itemRef != null) {
        	Item item = ItemFactoryCollection.getItem(itemRef.get().getItemCode(), itemRef.get().getId(), itemRef.get().getName(), itemRef.get().getComment(),itemRef.get().getRoom(), itemRef.get().getCount(), itemRef.get().getX(), itemRef.get().getY(), itemRef.get().getCreationTime(), itemRef.get().getStates());
			getSession().send(ServerMessages.sendItemField(playerId, OBJECTCODE.CONTAINER_EQUIPMENT_WEAPON, item, fieldX, fieldY));
        } else {
        	// nothing should be send to player
        }  
        
        /** NEXT CONTAINER */
        foundItem = false;
        fieldX = 0;
        fieldY = 0;
        itemRef = null;
        for(int i = 0; i <serverInventoryEqHelmet.getContainerHeight(); i++) {
        	for (int j = 0; j < serverInventoryEqHelmet.getContainerWidth(); j++) {
        		if (serverInventoryEqHelmet.getItemList().get(serverInventoryEqHelmet.getContainerArray()[i][j]) != null) {
        			foundItem = true;
        			itemRef = serverInventoryEqHelmet.getItemList().get(serverInventoryEqHelmet.getContainerArray()[i][j]);
        			fieldX = j;
        			fieldY = i;
        			break;
        		}
        	}
        	if (foundItem) break;
        }
        if (foundItem && itemRef != null) {
        	Item item = ItemFactoryCollection.getItem(itemRef.get().getItemCode(), itemRef.get().getId(), itemRef.get().getName(), itemRef.get().getComment(),itemRef.get().getRoom(), itemRef.get().getCount(),  itemRef.get().getX(), itemRef.get().getY(), itemRef.get().getCreationTime(), itemRef.get().getStates());
			getSession().send(ServerMessages.sendItemField(playerId, OBJECTCODE.CONTAINER_EQUIPMENT_HELMET, item, fieldX, fieldY));
        } else {
        	// nothing should be send to player
        }  
        
        /** NEXT CONTAINER */
        foundItem = false;
        fieldX = 0;
        fieldY = 0;
        itemRef = null;
        for(int i = 0; i <serverInventoryEqFeet.getContainerHeight(); i++) {
        	for (int j = 0; j < serverInventoryEqFeet.getContainerWidth(); j++) {
        		if (serverInventoryEqFeet.getItemList().get(serverInventoryEqFeet.getContainerArray()[i][j]) != null) {
        			foundItem = true;
        			itemRef = serverInventoryEqFeet.getItemList().get(serverInventoryEqFeet.getContainerArray()[i][j]);
        			fieldX = j;
        			fieldY = i;
        			break;
        		}
        	}
        	if (foundItem) break;
        }
        if (foundItem && itemRef != null) {
        	Item item = ItemFactoryCollection.getItem(itemRef.get().getItemCode(), itemRef.get().getId(), itemRef.get().getName(),itemRef.get().getComment(),itemRef.get().getRoom(),  itemRef.get().getCount(),  itemRef.get().getX(), itemRef.get().getY(), itemRef.get().getCreationTime(), itemRef.get().getStates());
			getSession().send(ServerMessages.sendItemField(playerId, OBJECTCODE.CONTAINER_EQUIPMENT_FEET, item, fieldX, fieldY));
        } else {
        	// nothing should be send to player
        }  
        
        /** SHIELD CONTAINER */
        foundItem = false;
        fieldX = 0;
        fieldY = 0;
        itemRef = null;
        for(int i = 0; i <serverInventoryEqShield.getContainerHeight(); i++) {
        	for (int j = 0; j < serverInventoryEqShield.getContainerWidth(); j++) {
        		if (serverInventoryEqShield.getItemList().get(serverInventoryEqShield.getContainerArray()[i][j]) != null) {
        			foundItem = true;
        			itemRef = serverInventoryEqShield.getItemList().get(serverInventoryEqShield.getContainerArray()[i][j]);
        			fieldX = j;
        			fieldY = i;
        			break;
        		}
        	}
        	if (foundItem) break;
        }
        if (foundItem && itemRef != null) {
        	Item item = ItemFactoryCollection.getItem(itemRef.get().getItemCode(), itemRef.get().getId(), itemRef.get().getName(),itemRef.get().getComment(), itemRef.get().getRoom(), itemRef.get().getCount(), itemRef.get().getX(), itemRef.get().getY(), itemRef.get().getCreationTime(), itemRef.get().getStates());
			getSession().send(ServerMessages.sendItemField(playerId, OBJECTCODE.CONTAINER_EQUIPMENT_SHIELD, item, fieldX, fieldY));
        } else {
        	// nothing should be send to player
        }  
        
        /** Amulet CONTAINER */
        foundItem = false;
        fieldX = 0;
        fieldY = 0;
        itemRef = null;
        for(int i = 0; i <serverInventoryEqAmulet.getContainerHeight(); i++) {
        	for (int j = 0; j < serverInventoryEqAmulet.getContainerWidth(); j++) {
        		if (serverInventoryEqAmulet.getItemList().get(serverInventoryEqAmulet.getContainerArray()[i][j]) != null) {
        			foundItem = true;
        			itemRef = serverInventoryEqAmulet.getItemList().get(serverInventoryEqAmulet.getContainerArray()[i][j]);
        			fieldX = j;
        			fieldY = i;
        			break;
        		}
        	}
        	if (foundItem) break;
        }
        if (foundItem && itemRef != null) {
        	Item item = ItemFactoryCollection.getItem(itemRef.get().getItemCode(), itemRef.get().getId(), itemRef.get().getName(),itemRef.get().getComment(),itemRef.get().getRoom(),  itemRef.get().getCount(),  itemRef.get().getX(), itemRef.get().getY(), itemRef.get().getCreationTime(), itemRef.get().getStates());
			getSession().send(ServerMessages.sendItemField(playerId, OBJECTCODE.CONTAINER_EQUIPMENT_AMULET, item, fieldX, fieldY));
        } else {
        	// nothing should be send to player
        }  
        
        /** Glove CONTAINER */
        foundItem = false;
        fieldX = 0;
        fieldY = 0;
        itemRef = null;
        for(int i = 0; i <serverInventoryEqGlove.getContainerHeight(); i++) {
        	for (int j = 0; j < serverInventoryEqGlove.getContainerWidth(); j++) {
        		if (serverInventoryEqGlove.getItemList().get(serverInventoryEqGlove.getContainerArray()[i][j]) != null) {
        			foundItem = true;
        			itemRef = serverInventoryEqGlove.getItemList().get(serverInventoryEqGlove.getContainerArray()[i][j]);
        			fieldX = j;
        			fieldY = i;
        			break;
        		}
        	}
        	if (foundItem) break;
        }
        if (foundItem && itemRef != null) {
        	Item item = ItemFactoryCollection.getItem(itemRef.get().getItemCode(), itemRef.get().getId(), itemRef.get().getName(), itemRef.get().getComment(),itemRef.get().getRoom(), itemRef.get().getCount(), itemRef.get().getX(), itemRef.get().getY(), itemRef.get().getCreationTime(), itemRef.get().getStates());
			getSession().send(ServerMessages.sendItemField(playerId, OBJECTCODE.CONTAINER_EQUIPMENT_GLOVE, item, fieldX, fieldY));
        } else {
        	// nothing should be send to player
        }  
        
        /** Shirt CONTAINER */
        foundItem = false;
        fieldX = 0;
        fieldY = 0;
        itemRef = null;
        for(int i = 0; i <serverInventoryEqShirt.getContainerHeight(); i++) {
        	for (int j = 0; j < serverInventoryEqShirt.getContainerWidth(); j++) {
        		if (serverInventoryEqShirt.getItemList().get(serverInventoryEqShirt.getContainerArray()[i][j]) != null) {
        			foundItem = true;
        			itemRef = serverInventoryEqShirt.getItemList().get(serverInventoryEqShirt.getContainerArray()[i][j]);
        			fieldX = j;
        			fieldY = i;
        			break;
        		}
        	}
        	if (foundItem) break;
        }
        if (foundItem && itemRef != null) {
        	Item item = ItemFactoryCollection.getItem(itemRef.get().getItemCode(), itemRef.get().getId(), itemRef.get().getName(), itemRef.get().getComment(),itemRef.get().getRoom(), itemRef.get().getCount(),  itemRef.get().getX(), itemRef.get().getY(), itemRef.get().getCreationTime(), itemRef.get().getStates());
			getSession().send(ServerMessages.sendItemField(playerId, OBJECTCODE.CONTAINER_EQUIPMENT_SHIRT, item, fieldX, fieldY));
        } else {
        	// nothing should be send to player
        }  
        
        /** Trousers CONTAINER */
        foundItem = false;
        fieldX = 0;
        fieldY = 0;
        itemRef = null;
        for(int i = 0; i <serverInventoryEqTrousers.getContainerHeight(); i++) {
        	for (int j = 0; j < serverInventoryEqTrousers.getContainerWidth(); j++) {
        		if (serverInventoryEqTrousers.getItemList().get(serverInventoryEqTrousers.getContainerArray()[i][j]) != null) {
        			foundItem = true;
        			itemRef = serverInventoryEqTrousers.getItemList().get(serverInventoryEqTrousers.getContainerArray()[i][j]);
        			fieldX = j;
        			fieldY = i;
        			break;
        		}
        	}
        	if (foundItem) break;
        }
        if (foundItem && itemRef != null) {
        	Item item = ItemFactoryCollection.getItem(itemRef.get().getItemCode(), itemRef.get().getId(), itemRef.get().getName(),itemRef.get().getComment(), itemRef.get().getRoom(), itemRef.get().getCount(),  itemRef.get().getX(), itemRef.get().getY(), itemRef.get().getCreationTime(), itemRef.get().getStates());
			getSession().send(ServerMessages.sendItemField(playerId, OBJECTCODE.CONTAINER_EQUIPMENT_TROUSERS, item, fieldX, fieldY));
        } else {
        	// nothing should be send to player
        }  

	}
	
	/** send the updated Equipment to other players */
	public void sendPlayersMyUpdatedEquipment(String playerName) {
		String armorName = "";
		if (this.getWorldRoom().getContainerEqBodyList().get().get(playerName).get().getItemList().values().iterator().hasNext())
			armorName = this.getWorldRoom().getContainerEqBodyList().get().get(playerName).get().getItemList().values().iterator().next().get().getName();
		String weaponName = "";
		if (this.getWorldRoom().getContainerEqWeaponList().get().get(playerName).get().getItemList().values().iterator().hasNext())
			weaponName = this.getWorldRoom().getContainerEqWeaponList().get().get(playerName).get().getItemList().values().iterator().next().get().getName();
		String feetName = "";
		if (this.getWorldRoom().getContainerEqFeetList().get().get(playerName).get().getItemList().values().iterator().hasNext())
			feetName = this.getWorldRoom().getContainerEqFeetList().get().get(playerName).get().getItemList().values().iterator().next().get().getName();
		String helmetName = "";
		if (this.getWorldRoom().getContainerEqHelmetList().get().get(playerName).get().getItemList().values().iterator().hasNext())
			helmetName = this.getWorldRoom().getContainerEqHelmetList().get().get(playerName).get().getItemList().values().iterator().next().get().getName();
		String shieldName = "";
		if (this.getWorldRoom().getContainerEqShieldList().get().get(playerName).get().getItemList().values().iterator().hasNext())
			shieldName = this.getWorldRoom().getContainerEqShieldList().get().get(playerName).get().getItemList().values().iterator().next().get().getName();
		String amuletName = "";
		if (this.getWorldRoom().getContainerEqAmuletList().get().get(playerName).get().getItemList().values().iterator().hasNext())
			amuletName = this.getWorldRoom().getContainerEqAmuletList().get().get(playerName).get().getItemList().values().iterator().next().get().getName();
		String gloveName = "";
		if (this.getWorldRoom().getContainerEqGloveList().get().get(playerName).get().getItemList().values().iterator().hasNext())
			gloveName = this.getWorldRoom().getContainerEqGloveList().get().get(playerName).get().getItemList().values().iterator().next().get().getName();
		String shirtName = "";
		if (this.getWorldRoom().getContainerEqShirtList().get().get(playerName).get().getItemList().values().iterator().hasNext())
			shirtName = this.getWorldRoom().getContainerEqShirtList().get().get(playerName).get().getItemList().values().iterator().next().get().getName();
		String trousersName = "";
		if (this.getWorldRoom().getContainerEqTrousersList().get().get(playerName).get().getItemList().values().iterator().hasNext())
			trousersName = this.getWorldRoom().getContainerEqTrousersList().get().get(playerName).get().getItemList().values().iterator().next().get().getName();
		this.getWorldRoom().sendEditPlayerEquipment(this.getId(), weaponName, armorName, helmetName, feetName, shieldName, amuletName, gloveName, shirtName, trousersName);
	}
	
	
	public void sendPlayerData(BigInteger id) {
		PlayerEntity[] playerArray = new PlayerEntity[1];
		playerArray[0] = PlayerManager.get(id);
		getSession().send(ServerMessages.sendPlayers(playerArray));
	}
	
	/** start a download for a specific texture */
	public void startTextureDownload(String textureName) {
		String thisPlayerName = this.getName().substring(this.getName().indexOf(".")+1, this.getName().length());
		getSession().send(ServerMessages.sendTextureStart(thisPlayerName, textureName));
	}

	/** {@inheritDoc} */
    public void disconnected(boolean graceful) {
        setSession(null, null);
        logger.log(Level.INFO, "Disconnected: {0}", this);
        getWorldRoom().removePlayer(this);
        PlayerManager.remove(this.getId());
        
        
        setWorldRoom(null);
    }

    /**
     * Returns the room this player is currently in, or {@code null} if
     * this player is not in a room.
     * <p>
     * @return the room this player is currently in, or {@code null}
     */
    protected WorldRoom getWorldRoom() {
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
    protected void setWorldRoom(WorldRoom room) {
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
    
    private static ITEMCODE getItemCode(ByteBuffer packet) 
    {
        byte itemByte = packet.get();
        if ((itemByte < 0) || (itemByte > ITEMCODE.values().length - 1)) {
        	//TODO: exception is better
        	System.out.println("Unknown es value: " + itemByte);
//            logger.severe("Unknown op value: " + opbyte);
            return null;
        }
        ITEMCODE itemCode = ITEMCODE.values()[itemByte];
        
        return itemCode;
    }
    
    private static Priority getPriority(ByteBuffer packet) 
    {
        byte priorityByte = packet.get();
        if ((priorityByte < 0) || (priorityByte > Priority.values().length - 1)) {
        	//TODO: exception is better
        	System.out.println("Unknown es value: " + priorityByte);
//            logger.severe("Unknown op value: " + opbyte);
            return null;
        }
        Priority priority = Priority.values()[priorityByte];
        
        return priority;
    }
    
    /**
     * Get the OPCODE from the packet
     * @param packet
     * @return
     */
    private OBJECTCODE getObjectCode(ByteBuffer packet) 
    {
       // byte opbyte = packet.get();
        byte objbyte = packet.get();
        if ((objbyte < 0) || (objbyte > OBJECTCODE.values().length - 1)) {
            logger.severe("Unknown op value: " + objbyte);
            return null;
        }
        OBJECTCODE code = OBJECTCODE.values()[objbyte];
        
        return code;
    }
    
    public boolean isReady() {
    	return ready;
    }
    
    public void setReady(boolean bool) {
    	this.ready = bool;
    }
    
    public boolean isInitializingEntities() {
    	return initializingEntities;
    }
    
    public void setInitializingEntities(boolean init) {
    	this.initializingEntities=init;
    }
    
    public boolean isInitializingItems() {
    	return initializingItems;
    }
    
    public void setInitializingItems(boolean init) {
    	this.initializingItems=init;
    }

	public void sendEditSpaceAddons(BigInteger id, String textureName, int[] rgb, float trans, int filled, float scale, float area) {
		// TODO Auto-generated method stub
		getSession().send(ServerMessages.editSpaceAddons(id, textureName, rgb, trans, filled, scale, area));
	}
	
	public void sendEditPlayerAddons(BigInteger id, String playerName, String room, String tileName, String tilePathName, int tileWidth, int tileHeight, String country, String groupName, int experience) {
		// TODO Auto-generated method stub
		getSession().send(ServerMessages.editPlayerAddons(id, playerName, room, tileName, tilePathName, tileWidth, tileHeight, country, groupName, experience));
	}
	
	public void sendEditPlayerEquipment(BigInteger id, String weaponName, String armorName, String helmetName, String feetName, String shieldName, String amuletName, String gloveName, String shirtName, String trousersName) {
		getSession().send(ServerMessages.editPlayerEquipment(id, weaponName, armorName, helmetName, feetName, shieldName, amuletName, gloveName, shirtName, trousersName));
//		System.out.println("send edit PlayerEquipment: "+weaponName + armorName + helmetName + feetName);
	}
	
	public boolean isReadyForNextMessage() {
		return readyForNextMessage;
	}
	
	public void setReadyForNextMessage(boolean b) {
		readyForNextMessage = b;
	}

	public boolean isInitializingAreaInfluences() {
		return initializingAreaInfluences;
	}

	public void setInitializingAreaInfluences(boolean initializingAreaInfluences) {
		this.initializingAreaInfluences = initializingAreaInfluences;
	}
	
	public boolean isInitializingGrowthSpots() {
		return initializingGrowthSpots;
	}

	public void setInitializingGrowthSpots(boolean b) {
		this.initializingGrowthSpots = b;
	}
	
	
	private static EntityStates getEntityStates(ByteBuffer packet) 
    {
        byte esbyte = packet.get();
        if ((esbyte < 0) || (esbyte > EntityStates.values().length - 1)) {
        	//TODO: exception is better
        	System.out.println("Unknown es value: " + esbyte);
//            logger.severe("Unknown op value: " + opbyte);
            return null;
        }
        EntityStates entityStates = EntityStates.values()[esbyte];
        
        return entityStates;
    }
}