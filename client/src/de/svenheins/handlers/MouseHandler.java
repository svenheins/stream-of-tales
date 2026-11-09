/* Copyright (C) Sven Heins - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * author: Sven Heins
 * date: 05.07.2015
 */
package de.svenheins.handlers;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.math.BigInteger;
import java.util.ArrayList;

import javax.swing.SwingUtilities;

import de.svenheins.objects.AreaInfluence;
import de.svenheins.objects.InteractionArea;
import de.svenheins.objects.InteractionTile;
import de.svenheins.objects.ItemInfluence;
import de.svenheins.objects.LocalObject;
import de.svenheins.objects.PlayerEntity;
import de.svenheins.objects.Space;
import de.svenheins.objects.WorldPosition;
import de.svenheins.objects.agents.Agent;
import de.svenheins.objects.items.Food;
import de.svenheins.objects.items.Item;
//import de.svenheins.objects.items.materials.Wood;

import de.svenheins.objects.items.Material;
import de.svenheins.functions.MyMath;


import de.svenheins.main.AttributeType;
import de.svenheins.main.ConnectionWindow;
import de.svenheins.main.EntityStates;
import de.svenheins.main.GUI;
import de.svenheins.main.GameModus;
import de.svenheins.main.GamePanel;
import de.svenheins.main.GameStates;
import de.svenheins.main.GameWindow;
import de.svenheins.main.Priority;
import de.svenheins.main.SoundEffect;
import de.svenheins.main.StatPanel;
import de.svenheins.main.TileType;
import de.svenheins.main.WorldType;
import de.svenheins.main.gui.ContainerGUI;
import de.svenheins.main.gui.ContainerGUIManager;
import de.svenheins.main.gui.EditorGUIManager;
import de.svenheins.main.gui.PlayerListGUIManager;
import de.svenheins.managers.AgentManager;
import de.svenheins.managers.AreaInfluenceManager;
import de.svenheins.managers.EntityManager;
import de.svenheins.managers.InteractionManager;
import de.svenheins.managers.ItemManager;
import de.svenheins.managers.ObjectMapManager;
import de.svenheins.managers.PlayerManager;
import de.svenheins.managers.SpaceManager;
import de.svenheins.messages.ClientMessages;

public class MouseHandler implements MouseListener, MouseMotionListener{

	private boolean dragging = false;
	protected Space dragSpace=null; 
	private boolean mouseLeftDown = false;
	private boolean isRunning = false;
	
//	protected int localX, localY;
	
//	public void mousePressed(MouseEvent e) {
//	    if (e.getButton() == MouseEvent.BUTTON1) {
//	        mouseDown = true;
//	        initThread();
//	    }
//	}

//	public void mouseReleased(MouseEvent e) {
//	    if (e.getButton() == MouseEvent.BUTTON1) {
//	        mouseDown = false;
//	    }
//	}

	
	private synchronized boolean checkAndMark() {
	    if (isRunning) return false;
	    isRunning = true;
	    return true;
	}
	
	private void initPlayerAttackAndInteractThread(Point correctedPoint) {
	    if (checkAndMark()) {
	        new Thread() {
	            public void run() {
	                do {
	                    playerEntityAttackAndInteract(correctedPoint);
	                } while (mouseLeftDown);
	                isRunning = false;
	            }
	        }.start();
	    }
	}
	
	@Override
	public void mouseClicked(MouseEvent mouseEvent) {
		Point point = mouseEvent.getPoint();
//		if (SwingUtilities.isLeftMouseButton(mouseEvent)) {
//			if (GameModus.modus == GameModus.GAME && GameWindow.gw.getShowConsole() == false){
//				gameMouseLeftClicked(point);
//			} else 
//			if (GameModus.modus == GameModus.MAINMENU) {
//				mainMenuMouseLeftClicked(point);
//			}
//		}
//		if (SwingUtilities.isRightMouseButton(mouseEvent)) {
//			if (GameModus.modus == GameModus.GAME && GameWindow.gw.getShowConsole() == false){
//				gameMouseRightClicked(point);
//			} else 
//			if (GameModus.modus == GameModus.MAINMENU) {
//				mainMenuMouseRightClicked(point);
//			}
//		}
		
		
	}

	@Override
	public void mouseEntered(MouseEvent mouseEvent) {
		
	}

	@Override
	public void mouseExited(MouseEvent mouseEvent) {
		
	}

	@Override
	public void mousePressed(MouseEvent mouseEvent) {
		Point point = mouseEvent.getPoint();
		
		if (SwingUtilities.isLeftMouseButton(mouseEvent)) {
			if (GameModus.modus == GameModus.GAME && GUI.gui.getShowConsole() == false){
				gameMouseLeftPressed(point);
//				System.out.println("left pressed");
			}
		}
		if (SwingUtilities.isRightMouseButton(mouseEvent)) {
			
			if (GameModus.modus == GameModus.GAME && GUI.gui.getShowConsole() == false){
				gameMouseRightPressed(point);
			}
		}
		
		
		
	}

	@Override
	public void mouseReleased(MouseEvent mouseEvent) {
		Point point = mouseEvent.getPoint();
		if (SwingUtilities.isLeftMouseButton(mouseEvent)) {
			if (GameModus.modus == GameModus.GAME && GUI.gui.getShowConsole() == false){
				gameMouseReleased(point);
			} else	if (GameModus.modus == GameModus.MAINMENU){
				mainMenuMouseLeftClicked(point);
			}
		}
		if (SwingUtilities.isRightMouseButton(mouseEvent)) {
//			if (GameModus.modus == GameModus.GAME && GameWindow.gw.getShowConsole() == false){
//				gameMouseReleased(point);
//			} else	if (GameModus.modus == GameModus.MAINMENU){
//				mainMenuMouseLeftClicked(point);
//			}
		}
		
		if (mouseEvent.getButton() == MouseEvent.BUTTON1) {
	        mouseLeftDown = false;
	    }
		
	}
	
	public void gameMouseReleased(Point point) {
		// TODO Auto-generated method stub
//		this.gameMouseClicked(point);
		if (dragSpace != null) this.dragSpace.setUpdateByServer(true);
	}

	@Override
	public void mouseDragged(MouseEvent mouseEvent) {
		Point point = mouseEvent.getPoint();
		
		if (SwingUtilities.isLeftMouseButton(mouseEvent)) {
			GamePanel.gp.setDeleteModus(false);
		}
		if (SwingUtilities.isRightMouseButton(mouseEvent)) {
			GamePanel.gp.setDeleteModus(true);
		}
		
		if (GameModus.modus == GameModus.GAME && GUI.gui.getShowConsole() == false){
			gameMouseDragged(point);
		}
		
	}

	@Override
	public void mouseMoved(MouseEvent mouseEvent) {
		Point point = mouseEvent.getPoint();
		if (GameModus.modus == GameModus.GAME && GUI.gui.getShowConsole() == false){
			gameMouseMoved(point);
		} else
		if (GameModus.modus == GameModus.MAINMENU){
			mainMenuMouseMoved(point);
		}
		
		
	}

	
	
	/**
	 * @ModusImplementation: MAINMENU
	 * @Method: mouseLeftClicked
	 * @param point
	 */
	public void mainMenuMouseLeftClicked(Point point) {
		if (GamePanel.gp.connectButton.getPolygon().get(0).contains(point)) {
			new ConnectionWindow();
		}
	}
	
	/**
	 * @ModusImplementation: MAINMENU
	 * @Method: mouseRightClicked
	 * @param point
	 */
	public void mainMenuMouseRightClicked(Point point) {
//		if (GamePanel.gp.connectButton.getPolygon().get(0).contains(point)) {
//			new ConnectionWindow();
//		}
	}
	
	
	/**
	 * @ModusImplementation: GAME
	 * @Method: mouseClicked
	 * @param point
	 */
//	public void gameMouseLeftClicked(Point point) {
//		Point p_save = new Point(point);
//		Point correctedPoint = new Point( (int) (point.x/GamePanel.gp.getZoomFactor()) +GamePanel.gp.getViewPointX(), (int) (point.y/GamePanel.gp.getZoomFactor())+GamePanel.gp.getViewPointY());
//		point = correctedPoint;
//		/** update contextGUI Menu */
//		if (StatPanel.sp.contextMenu.isVisible()) {
//			StatPanel.sp.contextMenu.mouseClick(p_save);
//		}
//		
//		
//	}
	
