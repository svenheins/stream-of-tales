package de.svenheins.managers;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;

import javax.imageio.ImageIO;

import de.svenheins.main.GameStates;

public class TileMapManager {
//	public ArrayList<BufferedImage> tileSet=new ArrayList<BufferedImage>();
	public HashMap<String, BufferedImage> tileSet = new HashMap<String, BufferedImage>();
	public String tileSetFileName;
	
	public TileMapManager(String fileName) {
		setTileSetFileName(fileName);
		try {
			BufferedImage readTileset=ImageIO.read(getClass().getResource(GameStates.resourcePath+"images/"+fileName));
			int width=readTileset.getWidth()/GameStates.mapTileSetWidth;
			int height=readTileset.getHeight()/GameStates.mapTileSetHeight;
			int index = 0;
			for(int y=0;y<height;y++)
			{
				for(int x=0;x<width;x++)
				{
					String tileIndexName = ""+index;
					BufferedImage tile=readTileset.getSubimage(x*GameStates.mapTileSetWidth, y*GameStates.mapTileSetWidth, GameStates.mapTileSetWidth, GameStates.mapTileSetWidth);
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
	
	public BufferedImage getTileImage(String tile)
	{
		return tileSet.get(tile); //tileSet.get(tile);
	}
}
