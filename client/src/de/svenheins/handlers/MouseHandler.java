package de.svenheins.handlers;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.IOException;
import java.math.BigInteger;

import javax.swing.SwingUtilities;

import com.sun.sgs.client.ClientChannel;


import de.svenheins.objects.Entity;
import de.svenheins.objects.PlayerEntity;
import de.svenheins.objects.Space;
import de.svenheins.objects.Tile;
import de.svenheins.objects.TileSet;
import de.svenheins.objects.items.WorldItem;
import de.svenheins.objects.items.materials.Wood;

import de.svenheins.animation.SpaceDisappear;


import de.svenheins.main.ConnectionWindow;
import de.svenheins.main.EditWindow;
import de.svenheins.main.EntityStates;
import de.svenheins.main.GameModus;
import de.svenheins.main.GamePanel;
import de.svenheins.main.GameStates;
import de.svenheins.main.GameWindow;
import de.svenheins.main.StatPanel;
import de.svenheins.main.gui.EditorGUIManager;
import de.svenheins.main.gui.PlayerListGUIManager;
import de.svenheins.managers.EntityManager;
import de.svenheins.managers.MapManager;
import de.svenheins.managers.ObjectMapManager;
import de.svenheins.managers.PlayerManager;
import de.svenheins.managers.SpaceManager;
import de.svenheins.managers.UndergroundMapManager;
import de.svenheins.managers.WorldItemManager;
import de.svenheins.messages.ClientMessages;
import de.svenheins.messages.ITEMCODE;
import de.svenheins.messages.OBJECTCODE;

public class MouseHandler implements MouseListener, MouseMotionListener{

