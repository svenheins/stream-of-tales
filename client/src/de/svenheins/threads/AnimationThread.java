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

import de.svenheins.objects.Entity;
import de.svenheins.objects.PlayerEntity;
import de.svenheins.objects.Space;
import de.svenheins.objects.agents.Agent;
import de.svenheins.objects.items.Item;
import de.svenheins.main.GUI;
import de.svenheins.main.GameModus;
import de.svenheins.main.GamePanel;
import de.svenheins.main.GameWindow;
import de.svenheins.main.StatPanel;
import de.svenheins.main.gui.ContainerGUIManager;
import de.svenheins.main.gui.EditorGUIManager;
import de.svenheins.main.gui.PlayerListGUIManager;
import de.svenheins.managers.AgentManager;
import de.svenheins.managers.EntityManager;
import de.svenheins.managers.ItemManager;
import de.svenheins.managers.MainMenuEntityManager;
import de.svenheins.managers.OverlayEntityManager;
import de.svenheins.managers.PlayerAgentManager;
import de.svenheins.managers.PlayerAgentManagerManager;
import de.svenheins.managers.PlayerManager;
import de.svenheins.managers.SpaceManager;
import de.svenheins.managers.TextEffectManager;

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
			if((!GamePanel.gp.isPaused() || GamePanel.gp.isAllwaysAnimated()) && GameModus.modus == GameModus.GAME ) {

				PlayerEntity playerEntity = GamePanel.gp.getPlayerEntity();
				Point mouseCoordinatePoint = new Point( (int) (GamePanel.gp.getMouseCoordinates().x/GamePanel.gp.getZoomFactor()) +GamePanel.gp.getViewPointX(), (int) (GamePanel.gp.getMouseCoordinates().y/GamePanel.gp.getZoomFactor())+GamePanel.gp.getViewPointY());
				/** only update the orientation if there is no active singleAnimation playing */
				if (playerEntity.getSingleAnimation() == null) {
					playerEntity.determineOrientation(mouseCoordinatePoint);
				}
				if (playerEntity.hasChangedStates()) {
					playerEntity.determineAnimation();
					playerEntity.startAnimation();
				} else {
//					System.out.println("states didnt change!");
				}
				
				playerEntity.updateSprite();
				
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
						entity.determineAnimation();
						entity.updateSprite();
					}
				}
				
				for (int i = AgentManager.size()-1; i >= 0; i--) {
					PlayerEntity entity = null;
					try {
						entity = AgentManager.get(AgentManager.idList.get(i));
					} catch (IndexOutOfBoundsException exception){
						System.out.println(exception);
					} catch (java.lang.NullPointerException e) {
						System.out.println(e);
					}
					if(entity != null) {
						if (entity.hasChangedStates()) {
							entity.determineAnimation();
							entity.setChangedStates(false);
							entity.startAnimation();
						}
						entity.updateSprite();
						if (entity instanceof Agent) {
							Entity infoIcon = ((Agent) entity).getInfoIcon();
							if ( infoIcon != null) {
								infoIcon.updateSprite();
							}
						}
					}
				}
				
				for (PlayerAgentManager playerAgentManager : PlayerAgentManagerManager.playerAgentManagerList.values()) {
					List<BigInteger> idListTempPlayerAgents = new ArrayList<BigInteger>(playerAgentManager.getIdList());
					for (BigInteger i: idListTempPlayerAgents) {
						PlayerEntity tempAgent = playerAgentManager.get(i);
						if (tempAgent != null && tempAgent.isVisible()) {
							if (tempAgent.hasChangedStates()) {
								tempAgent.determineAnimation();
//								System.out.println("changes!!!");
								tempAgent.setChangedStates(false);
								tempAgent.startAnimation();
							}
							tempAgent.updateSprite();
							
//							tempAgent.determineAnimation();
//							tempAgent.updateSprite();
//							Entity e= tempAgent;
//							if(e != null) {
//								e.determineAnimation();
//								e.updateSprite();
//							}
//							else
//							GameWindow.gw.gameInfoConsole.appendInfo("I got a NULL Agent");
						} else {
							GUI.gui.gameInfoConsole.appendInfo("I got a NULL Agent");
						}
					}
				}
				
				for (int i = ItemManager.size()-1; i >= 0; i--) {
					Entity entity = null;
					try {
						entity = ItemManager.get(ItemManager.idList.get(i)).getEntity();
					} catch (IndexOutOfBoundsException exception){
						System.out.println(exception);
					} catch (java.lang.NullPointerException e) {
						System.out.println(e);
					}
					if(entity != null) {
						entity.determineAnimation();
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
							e2.determineAnimation();
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
				
				for (int j=OverlayEntityManager.size()-1;j>=0; j--){
					Entity e2 = null;
					try {
						e2 = OverlayEntityManager.get(OverlayEntityManager.idList.get(j));
					} catch (IndexOutOfBoundsException exception) {
						System.out.println(exception);
					} catch (java.lang.NullPointerException e) {
						System.out.println(e);
					}
					if(e2 != null) {
						//e2.updateSprite();
						
//						if (e2.hasChangedStates()) {
//							e2.determineAnimation();
////							System.out.println("changes!!!");
//							e2.setChangedStates(false);
//							e2.startAnimation();
//						}
						e2.updateSprite();
					}
				}
				
				try {
					for (BigInteger buttonID : EditorGUIManager.get("floor").idList) {
						if (EditorGUIManager.get("floor").get(buttonID) != null) 
							EditorGUIManager.get("floor").get(buttonID).updateSprite();
					}
					for (BigInteger buttonID : PlayerListGUIManager.get("playerList").idList) {
						if ( PlayerListGUIManager.get("playerList").get(buttonID)!= null) 
							PlayerListGUIManager.get("playerList").get(buttonID).updateSprite();
					}
					for (BigInteger buttonID : StatPanel.sp.contextMenu.idList) {
						if (StatPanel.sp.contextMenu.get(buttonID) != null)	
							StatPanel.sp.contextMenu.get(buttonID).updateSprite();
					}
					for (String inventoryName : ContainerGUIManager.idList) {
						for (Item item: ContainerGUIManager.get(inventoryName).getContainer().getItemList().values()) {
							if(item.getEntity() != null) {
								item.getEntity().determineAnimation();
								item.getEntity().updateSprite();
							}
//							item.getEntity().updateSprite();
						}
					}
				} catch(java.util.ConcurrentModificationException error) {
					System.out.println(error);
				}
				
			} else if (GameModus.modus == GameModus.MAINMENU) {
				for (int j=MainMenuEntityManager.size()-1;j>=0; j--){
					Entity e2 = null;
					try {
						e2 = MainMenuEntityManager.get(MainMenuEntityManager.idList.get(j));
					} catch (IndexOutOfBoundsException exception) {
						System.out.println(exception);
					} catch (java.lang.NullPointerException e) {
						System.out.println(e);
					}
					if(e2 != null) {
						//e2.updateSprite();
						
//						if (e2.hasChangedStates()) {
//							e2.determineAnimation();
////							System.out.println("changes!!!");
//							e2.setChangedStates(false);
//							e2.startAnimation();
//						}
						e2.updateSprite();
					}
				}
			}
			
			/** update the TextEffectManager */
			TextEffectManager.update();
			
			
			// OUTPUT
			if (millis >1000 && GamePanel.gp.showStats ==true) {
				System.out.println("Animation-Frames: "+ frames);
				frames = 0;
				millis =0;
			}
			try {
				Thread.sleep(10);
			}
			catch(InterruptedException exception) {
			
			}
		}
	}

}
