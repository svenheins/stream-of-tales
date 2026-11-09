/* Copyright (C) Sven Heins - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * author: Sven Heins
 * date: 05.07.2015
 */
package de.svenheins.managers;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;

import javax.imageio.ImageIO;

import de.svenheins.main.GameStates;

public class TileMapManager {
//	public ArrayList<BufferedImage> tileSet=new ArrayList<BufferedImage>();
	public HashMap<Integer, BufferedImage> tileSet = new HashMap<Integer, BufferedImage>();
	public String tileSetFileName;
	
	public TileMapManager(String fileName, int mapTileSetWidth, int mapTileSetHeight) {
		setTileSetFileName(fileName);
		try {
			BufferedImage readTileset=ImageIO.read(getClass().getResource(GameStates.resourcePath+"images/"+fileName));
			int width=readTileset.getWidth()/mapTileSetWidth;
			int height=readTileset.getHeight()/mapTileSetHeight;
			int index = 0;
			for(int y=0;y<height;y++)
			{
				for(int x=0;x<width;x++)
				{
					Integer tileIndexName = Integer.valueOf(index);
					BufferedImage tile=readTileset.getSubimage(x*mapTileSetWidth, y*mapTileSetWidth, mapTileSetWidth, mapTileSetWidth);
					tileSet.put(tileIndexName, tile);
					index++;
				}				
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String getTileSetFileName() {
		return tileSetFileName;
	}

	public void setTileSetFileName(String tileName) {
		tileSetFileName = tileName;
	}
	
	public BufferedImage getTileImage(int tile)
	{
		return tileSet.get(Integer.valueOf(tile)); //tileSet.get(tile);
	}
}
