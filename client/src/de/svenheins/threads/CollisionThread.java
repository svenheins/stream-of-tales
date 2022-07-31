package de.svenheins.threads;
import java.awt.Point;

import de.svenheins.main.GUI;
import de.svenheins.main.GameModus;
import de.svenheins.main.GamePanel;
import de.svenheins.main.GameStates;
import de.svenheins.main.GameWindow;
import de.svenheins.managers.PlayerManager;
import de.svenheins.objects.Entity;


public class CollisionThread implements Runnable {
	private long duration, last; 
	private long millis, frames;
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		while (GUI.running) {
			duration = System.currentTimeMillis() - last;
			last = System.currentTimeMillis();
			millis += duration;
			frames +=1;
			if(!GamePanel.gp.isPaused() && GameModus.modus == GameModus.GAME) {
				/** check if player is moving */
//				if (GamePanel.gp.getPlayerEntity().getMX() != 0 || GamePanel.gp.getPlayerEntity().getMY() != 0) {
//					/** playerPosition */
//					Point entityPoint = new Point( (int) GamePanel.gp.getPlayerEntity().getX(), (int) GamePanel.gp.getPlayerEntity().getY() );
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
//							GamePanel.gp.getPlayerEntity().setX(ulLatticePointX + (ulLocalX+1)*GameStates.tileSetWidth-1); // -1
//						} else {
//							/** x is farer than y, so move the entity down */
//							GamePanel.gp.getPlayerEntity().setY(ulLatticePointY + (ulLocalY+1)*GameStates.tileSetHeight - entityHeight/2+1); // 0
//						}
//					}
//					/** check ur corner */
//					if ((GameWindow.gw.getObjectMapManagers().get("tree1").checkCollision(new Point(urLatticePointX, urLatticePointY), urLocalX, urLocalY) ) ||
//					(GameWindow.gw.getObjectMapManagers().get("tree2").checkCollision(new Point(urLatticePointX, urLatticePointY), urLocalX, urLocalY) )) {
//						int distX = Math.abs( urPoint.x - (urLatticePointX + urLocalX*GameStates.tileSetWidth));
//						int distY = Math.abs( urPoint.y - (urLatticePointY + (urLocalY+1)*GameStates.tileSetHeight));
//						if ( distX < distY ) {
//							/** x is nearer than y, so move the entity right */
//							GamePanel.gp.getPlayerEntity().setX(urLatticePointX + (urLocalX)*GameStates.tileSetWidth - entityWidth); // 0
//						} else {
//							/** x is farer than y, so move the entity down */
//							GamePanel.gp.getPlayerEntity().setY(urLatticePointY + (urLocalY+1)*GameStates.tileSetHeight - entityHeight/2 +1); // 0
//						}
//					}
//					/** check dl corner */
//					if ((GameWindow.gw.getObjectMapManagers().get("tree1").checkCollision(new Point(dlLatticePointX, dlLatticePointY), dlLocalX, dlLocalY) ) ||
//					(GameWindow.gw.getObjectMapManagers().get("tree2").checkCollision(new Point(dlLatticePointX, dlLatticePointY), dlLocalX, dlLocalY) )) {
//						int distX = Math.abs( dlPoint.x - (dlLatticePointX + (dlLocalX+1)*GameStates.tileSetWidth));
//						int distY = Math.abs( dlPoint.y - (dlLatticePointY + dlLocalY*GameStates.tileSetHeight));
//						if ( distX < distY ) {
//							/** x is nearer than y, so move the entity right */
//							GamePanel.gp.getPlayerEntity().setX(dlLatticePointX + (dlLocalX+1)*GameStates.tileSetWidth+1); // +1
//						} else { 
//							/** x is farer than y, so move the entity up */
//							GamePanel.gp.getPlayerEntity().setY(dlLatticePointY + (dlLocalY)*GameStates.tileSetHeight -entityHeight-1); // -1
//						}
//					}
//					/** check dr corner */
//					if ((GameWindow.gw.getObjectMapManagers().get("tree1").checkCollision(new Point(drLatticePointX, drLatticePointY), drLocalX, drLocalY) ) ||
//					(GameWindow.gw.getObjectMapManagers().get("tree2").checkCollision(new Point(drLatticePointX, drLatticePointY), drLocalX, drLocalY) )) {
//						int distX = Math.abs( drPoint.x - (drLatticePointX + drLocalX*GameStates.tileSetWidth));
//						int distY = Math.abs( drPoint.y - (drLatticePointY + drLocalY*GameStates.tileSetHeight));
//						if ( distX < distY ) {
//							/** x is nearer than y, so move the entity right */
//							GamePanel.gp.getPlayerEntity().setX(drLatticePointX + (drLocalX)*GameStates.tileSetWidth - entityWidth -1); // -1
//						} else {
//							/** x is farer than y, so move the entity down */
//							GamePanel.gp.getPlayerEntity().setY(drLatticePointY + (drLocalY)*GameStates.tileSetHeight - entityHeight-1); // 0
//						}
//					}
//					
//				}
				
				
//				for (int i = EnemyManager.size()-1; i>=0 ; i--) {
//					// test it the list was shortened since the loop start
//					if (i< EnemyManager.size()) {
//						Entity e= EnemyManager.get(i);
//						if(e != null) {
//							for (int j=PlayerManager.size()-1;j>=0;j--){
//								if (j< PlayerManager.size()) {
//									Entity e2 = PlayerManager.get(j);
//									if(e2 != null) {
//										if(e.collides(e2)){
//											/*if (EntityManager.entitiesCanDestroyEachOther(e, e2))*/
//											if (e instanceof BossEntity) {
//												BossEntity a = (BossEntity) e;
//												System.out.println("Boss getroffen");
//												a.setLife(a.getLife()-1);
//												System.out.println(a.getLife());
//												PlayerManager.remove(e2);
//												if(a.getLife() <= 0) EnemyManager.remove(e);
//											} else if(e instanceof AlienEntity && e2 instanceof ShotEntity){
//												EnemyManager.remove(e);
//												AlienEntity.aliens--;
//											}
//											else {
//												// Enemy Shot and Player disappear
//												EnemyManager.remove(e);
//												PlayerManager.remove(e2);
//												
//											}
//										}
//									} else {
//										// remove null-Object
//										PlayerManager.remove(e2);
//									}
//								// do nothing, because we met an index, that exceeds the dimension of the array!!!
//								} else {}
//							}
//						}
//						else EnemyManager.remove(e);
//					
//					// do nothing, because we met an index, that exceeds the dimension of the array!!!
//					} else {}
//				}
//				if( EnemyManager.size() == 0){
//					BossEntity boss = new BossEntity("boss.png", 300, 70);
//					boss.setHorizontalMovement(350);
//					EnemyManager.add(boss);
//				}
			}
			if (millis >1000 && GamePanel.gp.showStats ==true) {
				System.out.println("Collision-Frames: "+ frames);
				frames = 0;
				millis =0;
			}
			try {
				Thread.sleep(5);
			}
			catch(InterruptedException exception) {
				System.out.println(exception);
			}
		}	
	}

}
