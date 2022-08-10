package de.svenheins.messages;

import java.awt.Polygon;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.ArrayList;

import de.svenheins.managers.ServerTextureManager;
//import de.svenheins.managers.TextureManager;
import de.svenheins.objects.Entity;
import de.svenheins.objects.InteractionTile;
import de.svenheins.objects.PlayerEntity;
import de.svenheins.objects.Space;
import de.svenheins.objects.items.Container;
import de.svenheins.objects.items.Item;

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
        byte[] bytes = new byte[1 + 4 + 8 + 16];
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
//        // height
//        buffer.putFloat(state[4]);
//        // width
//        buffer.putFloat(state[5]);
        buffer.flip();
        return buffer;
    }
    
    
    /** get object state */
    public static ByteBuffer sendTakeItem (BigInteger id) {
        byte[] bytes = new byte[1 + 8];
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        /** let the player take the item */
//        System.out.println("send take");
        buffer.put((byte) OPCODE.TAKEITEM.ordinal());    
        buffer.putLong(id.longValue()); // 8 Bytes
        buffer.flip();
        return buffer;
    }
    
    public static ByteBuffer sendDeleteMapObject(InteractionTile iTile, String objectMapName, String objectOverlayMapName) {
        int tileValuesLength = 4 + iTile.getValues().length*4;
        int tileValuesPositionName = iTile.getPosition().getRoom().length() + 4;
        int objectMapNameLength = objectMapName.length() + 4;
        int objectOverlayMapNameLength = objectOverlayMapName.length() + 4;
    	byte[] bytes = new byte[1 + 4 + 4 + 4 + 4  + tileValuesPositionName + objectMapNameLength+ objectOverlayMapNameLength+ tileValuesLength  ];
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        buffer.put((byte) OPCODE.DELETE_MAPOBJECT.ordinal());
	    buffer.putInt(iTile.getPosition().getLocalX()); // 4
	    buffer.putInt(iTile.getPosition().getLocalY()); // 4
	    buffer.putInt(iTile.getPosition().getMapCoordinates().x); // 4
	    buffer.putInt(iTile.getPosition().getMapCoordinates().y); // 4
	    
	    buffer.putInt(iTile.getPosition().getRoom().length()); // 4
    	buffer.put(iTile.getPosition().getRoom().getBytes()); // iTile.getPosition().getRoom().length()
    	buffer.putInt(objectMapName.length()); // 4
    	buffer.put(objectMapName.getBytes()); // objectMapName.length()
    	buffer.putInt(objectOverlayMapName.length()); // 4
    	buffer.put(objectOverlayMapName.getBytes()); // objectOverlayMapName.length()
    	
    	/** values */
    	buffer.putInt(iTile.getValues().length); // 4
    	for ( int i =0; i < iTile.getValues().length; i++) {
    		buffer.putInt(iTile.getValues()[i]); // 4
    	}
    	
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
    
    
    /** send all entityObjects */
    public static ByteBuffer sendItems(Item[] localObjects) {
    	byte[] bytes;
    	ByteBuffer buffer = null;
        int attributeLength = 0;
        int itemNamesLength = 0;
        int itemCodeLength = localObjects.length*1;
        int IDLength = localObjects.length*8;
        int xyLength = localObjects.length*(4+4);
        int countLength = localObjects.length*(4); 
        for (int i = 0; i<localObjects.length; i++) {
    		Item e = localObjects[i];
    		if (e.getAttributes() != null) attributeLength += 4 + (e.getAttributes().length*4); 
    		else {
    			attributeLength += 4; 
    		}
    		itemNamesLength += 4+e.getName().length();
    	}
    	bytes = new byte[1 + 4 + itemCodeLength + IDLength + itemNamesLength + xyLength + countLength +attributeLength];
        buffer = ByteBuffer.wrap(bytes);
        buffer.put((byte) OPCODE.INITITEMS.ordinal()); // 1
        buffer.putInt(localObjects.length); // 4
        
        /** now put the items in the buffer */
        for (int i = 0; i<localObjects.length; i++) {
    		Item e = localObjects[i];
        	BigInteger id = e.getId();
        	
        	buffer.put((byte) e.getItemCode().ordinal()); // 1
            buffer.putLong(id.longValue()); // 8 Bytes
            buffer.putInt(e.getName().length()); // 4
        	buffer.put(e.getName().getBytes()); // playerName.length() 
        	buffer.putFloat(e.getX()); // 4
        	buffer.putFloat(e.getY()); // 4
            buffer.putInt(e.getCount()); // 4
    		if (e.getAttributes() != null) {
    			buffer.putInt(e.getAttributes().length);
    			for (int j = 0; j < e.getAttributes().length; j++) {
                	buffer.putFloat(e.getAttributes()[j]);
                }
    		}
    		else {
    			buffer.putInt(0);
    		}
    	}
//        
//        buffer.put((byte) itemCode.ordinal()); // 1
//        buffer.putLong(id.longValue()); // 8 Bytes
//        buffer.putInt(name.length()); // 4
//    	buffer.put(name.getBytes()); // playerName.length() 
//    	buffer.putFloat(x); // 4
//    	buffer.putFloat(y); // 4
//        buffer.putInt(count); // 4
//        buffer.putInt(attributeLength);
//        for (int i = 0; i < attributeLength; i++) {
//        	buffer.putFloat(attributes[i]);
//        }
        
        
        
        buffer.flip();	
        return buffer;
    }
    
    
    /** 
     * 
     * @param id
     * @return 
     */
    public static ByteBuffer addCompleteItem(ITEMCODE itemCode, BigInteger id, String name, float x, float y, int count, float[] attributes) {
        int attributeLength = 0;
        if (attributes != null) attributeLength = attributes.length;
    	byte[] bytes = new byte[1 + 1 + 8 + 4 + name.length() + 4 + 4 + 4 +4 +attributeLength*4];
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        buffer.put((byte) OPCODE.ADDCOMPLETEITEM.ordinal()); // 1
        
        buffer.put((byte) itemCode.ordinal()); // 1
        buffer.putLong(id.longValue()); // 8 Bytes
        buffer.putInt(name.length()); // 4
    	buffer.put(name.getBytes()); // playerName.length() 
    	buffer.putFloat(x); // 4
    	buffer.putFloat(y); // 4
        buffer.putInt(count); // 4
        buffer.putInt(attributeLength);
        for (int i = 0; i < attributeLength; i++) {
        	buffer.putFloat(attributes[i]);
        }
        buffer.flip();
        return buffer;
    }
    
    
    
    /** here we only send known spaces 
     * those which are not yet known by clients should be handled separately
     * */
    public static ByteBuffer sendSpaces(Space[] localObjects) {
    	byte[] bytes;
    	ByteBuffer buffer = null;
    	/** get the length of Bytes that must be reserved for the names */
		int namesLength = 0;
		int texturesNameLength = 0;
    	
		int polygonBytesAbsolut = 0;
		int numPolygons = 0;
		ArrayList<Polygon> polygon;
		for (int i = 0; i<localObjects.length; i++) {
			namesLength += localObjects[i].getName().length();
			texturesNameLength += localObjects[i].getTextureName().length();
			
			/** reserve 4 + 4 bytes for the polyX and polyY Coordinates */
			polygonBytesAbsolut += 8;
	    	/** polygons start here */
			polygon = localObjects[i].getPolygon();
			numPolygons = polygon.size();
			/** the size of each localObject polygon-ArrayList must be reserved */
			polygonBytesAbsolut += 4;
			/** now check the required bytes for the polygon ArrayList */
			for (int j = 0; j < numPolygons; j++) {
	    		Polygon actualPolygon = polygon.get(j);
	    		/** save the length and the x- and y-coordinates */
	    		polygonBytesAbsolut += (4 + 4*actualPolygon.xpoints.length*2);
	    	}
		}
		/** use Object-specific send-routine */
		/** init bytes */
		bytes = new byte[1 + 44*localObjects.length + namesLength + texturesNameLength + polygonBytesAbsolut];
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
    		float area = s.getArea();
    		
    		String textureName = s.getTextureName();
    		
    		int polyX = s.getPolyX();
        	int polyY = s.getPolyY();
        	/** reset polygon */
        	polygon = s.getPolygon();
        	numPolygons = polygon.size();
        	
    		buffer.putLong(id.longValue()); // 8
    		buffer.putInt(name.length()); // 4
        	buffer.put(name.getBytes()); // name.length
        	buffer.putInt(rgb_each[0]); // 4
        	buffer.putInt(rgb_each[1]); // 4
        	buffer.putInt(rgb_each[2]); // 4
        	buffer.putInt(filled); // 4
        	buffer.putFloat(trans); // 4
        	buffer.putFloat(scale); // 4
        	buffer.putFloat(area); // 4
        	
        	buffer.putInt(textureName.length()); // 4
        	buffer.put(textureName.getBytes()); // texureName.length
        	
        	// polyX
            buffer.putInt(polyX); // 4 bytes
            // polyY
            buffer.putInt(polyY); // 4 bytes
            /** start of Polygon ArrayList */
        	buffer.putInt(numPolygons); // 4 bytes
        	for (int j = 0; j < numPolygons; j++) {
        		Polygon actualPolygon = polygon.get(j);
        		int[] actualPolygonX = actualPolygon.xpoints;
        		int[] actualPolygonY = actualPolygon.ypoints;
        		/** save the length of the actual Polygon */
        		buffer.putInt(actualPolygonX.length);  // 4 bytes
        		/** put the actual Polygon into the buffer */
        		for (int k = 0; k < actualPolygonX.length; k++) {
        			buffer.putInt(actualPolygonX[k]); // 4 bytes
        			buffer.putInt(actualPolygonY[k]); // 4 bytes
        		}
        	}
    	} 	
    	buffer.flip();
    		
        return buffer;
    }
    
    /** send players to client */
    public static ByteBuffer sendPlayers(PlayerEntity[] localObjects) {
    	byte[] bytes;
    	ByteBuffer buffer = null;
    	/** get the length of Bytes that must be reserved for the names */
		int namesLength = 0;
		int tileNameLength = 0;
		int tileFileNameLength = 0;
    	
		for (int i = 0; i<localObjects.length; i++) {
			namesLength += localObjects[i].getName().length();
			tileNameLength += localObjects[i].getTileSet().getName().length();
			tileFileNameLength += localObjects[i].getTileSet().getFileName().length();
		}
		/** use Object-specific send-routine */
		/** init bytes */
		bytes = new byte[1 + 36*localObjects.length + namesLength + tileNameLength + tileFileNameLength];
    	buffer = ByteBuffer.wrap(bytes);
        buffer.put((byte) OPCODE.INITPLAYERS.ordinal()); 
    	
        PlayerEntity[] lPlayer = localObjects;
        /** place all objects of the specific class */
        BigInteger id;
        String name;
        String tileName;
        String tileFileName;
        float playerWidth;
        float playerHeight;
        long animationDelay;
        PlayerEntity p;
        for (int i = 0; i<localObjects.length; i++) {
    		p = lPlayer[i];
        	id = p.getId();
    		name = p.getName();
    		tileName = p.getTileSet().getName();
    		tileFileName = p.getTileSet().getFileName();
    		playerWidth = p.getWidth();
    		playerHeight = p.getHeight();
    		animationDelay = p.getAnimation().getTimeBetweenAnimation();
        	
    		buffer.putLong(id.longValue()); // 8
    		buffer.putInt(name.length()); // 4
        	buffer.put(name.getBytes()); // name.length
        	buffer.putInt(tileName.length()); // 4
        	buffer.put(tileName.getBytes()); // tileName.length
        	buffer.putInt(tileFileName.length()); // 4
        	buffer.put(tileFileName.getBytes()); // tileName.length
        	buffer.putFloat(playerWidth); // 4 
        	buffer.putFloat(playerHeight); // 4
        	buffer.putLong(animationDelay); // 4
    	} 	
    	buffer.flip();
    		
        return buffer;
    }
    
    public static ByteBuffer sendMe(BigInteger id, String tileName, String tilePathName, String groupName, long firstServerLogin, int experience, String country, float x, float y, float mx, float my, BigInteger maxItemID) {
		/** use Object-specific send-routine */
		/** init bytes */
    	byte[] bytes = new byte[1 + 8 + 4 + tileName.length() + 4 + tilePathName.length() + 4 + groupName.length() + 8 + 4 + 4 + country.length() + 4 + 4 + 4 + 4 + 8];
//    	bytes = new byte[1 + 8 + 4 + tileName.length() + 4 + tilePathName.length() + 4 + groupName.length() + 8 + 4 + 4 + country.length() + 4 + 4 + 4 + 4 + 8 ];
    	ByteBuffer buffer = ByteBuffer.wrap(bytes);
        buffer.put((byte) OPCODE.INITME.ordinal()); // 1
 
        buffer.putLong(id.longValue()); // + 8 
        buffer.putInt(tileName.length()); // 4
    	buffer.put(tileName.getBytes()); // tileName.length
    	buffer.putInt(tilePathName.length()); // 4
    	buffer.put(tilePathName.getBytes()); // tileName.length
    	buffer.putInt(groupName.length()); // + 4 
    	buffer.put(groupName.getBytes()); // + groupName.length() 
    	buffer.putLong(firstServerLogin); // + 8 
    	buffer.putInt(experience); // + 4 
    	buffer.putInt(country.length()); // + 4 
    	buffer.put(country.getBytes()); // + country.length() 
    	buffer.putFloat(x); // + 4 
    	buffer.putFloat(y); // + 4 
    	buffer.putFloat(mx); // + 4 
    	buffer.putFloat(my); // + 4
    	buffer.putLong(maxItemID.longValue()); // + 8         
        buffer.flip();
        return buffer;
    }
    
    public static ByteBuffer sendContainer(BigInteger id, Container inventory) {
    	byte[] bytes;
    	ByteBuffer buffer = null;
    	/** get the length of Bytes that must be reserved for the names */
		int inventoryHeight = inventory.getHeight();
		int inventoryWidth = inventory.getWidth();
//		BigInteger[][] containerArray = inventory.getContainerArray();
    	
		int itemEntityIDLength = 0;
		int itemEntityNameLength = 0; // string
		int itemEntityTileNameLength = 0; // string
		int itemEntityTileFileNameLength = 0; //string
		int itemNameLength = 0; // String
		int itemAttributesLength = 0; // float[]
		int containerArraySize = 8 * inventoryHeight * inventoryWidth;
		
		int itemCountLength = 0;
		int itemCapacityLength = 0;
		int itemCodeLength = 0;
		int itemCreationTimeCodeLength = 0;
		int itemXLength = 0;
		int itemYLength = 0;
		
		Item item;
		for (int i = 0; i < inventoryHeight; i++) {
			for (int j = 0; j < inventoryWidth; j++) {
				/** only add item stuff if there is an item in the field */
				if (inventory.getItemList().containsKey(inventory.getContainerArray()[i][j])) {
					item = inventory.getItemList().get(inventory.getContainerArray()[i][j]);
					/** get the item id and the corresponding item */
					itemCountLength += 4;
					itemCapacityLength += 4;
					itemNameLength += 4 + item.getName().length();
					itemCodeLength += 1;
					itemCreationTimeCodeLength += 8;
					itemXLength += 4;
					itemYLength += 4;
					// entity
					itemEntityIDLength += 8;
					itemEntityNameLength += 4 + item.getEntity().getName().length();
					itemEntityTileNameLength += 4 + item.getEntity().getTileSet().getName().length();
					itemEntityTileFileNameLength += 4 + item.getEntity().getTileSet().getFileName().length();
					itemAttributesLength += 4;
					if (item.getAttributes() != null) itemAttributesLength += 4*item.getAttributes().length;
				} else {
					/** there is no item in this field */
				}
			}
		}
		
		
		
		/** use Object-specific send-routine */
		/** init bytes */
		bytes = new byte[1 + 8 + 1 +1+/**inventory*/ 4 + 4 + itemEntityIDLength+ itemEntityNameLength+ itemEntityTileNameLength+ itemEntityTileFileNameLength+ itemNameLength+ itemAttributesLength+ containerArraySize+ itemCountLength+ itemCapacityLength+ itemCodeLength+ itemCreationTimeCodeLength +itemXLength +itemYLength];
    	buffer = ByteBuffer.wrap(bytes);
        buffer.put((byte) OPCODE.SENDCONTAINER.ordinal()); // 1
        buffer.putLong(id.longValue()); // + 8 
        buffer.put((byte) inventory.getContainerType().ordinal()); // 1
        buffer.put((byte) inventory.getAllowedItems().ordinal()); // 1
        buffer.putInt(inventoryWidth);
        buffer.putInt(inventoryHeight);
        
        /** now put all container stuff into buffer */
    	for (int i = 0; i < inventoryHeight; i++) {
    		for(int j = 0 ; j< inventoryWidth; j++) {
    			buffer.putLong(inventory.getContainerArray()[i][j].longValue()); // 8 = ID for every array element
    			/** only add item stuff if there is an item in the field */
				if (inventory.getItemList().containsKey(inventory.getContainerArray()[i][j])) {
					item = inventory.getItemList().get(inventory.getContainerArray()[i][j]);
					/** get the item id and the corresponding item */
					buffer.putInt(item.getCount()); // 4
					buffer.putInt(item.getCapacity()); // 4
					buffer.putInt(item.getName().length()); // 4
			    	buffer.put(item.getName().getBytes()); // name.length
					buffer.put((byte) item.getItemCode().ordinal()); // 1
					buffer.putLong(item.getCreationTime());
					/** entity stuff */
					buffer.putLong(item.getEntity().getId().longValue()); // 8
					buffer.putInt(item.getEntity().getName().length()); // 4
			    	buffer.put(item.getEntity().getName().getBytes()); // name.length
			    	buffer.putInt(item.getEntity().getTileSet().getName().length()); // 4
			    	buffer.put(item.getEntity().getTileSet().getName().getBytes()); // name.length
			    	buffer.putInt(item.getEntity().getTileSet().getFileName().length()); // 4
			    	buffer.put(item.getEntity().getTileSet().getFileName().getBytes()); // name.length
			    	buffer.putFloat(item.getEntity().getX()); // 4
			    	buffer.putFloat(item.getEntity().getY()); // 4
//			    	System.out.println("Sending initial x: "+item.getEntity().getX()+" and y: "+item.getEntity().getY());
			    	/** item attributes */
			    	if (item.getAttributes() != null) {
						buffer.putInt(item.getAttributes().length);
						for (int k = 0; k< item.getAttributes().length; k++) {
							buffer.putFloat(item.getAttributes()[k]);
						}
			    	} else {
			    		// if there is no really itemAttributes
			    		buffer.putInt(0);
			    	}
				} else {
					/** there is no item in this field */
				}
    		}
    	}
        buffer.flip();
        return buffer;
    }
    
    public static ByteBuffer sendItemField(BigInteger playerID, OBJECTCODE containerType , Item item, int fieldX, int fieldY) {
    	byte[] bytes;
    	ByteBuffer buffer = null;
    	/** get the length of Bytes that must be reserved for the names */
    	
		int itemEntityIDLength = 8;
		int itemEntityNameLength = 4 + item.getEntity().getName().length(); // string
		int itemEntityTileNameLength = 4 + item.getEntity().getTileSet().getName().length(); // string
		int itemEntityTileFileNameLength = 4 + item.getEntity().getTileSet().getFileName().length(); //string
		int itemNameLength = 4 + item.getName().length(); // String
		int itemAttributesLength = 4; // float[]
		if (item.getAttributes() != null) itemAttributesLength += 4*item.getAttributes().length;
		
		int itemCountLength = 4;
		int itemCapacityLength = 4;
		int itemCodeLength = 1;
		int itemCreationTimeCodeLength = 8;
		int itemXLength = 4;
		int itemYLength = 4;
		
		/** use Object-specific send-routine */
		/** init bytes */
		bytes = new byte[1 +8+ 1 +/**inventory*/ 8 + itemEntityIDLength+ itemEntityNameLength+ itemEntityTileNameLength+ itemEntityTileFileNameLength+ itemNameLength+ itemAttributesLength+  itemCountLength+ itemCapacityLength+ itemCodeLength+ itemCreationTimeCodeLength +itemXLength +itemYLength+4+4];
    	buffer = ByteBuffer.wrap(bytes);
        buffer.put((byte) OPCODE.SENDITEMFIELD.ordinal()); // 1
        buffer.putLong(playerID.longValue());
        buffer.put((byte) containerType.ordinal()); // 1
        
		buffer.putLong(item.getId().longValue()); // 8 = ID for every array element
		/** get the item id and the corresponding item */
		buffer.putInt(item.getCount()); // 4
		buffer.putInt(item.getCapacity()); // 4
		buffer.putInt(item.getName().length()); // 4
    	buffer.put(item.getName().getBytes()); // name.length
		buffer.put((byte) item.getItemCode().ordinal()); // 1
		buffer.putLong(item.getCreationTime());
		/** entity stuff */
		buffer.putLong(item.getEntity().getId().longValue()); // 8
		buffer.putInt(item.getEntity().getName().length()); // 4
    	buffer.put(item.getEntity().getName().getBytes()); // name.length
    	buffer.putInt(item.getEntity().getTileSet().getName().length()); // 4
    	buffer.put(item.getEntity().getTileSet().getName().getBytes()); // name.length
    	buffer.putInt(item.getEntity().getTileSet().getFileName().length()); // 4
    	buffer.put(item.getEntity().getTileSet().getFileName().getBytes()); // name.length
    	buffer.putFloat(item.getEntity().getX()); // 4
    	buffer.putFloat(item.getEntity().getY()); // 4
    	/** item attributes */
    	if (item.getAttributes() != null) {
			buffer.putInt(item.getAttributes().length);
			for (int k = 0; k< item.getAttributes().length; k++) {
				buffer.putFloat(item.getAttributes()[k]);
			}
    	} else {
    		// if there is no really itemAttributes
    		buffer.putInt(0);
    	}
    	buffer.putInt(fieldX);
    	buffer.putInt(fieldY);
        buffer.flip();
        return buffer;
    }
    
    public static ByteBuffer sendItemEmptyField(BigInteger playerID, OBJECTCODE containerType , int fieldX, int fieldY) {
    	byte[] bytes;
    	ByteBuffer buffer = null;
		/** use Object-specific send-routine */
		/** init bytes */
		bytes = new byte[1 +8+ 1 +/**inventory*/ +4+4];
    	buffer = ByteBuffer.wrap(bytes);
        buffer.put((byte) OPCODE.SEND_EMPTY_ITEM_FIELD.ordinal()); // 1
        buffer.putLong(playerID.longValue());
        buffer.put((byte) containerType.ordinal()); // 1
    	buffer.putInt(fieldX);
    	buffer.putInt(fieldY);
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
    
    /** edit space-addons */
    public static ByteBuffer editSpaceAddons(BigInteger id, String textureName, int[] rgb, float trans, int filled, float scale, float area) {
        byte[] bytes = new byte[1+ 8 + 4 + textureName.length() + 4 + 4 + 4 + 4 + 4 + 4 + 4];
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        buffer.put((byte) OPCODE.EDIT_SPACE_ADDONS.ordinal());
        /** ID */
        buffer.putLong(id.longValue()); // 8 Bytes
        
        buffer.putInt(textureName.length()); // 4
    	buffer.put(textureName.getBytes()); // name.length
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
    
    /** here we only send known spaces 
     * those which are not yet known by clients should be handled separately
     * */
    public static ByteBuffer sendTextureStart(String playerName, String name) {
    	ByteBuffer buffer = null;
    	//String thisPlayerName = this.getName().substring(this.getName().indexOf(".")+1, this.getName().length());
//        ServerTextureManager.manager.prepareTextureForUpload(playerName, name);
    	byte[] byteTexture = ServerTextureManager.manager.getTexturePacket(playerName, 0);
    	int lengthOfFirstPacket = byteTexture.length;
    	int countPacketsOfWholeTexture = ServerTextureManager.manager.getLengthOfUploadTexture(playerName);
    	/** initialize bytes */
		byte[] bytes = new byte[1 + 4 + name.length() + 4 + 4 + 4+ lengthOfFirstPacket ];
    	buffer = ByteBuffer.wrap(bytes);
        buffer.put((byte) OPCODE.SENDTEXTURE.ordinal()); 
    	
        /** put name of texture */
        buffer.putInt(name.length()); // 4
    	buffer.put(name.getBytes()); // name.length
    	/** put part = 0 of packet */
    	buffer.putInt(0); // 4
    	/** put max parts of packets */
    	buffer.putInt(countPacketsOfWholeTexture); // 4
        /** put length of texture byte */
        buffer.putInt(lengthOfFirstPacket); // 4
        /** put texture */
        buffer.put(byteTexture); // image.length
        
    	buffer.flip();
    		
        return buffer;
    }
    
    /** here we only send known spaces 
     * those which are not yet known by clients should be handled separately
     * */
    public static ByteBuffer uploadTexture(String name, int packetId, int countPackets, int imageCountBytes, byte[] image, String playerName) {
    	ByteBuffer buffer = null;
    	int lengthOfPacket = image.length;
    	int countPacketsOfWholeTexture = countPackets;
    	/** initialize bytes */
		byte[] bytes = new byte[1 + 4 + name.length() + 4 + 4 + 4+ lengthOfPacket ];
    	buffer = ByteBuffer.wrap(bytes);
        buffer.put((byte) OPCODE.SENDTEXTURE.ordinal()); 
    	
        /** put name of texture */
        buffer.putInt(name.length()); // 4
    	buffer.put(name.getBytes()); // name.length
    	/** put part = 0 of packet */
    	buffer.putInt(packetId); // 4
    	/** put max parts of packets */
    	buffer.putInt(countPacketsOfWholeTexture); // 4
        /** put length of texture byte */
        buffer.putInt(lengthOfPacket); // 4
        /** put texture */
        buffer.put(image); // image.length
        
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
    
    /** get object state */
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


	public static ByteBuffer sendAvailableTextures(	ArrayList<String> externalTextures) {
		int bytesForTextureNames = 0;
		for (String s : externalTextures) {
			bytesForTextureNames += s.length() + 4;
		}
		byte[] bytes = new byte[1 + 4 + bytesForTextureNames];
		
		ByteBuffer buffer = ByteBuffer.wrap(bytes);
		buffer.put((byte) OPCODE.SEND_AVAILABLE_TEXTURES.ordinal()); // 1
		buffer.putInt(externalTextures.size()); // 4
		for (String s : externalTextures) {
			buffer.putInt(s.length()); // 4
			buffer.put(s.getBytes()); // s.length()
		}
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
