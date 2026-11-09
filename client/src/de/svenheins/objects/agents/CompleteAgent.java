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
import java.util.HashMap;
import java.util.NoSuchElementException;

import de.svenheins.functions.MyMath;
import de.svenheins.main.AttributeType;
import de.svenheins.main.EntityStates;
import de.svenheins.main.GameStates;
import de.svenheins.main.Priority;
import de.svenheins.managers.AgentManager;
import de.svenheins.managers.ObjectMapManager;
import de.svenheins.objects.AreaInfluence;
import de.svenheins.objects.ItemInfluence;
import de.svenheins.objects.LocalObject;
import de.svenheins.objects.PlayerEntity;
import de.svenheins.objects.TileSet;
import de.svenheins.objects.WorldLatticePosition;
import de.svenheins.objects.WorldPosition;
import de.svenheins.objects.agents.goals.Goal;
import de.svenheins.objects.agents.goals.GoalRankComparator;

public class CompleteAgent extends PlayerEntity{
	/** Motivation attributes */
	protected float satisfaction;
	protected float danger;
	protected boolean goal;
	protected float range;
	protected Goal actualGoal;
	protected WorldPosition actualPathElement;


	protected ArrayList<Goal> goalList = new ArrayList<Goal>(); // defines the goalList
	protected ArrayList<WorldPosition> pathList = new ArrayList<WorldPosition>(); // defines the way to the actualGoal
	protected boolean pathCalculationComplete = false;
	protected HashMap<WorldLatticePosition, PathTile> openList = new HashMap<WorldLatticePosition, PathTile>(); // defines the openList (A*-algorithm)
	protected HashMap<WorldLatticePosition, PathTile> closedList = new HashMap<WorldLatticePosition, PathTile>(); // defines the closedList (A*-algorithm)
	protected ArrayList<BigInteger> entityIDList = new ArrayList<BigInteger>(); // all known entities
	
	public static FScoreComparator fScoreComparator = new FScoreComparator();
	public static GoalRankComparator goalRankComparator = new GoalRankComparator();
	
	private boolean directModus;
	private long timestampPathCalculation;
	/** deceleration factor between 0.0f (standing still) and 1.0f (maxSPeed) */
	private float deceleration;
	
	private boolean attacking;
	private AreaInfluence attack;
	float[] attackAttributes;
	private long lastUpdate;
	
	
	public CompleteAgent(TileSet tileSet, String name, String room, BigInteger id, float x, float y, long animationDelay, boolean symmetricalEntity) {
		super(tileSet, name, room, id, x, y, animationDelay);
		satisfaction = 0;
		this.range = 10000;
		this.setVisible(true);
		goal = true;
		pathCalculationComplete = false;
		this.setRoom(room);
		this.setMaxSpeed(70.0f);
		this.setDirectModus(true);
		this.setDeceleration(1.0f);
		this.setSymmetricalEntity(symmetricalEntity);
		this.setAttacking(false);
		this.setLastUpdate(System.currentTimeMillis());
		this.setGroupName("aggressiveAgents");
		LocalObject localObject = new LocalObject(BigInteger.valueOf(0), this.getName(), this.getRoom(), this.getX(), this.getY(), GameStates.tileSetWidth, GameStates.tileSetHeight, 0,0,0,0);
		attackAttributes = new float[AttributeType.values().length];
		attackAttributes[AttributeType.LIFE.ordinal()] = this.getAttributes()[AttributeType.STRENGH.ordinal()];
		attack = new AreaInfluence(BigInteger.valueOf(0), 0, 0, localObject, this.getGroupName(), true, attackAttributes, Priority.HIGH, "agent:"+this.getName()+":"+this.getId().toString());
	}
	
