package de.svenheins.objects.agents;

import java.awt.Point;
import java.math.BigInteger;
import java.util.ArrayList;

import de.svenheins.main.EntityStates;
import de.svenheins.main.GameStates;
import de.svenheins.managers.EntityManager;
import de.svenheins.managers.ObjectMapManager;
import de.svenheins.objects.Entity;
import de.svenheins.objects.PlayerEntity;
import de.svenheins.objects.TileSet;
import de.svenheins.objects.WorldLatticePosition;
import de.svenheins.objects.WorldPosition;

public class SimpleAgent extends Agent {

	public SimpleAgent(TileSet tileSet, String name, BigInteger id, float x,
			float y, long animationDelay) {
		super(tileSet, name, id, x, y, animationDelay);
//		this.setMY(0.0f);
//		this.setMY(0.0f);
		this.setMaxSpeed(150.0f);
	}
	
	@Override
	public void run(ObjectMapManager collisionMap1, ObjectMapManager collisionMap2) {
		updateGoalRanking();
		if (actualGoal != null) {
			searchBetterLocation();
			if (actualGoal != null) {
				/** only search if we are too far away from goal */
				if ( this.getDistance(actualGoal.getPosition().getX(), actualGoal.getPosition().getY()) > GameStates.agentGoalDistance) {
					updatePathfinding(collisionMap1, collisionMap2);
					if (isPathCalculationComplete()) {
						updateMovement();
					} else {
						this.setMovement(0, 0);
						this.setContinuousState(EntityStates.STANDING);
						this.pathList = new ArrayList<WorldPosition>();
					}
				} else {
					this.nextGoal();
				}
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
				if ( getDistance(this.getActualPathElement().getX(), this.getActualPathElement().getY()-(this.getHeight()/2)) < this.getMaxSpeed()/20) {
//					int decelerationWeight = Math.max(2, (int)(this.getMaxSpeed()/2));
					tempVelocity = tempVelocity*1.5f;
//					tempVelocity = tempVelocity*((decelerationWeight*this.getMaxSpeed())/((this.getMaxSpeed())+(decelerationWeight-1)*getDistance(this.getActualPathElement().getX(), this.getActualPathElement().getY()-(this.getHeight()/2))));
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
	public void updateGoalRanking() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void searchBetterLocation() {
		/** update the pathList */
		if (this.pathList != null && this.pathList.size() > 0) {
			if ( getDistance(this.pathList.get(0).getX(), this.pathList.get(0).getY()-(this.getHeight()/2)) < GameStates.pathMinAcceptanceDistance) {
				/** ensure we landed exactly on the right spot */
//				if (Math.abs(this.getX() - this.pathList.get(0).getX()) <GameStates.pathMinAcceptanceDistance)
				this.setX(this.pathList.get(0).getX());
//				if (Math.abs(this.getY() +(this.getHeight()/2)- this.pathList.get(0).getY()) <GameStates.pathMinAcceptanceDistance)
				this.setY(this.pathList.get(0).getY()-(this.getHeight()/2));
				this.setMovement(0, 0);
				/** position is close enough, so get next pathElement */
				this.nextPathElement();
				System.out.println("next pathList element! remaining: "+this.pathList.size());
			} else {
//				System.out.println("Distance = "+getDistance(this.pathList.get(0).getX(), this.pathList.get(0).getY()-(this.getHeight()/2)));
			}
			if (this.pathList != null && this.pathList.size() > 0) {			
				/** here we already have a valid pathList */
				this.setActualPathElement(this.pathList.get(0));
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
		} else {
		/** here we have no more pathList, so check for other goals or positions */
			/** if there is an entityID as goal, update the location depending on position */
			if (!this.getActualGoal().getGoalEntityID().equals(BigInteger.valueOf(-1))) {
				this.getActualGoal().setPosition(new WorldPosition(this.getActualGoal().getEntity().getX(), this.getActualGoal().getEntity().getY()));
			} else if (this.getActualGoal().getPosition() != null) {
				// no entity-ID but simple position as goal
			} else {
				// no valid goal!
				System.out.println("no valid goal!");
			}
		}
	}

	@Override
	public void updatePathfinding(ObjectMapManager collisionMap1, ObjectMapManager collisionMap2) {
		/** check if the path-finding was completed */
		if (isPathCalculationComplete()) {
			/** check if the path is leading to the goal */
			/** AND check if the path is still valid (no new blocking objects) */
//			System.out.println("READY!!!");
			/** else stop movement and calculate new path */
		} else {			
//			int localWidth = GameStates.mapTotalWidth;
//			int localHeight = GameStates.mapTotalHeight;
			/** else continue with path calculation */
			if (closedList.size() > 0 && openList.size() > 0) {
				/** so we already started the generation of the lists */
				/** upper */
				PathTile originTile = this.getOpenListMinimumFScorePathTile();
//				System.out.println("F="+originTile.getFScore()+" G="+originTile.getGScore()+" H="+originTile.getHScore()); 
//				float f, g, h;
				if (originTile != null) {
					closedList.put(originTile.getPosition(), originTile); //(originTile);
					openList.remove(originTile.getPosition());
					System.out.println("added to closedList: x="+originTile.getPosition().getLocalX()+" y="+originTile.getPosition().getLocalY());
					System.out.println("with parent: x="+originTile.getParentPosition().getLocalX()+" y="+originTile.getParentPosition().getLocalY());
					
					/** upper */
					WorldPosition tileWorldPosition = new WorldPosition((int) originTile.getPosition().getMapCoordinates().getX()+GameStates.mapTileSetWidth*originTile.getPosition().getLocalX(), 
							(int) originTile.getPosition().getMapCoordinates().getY()+GameStates.mapTileSetWidth*originTile.getPosition().getLocalY() -GameStates.mapTileSetHeight);
	//				WorldLatticePosition tileWorldLatticePosition = WorldLatticePosition.getWorldLatticePosition(tileWorldPosition);
					checkTile(originTile, tileWorldPosition, collisionMap1, collisionMap2);
					
					/** right */
					tileWorldPosition = new WorldPosition((int) originTile.getPosition().getMapCoordinates().getX()+GameStates.mapTileSetWidth*originTile.getPosition().getLocalX() + GameStates.mapTileSetWidth, 
							(int) originTile.getPosition().getMapCoordinates().getY()+GameStates.mapTileSetWidth*originTile.getPosition().getLocalY());
	//				tileWorldLatticePosition = WorldLatticePosition.getWorldLatticePosition(tileWorldPosition);
					checkTile(originTile, tileWorldPosition, collisionMap1, collisionMap2);
					
					/** lower */
					tileWorldPosition = new WorldPosition((int) originTile.getPosition().getMapCoordinates().getX()+GameStates.mapTileSetWidth*originTile.getPosition().getLocalX(), 
							(int) originTile.getPosition().getMapCoordinates().getY()+GameStates.mapTileSetWidth*originTile.getPosition().getLocalY() + GameStates.mapTileSetHeight);
	//				tileWorldLatticePosition = WorldLatticePosition.getWorldLatticePosition(tileWorldPosition);
					checkTile(originTile, tileWorldPosition, collisionMap1, collisionMap2);
					
					/** left */
					tileWorldPosition = new WorldPosition((int) originTile.getPosition().getMapCoordinates().getX()+GameStates.mapTileSetWidth*originTile.getPosition().getLocalX() - GameStates.mapTileSetWidth, 
							(int) originTile.getPosition().getMapCoordinates().getY()+GameStates.mapTileSetWidth*originTile.getPosition().getLocalY());
	//				tileWorldLatticePosition = WorldLatticePosition.getWorldLatticePosition(tileWorldPosition);
					checkTile(originTile, tileWorldPosition, collisionMap1, collisionMap2);
					
					if (openList.containsKey(WorldLatticePosition.getWorldLatticePosition(actualGoal.getPosition()))) {
						/** create the path now! */
						System.out.println("found a path!!!");
						this.pathList = createCompletePath(WorldLatticePosition.getWorldLatticePosition(actualGoal.getPosition()));
						this.setActualPathElement(pathList.get(0));
						System.out.println("actualPathElement: X="+ this.getActualPathElement().getX()+" Y="+this.getActualPathElement().getY());
						this.setPathCalculationComplete(true);
					}
				} else {
					System.out.println("originTile is null");
				}
				
			} else {
				/** add first PathTile (entity itself) 
				 * here we do not need to calculate the manhatten distance
				 * */
				WorldLatticePosition firstPosition = WorldLatticePosition.getClosestWorldLatticePosition(new WorldPosition(this.getX(), this.getY()+(this.getHeight()/2)));
				PathTile originTile = new PathTile(firstPosition, firstPosition, 0, 0, 0);
				closedList.put(originTile.getPosition(), originTile); //(originTile);
				float f, g, h;
				
				/** upper */
				WorldPosition tileWorldPosition = new WorldPosition((int) originTile.getPosition().getMapCoordinates().getX()+GameStates.mapTileSetWidth*originTile.getPosition().getLocalX(), 
						(int) originTile.getPosition().getMapCoordinates().getY()+GameStates.mapTileSetWidth*originTile.getPosition().getLocalY() -GameStates.mapTileSetHeight);
				WorldLatticePosition tileWorldLatticePosition = WorldLatticePosition.getWorldLatticePosition(tileWorldPosition);
				h = this.guessDistanceValue(tileWorldPosition, actualGoal.getPosition());	
				g = originTile.getGScore() + 1.0f;
				f = g + h;
				PathTile addTile = new PathTile(tileWorldLatticePosition, firstPosition, f,g,h);
				checkFirstTiles(addTile, collisionMap1, collisionMap2);
				
				/** right */
				tileWorldPosition = new WorldPosition((int) originTile.getPosition().getMapCoordinates().getX()+GameStates.mapTileSetWidth*originTile.getPosition().getLocalX() + GameStates.mapTileSetWidth, 
						(int) originTile.getPosition().getMapCoordinates().getY()+GameStates.mapTileSetWidth*originTile.getPosition().getLocalY());
				tileWorldLatticePosition = WorldLatticePosition.getWorldLatticePosition(tileWorldPosition);
				h = this.guessDistanceValue(tileWorldPosition, actualGoal.getPosition());	
				g = originTile.getGScore() + 1.0f;
				f = g + h;
				addTile = new PathTile(tileWorldLatticePosition, firstPosition, f,g,h);
				checkFirstTiles(addTile, collisionMap1, collisionMap2);
				
				/** lower */
				tileWorldPosition = new WorldPosition((int) originTile.getPosition().getMapCoordinates().getX()+GameStates.mapTileSetWidth*originTile.getPosition().getLocalX(), 
						(int) originTile.getPosition().getMapCoordinates().getY()+GameStates.mapTileSetWidth*originTile.getPosition().getLocalY() + GameStates.mapTileSetHeight);
				tileWorldLatticePosition = WorldLatticePosition.getWorldLatticePosition(tileWorldPosition);
				h = this.guessDistanceValue(tileWorldPosition, actualGoal.getPosition());	
				g = originTile.getGScore() + 1.0f;
				f = g + h;
				addTile = new PathTile(tileWorldLatticePosition, firstPosition, f,g,h);
				checkFirstTiles(addTile, collisionMap1, collisionMap2);
				
				/** left */
				tileWorldPosition = new WorldPosition((int) originTile.getPosition().getMapCoordinates().getX()+GameStates.mapTileSetWidth*originTile.getPosition().getLocalX() - GameStates.mapTileSetWidth, 
						(int) originTile.getPosition().getMapCoordinates().getY()+GameStates.mapTileSetWidth*originTile.getPosition().getLocalY());
				tileWorldLatticePosition = WorldLatticePosition.getWorldLatticePosition(tileWorldPosition);
				h = this.guessDistanceValue(tileWorldPosition, actualGoal.getPosition());	
				g = originTile.getGScore() + 1.0f;
				f = g + h;
				addTile = new PathTile(tileWorldLatticePosition, firstPosition, f,g,h);
				checkFirstTiles(addTile, collisionMap1, collisionMap2);
			}
		}
		/** end */
	}
	
	
	/** as soon as we are ready, complete the path and return it to the agent */
	public ArrayList<WorldPosition> createCompletePath(WorldLatticePosition goalLatticePosition) {
		ArrayList<WorldPosition> returnList = new ArrayList<WorldPosition>();
		ArrayList<WorldLatticePosition> saveWLPList = new ArrayList<WorldLatticePosition>();
		if (closedList.containsKey(goalLatticePosition)) {
			// TODO: handle if this happens
		} else if (openList.containsKey(goalLatticePosition)) {
			System.out.println("PATH:");
			returnList.add(0, WorldPosition.getWorldPosition(goalLatticePosition));
			saveWLPList.add(0, goalLatticePosition);
			System.out.println("x = "+returnList.get(0).getX()+" | y = "+returnList.get(0).getY());
			
			/** first adding from openList to closedList */
			WorldLatticePosition oldWLP = goalLatticePosition;
			WorldLatticePosition parentWLP = openList.get(goalLatticePosition).getParentPosition();
			returnList.add(0, WorldPosition.getWorldPosition(closedList.get(parentWLP).getPosition()));
			saveWLPList.add(0, closedList.get(parentWLP).getPosition());
			System.out.println("x = "+returnList.get(0).getX()+" | y = "+returnList.get(0).getY());
			
			int sizeOfClosedList = closedList.size();			
			for (int i = 0; i < sizeOfClosedList-1; i++) {
//				System.out.println(i);
				oldWLP = saveWLPList.get(0);
				parentWLP = closedList.get(oldWLP).getParentPosition();
				saveWLPList.add(0, closedList.get(parentWLP).getPosition());
				/** only add the new coordinate if not yet existent */
				if (returnList.get(0).getX() != (WorldPosition.getWorldPosition(saveWLPList.get(0))).getX() ||
						returnList.get(0).getY() != (WorldPosition.getWorldPosition(saveWLPList.get(0))).getY()) {
					returnList.add(0, WorldPosition.getWorldPosition(saveWLPList.get(0)));
					System.out.println("x = "+returnList.get(0).getX()+" | y = "+returnList.get(0).getY());
				} else {
					System.out.println("double entry!");
				}
				
			}
		}
		return returnList;
	}

	/** check if the tile is valid (no collision-tile) and then check how to rank this tile in the lists */
	public void checkTile(PathTile originTile, WorldPosition tileWorldPosition, ObjectMapManager collisionMap1, ObjectMapManager collisionMap2) {
		WorldLatticePosition tileWorldLatticePosition = WorldLatticePosition.getWorldLatticePosition(tileWorldPosition);
		if ((collisionMap1.checkCollision(tileWorldLatticePosition.getMapCoordinates(), tileWorldLatticePosition.getLocalX(), tileWorldLatticePosition.getLocalY() ) ) ||
				(collisionMap2.checkCollision(tileWorldLatticePosition.getMapCoordinates(), tileWorldLatticePosition.getLocalX(), tileWorldLatticePosition.getLocalY() ) )) {
			/** collision!!! */
			// do nothing
		} else {
			if (closedList.containsKey(tileWorldLatticePosition)) {
				// ignore it
			} else if (!openList.containsKey(tileWorldLatticePosition)) {
				/** calculate PathTile */
				float h = this.guessDistanceValue(tileWorldPosition, actualGoal.getPosition());	
				float g = originTile.getGScore() + 1.0f;
				float f = g + h;
				PathTile addTile = new PathTile(tileWorldLatticePosition, originTile.getPosition(), f,g,h);
				/** add to openList */
				openList.put(addTile.getPosition(), addTile); //(checkTile);
			} else if (openList.containsKey(tileWorldLatticePosition)){
				/** check the other path and take the better score and update its parent */
				/** calculate PathTile */
				float h = this.guessDistanceValue(tileWorldPosition, actualGoal.getPosition());	
				float g = originTile.getGScore() + 1.0f;
				float f = g + h;
				PathTile addTile = new PathTile(tileWorldLatticePosition, originTile.getPosition(), f,g,h);
				if (openList.get(tileWorldLatticePosition).getFScore() > addTile.getFScore()) {
					/** update the score and its parent */
					openList.put(tileWorldLatticePosition, addTile);
				}
			}
			
		}
	}
	
	public void checkFirstTiles(PathTile tile,ObjectMapManager collisionMap1, ObjectMapManager collisionMap2) {
		if ((collisionMap1.checkCollision(tile.getPosition().getMapCoordinates(), tile.getPosition().getLocalX(), tile.getPosition().getLocalY() ) ) ||
				(collisionMap2.checkCollision(tile.getPosition().getMapCoordinates(), tile.getPosition().getLocalX(), tile.getPosition().getLocalY() ) )) {
			/** collision!!! */
			// do nothing
		} else {
			openList.put(tile.getPosition(), tile); //(originTile);
		}
	}
	
	@Override
	public void updateGoals() {
		// TODO Auto-generated method stub
		
	}


}
