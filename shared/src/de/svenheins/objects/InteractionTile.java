package de.svenheins.objects;

import de.svenheins.main.TileType;

public class InteractionTile {
	private WorldLatticePosition position;
	private int[] values;
	private long creationTime;
	
	public InteractionTile(WorldLatticePosition position) {
		this.setPosition(position);
		this.values = new int[TileType.values().length];
		this.setCreationTime(System.currentTimeMillis());
	}
	
	public void interact(int[] addValues) {
		System.out.println("addValues length = "+addValues.length);
		if (addValues.length == values.length) {
			for (int i =0 ; i< values.length; i++) {
				values[i] += addValues[i];
				System.out.println("values["+i+"]="+values[i]);
			}
		} else {
			System.out.println("wrong length of interaction values");
		}
		
	}
	
	

	public WorldLatticePosition getPosition() {
		return position;
	}

	public void setPosition(WorldLatticePosition position) {
		this.position = position;
	}

	public int[] getValues() {
		return values;
	}

	public void setValues(int[] value) {
		this.values = value;
	}

	public long getCreationTime() {
		return creationTime;
	}

	public void setCreationTime(long creationTime) {
		this.creationTime = creationTime;
	}
	
	
	
}
