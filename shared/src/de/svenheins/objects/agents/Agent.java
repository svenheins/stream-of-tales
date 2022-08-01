package de.svenheins.objects.agents;

import java.awt.Point;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.NoSuchElementException;

import de.svenheins.main.EntityStates;
import de.svenheins.main.GameStates;
import de.svenheins.managers.ObjectMapManager;
import de.svenheins.objects.Entity;
import de.svenheins.objects.PlayerEntity;
import de.svenheins.objects.TileSet;
import de.svenheins.objects.WorldLatticePosition;
import de.svenheins.objects.WorldPosition;
import de.svenheins.objects.agents.goals.Goal;
import de.svenheins.objects.agents.goals.GoalRankComparator;

public abstract class Agent extends PlayerEntity {

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
	
	public Agent(TileSet tileSet, String name, BigInteger id, float x, float y, long animationDelay) {
		super(tileSet, name, id, x, y, animationDelay);
		satisfaction = 0;
		this.range = 10000;
		this.setVisible(true);
		goal = false;
		pathCalculationComplete = false;
	}
	
	/** run */
	public abstract void run(ObjectMapManager collisionMap1, ObjectMapManager collisionMap2);
	
	/** find a location based on needs */
	public abstract void searchBetterLocation();
	
	/** update pathfinding */
	public abstract void updatePathfinding(ObjectMapManager collisionMap1, ObjectMapManager collisionMap2);

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
//		newMX = (float) (this.getActualGoal().getPosition().getX()-this.getX());
//		newMY = (float) (this.getActualGoal().getPosition().getY()-this.getY());
		newMX = (float) (this.getActualPathElement().getX()-(this.getX()));
		newMY = (float) (this.getActualPathElement().getY()-(this.getY()+(this.getHeight()/2)));
		float tempVelocity = (float) Math.sqrt(newMX*newMX + newMY*newMY);
		if (tempVelocity > 0) {
			this.setMovement(velocity*(newMX/tempVelocity), velocity*(newMY/tempVelocity));
		} else
		{
			this.setMovement(0, 0);
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
	
	/** calculate distance to Point (x,y) */
	public float getDistance(float x, float y) {
		return (float) Math.sqrt((x-this.getX())*(x-this.getX()) + (y-(this.getY()+this.getHeight()/2))*(y-(this.getY()+this.getHeight()/2)));
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
	
	public void addGoal( Goal additionalGoal)  {
		this.goalList.add(additionalGoal);
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
		if (pathList.size() >0 && pathList != null) {
//			System.out.println("old position: x="+pathList.get(0).getX()+" y="+pathList.get(0).getY());
			this.pathList.remove(0);
		}
	}
	
	public void nextGoal() {
		if (goalList != null && goalList.size()>0) {
//			System.out.println("old position: x="+pathList.get(0).getX()+" y="+pathList.get(0).getY());
			this.actualGoal = goalList.get(0);
			System.out.println("actualGoal: x="+actualGoal.getPosition().getX()+" y="+actualGoal.getPosition().getY());
			this.restartPathCalculation();
			this.goalList.remove(0);
		} else {
			this.setActualGoal(null);
			System.out.println("no more goals!");
		}
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
		this.setMovement(0, 0);
		this.setContinuousState(EntityStates.STANDING);
		pathList = new ArrayList<WorldPosition>(); // defines the way to the actualGoal
		pathCalculationComplete = false;
		openList = new HashMap<WorldLatticePosition, PathTile>(); // defines the openList (A*-algorithm)
		closedList = new HashMap<WorldLatticePosition, PathTile>(); // defines the closedList (A*-algorithm)
	}
}
