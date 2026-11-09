/* Copyright (C) Sven Heins - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * author: Sven Heins
 * date: 05.07.2015
 */
package de.svenheins.functions;

import java.awt.Color;
import java.awt.Point;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import de.svenheins.main.GameColors;
import de.svenheins.main.GameStates;
import de.svenheins.main.MiniMapColor;

public class MyUtil {
	public static
	<T extends Comparable<? super T>> List<T> asSortedList(Collection<T> c) {
	  List<T> list = new ArrayList<T>(c);
	  java.util.Collections.sort(list);
	  return list;
	}
	
	public static int[] niceColorGenerator() {
		int[] color = new int[3];//{firstStep, firstStep, firstStep};
//		int[] permutation = new int[classes];
		int spectre = GameColors.values().length;
		int chooseColor = new Random().nextInt(spectre);
//		for (int i = 0; i < spectre; i++) {
//			permutation[i] = i;
//		}
		float contrast = (float) Math.random()*0.75f+0.25f;
		color = initColor(GameColors.values()[chooseColor], contrast);
//		int minColor = MyMath.min(color);
//		int maxColor = MyMath.max(color);
//		minColor += (new Random().nextInt(steps))*((maxColor-minColor)/steps);
		
		return color;
	}
	
	private static int[] initColor(GameColors color, float contrast){
		int[] retColor = new int[]{0,0,0};
		switch (color) {
			case GREEN:
				retColor =  new int[]{(int) (55+(170-55)*(1-contrast)), 170, (int) (55+(170-55)*(1-contrast))};
				break;
			case BLUE:
				retColor =  new int[]{62, 62, 170};
				break;
//			case YELLOW:
//				retColor =  new int[]{170, 170, (int) (20+(170-20)*(1-contrast))};
//				break;
			case BROWN:
				retColor =  new int[]{170, (int) (96+(170-96)*(1-contrast)), (int) (170*(1-contrast))};
				break;
//			case RED:
//				retColor =  new int[]{170, (int) (20+(170-20)*(1-contrast)), (int) (20+(170-20)*(1-contrast))};
//				break;
			default: ;
		}
		return retColor;
	}
	
	/** copy a file */
	public static void copyFile(File sourceFile, File destFile) throws IOException {
	    if(!destFile.exists()) {
	        destFile.createNewFile();
	    }

	    FileChannel source = null;
	    FileChannel destination = null;

	    try {
	        source = new FileInputStream(sourceFile).getChannel();
	        destination = new FileOutputStream(destFile).getChannel();
	        destination.transferFrom(source, 0, source.size());
	    }
	    finally {
	        if(source != null) {
	            source.close();
	        }
	        if(destination != null) {
	            destination.close();
	        }
	    }
	}
	
//	/** list all files in a directory */
//	public static ArrayList<String> listFilesForFolderWithFolder(final File folder) {
//	    ArrayList<String> fileList = new ArrayList<String>();
//		for (final File fileEntry : folder.listFiles()) {
//	        if (fileEntry.isDirectory()) {
//	            listFilesForFolderWithFolder(fileEntry);
//	        } else {
//	        	fileList.add(folder+"/"+fileEntry.getName());
////	        	System.out.println(fileEntry.getName());
//	        }
//	    }
//		return fileList;
//	}
//
//	public static ArrayList<String> listFilesForFolderWithFolder(URL url) {
//		ArrayList<String> fileList = MyUtil.listFilesForFolderWithFolder(MyUtil.urlToFile(url));
//		return fileList;
//	}
	
	/** list all files in a directory */
	public static ArrayList<String> listFilesForFolder(final File folder) {
	    ArrayList<String> fileList = new ArrayList<String>();
		for (final File fileEntry : folder.listFiles()) {
	        if (fileEntry.isDirectory()) {
	            listFilesForFolder(fileEntry);
	        } else {
	        	fileList.add(fileEntry.getName());
//	        	System.out.println(fileEntry.getName());
	        }
	    }
		return fileList;
	}

	public static ArrayList<String> listFilesForFolder(URL url) {
		ArrayList<String> fileList = MyUtil.listFilesForFolder(MyUtil.urlToFile(url));
		return fileList;
	}

	static File urlToFile(URL url) {
        File file = null;
        try {
          file = new File(url.toURI());
        } catch(URISyntaxException e) {
          file = new File(url.getPath());
        }
//        System.out.println(file);
        return file;
    } 
	
