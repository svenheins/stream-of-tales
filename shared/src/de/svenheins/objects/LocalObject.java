package de.svenheins.objects;

import java.math.BigInteger;

public class LocalObject {
	protected float x, y;
	protected float zIndex;
	protected float height, width;
	protected float mx;
	protected float my;
	protected BigInteger id;
	protected String name;

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
	
	public float getMX() {
		return mx;
	}
	
	public float getVerticalMovement() {
		return my;
	}
	
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
}
