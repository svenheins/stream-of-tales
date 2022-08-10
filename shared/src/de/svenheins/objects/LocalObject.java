package de.svenheins.objects;

import java.awt.Point;
import java.awt.Rectangle;
import java.math.BigInteger;

public class LocalObject {
	protected float x, y;
	protected float zIndex;
	protected float height, width;
	protected float mx;
	protected float my;
	protected float maxSpeed;
	protected BigInteger id;
	protected String name;
	
	public LocalObject() {
		float standardValue = 0.0f;
		this.id = BigInteger.valueOf(0);
		this.x = standardValue;
		this.y = standardValue;
		this.height = standardValue;
		this.width = standardValue;
		this.mx = standardValue;
		this.my = standardValue;
		this.zIndex = standardValue;
		this.maxSpeed = standardValue;
		this.name = "";
	}
	
	public LocalObject(BigInteger id, String name, float x, float y, float width, float height, float mx, float my, float zIndex, float maxSpeed) {
		this.setId(id);
		this.setX(x);
		this.setY(y);
		this.setHeight(height);
		this.setWidth(width);
		this.setMX(mx);
		this.setMY(my);
		this.setZIndex(zIndex);
		this.setMaxSpeed(maxSpeed);
		this.setName(name);
	}

	public float getZIndex() {
		return zIndex;
	}

	public void setZIndex(float zIndex) {
		this.zIndex = zIndex;
	}

	public float getX() {
		return x;
	}
	
	public float getY() {
		return y;
	}
	
	public void setX(float x) {
		this.x = x;
	}

	public void setY(float y) {
		this.y = y;
	}
	
	public void setMX(float hm) {
		mx = hm;
	}
	
	public void setMY(float vm) {
		my = vm;
	}
	
	public void setMovement(float mx, float my) {
		this.mx = mx;
		this.my = my;
	}
	
	public void normalizeMovement() {
		float tempVelocity = (float) Math.sqrt((mx*mx) + (my*my));
		if ( tempVelocity > maxSpeed) {
			this.setMovement(maxSpeed*(mx/tempVelocity), maxSpeed*(my/tempVelocity));
		} else {
			// not too fast
		}
	}
//	
//	public float getHorizontalMovement() {
//		return mx;
//	}
	
	public float getMX() {
		return mx;
	}
	
//	public float getVerticalMovement() {
//		return my;
//	}
	
	public float getMY() {
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
	
	public BigInteger getId() {
		return id;
	}
	
	public void setId(BigInteger id) {
		this.id = id;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return this.name;
	}

	public float getMaxSpeed() {
		return maxSpeed;
	}

	public void setMaxSpeed(float maxSpeed) {
		this.maxSpeed = maxSpeed;
	}
	
	public boolean collides(LocalObject otherLocalObject) {
		//Sprite sprite = animation.getSprite(System.currentTimeMillis(), this.standardAnimation);
		Rectangle r1 = new Rectangle((int) x, (int) y, (int) this.getWidth(), (int) this.getHeight());
		Rectangle r2 = new Rectangle((int) otherLocalObject.x, (int) otherLocalObject.y, (int) otherLocalObject.getWidth(), (int) otherLocalObject.getHeight());
		return r1.intersects(r2);
	}
	
	public boolean contains(Point p) {
		//Sprite sprite = animation.getSprite(System.currentTimeMillis(), this.standardAnimation);
		Rectangle r1 = new Rectangle((int) x, (int) y, (int) this.getWidth(), (int) this.getHeight());
		return r1.contains(p);
	}
}
