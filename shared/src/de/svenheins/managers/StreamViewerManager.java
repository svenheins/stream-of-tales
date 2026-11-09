/* Copyright (C) Sven Heins - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * author: Sven Heins
 * date: 05.07.2015
 */
package de.svenheins.managers;

import java.util.HashMap;

import de.svenheins.objects.StreamViewer;

public class StreamViewerManager {
	public static HashMap<String, StreamViewer> streamViewerList = new HashMap<String, StreamViewer>();
	
	public static boolean add(String name) {
		StreamViewer streamViewer = new StreamViewer(name);
		if (streamViewerList.containsKey(name)) {
			return false;
		} else {
			streamViewerList.put(name, streamViewer);
			return true;
		}
	}
	
	public static StreamViewer get(String name){
		try {
			return (StreamViewer) streamViewerList.get(name);
		}
		catch(IndexOutOfBoundsException e){
			return null;
		}
		
	}
	
	public static boolean overwrite(StreamViewer streamViewer){
		if (!streamViewerList.containsKey(streamViewer.getName())) {
			/** do nothing if key is not yet set*/
			return false;
		} else {
			streamViewerList.put(streamViewer.getName(), streamViewer);
			return true;
		}
	}
	

	public static int size(){
		return streamViewerList.size();
	}
	
	public static boolean contains(String name) {
		return streamViewerList.containsKey(name);
	}
	
	public static void emptyAll() {
		streamViewerList = new HashMap<String, StreamViewer>();
	}
	
	public static void remove(String streamViewer) {
		streamViewerList.remove(streamViewer);
	}
}
