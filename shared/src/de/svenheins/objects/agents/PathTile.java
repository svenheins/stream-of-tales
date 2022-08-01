package de.svenheins.objects.agents;

import de.svenheins.objects.WorldLatticePosition;
import de.svenheins.objects.WorldPosition;

public class PathTile {
	private WorldLatticePosition position;
	private float fScore;
	private float gScore;
	private float hScore;
	
	public PathTile(WorldLatticePosition position, float f, float g, float h) {
		this.position = position;
		fScore = f;
		gScore = g;
		hScore = h;
	}
	
	public PathTile(WorldPosition position, float f, float g, float h) {
		this.position = WorldLatticePosition.getWorldLatticePosition(position);
		fScore = f;
		gScore = g;
		hScore = h;
	}
	
	public WorldLatticePosition getPosition() {
		return position;
	}
	public void setPosition(WorldLatticePosition position) {
		this.position = position;
	}
	public float getFScore() {
		return fScore;
	}
	public void setFScore(float fScore) {
		this.fScore = fScore;
	}
	public float getGScore() {
		return gScore;
	}
	public void setGScore(float gScore) {
		this.gScore = gScore;
	}
	public float getHScore() {
		return hScore;
	}
	public void setHScore(float hScore) {
		this.hScore = hScore;
	}
}
