package de.svenheins.objects;



import java.awt.Point;
import java.awt.image.BufferedImage;

import javax.rmi.CORBA.Tie;


import de.svenheins.main.GameStates;
import de.svenheins.main.TileType;
import de.svenheins.managers.TileMapManager;
import de.svenheins.messages.ITEMCODE;

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
	/** center is the middle center Tile of the paint */
	public void setIdByCorners(int x, int y, int paintType) {
		boolean ul = getUl(x, y) != 0;
		boolean ur = getUr(x, y) != 0;
		boolean dl = getDl(x, y) != 0;
		boolean dr = getDr(x, y) != 0;

		int tsWidth = GameStates.tileSetWidth;
		if (ul && !ur && !dr && !dl) setTile(x, y, paintType+tsWidth+1); // dr
		else if (ul && ur && !dr && !dl) setTile(x, y, paintType+tsWidth); // d
		else if (!ul && ur && !dr && !dl) setTile(x, y, paintType+tsWidth-1); // dl
		else if (ul && !ur && !dr && dl) setTile(x, y, paintType+1); // r
		else if (ul && ur && dr && dl) setTile(x, y, paintType); // center
		else if (!ul && ur && dr && !dl) setTile(x, y, paintType-1); // l
		else if (!ul && !ur && !dr && dl) setTile(x, y, paintType-tsWidth+1); // ul
		else if (!ul && !ur && dr && dl) setTile(x, y, paintType-tsWidth); // u
		else if (!ul && !ur && dr && !dl) setTile(x, y, paintType-tsWidth-1); // ul
		else if (ul && !ur && dr && dl) setTile(x, y, paintType-2);
		else if (!ul && ur && dr && dl) setTile(x, y, paintType-3);
		else if (ul && ur && !dr && dl) setTile(x, y, paintType+tsWidth-2);
		else if (ul && ur && dr && !dl) setTile(x, y, paintType+tsWidth-3);
		else if (!ul && ur && !dr && dl) setTile(x, y, paintType-tsWidth-2);
		else if (ul && !ur && dr && !dl) setTile(x, y, paintType-tsWidth-3);
		else if (!ul && !ur && !dr && !dl) setTile(x, y, 0);
	}
	
	/** Set Id by corners for (2 x 3) Objects with an Overlay (e.g. trees) */
	/** center is the down center tile of the object */
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
	
	/** Set Id by corners for (2 x 2) Objects with an Overlay (e.g. thin trees) */
	/** center is the upper left corner Tile of the object */
	public void setIdByCornersObject2x2(int x, int y, int paintType) {
		boolean ul = getUl(x, y) != 0;
		boolean ur = getUr(x, y) != 0;
		boolean dl = getDl(x, y) != 0;
		boolean dr = getDr(x, y) != 0;
		
		int tsWidth = GameStates.tileSetWidth;
		if (ul && !ur && !dr && !dl) setTile(x, y, paintType + tsWidth +1); // dr Tile
		else if (!ul && ur && !dr && !dl) setTile(x, y, paintType + tsWidth); // dl Tile
		else if (!ul && !ur && !dr && dl) setTile(x, y, paintType +1); // ur Tile
		else if (!ul && !ur && dr && !dl) setTile(x, y, paintType); // ul Tile
		else if (!ul && !ur && !dr && !dl) setTile(x, y, 0);
	}
	
	/** Set Id by corners for (1 x 2) Objects without an Overlay (e.g. large chest) */
	/** center is the left side Tile of the object */
	public void setIdByCornersObject1x2(int x, int y, int paintType) {
		boolean ul = getUl(x, y) != 0;
		boolean ur = getUr(x, y) != 0;
		boolean dl = getDl(x, y) != 0;
		boolean dr = getDr(x, y) != 0;
		
//		int tsWidth = GameStates.tileSetWidth;
		if (!ul && ur && dr && !dl) setTile(x, y, paintType); // l Tile
		else if (ul && !ur && !dr && dl) setTile(x, y, paintType +1 ); // r Tile
		else if (!ul && !ur && !dr && !dl) setTile(x, y, 0);
	}
	
	/** Set Id by corners for (2 x 1) Objects without an Overlay (e.g. very thin and large tree or medium/ long sign) */
	/** center is the lower Tile of the object */
	public void setIdByCornersObject2x1(int x, int y, int paintType) {
		boolean ul = getUl(x, y) != 0;
		boolean ur = getUr(x, y) != 0;
		boolean dl = getDl(x, y) != 0;
		boolean dr = getDr(x, y) != 0;
		
		int tsWidth = GameStates.tileSetWidth;
		if (ul && ur && !dr && !dl) setTile(x, y, paintType); // d Tile
		else if (!ul && !ur && dr && dl) setTile(x, y, paintType-tsWidth); // u Tile
		else if (!ul && !ur && !dr && !dl) setTile(x, y, 0);
	}
	
	/** determines the main tile of a paintTile (center) */
	public static int getPaintType(int paintTile) {
		int retValue = 0;
		int tsWidth = GameStates.tileSetWidth;
		if (paintTile == GameStates.treeTile || paintTile == GameStates.treeTile-1 || paintTile == GameStates.treeTile+1 ||
				paintTile == GameStates.treeTile-tsWidth-1 || paintTile == GameStates.treeTile-tsWidth || paintTile == GameStates.treeTile-tsWidth+1)
		{
			retValue = GameStates.treeTile;
		}
		if (paintTile == GameStates.snowTreeTile || paintTile == GameStates.snowTreeTile-1 || paintTile == GameStates.snowTreeTile+1 ||
				paintTile == GameStates.snowTreeTile-tsWidth-1 || paintTile == GameStates.snowTreeTile-tsWidth || paintTile == GameStates.snowTreeTile-tsWidth+1)
		{
			retValue = GameStates.snowTreeTile;
		}
		return retValue;
	}
	
	/** this determines how hard it is too destroy a given paintType with tools
	 * and what kind of tool is optimal for the given object
	 * */
	public static TileType getTileType(int paintType) {
		TileType tileType = TileType.FLOOR;
		if (paintType == GameStates.treeTile) {
			tileType = TileType.TREE;
		}
		if (paintType == GameStates.snowTreeTile) {
			tileType = TileType.STONE;
		}
		
		return tileType;
	}
	
	/** this determines what kind of item will drop from the given paintTile 
	 * */
	public static ITEMCODE getItemCode(int paintTile) {
		ITEMCODE retValue = ITEMCODE.WOOD;
		int tsWidth = GameStates.tileSetWidth;
		if (paintTile == GameStates.treeTile || paintTile == GameStates.treeTile-1 || paintTile == GameStates.treeTile+1 ||
				paintTile == GameStates.treeTile-tsWidth-1 || paintTile == GameStates.treeTile-tsWidth || paintTile == GameStates.treeTile-tsWidth+1)
		{
			retValue = ITEMCODE.WOOD;
		}
		if (paintTile == GameStates.snowTreeTile || paintTile == GameStates.snowTreeTile-1 || paintTile == GameStates.snowTreeTile+1 ||
				paintTile == GameStates.snowTreeTile-tsWidth-1 || paintTile == GameStates.snowTreeTile-tsWidth || paintTile == GameStates.snowTreeTile-tsWidth+1)
		{
			retValue = ITEMCODE.BODY;
		}
		return retValue;
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