	/**
	 * @ModusImplementation: GAME
	 * @Method: mouseClicked
	 * @param point
	 */
	public void gameMouseRightClicked(Point point) {
		Point p_save = new Point(point);
		Point correctedPoint = new Point( (int) (point.x/GamePanel.gp.getZoomFactor()) +GamePanel.gp.getViewPointX(), (int) (point.y/GamePanel.gp.getZoomFactor())+GamePanel.gp.getViewPointY());
		point = correctedPoint;
		
		for (BigInteger i : AgentManager.idList) {
			if (AgentManager.get(i).contains(point) && GameWindow.gw.isGameMaster()) {
//				StatPanel.sp.contextMenu.create(EntityManager.get(i), p_save.x-GameStates.contextMenuFrameDistX, p_save.y-GameStates.contextMenuFrameDistY);
//				Goal goal = new Goal(new WorldPosition(GamePanel.gp.getPlayerEntity().getX(), GamePanel.gp.getPlayerEntity().getY()));//, GamePanel.gp.getPlayerEntity().getId(), GamePanel.gp.getPlayerEntity());
				AgentManager.get(i).getAttributes()[AttributeType.LIFE.ordinal()] = -100.0f;
//				System.out.println("added new goal!");
//				AgentManager.get(i).getActualGoal().setEntity(GamePanel.gp.getPlayerEntity());
			}
		}
		
		for(BigInteger i : PlayerManager.idList){
			if (PlayerManager.get(i).contains(point)) {
				// open edit-windows
				//GamePanel.gp.setPause(true);
//				new EditWindow(EntityManager.get(i));
				StatPanel.sp.contextMenu.create(PlayerManager.get(i), p_save.x-GameStates.contextMenuFrameDistX, p_save.y-GameStates.contextMenuFrameDistY);
				if (StatPanel.sp.infoBox.isVisible()) {
					StatPanel.sp.infoBox.setVisible(false);
				}
			}
		}
		
		if (GamePanel.gp.getPlayerEntity().contains(point)) {
			StatPanel.sp.contextMenu.create(GamePanel.gp.getPlayerEntity(), p_save.x-GameStates.contextMenuFrameDistX, p_save.y-GameStates.contextMenuFrameDistY);
			if (StatPanel.sp.infoBox.isVisible()) {
				StatPanel.sp.infoBox.setVisible(false);
			}
		}
		
		/** Set new ViewPoint 
		 * (this is set automatically)
		 * */
//		GamePanel.gp.setViewPoint(GamePanel.gp.getViewPointX()+p_save.x-(GameStates.width/2) ,GamePanel.gp.getViewPointY()+ p_save.y-(GameStates.height/2));

	}
	
	
	
	
	/**
	 * @ModusImplementation: GAME
	 * @Method: mouseClicked
	 * @param point
	 */
	public void gameMouseLeftPressed(Point point) {
		/** update orientation of the player */
		Point correctedPoint = new Point( (int) (point.x/GamePanel.gp.getZoomFactor()) +GamePanel.gp.getViewPointX(), (int) (point.y/GamePanel.gp.getZoomFactor())+GamePanel.gp.getViewPointY());
		GamePanel.gp.setMouseCoordinates(point);
		
		boolean clickedOnGUI = false;
		if (StatPanel.sp.contextMenu.isVisible()) {
			if(StatPanel.sp.contextMenu.mouseClick(point) == true)  {
				clickedOnGUI = true;
			}
		} else {
		
			for ( String editorName  : EditorGUIManager.idList) {
				if (editorName.equals("floor")) {
					if (EditorGUIManager.get(editorName).mouseClick(point) == true) clickedOnGUI = true;
					GamePanel.gp.setPaintType(EditorGUIManager.get(editorName).getIntValue());
					GamePanel.gp.setPaintLayer(EditorGUIManager.get(editorName).getStrValue());
				}
			}
			for ( String playerGUIName  : PlayerListGUIManager.idList) {
				if (playerGUIName.equals("playerList")) {
					if (PlayerListGUIManager.get(playerGUIName).mouseClick(point) != null) clickedOnGUI = true;
				}
			}
	
			for(BigInteger i : EntityManager.idList){
				if (EntityManager.get(i).contains(correctedPoint) && EntityManager.get(i).getRoom().equals(GamePanel.gp.getPlayerEntity().getRoom())) {
					clickedOnGUI = false;
				}
			}
			if (GamePanel.gp.getPlayerEntity().contains(correctedPoint)) {
				/** ignore left-clicks on the player himself */
				clickedOnGUI = false;
			}
			boolean clickedOnAnyContainer = false;
			for (ContainerGUI congui : ContainerGUIManager.containerGUIList.values()) {
				if (congui.getBackgroundSpace().contains(point) && congui.isVisible()) {
	//				System.out.println("clicked on inventory");
					congui.mouseClick(point);
					clickedOnGUI = true;
					clickedOnAnyContainer = true;
					GamePanel.gp.getPlayerEntity().setChangedStates(true);
				}
			}
			if (!clickedOnAnyContainer && GamePanel.gp.getMouseItem() != null) {
				int putX = 0;
				int putY = 0;
				switch (GamePanel.gp.getPlayerEntity().getOrientation()) {
				case RIGHT:
					putX = (int) (GamePanel.gp.getPlayerEntity().getX() + GamePanel.gp.getPlayerEntity().getWidth()/2 + GameStates.dropDistance);
					putY = (int) (GamePanel.gp.getPlayerEntity().getY() + GamePanel.gp.getPlayerEntity().getHeight()*3/4 - GamePanel.gp.getMouseItem().getEntity().getHeight()/2);
					break;
				case LEFT:
					putX = (int) (GamePanel.gp.getPlayerEntity().getX() + GamePanel.gp.getPlayerEntity().getWidth()/2 - (GameStates.dropDistance+GamePanel.gp.getMouseItem().getEntity().getWidth()));
					putY = (int) (GamePanel.gp.getPlayerEntity().getY() + GamePanel.gp.getPlayerEntity().getHeight()*3/4 -GamePanel.gp.getMouseItem().getEntity().getHeight()/2);
					break;
				case UP:
					putX = (int) (GamePanel.gp.getPlayerEntity().getX() + GamePanel.gp.getPlayerEntity().getWidth()/2- GamePanel.gp.getMouseItem().getEntity().getWidth()/2);
					putY = (int) (GamePanel.gp.getPlayerEntity().getY() + GamePanel.gp.getPlayerEntity().getHeight()*3/4 - (GameStates.dropDistance+GamePanel.gp.getMouseItem().getEntity().getHeight()));
					break;
				case DOWN:
					putX = (int) (GamePanel.gp.getPlayerEntity().getX() + GamePanel.gp.getPlayerEntity().getWidth()/2 - GamePanel.gp.getMouseItem().getEntity().getWidth()/2);
					putY = (int) (GamePanel.gp.getPlayerEntity().getY() + GamePanel.gp.getPlayerEntity().getHeight()*3/4 + (GameStates.dropDistance));
					break;
					
				default: ;
				}
				GamePanel.gp.dropMouseItem(new Point(putX, putY));
				clickedOnGUI = true;
			}
			
			for (String ircUser : AgentManager.ircUserAgentList.keySet()) {
				Agent ircAgent = AgentManager.get(AgentManager.ircUserAgentList.get(ircUser));
				if (ircAgent.getInfoIcon() != null) {
					if (ircAgent.getInfoIcon().contains(correctedPoint)) {
						if (ircAgent.getMessageBoard().size() > 0 ) {
							GUI.gui.messageWindow.writeMessageToWindow(StatPanel.sp.graphics2D, ircUser, ircAgent.retrieveFirstMessage());
							GUI.gui.messageWindow.setVisible(true);
						}
					}
				}
			}
			
			/** only paint tiles if no Editor icon was hit*/
			if (!clickedOnGUI) {
				/** only paint if we are the GameMaster (else the changes wouldn't effect the map anyways) */
				if (GameWindow.gw.isGameMaster()) {
					GamePanel.gp.setDeleteModus(false);
					GamePanel.gp.paintAfterDeleteTiles(point, GamePanel.gp.getPaintLayer(), GamePanel.gp.getPaintType(), GamePanel.gp.isDeleteModus());
				} else {
					/** here the player just attacks and interacts with surrounding */
					mouseLeftDown = true;
				    initPlayerAttackAndInteractThread(correctedPoint);
				}
			}
			
			/** update item info */
			for (ContainerGUI congui : ContainerGUIManager.containerGUIList.values()) {
				if (congui.getBackgroundSpace().contains(point) && congui.isVisible()) {
					Item item = congui.mouseMoved(point);
					if (item != null) {
						StatPanel.sp.infoBox.create(item, point.x-GameStates.infoBoxTextDistance, point.y-GameStates.infoBoxTextDistance);
					}
				}
			}
		}
	}
	
	
	public void gameMouseRightPressed(Point point) {
		boolean clickedOnGUI = false;
		for ( String playerGUIName  : PlayerListGUIManager.idList) {
			if (playerGUIName.equals("playerList")) {
//				System.out.println(point.x+" "+ point.y);
				PlayerEntity playerEntity = PlayerListGUIManager.get(playerGUIName).mouseClick(point);
				if (playerEntity != null)  {
					clickedOnGUI = true;
					StatPanel.sp.contextMenu.create(playerEntity, point.x-GameStates.contextMenuFrameDistX, point.y-GameStates.contextMenuFrameDistY);
				}
//				GamePanel.gp.setPaintType(PlayerListGUIManager.get(playerGUIName).getIntValue());
//				GamePanel.gp.setPaintLayer(PlayerListGUIManager.get(playerGUIName).getStrValue());
			}
		}
		Point correctedPoint = new Point( (int) (point.x/GamePanel.gp.getZoomFactor()) +GamePanel.gp.getViewPointX(), (int) (point.y/GamePanel.gp.getZoomFactor())+GamePanel.gp.getViewPointY());
		for(BigInteger i : EntityManager.idList){
			if (EntityManager.get(i).contains(correctedPoint)) {
				StatPanel.sp.contextMenu.create(EntityManager.get(i), point.x, point.y);
				clickedOnGUI = true;
			}
		}
		
		for (BigInteger i : AgentManager.idList) {
			if (AgentManager.get(i).contains(correctedPoint) && GameWindow.gw.isGameMaster()) {
//				StatPanel.sp.contextMenu.create(EntityManager.get(i), p_save.x-GameStates.contextMenuFrameDistX, p_save.y-GameStates.contextMenuFrameDistY);
//				Goal goal = new Goal(new WorldPosition(GamePanel.gp.getPlayerEntity().getX(), GamePanel.gp.getPlayerEntity().getY()));//, GamePanel.gp.getPlayerEntity().getId(), GamePanel.gp.getPlayerEntity());
//				AgentManager.get(i).getGoalList().add(goal);
//				AgentManager.get(i).setActualGoal(goal);
//				System.out.println("added new goal!");
				AgentManager.get(i).getAttributes()[AttributeType.LIFE.ordinal()] = -100.0f;
//				AgentManager.get(i).getActualGoal().setEntity(GamePanel.gp.getPlayerEntity());
			}
		}
		
		for(BigInteger i : PlayerManager.idList){
			if (PlayerManager.get(i) != null) {
				if (PlayerManager.get(i).contains(correctedPoint)) {
					// open edit-windows
					//GamePanel.gp.setPause(true);
	//				new EditWindow(EntityManager.get(i));
					StatPanel.sp.contextMenu.create(PlayerManager.get(i), point.x-GameStates.contextMenuFrameDistX, point.y-GameStates.contextMenuFrameDistY);
					clickedOnGUI = true;
				}
			}
		}
		if (GamePanel.gp.getPlayerEntity().contains(correctedPoint)) {
			StatPanel.sp.contextMenu.create(GamePanel.gp.getPlayerEntity(), point.x-GameStates.contextMenuFrameDistX, point.y-GameStates.contextMenuFrameDistY);
			clickedOnGUI = true;
		}
		for (ContainerGUI congui : ContainerGUIManager.containerGUIList.values()) {
			if (congui.getBackgroundSpace().contains(point) && congui.isVisible() && congui.getName().equals("Player Inventory")) {
//				System.out.println("clicked on inventory");
//				congui.mouseClick(point);
				Item item = congui.mouseMoved(point);
//				StatPanel.sp.infoBox.create(item, point.x-GameStates.infoBoxTextDistance, point.y-GameStates.infoBoxTextDistance);
				StatPanel.sp.contextMenu.create(item, point.x-GameStates.contextMenuFrameDistX, point.y-GameStates.contextMenuFrameDistY);
				clickedOnGUI = true;
			}
		}
		
		/** right click onto the EditorGUI switches the size of the paint */
		for ( String editorName  : EditorGUIManager.idList) {
			if (editorName.equals("floor")) {
				if (EditorGUIManager.get(editorName).mouseClick(point) == true) GamePanel.gp.setSmallPaint(!GamePanel.gp.isSmallPaint());
			}
		}
		
		/** close the messageWindow if clicked */
		if (GUI.gui.messageWindow.isVisible() && GUI.gui.messageWindow.contains(point)) {
			GUI.gui.messageWindow.setVisible(false);
		}
		
		
		/** only paint tiles if no Editor icon was hit*/
		if (!clickedOnGUI && GameWindow.gw.isGameMaster()) {
			GamePanel.gp.setDeleteModus(true);
			GamePanel.gp.paintAfterDeleteTiles(point, GamePanel.gp.getPaintLayer(), GamePanel.gp.getPaintType(), GamePanel.gp.isDeleteModus());
		} 
		
		if (!clickedOnGUI && GamePanel.gp.getMouseItem() != null && MyMath.getDistance(point, new Point(GameStates.width/2, GameStates.height/2)) < GameStates.buildDistance) {
			if (GamePanel.gp.getMouseItem() instanceof Material) {
				int tileType = -1;
				String paintLayer = "";
				boolean deleteModus = false;
				int needQuantity = 1;
				switch (GamePanel.gp.getMouseItem().getItemCode()) {
				case WOOD: {
					tileType = GameStates.fluidWoodTile;
					paintLayer = "tree";
					needQuantity = 1;
					break;
				}
				case STONE: {
					tileType = GameStates.fluidStoneTile;
					paintLayer = "tree";
					needQuantity = 1;
					break;
				}
				case SHOVEL: {
					tileType = GameStates.holeTile;
					paintLayer = "hole";
					break;
				}
				case SOIL: {
					if (GameWindow.gw.getWorldType(GamePanel.gp.getPlayerEntity().getRoom()) == WorldType.BASE) {
						tileType = GameStates.dirtTrackTile;
						paintLayer = "cobble";
						needQuantity = 1;
					} else {
						tileType = GameStates.fluidSoilTile;
						paintLayer = "tree";
						needQuantity = 1;
					}
					break;
				}
				case CAMPFIRE: {
					tileType = GameStates.campfireTile;
					paintLayer = "tree";
				break;
			}
				default: {
					break;
				}
				}
				
				if (tileType != -1 && !paintLayer.equals("") && GamePanel.gp.getMouseItem().getCount() > needQuantity-1) {
					GamePanel.gp.setDeleteModus(deleteModus);
					GamePanel.gp.setPaintType(tileType);
					GamePanel.gp.setPaintLayer(paintLayer);
					if (GamePanel.gp.paintAfterDeleteTiles(point, GamePanel.gp.getPaintLayer(), GamePanel.gp.getPaintType(), GamePanel.gp.isDeleteModus())) {
						if (GamePanel.gp.getMouseItem().getCount() == needQuantity) {
							GamePanel.gp.setMouseItem(null);
						} else {
							GamePanel.gp.getMouseItem().setCount(GamePanel.gp.getMouseItem().getCount()-needQuantity);
						}
						GameWindow.gw.sound.addStartingSoundEffect(SoundEffect.PLACE_OBJECT);
					}
				}
			}
//			clickedOnGUI = true;
		}
		
		if (GamePanel.gp.getMouseItem() != null) {
			if (GamePanel.gp.getMouseItem() instanceof Food) {
				ItemInfluence itemInfluence = ((Food)GamePanel.gp.getMouseItem()).getEffect();
				long timeNow = System.currentTimeMillis();
				itemInfluence.setTimeBegin(timeNow);
				itemInfluence.setTimeEnd(timeNow+((Food)GamePanel.gp.getMouseItem()).getEffectDuration());
				
				/** FIRST Sound, to avoid null point */
				if (((Food)GamePanel.gp.getMouseItem()).isFluid()) {
					GameWindow.gw.sound.addStartingSoundEffect(SoundEffect.DRINKING);
				} else{
					GameWindow.gw.sound.addStartingSoundEffect(SoundEffect.EATING);
				}
				
				/** use mouseitem*/
				if (GamePanel.gp.getMouseItem().getCount() == 1) {
					GamePanel.gp.getPlayerEntity().addItemInfluence(itemInfluence);
					GamePanel.gp.setMouseItem(null);
				} else {
					GamePanel.gp.getPlayerEntity().addItemInfluence(itemInfluence);
					GamePanel.gp.getMouseItem().setCount(GamePanel.gp.getMouseItem().getCount()-1);
				}
				
			}
		}
		
		/** let the infoBox disappear if the click was on th eGUI */
		if (clickedOnGUI) {
			if (StatPanel.sp.infoBox.isVisible()) {
				StatPanel.sp.infoBox.setVisible(false);
			}
		}
		
	}
	
