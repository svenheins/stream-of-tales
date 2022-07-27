package de.svenheins.handlers;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;


import de.svenheins.objects.Space;

import de.svenheins.animation.SpaceDisappear;


import de.svenheins.main.ConnectionWindow;
import de.svenheins.main.EditWindow;
import de.svenheins.main.GameModus;
import de.svenheins.main.GamePanel;
import de.svenheins.managers.EnemyManager;
import de.svenheins.managers.PlayerManager;
import de.svenheins.managers.SpaceManager;

public class MouseHandler implements MouseListener, MouseMotionListener{

	private boolean dragging = false;
	protected Space dragSpace=null; 
	protected int localX, localY;
	
	@Override
	public void mouseClicked(MouseEvent mouseEvent) {
		Point point = mouseEvent.getPoint();
		
		if (GameModus.modus == GameModus.GAME && GamePanel.gp.getShowConsole() == false){
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
		if (GameModus.modus == GameModus.MAINMENU){
			mainMenuMouseClicked(point);
		}
	}
	
	@Override
	public void mouseDragged(MouseEvent mouseEvent) {
		Point point = mouseEvent.getPoint();
		if (GameModus.modus == GameModus.GAME && GamePanel.gp.getShowConsole() == false){
			gameMouseDragged(point);
		}
	}

	@Override
	public void mouseMoved(MouseEvent mouseEvent) {
		Point point = mouseEvent.getPoint();
		if (GameModus.modus == GameModus.GAME && GamePanel.gp.getShowConsole() == false){
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
		if (GamePanel.gp.startButton.getPolygon().get(0).contains(point)) {
			GameModus.modus = GameModus.GAME;
		}
		
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
		EnemyManager.sortZIndex();
		for( int i =0; i < EnemyManager.sizeSorted(); i++){
			
				if (EnemyManager.getSorted(i).contains(point)) {
					// open edit-windows
					GamePanel.gp.setPause(true);
					new EditWindow(EnemyManager.getSorted(i));
				}
			
		}
		
		for( int i =0; i < SpaceManager.size(); i++){
			for (int j = 0; j< SpaceManager.get(i).getPolygon().size(); j++) {
				if (SpaceManager.get(i).getPolygon().get(j).contains(point)) {
					// open edit-windows
					GamePanel.gp.setPause(true);
					new EditWindow(SpaceManager.get(i));
				}
			}
		}
		
		for( int i =0; i < PlayerManager.size(); i++){
			if (PlayerManager.get(i).contains(point)) {
				// open edit-windows
				GamePanel.gp.setPause(true);
				new EditWindow(PlayerManager.get(i));
			}
		}
		
//		if (GamePanel.gp.getSpace().getPolygon().get(0).contains(point)) {
//			// open edit-windows
//			GamePanel.gp.setPause(true);
//			new EditWindow(GamePanel.gp.getSpace());
//		}
	}
	
	
	/**
	 * @ModusImplementation: MAINMENU
	 * @Method: mouseMoved
	 * @param point
	 */
	public void mainMenuMouseMoved(Point point){
		if (GamePanel.gp.startButton.getPolygon().get(0).contains(point)) {
			GamePanel.gp.startButton.setTrans(1.0f);
		} else {
			GamePanel.gp.startButton.setTrans(0.5f);
		}
		
		if (GamePanel.gp.connectButton.getPolygon().get(0).contains(point)) {
			GamePanel.gp.connectButton.setTrans(1.0f);
		} else {
			GamePanel.gp.connectButton.setTrans(0.5f);
		}
	}
	
	/**
	 * @ModusImplementation: GAME
	 * @Method: mouseMoved
	 * @param point
	 */
	public void gameMouseMoved(Point point){
		dragging = false;
		dragSpace = null;
	}
	
	/**
	 * @ModusImplementation: GAME
	 * @Method: mouseDragged
	 * @param point
	 */
	public void gameMouseDragged(Point point) {
		if(dragging == true && dragSpace != null) {
			double newX = point.getX()-localX;
			double newY = point.getY()-localY;
			dragSpace.setAllXY(newX, newY);
		} else {
			SpaceManager.sortZIndex();
			for( int i =0; i < SpaceManager.sizeSorted(); i++){
				for (int j = 0; j< SpaceManager.getSorted(i).getPolygon().size(); j++) {
					if (SpaceManager.getSorted(i).getPolygon().get(j).contains(point)) {
						SpaceDisappear spaceDis = new SpaceDisappear(SpaceManager.getSorted(i));
						spaceDis.setRGBBefore(SpaceManager.getSorted(i).getRGB());
						SpaceManager.getSorted(i).setSpaceAnimation(spaceDis);
						SpaceManager.getSorted(i).setMovement(0, 0);
						dragSpace = SpaceManager.getSorted(i);
						localX = (int) (point.getX()- (int) dragSpace.getX());
						localY = (int) (point.getY()- (int) dragSpace.getY());
						dragging = true;
					}
				}
			}
		}
	}
	
}
