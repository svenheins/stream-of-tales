package de.svenheins.threads;


import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;

import de.svenheins.main.AttributeType;
import de.svenheins.main.GUI;
import de.svenheins.main.GameModus;
import de.svenheins.main.GamePanel;
import de.svenheins.main.GameWindow;
import de.svenheins.main.Priority;
import de.svenheins.managers.ClientTextureManager;
import de.svenheins.managers.EntityManager;
import de.svenheins.managers.PlayerManager;
import de.svenheins.managers.SpaceManager;
//import de.svenheins.managers.TextureManager;
import de.svenheins.messages.ClientMessages;
import de.svenheins.objects.AreaInfluence;
import de.svenheins.objects.Entity;
import de.svenheins.objects.LocalObject;
import de.svenheins.objects.PlayerEntity;
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
			if(!GamePanel.gp.isPaused() && GameModus.modus == GameModus.LOADING && GameWindow.gw.isLoggedIn()) {
				if (!GamePanel.gp.isServerInitialized()) {
					EntityManager.entityList = new HashMap<BigInteger, Entity>();
					EntityManager.idList = new ArrayList<BigInteger>();
					SpaceManager.spaceList = new HashMap<BigInteger, Space>();
					SpaceManager.idList = new ArrayList<BigInteger>();
					PlayerManager.playerList = new HashMap<BigInteger, PlayerEntity>();
					PlayerManager.idList = new ArrayList<BigInteger>();
					ClientTextureManager.manager.init();
					GameWindow.gw.send(ClientMessages.initEntities());
					GameWindow.gw.send(ClientMessages.initItems());
					GameWindow.gw.send(ClientMessages.initAreaInfluences());
					GameWindow.gw.send(ClientMessages.initSpaces());
					GameWindow.gw.send(ClientMessages.initTextures());
					GameWindow.gw.send(ClientMessages.initPlayers());
					GameWindow.gw.send(ClientMessages.initMe());
					
					float[] tempAttributes = new float[AttributeType.values().length];
					tempAttributes[AttributeType.MX.ordinal()]= 20.0f;
					LocalObject localObject = new LocalObject(BigInteger.valueOf(0), "", 0,0,500,500, 0,0,0,0);
					AreaInfluence areaInfluence1 = new AreaInfluence(BigInteger.valueOf(0), System.currentTimeMillis(), System.currentTimeMillis()+120000, localObject, "movers", true, tempAttributes, Priority.LOW);
//					AreaInfluenceManager.add(areaInfluence1);
					GameWindow.gw.send(ClientMessages.addAreaInfluence(areaInfluence1));//(iTile, GameWindow.gw.getGameMasterName(), PlayerManager.get(GameWindow.gw.getGameMasterName()).getId(), "tree1", "overlayTree1"));
					
					
					
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
