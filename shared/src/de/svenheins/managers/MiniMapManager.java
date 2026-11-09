/* Copyright (C) Sven Heins - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * author: Sven Heins
 * date: 05.07.2015
 */
package de.svenheins.managers;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.imageio.ImageIO;

import de.svenheins.functions.MyUtil;
import de.svenheins.main.GameStates;

public class MiniMapManager {

	public static MiniMapManager manager = new MiniMapManager();
	private HashMap<String, BufferedImage> map = new HashMap<String, BufferedImage>();
	
	
	public BufferedImage getMiniMap(String src) {
		if(map.get(src) != null) return map.get(src);
		else {
			BufferedImage bufferedImage;
			try {
				bufferedImage = ImageIO.read(this.getClass().getResource(GameStates.resourcePath+"images/"+src));
				map.put(src, bufferedImage);
				return bufferedImage;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
			
		}
	}
	
	/** get one file by other folderpath than standard */
	public BufferedImage getExternalMiniMap(String folderPath, String src) {
		if(map.get(src) != null) return map.get(src);
		else {
			BufferedImage bufferedImage;
			try {
				bufferedImage = ImageIO.read(new File(folderPath+src));
				map.put(src, bufferedImage);
				System.out.println("image added: "+src);
				return bufferedImage;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
			
		}
	}
	
	/** first init of external images */
	public void initExternalMiniMaps(String folderPath) {
		ArrayList<String> listOfFiles = new ArrayList<String>();
		File folder = new File(folderPath);
		listOfFiles = MyUtil.listFilesForFolder(folder);
		for (String imageFile: listOfFiles) {
			this.getExternalMiniMap(folderPath, imageFile);
		}
	}
	
	/** first init of external images */
	public ArrayList<String> listExternalMiniMap(String folderPath) {
		ArrayList<String> listOfFiles = new ArrayList<String>();
		File folder = new File(folderPath);
		listOfFiles = MyUtil.listFilesForFolder(folder);
		return listOfFiles;
	}
	
	/** add texture only if there is no texture under the given key */
	public boolean addMiniMap(String name, BufferedImage image) {
		if (map.containsKey(name)) {
			return false;
		} else
		{
			map.put(name, image);
			return true;
		}
	}
	
	/** overwrite, no matter what is saved at the given key */
	public void overwriteMiniMap(String name, BufferedImage image) {
		map.put(name, image);
	}
	
	/** return array of texture names */
	public String[] getMiniMapNames() {
		int lengthOfManager = map.size();
//		System.out.println("length of textureManager: "+ lengthOfManager);
		String[] textureNames = new String[lengthOfManager];
		int index = 0;
		for (String name: map.keySet()) {
			textureNames[index] = name;
			index++;
		}
		return textureNames;
	}
	
	/** return size of manager */
	public int getSize() {
		return map.size();
	}
	
	
	/** return value at position */
	public Color getColor(String name, int x, int y) {
		int color = map.get(name).getRGB(x, y);
		return new Color((color & 0xff0000) >> 16, (color & 0xff00) >> 8, color & 0xff,(color & 0xff000000) >>> 24);
	}
	
	/** return width */
	public int getWidth(String name) {
		return map.get(name).getWidth();
	}

	/** return height */
	public int getHeight(String name) {
		return map.get(name).getHeight();
	}
}