	public static ArrayList<Point> unionListNoDuplicates(ArrayList<Point> aList, ArrayList<Point> bList) {
		ArrayList<Point> retList = aList;
		for (Point p : bList) {
			if (!retList.contains(p)) retList.add(p);
		}
		return retList;
	}
	
	/** creates am array of int values in the interval [min, max) -> max is not included in the interval*/
	public static int[] createIntArray(int size, int min, int max) {
		int[] retIntArray = new int[size];
		for (int i = 0; i < size; i++ ) {
			retIntArray[i] = (int)((Math.random()*(max-min))+min);
//			System.out.println("size = "+size+ " entry: "+retIntArray[i]);
		}
		return retIntArray;
	}
	
	/** creates int[] of x Values*/
	public static int[] createFullIntArrayWithOffsetX(int offset) {
		int[] retIntArray = new int[(GameStates.mapWidth/4)];
		for (int i = offset; i < GameStates.mapWidth; i=i+4 ) {
			retIntArray[i/4] = (int)((i));
		}
		return retIntArray;
	}
	
	/** creates an int[] of y values */
	public static int[] createFullIntArrayWithOffsetY(int offset) {
		int[] retIntArray = new int[(GameStates.mapHeight/2)];
		for (int i = offset; i < GameStates.mapHeight; i=i+2 ) {
			retIntArray[i/2] = (int)((i));
		}
		return retIntArray;
	}
	
	public static int[] getRandomPermutation (int length){

	    // initialize array and fill it with {0,1,2...}
	    int[] array = new int[length];
	    for(int i = 0; i < array.length; i++)
	        array[i] = i;

	    for(int i = 0; i < length; i++){

	        // randomly chosen position in array whose element
	        // will be swapped with the element in position i
	        // note that when i = 0, any position can chosen (0 thru length-1)
	        // when i = 1, only positions 1 through length -1
	                    // NOTE: r is an instance of java.util.Random
	        int ran = i + MyMath.random.nextInt (length-i);

	        // perform swap
	        int temp = array[i];
	        array[i] = array[ran];
	        array[ran] = temp;
	    }                       
	    return array;
	}
	
	public static MiniMapColor getClosestColor(Color color) {
		Color tempColor = new Color(0,255,0);
		MiniMapColor retMini = MiniMapColor.GREEN;
		double distance = Double.MAX_VALUE;//MyMath.getColorDistance3D(color.getColorComponents(null)[0], color.getColorComponents(null)[1], color.getColorComponents(null)[2], tempColor.getColorComponents(null)[0], tempColor.getColorComponents(null)[1], tempColor.getColorComponents(null)[2]);
		for (MiniMapColor miniColor : MiniMapColor.values()){
			double tempDistance = MyMath.getColorDistance3D(color.getColorComponents(null)[0], color.getColorComponents(null)[1], color.getColorComponents(null)[2], MyUtil.MiniMapColor2Color(miniColor).getColorComponents(null)[0], MyUtil.MiniMapColor2Color(miniColor).getColorComponents(null)[1], MyUtil.MiniMapColor2Color(miniColor).getColorComponents(null)[2]);
			if (tempDistance < distance) {
				tempColor = MiniMapColor2Color(miniColor);
				retMini = miniColor;
				distance = tempDistance;
			} else ;
		}
		return retMini;
	}
	
	public static Color MiniMapColor2Color(MiniMapColor miniMapColor) {
		Color retValue = new Color(0,0,0); 
		switch (miniMapColor) {
		case BLUE: {
			retValue =  new Color(0,0,255/2);
			break;
		}
		case BROWN: {
			retValue = new Color(160, 72, 0);
			break;
		}
		case GREEN: {
			retValue = new Color(0,255/2,0);
			break;
		}
		case GRAY: {
			retValue = new Color(192,192,192);
			break;
		}
		case RED:  {
			retValue= new Color(255/2,0,0);
			break;
		}
		case WHITE: {
			retValue = new Color(255,255,255);
			break;
		}
		case YELLOW: {
			retValue = new Color(232,232,0);
			break;
		}
		case BLACK: {
			retValue = new Color(0,0,0);
			break;
		}
		case VIOLET: {
			retValue = new Color(255, 0, 255);
			break;
		}
		case ORANGE: {
			retValue = new Color(255, 128, 0);
			break;
		}
		case CYAN: {
			retValue = new Color(0, 255, 255);
			break;
		}
		case LIME: {
			retValue = new Color(196, 255, 0);
			break;
		}
		default: {
			retValue = new Color(0,0,0);
			break;
		}
		}
		return retValue;
	}
}
