package de.svenheins.handlers;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.math.BigInteger;


import de.svenheins.objects.Space;

import de.svenheins.animation.SpaceDisappear;


import de.svenheins.main.ConnectionWindow;
import de.svenheins.main.EditWindow;
import de.svenheins.main.GameModus;
import de.svenheins.main.GamePanel;
import de.svenheins.main.GameStates;
import de.svenheins.main.GameWindow;
import de.svenheins.managers.EntityManager;
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
		
		for( BigInteger i : SpaceManager.idList) {
			for (int j = 0; j< SpaceManager.get(i).getPolygon().size(); j++) {
				if (SpaceManager.get(i).getPolygon().get(j).contains(point)) {
					// open edit-windows
//					GamePanel.gp.setPause(true);
					new EditWindow(SpaceManager.get(i));
				}
			}
		}
		
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
		Point correctedPoint = new Point( (int) (point.x/GamePanel.gp.getZoomFactor()) +GamePanel.gp.getViewPointX(), (int) (point.y/GamePanel.gp.getZoomFactor())+GamePanel.gp.getViewPointY());
		point = correctedPoint;
//		point.x = point.x - GamePanel.gp.getViewPointX();
//		point.y = point.y - GamePanel.gp.getViewPointY();
		if(dragging == true && dragSpace != null) {
			float newX = (float) (point.getX()-localX);
			float newY = (float) (point.getY()-localY);
			dragSpace.setAllXY(newX, newY);
			dragSpace.setUpdateByServer(false);
			GameWindow.gw.send(ClientMessages.editObjectState(OBJECTCODE.SPACE, dragSpace.getId(),  new float[]{newX, newY, dragSpace.getMX(), dragSpace.getMY(), dragSpace.getHeight(), dragSpace.getWidth()}));
//			System.out.println("ID: "+dragSpace.getId() +"  x: "+newX+" y: "+newY);
		} else {
//			SpaceManager.sortZIndex();
//			for( int i =0; i < SpaceManager.sizeSorted(); i++){
//				for (int j = 0; j< SpaceManager.getSorted(i).getPolygon().size(); j++) {
//					if (SpaceManager.getSorted(i).getPolygon().get(j).contains(point)) {
//						SpaceDisappear spaceDis = new SpaceDisappear(SpaceManager.getSorted(i));
//						spaceDis.setRGBBefore(SpaceManager.getSorted(i).getRGB());
//						SpaceManager.getSorted(i).setSpaceAnimation(spaceDis);
//						SpaceManager.getSorted(i).setMovement(0, 0);
//						dragSpace = SpaceManager.getSorted(i);
//						localX = (int) (point.getX()- (int) dragSpace.getX());
//						localY = (int) (point.getY()- (int) dragSpace.getY());
//						dragging = true;
//					}
//				}
//			}
//			SpaceManager.sortZIndex();
//			for( int i =0; i < SpaceManager.size(); i++){
			for (BigInteger i: SpaceManager.idList) {
				for (int j = 0; j< SpaceManager.get(i).getPolygon().size(); j++) {
					if (SpaceManager.get(i).getPolygon().get(j).contains(point)) {
						SpaceDisappear spaceDis = new SpaceDisappear(SpaceManager.get(i));
						spaceDis.setRGBBefore(SpaceManager.get(i).getRGB());
						SpaceManager.get(i).setSpaceAnimation(spaceDis);
						SpaceManager.get(i).setMovement(0, 0);
						dragSpace = SpaceManager.get(i);
						localX = (int) (point.getX()- (int) dragSpace.getPolyX());
						localY = (int) (point.getY()- (int) dragSpace.getPolyY());
						dragging = true;
					}
				}
			}
		}
	}
	
}
