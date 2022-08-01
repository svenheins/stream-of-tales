package de.svenheins.objects.agents;

import java.awt.Point;
import java.math.BigInteger;
import java.util.ArrayList;

import de.svenheins.main.EntityStates;
import de.svenheins.objects.Entity;
import de.svenheins.objects.PlayerEntity;
import de.svenheins.objects.TileSet;
import de.svenheins.objects.agents.goals.Goal;

public abstract class Agent extends PlayerEntity {

	/** Motivation attributes */
	protected float satisfaction;
	protected float danger;
	protected boolean goal;
	protected float range;
	protected Goal actualGoal;
	
	protected ArrayList<Goal> goalPriorityList = new ArrayList<Goal>();
	protected ArrayList<Point> pathList = new ArrayList<Point>();
	protected ArrayList<BigInteger> entityIDList = new ArrayList<BigInteger>();

	public Agent(TileSet tileSet, String name, BigInteger id, float x, float y, long animationDelay) {
		super(tileSet, name, id, x, y, animationDelay);
		satisfaction = 0;
		this.range = 10000;
		this.setVisible(true);
		goal = false;
	}
	
	/** run */
	public abstract void run();
	
	/** find a location based on needs */
	public abstract void searchBetterLocation();
	
	/** update pathfinding */
	public abstract void updatePathfinding();

	/** update goals depending on the changes circumstances */
	public abstract void updateGoals();

	/** rethink goal */
	public abstract void updateGoalRanking();
	
	public float getSatisfaction() {
		return satisfaction;
	}

	public void setSatisfaction(float satisfaction) {
		this.satisfaction = satisfaction;
	}
		
	/** update the movement depending on desired Position */
	public void updateMovement() {
		float velocity = this.getMaxSpeed();//Math.sqrt(this.getHorizontalMovement()*this.getHorizontalMovement() + this.getVerticalMovement()*this.getVerticalMovement());
		float newMX, newMY;
		newMX = (float) (this.getActualGoal().getPosition().getX()-this.getX());
		newMY = (float) (this.getActualGoal().getPosition().getY()-this.getY());
		float tempVelocity = (float) Math.sqrt(newMX*newMX + newMY*newMY);
		this.setMovement(velocity*(newMX/tempVelocity), velocity*(newMY/tempVelocity));
		
		this.determineOrientation(new Point((int) this.getActualGoal().getEntity().getX(),(int) this.getActualGoal().getEntity().getY()));
		if ( this.getMX() == 0 && this.getMY()== 0) {
			if (this.getContinuousState() == EntityStates.WALKING) {
				this.setContinuousState(EntityStates.STANDING);
//				this.setChangedStates(true);
			}
			
		} else {
			this.setContinuousState(EntityStates.WALKING);
		}
		
		
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

	public Goal getActualGoal() {
		return actualGoal;
	}

	public void setActualGoal(Goal actualGoal) {
		this.actualGoal = actualGoal;
	}
}
