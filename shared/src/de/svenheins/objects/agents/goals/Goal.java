package de.svenheins.objects.agents.goals;

import java.math.BigInteger;
import java.util.ArrayList;

import de.svenheins.main.EntityStates;
import de.svenheins.objects.Entity;
import de.svenheins.objects.WorldPosition;
import de.svenheins.objects.items.Item;

/**
 * there are different kind of goals:
 * get something from someone
 * run away from danger
 * attack an enemy
 * @author Sven Heins
 *
 */
public class Goal {
	private WorldPosition position;
	private int rank;
	private BigInteger goalEntityID;
	private Entity entity;
	private ArrayList<Item> sellItem;
	private ArrayList<Item> buyItem;
	private boolean takeAll;
	
	public Goal(WorldPosition position, BigInteger entityID, Entity entity) {
		this.setPosition(position);
		this.setGoalEntityID(entityID);
		this.setEntity(entity);
	}
	
	public Goal(WorldPosition position) {
		this.setPosition(position);
		this.setGoalEntityID(BigInteger.valueOf(-1));
	}

	public WorldPosition getPosition() {
		return position;
	}

	public void setPosition(WorldPosition position) {
		this.position = position;
	}

	public int getRank() {
		return rank;
	}

	public void setRank(int rank) {
		this.rank = rank;
	}

	public BigInteger getGoalEntityID() {
		return goalEntityID;
	}

	public void setGoalEntityID(BigInteger goalEntityID) {
		this.goalEntityID = goalEntityID;
	}

	public ArrayList<Item> getBuyItem() {
		return buyItem;
	}

	public void setBuyItem(ArrayList<Item> buyItem) {
		this.buyItem = buyItem;
	}

	public ArrayList<Item> getSellItem() {
		return sellItem;
	}

	public void setSellItem(ArrayList<Item> sellItem) {
		this.sellItem = sellItem;
	}

	public boolean isTakeAll() {
		return takeAll;
	}

	public void setTakeAll(boolean takeAll) {
		this.takeAll = takeAll;
	}

	public Entity getEntity() {
		return entity;
	}

	public void setEntity(Entity entity) {
		this.entity = entity;
	}
}
