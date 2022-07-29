package de.svenheins.handlers;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.math.BigInteger;


import de.svenheins.objects.Space;
import de.svenheins.objects.Tile;

import de.svenheins.animation.SpaceDisappear;


import de.svenheins.main.ConnectionWindow;
import de.svenheins.main.EditWindow;
import de.svenheins.main.GameModus;
import de.svenheins.main.GamePanel;
import de.svenheins.main.GameStates;
import de.svenheins.main.GameWindow;
import de.svenheins.managers.EntityManager;
import de.svenheins.managers.MapManager;
import de.svenheins.managers.ObjectMapManager;
import de.svenheins.managers.PlayerManager;
import de.svenheins.managers.SpaceManager;
import de.svenheins.messages.ClientMessages;
import de.svenheins.messages.OBJECTCODE;

public class MouseHandler implements MouseListener, MouseMotionListener{

	private boolean dragging = false;
	protected Space dragSpace=null; 
	protected int localX, localY;
	
	@Override
	public void mouseClicked(MouseEvent mouseEvent) {
		Point point = mouseEvent.getPoint();
		
		if (GameModus.modus == GameModus.GAME && GameWindow.gw.getShowConsole() == false){
			gameMouseClicked(point);
		} else 
		if (GameModus.modus == GameModus.MAINMENU) {
			mainMenuMouseClicked(point);
		}
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
		if (GameModus.modus == GameModus.GAME && GameWindow.gw.getShowConsole() == false){
			gameMousePressed(point);
		}
	}

	@Override
	public void mouseReleased(MouseEvent mouseEvent) {
		Point point = mouseEvent.getPoint();
		if (GameModus.modus == GameModus.GAME && GameWindow.gw.getShowConsole() == false){
			gameMouseReleased(point);
		} else
		if (GameModus.modus == GameModus.MAINMENU){
			mainMenuMouseClicked(point);
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
	 * @Method: mouseClicked
	 * @param point
	 */
	public void mainMenuMouseClicked(Point point) {
		if (GamePanel.gp.connectButton.getPolygon().get(0).contains(point)) {
			new ConnectionWindow();
		}
	}
	
	/**
	 * @ModusImplementation: GAME
	 * @Method: mouseClicked
	 * @param point
	 */
	public void gameMouseClicked(Point point) {
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
				new EditWindow(EntityManager.get(i));
			}
		
		}
		
		if (GamePanel.gp.playerEntity.contains(point)) {
			new EditWindow(GamePanel.gp.playerEntity);
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
	public void gameMousePressed(Point point) {
//		Point p_save = new Point(point);
//		System.out.println("ViewPoint: x = "+GamePanel.gp.getViewPointX()+ " y = "+ GamePanel.gp.getViewPointY());

		Point correctedPoint = new Point( (int) (point.x/GamePanel.gp.getZoomFactor()) +GamePanel.gp.getViewPointX(), (int) (point.y/GamePanel.gp.getZoomFactor())+GamePanel.gp.getViewPointY());
		point = correctedPoint;
		
		int localWidth = GameStates.mapWidth * GameStates.mapTileSetWidth;
		int localHeight = GameStates.mapHeight * GameStates.mapTileSetHeight;
		int latticePointX = (int) Math.floor( (float) correctedPoint.x / (localWidth)) * localWidth;
		int latticePointY = (int) Math.floor( (float) correctedPoint.y / (localHeight)) * localHeight;
		int localX = (int) Math.floor( (float) (correctedPoint.x - latticePointX )/ GameStates.mapTileSetWidth);
		int localY = (int) Math.floor( (float) (correctedPoint.y - latticePointY )/ GameStates.mapTileSetHeight);
		
		Point p = new Point(latticePointX, latticePointY);
		
		if (GamePanel.gp.getPaintLayer().equals("tree")) {
			/** allways paint trees */
			if ( (localX % 4 != 0) && (localX % 4 != 2)) localX ++;
			if (localY % 2 != 0) localY ++;
//			if ((localY % 2 == 0) && ( (localX % 4 == 0) || (localX % 4 == 2))) {
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
					//Tile tile = new Tile(62, true, true, true, true);
		//			MapManager.get(p).setTile(localX, localY, 62);
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
//			}
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
		
		
	}
	
	/**
	 * @ModusImplementation: GAME
	 * @Method: mouseDragged
	 * @param point
	 */
	public void gameMouseDragged(Point point) {
		gameMousePressed(point);
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
	
}
