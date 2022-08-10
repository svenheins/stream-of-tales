package de.svenheins.objects.agents;

import java.awt.Point;
import java.math.BigInteger;
import java.util.ArrayList;

import javax.swing.plaf.basic.BasicInternalFrameTitlePane.RestoreAction;

import de.svenheins.functions.MyMath;
import de.svenheins.main.AttributeType;
import de.svenheins.main.EntityStates;
import de.svenheins.main.GameStates;
import de.svenheins.managers.ObjectMapManager;
import de.svenheins.objects.TileSet;
import de.svenheins.objects.WorldLatticePosition;
import de.svenheins.objects.WorldPosition;
import de.svenheins.objects.agents.goals.Goal;

public class NormalAgent extends Agent {
	private boolean directModus;
	private long timestampPathCalculation;
	
	public NormalAgent(TileSet tileSet, String name, BigInteger id, float x,
			float y, long animationDelay) {
		super(tileSet, name, id, x, y, animationDelay);
		this.setMaxSpeed(100.0f);
		this.setDirectModus(true);
	}

	@Override
	public void run(ObjectMapManager collisionMap1,
			ObjectMapManager collisionMap2) {
		updateGoalRanking();
		if (actualGoal != null) {
			searchBetterLocation();
			if (actualGoal != null) {
				/** only search if we are too far away from goal */
				if ( this.getDistance(actualGoal.getPosition().getX(), actualGoal.getPosition().getY()) > GameStates.agentGoalDistance) {	
					if (isPathCalculationComplete()) {
						updateMovement();
					} else {
						this.setMovement(-this.getAttributes()[AttributeType.MX.ordinal()], -this.getAttributes()[AttributeType.MY.ordinal()]);
						this.setContinuousState(EntityStates.STANDING);
						this.pathList = new ArrayList<WorldPosition>();
						/** pathfinding depending on direct or exact mode */	
						if (isDirectModus()) {
							updatePathfindingDirect(collisionMap1, collisionMap2);
						} else {
							updatePathfinding(collisionMap1, collisionMap2);
						}
						
					}
				} else {
					System.out.println("close enough to take NEXT GOAL!");
					this.nextGoal();
				}
				updateGoals();
			}
		}
	}

//	public void updateMovementOLD() {
//		float actualVelocity = (float) Math.sqrt((this.getMX()+this.getAttributes()[AttributeType.MX.ordinal()])*(this.getMX()+this.getAttributes()[AttributeType.MX.ordinal()]) + (this.getMY()+this.getAttributes()[AttributeType.MY.ordinal()])*(this.getMY()+this.getAttributes()[AttributeType.MY.ordinal()]));
//		float velocity = this.getMaxSpeed();
//		float newMX, newMY;
//		newMX = (float) (this.getActualPathElement().getX()-(this.getX())-this.getAttributes()[AttributeType.MX.ordinal()]);
//		newMY = (float) (this.getActualPathElement().getY()-(this.getY()+(this.getHeight()/2))-this.getAttributes()[AttributeType.MY.ordinal()]);
//		/** correct the destination if it is an old one */
//		if ((newMX ==0 && newMY == 0) && this.pathList.size() >0) {
//			this.nextPathElement();
//			this.setMovement(-this.getAttributes()[AttributeType.MX.ordinal()], -this.getAttributes()[AttributeType.MX.ordinal()]);
//			/** if we have a path to follow */
//			System.out.println("old pathelement");
//		} else {
//			float tempVelocity = (float) Math.sqrt(newMX*newMX + newMY*newMY);// (float) Math.sqrt((newMX+this.getAttributes()[AttributeType.MX.ordinal()])*(newMX+this.getAttributes()[AttributeType.MX.ordinal()]) + (newMY+this.getAttributes()[AttributeType.MY.ordinal()])*(newMY+this.getAttributes()[AttributeType.MY.ordinal()]));
//			if (tempVelocity > 0) {
//				if (isDirectModus()) {
//					/** deceleration */
//					if ( getDistance(this.getActualPathElement().getX(), this.getActualPathElement().getY()-(this.getHeight()/2)) < actualVelocity) {
//						int decelerationWeight = Math.max(2, (int)(actualVelocity/2));
//						tempVelocity = tempVelocity*((decelerationWeight*actualVelocity)/(actualVelocity+(decelerationWeight-1)*getDistance(this.getActualPathElement().getX(), this.getActualPathElement().getY()-(this.getHeight()/2))));
//					}
//					/** acceleration */
//					if ((actualVelocity < this.getMaxSpeed() && getDistance(this.getActualPathElement().getX(), this.getActualPathElement().getY()-(this.getHeight()/2)) >= actualVelocity)) {
//						tempVelocity = tempVelocity / 1.5f;
//						//						float accelerationWeight = Math.max(2, (int)(actualVelocity/2));
////						tempVelocity = tempVelocity*((float) 1 / ((accelerationWeight*actualVelocity)/(actualVelocity+(accelerationWeight-1)*getDistance(this.getActualPathElement().getX(), this.getActualPathElement().getY()-(this.getHeight()/2)))));
//					}
//				} else {
//					if ( getDistance(this.getActualPathElement().getX(), this.getActualPathElement().getY()-(this.getHeight()/2)) < actualVelocity/20) {
//						tempVelocity = tempVelocity*1.5f;
//					}
//				}
//				this.setMovement( velocity*(newMX/tempVelocity), velocity*(newMY/tempVelocity));
//			} else
//			{
//				this.setMovement(0, 0);
//				/** if we have a path to follow */
//				System.out.println("STOP, because we reached the pathlistelement");
//			}
//		}
//		
////		System.out.println("movement: X="+this.getMX()+" Y="+this.getMY());
//		
//		this.determineOrientation(new Point((int) this.getActualGoal().getPosition().getX(),(int) this.getActualGoal().getPosition().getY()));
////		this.determineOrientation(new Point((int) this.getActualPathElement().getX(),(int) this.getActualPathElement().getY()));
//
//		if ( this.getMX() == 0 && this.getMY()== 0) {
//			if (this.getContinuousState() == EntityStates.WALKING) {
//				this.setContinuousState(EntityStates.STANDING);
//			}
//			
//		} else {
//			this.setContinuousState(EntityStates.WALKING);
//		}
//	}
	

