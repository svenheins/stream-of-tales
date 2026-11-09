/* Copyright (C) Sven Heins - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * author: Sven Heins
 * date: 05.07.2015
 */
package de.svenheins.objects;

import java.math.BigInteger;


public class GrowthSpot {
	/** ID */
	private BigInteger id;	
	/** begin and end time of the influence */
	private int tileType;
	private long timeBegin;
	private long timeEnd;
	private int mapTileX;
	private int mapTileY;
	private int localX;
	private int localY;
	private String room;
	/** values are InfluenceType enums */
	private float[] attributes;
	/** creator */
	private String creator;

	
	public GrowthSpot(BigInteger id, int tyleType, int localX, int localY, int mapTileX, int mapTileY, String room, long timeBegin, long timeEnd, float[] attributes, String creator) {
		this.setId(id);
		this.setTileType(tyleType);
		this.setMapTileX(mapTileX);
    	this.setMapTileY(mapTileY);
    	this.setLocalX(localX);
    	this.setLocalY(localY);
    	this.setRoom(room);
		this.setTimeBegin(timeBegin);
		this.setTimeEnd(timeEnd);
		this.setAttributes(attributes);	
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


	public float[] getAttributes() {
		return attributes;
	}
	public void setAttributes(float[] attributes) {
		this.attributes = attributes;
	}

	

	public BigInteger getId() {
		return id;
	}

	public void setId(BigInteger id) {
		this.id = id;
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
	public String getRoom() {
		return room;
	}

	public void setRoom(String room) {
		this.room = room;
	}

	public int getTileType() {
		return tileType;
	}

	public void setTileType(int tileType) {
		this.tileType = tileType;
	}
}
