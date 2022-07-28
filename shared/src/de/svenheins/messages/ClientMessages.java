package de.svenheins.messages;

import java.awt.Polygon;
import java.awt.TexturePaint;
import java.awt.image.BufferedImage;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.ArrayList;

import de.svenheins.animation.SpaceAnimation;
import de.svenheins.objects.Space;


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
     * @param id, state
     * @return ByteBuffer
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
   
    
    public static ByteBuffer initTextures() {
        byte[] bytes = new byte[1];
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        buffer.put((byte) OPCODE.INITTEXTURES.ordinal());
//        buffer.putInt(objCode.ordinal());
        buffer.flip();
        return buffer;
    }
    
    /** edit space-addons */
    public static ByteBuffer editSpaceAddons(BigInteger id, String textureName, int[] rgb, float trans, int filled, float scale, float area) {
        byte[] bytes = new byte[1 + 8 + 4 + textureName.length() + 4 + 4 + 4 + 4 + 4 + 4 + 4];
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        buffer.put((byte) OPCODE.EDIT_SPACE_ADDONS.ordinal());
        /** ID */
        buffer.putLong(id.longValue()); // 8 Bytes
        
        buffer.putInt(textureName.length()); // 4
    	buffer.put(textureName.getBytes()); // textureName.length
    	buffer.putInt(rgb[0]); // 4
    	buffer.putInt(rgb[1]); // 4
    	buffer.putInt(rgb[2]); // 4
    	buffer.putFloat(trans); // 4
    	buffer.putInt(filled); // 4
    	buffer.putFloat(scale); // 4
    	buffer.putFloat(area); // 4
        
        buffer.flip();
        return buffer;
    }
    
    /** ready for next Texture PACKET */
    public static ByteBuffer sendReadyForNextTexturePacket (String name, int oldPacketId) {
        byte[] bytes = new byte[1 + 4 + name.length() + 4];
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        buffer.put((byte) OPCODE.READY_FOR_NEXT_TEXTURE_PACKET.ordinal()); // 1
        
        buffer.putInt(name.length()); // 4
    	buffer.put(name.getBytes()); // name.length
        buffer.putInt(oldPacketId); // 4
        
        buffer.flip();
        return buffer;
    }
    
    /** give me the missing textures */
    public static ByteBuffer sendMissingTextures(ArrayList<String> missingTextures) {
    	int bytesForTextureNames = 0;
		for (String s : missingTextures) {
			bytesForTextureNames += s.length() + 4;
		}
		byte[] bytes = new byte[1 + 4 + bytesForTextureNames];
		
		ByteBuffer buffer = ByteBuffer.wrap(bytes);
		buffer.put((byte) OPCODE.SEND_MISSING_TEXTURES.ordinal()); // 1
		buffer.putInt(missingTextures.size()); // 4
		for (String s : missingTextures) {
			buffer.putInt(s.length()); // 4
			buffer.put(s.getBytes()); // s.length()
		}
		buffer.flip();
        return buffer;
    }
    
    /** ready for next TEXTURE */
    public static ByteBuffer sendNextTexture(String lastName) {
        byte[] bytes = new byte[1 + 4 + lastName.length()];
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        buffer.put((byte) OPCODE.READY_FOR_NEXT_TEXTURE.ordinal()); // 1
        
        buffer.putInt(lastName.length()); // 4
    	buffer.put(lastName.getBytes()); // name.length
        
        buffer.flip();
        return buffer;
    }
    
    /** 
     * Upload Object onto the server
     * @param id, state
     * @return ByteBuffer
     */
    public static ByteBuffer uploadObject(OBJECTCODE objCode, BigInteger id,  Space uploadSpace) {
    	/** divide Space by all its relevant parts */
    	int[] pubXCoord = uploadSpace.getPubXCoord(); // (pubXCoord.length * 4) bytes
    	int[] pubYCoord = uploadSpace.getPubYCoord(); // (pubYCoord.length * 4) bytes
    	int[] rgb = uploadSpace.getRGB(); // (rgb.length * 4) bytes
    	String name = uploadSpace.getName();
    	float trans = uploadSpace.getTrans(); // 4 bytes
    	Boolean filled = uploadSpace.isFilled(); // 4 bytes
    	float scale = uploadSpace.getScale(); // 4 bytes
    	float area = uploadSpace.getArea(); // 4 bytes
    	
    	String textureName = uploadSpace.getTextureName(); // 4 + textureName.length
    	
    	int polyX = uploadSpace.getPolyX(); // 4 bytes
    	int polyY = uploadSpace.getPolyY(); // 4 bytes
    	/** now the more complex structure */
    	ArrayList<Polygon> polygon = uploadSpace.getPolygon();
    	int numPolygons = polygon.size();
    	/** begin with the number of polygonElements */
    	int bytesOfPolygon = 4;
    	for (int i = 0; i < numPolygons; i++) {
    		Polygon actualPolygon = polygon.get(i);
    		int[] actualPolygonX = actualPolygon.xpoints;
    		/** save the length and the x- and y-coordinates */
    		bytesOfPolygon += (4 + 4*actualPolygonX.length*2);
    	}

    	byte[] bytes = new byte[1 + 4 + 8 + 4 + name.length() + 4 + (pubXCoord.length * 4) + (pubYCoord.length * 4)+(rgb.length * 4)+4+4+4+4+ 4 + textureName.length() +4+4 + bytesOfPolygon];
    	
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        buffer.put((byte) OPCODE.UPLOAD_OBJECT.ordinal()); // 1 Byte
        buffer.putInt(objCode.ordinal()); // 4 Bytes 
        
        /** ID */
        buffer.putLong(id.longValue()); // 8 Bytes
        /** name */
        buffer.putInt(name.length()); // 4
    	buffer.put(name.getBytes()); // name.length
        /** insert pubXCoord and pubYCoord*/
        buffer.putInt(pubXCoord.length); // 4 bytes
        for (int i = 0; i < pubXCoord.length; i++) {
        	buffer.putInt(pubXCoord[i]); // 4 Bytes
        	buffer.putInt(pubYCoord[i]); // 4 Bytes
        }
        /** insert rgb*/
        for (int i = 0; i < rgb.length; i++) {
        	buffer.putInt(rgb[i]);
        }
        // trans
        buffer.putFloat(trans); // 4 Bytes
        // filled
        if(filled) buffer.putInt(1); else buffer.putInt(0); // 4 bytes
        // scale
        buffer.putFloat(scale); // 4 Bytes
        // area
        buffer.putFloat(area); // 4 Bytes
        // textureName.length()
        buffer.putInt(textureName.length()); // 4 Bytes
        // textureName
        buffer.put(textureName.getBytes()); // textureName.length
        // polyX
        buffer.putInt(polyX); // 4 bytes
        // polyY
        buffer.putInt(polyY); // 4 bytes
        /** now the more complex polygon structure */
        /** first save the number of polygon-structures */
        buffer.putInt(numPolygons); // 4 bytes
    	for (int i = 0; i < numPolygons; i++) {
    		Polygon actualPolygon = polygon.get(i);
    		int[] actualPolygonX = actualPolygon.xpoints;
    		int[] actualPolygonY = actualPolygon.ypoints;
    		/** save the length of the actual Polygon */
    		buffer.putInt(actualPolygonX.length);  // 4 bytes
    		/** put the actual Polygon into the buffer */
    		for (int j = 0; j < actualPolygonX.length; j++) {
    			buffer.putInt(actualPolygonX[j]); // 4 bytes
    			buffer.putInt(actualPolygonY[j]); // 4 bytes
    		}
    	}
        buffer.flip();
        return buffer;
    }
    
    
    /** 
     * Upload Texture
     * @param name, packageId, image: we need image-bytes!
     * @return ByteBuffer
     */
    public static ByteBuffer uploadTexture(String name, int packetId, int numberOfPackets, int sizeOfPacket, byte[] image, String playerName) {
    	/** define byte array */
    	byte[] bytes = new byte[1 + 4 + name.length() + 4 + 4 + 4+ image.length + 4 + playerName.length()];
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        buffer.put((byte) OPCODE.UPLOAD_TEXTURE.ordinal()); // 1 Byte
        /** name */
        buffer.putInt(name.length()); // 4
    	buffer.put(name.getBytes()); // name.length
    	/** packageId */
    	buffer.putInt(packetId); // 4
    	/** the number of packets */
    	buffer.putInt(numberOfPackets); // 4
    	/** store the size of the actual packet */
    	buffer.putInt(sizeOfPacket); // 4
    	/** image data */
    	buffer.put(image); // image.length
    	/** put player Name */
    	buffer.putInt(playerName.length()); // 4
    	buffer.put(playerName.getBytes()); // playerName.length
    	
    	/** buffer is ready */
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