	public void playerEntityAttackAndInteract(Point correctedPoint) {
		long timeNow = System.currentTimeMillis();
		if(timeNow-(GamePanel.gp.getPlayerEntity().getLastAttack()) > GameStates.attackDelay/GamePanel.gp.getPlayerEntity().getAttributes()[AttributeType.ATTACKSPEED.ordinal()] ) {
			/** successfully attack! */
			GamePanel.gp.getPlayerEntity().setLastAttack(timeNow);
			
			PlayerEntity playerEntity = GamePanel.gp.getPlayerEntity();
			/** normal player might interact with objects */
			int interactionWidth = GameStates.mapTileSetWidth;
			int interactionHeight = GameStates.mapTileSetHeight;
			/** define center of Player */
			float playerCenterX = playerEntity.getX() + (playerEntity.getWidth()/2);
			float playerCenterY = playerEntity.getY() + ((float)playerEntity.getHeight());
			Point playerPos = new Point((int)playerCenterX,(int) playerCenterY);
			
			float interactionPosX;// = (float) (playerCenterX + GameStates.interactDistance*(((float)(correctedPoint.getX()- playerCenterX))/MyMath.getDistance(playerPos, correctedPoint))-(interactionWidth/2));
			float interactionPosY;
			float attackX;
			float attackY;
			float attackSquareSize = playerEntity.getAttributes()[AttributeType.ATTACKSQUARESIZE.ordinal()];
			float attackDistance = playerEntity.getAttributes()[AttributeType.ATTACKDISTANCE.ordinal()];
			float playerWidth = playerEntity.getWidth();
			float playerHeight = playerEntity.getHeight();
			
			WorldPosition interactionPosition;
			if (MyMath.getDistance(playerPos, correctedPoint) >0) {
				interactionPosX = (float) (playerCenterX + GameStates.interactDistance*(((float)(correctedPoint.getX()- playerCenterX))/MyMath.getDistance(playerPos, correctedPoint))-(interactionWidth/2));
				interactionPosY = (float) (playerCenterY + GameStates.interactDistance*(((float)(correctedPoint.getY()- playerCenterY))/MyMath.getDistance(playerPos, correctedPoint))-(interactionHeight/2));
	//			int attackDistance = interactionWidth;
				attackX = (float) (playerCenterX + ((Math.min(playerWidth, playerHeight)/2)+attackDistance)*(((float)(correctedPoint.getX()- playerCenterX))/MyMath.getDistance(playerPos, correctedPoint))-(attackSquareSize/2));
				attackY = (float) (playerCenterY + ((Math.min(playerWidth, playerHeight)/2)+attackDistance)*(((float)(correctedPoint.getY()- playerCenterY))/MyMath.getDistance(playerPos, correctedPoint))-(attackSquareSize/2));
				interactionPosition = new WorldPosition(interactionPosX, interactionPosY);
			} else {
				interactionPosX = (float) (playerCenterX);
				interactionPosY = (float) (playerCenterY);
				attackX = interactionPosX;
				attackY = interactionPosY;
				interactionPosition = new WorldPosition(interactionPosX, interactionPosY);
			}
			
			/** if the attack delay ran out and the click is not exactly on the center of the player*/
			if (MyMath.getDistance(playerPos, correctedPoint)>0) {
				// OK
			} else {
				// manipulate a little to prevent division by zero
				correctedPoint.x += 1;
				correctedPoint.y += 1;
			}
			float[] slideAttributes = new float[AttributeType.values().length];
			slideAttributes[AttributeType.MX.ordinal()] = (float) (GameStates.slideFactorAttacking*((Math.min(playerWidth, playerHeight)/2)+attackDistance)*((float)(correctedPoint.getX()- playerCenterX))/MyMath.getDistance(playerPos, correctedPoint));//(attackX+attackSquareSize/2+(Math.min(playerWidth, playerHeight)/2)-playerCenterX);
			slideAttributes[AttributeType.MY.ordinal()] = (float) (GameStates.slideFactorAttacking*((Math.min(playerWidth, playerHeight)/2)+attackDistance)*((float)(correctedPoint.getY()- playerCenterY))/MyMath.getDistance(playerPos, correctedPoint));//4*(attackY+attackSquareSize/2+(Math.min(playerWidth, playerHeight)/2)-playerCenterY);
			ItemInfluence slideInfluence = new ItemInfluence(System.currentTimeMillis(), System.currentTimeMillis()+GamePanel.gp.getPlayerEntity().getAnimation().getTimeBetweenAnimation()/2, slideAttributes, Priority.HIGH);

			/** attack */
			float[] attackAttributes = new float[AttributeType.values().length];
			attackAttributes[AttributeType.LIFE.ordinal()]= -GamePanel.gp.getPlayerEntity().getAttributes()[AttributeType.STRENGH.ordinal()];
			attackAttributes[AttributeType.SENDMX.ordinal()] = slideAttributes[AttributeType.MX.ordinal()];
			attackAttributes[AttributeType.SENDMY.ordinal()] = slideAttributes[AttributeType.MY.ordinal()];

			LocalObject localObject = new LocalObject(BigInteger.valueOf(0), "", GamePanel.gp.getPlayerEntity().getRoom(), attackX,attackY,attackSquareSize,attackSquareSize, 0,0,0,0);
//				LocalObject localObject = new LocalObject(BigInteger.valueOf(0), "", correctedPoint.x-interactionWidth/2,correctedPoint.y-interactionHeight/2,interactionWidth,interactionHeight, 0,0,0,0); // mouseVersion
//			long timeNow = System.currentTimeMillis();
			String creator = "player;"+GamePanel.gp.getPlayerEntity().getName()+";"+GamePanel.gp.getPlayerEntity().getId().toString();
			AreaInfluence areaInfluence1 = new AreaInfluence(AreaInfluenceManager.getMaxIDValue().add(GamePanel.gp.getPlayerEntity().getId()), timeNow, timeNow, localObject, GamePanel.gp.getPlayerEntity().getGroupName(), true, attackAttributes, Priority.HIGH, creator);
			
			/** interact with actual interactionItem */
			ObjectMapManager objectMapManagerTree1 = GameWindow.gw.getObjectMapManagers().get("tree1");
			ObjectMapManager objectMapManagerTree2 = GameWindow.gw.getObjectMapManagers().get("tree2");
			
			int[] values = new int[TileType.values().length];
			values[TileType.TREE.ordinal()] = (int) GamePanel.gp.getPlayerEntity().getAttributes()[AttributeType.INTERACTWOOD.ordinal()];
			values[TileType.STONE.ordinal()] = (int) GamePanel.gp.getPlayerEntity().getAttributes()[AttributeType.INTERACTSTONE.ordinal()];
			values[TileType.EARTH.ordinal()] = (int) GamePanel.gp.getPlayerEntity().getAttributes()[AttributeType.INTERACTSOIL.ordinal()];
			values[TileType.COLLECT.ordinal()] = 100;//(int) GamePanel.gp.getPlayerEntity().getAttributes()[AttributeType.INTERACTSOIL.ordinal()];
			InteractionArea iArea = new InteractionArea(interactionPosition, values, interactionWidth, interactionHeight);
			InteractionTile tempInterTile = InteractionManager.interactionList.get(interactionPosition);
			ArrayList<InteractionTile> overflowTile1 = objectMapManagerTree1.interact(iArea);
			ArrayList<InteractionTile> overflowTile2 = objectMapManagerTree2.interact(iArea);
			if (overflowTile1 != null) {
//					System.out.println("overflowTile1 != null");
				if (overflowTile1.size() > 0) {
//						System.out.println("overflowTile1.size() = "+overflowTile1.size());
					for (InteractionTile iTile : overflowTile1) {
//							System.out.println("reached limit at map "+iTile.getPosition().getMapCoordinates().getX()+"|"+iTile.getPosition().getMapCoordinates().getY()+" localX|Y="+iTile.getPosition().getLocalX()+"|"+iTile.getPosition().getLocalY());
						GameWindow.gw.send(ClientMessages.deleteMapObject(iTile, GameWindow.gw.getMapMasterName(), GamePanel.gp.getPlayerEntity().getRoom(), GameWindow.gw.getMapMasterID(), "tree1", "overlayTree1"));
						InteractionManager.remove(iTile.getPosition());
					}
				}
				
				
				
			}
			if ( overflowTile2 != null) {
//					System.out.println("overflowTile2 != null");
				if (overflowTile2.size() > 0) {
//						System.out.println("overflowTile2.size() = "+overflowTile2.size());
					for (InteractionTile iTile : overflowTile2) {
//							System.out.println("reached limit at map "+iTile.getPosition().getMapCoordinates().getX()+"|"+iTile.getPosition().getMapCoordinates().getY()+" localX|Y="+iTile.getPosition().getLocalX()+"|"+iTile.getPosition().getLocalY());
						GameWindow.gw.send(ClientMessages.deleteMapObject(iTile, GameWindow.gw.getMapMasterName(), GamePanel.gp.getPlayerEntity().getRoom(), GameWindow.gw.getMapMasterID(), "tree2", "overlayTree2"));
						InteractionManager.remove(iTile.getPosition());
					}
				}
			}

			addSoundEffect(values);
			GameWindow.gw.send(ClientMessages.addAreaInfluence(areaInfluence1));//(iTile, GameWindow.gw.getGameMasterName(), PlayerManager.get(GameWindow.gw.getGameMasterName()).getId(), "tree1", "overlayTree1"));
			GamePanel.gp.getPlayerEntity().setSingleState(EntityStates.ATTACKING);
			GamePanel.gp.getPlayerEntity().setChangedStates(true);
			
			
			GamePanel.gp.getPlayerEntity().addItemInfluence(slideInfluence);
			GameWindow.gw.sound.addStartingSoundEffect(SoundEffect.STANDARDPLAYER_ATTACK);
			
		}
	}
	
	
	/** enqueu soundeffect depending on interaction values */
	public void addSoundEffect(int[] values) {
		if (InteractionManager.sound.equals("stone") && values[TileType.STONE.ordinal()]>0) {
			InteractionManager.sound = "empty";
			GameWindow.gw.sound.addStartingSoundEffect(SoundEffect.HIT_STONE);
		}
		if (InteractionManager.sound.equals("wood") && values[TileType.TREE.ordinal()] > 0) {
			InteractionManager.sound = "empty";
			GameWindow.gw.sound.addStartingSoundEffect(SoundEffect.HIT_WOOD);
		}
		if (InteractionManager.sound.equals("soil") && values[TileType.EARTH.ordinal()] >0) {
			InteractionManager.sound = "empty";
			GameWindow.gw.sound.addStartingSoundEffect(SoundEffect.HIT_SOIL);
		}
		if (InteractionManager.sound.equals("collect")) {
			InteractionManager.sound = "empty";
			GameWindow.gw.sound.addStartingSoundEffect(SoundEffect.COLLECT_ITEM);
		} 
	}
	
	
	/**
	 * @ModusImplementation: MAINMENU
	 * @Method: mouseMoved
	 * @param point
	 */
	public void mainMenuMouseMoved(Point point){
		
		if (GamePanel.gp.connectButton.getPolygon().get(0).contains(point)) {
			GamePanel.gp.connectButton.setTrans(1.0f);
		} else {
			GamePanel.gp.connectButton.setTrans(0.75f);
		}
	}
	
