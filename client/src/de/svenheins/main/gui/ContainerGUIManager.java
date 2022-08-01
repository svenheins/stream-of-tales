package de.svenheins.main.gui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ContainerGUIManager {
	public static HashMap<String, ContainerGUI> containerGUIList = new HashMap<String, ContainerGUI>();
	public static List<String> idList = new ArrayList<String>();
	
	
	public static void remove(String index) throws IllegalArgumentException {
		try {
			containerGUIList.remove(index);
			idList.remove(index);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
		
	}
	
	public static boolean add(ContainerGUI containerGUI) {
		if (containerGUIList.containsKey(containerGUI.getName())) {
			return false;
		} else {
			containerGUIList.put(containerGUI.getName(), containerGUI);
			idList.add(containerGUI.getName());
			return true;
		}
	}
	
	public static ContainerGUI get(String index){
		try {
			return (ContainerGUI) containerGUIList.get(index);
		}
		catch(IndexOutOfBoundsException e){
			return null;
		}
		
	}
	
	public static boolean overwrite(ContainerGUI containerGUI){
		if (!containerGUIList.containsKey(containerGUI.getName())) {
			/** do nothing if key is not yet set*/
			return false;
		} else {
			containerGUIList.put(containerGUI.getName(), containerGUI);
			idList.add(containerGUI.getName());
			return true;
		}
	}
	
	public static int size(){
		return containerGUIList.size();
	}
	
	public static boolean contains(ContainerGUI containerGUI) {
		return containerGUIList.containsValue(containerGUI);
	}
	
	
	public static void clear() {
		containerGUIList.clear();
		idList.clear();
	}
}
