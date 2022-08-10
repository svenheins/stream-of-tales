package de.svenheins.objects;

import de.svenheins.main.Priority;

public class ItemInfluence {
	/** begin and end time of the influence */
	private long timeBegin;
	private long timeEnd;
//	/** entity is a short visual effect for the influence and the position, height, width, mx, my*/
//	private Entity entity;
//	/** groupName defines the group, that is effected by the influence */
//	private String groupName;
//	/** exclude group or include? */
//	private boolean exclusive;
	/** values are AttributeType floats */
	private float[] attributes;
	/** priority */
	private Priority priority;
	
	public ItemInfluence(long timeBegin, long timeEnd, float[] attributes, Priority priority) {
		this.setTimeBegin(timeBegin);
		this.setTimeEnd(timeEnd);
		this.setAttributes(attributes);	
		this.setPriority(priority);
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

	public Priority getPriority() {
		return priority;
	}

	public void setPriority(Priority priority) {
		this.priority = priority;
	}

}
