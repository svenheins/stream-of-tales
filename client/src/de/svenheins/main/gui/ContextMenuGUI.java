/* Copyright (C) Sven Heins - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * author: Sven Heins
 * date: 05.07.2015
 */
package de.svenheins.main.gui;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.image.ImageObserver;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import com.sun.sgs.client.ClientChannel;

import de.svenheins.handlers.ClientMessageHandler;
import de.svenheins.main.AttributeType;
import de.svenheins.main.GamePanel;
import de.svenheins.main.GameStates;
import de.svenheins.main.GameWindow;
import de.svenheins.managers.ItemManager;
import de.svenheins.messages.ClientMessages;
import de.svenheins.messages.ITEMCODE;
import de.svenheins.objects.LocalMap;
import de.svenheins.objects.PlayerEntity;
import de.svenheins.objects.LocalObject;
import de.svenheins.objects.Space;
import de.svenheins.objects.TileSet;
import de.svenheins.objects.items.Equipment;
import de.svenheins.objects.items.Item;
import de.svenheins.objects.items.ItemFactoryCollection;
import de.svenheins.objects.items.Material;

public class ContextMenuGUI {
	public HashMap<BigInteger, Button> buttonList = new HashMap<BigInteger, Button>();
	public List<BigInteger> idList = new ArrayList<BigInteger>();
	private Space backgroundSpace;
	private LocalObject localObject;
	private boolean visible;
	private int x;
	private int y;
	private int width;
	private int height;
	
	public ContextMenuGUI() {
		setVisible(false);
	}
	
