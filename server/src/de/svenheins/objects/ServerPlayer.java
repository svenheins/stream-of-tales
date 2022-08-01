package de.svenheins.objects;

import java.math.BigInteger;

import com.sun.sgs.app.AppContext;
import com.sun.sgs.app.ManagedReference;

import de.svenheins.main.GameStates;
import de.svenheins.messages.ITEMCODE;
import de.svenheins.messages.OBJECTCODE;
import de.svenheins.objects.items.Container;

public class ServerPlayer extends ServerEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/** attributes */
	private String groupName;
	private long firstServerLogin;
	private int experience;
	private String country;
	private ManagedReference<ServerContainer> inventory;
	private ManagedReference<ServerContainer> inventoryUse;
	private ManagedReference<ServerContainer> inventoryEqBody;
	
	
	public ServerPlayer(ServerSprite sprite, BigInteger id, String tileSetName, String tileSetPathName, float x, float y,
			float mx, float my) {
		super(sprite, id, tileSetName, tileSetPathName, x, y, mx, my);
		this.groupName = "";
		this.country = "";
		this.firstServerLogin = System.currentTimeMillis();
		this.experience = 0;
		ServerContainer inventory = new ServerContainer(GameStates.inventoryWidthPlayer, GameStates.inventoryHeightPlayer, OBJECTCODE.CONTAINER_MAIN, ITEMCODE.ALL);
		this.inventory = AppContext.getDataManager().createReference(inventory);
		
		ServerContainer inventoryUseRef= new ServerContainer(GameStates.inventoryUseWidthPlayer, GameStates.inventoryUseHeightPlayer, OBJECTCODE.CONTAINER_USE, ITEMCODE.ALL);
		this.inventoryUse = AppContext.getDataManager().createReference(inventoryUseRef);
		
		ServerContainer inventoryEqBodyRef= new ServerContainer(1,1 , OBJECTCODE.CONTAINER_EQUIPMENT_BODY, ITEMCODE.BODY);
		this.inventoryEqBody = AppContext.getDataManager().createReference(inventoryEqBodyRef);
		
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

	public ManagedReference<ServerContainer> getInventory() {
		return inventory;
	}

	public void setInventory(ManagedReference<ServerContainer> inventory) {
		this.inventory = inventory;
	}

	public ManagedReference<ServerContainer> getInventoryUse() {
		return inventoryUse;
	}

	public void setInventoryUse(ManagedReference<ServerContainer> inventoryUse) {
		this.inventoryUse = inventoryUse;
	}

	public ManagedReference<ServerContainer> getInventoryEqBody() {
		return inventoryEqBody;
	}

	public void setInventoryEqBody(ManagedReference<ServerContainer> inventoryEqBody) {
		this.inventoryEqBody = inventoryEqBody;
	}

	
	
}
