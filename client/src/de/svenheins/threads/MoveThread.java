package de.svenheins.threads;


import java.awt.Point;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import de.svenheins.main.GUI;
import de.svenheins.main.GameModus;
import de.svenheins.main.GamePanel;
import de.svenheins.main.GameStates;
import de.svenheins.main.GameWindow;
import de.svenheins.managers.AnimationManager;
import de.svenheins.managers.EntityManager;
import de.svenheins.managers.PlayerManager;
import de.svenheins.managers.SpaceManager;
import de.svenheins.messages.ClientMessages;

import de.svenheins.objects.Entity;
import de.svenheins.objects.PlayerEntity;
import de.svenheins.objects.Space;

public class MoveThread implements Runnable {

	private long duration, last; 
	private long millis, frames;
	
	private boolean changeDirection;
	

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

				/** determine Animation */
				this.determineAnimation(GamePanel.gp.playerEntity);	
				/** check collision here */
//				if (GamePanel.gp.playerEntity.getMX() != 0 || GamePanel.gp.playerEntity.getMY() != 0) {
//					/** playerPosition */
//					float movementX = duration * GamePanel.gp.playerEntity.getMX()/1000;
//					float movementY = duration * GamePanel.gp.playerEntity.getMY()/1000;
//					int newX = (int) (GamePanel.gp.playerEntity.getX() + (movementX));
//					int newY = (int) (GamePanel.gp.playerEntity.getY() + (movementY));
//					float newMX = GamePanel.gp.playerEntity.getMX();
//					float newMY = GamePanel.gp.playerEntity.getMY();
//					
//					Point entityPoint = new Point( newX, newY );
//					int localWidth = GameStates.mapTotalWidth;
//					int localHeight = GameStates.mapTotalHeight;
//					int entityHeight = (int) GamePanel.gp.getPlayerEntity().getHeight();
//					int entityWidth = (int) GamePanel.gp.getPlayerEntity().getWidth();
//					/** define collisionCorners of PlayerEntity */
//					Point ulPoint = new Point(entityPoint.x, entityPoint.y+entityHeight/2);
//					Point urPoint = new Point(entityPoint.x + entityWidth, entityPoint.y+entityHeight/2);
//					Point dlPoint = new Point(entityPoint.x, entityPoint.y + entityHeight);
//					Point drPoint = new Point(entityPoint.x + entityWidth, entityPoint.y +entityHeight);
//					
//					int ulLatticePointX = (int) Math.floor( (float) (ulPoint.x) / (localWidth)) * localWidth;
//					int ulLatticePointY = (int) Math.floor( (float) (ulPoint.y) / (localHeight)) * localHeight;
//					int ulLocalX = (int) Math.floor( (float) (ulPoint.x - ulLatticePointX )/ GameStates.mapTileSetWidth);
//					int ulLocalY = (int) Math.floor( (float) ( ulPoint.y - ulLatticePointY )/ GameStates.mapTileSetHeight);
//					
//					int urLatticePointX = (int) Math.floor( (float) (urPoint.x) / (localWidth)) * localWidth;
//					int urLatticePointY = (int) Math.floor( (float) (urPoint.y) / (localHeight)) * localHeight;
//					int urLocalX = (int) Math.floor( (float) (urPoint.x - urLatticePointX )/ GameStates.mapTileSetWidth);
//					int urLocalY = (int) Math.floor( (float) ( urPoint.y - urLatticePointY )/ GameStates.mapTileSetHeight);
//					
//					int dlLatticePointX = (int) Math.floor( (float) (dlPoint.x) / (localWidth)) * localWidth;
//					int dlLatticePointY = (int) Math.floor( (float) (dlPoint.y) / (localHeight)) * localHeight;
//					int dlLocalX = (int) Math.floor( (float) (dlPoint.x - dlLatticePointX )/ GameStates.mapTileSetWidth);
//					int dlLocalY = (int) Math.floor( (float) ( dlPoint.y - dlLatticePointY )/ GameStates.mapTileSetHeight);
//					
//					int drLatticePointX = (int) Math.floor( (float) (drPoint.x) / (localWidth)) * localWidth;
//					int drLatticePointY = (int) Math.floor( (float) (drPoint.y) / (localHeight)) * localHeight;
//					int drLocalX = (int) Math.floor( (float) (drPoint.x - drLatticePointX )/ GameStates.mapTileSetWidth);
//					int drLocalY = (int) Math.floor( (float) ( drPoint.y - drLatticePointY )/ GameStates.mapTileSetHeight);
//					
//					/** check ul corner */
//					if ((GameWindow.gw.getObjectMapManagers().get("tree1").checkCollision(new Point(ulLatticePointX, ulLatticePointY), ulLocalX, ulLocalY) ) ||
//					(GameWindow.gw.getObjectMapManagers().get("tree2").checkCollision(new Point(ulLatticePointX, ulLatticePointY), ulLocalX, ulLocalY) )) {
//						int distX = Math.abs( ulPoint.x - (ulLatticePointX + (ulLocalX+1)*GameStates.tileSetWidth));
//						int distY = Math.abs( ulPoint.y - (ulLatticePointY + (ulLocalY+1)*GameStates.tileSetHeight));
//						if ( distX < distY ) {
//							/** x is nearer than y, so move the entity right */
////							GamePanel.gp.getPlayerEntity().setX(ulLatticePointX + (ulLocalX+1)*GameStates.tileSetWidth);
//							newMY = 0;
//						} else {
//							/** x is farer than y, so move the entity down */
////							GamePanel.gp.getPlayerEntity().setY(ulLatticePointY + (ulLocalY+1)*GameStates.tileSetHeight - entityHeight/2);
//							newMX = 0;
//						}
//					}
//					/** check ur corner */
//					if ((GameWindow.gw.getObjectMapManagers().get("tree1").checkCollision(new Point(urLatticePointX, urLatticePointY), urLocalX, urLocalY) ) ||
//					(GameWindow.gw.getObjectMapManagers().get("tree2").checkCollision(new Point(urLatticePointX, urLatticePointY), urLocalX, urLocalY) )) {
//						int distX = Math.abs( urPoint.x - (urLatticePointX + urLocalX*GameStates.tileSetWidth));
//						int distY = Math.abs( urPoint.y - (urLatticePointY + (urLocalY+1)*GameStates.tileSetHeight));
//						if ( distX < distY ) {
//							/** x is nearer than y, so move the entity right */
////							GamePanel.gp.getPlayerEntity().setX(urLatticePointX + (urLocalX)*GameStates.tileSetWidth - entityWidth);
//							newMY = 0;
//						} else {
//							/** x is farer than y, so move the entity down */
////							GamePanel.gp.getPlayerEntity().setY(urLatticePointY + (urLocalY+1)*GameStates.tileSetHeight - entityHeight/2);
//							newMX = 0;
//						}
//					}
//					/** check dl corner */
//					if ((GameWindow.gw.getObjectMapManagers().get("tree1").checkCollision(new Point(dlLatticePointX, dlLatticePointY), dlLocalX, dlLocalY) ) ||
//					(GameWindow.gw.getObjectMapManagers().get("tree2").checkCollision(new Point(dlLatticePointX, dlLatticePointY), dlLocalX, dlLocalY) )) {
//						int distX = Math.abs( dlPoint.x - (dlLatticePointX + (dlLocalX+1)*GameStates.tileSetWidth));
//						int distY = Math.abs( dlPoint.y - (dlLatticePointY + dlLocalY*GameStates.tileSetHeight));
//						if ( distX < distY ) {
//							/** x is nearer than y, so move the entity right */
////							GamePanel.gp.getPlayerEntity().setX(dlLatticePointX + (dlLocalX+1)*GameStates.tileSetWidth);
//							newMY = 0;
//						} else {
//							/** x is farer than y, so move the entity up */
////							GamePanel.gp.getPlayerEntity().setY(dlLatticePointY + (dlLocalY)*GameStates.tileSetHeight -entityHeight);
//							newMX = 0;
//						}
//					}
//					/** check dr corner */
//					if ((GameWindow.gw.getObjectMapManagers().get("tree1").checkCollision(new Point(drLatticePointX, drLatticePointY), drLocalX, drLocalY) ) ||
//					(GameWindow.gw.getObjectMapManagers().get("tree2").checkCollision(new Point(drLatticePointX, drLatticePointY), drLocalX, drLocalY) )) {
//						int distX = Math.abs( drPoint.x - (drLatticePointX + drLocalX*GameStates.tileSetWidth));
//						int distY = Math.abs( drPoint.y - (drLatticePointY + drLocalY*GameStates.tileSetHeight));
//						if ( distX < distY ) {
//							/** x is nearer than y, so move the entity right */
////							GamePanel.gp.getPlayerEntity().setX(drLatticePointX + (drLocalX)*GameStates.tileSetWidth - entityWidth);
//							newMY = 0;
//						} else {
//							/** x is farer than y, so move the entity down */
////							GamePanel.gp.getPlayerEntity().setY(drLatticePointY + (drLocalY)*GameStates.tileSetHeight - entityHeight);
//							newMX = 0;
//						}
//					}
//						
//					GamePanel.gp.playerEntity.setMovement(newMX, newMY);
					GamePanel.gp.playerEntity.move(duration);
//				}
				
		
				/** move spaces */
				List<BigInteger> idListTempSpaces = new ArrayList<BigInteger>(SpaceManager.idList);
				boolean playerInsideSpace;
				for (BigInteger i : idListTempSpaces) {
					Space space= SpaceManager.get(i);
					if(space != null) {
//						if(space.getHorizontalMovement() != 0 || space.getVerticalMovement()!=0) {
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
						this.determineAnimation(e);
						e.move(duration);
					}
					//System.out.println("id: "+ e.getId()+" x: "+e.getX()+" y: "+ e.getY()+" mx: "+e.getHorizontalMovement()+" my: "+e.getVerticalMovement());
				}