	/**
	 * @ModusImplementation: GAME
	 * @Method: mouseMoved
	 * @param point
	 */
	public void gameMouseMoved(Point point){
		
		if (dragSpace != null) this.dragSpace.setUpdateByServer(true);
		
		dragging = false;
		dragSpace = null;
		
		Point correctedPoint = new Point( (int) (point.x/GamePanel.gp.getZoomFactor()) +GamePanel.gp.getViewPointX(), (int) (point.y/GamePanel.gp.getZoomFactor())+GamePanel.gp.getViewPointY());
		for (BigInteger i: SpaceManager.idList) {
			boolean inside = false;
			for (int j = 0; j< SpaceManager.get(i).getPolygon().size(); j++) {
				if (SpaceManager.get(i).getPolygon().get(j).contains(correctedPoint))
				inside = true;
			}
		}
		/** update contextGUI Menu */
		if (StatPanel.sp.contextMenu.isVisible()) {
			StatPanel.sp.contextMenu.mouseOver(point);
		}
		
		if (GamePanel.gp.getMouseItem() != null) {
			GamePanel.gp.getMouseItem().getEntity().setX(point.x);
			GamePanel.gp.getMouseItem().getEntity().setY(point.y);
		}
		
		GamePanel.gp.setMouseCoordinates(point);
//		GamePanel.gp.getPlayerEntity().determineOrientation(correctedPoint);

		boolean anyInfoBox = false;
		if (GUI.gui.getShowInfoConsole()) {
			/** InfoBox */
			if (GamePanel.gp.getPlayerEntity().contains(correctedPoint)) {
				StatPanel.sp.infoBox.create(GamePanel.gp.getPlayerEntity(), point.x-GameStates.infoBoxTextDistance, point.y-GameStates.infoBoxTextDistance);
				anyInfoBox = true;
			}
			for ( String playerGUIName  : PlayerListGUIManager.idList) {
				if (playerGUIName.equals("playerList")) {
					PlayerEntity playerEntity = PlayerListGUIManager.get(playerGUIName).mouseMoved(point);
					if (playerEntity != null)  {
						StatPanel.sp.infoBox.create(playerEntity, point.x-GameStates.infoBoxTextDistance, point.y-GameStates.infoBoxTextDistance);
						anyInfoBox = true;
					}
				}
			}
			for (BigInteger i : AgentManager.idList) {
				if (AgentManager.get(i).contains(correctedPoint)) {
					StatPanel.sp.infoBox.create(AgentManager.get(i), point.x-GameStates.infoBoxTextDistance, point.y-GameStates.infoBoxTextDistance);
					anyInfoBox = true;
				}
				if (AgentManager.get(i).getInfoIcon() != null) {
					if (AgentManager.get(i).getInfoIcon().contains(correctedPoint)) {
						StatPanel.sp.infoBox.create(AgentManager.get(i).getInfoIcon(), point.x-GameStates.infoBoxTextDistance, point.y-GameStates.infoBoxTextDistance);
						anyInfoBox = true;
					}
				}
			}
			for(BigInteger i : PlayerManager.idList){
				if (PlayerManager.get(i) != null) {
					if (PlayerManager.get(i).contains(correctedPoint)) {
						StatPanel.sp.infoBox.create(PlayerManager.get(i), point.x-GameStates.infoBoxTextDistance, point.y-GameStates.infoBoxTextDistance);
						anyInfoBox = true;
					}
				}
			}
				
			for(BigInteger i : ItemManager.idList){
				if (ItemManager.get(i) != null) {
					if (ItemManager.get(i).contains(correctedPoint)) {
						StatPanel.sp.infoBox.create(ItemManager.get(i), point.x-GameStates.infoBoxTextDistance, point.y-GameStates.infoBoxTextDistance);
						anyInfoBox = true;
					}
				}
			}
		} 
		for (ContainerGUI congui : ContainerGUIManager.containerGUIList.values()) {
			if (congui.getBackgroundSpace().contains(point) && congui.isVisible()) {
				Item item = congui.mouseMoved(point);
				if (item != null) {
					StatPanel.sp.infoBox.create(item, point.x-GameStates.infoBoxTextDistance, point.y-GameStates.infoBoxTextDistance);
					anyInfoBox = true;
				}
			}
		}	
		/** change visibility depending on mousePosition */
		if (anyInfoBox && !StatPanel.sp.contextMenu.isVisible()) {
			StatPanel.sp.infoBox.setVisible(true);
		} else StatPanel.sp.infoBox.setVisible(false);
		
	}
	
