/*
 * Copyright 2007-2010 Sun Microsystems, Inc.
 *
 * This file is part of Project Darkstar Server.
 *
 * Project Darkstar Server is free software: you can redistribute it
 * and/or modify it under the terms of the GNU General Public License
 * version 2 as published by the Free Software Foundation and
 * distributed hereunder to you.
 *
 * Project Darkstar Server is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * --
 */

package de.svenheins.objects;

import java.io.Serializable;
import java.math.BigInteger;

import com.sun.sgs.app.AppContext;
import com.sun.sgs.app.ManagedObject;

/**
 * A {@code ManagedObject} that has a name, a description and coordinates (x/y)
 */
public class WorldObject
    implements Serializable, ManagedObject
{
    /** The version of the serialized form of this class. */
    private static final long serialVersionUID = 1L;

    /** The name of this object. */
    private String name;

    /** The description of this object. */
    private String description;
    
	protected BigInteger id;
    
    /** location of the Object    */
    protected float x;

	protected float y;
    
    protected float zIndex;
	protected float height, width;
	protected float mx;
	protected float my;
	
	protected float speed;
	

    /**
     * Creates a new {@code WorldObject} with the given {@code name}
     * and {@code description}.
     *
     * @param name the name of this object
     * @param description the description of this object
     */
    public WorldObject(String name, String description, float x, float y) {
        this.name = name;
        this.description = description;
        this.x = x;
        this.y = y;
        this.id = BigInteger.valueOf(0);
    }
    
    
    public WorldObject() {
        this.name = "standard";
        this.description = "description";
        this.x = 0;
        this.y = 0;
        this.id = BigInteger.valueOf(0);
    }

    /**
     * Sets the name of this object.
     *
     * @param name the name of this object
     */
    public void setName(String name) {
        AppContext.getDataManager().markForUpdate(this);
        this.name = name;
    }

    /**
     * Returns the name of this object.
     *
     * @return the name of this object
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the description of this object.
     *
     * @param description the description of this object
     */
    public void setDescription(String description) {
        AppContext.getDataManager().markForUpdate(this);
        this.description = description;
    }

    /**
     * Returns the description of this object.
     *
     * @return the description of this object
     */
    public String getDescription() {
        return description;
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return getName();
    }
    
    public BigInteger getId() {
    	return this.id;
    }
    
    public void setId(BigInteger id) {
    	this.id = id;
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
	
	public float getZIndex() {
		return zIndex;
	}

	public void setZIndex(float zIndex) {
		this.zIndex = zIndex;
	}
	
	public void setHorizontalMovement(float hm) {
		mx = hm;
	}
	
	public void setVerticalMovement(float vm) {
		my = vm;
	}
	
	public void setMovement(float mx, float my) {
		this.mx = mx;
		this.my = my;
	}
	
	public float getHorizontalMovement() {
		return mx;
	}
	
	public float getVerticalMovement() {
		return my;
	}

	public float getHeight() {
		return this.height;
	}
	
	
	public void setHeight(float height){
		this.height = height;
	}

	public float getWidth() {
		return this.width;
	}
	
	public void setWidth(float width){
		this.width = width;
	}
	
	public float getSpeed() {
		return speed;
	}
	
	public void setSpeed(float speed) {
		this.speed = speed;
	}
}