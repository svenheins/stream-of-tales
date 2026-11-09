/* Copyright (C) Sven Heins - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * author: Sven Heins
 * date: 05.07.2015
 */
package de.svenheins.handlers;

import java.awt.Point;
import java.awt.Polygon;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;

import com.sun.sgs.client.ClientChannel;

import de.svenheins.main.AttributeType;
import de.svenheins.main.EntityStates;
import de.svenheins.main.GUI;
import de.svenheins.main.GamePanel;
import de.svenheins.main.GameStates;
import de.svenheins.main.GameWindow;
import de.svenheins.main.LoadingStates;
import de.svenheins.main.PlayerProperty;
import de.svenheins.main.Priority;
import de.svenheins.main.SoundEffect;
import de.svenheins.main.TileDimensions;
import de.svenheins.main.TileSize;
import de.svenheins.main.gui.Button;
import de.svenheins.main.gui.PlayerListGUI;
import de.svenheins.main.gui.PlayerListGUIManager;
import de.svenheins.managers.AgentManager;
import de.svenheins.managers.AreaInfluenceManager;
import de.svenheins.managers.ClientTextureManager;
import de.svenheins.managers.EntityManager;
import de.svenheins.managers.GrowthSpotManager;
import de.svenheins.managers.ItemManager;
import de.svenheins.managers.LightManager;
import de.svenheins.managers.ObjectMapManager;
import de.svenheins.managers.PlayerAgentManager;
import de.svenheins.managers.PlayerAgentManagerManager;
import de.svenheins.managers.PlayerManager;
import de.svenheins.managers.SpaceManager;
import de.svenheins.managers.TileSetManager;
//import de.svenheins.managers.TextureManager;
import de.svenheins.messages.ClientMessages;
import de.svenheins.messages.ITEMCODE;
import de.svenheins.messages.OBJECTCODE;
import de.svenheins.messages.OPCODE;
import de.svenheins.objects.AreaInfluence;
import de.svenheins.objects.Entity;
import de.svenheins.objects.GrowthSpot;
import de.svenheins.objects.InteractionTile;
import de.svenheins.objects.Light;
import de.svenheins.objects.LocalMap;
import de.svenheins.objects.LocalObject;
import de.svenheins.objects.PlayerEntity;
import de.svenheins.objects.Space;
import de.svenheins.objects.TileSet;
import de.svenheins.objects.WorldLatticePosition;
import de.svenheins.objects.agents.Agent;
import de.svenheins.objects.items.Container;
import de.svenheins.objects.items.Item;
import de.svenheins.objects.items.ItemFactoryCollection;

public class ClientMessageHandler {
	/* {@inheritDoc} */
    public static void parseClientPacket(ByteBuffer packet) {
        OPCODE code = getOpCode(packet);
        parseClientPacket(code, packet);
    }
    
