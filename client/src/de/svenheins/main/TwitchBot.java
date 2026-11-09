/* Copyright (C) Sven Heins - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * author: Sven Heins
 * date: 05.07.2015
 */
package de.svenheins.main;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.sun.sgs.client.ClientChannel;

import de.svenheins.functions.MyMath;
import de.svenheins.handlers.ClientMessageHandler;
import de.svenheins.managers.AgentManager;
import de.svenheins.managers.ItemManager;
import de.svenheins.managers.StreamViewerManager;
import de.svenheins.managers.TextEffectManager;
import de.svenheins.managers.TileSetManager;
import de.svenheins.messages.ClientMessages;
import de.svenheins.messages.ITEMCODE;
import de.svenheins.objects.TextEffect;
import de.svenheins.objects.TileSet;
import de.svenheins.objects.agents.Agent;
import de.svenheins.objects.agents.AggressiveAgent;
import de.svenheins.objects.agents.goals.Goal;
import de.svenheins.objects.items.Container;
import de.svenheins.objects.items.Item;
import de.svenheins.objects.items.ItemFactoryCollection;
import de.svenheins.objects.items.Stackable;
import de.svenheins.threads.IRCThread;

public class TwitchBot extends IRCThread{
	private List<Long> messageList;
	private boolean moderator;
	private int timeLimit = 29999;
	private int countMessageLimitNonModerator = 20;
	private int countMessageLimitModerator = 100;
	private int whichItem = 0;

	public TwitchBot(String hostName, int port, String nickName,
			String oauthPWD, String realName, String channelName, boolean moderator) {
		super(hostName, port, nickName, oauthPWD, realName, channelName);
		this.setModerator(moderator);
		this.setMessageList(new ArrayList<Long>());
	}

