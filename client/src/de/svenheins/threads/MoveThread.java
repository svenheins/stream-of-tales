package de.svenheins.threads;


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
				GamePanel.gp.playerEntity.move(duration);
		
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
//					if(e.getHorizontalMovement() != 0) e.moveOnX(duration);
//					if(e.getVerticalMovement()!=0) e.moveOnY(duration);
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