	@Override
	public void updateMovement() {
		float areaInfluenceMX = this.getAttributes()[AttributeType.MX.ordinal()];
		float areaInfluenceMY = this.getAttributes()[AttributeType.MY.ordinal()];
		float actualVelocity = (float) Math.sqrt((this.getMX()+areaInfluenceMX)*(this.getMX()+areaInfluenceMX) + (this.getMY()+areaInfluenceMY)*(this.getMY()+areaInfluenceMY));
		float maxSpeed = this.getMaxSpeed();
		float newMX = (float) (this.getActualPathElement().getX()-(this.getX()));//-this.getAttributes()[AttributeType.MX.ordinal()]); 
		float newMY = (float) (this.getActualPathElement().getY()-(this.getY()+(this.getHeight()/2)));//-this.getAttributes()[AttributeType.MY.ordinal()]);
		
		float moveDirectionX, moveDirectionY;
		float vectorAx;
		float vectorAy;
		float lengthA;
		/** correct the destination if it is an old one */
		if ((newMX == 0 && newMY == 0) && this.pathList.size() >0) {
			this.nextPathElement();
			this.setMovement(-this.getAttributes()[AttributeType.MX.ordinal()], -this.getAttributes()[AttributeType.MY.ordinal()]);
			/** if we have a path to follow */
			System.out.println("old pathelement");
		} else {
			float tempVelocity = (float) Math.sqrt(newMX*newMX + newMY*newMY);// (float) Math.sqrt((newMX+this.getAttributes()[AttributeType.MX.ordinal()])*(newMX+this.getAttributes()[AttributeType.MX.ordinal()]) + (newMY+this.getAttributes()[AttributeType.MY.ordinal()])*(newMY+this.getAttributes()[AttributeType.MY.ordinal()]));
			if (tempVelocity > 0) {
//				if (isDirectModus()) {
//					/** deceleration */
//					if ( getDistance(this.getActualPathElement().getX(), this.getActualPathElement().getY()-(this.getHeight()/2)) < actualVelocity) {
//						int decelerationWeight = Math.max(2, (int)(actualVelocity/2));
//						tempVelocity = tempVelocity*((decelerationWeight*actualVelocity)/(actualVelocity+(decelerationWeight-1)*getDistance(this.getActualPathElement().getX(), this.getActualPathElement().getY()-(this.getHeight()/2))));
//					}
//					/** acceleration */
//					if ((actualVelocity < this.getMaxSpeed() && getDistance(this.getActualPathElement().getX(), this.getActualPathElement().getY()-(this.getHeight()/2)) >= actualVelocity)) {
//						tempVelocity = tempVelocity / 1.5f;
//						//						float accelerationWeight = Math.max(2, (int)(actualVelocity/2));
////						tempVelocity = tempVelocity*((float) 1 / ((accelerationWeight*actualVelocity)/(actualVelocity+(accelerationWeight-1)*getDistance(this.getActualPathElement().getX(), this.getActualPathElement().getY()-(this.getHeight()/2)))));
//					}
//				} else {
//					if ( getDistance(this.getActualPathElement().getX(), this.getActualPathElement().getY()-(this.getHeight()/2)) < actualVelocity/20) {
//						tempVelocity = tempVelocity*1.5f;
//					}
//				}
				
				/** adapt speed */
				if (isDirectModus()) {
					
				} else {
					if ( getDistance(this.getActualPathElement().getX(), this.getActualPathElement().getY()-(this.getHeight()/2)) < actualVelocity/20) {
						tempVelocity = tempVelocity*1.5f;
					}
				}
							
				/** calculate new movement */
				if (areaInfluenceMX != 0 || areaInfluenceMY != 0) {
					float areaInfluenceStrength = (float) Math.sqrt(areaInfluenceMX*areaInfluenceMX + areaInfluenceMY*areaInfluenceMY);
					if ( (newMX/ newMY) == (areaInfluenceMX/areaInfluenceMY) ) lengthA = maxSpeed+areaInfluenceStrength; 
					else if ((newMX/newMY)== -(areaInfluenceMX/areaInfluenceMY)) lengthA = maxSpeed-areaInfluenceStrength;
					else lengthA = MyMath.calculateTriangleA(newMX, newMY, areaInfluenceMX, areaInfluenceMY, maxSpeed);
					vectorAx = lengthA*(newMX / MyMath.calculateNorm(newMX, newMY));
					vectorAy = lengthA*(newMY / MyMath.calculateNorm(newMX, newMY));
					moveDirectionX = vectorAx - areaInfluenceMX;
					moveDirectionY = vectorAy - areaInfluenceMY;
					this.setMovement(moveDirectionX, moveDirectionY);
				} else {
					this.setMovement( maxSpeed*(newMX/tempVelocity), maxSpeed*(newMY/tempVelocity));
				}

			} else
			{
				this.setMovement(-this.getAttributes()[AttributeType.MX.ordinal()], -this.getAttributes()[AttributeType.MY.ordinal()]);
				/** if we have a path to follow */
				System.out.println("STOP, because we reached the pathlistelement");
			}
		}

		/** Orientation and animation */
		this.determineOrientation(new Point((int) this.getActualGoal().getPosition().getX(),(int) this.getActualGoal().getPosition().getY()));
		if ( this.getMX() == 0 && this.getMY()== 0) {
			if (this.getContinuousState() == EntityStates.WALKING) {
				this.setContinuousState(EntityStates.STANDING);
			}
		} else {
			this.setContinuousState(EntityStates.WALKING);
		}
	}

