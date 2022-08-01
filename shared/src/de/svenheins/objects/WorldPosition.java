package de.svenheins.objects;

import java.awt.Point;

import de.svenheins.main.GameStates;

public class WorldPosition {
	private float x;
	private float y;
	private String room;
	
	
	public WorldPosition(float x,
			float y) {
		super();

		this.x = x;
		this.y = y;
		this.room = "";
//		this.room = room;
	}
	
	public static WorldPosition getWorldPosition(WorldLatticePosition position) {
		return new WorldPosition((float) position.getMapCoordinates().getX() + position.getLocalX()*GameStates.mapTileSetWidth,(float) position.getMapCoordinates().getY() + position.getLocalY()*GameStates.mapTileSetHeight);
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((room == null) ? 0 : room.hashCode());
		result = prime * result + Float.floatToIntBits(x);
		result = prime * result + Float.floatToIntBits(y);
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
		WorldPosition other = (WorldPosition) obj;
		if (room == null) {
			if (other.room != null)
				return false;
		} else if (!room.equals(other.room))
			return false;
		if (Float.floatToIntBits(x) != Float.floatToIntBits(other.x))
			return false;
		if (Float.floatToIntBits(y) != Float.floatToIntBits(other.y))
			return false;
		return true;
	}

	public float getX() {
		return x;
	}
	public void setX(float x) {
		this.x = x;
	}
	public float getY() {
		return y;
	}
	public void setY(float y) {
		this.y = y;
	}
	public String getRoom() {
		return room;
	}
	public void setRoom(String room) {
		this.room = room;
	}
}
