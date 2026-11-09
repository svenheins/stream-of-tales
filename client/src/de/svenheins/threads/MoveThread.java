/* Copyright (C) Sven Heins - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * author: Sven Heins
 * date: 05.07.2015
 */
package de.svenheins.threads;


import java.awt.Point;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import de.svenheins.functions.MyMath;
import de.svenheins.main.AttributeType;
import de.svenheins.main.EntityStates;
import de.svenheins.main.GUI;
import de.svenheins.main.GameModus;
import de.svenheins.main.GamePanel;
import de.svenheins.main.GameStates;
import de.svenheins.main.GameWindow;
import de.svenheins.main.PlayerProperty;
import de.svenheins.main.TileType;
import de.svenheins.managers.AgentManager;
import de.svenheins.managers.ItemManager;
import de.svenheins.managers.MainMenuEntityManager;
import de.svenheins.managers.ObjectMapManager;
import de.svenheins.managers.OverlayEntityManager;
import de.svenheins.managers.PlayerAgentManager;
import de.svenheins.managers.PlayerAgentManagerManager;
import de.svenheins.managers.PlayerManager;
import de.svenheins.managers.SpaceManager;
import de.svenheins.messages.ClientMessages;
import de.svenheins.objects.Entity;
import de.svenheins.objects.LocalMap;
import de.svenheins.objects.PlayerEntity;
import de.svenheins.objects.Space;
import de.svenheins.objects.agents.Agent;
import de.svenheins.objects.agents.NormalAgent;
import de.svenheins.objects.items.Item;
import de.svenheins.objects.items.Stackable;

public class MoveThread implements Runnable {

	private long duration, last; 
	private long millis, frames;
	
//	private JComponent comp;
	

	public void setLast(long last) {
		this.last = last;
	}

	
	/**
	 * @param last
	 * needs the currentTimeMillis(), to avoid the bug at the first frame
	 */
	public MoveThread(long last) {
		this.last = last;

	}
	
