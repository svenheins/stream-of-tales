/* Copyright (C) Sven Heins - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * author: Sven Heins
 * date: 05.07.2015
 */
package de.svenheins.threads;

import de.svenheins.main.EntityStates;
import de.svenheins.main.GUI;
import de.svenheins.main.GameModus;
import de.svenheins.main.GamePanel;
import de.svenheins.main.GameStates;
import de.svenheins.main.GameWindow;
import de.svenheins.main.MusicName;
import de.svenheins.main.MusicType;
import de.svenheins.main.gui.ContainerGUIManager;
import de.svenheins.managers.TextEffectManager;
import de.svenheins.handlers.InputHandler;
import de.svenheins.objects.Player;
import de.svenheins.objects.PlayerEntity;
import de.svenheins.objects.TextEffect;


public class InputThread implements Runnable{
	
	
	private long duration, last;
	private Player[] players;
	private PlayerEntity playerEntity;
	
	public InputThread(Player[] players) {
		this.players = players;
//		this.playerEntity = playerEntity;
	}

	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		while (GUI.running) {
			duration = System.currentTimeMillis() - last;
			last = System.currentTimeMillis();
//			System.out.println("input-thread runs");
			if(GameModus.modus == GameModus.GAME) {
				gameRun();
			}
			
			try {
				Thread.sleep(20);
			}
			catch(InterruptedException exception) {
				System.out.println(exception);
			}
		}
		
	}
	
	public void gameRun() {
		
		// Handle Game-Input
		if(GUI.gui.getShowConsole() == false) {
			for (int h=0; h< players.length; h++) {
//				ShipEntity s = ships[h];
				InputHandler input = players[h].getInputHandler();
				Player p = players[h];
				playerEntity = GamePanel.gp.getPlayerEntity();
				playerEntity.setMovement(0, 0);
//				GamePanel.gp.setViewPoint((int)playerEntity.getX()-(GameStates.getWidth()/2), (int) playerEntity.getY()-(GameStates.getHeight()/2));
				p.setLastAttack(p.getLastAttack()+duration);
//				s.setHorizontalMovement(0);
//				s.setVerticalMovement(0);
//				if ( PlayerManager.contains(s)){

//				if(p.getLastAttack() > 20*p.getTimebetweenAttacks()) {
					if (input.down && !input.up){
						playerEntity.setMY(playerEntity.getMaxSpeed());
						playerEntity.setWalkingAnimation();
//						GamePanel.gp.setViewPoint(GamePanel.gp.getViewPointX(), GamePanel.gp.getViewPointY()+20);
//						GamePanel.gp.setViewPoint((int)playerEntity.getX()-(GameStates.getWidth()/2), (int) playerEntity.getY()-(GameStates.getHeight()/2));
//						System.out.println("down");
					}
					if (input.up && !input.down){
						playerEntity.setMY(-playerEntity.getMaxSpeed());
						playerEntity.setWalkingAnimation();
//						GamePanel.gp.setViewPoint(GamePanel.gp.getViewPointX(), GamePanel.gp.getViewPointY()-20);
//						GamePanel.gp.setViewPoint((int)playerEntity.getX()-(GameStates.getWidth()/2), (int) playerEntity.getY()-(GameStates.getHeight()/2));
//						System.out.println("up with "+GameStates.DEFAULT_MOVEMENT_ON_Y);
					}
					if (input.left && !input.right){
						playerEntity.setMX(-playerEntity.getMaxSpeed());
						playerEntity.setWalkingAnimation();
//						GamePanel.gp.setViewPoint(GamePanel.gp.getViewPointX()-20, GamePanel.gp.getViewPointY());
//						GamePanel.gp.setViewPoint((int)playerEntity.getX()-(GameStates.getWidth()/2), (int) playerEntity.getY()-(GameStates.getHeight()/2));
					}
					if (input.right && !input.left){
						playerEntity.setMX(playerEntity.getMaxSpeed());
						playerEntity.setWalkingAnimation();
//						System.out.println("right with " +-GameStates.DEFAULT_MOVEMENT_ON_X);
//						GamePanel.gp.setViewPoint(GamePanel.gp.getViewPointX()+20, GamePanel.gp.getViewPointY());
						
					}

//				} else {
//					playerEntity.setMX(0);
//					playerEntity.setMY(0);
//				}
				
					playerEntity.normalizeMovement();
				
					/** Standing */
					if ( playerEntity.getMX() == 0 && playerEntity.getMY()== 0) {
						playerEntity.setStandingAnimation();
						GameWindow.gw.sound.startMusic(MusicType.MOVINGSOUND, MusicName.EMPTY);
					} else {
						playerEntity.setWalkingAnimation();
						GameWindow.gw.sound.startMusic(MusicType.MOVINGSOUND, MusicName.FOOTSTEPS);
					}
					if (input.attack && p.getLastAttack() > p.getTimebetweenAttacks() && !GamePanel.gp.isPaused()){
						/** activate singleState, the waitingForSingleAnimation is changed in the channelUpdateThread */
						playerEntity.setSingleState(EntityStates.ATTACKING);
						playerEntity.setChangedStates(true);
						playerEntity.setWaitingForSingleAnimation(true);
						playerEntity.determineAnimation();
						p.setLastAttack(0);
					}
					if (input.pause ){
						GamePanel.gp.setPause(!GamePanel.gp.isPaused());
						input.pause = false;
					}
					if (input.zoomIn && GamePanel.gp.getZoomFactor()<GameStates.maxZoomFactor && GameWindow.gw.isGameMaster()) {
						GamePanel.gp.setZoomFactor(GamePanel.gp.getZoomFactor()*1.1f);
					}
					if (input.zoomOut && GamePanel.gp.getZoomFactor()>GameStates.minZoomFactor && GameWindow.gw.isGameMaster()) {
						GamePanel.gp.setZoomFactor(GamePanel.gp.getZoomFactor()/1.1f);
					}
					if (input.options ){
						GamePanel.gp.setPause(!GamePanel.gp.isPaused());
						GamePanel.gp.setMenu(!GamePanel.gp.isMenu());
						input.options = false;
					}
					if (input.infoConsole ){
						GUI.gui.setShowInfoConsole(!GUI.gui.getShowInfoConsole());
						TextEffectManager.add(new TextEffect("InfoConsole is "+ (GUI.gui.getShowInfoConsole() ? "ON":"OFF")));
//						GamePanel.gp.setMenu(!GamePanel.gp.isMenu());
						input.infoConsole = false;
					}
					if (input.messageWindow ){
						GUI.gui.messageWindow.setVisible(!GUI.gui.messageWindow.isVisible());
						TextEffectManager.add(new TextEffect("MessageWindow is "+ (GUI.gui.messageWindow.isVisible() ? "ON":"OFF")));
//						GamePanel.gp.setMenu(!GamePanel.gp.isMenu());
						input.messageWindow = false;
					}
					if (input.lightOnOff ){
						GameWindow.gw.setUsingLight(!GameWindow.gw.isUsingLight());
						TextEffectManager.add(new TextEffect("Lightning-Engine is "+ (GameWindow.gw.isUsingLight() ? "ON":"OFF (light = 100%)")));
//						GamePanel.gp.setMenu(!GamePanel.gp.isMenu());
						input.lightOnOff = false;
					}
					if (input.inventory ){
						ContainerGUIManager.get("Player Inventory").setVisible(!ContainerGUIManager.get("Player Inventory").isVisible());
						ContainerGUIManager.get("Player EqBody Inventory").setVisible(!ContainerGUIManager.get("Player EqBody Inventory").isVisible());
						ContainerGUIManager.get("Player EqWeapon Inventory").setVisible(!ContainerGUIManager.get("Player EqWeapon Inventory").isVisible());
						ContainerGUIManager.get("Player EqHelmet Inventory").setVisible(!ContainerGUIManager.get("Player EqHelmet Inventory").isVisible());
						ContainerGUIManager.get("Player EqFeet Inventory").setVisible(!ContainerGUIManager.get("Player EqFeet Inventory").isVisible());
						ContainerGUIManager.get("Player EqShield Inventory").setVisible(!ContainerGUIManager.get("Player EqShield Inventory").isVisible());
						ContainerGUIManager.get("Player EqAmulet Inventory").setVisible(!ContainerGUIManager.get("Player EqAmulet Inventory").isVisible());
						ContainerGUIManager.get("Player EqGlove Inventory").setVisible(!ContainerGUIManager.get("Player EqGlove Inventory").isVisible());
						ContainerGUIManager.get("Player EqShirt Inventory").setVisible(!ContainerGUIManager.get("Player EqShirt Inventory").isVisible());
						ContainerGUIManager.get("Player EqTrousers Inventory").setVisible(!ContainerGUIManager.get("Player EqTrousers Inventory").isVisible());
//						GamePanel.gp.setMenu(!GamePanel.gp.isMenu());
						input.inventory = false;
					}
					
					/** add here new key for the inventory */
					if (GamePanel.gp.getPlayerEntity().getInventory() != null) {
						ContainerGUIManager.get("Player Inventory").setContainer(GamePanel.gp.getPlayerEntity().getInventory());
					}
					
//					GamePanel.gp.setViewPoint((int)playerEntity.getX()-(GameStates.getWidth()/2), (int) playerEntity.getY()-(GameStates.getHeight()/2));
//				}
					
			}
		}
		else if (GUI.gui.getShowConsole() == true) {
			// Handle Input inside the console

		}

		if (GUI.gui.getConsoleInputHandler().console ){
			GUI.gui.setShowConsole(!GUI.gui.getShowConsole());
//			System.out.println("show console: "+GameWindow.gw.getShowConsole());
			GUI.gui.getConsoleInputHandler().console = false;
			//GamePanel.gp.setShowConsole(true);
		}

		
	}

	
	
}