	public void run(ObjectMapManager collisionMap1,
			ObjectMapManager collisionMap2) {
		/** update attributes of agent */
		if (this.getAttributes()[AttributeType.LIFE.ordinal()]>0 && this.isAlive()) {
			long timeNow = System.currentTimeMillis();
			long duration = timeNow - this.getLastUpdate();
			this.setLastUpdate(timeNow);
			this.updateAttributes(duration);
			attackAttributes[AttributeType.LIFE.ordinal()] = -this.getAttributes()[AttributeType.STRENGH.ordinal()];

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
	//					System.out.println("close enough to take NEXT GOAL!");
						if (this.getActualGoal().getEntity() == null) {
							this.nextGoal();
						}
					}
					
					/** if there still is a valid goal */
					if (this.getActualGoal() != null) {
						if (this.getActualGoal().getEntity() != null) {
							/** get the actual Attack properties */
							float attackSquareSize = this.getAttributes()[AttributeType.ATTACKSQUARESIZE.ordinal()];
							float attackDistance = this.getAttributes()[AttributeType.ATTACKDISTANCE.ordinal()];
							/** we have a valid entity as goal */
							if (this.getDistanceCenter(actualGoal.getEntity().getX()+actualGoal.getEntity().getWidth()/2, actualGoal.getEntity().getY()+actualGoal.getEntity().getHeight()/2) < (attackDistance +attackSquareSize/2 + Math.min(actualGoal.getEntity().getWidth(), actualGoal.getEntity().getHeight())/2 + Math.min(this.getWidth(), this.getHeight())/2)) {
								if (this.actualGoal.isAttackModus()) {
									if ( (timeNow-this.getLastAttack()) > GameStates.attackDelay/this.getAttributes()[AttributeType.ATTACKSPEED.ordinal()] ) {
										float agentCenterX = this.getX() + ((float)this.getWidth()/2);
										float agentCenterY = this.getY() + ((float)this.getHeight()/2);
										Point agentPos = new Point((int)agentCenterX,(int) agentCenterY);
										Point goalPos = new Point ((int) (this.getActualGoal().getEntity().getX()+this.getActualGoal().getEntity().getWidth()/2), (int) (this.getActualGoal().getEntity().getY()+this.getActualGoal().getEntity().getHeight()/2));
										float attackX;
										float attackY;
										/** calculate attack Center */
										if (MyMath.getDistance(agentPos, goalPos) >0) {
//											attackX = (float) (agentCenterX + ((Math.min(this.getWidth(), this.getHeight())/2)+attackDistance)*(((float)(goalPos.getX()- agentCenterX))/MyMath.getDistance(agentPos, goalPos))-(attackSquareSize/2));
//											attackY = (float) (agentCenterY + ((Math.min(this.getWidth(), this.getHeight())/2)+attackDistance)*(((float)(goalPos.getY()- agentCenterY))/MyMath.getDistance(agentPos, goalPos))-(attackSquareSize/2));
											attackX = goalPos.x-(attackSquareSize/2);//(float) (agentCenterX + ((Math.min(this.getWidth(), this.getHeight())/2)+attackDistance)*(((float)(goalPos.getX()- agentCenterX))/MyMath.getDistance(agentPos, goalPos))-(attackSquareSize/2));
											attackY = goalPos.y-(attackSquareSize/2);//(float) (agentCenterY + ((Math.min(this.getWidth(), this.getHeight())/2)+attackDistance)*(((float)(goalPos.getY()- agentCenterY))/MyMath.getDistance(agentPos, goalPos))-(attackSquareSize/2));
										} else {
											attackX = (float) goalPos.x-(attackSquareSize/2);
											attackY = (float) goalPos.y-(attackSquareSize/2);
										}
	//									System.out.println("attack: x="+attackX+" y="+attackY+" width="+attackSquareSize);
	//									System.out.println("goalPos: x="+goalPos.x+" y="+goalPos.y);
	//									System.out.println("entity: x="+this.getActualGoal().getEntity().getX()+" y="+this.getActualGoal().getEntity().getY()+" width="+this.getActualGoal().getEntity().getWidth()+" height="+this.getActualGoal().getEntity().getHeight());
										
										float[] slideAttributes = new float[AttributeType.values().length];
										slideAttributes[AttributeType.MX.ordinal()] = 4*(actualGoal.getEntity().getX()-this.getX());
										slideAttributes[AttributeType.MY.ordinal()] = 4*(actualGoal.getEntity().getY()-this.getY());
										ItemInfluence slideInfluence = new ItemInfluence(System.currentTimeMillis(), System.currentTimeMillis()+1*this.getAnimation().getTimeBetweenAnimation(), slideAttributes, Priority.HIGH);
										
										
										this.getAttack().getLocalObject().setX(attackX);
										this.getAttack().getLocalObject().setY(attackY);
										this.getAttack().getLocalObject().setWidth(attackSquareSize);
										this.getAttack().getLocalObject().setHeight(attackSquareSize);
										
										this.getAttack().getAttributes()[AttributeType.SENDMX.ordinal()] = slideAttributes[AttributeType.MX.ordinal()];
										this.getAttack().getAttributes()[AttributeType.SENDMY.ordinal()] = slideAttributes[AttributeType.MY.ordinal()];
												
	//									this.getAttack().getLocalObject().setWidth(this.);
	//									this.getAttack().getLocalObject().setHeight(duration);
										this.getAttack().setTimeBegin(timeNow);
										this.getAttack().setTimeEnd(timeNow);
										this.setAttacking(true);
										this.setLastAttack(timeNow);
	//									this.determineAttackingAnimation();
										this.setSingleState(EntityStates.ATTACKING);
										this.setChangedStates(true);
										
										this.addItemInfluence(slideInfluence);
									}
									
	//								/** check if the goalEntity is dead or is not a PlayerEntity */
	//								if (actualGoal.getEntity() instanceof PlayerEntity ) {
	//									if (((PlayerEntity) actualGoal.getEntity()).getAttributes()[AttributeType.LIFE.ordinal()]> 0) {
	//										// continue to attack
	//									} else {
	//										// enemy is dead -> take the next goal
	//										this.nextGoal();
	//									}
	//								} else {
	//									this.nextGoal();
	//								}
								} else {
									this.nextGoal();
								}
							}
						}
					}
					
					updateGoals();
				}
			} else {
				this.setGoal(false);
			}
		}
	}
		
		public void searchBetterLocation() {
			/** update entity goals */
			if (this.getActualGoal().getEntity() != null && this.getDistance(this.getActualGoal().getEntity().getX(), this.getActualGoal().getEntity().getY()) < GameStates.agentEntityVisionDistance) {
//				System.out.println("Actual Goal Entity is not null! ");
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
//				System.out.println("no entity as goal");
				
//				this.setActualPathElement(new WorldPosition (this.getX(), this.getY()+this.getHeight()/2));
//				this.setContinuousState(EntityStates.STANDING);
			}
			
			/** Direct Modus */
			if (isDirectModus()) {
				if (this.getActualGoal() != null) {			
//					/** here we already have a valid pathList */
//					this.setActualPathElement(this.getActualGoal().getPosition());
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
//					System.out.println("restart pathfinding...");
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
						this.setMovement(-this.getTotalAreaInfluences()[AttributeType.MX.ordinal()], -this.getTotalAreaInfluences()[AttributeType.MY.ordinal()]);
						/** position is close enough, so get next pathElement */
						this.nextPathElement();
//						System.out.println("next pathList element! remaining: "+this.pathList.size());
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
//						System.out.println("restart pathfinding...");
						if (goalList != null && (goalList.size() >0) ) {
							/** take the next goal */
//							System.out.println("goalList == null || goalList.size() == 0 -> nextGoal");
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
					if ( (!this.getActualGoal().getGoalEntityID().equals(BigInteger.valueOf(-1))) && (this.getActualGoal().getEntity() != null) ) {
						this.getActualGoal().setPosition(new WorldPosition(this.getActualGoal().getEntity().getX(), this.getActualGoal().getEntity().getY()));
					} else if (this.getActualGoal().getPosition() != null) {
						// no entity-ID but simple position as goal
					} else {
						// no valid goal!
//						System.out.println("no valid goal!");
					}
				}
			}
		}
		
		public void updateMovement() {
			long timeNow = System.currentTimeMillis();
			float areaInfluenceMX = this.getTotalAreaInfluences()[AttributeType.MX.ordinal()];
			float areaInfluenceMY = this.getTotalAreaInfluences()[AttributeType.MY.ordinal()];
			float actualVelocity = (float) Math.sqrt((this.getMX()+areaInfluenceMX)*(this.getMX()+areaInfluenceMX) + (this.getMY()+areaInfluenceMY)*(this.getMY()+areaInfluenceMY));
			float maxSpeed = this.getMaxSpeed();
			float newMX = (float) (this.getActualPathElement().getX()-(this.getX()));//-this.getAttributes()[AttributeType.MX.ordinal()]); 
			float newMY = (float) (this.getActualPathElement().getY()-(this.getY()+(this.getHeight()/2)));//-this.getAttributes()[AttributeType.MY.ordinal()]);
			
			float moveDirectionX, moveDirectionY;
			float vectorAx;
			float vectorAy;
			float lengthA;
			/** correct the destination if it is an old one */
			if ((newMX == 0 && newMY == 0)) {// && this.pathList.size() >0) {
				this.nextPathElement();
				this.setMovement(-this.getTotalAreaInfluences()[AttributeType.MX.ordinal()], -this.getTotalAreaInfluences()[AttributeType.MY.ordinal()]);
				/** if we have a path to follow */
//				System.out.println("old pathelement");
			} else {
				if ( (timeNow-this.getLastAttack()) > GameStates.attackDelay/this.getAttributes()[AttributeType.ATTACKSPEED.ordinal()] ) {
					this.setContinuousState(EntityStates.WALKING);
					float tempVelocity = (float) Math.sqrt(newMX*newMX + newMY*newMY);// (float) Math.sqrt((newMX+this.getAttributes()[AttributeType.MX.ordinal()])*(newMX+this.getAttributes()[AttributeType.MX.ordinal()]) + (newMY+this.getAttributes()[AttributeType.MY.ordinal()])*(newMY+this.getAttributes()[AttributeType.MY.ordinal()]));
					if (tempVelocity > 0) {
						float distanceGoal = getDistance(this.getActualPathElement().getX(), this.getActualPathElement().getY()-(this.getHeight()/2));
						/** adapt speed */
						if (isDirectModus()) {
							if ( distanceGoal < actualVelocity/(Math.max(1, 30-(AgentManager.size()/3)))) {
								this.setDeceleration(Math.max(GameStates.agentGoalDecelerationMinValue, this.getDeceleration()*(distanceGoal/(actualVelocity))));
							} else if (distanceGoal >= GameStates.agentGoalDecelerationResetDistance) {
								this.setDeceleration(1.0f);
							}
						} else {
							if ( distanceGoal < actualVelocity/(Math.max(1, 30-(AgentManager.size()/3)))) {
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
							if (distanceGoal<GameStates.agentGoalDistance) {
								this.setMovement(-this.getTotalAreaInfluences()[AttributeType.MX.ordinal()], -this.getTotalAreaInfluences()[AttributeType.MY.ordinal()]);
							}
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
		
		public void updatePathfindingDirect(ObjectMapManager collisionMap1,
				ObjectMapManager collisionMap2) {
			this.setActualPathElement(actualGoal.getPosition());
			this.setPathCalculationComplete(true);
		}
		
		public void updatePathfinding(ObjectMapManager collisionMap1,
				ObjectMapManager collisionMap2) {
			/** check if the path-finding was completed */
			if (isPathCalculationComplete()) {
				/** check if the path is leading to the goal */
				/** AND check if the path is still valid (no new blocking objects) */
//				System.out.println("READY!!!");
				/** else stop movement and calculate new path */
			} else {		
				/** else continue with path calculation */
				if (closedList.size() > 0 && openList.size() > 0) {
					/** so we already started the generation of the lists */
					long timeNow = System.currentTimeMillis();
					/** if too long time went by, stop the calculation and goto next goal */
					if (timeNow - this.getTimestampPathCalculation() > GameStates.timeForPathCalculation) {
//						System.out.println("NEXT GOAL, because time is up!");
						this.nextGoal();
						this.setDirectModus(false);
						return;
					}
//					System.out.println("time passed: "+(timeNow - this.getTimestampPathCalculation()));
					/** upper */
					PathTile originTile = this.getOpenListMinimumFScorePathTile();
//					System.out.println("F="+originTile.getFScore()+" G="+originTile.getGScore()+" H="+originTile.getHScore()); 
					if (originTile != null) {
						closedList.put(originTile.getPosition(), originTile); //(originTile);
						openList.remove(originTile.getPosition());
//						System.out.println("added to closedList: x="+originTile.getPosition().getLocalX()+" y="+originTile.getPosition().getLocalY());
//						System.out.println("with parent: x="+originTile.getParentPosition().getLocalX()+" y="+originTile.getParentPosition().getLocalY());
						
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
//							System.out.println("found a path!!!");
							this.pathList = createCompletePath(WorldLatticePosition.getWorldLatticePosition(actualGoal.getPosition()));
							this.setActualPathElement(pathList.get(0));
//							System.out.println("actualPathElement: X="+ this.getActualPathElement().getX()+" Y="+this.getActualPathElement().getY());
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
					if ((tileCollision1!=0) || (tileCollision2 !=0 )) {
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
		
		/** as soon as we are ready, complete the path and return it to the agent */
		public ArrayList<WorldPosition> createCompletePath(WorldLatticePosition goalLatticePosition) {
			ArrayList<WorldPosition> returnList = new ArrayList<WorldPosition>();
			ArrayList<WorldLatticePosition> saveWLPList = new ArrayList<WorldLatticePosition>();
			if (closedList.containsKey(goalLatticePosition)) {
				// TODO: handle if this happens
			} else if (openList.containsKey(goalLatticePosition)) {
//				System.out.println("PATH:");
				returnList.add(0, WorldPosition.getWorldPosition(goalLatticePosition));
				saveWLPList.add(0, goalLatticePosition);
//				System.out.println("x = "+returnList.get(0).getX()+" | y = "+returnList.get(0).getY());
				
				/** first adding from openList to closedList */
				WorldLatticePosition oldWLP = goalLatticePosition;
				WorldLatticePosition parentWLP = openList.get(goalLatticePosition).getParentPosition();
				returnList.add(0, WorldPosition.getWorldPosition(closedList.get(parentWLP).getPosition()));
				saveWLPList.add(0, closedList.get(parentWLP).getPosition());
//				System.out.println("x = "+returnList.get(0).getX()+" | y = "+returnList.get(0).getY());
				
				int sizeOfClosedList = closedList.size();			
				for (int i = 0; i < sizeOfClosedList-1; i++) {
//					System.out.println(i);
					oldWLP = saveWLPList.get(0);
					parentWLP = closedList.get(oldWLP).getParentPosition();
					saveWLPList.add(0, closedList.get(parentWLP).getPosition());
					/** only add the new coordinate if not yet existent */
					if (returnList.get(0).getX() != (WorldPosition.getWorldPosition(saveWLPList.get(0))).getX() ||
							returnList.get(0).getY() != (WorldPosition.getWorldPosition(saveWLPList.get(0))).getY()) {
						returnList.add(0, WorldPosition.getWorldPosition(saveWLPList.get(0)));
//						System.out.println("x = "+returnList.get(0).getX()+" | y = "+returnList.get(0).getY());
					} else {
//						System.out.println(sizeOfClosedList+ " NormalAgent: double entry in create Complete Path! "+sizeOfClosedList);
					
					}
					
				}
			}
			return returnList;
		}
	
	public float getSatisfaction() {
		return satisfaction;
	}

	public void setSatisfaction(float satisfaction) {
		this.satisfaction = satisfaction;
	}
		
	
	/** calculate distance to Point (x,y) */
	public float getDistance(float x, float y) {
		return (float) Math.sqrt((x-this.getX())*(x-this.getX()) + (y-(this.getY()))*(y-(this.getY())));
	}
	
	/** calculate center distance to Point (x,y) */
	public float getDistanceCenter(float x, float y) {
		return (float) Math.sqrt((x-(this.getX()+this.getWidth()/2))*(x-(this.getX()+this.getWidth()/2)) + (y-(this.getY()+this.getHeight()/2))*(y-(this.getY()+this.getHeight()/2)));
	}
	
//	/** calculate distance to Point (x,y) */
//	public int getDistance(int x, int y) {
//		return (int) Math.sqrt((x-this.getX())*(x-this.getX()) + (y-(this.getY()))*(y-(this.getY())));
//	}
	
	public void setGoal(boolean goal) {
		this.goal = goal;
	}
	
	public boolean hasGoal() {
		return goal;
	}

	public Goal getActualGoal() {
		return actualGoal;
	}
	
	public void addGoal( Goal additionalGoal)  {
		this.goalList.add(additionalGoal);
//		for (int i = 0; i < goalList.size(); i ++) {
//			System.out.println("Goal No: "+i+": x="+goalList.get(i).getPosition().getX()+" y="+goalList.get(i).getPosition().getY());
//		}
	}
	
	public void addFirstGoal( Goal additionalGoal)  {
//		for (int i = goalList.size(); i > 0; i--) {
//			goalList.add(i, goalList.get(i-1));
//		}
		this.goalList.add(0, additionalGoal);
//		for (int i = 0; i < goalList.size(); i ++) {
//			System.out.println("Goal No: "+i+": x="+goalList.get(i).getPosition().getX()+" y="+goalList.get(i).getPosition().getY());
//		}
	}

	public void setActualGoal(Goal actualGoal) {
		this.actualGoal = actualGoal;
	}
	
	public void sortGoalsByRank() {
		try {
			java.util.Collections.sort(goalList, goalRankComparator);
		} catch (NoSuchElementException e) {
			// do nothing
		}
	}
	
	/** return the minimum openList PathTile, depending on f-score */
	public PathTile getOpenListMinimumFScorePathTile() {
		PathTile retPathTile = null;
		if (openList.size() > 0 && openList != null ) {
			for (PathTile pTile : openList.values()) {
				if (retPathTile != null) {
					if (retPathTile.getFScore() > pTile.getFScore()){
						retPathTile = pTile;
					}
				} else {
					// first run
					retPathTile = pTile;
				}
			}
		}
		return retPathTile;
	}

	public boolean isPathCalculationComplete() {
		return pathCalculationComplete;
	}

	public void setPathCalculationComplete(boolean pathCalculationComplete) {
		this.pathCalculationComplete = pathCalculationComplete;
	}
	
	public void nextPathElement() {
		if (pathList.size() > 0 && pathList != null) {
//			System.out.println("old position: x="+pathList.get(0).getX()+" y="+pathList.get(0).getY());
			this.pathList.remove(0);
		}
	}
	
	public void nextGoal() {
//		System.out.println("next goal!");
		this.setMovement(-this.getTotalAreaInfluences()[AttributeType.MX.ordinal()], -this.getTotalAreaInfluences()[AttributeType.MY.ordinal()]);
		this.setContinuousState(EntityStates.STANDING);
		if (goalList != null && goalList.size()>0) {
			this.setGoal(true);
//			System.out.println("old position: x="+pathList.get(0).getX()+" y="+pathList.get(0).getY());
			this.actualGoal = goalList.get(0);
//			System.out.println("actualGoal: x="+actualGoal.getPosition().getX()+" y="+actualGoal.getPosition().getY());
			this.restartPathCalculation();
			if (this.goalList.remove(this.actualGoal)) {
				/** sucessfully removed the last goal */
			} else {
				System.out.println("Error CompleteAgent couldn't remove the last goal");
			}
//			for (int i = 0; i < goalList.size(); i ++) {
//				System.out.println("Goal No: "+i+": x="+goalList.get(i).getPosition().getX()+" y="+goalList.get(i).getPosition().getY());
//			}
		} else {
			this.setActualGoal(null);
			this.setGoal(false);
			System.out.println("no more goals!");
		}
		setDirectModus(true);
	}
	
	public float guessDistanceValue(WorldPosition pos1, WorldPosition pos2) {
		return (Math.abs(pos1.getX()-pos2.getX())/GameStates.mapTileSetWidth+ Math.abs(pos1.getY()-pos2.getY())/GameStates.mapTileSetHeight);
	}

	public ArrayList<Goal> getGoalList() {
		return goalList;
	}

	public void setGoalList(ArrayList<Goal> goalList) {
		this.goalList = goalList;
	}

	protected WorldPosition getActualPathElement() {
		return actualPathElement;
	}

	protected void setActualPathElement(WorldPosition actualPathElement) {
		this.actualPathElement = actualPathElement;
	}
	
	public void restartPathCalculation() {
		this.setMovement(-this.getTempTotalAreaInfluences()[AttributeType.MX.ordinal()], -this.getTotalAreaInfluences()[AttributeType.MY.ordinal()]);
		this.setContinuousState(EntityStates.STANDING);
		pathList = new ArrayList<WorldPosition>(); // defines the way to the actualGoal
		pathCalculationComplete = false;
		openList = new HashMap<WorldLatticePosition, PathTile>(); // defines the openList (A*-algorithm)
		closedList = new HashMap<WorldLatticePosition, PathTile>(); // defines the closedList (A*-algorithm)
		this.setActualPathElement(null); // important to set it null!
	}
	
	
	public void updateGoals() {
		// TODO Auto-generated method stub
		
	}

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
		/** Orientation and animation */
		if (this.hasSymmetricalEntity()) {
			this.determineOrientation(new Point((int) this.getActualGoal().getPosition().getX(),(int) this.getActualGoal().getPosition().getY()));
		} else {
			// treat special Entities, which are to wide for pathfinding
			this.determineOrientation(new Point((int) (this.getX()+(this.getWidth()/2)),(int) this.getActualGoal().getPosition().getY()));
		}
		restartPathCalculation();
		int pathSeparation = (int) Math.ceil(this.getDistance(this.getActualGoal().getPosition().getX(), this.getActualGoal().getPosition().getY())/(4*GameStates.mapTileSetWidth));
		/** if no intermediate step will be found, we take the goal itself */
		WorldLatticePosition pathStepPosition = WorldLatticePosition.getClosestWorldLatticePosition(new WorldPosition(this.getActualGoal().getPosition().getX(), this.getActualGoal().getPosition().getY()));
		for (int i = 1; i <= 2*pathSeparation; i++) {
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
		}	
		Goal intermediateGoal = new Goal(WorldPosition.getWorldPosition(pathStepPosition));
		this.addFirstGoal(this.getActualGoal());
		this.setActualGoal(intermediateGoal);
//		System.out.println("OK, found goals "+this.getGoalList().size());
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
	
	public boolean isAttacking() {
		return attacking;
	}

	public void setAttacking(boolean attacking) {
		this.attacking = attacking;
	}

	public AreaInfluence getAttack() {
		return attack;
	}

	public void setAttack(AreaInfluence attack) {
		this.attack = attack;
	}

	public long getLastUpdate() {
		return lastUpdate;
	}

	public void setLastUpdate(long lastUpdate) {
		this.lastUpdate = lastUpdate;
	}
}