	/** show the contextMenu for the corresponding entity */
	public void create(LocalObject localObject, int x, int y) {
//		int countButtons = 0;
		this.setX(x);
		this.setY(y);
		ArrayList<Polygon> contextMenuSpacePolygon = new ArrayList<Polygon>();
		
//		if (localObject != this.localObject) {	
			buttonList.clear();
			idList.clear();
			int guiHeight = GameStates.contextMenuButtonDistY;
			if (localObject instanceof PlayerEntity) {
				BigInteger runningID = BigInteger.valueOf(0);
				
				
				/** add one button */
				Button mapMasterButton = standardButton(runningID, localObject.getName(), 0, GameStates.contextMenuButtonDistX, guiHeight, "Enter "+localObject.getName()+"'s World");
				if (this.add(mapMasterButton)) ; else System.out.println("couldn't add");
				guiHeight += mapMasterButton.getHeight() + GameStates.contextMenuButtonDistY;
				runningID = runningID.add(BigInteger.valueOf(1));
				
				/** if it is me myself */
				if (localObject.getName().equals(GameWindow.gw.getPlayerName())) {
					/** the same for the Y point */
					float playerX = GamePanel.gp.getPlayerEntity().getX()+GamePanel.gp.getPlayerEntity().getWidth()/2;
					float playerY = GamePanel.gp.getPlayerEntity().getY()+GamePanel.gp.getPlayerEntity().getHeight()*(3/4);
					int localWidth = GameStates.mapTotalWidth;
					int localHeight = GameStates.mapTotalHeight;
					int playerLatticePointX = (int) Math.floor( (float) (playerX) / (localWidth)) * localWidth;
					int playerLatticePointY = (int) Math.floor( (float) (playerY) / (localHeight)) * localHeight;
					int playerLocalX = (int) Math.floor( (float) (playerX - playerLatticePointX )/ GameStates.mapTileSetWidth);
					int playerLocalY = (int) Math.floor( (float) ( playerY - playerLatticePointY )/ GameStates.mapTileSetHeight);
					
					LocalMap lightMap = GameWindow.gw.getMapManagers().get("cobble").get(new Point(playerLatticePointX, playerLatticePointY));
					int tileTypeStanding = -1;
					if (lightMap != null) {
						tileTypeStanding = LocalMap.getPaintType(lightMap.getTile(playerLocalX, playerLocalY));
					}
					
					if (tileTypeStanding == GameStates.lightTile) {
						Button climbUpButton = standardButton(runningID, localObject.getName(), 0, GameStates.contextMenuButtonDistX, guiHeight, "climb up");
						if (this.add(climbUpButton)) ; else System.out.println("couldn't add");
						guiHeight += climbUpButton.getHeight() + GameStates.contextMenuButtonDistY;
						runningID = runningID.add(BigInteger.valueOf(1));
					}
					/** only allow this button for the mapMaster */
					if (GameWindow.gw.isMapMaster()) {
						String gmText = "";
						if (GameWindow.gw.isGameMaster()) gmText = "Leave Gamemaster-Mode"; else gmText = "Enter Gamemaster-Mode";
						/** add next button */
						Button gameMasterButton = standardButton(runningID, localObject.getName(), 0, GameStates.contextMenuButtonDistX, guiHeight, gmText);
						if (this.add(gameMasterButton)) ; else System.out.println("couldn't add");
						guiHeight += gameMasterButton.getHeight() + GameStates.contextMenuButtonDistY;
						runningID = runningID.add(BigInteger.valueOf(1));
					}
						
//					/** add next button */
//					Button animationButton = standardButton(runningID, "abc", 0, GameStates.contextMenuButtonDistX, guiHeight, "go to bed");
//					if (this.add(animationButton)) ; else System.out.println("couldn't add");
//					guiHeight += animationButton.getHeight() + GameStates.contextMenuButtonDistY;
//					runningID = runningID.add(BigInteger.valueOf(1));
					
					/** add next button */
					Button itemButton = standardButton(runningID, "abc", 0, GameStates.contextMenuButtonDistX, guiHeight, "drop random item");
					if (this.add(itemButton)) ; else System.out.println("couldn't add");
					guiHeight += itemButton.getHeight() + GameStates.contextMenuButtonDistY;
					runningID = runningID.add(BigInteger.valueOf(1));
					
//					/** add next button */
//					Button shoeButton = standardButton(runningID, "abc", 0, GameStates.contextMenuButtonDistX, guiHeight, "drop shoe");
//					if (this.add(shoeButton)) ; else System.out.println("couldn't add");
//					guiHeight += shoeButton.getHeight() + GameStates.contextMenuButtonDistY;
//					runningID = runningID.add(BigInteger.valueOf(1));
				} else {
					/** if i clicked onto another playerEntity */
					/** TODO: add stuff for interaction with other playerEntites */
				}
			} else if (localObject instanceof Material) {
				BigInteger runningID = BigInteger.valueOf(0);
				for (String itemName: ((Material) localObject).getCraftList().keySet()) {
					Item itemIngredient= ItemFactoryCollection.getItemFactory(ItemFactoryCollection.itemCodeList.get(itemName)).getItem(itemName);
					if (itemIngredient.getLevel().intValue() <= GamePanel.gp.getPlayerEntity().getAttributes()[AttributeType.CRAFTLEVEL.ordinal()]) {
						/** add one button for each craftItem*/
						Button craftItemButton = standardButton(runningID, localObject.getName(), 0, GameStates.contextMenuButtonDistX, guiHeight, itemName);
						if (this.add(craftItemButton)) ; else System.out.println("couldn't add");
						guiHeight += craftItemButton.getHeight() + GameStates.contextMenuButtonDistY;
						runningID = runningID.add(BigInteger.valueOf(1));
					}
				}
			} else if (localObject instanceof Equipment) {
				BigInteger runningID = localObject.getId();
//				for (String itemName: ((Material) localObject).getCraftList().keySet()) {
//					Item itemIngredient= ItemFactoryCollection.getItemFactory(ItemFactoryCollection.itemCodeList.get(itemName)).getItem(itemName);
//					if (itemIngredient.getLevel().intValue() <= GamePanel.gp.getPlayerEntity().getAttributes()[AttributeType.CRAFTLEVEL.ordinal()]) {
//						/** add one button for each craftItem*/
				System.out.println("Created Button: "+runningID);
						Button dismantleItemButton = standardButton(runningID, localObject.getName(), 0, GameStates.contextMenuButtonDistX, guiHeight, "dismantle");
						if (this.add(dismantleItemButton)) ; else System.out.println("couldn't add");
						guiHeight += dismantleItemButton.getHeight() + GameStates.contextMenuButtonDistY;
						runningID = runningID.add(BigInteger.valueOf(1));
//					}
//				}
			}
			this.setLocalObject(localObject);
			contextMenuSpacePolygon.add((new Polygon(new int[]{0 , 0 , GameStates.contextMenuButtonWidth + GameStates.contextMenuButtonDistX *2, GameStates.contextMenuButtonWidth+ GameStates.contextMenuButtonDistX *2}, new int[]{0 ,  guiHeight, guiHeight, 0}, 4) ));
			backgroundSpace = new Space(contextMenuSpacePolygon, 0, 0, "backgroundGUISpace", BigInteger.valueOf(0), new int[]{0, 0, 0}, true, 0.6f, 1.0f, 1.0f, "empty");
//		}
		this.setVisible(true);
	}
	
