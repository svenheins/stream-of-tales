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


public class MainMenuEntityManager{
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
		TileSet entityTileSet = TileSetManager.manager.getTileSet(GameStates.nameDarkWolf);
		TileSet entityTileSetCloud = TileSetManager.manager.getTileSet(GameStates.cloudName);
		TileSet entityTileSetStarBlue = TileSetManager.manager.getTileSet(GameStates.starBlueName);
		TileSet entityTileSetStarBlue3 = TileSetManager.manager.getTileSet(GameStates.starBlue3Name);
		TileSet entityTileSetStarBlue4 = TileSetManager.manager.getTileSet(GameStates.starBlue4Name);
		TileSet entityTileSetStarBlue5 = TileSetManager.manager.getTileSet(GameStates.starBlue5Name);
		TileSet entityTileSetStarBlueBig = TileSetManager.manager.getTileSet(GameStates.starBlueBigName);
		TileSet entityTileSetStarBlueBig2 = TileSetManager.manager.getTileSet(GameStates.starBlueBig2Name);
		TileSet entityTileSetStarBlueBig3 = TileSetManager.manager.getTileSet(GameStates.starBlueBig3Name);
		TileSet entityTileSetStarBlueBig4 = TileSetManager.manager.getTileSet(GameStates.starBlueBig4Name);
		TileSet entityTileSetWolf = TileSetManager.manager.getTileSet(GameStates.nameWolf);
//		TileSet entityTileSet2 = TileSetManager.manager.getTileSet(GameStates.cloudName);
		TileSet entityTileSetSky = TileSetManager.manager.getTileSet(GameStates.skyName);
		
		Random rand = new Random();
		
