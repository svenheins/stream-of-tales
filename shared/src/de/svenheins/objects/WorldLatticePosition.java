package de.svenheins.objects;

import java.awt.Point;

import de.svenheins.main.GameStates;

public class WorldLatticePosition {
	private Point mapCoordinates;
	private int localX;
	private int localY;
	private String room;
	
	public WorldLatticePosition(Point mapCoordinates, int localX, int localY) {
		super();
		this.mapCoordinates = mapCoordinates;
		this.localX = localX;
		this.localY = localY;
		this.room = "";
//		this.room = room;
	}
	
	public static WorldLatticePosition getWorldLatticePosition(WorldPosition position) {
		int localWidth = GameStates.mapTotalWidth;
		int localHeight = GameStates.mapTotalHeight;
		
		int latticePointX = (int) Math.floor( (float) (position.getX()) / (localWidth)) * localWidth;
		int latticePointY = (int) Math.floor( (float) (position.getY()) / (localHeight)) * localHeight;
		int localX = (int) Math.floor( (float) (position.getX() - latticePointX )/ GameStates.mapTileSetWidth);
		int localY = (int) Math.floor( (float) ( position.getY() - latticePointY )/ GameStates.mapTileSetHeight);
		
		return new WorldLatticePosition(new Point(latticePointX, latticePointY), localX, localY);
	}
	
	public static WorldLatticePosition getClosestWorldLatticePosition(WorldPosition position) {
		int localWidth = GameStates.mapTotalWidth;
		int localHeight = GameStates.mapTotalHeight;
		
		int latticePointX = (int) Math.floor( (float) (position.getX()) / (localWidth)) * localWidth;
		int latticePointY = (int) Math.floor( (float) (position.getY()) / (localHeight)) * localHeight;
		int localXFloor = (int) Math.floor( (float) (position.getX() - latticePointX )/ GameStates.mapTileSetWidth);
		int localYFloor = (int) Math.floor( (float) ( position.getY() - latticePointY )/ GameStates.mapTileSetHeight);
		int localXCeiling = (int) Math.ceil( (float) (position.getX() - latticePointX )/ GameStates.mapTileSetWidth);
		int localYCeiling = (int) Math.ceil( (float) ( position.getY() - latticePointY )/ GameStates.mapTileSetHeight);
		
		int localX,localY;
		
		if (Math.abs(latticePointX+localXFloor*GameStates.mapTileSetWidth-position.getX()) < Math.abs(latticePointX+localXCeiling*GameStates.mapTileSetWidth-position.getX())) {
			localX = localXFloor;
		} else {
			localX = localXCeiling;
		}
		if (Math.abs(latticePointY+localYFloor*GameStates.mapTileSetHeight-position.getY()) < Math.abs(latticePointY+localYCeiling*GameStates.mapTileSetHeight-position.getY())) {
			localY = localYFloor;
		} else {
			localY = localYCeiling;
		}
		
		return new WorldLatticePosition(new Point(latticePointX, latticePointY), localX, localY);
	}
	
	
@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + localX;
		result = prime * result + localY;
		result = prime * result
				+ ((mapCoordinates == null) ? 0 : mapCoordinates.hashCode());
		result = prime * result + ((room == null) ? 0 : room.hashCode());
		return result;
	}



	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		WorldLatticePosition other = (WorldLatticePosition) obj;
		if (localX != other.localX)
			return false;
		if (localY != other.localY)
			return false;
		if (mapCoordinates == null) {
			if (other.mapCoordinates != null)
				return false;
		} else if (!mapCoordinates.equals(other.mapCoordinates))
			return false;
		if (room == null) {
			if (other.room != null)
				return false;
		} else if (!room.equals(other.room))
			return false;
		return true;
	}



	//	public boolean equals(Object obj)
//	  {
//	    if ( (((WorldLatticePosition)obj).getMapCoordinates().equals(mapCoordinates))
//	        && (((WorldLatticePosition)obj).getLocalX() == localX) 
//	        && (((WorldLatticePosition)obj).getLocalY() == localY)) 
//	        return true;
//	    return false;
//	  }
//	
	public Point getMapCoordinates() {
		return mapCoordinates;
	}
	public void setMapCoordinates(Point mapCoordinates) {
		this.mapCoordinates = mapCoordinates;
	}
	public int getLocalX() {
		return localX;
	}
	public void setLocalX(int localX) {
		this.localX = localX;
	}
	public int getLocalY() {
		return localY;
	}
	public void setLocalY(int localY) {
		this.localY = localY;
	}
	public String getRoom() {
		return room;
	}
	public void setRoom(String room) {
		this.room = room;
	}
	
	
}
