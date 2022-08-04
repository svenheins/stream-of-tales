package de.svenheins.objects.agents;

import java.awt.Point;
import java.math.BigInteger;
import java.util.ArrayList;

import de.svenheins.main.EntityStates;
import de.svenheins.main.GameStates;
import de.svenheins.managers.ObjectMapManager;
import de.svenheins.objects.TileSet;
import de.svenheins.objects.WorldPosition;

public class StupidAgent extends Agent {

	public StupidAgent(TileSet tileSet, String name, BigInteger id, float x,
			float y, long animationDelay) {
		super(tileSet, name, id, x, y, animationDelay);
		// TODO Auto-generated constructor stub
		this.setMaxSpeed(150.0f);
	}

	@Override
	public void run(ObjectMapManager collisionMap1,
			ObjectMapManager collisionMap2) {
//		updateGoalRanking();
		if (actualGoal != null) {
			searchBetterLocation();
			if (actualGoal != null) {
				/** only search if we are too far away from goal */
//				if ( this.getDistance(actualGoal.getPosition().getX(), actualGoal.getPosition().getY()) > GameStates.agentGoalDistance) {
					updatePathfinding(collisionMap1, collisionMap2);
//					if (isPathCalculationComplete()) {
					updateMovement();
//					} else {
//						this.setMovement(0, 0);
//						this.setContinuousState(EntityStates.STANDING);
//						this.pathList = new ArrayList<WorldPosition>();
//					}
//				}
				updateGoals();
			}
		}
	}
	
	@Override
	/** update the movement depending on desired Position */
	public void updateMovement() {
		float velocity = this.getMaxSpeed();//Math.sqrt(this.getHorizontalMovement()*this.getHorizontalMovement() + this.getVerticalMovement()*this.getVerticalMovement());
		float newMX, newMY;
//		newMX = (float) (this.getActualGoal().getPosition().getX()-this.getX());
//		newMY = (float) (this.getActualGoal().getPosition().getY()-this.getY());
		newMX = (float) (this.getActualPathElement().getX()-(this.getX()));
		newMY = (float) (this.getActualPathElement().getY()-(this.getY()+(this.getHeight()/2)));
		/** correct the destination if it is an old one */
		if ((newMX ==0 && newMY == 0) && this.pathList.size() >0) {
			this.nextPathElement();
			this.setMovement(0, 0);
			/** if we have a path to follow */
			System.out.println("old pathelement");
		} else {
			float tempVelocity = (float) Math.sqrt(newMX*newMX + newMY*newMY);
			if (tempVelocity > 0) {
				if ( getDistance(this.getActualPathElement().getX(), this.getActualPathElement().getY()-(this.getHeight()/2)) < this.getMaxSpeed()) {
					int decelerationWeight = Math.max(2, (int)(this.getMaxSpeed()/2));
					tempVelocity = tempVelocity*((decelerationWeight*this.getMaxSpeed())/((this.getMaxSpeed())+(decelerationWeight-1)*getDistance(this.getActualPathElement().getX(), this.getActualPathElement().getY()-(this.getHeight()/2))));
				}
//				if ( getDistance(this.pathList.get(0).getX(), this.pathList.get(0).getY()-(this.getHeight()/2)) < 3*GameStates.pathMinAcceptanceDistance) {
//					tempVelocity = tempVelocity*1.5f;
//				}
				this.setMovement(velocity*(newMX/tempVelocity), velocity*(newMY/tempVelocity));
				
			} else
			{
				this.setMovement(0, 0);
				/** if we have a path to follow */
				System.out.println("STOP, because we reached the pathlistelement");
			}
		}
		
//		System.out.println("movement: X="+this.getMX()+" Y="+this.getMY());
		
		this.determineOrientation(new Point((int) this.getActualGoal().getPosition().getX(),(int) this.getActualGoal().getPosition().getY()));
//		this.determineOrientation(new Point((int) this.getActualPathElement().getX(),(int) this.getActualPathElement().getY()));

		if ( this.getMX() == 0 && this.getMY()== 0) {
			if (this.getContinuousState() == EntityStates.WALKING) {
				this.setContinuousState(EntityStates.STANDING);
//				this.setChangedStates(true);
			}
			
		} else {
			this.setContinuousState(EntityStates.WALKING);
		}
		
	}

	@Override
	public void searchBetterLocation() {
		/** if the agent is close enough */
		if ( getDistance(this.getActualGoal().getPosition().getX(), this.getActualGoal().getPosition().getY()-(this.getHeight()/2)) < GameStates.pathMinAcceptanceDistance) {		
			/** ensure we landed exactly on the right spot */
			this.setX(this.getActualGoal().getPosition().getX());
			this.setY(this.getActualGoal().getPosition().getY()-(this.getHeight()/2));
			this.setMovement(0, 0);
			/** position is close enough, so get next goal */
			this.nextGoal();
		} 
		if (this.getActualGoal() != null) {			
			/** here we already have a valid pathList */
//			this.setActualPathElement(this.getActualGoal().getPosition());
		} else {
			/** here the pathList does not yet exist or the pathList might be empty */
			this.pathList = new ArrayList<WorldPosition>();
			this.setPathCalculationComplete(false);
			System.out.println("restart pathfinding...");
			if (goalList != null && (goalList.size() >0) ) {
				/** take the next goal */
				this.nextGoal();
			} else {
				/** no more goals available */
				setActualGoal(null);
				this.setMovement(0, 0);
				this.setContinuousState(EntityStates.STANDING);
			}
		} 
	}
	
	
	protected WorldPosition getActualPathElement() {
		if (this.getActualGoal() != null)		
		return this.getActualGoal().getPosition();
		else return null;
	}
	
	@Override
	public void updatePathfinding(ObjectMapManager collisionMap1,
			ObjectMapManager collisionMap2) {
		this.setActualPathElement(actualGoal.getPosition());
		this.setPathCalculationComplete(true);
	}

	@Override
	public void updateGoals() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateGoalRanking() {
		// TODO Auto-generated method stub
		
	}

}
