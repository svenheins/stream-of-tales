package de.svenheins.threads;

import java.awt.Point;
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import com.sun.sgs.client.ClientChannel;

import de.svenheins.functions.MyUtil;
import de.svenheins.main.GUI;
import de.svenheins.main.GameModus;
import de.svenheins.main.GamePanel;
import de.svenheins.main.GameStates;
import de.svenheins.main.GameWindow;
import de.svenheins.managers.MapManager;
import de.svenheins.managers.ObjectMapManager;
import de.svenheins.managers.SpaceManager;
import de.svenheins.messages.ClientMessages;
import de.svenheins.messages.OBJECTCODE;
import de.svenheins.objects.LocalMap;
import de.svenheins.objects.Player;
import de.svenheins.objects.PlayerEntity;
import de.svenheins.objects.Space;

public class ChannelUpdateMapsThread implements Runnable {
	private long duration = 0;
	private long oldTime = System.currentTimeMillis();
	private PlayerEntity playerEntity;
	private float playerOldMX = 0;
	private float playerOldMY = 0;
	private final int sleepingTime = 20;
	
	@Override
	public void run() {
		while (GUI.running) {
			duration = System.currentTimeMillis() - oldTime;
			if(GameModus.modus == GameModus.GAME && (GameWindow.gw.isGameMaster())) {
//				System.out.println(GameWindow.gw.getSendMapList().size());
				if (GameWindow.gw.getSendMapList().size() > 0) {
					String mapFileName = GameWindow.gw.takeFirstSendMap();
					GameWindow.gw.gameInfoConsole.appendInfo("got "+mapFileName);
					if (mapFileName != null) {
						channelSendUpdateMapsRun(mapFileName);
					}
				}
			}
			try {
				Thread.sleep(sleepingTime);
			}
			catch(InterruptedException exception) {
				System.out.println(exception);
			}
			
		}
		
		
		
	}
	
