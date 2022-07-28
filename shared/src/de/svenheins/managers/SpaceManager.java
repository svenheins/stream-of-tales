package de.svenheins.managers;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.NoSuchElementException;

import de.svenheins.objects.Entity;
import de.svenheins.objects.LocalObject;
import de.svenheins.objects.Space;
import de.svenheins.objects.ZIndexObjectComparator;


public class SpaceManager{
	public static HashMap<BigInteger, Space> spaceList = new HashMap<BigInteger, Space>();
//	public static List<Space> sortedSpaceList;
	public static Comparator<LocalObject> comparator = new ZIndexObjectComparator();
	
	public static List<BigInteger> idList = new ArrayList<BigInteger>();
	
	public static void remove(BigInteger index) {
		idList.remove(index);
		spaceList.remove(index);
	}
	
//	public static void remove(Space space) {
//		spaceList.remove(space);
//	}
	
	public static boolean add(Space space){
		if (spaceList.containsKey(space.getId())) {
			return false;
		} else {
			spaceList.put(space.getId(), space);
			idList.add(space.getId());
//			System.out.println("SpaceManager added new space: "+ space.getId());
			return true;
		}
	}
	
	public static Space get(BigInteger index){
		try {
			return (Space) spaceList.get(index);
//			return (Space) spaceList.values().toArray()[index];
		}
		catch(IndexOutOfBoundsException e){
			//System.out.println("Null-Object returned");
			return null;
		}
	}
	
	public static boolean overwrite(Space space){
		if (!spaceList.containsKey(space.getId())) {
			/** do nothing if key is not yet set*/
			return false;
		} else {
			spaceList.put(space.getId(), space);
//			idList.add(space.getId());
			return true;
		}
	}
	
//	public static Space getSorted(int index){
//		try {
//			return sortedSpaceList.get(index);
//		}
//		catch(IndexOutOfBoundsException e){
//			//System.out.println("Null-Object returned");
//			return null;
//		}	
//	}
	
	public static int size(){
		return spaceList.size();
	}
	
//	public static int sizeSorted(){
//		return sortedSpaceList.size();
//	}
	
	public static boolean contains(Space space) {
		return spaceList.containsValue(space);
	}
	
//	public static void sortZIndex() {
//		sortedSpaceList = new ArrayList<Space>(0);
//		sortedSpaceList.addAll(spaceList);
//		try {
//			java.util.Collections.sort(sortedSpaceList, comparator);
//		} catch (NoSuchElementException e) {
//			// do nothing
//		}
//	}
	
	/** update the Entity with the ID objectId */
	public static void updateSpace(BigInteger objectId, float objectX,
			float objectY, float objectMX, float objectMY) {
		Space space = SpaceManager.spaceList.get(objectId);
		if (space != null && space.getUpdateByServer()) {
			space.setX((int)objectX);
			space.setY((int)objectY);
//			space.setPolyX((int)objectX);
//			space.setPolyY((int)objectY);
			space.setAllXY(objectX, objectY);
			space.setMovement(objectMX, objectMY);
			SpaceManager.spaceList.put(objectId, space);
		}
//		for (int i = 0; i<SpaceManager.spaceList.size(); i++) {
//			if (SpaceManager.get(i).getId() == objectId) {
//				SpaceManager.get(i).setX(objectX);
//				SpaceManager.get(i).setY(objectY);
//				SpaceManager.get(i).setMovement(objectMX, objectMY);
//			}
//		}
	}	
	
	/** compare the idList with a given int[] and return the difference */
	public static void createSpacesFromArray(Space[] spaceArray) {
		if(spaceArray.length > 0) {
			for (int i =0; i< spaceArray.length; i++) {
				spaceList.put(spaceArray[i].getId(), spaceArray[i]);
				if (!idList.contains(spaceArray[i].getId())) idList.add(spaceArray[i].getId());
			}
		}
	}
	
	public static void emptyAll() {
		spaceList = new HashMap<BigInteger, Space>();
//		sortedSpaceList = new ArrayList<Space>();
		idList = new ArrayList<BigInteger>();
	}
	
	/** get Maximum of ids */
	public static BigInteger getMax(){
		BigInteger tempMax = BigInteger.valueOf(-1);
		for(BigInteger bId : idList) {
			/** if we found a new maximum replace the old one **/
			if (bId.compareTo(tempMax) > 0) {
				tempMax = bId;
			}
		}
		return tempMax;
	}
}
