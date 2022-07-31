package de.svenheins.objects;

import java.awt.Point;
import java.math.BigInteger;

import de.svenheins.main.GameStates;

public class Light {
	private int brightness;
	private Point location;
	private BigInteger id;
	private Entity entity;

	/** standard constructor */
	public Light(BigInteger id, int brightness, Point location) {
		this.setId(id);
		this.setBrightness(brightness);
		this.entity = null;
//		this.setLocation(location);
		/** set location in a lattice of the tiles */
		this.setLocation(new Point(location.x - (location.x % GameStates.lightTileWidth), location.y - (location.y % GameStates.lightTileHeight)) ); 
	}
	
	/** standard constructor */
	public Light(BigInteger id, int brightness, Entity entity) {
		this.setId(id);
		this.setBrightness(brightness);
//		this.setLocation(location);
		/** set location in a lattice of the tiles */
		this.entity = entity;
	}

	public int getBrightness() {
		return brightness;
	}

	public void setBrightness(int brightness) {
		this.brightness = brightness;
	}

	public Point getLocation() {
		if (entity != null) {
			System.out.println("here");
			return new Point( (int) entity.getX() - ((int) entity.getX() % GameStates.lightTileWidth), (int) entity.getY() - ((int) entity.getY() % GameStates.lightTileHeight));
		} else
		return location;
	}

	public void setLocation(Point location) {
		this.location = location;
	}

	public BigInteger getId() {
		return id;
	}

	public void setId(BigInteger id) {
		this.id = id;
	}
	
	public void setEntity(Entity ent) {
		this.entity = ent;
	}
	
	public Entity getEntity() {
		return this.entity;
	}
}
