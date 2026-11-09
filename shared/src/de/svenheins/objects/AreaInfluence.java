/* Copyright (C) Sven Heins - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * author: Sven Heins
 * date: 05.07.2015
 */
package de.svenheins.objects;

import java.math.BigInteger;

import de.svenheins.main.Priority;

public class AreaInfluence {
	/** ID */
	private BigInteger id;	
	/** begin and end time of the influence */
	private long timeBegin;
	private long timeEnd;
//	/** entity is a short visual effect for the influence and the position, height, width, mx, my*/
//	private Entity entity;
	private LocalObject localObject;
	/** groupName defines the group, that is effected by the influence */
	private String groupName;
	/** exclude group or include? */
	private boolean exclusive;
	/** values are InfluenceType enums */
	private float[] attributes;
	/** priority */
	private Priority priority;
	/** creator */
	private String creator;

	
	public AreaInfluence(BigInteger id, long timeBegin, long timeEnd, LocalObject localObject, String groupName, boolean exclusive, float[] attributes, Priority priority, String creator) {
		this.setId(id);
		this.setTimeBegin(timeBegin);
		this.setTimeEnd(timeEnd);
		this.setLocalObject(localObject);
		this.setGroupName(groupName);
		this.setExclusive(exclusive);
		this.setAttributes(attributes);	
		this.setPriority(priority);
		this.setCreator(creator);
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

	public boolean isExclusive() {
		return exclusive;
	}

	public void setExclusive(boolean exclusive) {
		this.exclusive = exclusive;
	}

	public BigInteger getId() {
		return id;
	}

	public void setId(BigInteger id) {
		this.id = id;
	}

	
	public LocalObject getLocalObject() {
		return localObject;
	}

	public void setLocalObject(LocalObject localObject) {
		this.localObject = localObject;
	}
	
	public void setX(float x) {
		this.localObject.setX(x);
	}
	
	public void setY(float y) {
		this.localObject.setY(y);
	}

	public String getCreator() {
		return creator;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}

}
