package de.svenheins.threads;

import java.math.BigInteger;

import de.svenheins.objects.Entity;
import de.svenheins.objects.Space;
import de.svenheins.main.GUI;
import de.svenheins.main.GamePanel;
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
				
				for (int j=PlayerManager.size()-1;j>=0; j--){
					Entity e2 = PlayerManager.get(PlayerManager.idList.get(j));
					if(e2 != null) e2.updateSprite(); 
				}
//				for (int k = SpaceManager.size()-1; k >=0; k--) {
				for (int k = SpaceManager.size()-1; k >= 0; k --) {
					Space space = SpaceManager.get(SpaceManager.idList.get(k));
					if(space != null) space.updateSpace();
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
