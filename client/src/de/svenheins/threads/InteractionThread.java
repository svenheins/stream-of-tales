/* Copyright (C) Sven Heins - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * author: Sven Heins
 * date: 05.07.2015
 */
package de.svenheins.threads;

import de.svenheins.main.GUI;
import de.svenheins.main.GameModus;
import de.svenheins.managers.InteractionManager;

public class InteractionThread implements Runnable {
//	private long duration, last;
	
	
	@Override
	public void run() {
		while (GUI.running) {
//			duration = System.currentTimeMillis() - last;
//			last = System.currentTimeMillis();
			if(GameModus.modus == GameModus.GAME) {
				InteractionManager.update();
			}
			
			try {
				Thread.sleep(1000);
			}
			catch(InterruptedException exception) {
				System.out.println(exception);
			}
		}
	}

}