	private boolean dragging = false;
	protected Space dragSpace=null; 
	protected int localX, localY;
	
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
				StatPanel.sp.contextMenu.create(EntityManager.get(i), p_save.x, p_save.y);
			}
		}
		
		for(BigInteger i : PlayerManager.idList){
			if (PlayerManager.get(i).contains(point)) {
				// open edit-windows
				//GamePanel.gp.setPause(true);
//				new EditWindow(EntityManager.get(i));
				StatPanel.sp.contextMenu.create(PlayerManager.get(i), p_save.x, p_save.y);
			}
		}
		
		if (GamePanel.gp.getPlayerEntity().contains(point)) {
			StatPanel.sp.contextMenu.create(GamePanel.gp.getPlayerEntity(), p_save.x, p_save.y);
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
			clickedOnGUI = true;
		}
		
		/** only paint tiles if no Editor icon was hit*/
		if (!clickedOnGUI) {
			GamePanel.gp.setDeleteModus(false);
			paintTiles(point);
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
					StatPanel.sp.contextMenu.create(playerEntity, point.x, point.y);
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
		for(BigInteger i : PlayerManager.idList){
			if (PlayerManager.get(i).contains(correctedPoint)) {
				// open edit-windows
				//GamePanel.gp.setPause(true);
//				new EditWindow(EntityManager.get(i));
				StatPanel.sp.contextMenu.create(PlayerManager.get(i), point.x, point.y);
				clickedOnGUI = true;
			}
		}
		if (GamePanel.gp.getPlayerEntity().contains(correctedPoint)) {
			StatPanel.sp.contextMenu.create(GamePanel.gp.getPlayerEntity(), point.x, point.y);
			clickedOnGUI = true;
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
//			 if (inside) {
//					SpaceManager.get(i).setTrans(1.0f);
//				} else {
//					SpaceManager.get(i).setTrans(0.5f);
//				}
		}
		
		
		/** update contextGUI Menu */
		if (StatPanel.sp.contextMenu.isVisible()) {
			StatPanel.sp.contextMenu.mouseOver(point);
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
			/** only paint tiles if no Editor icon was hit*/
			if (!draggedOnGUI) {
				paintTiles(point);
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
		/** only paint if we are the GameMaster (else the changes wouldn't effect the map anyways) */
		if (GameWindow.gw.isGameMaster()) {
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
								objectMapManager.adjustSurrounding(objectMapManager.get(p), localX, localY, GamePanel.gp.getPaintType());
								
								overlayMapManager.get(p).setUl(localX, localY, GamePanel.gp.getPaintType()-GameStates.mapTileSetWidth*2);
								overlayMapManager.get(p).setUr(localX, localY, GamePanel.gp.getPaintType()-GameStates.mapTileSetWidth*2);
								overlayMapManager.get(p).setDl(localX, localY, 0);
								overlayMapManager.get(p).setDr(localX, localY, 0);
								overlayMapManager.get(p).setIdByCornersObject(localX, localY, GamePanel.gp.getPaintType()-GameStates.mapTileSetWidth*2);
								overlayMapManager.adjustSurrounding(overlayMapManager.get(p), localX, localY, GamePanel.gp.getPaintType()-GameStates.mapTileSetWidth*2);
							} else {
								//MapManager.get(p).setTile(localX, localY, null);
								if (objectMapManager.get(p).getLocalMap()[localX][localY] != 0) {
									// create drop 
									BigInteger itemId = WorldItemManager.getMaxID().add(GamePanel.gp.getPlayerEntity().getId());
//									System.out.println("added new wood object: "+itemId);
//									Wood item = new Wood();
//									TileSet woodTileSet = new TileSet(GameStates.standardTilePathItems+"wood2.png", "WoodPileTileSet", GameStates.itemTileWidth, GameStates.itemTileHeight);
//									Entity itemEntity = new Entity(woodTileSet, "wood", itemId, p.x+localX*GameStates.mapTileSetWidth+(int) (Math.random()*(GameStates.mapTileSetWidth-GameStates.itemTileWidth)), p.y+localY*GameStates.mapTileSetHeight +(int) (Math.random()*(GameStates.mapTileSetHeight-GameStates.itemTileHeight)), GameStates.animationDelayItems);
//									WorldItem wood = new WorldItem(itemId, item, itemEntity);
//									WorldItemManager.add(wood);
									
									/** send the complete Item to all players of the channel */
									if (GameWindow.gw.isLoggedIn() && GamePanel.gp.isInitializedPlayer()) {
										/** first send to server for the itemList */
										GameWindow.gw.send(ClientMessages.addItem(itemId));
										for (String channelName : GameWindow.gw.getSpaceChannels().values()) {
											ClientChannel channel = GameWindow.gw.getChannelByName(channelName);
											try {
												channel.send(ClientMessages.addCompleteItem(ITEMCODE.WOOD, itemId, "wood", p.x+localX*GameStates.mapTileSetWidth+(int) (Math.random()*(GameStates.mapTileSetWidth-GameStates.itemTileWidth)), p.y+localY*GameStates.mapTileSetHeight +(int) (Math.random()*(GameStates.mapTileSetHeight-GameStates.itemTileHeight)), 5, new float[1]));
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
								objectMapManager.deleteSurrounding(objectMapManager.get(p), localX, localY, GamePanel.gp.getPaintType());
								
								overlayMapManager.get(p).setUl(localX, localY, 0);
								overlayMapManager.get(p).setUr(localX, localY, 0);
								overlayMapManager.get(p).setDl(localX, localY, 0);
								overlayMapManager.get(p).setDr(localX, localY, 0);
								overlayMapManager.get(p).setIdByCornersObject(localX, localY, GamePanel.gp.getPaintType()-GameStates.mapTileSetWidth*2);
								overlayMapManager.deleteSurrounding(overlayMapManager.get(p), localX, localY, GamePanel.gp.getPaintType()-GameStates.mapTileSetWidth*2);
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
		}
	}
	
	public void determineAnimation(PlayerEntity entity, Point p) {
		int distX = Math.abs((int) (entity.getX())-p.x);
		int distY = Math.abs((int) (entity.getY())-p.y);
		
		/** define orientation by point of the mouse */
		if (distX > distY) {
			if (entity.getX() < p.x){
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
			if (entity.getY() < p.y){
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