	public void channelSendUpdateMapsRun(String mapFileName) {
		if (GameWindow.gw.isLoggedIn() && GamePanel.gp.isInitializedPlayer()) {
			byte[] sendMapFileByte = getMapFromFileName(mapFileName);
			int sizeOfSendFileByte = sendMapFileByte.length;
			for (String channelName : GameWindow.gw.getSpaceChannels().values()) {
				ClientChannel channel = GameWindow.gw.getChannelByName(channelName);
				try {
					GameWindow.gw.gameInfoConsole.appendInfo("Sending File "+mapFileName+" with bytes: "+sizeOfSendFileByte +" bytes");
					channel.send(ClientMessages.sendMap(GameWindow.gw.getPlayerName(), sizeOfSendFileByte, sendMapFileByte, mapFileName, GameWindow.gw.getSendMapList().size()) );
				} catch (IOException e) {
					e.printStackTrace();
				}	
			}
		}
	}

	
	public byte[] getMapFromFileName(String mapFile) {
		byte[] retMapByte = null;
		String playerName = GameWindow.gw.getPlayerName();
		String paintLayer;
		String strSplit[] = mapFile.split("_");
		int x = Integer.parseInt(strSplit[1]);
		int y = Integer.parseInt((strSplit[2]).replace(".map", ""));
		Point p = new Point(x, y);
		
		if (mapFile.startsWith("cobble")) {
			paintLayer = "cobble";
			retMapByte = GameWindow.gw.getMapManagers().get(paintLayer).LocalMap2ByteArray(playerName, paintLayer, p);
		} else if (mapFile.startsWith("grass")) {
			paintLayer = "grass";
			retMapByte = GameWindow.gw.getMapManagers().get(paintLayer).LocalMap2ByteArray(playerName, paintLayer, p);
		} else if (mapFile.startsWith("snow")) {
			paintLayer = "snow";
			retMapByte = GameWindow.gw.getMapManagers().get(paintLayer).LocalMap2ByteArray(playerName, paintLayer, p);
		} else if (mapFile.startsWith("desert")) {
			paintLayer = "desert";
			retMapByte = GameWindow.gw.getMapManagers().get(paintLayer).LocalMap2ByteArray(playerName, paintLayer, p);
		} else if (mapFile.startsWith("tree1")) {
			paintLayer = "tree1";
			retMapByte = GameWindow.gw.getObjectMapManagers().get(paintLayer).LocalMap2ByteArray(playerName, paintLayer, p);
		} else if (mapFile.startsWith("tree2")) {
			paintLayer = "tree2";
			retMapByte = GameWindow.gw.getObjectMapManagers().get(paintLayer).LocalMap2ByteArray(playerName, paintLayer, p);
		} else if (mapFile.startsWith("overlayTree1")) {
			paintLayer = "overlayTree1";
			retMapByte = GameWindow.gw.getObjectMapManagers().get(paintLayer).LocalMap2ByteArray(playerName, paintLayer, p);
		} else if (mapFile.startsWith("overlayTree2")) {
			paintLayer = "overlayTree2";
			retMapByte = GameWindow.gw.getObjectMapManagers().get(paintLayer).LocalMap2ByteArray(playerName, paintLayer, p);
		} else if (mapFile.startsWith("underground")) {
			paintLayer = "underground";
			retMapByte = GameWindow.gw.getUndergroundMapManagers().get(paintLayer).LocalUndergroundMap2ByteArray(playerName, paintLayer, p);
		} else {
			GameWindow.gw.gameInfoConsole.appendInfo("Didn't find map: "+mapFile);
		}
		
		return retMapByte;
	}
	
//	public void channelUpdateMapsRun(String mapFileName) {
//		if (GameWindow.gw.isLoggedIn() && GamePanel.gp.isInitializedPlayer()) {
//			byte[] sendMapFileByte = getMapFromFileName(mapFileName);
//			int sizeOfSendFileByte = sendMapFileByte.length;
//			for (String channelName : GameWindow.gw.getSpaceChannels().values()) {
//				ClientChannel channel = GameWindow.gw.getChannelByName(channelName);
//				try {
//					GameWindow.gw.gameInfoConsole.appendInfo("Sending File "+mapFileName+" with bytes: "+sizeOfSendFileByte +" bytes");
//					channel.send(ClientMessages.sendMap(GameWindow.gw.getPlayerName(), sizeOfSendFileByte, sendMapFileByte, mapFileName) );
//				} catch (IOException e) {
//					e.printStackTrace();
//				}	
//			}
//		}
//	}
//	
//	public byte[] getMapFromFileName(String mapFile) {
//		byte[] retMapByte = null;
//		String playerName = GameWindow.gw.getPlayerName();
//		String paintLayer;
//		String strSplit[] = mapFile.split("_");
//		int x = Integer.parseInt(strSplit[1]);
////		System.out.println(x);
////		System.out.println((strSplit[2]).replace(".map", ""));
//		int y = Integer.parseInt((strSplit[2]).replace(".map", ""));
//		Point p = new Point(x, y);
//		
//		if (mapFile.startsWith("cobble")) {
//			paintLayer = "cobble";
//			retMapByte = GameWindow.gw.getMapManagers().get(paintLayer).LocalMap2ByteArray(playerName, paintLayer, p);
//		} else if (mapFile.startsWith("grass")) {
//			paintLayer = "grass";
//			retMapByte = GameWindow.gw.getMapManagers().get(paintLayer).LocalMap2ByteArray(playerName, paintLayer, p);
//		} else if (mapFile.startsWith("snow")) {
//			paintLayer = "snow";
//			retMapByte = GameWindow.gw.getMapManagers().get(paintLayer).LocalMap2ByteArray(playerName, paintLayer, p);
//		} else if (mapFile.startsWith("tree1")) {
//			paintLayer = "tree1";
//			retMapByte = GameWindow.gw.getObjectMapManagers().get(paintLayer).LocalMap2ByteArray(playerName, paintLayer, p);
//		} else if (mapFile.startsWith("tree2")) {
//			paintLayer = "tree2";
//			retMapByte = GameWindow.gw.getObjectMapManagers().get(paintLayer).LocalMap2ByteArray(playerName, paintLayer, p);
//		} else if (mapFile.startsWith("overlayTree1")) {
//			paintLayer = "overlayTree1";
//			retMapByte = GameWindow.gw.getObjectMapManagers().get(paintLayer).LocalMap2ByteArray(playerName, paintLayer, p);
//		} else if (mapFile.startsWith("overlayTree2")) {
//			paintLayer = "overlayTree2";
//			retMapByte = GameWindow.gw.getObjectMapManagers().get(paintLayer).LocalMap2ByteArray(playerName, paintLayer, p);
//		}
//		
//		return retMapByte;
//	}
//	
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
