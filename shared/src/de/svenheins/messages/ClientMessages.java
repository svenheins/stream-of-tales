package de.svenheins.messages;

import java.math.BigInteger;
import java.nio.ByteBuffer;


/** Message from the client TO the server */
public class ClientMessages extends Messages{
	/**
     * Create a "move me" packet which notifies the server that this client is trying
     * to move towards the given destination.
     * @param x The x coordinate of the start point.
     * @param y The y coordinate of the start point.
     * @param endx The x coordinate of the clicked position.
     * @param endy The y coordinate of the clicked position.
     * @return The <code>ByteBuffer</code> "move me" packet.
     */
    public static ByteBuffer createMoveMePkt(float x, float y, float endx, float endy) {
        byte[] bytes = new byte[1 + 16];
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        buffer.put((byte) OPCODE.MOVEME.ordinal());
        buffer.putFloat(x);
        buffer.putFloat(y);
        buffer.putFloat(endx);
        buffer.putFloat(endy);
        buffer.flip();
        return buffer;
    }
    
    
//    public static ByteBuffer getEntityState(int id) {
//        byte[] bytes = new byte[1+4];
//        ByteBuffer buffer = ByteBuffer.wrap(bytes);
//        buffer.put((byte) OPCODE.EYE.ordinal());
//        buffer.putInt(id);
//        buffer.flip();
//        return buffer;
//    }
//    
//    public static ByteBuffer getSpaceState(int id) {
//        byte[] bytes = new byte[1+4];
//        ByteBuffer buffer = ByteBuffer.wrap(bytes);
//        buffer.put((byte) OPCODE.SPACE.ordinal());
//        buffer.putInt(id);
//        buffer.flip();
//        return buffer;
//    }
    /** 
     * 
     * @param id
     * @return 
     */
    public static ByteBuffer getObjectState(OBJECTCODE objCode, int id) {
        byte[] bytes = new byte[1 + 4 + 4];
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        buffer.put((byte) OPCODE.OBJECTSTATE.ordinal());
        buffer.putInt(objCode.ordinal());
        buffer.putInt(id);
        buffer.flip();
        return buffer;
    }
    
    /** 
     * 
     * @param id
     * @return 
     */
    public static ByteBuffer editObjectState(OBJECTCODE objCode, BigInteger id,  float[] state) {
        byte[] bytes = new byte[1 + 4 + 8 + 24];
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        buffer.put((byte) OPCODE.EDIT_OBJECT.ordinal());
        buffer.putInt(objCode.ordinal());
        
        /** ID */
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
    
    public static ByteBuffer initEntities() {
        byte[] bytes = new byte[1];
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        buffer.put((byte) OPCODE.INITENTITIES.ordinal());
//        buffer.putInt(objCode.ordinal());
        buffer.flip();
        return buffer;
    }
    
    public static ByteBuffer initSpaces() {
        byte[] bytes = new byte[1];
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        buffer.put((byte) OPCODE.INITSPACES.ordinal());
//        buffer.putInt(objCode.ordinal());
        buffer.flip();
        return buffer;
    }
   
//    public static ByteBuffer getIds(OBJECTCODE objCode, int id) {
//        byte[] bytes = new byte[1 + 4 + 4];
//        ByteBuffer buffer = ByteBuffer.wrap(bytes);
//        buffer.put((byte) OPCODE.OBJECTSTATE.ordinal());
//        buffer.putInt( objCode.ordinal());
//        buffer.putInt(id);
//        buffer.flip();
//        return buffer;
//    }
    
//    public static ByteBuffer getIdsAll(OBJECTCODE objCode) {
//        byte[] bytes = new byte[1 + 4];
//        ByteBuffer buffer = ByteBuffer.wrap(bytes);
//        buffer.put((byte) OPCODE.IDS.ordinal());
//        buffer.putInt( objCode.ordinal());
//        buffer.flip();
//        return buffer;
//    }

//    /**
//     * Create an "attack" packet which notifies the server that the sending
//     * client is attacking the object with given ID from the given location.
//     * @param targetID The ID number of the target.
//     * @param x The x coordinate of the position of the attacker
//     * @param y The y coordinate of the position of the attacker
//     * @return The <code>ByteBuffer</code> "attack" packet.
//     */
//    public static ByteBuffer createAttackPkt(int targetID, float x, float y) {
//        byte[] bytes = new byte[1 + 12];
//        ByteBuffer buffer = ByteBuffer.wrap(bytes);
//        buffer.put((byte) OPCODE.ATTACK.ordinal());
//        buffer.putInt(targetID);
//        buffer.putFloat(x);
//        buffer.putFloat(y);
//        
//        buffer.flip();
//        return buffer;
//    }
//
//
//    /**
//     * Create a "get flag" packet which notifies the server that this client is
//     * trying to pick up the flag with given ID.
//     * @param flagID The ID number of the flag the client is picking up.
//     * @param x The x coordinate of the client's position
//     * @param y The y coordinate of the client's position
//     * @return The <code>ByteBuffer</code> "get flag" packet.
//     */
//    public static ByteBuffer createGetFlagPkt(int flagID, float x, float y) {
//        byte[] bytes = new byte[1 + 12];
//        ByteBuffer buffer = ByteBuffer.wrap(bytes);
//        buffer.put((byte) EOPCODE.GETFLAG.ordinal());
//        buffer.putInt(flagID);
//        buffer.putFloat(x);
//        buffer.putFloat(y);
//        
//        buffer.flip();
//        return buffer;
//    }
//    
//    /**
//     * Create a "score" packet which notifies the server that this client
//     * has reached the goal position with the flag and wishes to place the flag
//     * down to win the game.
//     * @param x The x coordinate of the client's position
//     * @param y The y coordinate of the client's position
//     * @return The <code>ByteBuffer</code> "place flag" packet
//     */
//    public static ByteBuffer createScorePkt(float x, float y) {
//        byte[] bytes = new byte[1 + 8];
//        ByteBuffer buffer = ByteBuffer.wrap(bytes);
//        buffer.put((byte) EOPCODE.SCORE.ordinal());
//        buffer.putFloat(x);
//        buffer.putFloat(y);
//        
//        buffer.flip();
//        return buffer;
//    }
//    
//    /**
//     * Create a chat message packet with given values.
//     * @param message The <code>String</code> message to be displayed.
//     * @return The <code>ByteBUffer</code> 'chat message' packet.
//     */
//    public static ByteBuffer createChatPkt(String message) {
//    	byte[] bytes = new byte[1 + 4 + message.length()];
//    	ByteBuffer buffer = ByteBuffer.wrap(bytes);
//    	buffer.put((byte) EOPCODE.CHAT.ordinal());
//    	buffer.putInt(message.length());
//    	buffer.put(message.getBytes());
//        
//        buffer.flip();
//    	return buffer;
//    }
}
