package de.svenheins.objects;

import java.math.BigInteger;

import de.svenheins.main.GameStates;
import de.svenheins.messages.ITEMCODE;
import de.svenheins.messages.OBJECTCODE;
import de.svenheins.objects.items.Container;

public class PlayerEntity extends Entity {
	/** attributes */
	private String groupName;
	private long firstServerLogin;
	private int experience;
	private String country;
	private long lastSeen;
	private boolean visible;
	private Container inventory;
	private Container inventoryUse;
	private Container equipmentBody;
	
	/** TileSet-Constructor */
	public PlayerEntity(TileSet tileSet, String name, BigInteger id, float x,
			float y, long animationDelay) {
		super(tileSet, name, id, x, y, animationDelay);
		this.groupName = "";
		this.country = "";
		this.firstServerLogin = System.currentTimeMillis();
		this.experience = 0;
		this.lastSeen = System.currentTimeMillis();
		this.setVisible(false);
		System.out.println("created "+name);
		this.inventory = new Container(GameStates.inventoryWidthPlayer, GameStates.inventoryHeightPlayer, OBJECTCODE.CONTAINER_MAIN, ITEMCODE.ALL);
		this.inventoryUse = new Container(GameStates.inventoryUseWidthPlayer, GameStates.inventoryUseHeightPlayer, OBJECTCODE.CONTAINER_USE, ITEMCODE.ALL);
		this.equipmentBody = new Container(1, 1, OBJECTCODE.CONTAINER_EQUIPMENT_BODY, ITEMCODE.BODY);
	}


	public String getGroupName() {
		return groupName;
	}


	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}


	public long getFirstServerLogin() {
		return firstServerLogin;
	}


	public void setFirstServerLogin(long firstServerLogin) {
		this.firstServerLogin = firstServerLogin;
	}


	public int getExperience() {
		return experience;
	}


	public void setExperience(int experience) {
		this.experience = experience;
	}


	public String getCountry() {
		return country;
	}


	public void setCountry(String country) {
		this.country = country;
	}


	public long getLastSeen() {
		return lastSeen;
	}


	public void setLastSeen(long lastSeen) {
		this.lastSeen = lastSeen;
	}


	public boolean isVisible() {
		return visible;
	}


	public void setVisible(boolean visible) {
		this.visible = visible;
	}


	public Container getInventory() {
		return inventory;
	}


	public void setInventory(Container inventory) {
		this.inventory = inventory;
	}


	public Container getInventoryUse() {
		return inventoryUse;
	}


	public void setInventoryUse(Container inventoryUse) {
		this.inventoryUse = inventoryUse;
	}


	public Container getEquipmentBody() {
		return equipmentBody;
	}


	public void setEquipmentBody(Container equipmentBody) {
		this.equipmentBody = equipmentBody;
	}
	
	

}
