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
	
	/** update the movement depending on desired Position */
	public abstract void updateMovement();
	
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
		
	
	/** calculate distance to Point (x,y) */
	public float getDistance(float x, float y) {
		return (float) Math.sqrt((x-this.getX())*(x-this.getX()) + (y-(this.getY()))*(y-(this.getY())));
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
		if (pathList.size() >0 && pathList != null) {
//			System.out.println("old position: x="+pathList.get(0).getX()+" y="+pathList.get(0).getY());
			this.pathList.remove(0);
			
		}
	}
	
	public void nextGoal() {
		this.setMovement(0, 0);
		this.setContinuousState(EntityStates.STANDING);
		if (goalList != null && goalList.size()>0) {
//			System.out.println("old position: x="+pathList.get(0).getX()+" y="+pathList.get(0).getY());
			this.actualGoal = goalList.get(0);
//			System.out.println("actualGoal: x="+actualGoal.getPosition().getX()+" y="+actualGoal.getPosition().getY());
			this.restartPathCalculation();
			this.goalList.remove(0);
//			for (int i = 0; i < goalList.size(); i ++) {
//				System.out.println("Goal No: "+i+": x="+goalList.get(i).getPosition().getX()+" y="+goalList.get(i).getPosition().getY());
//			}
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
		this.setActualPathElement(null); // important to set it null!
	}
	
	public void restartPathCalculationAfterCollision(ObjectMapManager collisionMap1, ObjectMapManager collisionMap2) {
		restartPathCalculation();
	}
}
