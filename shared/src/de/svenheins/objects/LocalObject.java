package de.svenheins.objects;

public class LocalObject {
	protected double x, y;
	protected double zIndex;
	protected double height, width;
	protected double mx;
	protected double my;
	protected int id;
	protected String name;

	public double getZIndex() {
		return zIndex;
	}

	public void setZIndex(double zIndex) {
		this.zIndex = zIndex;
	}

	public double getX() {
		return x;
	}
	
	public double getY() {
		return y;
	}
	
	public void setX(double x) {
		this.x = x;
	}

	public void setY(double y) {
		this.y = y;
	}
	
	public void setHorizontalMovement(double hm) {
		mx = hm;
	}
	
	public void setVerticalMovement(double vm) {
		my = vm;
	}
	
	public void setMovement(double mx, double my) {
		this.mx = mx;
		this.my = my;
	}
	
	public double getHorizontalMovement() {
		return mx;
	}
	
	public double getVerticalMovement() {
		return my;
	}

	public double getHeight() {
		return this.height;
	}
	
	
	public void setHeight(double height){
		this.height = height;
	}

	public double getWidth() {
		return this.width;
	}
	
	public void setWidth(double width){
		this.width = width;
	}
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return this.name;
	}
}
