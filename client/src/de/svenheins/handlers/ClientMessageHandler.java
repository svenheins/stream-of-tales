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

import com.sun.sgs.client.ClientChannel;

import de.svenheins.main.EntityStates;
import de.svenheins.main.GamePanel;
import de.svenheins.main.GameStates;
import de.svenheins.main.GameWindow;
import de.svenheins.main.LoadingStates;
import de.svenheins.main.TileDimensions;
import de.svenheins.main.gui.Button;
import de.svenheins.main.gui.PlayerListGUI;
import de.svenheins.main.gui.PlayerListGUIManager;
import de.svenheins.managers.ClientTextureManager;
import de.svenheins.managers.EntityManager;
import de.svenheins.managers.ItemManager;
import de.svenheins.managers.ObjectMapManager;
import de.svenheins.managers.PlayerManager;
import de.svenheins.managers.SpaceManager;
import de.svenheins.managers.TileSetManager;
//import de.svenheins.managers.TextureManager;
import de.svenheins.messages.ClientMessages;
import de.svenheins.messages.ITEMCODE;
import de.svenheins.messages.OBJECTCODE;
import de.svenheins.messages.OPCODE;
import de.svenheins.objects.Entity;
import de.svenheins.objects.InteractionTile;
import de.svenheins.objects.LocalMap;
import de.svenheins.objects.PlayerEntity;
import de.svenheins.objects.Space;
import de.svenheins.objects.TileSet;
import de.svenheins.objects.WorldLatticePosition;
import de.svenheins.objects.items.Container;
import de.svenheins.objects.items.Item;

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
    			id_entity = BigInteger.valueOf(packet.getLong()); // ID
    			byte[] nameBytes = new byte[packet.getInt()];
    			packet.get(nameBytes);
    			name_entity = new String(nameBytes); // name
    			tileEntity = new TileSet(name_entity, name_entity, 50, 50);
    			entityList.add(new Entity(tileEntity,name_entity, id_entity, 0,0,GameStates.animationDelay));
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
//    				System.out.println(name_player +" VS "+ GameWindow.gw.getPlayerName());
    				tile = new TileSet(name_player_TileSet_FileName, name_player_TileSet, (int) spriteWidth, (int) spriteHeight);
    				PlayerEntity playerEntity = new PlayerEntity(tile,name_player, id_player, 0,0, animationDelay);
    				playerList.add(playerEntity);
    				
    				/** add PlayerButton */
    				TileSet tileSet = new TileSet("tilesets/buttons/undergroundGrassButton.png", "undergroundGrassButton", 32, 32);
    				PlayerListGUI playerListGUI = PlayerListGUIManager.get("playerList");
    				Button newPlayerButton = new Button(tileSet, name_player_TileSet, id_player, 0 , 0, animationDelay, name_player, 0, "");
    				playerListGUI.add(newPlayerButton);
    				
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
    		
    		BigInteger maxItemID = BigInteger.valueOf(packet.getLong());
    		
    		TileSet tileSet = new TileSet(tilePathName, tileName, GameStates.playerTileWidth, GameStates.playerTileHeight);
    		PlayerEntity playerEntity = new PlayerEntity(tileSet, GameWindow.gw.getPlayerName(), myId, x, y, GameStates.animationDelay);
    		playerEntity.setGroupName(groupName);
    		playerEntity.setFirstServerLogin(firstServerLogin);
    		playerEntity.setExperience(experience);
    		playerEntity.setCountry(country);
    		playerEntity.setMovement(mx, my);
    		playerEntity.setX(x);
    		playerEntity.setY(y);
    		
    		ItemManager.setMaxIDValue(maxItemID);
    		GamePanel.gp.setPlayerEntity(playerEntity);
    		GamePanel.gp.initContainers();
    		
