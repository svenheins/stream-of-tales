package de.svenheins.threads;

import java.math.BigInteger;

import de.svenheins.objects.Entity;
import de.svenheins.objects.Space;
import de.svenheins.main.GUI;
import de.svenheins.main.GamePanel;
import de.svenheins.main.StatPanel;
import de.svenheins.main.gui.EditorGUIManager;
import de.svenheins.main.gui.PlayerListGUIManager;
import de.svenheins.managers.EntityManager;
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
//				System.out.println("real time: "+System.currentTimeMillis());
				GamePanel.gp.updatePlayerSprite();
				
				for (int i = EntityManager.size()-1; i >= 0; i--) {
					Entity entity = null;
					try {
						entity = EntityManager.get(EntityManager.idList.get(i));
					} catch (IndexOutOfBoundsException exception){
						System.out.println(exception);
					}
					if(entity != null) entity.updateSprite();
				}
				
				for (int j=PlayerManager.size()-1;j>=0; j--){
					Entity e2 = null;
					try {
						e2 = PlayerManager.get(PlayerManager.idList.get(j));
					} catch (IndexOutOfBoundsException exception) {
						System.out.println(exception);
					}
					if(e2 != null) e2.updateSprite(); 
				}
//				for (int k = SpaceManager.size()-1; k >=0; k--) {
				for (int k = SpaceManager.size()-1; k >= 0; k --) {
					Space space = null;
					try {
						space = SpaceManager.get(SpaceManager.idList.get(k));
					} catch (IndexOutOfBoundsException exception ) {
						System.out.println(exception);
					}
					if(space != null) space.updateSpace();
				}
				
				for (BigInteger buttonID : EditorGUIManager.get("floor").idList) {
					EditorGUIManager.get("floor").get(buttonID).updateSprite();
				}
				for (BigInteger buttonID : PlayerListGUIManager.get("playerList").idList) {
					PlayerListGUIManager.get("playerList").get(buttonID).updateSprite();
				}
				for (BigInteger buttonID : StatPanel.sp.contextMenu.idList) {
					StatPanel.sp.contextMenu.get(buttonID).updateSprite();
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
	
	

}
