package de.svenheins.objects;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.logging.Logger;

import com.sun.sgs.app.AppContext;
import com.sun.sgs.app.ManagedObject;
import com.sun.sgs.app.ManagedReference;

import de.svenheins.WorldRoom;
import de.svenheins.main.Priority;

public class ServerAreaInfluence implements Serializable, ManagedObject {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	

	private BigInteger id;
	/** entity is a short visual effect for the influence and the position, height, width, mx, my*/
	private ManagedReference<WorldObject> worldObject;
	
	/** begin and end time of the influence */
	private long timeBegin;
	private long timeEnd;
	/** groupName defines the group, that is effected by the influence */
	private String groupName;
	/** exclude group or include? */
	private boolean exclusive;
	/** values are InfluenceType enums */
	private float[] attributes;
	/** priority */
	private Priority priority;
	
	/** The {@link WorldRoom} this player is in, or null if none. */
    protected ManagedReference<WorldRoom> currentRoomRef = null;
    /** The {@link Logger} for this class. */
    protected static final Logger logger =
        Logger.getLogger(ServerEntity.class.getName());
    
    
    public ServerAreaInfluence(BigInteger id, long timeBegin, long timeEnd, String groupName, boolean exclusive, WorldObject worldObject, float[] attributes, Priority priority) {
    	this.setId(id);
    	this.setTimeBegin(timeBegin);
    	this.setTimeEnd(timeEnd);
    	this.setGroupName(groupName);
    	this.setExclusive(exclusive);
    	this.setWorldObject(AppContext.getDataManager().createReference(worldObject));
    	this.setAttributes(attributes);
    	this.setPriority(priority);
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
	public String getGroupName() {
		return groupName;
	}
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	public boolean isExclusive() {
		return exclusive;
	}
	public void setExclusive(boolean exclusive) {
		this.exclusive = exclusive;
	}
	public float[] getAttributes() {
		return attributes;
	}
	public void setAttributes(float[] attributes) {
		this.attributes = attributes;
	}
	public Priority getPriority() {
		return priority;
	}
	public void setPriority(Priority priority) {
		this.priority = priority;
	}

	public ManagedReference<WorldObject> getWorldObject() {
		return worldObject;
	}

	public void setWorldObject(ManagedReference<WorldObject> worldObject) {
		this.worldObject = worldObject;
	}

    
}