//    		System.out.println("OK, initme complete!"+x);
    		GamePanel.gp.setInitializedPlayer(true);
    		
    		/** update GameModus if necessary */
    		GameWindow.gw.setLoadingStates(LoadingStates.ME, 100);
    		GameWindow.gw.updateGameModus();
    		
    		break;
    		
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
    					itemEntity_SC = new Entity(new TileSet(entityTileSetFileName_SC, entityTileSetName_SC, Item.tileSetX, Item.tileSetY, Item.tileSetWidth, Item.tileSetHeight), entityName_SC, entityID_SC, itemX_SC, itemY_SC, GameStates.animationDelayItems);
    					item_SC = Item.getItem(itemCode_SC,containerArray_SC[i][j] , itemName_SC, count_SC, capacity_SC, itemEntity_SC.getX(), itemEntity_SC.getY(), creationTime_SC, itemStates_SC);
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
    				
    				break;
    			case CONTAINER_USE:
    				GamePanel.gp.getPlayerEntity().setInventoryUse(inventory_SC);
    				break;
				default: ;
    			}
    		}
        	
//    		HIER WEITERMACHEN!!!
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
			itemEntity_SC = new Entity(new TileSet(entityTileSetFileName_SC, entityTileSetName_SC, Item.tileSetX, Item.tileSetY, Item.tileSetWidth, Item.tileSetHeight), entityName_SC, entityID_SC, itemX_SC, itemY_SC, GameStates.animationDelayItems);
			
			item_SC = Item.getItem(itemCode_SC,itemID_SC , itemName_SC, count_SC, capacity_SC, itemEntity_SC.getX(), itemEntity_SC.getY(), creationTime_SC, itemStates_SC);
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
//	    	System.out.println("got tileset: "+tileName_add + " from player "+name_player_add+" ID="+objectId_player);
	    	PlayerEntity playerEntity_overwrite = new PlayerEntity(tileSet_add, name_player_add, objectId_player, 0, 0, GameStates.animationDelay);
	    	playerEntity_overwrite.setWidth(tileWidth);
	    	playerEntity_overwrite.setHeight(tileHeight);
	    	playerEntity_overwrite.setCountry(country_add);
	    	playerEntity_overwrite.setGroupName(groupName_add);
	    	playerEntity_overwrite.setExperience(experience_add);
	    	PlayerManager.updatePlayerAddons(objectId_player, name_player_add, tileName_add, tilePathName_add, tileWidth, tileHeight, country_add, groupName_add, experience_add);
//	    	this.getRoom().editPlayerAddons(thisPlayerName, tileName, tilePathName, tileWidth, tileHeight, country, groupName, experience);
	    	
    		break;
    		
    	case EDIT_PLAYER_STATES:
    		/** ID */
			BigInteger entityID_editStates = BigInteger.valueOf(packet.getLong()); // 8
			/** get the orientation and states, to update the actual animation of the player */
			EntityStates orientation = getEntityStates(packet);
			EntityStates singleState = getEntityStates(packet);
			EntityStates continuousState = getEntityStates(packet);
			PlayerManager.updatePlayerState(entityID_editStates, orientation, singleState, continuousState);
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
	    		BigInteger objectId = BigInteger.valueOf(packet.getLong());
	    		
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
	    			PlayerListGUIManager.get("playerList").remove(objectId);
	    		}
	    		if (objCode == OBJECTCODE.ITEM) {
	    			deleteText = "Item "+ objectId +" was removed";
	    			if (ItemManager.get(objectId) != null) {
		    			ItemManager.get(objectId).setVisible(true);
		    			ItemManager.remove(objectId);
	    			}
	    		}
	    		GameWindow.gw.gameInfoConsole.appendInfo(deleteText);
