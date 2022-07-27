package de.svenheins.managers;
import java.util.ArrayList;
import java.util.List;

import de.svenheins.objects.Entity;


public class PlayerManager{
	public static List<Entity> playerList = new ArrayList<Entity>();
	
	public void remove(int index) {
		playerList.remove(index);
	}
	
	public static void remove(Entity entity) {
		playerList.remove(entity);
	}
	
	public static void add(Entity entity){
		playerList.add(entity);
	}
	
	public static Entity get(int index){
		try {
			return playerList.get(index);
		}
		catch(IndexOutOfBoundsException e){
			//System.out.println("Null-Object returned");
			return null;
		}
	}
	
	public static int size(){
		return playerList.size();
	}
	
	public static boolean contains(Entity entity) {
		return playerList.contains(entity);
	}
}