	public void run() {
		while (GUI.running) {			
			duration = System.currentTimeMillis() - last;
			last = System.currentTimeMillis();
			millis += duration;
			frames +=1;
			if(!GamePanel.gp.isPaused() && GameModus.modus == GameModus.GAME) {
				/** check and handle collision here */
				if(this.moveWithCollisions(GamePanel.gp.getPlayerEntity())) {
					PlayerEntity playerEntity = GamePanel.gp.getPlayerEntity();
					freeStuckedPlayerEntity(playerEntity);
					
					
//					System.out.println("Collision");
//					float[] damageAttributes = new float[AttributeType.values().length];
//					damageAttributes[AttributeType.LIFEREGENERATION.ordinal()] = -3.0f;
//					ItemInfluence slowDamage = new ItemInfluence(System.currentTimeMillis(), System.currentTimeMillis()+3000, damageAttributes, Priority.LOW);
//					GamePanel.gp.getPlayerEntity().addItemInfluence(slowDamage);
				} else {
					/** no collision */
					GamePanel.gp.getPlayerEntity().setCollided(0);
				}
				int totalX = GameStates.mapTotalWidth * GamePanel.gp.getMiniMap().getWidth();
				int totalY = GameStates.mapTotalHeight * GamePanel.gp.getMiniMap().getHeight();
//				Point moduloPointPlayer = new Point( ( (int) GamePanel.gp.getPlayerEntity().getX() % totalX+totalX) % totalX , ((int) GamePanel.gp.getPlayerEntity().getY() % totalY+totalY) % totalY);
//				GamePanel.gp.getPlayerEntity().setX(moduloPointPlayer.x);
//				GamePanel.gp.getPlayerEntity().setY(moduloPointPlayer.y);
				
				
				List<BigInteger> idListTempItems = new ArrayList<BigInteger>(ItemManager.idList);
				for (BigInteger i : idListTempItems) {
					Item tempItem = ItemManager.get(i);
					if (tempItem != null )  {				
						if (tempItem.isVisible() && GamePanel.gp.getPlayerEntity().getRoom().equals(tempItem.getRoom())) {
							Entity e = tempItem.getEntity();
							Point moduloPointItem= new Point( ( (int) e.getX() % totalX+totalX) % totalX , ((int) e.getY() % totalY+totalY) % totalY);
							e.setX(moduloPointItem.x);
							e.setY(moduloPointItem.y);
							
						} else {
							// not visible to this player
						}
//							GameWindow.gw.gameInfoConsole.appendInfo("I got a NULL Item");
					} else {
						System.out.println("i got a null item");
					}
				}
				

				/** check if there is enough space inside the inventory */
				if(GamePanel.gp.getPlayerEntity().getInventory().hasAvailableField()) {
					/** check if wants to take an item */
					try {
						for (BigInteger itemID : ItemManager.idList) {
							if (itemID != null ) {
								if (ItemManager.get(itemID) != null) {
									if (GamePanel.gp.getPlayerEntity().getRoom().equals(ItemManager.get(itemID).getRoom())) {
										try {
											if (Point.distance(GamePanel.gp.getPlayerEntity().getX()+GameStates.getPlayerProperty(GamePanel.gp.getPlayerEntity().getTileSet().getName(), PlayerProperty.WIDTH)/2, GamePanel.gp.getPlayerEntity().getY()+3*GameStates.getPlayerProperty(GamePanel.gp.getPlayerEntity().getTileSet().getName(), PlayerProperty.HEIGHT)/4, ItemManager.get(itemID).getEntity().getX()+GameStates.itemTileWidth/2, ItemManager.get(itemID).getEntity().getY()+GameStates.itemTileHeight/2) < GameStates.takeDistance) {
	//											System.out.println("Get Item: "+itemID.longValue());
												/** send a item collect request */
		//										EFFEKTIVER nur einmal senden lassen, solange die antwort noch nicht kam!!!
												boolean freeField = GamePanel.gp.getPlayerEntity().getInventory().hasEmptyField();
												if (freeField || (ItemManager.get(itemID) instanceof Stackable)) {
													if (!freeField) {
														if (GamePanel.gp.getPlayerEntity().getInventory().getAvailableItemFields().contains(ItemManager.get(itemID).getItemCode())) {
															if (!GameWindow.gw.getSendItemList().contains(itemID)) {
																ItemManager.get(itemID).setVisible(false);
																GameWindow.gw.send(ClientMessages.takeItem(itemID));
																GameWindow.gw.addSendItemListEntry(itemID);
															}
														}
													} else {
														if (!GameWindow.gw.getSendItemList().contains(itemID)) {
															ItemManager.get(itemID).setVisible(false);
															GameWindow.gw.send(ClientMessages.takeItem(itemID));
															GameWindow.gw.addSendItemListEntry(itemID);
														}
													}
												}
											}
										} catch(java.lang.NullPointerException error) {
											System.out.println(error);
										}
									}
								}
							}
						} 
					} catch(java.util.ConcurrentModificationException modError) {
						// IGNORE CONCURRENT MODS
	//					System.out.println(modError);
					}
				}
		
				/** move spaces */
				List<BigInteger> idListTempSpaces = new ArrayList<BigInteger>(SpaceManager.idList);
				boolean playerInsideSpace;
				for (BigInteger i : idListTempSpaces) {
					Space space= SpaceManager.get(i);
					if(space != null) {
						/** spaces move without limitations */	
						space.move(duration);
						
						space.setAllXY(space.getX(), space.getY());	
//						playerInsideSpace = false;
//						for (int j = 0; j < space.getPolygon().size(); j++) {
//							if (space.getPolygon().get(j).contains(GamePanel.gp.playerEntity.getX(), GamePanel.gp.playerEntity.getY()) )
//									playerInsideSpace = true;
//						}
//						if(playerInsideSpace) {
//							if (!GameWindow.gw.getSpaceChannels().containsKey(space.getId())) {
////								GameWindow.gw.send(ClientMessages.leaveSpaceChannel(space.getId()));
//								GameWindow.gw.send(ClientMessages.joinSpaceChannel(space.getId()));
//							}
//						} else {
//							if (GameWindow.gw.getSpaceChannels().containsKey(space.getId())) {
//								GameWindow.gw.send(ClientMessages.leaveSpaceChannel(space.getId()));
//							}
//						}
//						}
					}
				}
//				/** EntityManager move */
//				List<BigInteger> idListTempEntities = new ArrayList<BigInteger>(EntityManager.idList);
//				for (BigInteger i: idListTempEntities) {
//					Entity e= EntityManager.get(i);
//					if (e != null) { 
//						e.move(duration);
//					}
//					//System.out.println("id: "+ e.getId()+" x: "+e.getX()+" y: "+ e.getY()+" mx: "+e.getHorizontalMovement()+" my: "+e.getVerticalMovement());
//				}
				
				List<BigInteger> idListTempAgents = new ArrayList<BigInteger>(AgentManager.idList);
				for (BigInteger i: idListTempAgents) {
					Agent e= AgentManager.get(i);
					if (e != null) { 
//						this.determineAnimation(e);
//						e.getActualGoal().setEntity(GamePanel.gp.getPlayerEntity());
						if(this.moveWithCollisions(e)) {
							/** collided -> restart path calculation */
//							System.out.println("Agent collided!!!");
							if ( (e.getMX() != 0 || e.getMY() != 0) && e.isPathCalculationComplete() == true) {
								e.setMovement(-e.getTotalAreaInfluences()[AttributeType.MX.ordinal()], -e.getTotalAreaInfluences()[AttributeType.MY.ordinal()]);
								e.restartPathCalculationAfterCollision(GameWindow.gw.getObjectMapManagers().get("tree1"), GameWindow.gw.getObjectMapManagers().get("tree2"));
								
							}
							freeStuckedPlayerEntity(e);
							if (e instanceof NormalAgent) {
//								System.out.println("is agent in direct mode? -"+ ((NormalAgent) e).isDirectModus());
							}
						} else {
							e.setCollided(0);
						}
//						Point moduloPointAgent = new Point( ( (int) e.getX() % totalX+totalX) % totalX , ((int) e.getY() % totalY+totalY) % totalY);
//						e.setX(moduloPointAgent.x);
//						e.setY(moduloPointAgent.y);
						
					}
					//System.out.println("id: "+ e.getId()+" x: "+e.getX()+" y: "+ e.getY()+" mx: "+e.getHorizontalMovement()+" my: "+e.getVerticalMovement());
				}

				/** PlayerManager move */
//				for (int i = 0; i<EntityManager.size(); i++){
				List<BigInteger> idListTempPlayers = new ArrayList<BigInteger>(PlayerManager.idList);
				for (BigInteger i: idListTempPlayers) {
					PlayerEntity e= PlayerManager.get(i);
					if (e != null) {
						/** if too long time passed */
						if(System.currentTimeMillis()-e.getLastSeen() >= 500) {
							e.setVisible(false);
						} else {
							if (e != null) { 
								e.setVisible(true);
								e.move(duration);
							}
						}
						Point moduloPointAgent = new Point( ( (int) e.getX() % totalX+totalX) % totalX , ((int) e.getY() % totalY+totalY) % totalY);
						e.setX(moduloPointAgent.x);
						e.setY(moduloPointAgent.y);
					}
						//System.out.println("id: "+ e.getId()+" x: "+e.getX()+" y: "+ e.getY()+" mx: "+e.getHorizontalMovement()+" my: "+e.getVerticalMovement());
				}
				
				/** PlayerAgentsMove */
				for (PlayerAgentManager playerAgentManager : PlayerAgentManagerManager.playerAgentManagerList.values()) {
					List<BigInteger> idListTempPlayerAgents = new ArrayList<BigInteger>(playerAgentManager.getIdList());
					for (BigInteger i: idListTempPlayerAgents) {
						PlayerEntity tempAgent = playerAgentManager.get(i);
						if (tempAgent != null && tempAgent.isVisible()) {
							Entity e= tempAgent;
							if(e != null) {
								if(e.getSprite().getImage() != null) {
									e.move(duration);
								}
								Point moduloPointAgent = new Point( ( (int) e.getX() % totalX+totalX) % totalX , ((int) e.getY() % totalY+totalY) % totalY);
								e.setX(moduloPointAgent.x);
								e.setY(moduloPointAgent.y);
							}
						} else {
							GUI.gui.gameInfoConsole.appendInfo("I got a NULL Agent");
						}
					}
				}
				
				List<BigInteger> idListTempOverlay = new ArrayList<BigInteger>(OverlayEntityManager.idList);
				for (BigInteger i: idListTempOverlay) {
					Entity e= OverlayEntityManager.get(i);
					Random rand = new Random();
					if (e != null) { 
						e.move(duration);
						if (e.getName().equals("cloud")) {
							if (e.getY() > 2*GameStates.height) {
								e.setY(e.getY()-(1+rand.nextFloat()*0.5f)*GameStates.height);
							}
							if (e.getY() < -GameStates.height) {
								e.setY(e.getY()+(1+rand.nextFloat()*0.5f)*GameStates.height);
							}
							if (e.getX()-GamePanel.gp.getViewPointX()*2 > 2*GameStates.width ) {
								e.setX(e.getX()-(1+rand.nextFloat()*0.5f)*GameStates.width);
							}
							if (e.getX()-GamePanel.gp.getViewPointX()*2+e.getWidth()<-GameStates.width) {
								e.setX(e.getX()+(1+rand.nextFloat()*0.5f)*GameStates.width);
								//-e.getHeight()-2*GamePanel.gp.getViewPointY());
//								e.setX(rand.nextFloat()*GameStates.width - e.getWidth()/2-2*GamePanel.gp.getViewPointX());
//								if (e.getX() > GameStates.width/2) {
//									e.setMovement(GameStates.introSpeedMainMenu, GameStates.introSpeedMainMenu);
//								} else {
//									e.setMovement(-GameStates.introSpeedMainMenu, GameStates.introSpeedMainMenu);
//								}
							}
						} 
					}
					
						//System.out.println("id: "+ e.getId()+" x: "+e.getX()+" y: "+ e.getY()+" mx: "+e.getHorizontalMovement()+" my: "+e.getVerticalMovement());
				}
				
				/** InfoConsole Update */
				GUI.gui.gameInfoConsole.update();

				if (millis >1000 && GamePanel.gp.showStats ==true) {
					System.out.println("Move-Frames: "+ frames);
					frames = 0;
					millis =0;
				}
				

			} else if (GameModus.modus == GameModus.MAINMENU) {
				/** PlayerManager move */
//				for (int i = 0; i<EntityManager.size(); i++){
				List<BigInteger> idListTempPlayers = new ArrayList<BigInteger>(MainMenuEntityManager.idList);
				for (BigInteger i: idListTempPlayers) {
					Entity e= MainMenuEntityManager.get(i);
					Random rand = new Random();
					if (e != null) { 
						e.move(duration);
						if (e.getName().equals("cloud")) {
							if (e.getY() > GameStates.height || e.getX() > GameStates.width || e.getX()+e.getWidth()<0) {
								e.setY(-e.getHeight());
								e.setX(rand.nextFloat()*GameStates.width - e.getWidth()/2);
								if (e.getX() > GameStates.width/2) {
									e.setMovement(GameStates.introSpeedMainMenu, GameStates.introSpeedMainMenu);
								} else {
									e.setMovement(-GameStates.introSpeedMainMenu, GameStates.introSpeedMainMenu);
								}
							}
						} else if (!e.getName().equals("sky")) {
							if (e.getY() > GameStates.height) {
								e.setY(-e.getHeight()-3*e.getHeight());
								e.setX(GameStates.width/2);
								e.setMX(0);
							} else if (e.getY() >-3*e.getHeight() && e.getMX() == 0) {
								e.setMX(-GameStates.introSpeedMainMenu+2*rand.nextFloat()*GameStates.introSpeedMainMenu);
							}
						}
					}
					
						//System.out.println("id: "+ e.getId()+" x: "+e.getX()+" y: "+ e.getY()+" mx: "+e.getHorizontalMovement()+" my: "+e.getVerticalMovement());
				}
				
			}
			try {
				Thread.sleep(10);
			}
			catch(InterruptedException exception) {
				System.out.println(exception);
			}
		}
	}
	
	
	/** this entity moves with colliding with objectMap-objects */
	public boolean moveWithCollisions(Entity e) {
		boolean hasCollided = false;
		float addMX = 0;
		float addMY = 0;
		int collided = 0;
		if (e instanceof PlayerEntity) {
			addMX = ((PlayerEntity) e).getAttributes()[AttributeType.MX.ordinal()];
			addMY = ((PlayerEntity) e).getAttributes()[AttributeType.MY.ordinal()];
			collided = ((PlayerEntity)e).getCollided();
//			System.out.println(collided);
//			System.out.println("mx, my = "+ addMX +" "+addMY);
			
		}
		
		/** correct the animation if necessary */
		if (e.getMX() != 0 || e.getMY() != 0) {
			if (e.getContinuousState() != EntityStates.WALKING) {
				e.setContinuousState(EntityStates.WALKING);
				e.determineAnimation();
			} else if (e.getAnimation() != null) {
				if (e.getAnimation().getName() != null) {
					if (!e.getAnimation().getName().startsWith("walking")) {
						e.setContinuousState(EntityStates.WALKING);
						e.determineAnimation();
					}
				} else {
					/** happens quite often, i.e. when singleAnimation is playing and the sprite is moving */
					e.setContinuousState(EntityStates.WALKING);
					e.determineAnimation();
				}
			} else {
				/** should not happen too often */
				e.setContinuousState(EntityStates.WALKING);
				e.determineAnimation();
			}
		}
		
		/** check collision here */
		if ((e.getMX() != 0 || e.getMY() != 0 || addMX != 0 || addMY != 0 || collided>0) && (GameWindow.gw.getObjectMapManagers().get("tree1") != null && GameWindow.gw.getObjectMapManagers().get("tree2") != null)) {
			/** playerPosition */
			float movementX = duration * (e.getMX()+addMX)/1000;
			float movementY = duration * (e.getMY()+addMY)/1000;
			
			/** special treatment for collided entities */
			if ((e.getMX() == 0 && e.getMY() == 0 && addMX == 0 && addMY == 0) && collided >0) {
				movementX = duration * (e.getMX()+addMX+collided)/1000;
			}
//			System.out.println("MX="+movementX+" MY="+movementY);
			int newX = (int) (e.getX() + (movementX));
			int newY = (int) (e.getY() + (movementY));
			
			Point entityPointX = new Point( newX, (int) e.getY() );
			Point entityPointY = new Point( (int) e.getX(), newY );
			int localWidth = GameStates.mapTotalWidth;
			int localHeight = GameStates.mapTotalHeight;
			int entityHeight = (int) e.getHeight();
			int entityWidth = (int) e.getWidth();
//			if (entityHeight % 2 != 0) entityHeight++; 
			
			/** define collisionCorners of PlayerEntity */
			Point ulPointX = new Point(entityPointX.x, entityPointX.y+entityHeight/2);
			Point urPointX = new Point(entityPointX.x + entityWidth-1, entityPointX.y+entityHeight/2);
			Point dlPointX = new Point(entityPointX.x, entityPointX.y + entityHeight-1);
			Point drPointX = new Point(entityPointX.x + entityWidth-1, entityPointX.y +entityHeight-1);
			
			Point ulPointY = new Point(entityPointY.x, entityPointY.y+entityHeight/2);
			Point urPointY = new Point(entityPointY.x + entityWidth-1, entityPointY.y+entityHeight/2);
			Point dlPointY = new Point(entityPointY.x, entityPointY.y + entityHeight-1);
			Point drPointY = new Point(entityPointY.x + entityWidth-1, entityPointY.y +entityHeight-1);
			
			int ulLatticePointXX = (int) Math.floor( (float) (ulPointX.x) / (localWidth)) * localWidth;
			int ulLatticePointYX = (int) Math.floor( (float) (ulPointX.y) / (localHeight)) * localHeight;
			int ulLocalXX = (int) Math.floor( (float) (ulPointX.x - ulLatticePointXX )/ GameStates.mapTileSetWidth);
			int ulLocalYX = (int) Math.floor( (float) ( ulPointX.y - ulLatticePointYX )/ GameStates.mapTileSetHeight);
			
			int urLatticePointXX = (int) Math.floor( (float) (urPointX.x) / (localWidth)) * localWidth;
			int urLatticePointYX = (int) Math.floor( (float) (urPointX.y) / (localHeight)) * localHeight;
			int urLocalXX = (int) Math.floor( (float) (urPointX.x - urLatticePointXX )/ GameStates.mapTileSetWidth);
			int urLocalYX = (int) Math.floor( (float) ( urPointX.y - urLatticePointYX )/ GameStates.mapTileSetHeight);
			
			/** the same for the Y point */
			int ulLatticePointXY = (int) Math.floor( (float) (ulPointY.x) / (localWidth)) * localWidth;
			int ulLatticePointYY = (int) Math.floor( (float) (ulPointY.y) / (localHeight)) * localHeight;
			int ulLocalXY = (int) Math.floor( (float) (ulPointY.x - ulLatticePointXY )/ GameStates.mapTileSetWidth);
			int ulLocalYY = (int) Math.floor( (float) ( ulPointY.y - ulLatticePointYY )/ GameStates.mapTileSetHeight);
			
			int dlLatticePointXY = (int) Math.floor( (float) (dlPointY.x) / (localWidth)) * localWidth;
			int dlLatticePointYY = (int) Math.floor( (float) (dlPointY.y) / (localHeight)) * localHeight;
			int dlLocalXY = (int) Math.floor( (float) (dlPointY.x - dlLatticePointXY )/ GameStates.mapTileSetWidth);
			int dlLocalYY = (int) Math.floor( (float) ( dlPointY.y - dlLatticePointYY )/ GameStates.mapTileSetHeight);
			
			/** direction determines the critical tiles (differ between x and y) */
			/** X movement: right/ left tiles */
			boolean xCollides = false;
			boolean yCollides = false;
			if (movementX > 0) {
				/** get all right hand sided tiles */
				int it_RightLocalY = urLocalYX;
				int rightLocalYLimit = (int) Math.floor( (float) ( drPointX.y - urLatticePointYX )/ GameStates.mapTileSetHeight);
				int it_RightLatticeY = urLatticePointYX;
				for (int k = it_RightLocalY; k<= rightLocalYLimit; k++) {
					ObjectMapManager tree1Manager = GameWindow.gw.getObjectMapManagers().get("tree1");
					ObjectMapManager tree2Manager = GameWindow.gw.getObjectMapManagers().get("tree2");
					if (tree1Manager != null && tree2Manager != null) {
						int tileCollision1 = tree1Manager.checkCollision(new Point(urLatticePointXX, it_RightLatticeY), urLocalXX, it_RightLocalY);
						int tileCollision2 = tree2Manager.checkCollision(new Point(urLatticePointXX, it_RightLatticeY), urLocalXX, it_RightLocalY);
						if ((tileCollision1 != 0 ) || ( tileCollision2 != 0)) {
							/** collision!!! */
//							System.out.println("collision +x!");
							TileType tile1 = LocalMap.getTileType(LocalMap.getPaintType(tileCollision1));
							TileType tile2 = LocalMap.getTileType(LocalMap.getPaintType(tileCollision2));
							if ((tile1==TileType.HOLE || tile2 == TileType.HOLE) && e.equals(GamePanel.gp.getPlayerEntity())) {
								GamePanel.gp.getPlayerEntity().setX(urLatticePointXX+urLocalXX*GameStates.mapTileSetWidth);
								GamePanel.gp.getPlayerEntity().setY(it_RightLatticeY+it_RightLocalY*GameStates.mapTileSetWidth-GamePanel.gp.getPlayerEntity().getHeight()/2);
								GameWindow.gw.enterRoom(GameWindow.gw.getLowerRoom(GamePanel.gp.getPlayerEntity().getRoom()));
							}
							xCollides = true;
							break;
						}
						/** increment variables */
						it_RightLocalY++;
						if (it_RightLocalY >= GameStates.mapHeight) {
							it_RightLocalY = 0;
							it_RightLatticeY += GameStates.mapTotalHeight;
						}
					}
				}
			} else if (movementX < 0) {
				/** get all left hand sided tiles */
				int it_LeftLocalY = ulLocalYX;
				int leftLocalYLimit = (int) Math.floor( (float) ( dlPointX.y - ulLatticePointYX )/ GameStates.mapTileSetHeight);
				int it_LeftLatticeY = ulLatticePointYX;
				for (int k = it_LeftLocalY; k<= leftLocalYLimit; k++) {
					ObjectMapManager tree1Manager = GameWindow.gw.getObjectMapManagers().get("tree1");
					ObjectMapManager tree2Manager = GameWindow.gw.getObjectMapManagers().get("tree2");
					if (tree1Manager != null && tree2Manager != null) {
						int tileCollision1 = tree1Manager.checkCollision(new Point(ulLatticePointXX, it_LeftLatticeY), ulLocalXX, it_LeftLocalY);
						int tileCollision2 = tree2Manager.checkCollision(new Point(ulLatticePointXX, it_LeftLatticeY), ulLocalXX, it_LeftLocalY);
						if (((tileCollision1 != 0 ) || (tileCollision2 != 0 )) && e.equals(GamePanel.gp.getPlayerEntity())) {
							/** collision!!! */
//							System.out.println("collision +x!");
							TileType tile1 = LocalMap.getTileType(LocalMap.getPaintType(tileCollision1));
							TileType tile2 = LocalMap.getTileType(LocalMap.getPaintType(tileCollision2));
							if (tile1==TileType.HOLE || tile2 == TileType.HOLE) {
								GamePanel.gp.getPlayerEntity().setX(ulLatticePointXX+ulLocalXX*GameStates.mapTileSetWidth);
								GamePanel.gp.getPlayerEntity().setY(it_LeftLatticeY+it_LeftLocalY*GameStates.mapTileSetWidth-GamePanel.gp.getPlayerEntity().getHeight()/2);
								GameWindow.gw.enterRoom(GameWindow.gw.getLowerRoom(GamePanel.gp.getPlayerEntity().getRoom()));
							}
							xCollides = true;
							break;
						}
						/** increment variables */
						it_LeftLocalY++;
						if (it_LeftLocalY >= GameStates.mapHeight) {
							it_LeftLocalY = 0;
							it_LeftLatticeY += GameStates.mapTotalHeight;
						}
					}
				}
			} else {
				/** movement X = 0 */
			}
			
			/** Y movement upper / lower tiles */
			if (movementY < 0) {
				/** get all upper hand sided tiles */
				int it_UpperLocalX = ulLocalXY;
				int upperLocalXLimit = (int) Math.floor( (float) ( urPointY.x - ulLatticePointXY )/ GameStates.mapTileSetWidth);
				int it_UpperLatticeX = ulLatticePointXY;
				for (int k = it_UpperLocalX; k<= upperLocalXLimit; k++) {
					ObjectMapManager tree1Manager = GameWindow.gw.getObjectMapManagers().get("tree1");
					ObjectMapManager tree2Manager = GameWindow.gw.getObjectMapManagers().get("tree2");
					if (tree1Manager != null && tree2Manager != null) {
						int tileCollision1 = tree1Manager.checkCollision(new Point(it_UpperLatticeX, ulLatticePointYY ), it_UpperLocalX, ulLocalYY ) ;
						int tileCollision2 = tree2Manager.checkCollision(new Point(it_UpperLatticeX, ulLatticePointYY ), it_UpperLocalX, ulLocalYY ) ;
						if ((tileCollision1 != 0) ||( tileCollision2 != 0)) {
							/** collision!!! */
//							System.out.println("collision y!");
							TileType tile1 = LocalMap.getTileType(LocalMap.getPaintType(tileCollision1));
							TileType tile2 = LocalMap.getTileType(LocalMap.getPaintType(tileCollision2));
							if ((tile1==TileType.HOLE || tile2 == TileType.HOLE) && e.equals(GamePanel.gp.getPlayerEntity())) {
								GamePanel.gp.getPlayerEntity().setX(it_UpperLatticeX+it_UpperLocalX*GameStates.mapTileSetWidth);
								GamePanel.gp.getPlayerEntity().setY(ulLatticePointYY+ulLocalYY*GameStates.mapTileSetWidth-GamePanel.gp.getPlayerEntity().getHeight()/2);
								
								GameWindow.gw.enterRoom(GameWindow.gw.getLowerRoom(GamePanel.gp.getPlayerEntity().getRoom()));
							}
							yCollides = true;
							break;
						}
						/** increment variables */
						it_UpperLocalX++;
						if (it_UpperLocalX >= GameStates.mapWidth) {
							it_UpperLocalX = 0;
							it_UpperLatticeX += GameStates.mapTotalWidth;
						}
					}
				}
			} else if (movementY > 0) {
				/** get all left hand sided tiles */
				int it_LowerLocalX = dlLocalXY;
				int lowerLocalXLimit = (int) Math.floor( (float) ( drPointY.x - dlLatticePointXY )/ GameStates.mapTileSetWidth);
				int it_LowerLatticeX = dlLatticePointXY;
				for (int k = it_LowerLocalX; k<= lowerLocalXLimit; k++) {
					ObjectMapManager tree1Manager = GameWindow.gw.getObjectMapManagers().get("tree1");
					ObjectMapManager tree2Manager = GameWindow.gw.getObjectMapManagers().get("tree2");
					if (tree1Manager != null && tree2Manager != null) {
						int tileCollision1 = tree1Manager.checkCollision(new Point(it_LowerLatticeX, dlLatticePointYY ), it_LowerLocalX, dlLocalYY );
						int tileCollision2 = tree2Manager.checkCollision(new Point(it_LowerLatticeX, dlLatticePointYY ), it_LowerLocalX, dlLocalYY ) ;
						if (( tileCollision1 !=0) || (tileCollision2!=0)) {
							/** collision!!! */
//							System.out.println("collision y!");
							TileType tile1 = LocalMap.getTileType(LocalMap.getPaintType(tileCollision1));
							TileType tile2 = LocalMap.getTileType(LocalMap.getPaintType(tileCollision2));
							if ((tile1==TileType.HOLE || tile2 == TileType.HOLE) && e.equals(GamePanel.gp.getPlayerEntity())) {
								GamePanel.gp.getPlayerEntity().setX(it_LowerLatticeX+it_LowerLocalX*GameStates.mapTileSetWidth);
								GamePanel.gp.getPlayerEntity().setY(dlLatticePointYY+dlLocalYY*GameStates.mapTileSetWidth-GamePanel.gp.getPlayerEntity().getHeight()/2);
								
								GameWindow.gw.enterRoom(GameWindow.gw.getLowerRoom(GamePanel.gp.getPlayerEntity().getRoom()));
							}
							yCollides = true;
							break;
						}
						/** increment variables */
						it_LowerLocalX++;
						if (it_LowerLocalX >= GameStates.mapWidth) {
							it_LowerLocalX = 0;
							it_LowerLatticeX += GameStates.mapTotalWidth;
						}
					}
				}
			} else {
				/** movement Y = 0 */
			}
			
			if (e.equals(GamePanel.gp.getPlayerEntity())) {
//				System.out.println("correcting mouseposition");
//				float newMouseX = GamePanel.gp.getMouseCoordinates().x;
//				float newMouseY = GamePanel.gp.getMouseCoordinates().y;
				/** calculate collisions separately for x and y movement */
				if (!xCollides) {
					e.setX(e.getX()+movementX);
//					newMouseX += movementX;
				} else hasCollided = true;
				if (!yCollides) {
					e.setY(e.getY()+movementY); 
//					newMouseY += movementY;
				} else hasCollided = true;
//				Point newMousePosition = new Point((int)(newMouseX), (int) (newMouseY)); 
//				GamePanel.gp.setMouseCoordinates(newMousePosition);
			} else {
				/** calculate collisions separately for x and y movement */
				if (!xCollides) {
					e.setX(e.getX()+movementX);
				} else hasCollided = true;
				if (!yCollides) {
					e.setY(e.getY()+movementY); 
				} else hasCollided = true;
			}
		}
//		System.out.println(hasCollided);
		return hasCollided;
	}
	
