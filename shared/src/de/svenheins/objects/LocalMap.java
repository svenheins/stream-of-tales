package de.svenheins.objects;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import de.svenheins.main.GameStates;

public class LocalMap {
	private Tile[][] localMap;
	private Point origin;
	 
	String tileSetFileName;
 
	ArrayList<BufferedImage> tileSet=new ArrayList<BufferedImage>();
 
	public LocalMap(Tile[][] localMap, String fileName, Point origin)
	{
		this.localMap=localMap;
		this.tileSetFileName=fileName;
		this.origin = origin;
		try {
			BufferedImage readTileset=ImageIO.read(this.getClass().getResource(GameStates.resourcePath+"images/"+fileName));
			int width=readTileset.getWidth()/GameStates.mapTileSetWidth;
			int height=readTileset.getHeight()/GameStates.mapTileSetHeight;
			for(int y=0;y<height;y++)
			{
				for(int x=0;x<width;x++)
				{
					BufferedImage tile=readTileset.getSubimage(x*GameStates.mapTileSetWidth, y*GameStates.mapTileSetWidth, GameStates.mapTileSetWidth, GameStates.mapTileSetWidth);
					this.tileSet.add(tile);
				}				
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
 
 
	public BufferedImage getTileImage(int x, int y)
	{
		if (localMap[x][y] != null) {
			int tile=localMap[x][y].getId();
			return tileSet.get(tile);
		} else {
			return null;
		}
		
	}
 
	public void setTile(int x, int y, Tile tile)
	{
		localMap[x][y] = tile;
	}
	
	public Tile getTile(int x, int y)
	{
		if (localMap[x][y] == null) {
			return new Tile(0, false, false, false, false );
		} else 	return localMap[x][y];
	}


	public Point getOrigin() {
		return origin;
	}

	
	public void setOrigin( Point origin) {
		this.origin = origin;
	}
	
	public Tile[][] getLocalMap() {
		return localMap;
	}
}
