package de.svenheins.handlers;

import java.awt.Polygon;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.ArrayList;

import de.svenheins.main.GamePanel;
import de.svenheins.main.GameStates;
import de.svenheins.main.GameWindow;
import de.svenheins.managers.EntityManager;
import de.svenheins.managers.PlayerManager;
import de.svenheins.managers.RessourcenManager;
import de.svenheins.managers.SpaceManager;
import de.svenheins.managers.TextureManager;
import de.svenheins.messages.ClientMessages;
import de.svenheins.messages.OBJECTCODE;
import de.svenheins.messages.OPCODE;
import de.svenheins.messages.ServerMessages;
import de.svenheins.objects.Entity;
import de.svenheins.objects.Space;

public class ClientMessageHandler {
	/* {@inheritDoc} */
    public static void parseClientPacket(ByteBuffer packet) {
        OPCODE code = getOpCode(packet);
        parseClientPacket(code, packet);
    }
    
    /* {@inheritDoc} */
    public static void parseClientPacket(OPCODE opCode, ByteBuffer packet) {
    	switch(opCode) {
    	case OBJECTSTATE:
    		if (GamePanel.gp.isServerInitialized()) {
	    		OBJECTCODE objCode = OBJECTCODE.values()[packet.getInt()];
	    		BigInteger objectId = BigInteger.valueOf(packet.getLong());;
	    		float objectX = packet.getFloat();
	    		float objectY = packet.getFloat();
	    		float objectMX = packet.getFloat();
	    		float objectMY = packet.getFloat();
	    		float objectWidth = packet.getFloat();
	    		float objectHeight = packet.getFloat();
	    		if (objCode == OBJECTCODE.SPACE) SpaceManager.updateSpace(objectId, objectX, objectY, objectMX, objectMY);
	    		if (objCode == OBJECTCODE.ENTITY) EntityManager.updateEntity(objectId, objectX, objectY, objectMX, objectMY);
	    		if (objCode == OBJECTCODE.PLAYER) {
	    			if (PlayerManager.idList.contains(objectId)) {
	    				PlayerManager.updatePlayer(objectId, objectX, objectY, objectMX, objectMY);	
	    			} else {
	    				/** new Player logged in (first update of this player)*/
	    				GameWindow.gw.gameInfoConsole.appendInfo("Login of new Player: ID="+objectId);
	    				PlayerManager.updatePlayer(objectId, objectX, objectY, objectMX, objectMY);
	    			}
	    		}
//	    		if(objectId.intValue() == 0 && objectX != 0) {
//					GameWindow.gw.gameInfoConsole.appendInfo("Entity: x="+objectX+" y="+objectY);
//				}
    		}
    		break;
    	case INITSPACES:
    		/** no more need for init requests*/
    		GamePanel.gp.setServerInitialized(true);
    		/** Init Spaces */
			BigInteger id;
			String name;
			int r, g, b;
    		int i_filled;
    		boolean filled;
    		float trans;
    		float scale;
    		float area;
    		int polyX;
        	int polyY;
        	int numberOfPolygons;
        	int numberOfActualPolygon;
        	int[] xpoints;
			int[] ypoints;
        	ArrayList<Polygon> polygon;
			ArrayList<Space> spaceList = new ArrayList<Space>();
			/** for each available packet do */
    		while (packet.hasRemaining()) {
//    			byte[] bigByte = new byte[packet.getInt()];
//				for (int i =0; i<bigByte.length; i++) {
//					bigByte[i] = packet.get();
//				}
//	    		BigInteger objectId = new BigInteger(bigByte);
    			id = BigInteger.valueOf(packet.getLong()); // ID
    			byte[] nameBytes = new byte[packet.getInt()];
    			packet.get(nameBytes);
    			name = new String(nameBytes); // name
    			r = packet.getInt();
    			g = packet.getInt();
    			b = packet.getInt();
    			i_filled = packet.getInt();
    			if (i_filled == 0) filled = false;
    			else filled = true;
    			trans = packet.getFloat();
    			scale = packet.getFloat();
    			area = packet.getFloat();
    			
    			polyX = packet.getInt();
	            polyY = packet.getInt();
	    		
	            numberOfPolygons = packet.getInt();
	            polygon = new ArrayList<Polygon>();
	    		for (int i = 0; i < numberOfPolygons; i++) {
	    			numberOfActualPolygon = packet.getInt();
	    			System.out.println("number of edges: "+ numberOfActualPolygon);
	    			xpoints = new int[numberOfActualPolygon];
	    			ypoints = new int[numberOfActualPolygon];
	    			for (int j = 0; j < numberOfActualPolygon; j++) {
	    				xpoints[j] = packet.getInt();
	    				ypoints[j] = packet.getInt();
	    			}
	    			Polygon addPolygon = new Polygon(xpoints, ypoints, numberOfActualPolygon);
	    			polygon.add(addPolygon);
	    		}
    			// if the name not yet known, we have to download the file from the server
    			/** !!!!!!!!!!!!!!!!!!
    			 * OK, new strategy: load every space-coordinates, its less performance but BUG-safer
    			 *  !!!!!!!!!!!!!!!!!!!!!!!
    			 */
    			// TODO: Add here the polygons like from client to server
//    			if (!RessourcenManager.containsSVG(name)) {
//    				//TODO: add to "get full space list"
//    			}
    			
    			/** now everything is well prepared */
	    		Space spaceAdd = new Space(polygon, polyX, polyY, "polygon", id, new int[]{r,g,b}, filled, trans, scale);
	    		spaceAdd.setName(name);
	    		spaceAdd.setArea(area);
	    		//SpaceManager.add(spaceAdd);
	    		spaceList.add(spaceAdd);
    		}
    		/** transform list into array */
    		Space[] spaces = new Space[spaceList.size()];
    		for (int i = 0; i<spaceList.size(); i++){
    			spaces[i] = spaceList.get(i);
    			System.out.println("ID="+spaces[i].getId());
    		}
    		GameWindow.gw.gameInfoConsole.appendInfo("Loaded "+spaceList.size()+ " Spaces");
    		GamePanel.gp.loadSpaceList(spaces);
    		GameWindow.gw.gameInfoConsole.appendInfo("There are "+SpaceManager.size()+ " Spaces");
    		break;
    	case INITENTITIES:
    		/** no more need for init requests*/
    		GamePanel.gp.setServerInitialized(true);
    		/** Init Entities */
			BigInteger id_entity;
			String name_entity;
			ArrayList<Entity> entityList = new ArrayList<Entity>();
			/** for each available packet do */
    		while (packet.hasRemaining()) {
//    			byte[] bigByte = new byte[packet.getInt()];
//				for (int i =0; i<bigByte.length; i++) {
//					bigByte[i] = packet.get();
//				}
//	    		BigInteger objectId = new BigInteger(bigByte);
    			id_entity = BigInteger.valueOf(packet.getLong()); // ID
    			byte[] nameBytes = new byte[packet.getInt()];
    			packet.get(nameBytes);
    			name_entity = new String(nameBytes); // name
    			entityList.add(new Entity(name_entity, id_entity, 0,0, 0, 0));
    		}
    		/** transform list into array */
    		Entity[] entities = new Entity[entityList.size()];
    		for (int i = 0; i<entityList.size(); i++){
    			entities[i] = entityList.get(i);
    		}
    		GameWindow.gw.gameInfoConsole.appendInfo("Loaded "+entities.length+ " entities from array");
    		GamePanel.gp.loadEntityList(entities);	
			GameWindow.gw.gameInfoConsole.appendInfo("Loaded "+entityList.size()+ " entityList");
			GameWindow.gw.gameInfoConsole.appendInfo("There are "+EntityManager.size()+ " Entities");
//			System.out.println("got entities: "+entities.length);
//			if (entities.length>253)
//			GamePanel.gp.setServerInitialized(true);
			
    		break;
    	case SENDTEXTURE:
    		byte[] nameBytesTexture = new byte[packet.getInt()];
			packet.get(nameBytesTexture);
			String name_texture = new String(nameBytesTexture); // name
			
//			for (int i = 0; i< TextureManager.manager.getSize(); i++) {
//				System.out.println("texture: "+ i + " is "+ TextureManager.manager.getTextureNames()[i]);
//			}
			
			/** if not yet contained inside the manager and there is no actual texture to download */
			if (!TextureManager.manager.contains(name_texture)) {
				int packetIndex = packet.getInt();
				int countPackets = packet.getInt();
//				System.out.println("Got a new Texture: "+name_texture);
				String playerName = name_texture.substring(0, name_texture.indexOf("_"));
				/** init for the first packet */
				if (packetIndex == 0 && TextureManager.manager.getDownloadTextureName().equals("")) {
					TextureManager.manager.initDownload(name_texture,countPackets, playerName);
				}
				if (packetIndex == TextureManager.manager.getActualDownloadIndex() && name_texture.equals(TextureManager.manager.getDownloadTextureName())) {
					System.out.println("got the right part of the texture!");
					/** get the part of the texture */
					int lengthOfPacket = packet.getInt();
					byte[] image = new byte[lengthOfPacket];
					packet.get(image);
					
					TextureManager.manager.getPartOfDownload(name_texture, packetIndex, image, playerName);
					/** send OK, if we still nedd packets */
					if (packetIndex < countPackets-1) {
		    			/** send the "received!!"-message if there are textures remaining */
		    			GameWindow.gw.send(ClientMessages.sendReadyForNextTexturePacket(GameWindow.gw.getPlayer(), packetIndex));
		    		} else {
		    			/** send "this one is complete, send next! */
		    			System.out.println("next texture, please!");
		    			GameWindow.gw.gameInfoConsole.appendInfo("Got the new texture "+name_texture+" by player "+playerName);
		    			GameWindow.gw.send(ClientMessages.sendNextTexture(name_texture));
		    		}
				} else {
					System.out.println("got the WRONG part of the texture!");
				}
			} else {
				System.out.println("texture is already there!");
				/** send "this one is complete, send next! */
    			System.out.println("next texture, please!");
    			String playerName = name_texture.substring(0, name_texture.indexOf("_"));
    			GameWindow.gw.gameInfoConsole.appendInfo("I have already the texture: "+name_texture+" by player "+playerName+" - next please!");
    			GameWindow.gw.send(ClientMessages.sendNextTexture(name_texture));
			}
    		
    		break;
    	case OBJECTDELETE:
    		if (GamePanel.gp.isServerInitialized()) {
	    		OBJECTCODE objCode = OBJECTCODE.values()[packet.getInt()];
	    		BigInteger objectId = BigInteger.valueOf(packet.getLong());;
	    		
	    		if (objCode == OBJECTCODE.SPACE) SpaceManager.remove(objectId);
	    		if (objCode == OBJECTCODE.ENTITY) EntityManager.remove(objectId);
	    		if (objCode == OBJECTCODE.PLAYER) PlayerManager.remove(objectId);
	    		GameWindow.gw.gameInfoConsole.appendInfo("Deleted: "+objectId);
//	    		if(objectId.intValue() == 0 && objectX != 0) {
//					GameWindow.gw.gameInfoConsole.appendInfo("Entity: x="+objectX+" y="+objectY);
//				}
    		}
    		break;
    	case READY_FOR_NEXT_TEXTURE_PACKET:
    		byte[] nameBytes = new byte[packet.getInt()];
			packet.get(nameBytes);
			String namePlayer = new String(nameBytes); // name
    		int oldPacket = packet.getInt();
    		System.out.println("OK, packet "+oldPacket +" is ready, sending next one!");
    		
    		/** get next Packet and send it to the server */			
    		byte[] imagePacket = TextureManager.manager.getTexturePacket(oldPacket+1);
    		String textureName = TextureManager.manager.getUploadTextureName();
    		/** send the next packet */
    		GameWindow.gw.send(ClientMessages.uploadTexture(textureName, oldPacket+1, TextureManager.manager.getNumberOfPacketsUploadTexture() , imagePacket.length, imagePacket, GameWindow.gw.getPlayer()));
    		
    		break;
    	case MOVEMOB:
    		break;
    	case ADDMOB:
//    		int addId = packet.getInt();
//            float addX = packet.getFloat();
//            float addY = packet.getFloat();
//            EMOBType addType = EMOBType.values()[packet.getInt()];
//            ETeamColor addColor = ETeamColor.values()[packet.getInt()];
//            byte[] mobNameBytes = new byte[packet.getInt()];
//            packet.get(mobNameBytes);
//            String mobName = new String(mobNameBytes);
//            logger.log(Level.FINEST, "Processing {0} packet : {1}, {2}, {3}, {4}, {5}, {6}", 
//                       new Object[]{code, addId, addX, addY, addType, addColor, mobNameBytes});
//            unit.addMOB(addId,
//                        addX,
//                        addY,
//                        addType,
//                        addColor,
//                        mobName);
            break;
    	default:
			;
    	
    	}
    
    }
    
    private static OPCODE getOpCode(ByteBuffer packet) 
    {
        byte opbyte = packet.get();
        if ((opbyte < 0) || (opbyte > OPCODE.values().length - 1)) {
        	//TODO: exception is better
        	System.out.println("Unknown op value: " + opbyte);
//            logger.severe("Unknown op value: " + opbyte);
            return null;
        }
        OPCODE code = OPCODE.values()[opbyte];
        
        return code;
    }
}
