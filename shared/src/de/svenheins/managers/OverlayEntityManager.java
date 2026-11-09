/* Copyright (C) Sven Heins - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * author: Sven Heins
 * date: 05.07.2015
 */
package de.svenheins.managers;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import de.svenheins.main.EntityStates;
import de.svenheins.main.GameStates;
import de.svenheins.objects.Entity;
import de.svenheins.objects.LocalObject;
import de.svenheins.objects.TileSet;
import de.svenheins.objects.ZIndexObjectComparator;

public class OverlayEntityManager {
	public static HashMap<BigInteger, Entity> entityList = new HashMap<BigInteger, Entity>();
//	public static List<Entity> sortedEntityList;
	public static Comparator<LocalObject> comparator = new ZIndexObjectComparator();
	
	public static List<BigInteger> idList = new ArrayList<BigInteger>();
	
	public static void remove(BigInteger index) throws IllegalArgumentException {
//		if(entityList.get(index) instanceof AlienEntity) AlienEntity.aliens--;
		try {
			entityList.remove(index);
			idList.remove(index);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
		
	}
	
	public static void init () {
		BigInteger runningID = BigInteger.valueOf(0);
		TileSet entityTileSetCloud = TileSetManager.manager.getTileSet(GameStates.cloudNameOverlay);
		
		Random rand = new Random();
		
		for (int i = 0; i < 30; i++) {	
//			System.out.println(entityTileSet.getTileImage(0).getHeight());
			Entity entity = new Entity(entityTileSetCloud, "entity", "", runningID,rand.nextFloat()*(GameStates.width+1*entityTileSetCloud.getTileImage(0).getWidth())-1*entityTileSetCloud.getTileImage(0).getWidth(),rand.nextFloat()*(GameStates.height+1*entityTileSetCloud.getTileImage(0).getHeight())-1*entityTileSetCloud.getTileImage(0).getHeight(), GameStates.animationDelay); //rand.nextFloat()*(GameStates.height+entityTileSet.getTileImage(0).getHeight())-entityTileSet.getTileImage(0).getHeight(), GameStates.animationDelay);
			entity.setMovement(0, GameStates.overlayCloudSpeed);
			entity.setContinuousState(EntityStates.STANDING);
			entity.determineAnimation();
			OverlayEntityManager.add(entity);
			runningID = runningID.add(BigInteger.valueOf(1));
		}
		
	}
	
	
	public static boolean add(Entity entity) {
		if (entityList.containsKey(entity.getId())) {
			return false;
		} else {
			entityList.put(entity.getId(), entity);
			idList.add(entity.getId());
			return true;
		}
	}
	
	public static Entity get(BigInteger index){
		try {
			return (Entity) entityList.get(index);
//			return (Entity) entityList.values().toArray()[index];
		}
		catch(IndexOutOfBoundsException e){
			//System.out.println("Null-Object returned");
			return null;
		}
		
	}
	
	public static boolean overwrite(Entity entity){
		if (!entityList.containsKey(entity.getId())) {
			/** do nothing if key is not yet set*/
			return false;
		} else {
			entityList.put(entity.getId(), entity);
			idList.add(entity.getId());
			return true;
		}
	}
	
//	public static Entity getSorted(int index){
//		try {
//			return sortedEntityList.get(index);
//		}
//		catch(IndexOutOfBoundsException e){
//			//System.out.println("Null-Object returned");
//			return null;
//		}
//		
//	}
	
	public static int size(){
		return entityList.size();
	}
	
//	public static int sizeSorted(){
//		return sortedEntityList.size();
//	}
	
	public static boolean contains(Entity entity) {
		return entityList.containsValue(entity);
	}
	
//	public static void sortZIndex() {
//		sortedEntityList = new ArrayList<Entity>(0);
//		sortedEntityList.addAll(entityList);
//		try {
//			java.util.Collections.sort(sortedEntityList, comparator);
//		} catch (NoSuchElementException e) {
//			// do nothing
//		}
//	}

	/** update the Entity with the ID objectId */
	public static void updateEntity(BigInteger objectId, float objectX,
			float objectY, float objectMX, float objectMY) {
		Entity entity = EntityManager.entityList.get(objectId);
		if (entity != null) {
			entity.setX(objectX);
			entity.setY(objectY);
			entity.setMovement(objectMX, objectMY);
			EntityManager.entityList.put(objectId, entity);
		}
//		for (int i = 0; i<EntityManager.entityList.size(); i++) {
//			if (EntityManager.get(i).getId() == objectId) {
//				EntityManager.get(i).setX(objectX);
//				EntityManager.get(i).setY(objectY);
//				EntityManager.get(i).setMovement(objectMX, objectMY);
//				
//			}
//		}
	}
	
	
	/** compare the idList with a given int[] and return the difference */
	public static void createEntitiesFromArray(Entity[] entityArray) {
		if(entityArray.length > 0) {
			for (int i =0; i< entityArray.length; i++) {
				if (entityList.containsKey(entityArray[i].getId()) ) System.out.println("Doppelte ID: " + entityArray[i].getId());
				entityList.put(entityArray[i].getId(), entityArray[i]);
				idList.add(entityArray[i].getId());
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
	
	public static void emptyAll() {
		entityList = new HashMap<BigInteger, Entity>();
//		sortedEntityList = new ArrayList<Entity>();
		idList = new ArrayList<BigInteger>();
	}
}