	/** remove a button from the list */
	public void remove(BigInteger index) throws IllegalArgumentException {
		try {
			buttonList.remove(index);
			idList.remove(index);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
		
	}
	
	public boolean add(Button button) {
		if (buttonList.containsKey(button.getId())) {
			return false;
		} else {
			buttonList.put(button.getId(), button);
			idList.add(button.getId());
			return true;
		}
	}
	
	public Button get(BigInteger index){
		try {
			return (Button) buttonList.get(index);
		}
		catch(IndexOutOfBoundsException e){
			return null;
		}
		
	}
	
	public boolean overwrite(Button button){
		if (!buttonList.containsKey(button.getId())) {
			/** do nothing if key is not yet set*/
			return false;
		} else {
			buttonList.put(button.getId(), button);
			idList.add(button.getId());
			return true;
		}
	}
	
	public int size(){
		return buttonList.size();
	}
	
	public boolean contains(Button button) {
		return buttonList.containsValue(button);
	}
	
	public boolean mouseClick(Point p) {
		Point correctedPoint = new Point(p.x-this.getX(), p.y-this.getY());
		boolean ret = false;
		for (BigInteger id : idList) {
			Button button = buttonList.get(id);
			if (button.contains(correctedPoint)) {
				/** now change the animation and status */
//				if (button.isActivated()) {
//					button.setInactive();
//				} else {
//					button.setActive();
//				}
				buttonClick(button.getId());
				ret = true;
			}
		}
		return ret;
	}
	
	/** click different buttons */
	public void buttonClick(BigInteger id) {
		/** click the mapMaster-button */
		if (get(id).getText().equals("Enter "+get(id).getStrValue()+"'s World")){
			GameWindow.gw.setMapMaster(get(id).getStrValue());
			if (!GameWindow.gw.isMapMaster()) GameWindow.gw.setGameMaster(false);
			GameWindow.gw.enterRoom(GamePanel.gp.getPlayerEntity().getRoom());
//			if (GameWindow.gw.getMapMasterName().equals(GameWindow.gw.getPlayerName())) {
//				GameWindow.gw.sound.startMusic(MusicType.BACKGROUND, MusicName.GAMEMASTER);
//			} else {
//				GameWindow.gw.sound.startMusic(MusicType.BACKGROUND, MusicName.GAME);
//			}
			
//			System.out.println(get(id).getStrValue());
		}
		
		if (get(id).getText().equals("climb up")){
			float playerX = GamePanel.gp.getPlayerEntity().getX()+GamePanel.gp.getPlayerEntity().getWidth()/2;
			float playerY = GamePanel.gp.getPlayerEntity().getY()+GamePanel.gp.getPlayerEntity().getHeight()*(3/4);
			int localWidth = GameStates.mapTotalWidth;
			int localHeight = GameStates.mapTotalHeight;
			int playerLatticePointX = (int) Math.floor( (float) (playerX) / (localWidth)) * localWidth;
			int playerLatticePointY = (int) Math.floor( (float) (playerY) / (localHeight)) * localHeight;
			int playerLocalX = (int) Math.floor( (float) (playerX - playerLatticePointX )/ GameStates.mapTileSetWidth);
			int playerLocalY = (int) Math.floor( (float) ( playerY - playerLatticePointY )/ GameStates.mapTileSetHeight);
			
			LocalMap lightMap = GameWindow.gw.getMapManagers().get("cobble").get(new Point(playerLatticePointX, playerLatticePointY));
			int tileTypeStanding = LocalMap.getPaintType(lightMap.getTile(playerLocalX, playerLocalY));
			
			if (tileTypeStanding == GameStates.lightTile) {
				GameWindow.gw.enterRoom(GameWindow.gw.getUpperRoom(GamePanel.gp.getPlayerEntity().getRoom()));
			} else {
				// moved too far away from hole
			}
			
		}
		
		/** click the gameMaster-button which was deactivated*/
		if (get(id).getText().equals("Enter Gamemaster-Mode")){
			GameWindow.gw.setGameMaster(true);
			get(id).setText("Leave Gamemaster-Mode");
		} else 
			/** click the gameMaster-button which was activated*/
		if (get(id).getText().equals("Leave Gamemaster-Mode")){
			GameWindow.gw.setGameMaster(false);
			get(id).setText("Enter Gamemaster-Mode");
		}
		
		
//		/** click animation  */
//		if (get(id).getText().equals("go to bed")){
//			GameWindow.gw.gameInfoConsole.appendInfo("go to bed");
//			GamePanel.gp.getPlayerEntity().setContinuousState(EntityStates.SLEEPING);
//			GamePanel.gp.getPlayerEntity().setChangedStates(true);
////			System.out.println(get(id).getStrValue());
//		}
		
		/** drop random item */
		if (get(id).getText().equals("drop random item")) {
//			BigInteger itemId = ItemManager.getMaxIDValue().add(GamePanel.gp.getPlayerEntity().getId());
//			
//			int putX = 0;
//			int putY = 0;
//			switch (GamePanel.gp.getPlayerEntity().getOrientation()) {
//			case RIGHT:
//				putX = (int) (GamePanel.gp.getPlayerEntity().getX() + GamePanel.gp.getPlayerEntity().getWidth()/2 + GameStates.dropDistance);
//				putY = (int) (GamePanel.gp.getPlayerEntity().getY() + GamePanel.gp.getPlayerEntity().getHeight()*3/4 - GameStates.inventoryItemTileHeight/2);
//				break;
//			case LEFT:
//				putX = (int) (GamePanel.gp.getPlayerEntity().getX() + GamePanel.gp.getPlayerEntity().getWidth()/2 - (GameStates.dropDistance+GameStates.inventoryItemTileWidth));
//				putY = (int) (GamePanel.gp.getPlayerEntity().getY() + GamePanel.gp.getPlayerEntity().getHeight()*3/4 -GameStates.itemTileHeight/2);
//				break;
//			case UP:
//				putX = (int) (GamePanel.gp.getPlayerEntity().getX() + GamePanel.gp.getPlayerEntity().getWidth()/2- GameStates.inventoryItemTileWidth/2);
//				putY = (int) (GamePanel.gp.getPlayerEntity().getY() + GamePanel.gp.getPlayerEntity().getHeight()*3/4 - (GameStates.dropDistance+GameStates.itemTileHeight));
//				break;
//			case DOWN:
//				putX = (int) (GamePanel.gp.getPlayerEntity().getX() + GamePanel.gp.getPlayerEntity().getWidth()/2 - GameStates.inventoryItemTileWidth/2);
//				putY = (int) (GamePanel.gp.getPlayerEntity().getY() + GamePanel.gp.getPlayerEntity().getHeight()*3/4 + (GameStates.dropDistance));
//				break;
//			default: ;
//			}
				
//			Item randomItem = ItemFactoryCollection.createRandomItem(Integer.valueOf(10), itemId, GamePanel.gp.getPlayerEntity().getRoom(), putX, putY);
//			
//			if (randomItem != null) {
//				System.out.println("added new "+randomItem.getName()+" with ID "+itemId.longValue());
//				
//				/** send the complete Item to all players of the channel */
//				if (GameWindow.gw.isLoggedIn() && GamePanel.gp.isInitializedPlayer()) {
//					/** first send to server for the itemList */
//					GameWindow.gw.send(ClientMessages.addItem(randomItem.getId(), randomItem.getItemCode(), randomItem.getCount(), randomItem.getCapacity(), randomItem.getEntity().getX(), randomItem.getEntity().getY(), randomItem.getEntity().getMX(), randomItem.getEntity().getMY(), randomItem.getName(),  GamePanel.gp.getPlayerEntity().getRoom(), randomItem.getEntity().getTileSet().getFileName(), randomItem.getEntity().getName(), randomItem.getAttributes()));
//					
//	//				GameWindow.gw.send(ClientMessages.addItem(itemId));
//					
//					for (String channelName : GameWindow.gw.getSpaceChannels().values()) {
//						ClientChannel channel = GameWindow.gw.getChannelByName(channelName);
//						try {
//							channel.send(ClientMessages.addCompleteItem(randomItem.getItemCode(), randomItem.getId(), randomItem.getName(),  GamePanel.gp.getPlayerEntity().getRoom(), randomItem.getX(), randomItem.getY(), randomItem.getCount(), randomItem.getAttributes()));
//						} catch (IOException e) {
//							e.printStackTrace();
//						}	
//					}
					BigInteger itemId  = ItemManager.getMaxIDValue().add(GamePanel.gp.getPlayerEntity().getId());
					ItemManager.setMaxIDValue(itemId);
					
					Random rand = new Random();
					float randValue = (float) ((rand.nextFloat()*2*Math.PI));//Math.PI/2)+ Math.PI/4);
					float plusMinusX = (float) Math.sin(randValue);
					float plusMinusY = (float) Math.cos(randValue);
					float randomShiftX = plusMinusX * GameStates.dropDistanceStream;// + rand.nextFloat()*500;
					float randomShiftY = plusMinusY * GameStates.dropDistanceStream;// + rand.nextFloat()*500;
					float dropX = GamePanel.gp.getPlayerEntity().getX()+GamePanel.gp.getPlayerEntity().getWidth()/2 - GameStates.itemTileWidth/2 + randomShiftX;
					float dropY = GamePanel.gp.getPlayerEntity().getY()+GamePanel.gp.getPlayerEntity().getHeight()/2 - GameStates.itemTileHeight/2 + randomShiftY;
					
					Item dropItem = ItemFactoryCollection.createRandomItem(Integer.valueOf(100), itemId, GamePanel.gp.getPlayerEntity().getRoom(), dropX, dropY);
					
					System.out.println("added new item: "+itemId);
					
					/** send the complete Item to all players of the channel */
					if (dropItem != null && GameWindow.gw.isLoggedIn() && GamePanel.gp.isInitializedPlayer()) {
						/** first send to server for the itemList */
						GameWindow.gw.send(ClientMessages.addItem(dropItem.getId(), dropItem.getItemCode(), dropItem.getCount(), dropItem.getCapacity(), dropItem.getEntity().getX(), dropItem.getEntity().getY(), dropItem.getEntity().getMX(), dropItem.getEntity().getMY(), dropItem.getName(), dropItem.getComment(), dropItem.getRoom(), dropItem.getEntity().getTileSet().getFileName(), dropItem.getEntity().getName(), dropItem.getAttributes()));
						for (String channelName : GameWindow.gw.getSpaceChannels().values()) {
							ClientChannel clientChannel = GameWindow.gw.getChannelByName(channelName);
							try {
								clientChannel.send(ClientMessages.addCompleteItem(dropItem.getItemCode(), dropItem.getId(), dropItem.getName(), dropItem.getComment(), dropItem.getRoom(), dropItem.getEntity().getX(), dropItem.getEntity().getY(), dropItem.getCount(), dropItem.getAttributes()));
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
//						StreamViewerManager.get(sender).removeGold(GameStates.streamCostDropItem);
						String addCount = "";
						if (dropItem.getCount() > 1) addCount = " x "+dropItem.getCount();
//						sendSafeMessage(sender+" dropped for "+GameStates.streamCostDropItem+" gold a random level-"+StreamViewerManager.get(sender).getLevel()+" item: "+dropItem.getName()+addCount);
					} else {
//						sendSafeMessage(sender+" tried to drop an item, but it has failed - just try it again! (You haven't lost the gold)");
					}
//				}
//			}
		
			
		}
		
		/** craft item */
		HashMap<String, ITEMCODE> tempItemCodeList = new HashMap<String, ITEMCODE>(ItemFactoryCollection.itemCodeList);
//		System.out.println(tempItemCodeList.size());
		for (String itemName : tempItemCodeList.keySet()) {
//			System.out.println("ItemName = "+itemName);
			/** click animation  */
			if (get(id).getText().equals(itemName)){
				/** check if inventory not yet full */
//				if (GamePanel.gp.getPlayerEntity().getInventory().hasEmptyField()) {
					/** check if there is enough material */
					BigInteger itemId = GamePanel.gp.getPlayerEntity().getInventory().getMaxID().add(BigInteger.valueOf(1));
//					System.out.println(itemId);
					Item item = ItemFactoryCollection.getItemFactory(tempItemCodeList.get(itemName)).getItem(itemName);
					item.setId(itemId);
					boolean enoughIngredients = true;
					for (String itemIngredient : item.getIngredients().keySet()) {
						int count = item.getIngredients().get(itemIngredient).intValue();
						if (GamePanel.gp.getPlayerEntity().getInventory().countItems(itemIngredient) >= count) {
							/** here we have enough material of the ingredient */
						} else {
							enoughIngredients = false;
							break;
						}
//						System.out.println("need "+count +" "+itemIngredient+" have: "+GamePanel.gp.getPlayerEntity().getInventory().countItems(itemIngredient));
					}
					if (enoughIngredients) {
						/** remove ingredients and add item to inventory */
						for (String itemIngredient : item.getIngredients().keySet()) {
							int count = item.getIngredients().get(itemIngredient).intValue();
							GamePanel.gp.getPlayerEntity().getInventory().removeCountItems(itemIngredient, count);
						}
						
						Item tooManyItem = GamePanel.gp.getPlayerEntity().getInventory().addItem(item);
						if (tooManyItem != null) {
							int putX = 0;
							int putY = 0;
							switch (GamePanel.gp.getPlayerEntity().getOrientation()) {
							case RIGHT:
								putX = (int) (GamePanel.gp.getPlayerEntity().getX() + GamePanel.gp.getPlayerEntity().getWidth()/2 + GameStates.dropDistance);
								putY = (int) (GamePanel.gp.getPlayerEntity().getY() + GamePanel.gp.getPlayerEntity().getHeight()*3/4 - GameStates.inventoryItemTileHeight/2);
								break;
							case LEFT:
								putX = (int) (GamePanel.gp.getPlayerEntity().getX() + GamePanel.gp.getPlayerEntity().getWidth()/2 - (GameStates.dropDistance+GameStates.inventoryItemTileWidth));
								putY = (int) (GamePanel.gp.getPlayerEntity().getY() + GamePanel.gp.getPlayerEntity().getHeight()*3/4 -GameStates.itemTileHeight/2);
								break;
							case UP:
								putX = (int) (GamePanel.gp.getPlayerEntity().getX() + GamePanel.gp.getPlayerEntity().getWidth()/2- GameStates.inventoryItemTileWidth/2);
								putY = (int) (GamePanel.gp.getPlayerEntity().getY() + GamePanel.gp.getPlayerEntity().getHeight()*3/4 - (GameStates.dropDistance+GameStates.itemTileHeight));
								break;
							case DOWN:
								putX = (int) (GamePanel.gp.getPlayerEntity().getX() + GamePanel.gp.getPlayerEntity().getWidth()/2 - GameStates.inventoryItemTileWidth/2);
								putY = (int) (GamePanel.gp.getPlayerEntity().getY() + GamePanel.gp.getPlayerEntity().getHeight()*3/4 + (GameStates.dropDistance));
								break;
							default : ;
							}
							tooManyItem.setX(putX);
							tooManyItem.setY(putY);
//							tooManyItem =  ItemFactoryCollection.createRandomItem(tooManyItem.getLevel(), itemId, GamePanel.gp.getPlayerEntity().getRoom(), putX, putY);
							/** send the complete Item to all players of the channel */
							if (GameWindow.gw.isLoggedIn() && GamePanel.gp.isInitializedPlayer()) {
								/** first send to server for the itemList */
								GameWindow.gw.send(ClientMessages.addItem(tooManyItem.getId(), tooManyItem.getItemCode(), tooManyItem.getCount(), tooManyItem.getCapacity(), tooManyItem.getEntity().getX(), tooManyItem.getEntity().getY(), tooManyItem.getEntity().getMX(), tooManyItem.getEntity().getMY(), tooManyItem.getName(), tooManyItem.getComment(), GamePanel.gp.getPlayerEntity().getRoom(), tooManyItem.getEntity().getTileSet().getFileName(), tooManyItem.getEntity().getName(), tooManyItem.getAttributes()));
								for (String channelName : GameWindow.gw.getSpaceChannels().values()) {
									ClientChannel channel = GameWindow.gw.getChannelByName(channelName);
									try {
										channel.send(ClientMessages.addCompleteItem(tooManyItem.getItemCode(), tooManyItem.getId(), tooManyItem.getName(),  tooManyItem.getComment(), GamePanel.gp.getPlayerEntity().getRoom(), tooManyItem.getX(), tooManyItem.getY(), tooManyItem.getCount(), tooManyItem.getAttributes()));
									} catch (IOException e) {
										e.printStackTrace();
									}	
								}
							
							}
						}
					}
//				}
			}
			
//			GamePanel.gp.getPlayerEntity().calculateTotalEquipmentInfluenceAndUpdateVisuals();
		}
		/** update server */
		ClientMessageHandler.containerUpdateSendBrutal(GamePanel.gp.getPlayerEntity().getInventory());
		
		/** dismantle object */
		if (get(id).getText().equals("dismantle")){
//			System.out.println("Clicked on Button: "+id);
			HashMap<String, Integer> ingredientList = GamePanel.gp.getPlayerEntity().getInventory().getItemList().get(id).getIngredients();
//			System.out.println("has item "+GamePanel.gp.getPlayerEntity().getInventory().getItemList().get(id).getName());
			for(String itemName : ingredientList.keySet()) {
				BigInteger itemId = GamePanel.gp.getPlayerEntity().getInventory().getMaxID().add(BigInteger.valueOf(1));
//				System.out.println(itemId);
				Item item = ItemFactoryCollection.getItemFactory(tempItemCodeList.get(itemName)).getItem(itemName);
				item.setCount(ingredientList.get(itemName).intValue()/2);
				item.setId(itemId);
				Item tooManyItem = GamePanel.gp.getPlayerEntity().getInventory().addItem(item);
				if (tooManyItem != null) {
					int putX = 0;
					int putY = 0;
					switch (GamePanel.gp.getPlayerEntity().getOrientation()) {
					case RIGHT:
						putX = (int) (GamePanel.gp.getPlayerEntity().getX() + GamePanel.gp.getPlayerEntity().getWidth()/2 + GameStates.dropDistance);
						putY = (int) (GamePanel.gp.getPlayerEntity().getY() + GamePanel.gp.getPlayerEntity().getHeight()*3/4 - GameStates.inventoryItemTileHeight/2);
						break;
					case LEFT:
						putX = (int) (GamePanel.gp.getPlayerEntity().getX() + GamePanel.gp.getPlayerEntity().getWidth()/2 - (GameStates.dropDistance+GameStates.inventoryItemTileWidth));
						putY = (int) (GamePanel.gp.getPlayerEntity().getY() + GamePanel.gp.getPlayerEntity().getHeight()*3/4 -GameStates.itemTileHeight/2);
						break;
					case UP:
						putX = (int) (GamePanel.gp.getPlayerEntity().getX() + GamePanel.gp.getPlayerEntity().getWidth()/2- GameStates.inventoryItemTileWidth/2);
						putY = (int) (GamePanel.gp.getPlayerEntity().getY() + GamePanel.gp.getPlayerEntity().getHeight()*3/4 - (GameStates.dropDistance+GameStates.itemTileHeight));
						break;
					case DOWN:
						putX = (int) (GamePanel.gp.getPlayerEntity().getX() + GamePanel.gp.getPlayerEntity().getWidth()/2 - GameStates.inventoryItemTileWidth/2);
						putY = (int) (GamePanel.gp.getPlayerEntity().getY() + GamePanel.gp.getPlayerEntity().getHeight()*3/4 + (GameStates.dropDistance));
						break;
					default : ;
					}
					tooManyItem.setX(putX);
					tooManyItem.setY(putY);
//					tooManyItem =  ItemFactoryCollection.createRandomItem(tooManyItem.getLevel(), itemId, GamePanel.gp.getPlayerEntity().getRoom(), putX, putY);
					/** send the complete Item to all players of the channel */
					if (GameWindow.gw.isLoggedIn() && GamePanel.gp.isInitializedPlayer()) {
						/** first send to server for the itemList */
						GameWindow.gw.send(ClientMessages.addItem(tooManyItem.getId(), tooManyItem.getItemCode(), tooManyItem.getCount(), tooManyItem.getCapacity(), tooManyItem.getEntity().getX(), tooManyItem.getEntity().getY(), tooManyItem.getEntity().getMX(), tooManyItem.getEntity().getMY(), tooManyItem.getName(), tooManyItem.getComment(), GamePanel.gp.getPlayerEntity().getRoom(), tooManyItem.getEntity().getTileSet().getFileName(), tooManyItem.getEntity().getName(), tooManyItem.getAttributes()));
						for (String channelName : GameWindow.gw.getSpaceChannels().values()) {
							ClientChannel channel = GameWindow.gw.getChannelByName(channelName);
							try {
								channel.send(ClientMessages.addCompleteItem(tooManyItem.getItemCode(), tooManyItem.getId(), tooManyItem.getName(), tooManyItem.getComment(), GamePanel.gp.getPlayerEntity().getRoom(), tooManyItem.getX(), tooManyItem.getY(), tooManyItem.getCount(), tooManyItem.getAttributes()));
							} catch (IOException e) {
								e.printStackTrace();
							}	
						}
					
					}
				}
			}
			GamePanel.gp.getPlayerEntity().getInventory().removeItem(id);
			this.setVisible(false);
			/** update server */
			ClientMessageHandler.containerUpdateSendBrutal(GamePanel.gp.getPlayerEntity().getInventory());
//			GamePanel.gp.getPlayerEntity().calculateTotalEquipmentInfluenceAndUpdateVisuals();
		}
	}
	
	/** mouseOver effect*/
	public void mouseOver(Point p) {
		Point correctedPoint = new Point(p.x-this.getX(), p.y-this.getY());
		if (backgroundSpace.contains(correctedPoint) && this.isVisible()) {
			for (BigInteger id : idList) {
				Button button = buttonList.get(id);
				if (button.contains(correctedPoint)) {
					/** now change the animation and status */
					button.setMouseover();
				} else {
					if (button.isActivated()) {
						button.setActive();
					} else {
						button.setInactive();
					}
				}
			}
		} else {
			this.setVisible(false);
		}
	}
	
	public void deactivateOthers(Button button) {
		for(Button otherButton : buttonList.values()) {
			if (otherButton != button) {
				otherButton.setInactive();
			}
		}
	}
	
	public void paint(Graphics2D g, int x, int y, ImageObserver iObserver) {
//		System.out.println("paint context GUI");
		if (!idList.isEmpty() && this.isVisible()) {
			g.setPaintMode();
			backgroundSpace.paint(g, x+ this.x, y + this.y);
			g.setPaintMode();
			for(BigInteger id : idList) {
				Button button = get(id);
				g.drawImage(button.getSprite().getImage(), x + this.x + (int) (button.getX()), y + this.y + (int) (button.getY()), iObserver);
				
				g.setPaintMode();
				g.setColor(GameStates.standardFontColor);
				g.setFont(new Font(GameStates.standardFont, Font.PLAIN , GameStates.contextMenuFontSize));
//				drawConsoleText(g, (int)((this.position.x+10)/GamePanel.gp.getZoomFactor()), (int)((this.position.y+10)/GamePanel.gp.getZoomFactor()));
				g.drawString(button.getText(), GameStates.contextMenuTextDistX + x + this.x + (int) (button.getX()), GameStates.contextMenuTextDistY + y + this.y + (int) (button.getY()) + g.getFontMetrics().getHeight());
			}
		}
	}

	public Space getBackgroundSpace() {
		return backgroundSpace;
	}

	public void setBackgroundSpace(Space backgroundSpace) {
		this.backgroundSpace = backgroundSpace;
	}

	public LocalObject getLocalObject() {
		return localObject;
	}

	public void setLocalObject(LocalObject localObject) {
		this.localObject = localObject;
	}

	public Button standardButton(BigInteger id, String strValue, int intValue, int x, int y, String text) {
		TileSet tileSetStandardButton = new TileSet("tilesets/buttons/contextButton.png", "contextButton", GameStates.contextMenuButtonWidth, GameStates.contextMenuButtonHeight);
		Button standardButtonGUI = new Button(tileSetStandardButton, "standardButton", id, x, y, GameStates.animationDelay, strValue, intValue, text);
//		standardButtonGUI.setInactive();
		return standardButtonGUI;		
		
//		ArrayList<Polygon> editorGUISpacePolygon = new ArrayList<Polygon>();
//		editorGUISpacePolygon.add((new Polygon(new int[]{GameStates.width -250 , GameStates.width -250 ,GameStates.width -25, GameStates.width -25}, new int[]{40 , 85 ,85, 40}, 4) ));
//		Space editorGUISpace = new Space(editorGUISpacePolygon, 0, 0, "editorGUISpace", BigInteger.valueOf(0), new int[]{0, 0, 0}, true, 0.6f, 1.0f, 1.0f, "empty");
//		
//		EditorGUI floorEditor = new EditorGUI("floor", "cobble", 110, editorGUISpace);
//		floorEditor.add(cobbleButtonGUI);
//		floorEditor.add(grassButtonGUI);
//		floorEditor.add(snowButtonGUI);
//		floorEditor.add(treeButtonGUI);
//		floorEditor.add(snowTreeButtonGUI);
//		floorEditor.add(undergroundGrassButtonGUI);
//		EditorGUIManager.add(floorEditor);
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}
}
