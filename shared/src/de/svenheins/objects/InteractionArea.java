package de.svenheins.objects;

import java.awt.Point;
import java.util.ArrayList;

import de.svenheins.main.GameStates;

public class InteractionArea {
	private WorldPosition position;
	private int[] values;
	private int width;
	private int height;
	
	
	
	public InteractionArea(WorldPosition position, int[] values, int width,
			int height) {
		super();
		this.position = position;
		this.values = values;
		this.width = width;
		this.height = height;
	}
	
	public ArrayList<WorldLatticePosition> getInfluencedInteractionTilePositions() {
		ArrayList<WorldLatticePosition> retList = new ArrayList<WorldLatticePosition>();
		int localWidth = GameStates.mapWidth * GameStates.mapTileSetWidth;
		int localHeight = GameStates.mapHeight * GameStates.mapTileSetHeight;
//		int latticePointULX = (int) Math.floor( (float) position.getX() / (localWidth)) * localWidth;
//		int latticePointULY = (int) Math.floor( (float) position.getY() / (localHeight)) * localHeight;
//		int localULX = (int) Math.floor( (float) (position.getX() - latticePointULX )/ GameStates.mapTileSetWidth);
//		int localULY = (int) Math.floor( (float) (position.getY() - latticePointULY )/ GameStates.mapTileSetHeight);
//		
//		int latticePointURX = (int) Math.floor( (float) (position.getX()+this.getWidth()) / (localWidth)) * localWidth;
//		int latticePointURY = (int) Math.floor( (float) position.getY() / (localHeight)) * localHeight;
//		int localURX = (int) Math.floor( (float) ((position.getX()+this.getWidth()) - latticePointURX )/ GameStates.mapTileSetWidth);
//		int localURY = (int) Math.floor( (float) (position.getY() - latticePointURY )/ GameStates.mapTileSetHeight);
//		
//		int latticePointDLX = (int) Math.floor( (float) position.getX() / (localWidth)) * localWidth;
//		int latticePointDLY = (int) Math.floor( (float) (position.getY()+this.getHeight()) / (localHeight)) * localHeight;
//		int localDLX = (int) Math.floor( (float) (position.getX() - latticePointDLX )/ GameStates.mapTileSetWidth);
//		int localDLY = (int) Math.floor( (float) ((position.getY()+this.getHeight()) - latticePointDLY )/ GameStates.mapTileSetHeight);
//		
//		int latticePointDRX = (int) Math.floor( (float) (position.getX()+this.getWidth()) / (localWidth)) * localWidth;
//		int latticePointDRY = (int) Math.floor( (float) (position.getY()+this.getHeight()) / (localHeight)) * localHeight;
//		int localDRX = (int) Math.floor( (float) ((position.getX()+this.getWidth()) - latticePointDRX )/ GameStates.mapTileSetWidth);
//		int localDRY = (int) Math.floor( (float) ((position.getY()+this.getHeight()) - latticePointDRY )/ GameStates.mapTileSetHeight);
		
		int limitX = (int) Math.ceil( ((double) this.getWidth())/ GameStates.mapTileSetWidth);
		int limitY = (int) Math.ceil( ((double) this.getHeight())/ GameStates.mapTileSetHeight);
		int latticePointX; //= (int) Math.floor( (float) (position.getX()+this.getWidth()) / (localWidth)) * localWidth;
		int latticePointY; //= (int) Math.floor( (float) (position.getY()+this.getHeight()) / (localHeight)) * localHeight;
		int localX; //= (int) Math.floor( (float) ((position.getX()+this.getWidth()) - latticePointDRX )/ GameStates.mapTileSetWidth);
		int localY; //= (int) Math.floor( (float) ((position.getY()+this.getHeight()) - latticePointDRY )/ GameStates.mapTileSetHeight);
//		WorldLatticePosition worldLatticePositionAdd;// = new WorldLatticePosition(mapCoordinates, localX, localY, room)
		for (int i = 0; i<= limitY; i++) {
			for (int j =0 ; j<= limitX; j++) {
				latticePointX = (int) Math.floor( (float) (position.getX()+i*GameStates.mapTileSetWidth) / (localWidth)) * localWidth;
				latticePointY = (int) Math.floor( (float) (position.getY()+j*GameStates.mapTileSetHeight) / (localHeight)) * localHeight;
				localX = (int) Math.floor( (float) ((position.getX()+i*GameStates.mapTileSetWidth) - latticePointX )/ GameStates.mapTileSetWidth);
				localY = (int) Math.floor( (float) ((position.getY()+j*GameStates.mapTileSetHeight) - latticePointY )/ GameStates.mapTileSetHeight);
				retList.add(new WorldLatticePosition(new Point(latticePointX, latticePointY), localX, localY));
			}
		}
		return retList;
	}
	
	public WorldPosition getPosition() {
		return position;
	}
	public void setPosition(WorldPosition position) {
		this.position = position;
	}
	public int[] getValues() {
		return values;
	}
	public void setValues(int[] values) {
		this.values = values;
	}
	public int getWidth() {
		return width;
	}
	public void setWidth(int width) {
		this.width = width;
	}
	public int getHeight() {
		return height;
	}
	public void setHeight(int height) {
		this.height = height;
	}
	
}