	/** check and solve stuck state */
	public void freeStuckedPlayerEntity(PlayerEntity playerEntity) {

		/** player collided */
		float addMX = playerEntity.getAttributes()[AttributeType.MX.ordinal()];
		float addMY = playerEntity.getAttributes()[AttributeType.MY.ordinal()];
		if (playerEntity.getCollided() < 1) {
			playerEntity.setCollided(playerEntity.getCollided()+1);
		}
		
		if (playerEntity.getMX() == 0 && playerEntity.getMY() == 0 && addMX == 0 && addMY == 0) {
			/** only increase the collision counter, if the player is stuck */
			playerEntity.setCollided(playerEntity.getCollided()+1);
		}
		
		if (playerEntity.getCollided() > 20) {
			/** the player is stuck in an object! 
			 * -> find the next free field and put the player onto it*/
			
//			int lPX = (int) Math.floor( (float) candidatePoint.x / (localWidth)) * localWidth;
//			int lPY = (int) Math.floor( (float) candidatePoint.y / (localHeight)) * localHeight;
//			int lX = (int) Math.floor( (float) (candidatePoint.x - lPX )/ GameStates.mapTileSetWidth);
//			int lY = (int) Math.floor( (float) (candidatePoint.y - lPY )/ GameStates.mapTileSetHeight);
			int playerX = (int) Math.floor( (float) playerEntity.getX() / GameStates.mapTileSetWidth) * GameStates.mapTileSetWidth;
			int playerY = (int) Math.floor( (float) (playerEntity.getY() + playerEntity.getHeight()/2) / GameStates.mapTileSetHeight) *GameStates.mapTileSetHeight;
			Point playerPoint = new Point(playerX, playerY);
			ObjectMapManager objectMapManager1 = GameWindow.gw.getObjectMapManagers().get("tree1");
			ObjectMapManager objectMapManager2 = GameWindow.gw.getObjectMapManagers().get("tree2");
			int localWidth = GameStates.mapWidth * GameStates.mapTileSetWidth;
			int localHeight = GameStates.mapHeight * GameStates.mapTileSetHeight;
//			int latticePointX = (int) Math.floor( (float) x / (localWidth)) * localWidth;
//			int latticePointY = (int) Math.floor( (float) y / (localHeight)) * localHeight;
//			int localX = (int) Math.floor( (float) (x - latticePointX )/ GameStates.mapTileSetWidth);
//			int localY = (int) Math.floor( (float) (y - latticePointY )/ GameStates.mapTileSetHeight);
			
//			ObjectMapManager objectMapManager;
			boolean stuck = true;
			int ringDistance = 1;
			Point candidatePoint;
			Point goodCandidatePoint = new Point(0, 0);
			double shortestDistance = Double.MAX_VALUE;
			int iterationLimit = 200;
			int iteration = 0;
			/** while the entity is stuck in an object, find the next available field to place the player in */
			while(stuck && iteration<iterationLimit ) {
				// upper side
				for (int i = -ringDistance; i<= ringDistance; i++) {
					int j = -ringDistance;
					
					candidatePoint = new Point(playerX+i*GameStates.mapTileSetWidth, playerY+j* GameStates.mapTileSetHeight);
					int lPX = (int) Math.floor( (float) candidatePoint.x / (localWidth)) * localWidth;
					int lPY = (int) Math.floor( (float) candidatePoint.y / (localHeight)) * localHeight;
					int lX = (int) Math.floor( (float) (candidatePoint.x - lPX )/ GameStates.mapTileSetWidth);
					int lY = (int) Math.floor( (float) (candidatePoint.y - lPY )/ GameStates.mapTileSetHeight);
					Point p = new Point(lPX, lPY);
					
					/** check for null objectMapManagers */
					if (objectMapManager1.get(p) == null || objectMapManager2.get(p) == null) {
						if (objectMapManager1.get(p) != null) {
							if (objectMapManager1.get(p).getTile(lX, lY) == 0) {
								/** this tile is ok! */
								if (shortestDistance < Double.MAX_VALUE) {
									if (MyMath.getDistance(candidatePoint, playerPoint) < shortestDistance) {
										shortestDistance = MyMath.getDistance(candidatePoint, playerPoint);
										goodCandidatePoint = candidatePoint;
									}
								} else {
									/** add the first goodPoint here */
									shortestDistance = MyMath.getDistance(candidatePoint, playerPoint);
									goodCandidatePoint = candidatePoint;
								}
							}
						} else if (objectMapManager2.get(p)!= null) {
							/** objectMapManager2 must be non-null */
							if (objectMapManager2.get(p).getTile(lX, lY) == 0) {
								/** this tile is ok! */
								if (shortestDistance < Double.MAX_VALUE) {
									if (MyMath.getDistance(candidatePoint, playerPoint) < shortestDistance) {
										shortestDistance = MyMath.getDistance(candidatePoint, playerPoint);
										goodCandidatePoint = candidatePoint;
									}
								} else {
									/** add the first goodPoint here */
									shortestDistance = MyMath.getDistance(candidatePoint, playerPoint);
									goodCandidatePoint = candidatePoint;
								}
							}
						} else {
							// do nothing because both objectMapmanagers are null
						}
							
					} else {
						if (objectMapManager1.get(p).getTile(lX, lY) == 0 && objectMapManager2.get(p).getTile(lX, lY) == 0) {
							/** this tile is ok! */
							if (shortestDistance < Double.MAX_VALUE) {
								if (MyMath.getDistance(candidatePoint, playerPoint) < shortestDistance) {
									shortestDistance = MyMath.getDistance(candidatePoint, playerPoint);
									goodCandidatePoint = candidatePoint;
								}
							} else {
								/** add the first goodPoint here */
								shortestDistance = MyMath.getDistance(candidatePoint, playerPoint);
								goodCandidatePoint = candidatePoint;
							}
						}
					}
				}
				// lower side
				for (int i = -ringDistance; i<= ringDistance; i++) {
					int j = ringDistance;
					
					candidatePoint = new Point(playerX+i*GameStates.mapTileSetWidth, playerY+j* GameStates.mapTileSetHeight);
					int lPX = (int) Math.floor( (float) candidatePoint.x / (localWidth)) * localWidth;
					int lPY = (int) Math.floor( (float) candidatePoint.y / (localHeight)) * localHeight;
					int lX = (int) Math.floor( (float) (candidatePoint.x - lPX )/ GameStates.mapTileSetWidth);
					int lY = (int) Math.floor( (float) (candidatePoint.y - lPY )/ GameStates.mapTileSetHeight);
					Point p = new Point(lPX, lPY);
					/** check for null objectMapManagers */
					if (objectMapManager1.get(p) == null || objectMapManager2.get(p) == null) {
						if (objectMapManager1.get(p) != null) {
							if (LocalMap.getCollisionValue(objectMapManager1.get(p).getTile(lX, lY)) == 0) {
								/** this tile is ok! */
								if (shortestDistance < Double.MAX_VALUE) {
									if (MyMath.getDistance(candidatePoint, playerPoint) < shortestDistance) {
										shortestDistance = MyMath.getDistance(candidatePoint, playerPoint);
										goodCandidatePoint = candidatePoint;
									}
								} else {
									/** add the first goodPoint here */
									shortestDistance = MyMath.getDistance(candidatePoint, playerPoint);
									goodCandidatePoint = candidatePoint;
								}
							}
						} else if (objectMapManager2.get(p) != null) {
							/** objectMapManager2 must be non-null */
							if (LocalMap.getCollisionValue(objectMapManager2.get(p).getTile(lX, lY)) == 0) {
								/** this tile is ok! */
								if (shortestDistance < Double.MAX_VALUE) {
									if (MyMath.getDistance(candidatePoint, playerPoint) < shortestDistance) {
										shortestDistance = MyMath.getDistance(candidatePoint, playerPoint);
										goodCandidatePoint = candidatePoint;
									}
								} else {
									/** add the first goodPoint here */
									shortestDistance = MyMath.getDistance(candidatePoint, playerPoint);
									goodCandidatePoint = candidatePoint;
								}
							}
						}
							
					} else {
						if (LocalMap.getCollisionValue(objectMapManager1.get(p).getTile(lX, lY)) == 0 && LocalMap.getCollisionValue(objectMapManager2.get(p).getTile(lX, lY)) == 0) {
							/** this tile is ok! */
							if (shortestDistance < Double.MAX_VALUE) {
								if (MyMath.getDistance(candidatePoint, playerPoint) < shortestDistance) {
									shortestDistance = MyMath.getDistance(candidatePoint, playerPoint);
									goodCandidatePoint = candidatePoint;
								}
							} else {
								/** add the first goodPoint here */
								shortestDistance = MyMath.getDistance(candidatePoint, playerPoint);
								goodCandidatePoint = candidatePoint;
							}
						}
					}
				}
				// left side
				for (int j = -ringDistance+1; j<= ringDistance-1; j++) {
					int i = -ringDistance;
					
					candidatePoint = new Point(playerX+i*GameStates.mapTileSetWidth, playerY+j* GameStates.mapTileSetHeight);
					int lPX = (int) Math.floor( (float) candidatePoint.x / (localWidth)) * localWidth;
					int lPY = (int) Math.floor( (float) candidatePoint.y / (localHeight)) * localHeight;
					int lX = (int) Math.floor( (float) (candidatePoint.x - lPX )/ GameStates.mapTileSetWidth);
					int lY = (int) Math.floor( (float) (candidatePoint.y - lPY )/ GameStates.mapTileSetHeight);
					Point p = new Point(lPX, lPY);
					/** check for null objectMapManagers */
					if (objectMapManager1.get(p) == null || objectMapManager2.get(p) == null) {
						if (objectMapManager1.get(p) != null) {
							if (LocalMap.getCollisionValue(objectMapManager1.get(p).getTile(lX, lY)) == 0) {
								/** this tile is ok! */
								if (shortestDistance < Double.MAX_VALUE) {
									if (MyMath.getDistance(candidatePoint, playerPoint) < shortestDistance) {
										shortestDistance = MyMath.getDistance(candidatePoint, playerPoint);
										goodCandidatePoint = candidatePoint;
									}
								} else {
									/** add the first goodPoint here */
									shortestDistance = MyMath.getDistance(candidatePoint, playerPoint);
									goodCandidatePoint = candidatePoint;
								}
							}
						} else if (objectMapManager2.get(p) != null) {
							/** objectMapManager2 must be non-null */
							if (LocalMap.getCollisionValue(objectMapManager2.get(p).getTile(lX, lY)) == 0) {
								/** this tile is ok! */
								if (shortestDistance < Double.MAX_VALUE) {
									if (MyMath.getDistance(candidatePoint, playerPoint) < shortestDistance) {
										shortestDistance = MyMath.getDistance(candidatePoint, playerPoint);
										goodCandidatePoint = candidatePoint;
									}
								} else {
									/** add the first goodPoint here */
									shortestDistance = MyMath.getDistance(candidatePoint, playerPoint);
									goodCandidatePoint = candidatePoint;
								}
							}
						} else {
							// both are null
						}
							
					} else {
						if (LocalMap.getCollisionValue(objectMapManager1.get(p).getTile(lX, lY)) == 0 && LocalMap.getCollisionValue(objectMapManager2.get(p).getTile(lX, lY)) == 0) {
							/** this tile is ok! */
							if (shortestDistance < Double.MAX_VALUE) {
								if (MyMath.getDistance(candidatePoint, playerPoint) < shortestDistance) {
									shortestDistance = MyMath.getDistance(candidatePoint, playerPoint);
									goodCandidatePoint = candidatePoint;
								}
							} else {
								/** add the first goodPoint here */
								shortestDistance = MyMath.getDistance(candidatePoint, playerPoint);
								goodCandidatePoint = candidatePoint;
							}
						}
					}
				}
				// right side
				for (int j = -ringDistance+1; j<= ringDistance-1; j++) {
					int i = ringDistance;
					
					candidatePoint = new Point(playerX+i*GameStates.mapTileSetWidth, playerY+j* GameStates.mapTileSetHeight);
					int lPX = (int) Math.floor( (float) candidatePoint.x / (localWidth)) * localWidth;
					int lPY = (int) Math.floor( (float) candidatePoint.y / (localHeight)) * localHeight;
					int lX = (int) Math.floor( (float) (candidatePoint.x - lPX )/ GameStates.mapTileSetWidth);
					int lY = (int) Math.floor( (float) (candidatePoint.y - lPY )/ GameStates.mapTileSetHeight);
					Point p = new Point(lPX, lPY);
					/** check for null objectMapManagers */
					if (objectMapManager1.get(p) == null || objectMapManager2.get(p) == null) {
						if (objectMapManager1.get(p) != null) {
							if (LocalMap.getCollisionValue(objectMapManager1.get(p).getTile(lX, lY)) == 0) {
								/** this tile is ok! */
								if (shortestDistance < Double.MAX_VALUE) {
									if (MyMath.getDistance(candidatePoint, playerPoint) < shortestDistance) {
										shortestDistance = MyMath.getDistance(candidatePoint, playerPoint);
										goodCandidatePoint = candidatePoint;
									}
								} else {
									/** add the first goodPoint here */
									shortestDistance = MyMath.getDistance(candidatePoint, playerPoint);
									goodCandidatePoint = candidatePoint;
								}
							}
						} else if (objectMapManager2.get(p) != null){
							/** objectMapManager2 can be non-null */
							if (LocalMap.getCollisionValue(objectMapManager2.get(p).getTile(lX, lY)) == 0) {
								/** this tile is ok! */
								if (shortestDistance < Double.MAX_VALUE) {
									if (MyMath.getDistance(candidatePoint, playerPoint) < shortestDistance) {
										shortestDistance = MyMath.getDistance(candidatePoint, playerPoint);
										goodCandidatePoint = candidatePoint;
									}
								} else {
									/** add the first goodPoint here */
									shortestDistance = MyMath.getDistance(candidatePoint, playerPoint);
									goodCandidatePoint = candidatePoint;
								}
							}
						} else {
							// both are NULL 
						}
							
					} else {
						if (LocalMap.getCollisionValue(objectMapManager1.get(p).getTile(lX, lY)) == 0 && LocalMap.getCollisionValue(objectMapManager2.get(p).getTile(lX, lY)) == 0) {
							/** this tile is ok! */
							if (shortestDistance < Double.MAX_VALUE) {
								if (MyMath.getDistance(candidatePoint, playerPoint) < shortestDistance) {
									shortestDistance = MyMath.getDistance(candidatePoint, playerPoint);
									goodCandidatePoint = candidatePoint;
								}
							} else {
								/** add the first goodPoint here */
								shortestDistance = MyMath.getDistance(candidatePoint, playerPoint);
								goodCandidatePoint = candidatePoint;
							}
						}
					}
				}
				
				/** we found a valid spot in this ring round */
				if (shortestDistance < Double.MAX_VALUE) {
					stuck = false;
					playerEntity.setX(goodCandidatePoint.x);
					playerEntity.setY(goodCandidatePoint.y-playerEntity.getHeight()/2);
//					if (playerEntity.getWidth() > GameStates.mapTileSetWidth || playerEntity.getHeight()/2 > GameStates.mapTileSetHeight) {
//						Random randomGen = new Random();
//						float randomShiftX = randomGen.nextFloat()*500-250;
//						float randomShiftY = randomGen.nextFloat()*500-250;
//						playerEntity.setX(goodCandidatePoint.x+randomShiftX);
//						playerEntity.setY(goodCandidatePoint.y+randomShiftY);
//					}
					playerEntity.setCollided(0);
				} else {
					ringDistance ++;
				}
				
				/** increase iteration as a maximum limit for this loop */
				iteration++;
			}
			
		}
	}
	
//	public void determineAnimation(Entity entity) {
//		if(entity.getMX() > 0) {
//			if(entity.getMY() > 0) {
//				// dr
//				entity.setAnimation(AnimationManager.manager.getAnimation("dr", entity.getTileSet(), GameStates.ani_dr_start, GameStates.ani_dr_end, entity.getAnimation().getTimeBetweenAnimation()));
//			} else if (entity.getMY() == 0)
//			{
//				// r
//				entity.setAnimation(AnimationManager.manager.getAnimation("r", entity.getTileSet(), GameStates.ani_r_start, GameStates.ani_r_end, entity.getAnimation().getTimeBetweenAnimation()));
//			} else {
//				// ur
//				entity.setAnimation(AnimationManager.manager.getAnimation("ur", entity.getTileSet(), GameStates.ani_ur_start, GameStates.ani_ur_end, entity.getAnimation().getTimeBetweenAnimation()));
//			}
//		} else if (entity.getMX() == 0){
//			if(entity.getMY() > 0) {
//				// d
//				entity.setAnimation(AnimationManager.manager.getAnimation("d", entity.getTileSet(), GameStates.ani_d_start, GameStates.ani_d_end, entity.getAnimation().getTimeBetweenAnimation()));
//			} else if (entity.getMY() == 0)
//			{
//				// standing still
//				entity.setAnimation(AnimationManager.manager.getAnimation("standard", entity.getTileSet(), GameStates.ani_standard_start, GameStates.ani_standard_end, entity.getAnimation().getTimeBetweenAnimation()));
//			} else {
//				// u
//				entity.setAnimation(AnimationManager.manager.getAnimation("u", entity.getTileSet(), GameStates.ani_u_start, GameStates.ani_u_end, entity.getAnimation().getTimeBetweenAnimation()));
//			}
//		} else {
//			if(entity.getMY() > 0) {
//				// dl
//				entity.setAnimation(AnimationManager.manager.getAnimation("dl", entity.getTileSet(), GameStates.ani_dl_start, GameStates.ani_dl_end, entity.getAnimation().getTimeBetweenAnimation()));
//			} else if (entity.getMY() == 0)
//			{
//				// l
//				entity.setAnimation(AnimationManager.manager.getAnimation("l", entity.getTileSet(), GameStates.ani_l_start, GameStates.ani_l_end, entity.getAnimation().getTimeBetweenAnimation()));
//			} else {
//				// ul
//				entity.setAnimation(AnimationManager.manager.getAnimation("ul", entity.getTileSet(), GameStates.ani_ul_start, GameStates.ani_ul_end, entity.getAnimation().getTimeBetweenAnimation()));
//			}
//		}
//	}
//	public void determineAnimation(Entity entity) {
//		if ((entity.getMX() != 0) || (entity.getMY() != 0)) {
//			if (entity.getState() != EntityStates.WALKING) {
////				entity.startAnimation();
//				entity.setState(EntityStates.WALKING);
//				entity.setChangedStates(true);
//			}
//		} else {
//			if (entity.getState() != EntityStates.STANDING) {
////				entity.startAnimation();
//				entity.setState(EntityStates.STANDING);
//				entity.setChangedStates(true);
//			}
//			
//		}
//	}
}
