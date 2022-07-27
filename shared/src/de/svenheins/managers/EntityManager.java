package de.svenheins.managers;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;

import de.svenheins.objects.Entity;
import de.svenheins.objects.LocalObject;
import de.svenheins.objects.ZIndexObjectComparator;


public class EntityManager{
	public static List<Entity> entityList = new ArrayList<Entity>();
	public static List<Entity> sortedEntityList;
	public static Comparator<LocalObject> comparator = new ZIndexObjectComparator();
	
	public static List<Integer> idList = null;
	
	public static void remove(int index) {
//		if(entityList.get(index) instanceof AlienEntity) AlienEntity.aliens--;
		entityList.remove(index);
		idList.remove(index);
	}
	
//	public static void remove(Entity entity) {
////		if(entity instanceof AlienEntity) AlienEntity.aliens--;
//		idList.remove(entity.getId());
//		entityList.remove(entity);
//		
//	}
	
	public static void add(Entity entity){
		entityList.add(entity);
		idList.add(entity.getId());
	}
	
	public static Entity get(int index){
		try {
			return entityList.get(index);
		}
		catch(IndexOutOfBoundsException e){
			//System.out.println("Null-Object returned");
			return null;
		}
		
	}
	
	public static Entity getSorted(int index){
		try {
			return sortedEntityList.get(index);
		}
		catch(IndexOutOfBoundsException e){
			//System.out.println("Null-Object returned");
			return null;
		}
		
	}
	
	public static int size(){
		return entityList.size();
	}
	
	public static int sizeSorted(){
		return sortedEntityList.size();
	}
	
	public static boolean contains(Entity entity) {
		return entityList.contains(entity);
	}
	
	public static void sortZIndex() {
		sortedEntityList = new ArrayList<Entity>(0);
		sortedEntityList.addAll(entityList);
		try {
			java.util.Collections.sort(sortedEntityList, comparator);
		} catch (NoSuchElementException e) {
			// do nothing
		}
	}

	/** update the Entity with the ID objectId */
	public static void updateEntity(int objectId, double objectX,
			double objectY, double objectMX, double objectMY) {
		for (int i = 0; i<EntityManager.entityList.size(); i++) {
			if (EntityManager.get(i).getId() == objectId) {
				EntityManager.get(i).setX(objectX);
				EntityManager.get(i).setY(objectY);
				EntityManager.get(i).setMovement(objectMX, objectMY);
			}
		}
	}
	
	
	/** compare the idList with a given int[] and return the difference */
	public static void createEntitiesFromArray(Entity[] entityArray) {
		if(entityArray.length > 0) {
			for (int i =0; i< entityArray.length; i++) {
				entityList.add(entityArray[i]);
			}
		}
		//TODO: correct implementation
//		if (idList != null) {
//			for(int i = 0; i< idArray.length; i++) {
//				if (idList.contains(idArray[i])){
//					// OK
//					//System.out.println("aktuelle ids " + idArray[i]);
//				} else
//				{
//					/** add entity, depending on id */
//					idList.add(idArray[i]);
//					Entity entity = new Entity("eye.png", idArray[i], 0, 0);
//					entityList.add(entity);
//				}
//			}
//		} else {
//			/** here we got an empty idList, so we add whole the int[] */
//			idList = new ArrayList<Integer>();
//			for(int i = 0; i< idArray.length; i++) {
//				/** add entity, depending on id */
//				idList.add(idArray[i]);
//				Entity entity = new Entity("eye.png", idArray[i], 0, 0);
//				entityList.add(entity);
//			}
//		}
		/** now remove the deleted entities */
//		List<Entity> copyList = entityList;
//		for(int i = 0; i < entityList.size(); i++) {
//			
//		}
//		return new int[]{0};
	}
	
}
