package de.svenheins.objects;



import java.awt.Point;
import java.awt.image.BufferedImage;


import de.svenheins.main.GameStates;
import de.svenheins.managers.TileMapManager;

public class LocalMap {
	private int[][] localMap;
	private int[][] ulArray, urArray, dlArray, drArray;
	private Point origin;
	private long timeStamp;
	private String paintLayer;
 
	public LocalMap(int[][] localMap, Point origin)
	{
		this.localMap=localMap;
		this.origin = origin;
		/** update timeStamp */
		this.setTimeStamp(System.currentTimeMillis());
		ulArray = new int[GameStates.mapWidth][ GameStates.mapHeight];
		urArray = new int[GameStates.mapWidth][ GameStates.mapHeight];
		dlArray = new int[GameStates.mapWidth][ GameStates.mapHeight];
		drArray = new int[GameStates.mapWidth][ GameStates.mapHeight];

	}
	
	public LocalMap(int[][] localMap, int[][] ulArray, int[][] urArray, int[][] dlArray, int[][] drArray, Point origin, String paintLayer)
	{
		this.localMap=localMap;
		this.origin = origin;
		this.paintLayer = paintLayer;
		/** update timeStamp */
		this.setTimeStamp(System.currentTimeMillis());
		this.ulArray = ulArray;
		this.urArray = urArray;
		this.dlArray = dlArray;
		this.drArray = drArray;
	}
 
 
	public BufferedImage getTileImage(int x, int y, TileMapManager tileMapManager)
	{
		if (localMap[x][y] != 0) {
			int tile=localMap[x][y];
			return tileMapManager.getTileImage(""+tile);
		} else {
			return null;
		}
		
	}
 
	public void setTile(int x, int y, int tile)
	{
		localMap[x][y] = tile;
		/** update timeStamp */
		this.setTimeStamp(System.currentTimeMillis());
	}
	
	public int getTile(int x, int y)
	{
		return localMap[x][y];
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
	
	/** Set Id by corners for (3 x 3) Objects normal floor with extensions */
	public void setIdByCorners(int x, int y, int paintType) {
		boolean ul = getUl(x, y) != 0;
		boolean ur = getUr(x, y) != 0;
		boolean dl = getDl(x, y) != 0;
		boolean dr = getDr(x, y) != 0;

		int tsWidth = GameStates.tileSetWidth;
		if (ul && !ur && !dr && !dl) setTile(x, y, paintType+tsWidth+1);
		else if (ul && ur && !dr && !dl) setTile(x, y, paintType+tsWidth);
		else if (!ul && ur && !dr && !dl) setTile(x, y, paintType+tsWidth-1);
		else if (ul && !ur && !dr && dl) setTile(x, y, paintType+1);
		else if (ul && ur && dr && dl) setTile(x, y, paintType);
		else if (!ul && ur && dr && !dl) setTile(x, y, paintType-1);
		else if (!ul && !ur && !dr && dl) setTile(x, y, paintType-tsWidth+1);
		else if (!ul && !ur && dr && dl) setTile(x, y, paintType-tsWidth);
		else if (!ul && !ur && dr && !dl) setTile(x, y, paintType-tsWidth-1);
		else if (ul && !ur && dr && dl) setTile(x, y, paintType-2);
		else if (!ul && ur && dr && dl) setTile(x, y, paintType-3);
		else if (ul && ur && !dr && dl) setTile(x, y, paintType+tsWidth-2);
		else if (ul && ur && dr && !dl) setTile(x, y, paintType+tsWidth-3);
		else if (!ul && ur && !dr && dl) setTile(x, y, paintType-tsWidth-2);
		else if (ul && !ur && dr && !dl) setTile(x, y, paintType-tsWidth-3);
		else if (!ul && !ur && !dr && !dl) setTile(x, y, 0);
	}
	
	/** Set Id by corners for (2 x 3) Objects with an Overlay (e.g. trees) */
	public void setIdByCornersObject(int x, int y, int paintType) {
		boolean ul = getUl(x, y) != 0;
		boolean ur = getUr(x, y) != 0;
		boolean dl = getDl(x, y) != 0;
		boolean dr = getDr(x, y) != 0;
		
		int tsWidth = GameStates.tileSetWidth;
		if (ul && !ur && !dr && !dl) setTile(x, y, paintType+1);
		else if (ul && ur && !dr && !dl) setTile(x, y, paintType);
		else if (!ul && ur && !dr && !dl) setTile(x, y, paintType-1);
		else if (!ul && !ur && !dr && dl) setTile(x, y, paintType-tsWidth+1);
		else if (!ul && !ur && dr && dl) setTile(x, y, paintType-tsWidth);
		else if (!ul && !ur && dr && !dl) setTile(x, y, paintType-tsWidth-1);
		else if (!ul && !ur && !dr && !dl) setTile(x, y, 0);
	}
	
	public void setUl(int x, int y, int id) {
		ulArray[x][y] = id;
	}
	public void setUr(int x, int y, int id) {
		urArray[x][y] = id;
	}
	public void setDl(int x, int y, int id) {
		dlArray[x][y] = id;
	}
	public void setDr(int x, int y, int id) {
		drArray[x][y] = id;
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
	public String getPaintLayer() {
		return paintLayer;
	}
	public void setPaintLayer(String paintLayer) {
		this.paintLayer = paintLayer;
	}
}
