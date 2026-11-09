/* Copyright (C) Sven Heins - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * author: Sven Heins
 * date: 05.07.2015
 */
package de.svenheins.objects;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import de.svenheins.main.GameStates;
import de.svenheins.main.MapType;


public class MiniMap {
	private Color[][] map; // one Color[][] as the underground-Map
	
	public MiniMap(String src) {
		BufferedImage bufferedImage = null;
		map = null;
		try {
//			File file = new File(src);
//			if (file.exists()) {
//			bufferedImage = ImageIO.read(file);
			bufferedImage = ImageIO.read(this.getClass().getResource("/res/"+src));
			map = new Color[bufferedImage.getHeight()][bufferedImage.getWidth()];
			/** create the array of Colors, depending on the image at the folderPath\src-Image */
			for (int i = 0; i < bufferedImage.getHeight(); i++) {
				for (int j = 0; j < bufferedImage.getWidth(); j++) {
					int color = bufferedImage.getRGB(j, i);
					map[i][j] = new Color((color & 0xff0000) >> 16, (color & 0xff00) >> 8, color & 0xff,(color & 0xff000000) >>> 24);
				}
			}
	//			System.out.println("width = "+map[0].length + " height = "+map.length);
//			} else {
//				System.out.println("Error reading file: "+src);
//			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public MiniMap(String folderPath, String src) {
		BufferedImage bufferedImage = null;
		map = null;
		try {
			File file = new File(folderPath+src);
			if (file.exists()) {
			bufferedImage = ImageIO.read(file);
				map = new Color[bufferedImage.getHeight()][bufferedImage.getWidth()];
				/** create the array of Colors, depending on the image at the folderPath\src-Image */
				for (int i = 0; i < bufferedImage.getHeight(); i++) {
					for (int j = 0; j < bufferedImage.getWidth(); j++) {
						int color = bufferedImage.getRGB(j, i);
						map[i][j] = new Color((color & 0xff0000) >> 16, (color & 0xff00) >> 8, color & 0xff,(color & 0xff000000) >>> 24);
					}
				}
	//			System.out.println("width = "+map[0].length + " height = "+map.length);
			} else {
				System.out.println("Error reading file: "+folderPath+src);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public MiniMap() {
		map = new Color[10][10];
		for (int i = 0; i < 10; i++) {
			for (int j = 0; j < 10; j++) {
				map[i][j] = new Color(0,255,0);
			}
		}
	}
	
	public MiniMap(int multi) {
		map = new Color[10*multi][10*multi];
		for (int i = 0; i < 10*multi; i++) {
			for (int j = 0; j < 10*multi; j++) {
				map[i][j] = new Color(0,0,0);
			}
		}
	}
	
	/** getter*/
	public Color[][] getMap() {
		return this.map;
	}
	
	/** setter*/
	public void setMap(Color[][] map) {
		this.map = map;
	}
	
	/** width */
	public int getWidth() {
		if (map != null) {
			if (map[0] != null) {
				return map[0].length;
			} else {
				return -1;
			}
		} else {
			return -1;
		}
	}
	  
	/** height */
	public int getHeight() {
		if (map != null) {
			if (map[0] != null) {
				return map.length;
			} else {
				return -1;
			}
		} else {
			return -1;
		}
	}
	
	/** return value in [0,1] */
	public float getRed(int i, int j) {
		float retValue = -1.0f;
		if (map != null) {
			if (i >=0 && i < this.getHeight() && j >=0 && j < this.getWidth()) {
				if (map[i][j] != null) {
					retValue = (float) map[i][j].getRGBComponents(null)[0];
				}	
			}
		}
		return retValue;
	}
	
	/** return value in [0,1] */
	public float getGreen(int i, int j) {
		float retValue = -1.0f;
		if (map != null) {
			if (i >=0 && i < this.getHeight() && j >=0 && j < this.getWidth()) {
				if (map[i][j] != null) {
					retValue = (float) map[i][j].getRGBComponents(null)[1];
				}
				
			}
		}
		return retValue;
	}
	
	/** return value in [0,1] */
	public float getBlue(int i, int j) {
		float retValue = -1.0f;
		if (map != null) {
			if (i >=0 && i < this.getHeight() && j >=0 && j < this.getWidth()) {
				if (map[i][j] != null) {
					retValue = (float) map[i][j].getRGBComponents(null)[2];
				}
				
			}
		}
		return retValue;
	}
	
	/** return value in [0,1] */
	public float getAlpha(int i, int j) {
		float retValue = -1.0f;
		if (map != null) {
			if (i >=0 && i < this.getHeight() && j >=0 && j < this.getWidth()) {
				if (map[i][j] != null) {
					retValue = (float) map[i][j].getRGBComponents(null)[3];
				}
				
			}
		}
		return retValue;
	}
	
	/** image to dungeon Map */
	/** Here we define some functions, that enables us to take (nx3) x (mx3) images and get a dungeon map from that
	 * 
	 * 010
	 * 0L-  would correspond to ur connections
	 * 000
	 */
	public MapType getMapType(int x, int y) {
		MapType retValue = MapType.LURD;
		if (this.getWidth() % 3 == 0 && this.getHeight() % 3 == 0) {
			int xLoc = x*3;
			int yLoc = y*3;
			boolean p11 = (getBlue(xLoc, yLoc) == 0) && (getGreen(xLoc, yLoc) == 0) && (getRed(xLoc, yLoc) == 0); 
			
			xLoc = x*3;
			yLoc = y*3+1;
			boolean p12 = (getBlue(xLoc, yLoc) == 0) && (getGreen(xLoc, yLoc) == 0) && (getRed(xLoc, yLoc) == 0); 
			
//			System.out.println("blue="+getBlue(xLoc, yLoc)+" green="+getGreen(xLoc, yLoc)+" red="+getRed(xLoc, yLoc));
			xLoc = x*3 ;
			yLoc = y*3+ 2;
			boolean p13 = (getBlue(xLoc, yLoc) == 0) && (getGreen(xLoc, yLoc) == 0) && (getRed(xLoc, yLoc) == 0);
			
			xLoc = x*3+1;
			yLoc = y*3;
			boolean p21 = (getBlue(xLoc, yLoc) == 0) && (getGreen(xLoc, yLoc) == 0) && (getRed(xLoc, yLoc) == 0); 
			
			xLoc = x*3+1;
			yLoc = y*3+1;
			boolean p22 = (getBlue(xLoc, yLoc) == 0) && (getGreen(xLoc, yLoc) == 0) && (getRed(xLoc, yLoc) == 0); 
			
			xLoc = x*3+1;
			yLoc = y*3+2;
			boolean p23 = (getBlue(xLoc, yLoc) == 0) && (getGreen(xLoc, yLoc) == 0) && (getRed(xLoc, yLoc) == 0); 
			
			xLoc = x*3+2;
			yLoc = y*3;
			boolean p31 = (getBlue(xLoc, yLoc) == 0) && (getGreen(xLoc, yLoc) == 0) && (getRed(xLoc, yLoc) == 0); 
			xLoc = x*3+2;
			yLoc = y*3+1;
			boolean p32 = (getBlue(xLoc, yLoc) == 0) && (getGreen(xLoc, yLoc) == 0) && (getRed(xLoc, yLoc) == 0); 
			xLoc = x*3+2;
			yLoc = y*3+2;
			boolean p33 = (getBlue(xLoc, yLoc) == 0) && (getGreen(xLoc, yLoc) == 0) && (getRed(xLoc, yLoc) == 0); 
			
//			System.out.println("p11="+p11);
//			System.out.println("p12="+p12);
//			System.out.println("p13="+p13);
//			System.out.println("p21="+p21);
//			System.out.println("p22="+p22);
//			System.out.println("p23="+p23);
//			System.out.println("p31="+p31);
//			System.out.println("p32="+p32);
//			System.out.println("p33="+p33);
//			
			/** the middle pixel must be set else the field is empty*/
			if (p22) {
				if(p12&&p21&&p23&&p32) return MapType.LURD; //LURD
				/** 3 connections */
				if(p12&&p21&&p23&& !p32) return MapType.LUR;
				if(p12&&p21&&!p23&&p32) return MapType.LUD;
				if(p12&&!p21&&p23&&p32) return MapType.URD;
				if(!p12&&p21&&p23&&p32) return MapType.LRD;
				/** 2 connections */
				if(!p12&&!p21&&p23&&p32) return MapType.RD;
				if(!p12&&p21&&!p23&&p32) return MapType.LD;
				if(!p12&&p21&&p23&&!p32) return MapType.LR;
				if(p12&&p21&&!p23&&!p32) return MapType.LU;
				if(p12&&!p21&&p23&&!p32) return MapType.UR;
				if(p12&&!p21&&!p23&&p32) return MapType.UD;
				/** 1 connection */
				if(p12&&!p21&&!p23&&!p32) return MapType.U;
				if(!p12&&p21&&!p23&&!p32) return MapType.L;
				if(!p12&&!p21&&p23&&!p32) return MapType.R;
				if(!p12&&!p21&&!p23&&p32) return MapType.D;
			} else {
				return MapType.ZERO;
			}
		}
		return retValue;
	}
	
}
