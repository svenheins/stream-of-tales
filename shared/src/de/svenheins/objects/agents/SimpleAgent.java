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
		this.setMaxSpeed(50.0f);
	}
	
	@Override
	public void run(ObjectMapManager collisionMap1, ObjectMapManager collisionMap2) {
		updateGoalRanking();
		searchBetterLocation();
		/** only search if we are too far away from goal */
		if ( this.getDistance(actualGoal.getPosition().getX(), actualGoal.getPosition().getY()) > GameStates.agentGoalDistance) {
			updatePathfinding(collisionMap1, collisionMap2);
			updateMovement();
		} else {
			this.setMovement(0, 0);
			this.setContinuousState(EntityStates.STANDING);
			this.pathList = new ArrayList<WorldPosition>();
		}
		updateGoals();
	}
	
	@Override
	public void updateGoalRanking() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void searchBetterLocation() {
		
		/** update the pathList */
		if (this.pathList != null && this.pathList.size() > 0) {
			if ( getDistance(this.pathList.get(0).getX(), this.pathList.get(0).getY()) < GameStates.pathMinAcceptanceDistance) {
				/** position is close enough, so get next pathElement */
				this.nextPathElement();
			}
			if (this.pathList != null && this.pathList.size() > 0) {			
				/** here we already have a valid pathList */
				this.getActualGoal().setPosition(this.pathList.get(0));
			} else {
				this.pathList = new ArrayList<WorldPosition>();

			}
		} else {
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
			} else if (this.getActualGoal().getPosition() != null) {
				
				
	//			WorldPosition worldPos = this.getActualGoal().getPosition();
	//			worldPos.setX(entity.getX());
	//			worldPos.setY(entity.getY());
	//			this.getActualGoal().setPosition(worldPos);
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
			
			/** else stop movement and calculate new path */
		} else {
//			int localWidth = GameStates.mapTotalWidth;
//			int localHeight = GameStates.mapTotalHeight;
		/** else continue with path calculation */
			if (closedList.size() > 0) {
				/** so we already started the generation of the lists */
				/** upper */
				PathTile originTile = openList.get(0);
//				float f, g, h;
				closedList.add(originTile);
				
				/** upper */
				WorldPosition tileWorldPosition = new WorldPosition((int) originTile.getPosition().getMapCoordinates().getX()+GameStates.mapTileSetWidth*originTile.getPosition().getLocalX(), 
						(int) originTile.getPosition().getMapCoordinates().getY()+GameStates.mapTileSetWidth*originTile.getPosition().getLocalY() -GameStates.mapTileSetHeight);
//				WorldLatticePosition tileWorldLatticePosition = WorldLatticePosition.getWorldLatticePosition(tileWorldPosition);
				checkTile(originTile, tileWorldPosition, collisionMap1, collisionMap2);
				
				/** right */
				tileWorldPosition = new WorldPosition((int) closedList.get(0).getPosition().getMapCoordinates().getX()+GameStates.mapTileSetWidth*closedList.get(0).getPosition().getLocalX() + GameStates.mapTileSetWidth, 
						(int) closedList.get(0).getPosition().getMapCoordinates().getY()+GameStates.mapTileSetWidth*closedList.get(0).getPosition().getLocalY());
//				tileWorldLatticePosition = WorldLatticePosition.getWorldLatticePosition(tileWorldPosition);
				checkTile(originTile, tileWorldPosition, collisionMap1, collisionMap2);
				
				/** lower */
				tileWorldPosition = new WorldPosition((int) closedList.get(0).getPosition().getMapCoordinates().getX()+GameStates.mapTileSetWidth*closedList.get(0).getPosition().getLocalX(), 
						(int) closedList.get(0).getPosition().getMapCoordinates().getY()+GameStates.mapTileSetWidth*closedList.get(0).getPosition().getLocalY() + GameStates.mapTileSetHeight);
//				tileWorldLatticePosition = WorldLatticePosition.getWorldLatticePosition(tileWorldPosition);
				checkTile(originTile, tileWorldPosition, collisionMap1, collisionMap2);
				
				/** left */
				tileWorldPosition = new WorldPosition((int) closedList.get(0).getPosition().getMapCoordinates().getX()+GameStates.mapTileSetWidth*closedList.get(0).getPosition().getLocalX() - GameStates.mapTileSetWidth, 
						(int) closedList.get(0).getPosition().getMapCoordinates().getY()+GameStates.mapTileSetWidth*closedList.get(0).getPosition().getLocalY());
//				tileWorldLatticePosition = WorldLatticePosition.getWorldLatticePosition(tileWorldPosition);
				checkTile(originTile, tileWorldPosition, collisionMap1, collisionMap2);
				
				openList.remove(originTile);
				
			} else {
				/** add first PathTile (entity itself) */
				PathTile originTile = new PathTile(new WorldPosition(this.getX(), this.getY()), 0, 0, 0);
				closedList.add(originTile);
				float f, g, h;
				
				/** upper */
				WorldPosition tileWorldPosition = new WorldPosition((int) originTile.getPosition().getMapCoordinates().getX()+GameStates.mapTileSetWidth*originTile.getPosition().getLocalX(), 
						(int) originTile.getPosition().getMapCoordinates().getY()+GameStates.mapTileSetWidth*originTile.getPosition().getLocalY() -GameStates.mapTileSetHeight);
				WorldLatticePosition tileWorldLatticePosition = WorldLatticePosition.getWorldLatticePosition(tileWorldPosition);
				h = this.guessDistanceValue(tileWorldPosition, actualGoal.getPosition());	
				g = originTile.getGScore() + 1.0f;
				f = g + h;
				PathTile addTile = new PathTile(tileWorldLatticePosition, f,g,h);
				checkFirstTiles(addTile, collisionMap1, collisionMap2);
				
				/** right */
				tileWorldPosition = new WorldPosition((int) closedList.get(0).getPosition().getMapCoordinates().getX()+GameStates.mapTileSetWidth*closedList.get(0).getPosition().getLocalX() + GameStates.mapTileSetWidth, 
						(int) closedList.get(0).getPosition().getMapCoordinates().getY()+GameStates.mapTileSetWidth*closedList.get(0).getPosition().getLocalY());
				tileWorldLatticePosition = WorldLatticePosition.getWorldLatticePosition(tileWorldPosition);
				h = this.guessDistanceValue(tileWorldPosition, actualGoal.getPosition());	
				g = originTile.getGScore() + 1.0f;
				f = g + h;
				addTile = new PathTile(tileWorldLatticePosition, f,g,h);
				checkFirstTiles(addTile, collisionMap1, collisionMap2);
				
				/** lower */
				tileWorldPosition = new WorldPosition((int) closedList.get(0).getPosition().getMapCoordinates().getX()+GameStates.mapTileSetWidth*closedList.get(0).getPosition().getLocalX(), 
						(int) closedList.get(0).getPosition().getMapCoordinates().getY()+GameStates.mapTileSetWidth*closedList.get(0).getPosition().getLocalY() + GameStates.mapTileSetHeight);
				tileWorldLatticePosition = WorldLatticePosition.getWorldLatticePosition(tileWorldPosition);
				h = this.guessDistanceValue(tileWorldPosition, actualGoal.getPosition());	
				g = originTile.getGScore() + 1.0f;
				f = g + h;
				addTile = new PathTile(tileWorldLatticePosition, f,g,h);
				checkFirstTiles(addTile, collisionMap1, collisionMap2);
				
				/** left */
				tileWorldPosition = new WorldPosition((int) closedList.get(0).getPosition().getMapCoordinates().getX()+GameStates.mapTileSetWidth*closedList.get(0).getPosition().getLocalX() - GameStates.mapTileSetWidth, 
						(int) closedList.get(0).getPosition().getMapCoordinates().getY()+GameStates.mapTileSetWidth*closedList.get(0).getPosition().getLocalY());
				tileWorldLatticePosition = WorldLatticePosition.getWorldLatticePosition(tileWorldPosition);
				h = this.guessDistanceValue(tileWorldPosition, actualGoal.getPosition());	
				g = originTile.getGScore() + 1.0f;
				f = g + h;
				addTile = new PathTile(tileWorldLatticePosition, f,g,h);
				checkFirstTiles(addTile, collisionMap1, collisionMap2);
			}
		}
		/** end */
	}

	public void checkTile(PathTile originTile, WorldPosition tileWorldPosition, ObjectMapManager collisionMap1, ObjectMapManager collisionMap2) {
		WorldLatticePosition tileWorldLatticePosition = WorldLatticePosition.getWorldLatticePosition(tileWorldPosition);
		if ((collisionMap1.checkCollision(tileWorldLatticePosition.getMapCoordinates(), tileWorldLatticePosition.getLocalX(), tileWorldLatticePosition.getLocalY() ) ) ||
				(collisionMap2.checkCollision(tileWorldLatticePosition.getMapCoordinates(), tileWorldLatticePosition.getLocalX(), tileWorldLatticePosition.getLocalY() ) )) {
			/** collision!!! */
			// do nothing
		} else {
			if (closedList.contains(tileWorldLatticePosition)) {
				// ignore it
			} else if (!openList.contains(tileWorldLatticePosition)) {
				/** calculate PathTile */
				float h = this.guessDistanceValue(tileWorldPosition, actualGoal.getPosition());	
				float g = originTile.getGScore() + 1.0f;
				float f = g + h;
				PathTile addTile = new PathTile(tileWorldLatticePosition, f,g,h);
				/** add to openList */
				openList.add(addTile);
			} else if (openList.contains(tileWorldLatticePosition)){
				// check the other representation and take the better score and update its parent
				
//				TODO: weiter
			}
			
		}
	}
	
	public void checkFirstTiles(PathTile tile,ObjectMapManager collisionMap1, ObjectMapManager collisionMap2) {
		if ((collisionMap1.checkCollision(tile.getPosition().getMapCoordinates(), tile.getPosition().getLocalX(), tile.getPosition().getLocalY() ) ) ||
				(collisionMap2.checkCollision(tile.getPosition().getMapCoordinates(), tile.getPosition().getLocalX(), tile.getPosition().getLocalY() ) )) {
			/** collision!!! */
			// do nothing
		} else {
			openList.add(tile);	
		}
	}
	
	@Override
	public void updateGoals() {
		// TODO Auto-generated method stub
		
	}

	

}
