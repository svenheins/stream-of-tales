package de.svenheins.messages;

import java.math.BigInteger;
import java.nio.ByteBuffer;

import de.svenheins.objects.Entity;
import de.svenheins.objects.Space;

/** Message from the server TO the client */
public class ServerMessages extends Messages{
	/**
     * Create a "new game" packet which notifies the client to enter battle state
     * with given ID number and map name.
     * @param myID The ID number assigned to the client.
     * @param mapname The name of the map to play on.
     * @return The <code>ByteBuffer</code> "new game" packet.
     */
    public static ByteBuffer createNewGamePkt(BigInteger myID, String mapname) {
        byte[] bytes = new byte[1 + 4 + myID.toByteArray().length + mapname.length()];
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        buffer.put((byte) OPCODE.NEWGAME.ordinal());
        buffer.put(myID.toByteArray()); // 8 Bytes (?)
        buffer.putInt(mapname.length());
        buffer.put(mapname.getBytes());
        
        buffer.flip();
        return buffer;
    }
    

    /** get object state */
    public static ByteBuffer sendObjectState (OBJECTCODE objCode, BigInteger id,  float[] state) {
        byte[] bytes = new byte[1 + 4 + 8 + 24];
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        buffer.put((byte) OPCODE.OBJECTSTATE.ordinal());
        
        // insert the Object-Code that identifies the Object 
        buffer.putInt(objCode.ordinal());
        
        buffer.putLong(id.longValue()); // 8 Bytes
        // x
        buffer.putFloat(state[0]);
        // y
        buffer.putFloat(state[1]);
        // mx
        buffer.putFloat(state[2]);
        // my
        buffer.putFloat(state[3]);
        // height
        buffer.putFloat(state[4]);
        // width
        buffer.putFloat(state[5]);
        buffer.flip();
        return buffer;
    }
    
//    /** get object name */
//    public static ByteBuffer sendObjectName (OBJECTCODE objCode, int id,  String name) {
//        byte[] bytes = new byte[1 + 4 + 4 + name.length()];
//        ByteBuffer buffer = ByteBuffer.wrap(bytes);
//        buffer.put((byte) OPCODE.OBJECTNAME.ordinal());
//        
//        // insert the Object-Code that identifies the Object 
//        buffer.putInt(objCode.ordinal());
//        
//        buffer.putInt(id);
//        // insert Name
//        buffer.putInt(name.length());
//        buffer.put(name.getBytes());
//        buffer.flip();
//        return buffer;
//    }

    /** send all entityObjects */
    public static ByteBuffer sendEntities(Entity[] localObjects) {
    	byte[] bytes;
    	ByteBuffer buffer = null;
    	/** get the length of Bytes that must be reserved for the names */
		int namesLength = 0;
		
		for (int i = 0; i<localObjects.length; i++) {
			namesLength += localObjects[i].getName().length();
		}
		/** use Object-specific send-routine */
		/** init bytes */
		bytes = new byte[1 + (12)*localObjects.length + namesLength];
    	buffer = ByteBuffer.wrap(bytes);
        buffer.put((byte) OPCODE.INITENTITIES.ordinal());	
        Entity[] lEntity = (Entity[]) localObjects;
        /** place all objects of the specific class */
        for (int i = 0; i<localObjects.length; i++) {
    		Entity e = lEntity[i];
        	BigInteger id = e.getId();
    		String name = e.getName();
    		buffer.putLong(id.longValue()); // 8
    		buffer.putInt(name.length()); // 4
        	buffer.put(name.getBytes()); // name.length
    	}
    	buffer.flip();
    		
        return buffer;
    }
    
    
    public static ByteBuffer sendSpaces(Space[] localObjects) {
    	byte[] bytes;
    	ByteBuffer buffer = null;
    	/** get the length of Bytes that must be reserved for the names */
		int namesLength = 0;
		for (int i = 0; i<localObjects.length; i++) {
			namesLength += localObjects[i].getName().length();
		}
		/** use Object-specific send-routine */
		/** init bytes */
		bytes = new byte[1 + 36*localObjects.length + namesLength];
    	buffer = ByteBuffer.wrap(bytes);
        buffer.put((byte) OPCODE.INITSPACES.ordinal()); 
    	
        Space[] lSpace = (Space[]) localObjects;
        /** place all objects of the specific class */
        int[] rgb_each;
        Space s;
        BigInteger id;
        String name;
        for (int i = 0; i<localObjects.length; i++) {
    		s = lSpace[i];
        	id = s.getId();
    		name = s.getName();
    		rgb_each = s.getRGB();
    		int filled;
    		if (s.isFilled()) filled = 1;
    		else filled = 0;
    		float trans = s.getTrans();
    		float scale = s.getScale();
    		buffer.putLong(id.longValue()); // 8
    		buffer.putInt(name.length()); // 4
        	buffer.put(name.getBytes()); // name.length
        	buffer.putInt(rgb_each[0]); // 4
        	buffer.putInt(rgb_each[1]); // 4
        	buffer.putInt(rgb_each[2]); // 4
        	buffer.putInt(filled); // 4
        	buffer.putFloat(trans); // 4
        	buffer.putFloat(scale); // 4
    	} 	
    	buffer.flip();
    		
        return buffer;
    }
    
