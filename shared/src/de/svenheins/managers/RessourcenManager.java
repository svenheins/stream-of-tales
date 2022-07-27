package de.svenheins.managers;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.ArrayList;

import de.svenheins.main.GameStates;

public class RessourcenManager {

	public static List<String> svgFiles = new ArrayList<String>(); 
	public static List<String> imageFiles = new ArrayList<String>();
	
	
	
	public static boolean addSVG(String  str) {
		if (svgFiles.contains(str)) {
			return false;
		} else {
			svgFiles.add(str);
			return true;
		}
	}
	
	public static boolean containsSVG( String str) {
		if (svgFiles.contains(str)) {
			return true;
		} else {
			return false;
		}
	}
	
	public static boolean addImage(String  str) {
		if (imageFiles.contains(str)) {
			return false;
		} else {
			imageFiles.add(str);
			return true;
		}
	}
	
	public static boolean containsImage( String str) {
		if (imageFiles.contains(str)) {
			return true;
		} else {
			return false;
		}
	}
}
