package de.svenheins.objects;

import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.math.BigInteger;
import java.util.logging.Level;

import com.sun.sgs.app.AppContext;
import com.sun.sgs.app.DataManager;
import com.sun.sgs.app.ManagedReference;

import de.svenheins.WorldRoom;
import de.svenheins.functions.MyMath;

public class ServerAgent extends ServerEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/** Motivation attributes */
	protected float satisfaction;
	protected float desiredX;
	protected float desiredY;
	protected boolean goal;
	protected float range;
	protected BigInteger regionID = null;

	public ServerAgent(ServerSprite sprite, BigInteger id, float x, float y,
			float mx, float my) {
		super(sprite, id, x, y, mx, my);
		satisfaction = 0;
		this.range = 10000;
		goal = false;
	}

	public float getSatisfaction() {
		return satisfaction;
	}

	public void setSatisfaction(float satisfaction) {
		this.satisfaction = satisfaction;
	}
	
	/** find a better location if not satisfied */
	public void updateLocation(){
		
	}
	
	/** find a location based on needs 
	 * 	The Standard-Agent stays where he is
	 */
	public void searchBetterLocation(){
		float desX = 0;
		float desY = 0;
		setDesiredPosition(desX, desY);
	}
	
	/** Check the goal */
	public boolean validateGoal() {
//		float dis = (float) Math.sqrt((this.getX()-this.desiredX)*(this.getX()-this.desiredX)+(this.getY()-this.desiredY)*(this.getY()-this.desiredY));
//		setSatisfaction( (float) (1/(1+Math.sqrt(dis/10))) );
//		/** brake a little bit if in range*/
//		if (dis<100) setMovement(mx*(dis/100), my*(dis/100));
		return false;
	}
	
	/** Set desired Position */
	public void setDesiredPosition(float x, float y) {
		this.desiredX = x;
		this.desiredY = y;
		/** now the agent has a new goal */
		this.setGoal(true);
//		updateMovement();
	}
	
	
	/** update the movement depending on desired Position */
	public void updateMovement() {
		float velocity = (float) Math.sqrt(this.getHorizontalMovement()*this.getHorizontalMovement() + this.getVerticalMovement()*this.getVerticalMovement());
		float newMX, newMY;
		newMX = (float) (this.desiredX-this.getX());
		newMY = (float) (this.desiredY-this.getY());
		float tempVelocity = (float) Math.sqrt(newMX*newMX + newMY*newMY);
		this.setMovement(velocity*(newMX/tempVelocity), velocity*(newMY/tempVelocity));
	}
	
	/** calculate distance to Point (x,y) */
	public float getDistance(float x, float y) {
		return (float) Math.sqrt((x-this.getX())*(x-this.getX()) + (y-this.getY())*(y-this.getY()));
	}

	
	public void setGoal(boolean goal) {
		this.goal = goal;
	}
	
	public boolean hasGoal() {
		return goal;
	}
	
	/** set a new region (if not null) */
	protected void setRegion(BigInteger regionID) {
//        DataManager dataManager = AppContext.getDataManager();
//        dataManager.markForUpdate(this);

        if (regionID == null) {
            this.regionID = null;
            return;
        }
        this.regionID = regionID;
    }
	
	/** get the current region of the agent */
	protected BigInteger getRegion() {
        if (this.regionID == null) {
            return null;
        }

        return regionID;
    }
}
