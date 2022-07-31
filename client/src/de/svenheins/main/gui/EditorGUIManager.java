package de.svenheins.main.gui;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class EditorGUIManager {
	public static HashMap<String, EditorGUI> editorGUIList = new HashMap<String, EditorGUI>();
	public static List<String> idList = new ArrayList<String>();
	
	
	public static void remove(String index) throws IllegalArgumentException {
		try {
			editorGUIList.remove(index);
			idList.remove(index);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
		
	}
	
	public static boolean add(EditorGUI editorGUI) {
		if (editorGUIList.containsKey(editorGUI.getName())) {
			return false;
		} else {
			editorGUIList.put(editorGUI.getName(), editorGUI);
			idList.add(editorGUI.getName());
			return true;
		}
	}
	
	public static EditorGUI get(String index){
		try {
			return (EditorGUI) editorGUIList.get(index);
		}
		catch(IndexOutOfBoundsException e){
			return null;
		}
		
	}
	
	public static boolean overwrite(EditorGUI editorGUI){
		if (!editorGUIList.containsKey(editorGUI.getName())) {
			/** do nothing if key is not yet set*/
			return false;
		} else {
			editorGUIList.put(editorGUI.getName(), editorGUI);
			idList.add(editorGUI.getName());
			return true;
		}
	}
	
	public static int size(){
		return editorGUIList.size();
	}
	
	public static boolean contains(EditorGUI editorGUI) {
		return editorGUIList.containsValue(editorGUI);
	}
	
	public static void clear() {
		editorGUIList.clear();
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
