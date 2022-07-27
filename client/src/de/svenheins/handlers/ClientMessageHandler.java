package de.svenheins.handlers;

import java.nio.ByteBuffer;
import java.util.ArrayList;

import de.svenheins.main.GamePanel;
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
    		OBJECTCODE objCode = OBJECTCODE.values()[packet.getInt()];
    		int objectId = packet.getInt();
    		double objectX = packet.getDouble();
    		double objectY = packet.getDouble();
    		double objectMX = packet.getDouble();
    		double objectMY = packet.getDouble();
    		double objectWidth = packet.getDouble();
    		double objectHeight = packet.getDouble();
    		if (objCode == OBJECTCODE.SPACE) SpaceManager.updateSpace(objectId, objectX, objectY, objectMX, objectMY);
    		else if (objCode == OBJECTCODE.ENTITY) EntityManager.updateEntity(objectId, objectX, objectY, objectMX, objectMY);	
    		break;
    	case INITSPACES:
    		/** no more need for init requests*/
    		GamePanel.gp.setServerInitialized(true);
    		/** Init Spaces */
			int id;
			String name;
			int r, g, b;
    		int i_filled;
    		boolean filled;
    		float trans;
			ArrayList<Space> spaceList = new ArrayList<Space>();
			/** for each available packet do */
    		while (packet.hasRemaining()) {
    			id = packet.getInt(); // ID
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
    			spaceList.add(new Space(name, id, new int[]{r, g, b}, filled, trans));
    		}
    		/** transform list into array */
    		Space[] spaces = new Space[spaceList.size()];
    		for (int i = 0; i<spaceList.size(); i++){
    			spaces[i] = spaceList.get(i);
    		}

    		GamePanel.gp.loadSpaceList(spaces);
    		break;
    	case INITENTITIES:
    		/** no more need for init requests*/
    		GamePanel.gp.setServerInitialized(true);
    		/** Init Entities */
			int id_entity;
			String name_entity;
			ArrayList<Entity> entityList = new ArrayList<Entity>();
			/** for each available packet do */
    		while (packet.hasRemaining()) {
    			id_entity = packet.getInt(); // ID
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
			GamePanel.gp.loadEntityList(entities);		
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
