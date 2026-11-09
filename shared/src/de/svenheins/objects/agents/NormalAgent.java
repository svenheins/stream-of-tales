/* Copyright (C) Sven Heins - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * author: Sven Heins
 * date: 05.07.2015
 */
package de.svenheins.objects.agents;

import java.awt.Point;
import java.math.BigInteger;
import java.util.ArrayList;

import de.svenheins.functions.MyMath;
import de.svenheins.main.AttributeType;
import de.svenheins.main.EntityStates;
import de.svenheins.main.GameStates;
import de.svenheins.managers.ObjectMapManager;
import de.svenheins.managers.SharedAgentManager;
import de.svenheins.objects.TileSet;
import de.svenheins.objects.WorldLatticePosition;
import de.svenheins.objects.WorldPosition;
import de.svenheins.objects.agents.goals.Goal;

public class NormalAgent extends Agent {
	private boolean directModus;
	private long timestampPathCalculation;
	/** deceleration factor between 0.0f (standing still) and 1.0f (maxSPeed) */
	private float deceleration;
	
	public NormalAgent(TileSet tileSet, String name, String room, BigInteger id, float x,
			float y, long animationDelay, boolean symmetricalEntity) {
		super(tileSet, name, room, id, x, y, animationDelay);
		this.setMaxSpeed(70.0f);
		this.setDirectModus(true);
		this.setDeceleration(1.0f);
		this.setSymmetricalEntity(symmetricalEntity);
	}

