package de.svenheins.handlers;

import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.TexturePaint;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.ArrayList;

import javax.swing.JComboBox;

import de.svenheins.main.GameModus;
import de.svenheins.main.GamePanel;
import de.svenheins.main.GameStates;
import de.svenheins.main.GameWindow;
import de.svenheins.main.LoadingStates;
import de.svenheins.managers.ClientTextureManager;
import de.svenheins.managers.EntityManager;
import de.svenheins.managers.PlayerManager;
import de.svenheins.managers.RessourcenManager;
import de.svenheins.managers.SpaceManager;
import de.svenheins.managers.TileSetManager;
//import de.svenheins.managers.TextureManager;
import de.svenheins.messages.ClientMessages;
import de.svenheins.messages.OBJECTCODE;
import de.svenheins.messages.OPCODE;
import de.svenheins.messages.ServerMessages;
import de.svenheins.objects.Entity;
import de.svenheins.objects.PlayerEntity;
import de.svenheins.objects.Space;
import de.svenheins.objects.TileSet;

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
	    		if (objectId.compareTo(GamePanel.gp.getPlayerEntity().getId())!=0) {
//	    			System.out.println("got: "+objectId+" I am: "+GamePanel.gp.getPlayerEntity().getId() );
		    		float objectX = packet.getFloat();
		    		float objectY = packet.getFloat();
		    		float objectMX = packet.getFloat();
		    		float objectMY = packet.getFloat();
	//	    		float objectWidth = packet.getFloat();
	//	    		float objectHeight = packet.getFloat();
		    		if (objCode == OBJECTCODE.SPACE) SpaceManager.updateSpace(objectId, objectX, objectY, objectMX, objectMY);
		    		if (objCode == OBJECTCODE.ENTITY) EntityManager.updateEntity(objectId, objectX, objectY, objectMX, objectMY);
		    		if (objCode == OBJECTCODE.PLAYER) {
//		    			System.out.println("Got Objectstate of Player");
		    			if (PlayerManager.idList.contains(objectId)) {
		    				PlayerManager.updatePlayer(objectId, objectX, objectY, objectMX, objectMY);	
//		    				System.out.println("got valid data of player "+objectId);
		    			} else {
		    				/** new Player logged in (first update of this player)*/
		    				GameWindow.gw.gameInfoConsole.appendInfo("Getting new data of Player: ID="+objectId+", requesting data...");
		    				GameWindow.gw.send(ClientMessages.getPlayerData(objectId));
		    				GameWindow.gw.initSendMapList();
	//	    				PlayerManager.updatePlayer(objectId, objectX, objectY, objectMX, objectMY);
		    			}
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
			String textureName;
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
    			
    			byte[] textureNameBytes = new byte[packet.getInt()];
    			packet.get(textureNameBytes);
    			textureName = new String(textureNameBytes); // name
//    			System.out.println("init Space "+id+" with texture: "+textureName);
    			
    			polyX = packet.getInt();
	            polyY = packet.getInt();
	    		
	            numberOfPolygons = packet.getInt();
	            polygon = new ArrayList<Polygon>();
	    		for (int i = 0; i < numberOfPolygons; i++) {
	    			numberOfActualPolygon = packet.getInt();
//	    			System.out.println("number of edges: "+ numberOfActualPolygon);
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
	    		Space spaceAdd = new Space(polygon, polyX, polyY, name, id, new int[]{r,g,b}, filled, trans, scale, area, textureName);
	    		/** change the corresponding serverspace and space and send an update to players */
	    		if (ClientTextureManager.manager.contains(textureName)) {
	    			BufferedImage bufferedImage = ClientTextureManager.manager.getMapTexture(textureName);
	    			spaceAdd.setBufferedTexture(bufferedImage);
	    		} else {
	    			spaceAdd.setTexturePaint(null);
	    			spaceAdd.setBufferedTexture(null);
	    		}
	    		//SpaceManager.add(spaceAdd);
	    		spaceList.add(spaceAdd);
    		}
    		/** transform list into array */
    		Space[] spaces = new Space[spaceList.size()];
    		for (int i = 0; i<spaceList.size(); i++){
    			spaces[i] = spaceList.get(i);
//    			System.out.println("ID="+spaces[i].getId());
    		}
//    		GameWindow.gw.gameInfoConsole.appendInfo("Loaded "+spaceList.size()+ " Spaces");
    		GamePanel.gp.loadSpaceList(spaces);
    		
    		/** update GameModus if necessary */
    		GameWindow.gw.setLoadingStates(LoadingStates.SPACES, 100);
    		GameWindow.gw.updateGameModus();
//    		GameWindow.gw.gameInfoConsole.appendInfo("There are "+SpaceManager.size()+ " Spaces");
    		break;
    		
    	case EDIT_SPACE_ADDONS: 
    		/** get the message */
			BigInteger id_sa = BigInteger.valueOf(packet.getLong()); // 8 Bytes
			byte[] textureNameBytes_sa = new byte[packet.getInt()];
			packet.get(textureNameBytes_sa);
			String nameTexture_sa = new String(textureNameBytes_sa); // name
			int[] rgb_sa = new int[3];
	    	rgb_sa[0] = packet.getInt();
	    	rgb_sa[1] = packet.getInt();
	    	rgb_sa[2] = packet.getInt();
	    	float trans_sa = packet.getFloat();
	    	int filled_sa = packet.getInt();
	    	float scale_sa = packet.getFloat();
	    	float area_sa = packet.getFloat();
	    	
	    	/** change the corresponding serverspace and space and send an update to players */
	    	if(SpaceManager.editSpaceAddons(id_sa, nameTexture_sa, rgb_sa, trans_sa, filled_sa, scale_sa, area_sa)) {
	    		/** we have an existing space so we can add the Texture*/
	    		SpaceManager.get(id_sa).setBufferedTexture(ClientTextureManager.manager.getMapTexture(nameTexture_sa));
	    	}
	    	
	    	
//	    	System.out.println("change space: "+ id_sa + "; textureName: "+nameTexture_sa);
	    	
    		break;
    	case INITENTITIES:
    		/** no more need for init requests*/
    		GamePanel.gp.setServerInitialized(true);
    		/** Init Entities */
			BigInteger id_entity;
			String name_entity;
			TileSet tileEntity;
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
    			tileEntity = new TileSet(name_entity, name_entity, 50, 50);
    			entityList.add(new Entity(tileEntity,name_entity, id_entity, 0,0,GameStates.animationDelay));
//    			entityList.add(new Entity(name_entity, id_entity, 0,0, 0, 0));
    		}
    		/** transform list into array */
    		Entity[] entities = new Entity[entityList.size()];
    		for (int i = 0; i<entityList.size(); i++){
    			entities[i] = entityList.get(i);
    		}
//    		GameWindow.gw.gameInfoConsole.appendInfo("Loaded "+entities.length+ " entities from array");
    		GamePanel.gp.loadEntityList(entities);	
    		
    		/** update GameModus if necessary */
    		GameWindow.gw.setLoadingStates(LoadingStates.ENTITIES, 100);
    		GameWindow.gw.updateGameModus();
			
    		break;
    	case INITPLAYERS:
    		/** no more need for init requests*/
    		GamePanel.gp.setServerInitialized(true);
    		/** Init Entities */
			BigInteger id_player;
			String name_player;
			String name_player_TileSet;
			String name_player_TileSet_FileName;
			float spriteWidth, spriteHeight;
			TileSet tile;
			long animationDelay;
			ArrayList<PlayerEntity> playerList = new ArrayList<PlayerEntity>();
			/** for each available packet do */
    		while (packet.hasRemaining()) {
//    			byte[] bigByte = new byte[packet.getInt()];
//				for (int i =0; i<bigByte.length; i++) {
//					bigByte[i] = packet.get();
//				}
//	    		BigInteger objectId = new BigInteger(bigByte);
    			
    			id_player = BigInteger.valueOf(packet.getLong()); // ID
    			byte[] nameBytes = new byte[packet.getInt()];
    			packet.get(nameBytes);
    			name_player = new String(nameBytes); // name
    			
//    			System.out.println("packet-nr.: "+id_player);
//    			System.out.println("packet-name.: "+name_player);
    			
    			byte[] nameTileSetBytes = new byte[packet.getInt()];
    			packet.get(nameTileSetBytes);
    			name_player_TileSet = new String(nameTileSetBytes); // name
    			byte[] nameTileSetFileNameBytes = new byte[packet.getInt()];
    			packet.get(nameTileSetFileNameBytes);
    			name_player_TileSet_FileName = new String(nameTileSetFileNameBytes); // name
    			
    			spriteWidth = packet.getFloat();
    			spriteHeight = packet.getFloat();
    			animationDelay = packet.getLong();
    			
    			/** only add if its not me myself */
    			if (!name_player.equals(GameWindow.gw.getPlayerName())) {
//    				System.out.println(name_player +" VS "+ GameWindow.gw.getPlayer());
    				tile = new TileSet(name_player_TileSet_FileName, name_player_TileSet, (int) spriteWidth, (int) spriteHeight);
    				PlayerEntity playerEntity = new PlayerEntity(tile,name_player, id_player, 0,0, animationDelay);
    				playerList.add(playerEntity);
    				GameWindow.gw.gameInfoConsole.appendInfo("got data of player: "+name_player);
    				boolean createMapFolderSccess = (new File(GameStates.standardMapFolder+name_player).mkdirs());
				    if (!createMapFolderSccess) {
				         // Directory creation failed
				    	GameWindow.gw.gameInfoConsole.appendInfo("couldn't add Map-folder of player: "+name_player);
				    } else
    				GameWindow.gw.gameInfoConsole.appendInfo("added Map-folder of player: "+name_player);
    			}
    		}
    		/** transform list into array */
    		PlayerEntity[] players = new PlayerEntity[playerList.size()];
//    		System.out.println("playerSize: " + playerList.size());
    		for (int i = 0; i<playerList.size(); i++){
    			players[i] = playerList.get(i);
    		}
//    		GameWindow.gw.gameInfoConsole.appendInfo("Loaded "+entities.length+ " entities from array");
    		GamePanel.gp.loadPlayerList(players);	
    		
//    		/** update GameModus if necessary */
//    		GameWindow.gw.setLoadingStates(LoadingStates.PLAYERS, 100);
//    		GameWindow.gw.updateGameModus();
			
    		break;
    	case INITME:
    		/** init the player himself */
    		BigInteger myId = BigInteger.valueOf(packet.getLong());    		
    		byte[] nameTileSetBytes = new byte[packet.getInt()];
    		packet.get(nameTileSetBytes);
    		String tileName = new String(nameTileSetBytes);
    		byte[] nameTileSetPathBytes = new byte[packet.getInt()];
    		packet.get(nameTileSetPathBytes);
    		String tilePathName = new String(nameTileSetPathBytes);
    		byte[] groupNameBytes = new byte[packet.getInt()];
    		packet.get(groupNameBytes);
    		String groupName = new String(groupNameBytes);
    		long firstServerLogin = packet.getLong();
    		int experience = packet.getInt();
    		byte[] countryBytes = new byte[packet.getInt()];
    		packet.get(countryBytes);
    		String country = new String(countryBytes);
    		
    		float x = packet.getFloat();
    		float y = packet.getFloat();
    		float mx = packet.getFloat();
    		float my = packet.getFloat();
    		
    		TileSet tileSet = new TileSet(tilePathName, tileName, GameStates.tileWidth, GameStates.tileHeight);
    		PlayerEntity playerEntity = new PlayerEntity(tileSet, groupName, myId, x, y, GameStates.animationDelay);
    		playerEntity.setGroupName(groupName);
    		playerEntity.setFirstServerLogin(firstServerLogin);
    		playerEntity.setExperience(experience);
    		playerEntity.setCountry(country);
    		playerEntity.setMovement(mx, my);
    		playerEntity.setX(x);
    		playerEntity.setY(y);
//    		playerEntity.setMovement(10, 10);
    		GamePanel.gp.setPlayerEntity(playerEntity);
//    		System.out.println("OK, initme complete!"+x);
    		GamePanel.gp.setInitializedPlayer(true);
    		
    		/** update GameModus if necessary */
    		GameWindow.gw.setLoadingStates(LoadingStates.ME, 100);
    		GameWindow.gw.updateGameModus();
    		
    		break;
    		
    	case EDIT_PLAYER_ADDONS:
    		/** ID */
			BigInteger objectId_player = BigInteger.valueOf(packet.getLong()); // 8
	        
			byte[] nameBytes_player = new byte[packet.getInt()];
			packet.get(nameBytes_player);
			String name_player_add = new String(nameBytes_player); // name
			byte[] tileNameBytes_player = new byte[packet.getInt()];
			packet.get(tileNameBytes_player);
			String tileName_add = new String(tileNameBytes_player); // name
			byte[] tilePathNameBytes_player = new byte[packet.getInt()];
			packet.get(tilePathNameBytes_player);
			String tilePathName_add = new String(tilePathNameBytes_player); // name
	    	int tileWidth = packet.getInt(); // 4 
	    	int tileHeight = packet.getInt(); // 4 
	    	byte[] countryBytes_add = new byte[packet.getInt()];
			packet.get(countryBytes_add);
			String country_add = new String(countryBytes_add); // name
			byte[] groupNameBytes_add = new byte[packet.getInt()];
			packet.get(groupNameBytes_add);
			String groupName_add = new String(groupNameBytes_add); // name
	    	int experience_add = packet.getInt(); // 4
	    	
	    	TileSet tileSet_add = TileSetManager.manager.getTileSet(tileName_add);
	    	System.out.println("got tileset: "+tileName_add + " from player "+name_player_add+" ID="+objectId_player);
	    	PlayerEntity playerEntity_overwrite = new PlayerEntity(tileSet_add, name_player_add, objectId_player, 0, 0, GameStates.animationDelay);
//	    	playerEntity.setTileSetName(tileName_add);
//	    	playerEntity.setTileSetPathName(tilePathName);
	    	playerEntity_overwrite.setWidth(tileWidth);
	    	playerEntity_overwrite.setHeight(tileHeight);
	    	playerEntity_overwrite.setCountry(country_add);
	    	playerEntity_overwrite.setGroupName(groupName_add);
	    	playerEntity_overwrite.setExperience(experience_add);
//	    	PlayerManager.overwrite(playerEntity_overwrite);
	    	PlayerManager.updatePlayerAddons(objectId_player, name_player_add, tileName_add, tilePathName_add, tileWidth, tileHeight, country_add, groupName_add, experience_add);
//	    	this.getRoom().editPlayerAddons(thisPlayerName, tileName, tilePathName, tileWidth, tileHeight, country, groupName, experience);
	    	
    		break;
    	case SENDTEXTURE:
    		byte[] nameBytesTexture = new byte[packet.getInt()];
			packet.get(nameBytesTexture);
			String name_texture = new String(nameBytesTexture); // name
			
//			for (int i = 0; i< TextureManager.manager.getSize(); i++) {
//				System.out.println("texture: "+ i + " is "+ TextureManager.manager.getTextureNames()[i]);
//			}
			
			/** if not yet contained inside the manager and there is no actual texture to download */
			if (!ClientTextureManager.manager.contains(name_texture)) {
				int packetIndex = packet.getInt();
				int countPackets = packet.getInt();
//				System.out.println("Got a new Texture: "+name_texture);
				String playerName = name_texture.substring(0, name_texture.indexOf("_"));
				/** init for the first packet */
				if (packetIndex == 0 && ClientTextureManager.manager.getDownloadTextureName().equals("")) {
					ClientTextureManager.manager.initDownload(name_texture,countPackets, playerName);
				}
				if (packetIndex == ClientTextureManager.manager.getActualDownloadIndex() && name_texture.equals(ClientTextureManager.manager.getDownloadTextureName())) {
//					System.out.println("got the right part of the texture!");
					/** get the part of the texture */
					int lengthOfPacket = packet.getInt();
					byte[] image = new byte[lengthOfPacket];
					packet.get(image);
					
					ClientTextureManager.manager.getPartOfDownload(name_texture, packetIndex, image, playerName);
					/** send OK, if we still need packets */
					if (packetIndex < countPackets-1) {
		    			/** send the "received!!"-message if there are textures remaining */
		    			GameWindow.gw.send(ClientMessages.sendReadyForNextTexturePacket(GameWindow.gw.getPlayerName(), packetIndex));
		    		} else {
		    			/** send "this one is complete, send next! */
//		    			System.out.println("next texture, please!");
		    			GameWindow.gw.setReadyForNextMessage(true);
		    			
		    			GameWindow.gw.gameInfoConsole.appendSimpleDate("Got the new texture "+name_texture+" by player "+playerName);
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
//    			GameWindow.gw.gameInfoConsole.appendSimpleDate("I have already the texture: "+name_texture+" by player "+playerName+" - next please!");
    			GameWindow.gw.send(ClientMessages.sendNextTexture(name_texture));
    			GameWindow.gw.setReadyForNextMessage(true);
			}
    		
    		break;
    	case OBJECTDELETE:
    		if (GamePanel.gp.isServerInitialized()) {
	    		OBJECTCODE objCode = OBJECTCODE.values()[packet.getInt()];
	    		BigInteger objectId = BigInteger.valueOf(packet.getLong());;
	    		
	    		String deleteText = "";
	    		
	    		if (objCode == OBJECTCODE.SPACE) {
	    			deleteText = "Deleted space "+objectId;
	    			SpaceManager.remove(objectId);
	    		}
	    		if (objCode == OBJECTCODE.ENTITY) {
	    			deleteText = "Deleted entity "+objectId;
	    			EntityManager.remove(objectId);
	    		}
	    		if (objCode == OBJECTCODE.PLAYER) {
	    			deleteText = "Player "+ PlayerManager.get(objectId).getName() + " logged out";
	    			PlayerManager.remove(objectId);
	    		}
	    		GameWindow.gw.gameInfoConsole.appendInfo(deleteText);
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
    		byte[] imagePacket = ClientTextureManager.manager.getTexturePacket(oldPacket+1);
    		String textureName_ready = ClientTextureManager.manager.getUploadTextureName();
    		/** send the next packet */
    		GameWindow.gw.send(ClientMessages.uploadTexture(textureName_ready, oldPacket+1, ClientTextureManager.manager.getNumberOfPacketsUploadTexture() , imagePacket.length, imagePacket, GameWindow.gw.getPlayerName()));
    		
    		break;
    		
    	case SEND_AVAILABLE_TEXTURES:
    		if (GameWindow.gw.isReadyForNextMessage()) {
    			GameWindow.gw.setReadyForNextMessage(false);
	    		ArrayList<String> textureNames = new ArrayList<String>();
	    		int countTextures = packet.getInt();
	    		for (int i = 0; i < countTextures; i++) {
	    			byte[] nameTextureBytes = new byte[packet.getInt()];
	    			packet.get(nameTextureBytes);
	    			String nameTexture = new String(nameTextureBytes); // name
	    			textureNames.add(nameTexture);
	    		}
	    		ArrayList<String> missingTextures = ClientTextureManager.manager.missingTextures(textureNames);
	    		if (missingTextures.size() > 0) {
	    			/** return answer: give me missing textures! */
	    			ArrayList<String> emptyList = new ArrayList<String>();
	    			emptyList.add(missingTextures.get(0));
	    			GameWindow.gw.send(ClientMessages.sendMissingTextures(emptyList));
	    		} else {
	    			/** I have all textures !!! */
	    			/** update GameModus if necessary */
	        		GameWindow.gw.setLoadingStates(LoadingStates.TEXTURES, 100);
	        		GameWindow.gw.updateGameModus();
	    		}
	    		GameWindow.gw.setReadyForNextMessage(true);
    		}
    		
    		break;
    	case SEND_MAP:
    		/** playerName */
    		byte[] nameBytes_player_sendMap = new byte[packet.getInt()];
			packet.get(nameBytes_player_sendMap);
			String name_player_sendMap = new String(nameBytes_player_sendMap); // name
			int sizeOfMap = packet.getInt(); // 4 
    		byte[] mapFile = new byte[sizeOfMap];
    		packet.get(mapFile);
    		byte[] fileNameBytes_player_sendMap = new byte[packet.getInt()];
			packet.get(fileNameBytes_player_sendMap);
			String fileName_player_sendMap = new String(fileNameBytes_player_sendMap); // name
			int sizeOfSendList = packet.getInt(); // 4 
			
			GameWindow.gw.gameInfoConsole.appendInfo("Remaining Map-Objects to download: "+sizeOfSendList);
			
			/** only reset Maps if we are not the author of the file */
			if (!name_player_sendMap.equals(GameWindow.gw.getPlayerName())) {
				
				FileOutputStream stream;
				try {
					stream = new FileOutputStream(GameStates.standardMapFolder+name_player_sendMap+"/"+fileName_player_sendMap);
					stream.write(mapFile);
					stream.close();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} 
				
				String paintLayer;
				String strSplit[] = fileName_player_sendMap.split("_");
				int x_point = Integer.parseInt(strSplit[1]);
				int y_point = Integer.parseInt((strSplit[2]).replace(".map", ""));
				Point p_sendMap = new Point(x_point, y_point);
				
				if (fileName_player_sendMap.startsWith("cobble")) {
					paintLayer = "cobble";
					GameWindow.gw.getMapManagers().get(paintLayer).remove(p_sendMap);
					GameWindow.gw.getMapManagers().get(paintLayer).putMapFileName(p_sendMap, GameStates.standardMapFolder+name_player_sendMap+"/"+fileName_player_sendMap);
				} else if (fileName_player_sendMap.startsWith("grass")) {
					paintLayer = "grass";
					GameWindow.gw.getMapManagers().get(paintLayer).remove(p_sendMap);
					GameWindow.gw.getMapManagers().get(paintLayer).putMapFileName(p_sendMap, GameStates.standardMapFolder+name_player_sendMap+"/"+fileName_player_sendMap);
				} else if (fileName_player_sendMap.startsWith("snow")) {
					paintLayer = "snow";
					GameWindow.gw.getMapManagers().get(paintLayer).remove(p_sendMap);
					GameWindow.gw.getMapManagers().get(paintLayer).putMapFileName(p_sendMap, GameStates.standardMapFolder+name_player_sendMap+"/"+fileName_player_sendMap);
				} else if (fileName_player_sendMap.startsWith("tree1")) {
					paintLayer = "tree1";
					GameWindow.gw.getObjectMapManagers().get(paintLayer).remove(p_sendMap);
					GameWindow.gw.getObjectMapManagers().get(paintLayer).putMapFileName(p_sendMap, GameStates.standardMapFolder+name_player_sendMap+"/"+fileName_player_sendMap);
				} else if (fileName_player_sendMap.startsWith("tree2")) {
					paintLayer = "tree2";
					GameWindow.gw.getObjectMapManagers().get(paintLayer).remove(p_sendMap);
					GameWindow.gw.getObjectMapManagers().get(paintLayer).putMapFileName(p_sendMap, GameStates.standardMapFolder+name_player_sendMap+"/"+fileName_player_sendMap);
				} else if (fileName_player_sendMap.startsWith("overlayTree1")) {
					paintLayer = "overlayTree1";
					GameWindow.gw.getObjectMapManagers().get(paintLayer).remove(p_sendMap);
					GameWindow.gw.getObjectMapManagers().get(paintLayer).putMapFileName(p_sendMap, GameStates.standardMapFolder+name_player_sendMap+"/"+fileName_player_sendMap);
				} else if (fileName_player_sendMap.startsWith("overlayTree2")) {
					paintLayer = "overlayTree2";
					GameWindow.gw.getObjectMapManagers().get(paintLayer).remove(p_sendMap);
					GameWindow.gw.getObjectMapManagers().get(paintLayer).putMapFileName(p_sendMap, GameStates.standardMapFolder+name_player_sendMap+"/"+fileName_player_sendMap);
				} else {
					GameWindow.gw.gameInfoConsole.appendInfo("Didn't find map: "+fileName_player_sendMap);
				}
			} else {
				// i got my own mapFile!
			}
			
    		break;
    	case CHAT:
    		byte[] chatStringBytes = new byte[packet.getInt()];
			packet.get(chatStringBytes);
			String chatString = new String(chatStringBytes); // name
    		GameWindow.gw.appendOutput(chatString);
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
