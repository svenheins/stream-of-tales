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
import de.svenheins.main.Priority;
import de.svenheins.managers.ObjectMapManager;
import de.svenheins.objects.AreaInfluence;
import de.svenheins.objects.ItemInfluence;
import de.svenheins.objects.LocalObject;
import de.svenheins.objects.TileSet;
import de.svenheins.objects.WorldPosition;

public class AggressiveAgent extends NormalAgent {
	
	private boolean attacking;
	private AreaInfluence attack;
	float[] attackAttributes;
	private long lastUpdate;

	public AggressiveAgent(TileSet tileSet, String name, String room, BigInteger id,
			float x, float y, long animationDelay, boolean symmetricalEntity) {
		super(tileSet, name, room, id, x, y, animationDelay, symmetricalEntity);
		this.setAttacking(false);
		this.setLastUpdate(System.currentTimeMillis());
		this.setGroupName("enemy");
		LocalObject localObject = new LocalObject(BigInteger.valueOf(0), this.getName(), this.getRoom(), this.getX(), this.getY(), GameStates.tileSetWidth, GameStates.tileSetHeight, 0,0,0,0);
		attackAttributes = new float[AttributeType.values().length];
		attackAttributes[AttributeType.LIFE.ordinal()] = this.getAttributes()[AttributeType.STRENGH.ordinal()];
		attack = new AreaInfluence(BigInteger.valueOf(0), 0, 0, localObject, this.getGroupName(), true, attackAttributes, Priority.HIGH, "agent;"+this.getName()+";"+this.getId().toString());
	}
	
	@Override
	public void run(ObjectMapManager collisionMap1,
			ObjectMapManager collisionMap2) {
		/** update attributes of agent */
		if (this.getAttributes()[AttributeType.LIFE.ordinal()]>0 && this.isAlive()) {
			long timeNow = System.currentTimeMillis();
			long duration = timeNow - this.getLastUpdate();
			this.setLastUpdate(timeNow);
			this.updateAttributes(duration);
			attackAttributes[AttributeType.LIFE.ordinal()] = -this.getAttributes()[AttributeType.STRENGH.ordinal()];

			
//			System.out.println("update goal ranking");
			updateGoalRanking();
			if (actualGoal != null) {
//				System.out.println("search better goal");
				searchBetterLocation();
				if (actualGoal != null) {
					/** only search if we are too far away from goal */
					if ( (this.getDistance(actualGoal.getPosition().getX(), actualGoal.getPosition().getY()) > GameStates.agentGoalDistance) && this.getDistance(actualGoal.getPosition().getX(), actualGoal.getPosition().getY()) < GameStates.agentEntityVisionDistance) {	
						if (isPathCalculationComplete()) {
//							System.out.println("update movement");
							updateMovement();
						} else {
//							System.out.println("pathfinding");
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
//						System.out.println("close enough to take NEXT GOAL!");
//						if (this.getActualGoal().getEntity() == null) {
							this.nextGoal();
//						}
					}
					
					/** if there still is a valid goal */
					if (this.getActualGoal() != null) {
						if (this.getActualGoal().getEntity() != null) {
							float distanceGoal = getDistance(this.getActualGoal().getEntity().getX(), this.getActualGoal().getEntity().getY());
							if (distanceGoal<GameStates.agentGoalDistance*3) {
								this.setRoom(actualGoal.getEntity().getRoom());
							}
							
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
//				this.setGoal(false);
				
			}
		}
//		
//		/** check moving iteminfluences again */
//		if ( this.getItemInfluenceList().size() > 0) {
//			for (ItemInfluence itemInfluence: this.getItemInfluenceList()) {
//				if (itemInfluence.getAttributes()[AttributeType.MX.ordinal()] != 0 || itemInfluence.getAttributes()[AttributeType.MY.ordinal()] != 0) {
//					
//				}
//			}
//		}
	}
	
	@Override
	public void restartPathCalculationAfterCollision(ObjectMapManager collisionMap1, ObjectMapManager collisionMap2) {
//		System.out.println("restartPathCalculation of NormalAgent");
		/** if there still is a valid goal */
		if (this.getActualGoal() != null) {
			if (this.getActualGoal().getEntity() != null) {
				if ( getDistance(this.getActualGoal().getPosition().getX(), this.getActualGoal().getPosition().getY()-(this.getHeight()/2)) > 2*GameStates.tileSetWidth) {		
					super.restartPathCalculationAfterCollision(collisionMap1, collisionMap2);
//					System.out.println("Goal Entity != null and distance TOO BIG -> restartPathCalculation");
				} else {
					// too close
					this.setPathCalculationComplete(true);
					this.setDirectModus(true);

//					System.out.println("Goal Entity != null CLOSE, DIRECT!");
				}
			} else
				super.restartPathCalculationAfterCollision(collisionMap1, collisionMap2);
		} else {
			super.restartPathCalculationAfterCollision(collisionMap1, collisionMap2);
		}
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

	@Override
	public void setGroupName(String groupName) {
		super.setGroupName(groupName);
		if (this.getAttack() != null) {
			this.getAttack().setGroupName(groupName);
			this.getAttack().setExclusive(true);
		}
		
	}

}
