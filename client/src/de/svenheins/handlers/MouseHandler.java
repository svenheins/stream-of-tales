package de.svenheins.handlers;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;

import javax.swing.SwingUtilities;

import com.sun.sgs.client.ClientChannel;


import de.svenheins.objects.Entity;
import de.svenheins.objects.InteractionArea;
import de.svenheins.objects.InteractionTile;
import de.svenheins.objects.PlayerEntity;
import de.svenheins.objects.Space;
import de.svenheins.objects.Tile;
import de.svenheins.objects.TileSet;
import de.svenheins.objects.WorldPosition;
import de.svenheins.objects.agents.goals.Goal;
import de.svenheins.objects.items.Item;
//import de.svenheins.objects.items.materials.Wood;

import de.svenheins.animation.SpaceDisappear;
import de.svenheins.functions.MyMath;


import de.svenheins.main.ConnectionWindow;
import de.svenheins.main.EditWindow;
import de.svenheins.main.EntityStates;
import de.svenheins.main.GameModus;
import de.svenheins.main.GamePanel;
import de.svenheins.main.GameStates;
import de.svenheins.main.GameWindow;
import de.svenheins.main.StatPanel;
import de.svenheins.main.TileDimensions;
import de.svenheins.main.TileType;
import de.svenheins.main.gui.ContainerGUI;
import de.svenheins.main.gui.ContainerGUIManager;
import de.svenheins.main.gui.EditorGUIManager;
import de.svenheins.main.gui.PlayerListGUIManager;
import de.svenheins.managers.AgentManager;
import de.svenheins.managers.EntityManager;
import de.svenheins.managers.InteractionManager;
import de.svenheins.managers.ItemManager;
import de.svenheins.managers.MapManager;
import de.svenheins.managers.ObjectMapManager;
import de.svenheins.managers.PlayerManager;
import de.svenheins.managers.SpaceManager;
import de.svenheins.managers.UndergroundMapManager;
import de.svenheins.messages.ClientMessages;
import de.svenheins.messages.ITEMCODE;
import de.svenheins.messages.OBJECTCODE;

public class MouseHandler implements MouseListener, MouseMotionListener{

