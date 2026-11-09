/* Copyright (C) Sven Heins - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * author: Sven Heins
 * date: 05.07.2015
 */
package de.svenheins.objects;



import java.awt.Point;
import java.awt.image.BufferedImage;

import de.svenheins.main.GameStates;
import de.svenheins.main.TileDimensions;
import de.svenheins.main.TileSize;
import de.svenheins.main.TileType;
import de.svenheins.managers.TileMapManager;
import de.svenheins.messages.ITEMCODE;

public class LocalMap {
	private int[][] localMap;
	private int[][] ulArray, urArray, dlArray, drArray;
	private Point origin;
	private long timeStamp;
	private String paintLayer;
	private boolean initialized;
 
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

		this.setInitialized(false);
	}
	
	public LocalMap(int[][] localMap, int[][] ulArray, int[][] urArray, int[][] dlArray, int[][] drArray, Point origin, String paintLayer, boolean initialized)
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
		this.setInitialized(initialized);
	}
	
	public LocalMap(LocalMap copyMap, int mainTile, int newMainTile) {
		this.localMap=new int[GameStates.mapWidth][ GameStates.mapHeight];
		this.origin = copyMap.getOrigin();
		/** update timeStamp */
		this.setTimeStamp(System.currentTimeMillis());
		ulArray = new int[GameStates.mapWidth][ GameStates.mapHeight];
		urArray = new int[GameStates.mapWidth][ GameStates.mapHeight];
		dlArray = new int[GameStates.mapWidth][ GameStates.mapHeight];
		drArray = new int[GameStates.mapWidth][ GameStates.mapHeight];
		
		for (int i = 0; i< GameStates.mapWidth; i++) {
			for (int j = 0; j < GameStates.mapHeight; j++) {
				if (copyMap.getLocalMap()[i][j] != 0) {
					this.localMap[i][j] = copyMap.getLocalMap()[i][j]+(newMainTile-mainTile);
					this.ulArray[i][j] = copyMap.getUlArray()[i][j] +(newMainTile-mainTile);
					this.urArray[i][j] = copyMap.getUrArray()[i][j] +(newMainTile-mainTile);
					this.dlArray[i][j] = copyMap.getDlArray()[i][j] +(newMainTile-mainTile);
					this.drArray[i][j] = copyMap.getDrArray()[i][j] +(newMainTile-mainTile);
				}
			}
		}
		
		this.setInitialized(true);
	}
 
 
	public BufferedImage getTileImage(int x, int y, TileMapManager tileMapManager)
	{
		if (localMap[x][y] != 0) {
			int tile=localMap[x][y];
			return tileMapManager.getTileImage(tile);
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
		if (localMap != null) {
			return localMap[x][y];
		}
		else return -1;
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
	public void setIdByCornersObject(int x, int y, int paintType, TileDimensions tileDimensions) {
		switch (tileDimensions) {
		case RowCol2x3: {
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
			break;
		}
		case RowCol2x2: {
			boolean ul = getUl(x, y) != 0;
			boolean ur = getUr(x, y) != 0;
			boolean dl = getDl(x, y) != 0;
			boolean dr = getDr(x, y) != 0;
			
			int tsWidth = GameStates.tileSetWidth;
			if (ul && !ur && !dr && !dl) setTile(x, y, paintType +1); // dr Tile
			else if (!ul && ur && !dr && !dl) setTile(x, y, paintType); // dl Tile
			else if (!ul && !ur && !dr && dl) setTile(x, y, paintType -tsWidth + 1); // ur Tile
			else if (!ul && !ur && dr && !dl) setTile(x, y, paintType - tsWidth); // ul Tile
			else if (!ul && !ur && !dr && !dl) setTile(x, y, 0);
		}
		default: {
			break;
		}
		}
		
		
	}
	
	/** Set Id by corners for (2 x 2) Objects with an Overlay (e.g. thin trees) */
	/** center is the lower left corner Tile of the object */
	public void setIdByCornersObject2x2(int x, int y, int paintType) {
		boolean ul = getUl(x, y) != 0;
		boolean ur = getUr(x, y) != 0;
		boolean dl = getDl(x, y) != 0;
		boolean dr = getDr(x, y) != 0;
		
		int tsWidth = GameStates.tileSetWidth;
		if (ul && !ur && !dr && !dl) setTile(x, y, paintType +1); // dr Tile
		else if (!ul && ur && !dr && !dl) setTile(x, y, paintType); // dl Tile
		else if (!ul && !ur && !dr && dl) setTile(x, y, paintType -tsWidth + 1); // ur Tile
		else if (!ul && !ur && dr && !dl) setTile(x, y, paintType - tsWidth); // ul Tile
		else if (!ul && !ur && !dr && !dl) setTile(x, y, 0);
		
//		if (ul && !ur && !dr && !dl) setTile(x, y, paintType + tsWidth +1); // dr Tile
//		else if (!ul && ur && !dr && !dl) setTile(x, y, paintType + tsWidth); // dl Tile
//		else if (!ul && !ur && !dr && dl) setTile(x, y, paintType +1); // ur Tile
//		else if (!ul && !ur && dr && !dl) setTile(x, y, paintType); // ul Tile
//		else if (!ul && !ur && !dr && !dl) setTile(x, y, 0);
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
	
	/** Set Id by corners for Fluid Objects */
	/** center is the middle center Tile of the paint */
	public void setIdByCornersFluid(int x, int y, int paintType) {
		boolean ul = getUl(x, y) != 0;
		boolean ur = getUr(x, y) != 0;
		boolean dl = getDl(x, y) != 0;
		boolean dr = getDr(x, y) != 0;

		int tsWidth = GameStates.tileSetWidth;
		if (ul && !ur && !dr && !dl) setTile(x, y, paintType+tsWidth+2); // up
		else if (ul && ur && !dr && !dl) setTile(x, y, paintType+tsWidth-1); // up right
		else if (!ul && ur && !dr && !dl) { // right
			if (isOverlay(paintType)) {
				/** overlay version */
				setTile(x, y, paintType-tsWidth+3); // right
			} else {
				/** normal version */
				setTile(x, y, paintType+tsWidth-1); // right
			}
		}
		else if (ul && !ur && !dr && dl) setTile(x, y, paintType+tsWidth+1); // up left
		else if (ul && ur && dr && dl) setTile(x, y, paintType); // center
		else if (!ul && ur && dr && !dl) setTile(x, y, paintType-tsWidth-1); // down right
		else if (!ul && !ur && !dr && dl) { // left
			if (isOverlay(paintType)) {
				/** overlay version */
				setTile(x, y, paintType+5*tsWidth+3); // left
			} else {
				/** normal version */
				setTile(x, y, paintType+tsWidth+1); // left
			}
		}
		else if (!ul && !ur && dr && dl) setTile(x, y, paintType-tsWidth+1); // down left
		else if (!ul && !ur && dr && !dl) setTile(x, y, paintType-tsWidth+2); // down
		else if (ul && !ur && dr && dl) setTile(x, y, paintType+1); // up down left
		else if (!ul && ur && dr && dl) setTile(x, y, paintType-tsWidth); // down right left
		else if (ul && ur && !dr && dl) setTile(x, y, paintType+tsWidth); // up right left
		else if (ul && ur && dr && !dl) setTile(x, y, paintType-1); // up down right
		else if (!ul && ur && !dr && dl) { // right left
			if (isOverlay(paintType)) {
				/** overlay version */
				setTile(x, y, paintType+3*tsWidth+3); // right left
			} else {
				/** normal version */
				setTile(x, y, paintType+tsWidth); // right left
			}
		}
		else if (ul && !ur && dr && !dl) setTile(x, y, paintType+2); // up down 
		else if (!ul && !ur && !dr && !dl) {
			if (isOverlay(paintType)) {
				/** overlay version */
				setTile(x, y, paintType+tsWidth+3); // no surrounding
			} else {
				/** normal version */
				setTile(x, y, paintType+2+tsWidth); // no surrounding
			}
			
		}
	}
	
	/** determines the main tile of a paintTile (center) */
	public static int getPaintType(int paintTile) {
//		int retValue = paintTile;
		int tsWidth = GameStates.tileSetWidth;
		// 2x3 tiles
		if (paintTile == GameStates.treeTile || paintTile == GameStates.treeTile-1 || paintTile == GameStates.treeTile+1 ||
				paintTile == GameStates.treeTile-tsWidth-1 || paintTile == GameStates.treeTile-tsWidth || paintTile == GameStates.treeTile-tsWidth+1)
		{
			return GameStates.treeTile;
		}
//		if (paintTile == GameStates.thickWoodWallTile || paintTile == GameStates.thickWoodWallTile-1 || paintTile == GameStates.thickWoodWallTile+1 ||
//				paintTile == GameStates.thickWoodWallTile-tsWidth-1 || paintTile == GameStates.thickWoodWallTile-tsWidth || paintTile == GameStates.thickWoodWallTile-tsWidth+1)
//		{
//			return GameStates.thickWoodWallTile;
//		}
		if (paintTile == GameStates.snowTreeTile || paintTile == GameStates.snowTreeTile-1 || paintTile == GameStates.snowTreeTile+1 ||
				paintTile == GameStates.snowTreeTile-tsWidth-1 || paintTile == GameStates.snowTreeTile-tsWidth || paintTile == GameStates.snowTreeTile-tsWidth+1)
		{
			return GameStates.snowTreeTile;
		}
		if (paintTile == GameStates.holeTile || paintTile == GameStates.holeTile+1 || paintTile == GameStates.holeTile-1 || paintTile == GameStates.holeTile-2 ||paintTile == GameStates.holeTile-3 ||
				paintTile == GameStates.holeTile-tsWidth || paintTile == GameStates.holeTile+1-tsWidth || paintTile == GameStates.holeTile-1-tsWidth || paintTile == GameStates.holeTile-2-tsWidth ||paintTile == GameStates.holeTile-3-tsWidth ||
				paintTile == GameStates.holeTile+tsWidth || paintTile == GameStates.holeTile+1+tsWidth || paintTile == GameStates.holeTile-1+tsWidth || paintTile == GameStates.holeTile-2+tsWidth ||paintTile == GameStates.holeTile-3+tsWidth)
			{
				return GameStates.holeTile;
			}
		if (paintTile == GameStates.waterTile || paintTile == GameStates.waterTile+1 || paintTile == GameStates.waterTile-1 || paintTile == GameStates.waterTile-2 ||paintTile == GameStates.waterTile-3 ||
				paintTile == GameStates.waterTile-tsWidth || paintTile == GameStates.waterTile+1-tsWidth || paintTile == GameStates.waterTile-1-tsWidth || paintTile == GameStates.waterTile-2-tsWidth ||paintTile == GameStates.waterTile-3-tsWidth ||
				paintTile == GameStates.waterTile+tsWidth || paintTile == GameStates.waterTile+1+tsWidth || paintTile == GameStates.waterTile-1+tsWidth || paintTile == GameStates.waterTile-2+tsWidth ||paintTile == GameStates.waterTile-3+tsWidth)
			{
				return GameStates.waterTile;
			}
		if (paintTile == GameStates.soilBlockTile || paintTile == GameStates.soilBlockTile-1 || paintTile == GameStates.soilBlockTile+1 ||
				paintTile == GameStates.soilBlockTile-tsWidth-1 || paintTile == GameStates.soilBlockTile-tsWidth || paintTile == GameStates.soilBlockTile-tsWidth+1)
		{
			return GameStates.soilBlockTile;
		}
		if (paintTile == GameStates.stoneTile || paintTile == GameStates.stoneTile-1 || paintTile == GameStates.stoneTile+1 ||
				paintTile == GameStates.stoneTile-tsWidth-1 || paintTile == GameStates.stoneTile-tsWidth || paintTile == GameStates.stoneTile-tsWidth+1)
		{
			return GameStates.stoneTile;
		}
		// 2x2 tiles
		if (paintTile == GameStates.smallTreeTile || paintTile == GameStates.smallTreeTile+1 ||
				paintTile == GameStates.smallTreeTile-tsWidth || paintTile == GameStates.smallTreeTile-tsWidth+1)
		{
			return GameStates.smallTreeTile;
		}
		if (paintTile == GameStates.smallStoneTile || paintTile == GameStates.smallStoneTile+1 ||
				paintTile == GameStates.smallStoneTile-tsWidth || paintTile == GameStates.smallStoneTile-tsWidth+1)
		{
			return GameStates.smallStoneTile;
		}
		if (paintTile == GameStates.bigBushTile || paintTile == GameStates.bigBushTile+1 ||
				paintTile == GameStates.bigBushTile-tsWidth || paintTile == GameStates.bigBushTile-tsWidth+1)
		{
			return GameStates.bigBushTile;
		}
		if (paintTile == GameStates.lightTile || paintTile == GameStates.lightTile+1 || paintTile == GameStates.lightTile-1 || paintTile == GameStates.lightTile-2 ||paintTile == GameStates.lightTile-3 ||
			paintTile == GameStates.lightTile-tsWidth || paintTile == GameStates.lightTile+1-tsWidth || paintTile == GameStates.lightTile-1-tsWidth || paintTile == GameStates.lightTile-2-tsWidth ||paintTile == GameStates.lightTile-3-tsWidth ||
			paintTile == GameStates.lightTile+tsWidth || paintTile == GameStates.lightTile+1+tsWidth || paintTile == GameStates.lightTile-1+tsWidth || paintTile == GameStates.lightTile-2+tsWidth ||paintTile == GameStates.lightTile-3+tsWidth)
		{
			return GameStates.lightTile;
		}
		if (paintTile == GameStates.grassTile || paintTile == GameStates.grassTile+1 || paintTile == GameStates.grassTile-1 || paintTile == GameStates.grassTile-2 ||paintTile == GameStates.grassTile-3 ||
				paintTile == GameStates.grassTile-tsWidth || paintTile == GameStates.grassTile+1-tsWidth || paintTile == GameStates.grassTile-1-tsWidth || paintTile == GameStates.grassTile-2-tsWidth ||paintTile == GameStates.grassTile-3-tsWidth ||
				paintTile == GameStates.grassTile+tsWidth || paintTile == GameStates.grassTile+1+tsWidth || paintTile == GameStates.grassTile-1+tsWidth || paintTile == GameStates.grassTile-2+tsWidth ||paintTile == GameStates.grassTile-3+tsWidth)
			{
				return GameStates.grassTile;
			}
		if (paintTile == GameStates.snowTile || paintTile == GameStates.snowTile+1 || paintTile == GameStates.snowTile-1 || paintTile == GameStates.snowTile-2 ||paintTile == GameStates.snowTile-3 ||
				paintTile == GameStates.snowTile-tsWidth || paintTile == GameStates.snowTile+1-tsWidth || paintTile == GameStates.snowTile-1-tsWidth || paintTile == GameStates.snowTile-2-tsWidth ||paintTile == GameStates.snowTile-3-tsWidth ||
				paintTile == GameStates.snowTile+tsWidth || paintTile == GameStates.snowTile+1+tsWidth || paintTile == GameStates.snowTile-1+tsWidth || paintTile == GameStates.snowTile-2+tsWidth ||paintTile == GameStates.snowTile-3+tsWidth)
			{
				return GameStates.snowTile;
			}
		// 1x1 tiles
		if (paintTile == GameStates.campfireTile )
		{
			return GameStates.campfireTile;
		}
		if (paintTile == GameStates.smallBushTile )
		{
			return GameStates.smallBushTile;
		}
		if (paintTile == GameStates.collectStoneTile1 )
		{
			return GameStates.collectStoneTile1;
		}
		if (paintTile == GameStates.collectStoneTile2 )
		{
			return GameStates.collectStoneTile2;
		}
		if (paintTile == GameStates.collectStoneTile3 )
		{
			return GameStates.collectStoneTile3;
		}
		if (paintTile == GameStates.collectArtifactStoneTile1 )
		{
			return GameStates.collectArtifactStoneTile1;
		}
		if (paintTile == GameStates.collectArtifactStoneTile2 )
		{
			return GameStates.collectArtifactStoneTile2;
		}
		if (paintTile == GameStates.collectArtifactStoneTile3 )
		{
			return GameStates.collectArtifactStoneTile3;
		}
		
		if (paintTile == GameStates.dirtTrackTile || paintTile == GameStates.dirtTrackTile+1 || paintTile == GameStates.dirtTrackTile-1 || paintTile == GameStates.dirtTrackTile-2 ||paintTile == GameStates.dirtTrackTile-3 ||
				paintTile == GameStates.dirtTrackTile-tsWidth || paintTile == GameStates.dirtTrackTile+1-tsWidth || paintTile == GameStates.dirtTrackTile-1-tsWidth || paintTile == GameStates.dirtTrackTile-2-tsWidth ||paintTile == GameStates.dirtTrackTile-3-tsWidth ||
				paintTile == GameStates.dirtTrackTile+tsWidth || paintTile == GameStates.dirtTrackTile+1+tsWidth || paintTile == GameStates.dirtTrackTile-1+tsWidth || paintTile == GameStates.dirtTrackTile-2+tsWidth ||paintTile == GameStates.dirtTrackTile-3+tsWidth)
			{
				return GameStates.dirtTrackTile;
			}
		if (paintTile == GameStates.cobbleTile || paintTile == GameStates.cobbleTile+1 || paintTile == GameStates.cobbleTile-1 || paintTile == GameStates.cobbleTile-2 ||paintTile == GameStates.cobbleTile-3 ||
				paintTile == GameStates.cobbleTile-tsWidth || paintTile == GameStates.cobbleTile+1-tsWidth || paintTile == GameStates.cobbleTile-1-tsWidth || paintTile == GameStates.cobbleTile-2-tsWidth ||paintTile == GameStates.cobbleTile-3-tsWidth ||
				paintTile == GameStates.cobbleTile+tsWidth || paintTile == GameStates.cobbleTile+1+tsWidth || paintTile == GameStates.cobbleTile-1+tsWidth || paintTile == GameStates.cobbleTile-2+tsWidth ||paintTile == GameStates.cobbleTile-3+tsWidth)
			{
				return GameStates.cobbleTile;
			}
		
		/** fluidObjects */
		for (int i = 0; i < GameStates.fluidObjects.length; i++) {
			int fluidObject = GameStates.fluidObjects[i];
			/** fluidObject tiles */
			if (paintTile == fluidObject || paintTile == fluidObject+1 || paintTile == fluidObject-1 || paintTile == fluidObject-tsWidth-1 ||paintTile == fluidObject-tsWidth ||
					paintTile == fluidObject-tsWidth+1 || paintTile == fluidObject+tsWidth-1 || paintTile == fluidObject+tsWidth || paintTile == fluidObject+tsWidth+1 ||paintTile == fluidObject -tsWidth+2 ||
					paintTile == fluidObject+2 || paintTile == fluidObject+tsWidth+2 ) {
				return fluidObject;
			}
			/** fluidObject Overlay tiles */
			int overlayCorrection = -4*tsWidth;
			if (paintTile == fluidObject+overlayCorrection || paintTile == fluidObject+1+overlayCorrection || paintTile == fluidObject-1+overlayCorrection || paintTile == fluidObject-tsWidth-1+overlayCorrection ||paintTile == fluidObject-tsWidth+overlayCorrection ||
					paintTile == fluidObject-tsWidth+1+overlayCorrection || paintTile == fluidObject+tsWidth-1+overlayCorrection || paintTile == fluidObject+tsWidth+overlayCorrection || paintTile == fluidObject+tsWidth+1 +overlayCorrection||paintTile == fluidObject -tsWidth+2+overlayCorrection ||
					paintTile == fluidObject+2+overlayCorrection || paintTile == fluidObject+tsWidth+2+overlayCorrection 
					|| paintTile == fluidObject+3+overlayCorrection-tsWidth
					|| paintTile == fluidObject+3+overlayCorrection+tsWidth
					|| paintTile == fluidObject+3+overlayCorrection+3*tsWidth
					|| paintTile == fluidObject+3+overlayCorrection+5*tsWidth
					) {
				return fluidObject+overlayCorrection;
			}
			/** fluidObject Addons */
			if (paintTile == fluidObject+2*tsWidth-1 || paintTile == fluidObject+2*tsWidth ||paintTile == fluidObject+2*tsWidth +1||paintTile == fluidObject+2*tsWidth+2) {
				return 1;
			}
		}
		
		
		/** 1: block tree1 for tree-objects, because there is a fluid object arround*/
		if (paintTile == 1)
			return 1;
		
		return paintTile;
	}
	
	/** determine the dimensions */
	public static TileDimensions getTileDimensions(int paintTile) {
//		TileDimensions tileDimensions = TileDimensions.RowCol2x3;
		int tsWidth = GameStates.tileSetWidth;
		// 2x3 tiles
		if (paintTile == GameStates.treeTile || paintTile == GameStates.treeTile-1 || paintTile == GameStates.treeTile+1 ||
				paintTile == GameStates.treeTile-tsWidth-1 || paintTile == GameStates.treeTile-tsWidth || paintTile == GameStates.treeTile-tsWidth+1)
		{
			return TileDimensions.RowCol2x3;
		}
//		if (paintTile == GameStates.thickWoodWallTile || paintTile == GameStates.thickWoodWallTile-1 || paintTile == GameStates.thickWoodWallTile+1 ||
//				paintTile == GameStates.thickWoodWallTile-tsWidth-1 || paintTile == GameStates.thickWoodWallTile-tsWidth || paintTile == GameStates.thickWoodWallTile-tsWidth+1)
//		{
//			return TileDimensions.RowCol2x3;
//		}
		if (paintTile == GameStates.snowTreeTile || paintTile == GameStates.snowTreeTile-1 || paintTile == GameStates.snowTreeTile+1 ||
				paintTile == GameStates.snowTreeTile-tsWidth-1 || paintTile == GameStates.snowTreeTile-tsWidth || paintTile == GameStates.snowTreeTile-tsWidth+1)
		{
			return TileDimensions.RowCol2x3;
		}
		
		if (paintTile == GameStates.stoneTile || paintTile == GameStates.stoneTile-1 || paintTile == GameStates.stoneTile+1 ||
				paintTile == GameStates.stoneTile-tsWidth-1 || paintTile == GameStates.stoneTile-tsWidth || paintTile == GameStates.stoneTile-tsWidth+1)
		{
			return TileDimensions.RowCol2x3;
		}
		// 2x2 tiles
		if (paintTile == GameStates.holeTile || paintTile == GameStates.holeTile-1 || paintTile == GameStates.holeTile+1 ||
				paintTile == GameStates.holeTile-tsWidth-1 || paintTile == GameStates.holeTile-tsWidth || paintTile == GameStates.holeTile-tsWidth+1)
		{
			return TileDimensions.RowCol2x2;
		}
		if (paintTile == GameStates.waterTile || paintTile == GameStates.waterTile-1 || paintTile == GameStates.waterTile+1 ||
				paintTile == GameStates.waterTile-tsWidth-1 || paintTile == GameStates.waterTile-tsWidth || paintTile == GameStates.waterTile-tsWidth+1)
		{
			return TileDimensions.RowCol2x2;
		}
		if (paintTile == GameStates.bigBushTile || paintTile == GameStates.bigBushTile+1 ||
				paintTile == GameStates.bigBushTile-tsWidth || paintTile == GameStates.bigBushTile-tsWidth+1)
		{
			return TileDimensions.RowCol2x2;
		}
		if (paintTile == GameStates.smallTreeTile || paintTile == GameStates.smallTreeTile+1 ||
				paintTile == GameStates.smallTreeTile-tsWidth || paintTile == GameStates.smallTreeTile-tsWidth+1)
		{
			return TileDimensions.RowCol2x2;
		}
		if (paintTile == GameStates.soilBlockTile || paintTile == GameStates.soilBlockTile+1 ||
				paintTile == GameStates.soilBlockTile-tsWidth || paintTile == GameStates.soilBlockTile-tsWidth+1)
		{
			return TileDimensions.RowCol2x3;
		}
		if (paintTile == GameStates.smallStoneTile || paintTile == GameStates.smallStoneTile+1 ||
				paintTile == GameStates.smallStoneTile-tsWidth || paintTile == GameStates.smallStoneTile-tsWidth+1)
		{
			return TileDimensions.RowCol2x2;
		}
		// 1x1 tiles 
		if (paintTile == GameStates.campfireTile)
		{
			return TileDimensions.RowCol1x1;
		}
		if (paintTile == GameStates.smallBushTile)
		{
			return TileDimensions.RowCol1x1;
		}
		if (paintTile == GameStates.collectStoneTile1 || paintTile == GameStates.collectStoneTile2|| paintTile == GameStates.collectStoneTile3 )
		{
			return TileDimensions.RowCol1x1;
		}
		if (paintTile == GameStates.collectArtifactStoneTile1 || paintTile == GameStates.collectArtifactStoneTile2|| paintTile == GameStates.collectArtifactStoneTile3 )
		{
			return TileDimensions.RowCol1x1;
		}
		
		return TileDimensions.RowCol2x3;
	}
	
	/** determine the dimensions */
	public static boolean isPartOfHole(int paintTile) {
		int tsWidth = GameStates.tileSetWidth;
		if (paintTile == GameStates.holeTile  || paintTile == GameStates.holeTile+1 || //paintTile == GameStates.holeTile-1||
			 paintTile == GameStates.holeTile-tsWidth || paintTile == GameStates.holeTile-tsWidth+1
//			 || paintTile == GameStates.holeTile-tsWidth-2  || paintTile == GameStates.holeTile+tsWidth-2
//			 || paintTile == GameStates.holeTile+tsWidth-3 || paintTile == GameStates.holeTile-3
				) // || paintTile == GameStates.holeTile-tsWidth-1 ||
			 //paintTile == GameStates.holeTile+tsWidth || paintTile == GameStates.holeTile+tsWidth+1 || paintTile == GameStates.holeTile+tsWidth-1)
		{
			return true;
		} else {
			return false;
		}
		
	}
	
	/** boolean if the Tile is a bottom-part of a fluid pbject */
	public static boolean isFluidBottom(int paintTile) {
		int tsWidth = GameStates.tileSetWidth;
//		boolean retValue = false;
		
		for (int i = 0; i <GameStates.fluidObjects.length;i++) {		
			int fluidObject = GameStates.fluidObjects[i];
			/** fluidObject tiles */
			if ( paintTile == fluidObject+tsWidth-1 || paintTile == fluidObject+tsWidth || paintTile == fluidObject+tsWidth+1 ||
				paintTile == fluidObject+tsWidth+2 ) {
				return true;
			}
			/** fluidObject Overlay tiles */
			int overlayCorrection = -4*tsWidth;
			if ( paintTile == fluidObject+tsWidth-1+overlayCorrection || paintTile == fluidObject+tsWidth+overlayCorrection || paintTile == fluidObject+tsWidth+1+overlayCorrection ||
				paintTile == fluidObject+tsWidth+2+overlayCorrection 
				|| paintTile == fluidObject+3+overlayCorrection-tsWidth
				|| paintTile == fluidObject+3+overlayCorrection+tsWidth
				|| paintTile == fluidObject+3+overlayCorrection+3*tsWidth
				|| paintTile == fluidObject+3+overlayCorrection+5*tsWidth
					) {
				return true;
			}
		}
		
		return false;
	}
	
	/** check if this is an addon */
	public static boolean isAddon(int paintTile) {
		if (isFluidBottom(paintTile-GameStates.tileSetWidth)) return true;
		else return false;
	}
	
	public static boolean isEmptyOrAddon(int paintTile) {
		if (paintTile <=1 || isAddon(paintTile)) return true;
		else return false;
	}
	
	public static boolean isOverlay(int paintTile) {
		int tsWidth = GameStates.tileSetWidth;
//		boolean retValue = false;
		/** fluidObject Overlay tiles */
		int overlayCorrection = -4*tsWidth;
		for (int i = 0; i< GameStates.fluidObjects.length; i++) {
			int fluidObject = GameStates.fluidObjects[i];
			if (paintTile == fluidObject+overlayCorrection || paintTile == fluidObject+1+overlayCorrection || paintTile == fluidObject-1+overlayCorrection || paintTile == fluidObject-tsWidth-1+overlayCorrection ||paintTile == fluidObject-tsWidth+overlayCorrection ||
					paintTile == fluidObject-tsWidth+1+overlayCorrection || paintTile == fluidObject+tsWidth-1+overlayCorrection || paintTile == fluidObject+tsWidth+overlayCorrection || paintTile == fluidObject+tsWidth+1 +overlayCorrection||paintTile == fluidObject -tsWidth+2+overlayCorrection ||
					paintTile == fluidObject+2+overlayCorrection || paintTile == fluidObject+tsWidth+2+overlayCorrection
					|| paintTile == fluidObject+3+overlayCorrection-tsWidth
					|| paintTile == fluidObject+3+overlayCorrection
					|| paintTile == fluidObject+3+overlayCorrection+tsWidth
					|| paintTile == fluidObject+3+overlayCorrection+2*tsWidth
					) {
				return true;
			}
		}

		return false;
	}
	
	/** this determines how hard it is too destroy a given paintType with tools
	 * and what kind of tool is optimal for the given object
	 * */
	public static TileType getTileType(int paintType) {
//		TileType tileType = TileType.FLOOR;
		if (paintType == GameStates.treeTile) {
			return TileType.TREE;
		}
//		if (paintType == GameStates.thickWoodWallTile) {
//			return TileType.TREE;
//		}
		if (paintType == GameStates.snowTreeTile) {
			return TileType.TREE;
		}
		if (paintType == GameStates.smallTreeTile) {
			return TileType.TREE;
		}
		if (paintType == GameStates.stoneTile) {
			return TileType.STONE;
		}
		if (paintType == GameStates.smallStoneTile) {
			return TileType.STONE;
		}
		if (paintType == GameStates.holeTile) {
			return TileType.HOLE;
		}
		if (paintType == GameStates.waterTile) {
			return TileType.WATER;
		}
		if (paintType == GameStates.soilBlockTile) {
			return TileType.EARTH;
		}
		if (paintType == GameStates.campfireTile) {
			return TileType.COLLECT;
		}
		if (paintType == GameStates.smallBushTile) {
			return TileType.COLLECT;
		}
		if (paintType == GameStates.collectStoneTile1 || paintType == GameStates.collectStoneTile2 || paintType == GameStates.collectStoneTile3) {
			return TileType.COLLECT;
		}
		if (paintType == GameStates.collectArtifactStoneTile1 || paintType == GameStates.collectArtifactStoneTile2 || paintType == GameStates.collectArtifactStoneTile3) {
			return TileType.COLLECT;
		}
		if (paintType == GameStates.bigBushTile) {
			return TileType.TREE;
		}
		if (paintType == GameStates.fluidSoilTile) {
			return TileType.EARTH;
		}
		if (paintType == GameStates.fluidWoodTile) {
			return TileType.TREE;
		}
		if (paintType == GameStates.fluidStoneTile) {
			return TileType.STONE;
		}
		
		return TileType.FLOOR;
	}
	
	/** this determines how hard it is too destroy a given paintType with tools
	 * and what kind of tool is optimal for the given object
	 * */
	public static int getTileLimit(int paintType) {
//		int tileLimit = 100;
		if (paintType == GameStates.treeTile) {
			return 100;
		}
//		if (paintType == GameStates.thickWoodWallTile) {
//			return 60;
//		}
		if (paintType == GameStates.snowTreeTile) {
			return 100;
		}
		if (paintType == GameStates.smallTreeTile) {
			return 60;
		}
		if (paintType == GameStates.stoneTile) {
			return 100;
		}
		if (paintType == GameStates.smallStoneTile) {
			return 60;
		}
		if (paintType == GameStates.holeTile) {
			return 100;
		}
		if (paintType == GameStates.waterTile) {
			return 100;
		}
		if (paintType == GameStates.soilBlockTile) {
			return 100;
		}
		if (paintType == GameStates.campfireTile) {
			return 40;
		}
		if (paintType == GameStates.smallBushTile) {
			return 10;
		}
		if (paintType == GameStates.collectStoneTile1) {
			return 10;
		}
		if (paintType == GameStates.collectStoneTile2) {
			return 10;
		}
		if (paintType == GameStates.collectStoneTile3) {
			return 10;
		}
		if (paintType == GameStates.collectArtifactStoneTile1) {
			return 10;
		}
		if (paintType == GameStates.collectArtifactStoneTile2) {
			return 10;
		}
		if (paintType == GameStates.collectArtifactStoneTile3) {
			return 10;
		}
		if (paintType == GameStates.bigBushTile) {
			return 40;
		}
		if (paintType == GameStates.fluidSoilTile) {
			return 10;
		}
		if (paintType == GameStates.fluidWoodTile) {
			return 40;
		}
		if (paintType == GameStates.fluidStoneTile) {
			return 60;
		}
		
		return 100;
	}
	
	/** this determines the size of items that drop from a specific kind of tile
	 * */
	public static TileSize getTileSize(int paintType) {
//		TileSize tileSize = TileSize.MEDIUM;
		if (paintType == GameStates.treeTile) {
			return TileSize.MEDIUM;
		}
//		if (paintType == GameStates.thickWoodWallTile) {
//			return TileSize.TEN;
//		}
		if (paintType == GameStates.snowTreeTile) {
			return TileSize.MEDIUM;
		}
		if (paintType == GameStates.smallTreeTile) {
			return TileSize.SMALL;
		}
		if (paintType == GameStates.stoneTile) {
			return TileSize.SMALL;
		}
		if (paintType == GameStates.smallStoneTile) {
			return TileSize.TINY;
		}
		if (paintType == GameStates.campfireTile) {
			return TileSize.ONE;
		}
		if (paintType == GameStates.smallBushTile) {
			return TileSize.TWO;
		}
		if (paintType == GameStates.collectStoneTile1) {
			return TileSize.TWO;
		}
		if (paintType == GameStates.collectStoneTile2) {
			return TileSize.THREE;
		}
		if (paintType == GameStates.collectStoneTile3) {
			return TileSize.TWO;
		}
		if (paintType == GameStates.collectArtifactStoneTile1) {
			return TileSize.ONE;
		}
		if (paintType == GameStates.collectArtifactStoneTile2) {
			return TileSize.ONE;
		}
		if (paintType == GameStates.collectArtifactStoneTile3) {
			return TileSize.ONE;
		}
		if (paintType == GameStates.bigBushTile) {
			return TileSize.THREE;
		}
		if (paintType == GameStates.fluidSoilTile) {
			return TileSize.ONE;
		}
		if (paintType == GameStates.fluidWoodTile) {
			return TileSize.ONE;
		}
		if (paintType == GameStates.fluidStoneTile) {
			return TileSize.ONE;
		}
		
		return TileSize.MEDIUM;
	}
	
	/** this determines what kind of item will drop from the given paintTile 
	 * */
	public static ITEMCODE getItemCode(int paintTile) {
//		ITEMCODE retValue = null;
		int tsWidth = GameStates.tileSetWidth;
		
		// 2x3
		if (paintTile == GameStates.treeTile || paintTile == GameStates.treeTile-1 || paintTile == GameStates.treeTile+1 ||
				paintTile == GameStates.treeTile-tsWidth-1 || paintTile == GameStates.treeTile-tsWidth || paintTile == GameStates.treeTile-tsWidth+1)
		{
			return ITEMCODE.WOOD;
		}
//		if (paintTile == GameStates.thickWoodWallTile || paintTile == GameStates.thickWoodWallTile-1 || paintTile == GameStates.thickWoodWallTile+1 ||
//				paintTile == GameStates.thickWoodWallTile-tsWidth-1 || paintTile == GameStates.thickWoodWallTile-tsWidth || paintTile == GameStates.thickWoodWallTile-tsWidth+1)
//		{
//			return ITEMCODE.WOOD;
//		}
		if (paintTile == GameStates.snowTreeTile || paintTile == GameStates.snowTreeTile-1 || paintTile == GameStates.snowTreeTile+1 ||
				paintTile == GameStates.snowTreeTile-tsWidth-1 || paintTile == GameStates.snowTreeTile-tsWidth || paintTile == GameStates.snowTreeTile-tsWidth+1)
		{
			return ITEMCODE.WOOD;
		}
		if (paintTile == GameStates.stoneTile || paintTile == GameStates.stoneTile-1 || paintTile == GameStates.stoneTile+1 ||
				paintTile == GameStates.stoneTile-tsWidth-1 || paintTile == GameStates.stoneTile-tsWidth || paintTile == GameStates.stoneTile-tsWidth+1)
		{
			return ITEMCODE.STONE;
		}
		// 2x2 
		if (paintTile == GameStates.smallTreeTile || paintTile == GameStates.smallTreeTile+1 ||
				paintTile == GameStates.smallTreeTile-tsWidth || paintTile == GameStates.smallTreeTile-tsWidth+1)
		{
			return ITEMCODE.WOOD;
		}
		if (paintTile == GameStates.bigBushTile || paintTile == GameStates.bigBushTile+1 ||
				paintTile == GameStates.bigBushTile-tsWidth || paintTile == GameStates.bigBushTile-tsWidth+1)
		{
			return ITEMCODE.WOOD;
		}
		if (paintTile == GameStates.smallStoneTile || paintTile == GameStates.smallStoneTile+1 ||
				paintTile == GameStates.smallStoneTile-tsWidth || paintTile == GameStates.smallStoneTile-tsWidth+1)
		{
			return ITEMCODE.STONE;
		}
		if (paintTile == GameStates.soilBlockTile || paintTile == GameStates.soilBlockTile+1 ||
				paintTile == GameStates.soilBlockTile-tsWidth || paintTile == GameStates.soilBlockTile-tsWidth+1)
		{
			return ITEMCODE.SOIL;
		}
		if (paintTile == GameStates.campfireTile )
		{
			return ITEMCODE.CAMPFIRE;
		}
		if (paintTile == GameStates.smallBushTile )
		{
			return ITEMCODE.WOOD;
		}
		if (paintTile == GameStates.collectStoneTile1 || paintTile == GameStates.collectStoneTile2 || paintTile == GameStates.collectStoneTile3 )
		{
			return ITEMCODE.STONE;
		}
		if (paintTile == GameStates.collectArtifactStoneTile1 || paintTile == GameStates.collectArtifactStoneTile2 || paintTile == GameStates.collectArtifactStoneTile3 )
		{
			return ITEMCODE.ARTIFACTSTONE;
		}
		if (getPaintType(paintTile) == GameStates.fluidSoilTile )
		{
			return ITEMCODE.SOIL;
		}
		if (getPaintType(paintTile) == GameStates.fluidWoodTile )
		{
			return ITEMCODE.WOOD;
		}
		if (getPaintType(paintTile) == GameStates.fluidStoneTile )
		{
			return ITEMCODE.STONE;
		}
		return null;
	}
	
	public static int getCollisionValue(int tile) {
//		int collisionValue = tile;

		if (tile == GameStates.bigBushTile || tile == GameStates.bigBushTile+1) {
			return 0;
		}
		if (tile == 1) {
			return 0;
		}
		if (isAddon(tile)) {
			return 0;
		}
		return tile;
	}
	
	/** determine the dimensions */
	public static boolean isFluid(int paintTile) {
//		boolean retValue = false;
		int tsWidth = GameStates.tileSetWidth;
		
		for (int i = 0; i < GameStates.fluidObjects.length; i++) {
			int fluidObject = GameStates.fluidObjects[i];
			if (paintTile == fluidObject  || paintTile == fluidObject+1 || paintTile == fluidObject-1||
				 paintTile == fluidObject-tsWidth || paintTile == fluidObject-tsWidth+1 || paintTile == fluidObject-tsWidth-1
				 || paintTile == fluidObject+tsWidth || paintTile == fluidObject+tsWidth+1 || paintTile == fluidObject+tsWidth-1
				 || paintTile == fluidObject+2*tsWidth || paintTile == fluidObject+2*tsWidth+1 || paintTile == fluidObject+2*tsWidth-1
				 || paintTile == fluidObject-tsWidth+2 || paintTile == fluidObject+2 || paintTile == fluidObject+tsWidth+2 || paintTile == fluidObject+2*tsWidth+2
					) 

			{
				return true;
			} 
		}
		
		return false;
	}
	
	/** lightSources */
	public static boolean isLighning(int paintType) {
		if(paintType == GameStates.campfireTile) return true;
		
		if (paintType == GameStates.collectArtifactStoneTile1 || paintType == GameStates.collectArtifactStoneTile2 || paintType == GameStates.collectArtifactStoneTile3)
			return true;
		
		return false;
	}
	
	/** lightSources */
	public static boolean isBurning(int paintType) {
		if(paintType == GameStates.campfireTile) return true;
		
		return false;
	}
	
	public void setUl(int x, int y, int id) {
		if (ulArray != null) {
			ulArray[x][y] = id;
		}
	}
	public void setUr(int x, int y, int id) {
		if (urArray != null) {
			urArray[x][y] = id;
		}
	}
	public void setDl(int x, int y, int id) {
		if (dlArray != null) {
			dlArray[x][y] = id;
		}
	}
	public void setDr(int x, int y, int id) {
		if (drArray != null) {
			drArray[x][y] = id;
		}
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

	public boolean isInitialized() {
		return initialized;
	}

	public void setInitialized(boolean initialized) {
		this.initialized = initialized;
	}
}
