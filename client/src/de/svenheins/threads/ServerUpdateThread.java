package de.svenheins.threads;


import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;

import de.svenheins.main.GUI;
import de.svenheins.main.GameModus;
import de.svenheins.main.GamePanel;
import de.svenheins.main.GameWindow;
import de.svenheins.managers.EntityManager;
import de.svenheins.managers.SpaceManager;
import de.svenheins.managers.TextureManager;
import de.svenheins.messages.ClientMessages;
import de.svenheins.objects.Entity;
import de.svenheins.objects.Space;

public class ServerUpdateThread implements Runnable {

	private long duration, last; 
	private long millis, frames;
		

	public void setLast(long last) {
		this.last = last;
	}
	
	
	/**
	 * @param last
	 * needs the currentTimeMillis(), to avoid the bug at the first frame
	 */
	public ServerUpdateThread(long last) {
		this.last = last;
	}
	
	public void run() {
		while (GUI.running) {			
			duration = System.currentTimeMillis() - last;
			last = System.currentTimeMillis();
			millis += duration;
			frames +=1;
			if(!GamePanel.gp.isPaused() && GameModus.modus == GameModus.GAME ) {
				if (!GamePanel.gp.isServerInitialized()) {
					EntityManager.entityList = new HashMap<BigInteger, Entity>();
					EntityManager.idList = new ArrayList<BigInteger>();
					SpaceManager.spaceList = new HashMap<BigInteger, Space>();
					SpaceManager.idList = new ArrayList<BigInteger>();
					TextureManager.manager.init();
					GameWindow.gw.send(ClientMessages.initEntities());
					GameWindow.gw.send(ClientMessages.initSpaces());
					GameWindow.gw.send(ClientMessages.initTextures());
					GamePanel.gp.setServerInitialized(true);
				}

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
