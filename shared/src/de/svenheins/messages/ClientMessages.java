package de.svenheins.messages;

import java.awt.Polygon;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.ArrayList;

import de.svenheins.main.EntityStates;
import de.svenheins.objects.AreaInfluence;
import de.svenheins.objects.InteractionTile;
import de.svenheins.objects.LocalObject;
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
    
    public static ByteBuffer chat(String str) {
    	byte[] bytes = new byte[1 + 4 + str.length()];
    	ByteBuffer chatBuffer = ByteBuffer.wrap(bytes);
    	chatBuffer.put((byte) OPCODE.CHAT.ordinal());
    	chatBuffer.putInt(str.length()); // 4
    	chatBuffer.put(str.getBytes()); // playerName.length() 
    	chatBuffer.flip();
    	return chatBuffer;
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
    public static ByteBuffer takeItem(BigInteger id) {
        byte[] bytes = new byte[1 + 8];
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        buffer.put((byte) OPCODE.TAKEITEM.ordinal());
        buffer.putLong(id.longValue()); // 8 Bytes
        buffer.flip();
        return buffer;
    }
    
    /** 
     * 
     * @param id
     * @return 
     */
    public static ByteBuffer tookItem(BigInteger id) {
        byte[] bytes = new byte[1 + 8];
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        buffer.put((byte) OPCODE.TOOKITEM.ordinal());
        buffer.putLong(id.longValue()); // 8 Bytes
        buffer.flip();
        return buffer;
    }
    
    /** 
     * 
     * @param id
     * @return 
     */
    public static ByteBuffer addItem(BigInteger id, ITEMCODE itemCode_ADDITEM, int count_ADDITEM, int capacity_ADDITEM, float x_ADDITEM, float y_ADDITEM, float mx_ADDITEM, float my_ADDITEM, String itemName_ADDITEM, String spriteString_ADDITEM, String spriteShortName_ADDITEM, float[] itemStates_ADDITEM) {
        int itemStatesLength = 0;
    	if (itemStates_ADDITEM != null) {
        	itemStatesLength = itemStates_ADDITEM.length;
        }
    	byte[] bytes = new byte[1 + 8 + 1 + 4 + 4 + 4 + 4 + 4 + 4 + 4 + itemName_ADDITEM.length() + 4 + spriteString_ADDITEM.length() + 4 + spriteShortName_ADDITEM.length() + 4 + 4*itemStatesLength ];
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        buffer.put((byte) OPCODE.ADDITEM.ordinal());
        buffer.putLong(id.longValue()); // 8 Bytes
        
        buffer.put((byte) itemCode_ADDITEM.ordinal()); // 1
		buffer.putInt(count_ADDITEM); // 4
		buffer.putInt(capacity_ADDITEM); // 4
		buffer.putFloat(x_ADDITEM); // 4
		buffer.putFloat(y_ADDITEM); // 4
		buffer.putFloat(mx_ADDITEM); // 4
		buffer.putFloat(my_ADDITEM); // 4
		buffer.putInt(itemName_ADDITEM.length()); // 4
	    buffer.put(itemName_ADDITEM.getBytes()); // itemName_ADDITEM.length() 
	    buffer.putInt(spriteString_ADDITEM.length()); // 4
	    buffer.put(spriteString_ADDITEM.getBytes()); // spriteString_ADDITEM.length() 
	    buffer.putInt(spriteShortName_ADDITEM.length()); // 4
	    buffer.put(spriteShortName_ADDITEM.getBytes()); // spriteShortName_ADDITEM.length() 
        
	    buffer.putInt(itemStatesLength);
	    for (int i = 0; i < itemStatesLength; i++) {
	    	buffer.putFloat(itemStates_ADDITEM[i]);
	    }
	    
        buffer.flip();
        return buffer;
    }
    
    /** 
     * 
     * @param id
     * @return 
     */
    public static ByteBuffer addItemToContainer(OBJECTCODE containerType, BigInteger id, ITEMCODE itemCode_ADDITEM, int count_ADDITEM, int capacity_ADDITEM, float x_ADDITEM, float y_ADDITEM, float mx_ADDITEM, float my_ADDITEM, String itemName_ADDITEM, String spriteString_ADDITEM, String spriteShortName_ADDITEM, float[] itemStates_ADDITEM, int containerXPos, int containerYPos ) {
        int itemStatesLength = 0;
    	if (itemStates_ADDITEM != null) {
        	itemStatesLength = itemStates_ADDITEM.length;
        }
    	byte[] bytes = new byte[1 + 1 + 8 + 1 + 4 + 4 + 4 + 4 + 4 + 4 + 4 + itemName_ADDITEM.length() + 4 + spriteString_ADDITEM.length() + 4 + spriteShortName_ADDITEM.length() + 4 + 4*itemStatesLength +4+4];
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        buffer.put((byte) OPCODE.ADDITEMTOCONTAINER.ordinal());
        buffer.put((byte) containerType.ordinal());
        buffer.putLong(id.longValue()); // 8 Bytes
        
        buffer.put((byte) itemCode_ADDITEM.ordinal()); // 1
		buffer.putInt(count_ADDITEM); // 4
		buffer.putInt(capacity_ADDITEM); // 4
		buffer.putFloat(x_ADDITEM); // 4
		buffer.putFloat(y_ADDITEM); // 4
		buffer.putFloat(mx_ADDITEM); // 4
		buffer.putFloat(my_ADDITEM); // 4
		buffer.putInt(itemName_ADDITEM.length()); // 4
	    buffer.put(itemName_ADDITEM.getBytes()); // itemName_ADDITEM.length() 
	    buffer.putInt(spriteString_ADDITEM.length()); // 4
	    buffer.put(spriteString_ADDITEM.getBytes()); // spriteString_ADDITEM.length() 
	    buffer.putInt(spriteShortName_ADDITEM.length()); // 4
	    buffer.put(spriteShortName_ADDITEM.getBytes()); // spriteShortName_ADDITEM.length() 
        
	    buffer.putInt(itemStatesLength);
	    for (int i = 0; i < itemStatesLength; i++) {
	    	buffer.putFloat(itemStates_ADDITEM[i]);
	    }
	    
	    buffer.putInt(containerXPos);
	    buffer.putInt(containerYPos);
	    
        buffer.flip();
        return buffer;
    }
    
    /** 
     * 
     * @param id
     * @return 
     */
    public static ByteBuffer clearContainerPosition(OBJECTCODE containerType, int containerXPos, int containerYPos ) {
        
    	byte[] bytes = new byte[1 + 1 + 4 + 4 ];
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        buffer.put((byte) OPCODE.CLEARCONTAINERPOSITION.ordinal());
        buffer.put((byte) containerType.ordinal());
	    buffer.putInt(containerXPos);
	    buffer.putInt(containerYPos);
	    
        buffer.flip();
        return buffer;
    }
    
    /** 
     * 
     * @param id
     * @return 
     */
    public static ByteBuffer addCompleteItem(ITEMCODE itemCode, BigInteger id, String name, float x, float y, int count, float[] states) {
        int stateLength = 0;
        if (states != null) stateLength = states.length;
    	byte[] bytes = new byte[1 + 1 + 8 + 4 + name.length() + 4 + 4 + 4 +4 +stateLength*4];
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        buffer.put((byte) OPCODE.ADDCOMPLETEITEM.ordinal()); // 1
        
        buffer.put((byte) itemCode.ordinal()); // 1
        buffer.putLong(id.longValue()); // 8 Bytes
        buffer.putInt(name.length()); // 4
    	buffer.put(name.getBytes()); // playerName.length() 
    	buffer.putFloat(x); // 4
    	buffer.putFloat(y); // 4
        buffer.putInt(count); // 4
        buffer.putInt(stateLength);
        for (int i = 0; i < stateLength; i++) {
        	buffer.putFloat(states[i]);
        }
        
        buffer.flip();
        return buffer;
    }
    
    /** 
     * 
     * @param 
     * @return 
     */
    public static ByteBuffer addAreaInfluence(AreaInfluence e) {
    	byte[] bytes;
    	ByteBuffer buffer = null;
        int attributeLength = 0;
        int areaInfluenceGroupNamesLength = 0;
        int areaInfluenceExclusive = 4;
        int areaInfluencePriorityLength = 1;
        int IDLength = 8;
        int timeBeginTimeEndLength = 16;
        int xyLength = 4+4;
        int widthHeightLength = (4+4);
        int mxmyLength = (4+4);
        int countLength = (4); 
    	attributeLength = 4 + (e.getAttributes().length*4);
    	areaInfluenceGroupNamesLength += 4+e.getGroupName().length();

    	bytes = new byte[1 + areaInfluencePriorityLength + areaInfluenceGroupNamesLength + areaInfluenceExclusive + IDLength + timeBeginTimeEndLength + xyLength + widthHeightLength + mxmyLength+ countLength +attributeLength];
        buffer = ByteBuffer.wrap(bytes);
        buffer.put((byte) OPCODE.ADDAREAINFLUENCE.ordinal()); // 1
        
        /** now put the items in the buffer */
    	LocalObject localObject = e.getLocalObject();
        BigInteger id = e.getId();
        	
        buffer.put((byte) e.getPriority().ordinal()); // 1
        buffer.putLong(id.longValue()); // 8 Bytes 
        buffer.putLong(e.getTimeBegin()); // 8
        buffer.putLong(e.getTimeEnd()); // 8
        buffer.putInt(e.getGroupName().length()); // 4
        buffer.put(e.getGroupName().getBytes()); // playerName.length() 
        	
        int exclusive;
        if (e.isExclusive()) exclusive = 1;
    	else exclusive = 0;
        buffer.putInt(exclusive); // 4 
     
        buffer.putFloat(localObject.getX()); // 4
        buffer.putFloat(localObject.getY()); // 4
        buffer.putFloat(localObject.getWidth()); // 4
        buffer.putFloat(localObject.getHeight()); // 4
        buffer.putFloat(localObject.getMX()); // 4
        buffer.putFloat(localObject.getMY()); // 4
//            buffer.putInt(e.getCount()); // 4
		if (e.getAttributes() != null) {
			buffer.putInt(e.getAttributes().length);
			for (int j = 0; j < e.getAttributes().length; j++) {
            	buffer.putFloat(e.getAttributes()[j]);
            }
		}
		else {
			buffer.putInt(0);
		}  
        
        buffer.flip();	
        return buffer;
    }
    
    /** 
     * 
     * @param id, state
     * @return ByteBuffer
     */
    public static ByteBuffer editObjectState(OBJECTCODE objCode, BigInteger id,  float[] state) {
        byte[] bytes = new byte[1 + 4 + 8 + 16];
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        buffer.put((byte) OPCODE.OBJECTSTATE.ordinal());
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
//        // height
//        buffer.putFloat(state[4]);
//        // width
//        buffer.putFloat(state[5]);
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
    
    public static ByteBuffer initItems() {
        byte[] bytes = new byte[1];
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        buffer.put((byte) OPCODE.INITITEMS.ordinal());
//        buffer.putInt(objCode.ordinal());
        buffer.flip();
        return buffer;
    }
    
    public static ByteBuffer initAreaInfluences() {
        byte[] bytes = new byte[1];
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        buffer.put((byte) OPCODE.INITAREAINFLUENCES.ordinal());
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
    
    public static ByteBuffer initPlayers() {
        byte[] bytes = new byte[1];
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        buffer.put((byte) OPCODE.INITPLAYERS.ordinal());
//        buffer.putInt(objCode.ordinal());
        buffer.flip();
        return buffer;
    }
    
    public static ByteBuffer initMe() {
        byte[] bytes = new byte[1];
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        buffer.put((byte) OPCODE.INITME.ordinal());
//        buffer.putInt(objCode.ordinal());
        buffer.flip();
        return buffer;
    }
    
    public static ByteBuffer logMeOut() {
        byte[] bytes = new byte[1];
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        buffer.put((byte) OPCODE.LOGOUT.ordinal());
//        buffer.putInt(objCode.ordinal());
        buffer.flip();
        return buffer;
    }
    
    public static ByteBuffer joinSpaceChannel(BigInteger spaceId) {
    	byte[] bytes = new byte[1 + 8];
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        buffer.put((byte) OPCODE.JOINSPACECHANNEL.ordinal());
        buffer.putLong(spaceId.longValue()); // 8 Bytes
        buffer.flip();
        return buffer;
    }
    
    public static ByteBuffer leaveSpaceChannel(BigInteger spaceId) {
    	byte[] bytes = new byte[1 + 8];
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        buffer.put((byte) OPCODE.LEAVESPACECHANNEL.ordinal());
        buffer.putLong(spaceId.longValue()); // 8 Bytes
        buffer.flip();
        return buffer;
    }
    
    /** 
     * 
     * @param id
     * @return 
     */
    public static ByteBuffer getPlayerData(BigInteger id) {
        byte[] bytes = new byte[1 + 8];
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        buffer.put((byte) OPCODE.PLAYERDATA.ordinal());
        buffer.putLong(id.longValue()); // 8 Bytes
        buffer.flip();
        return buffer;
    }
    
    /** 
     * 
     * @param id
     * @return 
     */
    public static ByteBuffer getNextItem(BigInteger playerID, OBJECTCODE containerType, int oldFieldX, int oldFieldY) {
        byte[] bytes = new byte[1 + 8 +1 + 4 + 4];
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        buffer.put((byte) OPCODE.GET_NEXT_ITEM.ordinal());
        buffer.putLong(playerID.longValue());
        buffer.put((byte) containerType.ordinal());
        buffer.putInt(oldFieldX);
        buffer.putInt(oldFieldY);
        buffer.flip();
        return buffer;
    }
    
    /** edit player-addons */
    public static ByteBuffer editPlayerAddons(BigInteger id, String playerName, String tileName, String tilePathName, int tileWidth, int tileHeight, String country, String groupName, int experience) {
        byte[] bytes = new byte[1 + 8 + 4 + playerName.length() + 4 + tileName.length() +4 + tilePathName.length() + 4 + 4 + 4 + country.length() + 4 + groupName.length() + 4];
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        buffer.put((byte) OPCODE.EDIT_PLAYER_ADDONS.ordinal());
        /** ID */
        buffer.putLong(id.longValue()); // 8 Bytes
        
        buffer.putInt(playerName.length()); // 4
    	buffer.put(playerName.getBytes()); // playerName.length() 
    	buffer.putInt(tileName.length()); // 4 
    	buffer.put(tileName.getBytes()); // tileName.length() 
    	buffer.putInt(tilePathName.length()); // 4 
    	buffer.put(tilePathName.getBytes()); // tileName.length() 
    	buffer.putInt(tileWidth); // 4 
    	buffer.putInt(tileHeight); // 4 
    	buffer.putInt(country.length()); // 4 
    	buffer.put(country.getBytes()); // country.length() 
    	buffer.putInt(groupName.length()); // 4 
    	buffer.put(groupName.getBytes()); // groupName.length() 
    	buffer.putInt(experience); // 4
        
        buffer.flip();
        return buffer;
    }
    
    /** edit player-addons */
    public static ByteBuffer editPlayerStates(BigInteger id, EntityStates orientation, EntityStates singleState, EntityStates continuousState) {
        byte[] bytes = new byte[1 + 8 + 1 + 1 + 1];
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        buffer.put((byte) OPCODE.EDIT_PLAYER_STATES.ordinal());
        /** ID */
        buffer.putLong(id.longValue()); // 8 Bytes
        /** put the states here */
        buffer.put((byte) orientation.ordinal());
        buffer.put((byte) singleState.ordinal());
        buffer.put((byte) continuousState.ordinal());
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
    
    /** 
     * prepare map byteBuffer
     * @param name, packageId, image: we need image-bytes!
     * @return ByteBuffer
     */
    public static ByteBuffer sendMap(String playerName, int sizeOfMap, byte[] map, String fileNameMap, int sendListSize) {
    	/** define byte array */
    	byte[] bytes = new byte[1 + 4 + playerName.length() + 4 + map.length + 4 + fileNameMap.length() + 4];
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        buffer.put((byte) OPCODE.SEND_MAP.ordinal()); // 1 Byte
        /** playerName */
        buffer.putInt(playerName.length()); // 4
    	buffer.put(playerName.getBytes()); // playerName.length
    	/** store the size of the map */
    	buffer.putInt(sizeOfMap); // 4
    	/** image data */
    	buffer.put(map); // image.length
    	/** fileName */
    	buffer.putInt(fileNameMap.length()); // 4
    	buffer.put(fileNameMap.getBytes()); // fileNameMap.length()
    	/** store the remaining size of the sendList */
    	buffer.putInt(sendListSize); // 4
    	
    	/** buffer is ready */
        buffer.flip();
        return buffer;
    }
    
    /** 
     * 
     * @param id
     * @return 
     */
    public static ByteBuffer deleteMapObject(InteractionTile iTile, String gameMasterName, BigInteger gameMasterID, String objectMapName, String objectOverlayMapName) {
    	int GMlength = 4+gameMasterName.length();
        int tileValuesLength = 4 + iTile.getValues().length*4;
        int tileValuesPositionName = iTile.getPosition().getRoom().length() + 4;
        int objectMapNameLength = 4 + objectMapName.length();
        int objectOverlayMapNameLength = 4 + objectOverlayMapName.length();
    	byte[] bytes = new byte[1 + 4 + 4 + 4 + 4 + 8 + tileValuesPositionName + tileValuesLength + +objectMapNameLength + objectOverlayMapNameLength + GMlength ];
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        buffer.put((byte) OPCODE.DELETE_MAPOBJECT.ordinal());
	    buffer.putInt(iTile.getPosition().getLocalX()); // 4
	    buffer.putInt(iTile.getPosition().getLocalY()); // 4
	    buffer.putInt(iTile.getPosition().getMapCoordinates().x); // 4
	    buffer.putInt(iTile.getPosition().getMapCoordinates().y); // 4
	    buffer.putLong(gameMasterID.longValue()); // 8
	    buffer.putInt(iTile.getPosition().getRoom().length()); // 4
    	buffer.put(iTile.getPosition().getRoom().getBytes()); // iTile.getPosition().getRoom().length()
    	buffer.putInt(gameMasterName.length()); // 4
    	buffer.put(gameMasterName.getBytes());
    	buffer.putInt(objectMapName.length()); // 4
    	buffer.put(objectMapName.getBytes()); 
    	buffer.putInt(objectOverlayMapName.length()); // 4
    	buffer.put(objectOverlayMapName.getBytes()); 
    	
    	/** values */
    	buffer.putInt(iTile.getValues().length);
    	for ( int i =0; i < iTile.getValues().length; i++) {
    		buffer.putInt(iTile.getValues()[i]);
    	}
    	
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
