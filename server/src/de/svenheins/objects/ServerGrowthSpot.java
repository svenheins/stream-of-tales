package de.svenheins.objects;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.logging.Logger;

import com.sun.sgs.app.AppContext;
import com.sun.sgs.app.ManagedObject;
import com.sun.sgs.app.ManagedReference;

import de.svenheins.WorldRoom;
import de.svenheins.main.Priority;

public class ServerGrowthSpot implements Serializable, ManagedObject {
private static final long serialVersionUID = 1L;
	

	private BigInteger id;
	/** begin and end time of the influence */
	private int tileType;
	private long timeBegin;
	private long timeEnd;
	private int mapTileX;
	private int mapTileY;
	private int localX;
	private int localY;
	/** groupName defines the group, that is effected by the influence */
	private String room;
	/** values are GrowthSpot enums */
	private float[] attributes;
	/** creator */
	private String creator;
	
	/** The {@link WorldRoom} this player is in, or null if none. */
    protected ManagedReference<WorldRoom> currentRoomRef = null;
    /** The {@link Logger} for this class. */
    protected static final Logger logger =
        Logger.getLogger(ServerEntity.class.getName());
    
    
    public ServerGrowthSpot(BigInteger id, int tileType, int localX, int localY,int mapTileX, int mapTileY,long timeBegin, long timeEnd, String room,float[] attributes, String creator) {
    	this.setId(id);
    	this.setTileType(tileType);
    	this.setMapTileX(mapTileX);
    	this.setMapTileY(mapTileY);
    	this.setLocalX(localX);
    	this.setLocalY(localY);
    	this.setTimeBegin(timeBegin);
    	this.setTimeEnd(timeEnd);  
    	this.setAttributes(attributes);
    	this.setRoom(room);
    	this.setCreator(creator);
    }
    
    public BigInteger getId() {
    	return this.id;
    }
    
    public void setId(BigInteger id) {
    	this.id = id;
    }
    
	public long getTimeBegin() {
		return timeBegin;
	}
	public void setTimeBegin(long timeBegin) {
		this.timeBegin = timeBegin;
	}
	public long getTimeEnd() {
		return timeEnd;
	}
	public void setTimeEnd(long timeEnd) {
		this.timeEnd = timeEnd;
	}
	
	public float[] getAttributes() {
		return attributes;
	}
	public void setAttributes(float[] attributes) {
		this.attributes = attributes;
	}
	
	public String getRoom() {
		return room;
	}

	public void setRoom(String room) {
		this.room = room;
	}

	public String getCreator() {
		return creator;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}

	public int getMapTileX() {
		return mapTileX;
	}

	public void setMapTileX(int mapTileX) {
		this.mapTileX = mapTileX;
	}

	public int getMapTileY() {
		return mapTileY;
	}

	public void setMapTileY(int mapTileY) {
		this.mapTileY = mapTileY;
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

	public int getTileType() {
		return tileType;
	}

	public void setTileType(int tileType) {
		this.tileType = tileType;
	}
}