	public void onMessage(String channel, String senderTwitch, String message) {
		String sender = senderTwitch.substring(0, 1).toUpperCase() + senderTwitch.substring(1);
		
		/** only process input if it begins with a "!"*/
		if (message.startsWith("!")) {
		/** only accept command if the sender is in the StreamViewerManager */
			if (StreamViewerManager.contains(sender)) {
				String order = message.substring(1);
				if (order.toUpperCase().startsWith("CREATE")) {
					if (!AgentManager.ircUserAgentList.containsKey(sender) ) {
						if (AgentManager.size() <= GameStates.agentMaxCount) {
							if (StreamViewerManager.get(sender).getGold() >= GameStates.streamCostCreate) {
								String groupName = GamePanel.gp.getPlayerEntity().getGroupName();
								float distanceAgent = GameStates.streamHostileAgentSpawnDistance;
								if (order.substring(6).length() > 0) {
									String orderGroupName = order.substring(6);
									if (orderGroupName.startsWith(" ")) {
										groupName = orderGroupName.substring(1);
										if (groupName.toUpperCase().equals(GamePanel.gp.getPlayerEntity().getGroupName().toUpperCase())) {
											groupName = GamePanel.gp.getPlayerEntity().getGroupName();
										}
									} else {
										// do nothing, because the cmd was spelt wrong 
									}
								} else {
									/** playerGroup member spawns closer*/
									distanceAgent = GameStates.streamFriendlyAgentSpawnDistance;
								}
								
								String emoticon = "";
								if (groupName.equals(GamePanel.gp.getPlayerEntity().getGroupName())) {
									/** playerGroup member spawns closer*/
									distanceAgent = GameStates.streamFriendlyAgentSpawnDistance;
									StreamViewerManager.get(sender).addCountPro(1);
									emoticon = "Keepo";
								} else {
									distanceAgent = GameStates.streamHostileAgentSpawnDistance;
									StreamViewerManager.get(sender).addCountContra(1);
									emoticon = "RalpherZ";
								}
								
								Random rand = new Random();
								float randValue = rand.nextFloat()*100;
								float plusMinusX = (float) Math.sin(randValue);
								float plusMinusY = (float) Math.cos(randValue);
								float randomShiftX = plusMinusX * distanceAgent;// + rand.nextFloat()*500;
								float randomShiftY = plusMinusY * distanceAgent;// + rand.nextFloat()*500;
								BigInteger agentID = AgentManager.getMaxID().add(BigInteger.valueOf(1));
								float x = GamePanel.gp.getPlayerEntity().getX() + randomShiftX;
								float y = GamePanel.gp.getPlayerEntity().getY() + randomShiftY;
								/* randomly variate the agent-types */
								TileSet tileSetAggressiveAgent;
								AggressiveAgent aggressiveAgent;
								
								int viewerLevel = StreamViewerManager.get(sender).getLevel();
								if (viewerLevel > 5) {
									// DarkWolf
									tileSetAggressiveAgent = TileSetManager.manager.getTileSet(GameStates.nameDarkWolf);
									aggressiveAgent = new AggressiveAgent(tileSetAggressiveAgent,sender+ ": Dark Wolf",GamePanel.gp.getPlayerEntity().getRoom(), agentID , x, y, GameStates.animationDelay, false);
								} else if (viewerLevel > 2) {
									// GreyWolf
									tileSetAggressiveAgent = TileSetManager.manager.getTileSet(GameStates.nameGreyWolf);
									aggressiveAgent = new AggressiveAgent(tileSetAggressiveAgent,sender +  ": Greywolf",GamePanel.gp.getPlayerEntity().getRoom(), agentID , x, y, GameStates.animationDelay, false);
								} else {
									// normal brownish wolf
									tileSetAggressiveAgent = TileSetManager.manager.getTileSet(GameStates.nameWolf);
									aggressiveAgent = new AggressiveAgent(tileSetAggressiveAgent,sender + ": Wolf",GamePanel.gp.getPlayerEntity().getRoom(), agentID , x, y, GameStates.animationDelay, false);
								}
								
								aggressiveAgent.setGroupName(groupName);
								aggressiveAgent.getAttack().setCreator(aggressiveAgent.getAttack().getCreator()+";player;"+GamePanel.gp.getPlayerEntity().getName()+";"+GamePanel.gp.getPlayerEntity().getId().toString());
								
								AgentManager.add(aggressiveAgent);
								AgentManager.addIRCUserAgent(sender, agentID);
								StreamViewerManager.get(sender).removeGold(GameStates.streamCostCreate);
								sendSafeMessage(sender+" created a new "+groupName +" member for "+GameStates.streamCostCreate+" gold! "+emoticon);
								TextEffectManager.add(new TextEffect(sender+" created a new "+groupName +" member"));
							} else {
								/** sender has not enough gold */
								sendSafeMessage(sender+", you have not enough gold! You need "+(GameStates.streamCostCreate-StreamViewerManager.get(sender).getGold())+" more gold! BibleThump");
							}
						} else {
							/** sender has already one agent under control */
							sendSafeMessage(sender+", unfortunately there are already too many creatures in the stream - just wait until any creature died...");
						}
					} else {
						/** sender has already one agent under control */
						sendSafeMessage(sender+", you have already one creature under control!");
					}
					
				}
				
				if (order.toUpperCase().startsWith("CG")) {
					if (AgentManager.ircUserAgentList.containsKey(sender) ) {
						if (order.substring(2).length() > 0) {
							if (StreamViewerManager.get(sender).getGold() >= GameStates.streamCostChangeGroup) {
								String orderGroupName = order.substring(2);
								String groupName = "";
								if (orderGroupName.startsWith(" ")) {
									groupName = orderGroupName.substring(1);
									String emoticon = "";
									if (groupName.toUpperCase().equals(GamePanel.gp.getPlayerEntity().getGroupName().toUpperCase())) {
										groupName = GamePanel.gp.getPlayerEntity().getGroupName();
										((AggressiveAgent) AgentManager.get(AgentManager.ircUserAgentList.get(sender))).setAttacking(false);
										emoticon = "FrankerZ";
									} else {
										emoticon = "BabyRage";
									}
									if (AgentManager.get(AgentManager.ircUserAgentList.get(sender)) instanceof AggressiveAgent) {
										((AggressiveAgent) AgentManager.get(AgentManager.ircUserAgentList.get(sender))).setGroupName(groupName);
									} else{
										AgentManager.get(AgentManager.ircUserAgentList.get(sender)).setGroupName(groupName);
									}
									
//									((AggressiveAgent) (AgentManager.get(AgentManager.ircUserAgentList.get(sender)))).getAttack().setGroupName(groupName);
									
									StreamViewerManager.get(sender).removeGold(GameStates.streamCostChangeGroup);
									sendSafeMessage(sender+" changed to "+groupName +"'s group for "+GameStates.streamCostChangeGroup +" gold! "+emoticon);
								} else {
									sendSafeMessage(sender+": you wrote: "+message+", please check the correct usage!");
									// do nothing, because the cmd was spelt wrong 
								}
								
								
								
							} else {
								/** sender has not enough gold */
								sendSafeMessage(sender+", you have not enough gold! You need "+(GameStates.streamCostChangeGroup-StreamViewerManager.get(sender).getGold())+" more gold! BibleThump");
							}
							
							
						} 
						
					} else {
						/** sender has already one agent under control */
						sendSafeMessage(sender+", you dont't have any creature under control");
					}
				}
				
				if (order.toUpperCase().startsWith("RED")) {
					if (order.substring(3).length() > 0) {
						if (StreamViewerManager.get(sender).getGold() >= GameStates.streamCostChangeColor) {
							String orderGroupName = order.substring(3);
							String colorValue = "";
							if (orderGroupName.startsWith(" ")) {
								colorValue = orderGroupName.substring(1,Math.min(4, orderGroupName.length()));
								if (colorValue.length() > 0) {
									try {
										int colorInt = Integer.parseInt(colorValue);
										int color = MyMath.normalizeToMinMax(colorInt, 0, 250);
										StreamViewerManager.get(sender).setRed(color);
										StreamViewerManager.get(sender).removeGold(GameStates.streamCostChangeColor);
										sendSafeMessage(sender+" changed RED to "+color +" for "+GameStates.streamCostChangeGroup +" gold! BloodTrail");
									} catch (NumberFormatException e) {
//										System.out.println("NumberformatException"+e);
										sendSafeMessage(sender+": you wrote: "+message+", please check the correct usage!");
									}
									
								}else {
									sendSafeMessage(sender+": you wrote: "+message+", please check the correct usage!");
									// do nothing, because the cmd was spelt wrong 
								}
							} else {
								sendSafeMessage(sender+": you wrote: "+message+", please check the correct usage!");
								// do nothing, because the cmd was spelt wrong 
							}
						} else {
							/** sender has not enough gold */
							sendSafeMessage(sender+", you have not enough gold! You need "+(GameStates.streamCostChangeColor-StreamViewerManager.get(sender).getGold())+" more gold! BibleThump");
						}	
					} 
				}
				if (order.toUpperCase().startsWith("GREEN")) {
					if (order.substring(5).length() > 0) {
						if (StreamViewerManager.get(sender).getGold() >= GameStates.streamCostChangeColor) {
							String orderGroupName = order.substring(5);
							String colorValue = "";
							if (orderGroupName.startsWith(" ")) {
								colorValue = orderGroupName.substring(1,Math.min(4, orderGroupName.length()));
								if (colorValue.length() > 0) {
									try {
										int colorInt = Integer.parseInt(colorValue);
										int color = MyMath.normalizeToMinMax(colorInt, 0, 250);
										StreamViewerManager.get(sender).setGreen(color);
										StreamViewerManager.get(sender).removeGold(GameStates.streamCostChangeColor);
										sendSafeMessage(sender+" changed GREEN to "+color +" for "+GameStates.streamCostChangeGroup +" gold! BrainSlug");
									} catch (NumberFormatException e) {
//										System.out.println("NumberformatException"+e);
										sendSafeMessage(sender+": you wrote: "+message+", please check the correct usage!");
									}
								}else {
									sendSafeMessage(sender+": you wrote: "+message+", please check the correct usage!");
									// do nothing, because the cmd was spelt wrong 
								}
							} else {
								sendSafeMessage(sender+": you wrote: "+message+", please check the correct usage!");
								// do nothing, because the cmd was spelt wrong 
							}
						} else {
							/** sender has not enough gold */
							sendSafeMessage(sender+", you have not enough gold! You need "+(GameStates.streamCostChangeColor-StreamViewerManager.get(sender).getGold())+" more gold! BibleThump");
						}	
					} 
				}
				if (order.toUpperCase().startsWith("BLUE")) {
					if (order.substring(4).length() > 0) {
						if (StreamViewerManager.get(sender).getGold() >= GameStates.streamCostChangeColor) {
							String orderGroupName = order.substring(4);
							String colorValue = "";
							if (orderGroupName.startsWith(" ")) {
								colorValue = orderGroupName.substring(1,Math.min(4, orderGroupName.length()));
								if (colorValue.length() > 0) {
									try {
										int colorInt = Integer.parseInt(colorValue);
										int color = MyMath.normalizeToMinMax(colorInt, 0, 250);
										StreamViewerManager.get(sender).setBlue(color);
										StreamViewerManager.get(sender).removeGold(GameStates.streamCostChangeColor);
										sendSafeMessage(sender+" changed BLUE to "+color +" for "+GameStates.streamCostChangeGroup +" gold! Volcania");
									} catch (NumberFormatException e) {
//										System.out.println("NumberformatException"+e);
										sendSafeMessage(sender+": you wrote: "+message+", please check the correct usage!");
									}
								}else {
									sendSafeMessage(sender+": you wrote: "+message+", please check the correct usage!");
									// do nothing, because the cmd was spelt wrong 
								}
							} else {
								sendSafeMessage(sender+": you wrote: "+message+", please check the correct usage!");
								// do nothing, because the cmd was spelt wrong 
							}
						} else {
							/** sender has not enough gold */
							sendSafeMessage(sender+", you have not enough gold! You need "+(GameStates.streamCostChangeColor-StreamViewerManager.get(sender).getGold())+" more gold! BibleThump");
						}	
					} 
				}
				
				if (order.toUpperCase().startsWith("FIND")) {
					if (AgentManager.ircUserAgentList.containsKey(sender) ) {
						if (order.substring(4).length() > 0) {
							if (StreamViewerManager.get(sender).getGold() >= GameStates.streamCostChangeGroup) {
								String orderGroupName = order.substring(4);
								String groupName = "";
								if (orderGroupName.startsWith(" ")) {
									groupName = orderGroupName.substring(1);
									if (groupName.toUpperCase().equals(GamePanel.gp.getPlayerEntity().getGroupName().toUpperCase())) {
										groupName = GamePanel.gp.getPlayerEntity().getGroupName();
									}
									if (groupName.toUpperCase().equals(AgentManager.get(AgentManager.ircUserAgentList.get(sender)).getGroupName().toUpperCase())) {
										// friendly search
									} else {
										// hostile search
									}
									
//									TODO: insert find entity here (if same group, don't attack!)
									
//									
									StreamViewerManager.get(sender).removeGold(GameStates.streamCostChangeGroup);
									sendSafeMessage(sender+" is searching "+groupName +"'s group for "+GameStates.streamCostFind+" gold!");
								} else {
									sendSafeMessage(sender+": you wrote: "+message+", please check the correct usage!");
									// do nothing, because the cmd was spelt wrong 
								}
								
								
								
							} else {
								/** sender has not enough gold */
								sendSafeMessage(sender+", you have not enough gold! You need "+(GameStates.streamCostChangeGroup-StreamViewerManager.get(sender).getGold())+" more gold! BibleThump");
							}
							
							
						} 
						
					} else {
						/** sender has already one agent under control */
						sendSafeMessage(sender+", you dont't have any creature under control");
					}
				}
				
				if (order.toUpperCase().startsWith("RESPAWN")) {
					if (AgentManager.ircUserAgentList.containsKey(sender) ) {
						if (StreamViewerManager.get(sender).getGold() >= GameStates.streamCostRespawn) {
							Agent streamAgent = AgentManager.get(AgentManager.ircUserAgentList.get(sender));
							
							float distanceAgent;
							if (!streamAgent.getGroupName().equals(GamePanel.gp.getPlayerEntity().getGroupName())) {
								distanceAgent = GameStates.streamHostileAgentSpawnDistance;
							} else {
								/** playerGroup member spawns closer*/
								distanceAgent = GameStates.streamFriendlyAgentSpawnDistance;
							}
							
							Random rand = new Random();
							float randValue = rand.nextFloat()*100;
							float plusMinusX = (float) Math.sin(randValue);
							float plusMinusY = (float) Math.cos(randValue);
							float randomShiftX = plusMinusX * distanceAgent;// + rand.nextFloat()*500;
							float randomShiftY = plusMinusY * distanceAgent;// + rand.nextFloat()*500;
							float x = GamePanel.gp.getPlayerEntity().getX() + randomShiftX;
							float y = GamePanel.gp.getPlayerEntity().getY() + randomShiftY;
							streamAgent.setX(x);
							streamAgent.setY(y);
							streamAgent.setGoalList(new ArrayList<Goal>());
							streamAgent.setActualGoal(null);
							
							StreamViewerManager.get(sender).removeGold(GameStates.streamCostRespawn);
							sendSafeMessage(sender+" just respawned for "+GameStates.streamCostRespawn+" gold! B)");
						} else {
							/** sender has not enough gold */
							sendSafeMessage(sender+", you have not enough gold! You need "+(GameStates.streamCostRespawn-StreamViewerManager.get(sender).getGold())+" more gold! BibleThump");
						}
					} else {
						/** no create under control */ 
						sendSafeMessage(sender+", you have no creature under control");
					}
				}
				
				/** create a drop */
				if (order.toUpperCase().startsWith("DROP") ) {
					if (StreamViewerManager.get(sender).getGold() >= GameStates.streamCostDropItem) {
						
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
						
						Item dropItem = null;						
						
						if (order.substring(4).length() > 0) {
							String itemDropAddon = order.substring(4);
							String itemDrop = "";
							if (itemDropAddon.startsWith(" ")) {
								itemDrop = itemDropAddon.substring(1);
								if (itemDrop.toUpperCase().startsWith("AXE")) {
									dropItem = ItemFactoryCollection.getItemFactory(ITEMCODE.WEAPON).getItem("StoneAxe");
								} else if (itemDrop.toUpperCase().startsWith("APPLE")) {
									dropItem = ItemFactoryCollection.getItemFactory(ITEMCODE.APPLE).getItem("Apple");
									dropItem.setCount(3);
								} else if (itemDrop.toUpperCase().startsWith("LIFE")) {
									dropItem = ItemFactoryCollection.getItemFactory(ITEMCODE.SMALLPOTIONOFLIFE).getItem("SmallPotionOfLife");
									dropItem.setCount(2);
								} else if (itemDrop.toUpperCase().startsWith("WOOD")) {
									dropItem = ItemFactoryCollection.getItemFactory(ITEMCODE.MATERIAL).getItem("Wood");
									dropItem.setCount(30);
								} else if (itemDrop.toUpperCase().startsWith("STONE")) {
									dropItem = ItemFactoryCollection.getItemFactory(ITEMCODE.MATERIAL).getItem("Stone");
									dropItem.setCount(10);
								} else if (itemDrop.toUpperCase().startsWith("ARTIFACTSTONE")) {
									dropItem = ItemFactoryCollection.getItemFactory(ITEMCODE.MATERIAL).getItem("ArtifactStone");
									dropItem.setCount(1);
								} else if (itemDrop.toUpperCase().startsWith("CAMPFIRE")) {
									dropItem = ItemFactoryCollection.getItemFactory(ITEMCODE.MATERIAL).getItem("Campfire");
									dropItem.setCount(3);
								} else if (itemDrop.toUpperCase().startsWith("HAIR")) {
									dropItem = ItemFactoryCollection.getItemFactory(ITEMCODE.HELMET).getItem("BlondHair");
								} else if (itemDrop.toUpperCase().startsWith("HELMET")) {
									dropItem = ItemFactoryCollection.getItemFactory(ITEMCODE.HELMET).getItem("LeatherHelmet");
								} else if (itemDrop.toUpperCase().startsWith("CLOAK")) {
									dropItem = ItemFactoryCollection.getItemFactory(ITEMCODE.BODY).getItem("Cloak");
								} else if (itemDrop.toUpperCase().startsWith("SHOE")) {
									dropItem = ItemFactoryCollection.getItemFactory(ITEMCODE.FEET).getItem("Shoe");
								} else if (itemDrop.toUpperCase().startsWith("SHIELD")) {
									dropItem = ItemFactoryCollection.getItemFactory(ITEMCODE.SHIELD).getItem("LeatherShield");
								} else if (itemDrop.toUpperCase().startsWith("SHIRT")) {
									dropItem = ItemFactoryCollection.getItemFactory(ITEMCODE.SHIRT).getItem("Greenshirt");
								} else if (itemDrop.toUpperCase().startsWith("CLOAK")) {
									dropItem = ItemFactoryCollection.getItemFactory(ITEMCODE.BODY).getItem("Cloak");
								} else if (itemDrop.toUpperCase().startsWith("PANTS")) {
									dropItem = ItemFactoryCollection.getItemFactory(ITEMCODE.TROUSERS).getItem("Leatherpants");
								} else if (itemDrop.toUpperCase().startsWith("SWORD")) {
									dropItem = ItemFactoryCollection.getItemFactory(ITEMCODE.WEAPON).getItem("WoodenSword");
								} else if (itemDrop.toUpperCase().startsWith("AMULET")) {
									dropItem = ItemFactoryCollection.getItemFactory(ITEMCODE.AMULET).getItem("GoldenAmulet");
								} else if (itemDrop.toUpperCase().startsWith("GLOVE")) {
									dropItem = ItemFactoryCollection.getItemFactory(ITEMCODE.GLOVE).getItem("LeatherGlove");
								} 
								
							} else {
								// command was spelled wrong, so just drop a random item 
								dropItem = ItemFactoryCollection.createRandomItem(Integer.valueOf(StreamViewerManager.get(sender).getLevel()), itemId, GamePanel.gp.getPlayerEntity().getRoom(), dropX, dropY);
							}
						} else {
							dropItem = ItemFactoryCollection.createRandomItem(Integer.valueOf(StreamViewerManager.get(sender).getLevel()), itemId, GamePanel.gp.getPlayerEntity().getRoom(), dropX, dropY);
						}
						
						dropItem.setId(itemId);
						dropItem.getEntity().setX(dropX);
						dropItem.getEntity().setY(dropY);
						dropItem.setRoom(GamePanel.gp.getPlayerEntity().getRoom());
						
//						Item dropItem = ItemFactoryCollection.createRandomItem(Integer.valueOf(StreamViewerManager.get(sender).getLevel()), itemId, GamePanel.gp.getPlayerEntity().getRoom(), dropX, dropY);

	//					System.out.println("added new wood object: "+itemId);
						
						/** send the complete Item to all players of the channel */
						if (dropItem != null && GameWindow.gw.isLoggedIn() && GamePanel.gp.isInitializedPlayer()) {
							/** first send to server for the itemList */
							if (dropItem instanceof Stackable) {
								GUI.gui.gameInfoConsole.appendSimpleDate(sender+" dropped "+dropItem.getCount()+" "+dropItem.getName()+"(s)");
							} else {
								dropItem.setComment(" of "+sender);
							}
							
							GameWindow.gw.send(ClientMessages.addItem(dropItem.getId(), dropItem.getItemCode(), dropItem.getCount(), dropItem.getCapacity(), dropItem.getEntity().getX(), dropItem.getEntity().getY(), dropItem.getEntity().getMX(), dropItem.getEntity().getMY(), dropItem.getName(), dropItem.getComment(),dropItem.getRoom(), dropItem.getEntity().getTileSet().getFileName(), dropItem.getEntity().getName(), dropItem.getAttributes()));
							for (String channelName : GameWindow.gw.getSpaceChannels().values()) {
								ClientChannel clientChannel = GameWindow.gw.getChannelByName(channelName);
								try {
									clientChannel.send(ClientMessages.addCompleteItem(dropItem.getItemCode(), dropItem.getId(), dropItem.getName(), dropItem.getComment(), dropItem.getRoom(), dropItem.getEntity().getX(), dropItem.getEntity().getY(), dropItem.getCount(), dropItem.getAttributes()));
								} catch (IOException e) {
									e.printStackTrace();
								}
							}
							StreamViewerManager.get(sender).addCountDroppedItems(1);
							StreamViewerManager.get(sender).removeGold(GameStates.streamCostDropItem);
							String addCount = "";
							if (dropItem.getCount() > 1) addCount = " x "+dropItem.getCount();
							sendSafeMessage(sender+" dropped for "+GameStates.streamCostDropItem+" gold a level-"+StreamViewerManager.get(sender).getLevel()+" item: "+dropItem.getName()+addCount+" <3");
						} else {
							sendSafeMessage(sender+" tried to drop an item, but it has failed - just try it again! (You haven't lost the gold)");
						}
					} else {
						/** sender has not enough gold */
						sendSafeMessage(sender+", you do not have enough gold! You need "+(GameStates.streamCostDropItem-StreamViewerManager.get(sender).getGold())+" more gold! BibleThump");
					}
				}
				
				if (order.toUpperCase().startsWith("STEAL") ) {
					if (StreamViewerManager.get(sender).getGold() >= GameStates.streamCostStealItem) {
						boolean correctCommand = false;
						String whichItem = "";
						Container whichContainer = null;
						if (order.substring(5).length() > 0) {
							String stealItem = order.substring(5);
							if (stealItem.startsWith(" ")) {
								whichItem = stealItem.substring(1);
								if (whichItem.toUpperCase().equals("HELMET")) {
									whichContainer = GamePanel.gp.getPlayerEntity().getEquipmentHelmet();
									correctCommand = true;
								}
								if (whichItem.toUpperCase().equals("PANTS")) {
									whichContainer = GamePanel.gp.getPlayerEntity().getEquipmentTrousers();
									correctCommand = true;
								}
								if (whichItem.toUpperCase().equals("GLOVE")) {
									whichContainer = GamePanel.gp.getPlayerEntity().getEquipmentGlove();
									correctCommand = true;
								}
								if (whichItem.toUpperCase().equals("WEAPON")) {
									whichContainer = GamePanel.gp.getPlayerEntity().getEquipmentWeapon();
									correctCommand = true;
								}
								if (whichItem.toUpperCase().equals("SHOES")) {
									whichContainer = GamePanel.gp.getPlayerEntity().getEquipmentFeet();
									correctCommand = true;
								}
								if (whichItem.toUpperCase().equals("SHIELD")) {
									whichContainer = GamePanel.gp.getPlayerEntity().getEquipmentShield();
									correctCommand = true;
								}
								if (whichItem.toUpperCase().equals("ARMOR")) {
									whichContainer = GamePanel.gp.getPlayerEntity().getEquipmentBody();
									correctCommand = true;
								}
								if (whichItem.toUpperCase().equals("AMULET")) {
									whichContainer = GamePanel.gp.getPlayerEntity().getEquipmentAmulet();
									correctCommand = true;
								}
								if (whichItem.toUpperCase().equals("SHIRT")) {
									whichContainer = GamePanel.gp.getPlayerEntity().getEquipmentShirt();
									correctCommand = true;
								}
								if (whichItem.toUpperCase().equals("INVENTORY")) {
									whichContainer = GamePanel.gp.getPlayerEntity().getInventoryUse();
									correctCommand = true;
								}
								if (whichItem.toUpperCase().equals("BACKPACK")) {
									whichContainer = GamePanel.gp.getPlayerEntity().getInventory();
									correctCommand = true;
								}
							} else {
								
							}
						} else {
							
						}
						
						/** only continue if the command was correct */
						if (correctCommand) {
							ArrayList<BigInteger> deleteList = new ArrayList<BigInteger>();
							for (Item removeItem: whichContainer.getItemList().values()) {
								if (removeItem.getComment().equals("")) {
									if (StreamViewerManager.get(sender).getGold() >= GameStates.streamCostStealItem) {
										deleteList.add(removeItem.getId());
										StreamViewerManager.get(sender).addCountContra(1);
										StreamViewerManager.get(sender).removeGold(GameStates.streamCostStealItem);
										sendSafeMessage(sender+" stole for "+GameStates.streamCostStealItem+" gold the "+whichItem+" item!");
										GUI.gui.gameInfoConsole.appendSimpleDate(sender+" stole the "+whichItem+" item to annoy "+GamePanel.gp.getPlayerEntity().getName());
										TextEffectManager.add(new TextEffect(sender+" stole the "+whichItem+" item to annoy "+GamePanel.gp.getPlayerEntity().getName()+" R)"));
									} else {
										/** sender has not enough gold */
										sendSafeMessage(sender+", you do not have enough gold! You need "+(GameStates.streamCostDropItem-StreamViewerManager.get(sender).getGold())+" more gold! BibleThump");
									}
								} else {
									sendSafeMessage(sender+" tried to steal the "+removeItem.getName()+" "+removeItem.getComment()+", but you can only HIDE personalized items!");
									GUI.gui.gameInfoConsole.appendSimpleDate(sender+" tried to steal the "+removeItem.getName()+" "+removeItem.getComment()+"!");
									TextEffectManager.add(new TextEffect(sender+" tried to steal the "+removeItem.getName()+" "+removeItem.getComment()+"!"));
//									TextEffectManager.add(new TextEffect("But you can only hide personalized items!"));
								}
							}
							
//							/** now delete those items */
//							for (BigInteger id : deleteList) {
//								whichContainer.getItemList().remove(id);
//							}
							
							BigInteger itemID;
							for (int i = 0; i < whichContainer.getHeight(); i++) {
					    		for (int j = 0; j < whichContainer.getWidth(); j++) {
					    			itemID = whichContainer.getContainerArray()[i][j];
					    			if (itemID.equals(BigInteger.valueOf(-1))) {
//					    				GameWindow.gw.send(ClientMessages.clearContainerPosition(inventory.getContainerType(), i, j));
					    			} else {
					    				if (deleteList.contains(itemID)) {
					    					whichContainer.getContainerArray()[i][j] = BigInteger.valueOf(-1);
					    					whichContainer.getItemList().remove(itemID);
					    				} else {
					    					
					    				}
//					    				tempItem = inventory.getItemList().get(inventory.getContainerArray()[i][j]);
//										GameWindow.gw.send(ClientMessages.addItemToContainer(inventory.getContainerType(), tempItem.getId(), tempItem.getItemCode(), tempItem.getCount(), tempItem.getCapacity(), tempItem.getEntity().getX(), tempItem.getEntity().getY(), tempItem.getEntity().getMX(), tempItem.getEntity().getMY(), tempItem.getName(), tempItem.getComment(), tempItem.getRoom(), tempItem.getEntity().getTileSet().getFileName(), tempItem.getEntity().getName(), tempItem.getAttributes(), i, j));
					    			}    	    			
					    		}
					    	}
							
							/** update server */
							ClientMessageHandler.containerUpdateSendBrutal(whichContainer);
							GamePanel.gp.getPlayerEntity().calculateTotalEquipmentInfluenceAndUpdateVisuals();
						} else {
							sendSafeMessage(sender+": you couldn't steal, because something went wrong - check if you spelled everything correctly.");
						}
						
						

					} else {
						/** sender has not enough gold */
						sendSafeMessage(sender+", you do not have enough gold! You need "+(GameStates.streamCostStealItem-StreamViewerManager.get(sender).getGold())+" more gold! BibleThump");
					}
							
				}
				
				if (order.toUpperCase().startsWith("HIDE") ) {
					if (StreamViewerManager.get(sender).getGold() >= GameStates.streamCostHideItem) {
						boolean correctCommand = false;
						String whichItem = "";
						Container whichContainer = null;
						if (order.substring(4).length() > 0) {
							String stealItem = order.substring(4);
							if (stealItem.startsWith(" ")) {
								whichItem = stealItem.substring(1);
								if (whichItem.toUpperCase().equals("HELMET")) {
									whichContainer = GamePanel.gp.getPlayerEntity().getEquipmentHelmet();
									correctCommand = true;
								}
								if (whichItem.toUpperCase().equals("PANTS")) {
									whichContainer = GamePanel.gp.getPlayerEntity().getEquipmentTrousers();
									correctCommand = true;
								}
								if (whichItem.toUpperCase().equals("GLOVE")) {
									whichContainer = GamePanel.gp.getPlayerEntity().getEquipmentGlove();
									correctCommand = true;
								}
								if (whichItem.toUpperCase().equals("WEAPON")) {
									whichContainer = GamePanel.gp.getPlayerEntity().getEquipmentWeapon();
									correctCommand = true;
								}
								if (whichItem.toUpperCase().equals("SHOES")) {
									whichContainer = GamePanel.gp.getPlayerEntity().getEquipmentFeet();
									correctCommand = true;
								}
								if (whichItem.toUpperCase().equals("SHIELD")) {
									whichContainer = GamePanel.gp.getPlayerEntity().getEquipmentShield();
									correctCommand = true;
								}
								if (whichItem.toUpperCase().equals("ARMOR")) {
									whichContainer = GamePanel.gp.getPlayerEntity().getEquipmentBody();
									correctCommand = true;
								}
								if (whichItem.toUpperCase().equals("AMULET")) {
									whichContainer = GamePanel.gp.getPlayerEntity().getEquipmentAmulet();
									correctCommand = true;
								}
								if (whichItem.toUpperCase().equals("SHIRT")) {
									whichContainer = GamePanel.gp.getPlayerEntity().getEquipmentShirt();
									correctCommand = true;
								}
								if (whichItem.toUpperCase().equals("INVENTORY")) {
									whichContainer = GamePanel.gp.getPlayerEntity().getInventoryUse();
									correctCommand = true;
								}
								if (whichItem.toUpperCase().equals("BACKPACK")) {
									whichContainer = GamePanel.gp.getPlayerEntity().getInventory();
									correctCommand = true;
								}
							} else {
								
							}
						} else {
							
						}
						
						/** only continue if the command was correct */
						if (correctCommand) {
							ArrayList<BigInteger> deleteList = new ArrayList<BigInteger>();
							for (Item dropItem : whichContainer.getItemList().values()) {
								if (StreamViewerManager.get(sender).getGold() >= GameStates.streamCostHideItem) {
									/** send the complete Item to all players of the channel */
									if (dropItem != null && GameWindow.gw.isLoggedIn() && GamePanel.gp.isInitializedPlayer()) {
										/** first send to server for the itemList */
										
										Random rand = new Random();
										float randValue = (float) ((rand.nextFloat()*2*Math.PI));//Math.PI/2)+ Math.PI/4);
										float plusMinusX = (float) Math.sin(randValue);
										float plusMinusY = (float) Math.cos(randValue);
										float randomShiftX = plusMinusX * GameStates.dropDistanceHideItems;// + rand.nextFloat()*500;
										float randomShiftY = plusMinusY * GameStates.dropDistanceHideItems;// + rand.nextFloat()*500;
										float dropX = GamePanel.gp.getPlayerEntity().getX()+GamePanel.gp.getPlayerEntity().getWidth()/2 - GameStates.itemTileWidth/2 + randomShiftX;
										float dropY = GamePanel.gp.getPlayerEntity().getY()+GamePanel.gp.getPlayerEntity().getHeight()/2 - GameStates.itemTileHeight/2 + randomShiftY;
										
										dropItem.getEntity().setX(dropX);
										dropItem.getEntity().setY(dropY);
										dropItem.setRoom(GamePanel.gp.getPlayerEntity().getRoom());
										
										GameWindow.gw.send(ClientMessages.addItem(dropItem.getId(), dropItem.getItemCode(), dropItem.getCount(), dropItem.getCapacity(), dropItem.getEntity().getX(), dropItem.getEntity().getY(), dropItem.getEntity().getMX(), dropItem.getEntity().getMY(), dropItem.getName(), dropItem.getComment(),dropItem.getRoom(), dropItem.getEntity().getTileSet().getFileName(), dropItem.getEntity().getName(), dropItem.getAttributes()));
										for (String channelName : GameWindow.gw.getSpaceChannels().values()) {
											ClientChannel clientChannel = GameWindow.gw.getChannelByName(channelName);
											try {
												clientChannel.send(ClientMessages.addCompleteItem(dropItem.getItemCode(), dropItem.getId(), dropItem.getName(), dropItem.getComment(), dropItem.getRoom(), dropItem.getEntity().getX(), dropItem.getEntity().getY(), dropItem.getCount(), dropItem.getAttributes()));
											} catch (IOException e) {
												e.printStackTrace();
											}
										}
										
										/** update server */
										deleteList.add(dropItem.getId());

										StreamViewerManager.get(sender).addCountContra(1);
										StreamViewerManager.get(sender).removeGold(GameStates.streamCostHideItem);
										sendSafeMessage(sender+" hid for "+GameStates.streamCostHideItem+" gold a "+whichItem+" item somewhere arround to annoy "+GamePanel.gp.getPlayerEntity().getName()+" R)");
										GUI.gui.gameInfoConsole.appendSimpleDate(sender+" hid a "+whichItem+" item somewhere arround to annoy "+GamePanel.gp.getPlayerEntity().getName());
										if (!dropItem.getComment().equals("")) {
											TextEffectManager.add(new TextEffect(sender+" hid a "+dropItem.getName()+" "+dropItem.getComment()+" somewhere arround to annoy "+GamePanel.gp.getPlayerEntity().getName()));
										}
									} else {
										sendSafeMessage(sender+" tried to drop an item, but it has failed - just try it again! (You haven't lost the gold)");
									}
								} else {
									
								}
							}
							
//							for (BigInteger id : deleteList) {
//								whichContainer.getItemList().remove(id);
//							}
							BigInteger itemID;
							for (int i = 0; i < whichContainer.getHeight(); i++) {
					    		for (int j = 0; j < whichContainer.getWidth(); j++) {
					    			itemID = whichContainer.getContainerArray()[i][j];
					    			if (itemID.equals(BigInteger.valueOf(-1))) {
//					    				GameWindow.gw.send(ClientMessages.clearContainerPosition(inventory.getContainerType(), i, j));
					    			} else {
					    				if (deleteList.contains(itemID)) {
					    					whichContainer.getContainerArray()[i][j] = BigInteger.valueOf(-1);
					    					whichContainer.getItemList().remove(itemID);
					    				} else {
					    					
					    				}
//					    				tempItem = inventory.getItemList().get(inventory.getContainerArray()[i][j]);
//										GameWindow.gw.send(ClientMessages.addItemToContainer(inventory.getContainerType(), tempItem.getId(), tempItem.getItemCode(), tempItem.getCount(), tempItem.getCapacity(), tempItem.getEntity().getX(), tempItem.getEntity().getY(), tempItem.getEntity().getMX(), tempItem.getEntity().getMY(), tempItem.getName(), tempItem.getComment(), tempItem.getRoom(), tempItem.getEntity().getTileSet().getFileName(), tempItem.getEntity().getName(), tempItem.getAttributes(), i, j));
					    			}    	    			
					    		}
					    	}
							
							ClientMessageHandler.containerUpdateSendBrutal(whichContainer);
							GamePanel.gp.getPlayerEntity().calculateTotalEquipmentInfluenceAndUpdateVisuals();
							
						} else {
							sendSafeMessage(sender+": you couldn't steal, because something went wrong - check if you spelled everything correctly.");
						}

					} else {
						/** sender has not enough gold */
						sendSafeMessage(sender+", you do not have enough gold! You need "+(GameStates.streamCostHideItem-StreamViewerManager.get(sender).getGold())+" more gold! BibleThump");
					}
							
				}
				
				if (order.toUpperCase().startsWith("SENDGOLD") ) {
					if (StreamViewerManager.get(sender).getGold() >= GameStates.streamCostStealItem) {
						if (order.substring(8).length() > 0) {
							boolean correctCommand = true;
							String[] orders = order.split(" ");
							String recipient = "";
							if (orders.length == 3) {
								if (orders[0].toUpperCase().equals("SENDGOLD")) {
									// OK
								} else correctCommand = false;
								if ((orders[1].length() > 0 ) && GameWindow.gw.getMySQL().contains("name", (orders[1]))) {
//									if (!orders[1].toUpperCase().equals(sender.toUpperCase())) {
									String recipientTemp = orders[1];
									recipient = recipientTemp.substring(0, 1).toUpperCase() + recipientTemp.substring(1);
//									} else correctCommand = false;
									
								} else  {
									correctCommand = false;
									sendSafeMessage(sender+": "+orders[1]+" has never entered the Stream of Tales!");
								}
								
								int sendGoldValueTemp = 0;
								int sendGoldValue = 0;
								if ((orders[2].length()> 0 )) {
									try {
										sendGoldValueTemp = Integer.parseInt(orders[2]);
										sendGoldValue = MyMath.normalizeToMinMax(sendGoldValueTemp, 0, Integer.MAX_VALUE);
										
									} catch (NumberFormatException e) {
//											System.out.println("NumberformatException"+e);
										sendSafeMessage(sender+": you wrote: "+message+", please check the correct usage!");
									}
								} else correctCommand = false;
								
								if (correctCommand) {
									/** check if it is possible to send the gold*/
									if (StreamViewerManager.get(sender).getGold() >= GameStates.streamCostSendGold+sendGoldValue) {
										StreamViewerManager.get(sender).removeGold(sendGoldValue+GameStates.streamCostSendGold);
										if  (StreamViewerManager.contains(recipient)) {
											StreamViewerManager.get(recipient).addGold(sendGoldValue);
										}
										String[] colNames = new String[]{"gold"};
										String[] getValues = GameWindow.gw.getMySQL().getData("name", recipient, colNames);
										for (int i = 0; i < getValues.length; i++) {
											if (colNames[i].equals("gold")) {
												int actualGold = Integer.parseInt(getValues[i]);
												int newGold = actualGold+ sendGoldValue;
												String[] values = new String[]{""+newGold};
												GameWindow.gw.getMySQL().update("name", recipient, colNames, values);
											}
					
										}
										sendSafeMessage(sender+" sent  "+sendGoldValue +" to "+ recipient+" for "+GameStates.streamCostSendGold +" gold!");
									} else {
										sendSafeMessage(sender+", you don't have enough gold! You'll need at least "+(GameStates.streamCostSendGold+sendGoldValue)+" gold for this transaction!");
									}	
								} else {
									sendSafeMessage(sender+": you wrote: "+message+", please check the correct usage!");
								}
							} else {
								sendSafeMessage(sender+": you wrote: "+message+", please check the correct usage!");
							}
						} else {
							sendSafeMessage(sender+": you wrote: "+message+", please check the correct usage!");
						}
					}
					
				}
				
				if (order.toUpperCase().startsWith("GOLD")) {
					sendSafeMessage(sender+" has "+StreamViewerManager.get(sender).getGold()+" gold");
				}
				
				if (order.toUpperCase().startsWith("EXP") || order.toUpperCase().startsWith("LEVEL") || order.toUpperCase().startsWith("LVL")) {
					sendSafeMessage(sender+" has "+StreamViewerManager.get(sender).getExperience()+" experience and is level "+StreamViewerManager.get(sender).getLevel());
				}
				
				if (order.toUpperCase().startsWith("KILLEDENEMIES")) {
					sendSafeMessage(sender+" has killed "+StreamViewerManager.get(sender).getCountKilledEnemies()+" enemies!");
				}
				
				if (order.toUpperCase().startsWith("PRO")) {
					sendSafeMessage(sender+" has helped "+StreamViewerManager.get(sender).getCountPro()+" times!");
				}
				if (order.toUpperCase().startsWith("CONTRA")) {
					sendSafeMessage(sender+" has interfered "+StreamViewerManager.get(sender).getCountContra()+" times!");
				}
				
				if (order.toUpperCase().startsWith("MESS")) {
					sendSafeMessage(sender+" has written "+StreamViewerManager.get(sender).getCountIngameMessages()+" times!");
				}
				
				if (order.toUpperCase().startsWith("DROPPEDITEMS")) {
					sendSafeMessage(sender+" has dropped "+StreamViewerManager.get(sender).getCountDroppedItems()+" items!");
				}
				
				if (order.startsWith(" ")) {
					String chatMessage = order.substring(1);
					if (chatMessage.length() > 0) {
						GUI.gui.gameInfoConsole.appendSimpleDate(sender+": "+chatMessage);
						/** if there is an agent, that was created by the sender, add one message to the agent messageboard */
						if (AgentManager.ircUserAgentList.containsKey(sender)) {
							BigInteger senderAgentID = AgentManager.ircUserAgentList.get(sender);
							AgentManager.get(senderAgentID).addMessage(chatMessage);
						}
						StreamViewerManager.get(sender).addCountIngameMessages(1);
					}
				}
				
				if (order.toUpperCase().startsWith("HELP") || order.toUpperCase().startsWith("COMMAND") || order.toUpperCase().startsWith("?")) {
					ArrayList<String> helpLines = getHelp(); 
					String finalString = "";
					for (String line : helpLines) {
						finalString = finalString+line+" ";
					}
					sendSafeMessage(finalString);
				}
			} else { /** not in the streamviewermanager yet */
				/** streamviewer is not in the stream of tales yet */
//				if (message.startsWith("!")) {
					String order = message.substring(1);
					if (order.toUpperCase().startsWith("ENTER")) {
						if (!StreamViewerManager.contains(sender)) {
							StreamViewerManager.add(sender);
							String[] colNames = new String[]{"name", "comment", "experience", "level", "gold", "countPro", "countContra", "countKilledEnemies", "countKilledPlayers", "countIngameMessages",  "countDroppedItems", "red", "green", "blue"};
							String[] values = new String[]{sender, "no comment", "0", "1", "10", "0", "0", "0", "0",  "0", "0", "250", "250", "250"};
							if (!GameWindow.gw.getMySQL().contains("name", sender)) {
								GameWindow.gw.getMySQL().insert(colNames, values);
								sendSafeMessage(sender+" has entered the Stream of Tales for the first time! :D");
								TextEffectManager.add(new TextEffect(sender+" has entered the Stream of Tales for the first time!"));
							} else {
								String[] getValues = GameWindow.gw.getMySQL().getData("name", sender, colNames);
								for (int i = 0; i < getValues.length; i++) {
									if (colNames[i].equals("name")) ;
									else if (colNames[i].equals("comment")) StreamViewerManager.get(sender).setComment(getValues[i]);
									else if (colNames[i].equals("experience")) StreamViewerManager.get(sender).setExperience(Integer.parseInt(getValues[i]));
									else if (colNames[i].equals("level")) StreamViewerManager.get(sender).setLevel(Integer.parseInt(getValues[i]));
									else if (colNames[i].equals("gold")) StreamViewerManager.get(sender).setGold(Integer.parseInt(getValues[i]));
									else if (colNames[i].equals("countPro")) StreamViewerManager.get(sender).setCountPro(Integer.parseInt(getValues[i]));
									else if (colNames[i].equals("countContra")) StreamViewerManager.get(sender).setCountContra(Integer.parseInt(getValues[i]));
									else if (colNames[i].equals("countKilledEnemies")) StreamViewerManager.get(sender).setCountKilledEnemies(Integer.parseInt(getValues[i]));
									else if (colNames[i].equals("countKilledPlayers")) StreamViewerManager.get(sender).setCountKilledPlayers(Integer.parseInt(getValues[i]));
									else if (colNames[i].equals("countIngameMessages")) StreamViewerManager.get(sender).setCountIngameMessages(Integer.parseInt(getValues[i]));
									else if (colNames[i].equals("countDroppedItems")) StreamViewerManager.get(sender).setCountDroppedItems(Integer.parseInt(getValues[i]));
									else if (colNames[i].equals("red")) StreamViewerManager.get(sender).setRed(Integer.parseInt(getValues[i]));
									else if (colNames[i].equals("green")) StreamViewerManager.get(sender).setGreen(Integer.parseInt(getValues[i]));
									else if (colNames[i].equals("blue")) StreamViewerManager.get(sender).setBlue(Integer.parseInt(getValues[i]));
									
								}
								sendSafeMessage(sender+" has entered the Stream of Tales! :D");
								TextEffectManager.add(new TextEffect(sender+" has entered the Stream of Tales!"));
							}
							
						} else {
							sendSafeMessage(sender+" is already in the Stream of Tales!");
						}
					
					} else {
						sendSafeMessage(sender+", you have not entered yet! Just type '!enter' and become a part of the Stream of Tales!");
					}
					/** disable, because antifraud..anticheat*/
		//			if (order.toUpperCase().startsWith("leave")) {
		//				if (StreamViewerManager.contains(sender)) {
		//					StreamViewerManager.streamViewerList.remove(sender);
		//				}
		//			}
									
				}
			
				String order = message.substring(1);
				if (order.toUpperCase().startsWith("DELETEMYDATA")) {
					if (GameWindow.gw.getMySQL().contains("name", sender)) {
							GameWindow.gw.getMySQL().delete("name", sender);
							StreamViewerManager.streamViewerList.remove(sender);
							if (AgentManager.ircUserAgentList.containsKey(sender) ) {
								AgentManager.ircUserAgentList.remove(sender);
							}
							sendSafeMessage(sender+" has deleted all his/ her data in the Stream of Tales!");
					}
				}
			} else { /** not starting with a "!" */
				// ignore those messages
				GUI.gui.gameInfoConsole.appendSimpleDate(sender+": "+message);
			}
		
//		if (message.startsWith("!")) {
//			String order = message.substring(1);
//			if (order.toUpperCase().startsWith("DELETEMYDATA")) {
//				if (GameWindow.gw.getMySQL().contains("name", sender)) {
//					GameWindow.gw.getMySQL().delete("name", sender);
//					StreamViewerManager.streamViewerList.remove(sender);
//					sendSafeMessage(sender+" has deleted all his/ her data in the Stream of Tales!");
//				}
//			}
//		}
	}
	