//	    		if(objectId.intValue() == 0 && objectX != 0) {
//					GameWindow.gw.gameInfoConsole.appendInfo("Entity: x="+objectX+" y="+objectY);
//				}
    		}
    		break;
    		
    	case ADDCOMPLETEITEM: {
    		ITEMCODE itemCode = getItemCode(packet);
    		BigInteger addCompleteItemId = BigInteger.valueOf(packet.getLong());
    		byte[] nameAddCompleteItemBytes = new byte[packet.getInt()];
			packet.get(nameAddCompleteItemBytes);
			String addCompleteItemName = new String(nameAddCompleteItemBytes); // name
    		float addCompleteItemX = packet.getFloat(); 
    		float addCompleteItemY = packet.getFloat();
			int addCompleteItemCount = packet.getInt();
			int stateLength = packet.getInt();
			float[] states = new float[stateLength];
			for(int i = 0; i< stateLength; i++) {
				states[i] = packet.getFloat();
			}
			
			Item addCompleteItem = Item.getItem(itemCode, addCompleteItemId, addCompleteItemName, addCompleteItemCount, 1, addCompleteItemX, addCompleteItemY, System.currentTimeMillis(), states);
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
    		GameWindow.gw.gameInfoConsole.appendInfo("Got "+itemCount+" items");
    		
    		for (int j = 0; j < itemCount; j++) {
    			ITEMCODE itemCode = getItemCode(packet);
        		BigInteger addCompleteItemId = BigInteger.valueOf(packet.getLong());
        		byte[] nameAddCompleteItemBytes = new byte[packet.getInt()];
    			packet.get(nameAddCompleteItemBytes);
    			String addCompleteItemName = new String(nameAddCompleteItemBytes); // name
        		float addCompleteItemX = packet.getFloat(); 
        		float addCompleteItemY = packet.getFloat();
    			int addCompleteItemCount = packet.getInt();
    			int stateLength = packet.getInt();
    			float[] states = new float[stateLength];
    			for(int i = 0; i< stateLength; i++) {
    				states[i] = packet.getFloat();
    			}
    			
    			Item itemInit = Item.getItem(itemCode, addCompleteItemId, addCompleteItemName, addCompleteItemCount, 1, addCompleteItemX, addCompleteItemY, System.currentTimeMillis(), states);
        		if (ItemManager.add(itemInit)) {
        			System.out.println("Successfully added item");
        		} else {
        			System.out.println("Could not added item");
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
    	case TAKEITEM:
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
		    		if (restItem != null) {
	    				/** first send to server for the itemList */
						GameWindow.gw.send(ClientMessages.addItem(restItem.getId(), restItem.getItemCode(), restItem.getCount(), restItem.getCapacity(), restItem.getEntity().getX(), restItem.getEntity().getY(), restItem.getEntity().getMX(), restItem.getEntity().getMY(), restItem.getName(), restItem.getEntity().getTileSet().getFileName(), restItem.getEntity().getName(), restItem.getStates()));
						for (String channelName : GameWindow.gw.getSpaceChannels().values()) {
							ClientChannel channel = GameWindow.gw.getChannelByName(channelName);
							try {
								channel.send(ClientMessages.addCompleteItem(restItem.getItemCode(), takeItemId, restItem.getName(), restItem.getEntity().getX(), restItem.getEntity().getY(), restItem.getCount(), new float[1]));
							} catch (IOException e) {
								e.printStackTrace();
							}	
						}
					}
	    		}
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
				/** save the actualized map */
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
				
				/** if the sending player is an active gameMaster */
				if (GameWindow.gw.getGameMasterName().equals(name_player_sendMap)) {
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
					} else if (fileName_player_sendMap.startsWith("underground")) {
						paintLayer = "underground";
						GameWindow.gw.getUndergroundMapManagers().get(paintLayer).remove(p_sendMap);
						GameWindow.gw.getUndergroundMapManagers().get(paintLayer).putMapFileName(p_sendMap, GameStates.standardMapFolder+name_player_sendMap+"/"+fileName_player_sendMap);
					} else {
						GameWindow.gw.gameInfoConsole.appendInfo("Didn't find map: "+fileName_player_sendMap);
					}
				} else {
					// this is not an authorized GameMaster!
				}
			} else {
				// i got my own mapFile!
			}
			
    		break;
    	case DELETE_MAPOBJECT: {
    		int localX = packet.getInt();
			int localY = packet.getInt();
			int mapX = packet.getInt();
			int mapY = packet.getInt();
			byte[] roomName_Bytes = new byte[packet.getInt()];
			packet.get(roomName_Bytes);
			String roomName = new String(roomName_Bytes); // name
			byte[] objectMapName_Bytes = new byte[packet.getInt()];
			packet.get(objectMapName_Bytes);
			String objectMapName = new String(objectMapName_Bytes); // name
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

			if ( localX % 2 == 0 && localY % 2 == 0) {
				/** determine here the relevant paintLayer */
				int paintType = LocalMap.getPaintType(localMap.getLocalMap()[localX][localY]);
				ITEMCODE itemCode = LocalMap.getItemCode(paintType);
					
				if (localMap.getLocalMap()[localX][localY] != 0) {
					BigInteger itemId = ItemManager.getMaxIDValue().add(GamePanel.gp.getPlayerEntity().getId());
//					Item dropItem = Item.getItem(ITEMCODE.WOOD, itemId, "wood",(int) (Math.random()*20+20), 0, mapX+localX*GameStates.mapTileSetWidth+(int) (Math.random()*(GameStates.mapTileSetWidth-GameStates.itemTileWidth)), mapY+localY*GameStates.mapTileSetHeight +(int) (Math.random()*(GameStates.mapTileSetHeight-GameStates.itemTileHeight)), System.currentTimeMillis(), new float[0]);
					Item dropItem = Item.createItem(itemCode,itemId, mapX+localX*GameStates.mapTileSetWidth+(int) (Math.random()*(GameStates.mapTileSetWidth-GameStates.itemTileWidth)), mapY+localY*GameStates.mapTileSetHeight +(int) (Math.random()*(GameStates.mapTileSetHeight-GameStates.itemTileHeight)));
					
					
					
					/** send the complete Item to all players of the channel */
					if (GameWindow.gw.isLoggedIn() && GamePanel.gp.isInitializedPlayer()) {
						/** first send to server for the itemList */
						GameWindow.gw.send(ClientMessages.addItem(dropItem.getId(), dropItem.getItemCode(), dropItem.getCount(), dropItem.getCapacity(), dropItem.getEntity().getX(), dropItem.getEntity().getY(), dropItem.getEntity().getMX(), dropItem.getEntity().getMY(), dropItem.getName(), dropItem.getEntity().getTileSet().getFileName(), dropItem.getEntity().getName(), dropItem.getStates()));
						for (String channelName : GameWindow.gw.getSpaceChannels().values()) {
							ClientChannel channel = GameWindow.gw.getChannelByName(channelName);
							try {
								channel.send(ClientMessages.addCompleteItem(dropItem.getItemCode(), dropItem.getId(), dropItem.getName(), dropItem.getEntity().getX(), dropItem.getEntity().getY(), dropItem.getCount(), dropItem.getStates()));
							} catch (IOException e) {
								e.printStackTrace();
							}	
						}
					}
					localMap.setUl(localX, localY, 0);
					localMap.setUr(localX, localY, 0);
					localMap.setDl(localX, localY, 0);
					localMap.setDr(localX, localY, 0);
//					localMap.setIdByCornersObject(localX, localY, localMap.getLocalMap()[localX][localY]);
//					objectMapManager.deleteSurrounding(localMap, localX, localY, localMap.getLocalMap()[localX][localY]);
					localMap.setIdByCornersObject(localX, localY, paintType);
					objectMapManager.deleteSurrounding(localMap, localX, localY, paintType, TileDimensions.RowCol2x3);
	
					localOverlayMap.setUl(localX, localY, 0);
					localOverlayMap.setUr(localX, localY, 0);
					localOverlayMap.setDl(localX, localY, 0);
					localOverlayMap.setDr(localX, localY, 0);
					localOverlayMap.setIdByCornersObject(localX, localY, paintType-GameStates.mapTileSetWidth*2);
					overlayMapManager.deleteSurrounding(localOverlayMap, localX, localY, paintType-GameStates.mapTileSetWidth*2, TileDimensions.RowCol2x3);
//					localOverlayMap.setIdByCornersObject(localX, localY, localMap.getLocalMap()[localX][localY]-GameStates.mapTileSetWidth*2);
//					overlayMapManager.deleteSurrounding(localOverlayMap, localX, localY, localMap.getLocalMap()[localX][localY]-GameStates.mapTileSetWidth*2);
							//MapManager.adjustSurrounding(MapManager.get(p), localX, localY);
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
			}
			
//			this.getRoom().deleteMapObject(gmID, iTile);
	    	
			break;
    	}
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
					GameWindow.gw.send(ClientMessages.addItemToContainer(inventory.getContainerType(), tempItem.getId(), tempItem.getItemCode(), tempItem.getCount(), tempItem.getCapacity(), tempItem.getEntity().getX(), tempItem.getEntity().getY(), tempItem.getEntity().getMX(), tempItem.getEntity().getMY(), tempItem.getName(), tempItem.getEntity().getTileSet().getFileName(), tempItem.getEntity().getName(), tempItem.getStates(), i, j));
    			}    	    			
    		}
    	}
	 }
}

