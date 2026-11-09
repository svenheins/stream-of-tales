/* Copyright (C) Sven Heins - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * author: Sven Heins
 * date: 05.07.2015
 */
package de.svenheins.objects;



import java.math.BigInteger;
import java.util.logging.Logger;

import com.sun.sgs.app.AppContext;
import com.sun.sgs.app.ManagedReference;

import de.svenheins.WorldRoom;
import de.svenheins.messages.ITEMCODE;



public class ServerItem extends WorldObject{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private int count;
	private int capacity;
	private String name;
	private String comment;
	private BigInteger id;
	private ITEMCODE itemCode;
	private ManagedReference<ServerEntity> itemEntity;
	private long creationTime;
	private float[] states;
    

	/** The {@link WorldRoom} this player is in, or null if none. */
    protected ManagedReference<WorldRoom> currentRoomRef = null;
    /** The {@link Logger} for this class. */
    protected static final Logger logger =
        Logger.getLogger(ServerEntity.class.getName());
	
	
    public ServerItem (BigInteger id, ITEMCODE itemCode, String name, String comment, String room, ServerEntity itemEntity, int count, int capacity, float[] states) {
    	setId(id);
    	setItemCode(itemCode);
    	setName(name);
    	setComment(comment);
    	setRoom(room);
    	setItemEntity(AppContext.getDataManager().createReference(itemEntity));
    	setCount(count);
    	setCapacity(capacity);
    	setStates(states);
    	setX(itemEntity.getX());
    	setY(itemEntity.getY());
    	setCreationTime(System.currentTimeMillis());
    }


	public int getCount() {
		return count;
	}


	public void setCount(int count) {
		this.count = count;
	}


	public int getCapacity() {
		return capacity;
	}


	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public BigInteger getId() {
		return id;
	}


	public void setId(BigInteger id) {
		this.id = id;
	}


	public ITEMCODE getItemCode() {
		return itemCode;
	}


	public void setItemCode(ITEMCODE itemCode) {
		this.itemCode = itemCode;
	}


	public ManagedReference<ServerEntity> getItemEntity() {
		return itemEntity;
	}


	public void setItemEntity(ManagedReference<ServerEntity> itemEntity) {
		this.itemEntity = itemEntity;
	}


	public long getCreationTime() {
		return creationTime;
	}


	public void setCreationTime(long creationTime) {
		this.creationTime = creationTime;
	}


	public float[] getStates() {
		return states;
	}


	public void setStates(float[] states) {
		this.states = states;
	}


	public String getComment() {
		return comment;
	}


	public void setComment(String comment) {
		this.comment = comment;
	}
}