	public ArrayList<String> getHelp() {
		ArrayList<String> retStrings = new ArrayList<String>();
		retStrings.add("You can use the following commands:");
		retStrings.add("| '! text': chat in my game (remember to use one SPACE after !)");
		retStrings.add("| '!create': add one random enemy to the game");
		retStrings.add("| '!darkwolf': add one dark wolf in the game");
		retStrings.add("| '!bigwolf': add one big wolf in the game");
//		retStrings.add("| '!users': list all users of this channel");
		
		
		return retStrings;
	}
	
	/** only send the message if we are still under the message limit */
	public void sendSafeMessage(String message) {
		int actualMessageCount = this.checkActiveMessageCount();
		if ( ( ( actualMessageCount<= countMessageLimitModerator) && (this.isModerator()) ) || ( (actualMessageCount <= countMessageLimitNonModerator)  && (!this.isModerator()) )  ) {
			Long timeNow = Long.valueOf(System.currentTimeMillis()); 
			String fullMessage = "PRIVMSG " +this.getChannelName() + " :" + message;
			super.sendMessageToServer(fullMessage);
			messageList.add(timeNow);
		} else {
			System.out.println("Warning: you sent too many messages during the last timespan!");
		}
    }
	
	/*** check the actual count of messages during the given time span timeLimit */ 
	public int checkActiveMessageCount() {
		int retValue = 0;
		long timeNow = System.currentTimeMillis();
		ArrayList<Long> afterList = new ArrayList<Long>();
		for (Long messageTime : messageList) {
			if (timeNow - messageTime.longValue() > timeLimit) {
				/** this timeStamp should be ignored from now on */
			} else {
				afterList.add(messageTime);
			}
		}
		messageList = afterList;
		retValue = messageList.size();
//		System.out.println("I sent "+retValue+" messages before this message during the last "+timeLimit+" milliseconds");
		return retValue;
	}
	
	public List<Long> getMessageList() {
		return messageList;
	}

	public void setMessageList(List<Long> messageList) {
		this.messageList = messageList;
	}



	public boolean isModerator() {
		return moderator;
	}



	public void setModerator(boolean moderator) {
		this.moderator = moderator;
	}

}