		Entity entitySky = new Entity(entityTileSetSky, "sky", "", runningID, 0,0, GameStates.animationDelay); //rand.nextFloat()*(GameStates.height+entityTileSet.getTileImage(0).getHeight())-entityTileSet.getTileImage(0).getHeight(), GameStates.animationDelay);
		entitySky.setMovement(0, 0);
		entitySky.setContinuousState(EntityStates.WALKING);
		entitySky.determineAnimation();
		MainMenuEntityManager.add(entitySky);
		runningID = runningID.add(BigInteger.valueOf(1));
		
//		for (int i = 0; i < 200; i++) {	
////			System.out.println(entityTileSet.getTileImage(0).getHeight());
//			Entity entity = new Entity(entityTileSet, "entity", "", runningID, GameStates.width/2,-GameStates.height-6*entityTileSet.getTileImage(0).getHeight()+rand.nextFloat()*(GameStates.height+4*entityTileSet.getTileImage(0).getHeight()), GameStates.animationDelay); //rand.nextFloat()*(GameStates.height+entityTileSet.getTileImage(0).getHeight())-entityTileSet.getTileImage(0).getHeight(), GameStates.animationDelay);
//			entity.setMovement(0, GameStates.introSpeedMainMenu);
//			entity.setContinuousState(EntityStates.WALKING);
//			entity.determineAnimation();
//			MainMenuEntityManager.add(entity);
//			runningID = runningID.add(BigInteger.valueOf(1));
//		}
//		for (int i = 0; i < 10; i++) {	
////			System.out.println(entityTileSet.getTileImage(0).getHeight());
//			Entity entity = new Entity(entityTileSetWolf, "entity", "", runningID, GameStates.width/2,-GameStates.height-6*entityTileSetWolf.getTileImage(0).getHeight()+rand.nextFloat()*(GameStates.height+4*entityTileSetWolf.getTileImage(0).getHeight()), GameStates.animationDelay); //rand.nextFloat()*(GameStates.height+entityTileSet.getTileImage(0).getHeight())-entityTileSet.getTileImage(0).getHeight(), GameStates.animationDelay);
//			entity.setMovement(0, GameStates.introSpeedMainMenu);
//			entity.setContinuousState(EntityStates.WALKING);
//			entity.determineAnimation();
//			MainMenuEntityManager.add(entity);
//			runningID = runningID.add(BigInteger.valueOf(1));
//		}
		for (int i = 0; i < 30; i++) {	
//			System.out.println(entityTileSet.getTileImage(0).getHeight());
			Entity entity = new Entity(entityTileSetCloud, "entity", "", runningID, GameStates.width/2,-GameStates.height-6*entityTileSetCloud.getTileImage(0).getHeight()+rand.nextFloat()*(GameStates.height+4*entityTileSetCloud.getTileImage(0).getHeight()), GameStates.animationDelay); //rand.nextFloat()*(GameStates.height+entityTileSet.getTileImage(0).getHeight())-entityTileSet.getTileImage(0).getHeight(), GameStates.animationDelay);
			entity.setMovement(0, GameStates.introSpeedMainMenu);
			entity.setContinuousState(EntityStates.STANDING);
			entity.determineAnimation();
			MainMenuEntityManager.add(entity);
			runningID = runningID.add(BigInteger.valueOf(1));
		}
		for (int i = 0; i <40; i++) {	
//			System.out.println(entityTileSet.getTileImage(0).getHeight());
			Entity entity;
			float dice = rand.nextFloat();
			if (dice > 0.75) entity = new Entity(entityTileSetStarBlue, "starBlue", "", runningID, GameStates.width/2,-GameStates.height-6*entityTileSetStarBlue.getTileImage(0).getHeight()+rand.nextFloat()*(GameStates.height+4*entityTileSetStarBlue.getTileImage(0).getHeight()), 175);// GameStates.animationDelay); 
			else if (dice > 0.5) entity = new Entity(entityTileSetStarBlue3, "starBlue", "", runningID, GameStates.width/2,-GameStates.height-6*entityTileSetStarBlue.getTileImage(0).getHeight()+rand.nextFloat()*(GameStates.height+4*entityTileSetStarBlue.getTileImage(0).getHeight()), 175);//GameStates.animationDelay);
			else if (dice > 0.25) entity = new Entity(entityTileSetStarBlue4, "starBlue", "", runningID, GameStates.width/2,-GameStates.height-6*entityTileSetStarBlue.getTileImage(0).getHeight()+rand.nextFloat()*(GameStates.height+4*entityTileSetStarBlue.getTileImage(0).getHeight()), 175);//GameStates.animationDelay);
			else entity = new Entity(entityTileSetStarBlue5, "starBlue", "", runningID, GameStates.width/2,-GameStates.height-6*entityTileSetStarBlue.getTileImage(0).getHeight()+rand.nextFloat()*(GameStates.height+4*entityTileSetStarBlue.getTileImage(0).getHeight()), 175);//GameStates.animationDelay);

			entity.setMovement(0, GameStates.introSpeedMainMenu);

			entity.setContinuousState(EntityStates.STANDING);
			entity.determineAnimation();
			long aniDif = (long) (rand.nextFloat()*3000);
			entity.getAnimation().setInstantOfAnimation(System.currentTimeMillis()+aniDif);
			MainMenuEntityManager.add(entity);
			runningID = runningID.add(BigInteger.valueOf(1));
		}
		for (int i = 0; i < 4; i++) {	
//			System.out.println(entityTileSet.getTileImage(0).getHeight());
			Entity entity;
			float dice = rand.nextFloat();
			if (dice > 0.75) entity = new Entity(entityTileSetStarBlueBig, "starBlue", "", runningID, GameStates.width/2,-GameStates.height-6*entityTileSetStarBlue.getTileImage(0).getHeight()+rand.nextFloat()*(GameStates.height+4*entityTileSetStarBlue.getTileImage(0).getHeight()), 175); 
			else if (dice > 0.5) entity = new Entity(entityTileSetStarBlueBig2, "starBlue", "", runningID, GameStates.width/2,-GameStates.height-6*entityTileSetStarBlue.getTileImage(0).getHeight()+rand.nextFloat()*(GameStates.height+4*entityTileSetStarBlue.getTileImage(0).getHeight()), 175);
			else if (dice > 0.25) entity = new Entity(entityTileSetStarBlueBig3, "starBlue", "", runningID, GameStates.width/2,-GameStates.height-6*entityTileSetStarBlue.getTileImage(0).getHeight()+rand.nextFloat()*(GameStates.height+4*entityTileSetStarBlue.getTileImage(0).getHeight()), 175);
			else entity = new Entity(entityTileSetStarBlueBig4, "starBlue", "", runningID, GameStates.width/2,-GameStates.height-6*entityTileSetStarBlue.getTileImage(0).getHeight()+rand.nextFloat()*(GameStates.height+4*entityTileSetStarBlue.getTileImage(0).getHeight()),175);// GameStates.animationDelay);

			entity.setMovement(0, GameStates.introSpeedMainMenu);

			entity.setContinuousState(EntityStates.STANDING);
			entity.determineAnimation();
			long aniDif = (long) (rand.nextFloat()*3000);
			entity.getAnimation().setInstantOfAnimation(System.currentTimeMillis()+aniDif);
			MainMenuEntityManager.add(entity);
			runningID = runningID.add(BigInteger.valueOf(1));
		}
		for (int i = 0; i < 100; i++) {	
//			System.out.println(entityTileSet.getTileImage(0).getHeight());
			Entity entity = new Entity(entityTileSetCloud, "cloud", "", runningID, rand.nextFloat()*GameStates.width, rand.nextFloat()*(GameStates.height+entityTileSetCloud.getTileImage(0).getHeight())-entityTileSetCloud.getTileImage(0).getHeight(), GameStates.animationDelay);
			if (entity.getX() > GameStates.width/2) {
				entity.setMovement(rand.nextFloat()*GameStates.introSpeedMainMenu, GameStates.introSpeedMainMenu);
			} else {
				entity.setMovement(-rand.nextFloat()*GameStates.introSpeedMainMenu, GameStates.introSpeedMainMenu);
			}
			
			entity.setContinuousState(EntityStates.STANDING);
			entity.determineAnimation();
			MainMenuEntityManager.add(entity);
			runningID = runningID.add(BigInteger.valueOf(1));
		}
		
	}
	
//	public static void remove(Entity entity) {
////		if(entity instanceof AlienEntity) AlienEntity.aliens--;
//		idList.remove(entity.getId());
//		entityList.remove(entity);
//		
//	}
	
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