				/** PlayerManager move */
//				for (int i = 0; i<EntityManager.size(); i++){
				List<BigInteger> idListTempPlayers = new ArrayList<BigInteger>(PlayerManager.idList);
				for (BigInteger i: idListTempPlayers) {
					PlayerEntity e= PlayerManager.get(i);
					/** if too long time passed */
					if(System.currentTimeMillis()-e.getLastSeen() >= 500) {
						e.setVisible(false);
					} else {
//					if(e.getHorizontalMovement() != 0) e.moveOnX(duration);
//					if(e.getVerticalMovement()!=0) e.moveOnY(duration);
						if (e != null) { 
							e.setVisible(true);
							this.determineAnimation(e);
							e.move(duration);
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
	
	public void determineAnimation(Entity entity) {
		if(entity.getMX() > 0) {
			if(entity.getMY() > 0) {
				// dr
				entity.setAnimation(AnimationManager.manager.getAnimation("dr", entity.getTileSet(), GameStates.ani_dr_start, GameStates.ani_dr_end, entity.getAnimation().getTimeBetweenAnimation()));
			} else if (entity.getMY() == 0)
			{
				// r
				entity.setAnimation(AnimationManager.manager.getAnimation("r", entity.getTileSet(), GameStates.ani_r_start, GameStates.ani_r_end, entity.getAnimation().getTimeBetweenAnimation()));
			} else {
				// ur
				entity.setAnimation(AnimationManager.manager.getAnimation("ur", entity.getTileSet(), GameStates.ani_ur_start, GameStates.ani_ur_end, entity.getAnimation().getTimeBetweenAnimation()));
			}
		} else if (entity.getMX() == 0){
			if(entity.getMY() > 0) {
				// d
				entity.setAnimation(AnimationManager.manager.getAnimation("d", entity.getTileSet(), GameStates.ani_d_start, GameStates.ani_d_end, entity.getAnimation().getTimeBetweenAnimation()));
			} else if (entity.getMY() == 0)
			{
				// standing still
				entity.setAnimation(AnimationManager.manager.getAnimation("standard", entity.getTileSet(), GameStates.ani_standard_start, GameStates.ani_standard_end, entity.getAnimation().getTimeBetweenAnimation()));
			} else {
				// u
				entity.setAnimation(AnimationManager.manager.getAnimation("u", entity.getTileSet(), GameStates.ani_u_start, GameStates.ani_u_end, entity.getAnimation().getTimeBetweenAnimation()));
			}
		} else {
			if(entity.getMY() > 0) {
				// dl
				entity.setAnimation(AnimationManager.manager.getAnimation("dl", entity.getTileSet(), GameStates.ani_dl_start, GameStates.ani_dl_end, entity.getAnimation().getTimeBetweenAnimation()));
			} else if (entity.getMY() == 0)
			{
				// l
				entity.setAnimation(AnimationManager.manager.getAnimation("l", entity.getTileSet(), GameStates.ani_l_start, GameStates.ani_l_end, entity.getAnimation().getTimeBetweenAnimation()));
			} else {
				// ul
				entity.setAnimation(AnimationManager.manager.getAnimation("ul", entity.getTileSet(), GameStates.ani_ul_start, GameStates.ani_ul_end, entity.getAnimation().getTimeBetweenAnimation()));
			}
		}
	}
}
