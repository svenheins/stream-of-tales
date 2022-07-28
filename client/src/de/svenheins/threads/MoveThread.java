package de.svenheins.threads;


import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import de.svenheins.main.GUI;
import de.svenheins.main.GameModus;
import de.svenheins.main.GamePanel;
import de.svenheins.main.GameWindow;
import de.svenheins.managers.EntityManager;
import de.svenheins.managers.PlayerManager;
import de.svenheins.managers.SpaceManager;

import de.svenheins.objects.Entity;
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
//				if(changeDirection) {
//					for (int i = 0; i< EnemyManager.size();i++) {
//						Entity e = EnemyManager.get(i);
//						if(e != null) {
//							e.changeDirection();
//						} else EnemyManager.remove(e); 
//							
//					}
//					changeDirection = false;
//				}
//				// Handle Player-entities
//				for (int i = 0; i<PlayerManager.size(); i++){
//					Entity e= PlayerManager.get(i);
//					if(e instanceof ShotEntity){
//						if (e.getY() <= 0){
//							PlayerManager.remove(e);
//						}
//					}
////					if(e.getHorizontalMovement() != 0) e.moveOnX(duration);
////					if(e.getVerticalMovement()!=0) e.moveOnY(duration);
//				}
				// Handle Spaces
//				for (int i = 0; i< SpaceManager.size(); i++) {
				List<BigInteger> idListTempSpaces = new ArrayList<BigInteger>(SpaceManager.idList);
				for (BigInteger i : idListTempSpaces) {
					Space space= SpaceManager.get(i);
					if(space != null) {
//						if(space.getHorizontalMovement() != 0 || space.getVerticalMovement()!=0) {
						/** spaces move without limitations */	
						space.move(duration);
						
						space.setAllXY(space.getX(), space.getY());
							//System.out.println("koordianten: "+ space.getX() + " "+ space.getY() + "  Dimensionen: "+space.getWidth()+" "+space.getHeight()+ "  Speed: "+space.getHorizontalMovement()+ " "+ space.getVerticalMovement());
//						}
						
					}
				}
				/** EntityManager move */
//				for (int i = 0; i<EntityManager.size(); i++){
				List<BigInteger> idListTempEntities = new ArrayList<BigInteger>(EntityManager.idList);
				for (BigInteger i: idListTempEntities) {
					Entity e= EntityManager.get(i);
//					if(e.getHorizontalMovement() != 0) e.moveOnX(duration);
//					if(e.getVerticalMovement()!=0) e.moveOnY(duration);
					if (e != null) { 
						e.move(duration);
					}
					//System.out.println("id: "+ e.getId()+" x: "+e.getX()+" y: "+ e.getY()+" mx: "+e.getHorizontalMovement()+" my: "+e.getVerticalMovement());
				}
				
				GamePanel.gp.playerEntity.move(duration);
				
				/** PlayerManager move */
//				for (int i = 0; i<EntityManager.size(); i++){
				List<BigInteger> idListTempPlayers = new ArrayList<BigInteger>(PlayerManager.idList);
				for (BigInteger i: idListTempPlayers) {
					Entity e= PlayerManager.get(i);
//					if(e.getHorizontalMovement() != 0) e.moveOnX(duration);
//					if(e.getVerticalMovement()!=0) e.moveOnY(duration);
					if (e != null) { 
						e.move(duration);
					}
					//System.out.println("id: "+ e.getId()+" x: "+e.getX()+" y: "+ e.getY()+" mx: "+e.getHorizontalMovement()+" my: "+e.getVerticalMovement());
				}
				
				/** InfoConsole Update */
				GameWindow.gw.gameInfoConsole.update();
				// Handle Enemy-entities
//				if(GamePanel.gp.eye.getHorizontalMovement() != 0)GamePanel.gp.eye.moveOnX(duration);
//				if(GamePanel.gp.eye.getVerticalMovement()!=0) GamePanel.gp.eye.moveOnY(duration);
//				for (int i = 0; i< EnemyManager.size() ; i++) {
//					Entity e= EnemyManager.get(i);
//					if(e != null) {
//	//					EntityManager.changeDirection(e);
////						if(e.getHorizontalMovement() != 0) e.moveOnX(duration);
////						if(e.getVerticalMovement()!=0) e.moveOnY(duration);
//						if(e instanceof AlienEntity || e instanceof BossEntity) {
//							if (e instanceof AlienEntity){
//								AlienEntity a = (AlienEntity) e;
//								int random = new Random().nextInt(10001);
//								if(random==1000 && a.canShoot() ){
//									AlienShot ashot = new AlienShot("alienshot.png", a.getX()+a.getSprite().getWidth()/2, a.getY() + a.getSprite().getHeight());
//									ashot.setVerticalMovement(80);
//									EnemyManager.add(ashot);
//								}
//							} 
//							else if (e instanceof BossEntity) {
//								BossEntity a = (BossEntity) e;
//								int random = new Random().nextInt(51);
//								if(random==50 && a.canShoot()){
//									AlienShot ashot = new AlienShot("alienshot.png", a.getX()+a.getSprite().getWidth()/2, a.getY() + a.getSprite().getHeight());
//									ashot.setVerticalMovement(150);
//									EnemyManager.add(ashot);
//								}
//							} 
//							else {
//								
//							}	
//							if(e.getX() <20 && e.getHorizontalMovement()<0 || e.getX() + e.getSprite().getWidth() > GamePanel.gp.getWidth()-20 && e.getHorizontalMovement() >0) {
//								changeDirection = true;
//							}
//						}
//						
//						if(e instanceof AlienShot){
//							if(e.getY() >= GamePanel.gp.getHeight()-e.getSprite().getHeight()){
//								EnemyManager.remove(e);
//							}
//						}
//					}
//				}
				
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
}