	/**
	 * @ModusImplementation: GAME
	 * @Method: mouseDragged
	 * @param point
	 */
	public void gameMouseDragged(Point point) {
		Point correctedPoint = new Point( (int) (point.x/GamePanel.gp.getZoomFactor()) +GamePanel.gp.getViewPointX(), (int) (point.y/GamePanel.gp.getZoomFactor())+GamePanel.gp.getViewPointY());
		GamePanel.gp.setMouseCoordinates(point);
		
		if (GamePanel.gp.getMouseItem() != null) {
			GamePanel.gp.getMouseItem().getEntity().setX(point.x);
			GamePanel.gp.getMouseItem().getEntity().setY(point.y);
		}
		
		if (StatPanel.sp.contextMenu.isVisible() == false) {
			boolean draggedOnGUI = false;
			for ( String editorName  : EditorGUIManager.idList) {
				if (editorName.equals("floor")) {
					if (EditorGUIManager.get(editorName).mouseClick(point) == true) draggedOnGUI = true;
	//				GamePanel.gp.setPaintType(EditorGUIManager.get(editorName).getIntValue());
	//				GamePanel.gp.setPaintLayer(EditorGUIManager.get(editorName).getStrValue());
				}
			}
			for ( String playerName  : PlayerListGUIManager.idList) {
				if (playerName.equals("playerList")) {
					if (PlayerListGUIManager.get(playerName).mouseClick(point) != null) draggedOnGUI = true;
				}
			}
			for (ContainerGUI congui : ContainerGUIManager.containerGUIList.values()) {
				if (congui.getBackgroundSpace().contains(point) && congui.isVisible()) {
//					System.out.println("dragged on inventory");
//					congui.mouseClick(point);
					draggedOnGUI = true;
				}
			}
			
			/** only paint tiles if no Editor icon was hit*/
			if (!draggedOnGUI) {
				if (GameWindow.gw.isGameMaster()) {
					GamePanel.gp.paintAfterDeleteTiles(point, GamePanel.gp.getPaintLayer(), GamePanel.gp.getPaintType(), GamePanel.gp.isDeleteModus());
				} else {
					/** here the player just attacks and interacts with surrounding */
//					this.playerEntityAttackAndInteract(correctedPoint);
					
//					gameMouseRightPressed(point);
				}
				
			}
		}
	//		Point correctedPoint = new Point( (int) (point.x/GamePanel.gp.getZoomFactor()) +GamePanel.gp.getViewPointX(), (int) (point.y/GamePanel.gp.getZoomFactor())+GamePanel.gp.getViewPointY());
	//		point = correctedPoint;
	//		if(dragging == true && dragSpace != null) {
	//			float newX = (float) (point.getX()-localX);
	//			float newY = (float) (point.getY()-localY);
	//			dragSpace.setAllXY(newX, newY);
	//			dragSpace.setUpdateByServer(false);
	//			GameWindow.gw.send(ClientMessages.editObjectState(OBJECTCODE.SPACE, dragSpace.getId(),  new float[]{newX, newY, dragSpace.getMX(), dragSpace.getMY(), dragSpace.getHeight(), dragSpace.getWidth()}));
	//		} else {
	//			for (BigInteger i: SpaceManager.idList) {
	//				for (int j = 0; j< SpaceManager.get(i).getPolygon().size(); j++) {
	//					if (SpaceManager.get(i).getPolygon().get(j).contains(point)) {
	//						SpaceDisappear spaceDis = new SpaceDisappear(SpaceManager.get(i));
	//						spaceDis.setRGBBefore(SpaceManager.get(i).getRGB());
	//						SpaceManager.get(i).setSpaceAnimation(spaceDis);
	//						SpaceManager.get(i).setMovement(0, 0);
	//						dragSpace = SpaceManager.get(i);
	//						localX = (int) (point.getX()- (int) dragSpace.getPolyX());
	//						localY = (int) (point.getY()- (int) dragSpace.getPolyY());
	//						dragging = true;
	//					}
	//				}
	//			}
	//		}
	}
	
	
//	/** painting on point with the actual paintLayer on the corresponding paintType */
//	public void paintTiles(Point point) {
//		/** only paint if the paintLayer is not "empty" */
//		if (!GamePanel.gp.getPaintLayer().equals("empty")) {
//			Point correctedPoint = new Point( (int) (point.x/GamePanel.gp.getZoomFactor()) +GamePanel.gp.getViewPointX(), (int) (point.y/GamePanel.gp.getZoomFactor())+GamePanel.gp.getViewPointY());
//			point = correctedPoint;
//			
//			if (GamePanel.gp.getPaintLayer().equals("underground")) {
//				int localWidthUnderground = GameStates.ugrMapWidth * GameStates.ugrMapTileSetWidth;
//				int localHeightUnderground = GameStates.ugrMapHeight * GameStates.ugrMapTileSetHeight;
//				int latticePointXUnderground = (int) Math.floor( (float) correctedPoint.x / (localWidthUnderground)) * localWidthUnderground;
//				int latticePointYUnderground = (int) Math.floor( (float) correctedPoint.y / (localHeightUnderground)) * localHeightUnderground;
//				int localXUnderground = (int) Math.floor( (float) (correctedPoint.x - latticePointXUnderground )/ GameStates.ugrMapTileSetWidth);
//				int localYUnderground = (int) Math.floor( (float) (correctedPoint.y - latticePointYUnderground )/ GameStates.ugrMapTileSetHeight);
//				Point pUnderground = new Point(latticePointXUnderground, latticePointYUnderground);
//				
//				int minX = ((int) Math.floor( (float) GamePanel.gp.getPlayerEntity().getX() / (localWidthUnderground)) * localWidthUnderground) - GameStates.factorOfViewDeleteDistance*localWidthUnderground + GameStates.ugrMapTileSetWidth;
//				int maxX = ((int) Math.floor( (float) GamePanel.gp.getPlayerEntity().getX() / (localWidthUnderground)) * localWidthUnderground) + GameStates.factorOfViewDeleteDistance*localWidthUnderground - GameStates.ugrMapTileSetWidth;
//				int minY = (int) Math.floor( (float) GamePanel.gp.getPlayerEntity().getY() / (localHeightUnderground)) * localHeightUnderground - GameStates.factorOfViewDeleteDistance*localHeightUnderground + GameStates.ugrMapTileSetHeight;
//				int maxY = (int) Math.floor( (float) GamePanel.gp.getPlayerEntity().getY() / (localHeightUnderground)) * localHeightUnderground + GameStates.factorOfViewDeleteDistance*localHeightUnderground - GameStates.ugrMapTileSetHeight;
//				Rectangle rect = new Rectangle(minX, minY, maxX-minX, maxY-minY);
//				
//				if ( rect.contains(correctedPoint) ) {	
//					UndergroundMapManager mapManager = GameWindow.gw.getUndergroundMapManagers().get(GamePanel.gp.getPaintLayer());
//					/** create Map if not yet created */
//					if (!mapManager.contains(pUnderground)) {
//						mapManager.createMap(pUnderground);
//					}
//					
//					/** check if deleteModus or paintModus */
//					if (!GamePanel.gp.isDeleteModus()) {
//						mapManager.get(pUnderground).setUl(localXUnderground, localYUnderground, GamePanel.gp.getPaintType());
//						mapManager.get(pUnderground).setUr(localXUnderground, localYUnderground, GamePanel.gp.getPaintType());
//						mapManager.get(pUnderground).setDl(localXUnderground, localYUnderground, GamePanel.gp.getPaintType());
//						mapManager.get(pUnderground).setDr(localXUnderground, localYUnderground, GamePanel.gp.getPaintType());
//						mapManager.get(pUnderground).setIdByCorners(localXUnderground, localYUnderground, GamePanel.gp.getPaintType());
//						mapManager.adjustSurrounding(mapManager.get(pUnderground), localXUnderground, localYUnderground, GamePanel.gp.getPaintType());
//					} else {
//						//MapManager.get(pUnderground).setTile(localXUnderground, localYUnderground, null);
//						mapManager.get(pUnderground).setUl(localXUnderground, localYUnderground, 0);
//						mapManager.get(pUnderground).setUr(localXUnderground, localYUnderground, 0);
//						mapManager.get(pUnderground).setDl(localXUnderground, localYUnderground, 0);
//						mapManager.get(pUnderground).setDr(localXUnderground, localYUnderground, 0);
//						mapManager.get(pUnderground).setIdByCorners(localXUnderground, localYUnderground, GamePanel.gp.getPaintType());
//						mapManager.deleteSurrounding(mapManager.get(pUnderground), localXUnderground, localYUnderground, GamePanel.gp.getPaintType());
//						//MapManager.adjustSurrounding(MapManager.get(pUnderground), localXUnderground, localYUnderground);
//					}
//					/** register lattice Point for save - and send procedure */
//					mapManager.addChangedList(pUnderground);
////					System.out.println(mapManager.changedList.size());
//					/** add corners to saveList and send-procedure if needed */
//					if ((localXUnderground == 0) && (localYUnderground == 0)) {
//						// ul
//						Point p2 = new Point(pUnderground.x-localWidthUnderground, pUnderground.y-localHeightUnderground);
//						mapManager.addChangedList(p2);
//					} 
//					if ( (localXUnderground == GameStates.ugrMapWidth-1) && (localYUnderground == 0)) {
//						// ur
//						Point p2 = new Point(pUnderground.x+localWidthUnderground, pUnderground.y-localHeightUnderground);
//						mapManager.addChangedList(p2);
//					}
//					if (localYUnderground == 0) {
//						// u
//						Point p2 = new Point(pUnderground.x, pUnderground.y-localHeightUnderground);
//						mapManager.addChangedList(p2);
//					} 
//					if ( (localXUnderground == 0) && (localYUnderground == GameStates.ugrMapHeight-1)) {
//						// dl
//						Point p2 = new Point(pUnderground.x-localWidthUnderground, pUnderground.y+localHeightUnderground);
//						mapManager.addChangedList(p2);
//					}
//					if ( (localXUnderground == 0)) {
//						// l
//						Point p2 = new Point(pUnderground.x-localWidthUnderground, pUnderground.y);
//						mapManager.addChangedList(p2);
//					}
//					if ( (localXUnderground == GameStates.ugrMapWidth-1) && (localYUnderground == GameStates.ugrMapHeight-1) ) {
//						// dr
//						Point p2 = new Point(pUnderground.x+localWidthUnderground, pUnderground.y+localHeightUnderground);
//						mapManager.addChangedList(p2);
//					}
//					if ( (localXUnderground == GameStates.ugrMapWidth-1) ) {
//						// r
//						Point p2 = new Point(pUnderground.x+localWidthUnderground, pUnderground.y);
//						mapManager.addChangedList(p2);
//					}
//					if (localYUnderground == GameStates.ugrMapHeight-1) {
//						// d
//						Point p2 = new Point(pUnderground.x, pUnderground.y+localHeightUnderground);
//						mapManager.addChangedList(p2);
//					}
//				}
//			} else {
//				/** here start the small tiles (32x32 normally) */
//				int localWidth = GameStates.mapWidth * GameStates.mapTileSetWidth;
//				int localHeight = GameStates.mapHeight * GameStates.mapTileSetHeight;
//				int latticePointX = (int) Math.floor( (float) correctedPoint.x / (localWidth)) * localWidth;
//				int latticePointY = (int) Math.floor( (float) correctedPoint.y / (localHeight)) * localHeight;
//				int localX = (int) Math.floor( (float) (correctedPoint.x - latticePointX )/ GameStates.mapTileSetWidth);
//				int localY = (int) Math.floor( (float) (correctedPoint.y - latticePointY )/ GameStates.mapTileSetHeight);
//				
//				Point p = new Point(latticePointX, latticePointY);
//				
//				int minX = ((int) Math.floor( (float) GamePanel.gp.getPlayerEntity().getX() / (localWidth)) * localWidth) - GameStates.factorOfViewDeleteDistance*localWidth + GameStates.mapTileSetWidth;
//				int maxX = ((int) Math.floor( (float) GamePanel.gp.getPlayerEntity().getX() / (localWidth)) * localWidth) + GameStates.factorOfViewDeleteDistance*localWidth - GameStates.mapTileSetWidth;
//				int minY = (int) Math.floor( (float) GamePanel.gp.getPlayerEntity().getY() / (localHeight)) * localHeight - GameStates.factorOfViewDeleteDistance*localHeight + GameStates.mapTileSetHeight;
//				int maxY = (int) Math.floor( (float) GamePanel.gp.getPlayerEntity().getY() / (localHeight)) * localHeight + GameStates.factorOfViewDeleteDistance*localHeight - GameStates.mapTileSetHeight;
//				Rectangle rect = new Rectangle(minX, minY, maxX-minX, maxY-minY);
//				
//				if ( rect.contains(correctedPoint) ) {				
//					if (GamePanel.gp.getPaintLayer().equals("tree")) {
//						/** allways paint trees */
//		//				if ( (localX % 4 != 0) && (localX % 4 != 2)) localX++;
//						if ( localX % 2 != 0 ) {
//							if (localX < GameStates.mapWidth-1)
//								localX++;
//							else localX--;
//						}
//						if (localY % 2 != 0 ) {
//							if (localY < GameStates.mapHeight-1)
//								localY++;
//							else localY--;
//						}
//		//				if ((localY % 2 == 0) && ( (localX % 4 == 0) || (localX % 4 == 2))) {
//							ObjectMapManager objectMapManager = null;
//							ObjectMapManager overlayMapManager = null;
//							
//							/** choose the correct ObjectMapManager */
//							if (localX % 4 == 0) {
//								objectMapManager = GameWindow.gw.getObjectMapManagers().get("tree1");
//								overlayMapMaznager = GameWindow.gw.getObjectMapManagers().get("overlayTree1");
//							}
//							if (localX % 4 == 2) {
//								objectMapManager = GameWindow.gw.getObjectMapManagers().get("tree2");
//								overlayMapManager = GameWindow.gw.getObjectMapManagers().get("overlayTree2");
//							}
//							
//							/** create Map if not yet created */
//							if (!objectMapManager.contains(p)) {
//								objectMapManager.createMap(p);
//								overlayMapManager.createMap(p);
//							}
//							
//							/** check if deleteModus or paintModus */
//							if (!GamePanel.gp.isDeleteModus()) {
//								if (LocalMap.getPaintType(objectMapManager.get(p).getTile(localX, localY)) == 0) {
//									TileDimensions tileDimensions = LocalMap.getTileDimensions(GamePanel.gp.getPaintType());
////									System.out.println("Painting with "+tileDimensions);
//									objectMapManager.get(p).setUl(localX, localY, GamePanel.gp.getPaintType());
//									if (tileDimensions == TileDimensions.RowCol2x2) objectMapManager.get(p).setUl(localX, localY, 0);
//									objectMapManager.get(p).setUr(localX, localY, GamePanel.gp.getPaintType());
//									objectMapManager.get(p).setDl(localX, localY, 0);
//									objectMapManager.get(p).setDr(localX, localY, 0);
//									objectMapManager.get(p).setIdByCornersObject(localX, localY, GamePanel.gp.getPaintType(), tileDimensions);
//									objectMapManager.adjustSurrounding(objectMapManager.get(p), localX, localY, GamePanel.gp.getPaintType(), tileDimensions);
//									
//									overlayMapManager.get(p).setUl(localX, localY, GamePanel.gp.getPaintType()-GameStates.mapTileSetWidth*2);
//									if (tileDimensions == TileDimensions.RowCol2x2) overlayMapManager.get(p).setUl(localX, localY, 0);
//									overlayMapManager.get(p).setUr(localX, localY, GamePanel.gp.getPaintType()-GameStates.mapTileSetWidth*2);
//									overlayMapManager.get(p).setDl(localX, localY, 0);
//									overlayMapManager.get(p).setDr(localX, localY, 0);
//									overlayMapManager.get(p).setIdByCornersObject(localX, localY, GamePanel.gp.getPaintType()-GameStates.mapTileSetWidth*2, tileDimensions);
//									overlayMapManager.adjustSurrounding(overlayMapManager.get(p), localX, localY, GamePanel.gp.getPaintType()-GameStates.mapTileSetWidth*2, tileDimensions);
//								}
//							} else {
//								/** only allow deletions for the actual PaintType */
//								if (LocalMap.getPaintType(objectMapManager.get(p).getTile(localX, localY)) == GamePanel.gp.getPaintType()) {
//								//MapManager.get(p).setTile(localX, localY, null);
//								if (objectMapManager.get(p).getLocalMap()[localX][localY] != 0) {
//////									System.out.println("mouse: localX/Y = "+localX+"/"+localY);
////									// create drop 
////									BigInteger itemId = ItemManager.getMaxIDValue().add(GamePanel.gp.getPlayerEntity().getId());
////									Item woodItem = Item.getItem(ITEMCODE.WOOD, itemId, "wood", (int) (Math.random()*20+20), 0, p.x+localX*GameStates.mapTileSetWidth+(int) (Math.random()*(GameStates.mapTileSetWidth-GameStates.itemTileWidth)), p.y+localY*GameStates.mapTileSetHeight +(int) (Math.random()*(GameStates.mapTileSetHeight-GameStates.itemTileHeight)), System.currentTimeMillis(), new float[0]);
////									
//////									System.out.println("added new wood object: "+itemId);
////									
////									/** send the complete Item to all players of the channel */
////									if (GameWindow.gw.isLoggedIn() && GamePanel.gp.isInitializedPlayer()) {
////										/** first send to server for the itemList */
////										GameWindow.gw.send(ClientMessages.addItem(woodItem.getId(), woodItem.getItemCode(), woodItem.getCount(), woodItem.getCapacity(), woodItem.getEntity().getX(), woodItem.getEntity().getY(), woodItem.getEntity().getMX(), woodItem.getEntity().getMY(), woodItem.getName(), woodItem.getEntity().getTileSet().getFileName(), woodItem.getEntity().getName(), woodItem.getAttributes()));
////										for (String channelName : GameWindow.gw.getSpaceChannels().values()) {
////											ClientChannel channel = GameWindow.gw.getChannelByName(channelName);
////											try {
////												channel.send(ClientMessages.addCompleteItem(woodItem.getItemCode(), woodItem.getId(), woodItem.getName(), woodItem.getEntity().getX(), woodItem.getEntity().getY(), woodItem.getCount(), woodItem.getAttributes()));
////											} catch (IOException e) {
////												e.printStackTrace();
////											}	
////										}
////									}
//								}
//								TileDimensions tileDimensions = LocalMap.getTileDimensions(objectMapManager.get(p).getTile(localX, localY));
////								System.out.println("Deleting "+tileDimensions);
//								
//								objectMapManager.get(p).setUl(localX, localY, 0);
//								objectMapManager.get(p).setUr(localX, localY, 0);
//								objectMapManager.get(p).setDl(localX, localY, 0);
//								objectMapManager.get(p).setDr(localX, localY, 0);
//								objectMapManager.get(p).setIdByCornersObject(localX, localY, GamePanel.gp.getPaintType(), tileDimensions);
//								objectMapManager.deleteSurrounding(objectMapManager.get(p), localX, localY, GamePanel.gp.getPaintType(), tileDimensions);
//								
//								overlayMapManager.get(p).setUl(localX, localY, 0);
//								overlayMapManager.get(p).setUr(localX, localY, 0);
//								overlayMapManager.get(p).setDl(localX, localY, 0);
//								overlayMapManager.get(p).setDr(localX, localY, 0);
//								overlayMapManager.get(p).setIdByCornersObject(localX, localY, GamePanel.gp.getPaintType()-GameStates.mapTileSetWidth*2, tileDimensions);
//								overlayMapManager.deleteSurrounding(overlayMapManager.get(p), localX, localY, GamePanel.gp.getPaintType()-GameStates.mapTileSetWidth*2, tileDimensions);
//								//MapManager.adjustSurrounding(MapManager.get(p), localX, localY);
//								}
//							}
//							/** register lattice Point for save - and send procedure */
//							objectMapManager.addChangedList(p);
//							overlayMapManager.addChangedList(p);
//							/** now check the corners */
//							if ((localX == 0) && (localY == 0)) {
//								// ul
//								Point p2 = new Point(p.x-localWidth, p.y-localHeight);
//								objectMapManager.addChangedList(p2);
//								overlayMapManager.addChangedList(p2);
//							} 
//							if ( (localX == GameStates.mapWidth-1) && (localY == 0)) {
//								// ur
//								Point p2 = new Point(p.x+localWidth, p.y-localHeight);
//								objectMapManager.addChangedList(p2);
//								overlayMapManager.addChangedList(p2);
//							}
//							if (localY == 0) {
//								// u
//								Point p2 = new Point(p.x, p.y-localHeight);
//								objectMapManager.addChangedList(p2);
//								overlayMapManager.addChangedList(p2);
//							} 
//							if ( (localX == 0) && (localY == GameStates.mapHeight-1)) {
//								// dl
//								// no need to create because trees dont have a dl tile
//							}
//							if ( (localX == 0)) {
//								// l
//								Point p2 = new Point(p.x-localWidth, p.y);
//								objectMapManager.addChangedList(p2);
//								overlayMapManager.addChangedList(p2);
//		
//							}
//							if ( (localX == GameStates.mapWidth-1) && (localY == GameStates.mapHeight-1) ) {
//								// dr
//								// no need to create because trees dont have a dr tile
//							}
//							if ( (localX == GameStates.mapWidth-1) ) {
//								// r
//								Point p2 = new Point(p.x+localWidth, p.y);
//								objectMapManager.addChangedList(p2);
//								overlayMapManager.addChangedList(p2);
//		
//							}
//							if (localY == GameStates.mapHeight-1) {
//								// d
//								// no need to create because trees dont have a d tile
//							}
//		//				}
//					} else {
//						MapManager mapManager = GameWindow.gw.getMapManagers().get(GamePanel.gp.getPaintLayer());
//				//		System.out.println("writing on "+GamePanel.gp.getPaintLayer());
//						/** create Map if not yet created */
//						if (!mapManager.contains(p)) {
//							mapManager.createMap(p);
//						}
//						
//						/** check if deleteModus or paintModus */
//						if (!GamePanel.gp.isDeleteModus()) {
//							//Tile tile = new Tile(62, true, true, true, true);
//				//			MapManager.get(p).setTile(localX, localY, 62);
//							mapManager.get(p).setUl(localX, localY, GamePanel.gp.getPaintType());
//							mapManager.get(p).setUr(localX, localY, GamePanel.gp.getPaintType());
//							mapManager.get(p).setDl(localX, localY, GamePanel.gp.getPaintType());
//							mapManager.get(p).setDr(localX, localY, GamePanel.gp.getPaintType());
//							mapManager.get(p).setIdByCorners(localX, localY, GamePanel.gp.getPaintType());
//							mapManager.adjustSurrounding(mapManager.get(p), localX, localY, GamePanel.gp.getPaintType());
//						} else {
//							//MapManager.get(p).setTile(localX, localY, null);
//							mapManager.get(p).setUl(localX, localY, 0);
//							mapManager.get(p).setUr(localX, localY, 0);
//							mapManager.get(p).setDl(localX, localY, 0);
//							mapManager.get(p).setDr(localX, localY, 0);
//							mapManager.get(p).setIdByCorners(localX, localY, GamePanel.gp.getPaintType());
//							mapManager.deleteSurrounding(mapManager.get(p), localX, localY, GamePanel.gp.getPaintType());
//							//MapManager.adjustSurrounding(MapManager.get(p), localX, localY);
//						}
//						/** register lattice Point for save - and send procedure */
//						mapManager.addChangedList(p);
//						/** add corners to saveList and send-procedure if needed */
//						if ((localX == 0) && (localY == 0)) {
//							// ul
//							Point p2 = new Point(p.x-localWidth, p.y-localHeight);
//							mapManager.addChangedList(p2);
//						} 
//						if ( (localX == GameStates.mapWidth-1) && (localY == 0)) {
//							// ur
//							Point p2 = new Point(p.x+localWidth, p.y-localHeight);
//							mapManager.addChangedList(p2);
//						}
//						if (localY == 0) {
//							// u
//							Point p2 = new Point(p.x, p.y-localHeight);
//							mapManager.addChangedList(p2);
//						} 
//						if ( (localX == 0) && (localY == GameStates.mapHeight-1)) {
//							// dl
//							Point p2 = new Point(p.x-localWidth, p.y+localHeight);
//							mapManager.addChangedList(p2);
//						}
//						if ( (localX == 0)) {
//							// l
//							Point p2 = new Point(p.x-localWidth, p.y);
//							mapManager.addChangedList(p2);
//						}
//						if ( (localX == GameStates.mapWidth-1) && (localY == GameStates.mapHeight-1) ) {
//							// dr
//							Point p2 = new Point(p.x+localWidth, p.y+localHeight);
//							mapManager.addChangedList(p2);
//						}
//						if ( (localX == GameStates.mapWidth-1) ) {
//							// r
//							Point p2 = new Point(p.x+localWidth, p.y);
//							mapManager.addChangedList(p2);
//						}
//						if (localY == GameStates.mapHeight-1) {
//							// d
//							Point p2 = new Point(p.x, p.y+localHeight);
//							mapManager.addChangedList(p2);
//						}
//					}
//				} else {
//					GameWindow.gw.gameInfoConsole.appendInfo("outside of paint area");
//				}
//			}
//		} else {
//			/** paintLayer is "empty" -> do nothing */
//		}
//	}

}
