package de.svenheins.main.gui;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PlayerListGUIManager {
	public static HashMap<String, PlayerListGUI> playerListGUIList = new HashMap<String, PlayerListGUI>();
	public static List<String> idList = new ArrayList<String>();
	
	
	public static void remove(String index) throws IllegalArgumentException {
		try {
			playerListGUIList.remove(index);
			idList.remove(index);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
		
	}
	
	public static boolean add(PlayerListGUI playerListGUI) {
		if (playerListGUIList.containsKey(playerListGUI.getName())) {
			return false;
		} else {
			playerListGUIList.put(playerListGUI.getName(), playerListGUI);
			idList.add(playerListGUI.getName());
			return true;
		}
	}
	
	public static PlayerListGUI get(String index){
		try {
			return (PlayerListGUI) playerListGUIList.get(index);
		}
		catch(IndexOutOfBoundsException e){
			return null;
		}
		
	}
	
	public static boolean overwrite(PlayerListGUI playerListGUI){
		if (!playerListGUIList.containsKey(playerListGUI.getName())) {
			/** do nothing if key is not yet set*/
			return false;
		} else {
			playerListGUIList.put(playerListGUI.getName(), playerListGUI);
			idList.add(playerListGUI.getName());
			return true;
		}
	}
	
	public static int size(){
		return playerListGUIList.size();
	}
	
	public static boolean contains(PlayerListGUI playerListGUI) {
		return playerListGUIList.containsValue(playerListGUI);
	}
	
	
	public static void clear() {
		playerListGUIList.clear();
		idList.clear();
	}
//	public static void mouseClick(Point p) {
//		for (String strName : idList) {
//			EditorGUI editorGUI = editorGUIList.get(strName);
//			if (editorGUI.contains(p)) {
//				this.setIntValue(editorGUI.getIntValue());
//				this.setStrValue(editorGUI.getStrValue());
//			}
//		}
//	}

}
