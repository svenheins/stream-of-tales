package de.svenheins.managers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.svenheins.animation.Animation;
import de.svenheins.main.GameStates;
import de.svenheins.objects.Sprite;
import de.svenheins.objects.TileSet;

public class TileSetManager {
	public static TileSetManager manager = new TileSetManager();
	private HashMap<String, TileSet> map = new HashMap<String, TileSet>();
	
	public TileSet getTileSet(String src) {
		if(map.get(src) != null) return map.get(src);
		else {
//			BufferedImage img;
//			try {
//				img = ImageIO.read(this.getClass().getResource(GameStates.resourcePath+"images/"+src));
			TileSet s = new TileSet(src, src, GameStates.tileWidth, GameStates.tileHeight);
			map.put(src, s);
			return s;
//			} catch (IOException e) {
//				e.printStackTrace();
//				return null;
//			}
			
		}
	}
	
	public TileSet getTileSet(String src, int width, int height) {
		if(map.get(src) != null) return map.get(src);
		else {
//			BufferedImage img;
//			try {
//				img = ImageIO.read(this.getClass().getResource(GameStates.resourcePath+"images/"+src));
			TileSet s = new TileSet(src, src, width, height);
//			System.out.println(GameStates.resourcePath+"images/"+src);
			map.put(src, s);
			return s;
//			} catch (IOException e) {
//				e.printStackTrace();
//				return null;
//			}
			
		}
	}
	
	
	public TileSet getTileSet(TileSet tile) {
		String idString = tile.getName();
		if(map.get(idString) != null) return map.get(idString);
		else {
			map.put(idString, tile);
//			System.out.println(idString);
			return map.get(idString);
		}
	}
	
	/** return all tileSets as ArrayList<String> */
	public ArrayList<String> getAllTileSetFileNames() {
		ArrayList<String> returnList = new ArrayList<String>();
		for (TileSet tile: map.values()) {
			returnList.add(tile.getFileName());
		}
		return returnList;
	}
	
	/** return all tileSets as ArrayList<String> */
	public ArrayList<String> getAllTileSetNames() {
		ArrayList<String> returnList = new ArrayList<String>();
		for (String name: map.keySet()) {
			returnList.add(name);
		}
		return returnList;
	}

	public TileSet getTileSetByPath(String path) {
		TileSet returnTile = null;
		for (TileSet tile: map.values()) {
			if (tile.getFileName().equals(path)) {
				returnTile = tile;
			}
		}
		return returnTile;
	}

}
