package de.svenheins.managers;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;

import de.svenheins.objects.AlienEntity;
import de.svenheins.objects.Entity;
import de.svenheins.objects.LocalObject;
import de.svenheins.objects.ZIndexObjectComparator;


public class EnemyManager{
	public static List<Entity> enemyList = new ArrayList<Entity>();
	public static List<Entity> sortedEnemyList;
	public static Comparator<LocalObject> comparator = new ZIndexObjectComparator();
	
	public static void remove(int index) {
		if(enemyList.get(index) instanceof AlienEntity) AlienEntity.aliens--;
		enemyList.remove(index);
	}
	
	public static void remove(Entity entity) {
		if(entity instanceof AlienEntity) AlienEntity.aliens--;
		enemyList.remove(entity);
	}
	
	public static void add(Entity entity){
		enemyList.add(entity);
	}
	
	public static Entity get(int index){
		try {
			return enemyList.get(index);
		}
		catch(IndexOutOfBoundsException e){
			//System.out.println("Null-Object returned");
			return null;
		}
		
	}
	
	public static Entity getSorted(int index){
		try {
			return sortedEnemyList.get(index);
		}
		catch(IndexOutOfBoundsException e){
			//System.out.println("Null-Object returned");
			return null;
		}
		
	}
	
	public static int size(){
		return enemyList.size();
	}
	
	public static int sizeSorted(){
		return sortedEnemyList.size();
	}
	
	public static boolean contains(Entity entity) {
		return enemyList.contains(entity);
	}
	
	public static void sortZIndex() {
		sortedEnemyList = new ArrayList<Entity>(0);
		sortedEnemyList.addAll(enemyList);
		try {
			java.util.Collections.sort(sortedEnemyList, comparator);
		} catch (NoSuchElementException e) {
			// do nothing
		}
	}
}
