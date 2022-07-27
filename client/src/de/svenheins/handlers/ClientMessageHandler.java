package de.svenheins.handlers;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.ArrayList;

import de.svenheins.main.GamePanel;
import de.svenheins.main.GameWindow;
import de.svenheins.managers.EntityManager;
import de.svenheins.managers.SpaceManager;
import de.svenheins.messages.OBJECTCODE;
import de.svenheins.messages.OPCODE;
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
	    		else if (objCode == OBJECTCODE.ENTITY) EntityManager.updateEntity(objectId, objectX, objectY, objectMX, objectMY);
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
    			spaceList.add(new Space(name, id, new int[]{r, g, b}, filled, trans, scale));
    		}
    		/** transform list into array */
    		Space[] spaces = new Space[spaceList.size()];
    		for (int i = 0; i<spaceList.size(); i++){
    			spaces[i] = spaceList.get(i);
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
    			entityList.add(new Entity(name_entity, id_entity, 0,0));
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
