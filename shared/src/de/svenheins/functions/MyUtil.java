package de.svenheins.functions;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import de.svenheins.main.GameColors;

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
	
	/** list all files in a directory */
	public static ArrayList<String> listFilesForFolder(final File folder) {
	    ArrayList<String> fileList = new ArrayList<String>();
		for (final File fileEntry : folder.listFiles()) {
	        if (fileEntry.isDirectory()) {
	            listFilesForFolder(fileEntry);
	        } else {
	        	fileList.add(fileEntry.getName());
	        }
	    }
		return fileList;
	}

}
