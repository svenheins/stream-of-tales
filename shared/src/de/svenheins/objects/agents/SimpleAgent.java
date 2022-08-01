package de.svenheins.objects.agents;

import java.math.BigInteger;

import de.svenheins.main.EntityStates;
import de.svenheins.main.GameStates;
import de.svenheins.managers.EntityManager;
import de.svenheins.objects.Entity;
import de.svenheins.objects.PlayerEntity;
import de.svenheins.objects.TileSet;
import de.svenheins.objects.WorldPosition;

public class SimpleAgent extends Agent {

	public SimpleAgent(TileSet tileSet, String name, BigInteger id, float x,
			float y, long animationDelay) {
		super(tileSet, name, id, x, y, animationDelay);
//		this.setMY(0.0f);
//		this.setMY(0.0f);
		this.setMaxSpeed(50.0f);
	}
	
	@Override
	public void run() {
		updateGoalRanking();
		searchBetterLocation();
		/** only search if we are too far away from goal */
		if ( this.getDistance(actualGoal.getPosition().getX(), actualGoal.getPosition().getY()) > GameStates.agentGoalDistance) {
			updatePathfinding();
			updateMovement();
		} else {
			this.setMovement(0, 0);
			this.setContinuousState(EntityStates.STANDING);
		}
		updateGoals();
	}
	
	@Override
	public void updateGoalRanking() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void searchBetterLocation() {
		/** if there is an entityID as goal, update the location depending on position */
		if (!this.getActualGoal().getGoalEntityID().equals(BigInteger.valueOf(-1))) {
			this.getActualGoal().setPosition(new WorldPosition(this.getActualGoal().getEntity().getX(), this.getActualGoal().getEntity().getY()));
			
//			if (EntityManager.idList.contains(this.getActualGoal().getGoalEntityID())) {
//				Entity entity = EntityManager.get(this.getActualGoal().getGoalEntityID());
//				WorldPosition worldPos = this.getActualGoal().getPosition();
//				worldPos.setX(entity.getX());
//				worldPos.setY(entity.getY());
//				this.getActualGoal().setPosition(worldPos);
//			}
		} else {
//			WorldPosition worldPos = this.getActualGoal().getPosition();
//			worldPos.setX(entity.getX());
//			worldPos.setY(entity.getY());
//			this.getActualGoal().setPosition(worldPos);
		}
	}

	@Override
	public void updatePathfinding() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateGoals() {
		// TODO Auto-generated method stub
		
	}

	

}
