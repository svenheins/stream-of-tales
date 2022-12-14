package de.svenheins.threads;


import java.awt.Point;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import de.svenheins.main.AttributeType;
import de.svenheins.main.GUI;
import de.svenheins.main.GameModus;
import de.svenheins.main.GamePanel;
import de.svenheins.main.GameStates;
import de.svenheins.main.GameWindow;
import de.svenheins.main.Priority;
import de.svenheins.managers.AgentManager;
import de.svenheins.managers.EntityManager;
import de.svenheins.managers.ItemManager;
import de.svenheins.managers.ObjectMapManager;
import de.svenheins.managers.PlayerManager;
import de.svenheins.managers.SpaceManager;
import de.svenheins.messages.ClientMessages;

import de.svenheins.objects.Entity;
import de.svenheins.objects.ItemInfluence;
import de.svenheins.objects.PlayerEntity;
import de.svenheins.objects.Space;
import de.svenheins.objects.agents.Agent;
import de.svenheins.objects.agents.NormalAgent;

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
					/** player collided */
//					System.out.println("Collision");
//					float[] damageAttributes = new float[AttributeType.values().length];
//					damageAttributes[AttributeType.LIFEREGENERATION.ordinal()] = -3.0f;
//					ItemInfluence slowDamage = new ItemInfluence(System.currentTimeMillis(), System.currentTimeMillis()+3000, damageAttributes, Priority.LOW);
//					GamePanel.gp.getPlayerEntity().addItemInfluence(slowDamage);
				} else {
					/** no collision */
				}

				/** check if there is enough space inside the inventory */
				if(GamePanel.gp.getPlayerEntity().getInventory().hasAvailableField()) {
					/** check if wants to take an item */
					try {
						for (BigInteger itemID : ItemManager.idList) {
							if (itemID != null) {
								try {
									if (Point.distance(GamePanel.gp.getPlayerEntity().getX()+GameStates.playerTileWidth/2, GamePanel.gp.getPlayerEntity().getY()+3*GameStates.playerTileHeight/4, ItemManager.get(itemID).getEntity().getX()+GameStates.itemTileWidth/2, ItemManager.get(itemID).getEntity().getY()+GameStates.itemTileHeight/2) < GameStates.takeDistance) {
										/** send a item collect request */
//										EFFEKTIVER nur einmal senden lassen, solange die antwort noch nicht kam!!!
										if (!GameWindow.gw.getSendItemList().contains(itemID)) {
											ItemManager.get(itemID).setVisible(false);
											GameWindow.gw.send(ClientMessages.takeItem(itemID));
											GameWindow.gw.addSendItemListEntry(itemID);
										}
										
									}
								} catch(java.lang.NullPointerException error) {
									System.out.println(error);
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
							System.out.println("Agent collided!!!");
							if ( (e.getMX() != 0 || e.getMY() != 0) && e.isPathCalculationComplete() == true) {
								e.setMovement(-e.getAttributes()[AttributeType.MX.ordinal()], -e.getAttributes()[AttributeType.MY.ordinal()]);
								e.restartPathCalculationAfterCollision(GameWindow.gw.getObjectMapManagers().get("tree1"), GameWindow.gw.getObjectMapManagers().get("tree2"));
							}
							if (e instanceof NormalAgent) {
//								System.out.println("is agent in direct mode? -"+ ((NormalAgent) e).isDirectModus());
							}
						}
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
					}
						//System.out.println("id: "+ e.getId()+" x: "+e.getX()+" y: "+ e.getY()+" mx: "+e.getHorizontalMovement()+" my: "+e.getVerticalMovement());
				}
				
				/** InfoConsole Update */
				GameWindow.gw.gameInfoConsole.update();

				if (millis >1000 && GamePanel.gp.showStats ==true) {
					System.out.println("Move-Frames: "+ frames);
					frames = 0;
					millis =0;
				}
				

			}
			try {
				Thread.sleep(5);
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
		if (e instanceof PlayerEntity) {
			addMX = ((PlayerEntity) e).getAttributes()[AttributeType.MX.ordinal()];
			addMY = ((PlayerEntity) e).getAttributes()[AttributeType.MY.ordinal()];
//			System.out.println("mx, my = "+ addMX +" "+addMY);
		}
		/** check collision here */
		if ((e.getMX() != 0 || e.getMY() != 0 || addMX != 0 || addMY != 0 ) && (GameWindow.gw.getObjectMapManagers().get("tree1") != null && GameWindow.gw.getObjectMapManagers().get("tree2") != null)) {
			/** playerPosition */
			float movementX = duration * (e.getMX()+addMX)/1000;
			float movementY = duration * (e.getMY()+addMY)/1000;
			int newX = (int) (e.getX() + (movementX));
			int newY = (int) (e.getY() + (movementY));
			
			Point entityPointX = new Point( newX, (int) e.getY() );
			Point entityPointY = new Point( (int) e.getX(), newY );
			int localWidth = GameStates.mapTotalWidth;
			int localHeight = GameStates.mapTotalHeight;
			int entityHeight = (int) e.getHeight();
			int entityWidth = (int) e.getWidth();
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
						if ((tree1Manager.checkCollision(new Point(urLatticePointXX, it_RightLatticeY), urLocalXX, it_RightLocalY) ) ||
								(tree2Manager.checkCollision(new Point(urLatticePointXX, it_RightLatticeY), urLocalXX, it_RightLocalY) )) {
							/** collision!!! */
	//						System.out.println("collision +x!");
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
						if ((tree1Manager.checkCollision(new Point(ulLatticePointXX, it_LeftLatticeY), ulLocalXX, it_LeftLocalY) ) ||
								(tree2Manager.checkCollision(new Point(ulLatticePointXX, it_LeftLatticeY), ulLocalXX, it_LeftLocalY) )) {
							/** collision!!! */
	//						System.out.println("collision +x!");
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
						if ((tree1Manager.checkCollision(new Point(it_UpperLatticeX, ulLatticePointYY ), it_UpperLocalX, ulLocalYY ) ) ||
								(tree2Manager.checkCollision(new Point(it_UpperLatticeX, ulLatticePointYY ), it_UpperLocalX, ulLocalYY ) )) {
							/** collision!!! */
	//						System.out.println("collision y!");
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
						if ((tree1Manager.checkCollision(new Point(it_LowerLatticeX, dlLatticePointYY ), it_LowerLocalX, dlLocalYY ) ) ||
								(tree2Manager.checkCollision(new Point(it_LowerLatticeX, dlLatticePointYY ), it_LowerLocalX, dlLocalYY ) )) {
							/** collision!!! */
	//						System.out.println("collision y!");
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
			/** calculate collisions separately for x and y movement */
			if (!xCollides) e.setX(e.getX()+movementX); else hasCollided = true;
			if (!yCollides) e.setY(e.getY()+movementY); else hasCollided = true;
		}
		
		return hasCollided;
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
