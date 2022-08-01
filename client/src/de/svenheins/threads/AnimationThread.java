package de.svenheins.threads;

import java.math.BigInteger;

import de.svenheins.objects.Entity;
import de.svenheins.objects.PlayerEntity;
import de.svenheins.objects.Space;
import de.svenheins.objects.items.Item;
import de.svenheins.main.EntityStates;
import de.svenheins.main.GUI;
import de.svenheins.main.GamePanel;
import de.svenheins.main.GameStates;
import de.svenheins.main.StatPanel;
import de.svenheins.main.gui.ContainerGUIManager;
import de.svenheins.main.gui.EditorGUIManager;
import de.svenheins.main.gui.PlayerListGUIManager;
import de.svenheins.managers.AnimationManager;
import de.svenheins.managers.EntityManager;
import de.svenheins.managers.ItemManager;
import de.svenheins.managers.PlayerManager;
import de.svenheins.managers.SpaceManager;

public class AnimationThread implements Runnable {
	private long duration, last; 
	private long millis, frames;
	
	@Override
	public void run() {
		while (GUI.running) {
			duration = System.currentTimeMillis() - last;
			last = System.currentTimeMillis();
			millis += duration;
			frames +=1;
			if((!GamePanel.gp.isPaused() || GamePanel.gp.isAllwaysAnimated()) ) {
//				for (int i = EnemyManager.size()-1; i>=0 ; i--) {
//					Entity e= EnemyManager.get(i);
//					if(e != null) e.updateSprite();
//				}
//				System.out.println("real time: "+(System.currentTimeMillis()-GamePanel.gp.getPlayerEntity().getAnimation().getInstantOfAnimation()));
//				System.out.println();
				if (GamePanel.gp.getPlayerEntity().hasChangedStates()) {
					determineAnimation(GamePanel.gp.getPlayerEntity());
					GamePanel.gp.getPlayerEntity().startAnimation();
				}
				GamePanel.gp.updatePlayerSprite();
				
				for (int i = EntityManager.size()-1; i >= 0; i--) {
					Entity entity = null;
					try {
						entity = EntityManager.get(EntityManager.idList.get(i));
					} catch (IndexOutOfBoundsException exception){
						System.out.println(exception);
					} catch (java.lang.NullPointerException e) {
						System.out.println(e);
					}
					if(entity != null) {
						determineAnimation(entity);
						entity.updateSprite();
					}
				}
				
				for (int i = ItemManager.size()-1; i >= 0; i--) {
					Entity entity = null;
					try {
						entity = ItemManager.get(ItemManager.idList.get(i)).getItemEntity();
					} catch (IndexOutOfBoundsException exception){
						System.out.println(exception);
					} catch (java.lang.NullPointerException e) {
						System.out.println(e);
					}
					if(entity != null) {
						determineAnimation(entity);
						entity.updateSprite();
					}
				}
				
				for (int j=PlayerManager.size()-1;j>=0; j--){
					PlayerEntity e2 = null;
					try {
						e2 = PlayerManager.get(PlayerManager.idList.get(j));
					} catch (IndexOutOfBoundsException exception) {
						System.out.println(exception);
					} catch (java.lang.NullPointerException e) {
						System.out.println(e);
					}
					if(e2 != null) {
						//e2.updateSprite();
						
						if (e2.hasChangedStates()) {
							determineAnimation(e2);
//							System.out.println("changes!!!");
							e2.setChangedStates(false);
							e2.startAnimation();
						}
						e2.updateSprite();
					}
				}
//				for (int k = SpaceManager.size()-1; k >=0; k--) {
				for (int k = SpaceManager.size()-1; k >= 0; k --) {
					Space space = null;
					try {
						space = SpaceManager.get(SpaceManager.idList.get(k));
					} catch (IndexOutOfBoundsException exception ) {
						System.out.println(exception);
					} catch (java.lang.NullPointerException e) {
						System.out.println(e);
					}
					if(space != null) space.updateSpace();
				}
				
				try {
					for (BigInteger buttonID : EditorGUIManager.get("floor").idList) {
						EditorGUIManager.get("floor").get(buttonID).updateSprite();
					}
					for (BigInteger buttonID : PlayerListGUIManager.get("playerList").idList) {
						PlayerListGUIManager.get("playerList").get(buttonID).updateSprite();
					}
					for (BigInteger buttonID : StatPanel.sp.contextMenu.idList) {
						StatPanel.sp.contextMenu.get(buttonID).updateSprite();
					}
					for (String inventoryName : ContainerGUIManager.idList) {
						for (Item item: ContainerGUIManager.get(inventoryName).getContainer().getItemList().values()) {
							item.getItemEntity().updateSprite();
						}
					}
				} catch(java.util.ConcurrentModificationException error) {
					System.out.println(error);
				}
				
			}
			// OUTPUT
			if (millis >1000 && GamePanel.gp.showStats ==true) {
				System.out.println("Animation-Frames: "+ frames);
				frames = 0;
				millis =0;
			}
			try {
				Thread.sleep(5);
			}
			catch(InterruptedException exception) {
			
			}
		}
	}
	
	
	public void determineAnimation(Entity entity) {
		if (entity.getSingleState() != EntityStates.EMPTY) {
//			if (entity != GamePanel.gp.getPlayerEntity()) {
//				switch(entity.getSingleState()) {
//				case ATTACKING:
//					switch(entity.getOrientation()) {
//					case LEFT:
//						entity.setSingleAnimation(AnimationManager.manager.getAnimation("attacking_l", entity.getTileSet(), GameStates.ani_attacking_l_start, GameStates.ani_attacking_l_end, entity.getAnimation().getTimeBetweenAnimation()));
//						entity.getSingleAnimation().start();
//						break;
//					case RIGHT:
//						entity.setSingleAnimation(AnimationManager.manager.getAnimation("attacking_r", entity.getTileSet(), GameStates.ani_attacking_r_start, GameStates.ani_attacking_r_end, entity.getAnimation().getTimeBetweenAnimation()));
//						entity.getSingleAnimation().start();
//						break;
//					case UP:
//						entity.setSingleAnimation(AnimationManager.manager.getAnimation("attacking_u", entity.getTileSet(), GameStates.ani_attacking_u_start, GameStates.ani_attacking_u_end, entity.getAnimation().getTimeBetweenAnimation()));
//						entity.getSingleAnimation().start();
//						break;
//					case DOWN:
//						entity.setSingleAnimation(AnimationManager.manager.getAnimation("attacking_d", entity.getTileSet(), GameStates.ani_attacking_d_start, GameStates.ani_attacking_d_end, entity.getAnimation().getTimeBetweenAnimation()));
//						entity.getSingleAnimation().start();
//						break;
//					default: ;
//					}
//					break;
//				}
//			}
		} else {
			switch(entity.getContinuousState()) {
			case STANDING:
				switch(entity.getOrientation()) {
				case LEFT:
					entity.setAnimation(AnimationManager.manager.getAnimation("standing_l", entity.getTileSet(), GameStates.ani_standing_l_start, GameStates.ani_standing_l_end, entity.getAnimation().getTimeBetweenAnimation()));
					break;
				case RIGHT:
					entity.setAnimation(AnimationManager.manager.getAnimation("standing_r", entity.getTileSet(), GameStates.ani_standing_r_start, GameStates.ani_standing_r_end, entity.getAnimation().getTimeBetweenAnimation()));
					break;
				case UP:
					entity.setAnimation(AnimationManager.manager.getAnimation("standing_u", entity.getTileSet(), GameStates.ani_standing_u_start, GameStates.ani_standing_u_end, entity.getAnimation().getTimeBetweenAnimation()));
					break;
				case DOWN:
					entity.setAnimation(AnimationManager.manager.getAnimation("standing_d", entity.getTileSet(), GameStates.ani_standing_d_start, GameStates.ani_standing_d_end, entity.getAnimation().getTimeBetweenAnimation()));
					break;
				default: ;
				}
				break;
			case WALKING:
				switch(entity.getOrientation()) {
				case LEFT:
					entity.setAnimation(AnimationManager.manager.getAnimation("walking_l", entity.getTileSet(), GameStates.ani_walking_l_start, GameStates.ani_walking_l_end, entity.getAnimation().getTimeBetweenAnimation()));
					break;
				case RIGHT:
					entity.setAnimation(AnimationManager.manager.getAnimation("walking_r", entity.getTileSet(), GameStates.ani_walking_r_start, GameStates.ani_walking_r_end, entity.getAnimation().getTimeBetweenAnimation()));
					break;
				case UP:
					entity.setAnimation(AnimationManager.manager.getAnimation("walking_u", entity.getTileSet(), GameStates.ani_walking_u_start, GameStates.ani_walking_u_end, entity.getAnimation().getTimeBetweenAnimation()));
					break;
				case DOWN:
					entity.setAnimation(AnimationManager.manager.getAnimation("walking_d", entity.getTileSet(), GameStates.ani_walking_d_start, GameStates.ani_walking_d_end, entity.getAnimation().getTimeBetweenAnimation()));
					break;
				default: ;
				}
				break;
			case SLEEPING:
				switch(entity.getOrientation()) {
				case LEFT:
					entity.setAnimation(AnimationManager.manager.getAnimation("sleeping_l", entity.getTileSet(), GameStates.ani_sleeping_l_start, GameStates.ani_sleeping_l_end, entity.getAnimation().getTimeBetweenAnimation()));
					break;
				case RIGHT:
					entity.setAnimation(AnimationManager.manager.getAnimation("sleeping_r", entity.getTileSet(), GameStates.ani_sleeping_r_start, GameStates.ani_sleeping_r_end, entity.getAnimation().getTimeBetweenAnimation()));
					break;
				case UP:
					entity.setAnimation(AnimationManager.manager.getAnimation("sleeping_u", entity.getTileSet(), GameStates.ani_sleeping_u_start, GameStates.ani_sleeping_u_end, entity.getAnimation().getTimeBetweenAnimation()));
					break;
				case DOWN:
					entity.setAnimation(AnimationManager.manager.getAnimation("sleeping_d", entity.getTileSet(), GameStates.ani_sleeping_d_start, GameStates.ani_sleeping_d_end, entity.getAnimation().getTimeBetweenAnimation()));
					break;
				default: ;
				}
				break;
	
			default: ;
					
			}
		}
	}

}
