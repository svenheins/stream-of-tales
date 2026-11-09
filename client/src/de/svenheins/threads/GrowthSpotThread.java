package de.svenheins.threads;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.List;

import de.svenheins.functions.MyMath;
import de.svenheins.main.AttributeType;
import de.svenheins.main.GUI;
import de.svenheins.main.GameModus;
import de.svenheins.main.GamePanel;
import de.svenheins.managers.AgentManager;
import de.svenheins.managers.GrowthSpotManager;
import de.svenheins.objects.GrowthSpot;
import de.svenheins.objects.LocalObject;
import de.svenheins.objects.agents.Agent;

public class GrowthSpotThread implements Runnable {

	@Override
	public void run() {
		while (GUI.running) {
//			duration = System.currentTimeMillis() - last;
//			last = System.currentTimeMillis();
			if(GameModus.modus == GameModus.GAME) {
				long timeNow = System.currentTimeMillis();
				int index = 0;
//				System.out.println("Creating tempAreaInfluenceList");
				HashMap<BigInteger, GrowthSpot> tempGrowthSpotList = new HashMap<BigInteger, GrowthSpot>(GrowthSpotManager.growthSpotList);
				ArrayList<BigInteger> tempIDList = new ArrayList<>(GrowthSpotManager.idList);
				int[] deleteIndex = new int[tempIDList.size()];
				
//				System.out.println("Before : tempAreainfluence.size = "+tempAreaInfluenceList.size());
//				System.out.println("Before : areainfluence.size = "+AreaInfluenceManager.areaInfluenceList.size());
//				for(AreaInfluence areaInfluence: tempAreaInfluenceList) {
				GrowthSpot growthSpot;
				for(BigInteger tempID: tempIDList) {
					growthSpot = tempGrowthSpotList.get(tempID);
					if (growthSpot != null) {
						/** first check if the growth spot has not ended yet */
						if ( ((growthSpot.getTimeBegin() <= timeNow) && (growthSpot.getTimeEnd() >= timeNow)) || (growthSpot.getTimeBegin() == growthSpot.getTimeEnd())) {
							/** ACTIVE GROWTH SPOT!!! */
							/** TODO: handle active growthSpot */
							if (growthSpot.getRoom().equals(GamePanel.gp.getPlayerEntity().getRoom())) {
								//TODO: handle relevant spots here (close enough and so on)
							} else {
								// wrong room
							}
						
						} else {
							/** the influence has not yet started, or has already ended
							 * -> do nothing, because the removal happens later */
						}
						
						/** finally remove the instantaneous influence (if applicable) or ended influence*/
						if (growthSpot.getTimeBegin() == growthSpot.getTimeEnd()) {
							/** instantaneous influence */
							deleteIndex[index] = 1;
						} else if (growthSpot.getTimeEnd() < timeNow) {
							/** ended continuous influence */
							deleteIndex[index] = 1;
						}
					}
					
					index += 1; // index of the next iterator element 
				}
				/** delete the invalid areaInfluences */
				for (int i = deleteIndex.length-1; i>= 0; i--) {
					if (deleteIndex[i] == 1) {
						tempGrowthSpotList.remove(tempIDList.get(i));
						tempIDList.remove(i);
					}
				}
				/** update the AreaInfluenceList of the AreaInfluenceManager */
				GrowthSpotManager.growthSpotList = tempGrowthSpotList;
				GrowthSpotManager.idList = tempIDList;

				
				if (GrowthSpotManager.growthSpotList.size() <= 0) {
					GrowthSpotManager.setMaxIDValue(BigInteger.valueOf(0));
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