    /** get object state */
    public static ByteBuffer sendDelete (OBJECTCODE objCode, BigInteger id) {
        byte[] bytes = new byte[1 + 4 + 8];
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        buffer.put((byte) OPCODE.OBJECTDELETE.ordinal());
        
        // insert the Object-Code that identifies the Object 
        buffer.putInt(objCode.ordinal());
        
        buffer.putLong(id.longValue()); // 8 Bytes
        buffer.flip();
        return buffer;
    }

//    /**
//     * Create a "start game" packet which notifies the client to start the battle.
//     * @return The <code>ByteBuffer</code> "start game" packet.
//     */
//    public static ByteBuffer createStartGamePkt() {
//        byte[] bytes = new byte[1];
//        ByteBuffer buffer = ByteBuffer.wrap(bytes);
//        buffer.put((byte) OPCODE.STARTGAME.ordinal());
//        
//        buffer.flip();
//        return buffer;
//    }
//
//    /**
//     * Create a "end game" packet which notifies the client to end the battle
//     * with given state.
//     * @param state The <code>EndState</code> of the battle.
//     * @return The <code>ByteBuffer</code> "end game" packet.
//     */
//    public static ByteBuffer createEndGamePkt(EEndState state) {
//        byte[] bytes = new byte[1 + 4];
//        ByteBuffer buffer = ByteBuffer.wrap(bytes);
//        buffer.put((byte) OPCODE.ENDGAME.ordinal());
//        buffer.putInt(state.ordinal());
//        
//        buffer.flip();
//        return buffer;
//    }
//
//
//    /**
//     * Create an "add MOB" packet which notifies the client to add a MOB with
//     * given ID number, <code>MOBType</code> at the given position.
//     * @param targetID The ID number of the new map object.
//     * @param x The X coordinate of the object.
//     * @param y The Y coordinate of the object.
//     * @param mobType The <code>MOBType</code> of object.
//     * @param team The <code>TeamColor</code> of object.
//     * @return The <code>ByteBuffer</code> "add MOB" packet.
//     */
//    public static ByteBuffer createAddMOBPkt(int targetID, float x, float y, EMOBType mobType, ETeamColor team, String mobName) {
//        byte[] bytes = new byte[1 + 20 + 4 + mobName.length()];
//        ByteBuffer buffer = ByteBuffer.wrap(bytes);
//        buffer.put((byte) OPCODE.ADDMOB.ordinal());
//        buffer.putInt(targetID);
//        buffer.putFloat(x);
//        buffer.putFloat(y);
//        buffer.putInt(mobType.ordinal());
//        buffer.putInt(team.ordinal());
//        buffer.putInt(mobName.length());
//        buffer.put(mobName.getBytes());
//        
//        buffer.flip();
//        return buffer;
//    }
//    
//    /**
//     * Create a "remove MOB" packet which notifies the client to remove
//     * the MOB with given ID.
//     * @param targetID The ID number of the MOB to be removed.
//     * @return The <code>ByteBuffer</code> "remove MOB" packet.
//     */
//    public static ByteBuffer createRemoveMOBPkt(int targetID) {
//        byte[] bytes = new byte[1 + 4];
//        ByteBuffer buffer = ByteBuffer.wrap(bytes);
//        buffer.put((byte) OPCODE.REMOVEMOB.ordinal());
//        buffer.putInt(targetID);
//        
//        buffer.flip();
//        return buffer;
//    }
//
//    /**
//     * Create a "move MOB" packet which notifies the client to move the MOB
//     * with given ID towards the given destination.
//     * @param targetID The ID number of the MOB to be moved.
//     * @param startx The x coordinate of the starting position.
//     * @param starty The y coordinate of the starting position.
//     * @param endx The x coordinate of the ending position.
//     * @param endy The y coordinate of the ending position.
//     * @return The <code>ByteBuffer</code> "move MOB" packet.
//     */
//    public static ByteBuffer createMoveMOBPkt(int targetID, float startx, float starty, float endx, float endy) {
//        byte[] bytes = new byte[1 + 20];
//        ByteBuffer buffer = ByteBuffer.wrap(bytes);
//        buffer.put((byte) OPCODE.MOVEMOB.ordinal());
//        buffer.putInt(targetID);
//        buffer.putFloat(startx);
//        buffer.putFloat(starty);
//        buffer.putFloat(endx);
//        buffer.putFloat(endy);
//        
//        buffer.flip();
//        return buffer;
//    }
//    
//    /**
//     * Create a "stop MOB" packet which notifies the client to stop the MOB
//     * with given ID at the given destination
//     * @param targetID The ID number of the MOB to be stopped.
//     * @param x The x coordinate of the stop position.
//     * @param y The y coordinate of the stop position.
//     * @return The <code>ByteBuffer</code> "move MOB" packet.
//     */
//    public static ByteBuffer createStopMOBPkt(int targetID, float x, float y) {
//        byte[] bytes = new byte[1 + 12];
//        ByteBuffer buffer = ByteBuffer.wrap(bytes);
//        buffer.put((byte) OPCODE.STOPMOB.ordinal());
//        buffer.putInt(targetID);
//        buffer.putFloat(x);
//        buffer.putFloat(y);
//        
//        buffer.flip();
//        return buffer;
//    }
//
//    /**
//     * Create an "attach object" packet which notifies the client to attach
//     * the object with given source ID to the object with given target ID.
//     * @param sourceID The ID number of the object to be re-attached.
//     * @param targetID the ID number of the object to attach it to.
//     * @return The <code>ByteBuffer</code> "attach object" packet.
//     */
//    public static ByteBuffer createAttachObjPkt(int sourceID, int targetID) {
//        byte[] bytes = new byte[1 + 8];
//        ByteBuffer buffer = ByteBuffer.wrap(bytes);
//        buffer.put((byte) OPCODE.ATTACHOBJ.ordinal());
//        buffer.putInt(sourceID);
//        buffer.putInt(targetID);
//        
//        buffer.flip();
//        return buffer;
//    }
//
//    /**
//     * Create an "attacked" packet which notifies the client that the object
//     * with given source ID has attacked the object with given target ID.
//     * @param sourceID The ID number of the attacker.
//     * @param targetID The ID number of the target.
//     * @param hp The hit point of the target (-1 if a miss)
//     * @return The <code>ByteBuffer</code> "attacked" packet.
//     */
//    public static ByteBuffer createAttackedPkt(int sourceID, int targetID, int hp) {
//        byte[] bytes = new byte[1 + 12];
//        ByteBuffer buffer = ByteBuffer.wrap(bytes);
//        buffer.put((byte) OPCODE.ATTACKED.ordinal());
//        buffer.putInt(sourceID);
//        buffer.putInt(targetID);
//        buffer.putInt(hp);
//        
//        buffer.flip();
//        return buffer;
//    }
//
//    /**
//     * Create a "respawn" packet which notifies the client to respawn
//     * the object with given ID at the given location.
//     * @param objectID The ID number of the object to be respawn.
//     * @param x The x coordinate of the respawn position.
//     * @param y The y coordinate of the respawn position.
//     * @return The <code>ByteBuffer</code> "respawn" packet.
//     */
//    public static ByteBuffer createRespawnPkt(int objectID, float x, float y) {
//        byte[] bytes = new byte[1 + 12];
//        ByteBuffer buffer = ByteBuffer.wrap(bytes);
//        buffer.put((byte) OPCODE.RESPAWN.ordinal());
//        buffer.putInt(objectID);
//        buffer.putFloat(x);
//        buffer.putFloat(y);
//        
//        buffer.flip();
//        return buffer;
//    }
//    
//    /**
//     * Create a chat message packet with given values.
//     * @param sourceID The <code>Integer</code> source ID.
//     * @param message The <code>String</code> message to be displayed.
//     * @return The <code>ByteBuffer</code> 'chat message' packet.
//     */
//    public static ByteBuffer createChatPkt(int sourceID, String message) {
//    	byte[] bytes = new byte[1 + 4 + 4 + message.length()];
//    	ByteBuffer buffer = ByteBuffer.wrap(bytes);
//    	buffer.put((byte) OPCODE.CHAT.ordinal());
//        buffer.putInt(sourceID);
//    	buffer.putInt(message.length());
//    	buffer.put(message.getBytes());
//        
//        buffer.flip();
//    	return buffer;
//    }
}
