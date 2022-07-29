package de.svenheins.threads;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import com.sun.sgs.client.ClientChannel;

import de.svenheins.main.GUI;
import de.svenheins.main.GameModus;
import de.svenheins.main.GamePanel;
import de.svenheins.main.GameWindow;
import de.svenheins.managers.SpaceManager;
import de.svenheins.messages.ClientMessages;
import de.svenheins.messages.OBJECTCODE;
import de.svenheins.objects.Player;
import de.svenheins.objects.PlayerEntity;
import de.svenheins.objects.Space;

public class ChannelUpdateMapsThread implements Runnable {
	private long duration = 0;
	private long oldTime = System.currentTimeMillis();
	private PlayerEntity playerEntity;
	private float playerOldMX = 0;
	private float playerOldMY = 0;
	private final int sleepingTime = 75;
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
		
		while (GUI.running) {
//			duration = System.currentTimeMillis() - last;
			duration = System.currentTimeMillis() - oldTime;
			
//			System.out.println("input-thread runs");
			if(GameModus.modus == GameModus.GAME) {
				channelUpdateMapsRun();
//				if (duration > 300) {
//					oldTime = System.currentTimeMillis();
//					channelUpdateRunStandingStill();
//				}
			}
			
			try {
				Thread.sleep(sleepingTime);
			}
			catch(InterruptedException exception) {
				System.out.println(exception);
			}
			
		}
		
		
		
	}
	
	public void channelUpdateMapsRun() {
		if (GameWindow.gw.isLoggedIn() && GamePanel.gp.isInitializedPlayer()) {
			playerEntity = GamePanel.gp.getPlayerEntity();
			if (playerEntity.getMX() != 0 || playerOldMX != 0 || playerEntity.getMY() != 0 || playerOldMY != 0) {
				for (String channelName : GameWindow.gw.getSpaceChannels().values()) {
					ClientChannel channel = GameWindow.gw.getChannelByName(channelName);
					try {
						channel.send(ClientMessages.editObjectState(OBJECTCODE.PLAYER, playerEntity.getId(),  new float[]{playerEntity.getX(), playerEntity.getY(), playerEntity.getMX(), playerEntity.getMY()}));
	//					System.out.println("ID send: "+playerEntity.getId());
					} catch (IOException e) {
						e.printStackTrace();
					}	
				}
			}
			playerOldMX = playerEntity.getMX();
			playerOldMY = playerEntity.getMY();
			GameWindow.gw.send(ClientMessages.editObjectState(OBJECTCODE.PLAYER, playerEntity.getId(),  new float[]{playerEntity.getX(), playerEntity.getY(), playerEntity.getMX(), playerEntity.getMY()}));
		}
	}
	
//	public void channelUpdateRunStandingStill() {
//		if (GameWindow.gw.isLoggedIn() && GamePanel.gp.isInitializedPlayer()) {
//			playerEntity = GamePanel.gp.getPlayerEntity();
////			if (playerEntity.getMX() != 0 || playerOldMX != 0 || playerEntity.getMY() != 0 || playerOldMY != 0) {
//				for (String channelName : GameWindow.gw.getSpaceChannels().values()) {
//					ClientChannel channel = GameWindow.gw.getChannelByName(channelName);
//					try {
//						channel.send(ClientMessages.editObjectState(OBJECTCODE.PLAYER, playerEntity.getId(),  new float[]{playerEntity.getX(), playerEntity.getY(), playerEntity.getMX(), playerEntity.getMY()}));
//	//					System.out.println("ID send: "+playerEntity.getId());
//					} catch (IOException e) {
//						e.printStackTrace();
//					}	
//				}
//				
//				/** check if inside space */
//				List<BigInteger> idListTempSpaces = new ArrayList<BigInteger>(SpaceManager.idList);
//				boolean playerInsideSpace;
//				for (BigInteger i : idListTempSpaces) {
//					Space space= SpaceManager.get(i);
//					if(space != null) {
//						playerInsideSpace = false;
//						PlayerEntity playerEnt = GamePanel.gp.playerEntity;
//						for (int j = 0; j < space.getPolygon().size(); j++) {
//							if (space.getPolygon().get(j).contains(playerEnt.getX()+ playerEnt.getWidth()/2, playerEnt.getY()+playerEnt.getHeight()/2) )
//									playerInsideSpace = true;
//						}
//						if(playerInsideSpace) {
//							if (!GameWindow.gw.getSpaceChannels().containsKey(space.getId())) {
//								GameWindow.gw.send(ClientMessages.leaveSpaceChannel(space.getId()));
//								GameWindow.gw.send(ClientMessages.joinSpaceChannel(space.getId()));
//							}
//						} else {
//							if (GameWindow.gw.getSpaceChannels().containsKey(space.getId())) {
//								GameWindow.gw.send(ClientMessages.leaveSpaceChannel(space.getId()));
//							}
//						}
////						}
//					}
//				}
////			}
//			playerOldMX = playerEntity.getMX();
//			playerOldMY = playerEntity.getMY();
//			GameWindow.gw.send(ClientMessages.editObjectState(OBJECTCODE.PLAYER, playerEntity.getId(),  new float[]{playerEntity.getX(), playerEntity.getY(), playerEntity.getMX(), playerEntity.getMY()}));
//		}
//	}

}
