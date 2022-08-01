package de.svenheins.managers;

import java.util.ArrayList;
import java.util.HashMap;

import de.svenheins.main.GameStates;
import de.svenheins.objects.InteractionArea;
import de.svenheins.objects.InteractionTile;
import de.svenheins.objects.WorldLatticePosition;

public class InteractionManager {
	public static HashMap<WorldLatticePosition, InteractionTile> interactionList = new HashMap<WorldLatticePosition, InteractionTile>();
	public static ArrayList<WorldLatticePosition> idList = new ArrayList<WorldLatticePosition>();
	
	public static int[] interact(InteractionTile iTile) {
		int[] retValues;

		if (interactionList.containsKey(iTile.getPosition())) {
			interactionList.get(iTile.getPosition()).interact(iTile.getValues());
			retValues = interactionList.get(iTile.getPosition()).getValues();
		} else {
			interactionList.put(iTile.getPosition(), iTile);
			idList.add(iTile.getPosition());
			retValues = iTile.getValues();
		}
		return retValues;		
	}
	
	
	
	public static int[] interact(InteractionArea interactionArea, WorldLatticePosition position) {
		int[] retValues;
		if (interactionList.containsKey(position)) {
			interactionList.get(position).interact(interactionArea.getValues());
			retValues = interactionList.get(position).getValues();
		} else {
			InteractionTile iTile = new InteractionTile(position);
			iTile.setValues(interactionArea.getValues());
			interactionList.put(position, iTile);
			idList.add(position);
//			System.out.println("interactionList.size = "+interactionList.size());
//			System.out.println("idList size: "+idList.size());
			
			retValues = iTile.getValues();
		}
		return retValues;
	}

	
	/** update depending on time (old interactions will be deleted) */
	public static void update() {
		long timeStamp = System.currentTimeMillis();
		WorldLatticePosition position;
		for (int i = idList.size()-1; i >= 0; i--) {
			position = idList.get(i);
			if (timeStamp- interactionList.get(position).getCreationTime() > GameStates.interactionMaximumLifeDuration) {
				/** delete interaction */
				remove(position);
			}
		}
//		System.out.println("Length of InteractionList = "+interactionList.size());
	}
	
	public static void remove(WorldLatticePosition position) throws IllegalArgumentException {
		try {
			interactionList.remove(position);
			idList.remove(position);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
	}
	
	public static boolean add(InteractionTile interactionTile) {
		if (interactionList.containsKey(interactionTile.getPosition())) {
			return false;
		} else {
			interactionList.put(interactionTile.getPosition(), interactionTile);
			idList.add(interactionTile.getPosition());
			return true;
		}
	}
	
	public static InteractionTile get(WorldLatticePosition position){
		try {
			return interactionList.get(position);
		}
		catch(IndexOutOfBoundsException e){
			return null;
		}
		
	}
	
	public static boolean overwrite(InteractionTile interactionTile){
		if (!interactionList.containsKey(interactionTile.getPosition())) {
			/** do nothing if key is not yet set*/
			return false;
		} else {
			interactionList.put(interactionTile.getPosition(), interactionTile);
			idList.add(interactionTile.getPosition());
			return true;
		}
	}
	
	public static int size(){
		return interactionList.size();
	}
	
	public static boolean contains(InteractionTile interactionTile) {
		return interactionList.containsValue(interactionTile);
	}
	
	public static void clear() {
		interactionList.clear();
		idList.clear();
	}
}
