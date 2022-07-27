package de.svenheins.managers;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;

import de.svenheins.objects.LocalObject;
import de.svenheins.objects.Space;
import de.svenheins.objects.ZIndexObjectComparator;


public class SpaceManager{
	public static List<Space> spaceList = new ArrayList<Space>();
	public static List<Space> sortedSpaceList;
	public static Comparator<LocalObject> comparator = new ZIndexObjectComparator();
	
	public static List<Integer> idList = null;
	
	public static void remove(int index) {
		spaceList.remove(index);
	}
	
//	public static void remove(Space space) {
//		spaceList.remove(space);
//	}
	
	public static void add(Space space){
		spaceList.add(space);
		idList.add(space.getId());
	}
	
	public static Space get(int index){
		try {
			return spaceList.get(index);
		}
		catch(IndexOutOfBoundsException e){
			//System.out.println("Null-Object returned");
			return null;
		}
	}
	
	public static Space getSorted(int index){
		try {
			return sortedSpaceList.get(index);
		}
		catch(IndexOutOfBoundsException e){
			//System.out.println("Null-Object returned");
			return null;
		}	
	}
	
	public static int size(){
		return spaceList.size();
	}
	
	public static int sizeSorted(){
		return sortedSpaceList.size();
	}
	
	public static boolean contains(Space space) {
		return spaceList.contains(space);
	}
	
	public static void sortZIndex() {
		sortedSpaceList = new ArrayList<Space>(0);
		sortedSpaceList.addAll(spaceList);
		try {
			java.util.Collections.sort(sortedSpaceList, comparator);
		} catch (NoSuchElementException e) {
			// do nothing
		}
	}
	
	/** update the Entity with the ID objectId */
	public static void updateSpace(int objectId, double objectX,
			double objectY, double objectMX, double objectMY) {
		for (int i = 0; i<SpaceManager.spaceList.size(); i++) {
			if (SpaceManager.get(i).getId() == objectId) {
				SpaceManager.get(i).setX(objectX);
				SpaceManager.get(i).setY(objectY);
				SpaceManager.get(i).setMovement(objectMX, objectMY);
			}
		}
	}	
	
	/** compare the idList with a given int[] and return the difference */
	public static void createSpacesFromArray(Space[] spaceArray) {
		if(spaceArray.length > 0) {
			for (int i =0; i< spaceArray.length; i++) {
				spaceList.add(spaceArray[i]);
			}
		}
	}
}
