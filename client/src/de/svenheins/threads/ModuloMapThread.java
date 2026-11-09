/* Copyright (C) Sven Heins - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * author: Sven Heins
 * date: 05.07.2015
 */
package de.svenheins.threads;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import de.svenheins.main.EntityStates;
import de.svenheins.main.GUI;
import de.svenheins.main.GameModus;
import de.svenheins.main.GamePanel;
import de.svenheins.main.GameStates;
import de.svenheins.managers.AgentManager;
import de.svenheins.managers.EntityManager;
import de.svenheins.managers.ItemManager;
import de.svenheins.managers.LightManager;
import de.svenheins.objects.Entity;
import de.svenheins.objects.Light;
import de.svenheins.objects.agents.Agent;
import de.svenheins.objects.items.Item;

public class ModuloMapThread implements Runnable {
	private final int sleepingTime = 20;
	
	
	@Override
	public void run() {
		while (GUI.running) {
//			duration = System.currentTimeMillis() - last;
//			last = System.currentTimeMillis();
//			System.out.println("input-thread runs");
			
			if(GameModus.modus == GameModus.GAME) {
//				calculateLight();
				int totalX = GameStates.mapTotalWidth * GamePanel.gp.getMiniMap().getWidth();
				int totalY = GameStates.mapTotalHeight * GamePanel.gp.getMiniMap().getHeight();
				if (LightManager.size() > 0) {
					List<BigInteger> idListTempLight = new ArrayList<BigInteger>(LightManager.idList);
					for (BigInteger i : idListTempLight) {
						Light e = LightManager.get(i);
						if (GamePanel.gp.getPlayerEntity().getX()-e.getLocation().getX() < -totalX/2) e.getLocation().x=(int) (e.getLocation().getX()-totalX);
						if (GamePanel.gp.getPlayerEntity().getY()-e.getLocation().getY() < -totalY/2) e.getLocation().y=(int) (e.getLocation().getY()-totalY);
						if (GamePanel.gp.getPlayerEntity().getX()-e.getLocation().getX() > totalX/2) e.getLocation().x=(int) (e.getLocation().getX()+totalX);
						if (GamePanel.gp.getPlayerEntity().getY()-e.getLocation().getY() > totalY/2) e.getLocation().y=(int) (e.getLocation().getY()+totalY);
						
					}

				}
				
				if (EntityManager.size() > 0) {
					List<BigInteger> idListTempEntities = new ArrayList<BigInteger>(EntityManager.idList);
					for (BigInteger i : idListTempEntities) {
						Entity e = EntityManager.get(i);
						if (e != null && GamePanel.gp.getPlayerEntity().getRoom().equals(e.getRoom())) {
								if (e.getSprite().getImage() != null) {									
									if (GamePanel.gp.getPlayerEntity().getX()-e.getX() < -totalX/2) e.setX(e.getX()-totalX);
									if (GamePanel.gp.getPlayerEntity().getY()-e.getY() < -totalY/2) e.setY(e.getY()-totalY);
									if (GamePanel.gp.getPlayerEntity().getX()-e.getX() > totalX/2) e.setX(e.getX()+totalX);
									if (GamePanel.gp.getPlayerEntity().getY()-e.getY() > totalY/2) e.setY(e.getY()+totalY);
								}
						} 
					}

				}
				
				/** correction of agent positions and agent ActualPathElement for pathfinding*/
				if (AgentManager.size() >0) {
					List<BigInteger> idListTempAgents = new ArrayList<BigInteger>(AgentManager.idList);
					for (BigInteger i : idListTempAgents) {
						Agent e = AgentManager.get(i);
						if (e != null && GamePanel.gp.getPlayerEntity().getRoom().equals(e.getRoom())) {
							if (e.getSprite().getImage() != null) {									
								if (GamePanel.gp.getPlayerEntity().getX()-e.getX() < -totalX/2) e.setX(e.getX()-totalX);
								if (GamePanel.gp.getPlayerEntity().getY()-e.getY() < -totalY/2) e.setY(e.getY()-totalY);
								if (GamePanel.gp.getPlayerEntity().getX()-e.getX() > totalX/2) e.setX(e.getX()+totalX);
								if (GamePanel.gp.getPlayerEntity().getY()-e.getY() > totalY/2) e.setY(e.getY()+totalY);
							}
							
							/** correction of pathElement */
							if (e.getActualPathElement() != null) {
								float pathX = e.getActualPathElement().getX();
								float pathY = e.getActualPathElement().getY();
								if (e.getX()- pathX< -totalX/2) e.getActualPathElement().setX(pathX-totalX);
								if (e.getY()-pathY < -totalY/2) e.getActualPathElement().setY(pathY-totalY);
								if (e.getX()-pathX > totalX/2) e.getActualPathElement().setX(pathX+totalX);
								if (e.getY()-pathY > totalY/2) e.getActualPathElement().setY(pathY+totalY);
							}
						} 
					}
				}
				
				
				if (ItemManager.size() > 0) {
					List<BigInteger> idListTempItems = new ArrayList<BigInteger>(ItemManager.idList);
					for (BigInteger i : idListTempItems) {
						Item e = ItemManager.get(i);
						if (e != null && GamePanel.gp.getPlayerEntity().getRoom().equals(e.getRoom()) && (e.getEntity().getContinuousState() != EntityStates.INVENTORY_SIMPLE && e.getEntity().getContinuousState() != EntityStates.INVENTORY_DAMAGED && e.getEntity().getContinuousState() != EntityStates.INVENTORY_DESTROYED && e.getEntity().getContinuousState() != EntityStates.INVENTORY_GOOD && e.getEntity().getContinuousState() != EntityStates.INVENTORY_MAGIC &&e.getEntity().getContinuousState() != EntityStates.INVENTORY_PERFECT)) {
							if (GamePanel.gp.getPlayerEntity().getX()-e.getEntity().getX() < -totalX/2) e.getEntity().setX(e.getEntity().getX()-totalX);
							if (GamePanel.gp.getPlayerEntity().getY()-e.getEntity().getY() < -totalY/2) e.getEntity().setY(e.getEntity().getY()-totalY);
							if (GamePanel.gp.getPlayerEntity().getX()-e.getEntity().getX() > totalX/2) e.getEntity().setX(e.getEntity().getX()+totalX);
							if (GamePanel.gp.getPlayerEntity().getY()-e.getEntity().getY() > totalY/2) e.getEntity().setY(e.getEntity().getY()+totalY);
						} 
					}

				}
	
			}
				
	
			
			try {
				Thread.sleep(sleepingTime);
			}
			catch(InterruptedException exception) {
				System.out.println(exception);
			}
			
	}
		}


}
