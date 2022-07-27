package de.svenheins.objects;

import java.io.Serializable;

import com.sun.sgs.app.ManagedObject;


public class ServerPolygon implements Serializable, ManagedObject {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected int[] polyX;
	protected int[] polyY;

	public ServerPolygon(int[] polyX, int[] polyY) {
		this.polyX = polyX;
		this.polyY = polyY;
	}
	
	public int[] getPolyX() {
		return polyX;
	}
	
	public int[] getPolyY() {
		return polyY;
	}
	
	public void setPolyX(int[] polyX) {
		this.polyX = polyX;
	}
	
	public void setPolyY(int[] polyY) {
		this.polyY = polyY;
}
}
