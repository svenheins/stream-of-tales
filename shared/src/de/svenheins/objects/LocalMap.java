package de.svenheins.objects;



import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;

import de.svenheins.main.GameStates;

public class LocalMap {
	private int[][] localMap;
	private int[][] ulArray, urArray, dlArray, drArray;
	private Point origin;
	private long timeStamp;
	 
	String tileSetFileName;
 
	ArrayList<BufferedImage> tileSet=new ArrayList<BufferedImage>();
 
	public LocalMap(int[][] localMap, String fileName, Point origin)
	{
		this.localMap=localMap;
		this.tileSetFileName=fileName;
		this.origin = origin;
		/** update timeStamp */
		this.setTimeStamp(System.currentTimeMillis());
		ulArray = new int[GameStates.mapWidth][ GameStates.mapHeight];
		urArray = new int[GameStates.mapWidth][ GameStates.mapHeight];
		dlArray = new int[GameStates.mapWidth][ GameStates.mapHeight];
		drArray = new int[GameStates.mapWidth][ GameStates.mapHeight];
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
	
	public LocalMap(int[][] localMap, int[][] ulArray, int[][] urArray, int[][] dlArray, int[][] drArray, String fileName, Point origin)
	{
		this.localMap=localMap;
		this.tileSetFileName=fileName;
		this.origin = origin;
		/** update timeStamp */
		this.setTimeStamp(System.currentTimeMillis());
		this.ulArray = ulArray;
		this.urArray = urArray;
		this.dlArray = dlArray;
		this.drArray = drArray;
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
		if (localMap[x][y] != 0) {
			int tile=localMap[x][y];
			return tileSet.get(tile);
		} else {
			return null;
		}
		
	}
 
	public void setTile(int x, int y, int tile)
	{
		localMap[x][y] = tile;
//		System.out.println("tile: "+tile);
		/** update timeStamp */
		this.setTimeStamp(System.currentTimeMillis());
	}
	
	public int getTile(int x, int y)
	{
		return localMap[x][y];
//		if (localMap[x][y] == 0) {
//			return 0; //new Tile(0, false, false, false, false );
//		} else 	return localMap[x][y];
	}


	public Point getOrigin() {
		return origin;
	}

	
	public void setOrigin( Point origin) {
		this.origin = origin;
	}
	
	public int[][] getLocalMap() {
		return localMap;
	}


	public long getTimeStamp() {
		return timeStamp;
	}


	public void setTimeStamp(long timeStamp) {
		this.timeStamp = timeStamp;
	}
	
	
	
	
	
	public void setIdByCorners(int x, int y) {
		boolean ul = getUl(x, y) != 0;
		boolean ur = getUr(x, y) != 0;
		boolean dl = getDl(x, y) != 0;
		boolean dr = getDr(x, y) != 0;
		
//		System.out.println("UL UR DL DR : "+ ul + " "+ ur + " "+ dl + " "+ dr + " x:"+x+" y:"+y);
				
		
		int centerTile = 110;
		int tsWidth = 32;
		if (ul && !ur && !dr && !dl) {
			setTile(x, y, centerTile+tsWidth+1);
//			System.out.println("ul");
		}
		else if (ul && ur && !dr && !dl) setTile(x, y, centerTile+tsWidth);
		else if (!ul && ur && !dr && !dl) setTile(x, y, centerTile+tsWidth-1);
		else if (ul && !ur && !dr && dl) setTile(x, y, centerTile+1);
		else if (ul && ur && dr && dl) setTile(x, y, centerTile);
		else if (!ul && ur && dr && !dl) setTile(x, y, centerTile-1);
		else if (!ul && !ur && !dr && dl) setTile(x, y, centerTile-tsWidth+1);
		else if (!ul && !ur && dr && dl) setTile(x, y, centerTile-tsWidth);
		else if (!ul && !ur && dr && !dl) setTile(x, y, centerTile-tsWidth-1);
		else if (ul && !ur && dr && dl) setTile(x, y, centerTile-2);
		else if (!ul && ur && dr && dl) setTile(x, y, centerTile-3);
		else if (ul && ur && !dr && dl) setTile(x, y, centerTile+tsWidth-2);
		else if (ul && ur && dr && !dl) setTile(x, y, centerTile+tsWidth-3);
		else if (!ul && ur && !dr && dl) setTile(x, y, centerTile+tsWidth-2);
		else if (ul && !ur && dr && !dl) setTile(x, y, centerTile+tsWidth-3);
		else if (!ul && !ur && !dr && !dl) setTile(x, y, 0);
	}
	
	public void setUl(int x, int y, int id) {
		ulArray[x][y] = id;
//		System.out.println("set ul: "+ id+ " x y "+ x+" "+y);
	}
	public void setUr(int x, int y, int id) {
		urArray[x][y] = id;
//		System.out.println("set ur: "+id+ " x y "+ x+" "+y);
	}
	public void setDl(int x, int y, int id) {
		dlArray[x][y] = id;
//		System.out.println("set dl: "+id+ " x y "+ x+" "+y);
	}
	public void setDr(int x, int y, int id) {
		drArray[x][y] = id;
//		System.out.println("set dr: "+id+ " x y "+ x+" "+y);
	}
	
	public int getUl(int x, int y) {
		return ulArray[x][y];
	}
	public int getUr(int x, int y) {
		return urArray[x][y];
	}
	public int getDl(int x, int y) {
		return dlArray[x][y];
	}
	public int getDr(int x, int y) {
		return drArray[x][y];
	}
	
	public int[][] getUlArray() {
		return this.ulArray;
	}
	public int[][] getUrArray() {
		return this.urArray;
	}
	public int[][] getDlArray() {
		return this.dlArray;
	}
	public int[][] getDrArray() {
		return this.drArray;
	}
	
	public String getTileSetFileName() {
		return tileSetFileName;
	}
}