	@Override
	public void searchBetterLocation() {
		/** Direct Modus */
		if (isDirectModus()) {
			if (this.getActualGoal() != null) {			
//				/** here we already have a valid pathList */
//				this.setActualPathElement(this.getActualGoal().getPosition());
				
				/** if the agent is close enough */
				if ( getDistance(this.getActualGoal().getPosition().getX(), this.getActualGoal().getPosition().getY()-(this.getHeight()/2)) < GameStates.pathMinAcceptanceDistance) {		
					/** ensure we landed exactly on the right spot */
					this.setX(this.getActualGoal().getPosition().getX());
					this.setY(this.getActualGoal().getPosition().getY()-(this.getHeight()/2));
					this.setMovement(-this.getAttributes()[AttributeType.MX.ordinal()], -this.getAttributes()[AttributeType.MY.ordinal()]);
					/** position is close enough, so get next goal */
					System.out.println("direct mode NEXT GOAL!: x="+this.getX()+" y="+this.getY());
					this.nextGoal();
				} 
			} else {
				/** here the pathList does not yet exist or the pathList might be empty */
				this.pathList = new ArrayList<WorldPosition>();
				this.setPathCalculationComplete(false);
				System.out.println("restart pathfinding...");
				if (goalList != null && (goalList.size() >0) ) {
					/** take the next goal */
					System.out.println("actualGoal == null NEXT GOAL!");
					this.nextGoal();
				} else {
					/** no more goals available */
					setActualGoal(null);
					this.setMovement(0, 0);
					this.setContinuousState(EntityStates.STANDING);
				}
			}
		} else {
			/** update the pathList */
			if (this.pathList != null && this.pathList.size() > 0) {
				if ( getDistance(this.pathList.get(0).getX(), this.pathList.get(0).getY()-(this.getHeight()/2)) < GameStates.pathMinAcceptanceDistance) {
					/** ensure we landed exactly on the right spot */
					this.setX(this.pathList.get(0).getX());
					this.setY(this.pathList.get(0).getY()-(this.getHeight()/2));
					this.setMovement(-this.getAttributes()[AttributeType.MX.ordinal()], -this.getAttributes()[AttributeType.MY.ordinal()]);
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
						System.out.println("goalList == null || goalList.size() == 0 -> nextGoal");
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
	}
	
	public void updatePathfindingDirect(ObjectMapManager collisionMap1,
			ObjectMapManager collisionMap2) {
		this.setActualPathElement(actualGoal.getPosition());
		this.setPathCalculationComplete(true);
	}

	@Override
	public void updatePathfinding(ObjectMapManager collisionMap1,
			ObjectMapManager collisionMap2) {
		/** check if the path-finding was completed */
		if (isPathCalculationComplete()) {
			/** check if the path is leading to the goal */
			/** AND check if the path is still valid (no new blocking objects) */
//			System.out.println("READY!!!");
			/** else stop movement and calculate new path */
		} else {		
			/** else continue with path calculation */
			if (closedList.size() > 0 && openList.size() > 0) {
				/** so we already started the generation of the lists */
				long timeNow = System.currentTimeMillis();
				/** if too long time went by, stop the calculation and goto next goal */
				if (timeNow - this.getTimestampPathCalculation() > GameStates.timeForPathCalculation) {
					System.out.println("NEXT GOAL, because time is up!");
					this.nextGoal();
					this.setDirectModus(false);
					return;
				}
				System.out.println("time passed: "+(timeNow - this.getTimestampPathCalculation()));
				/** upper */
				PathTile originTile = this.getOpenListMinimumFScorePathTile();
//				System.out.println("F="+originTile.getFScore()+" G="+originTile.getGScore()+" H="+originTile.getHScore()); 
				if (originTile != null) {
					closedList.put(originTile.getPosition(), originTile); //(originTile);
					openList.remove(originTile.getPosition());
//					System.out.println("added to closedList: x="+originTile.getPosition().getLocalX()+" y="+originTile.getPosition().getLocalY());
//					System.out.println("with parent: x="+originTile.getParentPosition().getLocalX()+" y="+originTile.getParentPosition().getLocalY());
					
					/** upper */
					WorldPosition tileWorldPosition = new WorldPosition((int) originTile.getPosition().getMapCoordinates().getX()+GameStates.mapTileSetWidth*originTile.getPosition().getLocalX(), 
							(int) originTile.getPosition().getMapCoordinates().getY()+GameStates.mapTileSetWidth*originTile.getPosition().getLocalY() -GameStates.mapTileSetHeight);
					checkTile(originTile, tileWorldPosition, collisionMap1, collisionMap2);
					
					/** right */
					tileWorldPosition = new WorldPosition((int) originTile.getPosition().getMapCoordinates().getX()+GameStates.mapTileSetWidth*originTile.getPosition().getLocalX() + GameStates.mapTileSetWidth, 
							(int) originTile.getPosition().getMapCoordinates().getY()+GameStates.mapTileSetWidth*originTile.getPosition().getLocalY());
					checkTile(originTile, tileWorldPosition, collisionMap1, collisionMap2);
					
					/** lower */
					tileWorldPosition = new WorldPosition((int) originTile.getPosition().getMapCoordinates().getX()+GameStates.mapTileSetWidth*originTile.getPosition().getLocalX(), 
							(int) originTile.getPosition().getMapCoordinates().getY()+GameStates.mapTileSetWidth*originTile.getPosition().getLocalY() + GameStates.mapTileSetHeight);
					checkTile(originTile, tileWorldPosition, collisionMap1, collisionMap2);
					
					/** left */
					tileWorldPosition = new WorldPosition((int) originTile.getPosition().getMapCoordinates().getX()+GameStates.mapTileSetWidth*originTile.getPosition().getLocalX() - GameStates.mapTileSetWidth, 
							(int) originTile.getPosition().getMapCoordinates().getY()+GameStates.mapTileSetWidth*originTile.getPosition().getLocalY());
					checkTile(originTile, tileWorldPosition, collisionMap1, collisionMap2);
					
					if (openList.containsKey(WorldLatticePosition.getWorldLatticePosition(actualGoal.getPosition()))) {
						/** create the path now! */
//						System.out.println("found a path!!!");
						this.pathList = createCompletePath(WorldLatticePosition.getWorldLatticePosition(actualGoal.getPosition()));
						this.setActualPathElement(pathList.get(0));
//						System.out.println("actualPathElement: X="+ this.getActualPathElement().getX()+" Y="+this.getActualPathElement().getY());
						this.setPathCalculationComplete(true);
					}
				} else {
					System.out.println("originTile is null");
				}
				
			} else {
				/** start pathCalculation */
				this.setTimestampPathCalculation(System.currentTimeMillis());
				/** check if the goal is reachable */
				WorldLatticePosition goalPosition = WorldLatticePosition.getClosestWorldLatticePosition(this.getActualGoal().getPosition());
				PathTile goalTile = new PathTile(goalPosition, goalPosition, 0, 0, 0);
				if ((collisionMap1.checkCollision(goalTile.getPosition().getMapCoordinates(), goalTile.getPosition().getLocalX(), goalTile.getPosition().getLocalY() ) ) ||
						(collisionMap2.checkCollision(goalTile.getPosition().getMapCoordinates(), goalTile.getPosition().getLocalX(), goalTile.getPosition().getLocalY() ) )) {
					/** collision!!! 
					 * GoalTile is definitely not reachable		
					 * so begin the pathCalculation with the next goal!			 * */
					this.nextGoal();
					this.setDirectModus(false);
					System.out.println("Occupied Goal! -> search a way to the next goal!");
				} else {
					/** goal is not occupied, so we can search a path to it */
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
//			System.out.println("PATH:");
			returnList.add(0, WorldPosition.getWorldPosition(goalLatticePosition));
			saveWLPList.add(0, goalLatticePosition);
//			System.out.println("x = "+returnList.get(0).getX()+" | y = "+returnList.get(0).getY());
			
			/** first adding from openList to closedList */
			WorldLatticePosition oldWLP = goalLatticePosition;
			WorldLatticePosition parentWLP = openList.get(goalLatticePosition).getParentPosition();
			returnList.add(0, WorldPosition.getWorldPosition(closedList.get(parentWLP).getPosition()));
			saveWLPList.add(0, closedList.get(parentWLP).getPosition());
//			System.out.println("x = "+returnList.get(0).getX()+" | y = "+returnList.get(0).getY());
			
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
//					System.out.println("x = "+returnList.get(0).getX()+" | y = "+returnList.get(0).getY());
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

	@Override
	public void updateGoalRanking() {
		// TODO Auto-generated method stub
		
	}

	public boolean isDirectModus() {
		return directModus;
	}

	public void setDirectModus(boolean directModus) {
		this.directModus = directModus;
	}
	
	public void restartPathCalculationAfterCollision(ObjectMapManager collisionMap1, ObjectMapManager collisionMap2) {
//		System.out.println("restartPathCalculation of NormalAgent");
		this.setDirectModus(false);
		super.restartPathCalculationAfterCollision(collisionMap1, collisionMap2);
		int pathSeparation = (int) Math.ceil(this.getDistance(this.getActualGoal().getPosition().getX(), this.getActualGoal().getPosition().getY())/(4*GameStates.mapTileSetWidth));
		/** if no intermediate step will be found, we take the goal itself */
		WorldLatticePosition pathStepPosition = WorldLatticePosition.getClosestWorldLatticePosition(new WorldPosition(this.getActualGoal().getPosition().getX(), this.getActualGoal().getPosition().getY()));
		for (int i = 1; i <= pathSeparation; i++) {
			pathStepPosition = WorldLatticePosition.getClosestWorldLatticePosition(new WorldPosition(this.getX()+((this.getActualGoal().getPosition().getX()-this.getX())*((float)(i)/pathSeparation)), this.getY()+((this.getActualGoal().getPosition().getY()-this.getY())*((float)(i)/pathSeparation))));
			PathTile pathStepTile = new PathTile(pathStepPosition, pathStepPosition, 0, 0, 0);
			if ((collisionMap1.checkCollision(pathStepTile.getPosition().getMapCoordinates(), pathStepTile.getPosition().getLocalX(), pathStepTile.getPosition().getLocalY() ) ) ||
					(collisionMap2.checkCollision(pathStepTile.getPosition().getMapCoordinates(), pathStepTile.getPosition().getLocalX(), pathStepTile.getPosition().getLocalY() ) )) {
				/** collision!!! 
				 * this tile is not acceptable as an intermediate step * */
			} else {
				/** we found the first acceptable intermediate step!*/
				break;
			}
		}	
		Goal intermediateGoal = new Goal(WorldPosition.getWorldPosition(pathStepPosition));
		this.addFirstGoal(this.getActualGoal());
		this.setActualGoal(intermediateGoal);
	}
	
	public void nextGoal() {
		super.nextGoal();
		setDirectModus(true);
	}

	public long getTimestampPathCalculation() {
		return timestampPathCalculation;
	}

	public void setTimestampPathCalculation(long timestampPathCalculation) {
		this.timestampPathCalculation = timestampPathCalculation;
	}
}
