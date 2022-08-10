package de.svenheins.managers;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.svenheins.main.Priority;
import de.svenheins.objects.AreaInfluence;
import de.svenheins.objects.items.Item;

public class AreaInfluenceManager {
	public static HashMap<BigInteger, AreaInfluence> areaInfluenceList = new HashMap<BigInteger, AreaInfluence>();
	public static ArrayList<BigInteger> idList = new ArrayList<BigInteger>();
	private static BigInteger maxIDValue = BigInteger.valueOf(0);
	
//	public static void add(AreaInfluence areaInfluence) {
//		switch (areaInfluence.getPriority()) {
//		case LOW: 
//			areaInfluenceList.add(areaInfluence);
//			break;
//		case MEDIUM:
//			if (areaInfluenceList.size() > 0) {
//				/** add right before the first HIGH priority-element*/
//				int index = 0;
//				for (int i = 0 ; i < areaInfluenceList.size(); i++) {
//					index = i;
//					if (areaInfluenceList.get(i).getPriority() == Priority.LOW)
//						break;
//					else 
//						index = i+1;
//				}
//				areaInfluenceList.add(index, areaInfluence);
//			} else {
//				/** add the first element */
//				areaInfluenceList.add(areaInfluence);
//			}
//			break;
//		case HIGH:
//			addFirst(areaInfluence);
//			break;
//		default: {
//			System.out.println("ERROR: couldn't match Priority of the influenceArea!");
//		}
//		}
//	}
	
	public static boolean add(AreaInfluence areaInfluence) {
		if (areaInfluenceList.containsKey(areaInfluence.getId())) {
			return false;
		} else {
			switch (areaInfluence.getPriority()) {
			case LOW: 
				areaInfluenceList.put(areaInfluence.getId(), areaInfluence);
				idList.add(areaInfluence.getId());
//				areaInfluenceList.add(areaInfluence);
				break;
			case MEDIUM:
				if (areaInfluenceList.size() > 0) {
					/** add right before the first HIGH priority-element*/
					int index = 0;

//					for (int i = 0 ; i < areaInfluenceList.size(); i++) {
//						index = i;
//						if (areaInfluenceList.get(i).getPriority() == Priority.LOW)
//							break;
//						else 
//							index = i+1;
//					}
					
					int runIndex = 0;
					
					for (BigInteger tempID : idList) {
//					for (int i = 0 ; i < areaInfluenceList.size(); i++) {
						index = runIndex;
						if (areaInfluenceList.get(tempID).getPriority() == Priority.LOW)
							break;
						else 
							index = runIndex+1;
						runIndex += 1;
					}
					areaInfluenceList.put(areaInfluence.getId(), areaInfluence);
					idList.add(index, areaInfluence.getId());
//					areaInfluenceList.add(index, areaInfluence);
				} else {
					/** add the first element */
//					areaInfluenceList.add(areaInfluence);
					areaInfluenceList.put(areaInfluence.getId(), areaInfluence);
					idList.add(areaInfluence.getId());
				}
				break;
			case HIGH:
				addFirst(areaInfluence);
				break;
			default: {
				System.out.println("ERROR: couldn't match Priority of the influenceArea!");
				return false;
				}
			}
			
			
//			areaInfluenceList.put(areaInfluence.getId(), areaInfluence);
//			idList.add(areaInfluence.getId());
			if (maxIDValue.compareTo(areaInfluence.getId()) < 0) {
				maxIDValue = areaInfluence.getId();
			}
			return true;
		}
	}
	
	public static void addFirst(AreaInfluence areaInfluence) {
		areaInfluenceList.put(areaInfluence.getId(), areaInfluence);
		idList.add(0, areaInfluence.getId());
//		areaInfluenceList.add(0, areaInfluence);
	}
	
	
	public static void remove(BigInteger id) throws IllegalArgumentException {
		try {
			areaInfluenceList.remove(id);
			idList.remove(id);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
		
	}
	
	public static AreaInfluence get(BigInteger id) {
		try {
			return areaInfluenceList.get(id);
		}
		catch(IndexOutOfBoundsException e){
			System.out.println(e);
			return null;
		}
	}
	
	public static boolean overwrite(AreaInfluence areaInfluence){
		if (!areaInfluenceList.containsKey(areaInfluence.getId())) {
			/** do nothing if key is not yet set*/
			return false;
		} else {
			areaInfluenceList.put(areaInfluence.getId(), areaInfluence);
			idList.add(areaInfluence.getId());
			return true;
		}
	}
	
	public static boolean contains(AreaInfluence areaInfluence) {
		return areaInfluenceList.containsValue(areaInfluence);
	}
	
	public static int size() {
		return areaInfluenceList.size();
	}
	
	public static void clear() {
		areaInfluenceList.clear();
		idList.clear();
	}

	public static BigInteger getMaxIDValue() {
		return maxIDValue;
	}

	public static void setMaxIDValue(BigInteger maxIDValue) {
		AreaInfluenceManager.maxIDValue = maxIDValue;
	}
}
