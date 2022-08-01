package de.svenheins.threads;


import java.awt.Point;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.JComponent;

import de.svenheins.functions.MyMath;
import de.svenheins.main.EntityStates;
import de.svenheins.main.GUI;
import de.svenheins.main.GameModus;
import de.svenheins.main.GamePanel;
import de.svenheins.main.GameStates;
import de.svenheins.main.GameWindow;
import de.svenheins.managers.AnimationManager;
import de.svenheins.managers.EntityManager;
import de.svenheins.managers.PlayerManager;
import de.svenheins.managers.SpaceManager;
import de.svenheins.managers.WorldItemManager;
import de.svenheins.messages.ClientMessages;

import de.svenheins.objects.Entity;
import de.svenheins.objects.PlayerEntity;
import de.svenheins.objects.Space;

public class MoveThread implements Runnable {

	private long duration, last; 
	private long millis, frames;
	
	private boolean changeDirection;
	
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
				/** check collision here */
				if ((GamePanel.gp.getPlayerEntity().getMX() != 0 || GamePanel.gp.getPlayerEntity().getMY() != 0) && (GameWindow.gw.getObjectMapManagers().get("tree1") != null && GameWindow.gw.getObjectMapManagers().get("tree2") != null)) {
					/** playerPosition */
					float movementX = duration * GamePanel.gp.getPlayerEntity().getMX()/1000;
					float movementY = duration * GamePanel.gp.getPlayerEntity().getMY()/1000;
					int newX = (int) (GamePanel.gp.getPlayerEntity().getX() + (movementX));
					int newY = (int) (GamePanel.gp.getPlayerEntity().getY() + (movementY));
					
					Point entityPointX = new Point( newX, (int) GamePanel.gp.getPlayerEntity().getY() );
					Point entityPointY = new Point( (int) GamePanel.gp.getPlayerEntity().getX(), newY );
					int localWidth = GameStates.mapTotalWidth;
					int localHeight = GameStates.mapTotalHeight;
					int entityHeight = (int) GamePanel.gp.getPlayerEntity().getHeight();
					int entityWidth = (int) GamePanel.gp.getPlayerEntity().getWidth();
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
						for (int i = it_RightLocalY; i<= rightLocalYLimit; i++) {
							if ((GameWindow.gw.getObjectMapManagers().get("tree1").checkCollision(new Point(urLatticePointXX, it_RightLatticeY), urLocalXX, it_RightLocalY) ) ||
									(GameWindow.gw.getObjectMapManagers().get("tree2").checkCollision(new Point(urLatticePointXX, it_RightLatticeY), urLocalXX, it_RightLocalY) )) {
								/** collision!!! */
//								System.out.println("collision +x!");
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
					} else if (movementX < 0) {
						/** get all left hand sided tiles */
						int it_LeftLocalY = ulLocalYX;
						int leftLocalYLimit = (int) Math.floor( (float) ( dlPointX.y - ulLatticePointYX )/ GameStates.mapTileSetHeight);
						int it_LeftLatticeY = ulLatticePointYX;
						for (int i = it_LeftLocalY; i<= leftLocalYLimit; i++) {
							if ((GameWindow.gw.getObjectMapManagers().get("tree1").checkCollision(new Point(ulLatticePointXX, it_LeftLatticeY), ulLocalXX, it_LeftLocalY) ) ||
									(GameWindow.gw.getObjectMapManagers().get("tree2").checkCollision(new Point(ulLatticePointXX, it_LeftLatticeY), ulLocalXX, it_LeftLocalY) )) {
								/** collision!!! */
//								System.out.println("collision +x!");
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
					} else {
						/** movement X = 0 */
					}
					
					/** Y movement upper / lower tiles */
					if (movementY < 0) {
						/** get all upper hand sided tiles */
						int it_UpperLocalX = ulLocalXY;
						int upperLocalXLimit = (int) Math.floor( (float) ( urPointY.x - ulLatticePointXY )/ GameStates.mapTileSetWidth);
						int it_UpperLatticeX = ulLatticePointXY;
						for (int i = it_UpperLocalX; i<= upperLocalXLimit; i++) {
							if ((GameWindow.gw.getObjectMapManagers().get("tree1").checkCollision(new Point(it_UpperLatticeX, ulLatticePointYY ), it_UpperLocalX, ulLocalYY ) ) ||
									(GameWindow.gw.getObjectMapManagers().get("tree2").checkCollision(new Point(it_UpperLatticeX, ulLatticePointYY ), it_UpperLocalX, ulLocalYY ) )) {
								/** collision!!! */
//								System.out.println("collision y!");
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
					} else if (movementY > 0) {
						/** get all left hand sided tiles */
						int it_LowerLocalX = dlLocalXY;
						int lowerLocalXLimit = (int) Math.floor( (float) ( drPointY.x - dlLatticePointXY )/ GameStates.mapTileSetWidth);
						int it_LowerLatticeX = dlLatticePointXY;
						for (int i = it_LowerLocalX; i<= lowerLocalXLimit; i++) {
							if ((GameWindow.gw.getObjectMapManagers().get("tree1").checkCollision(new Point(it_LowerLatticeX, dlLatticePointYY ), it_LowerLocalX, dlLocalYY ) ) ||
									(GameWindow.gw.getObjectMapManagers().get("tree2").checkCollision(new Point(it_LowerLatticeX, dlLatticePointYY ), it_LowerLocalX, dlLocalYY ) )) {
								/** collision!!! */
//								System.out.println("collision y!");
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
					} else {
						/** movement Y = 0 */
					}
					/** calculate collisions separately for x and y movement */
					if (!xCollides) GamePanel.gp.getPlayerEntity().setX(GamePanel.gp.getPlayerEntity().getX()+movementX);
					if (!yCollides) GamePanel.gp.getPlayerEntity().setY(GamePanel.gp.getPlayerEntity().getY()+movementY);
				}
				
				
				/** check if wants to take an item */
				try {
					for (BigInteger itemID : WorldItemManager.idList) {
						if (itemID != null) {
							if (Point.distance(GamePanel.gp.getPlayerEntity().getX()+GameStates.playerTileWidth/2, GamePanel.gp.getPlayerEntity().getY()+3*GameStates.playerTileHeight/4, WorldItemManager.get(itemID).getItemEntity().getX()+GameStates.itemTileWidth/2, WorldItemManager.get(itemID).getItemEntity().getY()+GameStates.itemTileHeight/2) < GameStates.takeDistance) {
								/** TODO: check if there is enough space inside the inventory */
								GameWindow.gw.send(ClientMessages.takeItem(itemID));
							}
						}
					} 
				} catch(java.util.ConcurrentModificationException modError) {
					// IGNORE CONCURRENT MODS
//					System.out.println(modError);
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
				/** EntityManager move */
				List<BigInteger> idListTempEntities = new ArrayList<BigInteger>(EntityManager.idList);
				for (BigInteger i: idListTempEntities) {
					Entity e= EntityManager.get(i);
					if (e != null) { 
//						this.determineAnimation(e);
						e.move(duration);
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
	//					if(e.getHorizontalMovement() != 0) e.moveOnX(duration);
	//					if(e.getVerticalMovement()!=0) e.moveOnY(duration);
							if (e != null) { 
								e.setVisible(true);
//								this.determineAnimation(e);
								e.move(duration);
							}
						}
					}
						//System.out.println("id: "+ e.getId()+" x: "+e.getX()+" y: "+ e.getY()+" mx: "+e.getHorizontalMovement()+" my: "+e.getVerticalMovement());
				}
				
				/** InfoConsole Update */
				GameWindow.gw.gameInfoConsole.update();

				if (millis >1000 && GamePanel.gp.showStats ==true) {
					System.out.println("MoveShip-Frames: "+ frames);
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