    /* {@inheritDoc} */
    public static void parseClientPacket(OPCODE opCode, ByteBuffer packet) {
    	switch(opCode) {
    	case OBJECTSTATE: {
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
		    				GUI.gui.gameInfoConsole.appendInfo("Getting new data of Player: ID="+objectId+", requesting data...");
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
    		break; }
    	case INITSPACES: {
    		/** no more need for init requests*/
    		GamePanel.gp.setServerInitialized(true);
    		/** Init Spaces */
			BigInteger id;
			String name;
			String room;
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
    			byte[] roomBytes = new byte[packet.getInt()];
    			packet.get(roomBytes);
    			room = new String(roomBytes); // name
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
    	}
    	case EDIT_SPACE_ADDONS: {
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
	    	
    		break; }
    	case INITENTITIES: {
    		/** no more need for init requests*/
    		GamePanel.gp.setServerInitialized(true);
    		/** Init Entities */
			BigInteger id_entity;
			String name_entity;
			String room_entity;
			TileSet tileEntity;
			ArrayList<Entity> entityList = new ArrayList<Entity>();
			/** for each available packet do */
    		while (packet.hasRemaining()) {
    			id_entity = BigInteger.valueOf(packet.getLong()); // ID
    			byte[] nameBytes = new byte[packet.getInt()];
    			packet.get(nameBytes);
    			name_entity = new String(nameBytes); // name
    			byte[] roomBytes = new byte[packet.getInt()];
    			packet.get(roomBytes);
    			room_entity = new String(roomBytes); // name
    			tileEntity = new TileSet(name_entity, name_entity, 50, 50);
    			entityList.add(new Entity(tileEntity,name_entity,room_entity, id_entity, 0,0,GameStates.animationDelay));
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
			
    		break; }
    	case INITPLAYERS: {
    		/** no more need for init requests*/
    		GamePanel.gp.setServerInitialized(true);
    		/** Init Entities */
			BigInteger id_player;
			String name_player;
			String room;
			String name_player_TileSet;
			String name_player_TileSet_FileName;
			String weapon, armor, helmet, feet, shield, amulet, glove, shirt, trousers, hair;
			byte[] weaponByte, armorByte, helmetByte, feetByte, shieldByte, amuletByte, gloveByte, shirtByte, trousersByte, hairBytes;
			float spriteWidth, spriteHeight;
			TileSet tile;
			long animationDelay;
			ArrayList<PlayerEntity> playerList = new ArrayList<PlayerEntity>();
			/** for each available packet do */
    		while (packet.hasRemaining()) {
    			id_player = BigInteger.valueOf(packet.getLong()); // ID
    			byte[] nameBytes = new byte[packet.getInt()];
    			packet.get(nameBytes);
    			name_player = new String(nameBytes); // name
    			byte[] roomBytes = new byte[packet.getInt()];
    			packet.get(roomBytes);
    			room = new String(roomBytes); // name
//    			System.out.println("packet-nr.: "+id_player);
//    			System.out.println("packet-name.: "+name_player);
    			byte[] nameTileSetBytes = new byte[packet.getInt()];
    			packet.get(nameTileSetBytes);
    			name_player_TileSet = new String(nameTileSetBytes); // name
    			byte[] nameTileSetFileNameBytes = new byte[packet.getInt()];
    			packet.get(nameTileSetFileNameBytes);
    			name_player_TileSet_FileName = new String(nameTileSetFileNameBytes); // name
    			weaponByte = new byte[packet.getInt()];
    			packet.get(weaponByte);
    			weapon = new String(weaponByte); // name
    			armorByte = new byte[packet.getInt()];
    			packet.get(armorByte);
    			armor = new String(armorByte); // name
    			helmetByte = new byte[packet.getInt()];
    			packet.get(helmetByte);
    			helmet = new String(helmetByte); // name
    			feetByte = new byte[packet.getInt()];
    			packet.get(feetByte);
    			feet = new String(feetByte); // name
    			shieldByte = new byte[packet.getInt()];
    			packet.get(shieldByte);
    			shield = new String(shieldByte); // name
    			
    			amuletByte = new byte[packet.getInt()];
    			packet.get(amuletByte);
    			amulet = new String(amuletByte); // name
    			gloveByte = new byte[packet.getInt()];
    			packet.get(gloveByte);
    			glove = new String(gloveByte); // name
    			shirtByte = new byte[packet.getInt()];
    			packet.get(shirtByte);
    			shirt = new String(shirtByte); // name
    			trousersByte = new byte[packet.getInt()];
    			packet.get(trousersByte);
    			trousers = new String(trousersByte); // name
    			hairBytes = new byte[packet.getInt()];
    			packet.get(hairBytes);
    			hair = new String(hairBytes); // name
    			
    			spriteWidth = packet.getFloat();
    			spriteHeight = packet.getFloat();
    			animationDelay = packet.getLong();
    			
    			/** only add if its not me myself */
    			if (!name_player.equals(GameWindow.gw.getPlayerName())) {
//    				System.out.println(name_player +" VS "+ GameWindow.gw.getPlayerName());
    				tile = new TileSet(name_player_TileSet_FileName, name_player_TileSet, (int) spriteWidth, (int) spriteHeight);
    				PlayerEntity playerEntity = new PlayerEntity(tile,name_player, room, id_player, 0,0, animationDelay);
    				Entity weaponEntity = null;
    				if (!weapon.equals("")) weaponEntity = new Entity(TileSetManager.manager.getTileSet(weapon), weapon, room,BigInteger.valueOf(0), 0, 0, GameStates.animationDelay);
    				Entity armorEntity = null;
    				if(!armor.equals("")) armorEntity = new Entity(TileSetManager.manager.getTileSet(armor), armor, room,BigInteger.valueOf(0), 0, 0, GameStates.animationDelay);
    				Entity helmetEntity = null;
    				if (!helmet.equals("")) helmetEntity = new Entity(TileSetManager.manager.getTileSet(helmet), helmet, room,BigInteger.valueOf(0), 0, 0, GameStates.animationDelay);
    				Entity feetEntity = null;
    				if (!feet.equals("")) feetEntity = new Entity(TileSetManager.manager.getTileSet(feet), feet, room,BigInteger.valueOf(0), 0, 0, GameStates.animationDelay);
    				Entity shieldEntity = null;
    				if (!shield.equals("")) shieldEntity = new Entity(TileSetManager.manager.getTileSet(shield), shield, room,BigInteger.valueOf(0), 0, 0, GameStates.animationDelay);
    				Entity amuletEntity = null;
    				if (!amulet.equals("")) amuletEntity = new Entity(TileSetManager.manager.getTileSet(amulet), amulet,room, BigInteger.valueOf(0), 0, 0, GameStates.animationDelay);
    				Entity gloveEntity = null;
    				if (!glove.equals("")) gloveEntity = new Entity(TileSetManager.manager.getTileSet(glove), glove, room,BigInteger.valueOf(0), 0, 0, GameStates.animationDelay);
    				Entity shirtEntity = null;
    				if (!shirt.equals("")) shirtEntity = new Entity(TileSetManager.manager.getTileSet(shirt), shirt, room,BigInteger.valueOf(0), 0, 0, GameStates.animationDelay);
    				Entity trousersEntity = null;
    				if (!trousers.equals("")) trousersEntity = new Entity(TileSetManager.manager.getTileSet(trousers), trousers, room,BigInteger.valueOf(0), 0, 0, GameStates.animationDelay);
    				Entity hairEntity = null;
    				if (!hair.equals("")) hairEntity = new Entity(TileSetManager.manager.getTileSet(hair), hair, room,BigInteger.valueOf(0), 0, 0, GameStates.animationDelay);
    				
    				
    				playerEntity.setWeaponEntity(weaponEntity);
    				playerEntity.setArmorEntity(armorEntity);
    				playerEntity.setHelmetEntity(helmetEntity);
    				playerEntity.setFeetEntity(feetEntity);
    				playerEntity.setShieldEntity(shieldEntity);
    				playerEntity.setAmuletEntity(amuletEntity);
    				playerEntity.setGloveEntity(gloveEntity);
    				playerEntity.setShirtEntity(shirtEntity);
    				playerEntity.setTrousersEntity(trousersEntity);
    				playerEntity.setHairEntity(hairEntity);
    				
    				playerList.add(playerEntity);
    				
    				/** add PlayerButton */
    				TileSet tileSet = new TileSet("tilesets/entities/playerSymbol.png", "playerSymbol", 32, 32);
    				PlayerListGUI playerListGUI = PlayerListGUIManager.get("playerList");
    				Button newPlayerButton = new Button(tileSet, name_player_TileSet, id_player, 0 , 0, animationDelay, name_player, 0, "");
    				playerListGUI.add(newPlayerButton);
    				
    				GUI.gui.gameInfoConsole.appendInfo("got data of player: "+name_player);
    				boolean createMapFolderSccess = (new File(GameStates.standardMapFolder+name_player).mkdirs());
				    if (!createMapFolderSccess) {
				         // Directory creation failed
				    	GUI.gui.gameInfoConsole.appendInfo("couldn't add Map-folder of player: "+name_player);
				    } else
    				GUI.gui.gameInfoConsole.appendInfo("added Map-folder of player: "+name_player);
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
			
    		break; }
    	case INITME: {
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
    		byte[] roomBytes = new byte[packet.getInt()];
    		packet.get(roomBytes);
    		String room = new String(roomBytes);
    		long firstServerLogin = packet.getLong();
    		int experience = packet.getInt();
    		byte[] countryBytes = new byte[packet.getInt()];
    		packet.get(countryBytes);
    		String country = new String(countryBytes);
    		
    		float x = packet.getFloat();
    		float y = packet.getFloat();
    		float mx = packet.getFloat();
    		float my = packet.getFloat();
    		
    		BigInteger maxItemID = BigInteger.valueOf(packet.getLong());
    		BigInteger maxAreaInfluenceID = BigInteger.valueOf(packet.getLong());
    		BigInteger maxGrowthSpotID = BigInteger.valueOf(packet.getLong());
    		long worldTime = packet.getLong();
/** OK, here we got all the bytes from the message !!!*/
    		
    		
    		TileSet tileSet = new TileSet(tilePathName, tileName, (int)GameStates.getPlayerProperty(tileName, PlayerProperty.WIDTH), (int)GameStates.getPlayerProperty(tileName, PlayerProperty.HEIGHT));
    		PlayerEntity playerEntity = new PlayerEntity(tileSet, GameWindow.gw.getPlayerName(), room, myId, x, y, GameStates.animationDelay);
    		playerEntity.setGroupName(groupName);
//    		System.out.println("groupName = "+groupName);
    		
    		playerEntity.setFirstServerLogin(firstServerLogin);
    		playerEntity.setExperience(experience);
    		playerEntity.setCountry(country);
    		playerEntity.setMovement(mx, my);
    		playerEntity.setX(x);
    		playerEntity.setY(y);
    		
    		ItemManager.setMaxIDValue(maxItemID);
    		AreaInfluenceManager.setMaxIDValue(maxAreaInfluenceID);
    		GrowthSpotManager.setMaxIDValue(maxGrowthSpotID);
//    		GamePanel.gp.setPlayerEntity(playerEntity);
    		
    		PlayerEntity plEntity = GamePanel.gp.getPlayerEntity();
    		plEntity.setFirstServerLogin(firstServerLogin);
    		plEntity.setTileSet(tileSet);
    		plEntity.setName(GameWindow.gw.getPlayerName());
    		plEntity.setId(myId);
    		plEntity.setGroupName(groupName);
    		plEntity.setRoom(room);
    		plEntity.setExperience(experience);
    		plEntity.setCountry(country);
    		plEntity.setMovement(mx, my);
    		plEntity.setX(x);
    		plEntity.setY(y);
    		
    		
    		
//    		System.out.println("ID = "+ GamePanel.gp.getPlayerEntity().getId().longValue());
    		GamePanel.gp.initContainers();
    		
//    		System.out.println("OK, initme complete!"+x);
    		GamePanel.gp.setInitializedPlayer(true);
    		
    		/** update GameModus if necessary */
    		GameWindow.gw.setLoadingStates(LoadingStates.ME, 100);
    		GameWindow.gw.updateGameModus();
    		
    		GamePanel.gp.initAgentManager();
    		if (GamePanel.gp.getWorldTime()<0) {
    			GamePanel.gp.setWorldTime(worldTime);
    		}
    		
    		break;
    	}
    	case EDIT_PLAYER_EQUIPMENT: {
    		BigInteger playerID = BigInteger.valueOf(packet.getLong());
    		if ( playerID.equals(GamePanel.gp.getPlayerEntity().getId())) {
    			// its me -> ignore it 
    		} else {
//    			System.out.println("Player: "+PlayerManager.get(playerID).getName()+" sent equipment update");
    			
	    		byte[] weaponByte = new byte[packet.getInt()];
	    		packet.get(weaponByte);
	    		String weaponName = new String(weaponByte);
	    		byte[] armorByte = new byte[packet.getInt()];
	    		packet.get(armorByte);
	    		String armorName = new String(armorByte);
	    		byte[] helmetByte = new byte[packet.getInt()];
	    		packet.get(helmetByte);
	    		String helmetName = new String(helmetByte);
	    		byte[] feetByte = new byte[packet.getInt()];
	    		packet.get(feetByte);
	    		String feetName = new String(feetByte);
	    		byte[] shieldByte = new byte[packet.getInt()];
	    		packet.get(shieldByte);
	    		String shieldName = new String(shieldByte);
	    		
	    		byte[] amuletByte = new byte[packet.getInt()];
	    		packet.get(amuletByte);
	    		String amuletName = new String(amuletByte);
	    		byte[] gloveByte = new byte[packet.getInt()];
	    		packet.get(gloveByte);
	    		String gloveName = new String(gloveByte);
	    		byte[] shirtByte = new byte[packet.getInt()];
	    		packet.get(shirtByte);
	    		String shirtName = new String(shirtByte);
	    		byte[] trousersByte = new byte[packet.getInt()];
	    		packet.get(trousersByte);
	    		String trousersName = new String(trousersByte);
	    		
//	    		System.out.println("Weapon = "+weaponName);
//	    		System.out.println("Armor = "+armorName);
//	    		System.out.println("Helmet = "+helmetName);
//	    		System.out.println("Feet = "+feetName);
//	    		System.out.println("Shield = "+shieldName);
	    		
	    		String tileName = PlayerManager.get(playerID).getTileSet().getName();
	    		
	    		PlayerEntity weaponEntity = null;
	    		if (!weaponName.equals("")) {
	    			weaponEntity = new PlayerEntity(TileSetManager.manager.getTileSet(GameStates.itemEquipPrefix+tileName+weaponName), GameStates.itemEquipPrefix+tileName+weaponName, PlayerManager.get(playerID).getRoom(),BigInteger.valueOf(0), 0, 0, GameStates.animationDelay);
	    		}
	    		PlayerManager.get(playerID).setWeaponEntity(weaponEntity);
	    		
	    		PlayerEntity armorEntity = null;
	    		if (!armorName.equals("")) {
	    			armorEntity = new PlayerEntity(TileSetManager.manager.getTileSet(GameStates.itemEquipPrefix+tileName+armorName), GameStates.itemEquipPrefix+tileName+armorName, PlayerManager.get(playerID).getRoom(),BigInteger.valueOf(0), 0, 0, GameStates.animationDelay);
	    		}
	    		PlayerManager.get(playerID).setArmorEntity(armorEntity);
	    		
	    		PlayerEntity helmetEntity = null;
	    		if (!helmetName.equals("")) {
	    			helmetEntity = new PlayerEntity(TileSetManager.manager.getTileSet(GameStates.itemEquipPrefix+tileName+helmetName), GameStates.itemEquipPrefix+tileName+helmetName, PlayerManager.get(playerID).getRoom(),BigInteger.valueOf(0), 0, 0, GameStates.animationDelay);
	    		}
	    		PlayerManager.get(playerID).setHelmetEntity(helmetEntity);
	    		
	    		PlayerEntity feetEntity = null;
	    		if (!feetName.equals("")) {
	    			feetEntity = new PlayerEntity(TileSetManager.manager.getTileSet(GameStates.itemEquipPrefix+tileName+feetName), GameStates.itemEquipPrefix+tileName+feetName, PlayerManager.get(playerID).getRoom(),BigInteger.valueOf(0), 0, 0, GameStates.animationDelay);
	    		}
	    		PlayerManager.get(playerID).setFeetEntity(feetEntity);
	    		
	    		PlayerEntity shieldEntity = null;
	    		if (!shieldName.equals("")) {
	    			shieldEntity = new PlayerEntity(TileSetManager.manager.getTileSet(GameStates.itemEquipPrefix+tileName+shieldName), GameStates.itemEquipPrefix+tileName+shieldName, PlayerManager.get(playerID).getRoom(),BigInteger.valueOf(0), 0, 0, GameStates.animationDelay);
	    		}
	    		PlayerManager.get(playerID).setShieldEntity(shieldEntity);
	    		
	    		PlayerEntity amuletEntity = null;
	    		if (!amuletName.equals("")) {
	    			amuletEntity = new PlayerEntity(TileSetManager.manager.getTileSet(GameStates.itemEquipPrefix+tileName+amuletName), GameStates.itemEquipPrefix+tileName+amuletName, PlayerManager.get(playerID).getRoom(),BigInteger.valueOf(0), 0, 0, GameStates.animationDelay);
	    		}
	    		PlayerManager.get(playerID).setAmuletEntity(amuletEntity);
	    		
	    		PlayerEntity gloveEntity = null;
	    		if (!gloveName.equals("")) {
	    			gloveEntity = new PlayerEntity(TileSetManager.manager.getTileSet(GameStates.itemEquipPrefix+tileName+gloveName), GameStates.itemEquipPrefix+tileName+gloveName, PlayerManager.get(playerID).getRoom(),BigInteger.valueOf(0), 0, 0, GameStates.animationDelay);
	    		}
	    		PlayerManager.get(playerID).setGloveEntity(gloveEntity);
	    		
	    		PlayerEntity shirtEntity = null;
	    		if (!shirtName.equals("")) {
	    			shirtEntity = new PlayerEntity(TileSetManager.manager.getTileSet(GameStates.itemEquipPrefix+tileName+shirtName), GameStates.itemEquipPrefix+tileName+shirtName, PlayerManager.get(playerID).getRoom(),BigInteger.valueOf(0), 0, 0, GameStates.animationDelay);
	    		}
	    		PlayerManager.get(playerID).setShirtEntity(shirtEntity);
	    		
	    		PlayerEntity trousersEntity = null;
	    		if (!trousersName.equals("")) {
	    			trousersEntity = new PlayerEntity(TileSetManager.manager.getTileSet(GameStates.itemEquipPrefix+tileName+trousersName), GameStates.itemEquipPrefix+tileName+trousersName, PlayerManager.get(playerID).getRoom(),BigInteger.valueOf(0), 0, 0, GameStates.animationDelay);
	    		}
	    		PlayerManager.get(playerID).setTrousersEntity(trousersEntity);
    		}
    		
            break;
    	}
    	
    	case SENDCONTAINER: {
    		/** if its my own id, it is my container, so overwrite my inventory with this one */
    		BigInteger myId_SC = BigInteger.valueOf(packet.getLong());    	
    		OBJECTCODE containerType_SC = getObjectCode(packet);//OBJECTCODE.values()[packet.getInt()];
    		ITEMCODE containerAllowedItems_SC = getItemCode(packet);
    		int inventoryWidth_SC = packet.getInt();
    		int inventoryHeight_SC = packet.getInt();
    		
    		BigInteger[][] containerArray_SC = new BigInteger[inventoryHeight_SC][inventoryWidth_SC];
    		Container inventory_SC = new Container(inventoryWidth_SC, inventoryHeight_SC, containerType_SC, containerAllowedItems_SC);
    		Item item_SC;
    		int count_SC;
    		int capacity_SC;
    		byte[] itemNameBytes_SC;
    		String itemName_SC;
    		byte[] commentBytes;
    		String comment;
    		byte[] itemRoomBytes_SC;
    		String itemRoom_SC;
    		ITEMCODE itemCode_SC;
    		long creationTime_SC;
    		
    		BigInteger entityID_SC;
    		byte[] entityNameBytes_SC;
    		String entityName_SC;
    		byte[] entityTileSetNameBytes_SC;
    		String entityTileSetName_SC;
    		byte[] entityTileSetFileNameBytes_SC;
    		String entityTileSetFileName_SC;
    		
    		float itemX_SC;
    		float itemY_SC;
    		
    		Entity itemEntity_SC;
    		
    		int itemStatesLength_SC;
    		float[] itemStates_SC;
    		
    		 /** now put all container stuff into buffer */
        	for (int i = 0; i < inventoryHeight_SC; i++) {
        		for(int j = 0 ; j< inventoryWidth_SC; j++) {
        			containerArray_SC[i][j] = BigInteger.valueOf(packet.getLong());
//        			buffer.putLong(inventory.getContainerArray()[i][j].longValue()); // 8 = ID for every array element
        			/** only add item stuff if there is an item in the field */
    				if (!containerArray_SC[i][j].equals(BigInteger.valueOf(-1))) {
//    					item = inventory.getItemList().get(inventory.getContainerArray()[i][j]);
    					/** get the item id and the corresponding item */
    					count_SC = packet.getInt();
//    					buffer.putInt(item.getCount()); // 4
    					capacity_SC = packet.getInt();
//    					buffer.putInt(item.getCapacity()); // 4
    					itemNameBytes_SC = new byte[packet.getInt()];
    		    		packet.get(itemNameBytes_SC);
    		    		itemName_SC = new String(itemNameBytes_SC);
    		    		commentBytes = new byte[packet.getInt()];
    		    		packet.get(commentBytes);
    		    		comment = new String(commentBytes);
    		    		itemRoomBytes_SC = new byte[packet.getInt()];
    		    		packet.get(itemRoomBytes_SC);
    		    		itemRoom_SC = new String(itemRoomBytes_SC);
//    					buffer.putInt(item.getName().length()); // 4
//    			    	buffer.put(item.getName().getBytes()); // name.length
    		    		itemCode_SC = getItemCode(packet);
//    					buffer.put((byte) item.getItemCode().ordinal()); // 1
    		    		creationTime_SC = packet.getLong();
//    					buffer.putLong(item.getCreationTime());
    					/** entity stuff */
    		    		entityID_SC = BigInteger.valueOf(packet.getLong());
    		    		entityNameBytes_SC = new byte[packet.getInt()];
    		    		packet.get(entityNameBytes_SC);
    		    		entityName_SC = new String(entityNameBytes_SC);
//    					buffer.putInt(item.getEntity().getName().length()); // 4
//    			    	buffer.put(item.getEntity().getName().getBytes()); // name.length
    		    		entityTileSetNameBytes_SC = new byte[packet.getInt()];
    		    		packet.get(entityTileSetNameBytes_SC);
    		    		entityTileSetName_SC = new String(entityTileSetNameBytes_SC);
//    			    	buffer.putInt(item.getEntity().getTileSet().getName().length()); // 4
//    			    	buffer.put(item.getEntity().getTileSet().getName().getBytes()); // name.length
    		    		entityTileSetFileNameBytes_SC = new byte[packet.getInt()];
    		    		packet.get(entityTileSetFileNameBytes_SC);
    		    		entityTileSetFileName_SC = new String(entityTileSetFileNameBytes_SC);
//    			    	buffer.putInt(item.getEntity().getTileSet().getFileName().length()); // 4
//    			    	buffer.put(item.getEntity().getTileSet().getFileName().getBytes()); // name.length
    			    	/** item states */
    		    		itemX_SC = packet.getFloat();
    		    		itemY_SC = packet.getFloat();  
//    		    		System.out.println("x:"+itemX_SC+" y:"+itemY_SC);
    		    		itemStatesLength_SC = packet.getInt();
    		    		itemStates_SC = new float[itemStatesLength_SC];
//    					buffer.putInt(item.getStates().length);
    					for (int k = 0; k< itemStatesLength_SC; k++) {
    						itemStates_SC[k] = packet.getFloat();
//    						buffer.putFloat(item.getStates()[k]);
    					}
    					itemEntity_SC = new Entity(new TileSet(entityTileSetFileName_SC, entityTileSetName_SC,  Item.tileSetX, Item.tileSetY, Item.tileSetWidth, Item.tileSetHeight), entityName_SC, itemRoom_SC,entityID_SC, itemX_SC, itemY_SC, GameStates.animationDelayItems);
    					item_SC = ItemFactoryCollection.getItem(itemCode_SC,containerArray_SC[i][j] , itemName_SC, comment, itemRoom_SC,count_SC, itemEntity_SC.getX(), itemEntity_SC.getY(), creationTime_SC, itemStates_SC);
    					inventory_SC.getItemList().put(containerArray_SC[i][j], item_SC);
    				} else {
    					/** there is no item in this field */
    				}
        		}
        	}
    		inventory_SC.setContainerArray(containerArray_SC);
    		
    		if (myId_SC.equals(GamePanel.gp.getPlayerEntity().getId())) {
    			switch (containerType_SC) {
    			case CONTAINER_MAIN:
//    				System.out.println("GOT my inventory!");
    				GamePanel.gp.getPlayerEntity().setInventory(inventory_SC);
    				break;
    			case CONTAINER_EQUIPMENT_BODY:
    				GamePanel.gp.getPlayerEntity().setEquipmentBody(inventory_SC);
    				GamePanel.gp.getPlayerEntity().calculateTotalEquipmentInfluenceAndUpdateVisuals();
    				break;
    			case CONTAINER_USE:
    				GamePanel.gp.getPlayerEntity().setInventoryUse(inventory_SC);
    				break;
				default: ;
    			}
    		}

    		break;}
    		
    	case SENDITEMFIELD: {
    		/** if its my own id, it is my container, so overwrite my inventory with this one */
    		BigInteger myId_SC = BigInteger.valueOf(packet.getLong());    	
    		OBJECTCODE containerType_SC = getObjectCode(packet);//OBJECTCODE.values()[packet.getInt()];
    		
    		BigInteger itemID_SC;
    		Item item_SC;
    		int count_SC;
    		int capacity_SC;
    		byte[] itemNameBytes_SC;
    		String itemName_SC;
    		byte[] commentBytes;
    		String comment;
    		byte[] itemRoomBytes;
    		String itemRoom;
    		ITEMCODE itemCode_SC;
    		long creationTime_SC;
    		
    		BigInteger entityID_SC;
    		byte[] entityNameBytes_SC;
    		String entityName_SC;
    		byte[] entityTileSetNameBytes_SC;
    		String entityTileSetName_SC;
    		byte[] entityTileSetFileNameBytes_SC;
    		String entityTileSetFileName_SC;
    		
    		float itemX_SC;
    		float itemY_SC;
    		int fieldX;
    		int fieldY;
    		
    		Entity itemEntity_SC;
    		
    		int itemStatesLength_SC;
    		float[] itemStates_SC;
    		
    		itemID_SC = BigInteger.valueOf(packet.getLong());
			/** get the item id and the corresponding item */
			count_SC = packet.getInt();
			capacity_SC = packet.getInt();
			itemNameBytes_SC = new byte[packet.getInt()];
    		packet.get(itemNameBytes_SC);
    		itemName_SC = new String(itemNameBytes_SC);
    		commentBytes = new byte[packet.getInt()];
    		packet.get(commentBytes);
    		comment = new String(commentBytes);
    		itemRoomBytes = new byte[packet.getInt()];
    		packet.get(itemRoomBytes);
    		itemRoom = new String(itemRoomBytes);
    		itemCode_SC = getItemCode(packet);
    		creationTime_SC = packet.getLong();
			/** entity stuff */
    		entityID_SC = BigInteger.valueOf(packet.getLong());
    		entityNameBytes_SC = new byte[packet.getInt()];
    		packet.get(entityNameBytes_SC);
    		entityName_SC = new String(entityNameBytes_SC);
    		entityTileSetNameBytes_SC = new byte[packet.getInt()];
    		packet.get(entityTileSetNameBytes_SC);
    		entityTileSetName_SC = new String(entityTileSetNameBytes_SC);
    		entityTileSetFileNameBytes_SC = new byte[packet.getInt()];
    		packet.get(entityTileSetFileNameBytes_SC);
    		entityTileSetFileName_SC = new String(entityTileSetFileNameBytes_SC);
	    	/** item states */
    		itemX_SC = packet.getFloat();
    		itemY_SC = packet.getFloat();  
    		itemStatesLength_SC = packet.getInt();
    		itemStates_SC = new float[itemStatesLength_SC];
			for (int k = 0; k< itemStatesLength_SC; k++) {
				itemStates_SC[k] = packet.getFloat();
			}
			fieldX = packet.getInt();
			fieldY = packet.getInt();
			
			/** prepare item*/
			itemEntity_SC = new Entity(new TileSet(entityTileSetFileName_SC, entityTileSetName_SC, Item.tileSetX, Item.tileSetY, Item.tileSetWidth, Item.tileSetHeight), entityName_SC, itemRoom, entityID_SC, itemX_SC, itemY_SC, GameStates.animationDelayItems);
			
			item_SC = ItemFactoryCollection.getItem(itemCode_SC,itemID_SC , itemName_SC, comment, itemRoom, count_SC,itemEntity_SC.getX(), itemEntity_SC.getY(), creationTime_SC, itemStates_SC);
			item_SC.getEntity().setContinuousState(EntityStates.INVENTORY_SIMPLE);
			
    		if (myId_SC.equals(GamePanel.gp.getPlayerEntity().getId())) {
    			switch (containerType_SC) {
    			case CONTAINER_MAIN:
//    				System.out.println("GOT my inventory!");
    				GamePanel.gp.getPlayerEntity().getInventory().getContainerArray()[fieldY][fieldX] = itemID_SC;
    				GamePanel.gp.getPlayerEntity().getInventory().getItemList().put(itemID_SC, item_SC);
    				if (fieldX < GamePanel.gp.getPlayerEntity().getInventory().getWidth() || fieldY < GamePanel.gp.getPlayerEntity().getInventory().getHeight() ) {
    					GameWindow.gw.send(ClientMessages.getNextItem(GamePanel.gp.getPlayerEntity().getId(), containerType_SC, fieldX, fieldY));
    				}
    				
    				break;
    			case CONTAINER_EQUIPMENT_BODY:
    				GamePanel.gp.getPlayerEntity().getEquipmentBody().getContainerArray()[fieldY][fieldX] = itemID_SC;
    				GamePanel.gp.getPlayerEntity().getEquipmentBody().getItemList().put(itemID_SC, item_SC);
    				if (fieldX < GamePanel.gp.getPlayerEntity().getEquipmentBody().getWidth() || fieldY < GamePanel.gp.getPlayerEntity().getEquipmentBody().getHeight() ) {
    					GameWindow.gw.send(ClientMessages.getNextItem(GamePanel.gp.getPlayerEntity().getId(), containerType_SC, fieldX, fieldY));
    				}
    				GamePanel.gp.getPlayerEntity().calculateTotalEquipmentInfluenceAndUpdateVisuals();
    				break;
    			case CONTAINER_EQUIPMENT_WEAPON:
    				GamePanel.gp.getPlayerEntity().getEquipmentWeapon().getContainerArray()[fieldY][fieldX] = itemID_SC;
    				GamePanel.gp.getPlayerEntity().getEquipmentWeapon().getItemList().put(itemID_SC, item_SC);
    				if (fieldX < GamePanel.gp.getPlayerEntity().getEquipmentWeapon().getWidth() || fieldY < GamePanel.gp.getPlayerEntity().getEquipmentWeapon().getHeight() ) {
    					GameWindow.gw.send(ClientMessages.getNextItem(GamePanel.gp.getPlayerEntity().getId(), containerType_SC, fieldX, fieldY));
    				}
    				GamePanel.gp.getPlayerEntity().calculateTotalEquipmentInfluenceAndUpdateVisuals();
    				break;
    			case CONTAINER_EQUIPMENT_HELMET:
    				GamePanel.gp.getPlayerEntity().getEquipmentHelmet().getContainerArray()[fieldY][fieldX] = itemID_SC;
    				GamePanel.gp.getPlayerEntity().getEquipmentHelmet().getItemList().put(itemID_SC, item_SC);
    				if (fieldX < GamePanel.gp.getPlayerEntity().getEquipmentHelmet().getWidth() || fieldY < GamePanel.gp.getPlayerEntity().getEquipmentHelmet().getHeight() ) {
    					GameWindow.gw.send(ClientMessages.getNextItem(GamePanel.gp.getPlayerEntity().getId(), containerType_SC, fieldX, fieldY));
    				}
    				GamePanel.gp.getPlayerEntity().calculateTotalEquipmentInfluenceAndUpdateVisuals();
    				break;
    			case CONTAINER_EQUIPMENT_FEET:
    				GamePanel.gp.getPlayerEntity().getEquipmentFeet().getContainerArray()[fieldY][fieldX] = itemID_SC;
    				GamePanel.gp.getPlayerEntity().getEquipmentFeet().getItemList().put(itemID_SC, item_SC);
    				if (fieldX < GamePanel.gp.getPlayerEntity().getEquipmentFeet().getWidth() || fieldY < GamePanel.gp.getPlayerEntity().getEquipmentFeet().getHeight() ) {
    					GameWindow.gw.send(ClientMessages.getNextItem(GamePanel.gp.getPlayerEntity().getId(), containerType_SC, fieldX, fieldY));
    				}
    				GamePanel.gp.getPlayerEntity().calculateTotalEquipmentInfluenceAndUpdateVisuals();
    				break;
    			case CONTAINER_EQUIPMENT_SHIELD:
    				GamePanel.gp.getPlayerEntity().getEquipmentShield().getContainerArray()[fieldY][fieldX] = itemID_SC;
    				GamePanel.gp.getPlayerEntity().getEquipmentShield().getItemList().put(itemID_SC, item_SC);
    				if (fieldX < GamePanel.gp.getPlayerEntity().getEquipmentShield().getWidth() || fieldY < GamePanel.gp.getPlayerEntity().getEquipmentShield().getHeight() ) {
    					GameWindow.gw.send(ClientMessages.getNextItem(GamePanel.gp.getPlayerEntity().getId(), containerType_SC, fieldX, fieldY));
    				}
    				GamePanel.gp.getPlayerEntity().calculateTotalEquipmentInfluenceAndUpdateVisuals();
    				break;
    			case CONTAINER_EQUIPMENT_AMULET:
    				GamePanel.gp.getPlayerEntity().getEquipmentAmulet().getContainerArray()[fieldY][fieldX] = itemID_SC;
    				GamePanel.gp.getPlayerEntity().getEquipmentAmulet().getItemList().put(itemID_SC, item_SC);
    				if (fieldX < GamePanel.gp.getPlayerEntity().getEquipmentAmulet().getWidth() || fieldY < GamePanel.gp.getPlayerEntity().getEquipmentAmulet().getHeight() ) {
    					GameWindow.gw.send(ClientMessages.getNextItem(GamePanel.gp.getPlayerEntity().getId(), containerType_SC, fieldX, fieldY));
    				}
    				GamePanel.gp.getPlayerEntity().calculateTotalEquipmentInfluenceAndUpdateVisuals();
    				break;
    			case CONTAINER_EQUIPMENT_GLOVE:
    				GamePanel.gp.getPlayerEntity().getEquipmentGlove().getContainerArray()[fieldY][fieldX] = itemID_SC;
    				GamePanel.gp.getPlayerEntity().getEquipmentGlove().getItemList().put(itemID_SC, item_SC);
    				if (fieldX < GamePanel.gp.getPlayerEntity().getEquipmentGlove().getWidth() || fieldY < GamePanel.gp.getPlayerEntity().getEquipmentGlove().getHeight() ) {
    					GameWindow.gw.send(ClientMessages.getNextItem(GamePanel.gp.getPlayerEntity().getId(), containerType_SC, fieldX, fieldY));
    				}
    				GamePanel.gp.getPlayerEntity().calculateTotalEquipmentInfluenceAndUpdateVisuals();
    				break;
    			case CONTAINER_EQUIPMENT_SHIRT:
    				GamePanel.gp.getPlayerEntity().getEquipmentShirt().getContainerArray()[fieldY][fieldX] = itemID_SC;
    				GamePanel.gp.getPlayerEntity().getEquipmentShirt().getItemList().put(itemID_SC, item_SC);
    				if (fieldX < GamePanel.gp.getPlayerEntity().getEquipmentShirt().getWidth() || fieldY < GamePanel.gp.getPlayerEntity().getEquipmentShirt().getHeight() ) {
    					GameWindow.gw.send(ClientMessages.getNextItem(GamePanel.gp.getPlayerEntity().getId(), containerType_SC, fieldX, fieldY));
    				}
    				GamePanel.gp.getPlayerEntity().calculateTotalEquipmentInfluenceAndUpdateVisuals();
    				break;
    			case CONTAINER_EQUIPMENT_TROUSERS:
    				GamePanel.gp.getPlayerEntity().getEquipmentTrousers().getContainerArray()[fieldY][fieldX] = itemID_SC;
    				GamePanel.gp.getPlayerEntity().getEquipmentTrousers().getItemList().put(itemID_SC, item_SC);
    				if (fieldX < GamePanel.gp.getPlayerEntity().getEquipmentTrousers().getWidth() || fieldY < GamePanel.gp.getPlayerEntity().getEquipmentTrousers().getHeight() ) {
    					GameWindow.gw.send(ClientMessages.getNextItem(GamePanel.gp.getPlayerEntity().getId(), containerType_SC, fieldX, fieldY));
    				}
    				GamePanel.gp.getPlayerEntity().calculateTotalEquipmentInfluenceAndUpdateVisuals();
    				break;
    			case CONTAINER_USE:
    				GamePanel.gp.getPlayerEntity().getInventoryUse().getContainerArray()[fieldY][fieldX] = itemID_SC;
    				GamePanel.gp.getPlayerEntity().getInventoryUse().getItemList().put(itemID_SC, item_SC);
    				if (fieldX < GamePanel.gp.getPlayerEntity().getInventoryUse().getWidth() || fieldY < GamePanel.gp.getPlayerEntity().getInventoryUse().getHeight() ) {
    					GameWindow.gw.send(ClientMessages.getNextItem(GamePanel.gp.getPlayerEntity().getId(), containerType_SC, fieldX, fieldY));
 
    				}
    				break;
				default: ;
    			}
    		}
    		break; }
//    	case SEND_EMPTY_ITEM_FIELD: {
//    		/** if its my own id, it is my container, so overwrite my inventory with this one */
//    		BigInteger myId_SC = BigInteger.valueOf(packet.getLong());    	
//    		OBJECTCODE containerType_SC = getObjectCode(packet);//OBJECTCODE.values()[packet.getInt()];
//    		int fieldX = packet.getInt();
//			int fieldY = packet.getInt();
//			if (myId_SC.equals(GamePanel.gp.getPlayerEntity().getId())) {
//    			switch (containerType_SC) {
//    			case CONTAINER_MAIN:
////    				System.out.println("GOT my inventory!");
//    				if (fieldY < GameStates.inventoryHeightPlayer || fieldX < GameStates.inventoryWidthPlayer) {
//    					
//    				}
////    				GamePanel.gp.getPlayerEntity().getInventory().getContainerArray()[fieldY][fieldX] = itemID_SC;
////    				GamePanel.gp.getPlayerEntity().getInventory().getItemList().put(itemID_SC, item_SC);
//    				break;
//    			case CONTAINER_EQUIPMENT:
//    				break;
//    			case CONTAINER_USE:
//    				break;
//				default: ;
//    			}
//    		}
//    		break;}
    	case EDIT_PLAYER_ADDONS:  {
    		/** ID */
			BigInteger objectId_player = BigInteger.valueOf(packet.getLong()); // 8
	        
			byte[] nameBytes_player = new byte[packet.getInt()];
			packet.get(nameBytes_player);
			String name_player_add = new String(nameBytes_player); // name
			byte[] roomBytes_player = new byte[packet.getInt()];
			packet.get(roomBytes_player);
			String room = new String(roomBytes_player); // name
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
//	    	System.out.println("got tileset: "+tileName_add + " from player "+name_player_add+" ID="+objectId_player);
	    	PlayerEntity playerEntity_overwrite = new PlayerEntity(tileSet_add, name_player_add, room, objectId_player, 0, 0, GameStates.animationDelay);
	    	playerEntity_overwrite.setWidth(tileWidth);
	    	playerEntity_overwrite.setHeight(tileHeight);
	    	playerEntity_overwrite.setCountry(country_add);
	    	playerEntity_overwrite.setGroupName(groupName_add);
	    	playerEntity_overwrite.setExperience(experience_add);
	    	PlayerManager.updatePlayerAddons(objectId_player, name_player_add, room, tileName_add, tilePathName_add, tileWidth, tileHeight, country_add, groupName_add, experience_add);
//	    	this.getRoom().editPlayerAddons(thisPlayerName, tileName, tilePathName, tileWidth, tileHeight, country, groupName, experience);
	    	
    		break;
    	}
    	case EDIT_PLAYER_ROOM: {
    		BigInteger playerID = BigInteger.valueOf(packet.getLong());
			byte[] playerNameByte = new byte[packet.getInt()];
			packet.get(playerNameByte);
			String playerName = new String(playerNameByte); 
			byte[] roomNameByte = new byte[packet.getInt()];
			packet.get(roomNameByte);
			String room = new String(roomNameByte); 
			if (!GameWindow.gw.getPlayerName().equals(playerName)) {
				if (PlayerManager.idList.contains(playerID)) {
					PlayerManager.get(playerName).setRoom(room);
				}
			}
			break;
		}
    	case EDIT_PLAYER_STATES: {
    		/** ID */
			BigInteger entityID_editStates = BigInteger.valueOf(packet.getLong()); // 8
			/** get the orientation and states, to update the actual animation of the player */
			EntityStates orientation = getEntityStates(packet);
			EntityStates singleState = getEntityStates(packet);
			EntityStates continuousState = getEntityStates(packet);
			PlayerManager.updatePlayerState(entityID_editStates, orientation, singleState, continuousState);
    		break; }
    	case SENDTEXTURE: {
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
		    			
		    			GUI.gui.gameInfoConsole.appendSimpleDate("Got the new texture "+name_texture+" by player "+playerName);
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
    		
    		break; }
    	case OBJECTDELETE: {
    		if (GamePanel.gp.isServerInitialized()) {
	    		OBJECTCODE objCode = OBJECTCODE.values()[packet.getInt()];
	    		BigInteger objectId = BigInteger.valueOf(packet.getLong());
	    		
	    		String deleteText = "EMPTY: ClientMessageHandler->OBJECTDELETE";
	    		
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
	    			PlayerAgentManagerManager.remove(PlayerManager.get(objectId).getName());
	    			PlayerManager.remove(objectId);
	    			PlayerListGUIManager.get("playerList").remove(objectId);
	    		}
	    		if (objCode == OBJECTCODE.ITEM) {
	    			deleteText = "Item "+ objectId +" was removed";
	    			if (ItemManager.get(objectId) != null) {
		    			ItemManager.get(objectId).setVisible(true);
		    			ItemManager.remove(objectId);
	    			}
	    		}
	    		if (objCode == OBJECTCODE.AREAINFLUENCE) {
	    			deleteText = "AreaInfluence "+ objectId +" is ignored";
//	    			if (AreaInfluenceManager.get(objectId) != null) {
//	    				AreaInfluenceManager.remove(objectId);
//	    			}
	    		}
	    		if (objCode == OBJECTCODE.GROWTHSPOT) {
	    			deleteText = "GrowthSpot "+ objectId +" is deleted";
//	    			if (AreaInfluenceManager.get(objectId) != null) {
//	    				AreaInfluenceManager.remove(objectId);
//	    			}
	    		}
//	    		System.out.println(deleteText);
//	    		GameWindow.gw.gameInfoConsole.appendInfo(deleteText);
    		}
    		break;
    	}
    	case ADDCOMPLETEITEM: {
    		ITEMCODE itemCode = getItemCode(packet);
    		BigInteger addCompleteItemId = BigInteger.valueOf(packet.getLong());
    		byte[] nameAddCompleteItemBytes = new byte[packet.getInt()];
			packet.get(nameAddCompleteItemBytes);
			String addCompleteItemName = new String(nameAddCompleteItemBytes); // name
			byte[] commentBytes = new byte[packet.getInt()];
			packet.get(commentBytes);
			String comment = new String(commentBytes); // name
			byte[] roomBytes = new byte[packet.getInt()];
			packet.get(roomBytes);
			String room = new String(roomBytes); // name
    		float addCompleteItemX = packet.getFloat(); 
    		float addCompleteItemY = packet.getFloat();
			int addCompleteItemCount = packet.getInt();
			int stateLength = packet.getInt();
			float[] states = new float[stateLength];
			for(int i = 0; i< stateLength; i++) {
				states[i] = packet.getFloat();
			}
			
			Item addCompleteItem = ItemFactoryCollection.getItem(itemCode, addCompleteItemId, addCompleteItemName, comment, room, addCompleteItemCount, addCompleteItemX, addCompleteItemY, System.currentTimeMillis(), states);
			ItemManager.add(addCompleteItem);
			
//			switch(itemCode) {
//    		case WOOD:
//    			Wood wood = new Wood(addCompleteItemId, addCompleteItemX, addCompleteItemY);
//    			wood.setCount(addCompleteItemCount);
//    			break;
//    		case STONE:
//    			break;
//    		case BODY:
//    			if (addCompleteItemName.equals("Cloak")) {
//    				Cloak cloak = new Cloak(addCompleteItemId, addCompleteItemX, addCompleteItemY, states);
//        			cloak.setCount(addCompleteItemCount);
//        			ItemManager.add(cloak);
//    			}
//    			
//    			break;
//    		default:
//    				;
//    		}
    		break;}
    	case INITITEMS: {
    		int itemCount = packet.getInt();
    		GUI.gui.gameInfoConsole.appendInfo("Got "+itemCount+" items");
    		
    		for (int j = 0; j < itemCount; j++) {
    			ITEMCODE itemCode = getItemCode(packet);
        		BigInteger addCompleteItemId = BigInteger.valueOf(packet.getLong());
        		byte[] nameAddCompleteItemBytes = new byte[packet.getInt()];
    			packet.get(nameAddCompleteItemBytes);
    			String addCompleteItemName = new String(nameAddCompleteItemBytes); // name
    			byte[] commentBytes = new byte[packet.getInt()];
    			packet.get(commentBytes);
    			String comment = new String(commentBytes); // name
    			byte[] roomBytes = new byte[packet.getInt()];
    			packet.get(roomBytes);
    			String room = new String(roomBytes); // name
        		float addCompleteItemX = packet.getFloat(); 
        		float addCompleteItemY = packet.getFloat();
    			int addCompleteItemCount = packet.getInt();
    			int stateLength = packet.getInt();
    			float[] states = new float[stateLength];
    			for(int i = 0; i< stateLength; i++) {
    				states[i] = packet.getFloat();
    			}
    			
    			Item itemInit = ItemFactoryCollection.getItem(itemCode, addCompleteItemId, addCompleteItemName, comment, room,  addCompleteItemCount, addCompleteItemX, addCompleteItemY, System.currentTimeMillis(), states);
        		if (ItemManager.add(itemInit)) {
//        			System.out.println("Successfully added item");
        		} else {
//        			System.out.println("Could not added item");
        		}
//    			switch(itemCode) {
//        		case WOOD:
//        			Wood wood = new Wood(addCompleteItemId, addCompleteItemX, addCompleteItemY);
//        			wood.setCount(addCompleteItemCount);
//        			ItemManager.add(wood);
//        			break;
//        		case STONE:
//        			break;
//        		case BODY:
//        			if (addCompleteItemName.equals("Cloak")) {
//        				Cloak cloak = new Cloak(addCompleteItemId, addCompleteItemX, addCompleteItemY, states);
//            			cloak.setCount(addCompleteItemCount);
//            			ItemManager.add(cloak);
//        			}
//        			
//        			break;
//        		default:
//        				;
//        		}
//    			
//    			
//    		}
    		
    		}
    		break;}
    	case TAKEITEM: {
//    		GameWindow.gw.gameConsole.appendInfo("taking item");
    		if (GamePanel.gp.isServerInitialized()) {
	    		BigInteger takeItemId = BigInteger.valueOf(packet.getLong());
	    		/** take the item */
//	    		System.out.println("inventory full: " +GamePanel.gp.getPlayerEntity().getPlayerInventory().getItemList().size()+"/"+GamePanel.gp.getPlayerEntity().getPlayerInventory().getWidth()*GamePanel.gp.getPlayerEntity().getPlayerInventory().getHeight());
//	    		GameWindow.gw.gameInfoConsole.appendInfo("inventory used: " +GamePanel.gp.getPlayerEntity().getInventory().getItemList().size()+"/"+GamePanel.gp.getPlayerEntity().getInventory().getWidth()*GamePanel.gp.getPlayerEntity().getInventory().getHeight());
//	    		System.out.println("ID = "+takeItemId);
	    		if (ItemManager.get(takeItemId)!= null) {
	    			/** temporary save the inventory */
//	    			int width = GamePanel.gp.getPlayerEntity().getInventory().getWidth();
//	    			int height = GamePanel.gp.getPlayerEntity().getInventory().getHeight();
//	    			Container tempInventory = new Container(width, height, GamePanel.gp.getPlayerEntity().getInventory().getContainerType());
//	    			BigInteger tempID;
//	    			Item tempItemOld;
//	    			Item tempItem;
//	    			for (int i = 0; i < height; i++) {
//	    				for (int j = 0; j< width; j++) {
//	    					tempID = GamePanel.gp.getPlayerEntity().getInventory().getContainerArray()[i][j];
//	    					tempInventory.getContainerArray()[i][j] = tempID;
//	    					if (!tempID.equals(BigInteger.valueOf(-1))) {
//	    						tempItemOld = GamePanel.gp.getPlayerEntity().getInventory().getItemList().get(tempID);
//		    					tempItem = Item.getItem(tempItemOld.getItemCode(), tempID, tempItemOld.getName(), tempItemOld.getCount(), tempItemOld.getCapacity(), tempItemOld.getEntity(), tempItemOld.getCreationTime(), tempItemOld.getStates());
//	    						tempInventory.getItemList().put(tempID, tempItem);
//	    					}
//	    				}
//	    			}
	    			/** add item */
//	    			ItemManager.get(takeItemId).setVisible(true);
		    		Item restItem = GamePanel.gp.getPlayerEntity().getInventory().addItem(ItemManager.get(takeItemId)/*, GamePanel.gp.getPlayerEntity().getId()*/);
		    		/** update server */
//		    		containerUpdateSend(tempInventory);
		    		containerUpdateSendBrutal(GamePanel.gp.getPlayerEntity().getInventory());
		    		GameWindow.gw.getSendItemList().remove(takeItemId);
		    		GameWindow.gw.send(ClientMessages.tookItem(takeItemId));
		    		GameWindow.gw.sound.addStartingSoundEffect(SoundEffect.COLLECT_ITEM);
		    		if (restItem != null) {
	    				/** first send to server for the itemList */
						GameWindow.gw.send(ClientMessages.addItem(restItem.getId(), restItem.getItemCode(), restItem.getCount(), restItem.getCapacity(), restItem.getEntity().getX(), restItem.getEntity().getY(), restItem.getEntity().getMX(), restItem.getEntity().getMY(), restItem.getName(), restItem.getComment(), restItem.getRoom(), restItem.getEntity().getTileSet().getFileName(), restItem.getEntity().getName(), restItem.getAttributes()));
						for (String channelName : GameWindow.gw.getSpaceChannels().values()) {
							ClientChannel channel = GameWindow.gw.getChannelByName(channelName);
							try {
								channel.send(ClientMessages.addCompleteItem(restItem.getItemCode(), takeItemId, restItem.getName(), restItem.getComment(), restItem.getRoom(), restItem.getEntity().getX(), restItem.getEntity().getY(), restItem.getCount(), restItem.getAttributes()));
							} catch (IOException e) {
								e.printStackTrace();
							}	
						}
					}
	    		} else {
	    			GameWindow.gw.getSendItemList().remove(takeItemId);
	    		}
    		}
    		break; }
    	case INITAREAINFLUENCES: {
    		BigInteger maxID = BigInteger.valueOf(packet.getLong());
    		int areaInfluenceCount = packet.getInt();
    		GUI.gui.gameInfoConsole.appendInfo("Got "+areaInfluenceCount+" areaInfluences");
    		
    		for (int j = 0; j < areaInfluenceCount; j++) {
    			Priority priority = getPriority(packet);
        		BigInteger addCompleteAreaInfluenceId = BigInteger.valueOf(packet.getLong());
        		long timeBegin = packet.getLong();
        		long timeEnd = packet.getLong();
        		byte[] groupNameAddCompleteGroupNameBytes = new byte[packet.getInt()];
    			packet.get(groupNameAddCompleteGroupNameBytes);
    			String addCompleteGroupName = new String(groupNameAddCompleteGroupNameBytes); // name
    			byte[] roomBytes = new byte[packet.getInt()];
    			packet.get(roomBytes);
    			String room = new String(roomBytes); // name
    			byte[] creatorBytes = new byte[packet.getInt()];
    			packet.get(creatorBytes);
    			String creator = new String(creatorBytes); // name
    			int exclusiveInt = packet.getInt();
    			boolean exclusive;
    			if (exclusiveInt == 1) exclusive = true; else exclusive = false;
        		float addCompleteAreaInfluenceX = packet.getFloat(); 
        		float addCompleteAreaInfluenceY = packet.getFloat();
        		float addCompleteAreaInfluenceWidth = packet.getFloat(); 
        		float addCompleteAreaInfluenceHeight = packet.getFloat();
        		float addCompleteAreaInfluenceMX = packet.getFloat(); 
        		float addCompleteAreaInfluenceMY = packet.getFloat();
//    			int addCompleteAreaInfluenceCount = packet.getInt();
    			int attributeLength = packet.getInt();
    			float[] attributes = new float[attributeLength];
    			for(int i = 0; i< attributeLength; i++) {
    				attributes[i] = packet.getFloat();
    			}
    			
    			AreaInfluence areaInfluenceInit = new AreaInfluence(addCompleteAreaInfluenceId, timeBegin, timeEnd, new LocalObject(addCompleteAreaInfluenceId, "", room,  addCompleteAreaInfluenceX, addCompleteAreaInfluenceY, addCompleteAreaInfluenceWidth, addCompleteAreaInfluenceHeight, addCompleteAreaInfluenceMX, addCompleteAreaInfluenceMY, 0, 0),addCompleteGroupName, exclusive, attributes, priority, creator);
//    					Item.getItem(itemCode, addCompleteItemId, addCompleteItemName, addCompleteItemCount, 1, addCompleteItemX, addCompleteItemY, System.currentTimeMillis(), states);
        		if (AreaInfluenceManager.add(areaInfluenceInit)) {
        			AreaInfluenceManager.setMaxIDValue(maxID);
//        			System.out.println("Successfully added areaInfluence");
        		} else {
//        			System.out.println("Could not added areaInfluence");
        		}    		
    		}
    		break;}

    	case ADDAREAINFLUENCE: {
    		
//    		System.out.println("Got an areaInfluence");
			BigInteger maxID = BigInteger.valueOf(packet.getLong()); // 8 
			Priority priority = getPriority(packet);
    		BigInteger addCompleteAreaInfluenceId = BigInteger.valueOf(packet.getLong());
//    		GameWindow.gw.gameInfoConsole.appendInfo("Got an areaInfluence: "+addCompleteAreaInfluenceId.longValue());
//    		System.out.println("ID = "+addCompleteAreaInfluenceId.longValue());
    		long timeBegin = packet.getLong();
    		long timeEnd = packet.getLong();
    		byte[] groupNameAddCompleteGroupNameBytes = new byte[packet.getInt()];
			packet.get(groupNameAddCompleteGroupNameBytes);
			String addCompleteGroupName = new String(groupNameAddCompleteGroupNameBytes); // name
			byte[] roomBytes = new byte[packet.getInt()];
			packet.get(roomBytes);
			String room = new String(roomBytes); // name
			byte[] creatorBytes = new byte[packet.getInt()];
			packet.get(creatorBytes);
			String creator = new String(creatorBytes); // name
			int exclusiveInt = packet.getInt();
			boolean exclusive;
			if (exclusiveInt == 1) exclusive = true; else exclusive = false;
    		float addCompleteAreaInfluenceX = packet.getFloat(); 
    		float addCompleteAreaInfluenceY = packet.getFloat();
    		float addCompleteAreaInfluenceWidth = packet.getFloat(); 
    		float addCompleteAreaInfluenceHeight = packet.getFloat();
    		float addCompleteAreaInfluenceMX = packet.getFloat(); 
    		float addCompleteAreaInfluenceMY = packet.getFloat();
//    			int addCompleteAreaInfluenceCount = packet.getInt();
			int attributeLength = packet.getInt();
			float[] attributes = new float[attributeLength];
			for(int i = 0; i< attributeLength; i++) {
				attributes[i] = packet.getFloat();
			}
//			System.out.println("timeBegin == timeEnd : "+(timeBegin==timeEnd));
//			System.out.println("areaInfluence x = "+attributes[AttributeType.MX.ordinal()]+" width= "+addCompleteAreaInfluenceWidth);
			AreaInfluence areaInfluenceInit = new AreaInfluence(addCompleteAreaInfluenceId, timeBegin, timeEnd, new LocalObject(addCompleteAreaInfluenceId, "", room, addCompleteAreaInfluenceX, addCompleteAreaInfluenceY, addCompleteAreaInfluenceWidth, addCompleteAreaInfluenceHeight, addCompleteAreaInfluenceMX, addCompleteAreaInfluenceMY, 0, 0),addCompleteGroupName, exclusive, attributes, priority, creator);
//    					Item.getItem(itemCode, addCompleteItemId, addCompleteItemName, addCompleteItemCount, 1, addCompleteItemX, addCompleteItemY, System.currentTimeMillis(), states);
    		if (AreaInfluenceManager.add(areaInfluenceInit)) {
    			AreaInfluenceManager.setMaxIDValue(maxID);
//    			System.out.println("Successfully added areaInfluence");
    		} else {
//    			System.out.println("Could not added areaInfluence");
    		}    		

    		break;}
    	case INITGROWTHSPOTS: {
    		BigInteger maxID = BigInteger.valueOf(packet.getLong());
    		int growthSpotCount = packet.getInt();
    		GUI.gui.gameInfoConsole.appendInfo("Got "+growthSpotCount+" growthSpots");
    		
    		for (int j = 0; j < growthSpotCount; j++) {
        		BigInteger addCompleteGrowthSpotId = BigInteger.valueOf(packet.getLong());
        		int tileType = packet.getInt();
        		long timeBegin = packet.getLong();
        		long timeEnd = packet.getLong();
    			byte[] roomBytes = new byte[packet.getInt()];
    			packet.get(roomBytes);
    			String room = new String(roomBytes); // name
    			byte[] creatorBytes = new byte[packet.getInt()];
    			packet.get(creatorBytes);
    			String creator = new String(creatorBytes); // name
        		int addCompleteGrowthSpotX = packet.getInt(); 
        		int addCompleteGrowthSpotY = packet.getInt();
        		int addCompleteGrowthSpotMapX = packet.getInt(); 
        		int addCompleteGrowthSpotMapY = packet.getInt();
//    			int addCompleteAreaInfluenceCount = packet.getInt();
    			int attributeLength = packet.getInt();
    			float[] attributes = new float[attributeLength];
    			for(int i = 0; i< attributeLength; i++) {
    				attributes[i] = packet.getFloat();
    			}
    			
    			GrowthSpot growthSpotInit = new GrowthSpot(addCompleteGrowthSpotId, tileType,addCompleteGrowthSpotX, addCompleteGrowthSpotY, addCompleteGrowthSpotMapX, addCompleteGrowthSpotMapY, room,  timeBegin, timeEnd, attributes, creator);
//    					Item.getItem(itemCode, addCompleteItemId, addCompleteItemName, addCompleteItemCount, 1, addCompleteItemX, addCompleteItemY, System.currentTimeMillis(), states);
        		if (GrowthSpotManager.add(growthSpotInit)) {
        			GrowthSpotManager.setMaxIDValue(maxID);
//        			System.out.println("Successfully added areaInfluence");
        		} else {
//        			System.out.println("Could not added areaInfluence");
        		}    		
    		}
    		break;}

    	case ADDGROWTHSPOT: {
    		BigInteger maxID = BigInteger.valueOf(packet.getLong()); // 8 
    		BigInteger addCompleteGrowthSpotId = BigInteger.valueOf(packet.getLong());
//    		GameWindow.gw.gameInfoConsole.appendInfo("Got an areaInfluence: "+addCompleteAreaInfluenceId.longValue());
//    		System.out.println("ID = "+addCompleteAreaInfluenceId.longValue());
    		int tileType = packet.getInt();
    		long timeBegin = packet.getLong();
    		long timeEnd = packet.getLong();
			byte[] roomBytes = new byte[packet.getInt()];
			packet.get(roomBytes);
			String room = new String(roomBytes); // name
			byte[] creatorBytes = new byte[packet.getInt()];
			packet.get(creatorBytes);
			String creator = new String(creatorBytes); // name
    		int addCompleteGrowthSpotX = packet.getInt(); 
    		int addCompleteGrowthSpotY = packet.getInt();
    		int addCompleteGrowthSpotMapX = packet.getInt(); 
    		int addCompleteGrowthSpotMapY = packet.getInt();
//    			int addCompleteAreaInfluenceCount = packet.getInt();
			int attributeLength = packet.getInt();
			float[] attributes = new float[attributeLength];
			for(int i = 0; i< attributeLength; i++) {
				attributes[i] = packet.getFloat();
			}
//			System.out.println("timeBegin == timeEnd : "+(timeBegin==timeEnd));
//			System.out.println("areaInfluence x = "+attributes[AttributeType.MX.ordinal()]+" width= "+addCompleteAreaInfluenceWidth);
			GrowthSpot growthSpotInit = new GrowthSpot(addCompleteGrowthSpotId, tileType,addCompleteGrowthSpotX, addCompleteGrowthSpotY, addCompleteGrowthSpotMapX, addCompleteGrowthSpotMapY, room,  timeBegin, timeEnd, attributes, creator);
//    					Item.getItem(itemCode, addCompleteItemId, addCompleteItemName, addCompleteItemCount, 1, addCompleteItemX, addCompleteItemY, System.currentTimeMillis(), states);
    		if (GrowthSpotManager.add(growthSpotInit)) {
    			GrowthSpotManager.setMaxIDValue(maxID);
//    			System.out.println("Successfully added areaInfluence");
    		} else {
//    			System.out.println("Could not added areaInfluence");
    		}    		

    		break;}
    	case SENDCOMPLETEPLAYERAGENTS: {
    		BigInteger toPlayerID = BigInteger.valueOf(packet.getLong()); // me
    		byte[] nameBytes = new byte[packet.getInt()];
			packet.get(nameBytes);
			String namePlayer = new String(nameBytes); // name
		
			
			if (namePlayer.equals(GameWindow.gw.getPlayerName())) {
				//STOP - its my own playerAgentList
				// remark: i could also check for toPlayerID, that should be my own ID!!!
			} else {
			
	    		int playerAgentsLength = packet.getInt(); // 4;
	    		ArrayList<PlayerEntity> playerAgentsList = new ArrayList<PlayerEntity>();
	    		
	    		/** here come all the agent properties */
	    		PlayerEntity e;
	    		byte[] agentNameBytes;
	    		String agentName;
	    		byte[] roomBytes;
	    		String room;
	    		BigInteger id;
	    		byte[] tileSetNameBytes;
	    		String tileSetName;
	    		float x, y, width, height, mx, my;
	    		int attributeLength;
	    		float[] attributes = new float[AttributeType.values().length];
	    		EntityStates orientation;
            	EntityStates singleState;
            	EntityStates continuousState;
	    		
	    		/** get every playerAgent */
	    		for (int i = 0; i < playerAgentsLength; i++) {
	//        		buffer.putLong(id.longValue()); // 8
	        		id = BigInteger.valueOf(packet.getLong());
	//        		buffer.putInt(name.length()); // 4
	//            	buffer.put(name.getBytes()); // name.length
	            	agentNameBytes = new byte[packet.getInt()];
	    			packet.get(agentNameBytes);
	    			agentName = new String(agentNameBytes); // agentName
	    			roomBytes = new byte[packet.getInt()];
	    			packet.get(roomBytes);
	    			room = new String(roomBytes); // room
	    			
	//            	buffer.putInt(e.getTileSet().getName().length()); // 4
	//            	buffer.put(e.getTileSet().getName().getBytes()); // name.length
	            	tileSetNameBytes = new byte[packet.getInt()];
	    			packet.get(tileSetNameBytes);
	    			tileSetName = new String(tileSetNameBytes); // tileSetName
	    			orientation = getEntityStates(packet);
	    			singleState = getEntityStates(packet);
	    			continuousState = getEntityStates(packet);
	//            	buffer.putFloat(e.getX()); // 4
	            	x = packet.getFloat();
	//            	buffer.putFloat(e.getY()); // 4
	            	y = packet.getFloat();
	//            	buffer.putFloat(e.getWidth()); // 4
	            	width = packet.getFloat();
	//            	buffer.putFloat(e.getHeight()); // 4
	            	height = packet.getFloat();
	            	//buffer.putFloat(e.getMX()); // 4
	            	mx = packet.getFloat();
	//            	buffer.putFloat(e.getMY()); // 4
	            	my = packet.getFloat();
	//            	buffer.putInt(e.getAttributes().length);
	            	attributeLength = packet.getInt();
	            	if (attributeLength != attributes.length) {
	            		//ERROR!!!
	            		System.out.println("Wrong length of attributes!!!");
	            		break;
	            	} else {
	            		for ( int j = 0; j < attributeLength; j++) {
	//                		buffer.putFloat(e.getAttributes()[j]);
	                		attributes[j] = packet.getFloat();
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
				
//	    		/** add all playerAgents to the new agentList/ idList */
//	    		HashMap<BigInteger, PlayerEntity> agentList = new HashMap<BigInteger, PlayerEntity>();
//	    		ArrayList<BigInteger> idList = new ArrayList<BigInteger>();
//	    		for (int i = 0; i < playerAgentsList.size(); i++) {
//	    			BigInteger agentID = playerAgentsList.get(i).getId();
//	    			agentList.put(agentID, playerAgentsList.get(i));
//	    			idList.add(agentID);
//	    		}
	    		
//	    		/** configure the new/ updated playerAgentManager */
//	    		PlayerAgentManager playerAgentManager = new PlayerAgentManager(namePlayer);
//	    		playerAgentManager.setAgentList(agentList);
//	    		playerAgentManager.setIdList(idList);
	    		/** and add it to the PlayerAgentManagerManager */
	    		if (PlayerAgentManagerManager.contains(namePlayer)) {
	    			/** add each missing playerAgent */
	    			for (PlayerEntity playerAgentAdd : playerAgentsList) {
	    				if (PlayerAgentManagerManager.get(namePlayer).add(playerAgentAdd) ) {
	    					//OK, everything is fine!!
	    				} else {
	    					// something went wrong!
	    					System.out.println("Couldn't add the already existing playerAgent "+playerAgentAdd.getName()+" with ID="+playerAgentAdd.getId().longValue());
	    				}
	    			}
	    			
//	    			PlayerAgentManagerManager.overwrite(namePlayer, playerAgentManager);
	    		} else {
	    			/** create the missing playerAgentManager */
	    			HashMap<BigInteger, PlayerEntity> agentList = new HashMap<BigInteger, PlayerEntity>();
		    		ArrayList<BigInteger> idList = new ArrayList<BigInteger>();
		    		for (int i = 0; i < playerAgentsList.size(); i++) {
		    			BigInteger agentID = playerAgentsList.get(i).getId();
		    			agentList.put(agentID, playerAgentsList.get(i));
		    			idList.add(agentID);
		    		}
	    			PlayerAgentManager playerAgentManager = new PlayerAgentManager(namePlayer);
	    			playerAgentManager.setAgentList(agentList);
	    			playerAgentManager.setIdList(idList);
	    			PlayerAgentManagerManager.add(playerAgentManager);
	    		}
//	    		GameWindow.gw.gameInfoConsole.appendLine("Player "+namePlayer+" send "+ playerAgentsLength+" agents.");
			}
    		break;
    	}
    	case SENDUPDATEPLAYERAGENTS: {
    		BigInteger playerID = BigInteger.valueOf(packet.getLong());
    		byte[] nameBytes = new byte[packet.getInt()];
			packet.get(nameBytes);
			String namePlayer = new String(nameBytes); // name
			if (namePlayer.equals(GameWindow.gw.getPlayerName())) {
				//STOP - its my own playerAgentList
			} else {
			
	    		int playerAgentsLength = packet.getInt(); // 4;
	    		ArrayList<BigInteger> sendUnknownPlayerAgentIDs = new ArrayList<BigInteger>();
	    		ArrayList<BigInteger> deleteMissingPlayerAgents = new ArrayList<BigInteger>();
	    		/** only set the list to the old known list of agents if it was existent before */
	    		if ( PlayerAgentManagerManager.contains(namePlayer)) {
	    			deleteMissingPlayerAgents = new ArrayList<BigInteger>(PlayerAgentManagerManager.get(namePlayer).getIdList());
	    		}
	    		
	    		/** here come all the agent properties */
	    		BigInteger id;
	    		float x, y, width, height, mx, my;
	    		int attributeLength;
	    		float[] attributes = new float[AttributeType.values().length];
	    		EntityStates orientation;
            	EntityStates singleState;
            	EntityStates continuousState;
	    		
	    		/** get every playerAgent */
	    		for (int i = 0; i < playerAgentsLength; i++) {
	        		id = BigInteger.valueOf(packet.getLong());
	    			orientation = getEntityStates(packet);
	    			singleState = getEntityStates(packet);
	    			continuousState = getEntityStates(packet);
	            	x = packet.getFloat();
	            	y = packet.getFloat();
	            	width = packet.getFloat();
	            	height = packet.getFloat();
	            	mx = packet.getFloat();
	            	my = packet.getFloat();
	            	attributeLength = packet.getInt();
	            	if (attributeLength != attributes.length) {
	            		//ERROR!!!
	            		System.out.println("Wrong length of attributes!!!");
	            		break;
	            	} else {
	            		for ( int j = 0; j < attributeLength; j++) {
	                		attributes[j] = packet.getFloat();
	                	}
	            	}
	            	if ( PlayerAgentManagerManager.contains(namePlayer)) {
		            	if (PlayerAgentManagerManager.get(namePlayer).updatePlayerAgent(id, x, y, width, height, mx, my, orientation, singleState, continuousState, attributes)) {
		            		// successfully updated playerAgent
//		            		System.out.println("Updated playerAgent with ID="+id.longValue());
		            	} else {
		            		sendUnknownPlayerAgentIDs.add(id);
		            	}
		            	
		            	/** prepare the list of potentially deleted agents */
		            	if (deleteMissingPlayerAgents.remove(id))
		            	{
		            		// success
		            	} else {
		            		System.out.println("Error: ID is not in the deleMissingPlayersAgent-ArrayList");
		            	}
	            	} else {
	            		/** add slowly all ids of the playerAgentsList */
	            		sendUnknownPlayerAgentIDs.add(id);
	            	}
	    		}
	    		/** if there are unknown ids, get them from the sender */
	    		if (sendUnknownPlayerAgentIDs.size() > 0) {
	    			/** send a message back, that needs the missing agents */
	    			GameWindow.gw.send(ClientMessages.sendUnknownPlayerAgents(playerID, namePlayer, sendUnknownPlayerAgentIDs));
	    		}
	    		/** if there are missing Agents, delete them from the Manager */
	    		if (deleteMissingPlayerAgents.size() > 0) {
	    			for (BigInteger tempID: deleteMissingPlayerAgents) {
	    				PlayerAgentManagerManager.get(namePlayer).remove(tempID);
//	    				System.out.println("Deleted missing agent: "+tempID.longValue());	
	    			}
	    		}
	    			
//	    		GameWindow.gw.gameInfoConsole.appendLine("Player "+namePlayer+" send "+ playerAgentsLength+" agents.");
			}
    		break;
    	}
    	case ASKFORPLAYERAGENTS: {
//            buffer.putLong(playerAID.longValue()); // 8 Bytes 
    		BigInteger playerAID = BigInteger.valueOf(packet.getLong());
//            buffer.putLong(playerBID.longValue()); // 8 Bytes 
    		BigInteger playerBID = BigInteger.valueOf(packet.getLong());
//            buffer.putInt(idList.size()); // 4
    		int idListSize = packet.getInt();
    		ArrayList<BigInteger> idList = new ArrayList<BigInteger>();
            /** now put the items in the buffer */
            for (int i = 0; i<idListSize; i++) {
            	idList.add(BigInteger.valueOf(packet.getLong()));
//            	buffer.putLong(idList.get(i).longValue()); // 8
        	} 
//            GameWindow.gw.gameInfoConsole.appendInfo("Got request for "+idListSize+" Agents by player with ID="+playerAID.longValue());
            
            /** exceptions */
            if(playerBID.compareTo(GamePanel.gp.getPlayerEntity().getId()) != 0) {
            	// id is not matching
            	System.out.println("playerBID is not me!");
            } else {
            	// OK!
            	/** get all playerAgents and send them to playerA */
            	ArrayList<Agent> completeAgents = new ArrayList<Agent>();
            	for (BigInteger tempID : idList) {
            		if (AgentManager.get(tempID) != null) {
            			completeAgents.add(AgentManager.get(tempID));
            		}
            	}
            	
            	/** send the agents back to the asking player */
            	GameWindow.gw.send(ClientMessages.sendCompletePlayerAgents(playerAID, GameWindow.gw.getPlayerName(), completeAgents));
            }
            
    		break;
    	}
    	case SENDEMPTYPLAYERAGENTS: {
    		byte[] nameBytes = new byte[packet.getInt()];
			packet.get(nameBytes);
			String namePlayer = new String(nameBytes); // name
			if (namePlayer.equals(GameWindow.gw.getPlayerName())) {
				//STOP - its my own playerAgentList
			} else {
				PlayerAgentManagerManager.empty(namePlayer);
			}
    		break;
    	}
    	case READY_FOR_NEXT_TEXTURE_PACKET: {
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
    	}
    	case SEND_AVAILABLE_TEXTURES: {
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
    		
    		break; }
    	case SEND_MAP: {
    		/** playerName */
    		byte[] nameBytes_player_sendMap = new byte[packet.getInt()];
			packet.get(nameBytes_player_sendMap);
			String name_player_sendMap = new String(nameBytes_player_sendMap); // name
			byte[] roomBytes = new byte[packet.getInt()];
			packet.get(roomBytes);
			String room = new String(roomBytes); // name
			int sizeOfMap = packet.getInt(); // 4 
    		byte[] mapFile = new byte[sizeOfMap];
    		packet.get(mapFile);
    		byte[] fileNameBytes_player_sendMap = new byte[packet.getInt()];
			packet.get(fileNameBytes_player_sendMap);
			String fileName_player_sendMap = new String(fileNameBytes_player_sendMap); // name
			int sizeOfSendList = packet.getInt(); // 4 
			
			GUI.gui.gameInfoConsole.appendInfo("Remaining Map-Objects to download: "+sizeOfSendList);
			
			/** only reset Maps if we are not the author of the file */
			if (!name_player_sendMap.equals(GameWindow.gw.getPlayerName())) {
//				GameWindow.gw.gameInfoConsole.appendInfo("Saving the map of "+name_player_sendMap);
				
				FileOutputStream stream;
				/** save the actualized map */
				try {
					boolean createMapSubFolderSccess = (new File(GameStates.standardMapFolder+name_player_sendMap+"/"+room)).mkdirs();
				    if (!createMapSubFolderSccess) {
				         // Directory creation failed
				    }
					stream = new FileOutputStream(GameStates.standardMapFolder+name_player_sendMap+"/"+room+"/"+fileName_player_sendMap);
//					GameWindow.gw.gameInfoConsole.appendInfo(GameStates.standardMapFolder+name_player_sendMap+"/"+room+"/"+fileName_player_sendMap);
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
				
				/** if the sending player is an active mapMaster */
				if (GameWindow.gw.getMapMasterName().equals(name_player_sendMap) && room.equals(GamePanel.gp.getPlayerEntity().getRoom())) {
					if (fileName_player_sendMap.startsWith("cobble")) {
						paintLayer = "cobble";
						GameWindow.gw.getMapManagers().get(paintLayer).remove(p_sendMap);
						GameWindow.gw.getMapManagers().get(paintLayer).putMapFileName(p_sendMap, GameStates.standardMapFolder+name_player_sendMap+"/"+room+"/"+fileName_player_sendMap);
					} else if (fileName_player_sendMap.startsWith("grass")) {
						paintLayer = "grass";
						GameWindow.gw.getMapManagers().get(paintLayer).remove(p_sendMap);
						GameWindow.gw.getMapManagers().get(paintLayer).putMapFileName(p_sendMap, GameStates.standardMapFolder+name_player_sendMap+"/"+room+"/"+fileName_player_sendMap);
					} else if (fileName_player_sendMap.startsWith("snow")) {
						paintLayer = "snow";
						GameWindow.gw.getMapManagers().get(paintLayer).remove(p_sendMap);
						GameWindow.gw.getMapManagers().get(paintLayer).putMapFileName(p_sendMap, GameStates.standardMapFolder+name_player_sendMap+"/"+room+"/"+fileName_player_sendMap);
					} else if (fileName_player_sendMap.startsWith("hole")) {
						paintLayer = "hole";
						GameWindow.gw.getMapManagers().get(paintLayer).remove(p_sendMap);
						GameWindow.gw.getMapManagers().get(paintLayer).putMapFileName(p_sendMap, GameStates.standardMapFolder+name_player_sendMap+"/"+room+"/"+fileName_player_sendMap);
					
					} else if (fileName_player_sendMap.startsWith("tree1")) {
						paintLayer = "tree1";
						GameWindow.gw.getObjectMapManagers().get(paintLayer).remove(p_sendMap);
						GameWindow.gw.getObjectMapManagers().get(paintLayer).putMapFileName(p_sendMap, GameStates.standardMapFolder+name_player_sendMap+"/"+room+"/"+fileName_player_sendMap);
					} else if (fileName_player_sendMap.startsWith("tree2")) {
						paintLayer = "tree2";
						GameWindow.gw.getObjectMapManagers().get(paintLayer).remove(p_sendMap);
						GameWindow.gw.getObjectMapManagers().get(paintLayer).putMapFileName(p_sendMap, GameStates.standardMapFolder+name_player_sendMap+"/"+room+"/"+fileName_player_sendMap);
					} else if (fileName_player_sendMap.startsWith("overlayTree1")) {
						paintLayer = "overlayTree1";
						GameWindow.gw.getObjectMapManagers().get(paintLayer).remove(p_sendMap);
						GameWindow.gw.getObjectMapManagers().get(paintLayer).putMapFileName(p_sendMap, GameStates.standardMapFolder+name_player_sendMap+"/"+room+"/"+fileName_player_sendMap);
					} else if (fileName_player_sendMap.startsWith("overlayTree2")) {
						paintLayer = "overlayTree2";
						GameWindow.gw.getObjectMapManagers().get(paintLayer).remove(p_sendMap);
						GameWindow.gw.getObjectMapManagers().get(paintLayer).putMapFileName(p_sendMap, GameStates.standardMapFolder+name_player_sendMap+"/"+room+"/"+fileName_player_sendMap);
					} else if (fileName_player_sendMap.startsWith("underground")) {
						paintLayer = "underground";
						GameWindow.gw.getUndergroundMapManagers().get(paintLayer).remove(p_sendMap);
						GameWindow.gw.getUndergroundMapManagers().get(paintLayer).putMapFileName(p_sendMap, GameStates.standardMapFolder+name_player_sendMap+"/"+room+"/"+fileName_player_sendMap);
					} else {
						GUI.gui.gameInfoConsole.appendInfo("Didn't find map: "+fileName_player_sendMap);
					}
				} else {
					// this is not an authorized MapMaster!
				}
			} else {
				// i got my own mapFile!
			}
			
    		break; }
    	case DELETE_MAPOBJECT: {
    		int localX = packet.getInt();
			int localY = packet.getInt();
			
//			System.out.println(  "localX = "+localX + " localY = "+localY);
			int mapX = packet.getInt();
			int mapY = packet.getInt();
			
//			BigInteger gameMasterID = BigInteger.valueOf(packet.getLong());
			
			byte[] roomName_Bytes = new byte[packet.getInt()];
			packet.get(roomName_Bytes);
			String roomName = new String(roomName_Bytes); // name
			
//			byte[] gameMasterName_Bytes = new byte[packet.getInt()];
//			packet.get(gameMasterName_Bytes);
//			String gameMasterName = new String(gameMasterName_Bytes); // name
			
			byte[] objectMapName_Bytes = new byte[packet.getInt()];
			packet.get(objectMapName_Bytes);
			String objectMapName = new String(objectMapName_Bytes); // name
//			System.out.println(  "  objectMapName = "+objectMapName);
			byte[] objectOverlayMapName_Bytes = new byte[packet.getInt()];
			packet.get(objectOverlayMapName_Bytes);
			String objectOverlayMapName = new String(objectOverlayMapName_Bytes); // name
			
			int valueLength = packet.getInt();
			int[] values = new int[valueLength];
			for (int i = 0; i< valueLength; i++) {
				values[i] = packet.getInt();
			}
				
			InteractionTile iTile = new InteractionTile(new WorldLatticePosition(new Point(mapX, mapY), localX, localY));
			iTile.setValues(values);
			iTile.getPosition().setRoom(roomName);
			
			/** if i'm the mapmaster just take the standard routine */
			if (GameWindow.gw.isMapMaster() && GamePanel.gp.getPlayerEntity().getRoom().equals(roomName)) {
				ObjectMapManager objectMapManager = GameWindow.gw.getObjectMapManagers().get(objectMapName);
				ObjectMapManager overlayMapManager = GameWindow.gw.getObjectMapManagers().get(objectOverlayMapName);
				LocalMap localMap = objectMapManager.get(new Point(mapX, mapY));
				LocalMap localOverlayMap = overlayMapManager.get(new Point(mapX, mapY));
				
				boolean distantMapCoordinates = false;
				if (localMap == null) {
					// get the map from filesystem
					objectMapManager.addStayList(new Point(mapX, mapY));
					objectMapManager.loadFromFileSystem(GameWindow.gw.getPlayerName(), mapX, mapY);
					localMap = objectMapManager.get(new Point(mapX, mapY));
					distantMapCoordinates = true;
				}
				if (localOverlayMap == null ) {
					// get the map from filesystem
					overlayMapManager.addStayList(new Point(mapX, mapY));
					overlayMapManager.loadFromFileSystem(GameWindow.gw.getPlayerName(), mapX, mapY);
					localOverlayMap = overlayMapManager.get(new Point(mapX, mapY));
					distantMapCoordinates = true;
				}
				int localWidth = GameStates.mapWidth * GameStates.mapTileSetWidth;
				int localHeight = GameStates.mapHeight * GameStates.mapTileSetHeight;
				
				if (distantMapCoordinates == true) {
					if ((localX == 0) && (localY == 0)) {
						// ul
						Point p2 = new Point(mapX-localWidth, mapY-localHeight);
						objectMapManager.addStayList(p2);
						overlayMapManager.addStayList(p2);
						objectMapManager.loadFromFileSystem(GameWindow.gw.getPlayerName(),p2.x, p2.y);
						overlayMapManager.loadFromFileSystem(GameWindow.gw.getPlayerName(),p2.x, p2.y);
					} 
					if ( (localX == GameStates.mapWidth-1) && (localY == 0)) {
						// ur
						Point p2 = new Point(mapX+localWidth, mapY-localHeight);
						objectMapManager.addStayList(p2);
						overlayMapManager.addStayList(p2);
						objectMapManager.loadFromFileSystem(GameWindow.gw.getPlayerName(),p2.x, p2.y);
						overlayMapManager.loadFromFileSystem(GameWindow.gw.getPlayerName(),p2.x, p2.y);
					}
					if (localY == 0) {
						// u
						Point p2 = new Point(mapX, mapY-localHeight);
						objectMapManager.addStayList(p2);
						overlayMapManager.addStayList(p2);
						objectMapManager.loadFromFileSystem(GameWindow.gw.getPlayerName(),p2.x, p2.y);
						overlayMapManager.loadFromFileSystem(GameWindow.gw.getPlayerName(),p2.x, p2.y);
					} 
					if ( (localX == 0) && (localY == GameStates.mapHeight-1)) {
						// dl
						// no need to create because trees dont have a dl tile
					}
					if ( (localX == 0)) {
						// l
						Point p2 = new Point(mapX-localWidth, mapY);
						objectMapManager.addStayList(p2);
						overlayMapManager.addStayList(p2);
						objectMapManager.loadFromFileSystem(GameWindow.gw.getPlayerName(),p2.x, p2.y);
						overlayMapManager.loadFromFileSystem(GameWindow.gw.getPlayerName(),p2.x, p2.y);
					}
					if ( (localX == GameStates.mapWidth-1) && (localY == GameStates.mapHeight-1) ) {
						// dr
						// no need to create because trees dont have a dr tile
					}
					if ( (localX == GameStates.mapWidth-1) ) {
						// r
						Point p2 = new Point(mapX+localWidth, mapY);
						objectMapManager.addStayList(p2);
						overlayMapManager.addStayList(p2);
						objectMapManager.loadFromFileSystem(GameWindow.gw.getPlayerName(),p2.x, p2.y);
						overlayMapManager.loadFromFileSystem(GameWindow.gw.getPlayerName(),p2.x, p2.y);
					}
					if (localY == GameStates.mapHeight-1) {
						// d
						// no need to create because trees dont have a d tile
					}
				}
	
//				if ( localX % 2 == 0 && localY % 2 == 0) {				
	
//				if (localX % 2 != 0) {
//					
//				}
				
				
				
					/** determine here the relevant paintLayer */
					int paintType = LocalMap.getPaintType(localMap.getLocalMap()[localX][localY]);
					
//					System.out.println("paintType = "+paintType);
					ITEMCODE itemCode = LocalMap.getItemCode(paintType);
					
					if (!LocalMap.isFluid(paintType)) {
						int shiftX = paintType - localMap.getLocalMap()[localX][localY];
						if (shiftX == 0) {
							// OK
						} else if (shiftX == -1) {
							// right side of center
							localX--;
						} else if (shiftX == 1) {
							// left side of center
							localX++;
						} else if (shiftX == GameStates.tileSetWidth-1) {
							// upper right
							localX--;
							localY++;
						} else if (shiftX == GameStates.tileSetWidth) {
							// upper tile
							localY++;
						} else if (shiftX == GameStates.tileSetWidth+1) {
							// upper left tile
							localX++;
							localY++;
						} else if (shiftX == -GameStates.tileSetWidth-1) {
							// upper right
							localX--;
							localY--;
						} else if (shiftX == -GameStates.tileSetWidth) {
							// upper tile
							localY--;
						} else if (shiftX == -GameStates.tileSetWidth+1) {
							// upper left tile
							localX++;
							localY--;
						}
					}
					
					if (localX >= 0 && localX <= GameStates.mapWidth-1 && localY >=0 && localY <=GameStates.mapHeight-1) {
					
						if (localMap.getLocalMap()[localX][localY] != 0) {
							BigInteger itemId = ItemManager.getMaxIDValue().add(GamePanel.gp.getPlayerEntity().getId());
							ItemManager.setMaxIDValue(itemId);
		//					Item dropItem = Item.getItem(ITEMCODE.WOOD, itemId, "wood",(int) (Math.random()*20+20), 0, mapX+localX*GameStates.mapTileSetWidth+(int) (Math.random()*(GameStates.mapTileSetWidth-GameStates.itemTileWidth)), mapY+localY*GameStates.mapTileSetHeight +(int) (Math.random()*(GameStates.mapTileSetHeight-GameStates.itemTileHeight)), System.currentTimeMillis(), new float[0]);
							TileSize tileSize = LocalMap.getTileSize(paintType);
							
							int shiftXValueFor2x2 = 0;
							if (LocalMap.getTileDimensions(paintType) == TileDimensions.RowCol2x2) shiftXValueFor2x2 = GameStates.mapTileSetWidth/2;
							Item dropItem = ItemFactoryCollection.createItem(itemCode,roomName, tileSize, itemId, mapX+shiftXValueFor2x2+localX*GameStates.mapTileSetWidth+(int) (Math.random()*(GameStates.mapTileSetWidth-GameStates.itemTileWidth)), mapY+localY*GameStates.mapTileSetHeight +(int) (Math.random()*(GameStates.mapTileSetHeight-GameStates.itemTileHeight)));
//							System.out.println("created Item: "+itemId);
							
							/** send the complete Item to all players of the channel */
							if (GameWindow.gw.isLoggedIn() && GamePanel.gp.isInitializedPlayer() && dropItem != null) {
								/** first send to server for the itemList */
								GameWindow.gw.send(ClientMessages.addItem(dropItem.getId(), dropItem.getItemCode(), dropItem.getCount(), dropItem.getCapacity(), dropItem.getEntity().getX(), dropItem.getEntity().getY(), dropItem.getEntity().getMX(), dropItem.getEntity().getMY(), dropItem.getName(), dropItem.getComment(), dropItem.getRoom(), dropItem.getEntity().getTileSet().getFileName(), dropItem.getEntity().getName(), dropItem.getAttributes()));
								for (String channelName : GameWindow.gw.getSpaceChannels().values()) {
									ClientChannel channel = GameWindow.gw.getChannelByName(channelName);
									try {
										channel.send(ClientMessages.addCompleteItem(dropItem.getItemCode(), dropItem.getId(), dropItem.getName(), dropItem.getComment(), dropItem.getRoom(), dropItem.getEntity().getX(), dropItem.getEntity().getY(), dropItem.getCount(), dropItem.getAttributes()));
									} catch (IOException e) {
										e.printStackTrace();
									}	
								}
							}
							
							/** check if there is a light source at this light tile */
							if (LocalMap.isLighning(localMap.getLocalMap()[localX][localY])) {
	//							System.out.println("painted light tile ");
								BigInteger removeID = LightManager.whichLightAtPosition(mapX+localX*GameStates.mapTileSetWidth, mapY+localY*GameStates.mapTileSetHeight);
								LightManager.remove(removeID);
								EntityManager.remove(removeID);
//								System.out.println("removed light "+removeID);
//								for (Light light: LightManager.lightList.values() ) {
//									System.out.println("light at "+light.getLocation().x+"|"+light.getLocation().y);
//								}
							}
							
							if (LocalMap.isFluid(paintType)) {
								
//								System.out.println("deleting fluid");
//								Point p = new Point(localX,localY);
								/** only allow deletions for the actual PaintType */
								if (LocalMap.getPaintType(localMap.getTile(localX, localY)) == paintType) {
//									System.out.println("correct painttype -> delete");
									localMap.setTile(localX, localY, 0);
									objectMapManager.adjustSurrounding(localMap, localX, localY, paintType, TileDimensions.Fluid);
									
									localOverlayMap.setTile(localX, localY, 0);
									overlayMapManager.adjustSurrounding(localOverlayMap, localX, localY, paintType-4*GameStates.tileSetWidth, TileDimensions.Fluid);
			
									/** define the relevant checkPositions */
									int checkPositionX = localX;
									if ( checkPositionX % 2 != 0 ) {
										if (checkPositionX < GameStates.mapWidth-1)
											checkPositionX++;
										else checkPositionX--;
									}
									int checkPositionY = localY;
									if ( checkPositionY % 2 != 0 ) {
										if (checkPositionY < GameStates.mapHeight-1)
											checkPositionY++;
										else checkPositionY--;
									}
											
									/** now check all six 2x2 fields, that correspond to the actual localX|localY and delete the 1 if there is no other paintTily */
									if (checkPositionX == 0 || checkPositionY == 0) {
										/** do nothing on the border */
									} else {
										if ((LocalMap.isEmptyOrAddon(localMap.getTile(checkPositionX-1, checkPositionY)) ) && (LocalMap.isEmptyOrAddon(localMap.getTile(checkPositionX, checkPositionY-1))) && (LocalMap.isEmptyOrAddon(localMap.getTile(checkPositionX-1, checkPositionY-1))) && (localMap.getTile(checkPositionX, checkPositionY) <= 1)) {
											localMap.setTile(checkPositionX, checkPositionY, 0);
											localOverlayMap.setTile(checkPositionX, checkPositionY, 0);
										} else if ((checkPositionX == localX && checkPositionY == localY) && (LocalMap.isEmptyOrAddon(localMap.getTile(checkPositionX, checkPositionY-1))) ) {
											/** only set as 1, if there is a fluid Tile nearby */
											if( LocalMap.isFluid(localMap.getTile(checkPositionX-1, checkPositionY)) || LocalMap.isFluid(localMap.getTile(checkPositionX-1, checkPositionY-1)) )		
												localMap.setTile(checkPositionX, checkPositionY, 1);
											localOverlayMap.setTile(checkPositionX, checkPositionY, 0);
										}
										
										if (checkPositionX == localX && checkPositionY == localY+1 && (LocalMap.isEmptyOrAddon(localMap.getTile(checkPositionX, checkPositionY)) ))  {
											if( LocalMap.isFluid(localMap.getTile(checkPositionX-1, checkPositionY)) || LocalMap.isFluid(localMap.getTile(checkPositionX-1, checkPositionY-1)) ) {	
												localMap.setTile(checkPositionX, checkPositionY, 1);
												localOverlayMap.setTile(checkPositionX, checkPositionY, 0);
											}
											if ((localMap.getTile(checkPositionX-1, checkPositionY) == 0 || LocalMap.isAddon(localMap.getTile(checkPositionX-1, checkPositionY)) ) && (localMap.getTile(checkPositionX, checkPositionY-1) == 0 || LocalMap.isAddon(localMap.getTile(checkPositionX, checkPositionY-1))) && (localMap.getTile(checkPositionX-1, checkPositionY-1) == 0 || LocalMap.isAddon(localMap.getTile(checkPositionX-1, checkPositionY-1))) && (localMap.getTile(checkPositionX, checkPositionY) <= 1)) {
												localMap.setTile(checkPositionX, checkPositionY, 0);
												localOverlayMap.setTile(checkPositionX, checkPositionY, 0);
											}
										}
									}
								} else {
//									System.out.println("painttype does not match: "+LocalMap.getPaintType(localMap.getTile(localX, localY)) + " vs correct: "+paintType);
								}
								
//								GamePanel.gp.paintTiles(p, "tree", LocalMap.getPaintType(paintType), true);
//								System.out.println("objectMapName:"+objectMapName +" paintType:"+paintType);
							} else {
								TileDimensions tileDimensions = LocalMap.getTileDimensions(paintType);
								
								localMap.setUl(localX, localY, 0);
								localMap.setUr(localX, localY, 0);
								localMap.setDl(localX, localY, 0);
								localMap.setDr(localX, localY, 0);
								localMap.setIdByCornersObject(localX, localY, paintType, tileDimensions);
								objectMapManager.deleteSurrounding(localMap, localX, localY, paintType, tileDimensions);
				
								localOverlayMap.setUl(localX, localY, 0);
								localOverlayMap.setUr(localX, localY, 0);
								localOverlayMap.setDl(localX, localY, 0);
								localOverlayMap.setDr(localX, localY, 0);
								localOverlayMap.setIdByCornersObject(localX, localY, paintType-GameStates.mapTileSetWidth*2, tileDimensions);
								overlayMapManager.deleteSurrounding(localOverlayMap, localX, localY, paintType-GameStates.mapTileSetWidth*2, tileDimensions);
							}
								
							/** register lattice Point for save - and send procedure */
							objectMapManager.addChangedList(new Point(mapX, mapY));
							overlayMapManager.addChangedList(new Point(mapX, mapY));
							/** now check the corners */
		//					if (distantMapCoordinates == false) {
								if ((localX == 0) && (localY == 0)) {
									// ul
									Point p2 = new Point(mapX-localWidth, mapY-localHeight);
									objectMapManager.addChangedList(p2);
									overlayMapManager.addChangedList(p2);
								} 
								if ( (localX == GameStates.mapWidth-1) && (localY == 0)) {
									// ur
									Point p2 = new Point(mapX+localWidth, mapY-localHeight);
									objectMapManager.addChangedList(p2);
									overlayMapManager.addChangedList(p2);
								}
								if (localY == 0) {
									// u
									Point p2 = new Point(mapX, mapY-localHeight);
									objectMapManager.addChangedList(p2);
									overlayMapManager.addChangedList(p2);
								} 
								if ( (localX == 0) && (localY == GameStates.mapHeight-1)) {
									// dl
									// no need to create because trees dont have a dl tile
								}
								if ( (localX == 0)) {
									// l
									Point p2 = new Point(mapX-localWidth, mapY);
									objectMapManager.addChangedList(p2);
									overlayMapManager.addChangedList(p2);
								}
								if ( (localX == GameStates.mapWidth-1) && (localY == GameStates.mapHeight-1) ) {
									// dr
									// no need to create because trees dont have a dr tile
								}
								if ( (localX == GameStates.mapWidth-1) ) {
									// r
									Point p2 = new Point(mapX+localWidth, mapY);
									objectMapManager.addChangedList(p2);
									overlayMapManager.addChangedList(p2);
								}
								if (localY == GameStates.mapHeight-1) {
									// d
									// no need to create because trees dont have a d tile
								}
		//					}
						}
					} else {
						// error: wrong value for localX, localY
					}
//				}
				
			} else {
				/** here the player is not the mapMaster (he is in another room or  at another player */
				
			}
			
//			this.getRoom().deleteMapObject(gmID, iTile);
	    	
			break;
    	}
    	case CHAT: {
    		byte[] chatStringBytes = new byte[packet.getInt()];
			packet.get(chatStringBytes);
			String chatString = new String(chatStringBytes); // name
    		GUI.gui.appendOutput(chatString);
    		break; }
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
        	System.out.println("Unknown priority value: " + priorityByte);
//            logger.severe("Unknown op value: " + opbyte);
            return null;
        }
        Priority priority = Priority.values()[priorityByte];
        
        return priority;
    }
    
    private static OBJECTCODE getObjectCode(ByteBuffer packet) 
    {
        byte objectByte = packet.get();
        if ((objectByte < 0) || (objectByte > OBJECTCODE.values().length - 1)) {
        	//TODO: exception is better
        	System.out.println("Unknown es value: " + objectByte);
//            logger.severe("Unknown op value: " + opbyte);
            return null;
        }
        OBJECTCODE objectCode = OBJECTCODE.values()[objectByte];
        
        return objectCode;
    }
    
//    public static void containerUpdateSend(Container tempInventory) {
//    	BigInteger itemID;
//    	Item tempItem;
////    	System.out.println("sending updates");
//    	for (int i = 0; i < tempInventory.getHeight(); i++) {
//    		for (int j = 0; j < tempInventory.getWidth(); j++) {
//    			itemID = tempInventory.getContainerArray()[i][j];
//    			/** if different ids */
////    			System.out.println("ids: "+itemID+" "+GamePanel.gp.getPlayerEntity().getInventory().getContainerArray()[i][j]);
//    			if ( (!itemID.equals(GamePanel.gp.getPlayerEntity().getInventory().getContainerArray()[i][j])) ) {
//    				/** here we have to delete an item from a field */
//    				if (GamePanel.gp.getPlayerEntity().getInventory().getContainerArray()[i][j].equals(BigInteger.valueOf(-1))) {
//    					GameWindow.gw.send(ClientMessages.clearContainerPosition(i, j));
////    					System.out.println("clear position "+i+"/"+j);
//    				}
//    				/** here we got a new item on an empty field */
//    				if (tempInventory.getContainerArray()[i][j].equals(BigInteger.valueOf(-1))) {
//    					tempItem = GamePanel.gp.getPlayerEntity().getInventory().getItemList().get(GamePanel.gp.getPlayerEntity().getInventory().getContainerArray()[i][j]);
//    					GameWindow.gw.send(ClientMessages.addItemToContainer(tempItem.getId(), tempItem.getItemCode(), tempItem.getCount(), tempItem.getCapacity(), tempItem.getEntity().getX(), tempItem.getEntity().getY(), tempItem.getEntity().getMX(), tempItem.getEntity().getMY(), tempItem.getName(), tempItem.getEntity().getTileSet().getFileName(), tempItem.getEntity().getName(), tempItem.getStates(), i, j));
////    					System.out.println("new item on position "+i+"/"+j +" ID="+tempItem.getId() );
//    				}
//    				
//    			} else {
//    				/** the id is the same, but... */
//    				if (!itemID.equals(BigInteger.valueOf(-1))) {
//	    				/** if different count */
////    					System.out.println("old:"+tempInventory.getItemList().get(itemID).getCount()+" new:"+GamePanel.gp.getPlayerEntity().getInventory().getItemList().get(itemID).getCount());
//	    				if (tempInventory.getItemList().get(itemID).getCount() != GamePanel.gp.getPlayerEntity().getInventory().getItemList().get(itemID).getCount()) {
//	    					tempItem = GamePanel.gp.getPlayerEntity().getInventory().getItemList().get(GamePanel.gp.getPlayerEntity().getInventory().getContainerArray()[i][j]);
//	    					GameWindow.gw.send(ClientMessages.addItemToContainer(tempItem.getId(), tempItem.getItemCode(), tempItem.getCount(), tempItem.getCapacity(), tempItem.getEntity().getX(), tempItem.getEntity().getY(), tempItem.getEntity().getMX(), tempItem.getEntity().getMY(), tempItem.getName(), tempItem.getEntity().getTileSet().getFileName(), tempItem.getEntity().getName(), tempItem.getStates(), i, j));
////	    					System.out.println("item count changed on position "+i+"/"+j +" ID="+tempItem.getId()+" count="+tempItem.getCount() );
//	    				}
//    				}
//    			}
//    			
//    		}
//    	}
//    }
    	
    	
	 public static void containerUpdateSendBrutal(Container inventory) {
    	BigInteger itemID;
    	Item tempItem;
//    	    	System.out.println("sending updates");
    	for (int i = 0; i < inventory.getHeight(); i++) {
    		for (int j = 0; j < inventory.getWidth(); j++) {
    			itemID = inventory.getContainerArray()[i][j];
    			if (itemID.equals(BigInteger.valueOf(-1))) {
    				GameWindow.gw.send(ClientMessages.clearContainerPosition(inventory.getContainerType(), i, j));
    			} else {
    				tempItem = inventory.getItemList().get(inventory.getContainerArray()[i][j]);
					GameWindow.gw.send(ClientMessages.addItemToContainer(inventory.getContainerType(), tempItem.getId(), tempItem.getItemCode(), tempItem.getCount(), tempItem.getCapacity(), tempItem.getEntity().getX(), tempItem.getEntity().getY(), tempItem.getEntity().getMX(), tempItem.getEntity().getMY(), tempItem.getName(), tempItem.getComment(), tempItem.getRoom(), tempItem.getEntity().getTileSet().getFileName(), tempItem.getEntity().getName(), tempItem.getAttributes(), i, j));
    			}    	    			
    		}
    	}
	 }
}