	@Override
	public void run(ObjectMapManager collisionMap1,
			ObjectMapManager collisionMap2) {
		updateGoalRanking();
		if (actualGoal != null) {
			System.out.println("search better goal location normal agent");
			searchBetterLocation();
			if (actualGoal != null) {
				/** only search if we are too far away from goal */
				if ( this.getDistance(actualGoal.getPosition().getX(), actualGoal.getPosition().getY()-this.getHeight()/2) > GameStates.agentGoalDistance) {	
					if (isPathCalculationComplete()) {
						updateMovement();
					} else {
						this.setMovement(-this.getTotalAreaInfluences()[AttributeType.MX.ordinal()], -this.getTotalAreaInfluences()[AttributeType.MY.ordinal()]);
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
//					System.out.println("close enough to take NEXT GOAL!");
					if (this.getActualGoal().getEntity() == null) {
						this.nextGoal();
					}
					else {
						if (this.getDistance(actualGoal.getEntity().getX(), actualGoal.getEntity().getY()-this.getHeight()/2) <= GameStates.agentGoalDistance) {
							this.nextGoal();
						}
					}
				}
				updateGoals();
			}
		} else {
//			this.setGoal(false);
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
		long timeNow = System.currentTimeMillis();
		float areaInfluenceMX = this.getTotalAreaInfluences()[AttributeType.MX.ordinal()];
		float areaInfluenceMY = this.getTotalAreaInfluences()[AttributeType.MY.ordinal()];
		float actualVelocity = (float) Math.sqrt((this.getMX()+areaInfluenceMX)*(this.getMX()+areaInfluenceMX) + (this.getMY()+areaInfluenceMY)*(this.getMY()+areaInfluenceMY));
		float maxSpeed = this.getMaxSpeed();
		float newMX = 0; 
		float newMY = 0;
		if (this.getActualPathElement() != null) {
			newMX = (float) (this.getActualPathElement().getX()-(this.getX()));//-this.getAttributes()[AttributeType.MX.ordinal()]); 
			newMY = (float) (this.getActualPathElement().getY()-(this.getY()+(this.getHeight()/2)));//-this.getAttributes()[AttributeType.MY.ordinal()]);
		}
		
		float moveDirectionX, moveDirectionY;
		float vectorAx;
		float vectorAy;
		float lengthA;
		/** correct the destination if it is an old one */
		if ((newMX == 0 && newMY == 0)) {// && this.pathList.size() >0) {
			this.nextPathElement();
			this.setMovement(-this.getTotalAreaInfluences()[AttributeType.MX.ordinal()], -this.getTotalAreaInfluences()[AttributeType.MY.ordinal()]);
			/** if we have a path to follow */
//			System.out.println("old pathelement");
		} else {
			if ( (timeNow-this.getLastAttack()) > GameStates.attackDelay/this.getAttributes()[AttributeType.ATTACKSPEED.ordinal()] ) {
//				this.setContinuousState(EntityStates.WALKING);
				this.setWalkingAnimation();
				float tempVelocity = (float) Math.sqrt(newMX*newMX + newMY*newMY);// (float) Math.sqrt((newMX+this.getAttributes()[AttributeType.MX.ordinal()])*(newMX+this.getAttributes()[AttributeType.MX.ordinal()]) + (newMY+this.getAttributes()[AttributeType.MY.ordinal()])*(newMY+this.getAttributes()[AttributeType.MY.ordinal()]));
				if (tempVelocity > 0) {
					float distanceGoal;
					if (this.getActualPathElement() != null) {
						distanceGoal = getDistance(this.getActualPathElement().getX(), this.getActualPathElement().getY()-(this.getHeight()/2));
					} else {
						System.out.println("ActualPathElement is NULL! -> return without speed adaption (NormalAgent)");
						return;
					}
					/** adapt speed */
					if (isDirectModus()) {
						if ( distanceGoal < actualVelocity/(Math.max(1, 30-(SharedAgentManager.size/3)))) {
							this.setDeceleration(Math.max(GameStates.agentGoalDecelerationMinValue, this.getDeceleration()*(distanceGoal/(actualVelocity))));
						} else if (distanceGoal >= GameStates.agentGoalDecelerationResetDistance) {
							this.setDeceleration(1.0f);
						}
					} else {
						if ( distanceGoal < actualVelocity/(Math.max(1, 30-(SharedAgentManager.size/3)))) {
							this.setDeceleration(this.getDeceleration()*(distanceGoal/(actualVelocity)));
						} else {
							this.setDeceleration(1.0f);
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
						this.setMovement(this.getDeceleration()*moveDirectionX, this.getDeceleration()*moveDirectionY);
					} else {
						this.setMovement( this.getDeceleration()*maxSpeed*(newMX/tempVelocity), this.getDeceleration()*maxSpeed*(newMY/tempVelocity));
					}
					
					if ( (actualGoal.getEntity() != null) && (isDirectModus())) {
						/** is another distance for entity goals */
	//					distanceGoal = getDistance(this.getActualPathElement().getX(), this.getActualPathElement().getY());
//						if (distanceGoal<GameStates.agentGoalDistance*3) {
//							this.setRoom(actualGoal.getEntity().getRoom());
							if (distanceGoal<GameStates.agentGoalDistance) {
								this.setMovement(-this.getTotalAreaInfluences()[AttributeType.MX.ordinal()], -this.getTotalAreaInfluences()[AttributeType.MY.ordinal()]);
							}
//						}
					}
	
				} else
				{
					this.setMovement(-this.getTotalAreaInfluences()[AttributeType.MX.ordinal()], -this.getTotalAreaInfluences()[AttributeType.MY.ordinal()]);
					/** if we have a path to follow */
	//				System.out.println("STOP, because we reached the pathlistelement");
				}
			} else {
				// wait for the attack-delay to end
			}
		}

		/** Orientation and animation */
		if (this.hasSymmetricalEntity() || this.isDirectModus()) {
			this.determineOrientation(new Point((int) this.getActualGoal().getPosition().getX(),(int) this.getActualGoal().getPosition().getY()));
		} else {
			// treat special Entities, which are to wide for pathfinding
			this.determineOrientation(new Point((int) (this.getX()+(this.getWidth()/2)),(int) this.getActualGoal().getPosition().getY()));
		}
	}


	@Override
	public void searchBetterLocation() {
		System.out.println("searching better location");
		/** update entity goals */
		if (this.getActualGoal().getEntity() != null && this.getDistance(this.getActualGoal().getEntity().getX(), this.getActualGoal().getEntity().getY()) < GameStates.agentEntityVisionDistance) {
			System.out.println("Actual Goal Entity is not null! ");
			this.getActualGoal().setPosition(new WorldPosition(this.getActualGoal().getEntity().getX(), this.getActualGoal().getEntity().getY()+this.getActualGoal().getEntity().getHeight()/2));
			if (isDirectModus()) {
				this.setActualPathElement(actualGoal.getPosition());
			} else {
				if (this.getGoalList().size() > 1) {
					this.getGoalList().get(1).setPosition(actualGoal.getPosition());
				}
			}
			this.setContinuousState(EntityStates.WALKING);
			
		} else {
//			System.out.println("no entity as goal");
			/** check if the current path is not optimal because the goal has moved towards me */
			
//			this.setActualPathElement(new WorldPosition (this.getX(), this.getY()+this.getHeight()/2));
//			this.setContinuousState(EntityStates.STANDING);
		}
		
		/** Direct Modus */
		if (isDirectModus()) {
			if (this.getActualGoal() != null) {			
//				/** here we already have a valid pathList */
//				this.setActualPathElement(this.getActualGoal().getPosition());
				if (this.getActualGoal().getEntity() == null) {
					/** if the agent is close enough */
					if ( getDistance(this.getActualGoal().getPosition().getX(), this.getActualGoal().getPosition().getY()-(this.getHeight()/2)) < GameStates.pathMinAcceptanceDistance) {		
						this.setMovement(-this.getTotalAreaInfluences()[AttributeType.MX.ordinal()], -this.getTotalAreaInfluences()[AttributeType.MY.ordinal()]);
						/** ensure we landed exactly on the right spot */
						this.setX(this.getActualGoal().getPosition().getX());
						this.setY(this.getActualGoal().getPosition().getY()-(this.getHeight()/2));
						/** position is close enough, so get next goal */
						this.nextGoal();
					}
				} else {
					/** if the agent is close to the entity */
					if ( getDistance(this.getActualGoal().getPosition().getX(), this.getActualGoal().getPosition().getY()-(this.getHeight()/2)) <= GameStates.agentGoalDistance) {		
						this.setMovement(-this.getTotalAreaInfluences()[AttributeType.MX.ordinal()], -this.getTotalAreaInfluences()[AttributeType.MY.ordinal()]);
					}
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
			System.out.println("indirect mode");
			/** *********** indirectModus **************** 
			 * */
			/** update the pathList */
			if (this.pathList != null && this.pathList.size() > 0 ) {
				System.out.println("pathlist > 0");
				if (this.pathList.get(0) != null) {

					if ( getDistance(this.pathList.get(0).getX(), this.pathList.get(0).getY()-(this.getHeight()/2)) < GameStates.pathMinAcceptanceDistance) {
						/** ensure we landed exactly on the right spot */
						this.setX(this.pathList.get(0).getX());
						this.setY(this.pathList.get(0).getY()-(this.getHeight()/2));
						this.setMovement(-this.getTotalAreaInfluences()[AttributeType.MX.ordinal()], -this.getTotalAreaInfluences()[AttributeType.MY.ordinal()]);
						/** position is close enough, so get next pathElement */
						this.nextPathElement();
						
						
						
	//					System.out.println("next pathList element! remaining: "+this.pathList.size());
					} else {
		//				System.out.println("Distance = "+getDistance(this.pathList.get(0).getX(), this.pathList.get(0).getY()-(this.getHeight()/2)));
					}
				}
				if (this.pathList != null && this.pathList.size() > 0) {
					System.out.println("pathlist is still > 0");
					/** here we already have a valid pathList */
					
					if ( this.pathList.get(0) != null) {
						this.setActualPathElement(this.pathList.get(0));
					}
					
					/** optimization: check if the real goal (goal[1]) is nearer than from the last pathlist point*/
					if ( this.getActualGoal().getEntity() != null) { // this.getGoalList().size() > 1 &&
						if (getDistance(this.getActualGoal().getEntity().getX(), this.getActualGoal().getEntity().getY()) < 300) {//if (getDistance(this.getGoalList().get(1).getPosition().getX(), this.getGoalList().get(1).getPosition().getY()) < 300) {//MyMath.getDistance(pathList.get(pathList.size()-1), getGoalList().get(1).getPosition())) {
							System.out.println("EXIT actual pathfinding!!!!!!!!");
							
							// Abort the actual pathfinding, because the goal is better reachable from the current position
							/** here the pathList does not yet exist or the pathList might be empty */
							this.pathList = new ArrayList<WorldPosition>();
							this.setPathCalculationComplete(false);
//							System.out.println("restart pathfinding...");
							if (goalList != null && (goalList.size() >0) ) {
								/** take the next goal */
//								System.out.println("goalList == null || goalList.size() == 0 -> nextGoal");
								this.nextGoal();
							} else {
								/** no more goals available */
								setActualGoal(null);
								this.setMovement(0, 0);
								this.setContinuousState(EntityStates.STANDING);
								this.setChangedStates(true);
							}
						} else {
							System.out.println("Distance: "+MyMath.getDistance(pathList.get(pathList.size()-1), getGoalList().get(1).getPosition()));
						}
					}
//					/** end of optimization */
				} else {
//					System.out.println("pathlist is not anymore > 0");
					/** here the pathList does not yet exist or the pathList might be empty */
					this.pathList = new ArrayList<WorldPosition>();
					this.setPathCalculationComplete(false);
					
					this.nextGoal();
					
////					System.out.println("restart pathfinding...");
//					if (goalList != null && (goalList.size() >0) ) {
//						/** take the next goal */
////						System.out.println("goalList == null || goalList.size() == 0 -> nextGoal");
//						this.nextGoal();
//					} else {
//						/** no more goals available */
//						setActualGoal(null);
//						this.setMovement(0, 0);
//						this.setContinuousState(EntityStates.STANDING);
//						this.setChangedStates(true);
//					}
				}
				
				 
			} else {
//				System.out.println("no more pathlist elements");
//			/** here we have no more pathList, so check for other goals or positions */
//				/** if there is an entityID as goal, update the location depending on position */
//				if ( (!this.getActualGoal().getGoalEntityID().equals(BigInteger.valueOf(-1))) && (this.getActualGoal().getEntity() != null) ) {
//					this.getActualGoal().setPosition(new WorldPosition(this.getActualGoal().getEntity().getX(), this.getActualGoal().getEntity().getY()));
//				} else if (this.getActualGoal().getPosition() != null) {
//					// no entity-ID but simple position as goal
//				} else {
//					// no valid goal!
////					System.out.println("no valid goal!");
//				}
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
//					System.out.println("NEXT GOAL, because time is up!");
					this.nextGoal();
					this.setDirectModus(false);
					return;
				}
//				System.out.println("time passed: "+(timeNow - this.getTimestampPathCalculation()));
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
				int tileCollision1 = collisionMap1.checkCollision(goalTile.getPosition().getMapCoordinates(), goalTile.getPosition().getLocalX(), goalTile.getPosition().getLocalY() ) ;
				int tileCollision2 = collisionMap2.checkCollision(goalTile.getPosition().getMapCoordinates(), goalTile.getPosition().getLocalX(), goalTile.getPosition().getLocalY() );
				
				if (this.determineAnimationWidth(EntityStates.DOWN) > GameStates.mapTileSetWidth || this.determineAnimationHeight(EntityStates.DOWN)/2 > GameStates.mapTileSetHeight) {
					/** check additional fields */
					WorldLatticePosition wlp1r = WorldLatticePosition.shiftWorldLatticePosition(goalPosition, 1, 0);
					WorldLatticePosition wlp2r = WorldLatticePosition.shiftWorldLatticePosition(goalPosition, 1, 0);
					WorldLatticePosition wlp1d = WorldLatticePosition.shiftWorldLatticePosition(goalPosition, 0, 1);
					WorldLatticePosition wlp2d = WorldLatticePosition.shiftWorldLatticePosition(goalPosition, 0, 1);
					WorldLatticePosition wlp1dr = WorldLatticePosition.shiftWorldLatticePosition(goalPosition, 1, 1);
					WorldLatticePosition wlp2dr = WorldLatticePosition.shiftWorldLatticePosition(goalPosition, 1, 1);
					
					int tileCollision1r = collisionMap1.checkCollision(wlp1r.getMapCoordinates(), wlp1r.getLocalX(), wlp1r.getLocalY() ) ;
					int tileCollision2r = collisionMap2.checkCollision(wlp2r.getMapCoordinates(), wlp2r.getLocalX(), wlp2r.getLocalY() );
					int tileCollision1d = collisionMap1.checkCollision(wlp1d.getMapCoordinates(), wlp1d.getLocalX(), wlp1d.getLocalY() ) ;
					int tileCollision2d = collisionMap2.checkCollision(wlp2d.getMapCoordinates(), wlp2d.getLocalX(), wlp2d.getLocalY() );
					int tileCollision1dr = collisionMap1.checkCollision(wlp1dr.getMapCoordinates(), wlp1dr.getLocalX(), wlp1dr.getLocalY() ) ;
					int tileCollision2dr = collisionMap2.checkCollision(wlp2dr.getMapCoordinates(), wlp2dr.getLocalX(), wlp2dr.getLocalY() );
							
					if (this.determineAnimationWidth(EntityStates.DOWN) > GameStates.mapTileSetWidth*2 || this.determineAnimationHeight(EntityStates.DOWN)/2 > GameStates.mapTileSetHeight*2) {
						/** check additional fields */
					}
					
					tileCollision1 += tileCollision1r+tileCollision1d+tileCollision1dr;
					tileCollision2 += tileCollision2r + tileCollision2d + tileCollision2dr;
				}
				
				if ((tileCollision1!=0) || (tileCollision2 !=0 )) {
					/** collision!!! 
					 * GoalTile is definitely not reachable		
					 * so begin the pathCalculation with the next goal!			 * */
//					this.nextGoal();
//					this.setDirectModus(true);
					
					this.goalList = new ArrayList<Goal>();
					this.setActualGoal(null);
					
//					this.restartPathCalculationAfterCollision(collisionMap1, collisionMap2);
					System.out.println("Occupied Goal for agent "+this.getId().longValue()+"! -> search a way to the next goal!");
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
//					System.out.println(sizeOfClosedList+ " NormalAgent: double entry in create Complete Path! "+sizeOfClosedList);
				
				}
				
			}
		}
		return returnList;
	}

	/** check if the tile is valid (no collision-tile) and then check how to rank this tile in the lists */
	public void checkTile(PathTile originTile, WorldPosition tileWorldPosition, ObjectMapManager collisionMap1, ObjectMapManager collisionMap2) {
		WorldLatticePosition tileWorldLatticePosition = WorldLatticePosition.getWorldLatticePosition(tileWorldPosition);
		int tileCollision1 = collisionMap1.checkCollision(tileWorldLatticePosition.getMapCoordinates(), tileWorldLatticePosition.getLocalX(), tileWorldLatticePosition.getLocalY() ) ;
		int tileCollision2 = collisionMap2.checkCollision(tileWorldLatticePosition.getMapCoordinates(), tileWorldLatticePosition.getLocalX(), tileWorldLatticePosition.getLocalY() ); 
		
		if (this.determineAnimationWidth(EntityStates.DOWN) > GameStates.mapTileSetWidth || this.determineAnimationHeight(EntityStates.DOWN)/2 > GameStates.mapTileSetHeight) {
			/** check additional fields */
			WorldLatticePosition wlp1r = WorldLatticePosition.shiftWorldLatticePosition(tileWorldLatticePosition, 1, 0);
			WorldLatticePosition wlp2r = WorldLatticePosition.shiftWorldLatticePosition(tileWorldLatticePosition, 1, 0);
			WorldLatticePosition wlp1d = WorldLatticePosition.shiftWorldLatticePosition(tileWorldLatticePosition, 0, 1);
			WorldLatticePosition wlp2d = WorldLatticePosition.shiftWorldLatticePosition(tileWorldLatticePosition, 0, 1);
			WorldLatticePosition wlp1dr = WorldLatticePosition.shiftWorldLatticePosition(tileWorldLatticePosition, 1, 1);
			WorldLatticePosition wlp2dr = WorldLatticePosition.shiftWorldLatticePosition(tileWorldLatticePosition, 1, 1);
			
			int tileCollision1r = collisionMap1.checkCollision(wlp1r.getMapCoordinates(), wlp1r.getLocalX(), wlp1r.getLocalY() ) ;
			int tileCollision2r = collisionMap2.checkCollision(wlp2r.getMapCoordinates(), wlp2r.getLocalX(), wlp2r.getLocalY() );
			int tileCollision1d = collisionMap1.checkCollision(wlp1d.getMapCoordinates(), wlp1d.getLocalX(), wlp1d.getLocalY() ) ;
			int tileCollision2d = collisionMap2.checkCollision(wlp2d.getMapCoordinates(), wlp2d.getLocalX(), wlp2d.getLocalY() );
			int tileCollision1dr = collisionMap1.checkCollision(wlp1dr.getMapCoordinates(), wlp1dr.getLocalX(), wlp1dr.getLocalY() ) ;
			int tileCollision2dr = collisionMap2.checkCollision(wlp2dr.getMapCoordinates(), wlp2dr.getLocalX(), wlp2dr.getLocalY() );
					
			if (this.determineAnimationWidth(EntityStates.DOWN) > GameStates.mapTileSetWidth*2 || this.determineAnimationHeight(EntityStates.DOWN)/2 > GameStates.mapTileSetHeight*2) {
				/** check additional fields */
			}
			
			tileCollision1 += tileCollision1r+tileCollision1d+tileCollision1dr;
			tileCollision2 += tileCollision2r + tileCollision2d + tileCollision2dr;
		}
		
		if ((tileCollision1!=0 ) ||( tileCollision2!=0)) {
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
		int tileCollision1 = collisionMap1.checkCollision(tile.getPosition().getMapCoordinates(), tile.getPosition().getLocalX(), tile.getPosition().getLocalY() );
		int tileCollision2 = collisionMap2.checkCollision(tile.getPosition().getMapCoordinates(), tile.getPosition().getLocalX(), tile.getPosition().getLocalY() );
		
		if (this.determineAnimationWidth(EntityStates.DOWN) > GameStates.mapTileSetWidth || this.determineAnimationHeight(EntityStates.DOWN)/2 > GameStates.mapTileSetHeight) {
			/** check additional fields */
			WorldLatticePosition wlp1r = WorldLatticePosition.shiftWorldLatticePosition(tile.getPosition(), 1, 0);
			WorldLatticePosition wlp2r = WorldLatticePosition.shiftWorldLatticePosition(tile.getPosition(), 1, 0);
			WorldLatticePosition wlp1d = WorldLatticePosition.shiftWorldLatticePosition(tile.getPosition(), 0, 1);
			WorldLatticePosition wlp2d = WorldLatticePosition.shiftWorldLatticePosition(tile.getPosition(), 0, 1);
			WorldLatticePosition wlp1dr = WorldLatticePosition.shiftWorldLatticePosition(tile.getPosition(), 1, 1);
			WorldLatticePosition wlp2dr = WorldLatticePosition.shiftWorldLatticePosition(tile.getPosition(), 1, 1);
			
			int tileCollision1r = collisionMap1.checkCollision(wlp1r.getMapCoordinates(), wlp1r.getLocalX(), wlp1r.getLocalY() ) ;
			int tileCollision2r = collisionMap2.checkCollision(wlp2r.getMapCoordinates(), wlp2r.getLocalX(), wlp2r.getLocalY() );
			int tileCollision1d = collisionMap1.checkCollision(wlp1d.getMapCoordinates(), wlp1d.getLocalX(), wlp1d.getLocalY() ) ;
			int tileCollision2d = collisionMap2.checkCollision(wlp2d.getMapCoordinates(), wlp2d.getLocalX(), wlp2d.getLocalY() );
			int tileCollision1dr = collisionMap1.checkCollision(wlp1dr.getMapCoordinates(), wlp1dr.getLocalX(), wlp1dr.getLocalY() ) ;
			int tileCollision2dr = collisionMap2.checkCollision(wlp2dr.getMapCoordinates(), wlp2dr.getLocalX(), wlp2dr.getLocalY() );
					
			if (this.determineAnimationWidth(EntityStates.DOWN) > GameStates.mapTileSetWidth*2 || this.determineAnimationHeight(EntityStates.DOWN)/2 > GameStates.mapTileSetHeight*2) {
				/** check additional fields */
			}
			
			tileCollision1 += tileCollision1r+tileCollision1d+tileCollision1dr;
			tileCollision2 += tileCollision2r + tileCollision2d + tileCollision2dr;
		}
		
		if (( tileCollision1 !=0) ||( tileCollision2!=0)) {
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
		if (this.getActualGoal() != null) {
	//		System.out.println("restartPathCalculation of NormalAgent");
			this.setDirectModus(false);
			/** Orientation and animation */
			if (this.hasSymmetricalEntity()) {
				this.determineOrientation(new Point((int) this.getActualGoal().getPosition().getX(),(int) this.getActualGoal().getPosition().getY()));
			} else {
				// treat special Entities, which are to wide for pathfinding
				if (this.getActualGoal() != null) {
					this.determineOrientation(new Point((int) (this.getX()+(this.getWidth()/2)),(int) this.getActualGoal().getPosition().getY()));
				}
			}
			super.restartPathCalculationAfterCollision(collisionMap1, collisionMap2);
			int pathSeparation = (int) Math.ceil(this.getDistance(this.getActualGoal().getPosition().getX(), this.getActualGoal().getPosition().getY())/(4*GameStates.mapTileSetWidth));
			/** if no intermediate step will be found, we take the goal itself */
			WorldLatticePosition pathStepPosition = WorldLatticePosition.getClosestWorldLatticePosition(new WorldPosition(this.getActualGoal().getPosition().getX(), this.getActualGoal().getPosition().getY()));
			if (pathStepPosition != null) {
				for (int i = 1; i <= 2*pathSeparation; i++) {
					if (this.getActualGoal() != null) {
						pathStepPosition = WorldLatticePosition.getClosestWorldLatticePosition(new WorldPosition(this.getX()+((this.getActualGoal().getPosition().getX()-this.getX())*((float)(i)/pathSeparation)), this.getY()+((this.getActualGoal().getPosition().getY()-this.getY())*((float)(i)/pathSeparation))));
						PathTile pathStepTile = new PathTile(pathStepPosition, pathStepPosition, 0, 0, 0);
						int tileCollision1 =collisionMap1.checkCollision(pathStepTile.getPosition().getMapCoordinates(), pathStepTile.getPosition().getLocalX(), pathStepTile.getPosition().getLocalY() );
						int tileCollision2 =collisionMap2.checkCollision(pathStepTile.getPosition().getMapCoordinates(), pathStepTile.getPosition().getLocalX(), pathStepTile.getPosition().getLocalY() ) ; 
						
						if (this.determineAnimationWidth(EntityStates.DOWN) > GameStates.mapTileSetWidth || this.determineAnimationHeight(EntityStates.DOWN)/2 > GameStates.mapTileSetHeight) {
							/** check additional fields */
							WorldLatticePosition wlp1r = WorldLatticePosition.shiftWorldLatticePosition(pathStepPosition, 1, 0);
							WorldLatticePosition wlp2r = WorldLatticePosition.shiftWorldLatticePosition(pathStepPosition, 1, 0);
							WorldLatticePosition wlp1d = WorldLatticePosition.shiftWorldLatticePosition(pathStepPosition, 0, 1);
							WorldLatticePosition wlp2d = WorldLatticePosition.shiftWorldLatticePosition(pathStepPosition, 0, 1);
							WorldLatticePosition wlp1dr = WorldLatticePosition.shiftWorldLatticePosition(pathStepPosition, 1, 1);
							WorldLatticePosition wlp2dr = WorldLatticePosition.shiftWorldLatticePosition(pathStepPosition, 1, 1);
							
							int tileCollision1r = collisionMap1.checkCollision(wlp1r.getMapCoordinates(), wlp1r.getLocalX(), wlp1r.getLocalY() ) ;
							int tileCollision2r = collisionMap2.checkCollision(wlp2r.getMapCoordinates(), wlp2r.getLocalX(), wlp2r.getLocalY() );
							int tileCollision1d = collisionMap1.checkCollision(wlp1d.getMapCoordinates(), wlp1d.getLocalX(), wlp1d.getLocalY() ) ;
							int tileCollision2d = collisionMap2.checkCollision(wlp2d.getMapCoordinates(), wlp2d.getLocalX(), wlp2d.getLocalY() );
							int tileCollision1dr = collisionMap1.checkCollision(wlp1dr.getMapCoordinates(), wlp1dr.getLocalX(), wlp1dr.getLocalY() ) ;
							int tileCollision2dr = collisionMap2.checkCollision(wlp2dr.getMapCoordinates(), wlp2dr.getLocalX(), wlp2dr.getLocalY() );
									
							if (this.determineAnimationWidth(EntityStates.DOWN) > GameStates.mapTileSetWidth*2 || this.determineAnimationHeight(EntityStates.DOWN)/2 > GameStates.mapTileSetHeight*2) {
								/** check additional fields */
							}
							
							tileCollision1 += tileCollision1r+tileCollision1d+tileCollision1dr;
							tileCollision2 += tileCollision2r + tileCollision2d + tileCollision2dr;
						}
						
						if ((tileCollision1!=0 ) ||	(tileCollision2!=0)) {
							/** collision!!! 
							 * this tile is not acceptable as an intermediate step * */
						} else {
							/** we found the first acceptable intermediate step!*/
							break;
						}
					} else {
						this.nextGoal();
						this.setDirectModus(true);
						break;
					}
				}	
				Goal intermediateGoal = new Goal(WorldPosition.getWorldPosition(pathStepPosition));
				this.addFirstGoal(this.getActualGoal());
				this.setActualGoal(intermediateGoal);
			} else {
				/** pathStepPosition == null */
				this.nextGoal();
				this.setDirectModus(true);
			}
			
//		System.out.println("OK, found goals "+this.getGoalList().size());
		}
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

	public float getDeceleration() {
		return deceleration;
	}

	public void setDeceleration(float deceleration) {
		this.deceleration = deceleration;
	}
}
