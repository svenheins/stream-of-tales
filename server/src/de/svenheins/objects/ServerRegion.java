package de.svenheins.objects;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

public class ServerRegion extends ServerSpace{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/** Environment related attributes */
	private float climate;
	private int capacity;
	private ArrayList<Point> blockedPoints = new ArrayList<Point>();
	private ArrayList<Point> residents = new ArrayList<Point>();
	
	public ServerRegion(Space space, float climate, int capacity) {
		super(space);
		this.setClimate(climate);
		this.setCapacity(capacity);
	}

	public float getClimate() {
		return climate;
	}

	public void setClimate(float climate) {
		this.climate = climate;
	}

	
	public boolean addBlockedPoint(Point point) {
		if (point != null && !this.blockedPoints.contains(point) && (this.blockedPoints.size() < this.capacity)) {
			this.blockedPoints.add(point);
			int[] rgb = this.getRGB();
			rgb[0] += ((250-this.rgb[0])/capacity);
			this.setRGB(rgb);
			return true;
		} else
		{
			return false;
		}
		
		
	}
	
	public boolean addResident(Point point) {
		if (point != null) {
			this.residents.add(point);
			int[] rgb = this.getRGB();
			rgb[2] += ((250-this.rgb[2])/capacity);
			this.setRGB(rgb);
			return true;
		} else
		{
			return false;
		}
	}
	
	
	public ArrayList<Point> getBlockedPoints() {
		return blockedPoints;
	}

	public void setBlockedPoints(ArrayList<Point> blockedPoints) {
		this.blockedPoints = blockedPoints;
	}
	
	public void setCapacity(int capacity)  {
		this.capacity = capacity;
	}
	
	public int getCapacity() {
		return this.capacity;
	}
	
	public ArrayList<Point> getResidents() {
		return residents;
	}
	
	public void setResidents(ArrayList<Point> residents) {
		this.residents = residents;
	}

}