	private boolean dragging = false;
	protected Space dragSpace=null; 
//	protected int localX, localY;
	
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
			if (GameModus.modus == GameModus.GAME && GameWindow.gw.getShowConsole() == false){
				gameMouseLeftPressed(point);
			}
		}
		if (SwingUtilities.isRightMouseButton(mouseEvent)) {
			
			if (GameModus.modus == GameModus.GAME && GameWindow.gw.getShowConsole() == false){
				gameMouseRightPressed(point);
			}
		}
		
		
	}

	@Override
	public void mouseReleased(MouseEvent mouseEvent) {
		Point point = mouseEvent.getPoint();
		if (SwingUtilities.isLeftMouseButton(mouseEvent)) {
			if (GameModus.modus == GameModus.GAME && GameWindow.gw.getShowConsole() == false){
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
		
		if (GameModus.modus == GameModus.GAME && GameWindow.gw.getShowConsole() == false){
			gameMouseDragged(point);
		}
	}

	@Override
	public void mouseMoved(MouseEvent mouseEvent) {
		Point point = mouseEvent.getPoint();
		if (GameModus.modus == GameModus.GAME && GameWindow.gw.getShowConsole() == false){
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
	public void gameMouseLeftClicked(Point point) {
		Point p_save = new Point(point);
		Point correctedPoint = new Point( (int) (point.x/GamePanel.gp.getZoomFactor()) +GamePanel.gp.getViewPointX(), (int) (point.y/GamePanel.gp.getZoomFactor())+GamePanel.gp.getViewPointY());
		point = correctedPoint;
		/** update contextGUI Menu */
		if (StatPanel.sp.contextMenu.isVisible()) {
			StatPanel.sp.contextMenu.mouseClick(p_save);
		}
		
		
	}
	
	/**
	 * @ModusImplementation: GAME
	 * @Method: mouseClicked
	 * @param point
	 */
	public void gameMouseRightClicked(Point point) {
		Point p_save = new Point(point);
//		System.out.println("ViewPoint: x = "+GamePanel.gp.getViewPointX()+ " y = "+ GamePanel.gp.getViewPointY());
//		EnemyManager.sortZIndex();
//		point.x = point.x - GamePanel.gp.getViewPointX();
//		point.y = point.y - GamePanel.gp.getViewPointY();
		Point correctedPoint = new Point( (int) (point.x/GamePanel.gp.getZoomFactor()) +GamePanel.gp.getViewPointX(), (int) (point.y/GamePanel.gp.getZoomFactor())+GamePanel.gp.getViewPointY());
		point = correctedPoint;
//		for( int i =0; i < EnemyManager.sizeSorted(); i++){
//			
//				if (EnemyManager.getSorted(i).contains(point)) {
//					// open edit-windows
////					GamePanel.gp.setPause(true);
//					new EditWindow(EnemyManager.getSorted(i));
//				}
//			
//		}
		
//		for( BigInteger i : SpaceManager.idList) {
//			for (int j = 0; j< SpaceManager.get(i).getPolygon().size(); j++) {
//				if (SpaceManager.get(i).getPolygon().get(j).contains(point)) {
//					// open edit-windows
//					new EditWindow(SpaceManager.get(i));
//				}
//			}
//		}
		
//		for( int i =0; i < PlayerManager.size(); i++){
//			if (PlayerManager.get(i).contains(point)) {
//				// open edit-windows
////				GamePanel.gp.setPause(true);
//				new EditWindow(PlayerManager.get(i));
//			}
//		}
		
//		for( int i =0; i < EntityManager.size(); i++){
		for(BigInteger i : EntityManager.idList){
			if (EntityManager.get(i).contains(point)) {
				// open edit-windows
				//GamePanel.gp.setPause(true);
//				new EditWindow(EntityManager.get(i));
				StatPanel.sp.contextMenu.create(EntityManager.get(i), p_save.x-GameStates.contextMenuFrameDistX, p_save.y-GameStates.contextMenuFrameDistY);
			}
		}
		
		for (BigInteger i : AgentManager.idList) {
			if (AgentManager.get(i).contains(point)) {
//				StatPanel.sp.contextMenu.create(EntityManager.get(i), p_save.x-GameStates.contextMenuFrameDistX, p_save.y-GameStates.contextMenuFrameDistY);
				Goal goal = new Goal(new WorldPosition(GamePanel.gp.getPlayerEntity().getX(), GamePanel.gp.getPlayerEntity().getY()));//, GamePanel.gp.getPlayerEntity().getId(), GamePanel.gp.getPlayerEntity());
				AgentManager.get(i).setActualGoal(goal);
				System.out.println("added new goal!");
//				AgentManager.get(i).getActualGoal().setEntity(GamePanel.gp.getPlayerEntity());
			}
		}
		
		for(BigInteger i : PlayerManager.idList){
			if (PlayerManager.get(i).contains(point)) {
				// open edit-windows
				//GamePanel.gp.setPause(true);
//				new EditWindow(EntityManager.get(i));
				StatPanel.sp.contextMenu.create(PlayerManager.get(i), p_save.x-GameStates.contextMenuFrameDistX, p_save.y-GameStates.contextMenuFrameDistY);
			}
		}
		
		if (GamePanel.gp.getPlayerEntity().contains(point)) {
			StatPanel.sp.contextMenu.create(GamePanel.gp.getPlayerEntity(), p_save.x-GameStates.contextMenuFrameDistX, p_save.y-GameStates.contextMenuFrameDistY);
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
		boolean clickedOnGUI = false;
		if (StatPanel.sp.contextMenu.isVisible()) {
			if(StatPanel.sp.contextMenu.mouseClick(point) == true) clickedOnGUI = true;
		}
		
		for ( String editorName  : EditorGUIManager.idList) {
			if (editorName.equals("floor")) {
//				System.out.println(point.x+" "+ point.y);
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
		
		Point correctedPoint = new Point( (int) (point.x/GamePanel.gp.getZoomFactor()) +GamePanel.gp.getViewPointX(), (int) (point.y/GamePanel.gp.getZoomFactor())+GamePanel.gp.getViewPointY());
		for(BigInteger i : EntityManager.idList){
			if (EntityManager.get(i).contains(correctedPoint)) {
				clickedOnGUI = true;
			}
		}
		if (GamePanel.gp.getPlayerEntity().contains(correctedPoint)) {
//			StatPanel.sp.contextMenu.create(GamePanel.gp.playerEntity, p_save.x, p_save.y);
//			clickedOnGUI = true;
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
		
		/** only paint tiles if no Editor icon was hit*/
		if (!clickedOnGUI) {
			/** only paint if we are the GameMaster (else the changes wouldn't effect the map anyways) */
			if (GameWindow.gw.isGameMaster()) {
				GamePanel.gp.setDeleteModus(false);
				paintTiles(point);
			} else {
				/** normal player might interact with objects */
				int interactionWidth = 32;
				int interactionHeight = 32;
				/** define center of Player */
				float playerCenterX = GamePanel.gp.getPlayerEntity().getX() + (GamePanel.gp.getPlayerEntity().getWidth()/2);
				float playerCenterY = GamePanel.gp.getPlayerEntity().getY() + ((float)GamePanel.gp.getPlayerEntity().getHeight());
				Point playerPos = new Point((int)playerCenterX,(int) playerCenterY);
				
//				System.out.println("Distance: "+MyMath.getDistance(playerPos, correctedPoint));
				/** if interaction is close enough */
				if(MyMath.getDistance(playerPos, correctedPoint) < GameStates.interactDistance) {
					float interactionPosX = (float) correctedPoint.getX();// - ((interactionWidth*2)/3);
					float interactionPosY = (float) correctedPoint.getY();// - ((interactionHeight*2)/3);
					
	//				WorldPosition position = new WorldPosition(GamePanel.gp.getPlayerEntity().getX()-32, GamePanel.gp.getPlayerEntity().getY());
					WorldPosition position = new WorldPosition(interactionPosX, interactionPosY);
					
					/** interact with actual interactionItem */
					ObjectMapManager objectMapManagerTree1 = GameWindow.gw.getObjectMapManagers().get("tree1");
					ObjectMapManager objectMapManagerTree2 = GameWindow.gw.getObjectMapManagers().get("tree2");
					
					int[] values = new int[TileType.values().length];
					values[TileType.TREE.ordinal()] = 30;
					values[TileType.STONE.ordinal()] = 15;
					InteractionArea iArea = new InteractionArea(position, values, interactionWidth, interactionHeight);
					ArrayList<InteractionTile> overflowTile1 = objectMapManagerTree1.interact(iArea);
					ArrayList<InteractionTile> overflowTile2 = objectMapManagerTree2.interact(iArea);
					if (overflowTile1 != null) {
	//					System.out.println("overflowTile1 != null");
						if (overflowTile1.size() > 0) {
	//						System.out.println("overflowTile1.size() = "+overflowTile1.size());
							for (InteractionTile iTile : overflowTile1) {
	//							System.out.println("reached limit at map "+iTile.getPosition().getMapCoordinates().getX()+"|"+iTile.getPosition().getMapCoordinates().getY()+" localX|Y="+iTile.getPosition().getLocalX()+"|"+iTile.getPosition().getLocalY());
								GameWindow.gw.send(ClientMessages.deleteMapObject(iTile, GameWindow.gw.getGameMasterName(), PlayerManager.get(GameWindow.gw.getGameMasterName()).getId(), "tree1", "overlayTree1"));
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
								GameWindow.gw.send(ClientMessages.deleteMapObject(iTile, GameWindow.gw.getGameMasterName(), PlayerManager.get(GameWindow.gw.getGameMasterName()).getId(), "tree2", "overlayTree2"));
								InteractionManager.remove(iTile.getPosition());
							}
						}
					}
				} else {
					/** interaction is too far away */
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
			if (AgentManager.get(i).contains(correctedPoint)) {
//				StatPanel.sp.contextMenu.create(EntityManager.get(i), p_save.x-GameStates.contextMenuFrameDistX, p_save.y-GameStates.contextMenuFrameDistY);
				Goal goal = new Goal(new WorldPosition(GamePanel.gp.getPlayerEntity().getX(), GamePanel.gp.getPlayerEntity().getY()));//, GamePanel.gp.getPlayerEntity().getId(), GamePanel.gp.getPlayerEntity());
				AgentManager.get(i).getGoalList().add(goal);
				AgentManager.get(i).setActualGoal(goal);
				System.out.println("added new goal!");
//				AgentManager.get(i).getActualGoal().setEntity(GamePanel.gp.getPlayerEntity());
			}
		}
		
		for(BigInteger i : PlayerManager.idList){
			if (PlayerManager.get(i).contains(correctedPoint)) {
				// open edit-windows
				//GamePanel.gp.setPause(true);
//				new EditWindow(EntityManager.get(i));
				StatPanel.sp.contextMenu.create(PlayerManager.get(i), point.x-GameStates.contextMenuFrameDistX, point.y-GameStates.contextMenuFrameDistY);
				clickedOnGUI = true;
			}
		}
		if (GamePanel.gp.getPlayerEntity().contains(correctedPoint)) {
			StatPanel.sp.contextMenu.create(GamePanel.gp.getPlayerEntity(), point.x-GameStates.contextMenuFrameDistX, point.y-GameStates.contextMenuFrameDistY);
			clickedOnGUI = true;
		}
		for (ContainerGUI congui : ContainerGUIManager.containerGUIList.values()) {
			if (congui.getBackgroundSpace().contains(point) && congui.isVisible()) {
//				System.out.println("clicked on inventory");
//				congui.mouseClick(point);
				clickedOnGUI = true;
			}
		}
		
		/** only paint tiles if no Editor icon was hit*/
		if (!clickedOnGUI) {
			GamePanel.gp.setDeleteModus(true);
			paintTiles(point);
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
		
		determineAnimation(GamePanel.gp.getPlayerEntity(), correctedPoint);
		
		
		
	}
	
	/**
	 * @ModusImplementation: GAME
	 * @Method: mouseDragged
	 * @param point
	 */
	public void gameMouseDragged(Point point) {
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
					paintTiles(point);
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
	
	
	/** painting on point with the actual paintLayer on the corresponding paintType */
	public void paintTiles(Point point) {
		/** only paint if the paintLayer is not "empty" */
		if (!GamePanel.gp.getPaintLayer().equals("empty")) {
			Point correctedPoint = new Point( (int) (point.x/GamePanel.gp.getZoomFactor()) +GamePanel.gp.getViewPointX(), (int) (point.y/GamePanel.gp.getZoomFactor())+GamePanel.gp.getViewPointY());
			point = correctedPoint;
			
			if (GamePanel.gp.getPaintLayer().equals("underground")) {
				int localWidthUnderground = GameStates.ugrMapWidth * GameStates.ugrMapTileSetWidth;
				int localHeightUnderground = GameStates.ugrMapHeight * GameStates.ugrMapTileSetHeight;
				int latticePointXUnderground = (int) Math.floor( (float) correctedPoint.x / (localWidthUnderground)) * localWidthUnderground;
				int latticePointYUnderground = (int) Math.floor( (float) correctedPoint.y / (localHeightUnderground)) * localHeightUnderground;
				int localXUnderground = (int) Math.floor( (float) (correctedPoint.x - latticePointXUnderground )/ GameStates.ugrMapTileSetWidth);
				int localYUnderground = (int) Math.floor( (float) (correctedPoint.y - latticePointYUnderground )/ GameStates.ugrMapTileSetHeight);
				Point pUnderground = new Point(latticePointXUnderground, latticePointYUnderground);
				
				int minX = ((int) Math.floor( (float) GamePanel.gp.getPlayerEntity().getX() / (localWidthUnderground)) * localWidthUnderground) - GameStates.factorOfViewDeleteDistance*localWidthUnderground + GameStates.ugrMapTileSetWidth;
				int maxX = ((int) Math.floor( (float) GamePanel.gp.getPlayerEntity().getX() / (localWidthUnderground)) * localWidthUnderground) + GameStates.factorOfViewDeleteDistance*localWidthUnderground - GameStates.ugrMapTileSetWidth;
				int minY = (int) Math.floor( (float) GamePanel.gp.getPlayerEntity().getY() / (localHeightUnderground)) * localHeightUnderground - GameStates.factorOfViewDeleteDistance*localHeightUnderground + GameStates.ugrMapTileSetHeight;
				int maxY = (int) Math.floor( (float) GamePanel.gp.getPlayerEntity().getY() / (localHeightUnderground)) * localHeightUnderground + GameStates.factorOfViewDeleteDistance*localHeightUnderground - GameStates.ugrMapTileSetHeight;
				Rectangle rect = new Rectangle(minX, minY, maxX-minX, maxY-minY);
				
				if ( rect.contains(correctedPoint) ) {	
					UndergroundMapManager mapManager = GameWindow.gw.getUndergroundMapManagers().get(GamePanel.gp.getPaintLayer());
					/** create Map if not yet created */
					if (!mapManager.contains(pUnderground)) {
						mapManager.createMap(pUnderground);
					}
					
					/** check if deleteModus or paintModus */
					if (!GamePanel.gp.isDeleteModus()) {
						mapManager.get(pUnderground).setUl(localXUnderground, localYUnderground, GamePanel.gp.getPaintType());
						mapManager.get(pUnderground).setUr(localXUnderground, localYUnderground, GamePanel.gp.getPaintType());
						mapManager.get(pUnderground).setDl(localXUnderground, localYUnderground, GamePanel.gp.getPaintType());
						mapManager.get(pUnderground).setDr(localXUnderground, localYUnderground, GamePanel.gp.getPaintType());
						mapManager.get(pUnderground).setIdByCorners(localXUnderground, localYUnderground, GamePanel.gp.getPaintType());
						mapManager.adjustSurrounding(mapManager.get(pUnderground), localXUnderground, localYUnderground, GamePanel.gp.getPaintType());
					} else {
						//MapManager.get(pUnderground).setTile(localXUnderground, localYUnderground, null);
						mapManager.get(pUnderground).setUl(localXUnderground, localYUnderground, 0);
						mapManager.get(pUnderground).setUr(localXUnderground, localYUnderground, 0);
						mapManager.get(pUnderground).setDl(localXUnderground, localYUnderground, 0);
						mapManager.get(pUnderground).setDr(localXUnderground, localYUnderground, 0);
						mapManager.get(pUnderground).setIdByCorners(localXUnderground, localYUnderground, GamePanel.gp.getPaintType());
						mapManager.deleteSurrounding(mapManager.get(pUnderground), localXUnderground, localYUnderground, GamePanel.gp.getPaintType());
						//MapManager.adjustSurrounding(MapManager.get(pUnderground), localXUnderground, localYUnderground);
					}
					/** register lattice Point for save - and send procedure */
					mapManager.addChangedList(pUnderground);
//					System.out.println(mapManager.changedList.size());
					/** add corners to saveList and send-procedure if needed */
					if ((localXUnderground == 0) && (localYUnderground == 0)) {
						// ul
						Point p2 = new Point(pUnderground.x-localWidthUnderground, pUnderground.y-localHeightUnderground);
						mapManager.addChangedList(p2);
					} 
					if ( (localXUnderground == GameStates.ugrMapWidth-1) && (localYUnderground == 0)) {
						// ur
						Point p2 = new Point(pUnderground.x+localWidthUnderground, pUnderground.y-localHeightUnderground);
						mapManager.addChangedList(p2);
					}
					if (localYUnderground == 0) {
						// u
						Point p2 = new Point(pUnderground.x, pUnderground.y-localHeightUnderground);
						mapManager.addChangedList(p2);
					} 
					if ( (localXUnderground == 0) && (localYUnderground == GameStates.ugrMapHeight-1)) {
						// dl
						Point p2 = new Point(pUnderground.x-localWidthUnderground, pUnderground.y+localHeightUnderground);
						mapManager.addChangedList(p2);
					}
					if ( (localXUnderground == 0)) {
						// l
						Point p2 = new Point(pUnderground.x-localWidthUnderground, pUnderground.y);
						mapManager.addChangedList(p2);
					}
					if ( (localXUnderground == GameStates.ugrMapWidth-1) && (localYUnderground == GameStates.ugrMapHeight-1) ) {
						// dr
						Point p2 = new Point(pUnderground.x+localWidthUnderground, pUnderground.y+localHeightUnderground);
						mapManager.addChangedList(p2);
					}
					if ( (localXUnderground == GameStates.ugrMapWidth-1) ) {
						// r
						Point p2 = new Point(pUnderground.x+localWidthUnderground, pUnderground.y);
						mapManager.addChangedList(p2);
					}
					if (localYUnderground == GameStates.ugrMapHeight-1) {
						// d
						Point p2 = new Point(pUnderground.x, pUnderground.y+localHeightUnderground);
						mapManager.addChangedList(p2);
					}
				}
			} else {
				/** here start the small tiles (32x32 normally) */
				int localWidth = GameStates.mapWidth * GameStates.mapTileSetWidth;
				int localHeight = GameStates.mapHeight * GameStates.mapTileSetHeight;
				int latticePointX = (int) Math.floor( (float) correctedPoint.x / (localWidth)) * localWidth;
				int latticePointY = (int) Math.floor( (float) correctedPoint.y / (localHeight)) * localHeight;
				int localX = (int) Math.floor( (float) (correctedPoint.x - latticePointX )/ GameStates.mapTileSetWidth);
				int localY = (int) Math.floor( (float) (correctedPoint.y - latticePointY )/ GameStates.mapTileSetHeight);
				
				Point p = new Point(latticePointX, latticePointY);
				
				int minX = ((int) Math.floor( (float) GamePanel.gp.getPlayerEntity().getX() / (localWidth)) * localWidth) - GameStates.factorOfViewDeleteDistance*localWidth + GameStates.mapTileSetWidth;
				int maxX = ((int) Math.floor( (float) GamePanel.gp.getPlayerEntity().getX() / (localWidth)) * localWidth) + GameStates.factorOfViewDeleteDistance*localWidth - GameStates.mapTileSetWidth;
				int minY = (int) Math.floor( (float) GamePanel.gp.getPlayerEntity().getY() / (localHeight)) * localHeight - GameStates.factorOfViewDeleteDistance*localHeight + GameStates.mapTileSetHeight;
				int maxY = (int) Math.floor( (float) GamePanel.gp.getPlayerEntity().getY() / (localHeight)) * localHeight + GameStates.factorOfViewDeleteDistance*localHeight - GameStates.mapTileSetHeight;
				Rectangle rect = new Rectangle(minX, minY, maxX-minX, maxY-minY);
				
				if ( rect.contains(correctedPoint) ) {				
					if (GamePanel.gp.getPaintLayer().equals("tree")) {
						/** allways paint trees */
		//				if ( (localX % 4 != 0) && (localX % 4 != 2)) localX++;
						if ( localX % 2 != 0 ) {
							if (localX < GameStates.mapWidth-1)
								localX++;
							else localX--;
						}
						if (localY % 2 != 0 ) {
							if (localY < GameStates.mapHeight-1)
								localY++;
							else localY--;
						}
		//				if ((localY % 2 == 0) && ( (localX % 4 == 0) || (localX % 4 == 2))) {
							ObjectMapManager objectMapManager = null;
							ObjectMapManager overlayMapManager = null;
							
							if (localX % 4 == 0) {
								objectMapManager = GameWindow.gw.getObjectMapManagers().get("tree1");
								overlayMapManager = GameWindow.gw.getObjectMapManagers().get("overlayTree1");
							}
							if (localX % 4 == 2) {
								objectMapManager = GameWindow.gw.getObjectMapManagers().get("tree2");
								overlayMapManager = GameWindow.gw.getObjectMapManagers().get("overlayTree2");
							}
							
							/** create Map if not yet created */
							if (!objectMapManager.contains(p)) {
								objectMapManager.createMap(p);
								overlayMapManager.createMap(p);
							}
							
							/** check if deleteModus or paintModus */
							if (!GamePanel.gp.isDeleteModus()) {
								objectMapManager.get(p).setUl(localX, localY, GamePanel.gp.getPaintType());
								objectMapManager.get(p).setUr(localX, localY, GamePanel.gp.getPaintType());
								objectMapManager.get(p).setDl(localX, localY, 0);
								objectMapManager.get(p).setDr(localX, localY, 0);
								objectMapManager.get(p).setIdByCornersObject(localX, localY, GamePanel.gp.getPaintType());
								objectMapManager.adjustSurrounding(objectMapManager.get(p), localX, localY, GamePanel.gp.getPaintType(), TileDimensions.RowCol2x3);
								
								overlayMapManager.get(p).setUl(localX, localY, GamePanel.gp.getPaintType()-GameStates.mapTileSetWidth*2);
								overlayMapManager.get(p).setUr(localX, localY, GamePanel.gp.getPaintType()-GameStates.mapTileSetWidth*2);
								overlayMapManager.get(p).setDl(localX, localY, 0);
								overlayMapManager.get(p).setDr(localX, localY, 0);
								overlayMapManager.get(p).setIdByCornersObject(localX, localY, GamePanel.gp.getPaintType()-GameStates.mapTileSetWidth*2);
								overlayMapManager.adjustSurrounding(overlayMapManager.get(p), localX, localY, GamePanel.gp.getPaintType()-GameStates.mapTileSetWidth*2, TileDimensions.RowCol2x3);
							} else {
								//MapManager.get(p).setTile(localX, localY, null);
								if (objectMapManager.get(p).getLocalMap()[localX][localY] != 0) {
									System.out.println("mouse: localX/Y = "+localX+"/"+localY);
									// create drop 
									BigInteger itemId = ItemManager.getMaxIDValue().add(GamePanel.gp.getPlayerEntity().getId());
									Item woodItem = Item.getItem(ITEMCODE.WOOD, itemId, "wood", (int) (Math.random()*20+20), 0, p.x+localX*GameStates.mapTileSetWidth+(int) (Math.random()*(GameStates.mapTileSetWidth-GameStates.itemTileWidth)), p.y+localY*GameStates.mapTileSetHeight +(int) (Math.random()*(GameStates.mapTileSetHeight-GameStates.itemTileHeight)), System.currentTimeMillis(), new float[0]);
									
									System.out.println("added new wood object: "+itemId);
									
									/** send the complete Item to all players of the channel */
									if (GameWindow.gw.isLoggedIn() && GamePanel.gp.isInitializedPlayer()) {
										/** first send to server for the itemList */
										GameWindow.gw.send(ClientMessages.addItem(woodItem.getId(), woodItem.getItemCode(), woodItem.getCount(), woodItem.getCapacity(), woodItem.getEntity().getX(), woodItem.getEntity().getY(), woodItem.getEntity().getMX(), woodItem.getEntity().getMY(), woodItem.getName(), woodItem.getEntity().getTileSet().getFileName(), woodItem.getEntity().getName(), woodItem.getStates()));
										for (String channelName : GameWindow.gw.getSpaceChannels().values()) {
											ClientChannel channel = GameWindow.gw.getChannelByName(channelName);
											try {
												channel.send(ClientMessages.addCompleteItem(woodItem.getItemCode(), woodItem.getId(), woodItem.getName(), woodItem.getEntity().getX(), woodItem.getEntity().getY(), woodItem.getCount(), woodItem.getStates()));
											} catch (IOException e) {
												e.printStackTrace();
											}	
										}
									}
								}
								objectMapManager.get(p).setUl(localX, localY, 0);
								objectMapManager.get(p).setUr(localX, localY, 0);
								objectMapManager.get(p).setDl(localX, localY, 0);
								objectMapManager.get(p).setDr(localX, localY, 0);
								objectMapManager.get(p).setIdByCornersObject(localX, localY, GamePanel.gp.getPaintType());
								objectMapManager.deleteSurrounding(objectMapManager.get(p), localX, localY, GamePanel.gp.getPaintType(), TileDimensions.RowCol2x3);
								
								overlayMapManager.get(p).setUl(localX, localY, 0);
								overlayMapManager.get(p).setUr(localX, localY, 0);
								overlayMapManager.get(p).setDl(localX, localY, 0);
								overlayMapManager.get(p).setDr(localX, localY, 0);
								overlayMapManager.get(p).setIdByCornersObject(localX, localY, GamePanel.gp.getPaintType()-GameStates.mapTileSetWidth*2);
								overlayMapManager.deleteSurrounding(overlayMapManager.get(p), localX, localY, GamePanel.gp.getPaintType()-GameStates.mapTileSetWidth*2, TileDimensions.RowCol2x3);
								//MapManager.adjustSurrounding(MapManager.get(p), localX, localY);
							}
							/** register lattice Point for save - and send procedure */
							objectMapManager.addChangedList(p);
							overlayMapManager.addChangedList(p);
							/** now check the corners */
							if ((localX == 0) && (localY == 0)) {
								// ul
								Point p2 = new Point(p.x-localWidth, p.y-localHeight);
								objectMapManager.addChangedList(p2);
								overlayMapManager.addChangedList(p2);
							} 
							if ( (localX == GameStates.mapWidth-1) && (localY == 0)) {
								// ur
								Point p2 = new Point(p.x+localWidth, p.y-localHeight);
								objectMapManager.addChangedList(p2);
								overlayMapManager.addChangedList(p2);
							}
							if (localY == 0) {
								// u
								Point p2 = new Point(p.x, p.y-localHeight);
								objectMapManager.addChangedList(p2);
								overlayMapManager.addChangedList(p2);
							} 
							if ( (localX == 0) && (localY == GameStates.mapHeight-1)) {
								// dl
								// no need to create because trees dont have a dl tile
							}
							if ( (localX == 0)) {
								// l
								Point p2 = new Point(p.x-localWidth, p.y);
								objectMapManager.addChangedList(p2);
								overlayMapManager.addChangedList(p2);
		
							}
							if ( (localX == GameStates.mapWidth-1) && (localY == GameStates.mapHeight-1) ) {
								// dr
								// no need to create because trees dont have a dr tile
							}
							if ( (localX == GameStates.mapWidth-1) ) {
								// r
								Point p2 = new Point(p.x+localWidth, p.y);
								objectMapManager.addChangedList(p2);
								overlayMapManager.addChangedList(p2);
		
							}
							if (localY == GameStates.mapHeight-1) {
								// d
								// no need to create because trees dont have a d tile
							}
		//				}
					} else {
						MapManager mapManager = GameWindow.gw.getMapManagers().get(GamePanel.gp.getPaintLayer());
				//		System.out.println("writing on "+GamePanel.gp.getPaintLayer());
						/** create Map if not yet created */
						if (!mapManager.contains(p)) {
							mapManager.createMap(p);
						}
						
						/** check if deleteModus or paintModus */
						if (!GamePanel.gp.isDeleteModus()) {
							//Tile tile = new Tile(62, true, true, true, true);
				//			MapManager.get(p).setTile(localX, localY, 62);
							mapManager.get(p).setUl(localX, localY, GamePanel.gp.getPaintType());
							mapManager.get(p).setUr(localX, localY, GamePanel.gp.getPaintType());
							mapManager.get(p).setDl(localX, localY, GamePanel.gp.getPaintType());
							mapManager.get(p).setDr(localX, localY, GamePanel.gp.getPaintType());
							mapManager.get(p).setIdByCorners(localX, localY, GamePanel.gp.getPaintType());
							mapManager.adjustSurrounding(mapManager.get(p), localX, localY, GamePanel.gp.getPaintType());
						} else {
							//MapManager.get(p).setTile(localX, localY, null);
							mapManager.get(p).setUl(localX, localY, 0);
							mapManager.get(p).setUr(localX, localY, 0);
							mapManager.get(p).setDl(localX, localY, 0);
							mapManager.get(p).setDr(localX, localY, 0);
							mapManager.get(p).setIdByCorners(localX, localY, GamePanel.gp.getPaintType());
							mapManager.deleteSurrounding(mapManager.get(p), localX, localY, GamePanel.gp.getPaintType());
							//MapManager.adjustSurrounding(MapManager.get(p), localX, localY);
						}
						/** register lattice Point for save - and send procedure */
						mapManager.addChangedList(p);
						/** add corners to saveList and send-procedure if needed */
						if ((localX == 0) && (localY == 0)) {
							// ul
							Point p2 = new Point(p.x-localWidth, p.y-localHeight);
							mapManager.addChangedList(p2);
						} 
						if ( (localX == GameStates.mapWidth-1) && (localY == 0)) {
							// ur
							Point p2 = new Point(p.x+localWidth, p.y-localHeight);
							mapManager.addChangedList(p2);
						}
						if (localY == 0) {
							// u
							Point p2 = new Point(p.x, p.y-localHeight);
							mapManager.addChangedList(p2);
						} 
						if ( (localX == 0) && (localY == GameStates.mapHeight-1)) {
							// dl
							Point p2 = new Point(p.x-localWidth, p.y+localHeight);
							mapManager.addChangedList(p2);
						}
						if ( (localX == 0)) {
							// l
							Point p2 = new Point(p.x-localWidth, p.y);
							mapManager.addChangedList(p2);
						}
						if ( (localX == GameStates.mapWidth-1) && (localY == GameStates.mapHeight-1) ) {
							// dr
							Point p2 = new Point(p.x+localWidth, p.y+localHeight);
							mapManager.addChangedList(p2);
						}
						if ( (localX == GameStates.mapWidth-1) ) {
							// r
							Point p2 = new Point(p.x+localWidth, p.y);
							mapManager.addChangedList(p2);
						}
						if (localY == GameStates.mapHeight-1) {
							// d
							Point p2 = new Point(p.x, p.y+localHeight);
							mapManager.addChangedList(p2);
						}
					}
				} else {
					GameWindow.gw.gameInfoConsole.appendInfo("outside of paint area");
				}
			}
		} else {
			/** paintLayer is "empty" -> do nothing */
		}
	}
	
	public void determineAnimation(PlayerEntity entity, Point p) {
		int distX = Math.abs((int) (entity.getX()+entity.getWidth()/2)-p.x);
		int distY = Math.abs((int) (entity.getY()+entity.getHeight()*3/4)-p.y);
		
		/** define orientation by point of the mouse */
		if (distX > distY) {
			if ((entity.getX()+entity.getWidth()/2) < p.x){
				if (entity.getOrientation() != EntityStates.RIGHT)  {
//					entity.startAnimation();
					entity.setOrientation(EntityStates.RIGHT);
					entity.setChangedStates(true);
//					System.out.println("dist: "+(System.currentTimeMillis() -entity.getAnimation().getInstantOfAnimation()));
					
				}
			} else {
				if (entity.getOrientation() != EntityStates.LEFT) {
//					entity.startAnimation();
					entity.setOrientation(EntityStates.LEFT);
					entity.setChangedStates(true);
					
				}
			}
		} else {
			if ((entity.getY()+entity.getHeight()*3/4) < p.y){
				if (entity.getOrientation() != EntityStates.DOWN) {
//					entity.startAnimation();
					entity.setOrientation(EntityStates.DOWN);
					entity.setChangedStates(true);
					
				}
			} else  {
				if (entity.getOrientation() != EntityStates.UP) {
//					entity.startAnimation();
					entity.setOrientation(EntityStates.UP);
					entity.setChangedStates(true);
					
				}
				
			}
		}
	}
}
